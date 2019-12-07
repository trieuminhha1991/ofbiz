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

<#if security.hasPermission("PERIOD_MAINT", session)>
   	<div class="widget-box transparent no-bottom-border">
     	<div class="widget-header widget-header-blue widget-header-flat">
			<h4>${uiLabelMap.DAShowOnlySalesPeriodsWithOrganization}</h4>
			<span class="widget-toolbar none-content">
				<a href="<@ofbizUrl>findCustomTimeSalesPeriod</@ofbizUrl>">
					<i class="icon-list open-sans">${uiLabelMap.DACustomTimePeriod}</i>
				</a>
				<a href="<@ofbizUrl>newCustomTimeSalesPeriod?viewEdit=Y</@ofbizUrl>">
					<i class="icon-plus open-sans">${uiLabelMap.DACreateNewCustomTimePeriod}</i>
				</a>
			</span>
		</div>
     	<div class="widget-body">
	     	<form method="post" action="<@ofbizUrl>editCustomTimeSalesPeriod</@ofbizUrl>" name="setOrganizationPartyIdForm" class="form-horizontal basic-custom-form">
	         	<input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
	         	<div class="row">
	         		<div class="span11">
	         			<div class="control-group">
							<label class="control-label">${uiLabelMap.DAOrganizationId}:</label>
							<div class="controls">
								<@htmlTemplate.lookupField name="findOrganizationPartyId" id="" value='${findOrganizationPartyId?if_exists}' 
											formName="setOrganizationPartyIdForm" fieldFormName="LookupPartyGroupName"/>
								<button class="btn btn-mini btn-primary" type="submit" style="margin-left: 5px">
					         		<i class="icon-ok" ></i>${uiLabelMap.CommonUpdate}
					         	</button>
							</div>
						</div>
	         		</div>
	         	</div>
	     	</form>
     	</div>
   	</div>
	
  	<div class="widget-box transparent no-bottom-border">
    	<div class="widget-header">
      		<#if currentCustomTimePeriod?has_content>
	      		<h4>${uiLabelMap.DACurrentCustomTimePeriod}</h4>
	    		<span class="widget-toolbar">
	          		<li><a href="<@ofbizUrl>editCustomTimeSalesPeriod?findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>">${uiLabelMap.CommonClearCurrent}</a></li>
	        	</span>
      		<#else>
        		<h4>${uiLabelMap.DACurrentCustomTimePeriod}</h4>
      		</#if>
    	</div>
    	<#if currentCustomTimePeriod?has_content>
    		<div class="widget-body">
        		<form method="post" action="<@ofbizUrl>updateCustomTimeSalesPeriod</@ofbizUrl>" name="updateCustomTimePeriodForm">
          			<input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
          			<input type="hidden" name="customTimePeriodId" value="${currentCustomTimePeriod.customTimePeriodId?if_exists}" />
          			<div style="overflow-x: auto;">
	      				<table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
					        <tr class="header-row">
				          		<td>${uiLabelMap.CommonId}</td>
				          		<td>${uiLabelMap.DAParentCustomTimePeriod}</td>
				          		<td nowrap>${uiLabelMap.DAPartyGroupId}</td>
				          		<td>${uiLabelMap.AccountingPeriodType}</td>
				          		<td>${uiLabelMap.CommonNbr}</td>
				          		<td>${uiLabelMap.AccountingPeriodName}</td>
				          		<td>${uiLabelMap.CommonFromDate}</td>
				          		<td>${uiLabelMap.CommonThruDate}</td>
				          		<td>&nbsp;</td>
				        	</tr>
	          				<tr>
	            				<td>${currentCustomTimePeriod.customTimePeriodId}</td>
	            				<td>
	            					<select name="parentPeriodId" class="width-cell-100px">
					                	<option value=''>&nbsp;</option>
					                	<#list allCustomTimePeriods as allCustomTimePeriod>
					                  		<#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
					                  		<#assign isDefault = false>
					                  		<#if (currentCustomTimePeriod.parentPeriodId)?exists>
					                    		<#if currentCustomTimePeriod.customTimePeriodId = allCustomTimePeriod.customTimePeriodId>
					                      			<#assign isDefault = true>
					                    		</#if>
					                  		</#if>
					                  		<option value='${allCustomTimePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
							                    [${allCustomTimePeriod.customTimePeriodId}] 
							                    ${allCustomTimePeriod.organizationPartyId}
							                    <#if allPeriodType??> ${allPeriodType.description}: </#if>
							                    ${allCustomTimePeriod.periodNum?if_exists}
				                  			</option>
				                		</#list>
					              	</select>
					              	<#if (currentCustomTimePeriod.parentPeriodId)?exists>
					                	<a href='<@ofbizUrl>editCustomTimeSalesPeriod?currentCustomTimePeriodId=${currentCustomTimePeriod.parentPeriodId}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>'>
					                	${uiLabelMap.CommonSetAsCurrent}</a>
					              	</#if>
					            </td>
	            				<td class="width150px" nowrap>
					            	<@htmlTemplate.lookupField name="currentCustomTimePeriod" id="" value='${currentCustomTimePeriod.organizationPartyId?if_exists}' 
											formName="updateCustomTimePeriodForm" fieldFormName="LookupPartyGroupName"/>
					            </td>
	            				<td>
					              	<select name="periodTypeId">
					                	<#list periodTypes as periodType>
					                  		<#assign isDefault = false>
						                  	<#if (currentCustomTimePeriod.periodTypeId)?exists>
							                    <#if currentCustomTimePeriod.periodTypeId = periodType.periodTypeId>
							                      	<#assign isDefault = true>
							                    </#if>
						                 	</#if>
					                  		<option value='${periodType.periodTypeId}'<#if isDefault> selected="selected"</#if>>
					                    		${periodType.description} [${periodType.periodTypeId}]
					                  		</option>
				                		</#list>
					              	</select>
	            				</td>
					            <td><input type="text" size='4' class="width-cell-50px" name="periodNum" value="${currentCustomTimePeriod.periodNum?if_exists}" /></td>
					            <td><input type="text" size='10' name="periodName" value="${currentCustomTimePeriod.periodName?if_exists}" /></td>
					            <td nowrap>
					            	<#assign hasntStarted = false>
					              	<#assign compareDate = currentCustomTimePeriod.getDate("fromDate")>
					              	<#if compareDate?has_content>
					                	<#if nowTimestamp.before(compareDate)><#assign hasntStarted = true></#if>
					              	</#if>
					              	<#assign classNameFromDate = "">
					              	<#if hasntStarted>
										<#assign classNameFromDate = "alert">
									</#if>
									<#assign fromDateValue = ""/>
									<#if currentCustomTimePeriod.thruDate?has_content>
										<#assign fromDateValue =Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(currentCustomTimePeriod.thruDate, "yyyy-MM-dd", locale, timeZone)! />
									</#if>
					              	<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" event="" action="" 
											value="${fromDateValue}" className="width100px ${classNameFromDate?if_exists}" alert="" 
											title="Format: yyyy-MM-dd" size="13" maxlength="30" dateType="date" 
											shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
											isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
					            </td>
					            <td nowrap>
					            	<#assign hasExpired = false>
					              	<#assign compareDate = currentCustomTimePeriod.getDate("thruDate")>
					              	<#if compareDate?has_content>
					                	<#if nowTimestamp.after(compareDate)><#assign hasExpired = true></#if>
					              	</#if>
					              	<#assign classNameThruDate = "">
					              	<#if hasExpired>
					              		<#assign classNameThruDate = "alert">
					              	</#if>
					              	<#assign thruDateValue = ""/>
									<#if currentCustomTimePeriod.thruDate?has_content>
										<#assign thruDateValue =Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(currentCustomTimePeriod.thruDate, "yyyy-MM-dd", locale, timeZone)! />
									</#if>
					              	<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" event="" action="" 
											value="${thruDateValue}" className="width100px ${classNameThruDate?if_exists}" alert="" 
											title="Format: yyyy-MM-dd" size="13" maxlength="30" dateType="date" 
											shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
											isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
					            </td>
					            <td>
					              	<div class="hidden-phone visible-desktop btn-group">
										<button type="submit" class="btn btn-mini btn-primary">
											<i class="icon-ok bigger-120"></i>
										</button>
										<button class="btn btn-mini btn-danger" onclick="window.location.href='<@ofbizUrl>deleteCustomTimeSalesPeriod?customTimePeriodId=${currentCustomTimePeriod.customTimePeriodId}</@ofbizUrl>';">
											<i class="icon-trash bigger-120"></i>
										</button>
									</div>
					            </td>
	          				</tr>
	      				</table>
					</div>
        		</form>
        	</div>
	    <#else>
	      	<div class="widget-body"><p class="alert alert-info">${uiLabelMap.DANoCurrentCustomTimePeriodSelected}</p></div>
	    </#if>
  	</div>
  	<br />
  	<div class="widget-box transparent no-bottom-border">
	    <div class="widget-header">
	    	<h4>${uiLabelMap.AccountingChildPeriods}</h4>
	      	<span class="widget-toolbar></span>
	      	<br class="clear"/>
	    </div>
	    <#if customTimePeriods?has_content>
			<div class="widget-body" >
	    		<div style="overflow-x: scroll !important; border: 0.5px solid rgb(204, 204, 204) !important; width:100%; ">
			      	<table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
			        	<tr class="header-row">
			          		<td>${uiLabelMap.CommonId}</td>
			          		<td>${uiLabelMap.DAParentCustomTimePeriod}</td>
			          		<td nowrap>${uiLabelMap.DAPartyGroupId}</td>
			          		<td>${uiLabelMap.AccountingPeriodType}</td>
			          		<td>${uiLabelMap.CommonNbr}</td>
			          		<td>${uiLabelMap.AccountingPeriodName}</td>
			          		<td>${uiLabelMap.CommonFromDate}</td>
			          		<td>${uiLabelMap.CommonThruDate}</td>
			          		<td>&nbsp;</td>
			        	</tr>
			        	<#assign line = 0>
			        	<#list customTimePeriods as customTimePeriod>
			          		<#assign line = line + 1>
			          		<#assign periodType = customTimePeriod.getRelatedOne("PeriodType", true)>
			          		<tr>
			            		<form method="post" action='<@ofbizUrl>updateCustomTimeSalesPeriod</@ofbizUrl>' name='lineForm${line}'>
		              				<input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
			              			<input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
	              					<input type="hidden" name="customTimePeriodId" value="${customTimePeriod.customTimePeriodId?if_exists}" />
			            			<td>${customTimePeriod.customTimePeriodId}</td>
						            <td>
						              	<select name="parentPeriodId" class="width-cell-100px">
						                	<option value=''>&nbsp;</option>
						                	<#list allCustomTimePeriods as allCustomTimePeriod>
						                  		<#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
						                  		<#assign isDefault = false>
						                  		<#if (customTimePeriod.parentPeriodId)?exists>
						                    		<#if customTimePeriod.parentPeriodId = allCustomTimePeriod.customTimePeriodId>
						                      			<#assign isDefault = true>
						                    		</#if>
						                  		</#if>
						                  		<option value='${allCustomTimePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
								                    [${allCustomTimePeriod.customTimePeriodId}] 
								                    ${allCustomTimePeriod.periodName?if_exists} - 
								                    ${allCustomTimePeriod.organizationPartyId}
								                    <#if allPeriodType??>- ${allPeriodType.description}: </#if>
								                    ${allCustomTimePeriod.periodNum?if_exists}
					                  			</option>
					                		</#list>
						              	</select>
						            </td>
						            <td class="width150px" nowrap>
						            	<@htmlTemplate.lookupField name="organizationPartyId" id="" value='${customTimePeriod.organizationPartyId?if_exists}' 
												formName="lineForm${line}" fieldFormName="LookupPartyGroupName"/>
						            </td>
						            <td>
						              	<select name="periodTypeId">
							                <#list periodTypes as periodType>
							                  	<#assign isDefault = false>
							                  	<#if (customTimePeriod.periodTypeId)?exists>
								                    <#if customTimePeriod.periodTypeId = periodType.periodTypeId>
								                     	<#assign isDefault = true>
								                    </#if>
							                  	</#if>
							                  	<option value='${periodType.periodTypeId}'<#if isDefault> selected="selected"</#if>>${periodType.description} [${periodType.periodTypeId}]</option>
							                </#list>
						              	</select>
						            </td>
			            			<td><input type="text" size='4' class="width-cell-50px" name="periodNum" value="${customTimePeriod.periodNum?if_exists}" /></td>
			            			<td><input type="text" size='10' name="periodName" value="${customTimePeriod.periodName?if_exists}" /></td>
			            			<td nowrap>
						              	<#assign hasntStarted = false>
						              	<#assign compareDate = customTimePeriod.getDate("fromDate")>
						              	<#if compareDate?has_content>
						                	<#if nowTimestamp.before(compareDate)><#assign hasntStarted = true></#if>
						              	</#if>
						              	<#if hasntStarted>
											<#assign classNameFromDate = "alert">
										</#if>
										<#assign fromDateValue = ""/>
										<#if customTimePeriod.fromDate?has_content>
											<#assign fromDateValue =Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimePeriod.fromDate, "yyyy-MM-dd", locale, timeZone)! />
										</#if>
						              	<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate${line}" event="" action="" 
												value="${fromDateValue}" className="width100px ${classNameFromDate?if_exists}" alert="" 
												title="Format: yyyy-MM-dd" size="13" maxlength="30" dateType="date" 
												shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
												timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
												isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
			            			</td>
			            			<td nowrap>
						              	<#assign hasExpired = false>
						              	<#assign compareDate = customTimePeriod.getDate("thruDate")>
						              	<#if compareDate?has_content>
							                <#if nowTimestamp.after(compareDate)><#assign hasExpired = true></#if>
						              	</#if>
						              	<#if hasExpired>
						              		<#assign classNameThruDate = "alert">
						              	</#if>
						              	<#assign thruDateValue = ""/>
										<#if customTimePeriod.thruDate?has_content>
											<#assign thruDateValue =Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimePeriod.thruDate, "yyyy-MM-dd", locale, timeZone)! />
										</#if>
						              	<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate${line}" event="" action="" 
												value="${thruDateValue}" className="width100px ${classNameThruDate?if_exists}" alert="" 
												title="Format: yyyy-MM-dd" size="13" maxlength="30" dateType="date" 
												shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
												timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
												isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
			             			</td>
					             	<td>
					              		<div class="hidden-phone visible-desktop btn-group">
											<button type="submit" class="btn btn-mini btn-primary">
												<i class="icon-ok bigger-120"></i>
											</button>
											<button class="btn btn-mini btn-danger" onclick="window.location.href='<@ofbizUrl>deleteCustomTimeSalesPeriod?customTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;currentCustomTimePeriodId=${currentCustomTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>';">
												<i class="icon-trash bigger-120"></i>
											</button>
											<button type="button" class="btn btn-mini btn-primary" onclick="window.location.href='<@ofbizUrl>editCustomTimeSalesPeriod?currentCustomTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>';" 
												title="${uiLabelMap.CommonSetAsCurrent}">
												<i class="icon-zoom-in bigger-120"></i>
											</button>
										</div>
						            </td>
			            		</form>
			          		</tr>
			        	</#list>
			      	</table>
	      		</div>
	  		</div>
	    <#else>
	      	<div class="widget-body"><p class="alert alert-info">${uiLabelMap.DANoChildPeriodsFound}</p></div>
	    </#if>
  	</div>
  	<br />
	<#include "newCustomTimePeriod.ftl"/>
<#else>
  	<p class="alert alert-info">${uiLabelMap.AccountingPermissionPeriod}.</p>
</#if>
