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

<div class="page-title"><h4 style="margin-left: 10px !important" class="blue">${uiLabelMap.WebtoolsExportFromDataSource}</h4></div>
<p class="alert alert-info">${uiLabelMap.WebtoolsXMLExportInfo}</p>
<#if results?has_content>
    <hr />
    <h2 style="margin-left: 10px;" class="blue">${uiLabelMap.WebtoolsResults}:</h2>
    <#list results as result>
        <p style="margin-left: 20px;">${result}</p>
    </#list>
</#if>
<hr />
<form method="post" action="<@ofbizUrl>entityExportAll</@ofbizUrl>">
    <p style="margin-left: 20px">${uiLabelMap.WebtoolsOutputDirectory}:</p> 
    <input style="margin-left: 20px" type="text" size="60" name="outpath" value="${outpath?if_exists}" /><br />
    <p style="margin-left: 20px">${uiLabelMap.CommonFromDate}:</p>
    <div style="margin-left: 20px"> 
    <@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><br/>
    </div>
    <p style="margin-left: 20px">${uiLabelMap.WebtoolsTimeoutSeconds}:</p> 
    <input style="margin-left: 20px" type="text" size="6" value="${txTimeout?default('7200')}" name="txTimeout"/><br />
    <button style="margin-left: 20px" class="btn btn-purple btn-small" type="submit">
    <i class="icon-share-alt"></i>
    ${uiLabelMap.WebtoolsExport}
    </button>
</form>
