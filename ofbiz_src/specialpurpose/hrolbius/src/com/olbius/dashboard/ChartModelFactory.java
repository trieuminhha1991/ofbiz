package com.olbius.dashboard;

public class ChartModelFactory {
	public static ChartModel createChartModel(int chartType) throws Exception{
		ChartModel chartModel = null;
		switch(chartType){
			case 1:
				chartModel = new GenderChartModel();
				break;
			case 2:
				chartModel = new AgreementChartModel();
				break;
			case 3:
				chartModel = new QualificationChartModel();
				break;
			case 100:
				chartModel = new PersonnelChartModel();
				break;
			default:
				throw new Exception("Chart Type is not exist");
		}
		return chartModel;
	}
}
