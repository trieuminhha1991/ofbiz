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
	<table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
		<tr class="header-row">
			<td class="header-table">${uiLabelMap.JobRequisitionId}</td>
			<td class="header-table" style = "width: 150px;">${uiLabelMap.SkillTypeIds}</td>
			<td class="header-table">${uiLabelMap.JobPostingType}</td>
			<td class="header-table">${uiLabelMap.ExamTypeEnumId}</td>
			<td class="header-table" style = "width: 150px;">${uiLabelMap.Qualifications}</td>
			<td class="header-table">${uiLabelMap.ExperienceInYears}</td>
			<td class="header-table">${uiLabelMap.ExperienceInMonths}</td>
			<td class="header-table">${uiLabelMap.expiredDate}</td>
		</tr>
		<#if context.listIt?has_content>
		<#list listIt as requisition>
			<tr>
				<td>
				 	<a class="btn btn-mini btn-info" href="javascript:set_value(${requisition.jobRequisitionId})">
						${requisition.jobRequisitionId?if_exists}
					</a>
				</td>
				<td>
					<#assign i = 1>
					<#list requisition.skillTypeIds?if_exists as skillType>
						${i}. &nbsp ${skillType.description}
						<#assign i = i+1>
						</br>
					</#list>
				</td>
				<td>
					${requisition.jobPostingType?if_exists}
				</td>
				<td>
					${requisition.examTypeEnumId?if_exists}
				</td>
				<td>
					<#assign i = 1>
					<#list requisition.qualifications?if_exists as qualification>
						${i}. &nbsp ${qualification.description}
						<#assign i = i + 1>
						</br>
					</#list>
				</td>
				<td>
					${requisition.experienceYears?if_exists}
				</td>
				<td>
					${requisition.experienceMonths?if_exists}
				</td>
				<td>
					${requisition.expiredDate?if_exists}
				</td>
			</tr>
		</#list>
	</#if>
	</table>