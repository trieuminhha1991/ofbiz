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
<div class="row-fluid">
<div class="span12">
<div class="span6">
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCheckUpdateDatabase}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="checkupdatetables"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text" class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <label>
        &nbsp;<input type="checkbox" name="checkPks" value="true" checked="checked"/>&nbsp;<span class="lbl">${uiLabelMap.WebtoolsPks}</span>
        </label>
        <label>
        &nbsp;<input type="checkbox" name="checkFks" value="true"/>&nbsp;<span class="lbl">${uiLabelMap.WebtoolsFks}</span>
        </label>
        <label>
        &nbsp;<input type="checkbox" name="checkFkIdx" value="true"/>&nbsp;<span class="lbl">${uiLabelMap.WebtoolsFkIdx}</span>
        </label>
        <label>
        &nbsp;<input type="checkbox" name="addMissing" value="true"/>&nbsp;<span class="lbl">${uiLabelMap.WebtoolsAddMissing}</span>
        </label>
        <label>
        &nbsp;<input type="checkbox" name="repair" value="true"/>&nbsp;<span class="lbl">${uiLabelMap.WebtoolsRepairColumnSizes}</span>
        </label>
        <button type="submit" class="btn btn-purple btn-small">
        <i class="icon-ok"></i>
        ${uiLabelMap.WebtoolsCheckUpdateDatabase}
        </button>
    </form>
    
    <p style="margin-left: 20px; font-style:italic">${uiLabelMap.WebtoolsNoteUseAtYourOwnRisk}</p>
    <script language="JavaScript" type="text/javascript">
         function enableTablesRemove() {
             document.forms["TablesRemoveForm"].elements["TablesRemoveButton"].disabled=false;
         }
    </script>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsRemoveAllTables}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}" name="TablesRemoveForm">
        <input type="hidden" name="option" value="removetables"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" name="TablesRemoveButton" class="btn btn-danger btn-small margin-top10" disabled="disabled">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button>
        <button type="submit" class="btn btn-info btn-small margin-top10" onclick="enableTablesRemove();">
        <i class="icon-check"></i>
        ${uiLabelMap.WebtoolsEnable}
        </button>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}" name="TableRemoveForm">
        <div><input type="hidden" name="option" value="removetable"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text" class="margin-top10" name="groupName" value="${groupName}" size="20"/></div><div>
        ${uiLabelMap.WebtoolsEntityName}: <input type="text"  class="margin-top10" name="entityName" value="${entityName}" size="20"/>
        <button type="submit" class="btn btn-danger btn-small margin-top10" name="TablesRemoveButton">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button></div>
    </form>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCreateRemoveAllPrimaryKeys}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="createpks"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonCreate}
        </button>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="removepks"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"   class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-danger btn-small margin-top10">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button>
    </form>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsUpdateCharacterSetAndCollate}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="updateCharsetCollate"/>
        ${uiLabelMap.WebtoolsGroupName}: <input class="margin-top10" type="text" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonUpdate}
        </button>
    </form>
    </div>
    <div class="span6">
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCreateRemovePrimaryKey}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <div><input type="hidden" name="option" value="createpk"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="20"/></div><div>
        ${uiLabelMap.WebtoolsEntityName}: <input type="text"  class="margin-top10" name="entityName" value="${entityName}" size="20"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonCreate}
        </button></div>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <div><input type="hidden" name="option" value="removepk"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="20"/></div><div>
        ${uiLabelMap.WebtoolsEntityName}: <input type="text"  class="margin-top10" name="entityName" value="${entityName}" size="20"/>
        <button type="submit" class="btn btn-danger btn-small margin-top10">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button></div>
    </form>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCreateRemoveAllDeclaredIndices}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="createidx"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonCreate}
        </button>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="removeidx"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
       <button type="submit" class="btn btn-danger btn-small margin-top10">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button>
    </form>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCreateRemoveAllForeignKeyIndices}</h3>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="createfkidxs"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonCreate}
        </button>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="removefkidxs"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text"  class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-danger btn-small margin-top10">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button>
    </form>
    <h3 class="header smaller lighter blue" style="margin-left: 10px !important">${uiLabelMap.WebtoolsCreateRemoveAllForeignKeys}</h3>
    <p style="margin-left: 20px; font-style:italic">${uiLabelMap.WebtoolsNoteForeighKeysMayAlsoBeCreated}</p>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="createfks"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text" class="margin-top10" name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-small btn-purple margin-top10">
		<i class="icon-ok"></i>        
        ${uiLabelMap.CommonCreate}
        </button>
    </form>
    <form style="padding-left: 20px;" method="post" action="${encodeURLCheckDb}">
        <input type="hidden" name="option" value="removefks"/>
        ${uiLabelMap.WebtoolsGroupName}: <input type="text" class="margin-top10"  name="groupName" value="${groupName}" size="40"/>
        <button type="submit" class="btn btn-danger btn-small margin-top10">
        <i class="icon-trash"></i>
        ${uiLabelMap.CommonRemove}
        </button>
    </form>
    </div>
    </div>
    </div>
<#if miters?has_content>
    <hr />
    <ul>
        <#list miters as miter>
            <li>${miter}</li>
        </#list>
    </ul>
</#if>
