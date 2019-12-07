<#if parameters.reportTypeId == "9000">
	${screens.render("component://delys/widget/accounting/accounting/ReportFinancialSummaryScreens.xml#BalanceSheetReport")}
</#if>
<#if parameters.reportTypeId == "9001">
	${screens.render("component://delys/widget/accounting/accounting/ReportFinancialSummaryScreens.xml#IncomeStatementReport")}
</#if>
<#if parameters.reportTypeId == "9002">
	${screens.render("component://delys/widget/accounting/accounting/ReportFinancialSummaryScreens.xml#CashflowReport")}
</#if>
<#if parameters.reportTypeId == "9003">
	${screens.render("component://delys/widget/accounting/accounting/ReportFinancialSummaryScreens.xml#DemonstrationReport")}
</#if>