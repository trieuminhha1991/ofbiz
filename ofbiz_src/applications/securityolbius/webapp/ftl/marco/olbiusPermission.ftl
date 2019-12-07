<#function hasOlbEntityPermission entity permssion>
    <#return Static["com.olbius.security.util.SecurityUtil"].getOlbiusSecurity(security).olbiusEntityPermission(session, permssion, entity)/>
</#function>

<#function hasOlbPermission type app permssion>
    <#return Static["com.olbius.security.util.SecurityUtil"].getOlbiusSecurity(security).olbiusHasPermission(session, permssion, type, app)/>
</#function>

<#global hasOlbEntityPermission=hasOlbEntityPermission>
<#global hasOlbPermission=hasOlbPermission>