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
        <form method="post" action="<@ofbizUrl>FindGeneric?entityName=${entityName}</@ofbizUrl>">
          <input type="hidden" name="find" value="true"/>
          <input type="hidden" name="searchOptions_collapsed" value="true"/>
          <table class="table table-striped table-hover table-bordered dataTable" cellspacing="0">
            <tr class="header-row-2">
              <td  style="font-weight:bold;">${uiLabelMap.WebtoolsFieldName}</td>
              <td  style="font-weight:bold;">${uiLabelMap.WebtoolsPk}</td>
              <td  style="font-weight:bold;">${uiLabelMap.WebtoolsFieldType}</td>
              <td>&nbsp;</td>
            </tr>
            <#list fieldList as field>
                <tr>
                    <td>${field.name}</td>
                    <td><#if field.isPk == 'Y'>*</#if></td>
                    <td>${field.javaType},&nbsp;${field.sqlType}</td>
                    <td><input type="text" name="${field.name}" value="${field.param}" size="40"/></td>
                </tr>
            </#list>
                <tr>
                    <td colspan="3"><p class="alert alert-info">${uiLabelMap.WebtoolsToFindAll} ${entityName}, ${uiLabelMap.WebtoolsLeaveAllEntriesBlank}</p></td>
                    <td><button class="btn btn-purple btn-small" type="submit">
                    <i class="icon-search"></i>
                    ${uiLabelMap.CommonFind}
                    </button>
                    </td>
                </tr>
            </table>
        </form>
