<#assign representativeOfficeInfos = dispatcher.runSync("loadMTRepresentativeOfficeInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "${modernTradeInfo.representativeOfficeId?if_exists}", "detail", "Y", "userLogin", userLogin))/>
<#assign officeInfo = representativeOfficeInfos.MTCustomerInfo/>
<div id="partyrep-tab" class="tab-pane<#if activeTab?exists && activeTab == "partyrep-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSCustomerId}:</label>
							</div>
							<div class="div-inline-block">
								<span><i>${officeInfo.partyCode?if_exists}</i></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSCustomerName}:</label>
							</div>
							<div class="div-inline-block">
								<span>${officeInfo.groupName?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
							</div>
							<div class="div-inline-block">
                                <span>
                                    ${officeInfo.officeSiteName?if_exists}
                                </span>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.PhoneNumber}:</label>
							</div>
							<div class="div-inline-block">
								<span>${officeInfo.contactNumber?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.EmailAddress}:</label>
							</div>
							<div class="div-inline-block">
								<span>${officeInfo.infoString?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSAddress}:</label>
							</div>
							<div class="div-inline-block">
                                    <span>
										<#assign arrayPurposes = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, null, false), "contactMechPurposeTypeId", true)?default([])/>
										<#assign conditionList = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", officeInfo.partyId)]/>
										<#assign conditionList = conditionList + [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechPurposeTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, arrayPurposes)]/>
										<#assign conditionList = conditionList + [Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()]/>
										<#assign partyContactMechPurposes = delegator.findList("PartyContactMechPurpose", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditionList), null, null, null, false)!/>
										<#if partyContactMechPurposes?has_content>
											<#assign contactMechIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(partyContactMechPurposes, "contactMechId", true)!/>
											<#assign contactMeches = delegator.findList("PostalAddressFullNameDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, contactMechIds), null, null, null, false)!/>
											<#if contactMeches?has_content>
												<ul class="unstyled spaced" style="margin: 0 0 0 0">
												<#list contactMeches as contactItem>
								                	<li style="margin-bottom:0; margin-top:0">
												      	${contactItem.fullName?if_exists}
													</li>
								                </#list>
								                </ul>
											</#if>
										</#if>
									</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
