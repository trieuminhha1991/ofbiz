var assignWSGroupEmpl = (function(){
	var init = function(){
		initJqxDropDown();
		initJqxDateTimeInput();
		initJqxCheckBox();
		initJqxGridSearchEmpl();
		initDropDownGrid();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerGroupEmpl"));
	};
	var setContentDropDownBtn = function(content){
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
		$("#dropDownButtonGroupEmpl").jqxDropDownButton("setContent", dropDownContent);
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, url: 'JQGetEmplListInOrg&hasrequest=Y', selectionmode: "multiplerows"});
	};
	var initJqxDropDown = function(){
		createJqxDropDownList(workingShiftArr, $("#workingShiftGroupEmpl"), 'workingShiftId', 'workingShiftName', 25, '97%');
	};
	var initJqxSplitter =  function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initJqxWindow = function(){
		createJqxWindow($("#assignWSEmplListWindow"), 500, 310);
		var initContent = function(){
			initJqxSplitter();
		};
		createJqxWindow($("#popupWindowEmplList"), 900, 560, initContent);
		$('#popupWindowEmplList').on('open', function(event){
			$("#assignWSEmplListWindow").jqxValidator('hide');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
		$("#assignWSEmplListWindow").on('open', function(event){
			var source = $("#jqxGridGroupEmpl").jqxGrid("source");
			if(source){
				var localdata = source._source.localdata;
				setContentDropDownBtn(localdata.length + " " + uiLabelMap.EmployeeSelected);
			}
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#assignGroupEmplFromDate").val(selection.from);
			$("#assignGroupEmplThruDate").val(selection.to);
		});
		$("#assignWSEmplListWindow").on('close', function(event){
			var source = $("#jqxGridGroupEmpl").jqxGrid("source");
			source._source.localdata = [];
			$("#assignWSEmplListWindow").jqxValidator('hide');
			$("#jqxGridGroupEmpl").jqxGrid("source", source);
			$("#workingShiftGroupEmpl").jqxDropDownList('clearSelection');
			$("#overrideData").jqxCheckBox({checked: true});
		});
	};
	var initJqxDateTimeInput = function(){
		$("#assignGroupEmplFromDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#assignGroupEmplThruDate").jqxDateTimeInput({width: '97%', height: 25});
	};
	var initJqxCheckBox = function(){
		$("#overrideData").jqxCheckBox({width: 120, height: 25, checked: true});
	};
	var initEvent = function(){
		$("#chooseEmplBtn").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
		});
		$("#cancelChooseEmpl").click(function(event){
			$("#popupWindowEmplList").jqxWindow('close');
		});
		$("#saveChooseEmpl").click(function(event){
			var selectedIndexes = $("#EmplListInOrg").jqxGrid('getselectedrowindexes')
			$("#EmplListInOrg").jqxGrid('clearselection');
			var partyIdArrAdd = [];
			for(var i = 0; i< selectedIndexes.length; i++){
				var rowData = $("#EmplListInOrg").jqxGrid('getrowdata', selectedIndexes[i]);
				partyIdArrAdd.push({partyId: rowData.partyId, partyName: rowData.fullName, emplPositionType: rowData.emplPositionType});
			}
			$('#popupWindowEmplList').jqxWindow('close');
			addPartyToEmplGroup(partyIdArrAdd);
		});
		$("#saveGroupEmpl").click(function(event){
			var valid = $("#assignWSEmplListWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			assignWorkingShiftForEmpl(true);
		});
		$("#saveAndContinueGroupEmpl").click(function(event){
			var valid = $("#assignWSEmplListWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			assignWorkingShiftForEmpl(false);
		});
		$("#cancelGroupEmpl").click(function(event){
			$("#assignWSEmplListWindow").jqxWindow('close');
		});
	};
	var assignWorkingShiftForEmpl = function(isCloseWindow){
		$("#loadingGroupEmpl").show();
		disable();
		var data = getData();
		$.ajax({
			url: 'assignWorkingShiftForGroupEmpl',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					if(isCloseWindow){
						$("#assignWSEmplListWindow").jqxWindow('close');
					}
					$("#notificationContent").text(response.successMessage);
					$("#jqxNotification").jqxNotification({template: 'info'});
					$("#jqxNotification").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					if(response.isUpdate){
						$("#jqxgrid").jqxGrid('updatebounddata');
					}
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete:  function(jqXHR, textStatus){
				$("#loadingGroupEmpl").hide();
				enable();
			}
		});
	};
	var getData = function(){
		var data = {};
		data.workingShiftId = $("#workingShiftGroupEmpl").val();
		var fromDate = $("#assignGroupEmplFromDate").jqxDateTimeInput('val', 'date');
		data.fromDate = fromDate.getTime();
		var thruDate = $("#assignGroupEmplThruDate").jqxDateTimeInput('val', 'date');
		data.thruDate = thruDate.getTime();
		var check = $("#overrideData").jqxCheckBox('checked');
		if(check){
			data.overrideData = "Y";
		}else{
			data.overrideData = "N";
		}
		var rows = $("#jqxGridGroupEmpl").jqxGrid('getrows');
		var listParty = [];
		for(var i = 0; i < rows.length; i++){
			listParty.push(rows[i].partyId);
		}
		data.partyIds = JSON.stringify(listParty);
		return data;
	};
	var initDropDownGrid = function(){
		$("#dropDownButtonGroupEmpl").jqxDropDownButton({width: '100%', height: 25});
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyId', width : '23%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'partyName', width: '30%', editable: false},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
		var grid = $("#jqxGridGroupEmpl");
   		var rendertoolbar = function (toolbar){
   			toolbar.html("");
   			var id = "jqxGridGroupEmpl";
   			var me = this;
   			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeListSelected + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
   			toolbar.append(jqxheader);
   	     	var container = $('#toolbarButtonContainer' + id);
   	        var maincontainer = $("#toolbarcontainer" + id);
	   	    var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+ uiLabelMap.wgdelete + '</span></button>';
	        container.append(str);
	        var obj = $("#deleterowbutton" + id);
	        obj.jqxButton();
	        obj.click(function(){
	        	var selectedrowindexes = grid.jqxGrid('getselectedrowindexes');
	        	var rowIDs = [];
	        	for(var i = 0; i < selectedrowindexes.length; i++){
	        		var rowid = grid.jqxGrid('getrowid', selectedrowindexes[i]);
	        		rowIDs.push(rowid);
	        	}
	        	grid.jqxGrid('deleterow', rowIDs);
	        	var source = $("#jqxGridGroupEmpl").jqxGrid('source');
	    		var records = source.records;
	    		source._source.localdata = records;
	    		setContentDropDownBtn(records.length + " " + uiLabelMap.EmployeeSelected);
	    		grid.jqxGrid('clearselection');
	        });
   		};               
		var config = {
		   		width: "50%", 
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: false,
		   		showfilterrow: false,
		   		selectionmode: 'multiplerows',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
	   			showtoolbar: true,
	   			rendertoolbar : rendertoolbar,
	        	source: {pagesize: 5, id: 'partyId', localdata: []}
		 };
		Grid.initGrid(config, datafield, columns, null, $("#jqxGridGroupEmpl"));
	};
	var addPartyToEmplGroup = function(partyArr){
		var source = $("#jqxGridGroupEmpl").jqxGrid('source');
		var localdata = source._source.localdata;
		for(var i = 0; i < partyArr.length; i++){
			var partyId = partyArr[i].partyId;
			var partyIdExists = $("#jqxGridGroupEmpl").jqxGrid('getrowdatabyid', partyId);
			if(!partyIdExists){
				localdata.push(partyArr[i]);
			}
		}
		source._source.localdata = localdata;
		$("#jqxGridGroupEmpl").jqxGrid('source', source);
		setContentDropDownBtn(localdata.length + " " + uiLabelMap.EmployeeSelected);
	};
	var initJqxValidator = function(){
		$("#assignWSEmplListWindow").jqxValidator({
			rules: [
		        {input : '#dropDownButtonGroupEmpl', message : uiLabelMap.NoPartyChoose, action : 'blur',
		        	rule : function(input, commit){
		        		var records = $("#jqxGridGroupEmpl").jqxGrid('source').records;
		        		if(records.length <= 0){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input : '#assignGroupEmplFromDate', message : uiLabelMap.FieldRequired, action : 'blur',
		        	rule : function(input, commit){
		        		if(!input.val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input : '#assignGroupEmplThruDate', message : uiLabelMap.FieldRequired, action : 'blur',
		        	rule : function(input, commit){
		        		if(!input.val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input : '#assignGroupEmplThruDate', message : uiLabelMap.ThruDateMustBeAfterFromDate, action : 'blur',
		        	rule : function(input, commit){
		        		var fromDate = $("#assignGroupEmplFromDate").jqxDateTimeInput('val', 'date');
		        		var thruDate = $("#assignGroupEmplThruDate").jqxDateTimeInput('val', 'date');
		        		if(fromDate && thruDate){
		        			if(fromDate >= thruDate){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
		        },
		        {input : '#workingShiftGroupEmpl', message : uiLabelMap.FieldRequired, action : 'blur',
		        	rule : function(input, commit){
		        		if(!input.val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        }
			]
		
		});
	};
	var disable = function(){
		$("#cancelGroupEmpl").attr("disabled", "disabled");
		$("#saveGroupEmpl").attr("disabled", "disabled");
		$("#saveAndContinueGroupEmpl").attr("disabled", "disabled");
		$("#chooseEmplBtn").attr("disabled", "disabled");
		$("#dropDownButtonGroupEmpl").jqxDropDownButton({disabled: true});
	};
	var enable = function(){
		$("#cancelGroupEmpl").removeAttr("disabled");
		$("#saveGroupEmpl").removeAttr("disabled");
		$("#saveAndContinueGroupEmpl").removeAttr("disabled");
		$("#chooseEmplBtn").removeAttr("disabled");
		$("#dropDownButtonGroupEmpl").jqxDropDownButton({disabled: false});
	};
	var openWindow = function(){
		openJqxWindow($("#assignWSEmplListWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	assignWSGroupEmpl.init()
});