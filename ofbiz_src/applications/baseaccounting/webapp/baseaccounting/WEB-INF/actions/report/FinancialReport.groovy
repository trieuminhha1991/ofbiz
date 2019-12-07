String reportTypeId = parameters.reportTypeId;
String titleProperty = "";
String tabButtonItem = "";

if (reportTypeId == '9000') {
	titleProperty = "BACCFSBalanceSheet";
	tabButtonItem = "FSBalanceSheet";
} else if (reportTypeId == '9001') {
	titleProperty = "BACCFSIncomeStatement";
	tabButtonItem = "FSIncomeStatement";
} else if (reportTypeId == '9002') {
	titleProperty = "BACCFSCashflow";
	tabButtonItem = "FSCashflowReport";
}

context.titleProperty = titleProperty;
context.tabButtonItem = tabButtonItem;