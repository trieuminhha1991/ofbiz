<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
		<#if agreement?exists>
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAgreementId}:</label>
							<div class="controls-desc">
								<b>${agreement.agreementId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAProductId}:</label>
							<div class="controls-desc">
								${agreement.productId?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyFrom}:</label>
							<div class="controls-desc">
								${agreement.partyIdFrom?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyTo}:</label>
							<div class="controls-desc">
								${agreement.partyIdTo?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DARoleTypeIdFrom}:</label>
							<div class="controls-desc">
								${agreement.roleTypeIdFrom?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DARoleTypeIdTo}:</label>
							<div class="controls-desc">
								${agreement.roleTypeIdTo?if_exists}
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAgreementTypeId}:</label>
							<div class="controls-desc">
								<#assign agreementType = agreement.getRelatedOne("AgreementType", false)/>
								${agreementType.description?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFromDate}:</label>
							<div class="controls-desc">
								<#if agreement.fromDate?exists>${agreement.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.AccountingThruDate}:</label>
							<div class="controls-desc">
								<#if agreement.thruDate?exists>${agreement.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.AccountingAgreementDate}:</label>
							<div class="controls-desc">
								<#if agreement.agreementDate?exists>${agreement.agreementDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.CommonDescription}:</label>
							<div class="controls-desc">
								${agreement.description?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.AccountingTextData}:</label>
							<div class="controls-desc">
								${agreement.textData?if_exists}
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-->
			</div><!--.form-horizontal-->
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DATermsInAgreement}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div id="list-agreement-term">
				<#if agreementTerms?exists && agreementTerms?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAAgreementTermId}</th>
								<th class="center">${uiLabelMap.AccountingAgreementItemSeqId}</th>
								<th class="center">${uiLabelMap.AccountingTermTypeId}</th>
								<th class="center">${uiLabelMap.DAInvoiceItemType}</th>
								<th class="center">${uiLabelMap.DAFromDate}</th>
								<th class="center">${uiLabelMap.DAThruDate}</th>
								<th class="center">${uiLabelMap.DATermValue}</th>
								<th class="center">${uiLabelMap.DATermDays}</th>
								<th class="center">${uiLabelMap.DATextValue}</th>
								<th class="center">${uiLabelMap.DAMinQuantity}</th>
								<th class="center">${uiLabelMap.DAMaxQuantity}</th>
								<th class="center">${uiLabelMap.DADescription}</th>
							</tr>
						</thead>
						<tbody>
						<#list agreementTerms as agreementTerm>
							<tr>
								<td>${agreementTerm_index + 1}</td>
								<td>${agreementTerm.agreementTermId?if_exists}</td>
								<td>${agreementTerm.agreementItemSeqId?if_exists}</td>
								<td>
									<#assign termType = agreementTerm.getRelatedOne("TermType", false)!>
									${termType.description?if_exists}
								</td>
								<td>
									<#assign invoiceItemType = agreementTerm.getRelatedOne("InvoiceItemType", false)!>
									${invoiceItemType.description?if_exists}
								</td>
								<td>
									<#if agreementTerm.fromDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreementTerm.fromDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
									</#if>
								</td>
								<td>
									<#if agreementTerm.thruDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreementTerm.thruDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
									</#if>
								</td>
								<td>${agreementTerm.termValue?if_exists}</td>
								<td>${agreementTerm.termDays?if_exists}</td>
								<td>${agreementTerm.textValue?if_exists}</td>
								<td>${agreementTerm.minQuantity?if_exists}</td>
								<td>${agreementTerm.maxQuantity?if_exists}</td>
								<td>${agreementTerm.description?if_exists}</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			<!--END LIST 1-->
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAItemsInAgreement}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div id="list-agreement-item">
				<#if agreementItemsMapList?exists && agreementItemsMapList?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAAgreementItemSeqId}</th>
								<th class="center">${uiLabelMap.DAAgreementItemTypeId}</th>
								<th class="center">${uiLabelMap.DACurrencyUomId}</th>
								<th class="center">${uiLabelMap.DAAgreementText}</th>
								<th class="center">${uiLabelMap.DAAgreementImage}</th>
							</tr>
						</thead>
						<tbody>
						<#list agreementItemsMapList as agreementItemMap>
							<#assign agreementItem = agreementItemMap.agreementItem>
							<#if agreementItem?exists>
								<tr>
									<td>${agreementItemMap_index + 1}</td>
									<td>${agreementItem.agreementItemSeqId?if_exists}</td>
									<td>
										<#assign agreementItemType = agreementItem.getRelatedOne("AgreementItemType", false)!>
										${agreementItemType.description?if_exists}
									</td>
									<td>${agreementItem.currencyUomId?if_exists}</td>
									<td>${agreementItem.agreementText?if_exists}</td>
									<td>${agreementItem.agreementImage?if_exists}</td>
									<#--
									<td>${agreementItemMap_index + 1}</td>
									<td>${agreementItemMap.agreementTermId?if_exists}</td>
									<td>${agreementItemMap.agreementItemSeqId?if_exists}</td>
									<td>
										<#assign termType = agreementTerm.getRelatedOne("TermType", false)!>
										${termType.description?if_exists}
									</td>
									<td>
										<#assign invoiceItemType = agreementTerm.getRelatedOne("InvoiceItemType", false)!>
										${invoiceItemType.description?if_exists}
									</td>
									<td>
										<#if agreementTerm.fromDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreementTerm.fromDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
										</#if>
									</td>
									<td>
										<#if agreementTerm.thruDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreementTerm.thruDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
										</#if>
									</td>
									<td>${agreementTerm.termValue?if_exists}</td>
									<td>${agreementTerm.termDays?if_exists}</td>
									<td>${agreementTerm.textValue?if_exists}</td>
									<td>${agreementTerm.minQuantity?if_exists}</td>
									<td>${agreementTerm.maxQuantity?if_exists}</td>
									<td>${agreementTerm.description?if_exists}</td>
									-->
								</tr>
								<#assign agreementPromo = agreementItemMap.agreementPromo>
								<#if agreementPromo?exists && (agreementPromo?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementPromo as agreementPromoItem>
												<tr>
													<#assign promoItem = delegator.findOne("ProductPromo", {"productPromoId" : agreementPromoItem.productPromoId}, false)>
													<#if agreementPromoItem_index == 0><td rowspan="${agreementPromo?size}" class="width10pc">${uiLabelMap.DAAgreementPromo}</td></#if>
													<td><span title="${uiLabelMap.DAProductPromoId}">${agreementPromoItem.productPromoId?if_exists}</span></td>
													<td><span title="">${promoItem.promoName?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAFromDate}">${agreementPromoItem.fromDate?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAThruDate}">${agreementPromoItem.thruDate?if_exists}</span></td>
													<td><span title="${uiLabelMap.DASequenceNum}">${agreementPromoItem.sequenceNum?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
								<#assign agreementTerm = agreementItemMap.agreementTerm>
								<#if agreementTerm?exists && (agreementTerm?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementTerm as agreementTermItem>
												<tr>
													<#if agreementTermItem_index == 0><td rowspan="${agreementTerm?size}" class="width10pc">${uiLabelMap.DATerm}</td></#if>
													<td><span title="${uiLabelMap.DAAgreementTermId}">${agreementTermItem.agreementTermId?if_exists}</span></td>
													<td>
														<#assign agreementTermItemType = agreementTermItem.getRelatedOne("TermType", false)!>
														<span title="${uiLabelMap.DATermTypeId}">${agreementTermItemType.description?if_exists}</span>
													</td>
													<td>
														<#assign invoiceItemType = agreementTermItem.getRelatedOne("InvoiceItemType", false)!>
														<span title="${uiLabelMap.DAInvoiceItemTypeId}">${invoiceItemType.description?if_exists}</span>
													</td>
													<td><span title="${uiLabelMap.DAFromDate}">${agreementTermItem.fromDate?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAThruDate}">${agreementTermItem.thruDate?if_exists}</span></td>
													<td><span title="${uiLabelMap.DATermValue}">${agreementTermItem.termValue?if_exists}</span></td>
													<td><span title="${uiLabelMap.DATermDays}">${agreementTermItem.termDays?if_exists}</span></td>
													<td><span title="${uiLabelMap.DATextValue}">${agreementTermItem.textValue?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAMinQuantity}">${agreementTermItem.minQuantity?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAMaxQuantity}">${agreementTermItem.maxQuantity?if_exists}</span></td>
													<td><span title="${uiLabelMap.DADescription}">${agreementTermItem.description?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
								<#assign agreementProduct = agreementItemMap.agreementProduct>
								<#if agreementProduct?exists && (agreementProduct?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementProduct as agreementProductItem>
												<tr>
													<#if agreementProductItem_index == 0><td rowspan="${agreementProduct?size}" class="width10pc">${uiLabelMap.DAProduct}</td></#if>
													<td><span title="${uiLabelMap.DAProductId}">${agreementProductItem.productId?if_exists}</span></td>
													<td><span title="${uiLabelMap.DAPrice}">${agreementProductItem.price?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
								<#assign agreementParty = agreementItemMap.agreementParty>
								<#if agreementParty?exists && (agreementParty?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementParty as agreementPartyItem>
												<tr>
													<#if agreementPartyItem_index == 0><td rowspan="${agreementParty?size}" class="width10pc">${uiLabelMap.DAParty}</td></#if>
													<td><span title="${uiLabelMap.DAPartyId}">${agreementPartyItem.partyId?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
								<#assign agreementGeo = agreementItemMap.agreementGeo>
								<#if agreementGeo?exists && (agreementGeo?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementGeo as agreementGeoItem>
												<tr>
													<#if agreementGeoItem_index == 0><td rowspan="${agreementGeo?size}" class="width10pc">${uiLabelMap.DAGeo}</td></#if>
													<td><span title="${uiLabelMap.DAGeoId}">${agreementGeoItem.geoId?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
								<#assign agreementFacility = agreementItemMap.agreementFacility>
								<#if agreementFacility?exists && (agreementFacility?size > 0)>
									<tr>
										<td><i class="fa-arrow-up open-sans"></i></td>
										<td colspan="5">
											<table class="table-bordered" style="width: 100%">
											<#list agreementFacility as agreementFacilityItem>
												<tr>
													<#if agreementFacilityItem_index == 0><td rowspan="${agreementFacility?size}" class="width10pc">${uiLabelMap.DAFacility}</td></#if>
													<td><span title="${uiLabelMap.DAFacilityId}">${agreementFacilityItem.facilityId?if_exists}</span></td>
												</tr>
											</#list>
											</table>
										</td>
									</tr>
								</#if>
							</#if>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			<!--END LIST 2-->
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAListWorkEffortInAgreement}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div id="list-work-effort">
				<#if agreementWorkEffortApplics?exists && agreementWorkEffortApplics?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAAgreementItemSeqId}</th>
								<th class="center">${uiLabelMap.DAWorkEffortId}</th>
							</tr>
						</thead>
						<tbody>
						<#list agreementWorkEffortApplics as agreementWorkEffortItem>
							<tr>
								<td>${agreementWorkEffortItem_index + 1}</td>
								<td>${agreementWorkEffortItem.agreementItemSeqId?if_exists}</td>
								<td>${agreementWorkEffortItem.workEffortId?if_exists}</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			<!--END LIST 3-->
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAListRoleInAgreement}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div id="list-agreement-role">
				<#if agreementRoles?exists && agreementRoles?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAAgreementId}</th>
								<th class="center">${uiLabelMap.DAPartyId}</th>
								<th class="center">${uiLabelMap.DARoleType}</th>
							</tr>
						</thead>
						<tbody>
						<#list agreementRoles as agreementRoleItem>
							<tr>
								<td>${agreementRoleItem_index + 1}</td>
								<td>${agreementRoleItem.agreementId?if_exists}</td>
								<td>${agreementRoleItem.partyId?if_exists}</td>
								<td>
									<#assign agreementRoleItemType = agreementRoleItem.getRelatedOne("RoleType", false)!>
									${agreementRoleItemType.description?if_exists}
								</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
		</#if>
		</div><!-- .row-fluid -->
	</div>
</div>