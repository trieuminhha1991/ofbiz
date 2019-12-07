var viewListEmplLeaveObject = (function(){
	var init = function(){
		initJqxNumberInput();
		initJqxTreeButton();
		initJqxWindow();
		initJqxDateTimeInput();
		initBtnEvent();
		initJqxValidator();
		initJqxWindowEvent();
		initContextMenu();
	};
	
	var initJqxWindowEvent = function(){
		$('#updateLeaveTimeTrackerWindow').bind('close', function(){
			$('#updateLeaveTimeTrackerWindow').jqxValidator('hide');
		})
	};
	
	var initJqxValidator = function(){
		$('#updateLeaveTimeTrackerWindow').jqxValidator({
			rules : [
			         {
			        	 input : '#fromDateUpdateTimeTracker',
			        	 message : uiLabelMap.FromDateLessThanEqualThruDate,
			        	 action : 'blur',
			        	 rule : function(input, commit){
			        		 if($('#thruDateUpdateTimeTracker').jqxDateTimeInput('getDate') <= input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#thruDateUpdateTimeTracker',
			        	 message : uiLabelMap.GTDateFieldRequired,
			        	 action : 'blur',
			        	 rule : function(input, commit){
			        		 if($('#fromDateUpdateTimeTracker').jqxDateTimeInput('getDate') >= input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         }
	         ]
		})
	};
	var initJqxWindow = function(){
		createJqxWindow($("#updateLeaveTimeTrackerWindow"), 420, 200);
		$("#updateLeaveTimeTrackerWindow").on('open', function(event){
			var nowDate = new Date();
			var year = $("#yearNumberInput").val();
			var month = nowDate.getMonth();
			var startDate = new Date(year, month, 1);
			var endDate = new Date(year, month + 1, 0);
			$("#fromDateUpdateTimeTracker").val(startDate);
			$("#thruDateUpdateTimeTracker").val(endDate);
		});
	};
	var initJqxDateTimeInput = function(){
		$("#fromDateUpdateTimeTracker").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateUpdateTimeTracker").jqxDateTimeInput({width: '98%', height: 25});
	};
	var initBtnEvent = function(){
		$('#removeFilter').click(function(){
			$('#jqxgrid').jqxGrid('clearfilters');
		});
		$("#updateFromTimeTracker").click(function(event){
			openJqxWindow($("#updateLeaveTimeTrackerWindow"));
		});
		$("#btnCancelUpdate").click(function(event){
			$("#updateLeaveTimeTrackerWindow").jqxWindow('close');
		});
		$("#btnSaveUpdate").click(function(event){
			var fromDate = $("#fromDateUpdateTimeTracker").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDateUpdateTimeTracker").jqxDateTimeInput('val', 'date');
			if($('#updateLeaveTimeTrackerWindow').jqxValidator('validate')){
				$("#jqxgrid").jqxGrid({disabled: true});
				$("#jqxgrid").jqxGrid('showloadelement');
				$("#updateLeaveTimeTrackerWindow").jqxWindow('close');
				$.ajax({
					url: 'updateEmplLeaveFromTimeTracker',
					data: {fromDate: fromDate.getTime(), thruDate: thruDate.getTime()},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							$("#jqxNtfContent").text(response.successMessage);
							$("#jqxNtf").jqxNotification({template: 'info'});
							$("#jqxNtf").jqxNotification("open");
							$("#jqxgrid").jqxGrid('updatebounddata');
						}else{
							$("#jqxNtfContent").text(response.errorMessage);
							$("#jqxNtf").jqxNotification({template: 'error'});
							$("#jqxNtf").jqxNotification("open");
						}
					},
					error: function(){
						
					},
					complete: function(jqXHR, textStatus){
						$("#jqxgrid").jqxGrid({disabled: false});
						$("#jqxgrid").jqxGrid('hideloadelement');
					}
				});
			}else{
				return false;
			}
		});
	}
	
	var initJqxNumberInput = function(){
		$("#yearNumberInput").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple'});
		$("#yearNumberInput").on('valueChanged', function(event){
			var value = event.args.value;
			var item = $("#jqxTree").jqxTree('getSelectedItem');
			if(item){
				var partyId = item.value;
				refreshGridData(partyId, value);
			}
		});
		$("#yearNumberInput").val(globalVar.YEAR);
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 300, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var year = $("#yearNumberInput").val();
			if(year){
				refreshGridData(partyId, year);
			}
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var refreshGridData = function(partyId, year){
		if(partyId && year){
			var tmpSource = $("#jqxgrid").jqxGrid('source');
			tmpSource._source.url = 'jqxGeneralServicer?sname=JQgetListEmplLeave&hasrequest=Y&year=' + year + "&partyId="+ partyId;
			$("#jqxgrid").jqxGrid('source', tmpSource);
		}
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var action = $(args).attr("action");
			if(action == "approver"){
				approvalEmplLeaveObject.setData($("#jqxgrid").jqxGrid('getrowdata', boundIndex));
				approvalEmplLeaveObject.openWindow();
			}
		});
		$("#contextMenu").on('shown', function () {
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			if(data.statusId != 'LEAVE_CREATED'){
				$('#contextMenu').jqxMenu('disable', 'approver', true);
			}else{
				$('#contextMenu').jqxMenu('disable', 'approver', false);
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	viewListEmplLeaveObject.init();
});