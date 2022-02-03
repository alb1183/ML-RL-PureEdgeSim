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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pureedgesim.datacentersmanager.DataCenter;
import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.SimulationManager;

public class ServersChart extends Chart {

	private List<Double> currentTime = new ArrayList<>();
	
	private List<Double> busyEdgeServers = new ArrayList<>();
	private List<Double> idleEdgeServers = new ArrayList<>();
	
	private List<Double> busyCloudServers = new ArrayList<>();
	private List<Double> idleCloudServers = new ArrayList<>();

	public ServersChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(SimulationParameters.INITIALIZATION_TIME, null, 0.0, null);
	}

	public void update() {
		// Avanzo en el tiempo
		currentTime.add(simulationManager.getSimulation().clock());
		
		Integer idleDevs = 0;
		Integer busyDevs = 0;
		DataCenter datacenter;

		// Edge Servers
		for (int j = SimulationParameters.NUM_OF_CLOUD_DATACENTERS; j < SimulationParameters.NUM_OF_EDGE_DATACENTERS + SimulationParameters.NUM_OF_CLOUD_DATACENTERS; j++) {
			datacenter = simulationManager.getDataCentersManager().getDatacenterList().get(j);
			// If it is an edge data center
			if (datacenter.getType() == SimulationParameters.TYPES.EDGE_DATACENTER) {
				if (datacenter.getResources().isIdle())
					idleDevs++;
				else // If the device is busy
					busyDevs++;
			}
		} 

		busyEdgeServers.add((double)(busyDevs));
		idleEdgeServers.add((double)(idleDevs));

		// Reinicio los contadores
		idleDevs = busyDevs = 0;
		
		// cloud servers
		for (int j = 0 ; j < SimulationParameters.NUM_OF_CLOUD_DATACENTERS; j++) {
			datacenter = simulationManager.getDataCentersManager().getDatacenterList().get(j);
			// If it is an cloud server
			if (datacenter.getType() == SimulationParameters.TYPES.CLOUD) {
				if (datacenter.getResources().isIdle())
					idleDevs++;
				else // If the device is busy
					busyDevs++;
			}
		} 

		busyCloudServers.add((double)(busyDevs));
		idleCloudServers.add((double)(idleDevs));

		BasicStroke dashed1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.50f, new float[] {5.0f, 5.0f}, 5.0f);
		updateSeries(getChart(), "Edge", toArray(currentTime), toArray(busyEdgeServers), SeriesMarkers.NONE, Color.BLACK);
		//updateSeries(getChart(), "Edge Servers Idle", toArray(currentTime), toArray(idleEdgeServers), SeriesMarkers.NONE, Color.BLACK);
		
		BasicStroke dashed2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.50f, new float[] {5.0f, 5.0f}, 0.0f);
		updateSeries(getChart(), "Cloud", toArray(currentTime), toArray(busyCloudServers), SeriesMarkers.NONE, Color.BLACK);
		//updateSeries(getChart(), "Cloud Servers Idle", toArray(currentTime), toArray(idleCloudServers), SeriesMarkers.NONE, Color.BLACK);
		
	}
}
