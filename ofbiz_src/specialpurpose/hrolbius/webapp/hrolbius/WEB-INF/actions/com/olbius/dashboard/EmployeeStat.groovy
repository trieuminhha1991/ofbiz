import com.olbius.dashboard.ChartModelFactory;

emplChartType = parameters.emplChartType;
if(emplChartType == null){
	emplChartType = "1";
}
int chartTypeTmp = Integer.parseInt(emplChartType);
chartModel = ChartModelFactory.createChartModel(chartTypeTmp);
chartModel.buildModel(delegator);
model = chartModel.getModel();
context.model = model;