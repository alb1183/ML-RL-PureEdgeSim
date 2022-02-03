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
package com.pureedgesim.simulationcore;

import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pureedgesim.scenariomanager.SimulationParameters;

public class ChartsGeneratorAvg {

	private List<List<String[]>> records = new ArrayList<>();
	private String fileName;
	private String folder;
	private int number;

	public ChartsGeneratorAvg(String fileName, int number) {
		this.fileName = fileName;
		this.number = number;
		loadFile();
	}

	private void loadFile() {
		try {
			for(int i = 1; i <= number; i++) {
				BufferedReader file = new BufferedReader(new FileReader(fileName + "_" + i + ".csv"));
				String line;
				List<String[]> lin = new ArrayList<>();
				while ((line = file.readLine()) != null) {
					lin.add(line.split(","));
				}
				records.add(lin);
				file.close();
			}
		} catch (Exception e) {
			SimLog.println("Problem reading file.");
		}
	}

	private int getColumnIndex(String name) {
		for (int j = 0; j < records.get(0).get(0).length; j++) {
			if (records.get(0).get(0)[j].trim().equals(name.trim())) {
				return j;
			}
		}
		return -1;
	}

	public void displayChart(String x_series, String y_series, String y_series_label, String folder) {
		this.folder = folder;
		// Create charts that are filtered by architectures (byAlgorithm = true)
		//generateChart(x_series, y_series, y_series_label, true);
		// Create the charts filtered by algorithms (byAlgorithm = false), in order to compare the orchestration algorithms
		generateChart(x_series, y_series, y_series_label, false);

	}

	public void generateChart(String x_series, String y_series, String y_series_label, boolean byAlgorithms) {
		XYChart chart;
		for (int i = 0; i < (byAlgorithms ? SimulationParameters.ORCHESTRATION_AlGORITHMS.length : SimulationParameters.ORCHESTRATION_ARCHITECTURES.length); i++) {
			chart = initChart(x_series, y_series, y_series_label, getArray(byAlgorithms)[i]);
			for (int j = 0; j < (byAlgorithms ? SimulationParameters.ORCHESTRATION_ARCHITECTURES.length	: SimulationParameters.ORCHESTRATION_AlGORITHMS.length); j++) {
				double[] xData = toArray(getColumn(x_series, SimulationParameters.ORCHESTRATION_ARCHITECTURES[(byAlgorithms ? j : i)], SimulationParameters.ORCHESTRATION_AlGORITHMS[(byAlgorithms ? i : j)]));
				double[] yData = toArray(getColumn(y_series, SimulationParameters.ORCHESTRATION_ARCHITECTURES[(byAlgorithms ? j : i)], SimulationParameters.ORCHESTRATION_AlGORITHMS[(byAlgorithms ? i : j)]));

				XYSeries series = chart.addSeries(getArray(!byAlgorithms)[j], xData, yData);
				series.setMarker(SeriesMarkers.CIRCLE); // Marker type: circle,rectangle, diamond..
				series.setLineStyle(new BasicStroke());
			}
			// Save the chart
			saveBitmap(chart, (byAlgorithms ? "Architectures" : "Algorithms") + folder + "/", y_series + "__" + getArray(byAlgorithms)[i]);
		}
	}

	private String[] getArray(boolean byAlgorithms) {
		return (byAlgorithms ? SimulationParameters.ORCHESTRATION_AlGORITHMS : SimulationParameters.ORCHESTRATION_ARCHITECTURES);
	}

	private XYChart initChart(String x_series, String y_series, String y_series_label, String title) {
		XYChart chart = new XYChartBuilder().height(400).width(600).theme(ChartTheme.Matlab).xAxisTitle(x_series).yAxisTitle(y_series_label).build();
		chart.setTitle(y_series + " (" + title + ")");
		chart.getStyler().setLegendVisible(true);
		return chart;
	}

	private void saveBitmap(XYChart chart, String folder, String name) {
		try {
			File file = new File(new File(fileName + "_1.csv").getParent() + "/Final results/" + folder);
			file.mkdirs();
			BitmapEncoder.saveBitmapWithDPI(chart, file.getPath() + "/" + name.replace("/", " per "), BitmapFormat.PNG,	300);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private List<Double> getColumn(String name, String orch, String alg) {
		List<Double> list = new ArrayList<>();
		int column = getColumnIndex(name);
		for (int line = 1; line < records.get(0).size(); line++) {
			if (records.get(0).get(line)[0].trim().equals(orch.trim()) && records.get(0).get(line)[1].trim().equals(alg.trim())) {
				Double d = 0.0;

				for(int i = 1; i <= number; i++) {
					d += Double.parseDouble(records.get(i-1).get(line)[column]);
				}
				d /= number;
				
				list.add(d);
			}
		}
		
		return list;
	}

	private double[] toArray(List<Double> list) {
		double[] results = new double[list.size()];
		for (int i = 0; i < list.size(); i++)
			results[i] = list.get(i);
		return results;
	}

	public void generate() { 
		generateTasksCharts();
		generateNetworkCharts();
		generateCpuCharts();
		generateEnergyCharts();
	}

	private void generateEnergyCharts() {
		displayChart("Edge devices count", "Energy consumption (W)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Average energy consumption (W/Data center)", "Consumed energy (W)",	"/Energy");
		displayChart("Edge devices count", "Cloud energy consumption (W)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Average Cloud energy consumption (W/Data center)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Edge energy consumption (W)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Average Edge energy consumption (W/Data center)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Mist energy consumption (W)", "Consumed energy (W)", "/Energy");
		displayChart("Edge devices count", "Average Mist energy consumption (W/Device)", "Consumed energy (W)", "/Energy");

		displayChart("Edge devices count", "Dead devices count", "Count", "/Edge Devices");
		displayChart("Edge devices count", "Average remaining power (Wh)", "Remaining energy (Wh)", "/Edge Devices");
		displayChart("Edge devices count", "Average remaining power (%)", "Remaining energy (%)", "/Edge Devices");
		displayChart("Edge devices count", "First edge device death time (s)", "Time (s)", "/Edge Devices");

	}

	private void generateCpuCharts() {
		displayChart("Edge devices count", "Average VM CPU usage (%)", "CPU utilization (%)", "/CPU Utilization");
		displayChart("Edge devices count", "Average VM CPU usage (Cloud) (%)", "CPU utilization (%)", "/CPU Utilization");
		displayChart("Edge devices count", "Average VM CPU usage (Edge) (%)", "CPU utilization (%)", "/CPU Utilization");
		displayChart("Edge devices count", "Average VM CPU usage (Mist) (%)", "CPU utilization (%)", "/CPU Utilization");
	}

	private void generateNetworkCharts() {
		displayChart("Edge devices count", "Network usage (s)", "Time (s)", "/Network");
		displayChart("Edge devices count", "Wan usage (s)", "Time (s)", "/Network");
		displayChart("Edge devices count", "Average bandwidth per task (Mbps)", "Bandwidth (Mbps)", "/Network");
		if (SimulationParameters.ENABLE_REGISTRY) {
			displayChart("Edge devices count", "Containers wan usage (s)", "Time (s)", "/Network");
			displayChart("Edge devices count", "Containers lan usage (s)", "Time (s)", "/Network");
		}
	}

	private void generateTasksCharts() {
		displayChart("Edge devices count", "Average waiting time (s)", "Time (s)", "/Delays");
		displayChart("Edge devices count", "Average execution delay (s)", "Time (s)", "/Delays");
		displayChart("Edge devices count", "Average total time (s)", "Time (s)", "/Delays");
		displayChart("Edge devices count", "Average real total time (s)", "Time (s)", "/Delays");
		
		displayChart("Edge devices count", "Average wasted waiting time (s)", "Time (s)", "/Delays");
		displayChart("Edge devices count", "Average wasted execution delay (s)", "Time (s)", "/Delays");
		displayChart("Edge devices count", "Average wasted total time (s)", "Time (s)", "/Delays");

		displayChart("Edge devices count", "Tasks successfully executed", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks failed (delay)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks failed (device dead)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks failed (mobility)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks not generated due to the death of devices", "Number of tasks", "/Tasks");

		displayChart("Edge devices count", "Total tasks executed (Cloud)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks successfully executed (Cloud)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Total tasks executed (Edge)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks successfully executed (Edge)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Total tasks executed (Mist)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks successfully executed (Mist)", "Number of tasks", "/Tasks");
		displayChart("Edge devices count", "Tasks success rate", "Success Rate (%)", "/Tasks");

	}

}
