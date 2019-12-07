<#assign agreements = resultAgreement.agreementList/>
<div class="widget-body">
    <div class="widget-body-inner">
   		<div class="widget-main">
			<input type="hidden" name="removeSelected" value="false">
			<div style="overflow:auto; overflow-y:hidden">
				<table cellspacing="0" cellpadding="1" border="0" class="table table-striped table-bordered table-hover dataTable">
					<thead class="align-center">
						<tr>
							<th>${uiLabelMap.DANo}</th>
							<th class="center">${uiLabelMap.DAAgreementId}</th>
							<th class="center">${uiLabelMap.DAProductId}</th>
							<th class="center">${uiLabelMap.DAPartyFrom}</th>
							<th class="center">${uiLabelMap.DAPartyTo}</th>
							<th class="center">${uiLabelMap.DARoleTypeIdFrom}</th>
							<th class="center">${uiLabelMap.DARoleTypeIdTo}</th>
							<th class="center">${uiLabelMap.DAAgreementTypeId}</th>
							<th class="center">${uiLabelMap.DAFromDate}</th>
							<th>${uiLabelMap.AccountingThruDate}</th>
							<th>${uiLabelMap.AccountingAgreementDate}</th>
							<th>${uiLabelMap.AccountingTextData}</th>
							<th>${uiLabelMap.DADescription?if_exists}</th>
							<#--<th></th>-->
						</tr>
					</thead>
					<tbody>
					<#list agreements as agreement>
						<tr>
							<td>${agreement_index + 1}</td>
							<td>
								<a href="<@ofbizUrl>agreementOverview?agreementId=${agreement.agreementId?if_exists}</@ofbizUrl>">${agreement.agreementId?if_exists}</a>
							</td>
							<td>${agreement.productId?if_exists}</td>
							<td>${agreement.partyIdFrom?if_exists}</td>
							<td>${agreement.partyIdTo?if_exists}</td>
							<td>${agreement.roleTypeIdFrom?if_exists}</td>
							<td>${agreement.roleTypeIdTo?if_exists}</td>
							<td>
								<#assign agreementType = agreement.getRelatedOne("AgreementType", false)!/>
								${agreementType.description?if_exists}
							</td>
							<td>
								<#if agreement.fromDate?exists>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreement.fromDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
								</#if>
							</td>
							<td>
								<#if agreement.thruDate?exists>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreement.thruDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
								</#if>
							</td>
							<td>
								<#if agreement.agreementDate?exists>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreement.agreementDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
								</#if>
							</td>
							<td>${agreement.textData?if_exists}</td>
							<td>${agreement.description?if_exists}</td>
							<#--
							<td>
								<div class="btn-group">
									<button type="button" class="btn btn-mini btn-success" onclick="window.location.href='<@ofbizUrl>viewQuotation?productQuotationId=${agreement.agreementId?if_exists}</@ofbizUrl>';">
										<i class="icon-zoom-in bigger-120"></i>
									</button>
								</div>
							</td>
							-->
						</tr>
					</#list>
					</tbody>
				</table>
			</div>
		</div><!--.widget-main-->
	</div>
</div>