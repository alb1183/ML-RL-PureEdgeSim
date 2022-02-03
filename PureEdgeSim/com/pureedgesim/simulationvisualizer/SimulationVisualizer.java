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
package com.pureedgesim.simulationvisualizer;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.SimulationManager;
import com.pureedgesim.simulationcore.Simulation;

public class SimulationVisualizer {
	private JFrame simulationResultsFrame;
	private SwingWrapper<XYChart> swingWrapper;
	private SimulationManager simulationManager;
	private Chart mapChart;
	private Chart cpuUtilizationChart;
	private WanChart networkUtilizationChart;
	private TasksSuccessChart tasksSuccessChart;
	private TasksFailedChart tasksFailedChart;
	private EdgeDevicesChart edgeDeviceChart;
	private ServersChart serversChart;
	private EnergyChart energyChart;
	private DelayChart delayChart;
	private RLChart rlChart;
	private MultiRLChart multiRLChart;
	private List<Chart> charts = new ArrayList<Chart>();
	private boolean firstTime = true;

	public SimulationVisualizer(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
		
		mapChart = new MapChart("Simulation map", "Width (meters)", "Length (meters)", simulationManager);
		cpuUtilizationChart = new CPUChart("CPU utilization", "Time (s)", "Utilization (%)", simulationManager);
		networkUtilizationChart = new WanChart("Network utilization", "Time (s)", "Utilization (Mbps)",	simulationManager);
		tasksSuccessChart = new TasksSuccessChart("Tasks success rate", "Time (minutes)", "Success rate (%)", simulationManager);
		tasksFailedChart = new TasksFailedChart("Tasks failures", "Time (s)", "Tasks number", simulationManager);
		edgeDeviceChart = new EdgeDevicesChart("Edge Devices", "Time (s)", "Devices number", simulationManager);
		serversChart = new ServersChart("Busy Servers", "Time (s)", "Devices number", simulationManager);
		energyChart = new EnergyChart("Energy", "Time (s)", "Power (W)", simulationManager);
		delayChart = new DelayChart("Delays", "Simulation Time (s)", "Time (s)", simulationManager);
		rlChart = new RLChart("Rewards", "Time (s)", "Reward", simulationManager);
		multiRLChart = new MultiRLChart("Tasks queries", "Time (s)", "Tasks", simulationManager);
		
		charts.add(mapChart);
		charts.add(cpuUtilizationChart);
		charts.add(energyChart);
		charts.add(tasksSuccessChart);
		charts.add(tasksFailedChart);
		charts.add(delayChart);
		charts.add(edgeDeviceChart);
		charts.add(serversChart);
		if(simulationManager.getScenario().getStringOrchAlgorithm().equals("RL"))
			charts.add(rlChart);
		else if(simulationManager.getScenario().getStringOrchAlgorithm().equals("RL_MULTILAYER") || simulationManager.getScenario().getStringOrchAlgorithm().equals("RL_MULTILAYER_DISABLED") || simulationManager.getScenario().getStringOrchAlgorithm().equals("RL_MULTILAYER_EMPTY"))
			charts.add(multiRLChart);
		else
			charts.add(networkUtilizationChart);
	}

	public void updateCharts() {
		if (firstTime) {
			swingWrapper = new SwingWrapper<>(charts.stream().map(Chart::getChart).collect(Collectors.toList()));
			simulationResultsFrame = swingWrapper.displayChartMatrix(); // Display charts
			simulationResultsFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			simulationResultsFrame.setPreferredSize(new Dimension(1920, 1080));
		}
		firstTime = false;
		repaint();

		// Display simulation time
		double time = this.simulationManager.getSimulation().clock() - SimulationParameters.INITIALIZATION_TIME;
		simulationResultsFrame.setTitle("Simulation time = " + ((int) time / 60) + " min : " + ((int) time % 60)
				+ " seconds  -  number of edge devices = " + simulationManager.getScenario().getDevicesCount()
				+ " -  Architecture = " + simulationManager.getScenario().getStringOrchArchitecture()
				+ " -  Algorithm = " + simulationManager.getScenario().getStringOrchAlgorithm());
	}

	private void repaint() {
		charts.forEach(chart -> chart.update());
		simulationResultsFrame.repaint();
	}

	public void close() {
		simulationResultsFrame.dispose();
	}

	public void saveCharts() throws IOException {
		String folderNameSimulation = Simulation.getOutputFolder() + "/"
				+ simulationManager.getSimulationLogger().getSimStartTime() + "/simulation_"
				+ simulationManager.getSimulationId();
		String folderNameIteration = "iteration_" + simulationManager.getIterationId() + "__" + simulationManager.getScenario().toString();
		String folderName = folderNameSimulation + "/" + folderNameIteration;
		
		new File(folderName).mkdirs();
		
		BitmapEncoder.saveBitmapWithDPI(mapChart.getChart(), folderName + "/map_chart", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(networkUtilizationChart.getChart(), folderName + "/network_usage", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(cpuUtilizationChart.getChart(), folderName + "/cpu_usage", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(tasksSuccessChart.getChart(), folderName + "/tasks_success_rate", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(tasksFailedChart.getChart(), folderName + "/tasks_failed", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(edgeDeviceChart.getChart(), folderName + "/edge_devices", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(serversChart.getChart(), folderName + "/busy_servers", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(delayChart.getChart(), folderName + "/delays", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(rlChart.getChart(), folderName + "/rl_avg_reward", BitmapFormat.PNG, 300);
		BitmapEncoder.saveBitmapWithDPI(multiRLChart.getChart(), folderName + "/rl_multilayer", BitmapFormat.PNG, 300);
		
		List<org.knowm.xchart.internal.chartpart.Chart> sCharts = new ArrayList<org.knowm.xchart.internal.chartpart.Chart>();
		for (Chart chart : charts) {
			sCharts.add(chart.getChart());
		}
		BitmapEncoder.saveBitmap(sCharts, 3, 3, folderName + "/final", BitmapFormat.PNG);
		BitmapEncoder.saveBitmap(sCharts, 3, 3, folderNameSimulation + "/" + folderNameIteration + "_final", BitmapFormat.PNG);

	}

}
