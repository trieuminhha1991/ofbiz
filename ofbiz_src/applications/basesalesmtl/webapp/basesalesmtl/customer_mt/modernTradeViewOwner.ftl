<#assign agentRep = modernTradeInfo.representative?default({})/>
<div id="partyowner-tab" class="tab-pane<#if activeTab?exists && activeTab == "partyowner-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.FullName}:</label>
							</div>
							<div class="div-inline-block">
								<span><i>${agentRep.partyFullName?if_exists}</i></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.DmsPartyBirthDate}:</label>
							</div>
							<div class="div-inline-block">
								<span>${agentRep.birthDate?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.DmsPartyGender}:</label>
							</div>
							<div class="div-inline-block">
                                <span>
                                    <#if agentRep.gender?if_exists = "M">
                                        ${uiLabelMap.CommonMale}
                                    <#elseif agentRep.gender?if_exists = "F">
                                        ${uiLabelMap.CommonFemale}
                                    </#if>
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
								<span>${agentRep.contactNumber?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.EmailAddress}:</label>
							</div>
							<div class="div-inline-block">
								<span>${agentRep.infoString?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSAddress}:</label>
							</div>
							<div class="div-inline-block">
								<span>
									<#if agentRep.contactMechId?exists>
										<#assign contactMechesRep = delegator.findByAnd("PostalAddressFullNameDetail", {"contactMechId", agentRep.contactMechId}, null, false)!/>
										<#if contactMechesRep?has_content>
											<ul class="unstyled spaced" style="margin: 0 0 0 0">
											<#list contactMechesRep as contactItem>
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
