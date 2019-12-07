<#--===================================Prepare Data=====================================================-->
<script>
	<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, parameters.organizationPartyId?if_exists, true, true)?if_exists>
	<#assign partyAddressTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(parameters.organizationPartyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
	<#assign partyTelePhoneTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.organizationPartyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
	<#assign faxNumber = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.organizationPartyId?if_exists, "FAX_NUMBER", delegator)?if_exists>
	<#assign orgTo = delegator.findOne("Party", {"partyId" : parameters.organizationPartyId?if_exists}, true)>
	<#assign repTo = Static["com.olbius.basehr.util.PartyUtil"].getManagerbyOrg(orgTo, delegator)?if_exists/>
	<#assign repNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, repTo?if_exists, true, true)?if_exists>
	<#assign emplPositionListTo = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, repTo?if_exists, nowTimestamp)?if_exists/>
	<#if emplPositionListTo?exists && emplPositionListTo?has_content>
		<#assign emplPosTo = emplPositionListTo.get(0) />
	</#if>
	
	<#assign partyNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, parameters.partyId?if_exists, true, true)?if_exists>
	<#assign partyAddressFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(parameters.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
	<#assign partyTelePhoneFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
	<#assign faxNumberFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.partyId?if_exists, "FAX_NUMBER", delegator)?if_exists>
	<#assign orgFrom = delegator.findOne("Party", {"partyId" : parameters.partyId?if_exists}, true)>
	<#assign repFrom = Static["com.olbius.basehr.util.PartyUtil"].getManagerbyOrg(orgFrom, delegator)?if_exists/>
	<#assign repNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, repFrom?if_exists, true, true)?if_exists>
	<#assign emplPositionListFrom = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, repFrom?if_exists, nowTimestamp)?if_exists/>
	<#if emplPositionListFrom?exists && emplPositionListFrom?has_content>
		<#assign emplPosFrom = emplPositionListFrom.get(0) />
	</#if>
	<#assign openingBal = Static["com.olbius.acc.report.summary.LiabilityPref"].getOpeningBalLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
	<#assign openingBalStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(openingBal?if_exists?double, "VND", locale, 2) />
	<#assign paidAmount = Static["com.olbius.acc.report.summary.LiabilityPref"].getPaidLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
	<#assign paidAmountStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(paidAmount?if_exists?double, "VND", locale, 2) />
	<#assign notPaidAmount = Static["com.olbius.acc.report.summary.LiabilityPref"].getNotPaidLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
	<#assign notPaidAmountStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notPaidAmount?if_exists?double, "VND", locale, 2) />
	
	<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
	var uomData = [
       <#if listUoms?exists>
       	<#list listUoms as uom>
       		{
       			uomId : "${uom.uomId}",
       			description : "${StringUtil.wrapString(uom.get('description'))}",
   			},
   		</#list>
   	  </#if>
 	];
</script>
<#--===================================/Prepare Data=====================================================-->