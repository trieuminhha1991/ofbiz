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

<#assign enableEdit = parameters.enableEdit?default("false")>
<script language="JavaScript" type="text/javascript">
var numTabs=${(entity.getRelationsSize()+1)};
function ShowTab(lname) {
  for(inc=1; inc <= numTabs; inc++) {
    // document.getElementById('area' + inc).className = (lname == 'tab' + inc) ? 'screenlet' : 'topcontainerhidden';
    // style of topcontainerhidden 
    // .topcontainerhidden { POSITION: absolute; VISIBILITY: hidden; }
    var elem = document.getElementById('area' + inc);
    if (lname == 'tab' + inc){
      elem.className = 'screenlet';
    }
    else {
      elem.className = 'topcontainerhidden';
      elem.style.position = 'absolute';
      elem.style.visibility = 'hidden';
    }
  }
}
</script>

<div class="widget-box transparent no-bottom-border">
  <div class="widget-header">
  <h4>${uiLabelMap.WebtoolsViewValue}</h4>
    <span class="widget-toolbar">
    	<li><a class=" margin-top5 open-sans icon-reply" style="text-decoration: blink; font-size:14px;"  href='<@ofbizUrl>FindGeneric?entityName=${entityName}&amp;find=true&amp;VIEW_SIZE=50&amp;VIEW_INDEX=0</@ofbizUrl>' class="btn btn-mini btn-info icon-reply">&nbsp;${uiLabelMap.WebtoolsBackToFindScreen}</a></li>
    	<#if enableEdit = "false">
        <#if hasCreatePermission>
    	<li><a class=" margin-top5 open-sans icon-plus-sign" style="text-decoration: blink; font-size:14px;"  href='<@ofbizUrl>ViewGeneric?entityName=${entityName}&amp;enableEdit=true</@ofbizUrl>' class="btn btn-mini btn-info create icon-plus-sign">&nbsp;${uiLabelMap.CommonCreateNew}</a></li>
    	<li><a class=" margin-top5 open-sans icon-edit" style="text-decoration: blink; font-size:14px;"  href="<@ofbizUrl>ViewGeneric?${curFindString}&amp;enableEdit=true</@ofbizUrl>" class="btn btn-mini btn-info icon-edit">&nbsp;${uiLabelMap.CommonEdit}</a></li>
    	</#if>
        <#if value?has_content>
        <#if hasDeletePermission>
        <li><a class=" margin-top5 open-sans icon-remove" style="text-decoration: blink; font-size:14px;"  href='<@ofbizUrl>UpdateGeneric?UPDATE_MODE=DELETE&amp;${curFindString}</@ofbizUrl>' class="btn btn-mini btn-info delete">${uiLabelMap.WebtoolsDeleteThisValue}</a></li>
    	</#if>
        </#if>
      	</#if>
    </span>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <h4 class="smaller lighter blue"  style="margin-left: 15px">${uiLabelMap.WebtoolsForEntity}: ${entityName}</h4>
    <h4 class="smaller lighter blue" style="margin-left: 15px">${uiLabelMap.WebtoolsWithPk}: ${findByPk}</h4>
    <br />
    <#if value?has_content>
      <form name="relationForm">
        <h4 style="margin-left:30px"><b>${uiLabelMap.CommonView}</b></p>
        <select name="viewRelated" onchange="javascript:ShowTab(this.options[this.selectedIndex].value)">
          <option value="tab1">${entityName}</option>
          <#list relationFieldList as relation>
            <option value="tab${(relation_index+2)}">${relation.title}${relation.relEntityName} (${relation.type})</option>
          </#list>
        </select>
      </form>
      <br />
    </#if>
    <div id="area1" class="widget-box transparent no-bottom-border">
      <div class="widget-header">
        <h4 style="margin-left: 15px">${uiLabelMap.WebtoolsEntityCurrentValue}</h4>
      </div>
	  <div class="widget-body">
      <#if value?has_content>
        <#assign alt_row = false>
        
        <table class="basic-table" cellspacing="0">
          <#list fields as field>
            <tr<#if alt_row> class="alternate-row"</#if>>
              <td class="label">${field.name}</td>
              <td>${field.value}</td>
            </tr>
            <#assign alt_row = !alt_row>
          </#list>
        </table>
        
      <#else>
      <p class="alert alert-info">
        ${uiLabelMap.WebtoolsSpecifiedEntity1} ${entityName} ${uiLabelMap.WebtoolsSpecifiedEntity2}.
      </p>
      </div>
      </#if>
    </div>
    <#if enableEdit = "true">
      <#if hasUpdatePermission || hasCreatePermission>
        <#assign alt_row = false>
        <div id="area2" class="widget-box transparent no-bottom-border">
          <div class="widget-header">
            <h4  style="margin-left: 15px">${uiLabelMap.WebtoolsEntityEditValue}</h4>
          </div>
          <#if pkNotFound>
            <p class="alert alert-info">${uiLabelMap.WebtoolsEntityName} ${entityName} ${uiLabelMap.WebtoolsWithPk} ${findByPk} ${uiLabelMap.WebtoolsSpecifiedEntity2}.</p>
          </#if>
          <div class="widget-body">
          <form action='<@ofbizUrl>UpdateGeneric?entityName=${entityName}</@ofbizUrl>' method="post" name="updateForm">
            <#assign showFields = true>
            <#assign alt_row = false>
            <table class="basic-table" cellspacing="0">
              <#if value?has_content>
                <#if hasUpdatePermission>
                  <#if newFieldPkList?has_content>
                    <input type="hidden" name="UPDATE_MODE" value="UPDATE"/>
                    <#list newFieldPkList as field>
                      <tr<#if alt_row> class="alternate-row"</#if>>
                        <td class="olbius-label">${field.name}</td>
                        <td>
                          <input type="hidden" name="${field.name}" value="${field.value}"/>
                          ${field.value}
                        </td>
                      </tr>
                      <#assign alt_row = !alt_row>
                    </#list>
                  </#if>
                <#else>
                <h4 style="margin-left: 30px">
                  ${uiLabelMap.WebtoolsEntityCretePermissionError} ${entityName} ${plainTableName}
                  </h4>
                  <#assign showFields = false>
                </#if>
              <#else>
                <#if hasCreatePermission>
                  <#if newFieldPkList?has_content>
                    <h4 style="margin-left: 30px">${uiLabelMap.WebtoolsYouMayCreateAnEntity}</p>
                    <input type="hidden" name="UPDATE_MODE" value="CREATE"/>
                    <#list newFieldPkList as field>
                      <tr<#if alt_row> class="alternate-row"</#if>>
                        <td class="olbius-label" style="margin-left:30px;">${field.name}</td>
                        <td>
                          <#if field.fieldType == 'DateTime'>
                            DateTime(YYYY-MM-DD HH:mm:SS.sss):<@htmlTemplate.renderDateTimeField name="${field.name}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${field.value}" size="25" maxlength="30" id="${field.name}" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                          <#elseif field.fieldType == 'Date'>
                            Date(YYYY-MM-DD):<@htmlTemplate.renderDateTimeField name="${field.name}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${field.value}" size="25" maxlength="30" id="${field.name}" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                          <#elseif field.fieldType == 'Time'>
                            Time(HH:mm:SS.sss):<input type="text" size="6" maxlength="10" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'Integer'>
                            <input type="text" size="20" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'Long'>
                            <input type="text" size="20" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'Double'>
                            <input type="text" size="20" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'Float'>
                            <input type="text" size="20" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'StringOneRow'>
                            <input type="text" size="${field.stringLength}" maxlength="${field.stringLength}" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'String'>
                            <input type="text" size="80" maxlength="${field.stringLength}" name="${field.name}" value="${field.value}" />
                          <#elseif field.fieldType == 'Textarea'>
                            <textarea cols="60" rows="3" maxlength="${field.stringLength}" name="${field.name}">${field.value}</textarea>
                          <#else>
                            <input type="text" size="20" name="${field.name}" value="${field.value}" />
                          </#if>
                        </td>
                      </tr>
                      <#assign alt_row = !alt_row>
                    </#list>
                  </#if>
                <#else>
                  <h4 style="margin-left: 30px">${uiLabelMap.WebtoolsEntityCretePermissionError} ${entityName} ${plainTableName}</h4>
                  <#assign showFields = false>
                </#if>
              </#if>
              <#if showFields>
                <#if newFieldNoPkList?has_content>
                  <#assign alt_row = false>
                  <#list newFieldNoPkList as field>
                    <tr<#if alt_row> class="alternate-row"</#if>>
                      <td style="margin-left:30px;" class="olbius-label">${field.name}</td>
                      <td>
                        <#if field.fieldType == 'DateTime'>
                          DateTime(YYYY-MM-DD HH:mm:SS.sss):<@htmlTemplate.renderDateTimeField name="${field.name}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${field.value}" size="25" maxlength="30" id="${field.name}" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <#elseif field.fieldType == 'Date'>
                          Date(YYYY-MM-DD):<@htmlTemplate.renderDateTimeField name="${field.name}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${field.value}" size="25" maxlength="30" id="${field.name}" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <#elseif field.fieldType == 'Time'>
                          Time(HH:mm:SS.sss):<input type="text" size="6" maxlength="10" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'Integer'>
                          <input type="text" size="20" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'Long'>
                          <input type="text" size="20" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'Double'>
                          <input type="text" size="20" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'Float'>
                          <input type="text" size="20" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'StringOneRow'>
                          <input type="text" size="${field.stringLength}" maxlength="${field.stringLength}" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'String'>
                          <input type="text" size="80" maxlength="${field.stringLength}" name="${field.name}" value="${field.value}" />
                        <#elseif field.fieldType == 'Textarea'>
                          <textarea cols="60" rows="3" maxlength="${field.stringLength}" name="${field.name}">${field.value}</textarea>
                        <#else>
                          <input type="text" size="20" name="${field.name}" value="${field.value}" />
                        </#if>
                      </td>
                    </tr>
                    <#assign alt_row = !alt_row>
                  </#list>
                  <#if value?has_content>
                  <h4 style="margin-left: 30px">
                    <#assign button = "${uiLabelMap.CommonUpdate}">
                    </h4>
                  <#else>
                  <h4 style="margin-left: 30px">
                    <#assign button = "${uiLabelMap.CommonCreate}">
                  </h4>
                  </#if>
                  <tr<#if alt_row> class="alternate-row"</#if>>
                    <td>&nbsp;</td>
                    <td>
                      <button class="btn btn-purple btn-small" type="submit" name="Update">
                      <i class="icon-ok open-sans"></i>
                       ${button}
                       </button>
                      <a href="<@ofbizUrl>ViewGeneric?${curFindString}</@ofbizUrl>" class="btn btn-small btn-warning icon-remove open-sans">&nbsp;${uiLabelMap.CommonCancel}</a>
                    </td>
                  </tr>
                </#if>
              </#if>
            </table>
            </div>
          </form>
        </div>
      </#if>
    </#if>
    <#if relationFieldList?has_content>
      <#list relationFieldList as relation>
          <div id="area${(relation_index + 2)}" class="topcontainerhidden">
            <div class="widget-header">
            <h4>${uiLabelMap.WebtoolsRelatedEntity}: ${relation.title}${relation.relatedTable} (${relation.type})</h4>
              <span class="widget-toolbar">
                
                <li><a href="<@ofbizUrl>FindGeneric?${relation.encodeRelatedEntityFindString}&amp;find=true</@ofbizUrl>">${uiLabelMap.CommonFind}</a></li>
                <#if relation.valueRelated?has_content>
                  <li><a href="<@ofbizUrl>ViewGeneric?${relation.encodeRelatedEntityFindString}</@ofbizUrl>">${uiLabelMap.CommonView}</a></li>
                </#if>
                <#if hasAllCreate || relCreate>
                  <li><a href="<@ofbizUrl>ViewGeneric?${relation.encodeRelatedEntityFindString}&amp;enableEdit=true</@ofbizUrl>">${uiLabelMap.CommonCreate}</a></li>
                </#if>
              </span>
              <br class="clear"/>
            </div>
            <div class="widget-body">
            <#if relation.valueRelated?has_content>
              <table class="basic-table" cellspacing="0">
                <#assign alt_row = false>
                <tr<#if alt_row> class="alternate-row"</#if>>
                  <td class="label">${uiLabelMap.WebtoolsPk}</td>
                  <td>${relation.valueRelatedPk}</td>
                </tr>
                <#list relation.relatedFieldsList as relatedField>
                  <tr<#if alt_row> class="alternate-row"</#if>>
                    <td class="label">${relatedField.name}</td>
                    <td>${relatedField.value}</td>
                  </tr>
                  <#assign alt_row = !alt_row>
                </#list>
              </table>
            <#else>
              <#if "one" = relation.type>
              <b>${uiLabelMap.WebtoolsNoValueFoundFor}</b> ${relation.title}${relation.relatedTable}.
              <#else/>
              <a href="<@ofbizUrl>FindGeneric?${relation.encodeRelatedEntityFindString}&amp;find=true</@ofbizUrl>" class="btn btn-mini btn-info">${uiLabelMap.CommonFind}</a>
              </#if>
            </#if>
            </div>
          </div>
      </#list>
    </#if>
  </div>
  <div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
      <h4  style="margin-left: 15px">${uiLabelMap.WebtoolsEntityXMLRepresentation}</h4>
    </div>
    <div class="widget-body">
      <p>
      <#if value?has_content>
        <#assign valueXmlDoc = Static["org.ofbiz.entity.GenericValue"].makeXmlDocument([value]) />
        ${Static["org.ofbiz.base.util.UtilXml"].writeXmlDocument(valueXmlDoc)?replace("\n", "<br />")?replace("    ", "&nbsp;&nbsp;&nbsp;&nbsp;")}
      </#if>
      </p>
    </div>
  </div>
  <br class="clear"/>
</div>
