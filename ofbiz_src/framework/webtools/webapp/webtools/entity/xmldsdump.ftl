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
<#if tobrowser?exists && tobrowser>
<h4>${uiLabelMap.WebtoolsExportFromDataSource}</h4>
<br />
<p>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</p>
<hr />
<#if security.hasPermission("ENTITY_MAINT", session)>
    <a href="<@ofbizUrl>xmldsrawdump</@ofbizUrl>" class="btn btn-mini btn-info" target="_blank">Click Here to Get Data (or save to file)</a>
<#else>
    <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
</#if>
<#else>
<#macro displayButtonBar>
	<div class="button-bar" style="margin-left: 20px; margin-top: 10px;">
    <button class="btn btn-purple btn-small" type="submit">
    <i class="icon-share-alt"> </i>
    ${uiLabelMap.WebtoolsExport}
    </button>
    <a href="<@ofbizUrl>xmldsdump?checkAll=true</@ofbizUrl>" class="btn btn-small btn-info icon-check open-sans">${uiLabelMap.WebtoolsCheckAll}</a>
    <a href="<@ofbizUrl>xmldsdump</@ofbizUrl>" class="btn btn-small btn-info icon-remove open-sans">${uiLabelMap.WebtoolsUnCheckAll}</a>
  </div>
</#macro>

<div><h2 class="blue" style="margin-left: 10px">${uiLabelMap.PageTitleEntityExport}</h2></div>
<p class="alert alert-info">${uiLabelMap.WebtoolsXMLExportInfo}</p>
<hr />

<#if security.hasPermission("ENTITY_MAINT", session)>
  <h2 class="header smaller lighter blue" style="margin-left: 10px">${uiLabelMap.WebtoolsResults}</h2>
  <#if parameters.filename?has_content && (numberOfEntities?number > 0)>
    <p>${uiLabelMap.WebtoolsWroteXMLForAllDataIn}</p>
    <p>${uiLabelMap.WebtoolsWroteNRecordsToXMLFile}</p>
  <#elseif parameters.outpath?has_content && (numberOfEntities?number > 0)>
    <#list results as result>
      <p style="margin-left: 20px">${result}</p>
    </#list>
  <#else>
    <p class="alert alert-info">${uiLabelMap.WebtoolsNoFilenameSpecified}</p>
  </#if>

  <hr />

  <h2 class="header smaller lighter blue" style="margin-left: 10px">${uiLabelMap.WebtoolsExport}</h2>
  <form method="post" action="<@ofbizUrl>xmldsdump</@ofbizUrl>" name="entityExport">
    <table class="basic-table" style="margin-left: 20px; margin-top: 15px;">
      <tr>
        <td class="label">${uiLabelMap.WebtoolsOutputDirectory}</td>
        <td><input type="text" size="60" name="outpath" value="${parameters.outpath?if_exists}"/></td>
      </tr>
      <tr>
        <td class="label">${uiLabelMap.WebtoolsMaxRecordsPerFile}</td>
        <td><input type="text" size="10" name="maxrecords"/></td>
      </tr>
      <tr>
        <td class="label">${uiLabelMap.WebtoolsSingleFilename}</td>
        <td><input type="text" size="60" name="filename" value="${parameters.filename?if_exists}"/></td>
      </tr>
      <tr>
        <td class="label">${uiLabelMap.WebtoolsRecordsUpdatedSince}</td>
        <td>
        <@htmlTemplate.renderDateTimeField name="entityFrom" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="entityFrom1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
        </td>
      </tr>
      <tr>
        <td class="label">${uiLabelMap.WebtoolsRecordsUpdatedBefore} </td>
        <td>
            <@htmlTemplate.renderDateTimeField name="entityThru" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="entityThru1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
        </td>
      </tr>
      <tr>
        <td class="label">${StringUtil.wrapString(uiLabelMap.WebtoolsOutToBrowser)}</td>
        <td>
        <label>
        <input type="checkbox" name="tobrowser"<#if tobrowser?has_content> checked="checked"</#if> /><span class="lbl"></span>
        </label>
        </td>
      </tr>
    </table>
    <br />
    <p style="margin-left: 20px">${uiLabelMap.WebtoolsEntityNames}:</p>
    <@displayButtonBar/>
      <div style="margin-left: 20px">${uiLabelMap.WebtoolsEntitySyncDump}:
        <input name="entitySyncId" size="30" style="margin-left: 3px; width: 216px;height: 24px;" value="${entitySyncId?if_exists}"/>
      </div>
      <div style="margin-left: 20px; margin-top:10px;">
      ${uiLabelMap.WebtoolsPreConfiguredSet}:
      <select name="preConfiguredSetName" style="">
        <option value="">${uiLabelMap.CommonNone}</option>
        <option value="CatalogExport">${uiLabelMap.WebtoolsPreConfiguredSet1}</option>
        <option value="Product1">${uiLabelMap.WebtoolsPreConfiguredSet2}</option>
        <option value="Product2">${uiLabelMap.WebtoolsPreConfiguredSet3}</option>
        <option value="Product3">${uiLabelMap.WebtoolsPreConfiguredSet4}</option>
        <option value="Product4">${uiLabelMap.WebtoolsPreConfiguredSet5}</option>
      </select>
      </div>
      <br />

      <table style="margin-left: 20px">
        <tr>
          <#assign entCount = 0>
          <#assign checkAll = parameters.checkAll?default("false")>
          <#list modelEntities as modelEntity>
            <#if entCount % 3 == 0 && entCount != 0>
              </tr><tr>
            </#if>
            <#assign entCount = entCount + 1>
            <#assign check = checkAll/>
            <#if checkAll == "true" && modelEntity.getClass().getName() == "org.ofbiz.entity.model.ModelViewEntity">
                <#assign check = "false"/>
            </#if>
            <#assign curEntityName = modelEntity.getEntityName()/>
            <td>
            <label>
            <input type="checkbox" name="entityName" value="${curEntityName}"<#if check="true"> checked="checked"</#if>/>
            <span class="lbl">
            ${curEntityName}
            </span>
            </label>
            </td>
          </#list>
        </tr>
      </table>
	
      <@displayButtonBar/>
    </form>
<#else>
    <div style="margin-left: 20px"><p class="alert alert-info">${uiLabelMap.WebtoolsPermissionMaint}<p></div>
</#if>
</#if>