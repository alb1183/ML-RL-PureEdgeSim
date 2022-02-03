package pruebas;

import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.ChartsGenerator;
import com.pureedgesim.simulationcore.ChartsGeneratorAvg;
import com.pureedgesim.simulationcore.SimLog;

public class generarGraficas {
	
	public static void main(String[] args) {
		/*SimLog.println("Main- Saving charts...");
		
		SimulationParameters.ORCHESTRATION_ARCHITECTURES = "ALL".split(",");
		SimulationParameters.ORCHESTRATION_AlGORITHMS = "ROUND_ROBIN,TRADE_OFF,TEST,RL,RL_MULTILAYER_DISABLED,RL_MULTILAYER".split(",");
		
		String ficheroCSV = "C:\\Users\\alberto\\workspace\\PureEdgeSim-master\\PureEdgeSim\\pruebas\\output\\test\\Sequential_simulation.csv";
		
		ChartsGenerator chartsGenerator = new ChartsGenerator(ficheroCSV);
		chartsGenerator.generate();*/
		
		
		SimLog.println("Main- Saving charts...");
		
		SimulationParameters.ORCHESTRATION_ARCHITECTURES = "ALL".split(",");
		SimulationParameters.ORCHESTRATION_AlGORITHMS = "TEST,RL_MULTILAYER_DISABLED,RL_MULTILAYER_EMPTY,RL_MULTILAYER".split(",");
		
		String ficheroCSV = "C:\\Users\\alberto\\workspace\\PureEdgeSim-master\\PureEdgeSim\\pruebas\\output\\avg\\Sequential_simulation";
		
		ChartsGeneratorAvg chartsGeneratorAvg = new ChartsGeneratorAvg(ficheroCSV, 10);
		chartsGeneratorAvg.generate();

		SimLog.println("Done");
	}

}
