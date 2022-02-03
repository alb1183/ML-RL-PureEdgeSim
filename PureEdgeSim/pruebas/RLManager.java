package pruebas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.vms.Vm;

import com.pureedgesim.datacentersmanager.DataCenter;
import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.SimLog;
import com.pureedgesim.simulationcore.SimulationManager;
import com.pureedgesim.tasksgenerator.Task;

public class RLManager {
	SimulationManager simulationManager;
	List<List<Integer>> orchestrationHistory;
	List<Vm> vmList;
	
	public List<Map<String, Qrow>> vmQTableList;
	public Map<String, Qrow> Qtable = new HashMap<String, Qrow>();
	
	private double rewardSum = 0;
	private int rewardNum = 0;
	
	// Parametro del algoritmo RL
	private double epsilon = 0.1;
	private double beta_a = 100;
	private double beta_b = 0.3;
	private double beta_c = 1;
	private double gamma = 0.3;
	private double alpha = 0.6;
		
	public RLManager(SimLog simLog, SimulationManager simulationManager, List<List<Integer>> orchestrationHistory, List<Vm> vmList) {
		this.simulationManager = simulationManager;
		this.orchestrationHistory = orchestrationHistory;
		this.vmList = vmList;
		
		vmQTableList = new ArrayList<>();
		for (int i = 0; i < simulationManager.getDataCentersManager().getDatacenterList().size() + 5; i++) {
			// Creating a list to store the orchestration history for each VM (virtual machine)
			vmQTableList.add(new HashMap<String, Qrow>());
		}
		
		// Cargo los valores de las tablas Q si está habilitado
		//simLog.loadQTables(vmQTableList, null);
	}
	
	// Algoritmo principal de offloading RL
	public int reinforcementLearning(String[] architecture, Task task) {
		// *** Determino el estado ***
		
		// Estado del dispositivo local
		DataCenter device = (SimulationParameters.ENABLE_ORCHESTRATORS) ? task.getOrchestrator() : task.getEdgeDevice();
		int localDeviceId = (int) device.getId();
		
		List<Vm> vmListDevice = device.getVmAllocationPolicy().getHostList().get(0).getVmList();
		Vm localDevice = null;
		
		// Si es un dispositivo que tiene capacidad de computo (no es un sensor)
		if(vmListDevice.size() > 0)
			localDevice = vmListDevice.get(0);
		
		String estado = getRLState(task, localDevice);
		
		
		
		// *** Determino el conjunto de acciones ***
		List<Qrow> acciones = getAccionesList(device, localDeviceId, localDevice, estado);
		

		// *** Exploración VS Explotación ***
		int accion;
		double e = new Random().nextFloat();
		if(e < epsilon) { // Exploración
			/*accion = new Random().nextInt(4);
			
			if(localDevice == null) {
				accion = 1 + new Random().nextInt(3);
			}*/

			// Acción aleatoria
			accion = acciones.get(new Random().nextInt(acciones.size())).getAccion();
			
		} else { // Explotación
			accion = getRLAccion(acciones);
			
			//System.out.println(localDeviceId + ": " + accionLocal.getValue() + ", " + accionMist.getValue() + ", " + accionEdge.getValue() + ", " + accionCloud.getValue() + " => " + accion);
			
		}
		

		// Indico que acción se ha tomado en esta tarea como un metadato
		task.setMetaData(new String[] { estado + "_" + accion, Integer.toString(accion)});
		
		return accion;
	}

	private String getRLState(Task task, Vm localDevice) {
		// *** Determino el estado original ***
		double taskLength = task.getLength();
		double taskMaxLatency = task.getMaxLatency();
		
		// Estado del dispositivo local
		double localCPU = 0;
		double localMIPS = 0;
		int localTaskRunning = 0;
		
		// Si es un dispositivo que tiene capacidad de computo (no es un sensor)
		if(localDevice != null) {
			localCPU = localDevice.getCpuPercentUtilization() * 100.0; // En tanto por ciento
			localMIPS = localDevice.getMips();
			localTaskRunning = orchestrationHistory.get((int) localDevice.getId()).size() - vmList.get((int) localDevice.getId()).getCloudletScheduler().getCloudletFinishedList().size() + 1;
		} else { // Si es un sensor
			
		}
		
		// Estado de Edge
		double edgeCPU = 0;
		for (int j = SimulationParameters.NUM_OF_CLOUD_DATACENTERS; j < SimulationParameters.NUM_OF_EDGE_DATACENTERS + SimulationParameters.NUM_OF_CLOUD_DATACENTERS; j++) {
			edgeCPU += simulationManager.getDataCentersManager().getDatacenterList().get(j).getResources().getAvgCpuUtilization();
		}
		edgeCPU /= SimulationParameters.NUM_OF_EDGE_DATACENTERS;
		
		// Estado de cloud
		double cloudCPU = simulationManager.getDataCentersManager().getDatacenterList().get(0).getResources().getAvgCpuUtilization();
		
		
		// *** Discretizo el estado en un conjuntos finitos *** (TODO: Fuzzification)
		String taskLengthTerm = (taskLength < 20000) ? "low" : (taskLength < 100000) ? "medium" : "high";
		String taskMaxLatencyTerm = (taskMaxLatency < 6) ? "low" : (taskMaxLatency < 15) ? "medium" : "high";
		
		
		String localCPUTerm = (localCPU < 25.0) ? "low" : (localCPU < 50) ? "medium" : (localCPU < 75) ? "busy" : "high";
		String localMIPSTerm = (localMIPS < 30000) ? "low" : (localMIPS < 130000) ? "medium" : "high";

		String edgeCPUTerm = (edgeCPU < 25.0) ? "low" : (edgeCPU < 50) ? "medium" : (edgeCPU < 75) ? "busy" : "high";
		
		String cloudCPUTerm = (cloudCPU < 25.0) ? "low" : (cloudCPU < 50) ? "medium" : (cloudCPU < 75) ? "busy" : "high";

		//String estado = taskMaxLatencyTerm;
		//String estado = taskLengthTerm + "_" + taskMaxLatencyTerm + "_" + localCPUTerm + "_" + localMIPSTerm;
		String estado = cloudCPUTerm + "_"  + edgeCPUTerm + "_" + localCPUTerm + "_" + taskMaxLatencyTerm + "_" + taskLengthTerm + "_" + localMIPSTerm;
		
		return estado;
	}
	
	private List<Qrow> getAccionesList(DataCenter device, int localDeviceId, Vm localDevice, String estado) {
		// *** Determino el conjunto de acciones ***
		List<Qrow> acciones = new LinkedList<Qrow>();
		
		// Accion de computo local (solo si el dispositivo tiene capacidad de computo)
		if(localDevice != null)
			acciones.add(getQTable(localDeviceId, estado + "_0", 0));

		// Accion de offloading a mist
		// Solo la tengo en cuenta si tengo vecinos disponible en mi rango
		if(getNumVecinos(device) > 0)
			acciones.add(getQTable(localDeviceId, estado + "_1", 1));
		
		// Accion de offloading al edge
		acciones.add(getQTable(localDeviceId, estado + "_2", 2));
		
		// Accion de offloading a cloud
		acciones.add(getQTable(localDeviceId, estado + "_3", 3));
		
		return acciones;
	}
	
	private int getNumVecinos(DataCenter device) {
		int vecinos = 0;

		for (int i = 0; i < vmList.size(); i++) {
			DataCenter dcd = (DataCenter) vmList.get(i).getHost().getDatacenter();
			if(device.getId() != dcd.getId() && dcd.getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
				if (device.getMobilityManager().distanceTo(dcd) < SimulationParameters.EDGE_DEVICES_RANGE) {
					vecinos++;
				}
			}
		}
		
		return vecinos;
	}


	//private int getRLAccion(int localDeviceId, Vm localDevice, String estado, List<Qrow> acciones) {
	private int getRLAccion(List<Qrow> acciones) {
		/*Qrow accionLocal = getQTable(localDeviceId, estado + "_0", 0);
		Qrow accionMist = getQTable(localDeviceId, estado + "_1", 1);
		Qrow accionEdge = getQTable(localDeviceId, estado + "_2", 2);
		Qrow accionCloud = getQTable(localDeviceId, estado + "_3", 3);
		
		int accion = 1;
		//double minQValue = accionMist.getValue() * 0.7;
		double minQValue = accionMist.getValue();

		//if(accionEdge.getValue() * 1.2 < minQValue) {
		if(accionEdge.getValue() < minQValue) {
			accion = 2;
			minQValue = accionEdge.getValue();
		}

		//if(accionCloud.getValue() * 1.8 < minQValue) { // TODO: Revisar
		if(accionCloud.getValue() < minQValue) {
			accion = 3;
			minQValue = accionCloud.getValue();
		}

		//if(localDevice != null && accionLocal.getValue() * 0.5 <= minQValue) {
		if(localDevice != null && accionLocal.getValue() <= minQValue) {
			accion = 0;
			minQValue = accionLocal.getValue();
		}*/
		
		int accion = acciones.get(0).getAccion();
		double minQValue = acciones.get(0).getValue();
		
		for(int i = 1; i < acciones.size(); i++) {
			if(acciones.get(i).getValue() < minQValue) {
				minQValue = acciones.get(i).getValue();
				accion = acciones.get(i).getAccion();
			}
		}
		
		return accion;
	}
	

	public void reinforcementFeedback(Task task) {
		// Calculo la recompensa (tiempo de ejecución de la tarea + tiempo de espera)
		double executionTime = task.getActualCpuTime();
		double waitingTime = task.getExecStartTime() - task.getTime();
		double receptionTime = 0;
		if (task.getReceptionTime() != -1) // the task is offloaded
			receptionTime += task.getReceptionTime() - task.getTime();

		//double reward = executionTime + waitingTime;

		//double totalTime = task.getFinishTime() - task.getTime();
		double totalTime = task.getCheckTime() - task.getTime();
		
		double totalEnergy = task.getTotalCost();
		
		double cpuExecution = task.getVm().getCpuPercentUtilization(task.getTime());
		
		// Recompensa
		//double reward = beta_a * totalTime;
		//double reward = beta_a * totalTime + beta_b * totalEnergy;
		//double reward = beta_a * totalTime + beta_b * totalEnergy + beta_c * cpuExecution;
		double reward = (beta_a * totalTime + beta_b * totalEnergy) * cpuExecution * beta_c;
		
		if (task.getStatus() == Status.FAILED) {
			if(task.getFailureReason() == Task.Status.FAILED_DUE_TO_LATENCY) {
				//System.out.println(task.getId() + ", " + ((DataCenter)task.getVm().getHost().getDatacenter()).getType() + "; " + task.getMaxLatency() + " : " + (task.getCheckTime()-task.getTime()));
			}
			reward = beta_a*99;
		}
		/*else if(((DataCenter) task.getVm().getHost().getDatacenter()).getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
			System.out.println(reward);
		}*/
		
		// Acción que se tomó en esa tarea
		String estadoTask = ((String[]) task.getMetaData())[0];
		int accion = Integer.parseInt(((String[]) task.getMetaData())[1]);
		
		// Estado del dispositivo local
		DataCenter device = (SimulationParameters.ENABLE_ORCHESTRATORS) ? task.getOrchestrator() : task.getEdgeDevice();
		int localDeviceId = (int) device.getId();
		
		List<Vm> vmListDevice = device.getVmAllocationPolicy().getHostList().get(0).getVmList();
		Vm localDevice = null;
		
		// Si es un dispositivo que tiene capacidad de computo (no es un sensor)
		if(vmListDevice.size() > 0)
			localDevice = vmListDevice.get(0);
		
		String estadoN = getRLState(task, localDevice);

		// *** Determino el conjunto de acciones y la accion a tomar ***
		List<Qrow> acciones = getAccionesList(device, localDeviceId, localDevice, estadoN);
		int accionN = getRLAccion(acciones);
		
		double q = getQTable(localDeviceId, estadoN + "_" + accionN, accionN).getValue();
		
		updateQTable((int) device.getId(), estadoTask, accion, reward, q);
	}

	private void updateQTable(int vm, String rule, int accion, double reward, double q) {
		Map<String, Qrow> Qtable = vmQTableList.get(vm);
		
		// Existe la entrada en la tabla
		if(Qtable.containsKey(rule)) {
			Qrow row = Qtable.get(rule);
			
			double QValue = row.getValue();
			row.increaseUpdatesCount();

			// Actualización del Q valor con un maximo de 100
			//double k = Math.min(row.getUpdatesCount(), 100);
			//QValue += 1 / k * (reward - QValue);
			//double k = row.getUpdatesCount();
			QValue = QValue*(1-alpha) + alpha*(reward + gamma*q);
			row.setValue(QValue);
		} else { // No está la entrada de esa regla, la creo desde cero
			Qrow row = new Qrow(rule, accion, reward);
			Qtable.put(rule, row);
		}
		
		// Actualizo el contador local de reward media
		updateAvgReward(vm, reward);
	}
	

	private Qrow getQTable(int vm, String rule, int accion) {
		Map<String, Qrow> Qtable = vmQTableList.get(vm);
		
		// Existe la entrada en la tabla
		if(Qtable.containsKey(rule)) {
			return Qtable.get(rule);
		} else { // No está la entrada de esa regla, la creo desde cero
			Qrow row = new Qrow(rule, accion, 1);
			Qtable.put(rule, row);
			return row;
		}
		
	}
	
	private void updateAvgReward(int vm, double reward) {
		this.rewardSum += reward;
		this.rewardNum++;
	}
	
	public double getAvgReward() {
		double avgReward = this.rewardSum / this.rewardNum;
		this.rewardNum = 0;
		this.rewardSum = 0;
		return avgReward;
	}

	public List<Map<String, Qrow>> getVmQTableList() {
		return this.vmQTableList;
	}
}
