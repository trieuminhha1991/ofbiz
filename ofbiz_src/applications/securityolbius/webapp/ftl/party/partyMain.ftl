<@jqGridMinimumLib/>
<#include "../marco/olbius.ftl"/>
<@olbiusFunc/>

<#assign config = []/>

<#if party?exists>
    <#assign config = config + [mapTab("Party permission", "component://securityolbius/widget/PartyScreens.xml#partyPerm")]/>
</#if>

<#assign config = config + [mapTab("List party", "component://securityolbius/widget/PartyScreens.xml#partyList")]/>

<@olbiusTab config=config/>