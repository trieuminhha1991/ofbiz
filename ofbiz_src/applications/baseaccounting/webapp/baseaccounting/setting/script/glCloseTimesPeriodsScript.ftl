<script type="text/javascript" language="Javascript">
	String.prototype.replaceAll = function (find, replace) {
		    var str = this;
		    return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
	};
	<#assign periodTypeListX = delegator.findList("PeriodType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FISCAL_ACCOUNT"), null, null, null, false) />
	var dataPT = new Array();
	<#list periodTypeListX as periodType>
		<#assign description = StringUtil.wrapString(periodType.get("description", locale)) />
		var row = {};
		row['description'] = "${description}";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>
    
    var dataCtp = new Array();
	dataCtp = [
				{
					'customTimePeriodId' : '',
					'periodName' : ''
				},
			<#list listTimePeriods as op>
				{
					'customTimePeriodId' : '${StringUtil.wrapString(op.customTimePeriodId?if_exists)}',
					'periodName' : "${op.customTimePeriodId?if_exists} " + ":" + "${StringUtil.wrapString(op.periodName?default(""))}" + "(" +  "${op.fromDate?string?if_exists}".replaceAll('-','/') + "-" + "${op.thruDate?string?if_exists}".replaceAll('-','/') + ")"	
				},
			</#list>
	]
    
    var parentPeriodRenderer = function (row, column, value) {
        if (value.indexOf('#') != -1) {
            value = value.substring(0, value.indexOf('#'));
        }
        var fb = false;
         for(i=0;i<dataCtp.length;i++){
        	if(dataCtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataCtp[i].periodName + "</span>";
        	}
        };
        for(i=0;i<dataCtp.length;i++){
        	if(dataCtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataCtp[i].periodName + "</span>";
        	}
        };
        return "<span>" + value + "</span>";
    };
    
</script>