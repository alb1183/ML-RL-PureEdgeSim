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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.SimulationManager;

public class TasksFailedChart extends Chart {

	private List<Double> currentTime = new ArrayList<>();
	private List<Double> tasksTotalList = new ArrayList<>();
	private List<Double> tasksTotalSuccessList = new ArrayList<>();
	private List<Double> tasksTotalFailedList = new ArrayList<>();
	private List<Double> tasksTotalFailedLatencyList = new ArrayList<>();
	private List<Double> tasksTotalFailedBatteryList = new ArrayList<>();
	private List<Double> tasksTotalFailedMobilityList = new ArrayList<>();
	private List<Double> tasksTotalFailedResourceList = new ArrayList<>();
	
	public TasksFailedChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(SimulationParameters.INITIALIZATION_TIME, null, 0.0, null);
	}

	public void update() {
		// Avanzo en el tiempo
		currentTime.add(simulationManager.getSimulation().clock());

		tasksTotalList.add((double) simulationManager.getTasksCount());
		tasksTotalSuccessList.add((double) simulationManager.getExecutedTasksCount());
		tasksTotalFailedList.add((double) simulationManager.getTasksFailedCount());
		tasksTotalFailedLatencyList.add((double) simulationManager.getTasksFailedDueLatency());
		tasksTotalFailedBatteryList.add((double) simulationManager.getTasksFailedDueBattery());
		tasksTotalFailedMobilityList.add((double) simulationManager.getTasksFailedDueMobility());
		tasksTotalFailedResourceList.add((double) simulationManager.getTasksFailedDueLackOfRessources());

		updateSeries(getChart(), "Total tasks", toArray(currentTime), toArray(tasksTotalList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Executed task", toArray(currentTime), toArray(tasksTotalSuccessList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Failed task", toArray(currentTime), toArray(tasksTotalFailedList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Failed due latency", toArray(currentTime), toArray(tasksTotalFailedLatencyList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Failed due battery", toArray(currentTime), toArray(tasksTotalFailedBatteryList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Failed due mobilty", toArray(currentTime), toArray(tasksTotalFailedMobilityList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Failed due resource", toArray(currentTime), toArray(tasksTotalFailedResourceList), SeriesMarkers.NONE, Color.BLACK);
	}
}
