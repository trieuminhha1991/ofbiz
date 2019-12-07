<div id="reqInfo" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.RequirementType}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="requirementTypeId" name="requirementTypeId">${requirementTypeDesc?if_exists}</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ReasonRequirement}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="reasonEnumId" name="reasonEnumId">${reasonDesc?if_exists}</div>
			   		</div>
				</div>
				<#if requirement.requirementTypeId == "CHANGEDATE_REQUIREMENT">
					<div class='row-fluid'>
						<div class='span4'>
							<span style="float: right"><b>${uiLabelMap.BSCustomerId}</b></span>
						</div>
						<div class="span8">
							<#assign requirementRoleCustomer = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("RequirementRole", {"requirementId" : requirement.requirementId, "roleTypeId": "CUSTOMER"}, null, false))!/>
							<#if requirementRoleCustomer?exists><#assign customerParty = delegator.findOne("PartyFullNameDetailSimple", {"partyId" : requirementRoleCustomer.partyId}, false)!/></#if>
							<div class="green-label" style="text-align: left;"><#if customerParty?has_content>${customerParty.fullName?if_exists} (${customerParty.partyCode?if_exists})</#if></div>
				   		</div>
					</div>
				</#if>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='row-fluid' style="margin-bottom: 5px !important">
						<div class='span3 align-right'>
							<span>${uiLabelMap.ExportFromFacility}</span>
						</div>
						<div class="span9">
							<div class="green-label" id="facilityId" name="facilityId">
							<#if originFacility?has_content>
								<#if originFacility.facilityCode?has_content>
									[${originFacility.facilityCode?if_exists}] ${originFacility.facilityName?if_exists}
								<#else>
									[${originFacility.facilityId?if_exists}] ${originFacility.facilityName?if_exists}
								</#if>
							</#if>
							</div>
				   		</div>
					</div>
					<#if originAddress?has_content>
						<div class='row-fluid'>
							<div class='span3 align-right'>
								<span>${uiLabelMap.Address}</span>
							</div>
							<div class="span9">
								<div class="green-label" id="contactMechId" name="contactMechId">
									<#if originAddress.fullName?has_content>
										${originAddress.fullName?if_exists}
									</#if>
								</div>
					   		</div>
						</div>
					</#if>
					<#if requirement.requirementTypeId == "CHANGEDATE_REQUIREMENT">
						<div class='row-fluid'>
							<div class='span3'>
								<span style="float: right"><b>${uiLabelMap.BSCustomerAddress}</b></span>
							</div>
							<div class="span9">
								<#assign customerAddress = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : requirement.destContactMechId?if_exists}, false)!/>
								<div class="green-label" style="text-align: left;"><#if customerAddress?has_content>${customerAddress.fullName?if_exists}</#if></div>
					   		</div>
						</div>
					</#if>
				</div>
			</div>
		</div>
	</div>
</div>
<div>
<#if requireDate?has_content && requireDate == 'Y'>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="splitterProduct" style="border-top: none; border-left: none; border-right: none;">
		<div id="leftPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
			<div id="jqxGridProduct"></div>
		</div>
		<div id="rightPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
           <div id="jqxGridProductInfo">
           </div>
    	</div>
	</div>
</div>
<script type="text/javascript" src="/logresources/js/requirement/reqExportRequirementProductWithDate.js?v=1.1.1"></script>
<#else>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="jqxGridProduct">
	</div>
</div>
<script type="text/javascript" src="/logresources/js/requirement/reqExportRequirementProduct.js?v=1.1.1"></script>
</#if>
</div>