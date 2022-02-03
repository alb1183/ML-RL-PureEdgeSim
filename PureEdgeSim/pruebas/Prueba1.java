package pruebas;

import com.pureedgesim.MainApplication;
import com.pureedgesim.simulationcore.Simulation;

public class Prueba1 extends MainApplication {
	//private static String settingsPath = "PureEdgeSim/pruebas/settings/";
	private static String settingsPath = "PureEdgeSim/pruebas/settings_tiny/";
	private static String outputPath = "PureEdgeSim/pruebas/output/";

	public static void main(String[] args) {
		Simulation sim = new Simulation();
		
		sim.setCustomOutputFolder(outputPath);
		sim.setCustomSettingsFolder(settingsPath);


		sim.setCustomEdgeOrchestrator(CustomEdgeOrchestrator.class);
		//sim.setCustomEnergyModel(CustomEnergyModel.class);
		//sim.setCustomEdgeDataCenters(CustomDataCenter.class);
		
		sim.launchSimulation();
	}


}
