<div id="partyoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "partyoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<#if distributorInfo.statusId?exists>
			<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : "${distributorInfo.statusId}"}, false)!/>
			<div class="title-status" id="statusTitle">
				<#if statusItem?exists>${statusItem.get("description", locale)}</#if>
			</div>
		</#if>
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDistributorId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${distributorInfo.partyCode?if_exists}</i></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDistributorName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.groupName?if_exists}<#-- distributorInfo.groupNameLocal --></span>
								</div>
							</div>
							
							<div class="row-fluid margin-top10">
								<div class="logo-company">
									<img width="300px" src="${distributorInfo.logoImageUrl?default("/salesmtlresources/logo/LOGO_demo.png")}"/>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.officeSiteName?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PartyTaxAuthInfos}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.taxAuthInfos?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCurrencyUomId}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.currencyUomId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.EmailAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.infoString?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PhoneNumber}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.contactNumber?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#assign arrayPurposes = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, null, false), "contactMechPurposeTypeId", true)?default([])/>
										<#assign conditionList = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", distributorInfo.partyId)]/>
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
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDescription}:</label>
								</div>
								<div class="div-inline-block">
									<span>${distributorInfo.comments?if_exists}</span>
								</div>
							</div>
						</div>
					</div>
					<#-- get info supervisor department -->
					<#assign supIds = Static["com.olbius.salesmtl.party.PartyWorker"].getSupByDist(delegator, distributorInfo.partyId)!/>
					<#if supIds?has_content>
						<#assign supId = supIds?first/>
						<#assign supNames = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : supId}, null, false)!/>
						<div class="row-fluid">
							<div class="span6">
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.BSSupervisor}:</label>
									</div>
									<div class="div-inline-block">
                                        <span><#list supNames as supName>${supName.fullName?if_exists} (${supName.partyCode?if_exists})</#list></span>
									</div>
								</div>
								<#assign supDeptIds = Static["com.olbius.basesales.party.PartyWorker"].getOrgByManager(delegator, supId)!/>
								<#if supDeptIds?has_content>
									<#assign supDeptId = supDeptIds?first/>
									<#assign supDepts = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : supDeptId}, null, false)!/>
									<div class="row-fluid">
										<div class="div-inline-block">
											<label>${uiLabelMap.BSSupDept}:</label>
										</div>
										<div class="div-inline-block">
											<span><#list supDepts as supDept>${supDept.fullName?if_exists} (${supDept.partyCode?if_exists})</#list></span>
										</div>
									</div>
								</#if>
							</div>
						</div>
					</#if>
				</div>
			</div>
		</div>
	</div>
	<#--comment by Huyen then trans data SQL-->
	<#--<#if distributorInfo.partyId?exists>
		<div class="row-fluid margin-top10">
			<div class="span12">
				${screens.render("component://basesalesmtl/widget/BaseSalesMtlReportScreens.xml#EvaluateAgencyInner")}
			</div>
		</div>
	</#if>-->
</div>