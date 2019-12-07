<#include 'component://basesalesmtl/webapp/basesalesmtl/distributor/requirement/script/requirementDetailScript.ftl'/>
<div id="notifyCreateSuccessful" style="display: none;">
	<div>
		${uiLabelMap.CreateSuccessfully}. 
	</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;"></div>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">					
	<div style="position:relative">
		<div class="row-fluid">
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class='row-fluid'>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.RequirementId}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementId" name="requirementId">
									${requirement.requirementId?if_exists}
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.Status}</span>
							</div>
							<div class="span7">
								<div class="span5">
									<div id="reqStatusId" style="text-align: left;" class="green-label"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.RequirementType}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementTypeId" name="requirementTypeId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.ReasonRequirement}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="reasonEnumId" name="reasonEnumId"></div>
					   		</div>
				   		</div>
				   		<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.CreatedBy}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="createdByUserLogin" name="createdByUserLogin">
									${createdBy.lastName?if_exists} ${createdBy.middleName?if_exists} ${createdBy.firstName?if_exists} 
								</div>
					   		</div>
				   		</div>
				   		<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.Department}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="createdByDepartment" name="createdByDepartment">
									${(createdByDepartment.groupName)?if_exists}
								</div>
					   		</div>
				   		</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.LogRequiredByDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requiredByDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.LogRequirementStartDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementStartDate">
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.EstimatedBudget}</span>
							</div>
							<div class="span7">	
								<div id="estimatedBudget" style="text-align: left;" class="green-label"></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.Description}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="description" name="description"></div>
					   		</div>
						</div>
						<#if requirement.requirementTypeId == "TRANSFER_REQUIREMENT" || requirement.requirementTypeId == "BORROW_REQUIREMENT">
							<#if requirement.facilityId?has_content>
								<div class='row-fluid'>
									<div class='span5'>
										<span style="float: right">${uiLabelMap.FacilityFrom}</span>
									</div>
									<div class="span7">
										<div class="green-label" style="text-align: left;" id="facilityIdDT" name="facilityIdDT"></div>
							   		</div>
								</div>
							<#else>
							
							</#if>
							<#if requirement.destFacilityId?has_content>
								<div class='row-fluid'>
									<div class='span5'>
										<span style="float: right">${uiLabelMap.FacilityTo}</span>
									</div>
									<div class="span7">
										<div class="green-label" style="text-align: left;" id="destFacilityIdDT" name="destFacilityIdDT"></div>
							   		</div>
								</div>
							<#else>
							
							</#if>
						<#else>
							<#if requirement.facilityId?exists>
								<div class='row-fluid'>
									<div class='span5'>
										<span style="float: right">${uiLabelMap.Facility}</span>
									</div>
									<div class="span7">
										<div class="green-label" style="text-align: left;" id="facilityIdDT" name="facilityIdDT"></div>
							   		</div>
								</div>
							</#if>
						</#if>
						
						<#if requirementItemAssoc?has_content>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right">${uiLabelMap.BSFrom} ${uiLabelMap.WithdrawalRequirement}</span>
								</div>  
								<div class="span7">
									<div class="green-label">
										<#list requirementItemAssoc as requirement>
											<a href="viewDisRequirementDetail?requirementId=${(requirement.requirementId)?if_exists}" target="_blank">${(requirement.requirementId)?if_exists}</a>
										</#list>
									</div>
						   		</div>
							</div>
						</#if>
						
						<#if requirementItemAssocChild?has_content>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right">${uiLabelMap.BSTo} ${uiLabelMap.RequestRemoveProduct}</span>
							</div>  
							<div class="span7">
								<div class="green-label">
								<#list requirementItemAssocChild as requirement>
									<a href="viewRemoveRequirementDetail?requirementId=${(requirement.toRequirementId)?if_exists}" target="_blank">${(requirement.toRequirementId)?if_exists}</a>
								</#list>
								</div>
							</div>
						</div>
						</#if>
						
						<#if returnRequirementCommitmentChild?has_content>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right">${uiLabelMap.BSTo} ${uiLabelMap.DAReturnOrder}</span>
								</div>  
								<div class="span7">
									<div class="green-label">
									<#list returnRequirementCommitmentChild as requirement>
										<a href="CustomerReturnDetailForSup?returnId=${(requirement.returnId)?if_exists}" target="_blank">${(requirement.returnId)?if_exists}</a>
									</#list>
									</div>
								</div>
							</div>
						</#if>
						
					</div>
				<div class='row-fluid' style="margin-bottom: -10px !important">
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
			    ${uiLabelMap.ListProduct}
			</h4>
			<div id = "listRequirementItem"></div>
		</div>
	</div>
</div>
	
<div id="selectFacilityWindow" class="hide popup-bound">
	<#if requirement.requirementTypeId == "EXPORT_REQUIREMENT">
		<div>${uiLabelMap.SelectFacilityToExport}</div>
	<#else>
		<div>${uiLabelMap.SelectFacilityToReceive}</div>
	</#if>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="defaultOrderId" value=""/>
			<div id="notifyNotFacEnough"></div>
			<div class="row-fluid margin-top20">
	    		<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.Facility}</div>
					</div>
					<div class="span7">	
						<div id="requiredFacilityId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.Address}</div>
					</div>
					<div class="span7">	
						<div id="requiredContactMechId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="selectFacilityCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="selectFacilitySave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="selectFacilityTransferWindow" class="hide popup-bound">
	<div>${uiLabelMap.CreateTransfer}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
		        ${uiLabelMap.GeneralInfo}
		    </h4>
			<div class='row-fluid'>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferOriginFacilityId" name="transferOriginFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferOriginContactMechId" name="transferOriginContactMechId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferDestFacilityId" name="transferDestFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferDestContactMechId" name="transferDestContactMechId"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferType}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferTypeId" name="transferTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.NeedsReservesInventory}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="needsReservesInventory" name="needsReservesInventory"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.MaySplit}</span>
						</div>
						<div class="span7">
							<div id="maySplit" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span>${uiLabelMap.Priority}</span>
						</div>
						<div class="span7">
							<div id="priority" class="green-label"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferDate}</span>
						</div>
						<div class="span7">
							<div id="transferDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span>${uiLabelMap.ShippingAbout}</span>
						</div>
						<div class="span7">
							<div class="row-fluid" style="margin-bottom: -10px !important">
								<div class="span5">
									<div id="shipAfterDate" class="green-label"></div>
								</div>
								<div class="span5">
									<div id="shipBeforeDate" class="green-label"></div>
								</div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.CarrierParty}</span>
						</div>
						<div class="span7">
							<div id="carrierPartyId" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.ShipmentMethod}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferShipmentMethodTypeId"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top10">
	    		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
				</h4>
				<div style="margin-left: 20px;"><div id="listRequirementItemPopup"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="selectFacilityTransferCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="selectFacilityTransferSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>