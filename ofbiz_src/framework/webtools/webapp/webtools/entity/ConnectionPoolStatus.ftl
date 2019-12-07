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
<div class="widget-box transparent no-bottom-border">
<div class="widget-header">
<h4>Connection Pool Status</h4>
</div>
<div class="screenlet-body">
<div>
<#assign groups = delegator.getModelGroupReader().getGroupNames(delegator.getDelegatorName())?if_exists/>
<table class="table table-hover table-striped table-bordered light-grid dataTable">
    <tr class="header-row">
        <td style="font-weight: bold">Helper Name</td>
        <td style="font-weight: bold">Num Active</td>
        <td style="font-weight: bold">Num Idle</td>
        <td style="font-weight: bold">Num Total</td>
        <td style="font-weight: bold">Max Active</td>
        <td style="font-weight: bold">Max Idle</td>
        <td style="font-weight: bold">Min Idle</td>
        <td style="font-weight: bold">Min Evictable Idle Time</td>
        <td style="font-weight: bold">Max Wait</td>
    </tr>
    <#assign alt_row = false>
    <#if (groups?has_content)>
        <#list groups as group>
            <#assign helper = delegator.getGroupHelperName(group)?if_exists/>
            <#if (helper?has_content)>
                <#assign dataSourceInfo = Static["org.ofbiz.entity.connection.DBCPConnectionFactory"].getDataSourceInfo(helper)?if_exists/>
                <#if (dataSourceInfo?has_content)>
                    <tr>
                        <td>${helper}</td>
                        <td>${dataSourceInfo.poolNumActive?if_exists}</td>
                        <td>${dataSourceInfo.poolNumIdle?if_exists}</td>
                        <td>${dataSourceInfo.poolNumTotal?if_exists}</td>
                        <td>${dataSourceInfo.poolMaxActive?if_exists}</td>
                        <td>${dataSourceInfo.poolMaxIdle?if_exists}</td>
                        <td>${dataSourceInfo.poolMinIdle?if_exists}</td>
                        <td>${dataSourceInfo.poolMinEvictableIdleTimeMillis?if_exists}</td>
                        <td>${dataSourceInfo.poolMaxWait?if_exists}</td>
                    </tr>
                </#if>
            </#if>
        </#list>
    </#if>
</table>
</div>
</div>
</div>