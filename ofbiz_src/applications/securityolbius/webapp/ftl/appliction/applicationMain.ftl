<@jqGridMinimumLib/>
<#include "../marco/olbius.ftl"/>
<@olbiusFunc/>

<#assign config = []/>

<#if olbiusApp?exists>
    <#assign config = config + [mapTab("Detail", "component://securityolbius/widget/ApplicationScreens.xml#appDetail")]/>
    <#assign config = config + [mapTab("Member", "component://securityolbius/widget/ApplicationScreens.xml#appMember")]/>
    <#assign config = config + [mapTab("Override Permission", "component://securityolbius/widget/ApplicationScreens.xml#overridePermission")]/>
</#if>

<#assign config = config + [mapTab("List App", "component://securityolbius/widget/ApplicationScreens.xml#appList")]/>

<@olbiusTab config=config/>