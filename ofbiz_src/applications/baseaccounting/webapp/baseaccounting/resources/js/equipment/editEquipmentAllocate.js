var editEquipmentAllocTimeObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
	};
	var initInput = function(){
		$("#allocatedYear").jqxNumberInput({ width: '40%', height: 25,  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		accutils.createJqxDropDownList($("#allocateMonth"), globalVar.monthArr, {valueMember: 'month', displayMember: 'description', width: '50%', height: 25});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#equipmentAllocateTimeWindow"), 330, 140);
	};
	var initEvent = function(){
		$("#equipmentAllocateTimeWindow").on('open', function(e){
			var date = new Date();
			$("#allocatedYear").val(date.getFullYear());
			$("#allocateMonth").val(date.getMonth());
		});
		$("#cancelEquipmentAllocTime").click(function(e){
			$("#equipmentAllocateTimeWindow").jqxWindow('close');
		});
		$("#saveEquipmentAllocTime").click(function(e){
			var month = $("#allocateMonth").val();
			var year = $("#allocatedYear").val();
			var date = new Date(year, parseInt(month) + 1, 0);
			editEquipmentAllocObj.openWindow({voucherDate: date});
		});
	};
	return{
		init: init
	}
}());

/**==================================================================**/

var editEquipmentAllocObj = (function(){
	var init = function(){
		initWindow();
		initWizard();
		initEvent();
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editEquipAllocateWindow"), 800, 550);
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.direction == "next") {
				var valid = editEquipmentAllocStep1.validate();
				if(!valid){
					return false;
				}
				editEquipmentAllocStep2.prepareData();
			}else if(info.direction == "previous"){
				
			}
		}).on('finished', function(e) {
			var valid = editEquipmentAllocStep2.validate();
			if(!valid){
				return false;
			}
			bootbox.dialog(uiLabelMap.BACCCreateEquipmentAllocateConfirm,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 editEquipmentAlloc();
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		}).on('stepclick', function(e){
 			//return false;//prevent clicking on steps
 		});
	};
	var openWindow = function(data){
		$("#voucherDate").val(data.voucherDate);
		accutils.openJqxWindow($("#editEquipAllocateWindow"));
	};
	var initEvent = function(){
		$("#editEquipAllocateWindow").on('open', function(e){
			editEquipmentAllocStep1.initWindowOpenEvent();
		});
		$("#editEquipAllocateWindow").on('close', function(e){
			editEquipmentAllocStep1.resetData();
			editEquipmentAllocStep2.resetData();
			$('#fuelux-wizard').wizard('previous');
			$("#equipmentAllocateTimeWindow").jqxWindow('close');
		});
	};
	
	var editEquipmentAlloc = function(){
		Loading.show('loadingMacro');
		var data = editEquipmentAllocStep1.getData();
		data.equipmentAllocItemParty = JSON.stringify(editEquipmentAllocStep2.getData());
		data.month = $("#allocateMonth").val();
		data.year = $("#allocatedYear").val();
		$.ajax({
			url: 'createEquipmentAllocateAndItem',
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					  );	
					  return;
				  }
				  $("#editEquipAllocateWindow").jqxWindow('close');
				  $("#jqxgrid").jqxGrid('updatebounddata');
				  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	editEquipmentAllocTimeObj.init();
	editEquipmentAllocStep1.init();
	editEquipmentAllocStep2.init();
	editEquipmentAllocObj.init();
	equipmentAllocationItemObj.init()
	equipmentAllocItemPtyAndStoreObj.init();
});