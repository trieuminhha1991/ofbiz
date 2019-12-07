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
    <form method="post" action="EntitySQLProcessor" name="EntitySQLCommand">
      <table class="basic-table width98pc" cellspacing="0">
        <tr>
            <td style="margin-top: 15px" class="olbius-label arrowed">
                ${uiLabelMap.CommonGroup}
            </td>
            <td>
                <select style="margin-top: 10px" name="group">
                    <#list groups as group>
                        <option value="${group}" <#if selGroup?exists><#if group = selGroup>selected="selected"</#if></#if>>${group}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="olbius-label arrowed">
                ${uiLabelMap.WebtoolsSqlCommand}
            </td>
            <td>
                <textarea name="sqlCommand" cols="100" rows="5" class="width100pc">${sqlCommand?if_exists}</textarea>
            </td>
        </tr>
        <tr>
            <td class="olbius-label arrowed">
                ${uiLabelMap.WebtoolsLimitRowsTo}
            </td>
            <td>
                <input name="rowLimit" type="text" size="5" value="${rowLimit?default(200)}"/>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <button type="submit" name="submitButton" class="btn btn-small btn-purple">
                <i class="icon-ok"></i>
                ${uiLabelMap.CommonSubmit}
                </button>
            </td>
        </tr>
      </table>
    </form>

<div class="widget-box transparent no-bottom-border">
  <div class="widget-header">
  <h4>${uiLabelMap.WebtoolsResults}</h4>
    <span class="widget-toolbar">
    </span>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <#if resultMessage?has_content>
    <p class="alert alert-danger">
      ${resultMessage}
      </p>
      <br />
    </#if>

    <#if columns?has_content>
    <div class="container-table-scroll-advance">
		<div class="scrolly">
	        <table class="table table-striped table-hover table-bordered dataTable table-scroll-advance" cellspacing="0">
	            <thead>
		            <tr>
		            <#list columns as column>
		                <th>${column}<div>${column}</div></th>
		            </#list>
		            </tr>
	            </thead>
	            <tbody>
	            	<#if records?has_content>
		            <#assign alt_row = false>
		            <#list records as record>
		                <tr <#if alt_row> class="alternate-row"</#if> >
		                <#list record as field>
		                    <td>${field?if_exists?string}</td>
		                </#list>
		                </tr>
		                <#assign alt_row = !alt_row>
		            </#list>
		            </#if>
	            </tbody>
	        </table>
		</div>
    </div>
    </#if>
  </div>
</div>
