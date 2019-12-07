<script type="text/javascript" language="Javascript">
    <#assign itlength = listPeriodType.size()/>
    <#if listPeriodType?size gt 0>
	    <#assign lpt="var lpt = ['" + StringUtil.wrapString(listPeriodType.get(0).periodTypeId?if_exists) + "'"/>
		<#assign lptValue="var lptValue = ['" + StringUtil.wrapString(listPeriodType.get(0).description?if_exists) +"'"/>
		<#if listPeriodType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lpt=lpt + ",'" + StringUtil.wrapString(listPeriodType.get(i).periodTypeId?if_exists) + "'"/>
				<#assign lptValue=lptValue + ",'" + StringUtil.wrapString(listPeriodType.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign lpt=lpt + "];"/>
		<#assign lptValue=lptValue + "];"/>
	<#else>
    	<#assign lpt="var lpt = [];"/>
    	<#assign lptValue="var lptValue = [];"/>
    </#if>
	${lpt}
	${lptValue}	
	var dataPT = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["periodTypeId"] = lpt[i];
        row["description"] = lptValue[i];
        dataPT[i] = row;
    }
    <#assign itlength = openTimePeriods.size()/>
    <#if openTimePeriods?size gt 0>
	    <#assign lotp="var lotp = ['" + StringUtil.wrapString(openTimePeriods.get(0).customTimePeriodId?if_exists) + "'"/>
		<#assign lotpValue="var lotpValue = ['" + StringUtil.wrapString(openTimePeriods.get(0).customTimePeriodId?if_exists) + ":" + StringUtil.wrapString(openTimePeriods.get(0).periodName?if_exists) + ":" + StringUtil.wrapString(openTimePeriods.get(0).fromDate?string?if_exists)
					+ "-" + StringUtil.wrapString(openTimePeriods.get(0).thruDate?string?if_exists) + "'"/>
		<#if openTimePeriods?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lotp=lotp + ",'" + StringUtil.wrapString(openTimePeriods.get(i).customTimePeriodId?if_exists) + "'"/>
				<#assign lotpValue=lotpValue + ",'" + StringUtil.wrapString(openTimePeriods.get(i).customTimePeriodId?if_exists) + ":" + StringUtil.wrapString(openTimePeriods.get(i).periodName?if_exists) + ":" + StringUtil.wrapString(openTimePeriods.get(i).fromDate?string?if_exists)
						+ "-" + StringUtil.wrapString(openTimePeriods.get(i).thruDate?string?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign lotp=lotp + "];"/>
		<#assign lotpValue=lotpValue + "];"/>
	<#else>
    	<#assign lotp="var lotp = [];"/>
    	<#assign lotpValue="var lotpValue = [];"/>
    </#if>
	${lotp}
	${lotpValue}	
	var dataOtp = new Array();
	row["customTimePeriodId"] = "";
    row["periodName"] = "";
    dataOtp[0] = row;
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["customTimePeriodId"] = lotp[i];
        row["periodName"] = lotpValue[i];
        dataOtp[i+1] = row;
    }
    <#assign itlength = closedTimePeriods.size()/>
    <#if closedTimePeriods?has_content && closedTimePeriods?size gt 0>
    	<#assign lctp="var lctp = ['" + StringUtil.wrapString(closedTimePeriods.get(0).customTimePeriodId?if_exists) + "'"/>
		<#assign lctpValue="var lctpValue = ['" + StringUtil.wrapString(closedTimePeriods.get(0).periodName?if_exists?string) + ":" + StringUtil.wrapString(closedTimePeriods.get(0).fromDate?if_exists?string)
					+ "-" + StringUtil.wrapString(closedTimePeriods.get(0).thruDate?if_exists?string) + "'"/>
		<#if closedTimePeriods?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lctp=lctp + ",'" + StringUtil.wrapString(closedTimePeriods.get(i).customTimePeriodId?if_exists) + "'"/>
				<#assign lctpValue=lctpValue + ",'" +StringUtil.wrapString(closedTimePeriods.get(i).periodName?if_exists) + ":" + StringUtil.wrapString(closedTimePeriods.get(i).fromDate?string?if_exists)
						+ "-" + StringUtil.wrapString(closedTimePeriods.get(i).thruDate?string?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign lctp=lctp + "];"/>
		<#assign lctpValue=lctpValue + "];"/>
    <#else>
    	<#assign lctp="var lctp = [];"/>
    	<#assign lctpValue="var lctpValue = [];"/>
    </#if>
	${lctp}
	${lctpValue}
	var dataCtp = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["customTimePeriodId"] = lctpValue[i];
        row["periodName"] = lctp[i];
        dataCtp[i] = row;
    };
    var parentPeriodRenderer = function (row, column, value) {
        if (value.indexOf('#') != -1) {
            value = value.substring(0, value.indexOf('#'));
        }
        var fb = false;
        for(i=0;i<lotp.length;i++){
        	if(lotp[i]===value){
        		fb=true;
        		return "<span>" + lotpValue[i] + "</span>";
        	}
        };
        for(i=0;i<lctp.length;i++){
        	if(lotp[i]===value){
        		fb=true;
        		return "<span>" + lctpValue[i] + "</span>";
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
    		return "<span>" + value + "</span>";
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
					 { name: 'periodNum', type: 'number' },
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp' },
					 { name: 'periodName', type: 'string' },
					 { name: 'isClosed', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.CustomTimePeriodId}', datafield: 'customTimePeriodId', width: 100},
					 { text: '${uiLabelMap.accParentPeriodId}', datafield: 'parentPeriodId', width: 190, cellsrenderer:parentPeriodRenderer},
					 { text: '${uiLabelMap.accPeriodTypeId}', datafield: 'periodTypeId', width: 110, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < lpt.length;i++){
                        		if(lpt[i] == value){
                        			return \"<span>\" + lptValue[i] + \"</span>\";
                        		}
                        	}
                        	return value;
                        }},
                     { text: '${uiLabelMap.accPeriodNumber}', datafield: 'periodNum', width: 150 },
                     { text: '${uiLabelMap.accStartDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.accEndDate}', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150 },
                     { text: '${uiLabelMap.accPeriodName}', datafield: 'periodName'},
					 { text: '${uiLabelMap.description}', datafield: 'description'},
					 { text: '${uiLabelMap.accIsClosed}', datafield: 'isClosed', width: 130, cellsrenderer: cellsrendererIsclose
	                 }
					 "/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListCustomTimePeriod" dataField=dataField columnlist=columnlist
		 addrow="true" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true"
		 editColumns="isClosed" createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createCustomTimePeriod"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId[${parameters.organizationPartyId}]" clearfilteringbutton="true"
		 alternativeAddPopup="alterpopupWindow"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accParentPeriodId}:</td>
	 			<td align="left"><div id="parentPeriodId"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accPeriodTypeId}:</td>
	 			<td align="left"><div id="periodTypeId"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accPeriodNumber}:</td>
	 			<td align="left"><input id="periodNum"/></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accPeriodName}:</td>
	 			<td align="left"><input id="periodName"/></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accStartDate}:</td>
	 			<td align="left"><div id="fromDate"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accEndDate}:</td>
	 			<td align="left"><div id="thruDate"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accIsClosed}:</td>
	 			<td align="left"><div id="isClose"></div></td>
    	 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
	var icData = new Array();
	var row = {};
	row["name"] = "Yes";
	row["isClosed"] = "Y";
	icData[0] = row;
	var row = {};
	row["name"] = "No";
	row["isClosed"] = "N";
	icData[1] = row;
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var sourceIc =
    {
        localdata: icData,
        datatype: "array"
    };
    var dataAdapterIc = new $.jqx.dataAdapter(sourceIc);
    var sourcePPI =
    {
        localdata: dataOtp,
        datatype: "array"
    };
    var dataAdapterPPI = new $.jqx.dataAdapter(sourcePPI);
    var sourcePT =
    {
        localdata: dataPT,
        datatype: "array"
    };
    var dataAdapterPT = new $.jqx.dataAdapter(sourcePT);
    
    $('#isClose').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterIc, displayMember: "name", valueMember: "isClosed"});
    $('#periodTypeId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterPT, displayMember: "description", valueMember: "periodTypeId"});
    $('#parentPeriodId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterPPI, displayMember: "periodName", valueMember: "customTimePeriodId"});
	$("#fromDate").jqxDateTimeInput({width: '200px', height: '25px'});
	$("#thruDate").jqxDateTimeInput({width: '200px', height: '25px'});
	$("#periodName").jqxInput({width: '195px'});
	$("#periodNum").jqxInput({width: '195px'});
	$("#alterpopupWindow").jqxWindow({
        width: 580, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
    });

    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
        		isClosed:$('#isClose').val(),
        		parentPeriodId:$('#parentPeriodId').val(),
        		periodName:$('#periodName').val(),
        		periodNum:$('#periodNum').val(),
        		periodTypeId:$('#periodTypeId').val(),
        		thruDate: $('#thruDate').jqxDateTimeInput('getDate'),            
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>
