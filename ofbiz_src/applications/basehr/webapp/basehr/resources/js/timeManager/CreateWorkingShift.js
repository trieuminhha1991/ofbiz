var createWorkingShiftObject = (function(){
	var init = function(){
		initJqxWindow();
		initBtnEvent();
		initJqxValidator();
		initJqxDatetimeInput();
		initJqxDropdownlist();
		initJqxInput();
		initCheckBox();
		initJqxGridCreateNewWorkShift();
	};
	
	var initCheckBox = function(){
		$("#allowOTAfterShift").jqxCheckBox({theme: 'olbius', height: 25});
	};
	
	var initJqxInput = function(){
		$("#workingShiftIdNew, #workingShiftNameNew").jqxInput({width: '95%', height: 20, theme: 'olbius'});
		$("#configWSPartyId, #configWSPartyName").jqxInput({width: '95%', height: 20, theme: 'olbius', disabled: true})
		$("#minMinuteOvertimeNew, #allowLateMinuteNew").jqxNumberInput({ 
			width: '97%', height: '25px',  spinButtons: true, theme: 'olbius', inputMode: 'simple',
			decimalDigits: 0, min: 0
		});
		$("#minMinuteOvertimeNew").jqxNumberInput({
			disabled: true
		});
	};
	
	var initJqxGridCreateNewWorkShift = function (){
		var dataDefault = generatedataWorkShiftTypeInWeek();
		var source =
	    {
	        localdata: dataDefault,
	        datatype: "array",
	        datafields: globalObject.getDayOfWeekList()
	    };
		
		var columnlist = globalObject.getColumnGridCreateNewWS();
		var rendertoolbar = function (toolbar){
			var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>" + uiLabelMap.WorkingShifWorkTypeWorkWeek + "</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridShiftWorkTypeNew").jqxGrid({
			width: '100%',
	        source: dataAdapter,
	        pageable: true,
	        autoheight: true,
	        columns: columnlist,
	        showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		sortable: false,
	        filterable: false,
	        editable: true,
	        editmode: 'dblclick',
	        rowsheight: 26,
	        localization: getLocalization(),
	        selectionmode: 'singlecell',
	        theme: 'olbius'
		});
	};
	
	var initJqxDropdownlist = function(){
		$("#workingShiftDropdownlist").jqxDropDownList({source: [], autoDropDownHeight: true, width: '97%', height: 25,  
			displayMember: "workingShiftName", valueMember: "workingShiftId",
		});
	};
	
	var initJqxDatetimeInput = function(){
		$("#shiftStartTimeNew, #shiftBreakStartTimeNew, #shiftBreakEndTimeNew, #shiftEndTimeNew, #startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
			formatString: 'HH:mm:ss', 
			showCalendarButton: false,
			width: '89%', 
			height: '25px',
			theme: 'olbius',
			value: null
		});
		$("#startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
			disabled: true
		});
	};
	var initJqxWindow = function(){
		$("#popupAddRow").jqxWindow({showCollapseButton: false,autoOpen: false,
			width: 850, height: 520, isModal: true, theme:'olbius',
			initContent: function(){
				
			}	
		});
		
		$("#popupAddRow").on('close', function(event){
			Grid.clearForm($(this));
			$("#popupAddRow").jqxValidator('hide');
		});
		
		$("#popupAddRow").on('open', function(event){
			var s = $("#jqxgridShiftWorkTypeNew").jqxGrid('source');
			s._source.localdata = generatedataWorkShiftTypeInWeek();
			$("#jqxgridShiftWorkTypeNew").jqxGrid('source', s);
		});
	};
	
	var initBtnEvent = function(){
		$("#btnCancel").click(function(event){
			$("#workingShiftEditWindow").jqxWindow('close');
		});
		
		$("#btnCancelNew").click(function(event){
			$("#popupAddRow").jqxWindow('close');
		});
		$('#allowOTAfterShift').on('change', function (event) {
			$("#minMinuteOvertimeNew").jqxNumberInput({ disabled: !event.args.checked });
	        $("#startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
	     		disabled: !event.args.checked
	     	});
	    });
		btnSaveNewAction();
		btnSaveAction();
	};
	
	var btnSaveNewAction = function(){
		$("#btnSaveNew").click(function(event){
			var valid = $("#popupAddRow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateNewWorkingShiftConfirm,
				 [
				 {
		   		    "label" : uiLabelMap.CommonSubmit,
		   		    "class" : "btn-primary btn-small icon-ok open-sans",
		   		    "callback": function() {
		   		    	createNewWorkingShift();	    			
		   		    }
		   		 },
		   		 {
		   		    "label" : uiLabelMap.CommonCancel,
		   		    "class" : "btn-danger icon-remove btn-small",
		   		    "callback": function() {
		   		    	
		   		    }
		   		 }
		   		 ]
			 );
		});
	};
	
	var btnSaveAction = function(){
		$("#btnSave").click(function(event){
			var valid = $("#workingShiftEditWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var dataSubmit = {};
			dataSubmit.workingShiftId = $("#workingShiftId").val();
			dataSubmit.workingShiftName = $("#workingShiftName").val();
			dataSubmit.minMinuteOvertime = $("#minMinuteOvertime").val();
			dataSubmit.allowLateMinute = $("#allowLateMinute").val();
			dataSubmit.shiftStartTime = $("#shiftStartTime").jqxDateTimeInput('val', 'date').getTime();
			dataSubmit.shiftEndTime = $("#shiftEndTime").jqxDateTimeInput('val', 'date').getTime();
			dataSubmit.isAllowOTAfterShift = $("#allowOTAfterShiftEdit").val();
			var shiftBreakStartTime = $("#shiftBreakStartTime").jqxDateTimeInput('val', 'date');
			if(shiftBreakStartTime){
				dataSubmit.shiftBreakStartTime = shiftBreakStartTime.getTime();
			}
			var shiftBreakEndTime = $("#shiftBreakEndTime").jqxDateTimeInput('val', 'date');
			if(shiftBreakEndTime){
				dataSubmit.shiftBreakEndTime = shiftBreakEndTime.getTime();
			}
			var startOverTimeAfterShift = $("#startOverTimeAfterShift").jqxDateTimeInput('val', 'date');
			if(startOverTimeAfterShift){
				dataSubmit.startOverTimeAfterShift = startOverTimeAfterShift.getTime();
			}
			var endOverTimeAfterShift = $("#endOverTimeAfterShift").jqxDateTimeInput('val', 'date');
			if(endOverTimeAfterShift){
				dataSubmit.endOverTimeAfterShift = endOverTimeAfterShift.getTime();
			}
			var rows = $("#jqxgridShiftWorkType").jqxGrid('getrows');
			if(rows[0]){
				var dayWeekArr = globalObject.getDayWeekArr();
				for(var i = 0; i < dayWeekArr.length; i++){
					dataSubmit[dayWeekArr[i]] = rows[0][dayWeekArr[i]];
				}
			}
			$("#workingShiftEditWindow").jqxWindow('close');
			$("#jqxgrid").jqxGrid({disabled: true});
			$("#jqxgrid").jqxGrid('showloadelement');
			$.ajax({
				url: 'updateWorkingShift',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					$("#jqxNotification").jqxNotification('closeLast');
					if(response.responseMessage == 'success'){
						$('#containerNtf').empty();
						$('#jqxNotification').jqxNotification({template: 'info'});
						$("#notificationContent").text(response.successMessage);
						$("#jqxNotification").jqxNotification("open");
						$("#jqxgrid").jqxGrid('updatebounddata');
						globalVar.getAllWorkingShift = true;
					}else{
						$('#containerNtf').empty();
						$('#jqxNotification').jqxNotification({template: 'error'});
						$("#notificationContent").text(response.errorMessage);
						$("#jqxNotification").jqxNotification("open");
					}
				},
				complete:  function(jqXHR, textStatus){
					$("#jqxgrid").jqxGrid({disabled: false});
					$("#jqxgrid").jqxGrid('hideloadelement');
				}
			});
		});
	};
	
	var createNewWorkingShift = function(){
		var dataSubmit = {};
		if($("#workingShiftIdNew").val()){
			dataSubmit.workingShiftId = $("#workingShiftIdNew").val();
		}
		dataSubmit.workingShiftName = $("#workingShiftNameNew").val();
		dataSubmit.minMinuteOvertime = $("#minMinuteOvertimeNew").val();
		dataSubmit.allowLateMinute = $("#allowLateMinuteNew").val();
		dataSubmit.shiftStartTime = $("#shiftStartTimeNew").jqxDateTimeInput('val', 'date').getTime();
		dataSubmit.shiftEndTime = $("#shiftEndTimeNew").jqxDateTimeInput('val', 'date').getTime();
		dataSubmit.isAllowOTAfterShift = $("#allowOTAfterShift").val();
		var shiftBreakStartTime = $("#shiftBreakStartTimeNew").jqxDateTimeInput('val', 'date');
		if(shiftBreakStartTime){
			dataSubmit.shiftBreakStartTime = shiftBreakStartTime.getTime();
		}
		var shiftBreakEndTime = $("#shiftBreakEndTimeNew").jqxDateTimeInput('val', 'date');
		if(shiftBreakEndTime){
			dataSubmit.shiftBreakEndTime = shiftBreakEndTime.getTime();
		}
		var startOverTimeAfterShift = $("#startOverTimeAfterShiftNew").jqxDateTimeInput('val', 'date');
		if(startOverTimeAfterShift){
			dataSubmit.startOverTimeAfterShift = startOverTimeAfterShift.getTime();
		}
		var endOverTimeAfterShift = $("#endOverTimeAfterShiftNew").jqxDateTimeInput('val', 'date');
		if(endOverTimeAfterShift){
			dataSubmit.endOverTimeAfterShift = endOverTimeAfterShift.getTime();
		}
		var rows = $("#jqxgridShiftWorkTypeNew").jqxGrid('getrows');
		if(rows[0]){
			var dayWeekArr = globalObject.getDayWeekArr();
			for(var i = 0; i < dayWeekArr.length; i++){
				dataSubmit[dayWeekArr[i]] = rows[0][dayWeekArr[i]];
			}
		}
		$("#popupAddRow").jqxWindow('close');
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'createWorkingShift',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				$("#jqxNotification").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$('#containerNtf').empty();
					$('#jqxNotification').jqxNotification({template: 'info'});
					$("#notificationContent").text(response.successMessage);
					$("#jqxNotification").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
					globalVar.getAllWorkingShift = true;
				}else{
					$('#containerNtf').empty();
					$('#jqxNotification').jqxNotification({template: 'error'});
					$("#notificationContent").text(response.errorMessage);
					$("#jqxNotification").jqxNotification("open");
				}
			},
			complete:  function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
			}
		});
	};
	
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules:[
		      {
		    	 input: "#workingShiftIdNew",
				 message: uiLabelMap.FieldRequired,
				 rule: 'required'
		      },
		      {
	    	     input: "#workingShiftIdNew",
				 message: uiLabelMap.MustntHaveSpaceChar,
				 rule: function(input, commit){
					 var val = input.val();
					 var space = " ";
					 if(val.indexOf(space) > -1){
						 return false;
					 }
					 return true;
				 }
		      },
		      {
		    	  input: "#workingShiftIdNew",
		    	  message : uiLabelMap.InvalidChar,
		    	  rule : function(input, commit){
		    		  var val = input.val();
		    		  if(val){
		    			  if(validationNameWithoutHtml(val)){
		    				  return false;
		    			  }
		    		  }
		    		  return true;
		    	  }
		      },
		      {
		    	  input: "#workingShiftNameNew",
		    	  message : uiLabelMap.InvalidChar,
		    	  rule : function(input, commit){
		    		  var val = input.val();
		    		  if(val){
		    			  if(validationNameWithoutHtml(val)){
		    				  return false;
		    			  }
		    		  }
		    		  return true;
		    	  }
		      },
			  {
				input: "#workingShiftNameNew",
				message: uiLabelMap.FieldRequired,
				rule: 'required'
			  },
			  {
				input: "#shiftStartTimeNew",
				message: uiLabelMap.FieldRequired,
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			  },
			  {
				input: "#shiftEndTimeNew",
				message: uiLabelMap.FieldRequired,
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			  },
			  {
				  input: "#startOverTimeAfterShiftNew",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShift").val()){
						  return true;
					  }
					  if(!input.val()){
						  return false;
					  }
					  return true;
				  }
			  },
			  {
				  input: "#endOverTimeAfterShiftNew",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShift").val()){
						  return true;
					  }
					  if(!input.val()){
						  return false;
					  }
					  return true;
				  }
			  },
			  {
				  input: "#minMinuteOvertimeNew",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShift").val()){
						  return true;
					  }
					  if(input.val() == undefined){
						  return false;
					  }
					  return true;
				  }
			  }
			]
		});
		$("#workingShiftEditWindow").jqxValidator({
			rules:[
			  {
				input: "#workingShiftName",
				message: uiLabelMap.FieldRequired,
				rule: 'required'
			  },
			  {
				input: "#shiftStartTime",
				message: uiLabelMap.FieldRequired,
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			  },
			  {
				input: "#shiftEndTime",
				message: uiLabelMap.FieldRequired,
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			  },
			  {
				  input: "#startOverTimeAfterShift",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShiftEdit").val()){
						  return true;
					  }
					  if(!input.val()){
						  return false;
					  }
					  return true;
				  }
			  },
			  {
				  input: "#endOverTimeAfterShift",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShiftEdit").val()){
						  return true;
					  }
					  if(!input.val()){
						  return false;
					  }
					  return true;
				  }
			  },
			  {
				  input: "#minMinuteOvertime",
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
					  if(!$("#allowOTAfterShiftEdit").val()){
						  return true;
					  }
					  if(input.val() == undefined){
						  return false;
					  }
					  return true;
				  }
			  }
			]
		});
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	createWorkingShiftObject.init();
});

function generatedataWorkShiftTypeInWeek(){
	var data = [{
		MONDAY: 'ALL_SHIFT',
		TUESDAY: 'ALL_SHIFT',
		WEDNESDAY: 'ALL_SHIFT',
		THURSDAY: 'ALL_SHIFT',
		FRIDAY:'ALL_SHIFT',
		SATURDAY: 'FIRST_HALF_SHIFT',
		SUNDAY: 'DAY_OFF'
	}];
	return data;
}