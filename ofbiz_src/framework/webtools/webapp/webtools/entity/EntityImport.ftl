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

<div style="margin-left:10px" class="page-title"><h4  class="blue">${uiLabelMap.WebtoolsImportToDataSource}</span></div>
<p class="alert alert-info">${uiLabelMap.WebtoolsXMLImportInfo}</p>
<hr />
<div class="row-fluid">
<div class="span12">
<div class="span6">
  <form method="post" action="<@ofbizUrl>entityImport</@ofbizUrl>">
    <p style="margin-left:10px">${uiLabelMap.WebtoolsAbsoluteFileNameOrUrl}:</p>
    <input style="margin-left:10px" type="text" size="60" name="filename" value="${filename?if_exists}"/><br />
    <p style="margin-left:10px">${uiLabelMap.WebtoolsAbsoluteFTLFilename}:</p>
    <input style="margin-left:10px" type="text" size="40" name="fmfilename" value="${fmfilename?if_exists}"/><br />
    <label style="margin-left:10px" ><input type="checkbox" name="isUrl" <#if isUrl?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsIsURL}
    </span>
    </label>
    <label style="margin-left:10px" >
    <input type="checkbox" name="mostlyInserts" <#if mostlyInserts?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMostlyInserts}
    </span>
    </label>
    <label style="margin-left:10px">
    <input  type="checkbox" name="maintainTimeStamps" <#if keepStamps?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMaintainTimestamps}
    </span>
    </label>
    <label  style="margin-left:10px" >
    <input type="checkbox" name="createDummyFks" <#if createDummyFks?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCreateDummyFks}
    </span>
    </label>
    <label style="margin-left:10px">
    <input  type="checkbox" name="checkDataOnly" <#if checkDataOnly?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCheckDataOnly}
    </span>
    </label>
    </div>
  	<div class="span6">
    <p style="margin-left:10px">${uiLabelMap.WebtoolsTimeoutSeconds}:</p>
    <input style="margin-left:10px" type="text" size="6" value="${txTimeoutStr?default("7200")}" name="txTimeout"/>
    <button class="btn btn-purple btn-small" style="" type="submit">
    <i class="icon-download"></i>
    ${uiLabelMap.WebtoolsImportFile}
    </button><br />
    <div class="button-bar">
    
    </div>
  </form>
 
  <form method="post" action="<@ofbizUrl>entityImport</@ofbizUrl>">
    <p style="margin-left:10px"> ${uiLabelMap.WebtoolsCompleteXMLDocument}:</p>	
    <textarea  style="margin-left:10px; width:90%; heigth:70%;" name="fulltext">${fulltext?default("<entity-engine-xml>\n</entity-engine-xml>")}</textarea>
    <div class="button-bar"><button style="margin-left:10px" class="btn btn-small btn-purple" type="submit" >
    <i class="icon-download"></i>
    ${uiLabelMap.WebtoolsImportText}
    </button>
    </div>
  </form>
  </div>
  </div>
  </div>
  <#if messages?exists>
      <hr />
      <div style="margin-left:10px">
      <h4 class="blue">${uiLabelMap.WebtoolsResults}:</h4>
      </div>
      <#list messages as message>
          <p style="margin-left:10px">${message}</p>
      </#list>
  </#if>
