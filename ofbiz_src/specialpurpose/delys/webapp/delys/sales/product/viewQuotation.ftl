<style type="text/css">
	.contain-view-calendar {
		display: block;
	}
</style>
<#if security.hasPermission("DELYS_QUOTATION_APPROVE", session)>
	<#assign hasApproved = true>
<#else>
	<#assign hasApproved = false>
</#if>
<#if security.hasPermission("QUOTT_NBD_APPROVE", session)>
	<#assign has1stApproved = true>
<#else>
	<#assign has1stApproved = false>
</#if>
<#assign currentStatusId = quotationSelected.statusId?if_exists>
<#-- 
<div class="widget-header widget-header-blue widget-header-flat">
	<h4 class="lighter">${uiLabelMap.DAViewQuotation}: ${quotationSelected.quotationName?if_exists} 
	<#if quotationSelected.productQuotationId?exists>
			(<a href="<@ofbizUrl>viewQuotation?productQuotationId=${quotationSelected.productQuotationId}</@ofbizUrl>">${quotationSelected.productQuotationId}</a>)
		</h4>
		<span class="widget-toolbar none-content">
			<#if currentStatusId?exists && currentStatusId == "QUOTATION_CREATED">
				<a href="<@ofbizUrl>editQuotation?productQuotationId=${quotationSelected.productQuotationId?if_exists}</@ofbizUrl>">
					<i class="icon-pencil open-sans">${uiLabelMap.DAEditQuotation}</i>
				</a>
			</#if>
			<a href="<@ofbizUrl>newQuotation</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DANewQuotation}</i>
			</a>
		</span>
	<#else>
		</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>newQuotation</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DANewQuotation}</i>
			</a>
		</span>
	</#if>
</div>
-->
<div class="row-fluid">	
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAQuotationId}:</label>
							<div class="controls-desc">
								<b>${quotationSelected.productQuotationId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAQuotationName}:</label>
							<div class="controls-desc">
								${quotationSelected.quotationName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAStatus}:</label>
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
							</div>
						</div>
						<div class="control-group">
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign quotationStatuses = quotationSelected.getRelated("ProductQuotationStatus", null, null, false)>
									<#if quotationStatuses?has_content>
					                  	<#list quotationStatuses as quotationStatus>
						                    <#assign loopStatusItem = quotationStatus.getRelatedOne("StatusItem", false)>
						                    <#assign userlogin = quotationStatus.getRelatedOne("UserLogin", false)>
						                    <div>
						                      	${loopStatusItem.get("description",locale)} <#if quotationStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(quotationStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
						                      	&nbsp;
						                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${quotationStatus.statusUserLogin}]
						                      	<#if quotationStatus.statusId == "QUOTATION_CANCELLED">
						                      		&nbsp;
						                      		${uiLabelMap.DAReason} - ${quotationStatus.changeReason?if_exists}
						                      	</#if>
						                    </div>
					                  	</#list>
				                	</#if>
								</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.CommonDescription}:</label>
							<div class="controls-desc">
								${quotationSelected.description?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACurrencyUomId}:</label>
							<div class="controls-desc">
								${quotationSelected.currencyUomId?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFromDate}:</label>
							<div class="controls-desc">
								<#if quotationSelected.fromDate?exists>${quotationSelected.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAThroughDate}:</label>
							<div class="controls-desc">
								<#if quotationSelected.thruDate?exists>${quotationSelected.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DASalesChannel}:</label>
							<div class="controls-desc">
								<#if quotationSelected.salesChannel?exists>
									<#assign salesMethodChannel = delegator.findOne("Enumeration", {"enumId" : quotationSelected.salesChannel}, false)/>
									<#if salesMethodChannel?exists>
										${salesMethodChannel.get("description", locale)}
									</#if>
								</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyApply}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list roleTypesSelected as roleTypeSelected>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-user green"></i>
											<#if roleTypeSelected.description?exists>${roleTypeSelected.description}<#else>${roleTypeSelected.roleTypeId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</div>
						<#if listRoleTypeMarket?exists && listRoleTypeMarket?size &gt; 0>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbPartyApplyPriceToMarket}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list listRoleTypeMarket as roleTypeSelected>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-user green"></i>
											<#if roleTypeSelected.description?exists>${roleTypeSelected.get("description", locale)}<#else>${roleTypeSelected.roleTypeId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</div>
						</#if>
						<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyIdApply}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list listPartyIdApply as partyIdSelected>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-user green"></i>
											${partyIdSelected}
										</li>
									</#list>
								</ul>
							</div>
						</div>
						</#if>
					</div><!--.span6-->
				</div><!--.row-->
			</div>
			
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAProductListInQuotation}</b></h5>
				
			</div>
			<div style="clear:both"></div>
			
			<div id="list-product-price-rules">
				<#if listProductQuotationRuleData?exists && listProductQuotationRuleData?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th rowspan="2" style="width:10px">${uiLabelMap.DANo}</th>
								<th rowspan="2" class="center">${uiLabelMap.DAProduct}</th>
								<th rowspan="2" class="center" style="width:60px">${uiLabelMap.DAPacking}</th>
								<th rowspan="2" class="center" style="width:40px">${uiLabelMap.DAPackingPerTray}</th>
								<th colspan="2" class="center">${uiLabelMap.DAPriceToDistributor}</th>
								<th colspan="2" class="center">${uiLabelMap.DATheMarketPriceOfDistributor}</th>
								<th colspan="2" class="center">${uiLabelMap.DAPricesProposalForConsumer}</th>
							</tr>
							<tr>
								<th class="center" style="width:80px">${uiLabelMap.DABeforeVATPerPacking}</th>
								<th class="center" style="width:80px">${uiLabelMap.DABeforeVATPerTray}</th>
								
								<th class="center" style="width:80px">${uiLabelMap.DAAfterVATPerPacking}</th>
								<th class="center" style="width:80px">${uiLabelMap.DAAfterVATPerTray}</th>
								
								<th class="center" style="width:80px">${uiLabelMap.DAAfterVATPerPacking}</th>
								<th class="center" style="width:80px">${uiLabelMap.DAAfterVATPerTray}</th>
							</tr>
						</thead>
						<tbody>
						<#list listProductQuotationRuleData as quotationRule>
				        	<tr>
				        		<td>${quotationRule_index + 1}</td>
				        		<td>${quotationRule.productName?if_exists}</td>
				        		<td><#if quotationRule.productWeightStr?exists>${quotationRule.productWeightStr}</#if></td>
				        		<td><#if quotationRule.productQuantityPerTray?exists>${quotationRule.productQuantityPerTray}</#if></td>
				        		<td class="align-right">
				        			<#if quotationRule.priceToDistNormal?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToDistNormal isoCode=productQuotation.currencyUomId/>
				                	</#if>
				        		</td>
				        		<td class="align-right">
				        			<#if quotationRule.priceToDistPerTray?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToDistPerTray isoCode=productQuotation.currencyUomId/>
		            				</#if>
				        		</td>
				        		<td class="align-right">
				        			<#if quotationRule.priceToMarketNormal?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToMarketNormal isoCode=productQuotation.currencyUomId/>
		            				</#if>
				        		</td>
				        		<td class="align-right">
				        			<#if quotationRule.priceToMarketPerTray?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToMarketPerTray isoCode=productQuotation.currencyUomId/>
		            				</#if>
		            			</td>
		            			<td class="align-right">
		            				<#if quotationRule.priceToConsumerNormal?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToConsumerNormal isoCode=productQuotation.currencyUomId/>
			                		</#if>
		            			</td>
		            			<td class="align-right">
		            				<#if quotationRule.priceToConsumerPerTray?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToConsumerPerTray isoCode=productQuotation.currencyUomId/>
			                		</#if>
		            			</td>
				        	</tr>
						</#list>
						<#--
						<#list productPriceRules as productPriceRule>
							<#if productPriceRule.productId?exists>
								<#assign product = delegator.findOne("Product", {"productId": productPriceRule.productId}, true)>
								<#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)>
								<#assign productWeight = productContentWrapper.get("PRODUCT_WEIGHT")>
								<#assign productWeightUomId	= productContentWrapper.get("WEIGHT_UOM_ID")>
								<#if productWeight?exists && productWeight?has_content && productWeightUomId?exists && productWeightUomId?has_content>
									<#assign productWeightUom = delegator.findByAnd("UomAndType", {"uomId": "WT_g"}, null, false)>
									<#if productWeightUom?exists && productWeightUom?has_content>
										<#assign productWeightUomName = productWeightUom[0].abbreviation>
									<#else>
										<#assign productWeightUomName = "">
									</#if>
								<#else>
									<#assign productWeightUomName = "">
								</#if>
								<tr>
									<td>${productPriceRule_index + 1}</td>
									<td>[${productPriceRule.productId?if_exists}] - ${productContentWrapper.get("PRODUCT_NAME")?default(uiLabelMap.DANoName)}</td>
									<td style="width:60px">
										<#if productWeight?exists && productWeight?has_content && productWeight?trim != "">
											${productWeight?number?string("00.00")} (${productWeightUomName?if_exists})
										</#if>
									</td>
									<td></td>
									<td style="text-align:right">
										<#if productPriceRule.priceToDist?exists && productPriceRule.priceToDist?has_content && productPriceRule.priceToDist?trim != "">
											<@ofbizCurrency amount=productPriceRule.priceToDist isoCode=quotationSelected.currencyUomId/>
										</#if>
									</td>
									<td style="text-align:right"></td>
									<td style="text-align:right">
										<#if productPriceRule.priceToMarket?exists && productPriceRule.priceToMarket?has_content && productPriceRule.priceToMarket?trim != "">
											<@ofbizCurrency amount=productPriceRule.priceToMarket isoCode=quotationSelected.currencyUomId/>
										</#if>
									</td>
									<td style="text-align:right"></td>
									<td style="text-align:right">
										<#if productPriceRule.priceToConsumer?exists && productPriceRule.priceToConsumer?has_content && productPriceRule.priceToConsumer?trim != "">
											<@ofbizCurrency amount=productPriceRule.priceToConsumer isoCode=quotationSelected.currencyUomId/>
										</#if>
									</td>
									<td style="text-align:right"></td>
								</tr>
							</#if>
						</#list>
						-->
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoQuotationItemsToDisplay}</div>
				</#if>
			</div>
			<div style="text-align:right">
				<#if currentStatusId?exists && currentStatusId == "QUOTATION_CREATED">
					<#if has1stApproved>
						<span class="widget-toolbar none-content">
							<a class="btn btn-danger btn-mini" href="javascript:enterCancelQuotation();" 
			              		style="font-size:13px; padding:0 8px">
								<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
							<a class="btn btn-primary btn-mini" href="javascript:document.QuotationAccept.submit()" 
								style="font-size:13px; padding:0 8px">
								<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
			              	
							<form name="QuotationAccept" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="QUOTT_NBD_ACCEPTED">
			                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
				                <input type="hidden" name="productQuotationId" value="${quotationSelected.productQuotationId?if_exists}">
			              	</form>
							<form name="QuotationCancel" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="QUOTATION_CANCELLED">
			                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
				                <input type="hidden" name="productQuotationId" value="${quotationSelected.productQuotationId?if_exists}">
				                <input type="hidden" name="changeReason" id="changeReason" value="" />
			              	</form>
						</span>
					</#if>
				<#elseif currentStatusId?exists && currentStatusId == "QUOTT_NBD_ACCEPTED">
					<#if hasApproved>
						<span class="widget-toolbar none-content">
							<a class="btn btn-danger btn-mini" href="javascript:enterCancelQuotation();" 
			              		style="font-size:13px; padding:0 8px">
								<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
							<a class="btn btn-primary btn-mini" href="javascript:document.QuotationAccept.submit()" 
								style="font-size:13px; padding:0 8px">
								<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
			              	
							<form name="QuotationAccept" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="QUOTATION_ACCEPTED">
			                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
				                <input type="hidden" name="productQuotationId" value="${quotationSelected.productQuotationId?if_exists}">
			              	</form>
							<form name="QuotationCancel" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="QUOTATION_CANCELLED">
			                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
				                <input type="hidden" name="productQuotationId" value="${quotationSelected.productQuotationId?if_exists}">
				                <input type="hidden" name="changeReason" id="changeReason" value="" />
			              	</form>
						</span>
					</#if>
				</#if>
				<#if security.hasEntityPermission("QUOTATION", "_THRUDATE", session) && ("QUOTATION_CANCELLED" != quotationSelected.statusId)>
					<#--<#if !(quotationSelected.thruDate?exists && quotationSelected.thruDate?has_content)>-->
					<#if (quotationSelected.thruDate?exists && quotationSelected.thruDate &gt; nowTimestamp) || !(quotationSelected.thruDate?exists)>
						<span class="widget-toolbar none-content" style="width:50%">
							<form name="updateQuotationThruDate" id="updateQuotationThruDate" method="POST" action="<@ofbizUrl>updateQuotationThruDate</@ofbizUrl>" 
								class="form-horizontal basic-custom-form">
								<input type="hidden" name="productQuotationId" value="${quotationSelected.productQuotationId?if_exists}" />
								<div class="row-fluid">
									<div class="span12">
										<div class="control-group">
											<label class="control-label required">${uiLabelMap.DAThroughDate}</label>
											<div class="controls">
												<div id="contain-view-calendar">
													<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${quotationSelected.thruDate?if_exists}" event="" action="" className="" alert="" 
														title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
														timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
														classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
														pmSelected="" compositeType="" formName=""/>
													<button class="btn btn-primary btn-small" type="button" onclick="updateQuotationthruDate();" style="vertical-align: top;">${uiLabelMap.DAUpdate}</button>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</span>
					<#else>
						<span style="color:#D7432E">${uiLabelMap.DAThisQuotationHasExpired}</span>
					</#if>
				</#if>
			</div>
</div>	

<script type="text/javascript" src="/delys/images/js/bootbox.min.js">
</script>
<script type="text/javascript">
	/*
	function deleteQuotation(productQuotationId) {
		bootbox.confirm("${uiLabelMap.DAAreYouSureDelete}", function(result) {
			if(result) {
				$('#productQuotationId').val(productQuotationId);
				$('#deleteProductQuotation').submit();
			}
		});
	}
	*/
	function updateQuotationthruDate(){
		if(!$('#updateQuotationThruDate').valid()) {
			return false;
		} else {
			bootbox.confirm("${uiLabelMap.DAAreYouSureWantCreateThruDate}", function(result){
				if(result){
					document.getElementById("updateQuotationThruDate").submit();
				}
			});
		}
	}
	function enterCancelQuotation() {
		bootbox.prompt("<span style='font-size:13px; padding:0; margin: -10px; display:block; height:25px'>${uiLabelMap.DAReasonCancelQuotation}:</span>", function(result) {
			if(result === null) {
			} else {
				document.getElementById('changeReason').value = "" + result;
				document.QuotationCancel.submit();
			}
		});
	}
	
	$.validator.addMethod('validateToDay',function(value,element){
		var now = new Date();
		now.setHours(0,0,0,0);
		return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= now;
	},'Greather than today');
	
	$(function() {
		$('#updateQuotationThruDate').validate({
			errorElement: 'span',
			errorClass: 'help-inline',
			focusInvalid: false,
			rules: {
				thruDate_i18n: {
					validateToDay: true
				}
			},
	
			messages: {
				thruDate_i18n: {
					validateToDay: "${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}"
				}
			},
	
			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},
	
			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},
			unhighlight: function(element, errorClass) {
	    		var parentControls = $(element).closest(".controls");
	    		if (parentControls != undefined) {
	    			parentControls.find("ul.chzn-choices").css("border", "1px solid #64a6bc");
	    		}
	    	},
			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},
	
			errorPlacement: function (error, element) {
				<#--
				if(element.is(':checkbox') || element.is(':radio')) {
					var controls = element.closest('.controls');
					if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
					else error.insertAfter(element.nextAll('.lbl').eq(0));
				} 
				else if(element.is('.chzn-select')) {
					error.insertAfter(element.nextAll('[class*="chzn-container"]').eq(0));
				}
				else error.insertAfter(element);
				-->
				var parentControls = element.closest(".controls");
				if (parentControls != undefined) {
					error.appendTo(parentControls);
					parentControls.find("ul.chzn-choices").css("border", "1px solid #f09784");
				}
			},
			submitHandler: function (form) {
				if(!$('#updateQuotationThruDate').valid()) return false;
			},
			invalidHandler: function (form) {
			}
		});
	});
</script>
