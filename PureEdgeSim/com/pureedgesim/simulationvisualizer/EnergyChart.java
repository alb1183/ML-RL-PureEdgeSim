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

public class EnergyChart extends Chart {

	private List<Double> currentTime = new ArrayList<>();
	private List<Double> energyConsumptionList = new ArrayList<>();
	private List<Double> averageEnergyConsumptionList = new ArrayList<>();
	private List<Double> averageDevicesEnergyConsumptionList = new ArrayList<>();
	private List<Double> averageEdgeEnergyConsumptionList = new ArrayList<>();
	private List<Double> averageCloudEnergyConsumptionList = new ArrayList<>();
	
	public EnergyChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(SimulationParameters.INITIALIZATION_TIME, null, 0.0, null);
	}

	public void update() {
		// Avanzo en el tiempo
		currentTime.add(simulationManager.getSimulation().clock());
		
		double energyConsumption = 0;
		double cloudEnConsumption = 0;
		double mistEnConsumption = 0;
		double edgeEnConsumption = 0;
		List<? extends DataCenter> datacentersList = simulationManager.getDataCentersManager().getDatacenterList();

		for (DataCenter dc : datacentersList) {
			if (dc.getType() == SimulationParameters.TYPES.CLOUD) {
				cloudEnConsumption += dc.getEnergyModel().getTotalEnergyConsumption();
			} else if (dc.getType() == SimulationParameters.TYPES.EDGE_DATACENTER) {
				edgeEnConsumption += dc.getEnergyModel().getTotalEnergyConsumption();
			} else if (dc.getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
				mistEnConsumption += dc.getEnergyModel().getTotalEnergyConsumption();
			}
		}
		
		energyConsumption = cloudEnConsumption + edgeEnConsumption + mistEnConsumption;

		double averageCloudEnConsumption = cloudEnConsumption / SimulationParameters.NUM_OF_CLOUD_DATACENTERS;
		double averageEdgeEnConsumption = edgeEnConsumption / SimulationParameters.NUM_OF_EDGE_DATACENTERS;
		double averageMistEnConsumption = mistEnConsumption / simulationManager.getScenario().getDevicesCount();
		double averageEnConsumption = energyConsumption / datacentersList.size();
		//double averageEnConsumption = averageCloudEnConsumption + averageEdgeEnConsumption + averageMistEnConsumption;

		energyConsumptionList.add(energyConsumption);
		averageEnergyConsumptionList.add(averageEnConsumption);
		averageDevicesEnergyConsumptionList.add(averageMistEnConsumption);
		averageEdgeEnergyConsumptionList.add(averageEdgeEnConsumption);
		averageCloudEnergyConsumptionList.add(averageCloudEnConsumption);

		//updateSeries(getChart(), "Energy Consumption", toArray(currentTime), toArray(energyConsumptionList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Avg. Energy Cons.", toArray(currentTime), toArray(averageEnergyConsumptionList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Avg. Devices Cons.", toArray(currentTime), toArray(averageDevicesEnergyConsumptionList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Avg. Edge Cons.", toArray(currentTime), toArray(averageEdgeEnergyConsumptionList), SeriesMarkers.NONE, Color.BLACK);
		updateSeries(getChart(), "Avg. Cloud Cons.", toArray(currentTime), toArray(averageCloudEnergyConsumptionList), SeriesMarkers.NONE, Color.BLACK);
	}
}
