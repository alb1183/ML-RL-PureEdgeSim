/**
 *     PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 *
 *     This file is part of PureEdgeSim Project.
 *
 *     PureEdgeSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PureEdgeSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 *     
 *     @author Mechalikh
 **/
package pruebas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.vms.Vm;

import com.pureedgesim.datacentersmanager.DataCenter;
import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.scenariomanager.SimulationParameters.TYPES;
import com.pureedgesim.simulationcore.SimLog;
import com.pureedgesim.simulationcore.SimulationManager;
import com.pureedgesim.tasksgenerator.Task;
import com.pureedgesim.tasksorchestration.Orchestrator;

import net.sourceforge.jFuzzyLogic.FIS;

public class CustomEdgeOrchestrator extends Orchestrator {
	RLManager rlManager;
	MultiLayerRLManager multiLayerRLManager;

	public CustomEdgeOrchestrator(SimulationManager simulationManager) {
		super(simulationManager);

		rlManager = new RLManager(simLog, simulationManager, orchestrationHistory, vmList);
		multiLayerRLManager = new MultiLayerRLManager(simLog, simulationManager, orchestrationHistory, vmList, algorithm);
	}

	protected int findVM(String[] architecture, Task task) {		
		int bestVM = -1;
		switch (algorithm) {
			case "RANDOM":
				bestVM = random(architecture, task);
				break;
			case "RANDOM_GOOD":
				bestVM = randomGood(architecture, task);
				break;
			case "LOCAL":
				bestVM = local(architecture, task);
				break;
			case "CLOSEST":
				bestVM = closestMist(architecture, task);
				break;
			case "MIST":
				bestVM = onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DEVICE);
				break;
			case "EDGE":
				bestVM = onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DATACENTER);
				break;
			case "CLOUD":
				bestVM = onlyType(architecture, task, SimulationParameters.TYPES.CLOUD);
				break;
			case "ROUND_ROBIN":
				bestVM = roundRobin(architecture, task);
				break;
			case "TRADE_OFF":
				bestVM = tradeOff(architecture, task);
				break;
			case "INCREASE_LIFETIME":
				bestVM = increaseLifetime(architecture, task);
				break;
			case "LATENCY_ENERGY_AWARE":
				bestVM = LatencyAndEnergyAware(architecture, task);
				break;
			case "WEIGHT_GREEDY":
				bestVM = weightGreedy(architecture, task);
				break;
			case "TEST":
				bestVM = test(architecture, task);
				break;
			case "RL":
				bestVM = reinforcementLearning(architecture, task);
				break;
			case "RL_MULTILAYER":
				bestVM = multilayerreinforcementLearning(architecture, task);
				break;
			case "RL_MULTILAYER_EMPTY":
				bestVM = multilayerreinforcementLearning(architecture, task);
				break;
			case "RL_MULTILAYER_DISABLED":
				bestVM = multilayerreinforcementLearning(architecture, task);
				break;
			case "FUZZY_LOGIC":
				bestVM = fuzzyLogic(task);
				break;
	
			default:
				SimLog.println("");
				SimLog.println("Custom Orchestrator- Unknown orchestration algorithm '" + algorithm
						+ "', please check the 'settings/simulation_parameters.properties' file you are using");
				// Cancel the simulation
				SimulationParameters.STOP = true;
				simulationManager.getSimulation().terminate();
				break;
		}
		
		return bestVM;
	}

	

	/************ Random ************/
	private int random(String[] architecture, Task task) {
		return (new Random()).nextInt(orchestrationHistory.size());
	}
	/************ Random ************/
	
	/************ Random Good ************/
	private int randomGood(String[] architecture, Task task) {
		Random r = new Random();
		int max = orchestrationHistory.size();
		int random = r.nextInt(max);
		
		while (!offloadingIsPossible(task, vmList.get(random), architecture))
			random = r.nextInt(max);
		
		return random;
	}
	/************ Random ************/

	/************ Local ************/
	private int local(String[] architecture, Task task) {
		int vm = -1;
		
		DataCenter device = (SimulationParameters.ENABLE_ORCHESTRATORS) ? task.getOrchestrator() : task.getEdgeDevice();
		List<Vm> vmListDevice = device.getVmAllocationPolicy().getHostList().get(0).getVmList();
		
		if(vmListDevice.size() > 0)
			vm = (int) vmListDevice.get(0).getId();

		if(vm == -1) {
			//System.out.println("Offloading to mist");
			vm = onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DEVICE);
			
			if(vm == -1) {
				//System.out.println("Offloading to mist failed");
			}
		}
		
		return vm;
	}
	/************ Local ************/
	

	/************ Closest Mist ************/
	// Basado en round robin
	private int closestMist(String[] architecture, Task task) {
		int vm = -1;
		double minDistance = SimulationParameters.EDGE_DEVICES_RANGE;
		int minTasksCount = -1; 
		double minCPU = 1;
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
				if (offloadingIsPossible(task, vmList.get(i), architecture)) {
					double localDistance = ((DataCenter)vmList.get(i).getHost().getDatacenter()).getMobilityManager().distanceTo(task.getOrchestrator());
					int assignedTasksNum = orchestrationHistory.get(i).size();
					double localCPU = vmList.get(i).getCpuPercentUtilization();
					double taskRunning = orchestrationHistory.get(i).size() - vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1;
					if(minTasksCount == -1 || (localDistance < minDistance && assignedTasksNum <= minTasksCount /*&& localCPU <= minCPU*/)) { // TODO : CPU
						minDistance = localDistance;
						minTasksCount = assignedTasksNum;
						minCPU = localCPU;
						vm = i;
					}
				}
			}
		}
		
		return vm;
	}
	/************ Closest Mist ************/
	
	/************ Only Type ************/
	// Basado en round robin
	private int onlyType(String[] architecture, Task task, TYPES tipo) {
		int vm = -1;
		int minTasksCount = -1; // vm with minimum assigned tasks;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == tipo) {
				if (offloadingIsPossible(task, vmList.get(i), architecture) && (minTasksCount == -1	|| minTasksCount > orchestrationHistory.get(i).size())) {
					//System.out.println(vmList.get(i).getMips() + " - " + vmList.get(i).getHost().getMips() + " - " + task.getLength());
					minTasksCount = orchestrationHistory.get(i).size();
					// if this is the first time, or new min found, so we choose it as the best VM set the first vm as the best one
					vm = i;
					
					/*if(i == task.getEdgeDevice().getId() || i == task.getOrchestrator().getId())
						System.out.println("Local offloading, " + i + ", " + task.getEdgeDevice().getId() + ", " + task.getOrchestrator().getId());*/ // Caso interesante
				}
				//if(/*offloadingIsPossible(task, vmList.get(i), architecture) &&*/ (vmList.get(i).getHost().getDatacenter().getId() == task.getEdgeDevice().getId() || vmList.get(i).getHost().getDatacenter().getId() == task.getOrchestrator().getId()))
				//	System.out.println("Test local offloading, " + i + ", " + vmList.get(i).getHost().getDatacenter().getId() + ", " + task.getEdgeDevice().getId() + ", " + task.getOrchestrator().getId() + ", " + task.getEdgeDevice().getVmAllocationPolicy().getHostList().get(0).getVmList().get(0).getId()); // Caso interesante
			}
		}
		
		/*if(vm == -1)
			System.out.println("No se envía a nadie");*/ // Error (enviar a si mismo si eso)
		
		// assign the tasks to the found vm
		return vm;
	}
	/************ Only Type ************/
	
	
	/************ Round Robin ************/
	private int roundRobin(String[] architecture, Task task) {
		int vm = -1;
		int minTasksCount = -1; // vm with minimum assigned tasks;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			// Comprueba si esa vm se le puede hacer offloading y en su caso comprueba si es el primer elemento o si historial de tareas es menor
			if (offloadingIsPossible(task, vmList.get(i), architecture) && (minTasksCount == -1	|| minTasksCount > orchestrationHistory.get(i).size())) {
				minTasksCount = orchestrationHistory.get(i).size();
				// if this is the first time, or new min found, so we choose it as the best VM set the first vm as the best one
				vm = i;
			}
		}
		// assign the tasks to the found vm
		return vm;
	}
	/************ Round Robin ************/
	
	

	/************ Trade Off ************/
	/*private int tradeOff(String[] architecture, Task task) {
		int vm = -1;
		double min = -1;
		double new_min;// vm with minimum assigned tasks;

		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				// the weight below represent the priority, the less it is, the more it is
				// suitable for offlaoding, you can change it as you want
				double weight = 1;

				if (((DataCenter) vmList.get(i).getHost().getDatacenter())
						.getType() == SimulationParameters.TYPES.CLOUD) {
					// this is the cloud, it consumes more energy and results in high latency, so
					// better to avoid it
					weight = 1.5;
				}

				new_min = (orchestrationHistory.get(i).size() + 1) * weight * task.getLength()
						/ vmList.get(i).getMips();
				if (min == -1 || min > new_min) {
					min = new_min;
					// set the first vm as the best one
					vm = i;
				}
			}
		}
		// assign the tasks to the found vm
		return vm;
	}*/

	private int tradeOff(String[] architecture, Task task) {
		int vm = -1;
		double min = -1;
		double new_min;// vm with minimum assigned tasks;

		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				// the weight below represent the priority, the less it is, the more it is
				// suitable for offlaoding, you can change it as you want
				double weight = 1.2; // this is an edge server 'cloudlet', the latency is slightly high then edge devices
				if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == SimulationParameters.TYPES.CLOUD) {
					weight = 1.8; // this is the cloud, it consumes more energy and results in high latency, so better to avoid it
				} else if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
					weight = 1.3;// this is an edge device, it results in an extremely low latency, but may consume more energy.
				}
				new_min = (orchestrationHistory.get(i).size() + 1) * weight * task.getLength() / vmList.get(i).getMips();
				if (min == -1 || min > new_min) { // if it is the first iteration, or if this vm has more cpu mips and less waiting tasks
					min = new_min;
					// set the first vm as thebest one
					vm = i;
				}
			}
		}
		// assign the tasks to the found vm
		return vm;
	}
	/************ Trade Off ************/


	/************ Increase Lifetime ************/
	protected int increaseLifetime(String[] architecture, Task task) {
		int vm = -1;
		double minTasksCount = -1; // vm with minimum assigned tasks;
		double vmMips = 0;
		double weight;
		double minWeight = 20;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				weight = getWeight(task, ((DataCenter) vmList.get(i).getHost().getDatacenter()));

				if (minTasksCount == -1 || vmMips / (minTasksCount * minWeight) < vmList.get(i).getMips() / ((orchestrationHistory.get(i).size()
						- vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1) * weight)) {
					minTasksCount = orchestrationHistory.get(i).size() - vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1; 
					vmMips = vmList.get(i).getMips();
					minWeight = weight;
					vm = i;
				}
			}
		}
		// assign the tasks to the vm found
		return vm;
	}

	private double getWeight(Task task, DataCenter dataCenter) {
		double weight = 1;// if it is not battery powered
		if (dataCenter.getEnergyModel().isBatteryPowered()) {
			if (task.getEdgeDevice().getEnergyModel().getBatteryLevel() > dataCenter.getEnergyModel().getBatteryLevel())
				weight = 20; // the destination device has lower remaining power than the task offloading device, 
								// in this case it is better not to offload that's why the weight is high (20)
			else
				weight = 15; // in this case the destination has higher remaining power, so it is okey to
								// offload tasks for it, if the cloud and the edge data centers are absent.
		}
		return weight;
	}
	/************ Increase Lifetime ************/
	


	/************ LatencyAndEnergyAware ************/
	private int LatencyAndEnergyAware(String[] architecture, Task task) {
		int vm = -1;
		double min = -1;
		double new_min;// vm with minimum affected tasks;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)
					&& vmList.get(i).getStorage().getCapacity() > 0) {// &&
				// vmList.get(i).getStorage().getCapacity()>0
				double latency = 1;
				double energy = 1;
				if (((DataCenter) vmList.get(i).getHost().getDatacenter())
						.getType() == SimulationParameters.TYPES.CLOUD) {
					latency = 1.6;
					energy = 1.1;
				} else if (((DataCenter) vmList.get(i).getHost().getDatacenter())
						.getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
					energy = 1.4;
				}
				new_min = (orchestrationHistory.get(i).size() + 1) * latency * energy * task.getLength()
						/ vmList.get(i).getMips();
				if (min == -1) { // if it is the first iteration
					min = new_min;
					// if this is the first time, set the first vm as the
					vm = i; // best one
				} else if (min > new_min) { // if this vm has more cpu mips and less waiting tasks
					// idle vm, no tasks are waiting
					min = new_min;
					vm = i;
				}
			}
		}
		// affect the tasks to the vm found
		return vm;
	}
	/************ LatencyAndEnergyAware ************/
	

	/************ weightGreedy ************/
	// https://github.com/wjy491156866/SatEdgeSim/blob/master/SatEdgeSim/edu/weijunyong/satedgesim/TasksOrchestration/DefaultEdgeOrchestrator.java
	private int weightGreedy(String[] architecture, Task task) {
		List<Double> disdelay = new ArrayList<>();
		List<Double> exedelay = new ArrayList<>();
		List<Double> vmnum = new ArrayList<>();
		List<Double> energylim = new ArrayList<>();
		
		architecture = new String[] {"Mist" , "Edge"};
		
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			double localDistance;
			if(((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() != SimulationParameters.TYPES.CLOUD)
				localDistance = ((DataCenter)vmList.get(i).getHost().getDatacenter()).getMobilityManager().distanceTo(task.getOrchestrator());
			else
				localDistance = 99999;
			
			double disdelay_tem = localDistance / SimulationParameters.WAN_PROPAGATION_SPEED;
			disdelay.add(disdelay_tem);
			double exedelay_tem = task.getLength()/vmList.get(i).getMips();
			exedelay.add(exedelay_tem);
			vmnum.add((double)orchestrationHistory.get(i).size());
			double energyuse =10*(Math.log10(((DataCenter) vmList.get(i).getHost().getDatacenter()).getEnergyModel().getTotalEnergyConsumption()));
			energylim.add(energyuse);	
		}
		List<Double> disdelay_stand = new ArrayList<>();
		List<Double> exedelay_stand = new ArrayList<>();
		List<Double> vmnum_stand = new ArrayList<>();
		List<Double> energylim_stand = new ArrayList<>();
		disdelay_stand = standardization(disdelay);
		exedelay_stand = standardization(exedelay);
		vmnum_stand = standardization(vmnum);
		energylim_stand = standardization(energylim);
		
		int vm = -1;
		double min = -1;
		double min_factor;// vm with minimum assigned tasks;
		double a=0.3, b=0.3, c=0.25, d=0.15;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				
				min_factor = a*disdelay_stand.get(i) + b*exedelay_stand.get(i) + c*vmnum_stand.get(i) + d*energylim_stand.get(i);
				if (min == -1) { // if it is the first iteration
					min = min_factor;
					// if this is the first time, set the first vm as the
					vm = i; // best one
				} else if (min > min_factor) { // if this vm has more cpu mips and less waiting tasks
					// idle vm, no tasks are waiting
					min = min_factor;
					vm = i;
				}
			}
		}
		// assign the tasks to the found vm
		return vm;
	}
	

	public List<Double> standardization (List<Double> Pre_standar){
		List<Double> standard = new ArrayList<>();
		double premax = Collections.max(Pre_standar);
		double premin = Collections.min(Pre_standar);
		for(int k=0; k<Pre_standar.size(); k++) {
			double temp =(Pre_standar.get(k)-premin)/(premax-premin);
			standard.add(temp);
		}
		return standard;
	}

	/************ weightGreedy ************/
	

	/************ Test ************/
	protected int test(String[] architecture, Task task) {
		int vm = -1;
		double minTasksCount = -1; 
		double vmMips = 0;
		double min = 0;
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				double weight = 1.1;
				if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == SimulationParameters.TYPES.CLOUD) {
					weight = 1.8;
				} else if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() == SimulationParameters.TYPES.EDGE_DATACENTER) {
					weight = 1.5;
				}
				
				
				int taskRunning = orchestrationHistory.get(i).size() - vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1;
				
				double newMin = weight * taskRunning * (task.getLength() / vmList.get(i).getMips());
				//newMin = weight * (orchestrationHistory.get(i).size() + 1) * (task.getLength() / vmList.get(i).getMips()); 
				//newMin = weight * taskRunning * (orchestrationHistory.get(i).size() + 1) * (task.getLength() / vmList.get(i).getMips()); 
				
				//newMin = 1 / (vmList.get(i).getMips() / taskRunning);
				
				newMin = weight * (task.getLength() / (vmList.get(i).getMips() / taskRunning)) * (vmList.get(i).getCpuPercentUtilization()*20+1);

				if (minTasksCount == -1 || newMin < min) {
					minTasksCount = taskRunning; 
					vmMips = vmList.get(i).getMips();
					min = newMin;
					vm = i;
				}
			}
		}
		
		if(vm == -1) {
			System.err.println("VM no encontrada para el offloading");
		}
		
		return vm;
	}
	/************ Test ************/
	

	/************ Reinforcement Learning ************/
	private int reinforcementLearning(String[] architecture, Task task) {
		// Ejecuto el algoritmo RL para esta tarea
		int accion = rlManager.reinforcementLearning(architecture, task);

		// En funcion de la decisión del algoritmo RL lanzo una politica de offloading u otra
		if (accion == 0) {			
			return local(architecture, task);
		} else if (accion == 1) {			
			String[] architecture2 = { "Mist" };
			return test(architecture2, task);
			//return onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DEVICE);
		} else if (accion == 2) {			
			String[] architecture2 = { "Edge" };
			return test(architecture2, task);
			//return onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DATACENTER);
		} else {			
			String[] architecture2 = { "Cloud" };
			return test(architecture2, task);	
			//return onlyType(architecture, task, SimulationParameters.TYPES.CLOUD);
		}
	}
	
	public RLManager getRLManager() {
		return rlManager;
	}
	/************ Reinforcement Learning ************/
	

	/************ MultiLayer Reinforcement Learning ************/
	private int multilayerreinforcementLearning(String[] architecture, Task task) {
		// Ejecuto el algoritmo RL para esta tarea		
		int accion = multiLayerRLManager.reinforcementLearning(architecture, task);

		// En funcion de la decisión del algoritmo RL lanzo una politica de offloading u otra
		if (accion == 0) {			
			return local(architecture, task);
		} else if (accion == 1) {			
			String[] architecture2 = { "Mist" };
			return test(architecture2, task);
			//return onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DEVICE);
		} else if (accion == 2) {			
			String[] architecture2 = { "Edge" };
			return test(architecture2, task);
			//return onlyType(architecture, task, SimulationParameters.TYPES.EDGE_DATACENTER);
		} else {			
			String[] architecture2 = { "Cloud" };
			return test(architecture2, task);	
			//return onlyType(architecture, task, SimulationParameters.TYPES.CLOUD);
		}
	}
	
	public MultiLayerRLManager getMultiLayerRLManager() {
		return multiLayerRLManager;
	}
	/************ Reinforcement Learning ************/
	

	/************ Fuzzy Logic ************/
	private int fuzzyLogic(Task task) { 
		String fileName = "PureEdgeSim/pruebas/settings/stage1.fcl";
		FIS fis = FIS.load(fileName, true);
		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return -1;
		}
		double vmUsage = 0;
		int count = 0;
		for (int i = 0; i < vmList.size(); i++) {
			if (((DataCenter) vmList.get(i).getHost().getDatacenter()).getType() != SimulationParameters.TYPES.CLOUD) {
				vmUsage += vmList.get(i).getCpuPercentUtilization() * 100;
				count++;
				vmUsage += ((DataCenter) vmList.get(i).getHost().getDatacenter()).getResources().getAvgCpuUtilization();

			}
		}

		// set fuzzy inputs
		fis.setVariable("wan", SimulationParameters.WAN_BANDWIDTH / 1000 - simulationManager.getNetworkModel().getWanUtilization());
		fis.setVariable("tasklength", task.getLength());
		fis.setVariable("delay", task.getMaxLatency());
		fis.setVariable("vm", vmUsage / count);

		// Evaluate
		fis.evaluate();

		// Es mejor hacer offloading a la Cloud
		if (fis.getVariable("offload").defuzzify() > 50) {
			String[] architecture2 = { "Cloud" };
			return increaseLifetime(architecture2, task);
			//return roundRobin(architecture2, task);
		} else { // No es mejor hacer offloading a la Cloud, lo envio al edge y mist
			String[] architecture2 = { "Edge", "Mist" };
			return stage2(architecture2, task);
		}

	}

	private int stage2(String[] architecture2, Task task) {
		double min = -1;
		int vm = -1;
		String fileName = "PureEdgeSim/pruebas/settings/stage2.fcl";
		FIS fis = FIS.load(fileName, true);
		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return -1;
		}
		for (int i = 0; i < vmList.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture2) && vmList.get(i).getStorage().getCapacity() > 0) {
				if (!task.getEdgeDevice().getMobilityManager().isMobile())
					fis.setVariable("vm_local", 0);
				else
					fis.setVariable("vm_local", 0);
				fis.setVariable("vm", (1 - vmList.get(i).getCpuPercentUtilization()) * vmList.get(i).getMips() / 1000);
				fis.evaluate();

				if (min == -1 || min > fis.getVariable("offload").defuzzify()) {
					min = fis.getVariable("offload").defuzzify();
					vm = i;
				}
			}
		}
		return vm;
	}
	/************ Fuzzy Logic ************/
	

	@Override
	public void resultsReturned(Task task) {
		if (task.getStatus() == Status.FAILED) {
			//System.err.println("CustomEdgeOrchestrator, task " + task.getId() + " has been failed, failure reason is: " + task.getFailureReason());
		} else {
			//System.out.println("CustomEdgeOrchestrator, task " + task.getId() + " has been successfully executed");
		}
		
		// Estamos haciendo las pruebas con el algoritmo RL
		if(algorithm.equals("RL")) {
			rlManager.reinforcementFeedback(task);
		} else if(algorithm.equals("RL_MULTILAYER") || algorithm.equals("RL_MULTILAYER_DISABLED") || algorithm.equals("RL_MULTILAYER_EMPTY")) {
			multiLayerRLManager.reinforcementFeedback(task);
		}

	}
	
	
	
}
