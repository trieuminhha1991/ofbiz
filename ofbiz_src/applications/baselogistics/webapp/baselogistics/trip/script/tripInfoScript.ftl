<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;

	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>

	var shipperPartyData = [];
	<#assign shippers = Static["com.olbius.basehr.util.PartyUtil"].getEmployeeHasRoleInDepartment(delegator, "LOGM", "LOG_DELIVERER","EMPLOYEE", nowTimestamp)!/>;
	<#if shippers?has_content>
		<#list shippers as shipper>
			var row = {};
			<#assign partyName = delegator.findOne("PartyNameView", {"partyId" : shipper?if_exists}, false)/>
			row['lastName'] = "${partyName.lastName?if_exists}";
			row['middleName'] = "${partyName.middleName?if_exists}";
			row['firstName'] = "${partyName.firstName?if_exists}";
			row['partyId'] = "${shipper?if_exists}";
			shipperPartyData.push(row);
		</#list>
	</#if>
	if (shipperPartyData.length > 0){
		for (var i = 0; i < shipperPartyData.length; i ++){
			var fullName = null;
			if (shipperPartyData[i].lastName){
				if (fullName){
					fullName = fullName + ' ' + shipperPartyData[i].lastName;
				} else {
					fullName = shipperPartyData[i].lastName;
				}
			}
			if (shipperPartyData[i].middleName){
				if (fullName){
					fullName = fullName + ' ' + shipperPartyData[i].middleName;
				} else {
					fullName = shipperPartyData[i].middleName;
				}
			}
			if (shipperPartyData[i].firstName){
				if (fullName){
					fullName = fullName + ' ' + shipperPartyData[i].firstName;
				} else {
					fullName = shipperPartyData[i].firstName;
				}
			}
			shipperPartyData[i]["description"] = fullName;
		}
	}

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.CostRequired = "${uiLabelMap.CostRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CannotAfterNow = "${uiLabelMap.CannotAfterNow}";
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.EndDateMustBeAfterStartDate = '${StringUtil.wrapString(uiLabelMap.EndDateMustBeAfterStartDate)}';
	uiLabelMap.StartDateMustBeAfterNow = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterNow)}';
	uiLabelMap.StartDateMustBeAfterEndDate = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterEndDate)}';
	uiLabelMap.CommonCancel = "${uiLabelMap.CommonCancel}";
	uiLabelMap.OK = "${uiLabelMap.OK}";
</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/trip/newTripInfo.js"></script>
