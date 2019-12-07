<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>

	<div class="row-fluid">
		<div class="span12">
			<div class="span3 blue"><h4 style="padding: 0; margin-top: 0"><b>${uiLabelMap.ChooseDepartment}</b></h4></div>
			<div class="span8" >
				<div id="dropDownButton">
					<div style="border: none;" id="jqxTree">
						
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid" style="margin-top: 10px">
		<div class="span2" style="margin: 0; padding: 0">
			<label style="display: inline;">${uiLabelMap.CommonMonth}</label>&nbsp;&nbsp;
			<input type="text" style="margin-bottom:0px;" name="month" class="input-mini" id="month" />
		</div>
		
		<div class="span2" style="margin: 0; padding: 0">
			<label style="display: inline;">${uiLabelMap.CommonYear}</label>&nbsp;&nbsp;
			<input type="text" style="margin-bottom:0px;"class="input-mini" id="year" name="year"/>
		</div>
		<div class="span4" style="margin: 0">
			<button class="btn btn-mini btn-primary icon-ok" id="monthYearSubmit">${uiLabelMap.CommonFind}</button>
		</div>
	</div>
	<div class="row-fluid" style="margin-top: 10px">	
		<div id="ContentPanel" class="span12">
              <div class="jqx-hideborder jqx-hidescrollbars" >
                  <div class="jqx-hideborder" id="feedListContainer">
                  	
                  </div>
              </div>
       	</div>
	
	</div>
	<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
	<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
	<#assign minYear = currentYear-10>
	<#assign maxYear = currentYear+10>
	<#assign dataFields = "[{name: 'partyId', type:'string'},
							{name: 'partyName', type:'string'},
							{name: 'emplPositionTypeId', type:'string'},
							{name: 'overtimeRegister', type: 'number'},
							{name: 'overtimeActual', type: 'number'},
							{name: 'totalWorkingLateHour', type:'number'},
							{name: 'totalDayLeave', type: 'number'},
							{name: 'totalDayLeavePaidApproved', type: 'number'},
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

var data = new Array();
<#list treePartyGroup as tree>
	var row = {};
	row["id"] = "${tree.id}_timekeeping";
	row["text"] = "${tree.text}";
	row["parentId"] = "${tree.parentId}_timekeeping";
	row["value"] = "${tree.idValueEntity}";
	data[${tree_index}] = row;
</#list>

<#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId',  editable: false,  cellsalign: 'left', width: 110, pinned: true},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 120, pinned: true},
						{text: '${uiLabelMap.HREmplFromPositionType}', datafield: 'emplPositionTypeId',  editable: false, cellsalign: 'right', width: 150,
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < emplPosType.length; i++){
									if(emplPosType[i].emplPositionTypeId == value){
										return '<div style=\"\">' + emplPosType[i].description + '</div>';		
									}
								}
							}	
						},
						{text: '${uiLabelMap.OvertimeRegister}', datafield: 'overtimeRegister', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
						{text: '${uiLabelMap.OvertimeActual}', datafield: 'overtimeActual', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
						{text: '${uiLabelMap.TotalWorkingLateHour}', datafield: 'totalWorkingLateHour' , editable: false, cellsalign: 'right', width: 130, filtertype: 'number'}, 
						{text: '${uiLabelMap.TotalDayLeave}', datafield: 'totalDayLeave', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
						{text: '${uiLabelMap.TotalDayPaidLeave}', datafield: 'totalDayLeavePaidApproved', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
						{text: '${uiLabelMap.DayLeaveApprove}', datafield: 'dayLeaveApprove', editable: false, cellsalign: 'right',  width: 130, filtertype: 'number'},
						{text: '${uiLabelMap.TotalTimeKeeping}', datafield: 'totalDayWork', editable: false, cellsalign: 'right', width: 150, filtertype: 'number'}">

$(document).ready(function () {
	
	jQuery('#month').ace_spinner({value:(${month} + 1),min:1,max:12,step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
	jQuery('#year').ace_spinner({value:${year},min:${minYear},max:${maxYear},step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
	$("#monthYearSubmit").click(function(){
		var month = $("#month").val();
		var year = $("#year").val();
		var item = $('#jqxTree').jqxTree('getSelectedItem');
		if(!item){
			<#if expandedList?has_content>	
				item = $("#jqxTree").jqxTree('getItem', $("#${expandedList.get(0)}_timekeeping")[0]);
			</#if>
		}
		if(item){
			var tmpS = $("#jqxgrid").jqxGrid('source');
			var value = item.value;
			tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQEmplTimekeepingGeneral&partyGroupId=" + item.value + "&month=" + (month - 1) + "&year=" + year;
			$("#jqxgrid").jqxGrid('source', tmpS);	
		}
		/* getEmplTimekeepingOverview(item.value, month, year); */
	});
	 var source =
	 {
	     datatype: "json",
	     datafields: [
	         { name: 'id'},
	         { name: 'parentId'},
	         { name: 'text'} ,
	         { name: 'value'}
	     ],
	     id: 'id',
	     localdata: data
	 };
	 //console.log(data);
	 var dataAdapter = new $.jqx.dataAdapter(source);
	 // perform Data Binding.
	 dataAdapter.dataBind();
	 // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
	 // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
	 // specifies the mapping between the 'text' and 'label' fields.  
	 var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	/*  $("#splitter").jqxSplitter({  width: '100%', height: '500', panels: [{ size: '25%'}, {size: '75%'}] }); */
    $("#dropDownButton").jqxDropDownButton({ width: '270px', height: 25, theme: 'olbius'}); 
	
     $('#jqxTree').on('select', function(event){
    	var id = event.args.element.id;
    	var item = $('#jqxTree').jqxTree('getItem', args.element);
    	setDropdownContent(item);
        var month = $("#month").val();
		var year = $("#year").val();
		var tmpS = $("#jqxgrid").jqxGrid('source');
		var value = jQuery("#jqxTree").jqxTree('getItem', $("#"+id)[0]).value;
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQEmplTimekeepingGeneral&partyGroupId=" + value + "&month=" + (month - 1) + "&year=" + year;
		$("#jqxgrid").jqxGrid('source', tmpS);
     });
     
     $('#jqxTree').jqxTree({source: records,width: "270px", height: "200px", theme: 'olbius'});
     <#if expandedList?has_content>
	 	<#list expandedList as expandId>
	 		$('#jqxTree').jqxTree('expandItem', $("#${expandId}_timekeeping")[0]);
	 	</#list>
	 </#if>    
	 <#if expandedList?has_content>
	 	<#assign defaultPartyId = expandedList.get(expandedList?size - 1)>
	 	var initElement = $("#${expandedList.get(0)}_timekeeping")[0];
	 	setDropdownContent(initElement);
	 </#if>   
	 
});

function setDropdownContent(element){
	var item = $("#jqxTree").jqxTree('getItem', element);
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
    $("#dropDownButton").jqxDropDownButton('setContent', dropDownContent);
}

/* function getEmplTimekeepingOverview(partyId, month, year){
	//console.log(partyId);
	jQuery.ajax({
		url:"<@ofbizUrl>getEmplTimekeepingOverview</@ofbizUrl>",
		type: 'POST',
		data: {partyId: partyId, month: month, year: year},
		success: function(data){
			jQuery("#feedListContainer").html(data);
		}
	});
} */
</script>
<@jqGrid filtersimplemode="true" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" selectionmode="singlecell"
		 filterable="true" deleterow="false" editable="true" addrow="false" bindresize="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQEmplTimekeepingGeneral&partyGroupId=${defaultPartyId}&month=${month}&year=${year}" 
		 id="jqxgrid" removeUrl="" deleteColumn=""
		 updateUrl="" 
		 editColumns=""/>
<div id="jqxNotification">
    <div id="notificationContent">
    </div>
</div>
<div id="popupTimeRegister">
	<div >${uiLabelMap.TimeRegisters}</div>
	<div id="container" style="background-color: transparent; overflow: auto;">
		<div style="text-align: right; margin-bottom: 5px">
			<button class="btn btn-primary btn-mini icon-edit" id="updateEmplOvertimeWorking">${uiLabelMap.UpdateEmplAttendanceTracker}</button>
		</div>
    	<div id="jqxTimeRegister"></div>
    </div>
</div>  

<div id="popupEmplWorkingLate">
	<div >${uiLabelMap.HREmplWorkingLateApproval}</div>
	<div id="container" style="background-color: transparent; overflow: auto;">
		<div style="text-align: right; margin-bottom: 5px">
			<button class="btn btn-primary btn-mini icon-edit" id="updateEmplWrokingLate">${uiLabelMap.UpdateEmplWorkingLate}</button>
		</div>
    	<div id="jqxEmplWorkingLate"></div>
    </div>
</div>  

<!-- <div id="popupDayLeave" >
	<div>${uiLabelMap.ConstructFormular}</div>
    <div id="jqxDayLeave"></div>
</div> -->
<script>
	var partyId = null;
	var popupTg = $("#popupTimeRegister");
	popupTg.jqxWindow({
        width: '85%', maxWidth: '85%', height: 500, resizable: true,   isModal: true, autoOpen: false, theme: 'olbius'           
    });
	
    /* var popupdl = $("#popupDayLeave");
	popupdl.jqxWindow({
        width: 640, height: 300, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01           
    }); */
	var statusArr = new Array();
	<#if statusList?exists>
		var row = {};
		<#list statusList as status>
			row = {};
			row["statusId"]= "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusArr[${status_index}] = row;
		</#list>
	</#if>
	
    var columnTr = [{text: '${uiLabelMap.overTimeFromDate}', datafield: 'workOvertimeRegisId', hidden: true},
                    {text: '${uiLabelMap.CommonDate}', datafield: 'dateRegistration', width: '15%',cellsformat: 'dd/MM/yyyy', editable: false, filtertype:'range', columntype: 'datetimeinput'},
					{text: '${uiLabelMap.overTimeFromDate}', datafield: 'overTimeFromDate', width: '15%',cellsformat: 'HH:mm:ss', editable: false},
					{text: '${uiLabelMap.overTimeThruDate}', datafield: 'overTimeThruDate', width: '15%', cellsformat: 'HH:mm:ss', editable: false},
					{text: '${uiLabelMap.ActualStartOverTime}', datafield: 'actualStartTime', width: '15%', cellsformat: 'HH:mm:ss', columntype: 'datetimeinput',
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDateTimeInput({width: '100%', height: 28, formatString: 'HH:mm:ss', showCalendarButton: false});
					        editor.val(cellvalue);
					    },
					    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
                            // return the old value, if the new value is empty.
                            if (newvalue == "" || !newvalue) return oldvalue;
                        }
					},
					{text: '${uiLabelMap.ActualEndOverTime}', datafield: 'actualEndTime', width: '18%', cellsformat: 'HH:mm:ss', columntype: 'datetimeinput',
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDateTimeInput({width: '100%', height: 28, formatString: 'HH:mm:ss', showCalendarButton: false});
					        editor.val(cellvalue);
					    },
					    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
                            // return the old value, if the new value is empty.
                            if (newvalue == "") return oldvalue;
                        }
					},
					{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusArr.length; i++){
								if(statusArr[i].statusId == value){
									return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusArr[i].description + '</div>';		
								}
							}
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							 var source =
				            {
				                localdata: statusArr,
				                datatype: "array"
				            };
							var dataAdapter = new $.jqx.dataAdapter(source);
							editor.jqxDropDownList({source: dataAdapter, autoDropDownHeight: true, displayMember: 'description', valueMember : 'statusId'});							 
					    },
					    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
                            // return the old value, if the new value is empty.
                            if (newvalue == "") return oldvalue;
                        }
					}
					];
	var dtFieldTr = [{name: 'workOvertimeRegisId', type: 'string'},
	                {name: 'dateRegistration', type: 'date'},
			      	{name: 'overTimeFromDate', type: 'date'},
			      	{name: 'overTimeThruDate', type: 'date'},
			      	{name: 'actualStartTime', type: 'date'},
			      	{name: 'actualEndTime', type: 'date'},
			      	{name: 'statusId', type: 'string'}];
	
	var timeRegisSource = {
			localdata: [{}],
            datafields: dtFieldTr,
            datatype: "json",
            updaterow: function(rowid, rowdata, commit){
				var dataEdit = {};
				dataEdit["workOvertimeRegisId"]= rowdata.workOvertimeRegisId;
				dataEdit["dateRegistration"] = rowdata.dateRegistration.getTime();
				if(rowdata.actualStartTime){
					dataEdit["actualStartTime"] = rowdata.actualStartTime.getTime();
				}
				if(rowdata.actualEndTime){
					dataEdit["actualEndTime"] = rowdata.actualEndTime.getTime();
				}
				dataEdit["statusId"] = rowdata.statusId;
				$.ajax({
					url: 'updateEmplWorkovertime',
					data: dataEdit,
					type: 'POST',
					success: function(data){
						if(data._ERROR_MESSAGE_){
							commit(false);
							$('#jqxNotification').jqxNotification({ template: 'error'});
                        	$("#jqxNotification").text(data._ERROR_MESSAGE_);
                        	//$("#jqxNotification").jqxNotification("open");
						}else{
							commit(true);
							$('#jqxNotification').jqxNotification({ template: 'info'});
							//$('#container').empty();
                        	$("#jqxNotification").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                        	//$("#jqxNotification").jqxNotification("open");
						}						
					},
					error: function(){
						commit(false);
					}
				});
			}
	}
	var timeRgisDataAdapter = new $.jqx.dataAdapter(timeRegisSource);
	var statusLateArr = new Array();
	<#list statusLateList as tempStatus>
		var row = {};
		row["statusId"] = "${tempStatus.statusId}";
		row["description"] = "${StringUtil.wrapString(tempStatus.description)}";
		statusLateArr[${tempStatus_index}] = row;
	</#list>
	
	var dtFieldWL = [{name: 'partyId', type: 'string'},
	                 {name: 'emplWorkingLateId', type: 'string'},
	                 {name: 'dateWorkingLate', type: 'date'},
	                 {name: 'arrivalTime', type: 'date'},
	                 {name: 'delayTime', type: 'number'},
	                 {name: 'reason', type: 'string'},
	                 {name: 'statusId', type:'string'}
	                 ]
	
	var columnlistWL = [{text: '${uiLabelMap.CommonDate}', datafield: 'dateWorkingLate', width: '15%',cellsformat: 'dd/MM/yyyy', editable: false, filtertype:'range', columntype: 'datetimeinput'},
	                    {text: '${uiLabelMap.ArrivalTime}', datafield: 'arrivalTime', width: '15%', editable: false, cellsformat: 'HH:mm:ss', columntype: 'datetimeinput'},
	                    {text: '${uiLabelMap.HRDelayTime}', datafield: 'delayTime', editable: true, width: '15%', cellsalign: 'right', columntype: 'numberinput',
	                    	validation: function (cell, value) {
	                            if (value < 0) {
	                                return { result: false, message: "${uiLabelMap.ValueNotLessThanZero}" };
	                            }
	                            return true;
	                        },
	                    	createeditor: function (row, cellvalue, editor) {
	                            editor.jqxNumberInput({ decimalDigits: 0, digits: 3 });
	                        }	
	                    },
	                    {text: '${uiLabelMap.HRCommonReason}', datafield: 'reason', editable: true,
	                    	cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                            // return the old value, if the new value is empty.
	                            if (newvalue == "") return oldvalue;
	                        }	
	                    },
	                    {text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editabel: true, width: '18%', columntype: 'dropdownlist',
	                    	cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusLateArr.length; i++){
									if(statusLateArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusLateArr[i].description + '</div>';		
									}
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								 var source =
					            {
					                localdata: statusLateArr,
					                datatype: "array"
					            };
								var dataAdapter = new $.jqx.dataAdapter(source);
								editor.jqxDropDownList({source: dataAdapter, autoDropDownHeight: true, displayMember: 'description', valueMember : 'statusId'});							 
						    },
	                    	cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                            // return the old value, if the new value is empty.
	                            if (newvalue == "") return oldvalue;
	                        }	
	                    },
	                    {text: '', datafield: 'emplWorkingLateId', hidden: true},
	                    {text: '', datafield: 'partyId', hidden: true}
	                    ]
	var emplWorkLateSource = {
			localdata: [{}],
            datafields: dtFieldWL,
            datatype: "json",
            updaterow: function(rowid, rowdata, commit){
            	var dataEdit = {};
				dataEdit["emplWorkingLateId"]= rowdata.emplWorkingLateId;
				if(rowdata.dateWorkingLate){
					dataEdit["dateWorkingLate"] = rowdata.dateWorkingLate.getTime();
				}
				dataEdit["delayTime"] = rowdata.delayTime;
				dataEdit["reason"] = rowdata.reason;
				dataEdit["statusId"] = rowdata.statusId;
				$.ajax({
					url: 'updateEmplWorkingLate',
					data: dataEdit,
					type: 'POST',
					success: function(data){
						if(data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_){
							commit(false);
							$('#jqxNotification').jqxNotification({ template: 'error'});
                        	$("#jqxNotification").text(data._ERROR_MESSAGE_);
                        	//$("#jqxNotification").jqxNotification("open");
						}else{
							commit(true);
							$('#jqxNotification').jqxNotification({ template: 'info'});
							//$('#container').empty();
                        	$("#jqxNotification").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                        	//$("#jqxNotification").jqxNotification("open");
						}						
					},
					error: function(){
						commit(false);
					}
				});
            }
	};
	var emplWorkLateAdapter = new $.jqx.dataAdapter(emplWorkLateSource);
	var columnDl = [{text: '${uiLabelMap.leaveTypeId}', datafield: 'leaveTypeId', width: '30%'},
					{text: '${uiLabelMap.emplLeaveReasonTypeId}', datafield: 'emplLeaveReasonTypeId', width: '30%'},
					{text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: '20%', cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput'},
					{text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '20%', cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput'},
					{text: '${uiLabelMap.HRolbiusRecruitmentTypeDescription}', datafield: 'description', width: '20%'}];
	var dtFieldDl = [{name: 'leaveTypeId', type: 'string'},
			   		{name: 'emplLeaveReasonTypeId', type: 'string'},
			      	{name: 'fromDate', type: 'date'},
			      	{name: 'thruDate', type: 'date'}];
	$(document).ready(function () {
		$("#jqxTimeRegister").jqxGrid({
			width:'100%',
			filterable: false,
			sortable:true,
			source: timeRgisDataAdapter,
			editable: true,
			autoheight:true,
			selectionmode: 'singlecell',
			editmode: 'click',
			pageable: true,
			columns: columnTr,
		});
		
		$("#jqxEmplWorkingLate").jqxGrid({
			width:'100%',
			filterable: false,
			sortable:true,
			source: emplWorkLateAdapter,
			editable: true,
			autoheight:true,
			selectionmode: 'singlecell',
			editmode: 'click',
			pageable: true,
			columns: columnlistWL
		});
		
		$("#popupEmplWorkingLate").jqxWindow({
	        width: '85%', maxWidth: '85%', height: 500, resizable: true,   isModal: true, autoOpen: false, theme: 'olbius'           
	    });
		$("#jqxNotification").jqxNotification({ width: $('#jqxTimeRegister').jqxGrid('width'), position: "top-right", opacity: 0.9, 
												autoClose: false, template: "info" });
		
		$("#updateEmplOvertimeWorking").click(function(event){
			if(partyId){
				$.ajax({
					url: 'updateActualEmplOvertimeWorking',
					data: {partyId: partyId, month: ($("#month").val() - 1), year: $("#year").val()},
					type: 'POST',
					success: function(data){
						renderJqx($("#jqxTimeRegister"), partyId, "getWorkOvertimeRegistration", timeRegisSource);
					}
				});
			}
		});
		
		$("#updateEmplWrokingLate").click(function(event){
			if(partyId){
				$.ajax({
					url: 'updateActualEmplWrokingLate',
					data: {partyId: partyId, month: ($("#month").val() - 1), year: $("#year").val()},
					type: 'POST',
					success: function(data){
						if(data._EVENT_MESSAGE_){
							renderJqx($("#jqxEmplWorkingLate"), partyId, "getEmplWorkingLate", emplWorkLateSource);	
						}
					}
				});
			}
		});
		
		$("#jqxgrid").on('cellDoubleClick', function(event){
			 var rowBoundIndex = event.args.rowindex;
			 var dataField = event.args.datafield;
			 var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
			 partyId = data.partyId;
			 if(dataField == 'dayLeaveApprove'){
				 //showDayLeave(partyId);
			 }else if(dataField == 'overtimeRegister'){
				 showTimeRegister(partyId);
			 }else if(dataField == 'totalDayLeave'){
				 
			 }else if(dataField == 'totalWorkingLateHour'){
				 showTimeWorkingLate(partyId);
			 }
		});
		popupTg.on('close', function (event){
			partyId = null;
		});
		$("#popupEmplWorkingLate").on('close', function(event){
			partyId = null;
		});
	});
	
	function showTimeRegister(id){
		popupTg.jqxWindow('open');
		renderJqx($("#jqxTimeRegister"), id, "getWorkOvertimeRegistration", timeRegisSource);
	}
	
	function showTimeWorkingLate(partyId){
		$("#popupEmplWorkingLate").jqxWindow('open');
		renderJqx($("#jqxEmplWorkingLate"), partyId, "getEmplWorkingLate", emplWorkLateSource);
	}
	
	function renderJqx(obj, partyId, urlStr, source){
		var month = ($("#month").val() - 1);
		var year = $("#year").val();
		$.ajax({
			url: urlStr,
			type:'POST',
			data: {partyId: partyId, month: month, year: year},
			success: function(data){
				source.localdata = data.data;
				obj.jqxGrid('updatebounddata');
			}
		});
		
	}
	function showDayLeave(id){
		/* popupdl.jqxWindow('open');
		$.ajax({
			url: "getDayLeaveApproved",
			data:{
				partyId: id,
				month: (jQuery('#month').val() - 1),
				year: jQuery('#year').val()
			},
			type: "POST",
			success : function(res){
				if(res && res.data){
					renderJqx(res.data, $("#jqxDayLeave"), dtFieldDl, columnDl);	
				}
			}
		}); */
	}
</script>		 