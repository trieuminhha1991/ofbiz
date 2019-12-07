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

<div class="widget-box">
	<div class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
		<h4>
			<i class="icon-edit"></i>
			${uiLabelMap.ApproveJobRequisitionPageTitle}
		</h4>
		<br class="clear"/>
	</div>
	<div class="widget-body">
		<form method="post" name="updateJobRequisitionApproval" action="<@ofbizUrl>updateJobRequisitionApproval</@ofbizUrl>" class="basic-form" style="padding-top: 10px; padding-left: 50px; padding-bottom: 10px;">
			<table cellspacing="0">
				<tr>
					<input type="hidden" name = "jobRequisitionId" value="${requisition.jobRequisitionId?if_exists}" />
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.JobRequisitionId}</label></td>
					<td style="color: red;">
						${requisition.jobRequisitionId?if_exists}
					</td>
				</tr>
				<tr>
					<td ><label class="padding-bottom5 padding-right15 ">${uiLabelMap.jobLocation}</label></td>
					<td style="color: red;">
						${requisition.jobLocation?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.age}</label></td>
					<td style="color: red;">
						${requisition.age?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.noOfResources}</label></td>
					<td style="color: red;">
						${requisition.noOfResources?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.gender}</label></td>
					<td style="color: red;">
							<#if requisition.gender?if_exists = "M">
								${uiLabelMap.CommonMale}
							<#elseif requisition.gender?if_exists = "F">
								${uiLabelMap.CommonFemale}
							</#if>
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.Qualifications}</label></td>
					<td style="color: red;">
							<#list customQualificationList?if_exists as qualification>
								<#if qualification.selected>
									- ${qualification.description} <br/>
								</#if>
							</#list>
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.ExamTypeEnumId}</label></td>
					<td style="color: red;">
							<#list examTypeEnumList?if_exists as examTypeEnum>
								<#if examTypeEnum.enumId == requisition.examTypeEnumId?if_exists>
									${examTypeEnum.description}
								</#if>
							</#list>
						</select>
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.SkillTypeIds}</label></td>
					<td style="color: red;">
							<#list customSkillTypeList?if_exists as skillType>
								<#if skillType.selected>
									- ${skillType.description}</br>
								</#if>
							</#list>
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.ExperienceInMonths}</label></td>
					<td style="color: red;">
						${requisition.experienceMonths?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">${uiLabelMap.ExperienceInYears}</label></td>
					<td style="color: red;">
						${requisition.experienceYears?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">
						${uiLabelMap.fromDate}
						</label>
					</td>
					<td style="color: red;">
						${requisition.fromDate?if_exists}
					</td>
				</tr>
				<tr>
					<td> <label class="padding-bottom5 padding-right15 ">
						${uiLabelMap.thruDate}
						</label>
					</td>
					<td style="color: red;">
						${requisition.thruDate?if_exists}
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15 " for="statusId">
						${uiLabelMap.Approve}
						</label>
					</td>
					<td>
						<select name="statusId" id="statusId">
							<#list statusList as status>
								<#if status.statusId == requisition.statusId>
									<#assign str = "selected" />
								<#else>
									<#assign str = "" />
								</#if>
								<option value="${status.statusId}" ${str}>
									${status.description}
								</option>
							</#list>
						</select>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<button name="submit" type="submit" class="btn btn-success btn-small">
						<i class="icon-ok" ></i>
						${uiLabelMap.CommonApprove}
						</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>