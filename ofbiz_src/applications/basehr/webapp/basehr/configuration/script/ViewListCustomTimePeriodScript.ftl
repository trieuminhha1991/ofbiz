<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: "${periodType.periodTypeId}",
			description: "${StringUtil.wrapString(periodType.description?if_exists)}",
		},
		</#list>
	</#if>
];

<#assign calendar = Static["java.util.Calendar"].getInstance()/>
<#assign year = calendar.get(Static["java.util.Calendar"].YEAR)/>
globalVar.year = ${year};
globalVar.monthNames = ["${StringUtil.wrapString(uiLabelMap.CommonJanuary)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonFebruary)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonMarch)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonApril)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonMay)}", 
                        "${StringUtil.wrapString(uiLabelMap.CommonJune)}",
                  		"${StringUtil.wrapString(uiLabelMap.CommonJuly)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonAugust)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonSeptember)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonOctober)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonNovember)}", 
                  		"${StringUtil.wrapString(uiLabelMap.CommonDecember)}"
                ];
<#assign addType = "popup"/>
<#assign alternativeAddPopup = "alternativeAddPopup"/>
var globalObject = (function(){
	var renderJqxTreeGridToolbar = function(toolbar){
		toolbar.html("");
		<#assign id = "jqxCustomTimePeriod"/>
    	var grid = $('#${id}');
        var me = this;
        <#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
            <@renderJqxTitle titlePropertyTmp=titleProperty id=id/>
        <#elseif customTitleProperties?exists && customTitleProperties != "">
            <@renderJqxTitle titlePropertyTmp=customTitleProperties id=id/>
        <#else>
            var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
        </#if>
        
        toolbar.append(jqxheader);
        var container = $('#toolbarButtonContainer${id}');
        var maincontainer = $("#toolbarcontainer${id}");
        Grid.createAddRowButton(
        	grid, container, "${uiLabelMap.wgaddnew}", {
        		type: "${addType}",
        		container: $("#${alternativeAddPopup}"),
        	}
        );
	};
	return {
		renderJqxTreeGridToolbar: renderJqxTreeGridToolbar
	}
}());

var uiLabelMap = {};
uiLabelMap.HRCustomTimePeriodId = "${StringUtil.wrapString(uiLabelMap.HRCustomTimePeriodId)}";
uiLabelMap.HRPeriodName = "${StringUtil.wrapString(uiLabelMap.HRPeriodName)}";
uiLabelMap.CommonFromDate = "${StringUtil.wrapString(uiLabelMap.CommonFromDate)}";
uiLabelMap.CommonThruDate = "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}";
uiLabelMap.CommonPeriodType = "${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateNewPayrollPeriodConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewPayrollPeriodConfirm)}";
uiLabelMap.HRContainSpecialSymbol = "${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}";
</script>