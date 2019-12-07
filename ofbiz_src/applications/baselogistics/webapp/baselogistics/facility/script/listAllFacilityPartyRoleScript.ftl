<@jqGridMinimumLib/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.Edit	= "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel	= "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.SequenceId	= "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.FacilityId	= "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName	= "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.OpenedDate	= "${StringUtil.wrapString(uiLabelMap.OpenedDate)}";
	uiLabelMap.ClosedDate	= "${StringUtil.wrapString(uiLabelMap.ClosedDate)}";
	uiLabelMap.BLExpiryDate	= "${StringUtil.wrapString(uiLabelMap.BLExpiryDate)}";
	uiLabelMap.BLEffectiveDate	= "${StringUtil.wrapString(uiLabelMap.BLEffectiveDate)}";
	uiLabelMap.PartyId	= "${StringUtil.wrapString(uiLabelMap.PartyId)}";
	uiLabelMap.CommonPartyCode	= "${StringUtil.wrapString(uiLabelMap.CommonPartyCode)}";
	uiLabelMap.PartyOrg	= "${StringUtil.wrapString(uiLabelMap.PartyOrg)}";
	uiLabelMap.BLRoles	= "${StringUtil.wrapString(uiLabelMap.BLRoles)}";
	uiLabelMap.AddNew	= "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.FacilityRoleManagerment	= "${StringUtil.wrapString(uiLabelMap.FacilityRoleManagerment)}";
	uiLabelMap.AddRole	= "${StringUtil.wrapString(uiLabelMap.AddRole)}";
	uiLabelMap.CommonPartyName	= "${StringUtil.wrapString(uiLabelMap.CommonPartyName)}";
	uiLabelMap.EmplPositionTypeId	= "${StringUtil.wrapString(uiLabelMap.EmplPositionTypeId)}";
	uiLabelMap.CommonPartyId	= "${StringUtil.wrapString(uiLabelMap.CommonPartyId)}";
	uiLabelMap.Role	= "${StringUtil.wrapString(uiLabelMap.Role)}";
	uiLabelMap.CannotBeforeNow	= "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.BLEffectiveDate	= "${StringUtil.wrapString(uiLabelMap.BLEffectiveDate)}";
	uiLabelMap.BLExpiryDate	= "${StringUtil.wrapString(uiLabelMap.BLExpiryDate)}";
	uiLabelMap.MustAfterEffectiveDate	= "${StringUtil.wrapString(uiLabelMap.MustAfterEffectiveDate)}";
	uiLabelMap.AreYouSureUpdate	= "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
			
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	var adminPermission = false;
	<#if hasOlbPermission("MODULE", "LOG_FACILITY", "CREATE")>
		adminPermission = true;
	</#if>
	
</script>
<script type="text/javascript" src="/logresources/js/facility/listAllFacilityPartyRole.js?v=1.0.5"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>