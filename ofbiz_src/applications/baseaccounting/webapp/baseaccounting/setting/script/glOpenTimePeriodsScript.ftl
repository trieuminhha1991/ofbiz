<script type="text/javascript" language="Javascript">
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
	uiLabelMap.BACCRefresh = "${StringUtil.wrapString(uiLabelMap.BACCRefresh)}";
	uiLabelMap.BACCaccIsClosed = "${StringUtil.wrapString(uiLabelMap.BACCIsClosed)}";
	uiLabelMap.BACCcloseTimesPeriodsError = "${StringUtil.wrapString(uiLabelMap.BACCCloseTimesPeriodsError)}";
	uiLabelMap.BACCcloseTimesPeriodsSuccess = "${StringUtil.wrapString(uiLabelMap.BACCCloseTimesPeriodsSuccess)}";
    uiLabelMap.BACCPleaseChooseAcc = "${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc)}";
    uiLabelMap.BACCSearchDropDownList = "${StringUtil.wrapString(uiLabelMap.BACCSearchDropDownList)}";
    uiLabelMap.BACCConfirmClosingPeriod = "${StringUtil.wrapString(uiLabelMap.BACCConfirmClosingPeriod )}";
    uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit )}";
    uiLabelMap.CommonClose= "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
    <#assign periodTypeListX = delegator.findList("PeriodType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FISCAL_ACCOUNT"), null, null, null, false) />
	var dataPT = new Array();
	<#list periodTypeListX as periodType>
		<#assign description = StringUtil.wrapString(periodType.get("description", locale)) />
		var row = {};
		row['description'] = "${description}";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>
	
	var parentPeriodRenderer = function (row, column, value) {
        if (value.indexOf('#') != -1) {
            value = value.substring(0, value.indexOf('#'));
        }
        var fb = false;
        for(i=0;i<dataOtp.length;i++){
        	if(dataOtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataOtp[i].periodName + "</span>";
        	}
        };
        for(i=0;i<dataCtp.length;i++){
        	if(dataCtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span" + dataCtp[i].periodName + "</span>";
        	}
        };
        return "<span class='custom-style-word'>" + value + "</span>";
    };
	
    String.prototype.replaceAll = function (find, replace) {
	    var str = this;
	    return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
    };
    
    var dataOOtp = new Array();
	dataOOtp = [
				{
					'customTimePeriodId' : ' ',
					'periodName' : ' '
				},
			<#list listTimePeriods as op>
				{
					'customTimePeriodId' : '${StringUtil.wrapString(op.customTimePeriodId?if_exists)}',
					'periodName' : '${op.customTimePeriodId?if_exists}' + ':' + '${StringUtil.wrapString(op.periodName?default(""))}' + '(' +  '${op.fromDate?string?if_exists}'.replaceAll('-','/') + '-' + '${op.thruDate?string?if_exists}'.replaceAll('-','/') + ')'	
				},
			</#list>
	]
	
	var dataOtp = new Array();
	dataOtp = [
		{
			'customTimePeriodId' : '',
			'periodName' : ''
		},
		<#list listTimePeriods as op>
			{
				'customTimePeriodId' : '${op.customTimePeriodId?if_exists}',
				'periodName' : 	"<span >[ ${op.customTimePeriodId?if_exists}]" + ":" + "${StringUtil.wrapString(op.get("periodName",locale)?if_exists)}" + ":" + "<span class='bg-trendcolor'>${op.fromDate?if_exists?string["dd/MM/yyyy"]}"+ " - " +  "${op.thruDate?if_exists?string["dd/MM/yyyy"]}</span>"
			},
		</#list>
	];
</script>