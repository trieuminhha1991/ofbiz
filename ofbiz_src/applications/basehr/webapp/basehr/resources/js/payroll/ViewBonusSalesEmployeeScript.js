var viewBonusSaleEmplObject = (function(){
	var init = function(){
		initJqxDateTime();
		initJqxTreeDropDownBtn();
		addBtnEvent();
		initJqxNotification();
	};
	var addBtnEvent = function(){
		$("#confirmSalesCommission").click(function(event){
			var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
			if(rowindexes.length <= 0){
				bootbox.dialog(uiLabelMap.NoDataRecordSelectedToConfirm,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-primary btn-mini icon-ok",
			    		    "callback": function() {
			    		    	
			    		    }
			    		}]
				);	
			}else{
				bootbox.dialog(uiLabelMap.AreYouSure,
						[{
			    		    "label" : uiLabelMap.CommonSubmit,
			    		    "class" : "btn-primary btn-mini icon-ok",
			    		    "callback": function() {
			    		    	createBonusParamEmplSales(rowindexes);		
			    		    }
			    		},
			    		{
			    		    "label" : uiLabelMap.CommonCancel,
			    		    "class" : "btn-danger icon-remove btn-mini",
			    		    "callback": function() {
			    		    }
			    		}]
				);
			}
		});
	}

	var initJqxNotification = function(){
		$("#jqxNtfSalesCommission").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};

	var createBonusParamEmplSales = function(rowindexes){
		$("#confirmSalesCommission").attr("disabled", "disabled");
		$('#jqxgrid').jqxGrid({disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		var salesCommissionIdArr = new Array();
		for(var i = 0; i < rowindexes.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			salesCommissionIdArr.push({salesCommissionId: data.salesCommissionId});
		}
		$.ajax({
			url: 'createBonusParamEmplSales',
			type: 'POST',
			data: {salesCommissionId: JSON.stringify(salesCommissionIdArr)},
			success: function(data){
				if(data.responseMessage == "success"){
					$('#jqxgrid').jqxGrid('updatebounddata');
					$('#jqxgrid').jqxGrid('clearselection')
					$("#jqxNtfSalesCommissionContent").text(data.successMessage);
					$("#jqxNtfSalesCommission").jqxNotification({template: 'info'});
					$("#jqxNtfSalesCommission").jqxNotification("open");
				}else{
					$("#jqxNtfSalesCommissionContent").text(data.errorMessage);
					$("#jqxNtfSalesCommission").jqxNotification({template: 'error'});
					$("#jqxNtfSalesCommission").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$("#confirmSalesCommission").removeAttr("disabled");
				$('#jqxgrid').jqxGrid('hideloadelement');
				$('#jqxgrid').jqxGrid({disabled: false});
			}
		});
	}

	var initJqxDateTime = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.prevMonthStart);
		var thruDate = new Date(globalVar.prevMonthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
		    refreshGridData(partyId, fromDate, thruDate);
		});
	}

	var initJqxTreeDropDownBtn = function (){
		var config = {dropDownBtnWidth: 300, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);

		$("#jqxTree").on('select', function(event){
			var id = event.args.element.id;
	    	var item = $("#jqxTree").jqxTree('getItem', args.element);
	    	setDropdownContent(item, $("#jqxTree"), $("#jqxDropDownButton"));
	    	var partyId = item.value;
	    	var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
	    	var fromDate = selection.from.getTime();
	    	var thruDate = selection.to.getTime();
	    	refreshGridData(partyId, fromDate, thruDate);
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};

	var refreshGridData = function(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getSalesCommissionData&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init
	}
	
}());

$(document).ready(function(){
	viewBonusSaleEmplObject.init();
});

