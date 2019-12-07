<#assign dataFields = "[{name: 'partyId', type:'string'},
						{name: 'partyName', type:'string'},
						{name: 'emplPositionTypeId', type:'string'},
						{name: 'overtimeRegister', type: 'number'},
						{name: 'totalDayLeave', type: 'number'},
						{name: 'overtimeActual', type: 'number'},
						{name: 'dayLeaveApprove', type: 'number'},
						{name: 'totalDayWork', type: 'number'}]"/>
<script type="text/javascript">
	var emplPosType = new Array();
	<#list emplPosType as posType>
		var row = {};
		row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
		row["description"] = "${posType.description?if_exists}";
		emplPosType[${posType_index}] = row;
	</#list>
	
	var sourceEmplPosType = {
	        localdata: emplPosType,
	        datatype: "array"
	};
	<#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId',  editable: false,  cellsalign: 'left', width: 130, pinned: true},
							{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 130, pinned: true},
							{text: '${uiLabelMap.HREmplFromPositionType}', datafield: 'emplPositionTypeId',  editable: false, cellsalign: 'right', width: 180,
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < emplPosType.length; i++){
										if(emplPosType[i].emplPositionTypeId == value){
											return '<div style=\"\">' + emplPosType[i].description + '</div>';		
										}
									}
								}	
							},
							{text: '${uiLabelMap.OvertimeRegister}', datafield: 'overtimeRegister', editable: false, cellsalign: 'right', width: 130, filtertype: 'number', 
								cellsrenderer : function(row, column, value){
									var val = $('#jqxgrid').jqxGrid(\"getrowdata\", row);
									var str = \"<a><div style='text-align: right; cursor: pointer'>\" + value + \"</div></a>\";
									return str;
								}
							},
							{text: '${uiLabelMap.TotalDayLeave}', datafield: 'totalDayLeave', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
							{text: '${uiLabelMap.OvertimeActual}', datafield: 'overtimeActual', editable: false, cellsalign: 'right', width: 130, filtertype: 'number',
								cellsrenderer : function(row, column, value){
									var val = $('#jqxgrid').jqxGrid(\"getrowdata\", row);
									var str = \"<a><div style='text-align: right;  cursor: pointer'>\" + value + \"</div></a>\";
									return str;
								}
							},
							{text: '${uiLabelMap.DayLeaveApprove}', datafield: 'dayLeaveApprove', editable: false, cellsalign: 'right',  width: 130, filtertype: 'number'},
							{text: '${uiLabelMap.TotalTimeKeeping}', datafield: 'totalDayWork', editable: false, cellsalign: 'right', filtertype: 'number'}"> 
</script>
						
<@jqGrid filtersimplemode="true" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" selectionmode="singlecell"
		 filterable="true" deleterow="false" editable="true" addrow="false" bindresize="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQEmplTimekeepingGeneral&partyId=${parameters.partyId}&month=${month}&year=${year}" 
		 id="jqxgrid" removeUrl="" deleteColumn=""
		 updateUrl="" 
		 editColumns=""/>	
<div id="popupTimeRegister" >
	<div>${uiLabelMap.WorkOvertimeRegisSent}</div>
    <div id="jqxTimeRegister"></div>
</div>  
<div id="popupDayLeave" >
	<div>${uiLabelMap.HREmplLeaveHistory}</div>
    <div id="jqxDayLeave"></div>
</div>
<script>
$(document).ready(function () {
	$("#jqxgrid").on('cellSelect', function (event) {
	    var args = event.args;
	    var dataField = event.args.datafield;
	    var row = event.args.rowindex;
	    var value = $(this).jqxGrid("getrowdata", row);
	    if(dataField == "overtimeActual"){
	    	showDayLeave(value.partyId);
	    }else if(dataField == "overtimeRegister"){
	    	showTimeRegister(value.partyId);
	    }
	    
	});
	
	jQuery("#jqxgrid").jqxGrid('pinColumn', 'partyId');
	jQuery("#jqxgrid").jqxGrid('pinColumn', 'partyName');
});
	
	
	var popupTg = $("#popupTimeRegister");
	popupTg.jqxWindow({
        width: 630, height: 300, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01           
    });
    var popupdl = $("#popupDayLeave");
	popupdl.jqxWindow({
        width: 630, height: 300, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01           
    });
    var columnTr = [{text: '${uiLabelMap.HREmplOvertimeDateRegis}', datafield: 'dateRegistration', width: '30%', cellsformat: 'dd-MM-yyyy',  columntype: 'datetimeinput'},
					{text: '${uiLabelMap.HREmplOverTimeFromDate}', datafield: 'overTimeFromDate', width: '20%'},
					{text: '${uiLabelMap.HREmplOverTimeThruDate}', datafield: 'overTimeThruDate', width: '20%'},
					{text: '${uiLabelMap.statusId}', datafield: 'statusId', width: '30%'}];
	var dtFieldTr = [{name: 'statusId', type: 'string'},
			   		{name: 'dateRegistration', type: 'date'},
			      	{name: 'overTimeFromDate', type: 'string'},
			      	{name: 'overTimeThruDate', type: 'string'}];
	var columnDl = [{text: '${uiLabelMap.leaveTypeId}', datafield: 'leaveTypeId', width: '30%'},
					{text: '${uiLabelMap.emplLeaveReasonTypeId}', datafield: 'emplLeaveReasonTypeId', width: '30%'},
					{text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: '20%', cellsformat: 'dd-MM-yyyy',  columntype: 'datetimeinput'},
					{text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '20%', cellsformat: 'dd-MM-yyyy',  columntype: 'datetimeinput'},
					{text: '${uiLabelMap.HRolbiusRecruitmentTypeDescription}', datafield: 'description', width: '20%'}];
	var dtFieldDl = [{name: 'leaveTypeId', type: 'string'},
			   		{name: 'emplLeaveReasonTypeId', type: 'string'},
			      	{name: 'fromDate', type: 'date'},
			      	{name: 'thruDate', type: 'date'}];
	function showTimeRegister(id){
		popupTg.jqxWindow('open');
		$.ajax({
			url: "getWorkOvertimeRegistration",
			type: "POST",
			data:{
				partyId: id
			},
			success : function(res){
				if(res && res.data){
					var tmp = new Array();
					for(var x in res.data){
						var obj = res.data[x];
						obj.dateRegistration = formatDateYMD(obj.dateRegistration.time, "-");
						tmp.push(obj);
					}
					renderJqx(res.data, $("#jqxTimeRegister"), dtFieldTr, columnTr);	
				}
			}
		});
	}
	function renderJqx(sourceData, obj, datafields, columnlist){
		var source = {
           localdata: sourceData,
           datatype: "json",
           datafields : datafields
        };
		var dataAdapter = new $.jqx.dataAdapter(source);
		obj.jqxGrid({
			width:600,
			source: dataAdapter,
			// filterable: false,
			virtualmode: true, 
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			// showfilterrow: true,
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns: columnlist
		});
	}
	function showDayLeave(id){
		popupdl.jqxWindow('open');
		$.ajax({
			url: "getDayLeaveApproved",
			data:{
				partyId: id
			},
			type: "POST",
			success : function(res){
				if(res && res.data){
					var tmp = new Array();
					for(var x in res.data){
						var obj = res.data[x];
						var fd = res.data[x].fromDate;
						var td = res.data[x].thruDate;
						obj.fromDate = formatDateYMD(fd.time, "-");
						obj.thruDate = formatDateYMD(td.time, "-");
						tmp.push(obj);
					}
					renderJqx(tmp, $("#jqxDayLeave"), dtFieldDl, columnDl);	
				}
			}
		});
	}
	function formatDateYMD(date, delimiter) {
		var today = new Date(date);
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var a = "-";
		if(delimiter){
			a = delimiter;
		}
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		var hh = today.getHours() < 10 ? "0" + (today.getHours())  : today.getHours() ;
		var min = today.getMinutes() < 10 ? "0" + today.getMinutes()  : today.getMinutes() ;
		var sec = today.getSeconds() < 10 ? "0" + today.getSeconds()  : today.getSeconds() ;
		today = yyyy + a + mm + a + dd + " " + hh + ":" + min + ":" + sec;
		return today;
	}
</script>
