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

  <div id="trainingStatus" class="widget-box transparent no-bottom-border">
  	<div class="widget-box collapsed">
  <div class="widget-header widget-header-small header-color-blue2">
   <h6>${uiLabelMap.HumanResTrainings}</h6>
   <div class="widget-toolbar">
   		<a href="#" data-action="collapse">
   			<i class="icon-chevron-up"></i>
   		</a>
   </div>
  </div>
    <div class="widget-body">
    <#assign listPersonTraining = delegator.findList("PersonTraining", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", parameters.partyId), null, null, null, false) />
		<#if listPersonTraining?has_content>	
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0" style="margin-bottom:10px;margin-top:5px;" >
          <tr>
            <th>${uiLabelMap.fromDate}</th>
          	<th>${uiLabelMap.thruDate}</th>
            <th>${uiLabelMap.EmplProposalCheck}</th>
            <th>${uiLabelMap.HROlbiusTrainingClassType}</th>
            <th>${uiLabelMap.ApproverEmployee}</th>
          </tr>
			<#list listPersonTraining as personTraining>
			<tr>
				<td>${(personTraining.fromDate)?if_exists}</td>
				<td>${(personTraining.thruDate)?if_exists}</td>
				<#assign approvalStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", personTraining.get("approvalStatus",locale)), null, null, null, false) />
				<td>${(approvalStatus.get(0).description)?if_exists}</td>
				<#assign trainingClassType = delegator.findList("TrainingClassType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("trainingClassTypeId", personTraining.get("trainingClassTypeId",locale)), null, null, null, false)?if_exists />
				<#if trainingClassType?has_content>
				<td>${(trainingClassType.get(0).description)?if_exists}</td>
				<#else><td></td></#if>
				<#assign partyNameView = delegator.findList("PartyNameView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", personTraining.get("approverId",locale)), null, null, null, false)?if_exists />
				<#if partyNameView?has_content>
				<td>${(partyNameView.get(0).firstName)?if_exists}
					${(partyNameView.get(0).middleName)?if_exists}
						${(partyNameView.get(0).lastName)?if_exists}
						${(partyNameView.get(0).groupName)?if_exists}
				</td>
				<#else><td></td>
				</#if>
			</tr>
			</#list>
        </table>
        <#else>
	 	<div style="margin-bottom:10px; margin-top:5px;" class="padding-top8">
        <span class="alert alert-warning font-size13"> ${uiLabelMap.PartyNoContent}</span>
		</div>
		</#if>
    </div>
  </div>
  </div>