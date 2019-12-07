$(document).ready(function(){
	
	$("input[name^='supplierPartyId']").val($("select[name='partyIdTo']").val());
	
	updateWithMultiValue({
		partyId: $("select[name='partyIdFrom']").val(),
		partyRelationshipTypeId: "REPRESENT_LEGAL",
		roleTypeIdFrom: "INTERNAL_ORGANIZATIO",
		roleTypeIdTo: "REPRESENT_LEGAL",
		}, 'getPartyRepresents' , 'listPartyRepresents', 'partyId', 'firstName', 'middleName', 'lastName', 'representPartyIdFrom');
	
	update({
		partyId: $("select[name='partyIdTo']").val(),
		contactMechTypeId: "EMAIL_ADDRESS",
		}, 'getPartyPrimaryEmails' , 'listPartyPrimaryEmails', 'contactMechId', 'infoString', 'emailAddressIdTo');
	
	update({
		partyId: $("select[name='partyIdFrom']").val(),
		contactMechPurposeTypeId: "PRIMARY_LOCATION",
		}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'addressIdFrom');
	
	update({
		partyId: $("select[name='partyIdFrom']").val(),
		finAccountTypeId: "BANK_ACCOUNT",
		}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'finAccountIdFroms');
	
	update({
		partyId: $("select[name='partyIdTo']").val(),
		finAccountTypeId: "BANK_ACCOUNT",
		}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'finAccountIdTos');
	
	update({
		partyId: $("select[name='partyIdFrom']").val(),
		contactMechPurposeTypeId: "PRIMARY_PHONE",
		}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'telephoneIdFrom');
	
	update({
		partyId: $("select[name='partyIdFrom']").val(),
		contactMechPurposeTypeId: "FAX_NUMBER",
		}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'faxNumberIdFrom');
	
	update({
		partyId: $("select[name='partyIdTo']").val(),
		contactMechPurposeTypeId: "PRIMARY_LOCATION",
		}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'addressIdTo');
	
	$("select[name='partyIdFrom']").change(function(){
		update({
			facilityId: $("select[name='partyIdFrom']").val(),
			contactMechPurposeTypeId: "PRIMARY_LOCATION",
			}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'addressIdFrom');
		
		update({
			partyId: $("select[name='partyIdFrom']").val(),
			finAccountTypeId: "BANK_ACCOUNT",
			}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'finAccountIdFroms');
		
		update({
			partyId: $("select[name='partyIdFrom']").val(),
			contactMechPurposeTypeId: "PRIMARY_PHONE",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'telephoneIdFrom');
		
		update({
			partyId: $("select[name='partyIdFrom']").val(),
			contactMechPurposeTypeId: "FAX_NUMBER",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'faxNumberIdFrom');
		
		updateWithMultiValue({
			partyId: $("select[name='partyIdFrom']").val(),
			partyRelationshipTypeId: "REPRESENT_LEGAL",
			roleTypeIdFrom: "INTERNAL_ORGANIZATIO",
			roleTypeIdTo: "REPRESENT_LEGAL",
			}, 'getPartyRepresents' , 'listPartyRepresents', 'partyId', 'firstName', 'middleName', 'lastName', 'representPartyIdFrom');
	});
	
	$("select[name='partyIdTo']").change(function(){
		update({
			partyId: $("select[name='partyIdTo']").val(),
			contactMechPurposeTypeId: "PRIMARY_LOCATION",
			}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'addressIdTo');
		update({
			partyId: $("select[name='partyIdTo']").val(),
			finAccountTypeId: "BANK_ACCOUNT",
			}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'finAccountIdTos');
		update({
			partyId: $("select[name='partyIdTo']").val(),
			contactMechTypeId: "EMAIL_ADDRESS",
			}, 'getPartyPrimaryEmails' , 'listPartyPrimaryEmails', 'contactMechId', 'infoString', 'emailAddressIdTo');
		
		$("input[name^='supplierPartyId']").val($("select[name='partyIdTo']").val());
	});
});
function update(jsonObject, url, data, key, value, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
            renderHtml(json, key, value, id);
//            console.log(2123);
        }
    });
}
function renderHtml(data, key, value, id){
	var y = "";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}
function updateWithMultiValue(jsonObject, url, data, key, value1, value2, value3, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
        	renderHtmlMultiValue(json, key, value1, value2, value3, id);
        }
    });
}
function renderHtmlMultiValue(data, key, value1, value2, value3, id){
	var y = "";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value1] +" "+ data[x][value2] +" "+ data[x][value3] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}