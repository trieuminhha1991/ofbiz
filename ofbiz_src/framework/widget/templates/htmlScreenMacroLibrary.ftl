<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#macro renderScreenBegin>
<!DOCTYPE html>
</#macro>

<#macro renderScreenEnd>
</#macro>

<#macro renderSectionBegin boundaryComment>
<#if boundaryComment?has_content>
<!-- ${boundaryComment} -->
</#if>
</#macro>

<#macro renderSectionEnd boundaryComment>
<#if boundaryComment?has_content>
<!-- ${boundaryComment} -->
</#if>
</#macro>

<#macro renderContainerBegin id style autoUpdateLink autoUpdateInterval>
<#if autoUpdateLink?has_content>
<script type="text/javascript">ajaxUpdateAreaPeriodic('${id}', '${autoUpdateLink}', '', '${autoUpdateInterval}');</script>
</#if>
<div<#if id?has_content> id="${id}"</#if><#if style?has_content> class="${style}"</#if>>
</#macro>
<#macro renderContainerEnd></div></#macro>
<#macro renderContentBegin editRequest enableEditValue editContainerStyle><#if editRequest?has_content && enableEditValue == "true"><div class=${editContainerStyle}></#if></#macro>
<#macro renderContentBody></#macro>
<#macro renderContentEnd urlString editMode editContainerStyle editRequest enableEditValue>
<#if editRequest?exists && enableEditValue == "true">
<#if urlString?exists><a href="${urlString}">${editMode}</a><#rt/></#if>
<#if editContainerStyle?exists></div><#rt/></#if>
</#if>
</#macro>
<#macro renderSubContentBegin editContainerStyle editRequest enableEditValue><#if editRequest?exists && enableEditValue == "true"><div class="${editContainerStyle}"></#if></#macro>
<#macro renderSubContentBody></#macro>
<#macro renderSubContentEnd urlString editMode editContainerStyle editRequest enableEditValue>
<#if editRequest?exists && enableEditValue == "true">
<#if urlString?exists><a href="${urlString}">${editMode}</a><#rt/></#if>
<#if editContainerStyle?exists></div><#rt/></#if>
</#if>
</#macro>

<#macro renderHorizontalSeparator id style><hr<#if id?has_content> id="${id}"</#if><#if style?has_content> class="${style}"</#if>/></#macro>

<#macro renderLabel text id style>
  <#if text?has_content>
    <#-- If a label widget has one of the h1-h6 styles, then it is considered block level element.
         Otherwise it is considered an inline element. -->
    <#assign idText = ""/>
    <#if id?has_content><#assign idText = " id=\"${id}\""/></#if>
    <#if style?has_content>
      <#if style=="h1">
        <h1${idText}>${text}</h1>
      <#elseif style=="h2">
        <h2${idText}>${text}</h2>
      <#elseif style=="h3">
        <h3${idText}>${text}</h3>
      <#elseif style=="h4">
        <h4${idText}>${text}</h4>
      <#elseif style=="h5">
        <h5${idText}>${text}</h5>
      <#elseif style=="h6">
        <h6${idText}>${text}</h6>
      <#else>
        <span${idText} class="${style}">${text}</span>
      </#if>
    <#else>
      <span${idText}>${text}</span>
    </#if>
  </#if>
</#macro>

<#macro renderLink parameterList targetWindow target uniqueItemName linkType actionUrl id style name height width linkUrl text imgStr>
<#if "ajax-window" != linkType>
<#if "hidden-form" == linkType>
<form method="post" action="${actionUrl}" <#if targetWindow?has_content>target="${targetWindow}"</#if> onsubmit="javascript:submitFormDisableSubmits(this)" name="${uniqueItemName}"><#rt/>
<#list parameterList as parameter>
<input name="${parameter.name}" value="${parameter.value}" type="hidden"/><#rt/>
</#list>
</form><#rt/>
</#if>
<a <#if id?has_content>id="${id}"</#if> <#if style?has_content>class="${style}"</#if> <#if name?has_content>name="${name}"</#if> <#if targetWindow?has_content>target="${targetWindow}"</#if> href="<#if "hidden-form"==linkType>javascript:document.${uniqueItemName}.submit()<#else>${linkUrl}</#if>"><#rt/>
<#if imgStr?has_content>${imgStr}</#if><#if text?has_content>${text}</#if></a>
<#else>
<div id="${uniqueItemName}"></div>

<a href="javascript:void(0);" id="${uniqueItemName}_link" <#if style?has_content>class="${style}"</#if>><#if text?has_content>${text}</#if></a>
<script type="text/javascript">
    function getRequestData () {
        var data =  {
            <#list parameterList as parameter>
                "${parameter.name}": "${parameter.value}",
            </#list>
            "presentation": "layer"
        };

        return data;
    }
    jQuery("#${uniqueItemName}_link").click( function () {
        jQuery("#${uniqueItemName}").dialog("open");
    });
    jQuery("#${uniqueItemName}").dialog({
         autoOpen: false,
         <#if text?has_content>title: "${text}",</#if>
         height: ${height},
         width: ${width},
         modal: true,
         open: function() {
                 jQuery.ajax({
                     url: "${target}",
                     type: "POST",
                     data: getRequestData(),
                     success: function(data) {jQuery("#${uniqueItemName}").html(data);}
                 });
         }
    });
</script>
</#if>
</#macro>
<#macro renderImage src id style wid hgt border alt urlString>
<#if src?has_content>
<img <#if id?has_content>id="${id}"</#if><#if style?has_content> class="${style}"</#if><#if wid?has_content> width="${wid}"</#if><#if hgt?has_content> height="${hgt}"</#if><#if border?has_content> border="${border}"</#if> alt="<#if alt?has_content>${alt}</#if>" src="${urlString}" />
</#if>
</#macro>

<#macro renderContentFrame fullUrl width height border><iframe src="${fullUrl}" width="${width}" height="${height}" <#if border?has_content>border="${border}"</#if> /></#macro>
<#macro renderScreenletBegin id title collapsible saveCollapsed collapsibleAreaId expandToolTip collapseToolTip fullUrlString padded menuString showMore collapsed findOptions findOptionsLabel javaScriptEnabled iconStyle="">
<div class="widget-box transparent no-bottom-border"<#if id?has_content> id="${id}"</#if>><#rt/>
<#if showMore>
<div class="widget-header"><#if title?has_content><h4>${title}</h4><div class="loading-image" style="width:20px;height:20px;float:left;padding-top:8px;"></div></#if>
<span class="widget-toolbar none-content">
<#if collapsible>
<a id="" href="#" data-action="collapse <#rt/>
collapsed"><i class="icon-chevron-down" <#if javaScriptEnabled>onclick="javascript:changeIconChev($(this));toggleScreenlet(this, '${collapsibleAreaId}', '${saveCollapsed?string}', '${expandToolTip}', '${collapseToolTip}');"<#else>href="${fullUrlString}"</#if><#if expandToolTip?has_content> title="${expandToolTip}"</#if>
></i></a>
</#if>
<script type="text/javascript">
	function changeIconChev(elm){
		if(elm.attr('class') == "icon-chevron-down"){
			elm.attr('class', 'icon-chevron-up');
		}else{
			elm.attr('class', 'icon-chevron-down');
		}
	}
</script>
<#--
<#if !collapsed>
${menuString}
</#if>
 -->
${menuString}
<#if findOptions>
<a href="javascript:void(0);" id="findOptions"><i class="icon-search open-sans">${findOptionsLabel}</i></a>
<script language="javascript">
    $(document).ready(function() {
    	$('#search-options').hide();
	});
	$(document).ready(function() {
    	$('#findOptions').click(function() {
    	    if ($('#search-options').is(':visible')){
    			$('#search-options').hide();
    		}else{
    			$('#search-options').show();
    		}
    	});
	});
</script>
</#if>
</span>
</div>
</#if>
<div class="widget-body">
<div <#if collapsibleAreaId?has_content> id="${collapsibleAreaId}" <#if collapsed> style="display: none;"</#if></#if><#if padded> class="widget-body-inner"<#else> class="widget-body-inner no-padding"</#if>>
</#macro>
<#macro renderScreenletSubWidget></#macro>
<#macro renderScreenletEnd></div></div></div></#macro>
<#macro renderScreenletPaginateMenu lowIndex actualPageSize ofLabel listSize paginateLastStyle lastLinkUrl paginateLastLabel paginateNextStyle nextLinkUrl paginateNextLabel paginatePreviousStyle paginatePreviousLabel previousLinkUrl paginateFirstStyle paginateFirstLabel firstLinkUrl>
    <li class="${paginateLastStyle}<#if !lastLinkUrl?has_content> disabled</#if>"><#if lastLinkUrl?has_content><a href="${lastLinkUrl}">${paginateLastLabel}</a><#else>${paginateLastLabel}</#if></li>
    <li class="${paginateNextStyle}<#if !nextLinkUrl?has_content> disabled</#if>"><#if nextLinkUrl?has_content><a href="${nextLinkUrl}">${paginateNextLabel}</a><#else>${paginateNextLabel}</#if></li>
    <#if (listSize?number > 0) ><li>${lowIndex?number + 1} - ${lowIndex?number + actualPageSize?number} ${ofLabel} ${listSize}</li><#rt/></#if>
    <li class="${paginatePreviousStyle?default("nav-previous")}<#if !previousLinkUrl?has_content> disabled</#if>"><#if previousLinkUrl?has_content><a href="${previousLinkUrl}">${paginatePreviousLabel}</a><#else>${paginatePreviousLabel}</#if></li>
    <li class="${paginateFirstStyle?default("nav-first")}<#if !firstLinkUrl?has_content> disabled</#if>"><#if firstLinkUrl?has_content><a href="${firstLinkUrl}">${paginateFirstLabel}</a><#else>${paginateFirstLabel}</#if></li>
</#macro>

<#macro renderPortalPageBegin originalPortalPageId portalPageId confMode="false" addColumnLabel="Add column" addColumnHint="Add a new column to this portal">
  <#if confMode == "true">
    <a class="btn btn-small btn-info" href="javascript:document.addColumn_${portalPageId}.submit()" title="${addColumnHint}">${addColumnLabel}</a> <h3 class="smaller blue lighter">PortalPageId: ${portalPageId}</h3>
    <form method="post" action="addPortalPageColumn" name="addColumn_${portalPageId}">
      <input name="portalPageId" value="${portalPageId}" type="hidden"/>
    </form>
  </#if>
   <!-- <tr> -->
</#macro>

<#macro renderPortalPageEnd>
    <!-- </tr> -->
</#macro>

<#macro renderPortalPageRowBegin>
    <tr>
</#macro>

<#macro renderPortalPageRowEnd>
    </tr>
</#macro>

<#macro renderPortalPageColumnBegin originalPortalPageId portalPageId columnSeqId="" rowSeqId="" colspan="1" confMode="false" width="auto" delColumnLabel="Delete column" delColumnHint="Delete this column" addPortletLabel="Add portlet" addPortletHint="Add a new portlet to this column" colWidthLabel="Col. width:" setColumnSizeHint="Set column size">
</#macro>

<#macro renderPortalPageColumnEnd>
</#macro>

<#macro renderPortalPagePortletBegin originalPortalPageId portalPageId portalPortletId portletSeqId prevPortletId="" prevPortletSeqId="" nextPortletId="" nextPortletSeqId="" columnSeqId="1" rowSeqId="1" colspan="4" rowspan="3" prevColumnSeqId="" nextColumnSeqId="" confMode="false" delPortletHint="Remove this portlet" editAttribute="false" editAttributeHint="Edit portlet parameters">
  <#assign portletKey = portalPageId+portalPortletId+portletSeqId>
  <#assign portletKeyFields = '<input name="portalPageId" value="' + portalPageId + '" type="hidden"/><input name="portalPortletId" value="' + portalPortletId + '" type="hidden"/><input name="portletSeqId" value="' + portletSeqId  + '" type="hidden"/>'>
  <#assign x = columnSeqId?number>
  <#assign y = rowSeqId?number>
  <div class="grid-stack-item"
    data-gs-x="${x - 1}" data-gs-y="${y - 1}"
    data-gs-width="${colspan}" data-gs-height="${rowspan}">
	<div class="grid-stack-item-content">
		${portletKeyFields}
</#macro>	

<#macro renderPortalPagePortletEnd confMode="false">
  </div></div>
</#macro>

<#macro renderColumnContainerBegin id style>
  <table cellspacing="0"<#if id?has_content> id="${id}"</#if><#if style?has_content> class="${style}"</#if>>
  <tr>
</#macro>

<#macro renderColumnContainerEnd>
  </tr>
  </table>
</#macro>

<#macro renderColumnBegin id style>
  <td<#if id?has_content> id="${id}"</#if><#if style?has_content> class="${style}"</#if>>
</#macro>

<#macro renderColumnEnd>
  </td>
</#macro>
