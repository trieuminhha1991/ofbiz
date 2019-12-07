<#if security.hasPermission("DELYS_QUOTATION_UPDATE", session)>
	<#assign hasApproved = true>
<#else>
	<#assign hasApproved = false>
</#if>
<#assign currentStatusId = quotationSelected.statusId?if_exists>

<#--
<div class="widget-header widget-header-blue widget-header-flat">
	<h4 class="lighter">${uiLabelMap.DAEditQuotation}: ${quotationSelected.quotationName?if_exists} 
		<#if quotationSelected.productQuotationId?exists>
			(<a href="<@ofbizUrl>viewQuotation?productQuotationId=${quotationSelected.productQuotationId}</@ofbizUrl>">${quotationSelected.productQuotationId}</a>)
		</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>viewQuotation?productQuotationId=${quotationSelected.productQuotationId?if_exists}</@ofbizUrl>">
				<i class="icon-zoom-in open-sans">${uiLabelMap.DAViewQuotation}</i>
			</a>
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
<#--
<div class="widget-body">	 
	<div class="widget-main">
-->		
	<#if hasApproved && currentStatusId?exists && currentStatusId == "QUOTATION_CREATED">
		<div class="row-fluid">
			<div id="fuelux-wizard" class="row-fluid">
				<ul class="wizard-steps">
					<li data-target="#step1" style="min-width: 25%; max-width: 25%;" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.GeneralInformation}</span></li>
					<li data-target="#step2" style="min-width: 25%; max-width: 25%;"><span class="step">2</span> <span class="title">${uiLabelMap.DAAddProductIntoQuotation}</span></li>
				</ul>
			</div>
			
			<hr>
			<div class="step-content row-fluid position-relative">
				<form class="form-horizontal basic-custom-form" id="updateQuotation" name="updateQuotation" method="post" action="<@ofbizUrl>updateQuotation</@ofbizUrl>" style="display: block;">
					<div class="step-pane active" id="step1">
						<div class="row">
							<div class="span6">
								<div class="control-group">
									<label class="control-label" for="productQuotationId">${uiLabelMap.DAQuotationId}:</label>
									<div class="controls">
										<div class="span12">
											<input type="text" name="productQuotationId" id="productQuotationId" class="span12" readonly="true" value="${quotationSelected.productQuotationId?if_exists}">
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="quotationName">${uiLabelMap.DAQuotationName} <span style="color:red">*</span>:</label>
									<div class="controls">
										<div class="span12">
											<input type="text" name="quotationName" id="quotationName" class="span12" value="${quotationSelected.quotationName?if_exists}">
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="description">${uiLabelMap.CommonDescription}:</label>
									<div class="controls" style="color:#000">
										<div class="span12">
											<textarea id="description" name="description" class="autosize-transition span12">${quotationSelected.description?if_exists}</textarea>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="salesChannel">${uiLabelMap.DASalesChannel} <span style="color:red">*</span>:</label>
									<div class="controls">
										<div class="span12">
											<select name="salesChannel" id="salesChannel" disabled="disabled" style="background-color:#f5f5f5" class="span12">
								              	<option value="SALES_GT_CHANNEL">${uiLabelMap.DAGTChannel}</option>
								              	<option value="SALES_MT_CHANNEL">${uiLabelMap.DAMTChannel}</option>
								            </select>
										</div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class="control-group">
									<#if quotationSelected.currencyUomId?exists>
										<#assign currencyUomId = quotationSelected.currencyUomId>
									<#else>
										<#assign shoppingCart = Static['org.ofbiz.order.shoppingcart.ShoppingCartEvents'].getCartObject(request)>
										<#if shoppingCart?exists && shoppingCart?has_content>
											<#assign currencyUomId = shoppingCart.getCurrency()>
										</#if>
									</#if>
									<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrencyUomId} <span style="color:red">*</span>:</label>
									<div class="controls">
										<div class="span12">
											<select name="currencyUomId" name="currencyUomId" disabled="disabled" style="background-color:#f5f5f5">
								              	<option value=""></option>
								              	<#list currencies as currency>
								              	<option value="${currency.uomId}" <#if currencyUomId?default('') == currency.uomId>selected="selected"</#if>>
								              		${currency.uomId}
							              		</option>
								              	</#list>
								            </select>
										</div>
									</div>
								</div>
			
								<div class="control-group">
									<label class="control-label" for="partyRoleTypesApply">${uiLabelMap.DAPartyApply} <span style="color:red">*</span>:</label>
									<div class="controls" style="color:#000">
										<div class="span12">
											<select multiple="" disabled="disabled" class="chzn-select" name="partyRoleTypesApply" id="partyRoleTypesApply" data-placeholder="Choose a Country...">
												<option value="" />
												<#list roleTypes as roleType>
													<#list roleTypesSelected as roleTypeSelected>
														<#if roleType.roleTypeId == roleTypeSelected.roleTypeId>
															<option value="${roleType.roleTypeId}" selected />${roleType.description}
														<#else>
															<option value="${roleType.roleTypeId}" />${roleType.description}
														</#if>
													</#list>
												</#list>
											</select>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="fromDate">${uiLabelMap.DAFromDate}:</label>
									<div class="controls">
										<div class="span12">
											<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" value="${quotationSelected.fromDate?if_exists}" event="" action="" className="" alert="" 
												title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
												timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
												classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
												pmSelected="" compositeType="" formName=""/>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="thruDate">${uiLabelMap.DAThroughDate}:</label>
									<div class="controls">
										<div class="span12">
											<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${quotationSelected.thruDate?if_exists}" event="" action="" className="" alert="" 
												title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
												timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
												classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
												pmSelected="" compositeType="" formName=""/>
										</div>
									</div>
								</div>
							</div><!--.span6-->
						</div><!--.row-->
				    </div><!--.step1-->
				</form>
				<input type="hidden" id="old_quotationName" value="${quotationSelected.quotationName?if_exists}" />
				<input type="hidden" id="old_description" value="${quotationSelected.description?if_exists}" />
				<input type="hidden" id="old_fromDate" value="${quotationSelected.fromDate?if_exists}" />
				<input type="hidden" id="old_thruDate" value="${quotationSelected.thruDate?if_exists}" />
				<div class="step-pane" id="step2">
					<form class="form-horizontal basic-custom-form form-decrease-padding" id="createQuotationRule" name="createQuotationRule" method="post" action="<@ofbizUrl>createQuotationRule</@ofbizUrl>">
						<div class="row" id="addQuotationItemToList">
							<div class="span6">
								<div class="control-group">
									<label class="control-label" for="inputCategory">${uiLabelMap.DAInputCategoryVAT}:</label>
									<div class="controls">
										<div class="span12">
					                      	<select name="inputCategory" id="inputCategory">
								              	<option value="">${uiLabelMap.DABeforeVAT}</option>
								              	<#--<option value="">${uiLabelMap.DAAfterVAT}</option>-->
								            </select>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="productId">${uiLabelMap.DAProduct} <span style="color:red">*</span>:</label>
									<div class="controls">
										<div class="span12">
					                      	<@htmlTemplate.lookupField formName="createQuotationRule" name="productId" id="productId" 
					                      		fieldFormName="LookupProduct" className=""/>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="priceToDist">${uiLabelMap.DAPriceToDistributor} <span style="color:red">*</span>:</label>
									<div class="controls">
										<div class="span12">
											<input type="text" name="priceToDist" id="priceToDist" size="25" value="">
										</div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class="control-group">
									<label class="control-label" for="priceToMarket">${uiLabelMap.DATheMarketPriceOfDistributor}:</label>
									<div class="controls">
										<div class="span12">
											<input type="text" name="priceToMarket" id="priceToMarket" size="25" value="">
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="priceToConsumer">${uiLabelMap.DAPricesProposalForConsumer}:</label>
									<div class="controls">
										<div class="span12">
											<input type="text" name="priceToConsumer" id="priceToConsumer" size="25" value="">
										</div>
									</div>
								</div>
								
							</div>
							<#--
							Gia ban den NPP *:
							Gia xuat hoa don:
							Gia ban ra thi truong:
							Gia de nghi cho nguoi tieu dung:
							-->
							<div style="clear:both"></div>
							<div class="span12">
								<div class="control-group" style="margin:0 !important">
									<span style="color:#F00"><i>(${uiLabelMap.DAThisPriceApplyFor1Packing})</i></span>
									<div class="controls">
										<div style="display:inline-block; vertical-align:bottom; margin-left:60px">
											<button class="btn btn-primary btn-small" type="button" style="margin:0"
												onclick="javascript:addProductPriceRule();">
												<i class="icon-ok"></i>${uiLabelMap.DAAdd}
											</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
					
					<div style="clear:both"></div>
					<hr/>
					<h5 class="lighter block green"><b>${uiLabelMap.DAProductListInQuotation}</b></h5>
					
					<div id="list-product-price-rules">
						${screens.render('component://delys/widget/sales/ProductScreens.xml#ViewQuotationRuleAjax')}
					</div>
				</div><!--.step2-->
			</div>
			
			<hr/>
			<div class="row-fluid wizard-actions">
				<button class="btn btn-prev btn-small"><i class="icon-arrow-left"></i> Prev</button>
				<button class="btn btn-success btn-next btn-small" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
			</div>
		</div>
	<#else>
		<div class="alert alert-info">${uiLabelMap.DAQuotationUpdatePermissionError}</div>
	</#if>
<#--
	</div> //widget-main
</div> //widget-body
-->

<div id="loading" class="modal hide fade" tabindex="-1"></div>
<div id="updateQuotationItemId-backgroud" class="modal-backdrop hide fade in" 
	onclick="javascript:closeUpdateQuotationItemId();" style="z-index:990"></div>

<div id="updateQuotationItemId" class="modal modal-resize hide fade" tabindex="-1" style="z-index:1000">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal" onclick="javascript:closeUpdateQuotationItemId();">
				&times;
			</button>
			${uiLabelMap.DAUpdateProductQuotationItem}
		</div>
	</div>

	<div class="modal-body" id="updateQuotationItemId-body">
		<form class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotationRule" name="updateQuotationRule" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
			<input type="hidden" id="update_inputParamEnumId" name="update_inputParamEnumId" value="" />
			<input type="hidden" id="update_productPriceRuleId" name="update_productPriceRuleId" value="" />
			<input type="hidden" id="update_ppa_productPriceActionSeqId" name="update_ppa_productPriceActionSeqId" value="" />
			<input type="hidden" id="update_ppam_productPriceActionSeqId" name="update_ppam_productPriceActionSeqId" value="" />
			<input type="hidden" id="update_ppac_productPriceActionSeqId" name="update_ppac_productPriceActionSeqId" value="" />
			<input type="hidden" id="update_pq_ProductQuotationId" name="update_pq_ProductQuotationId" value="" />
			<div class="row-fluid" id="updateQuotationItemToList">
				<div class="span12">
					<div class="control-group" style="margin:0 !important; font-size:9pt;">
						<span style="color:#666"><i>(${uiLabelMap.DAThisPriceApplyFor1Packing})</i></span>
					</div>
						<div class="control-group">
							<label class="control-label" for="update_productId">${uiLabelMap.DAProduct} <span style="color:red">*</span>:</label>
							<div class="controls">
								<div class="span12">
			                      	<input type="text" name="update_productId" id="update_productId" readonly="readonly"/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="update_inputCategory">${uiLabelMap.DAInputCategoryVAT}:</label>
							<div class="controls">
								<div class="span12">
			                      	<select name="update_inputCategory" id="update_inputCategory">
						              	<option value="">${uiLabelMap.DABeforeVAT}</option>
						              	<#--<option value="">${uiLabelMap.DAAfterVAT}</option>-->
						            </select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="update_priceToDist">${uiLabelMap.DAPriceToDistributor} <span style="color:red">*</span>:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="update_priceToDist" id="update_priceToDist" size="25" value="">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="update_priceToMarket">${uiLabelMap.DATheMarketPriceOfDistributor}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="update_priceToMarket" id="update_priceToMarket" size="25" value="">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="update_priceToConsumer">${uiLabelMap.DAPricesProposalForConsumer}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="update_priceToConsumer" id="update_priceToConsumer" size="25" value="">
								</div>
							</div>
						</div>
				</div>
			</div>
		</form>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal" onclick="javascript:closeUpdateQuotationItemId();">
			<i class="icon-remove"></i>
			Close
		</button>
		<button class="btn btn-primary btn-small" type="button" style="margin:0"
			onclick="javascript:updateProductPriceRule();">
			<i class="icon-ok"></i>${uiLabelMap.DAUpdate}
		</button>
	</div>
</div>

<style type="text/css">
	#loading {
		background:transparent;
		border:0;
		webkit-box-shadow: 0 0 0 rgba(0,0,0,0);
		-moz-box-shadow: 0 0 0 rgba(0,0,0,0);
		box-shadow: 0 0 0 rgba(0,0,0,0);
	}
	.modal.fade {
		top:10%;
	}
</style>

<script type="text/javascript">
function updateProductQuotationItem(index) {
	$('#content-messages').remove();
	$('#updateQuotationItemId').slideDown(500);
	$('#updateQuotationItemId-backgroud').show();
	$('#updateQuotationItemId').css("opacity", "1", "important");
	$('#updateQuotationItemId').css("top", "10%");
    $('#update_priceToDist').focus();
	//$('#updateQuotationItemId').css("left", "70%");
	$('input[name="update_productId"]').val($('#hidden_productId_' + index).val());
	$('#update_priceToDist').val($('#hidden_priceToDist_' + index).val());
	$('#update_priceToMarket').val($('#hidden_priceToMarket_' + index).val());
	$('#update_priceToConsumer').val($('#hidden_priceToConsumer_' + index).val());
	$('#update_inputParamEnumId').val($('#hidden_inputParamEnumId_' + index).val());
	$('#update_productPriceRuleId').val($('#hidden_productPriceRuleId_' + index).val());
	$('#update_ppa_productPriceActionSeqId').val($('#hidden_ppa_productPriceActionSeqId_' + index).val());
	$('#update_ppam_productPriceActionSeqId').val($('#hidden_ppam_productPriceActionSeqId_' + index).val());
	$('#update_ppac_productPriceActionSeqId').val($('#hidden_ppac_productPriceActionSeqId_' + index).val());
	$('#update_pq_ProductQuotationId').val($('#hidden_pq_ProductQuotationId_' + index).val());
}

function closeUpdateQuotationItemId() {
	$('#updateQuotationItemId').slideUp(500);
	setTimeout(function(){
	    $('#updateQuotationItemId-backgroud').hide();
  	}, 600);
}

function updateProductPriceRule () {
	jQuery.ajax({
		url: 'updateQuotationRuleAjax',
		data: $("#updateQuotationRule").serialize(),
		type: 'post',
		async: false,
		success: function(data) {
			if (data.indexOf("OFBIZ_ERROR_MESSAGE_RENDER") >= 0) {
				$('#updateQuotationItemId-body').html(data);
			} else {
				$('#list-product-price-rules').html(data);
				closeUpdateQuotationItemId();
			}
		},
		error: function(data) {
			alert("Error during create quotation item");
		}
	});
}

function deleteProductPriceRule (index) {
	var param = "productQuotationId=" + $('#hidden_pq_ProductQuotationId_' + index).val() + "&productPriceRuleId=" + $('#hidden_productPriceRuleId_' + index).val();
	jQuery.ajax({
		url: 'deleteQuotationRuleAjax',
		data: param,
		type: 'post',
		async: false,
		success: function(data) {
			$('#list-product-price-rules').html(data);
		},
		error: function(data) {
			alert("Error during create quotation item");
		}
	});
}

function addProductPriceRule() {
	if(!$('#createQuotationRule').valid()) {
		return false;
	} else {
		$('#loading').show();
		$('#loading').css("opacity", "1", "important");
		$('#loading').css("top", "60%");
		$('#loading').css("left", "70%");
		$('#loading').html('<img src="/images/spinner.gif"> ${uiLabelMap.DALoading} ...');
		// $('#loading-background').show();
		// document.getElementById("createQuotation").submit();
		
		var param = 'productQuotationId=' + $('#productQuotationId').val() + '&inputCategory=' + $('#inputCategory').val() + '&productId=' + $('input[name="productId"]').val() + '&priceToDist='
					 + $('#priceToDist').val() + '&priceToMarket=' + $('#priceToMarket').val() + '&priceToConsumer=' + $('#priceToConsumer').val();
		jQuery.ajax({
			url: 'createQuotationRuleAjax',
			data: param,
			type: 'post',
			async: false,
			success: function(data) {
				$('#loading').hide();
				$('#loading').html("");
        		jQuery('#list-product-price-rules').html(data);
        		$('#priceToDist').val("");
        		$('#priceToMarket').val("");
        		$('#priceToConsumer').val("");
        		var objectProductId = $('input[name="productId"]');
        		objectProductId.val("");
        		objectProductId.css("background-color", "transparent");
        		objectProductId.focus();
			},
			error: function(data) {
				alert("Error during create quotation item");
			}
		});
		
	}
}

$(function() {
	<#--
	$('#addQuotationItemToList').live('keyup',function(e){
	     var p = e.which;
	     if(p==13){
	         addProductPriceRule();
	     }
 	});
 	-->
 	$('#updateQuotationItemId-body').live('keyup',function(e){
	     var p = e.which;
	     if(p==13){
	         updateProductPriceRule();
	     }
 	});
	$('#priceToDist').live('keyup',function(e){
	     var p = e.which;
	     if(p==13){
	         addProductPriceRule();
	     }
 	});
 	$('#priceToMarket').live('keyup',function(e){
	     var p = e.which;
	     if(p==13){
	         addProductPriceRule();
	     }
 	});
 	$('#priceToConsumer').live('keyup',function(e){
	     var p = e.which;
	     if(p==13){
	         addProductPriceRule();
	     }
 	});
 	
 	$('#priceToDist').live('keyup',function(e){});
	
	$('[data-rel=tooltip]').tooltip();
	
	$(".chzn-select").css('width','220px').chosen({allow_single_deselect:true , no_results_text: "No such state!"})
	.on('change', function(){
		$(this).closest('form').validate().element($(this));
	});
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		if(info.step == 1) {
			if(!$('#updateQuotation').valid()) {
				return false;
			} else {
				if(($('#old_quotationName').val() == $('#quotationName').val()) 
					&& ($('#old_description').val() == $('#description').val())
					&& ($('#old_fromDate').val() == $('#fromDate').val())
					&& ($('#old_thruDate').val() == $('#thruDate').val())) {
				} else {
					document.getElementById("updateQuotation").submit();
				}
			}
		}
	}).on('finished', function(e) {
		window.location.href = "<@ofbizUrl>viewQuotation?productQuotationId=${quotationSelected.productQuotationId?if_exists}</@ofbizUrl>";
	});
	
	//documentation : http://docs.jquery.com/Plugins/Validation/validate
	$.mask.definitions['~']='[+-]';
	$('#phone').mask('(999) 999-9999');

	jQuery.validator.addMethod("phone", function (value, element) {
		return this.optional(element) || /^\(\d{3}\) \d{3}\-\d{4}( x\d{1,6})?$/.test(value);
	}, "Enter a valid phone number.");
	
	$('#createQuotationRule').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			<#--priceToDist: {
				required: true
			},
			productId: {
				required: true
			}-->
		},

		messages: {
			<#--
			priceToDist: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			},
			productId: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}
			-->
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		errorPlacement: function (error, element) {
			if(element.is(':checkbox') || element.is(':radio')) {
				var controls = element.closest('.controls');
				if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
				else error.insertAfter(element.nextAll('.lbl').eq(0));
			} 
			else if(element.is('.chzn-select')) {
				error.insertAfter(element.nextAll('[class*="chzn-container"]').eq(0));
			}
			else error.insertAfter(element);
		},
		submitHandler: function (form) {
			if(!$('#createQuotation').valid()) return false;
		},
		invalidHandler: function (form) {
		}
	});
	
	$('#createQuotation').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			currencyUomId: {
				required: true
			},
			partyRoleTypesApply: {
				required: true
			},
			quotationName: {
				required: true
			}
		},

		messages: {
			currencyUomId: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			},
			partyRoleTypesApply: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			},
			quotationName: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		errorPlacement: function (error, element) {
			if(element.is(':checkbox') || element.is(':radio')) {
				var controls = element.closest('.controls');
				if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
				else error.insertAfter(element.nextAll('.lbl').eq(0));
			} 
			else if(element.is('.chzn-select')) {
				error.insertAfter(element.nextAll('[class*="chzn-container"]').eq(0));
			}
			else error.insertAfter(element);
		},
		submitHandler: function (form) {
			if(!$('#createQuotation').valid()) return false;
		},
		invalidHandler: function (form) {
		}
	});
});
</script>