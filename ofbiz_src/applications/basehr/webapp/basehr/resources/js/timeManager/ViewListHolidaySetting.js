var viewListHolidayObject = (function(){
	var init = function(){
		initJqxNotification();	
		initJqxInput();
		initjqxDateTimeInput();
		initJqxDropdownlist();
		initJqxValidator();
		initJqxNumberInput();
		initJqxCheckBox();
		initJqxGrid();
		initJqxWindow();
	};
	
	var initJqxNumberInput = function (){
		$("#dateHolidayConfig").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 31});
		$("#monthHolidayConfig").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12});
	};
	
	var initJqxInput = function(){
		$("#holidayName").jqxInput({width: '94%', height: 20, theme: 'olbius'});
		$("#note").jqxInput({width: '94%', height: 20, theme: 'olbius'});
		$("#holidayNameConfig").jqxInput({width: '94%', height: 20, theme: 'olbius'});
	};

	var initjqxDateTimeInput = function(){
		$("#dateHoliday").jqxDateTimeInput({ width: '96%', height: '25px', theme: 'olbius'});	
	};

	var initJqxDropdownlist = function(){
		var source = {
		        localdata: emplTimekeepingSignArr,
		        datatype: "array"
	    };
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#emplTimekeepingSignId, #emplTimekeepingSignIdConfig").jqxDropDownList({source: dataAdapter, displayMember: "sign", valueMember: "emplTimekeepingSignId", theme: 'olbius', 
				width: '96%', height: 25,
				renderer: function (index, label, value){
					var data = emplTimekeepingSignArr[index];
					return data.sign + " (" + data.description + ")";
				}		
		});
	};

	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules:[
			  {
				input: "#holidayName",
				message: uiLabelMap.FieldRequired,
				rule: 'required'
			  },
			  {
				input : "#holidayName",
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
				input: "#dateHoliday",
				message: uiLabelMap.FieldRequired,
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			  },
			  {
				  input: '#emplTimekeepingSignId',
				  message: uiLabelMap.FieldRequired,
				  rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
				  }
			  }
			]
		});
		
		$("#newHolidayConfigWindow").jqxValidator({
			rules: [
				{
					input: '#holidayNameConfig',
					message: uiLabelMap.FieldRequired,
					rule: 'required'
				},
				{
					input : '#holidayNameConfig',
					message : uiLabelMap.InvalidChar,
					action : 'blur',
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
					  input: '#emplTimekeepingSignIdConfig',
					  message: uiLabelMap.FieldRequired,
					  rule: function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
					  }
				  }
			]
		});
	};
	
	var initJqxCheckBox = function(){
		 $('#lunarCalendar').jqxCheckBox({height: 25, theme: 'olbius'});
	};

	var initJqxGrid = function(){
		var datafield =  [
		          		{name: 'holidayConfigId', type: 'string'},
		          		{name: 'emplTimekeepingSignId', type: 'string'},
		          		{name: 'description', type: 'string'},
		          		{name: 'calendarType', type: 'bool'},
		          		{name: 'dateOfMonth', type: 'number'},
		          		{name: 'sign', type: 'string'},
		          		{name: 'description', type: 'string'},
		          		{name: 'month', type: 'number'},
		          		{name: 'descriptionSign', type: 'string'},
	   	];
	   	var columnlist = [
			{datafield: 'holidayConfigId' , editable: false, hidden: true},
	   	  	{text: uiLabelMap.HolidayName, datafield: 'description', editable: false, width: 220, filterable: false},
	   	  	{datafield: 'month', editable: false, hidden: true,filterable: false},
	   	  	{text: uiLabelMap.DateHoliday, datafield: 'dateOfMonth', editable: false, width: 100, cellsalign: 'left', filterable: false,
		   	  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		   	  		var data = $('#jqxgridHolidayConfig').jqxGrid('getrowdata', row);
		   	  		var month = data.month;
		   	  		if(value < 10){
		   	  			value = "0" + value;
		   	  		}
		   	  		if(month < 10){
		   	  			month = "0" + month;
		   	  		}
		   	  		return '<span>' + value + '/' + month +'</span>';
		   	  	}	
	   	  	},
	   	 	{datafield: 'emplTimekeepingSignId', hidden: true},
	   	  	{text: uiLabelMap.HRCommonEmplTimekeepingSignShort, datafield: 'sign', editable: false, width: 100, filterable: false,
		   	 	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
				  	if(value){
				  		var data = $('#jqxgridHolidayConfig').jqxGrid('getrowdata', row);
						var text = value + ' (' + data.descriptionSign + ')';
						return '<span>' + text + '<span>';							  
				 	}
			   }
	   	  	},
	   	 	{text: uiLabelMap.LunarCalendar, datafield: 'calendarType', editable: false,  columntype: 'checkbox'}   	  
	   	];
	   	var grid =  $("#jqxgridHolidayConfig");
	   	var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "jqxgridHolidayConfig";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 style='max-width: 60%; overflow: hidden'></h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newHolidayConfigWindow")});
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
	   
	   	var showtoolbar = false;
	   	var source;
	   	if(globalVar.hasPermission){
	   		showtoolbar = true;
	   		source = {pagesize: 10, removeUrl: 'deleteHolidayConfig', deleteColumns: 'holidayConfigId'};
	   	}else{
	   		source = {pagesize: 10};
	   	}
	   	var config = {
	   		width: '100%', 
	   		height: 355,
	   		autoheight: false,
	   		virtualmode: true,
	   		showfilterrow: true,
	   		rendertoolbar: rendertoolbar,
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: false,
	        rowsheight: 26,
	        selectionmode: 'singlerow',
	        url: 'JQGetListHolidayConfig&hasrequest=Y',    
   			showtoolbar: showtoolbar,
        	source: source
	   	};
	   	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#holidayConfigListWindow"), 600, 410);
		createJqxWindow($("#newHolidayConfigWindow"), 500, 260);
		createJqxWindow($("#popupAddRow"), 450, 270);
	};

	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
		$("#jqxNotificationjqxgridHolidayConfig").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxgridHolidayConfig"});
	};
	
	return{
		init: init
	}
}());

var eventObject = (function(){
	var init = function(){
		initBtnEvent();
		initGridEvent();
		initJqxWindowEvent();
	};
	
	var initJqxWindowEvent = function(){
		$("#popupAddRow").on('open', function(event){
			var year = $("#yearNumberInput").val();
			var startYear = new Date(globalVar.startYear);
			var endYear =  new Date(globalVar.endYear);
			var nowDate = new Date(globalVar.nowTimestamp);
			var currMonth = nowDate.getMonth();
			var currDate = nowDate.getDate();
			var selectDate = new Date(year, currMonth, currDate, 0, 0, 0, 0);
			$("#dateHoliday").jqxDateTimeInput({min: startYear, max: endYear});
			$("#dateHoliday").val(selectDate);
			$("#popupAddRow").jqxWindow('setTitle', uiLabelMap.AddHolidayInYear + ' ' + year);
		});
		
		$("#popupAddRow").on('close', function(event){
			$("#popupAddRow").jqxValidator('hide');
			Grid.clearForm($(this));
		});
		
		$("#newHolidayConfigWindow").on('open', function(event){
			var date = new Date(globalVar.nowTimestamp);
			$("#dateHolidayConfig").val(date.getDate());
			$("#monthHolidayConfig").val(date.getMonth() + 1);
		});
		
		$("#newHolidayConfigWindow").on('close', function(event){
			$("#newHolidayConfigWindow").jqxValidator('hide');
			Grid.clearForm($(this));
		});
	};
	
	var initGridEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(event){
			$("#yearNumberInput").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
				digits: 4, spinMode: 'simple',  inputMode: 'simple'});
			$("#yearNumberInput").val(globalVar.YEAR);
			$("#yearNumberInput").on('valueChanged', function(event){
				var value = event.args.value;
				var source = $("#jqxgrid").jqxGrid('source');
				source._source.url = 'jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y&year=' + value;
				$("#jqxgrid").jqxGrid('source', source);
			});
		});
	};
	
	var initBtnEvent = function(){
		$("#btnCancel").click(function(event){
			$("#popupAddRow").jqxWindow('close');
		});
		
		$("#btnSave").click(function(event){
			var valid = $("#popupAddRow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateHolidayConfirm,
				 [
				 {
	    		    "label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn btn-small btn-primary icon-ok open-sans",
	    		    "callback": function() {
	    		    	actionObject.createHoliday();	    		
	    		    }
	    		 },
	    		 {
	    		    "label" : uiLabelMap.CommonCancel,
	    		    "class" : "btn-danger icon-remove btn-small open-sans",
	    		 }
	    		 ]
			 );
		});
		
		$("#saveHolidayConfig").click(function(event){
			var valid = $("#newHolidayConfigWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
				 [
				 {
		   		    "label" : uiLabelMap.CommonSubmit,
		   		    "class" : "btn btn-primary btn-small icon-ok open-sans",
		   		    "callback": function() {
		   		    	actionObject.createNewHolidayConfig();	    			
		   		    }
		   		 },
		   		 {
		   		    "label" : uiLabelMap.CommonCancel,
		   		    "class" : "btn btn-danger icon-remove btn-small open-sans",
		   		 }
		   		 ]
			 );
		});
		
		$("#cancelHolidayConfig").click(function(event){
			$("#newHolidayConfigWindow").jqxWindow('close');
		});
	};
	
	return {
		init: init
	}
}());

$(document).ready(function () {
	viewListHolidayObject.init();
	eventObject.init();
});


var actionObject = (function(){
	var createNewHolidayConfig = function(){
		var lunarCalendarCheck = $("#lunarCalendar").val();	
		var row = {
				description: $("#holidayNameConfig").val(),
				emplTimekeepingSignId: $("#emplTimekeepingSignIdConfig").val(),
				dateOfMonth: $("#dateHolidayConfig").val(),
				month:$("#monthHolidayConfig").val(),
				lunarCalendar: $("#lunarCalendar").val()	
		}
		$("#jqxgridHolidayConfig").jqxGrid({disabled: true});
		$("#jqxgridHolidayConfig").jqxGrid('showloadelement');
		$("#newHolidayConfigWindow").jqxWindow('close');
		$.ajax({
			url: 'createHolidayConfig',
			data: row,
			type: 'POST',
			success: function(response){
				$("#jqxNotificationjqxgridHolidayConfig").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					Grid.renderMessage('jqxgridHolidayConfig', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgridHolidayConfig", opacity : 0.9});
					$("#jqxgridHolidayConfig").jqxGrid('updatebounddata');
				}else{
					Grid.renderMessage('jqxgridHolidayConfig', response.errorMessage, {autoClose : true,
						template : 'error', appendContainer: "#containerjqxgridHolidayConfig", opacity : 0.9});
				}
			},
			complete:  function(jqXHR, textStatus){
				$("#jqxgridHolidayConfig").jqxGrid({disabled: false});
				$("#jqxgridHolidayConfig").jqxGrid('hideloadelement');
			}
		});
	};

	var createHoliday = function(){
		var row = {
				holidayName: $("#holidayName").val(),
				dateHoliday: $("#dateHoliday").jqxDateTimeInput('val', 'date'),
				emplTimekeepingSignId: $("#emplTimekeepingSignId").val(),
				note: $("#note").val()
		}
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		$("#popupAddRow").jqxWindow('close');
	};

	var confirmHolidayConfig = function(){
		var selectIndex = $("#jqxgridHolidayConfig").jqxGrid('getselectedrowindex');
		if(selectIndex > -1){
			bootbox.dialog(uiLabelMap.NotifyDelete,
					 [
					 {
			   		    "label" : uiLabelMap.CommonSubmit,
			   		 	"class" : "btn btn-small btn-primary icon-ok open-sans",
			   		    "callback": function() {
			   		    	actionObject.deleteHolidayConfig(selectIndex, $("#jqxgridHolidayConfig"));	    			
			   		    }
			   		 },
			   		 {
			   		    "label" : uiLabelMap.CommonCancel,
			   		    "class" : "btn btn-small btn-danger icon-remove open-sans",
			   		    "callback": function() {
			   		    	
			   		    }
			   		 }
			   		 ]
				 );
		}
	};

	var deleteHolidayConfig = function(selectIndex, gridEle){
		var id = gridEle.jqxGrid('getrowid', selectIndex);
		gridEle.jqxGrid('deleterow', id);
//		console.log(gridEle);
	};

	return{
		deleteHolidayConfig: deleteHolidayConfig,
		confirmHolidayConfig: confirmHolidayConfig,
		createHoliday: createHoliday,
		createNewHolidayConfig: createNewHolidayConfig
	}
}());
function displayListHolidayConfig(){
	openJqxWindow($("#holidayConfigListWindow"));
}

function grabHolidayInYear(){
	var year = $("#yearNumberInput").val();
	$("#jqxgrid").jqxGrid({disabled: true});
	$("#jqxgrid").jqxGrid('showloadelement');
	$.ajax({
		url: 'createOrUpdateHolidayInYear',
		data: {year: year},
		type: 'POST',
		success: function(response){
			if(response._EVENT_MESSAGE_){
				var source = $("#jqxgrid").jqxGrid('source');
				source._source.url = 'jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y&year=' + year;
				$("#jqxgrid").jqxGrid('source', source);
			}else{
				$("#jqxNtfContent").text(response._ERROR_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		},
		complete:  function(jqXHR, textStatus){
			$("#jqxgrid").jqxGrid({disabled: false});
			$("#jqxgrid").jqxGrid('hideloadelement');
		}
	});
}

function RemoveFilter(){
	$("#jqxgrid").jqxGrid('clearfilters');
}
