var viewListAnnualObject = (function(){
	var init = function(){
		initJqxNumberInput();
		initJqxTreeButton();
		initJqxWindow();
		initBtnEvent();
		initJqxNotification();
		initJqxCheckBox();
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#popupAddNewWindow"), 400, 180);
		$("#popupAddNewWindow").on('open', function(event){
			var tempYear = globalVar.YEAR;
			if($("#yearNumberInput").val()){
				tempYear = $("#yearNumberInput").val();
			}
			$("#annualLeaveYear").val(tempYear);
		});
		$("#popupAddNewWindow").on('close', function(event){
			$("#moveAnnualLastYearLeft").jqxCheckBox({checked: false});
		});
	};
	
	var initJqxCheckBox = function(){
		$("#moveAnnualLastYearLeft").jqxCheckBox({width: 50, height: 25, checked: false, theme: 'olbius'});
	};
	
	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	
	var initBtnEvent = function(){
		$("#removeFilter").click(function(){
			$('#jqxgrid').jqxGrid('clearfilters');
		})
		$("#addNew").click(function(event){
			openJqxWindow($("#popupAddNewWindow"));
		});
		$("#alterCancel").click(function(event){
			$("#popupAddNewWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			createAnnualLeaveSheet();
			$("#popupAddNewWindow").jqxWindow('close');
		});
	};
	
	var createAnnualLeaveSheet = function(){
		$("#addNew").attr("disabled", "disabled");
		$("#jqxgrid").jqxGrid({ disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		var data = {};
		data.year = $("#annualLeaveYear").val();
		if($("#moveAnnualLastYearLeft").jqxCheckBox('checked')){
			data.isTransferredAnnualLastYear = "Y";
		}else{
			data.isTransferredAnnualLastYear = "N";
		}
		$.ajax({
			url: 'createEmplLeaveRegulation',
			data: data,
			type: 'POST',
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response._EVENT_MESSAGE_){
					$("#jqxNtfContent").text(response._EVENT_MESSAGE_);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNtfContent").text(response._ERROR_MESSAGE_);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({ disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
				$("#addNew").removeAttr("disabled");
			}
		});
	};
	
	var initJqxNumberInput = function(){
		$("#yearNumberInput").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple'});
		$("#annualLeaveYear").jqxNumberInput({width: '90%', height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
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
			var source = $("#jqxgrid").jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JQgetListAnnualLeaveSheet&hasrequest=Y&year=' + year + "&partyId="+ partyId;
			$("#jqxgrid").jqxGrid('source', source);
		}
	}
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewListAnnualObject.init();
});