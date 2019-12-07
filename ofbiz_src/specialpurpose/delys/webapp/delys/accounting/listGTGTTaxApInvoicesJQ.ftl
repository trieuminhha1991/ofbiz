<script type="text/javascript">
	<#assign itlength = listInvoiceType.size()/>
    <#if listInvoiceType?size gt 0>
	    <#assign vaIT="var vaIT = ['" + StringUtil.wrapString(listInvoiceType.get(0).invoiceTypeId?if_exists) + "'"/>
		<#assign vaITValue="var vaITValue = [\"" + StringUtil.wrapString(listInvoiceType.get(0).description?if_exists) + "\""/>
		<#if listInvoiceType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaIT=vaIT + ",'" + StringUtil.wrapString(listInvoiceType.get(i).invoiceTypeId?if_exists) + "'"/>
				<#assign vaITValue=vaITValue + ",\"" + StringUtil.wrapString(listInvoiceType.get(i).description?if_exists) +"\""/>
			</#list>
		</#if>
		<#assign vaIT=vaIT + "];"/>
		<#assign vaITValue=vaITValue + "];"/>
	<#else>
    	<#assign vaIT="var vaIT = [];"/>
    	<#assign vaITValue="var vaITValue = [];"/>
    </#if>
	${vaIT}
	${vaITValue}	
	
	<#assign itlength = listStatusItem.size()/>
    <#if listStatusItem?size gt 0>
	    <#assign vaSI="var vaSI = ['" + StringUtil.wrapString(listStatusItem.get(0).statusId?if_exists) + "'"/>
		<#assign vaSIValue="var vaSIValue = [\"" + StringUtil.wrapString(listStatusItem.get(0).description?if_exists) + "\""/>
		<#if listStatusItem?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaSI=vaSI + ",'" + StringUtil.wrapString(listStatusItem.get(i).statusId?if_exists) + "'"/>
				<#assign vaSIValue=vaSIValue + ",\"" + StringUtil.wrapString(listStatusItem.get(i).description?if_exists) +"\""/>
			</#list>
		</#if>
		<#assign vaSI=vaSI + "];"/>
		<#assign vaSIValue=vaSIValue + "];"/>
	<#else>
    	<#assign vaSI="var vaSI = [];"/>
    	<#assign vaSIValue="var vaSIValue = [];"/>
    </#if>
	${vaSI}
	${vaSIValue}	
	var dataStatusType = new Array();
	for(i=0;i < vaSI.length;i++){
		 var row = {};
	    row["statusId"] = vaSI[i];
	    row["description"] = vaSIValue[i];
	    dataStatusType[i] = row;
	}
	var dataInvoiceType = new Array();
	for(i=0;i < vaIT.length;i++){
		 var row = {};
	    row["invoiceTypeId"] = vaIT[i];
	    row["description"] = vaITValue[i];
	    dataInvoiceType[i] = row;
	}
	


</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'string'},
					 { name: 'amountToApply', type: 'string'},
					 { name: 'partyNameResultFrom', type: 'string'},
					 { name: 'partyNameResultTo', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', width:130, datafield: 'invoiceTypeId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i < vaIT.length;i++){
                        		if(value==vaIT[i]){
                        			return \"<span>\" + vaITValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', width:130, datafield: 'invoiceDate', cellsrenderer:
                     	 function(row, colum, value)
                        {
                            var jsonDateRE = /^\\/Date\\((-?\\d+)(\\+|-)?(\\d+)?\\)\\/$/;
                            var arr = jsonDateRE.exec(\"\\/\" + value + \"\\/\");
                            if (arr) {
                                // 0 - complete results; 1 - ticks; 2 - sign; 3 - minutes
                                var result = new Date(parseInt(arr[1]));
                                if (arr[2]) {
                                    var mins = parseInt(arr[3]);
                                    if (arr[2] === \"-\") {
                                        mins = -mins;
                                    }
                                    var current = result.getUTCMinutes();
                                    result.setUTCMinutes(current - mins);
                                }
                                if (!isNaN(result.valueOf())) {
                                    var date = $.jqx.dataFormat.formatdate(result, 'dd-MM-yyyy',getLocalization);
                                    return '<span style=\"float: left; margin: 4px;\">' + date + '</span>';
                                }
                            }
                            return \"\";
                     }},
					 { text: '${uiLabelMap.CommonStatus}', width:120, datafield: 'statusId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i < vaSI.length;i++){
                        		if(value==vaSI[i]){
                        			return \"<span>\" + vaSIValue[i] + \"</span>\";
                        		}
                        	}
                        	return value;
                        }},
					 { text: '${uiLabelMap.description}', width:150, datafield: 'description'},
					 { text: '${uiLabelMap.accAccountingFromParty}', width:500, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.accAccountingToParty}', width:150, datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_total}', width:200, datafield: 'total', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.total +\"&nbsp;${defaultOrganizationPartyCurrencyUomId}\" + \"</span>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', width:200, datafield: 'amountToApply', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.amountToApply +\"&nbsp;${defaultOrganizationPartyCurrencyUomId}\" + \"</span>\";
					 	}}"
					 />		
<@jqGridMinimumLib/>
<div id="jqxPanel" style="width:100%;">
	<table style="margin:0 auto;margin-top:10px;width:100%;position:relative;">
		<tr>
			<td><div style="width:100px;">${uiLabelMap.FormFieldTitle_invoiceId}</div></td>
			<td>
				<div id="filterType"></div>
			</td>
			<td>
				<input id="invoiceId" style="width:165px;"/>
			</td>
			<td></td>
			<td>${uiLabelMap.description}</td>
			<td>
				<div id="filterType2"></div>
			</td>
			<td>
				<input id="description" style="width:165px;"/>
			</td>
		</tr>
		<tr>
			<td width="30">${uiLabelMap.FormFieldTitle_billingAccountId}</td>
			<td>
		        <div id="jqxdropdownbuttonBillingAccountId">
		            <div style="border-color: transparent;" id="jqxgridBillingAccountIdy">
		            </div>
		        </div>
			</td>
			<td></td>
			<td></td>
			<td>${uiLabelMap.CommonStatus}</td>
			<td>
				<div id="statusId"></div>
			</td>
			<td></td>
		</tr>
		<tr>
			<td width="30">${uiLabelMap.accAccountingFromParty}</td>
			<td>
		        <div id="jqxdropdownbuttonFromParty">
		            <div style="border-color: transparent;" id="jqxgridFromParty">
		            </div>
		        </div>
			</td>
			<td></td>
			<td></td>
			<td>${uiLabelMap.accReferenceNumber}</td>
			<td>
				<div id="filterType3"></div>
			</td>
			<td>
				<input id="referenceNumber" style="width:165px;"/>
			</td>
		</tr>
		<tr>
			<td>${uiLabelMap.accAccountingToParty}</td>
			<td>
		        <div id="jqxdropdownbuttonToParty">
		            <div style="border-color: transparent;" id="jqxgridToParty">
		            </div>
		        </div>
			</td>
			<td></td>
			<td></td>		
			<td width="30">${uiLabelMap.FormFieldTitle_invoiceTypeId}</td>
			<td>
				<div id="invoiceTypeId"></div>
			</td>					
		</tr>
		<tr>
			<td width="30">${uiLabelMap.FormFieldTitle_invoiceDate}</td>
			<td>
		        <div id="filterType4"></div>
			</td>
			<td>
				<div id="invoiceDate1"></div>
			</td>
			<td>
				<div id="filterType5"></div>
			</td>
			<td>
				<div id="invoiceDate2"></div>
			</td>
		</tr>
		<tr>
			<td colspan="7" align="center">
		       <input type="button" value="${uiLabelMap.filter}" id='jqxButton' style="margin-left:8px;"/>
		    </td>
		</tr>
	</table>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	$('#filterType').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataStringFilterType, dropDownWidth:180, displayMember: "description", valueMember: "stringFilterType", theme: theme});
	$('#filterType2').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataStringFilterType, displayMember: "description", valueMember: "stringFilterType", theme: theme});
	$('#filterType3').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataStringFilterType, displayMember: "description", valueMember: "stringFilterType", theme: theme});
	$('#filterType4').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataDatetimeFilterType, displayMember: "description", valueMember: "datetimeFilterType", theme: theme});
	$('#filterType4').jqxDropDownList('val','GREATER_THAN_OR_EQUAL');
	$('#filterType5').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataDatetimeFilterType, displayMember: "description", valueMember: "datetimeFilterType", theme: theme});
	$('#filterType5').jqxDropDownList('val','LESS_THAN_OR_EQUAL');
	$("#invoiceDate1").jqxDateTimeInput({ width: '170px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#invoiceDate1").jqxDateTimeInput('val','');
	$("#invoiceDate2").jqxDateTimeInput({ width: '170px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$('#invoiceTypeId').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataInvoiceType, dropDownHeight:100, displayMember: "description", valueMember: "invoiceTypeId", theme: theme});
	$('#statusId').jqxDropDownList({ selectedIndex: 0, width:145,  source: dataStatusType, displayMember: "description", valueMember: "statusId", theme: theme});
	// FromParty
    var sourceP =
    {
        datafields:
        [
            { name: 'partyId', type: 'string' },
            { name: 'partyTypeId', type: 'string' },
            { name: 'firstName', type: 'string' },
            { name: 'lastName', type: 'string' },
            { name: 'groupName', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceP.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridFromParty").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridFromParty").jqxGrid('updatebounddata');
        },
        sortcolumn: 'partyId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:5,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=getFromParty',
    };
    var dataAdapterP = new $.jqx.dataAdapter(sourceP,
    {
    	formatData: function (data) {
	    	data.noConditionFind="Y";
			data.partyId_op="contains";
			data.partyId="";
			data.partyId_ic="Y";
			data.partyTypeId="";
			data.firstName_op="contains";
			data.firstName="";
			data.firstName_ic="Y";
			data.lastName_op="contains";
			data.lastName="";
			data.lastName_ic="Y";
			data.groupName_op="contains";
			data.groupName="";
			data.groupName_ic="Y";
			data.presentation="layer";
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceP.totalRecords) {
                    sourceP.totalRecords = parseInt(data["odata.count"]);
                }
        }, 
        beforeLoadComplete: function (records) {
        	for (var i = 0; i < records.length; i++) {
        		if(typeof(records[i])=="object"){
        			for(var key in records[i]) {
        				var value = records[i][key];
        				if(value != null && typeof(value) == "object" && typeof(value) != null){
        					var date = new Date(records[i][key]["time"]);
        					records[i][key] = date;
        				}
        			}
        		}
        	}
        }
    });
    $("#jqxdropdownbuttonFromParty").jqxDropDownButton({ width: 145, height: 25});
    $("#jqxgridFromParty").jqxGrid({
    	width:800,
        source: dataAdapterP,
        filterable: false,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        rendergridrows: function(obj)
		{
			return obj.data;
		},
        columns: [
          { text: '${uiLabelMap.accApInvoice_partyId}', datafield: 'partyId'},
          { text: '${uiLabelMap.accApInvoice_partyTypeId}', datafield: 'partyTypeId'},
          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName'},
          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName'},
          { text: '${uiLabelMap.accAccountingFromParty}', datafield: 'groupName'}
        ]
    });
    $("#jqxgridFromParty").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridFromParty").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
        $('#jqxdropdownbuttonFromParty').jqxDropDownButton('setContent', dropDownContent);
    });
    // TParty
    var sourceT =
    {
        datafields:
        [
            { name: 'partyId', type: 'string' },
            { name: 'partyTypeId', type: 'string' },
            { name: 'firstName', type: 'string' },
            { name: 'lastName', type: 'string' },
            { name: 'groupName', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceP.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridToParty").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridToParty").jqxGrid('updatebounddata');
        },
        sortcolumn: 'partyId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:5,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=getFromParty',
    };
    var dataAdapterT = new $.jqx.dataAdapter(sourceT,
    {
    	formatData: function (data) {
	    	data.noConditionFind="Y";
			data.partyId_op="contains";
			data.partyId="";
			data.partyId_ic="Y";
			data.partyTypeId="";
			data.firstName_op="contains";
			data.firstName="";
			data.firstName_ic="Y";
			data.lastName_op="contains";
			data.lastName="";
			data.lastName_ic="Y";
			data.groupName_op="contains";
			data.groupName="";
			data.groupName_ic="Y";
			data.presentation="layer";
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceT.totalRecords) {
                    sourceT.totalRecords = parseInt(data["odata.count"]);
                }
        }, 
        beforeLoadComplete: function (records) {
        	for (var i = 0; i < records.length; i++) {
        		if(typeof(records[i])=="object"){
        			for(var key in records[i]) {
        				var value = records[i][key];
        				if(value != null && typeof(value) == "object" && typeof(value) != null){
        					var date = new Date(records[i][key]["time"]);
        					records[i][key] = date;
        				}
        			}
        		}
        	}
        }
    });
    $("#jqxdropdownbuttonToParty").jqxDropDownButton({ width: 145, height: 25});
    $("#jqxgridToParty").jqxGrid({
    	width:800,
        source: dataAdapterT,
        filterable: false,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        rendergridrows: function(obj)
		{
			return obj.data;
		},
        columns: [
          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId'},
          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId'},
          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName'},
          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName'},
          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName'}
        ]
    });
    $("#jqxgridToParty").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridToParty").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
        $('#jqxdropdownbuttonToParty').jqxDropDownButton('setContent', dropDownContent);
    });
    // jqxgridBillingAccountIdy
    var sourceB =
    {
        datafields:
        [
            { name: 'billingAccountId', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'externalAccountId', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceP.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridBillingAccountIdy").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridBillingAccountIdy").jqxGrid('updatebounddata');
        },
        sortcolumn: 'partyId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:5,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=JQGetListBillingAccoun',
    };
    var dataAdapterB = new $.jqx.dataAdapter(sourceB,
    {
    	formatData: function (data) {
	    	data.noConditionFind="Y";
			data.billingAccountId_op="contains";
			data.billingAccountId="";
			data.billingAccountId_ic="Y";
			data.description_op="contains";
			data.description="";
			data.description_ic="Y";
			data.externalAccountId_op="contains";
			data.externalAccountId="";
			data.externalAccountId_ic="Y";
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceB.totalRecords) {
                    sourceB.totalRecords = parseInt(data["odata.count"]);
                }
        }, 
        beforeLoadComplete: function (records) {
        	for (var i = 0; i < records.length; i++) {
        		if(typeof(records[i])=="object"){
        			for(var key in records[i]) {
        				var value = records[i][key];
        				if(value != null && typeof(value) == "object" && typeof(value) != null){
        					var date = new Date(records[i][key]["time"]);
        					records[i][key] = date;
        				}
        			}
        		}
        	}
        }
    });
    $("#jqxdropdownbuttonBillingAccountId").jqxDropDownButton({ width: 145, height: 25});
    $("#jqxgridBillingAccountIdy").jqxGrid({
    	width:600,
        source: dataAdapterB,
        filterable: false,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        rendergridrows: function(obj)
		{
			return obj.data;
		},
        columns: [
          { text: '${uiLabelMap.FormFieldTitle_billingAccountId}', datafield: 'billingAccountId'},
          { text: '${uiLabelMap.FormFieldTitle_description}', datafield: 'description'},
          { text: '${uiLabelMap.FormFieldTitle_externalAccountId}', datafield: 'externalAccountId'}
        ]
    });
    $("#jqxgridBillingAccountIdy").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridBillingAccountIdy").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['billingAccountId'] +'</div>';
        $('#jqxdropdownbuttonBillingAccountId').jqxDropDownButton('setContent', dropDownContent);
    });
	$("#jqxPanel").jqxPanel({ height: 270, theme:theme});
	$("#jqxButton").jqxButton({ width: '154', height: '30', theme:theme});
	$("#jqxButton").on('click', function () {
		if($("#invoiceId").val() != ""){
			outFilterCondition = "|OLBIUS|invoiceId";
			outFilterCondition += "|SUIBLO|" + $('#invoiceId').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType').val();
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#description").val() != ""){
        	outFilterCondition += "|OLBIUS|description";
			outFilterCondition += "|SUIBLO|" + $('#description').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType2').val();
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#invoiceTypeId").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceTypeId";
			outFilterCondition += "|SUIBLO|" + $('#invoiceTypeId').val();
	        outFilterCondition += "|SUIBLO|" + "EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#statusId").val() != ""){
        	outFilterCondition += "|OLBIUS|statusId";
			outFilterCondition += "|SUIBLO|" + $('#statusId').val();
	        outFilterCondition += "|SUIBLO|" + "EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#jqxdropdownbuttonFromParty").val() != ""){
        	outFilterCondition += "|OLBIUS|partyIdFrom";
			outFilterCondition += "|SUIBLO|" + $('#jqxdropdownbuttonFromParty').val();
	        outFilterCondition += "|SUIBLO|" + "EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#jqxdropdownbuttonToParty").val() != ""){
        	outFilterCondition += "|OLBIUS|partyId";
			outFilterCondition += "|SUIBLO|" + $('#jqxdropdownbuttonToParty').val();
	        outFilterCondition += "|SUIBLO|" + "EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#jqxdropdownbuttonBillingAccountId").val() != ""){
        	outFilterCondition += "|OLBIUS|billingAccountId";
			outFilterCondition += "|SUIBLO|" + $('#jqxdropdownbuttonBillingAccountId').val();
	        outFilterCondition += "|SUIBLO|" + "EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#referenceNumber").val() != ""){
        	outFilterCondition += "|OLBIUS|referenceNumber";
			outFilterCondition += "|SUIBLO|" + $('#referenceNumber').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType3').val();
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#invoiceDate1").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate1').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType4').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
        if($("#invoiceDate2").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate2').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType5').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
		$('#jqxgrid').jqxGrid('updatebounddata');
    });
</script>
<style type="text/css">
	#jqxgridFromParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridToParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridBillingAccountIdy .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxPanel td{
		padding:5px;
	}
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListAPInvoice" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true" addrow="true" addType="popup"
		 otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 addColumns="invoiceId" showtoolbar="false" alternativeAddPopup="alterpopupWindow"/>
