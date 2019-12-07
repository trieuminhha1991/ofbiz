import com.olbius.dashboard.ChartModelFactory;

personnelChartType = parameters.personnelChartType;
if(personnelChartType == null){
	personnelChartType = "100";
}
int personnelChartType = Integer.parseInt(personnelChartType);
chartModel = ChartModelFactory.createChartModel(personnelChartType);
chartModel.buildModel(delegator);
model = chartModel.getModel();
context.model = model;