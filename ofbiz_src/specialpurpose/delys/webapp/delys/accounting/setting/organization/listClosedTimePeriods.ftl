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
		row['description'] = "<span >${description}</span>";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>
    var dataOOtp = new Array();
	dataOOtp = [
				{
					'customTimePeriodId' : '',
					'periodName' : ''
				},
			<#list openTimePeriods as op>
				{
					'customTimePeriodId' : '${StringUtil.wrapString(op.customTimePeriodId?if_exists)}',
					'periodName' : "${op.customTimePeriodId?if_exists}" + ":" + "${StringUtil.wrapString(op.periodName?default(""))}" + "(" +  "${op.fromDate?string?if_exists}".replaceAll('-','/') + "-" + "${op.thruDate?string?if_exists}".replaceAll('-','/') + ")"	
				},
			</#list>
	]
    
    var dataCtp = new Array();
	dataCtp = [
				{
					'customTimePeriodId' : '',
					'periodName' : ''
				},
			<#list closedTimePeriods as op>
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
         for(i=0;i<dataOOtp.length;i++){
        	if(dataOOtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataOOtp[i].periodName + "</span>";
        	}
        };
        retu
        for(i=0;i<dataCtp.length;i++){
        	if(dataCtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataCtp[i].periodName + "</span>";
        	}
        };
        return "<span>" + value + "</span>";
    };
    var cellsrendererIsclose= function (row, columnfield, value, defaulthtml, columnproperties) {
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(tmpData.isClosed=='N'){
    		var tmpId = 'tmpIc' + tmpData.customTimePeriodId;
    		var html = '<input type="button" onclick="changeState('+row+')" style="opacity: 0.99; position: absolute; top: 0%; left: 0%; padding: 0px; margin-top: 2px; margin-left: 2px; width: 96px; height: 21px;" value="${StringUtil.wrapString(uiLabelMap.commonClose)}" hidefocus="true" id="' + tmpId + '" role="button" class="jqx-rc-all jqx-rc-all-base jqx-button jqx-button-base jqx-widget jqx-widget-base jqx-fill-state-pressed jqx-fill-state-pressed-base" aria-disabled="false">';
    		return html;
    	}else{
    		return "<span >" + value + "</span>";
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
</script>

	<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
						 { name: 'parentPeriodId', type: 'string' },
						 { name: 'periodTypeId', type: 'string' },
						 { name: 'periodNum', type: 'number' ,other : 'Long'},
						 { name: 'fromDate', type: 'date'},
						 { name: 'thruDate', type: 'date'},
						 { name: 'periodName', type: 'string' }
						 ]
						"/>
<#assign columnlist="{ text: '${uiLabelMap.CustomTimePeriodId}', datafield: 'customTimePeriodId', width: 150},
					 { text: '${uiLabelMap.accParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer},
					 { text: '${uiLabelMap.accPeriodTypeId}', width:150, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxGridClosed').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataPT.length; i++)
	        						{
	        							if(data.periodTypeId == dataPT[i].periodTypeId)
	        							{	
	        								return '<span title=' + value +'>' + dataPT[i].description + '</span>';
		        						}
		        					}		        						
		        						return '<span title=' + value +'>' + value + '</span>';
		    						},
		    					createfilterwidget: function (column, columnElement, widget) {
					   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT,
					                {
					                    autoBind: true
					                });
					                var empty = {periodTypeId: '', description: 'Empty'};
					   				var uniqueRecords2 = filterBoxAdapter2.records;
					   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'periodTypeId', valueMember : 'periodTypeId', renderer: function (index, label, value) 
									{
										for(i=0;i < dataPT.length; i++){
											if(dataPT[i].periodTypeId == value){
												return dataPT[i].description;
											}
										}
									    return value;
									}});
					   			}},	
                     { text: '${uiLabelMap.accPeriodNumber}', datafield: 'periodNum', width: 150,filtertype : 'number' },
                     { text: '${uiLabelMap.accStartDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.accEndDate}', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150 },
                     { text: '${uiLabelMap.accPeriodName}', datafield: 'periodName'}	                 
					 "/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListClosedTimePeriod" dataField=dataField columnlist=columnlist
		 addrow="false"  addType="popup" filtersimplemode="true" showtoolbar="true"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createCustomTimePeriod" customTitleProperties="${uiLabelMap.AccountingClosedTimePeriods}"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId[${parameters.organizationPartyId}]" clearfilteringbutton="true"		 
		 id="jqxGridClosed"
		 />
<script type="text/javascript">
var row = {};

$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

$('#periodTypeId').jqxDropDownList({source: dataPT, displayMember: "description", valueMember: "periodTypeId"});
</script>
