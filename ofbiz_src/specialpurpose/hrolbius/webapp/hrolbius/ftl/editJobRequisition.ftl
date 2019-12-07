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
			${uiLabelMap.PageTitleEditJobRequisitionItem}
		</h4>
		<br class="clear"/>
	</div>
	<div class="widget-body">
		<#if requisition.jobRequisitionId?has_content>
			<#assign targetForm = "updateJobRequisition">
			<#assign display = "grid">
		<#else>
			<#assign targetForm = "createJobRequisition">
			<#assign display = "none">
		</#if>
		<form method="post" name="editJobRequisition" action="<@ofbizUrl>createJobRequisition</@ofbizUrl>" class="basic-form" style="padding-top: 10px; padding-left: 50px; padding-bottom: 10px;">
			<table cellspacing="0">
				<tr>
					<input name="jobRequisitionId" value="${requisition.jobRequisitionId?if_exists}" type="hidden">
				</tr>
				<tr style="display: ${display};">
					<td > <label class="padding-bottom5 padding-right15">${uiLabelMap.JobRequisitionId}</label></td>
					<td>
						${requisition.jobRequisitionId?if_exists}
						<span class="tooltipob">${uiLabelMap.change}</span>
					</td>
				</tr>
				
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="jobRequestId">${uiLabelMap.JobRequestId}</label></td>
					<td>
						<@htmlTemplate.lookupField formName="editJobRequisition" name="jobRequestId" id="jobRequestId" fieldFormName="LookupJobRequest"/>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="jobLocation">${uiLabelMap.jobLocation}</label></td>
					<td>
						<input type="text" name="jobLocation" id="jobLocation" value="${requisition.jobLocation?if_exists}" >
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="age">${uiLabelMap.age}</label></td>
					<td><input type="text" name="age" id="age" value="${requisition.age?if_exists}" ></td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="noOfResources">${uiLabelMap.noOfResources}</label></td>
					<td>
						<input type="text" name="noOfResources" id="noOfResources" value="${requisition.noOfResources?if_exists}" >
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="gender">${uiLabelMap.gender}</label></td>
					<td>
						<select name="gender">
							<#if requisition.gender?if_exists = "M">
								<#assign male = "selected">
								<#assign female = "">
							<#elseif requisition.gender?if_exists = "F">
								<#assign male = "">
								<#assign female = "selected">
							<#else>
								<#assign male = "">
								<#assign female = "">
							</#if>
							<option value>&nbsp;</option>
							<option value="M" ${male} >${uiLabelMap.CommonMale}</option>
							<option value="F" ${female}>${uiLabelMap.CommonFemale}</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="qualifications">${uiLabelMap.Qualifications}</label></td>
					<td>
						<select name="qualifications" id="qualifications" multiple>
							<option value>&nbsp;</option>
							<#list customQualificationList?if_exists as qualification>
								<#if qualification.selected>
									<#assign str = "selected">
								<#else>
									<#assign str = "">
								</#if>
								<option value=${qualification.partyQualTypeId} ${str}>${qualification.description}</option>
							</#list>
						</select>
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="examTypeEnumId">${uiLabelMap.ExamTypeEnumId}</label></td>
					<td>
						<select name="examTypeEnumId" id="examTypeEnumId">
							<option value>&nbsp;</option>
							<#list examTypeEnumList?if_exists as examTypeEnum>
								<#if examTypeEnum.enumId == requisition.examTypeEnumId?if_exists>
									<#assign str = "selected">
								<#else>
									<#assign str = "">
								</#if>
								<option value=${examTypeEnum.enumId} ${str}>${examTypeEnum.description}</option>
							</#list>
						</select>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="skillTypeIds">${uiLabelMap.SkillTypeIds}</label></td>
					<td>
						<select name="skillTypeIds" id="skillTypeIds" multiple>
							<option value>&nbsp;</option>
							<#list customSkillTypeList?if_exists as skillType>
								<#if skillType.selected>
									<#assign str = "selected">
								<#else>
									<#assign str = "">
								</#if>
								<option value=${skillType.skillTypeId} ${str}>${skillType.description}</option>
							</#list>
						</select>
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="experienceMonths">${uiLabelMap.ExperienceInMonths}</label></td>
					<td>
						<input name="experienceMonths" id="experienceMonths" type="text" value="${requisition.experienceMonths?if_exists}">
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="experienceYears">${uiLabelMap.ExperienceInYears}</label></td>
					<td>
						<input name="experienceYears" id="experienceYears" type="text" value="${requisition.experienceYears?if_exists}">
						<span class="tooltipob">${uiLabelMap.required}</span>
					</td>
				</tr>
				<tr>
					<td><input name="jobPostingTypeEnumId" type="hidden" value="JOB_POSTING_INTR"></td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="fromDate">
						${uiLabelMap.fromDate}
						</label>
					</td>
					<td>
						<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${requisition.fromDate?if_exists}" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</td>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="thruDate">
						${uiLabelMap.thruDate}
						</label>
					</td>
					<td>
						<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${requisition.thruDate?if_exists}" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</td>
				</tr>
				<tr>
					<input type="hidden" name="statusId" value="REQ_CREATED"/>
				</tr>
				<tr>
					<td><label class="padding-bottom5 padding-right15" for="description">${uiLabelMap.Description}</label></td>
					<td>
						<input name="description" id="description" type="text" />
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<button name="submit" type="submit" class="btn btn-success btn-small">
						<i class="icon-ok" ></i>
						${uiLabelMap.CommonApply}
						</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>