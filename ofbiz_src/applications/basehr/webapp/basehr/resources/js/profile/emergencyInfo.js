$(document).ready(function(){
	create_spinner($("#spinner-ajax"));
	emergencyInfoObject.init();
});

var emergencyInfoObject = (function(){
	var init = function(){
		initJqxWindow();
		initBtnEvent();
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxValidator();
	};
	
	var initJqxNumberInput = function(){
		$("#emergencyPhoneNbr").jqxNumberInput({ width: '97%', height: '25px', inputMode: 'simple', digits: 11, decimalDigits: 0, disabled: true});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(partyRelationshipType, $("#emergencyPartyRel"), "partyRelationshipTypeId", "partyRelationshipName", 25, '98%');
		$("#emergencyPartyRel").jqxDropDownList({disabled:true});
		createJqxDropDownList([], $("#emergencyFamilyList"), "personFamilyBackgroundId", "partyName", 25, '98%');
		$("#emergencyFamilyList").on('select', function (event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var dataRecord = personFamilyBackgroundArr[index];
				$("#emergencyPartyRel").jqxDropDownList('selectItem', dataRecord.partyRelationshipTypeId); 
				$("#emergencyPhoneNbr").val(dataRecord.telephone);
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#editPartyEmergencyWindow"), 400, 230);
		$("#editPartyEmergencyWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#editPartyEmergencyWindow").on('open', function(event){
			$("#emergencyFamilyList").jqxDropDownList({disabled: true});
			$("#ajaxLoading").show();
			$.ajax({
				url: 'getPersonFamilyBackground',
				data: {},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						var updateArr = response.listReturn;
						personFamilyBackgroundArr = updateArr;
						updateSourceDropdownlist($("#emergencyFamilyList"), updateArr);
						if(updateArr.length == 0){
							bootbox.dialog(uiLabelMap.PersonFamilyBackgroundIsNotDeclare,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);		
						}
					}
				},
				complete: function( jqXHR, textStatus){
					$("#emergencyFamilyList").jqxDropDownList({disabled: false});
					$("#ajaxLoading").hide();
				}
			});
		});
	};
	
	var initBtnEvent = function(){
		$("#editFamilyEmergency").click(function(event){
			openJqxWindow($("#editPartyEmergencyWindow"));
		});
		$("#cancelEmergency").click(function(event){
			$("#editPartyEmergencyWindow").jqxWindow('close');
		});
		$("#saveEmergency").click(function(event){
			if(!validate()){
				return;
			}
			$("#saveEmergency").attr("disabled", "disabled");
			$("#cancelEmergency").attr("disabled", "disabled");
			var item = $("#emergencyFamilyList").jqxDropDownList('getSelectedItem');
			var itemPartyRel = $("#emergencyPartyRel").jqxDropDownList('getSelectedItem');
			$("#ajaxLoading").show();
			$.ajax({
				url: 'updatePartyEmergency',
				data: {personFamilyBackgroundId: item.value},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						if(globalVar.emergencyNotSetting){
							$("#emergencyNotSetting").hide();
							$("#emergencyInfo").show()
						}
						$("#emergencyFullNameDesc").html(item.label);
						$("#emergencyPartyRelDesc").html(itemPartyRel.label);
						$("#emergencyTelephoneDesc").html($("#emergencyPhoneNbr").val());
					}else{
						
					}
				},
				complete: function(jqXHR, textStatus){
					$("#saveEmergency").removeAttr("disabled");
					$("#cancelEmergency").removeAttr("disabled");
					$("#ajaxLoading").hide();
					$("#editPartyEmergencyWindow").jqxWindow('close');
				}
			});
		});
	};
	
	var initJqxValidator = function(){
		$('#editPartyEmergencyWindow').jqxValidator({
			rules : [
					{
						 input : '#emergencyFamilyList',message : uiLabelMap.messageRequire, 
						 action : 'blur',
						 rule : function (input, commit){
							 if(!input.val()){
								 return false;
							 }
							 return true;
						 }
					},
					{
						input : '#emergencyPartyRel',message : uiLabelMap.messageRequire, 
						action : 'blur',
						rule : function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					}
			]
		});
	};
	
	var validate = function(){
		return $('#editPartyEmergencyWindow').jqxValidator('validate');
	};
	return {
		init: init,
		validate: validate
	};
}());
