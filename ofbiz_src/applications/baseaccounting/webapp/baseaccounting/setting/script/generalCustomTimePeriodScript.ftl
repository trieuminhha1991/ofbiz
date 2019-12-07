<script type="text/javascript" language="Javascript">
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
	uiLabelMap.OrganizationParty = '${StringUtil.wrapString(uiLabelMap.BACCOrganizationParty)}';
	uiLabelMap.accPeriodPartyTypeId = '${StringUtil.wrapString(uiLabelMap.BACCPeriodPartyTypeId)}';
	uiLabelMap.organizationName = '${StringUtil.wrapString(uiLabelMap.BACCOrganizationName)}';

	<#assign periodTypeList = delegator.findList("PeriodType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FISCAL_ACCOUNT"), null, null, null, false) />
	var dataPT = new Array();
	<#list periodTypeList as periodType>
		<#assign description = StringUtil.wrapString(periodType.get("description", locale)) />
		var row = {};
		row['description'] = "<span class='custom-style-word'>${description}</span>";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>

	var dataOtp = new Array();
	dataOtp = [
		{
			'customTimePeriodId' : '',
			'periodName' : ''
		},
		<#list openTimePeriods as op>
			{
				'customTimePeriodId' : '${op.customTimePeriodId?if_exists}',
				'periodName' : 	"<span >[ ${op.customTimePeriodId?if_exists}]" + ":" + "${StringUtil.wrapString(op.get("periodName",locale)?if_exists)}" + ":" + "<span class='bg-trendcolor'>${op.fromDate?if_exists?string["dd/MM/yyyy"]}"+ " - " +  "${op.thruDate?if_exists?string["dd/MM/yyyy"]}</span>"
			},
		</#list>
	];
	
	var dataCtp = new Array();
	dataCtp = [
		<#list closedTimePeriods as op>
			{
				'customTimePeriodId' : '${op.customTimePeriodId?if_exists}',
				'periodName' : 	"<span class='custom-style-word'>[ ${op.customTimePeriodId?if_exists} ]" + ":" + "${StringUtil.wrapString(op.get("periodName",locale)?if_exists)}" + ":" + "${op.fromDate?if_exists}" + "-" +  "${op.thruDate?if_exists}</span>"
			},
		</#list>
	];
	
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
    
    var cellsrendererIsclose= function (row, columnfield, value, defaulthtml, columnproperties) {
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(tmpData.isClosed == 'N'){
    		var tmpId = 'tmpIc' + tmpData.customTimePeriodId;
    		var html = '<input type="button" onclick="changeState('+row+')" style="opacity: 0.99; position: absolute; top: 0%; left: 0%; padding: 0px; margin-top: 2px; margin-left: 2px; width: 96px; height: 21px;" value="${StringUtil.wrapString(uiLabelMap.commonClose)}" hidefocus="true" id="' + tmpId + '" role="button" class="jqx-rc-all jqx-rc-all-base jqx-button jqx-button-base jqx-widget jqx-widget-base jqx-fill-state-pressed jqx-fill-state-pressed-base" aria-disabled="false">';
    		return html;
    	}else{
    		return "<span class='custom-style-word'>" + value + "</span>";
    	}
    }
    
    
    function changeState(rowIndex){
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
      	var data = 'columnList0' + '=' + 'customTimePeriodId'; 
		data = data + '&' + 'columnValues0' + '=' +  tmpData.customTimePeriodId;
		data += "&rl=1";
      	$.ajax({
            type: "POST",                        
            url: 'jqxGeneralServicer?&jqaction=U&sname=closeFinancialTimePeriod',
            data: data,
            success: function(odata, status, xhr) {
                // update command is executed.
                if(odata.responseMessage == "error"){
                	$('#jqxNotification').jqxNotification({ template: 'info'});
                	$('#jqxNotification').text(odata.results);
                	$('#jqxNotification').jqxNotification('open');
                }else{
                	$('#jqxgrid').jqxGrid('updatebounddata');
                	$('#container').empty();
                	$('#jqxNotification').jqxNotification({ template: 'info'});
                	$('#jqxNotification').text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                	$('#jqxNotification').jqxNotification('open');
                }
            },
            error: function(arg1) {
            	alert(arg1);
            }
        });  
    }
   
	
var cellclass = function (row, columnfield, value) {
		var now = new Date();
		now.setHours(0,0,0,0);
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
		    return 'background-red';
		}
	}	
</script>
<#assign isOrganization = "true"/>