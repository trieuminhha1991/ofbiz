<#assign buyer = delegator.findOne("RoleType", {"roleTypeId" : "BUYER"}, true) !>
<#assign supplier = delegator.findOne("RoleType", {"roleTypeId" : "SUPPLIER"}, true) !>

<#if agreementId == '0'>
	<input type="hidden" id="EditPurchaseAgreement2_agreementId"/>
	<input type="hidden" id="EditPurchaseAgreement2_weekETD" name="weekETD"/>
	<#else>
		<input type="hidden" id="EditPurchaseAgreement2_agreementId" value="${agreementId}"/>
		<input type="hidden" id="EditPurchaseAgreement2_weekETD" name="weekETD" value="${currentETDTerm}"/>
</#if>


<input type="hidden" id="EditPurchaseAgreement2_agreementTypeId" value="PURCHASE_AGREEMENT" name="agreementTypeId" />
<input type="hidden" id="EditPurchaseAgreement2_productPlanId" name="productPlanId" value="${productPlanId}"/>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.AgreementName}<span style="color:red;"> *</span>:</div>
		<div class="span5" id="">
		<#if agreementId == '0'>
			<input type="text" name="agreementName" size="25" id="EditPurchaseAgreement2_agreementName" style="width: 208px;"/>
			<#else>
			<input type="text" name="agreementName" size="25" id="EditPurchaseAgreement2_agreementName" style="width: 208px;" value="${agreementNameEdit}"/>
		</#if>
		</div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="fromDate" id="EditPurchaseAgreement2_fromDate"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.AgreementDate}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="agreementDate" id="EditPurchaseAgreement2_agreementDate"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.AvailableThruDate}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="thruDate" id="EditPurchaseAgreement2_thruDate"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.SlideA}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div id="EditPurchaseAgreement2_partyIdFrom" name="partyIdFrom"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.SlideB}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div id="EditPurchaseAgreement2_partyIdTo" name="partyIdTo"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.Role}<span style="color:red;"> *</span>:</div>
		<div class="span5" style="margin-top: 5px;" id=""><input type="hidden" id="EditPurchaseAgreement2_roleTypeIdFrom" name="roleTypeIdFrom" value="${buyer.roleTypeId}" /><div name="roleTypeIdFromText" id="EditPurchaseAgreement2_roleTypeIdFromText"><a>${buyer.get('description', locale)}</a></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.Role}<span style="color:red;"> *</span>:</div>
		<div class="span5" style="margin-top: 5px;"><input type="hidden" name="roleTypeIdTo" id="EditPurchaseAgreement2_roleTypeIdTo" value="${supplier.roleTypeId}" /><div name="roleTypeIdToText" id="EditPurchaseAgreement2_roleTypeIdToText"><a>${supplier.get('description', locale)}</a></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.Address}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="addressIdFrom" id="EditPurchaseAgreement2_addressIdFrom"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.Address}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="addressIdTo" id="EditPurchaseAgreement2_addressIdTo"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.BankAccount}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="finAccountIdFroms" id="EditPurchaseAgreement2_finAccountIdFroms"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.BankAccount}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="finAccountIdTos" id="EditPurchaseAgreement2_finAccountIdTos"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.TelephoneNumber}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="telephoneIdFrom" id="EditPurchaseAgreement2_telephoneIdFrom"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.EmailAddress}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="emailAddressIdTo" id="EditPurchaseAgreement2_emailAddressIdTo"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.FaxNumber}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="faxNumberIdFrom" id="EditPurchaseAgreement2_faxNumberIdFrom"></div></div>
	</div>
	<#-- <div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.EmailAddress}<span style="color:red;"> *</span>:</div>
		<div class="span5"><input type='text' id="" /></div>
	</div> -->
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.Represented}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="representPartyIdFrom" id="EditPurchaseAgreement2_representPartyIdFrom"></div></div>
	</div>
	<#-- <div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.EmailAddress}<span style="color:red;"> *</span>:</div>
		<div class="span5"><input type='text' id="" /></div>
	</div> -->
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.PortOfDischarge}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="portOfDischargeId" id="EditPurchaseAgreement2_portOfDischargeId"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.PaymentCurrency}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="currencyUomIds" id="EditPurchaseAgreement2_currencyUomIds"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.FacilityAddress}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="contactMechId" id="EditPurchaseAgreement2_contactMechId"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.AllowedTransshipment}<span style="color:red;"> *</span>:</div>
		<div class="span5"><div name="transshipment" id="EditPurchaseAgreement2_transshipment"></div></div>
	</div>
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.PartialShipment}<span style="color:red;"> *</span>:</div>
		<div class="span5" id=""><div name="partialShipment" id="EditPurchaseAgreement2_partialShipment"></div></div>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.CommonDescription}<span style="color:red;"> *</span>:</div>
		<div class="span5"><input name="description" id="EditPurchaseAgreement2_description" style="width: 220px; height: 24px;"/></div>
	</div>
</div>

<hr />
<@jqGridMinimumLib />
<#assign organizations = organizations !>

<script type="text/javascript">
	var listPartyA = [
		<#list organizations as list>
			{
				partyId: '${list.partyId}',
				groupName: '${StringUtil.wrapString(list.groupName)}'
			},
		</#list>
	];
	var listPartyB = [
	          		<#list suppliers as list>
	          			{
	          				partyId: '${list.partyId}',
	          				partyName: '${StringUtil.wrapString(list.partyId)}'
	          			},
	          		</#list>
	          	];
	var listFacilityPort = [
		          		<#list listFacility as list>
		          			{
		          				facilityId: '${list.facilityId}',
		          				facilityName: '${StringUtil.wrapString(list.facilityName)}'
		          			},
		          		</#list>
		          	];
	var transshipment = ["Y", "N"];
	var partialShipment = ["Y", "N"];
	$('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput({ width: '220px', height: '30', value: "${agreementFromDate}"});
	$('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput({ width: '220px', height: '30',value: "${agreementDate}" });
	$('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput({ width: '220px', height: '30', value: "${agreementThruDate}" });
	$('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList({ source: listPartyA, width: '220', height: '30',
		displayMember: 'groupName',
		valueMember: 'partyId'
	});
	$('#EditPurchaseAgreement2_partyIdTo').jqxDropDownList({ source: listPartyB, width: '220', height: '30',
		displayMember: 'partyId',
		valueMember: 'partyId'
	});
//	$('#EditPurchaseAgreement2_roleTypeIdFrom').jqxDropDownList({ source: "", selectedIndex: 1, width: '200', height: '25'});
//	$('#EditPurchaseAgreement2_roleTypeIdTo').jqxDropDownList({ source: "", selectedIndex: 1, width: '200', height: '25'});
	$('#EditPurchaseAgreement2_addressIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_addressIdTo').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_finAccountIdFroms').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_finAccountIdTos').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_telephoneIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_emailAddressIdTo').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_faxNumberIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_representPartyIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'});
	$('#EditPurchaseAgreement2_portOfDischargeId').jqxDropDownList({ source: listFacilityPort, width: '220', height: '30',
		displayMember: 'facilityName',
		valueMember: 'facilityId'
	});
	$('#EditPurchaseAgreement2_currencyUomIds').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'})
	$('#EditPurchaseAgreement2_contactMechId').jqxDropDownList({ source: "", selectedIndex: 0, width: '220', height: '30'})
	$('#EditPurchaseAgreement2_transshipment').jqxDropDownList({ source: transshipment, width: '220', height: '30'})
	$('#EditPurchaseAgreement2_partialShipment').jqxDropDownList({ source: partialShipment, width: '220', height: '30'})
	
//	$('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList('selectIndex', 0);
//	$("#EditPurchaseAgreement2_partyIdFrom").jqxDropDownList('selectItem','${partyIdFrom}');
//	$("#EditPurchaseAgreement2_partyIdTo").jqxDropDownList('selectItem','${partyIdTo}');
	$('#EditPurchaseAgreement2_partyIdFrom').on('select', function (event){
		var args = event.args;
	    if (args) {
	    var index = args.index;
	    var item = args.item;
	    var label = item.label;
	    var value = item.value;
	    update({
			partyId: value,
			contactMechPurposeTypeId: "PRIMARY_LOCATION",
			}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'EditPurchaseAgreement2_addressIdFrom');
		
		update({
			partyId: value,
			finAccountTypeId: "BANK_ACCOUNT",
			}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'EditPurchaseAgreement2_finAccountIdFroms');
		
		update({
			partyId: value,
			contactMechPurposeTypeId: "PRIMARY_PHONE",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'EditPurchaseAgreement2_telephoneIdFrom');
		
		update({
			partyId: value,
			contactMechPurposeTypeId: "FAX_NUMBER",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'EditPurchaseAgreement2_faxNumberIdFrom');
		
		updateWithMultiValue({
			partyId: value,
			partyRelationshipTypeId: "REPRESENT_LEGAL",
			roleTypeIdFrom: "INTERNAL_ORGANIZATIO",
			roleTypeIdTo: "REPRESENT_LEGAL",
			}, 'getPartyRepresents' , 'listPartyRepresents', 'partyId', 'firstName', 'middleName', 'lastName', 'EditPurchaseAgreement2_representPartyIdFrom');
	    }
	});
	
	$('#EditPurchaseAgreement2_partyIdTo').on('select', function (event){
		var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var value = item.value;
		    update({
				partyId: value,
				}, 'getCurrencyUomIdBySupplier' , 'listgetCurrencyUomIdBySupplier', 'currencyUomId', 'currencyUomId', 'EditPurchaseAgreement2_currencyUomIds');
			update({
				partyId: value,
				contactMechPurposeTypeId: "PRIMARY_LOCATION",
				}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'EditPurchaseAgreement2_addressIdTo');
			update({
				partyId: value,
				finAccountTypeId: "BANK_ACCOUNT",
				}, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId', 'finAccountCode', 'EditPurchaseAgreement2_finAccountIdTos');
			update({
				partyId: value,
				contactMechTypeId: "EMAIL_ADDRESS",
				}, 'getPartyPrimaryEmails' , 'listPartyPrimaryEmails', 'contactMechId', 'infoString', 'EditPurchaseAgreement2_emailAddressIdTo');
	    }
	});
	
	$("#EditPurchaseAgreement2_portOfDischargeId").on('select', function(event){
		var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var value = item.value;
			update({
				facilityId: value,
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'EditPurchaseAgreement2_contactMechId');
	    }
	});
	
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	function renderHtml(json, key, value, id){
		$('#'+id).jqxDropDownList({ source: json, selectedIndex: 0,
			displayMember: value,
			valueMember: key
		});
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
		var dataSource = [];
		for (var x in data){
			var row = {};
			row[key] = data[x][key];
			row["displayValue"] = "" +data[x][value1]+ " "+data[x][value2]+" "+data[x][value3];
			dataSource.push(row);
		}
		$('#'+id).jqxDropDownList({ source: dataSource, selectedIndex: 0,
			displayMember: "displayValue",
			valueMember: key
		});
	}
</script>
