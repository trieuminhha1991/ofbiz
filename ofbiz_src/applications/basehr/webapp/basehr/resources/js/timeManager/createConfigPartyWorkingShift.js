var createConfigPartyWSObject = (function(){
	var _partyId = null;
	var init = function(){
		initTree();
		initWindow();
		initEvent();
		$("#jqxNotificationEditConfigPartyWS").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, 
			template: "info", appendContainer: "#containerNtfEditConfigPartyWS"});
	};
	var initWindow = function(){
		createJqxWindow($("#editConfigPartyWSWindow"), 350, 230);
	};
	var initTree = function(){
		$("#configPartyShiftTreeGrid").on('rowDoubleClick', function(event){
			 var rowData = event.args.row;
			 if(globalVar.getAllWorkingShift){			
				$.ajax({
					url: 'getAllWorkingShift',
					type: 'POST',
					success: function(response){
						if(response.responseMessage == 'success'){
							globalVar.getAllWorkingShift = false;	
							globalVar.allWorkingShiftArr = response.listReturn; 
							var source = {
		                		   localdata: globalVar.allWorkingShiftArr,
		                           datatype: "array"
		                   	}
		                   	var dataAdapter = new $.jqx.dataAdapter(source);
		                   	$("#workingShiftDropdownlist").jqxDropDownList({source: dataAdapter});
		                   	fillDataInConfigWSWindow(rowData);
						}
					},
					complete:  function(jqXHR, textStatus){
						$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: false});
					}
				});
			}else{
				fillDataInConfigWSWindow(rowData);
			}
			 openJqxWindow($("#editConfigPartyWSWindow"));		 
		});
	};
	
	var fillDataInConfigWSWindow = function(rowData){
		_partyId = rowData.partyId;
		$("#configWSPartyId").val(rowData.partyCode);
		if(rowData.partyName){
			$("#configWSPartyName").val(rowData.partyName);
		}
		if(rowData.workingShiftId){
			$("#workingShiftDropdownlist").val(rowData.workingShiftId);
		}
	};
	
	var initEvent = function(){
		$("#saveConfigPartyWS").click(function(event){
			var workingShiftSelectItem = $("#workingShiftDropdownlist").jqxDropDownList('getSelectedItem');
			if(workingShiftSelectItem){
				var dataSubmit = {};	
				dataSubmit.partyId = _partyId;
				dataSubmit.workingShiftId = workingShiftSelectItem.value;
				$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: true});
				$("#saveConfigPartyWS").attr("disabled", "disabled");
				$("#cancelConfigPartyWS").attr("disabled", "disabled");
				$.ajax({
					url: 'EditWorkingShiftPartyConfig',
					data: dataSubmit,
					type: 'POST',
					success: function(response){
						//$("#jqxNotificationEditConfigPartyWS").jqxNotification("closeLast");
						if(response._EVENT_MESSAGE_){
							Grid.renderMessage('EditConfigPartyWS', response._EVENT_MESSAGE_, {autoClose : true,
								template : 'info', appendContainer: "#containerNtfEditConfigPartyWS", opacity : 0.9});
							//var selection = $("#configPartyShiftTreeGrid").jqxTreeGrid('getSelection');
							$("#configPartyShiftTreeGrid").jqxTreeGrid('setCellValue', dataSubmit.partyId, 'workingShiftName', workingShiftSelectItem.label);
							$("#configPartyShiftTreeGrid").jqxTreeGrid('setCellValue', dataSubmit.partyId, 'workingShiftId', workingShiftSelectItem.value);
						}else{
							Grid.renderMessage('EditConfigPartyWS', response._ERROR_MESSAGE_, {autoClose : true,
								template : 'error', appendContainer: "#containerNtfEditConfigPartyWS", opacity : 0.9});
						}
						$("#editConfigPartyWSWindow").jqxWindow('close');
					},
					complete:  function(jqXHR, textStatus){
						$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: false});
						$("#saveConfigPartyWS").removeAttr("disabled");
						$("#cancelConfigPartyWS").removeAttr("disabled");
					}
				});	
			}else{
				$("#editConfigPartyWSWindow").jqxWindow('close');
			}
		});
		$("#cancelConfigPartyWS").click(function(event){
			$("#editConfigPartyWSWindow").jqxWindow('close');
		});
		$("#editConfigPartyWSWindow").on('close', function(event){
			_partyId = null;
			$("#configWSPartyId").val("");
			$("#configWSPartyName").val("");
			$("#workingShiftDropdownlist").jqxDropDownList('clearSelection');
		});
	};
	
	return{
		init: init
	}
}());
$(document).ready(function(){
	createConfigPartyWSObject.init();
});