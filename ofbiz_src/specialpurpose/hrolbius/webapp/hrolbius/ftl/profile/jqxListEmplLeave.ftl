<script>
	//Prepare for Employee Leave Type Data
	<#assign listEmplLeaveType = delegator.findList("EmplLeaveType", null, null, null, null, false) />
	var emplLeaveTypeData = new Array();
	<#list listEmplLeaveType as item>
		var row = {};
		<#assign description = item.description?if_exists />
		row['leaveTypeId'] = '${item.leaveTypeId}';
		row['description'] = '${description}';
		emplLeaveTypeData[${item_index}] = row;
	</#list>

	//Prepare for Employee Leave Reason Type Data
	<#assign listEmplLeaveReasonType = delegator.findList("EmplLeaveReasonType", null, null, null, null, false) />
	var emplLeaveReasonTypeData = new Array();
	<#list listEmplLeaveReasonType as item>
		var row = {};
		<#assign description = item.description?if_exists />
		row['emplLeaveReasonTypeId'] = '${item.emplLeaveReasonTypeId}';
		row['description'] = '${description}';
		emplLeaveReasonTypeData[${item_index}] = row;
	</#list>

	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["LEAVE_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
</script>

<!--Set filter party -->
<div id="jqxwindowapproverPartyId">
	<div>${uiLabelMap.accList} ${uiLabelMap.hrApprovers}</div>
	<div style="overflow: hidden;">
		<table id="Approver">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowapproverPartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowapproverPartyIdvalue" value=""/>
					<div id="jqxgridapprover"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<@jqGridMinimumLib/>
<script type="text/javascript">
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;

	//Create window
	$("#jqxwindowapproverPartyId").jqxWindow({
       theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 600
    });

	//Create Button
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});

	//Handle event for alterSave
	$("#alterSave").click(function () {
		var tIndex = $('#jqxgridapprover').jqxGrid('selectedrowindex');

		var data = $('#jqxgridapprover').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowapproverPartyIdkey').val()).val(data.partyId);
		$("#jqxwindowapproverPartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowapproverPartyIdkey').val()).trigger(e);
	});

	// Bind Party Source
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
            $("#jqxgridapprover").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridapprover").jqxGrid('updatebounddata');
        },
        sortcolumn: 'partyId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:15,
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
    $('#jqxgridapprover').jqxGrid(
    {
        width:800,
        source: dataAdapterP,
        filterable: true,
        virtualmode: true,
        sortable:true,
        editable: false,
        showfilterrow: false,
        theme: theme,
        autoheight:true,
        pageable: true,
        pagesizeoptions: ['15', '30', '45'],
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
	    if(event.ctrlKey){
		cntrlIsPressed = true;
	    }
	});

	$(document).keyup(function(event){
		if(event.which=='17')
		cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
</script>
<#assign dataField="[{ name: 'leaveTypeId', type: 'string' },
					 { name: 'emplLeaveReasonTypeId', type: 'string' },
					 { name: 'approverPartyId', type: 'string' },
					 { name: 'leaveStatus', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.leaveTypeId}', datafield: 'leaveTypeId', width: 200, filtertype:'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < emplLeaveTypeData.length; i++){
								if(value == emplLeaveTypeData[i].leaveTypeId){
									return '<span title=' + value + '>' + emplLeaveTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(emplLeaveTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'leaveTypeId', valueMember: 'leaveTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < emplLeaveTypeData.length; i++){
										if(emplLeaveTypeData[i].leaveTypeId == value){
											return '<span>' + emplLeaveTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
					 },
                     { text: '${uiLabelMap.emplLeaveReasonTypeId}', datafield: 'emplLeaveReasonTypeId', width: 200, filtertype: 'checkedlist',
						 cellsrenderer: function(column, row, value){
							 for(var i = 0; i < emplLeaveReasonTypeData.length; i++){
								 if(value == emplLeaveReasonTypeData[i].emplLeaveReasonTypeId){
									 return '<span title=' + value + '>' + emplLeaveReasonTypeData[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 },
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(emplLeaveReasonTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'emplLeaveReasonTypeId', valueMember: 'emplLeaveReasonTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < emplLeaveReasonTypeData.length; i++){
										if(emplLeaveReasonTypeData[i].emplLeaveReasonTypeId == value){
											return '<span>' + emplLeaveReasonTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
                     },
                     { text: '${uiLabelMap.leaveStatus}', datafield: 'leaveStatus', width: 200, filtertype: 'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span>' + statusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
                     },
                     { text: '${uiLabelMap.description}', datafield: 'description', width: 200},
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 200, cellsformat:'d', filtertype: 'range'},
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 200, cellsformat:'d', filtertype: 'range'}
					 "/>

<@jqGrid addrow="false" addType="popup" isShowTitleProperty="false" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplLeave&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />