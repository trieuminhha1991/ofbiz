<#--INCLUDE COMPONENT-->
<#include "../common/accCommon.ftl"/>
<#include "script/glAccountScript.ftl"/>
<#include "glAccountGrid.ftl" />
<#if parameters.organizationPartyId?exists && parameters.organizationPartyId?has_content>
<#include "popupAddGlAccountOrg.ftl" />
<#else>
<#include "popupAddGlAccount.ftl" />
</#if>
<#--/INCLUDE COMPONENT-->
