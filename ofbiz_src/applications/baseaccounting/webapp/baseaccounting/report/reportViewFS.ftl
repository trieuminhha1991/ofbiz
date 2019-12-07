<#if parameters.reportTypeId == "9000">
	${screens.render("component://baseaccounting/widget/ReportScreens.xml#BalanceSheetReport")}
</#if>
<#if parameters.reportTypeId == "9001">
	${screens.render("component://baseaccounting/widget/ReportScreens.xml#IncomeStatementReport")}
</#if>
<#if parameters.reportTypeId == "9002">
	${screens.render("component://baseaccounting/widget/ReportScreens.xml#CashflowReport")}
</#if>
<#if parameters.reportTypeId == "9003">
	${screens.render("component://baseaccounting/widget/ReportScreens.xml#DemonstrationReport")}
</#if>