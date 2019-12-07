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
<div id="jqxwindowpartyId">
	<div>${uiLabelMap.SelectPartyId}</div>
	<div style="overflow: hidden;">
		<table id="PartyId">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
					<div id="jqxgridpartyid"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<div id="jqxwindowbillingAccountId">
	<div>${uiLabelMap.SelectBillingAccountId}</div>
	<div style="overflow: hidden;">
		<table id="BillingAccountId">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowbillingAccountIdkey" value=""/>
					<input type="hidden" id="jqxwindowbillingAccountIdvalue" value=""/>
					<div id="jqxgridbillingaccountid"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave2" value="${uiLabelMap.CommonSave}" /><input id="alterCancel2" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<div id="jqxwindowpartyIdFrom">
	<div>${uiLabelMap.SelectPartyIdFrom}</div>
	<div style="overflow: hidden;">
		<table id="PartyIdFrom">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdFromkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdFromvalue" value=""/>
					<div id="jqxgridpartyidfrom"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave3" value="${uiLabelMap.CommonSave}" /><input id="alterCancel3" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<div id='Menu'>
        <ul>
            <li>${uiLabelMap.process}</li>            
        </ul>
</div>
<@jqGridMinimumLib/>
<script type="text/javascript">
	$.jqx.theme = 'base';  
	theme = $.jqx.theme;  
	$("#jqxwindowpartyIdFrom").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
    $('#jqxwindowpartyIdFrom').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowpartyIdFrom").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave3").click(function () {
		var tIndex = $('#jqxgridpartyidfrom').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyidfrom').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdFromkey').val()).val(data.partyId);
		$("#jqxwindowpartyIdFrom").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdFromkey').val()).trigger(e);
	});
	// From party
    var sourceF =
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
            sourceF.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridpartyidfrom").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridpartyidfrom").jqxGrid('updatebounddata');
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
    var dataAdapterF = new $.jqx.dataAdapter(sourceF,
    {
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceF.totalRecords) {
                    sourceF.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridpartyidfrom').jqxGrid(
    {
        width:800,
        source: dataAdapterF,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        showfilterrow: false,
        theme: theme, 
        autoheight:true,
        pageable: true,
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
        ]
    });
</script>
<script type="text/javascript">
	$("#jqxwindowbillingAccountId").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
    $('#jqxwindowbillingAccountId').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowbillingAccountId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave2").click(function () {
		var tIndex = $('#jqxgridbillingaccountid').jqxGrid('selectedrowindex');
		var data = $('#jqxgridbillingaccountid').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowbillingAccountIdkey').val()).val(data.billingAccountId);
		$("#jqxwindowbillingAccountId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowbillingAccountIdkey').val()).trigger(e);
	});
	// BillingAccount
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
            sourceB.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridbillingaccountid").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridbillingaccountid").jqxGrid('updatebounddata');
        },
        sortcolumn: 'billingAccountId',
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
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceB.totalRecords) {
                    sourceB.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridbillingaccountid').jqxGrid(
    {
        width:800,
        source: dataAdapterB,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        theme: theme, 
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
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
</script>
<script type="text/javascript">
	$("#jqxwindowpartyId").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
    $('#jqxWindow').on('open', function (event) {
    	//var offset = $("#jqxgrid").offset();
   		//$("#jqxwindowpartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave").click(function () {
		var tIndex = $('#jqxgridpartyid').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyid').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdkey').val()).val(data.partyId);
		$("#jqxwindowpartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdkey').val()).trigger(e);
	});
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
            $("#jqxgridpartyid").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridpartyid").jqxGrid('updatebounddata');
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
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceP.totalRecords) {
                    sourceP.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridpartyid').jqxGrid(
    {
        width:800,
        source: dataAdapterP,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        theme: theme, 
        editable: false,
        autoheight:true,
        pageable: true,
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
			  { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
	          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
	          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
	          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
	          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
			]
    });
    
    $(document).keydown(function(event){
	    if(event.ctrlKey)
	        cntrlIsPressed = true;
	});
	
	$(document).keyup(function(event){
		if(event.which=='17')
	    	cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'billingAccountId', type: 'string'},
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
                     { text: '${uiLabelMap.accBillingAccountId}', filtertype: 'olbiusdropgrid', width:150, datafield: 'billingAccountId',
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
					 { text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', width:150, datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
					 	},
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
					 { text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:500, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
					 	},
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:130, datafield: 'invoiceTypeId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < vaIT.length;i++){
                        		if(value==vaIT[i]){
                        			return \"<span>\" + vaITValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceIT =
						    {
						        localdata: dataInvoiceType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataInvoiceType.length; i++){
									if(dataInvoiceType[i].invoiceTypeId == value){
										return dataInvoiceType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < vaSI.length;i++){
                        		if(value==vaSI[i]){
                        			return \"<span>\" + vaSIValue[i] + \"</span>\";
                        		}
                        	}
                        	return value;
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceST =
						    {
						        localdata: dataStatusType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataStatusType.length; i++){
									if(dataStatusType[i].statusId == value){
										return dataStatusType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.description}', width:150, datafield: 'description'},
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
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	// create context menu
    $("#Menu").jqxMenu({ width: 200, height: 30, autoOpenPopup: false, mode: 'popup'});
    // handle context menu clicks.
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        if ( $.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.process)}")
        {
	       	var rowIndexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
	        var rowIds = new Array();
	        var rowDatas = new Array(); 
	        for (var i = 0; i < rowIndexes.length; i++) {
	            var currentId = $('#jqxgrid').jqxGrid('getrowid', rowIndexes[i]);
	            rowIds.push(currentId);
	            var dataRecord =$('#jqxgrid').jqxGrid('getrowdata', rowIndexes[i]);
	            rowDatas.push(dataRecord);
	        };
	        $('#jqxgrid').jqxGrid('updaterow', rowIds,rowDatas);
	        $('#jqxgrid').jqxGrid('clearselection');
        }
    });
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<style type="text/css">
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListTaxAPInvoiceNotProcess" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true" addrow="true" addType="popup" editable="true"  defaultSortColumn ="invoiceDate" sortdirection="desc" selectionmode="checkbox"
		 updateUrl="jqxGeneralServicer?sname=updateTaxAPInvoiceNotProcess&jqaction=U" editColumns="invoiceId" ulistname="listInvoiceId" updatelist="true" 		 
		 otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 addColumns="invoiceId" showtoolbar="false" alternativeAddPopup="alterpopupWindow" editrefresh="true" id="jqxgrid" contextMenuId="Menu" mouseRightMenu="true"/>

