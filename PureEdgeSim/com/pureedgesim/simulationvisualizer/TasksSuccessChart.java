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

import com.pureedgesim.simulationcore.SimulationManager;

public class TasksSuccessChart extends Chart {

	private List<Double> tasksFailedList = new ArrayList<>();
	private List<Double> tasksTotalFailedList = new ArrayList<>();
	private List<Double> tasksTotalFailedSimLogList = new ArrayList<>();
	
	public TasksSuccessChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(0.0, null, null, 100.0);
	}

	public void update() {
		if (((int) Math.floor(simulationManager.getSimulation().clock() / 30)) != clock) {
			clock = (int) Math.floor(simulationManager.getSimulation().clock() / 30);
			
			double tasksFailed = 100.0 - simulationManager.getFailureRate();
			double tasksFailedTotal = 100.0 - simulationManager.getTotalFailuresRate();
			double tasksFailedSimLogTotal = 100.0 - simulationManager.getTotalFailuresRateSimLog();
			
			double[] time = new double[clock];
			for (int i = 0; i < clock; i++)
				time[i] = i*0.5;
			
			tasksFailedList.add(tasksFailed);
			tasksTotalFailedList.add(tasksFailedTotal);
			tasksTotalFailedSimLogList.add(tasksFailedSimLogTotal);
			
			updateSeries(getChart(), "Actual rate", time, toArray(tasksFailedList), SeriesMarkers.NONE, Color.BLACK);
			updateSeries(getChart(), "Total rate", time, toArray(tasksTotalFailedList), SeriesMarkers.NONE, Color.BLACK);
			updateSeries(getChart(), "SimLog rate", time, toArray(tasksTotalFailedSimLogList), SeriesMarkers.NONE, Color.BLACK);
		}
	}
}
