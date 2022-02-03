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

import com.pureedgesim.datacentersmanager.DataCenter;
import com.pureedgesim.scenariomanager.SimulationParameters;
import com.pureedgesim.simulationcore.SimulationManager;

public class EdgeDevicesChart extends Chart {

	private List<Double> currentTime = new ArrayList<>();
	private List<Double> aliveDevices = new ArrayList<>();
	private List<Double> deadDevices = new ArrayList<>();
	private List<Double> busyDevices = new ArrayList<>();
	private List<Double> idleDevices = new ArrayList<>();

	public EdgeDevicesChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(SimulationParameters.INITIALIZATION_TIME, null, 0.0, null);
	}

	public void update() {
		// Avanzo en el tiempo
		currentTime.add(simulationManager.getSimulation().clock());
		
		// Pongo a cero todos los contadores
		Integer totalDevs = 0;
		Integer	deadDevs = 0; 
		Integer idleDevs = 0;
		Integer busyDevs = 0;
		DataCenter datacenter;

		for (int i = SimulationParameters.NUM_OF_EDGE_DATACENTERS + SimulationParameters.NUM_OF_CLOUD_DATACENTERS; i < simulationManager.getDataCentersManager().getDatacenterList().size(); i++) {
			datacenter = simulationManager.getDataCentersManager().getDatacenterList().get(i);
			// If it is an edge device
			if (datacenter.getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
				if (datacenter.isDead())
					deadDevs++;
				else if (datacenter.getResources().isIdle())
					idleDevs++;
				else // If the device is busy
					busyDevs++;
				
				totalDevs++;
			}
		} 

		aliveDevices.add((double)(totalDevs-deadDevs));
		deadDevices.add((double)(deadDevs));
		busyDevices.add((double)(busyDevs));
		idleDevices.add((double)(idleDevs));


		updateSeries(getChart(), "Alive", toArray(currentTime), toArray(aliveDevices), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Dead", toArray(currentTime), toArray(deadDevices), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Busy", toArray(currentTime), toArray(busyDevices), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Idle", toArray(currentTime), toArray(idleDevices), SeriesMarkers.NONE, Color.BLACK);
		
	}
}
