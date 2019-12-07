<#if security.hasPermission("PERIOD_MAINT", session)>
	<div class="widget-box transparent no-bottom-border">
	    <div class="widget-header widget-header-blue widget-header-flat">
			<h4>${uiLabelMap.DACreateNewCustomTimePeriod}</h4>
			<#if displaySubTabBarHeader?exists && displaySubTabBarHeader == 'Y'>
			<span class="widget-toolbar none-content">
				<a href="<@ofbizUrl>findCustomTimeSalesPeriod</@ofbizUrl>">
					<i class="icon-list open-sans">${uiLabelMap.DACustomTimePeriod}</i>
				</a>
				<a href="<@ofbizUrl>editCustomTimeSalesPeriod</@ofbizUrl>">
					<i class="icon-pencil open-sans">${uiLabelMap.DAEditCustomTimePeriods}</i>
				</a>
			</span>
			</#if>
		</div>
	    <div class="widget-body">
	      	<form method="post" action="<@ofbizUrl>createCustomTimeSalesPeriod</@ofbizUrl>" name="createCustomTimePeriodForm" class="form-horizontal basic-custom-form">
		        <input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
		        <input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
		        <input type="hidden" name="useValues" value="true" />
		        <div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="parentPeriodId">${uiLabelMap.DAParentCustomTimePeriod}:</label>
							<div class="controls">
								<div class="span12">
									<select name="parentPeriodId" id="parentPeriodId">
							            <option value=''>&nbsp;</option>
							            <#list allCustomTimePeriods as allCustomTimePeriod>
							            	<#assign isDefault = false>
							                <#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
							              	<#if currentCustomTimePeriod?exists>
							                	<#if currentCustomTimePeriod.customTimePeriodId = allCustomTimePeriod.customTimePeriodId>
							                  		<#assign isDefault = true>
							                	</#if>
							              	</#if>
							              	<option value="${allCustomTimePeriod.customTimePeriodId}"<#if isDefault> selected="selected"</#if>>
							              		${allCustomTimePeriod.periodName?if_exists} - 
								                ${allCustomTimePeriod.organizationPartyId?if_exists}
								                <#if (allCustomTimePeriod.parentPeriodId)?exists>Par:${allCustomTimePeriod.parentPeriodId}</#if>
								                <#if allPeriodType??>- ${allPeriodType.description}:</#if>
								                ${allCustomTimePeriod.periodNum}
								                [${allCustomTimePeriod.customTimePeriodId}]
							              	</option>
							            </#list>
						          	</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="organizationPartyId">${uiLabelMap.DAFormFieldTitle_organizationPartyId}:</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.lookupField name="organizationPartyId" id="" value='' 
											formName="createCustomTimePeriodForm" fieldFormName="LookupPartyGroupName"/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="periodTypeId">${uiLabelMap.AccountingPeriodType}:</label>
							<div class="controls">
								<div class="span12">
									<select name="periodTypeId" id="periodTypeId">
							            <#list periodTypes as periodType>
							              	<#assign isDefault = false>
							              	<#if newPeriodTypeId?exists>
							                	<#if newPeriodTypeId = periodType.periodTypeId>
							                  		<#assign isDefault = true>
							                	</#if>
							              	</#if>
							              	<option value="${periodType.periodTypeId}" <#if isDefault>selected="selected"</#if>>${periodType.description} [${periodType.periodTypeId}]</option>
							            </#list>
						          	</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="periodName">${uiLabelMap.AccountingPeriodName}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" size='10' name='periodName' id="periodName"/>
								</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6 no-left-margin">
						<div class="control-group">
							<label class="control-label" for="periodNum">${uiLabelMap.AccountingPeriodNumber}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" size='10' name='periodNum' id='periodNum'/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="fromDate">${uiLabelMap.CommonFromDate}:</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDateNew" event="" action="" 
										value="" className="" alert="" 
										title="Format: yyyy-MM-dd" size="14" maxlength="30" dateType="date" 
										shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
										timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
										isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="periodName">${uiLabelMap.CommonThruDate}:</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDateNew" event="" action="" 
										value="" className="" alert="" 
										title="Format: yyyy-MM-dd" size="14" maxlength="30" dateType="date" 
										shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
										timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
										isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="periodName"></label>
							<div class="controls" style="margin-top:-10px !important">
								<div class="span12">
									<button type="submit" class="btn btn-small btn-primary">
					          			<i class="icon-ok"></i>${uiLabelMap.CommonAdd}
					         	 	</button>
					         	 	<#assign linkGoBack = parameters.viewEdit!''>
					         	 	<#if linkGoBack?exists && linkGoBack == 'Y'>
					         	 		<#assign linkGoBack = "editCustomTimeSalesPeriod">
					         	 	<#else>
					         	 		<#assign linkGoBack = "findCustomTimeSalesPeriod">
					         	 	</#if>
					         	 	<a class="btn btn-small btn-danger" href="<@ofbizUrl>${linkGoBack}</@ofbizUrl>">
										<i class="icon-remove open-sans">${uiLabelMap.DACancel}</i>
									</a>
								</div>
							</div>
						</div>
					</div><!--.span6-->
		        </div><!--.row-->
	      	</form>
	    </div>
	</div>
<#else>
  	<p class="alert alert-info">${uiLabelMap.AccountingPermissionPeriod}.</p>
</#if>
