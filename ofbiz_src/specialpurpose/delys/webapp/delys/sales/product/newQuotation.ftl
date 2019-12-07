<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid">
		<ul class="wizard-steps">
			<li data-target="#step1" style="min-width: 25%; max-width: 25%;" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.GeneralInformation}</span></li>
			<li data-target="#step2" style="min-width: 25%; max-width: 25%;"><span class="step">2</span> <span class="title">${uiLabelMap.DAAddProductIntoQuotation}</span></li>
		</ul>
	</div>
	
	<hr>

	<div class="step-content row-fluid position-relative">
		<form class="form-horizontal basic-custom-form" id="createQuotation" name="createQuotation" method="post" action="<@ofbizUrl>createQuotation</@ofbizUrl>" style="display: block;">
			<div class="step-pane active" id="step1">
				<div class="row">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="productQuotationId">${uiLabelMap.DAQuotationId}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="productQuotationId" id="productQuotationId" class="span12">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="quotationName">${uiLabelMap.DAQuotationName} <span style="color:red">*</span>:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="quotationName" id="quotationName" class="span12" value="${parameters.quotationName?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="description">${uiLabelMap.CommonDescription}:</label>
							<div class="controls" style="color:#000">
								<div class="span12">
									<textarea id="description" name="description" class="autosize-transition span12" value="${parameters.description?if_exists}"></textarea>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="salesChannel">${uiLabelMap.DASalesChannel} <span style="color:red">*</span>:</label>
							<div class="controls">
								<div class="span12">
									<select name="salesChannel" id="salesChannel" class="span12">
						              	<option value="SALES_GT_CHANNEL">${uiLabelMap.DAGTChannel}</option>
						              	<option value="SALES_MT_CHANNEL">${uiLabelMap.DAMTChannel}</option>
						            </select>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<#assign shoppingCart = Static['org.ofbiz.order.shoppingcart.ShoppingCartEvents'].getCartObject(request)>
							<#if shoppingCart?exists && shoppingCart?has_content>
								<#assign currencyUomId = shoppingCart.getCurrency()>
							</#if>
							<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrencyUomId} <span style="color:red">*</span>:</label>
							<div class="controls">
								<div class="span12">
									<select name="currencyUomId" id="currencyUomId">
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
									<select multiple="" class="chzn-select" name="partyRoleTypesApply" id="partyRoleTypesApply" data-placeholder="Choose a Country...">
										<option value="" />
										<#list roleTypes as roleType>
										<option value="${roleType.roleTypeId}" />${roleType.description}
										</#list>
									</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="fromDate">${uiLabelMap.DAFromDate}:</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" value="" event="" action="" className="" alert="" 
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
									<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="" event="" action="" className="" alert="" 
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
	 	
			<div class="step-pane" id="step2">
				${uiLabelMap.DALoading}
			</div><!--.step2-->
		</form>
	</div>
	
	<hr/>
			
	<div class="row-fluid wizard-actions">
		<button class="btn btn-prev btn-small"><i class="icon-arrow-left"></i> Prev</button>
		<button class="btn btn-success btn-next btn-small" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
	</div>
</div>

<script type="text/javascript">
$(function() {
	$('[data-rel=tooltip]').tooltip();
	
	$(".chzn-select").css('width','220px').chosen({allow_single_deselect:true , no_results_text: "No such state!"})
	.on('change', function(){
		$(this).closest('form').validate().element($(this));
	});
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		if(info.step == 1) {
			if(!$('#createQuotation').valid()) {
				return false;
			} else {
				document.getElementById("createQuotation").submit();
			}
		}
	}).on('finished', function(e) {
		document.getElementById("createQuotation").submit();
	});
	
	//documentation : http://docs.jquery.com/Plugins/Validation/validate
	$.mask.definitions['~']='[+-]';
	$('#phone').mask('(999) 999-9999');

	jQuery.validator.addMethod("phone", function (value, element) {
		return this.optional(element) || /^\(\d{3}\) \d{3}\-\d{4}( x\d{1,6})?$/.test(value);
	}, "Enter a valid phone number.");
	
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
	})
</script>