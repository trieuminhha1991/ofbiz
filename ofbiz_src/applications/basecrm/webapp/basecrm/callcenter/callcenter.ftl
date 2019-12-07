<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script src="/crmresources/js/notify.js"></script>
<script src="/crmresources/js/callcenter/Processor.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<#assign organizationId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<script>
	const organizationId = "${organizationId?if_exists}";
	var globalFamilyId = "", globalPartyTypeId, CreateMode = false, notTrigger = false, partyIdParameter, mapGender;
	$(document).ready(function() {
		var globalFamilyId = "${(parameters.familyId)?if_exists}";
		mapGender = {"M": multiLang.male, "F": multiLang.female};
		if ("${(parameters.partyId)?if_exists}") {
			CookieLayer.setCurrentParty("${(parameters.partyId)?if_exists}", "${(parameters.roleTypeIdTo)?if_exists}");
		}
		if ("${(partyRelationship.partyIdFrom)?if_exists}") {
			CookieLayer.setCurrentParty("${(partyRelationship.partyIdFrom)?if_exists}", "${(partyRelationship.roleTypeIdFrom)?if_exists}");
		}
	});
</script>

<#include "labelSearchCustomer.ftl"/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "component://basecrm/webapp/basecrm/callcenter/popup/popupAddAddress.ftl"/>
<#include "component://basecrm/webapp/basecrm/callcenter/popup/ContactDuplicate.ftl"/>

<#assign customLoadFunction="true"/>
<#assign jqGridMinimumLibEnable="false"/>

${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#SearchEngine")}
${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#CustomerInformation")}
<div id="extraInfor" class="hide">
	${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#MemberFamily")}
	${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#Order")}
	<div class="hide">
	${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#Agreement")}
	</div>
	${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#Payment")}
	${screens.render("component://basecrm/widget/BaseCallcenterScreens.xml#Invoice")}
</div>
	
<#include "component://basecrm/webapp/basecrm/callcenter/consideration/customerConsideration.ftl"/>