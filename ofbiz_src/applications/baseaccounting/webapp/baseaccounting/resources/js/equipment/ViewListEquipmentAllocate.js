var viewListEquipmentAllocateObj = (function(){
	var init = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			accutils.createJqxDropDownList($("#postedStatus"), globalVar.postedArr, 
					{valueMember: 'isPosted', displayMember: 'description', width: 140, height: 25});
			$("#postedStatus").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var source = $("#jqxgrid").jqxGrid('source');
					source._source.url = 'jqxGeneralServicer?sname=JQGetListEquipmentAllocate&isPosted=' + value;
					$("#jqxgrid").jqxGrid('source', source);
				}
			});
			$("#postedStatus").jqxDropDownList('selectItem', 'ALL');
		});
		initContextMenu();
		initEventContextment();
	};
	
	var initEventContextment = function(){
		$('#jqxgrid').on('rowselect', function (event){
			var args = event.args;
			var rowdata = args.row;
			if(rowdata['isPosted']==true){
				$("#contextMenu").jqxMenu('disable', "deleteEquipmentAllocate", true);
			}else{
				$("#contextMenu").jqxMenu('disable', "deleteEquipmentAllocate", false);
			}
		});
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 160);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				updateEquipmentAllocateObj.openWindow(data);//updateEquipmentAllocateObj is defined in updateEquipmentAllocateObj.js
			}else if(action == "delete"){
				bootbox.dialog(uiLabelMap.BACCConfirmDelete,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 deleteEquipmentAllocate(data.equipmentAllocateId);
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}
		});
	};
	var deleteEquipmentAllocate = function(equipmentAllocateId){
		Loading.show('loadingMacro');	
		$.ajax({
			url: 'deleteEquipmentAllocate',
			type: "POST",
			data: {equipmentAllocateId: equipmentAllocateId},
			success: function(response) {
				  if(response._ERROR_MESSAGE_){
					  bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					  );	
					  return;
				  }
				  $("#updateEquipmentAllocateWindow").jqxWindow('close');
				  $("#jqxgrid").jqxGrid('updatebounddata');
				  Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	viewListEquipmentAllocateObj.init();
});