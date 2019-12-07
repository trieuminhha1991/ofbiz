var viewListEmplAgreementObject = (function(){
	var agreementSelectedId;
	var init = function(){
		initJqxGridEvent();
	}
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			createJqxDropDownList(globalVar.agreementStatus, $("#statusDropDwonList"), "statusId", "description", 25, 200);
			$("#statusDropDwonList").on('select', function(event){
				var args = event.args;
				if(args){
					var item = args.item;
					var value = item.value;
					var source = $("#jqxgrid").jqxGrid('source');
					source._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQListEmplAgreement&statusId=" + value;
					$("#jqxgrid").jqxGrid('source', source);
				}
			});
			$("#statusDropDwonList").jqxDropDownList('selectItem', 'EMPL_AGR_EFFECTIVE');
		});
		$("#jqxgrid").on('rowdoubleclick', function(event){
			if(typeof(editEmplAgreementObject) != 'undefined' 
				&& typeof(editEmplAgreementObject.setAgreementData) != 'undefined'){
				var args = event.args;
				var boundIndex = args.rowindex;
				showDetailAgreement(boundIndex);
			}
		});
	};
	
	var showDetailAgreement = function showDetailAgreement(index){
		var data = $("#jqxgrid").jqxGrid('getrowdata', index);
		agreementSelectedId = data.agreementId;
		editEmplAgreementObject.setAgreementData(data);
	}
	
	var getSelectedAgreementId = function(){
		return agreementSelectedId;
	}; 
	return{
		init: init,
		getSelectedAgreementId: getSelectedAgreementId,
		showDetailAgreement: showDetailAgreement,
	}
}());

$(document).ready(function () {
	viewListEmplAgreementObject.init();
});