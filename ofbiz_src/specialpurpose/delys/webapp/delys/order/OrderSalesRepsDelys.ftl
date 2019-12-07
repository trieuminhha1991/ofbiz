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

<#if salesReps?has_content>
<div class="widget-box olbius-extra">
<div class="widget-header widget-header-small header-color-blue2">
	 <h6>${uiLabelMap.OrderSalesReps}</h6>
    <div class="widget-toolbar">
	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>         
    </div>
    </div>
    <div class="widget-body">
    <div class="widget-body-inner">
        <div class="widget-body-inner">
      <table class="table table-hover table-bordered table-striped dataTable" cellspacing='0'>
      <tr class="header-row">
        <td width="50%">${uiLabelMap.PartyLastName}</td>
        <td width="50%">${uiLabelMap.PartyFirstName}</td>
      </tr>
    <#list salesReps as salesRep>
      <#assign party = salesRep.getRelatedOne("Party", false)?if_exists/>
      <#assign person = party.getRelatedOne("Person", false)?if_exists/>
      <#if person?exists && person?has_content>
      <tr>
        <td width="50%">${person.lastName}</td>
        <td width="50%">${person.firstName}</td>
      </tr>
      </#if>
    </#list>
      </table>
    </div>
</div>
</div>
</div>
</#if>
