<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">${uiLabelMap.DAInputInfoOrder}</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAInputInfoOrder}" data-placement="bottom">1</span>
			</li>

			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAOrderConfirmation}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<a href="<@ofbizUrl>emptyCartDis</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAClearOrder}" data-placement="bottom" class="no-decoration"><i class="icon-trash icon-only" style="font-size:16pt"></i></a>
	</div>
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />

	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<#include "checkInitsDis.ftl"/>
		</div>
		<div class="step-pane" id="step2">
			<div class="row-fluid">
				<h4 class="header smaller lighter blue">${uiLabelMap.DAOrderConfirmation}</h4>
				<div>${uiLabelMap.DAProcessing} ... </div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid wizard-actions">
		<button class="btn btn-prev btn-small" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.DAPrev}
		</button>
		<button class="btn btn-success btn-next btn-small" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard" style="display:none">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
		<button class="btn btn-success btn-next btn-small" id="btnNextWizard2" style="display:inline-block">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/additional-methods.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/jquery.maskedinput.min.js"></script>
<script type="text/javascript">
	//"partyId=" + $("input[name='partyId']").val(),
	/*$("input[name='partyId']").on("blur lookupIdChange", function(){
		updateCheckoutArea();
	});*/
	
	function updateCheckoutArea() {
		if ($("#alterpopupWindow2").length > 0) {
			$("#alterpopupWindow2").jqxWindow('destroy');
		}
		var partyId = $("#partyId").val();
		var isGetPartyId = "true";
		if (partyId && !(/^\s*$/.test(partyId))) {
			var data = $("#initOrderEntry").serialize();
			data += "&partyId=" + partyId;
			data += "&preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&isGetPartyId=" + isGetPartyId;
			//quickCheckoutAjaxDis
			$.ajax({
				type: "POST", 
				url: "quickCheckoutAjaxSales",
				data: data, 
				beforeSend: function () {
					$("#checkoutInfoLoader").show();
				}, 
				success: function (data) {
					$("#checkoutInfo").html(data);
					$('#container').empty();
				}, 
				error: function() {
					//commit(false);
				},
				complete: function() {
			        $("#checkoutInfoLoader").hide();
			    }
			});
		}
	}
	
	function createCartAsync() {
		if (productQuantities.length > 0) {
			var strParam = "N";
			var countQuantity = 0;
			for (i = 0; i < productQuantities.length; i++) {
				var rowData = productQuantities[i];
	   			var productId = rowData.productId;
	   			var quantity = rowData.quantity;
	   			var quantityUomId = rowData.quantityUomId;
	   			var expireDate = rowData.expireDate;
	   			if (quantityUomId == undefined) {
	   				quantityUomId = "";
	   			}
	   			if (expireDate == undefined || /^\s*$/.test(expireDate)) {
	   				expireDate = "";
	   			} else {
	   				expireDate = (new Date(expireDate)).getTime();
	   			}
	   			countQuantity += quantity;
				strParam += "|OLBIUS|" + productId + "|SUIBLO|" + quantity + "|SUIBLO|" + quantityUomId + "|SUIBLO|" + expireDate;
			}
			if (countQuantity <= 0) {
				var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseProduct}!</span>";
				bootbox.dialog(message0, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
					}]
				);
				return false;
			} else {
				data = $("#initOrderEntry").serialize();
				data += "&partyId=" + $("#partyId").val();
				data += "&" + $("#checkoutInfoForm").serialize();
				if ($("#desiredDeliveryDate2").jqxDateTimeInput('getDate') != null) {
					data += "&desiredDeliveryDate=" + $("#desiredDeliveryDate2").jqxDateTimeInput('getDate').getTime();
				}
				data += "&strParam=" + strParam;
				$.ajax({
		            type: "POST",
		            url: "initSalesOrderEntryDis",
		            data: data,
		            beforeSend: function () {
						$("#info_loader").show();
					}, 
		            success: function (data) {
		            	if (data.thisRequestUri == "json") {
		            		var errorMessage = "";
					        if (data._ERROR_MESSAGE_LIST_ != null) {
					        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
					        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
					        	}
					        }
					        if (data._ERROR_MESSAGE_ != null) {
					        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
					        }
					        if (errorMessage != "") {
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        } else {
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
					        }
					        return false;
		            	} else {
		            		jQuery('#btnNextWizard').css("display", "inline-block");
		            		jQuery('#btnNextWizard2').css("display", "none");
		            		jQuery('#btnNextWizard').trigger("click");
		            		$("#step-title").html("${uiLabelMap.DAOrderConfirmation}");
		            		$("#step2").html(data);
		            		return true;
		            	}
		            },
		            error: function () {
		                //commit(false);
		            },
		            complete: function() {
				        $("#info_loader").hide();
				    }
		        });
			}
	        return true;
		} else {
			/* bootbox.alert("${uiLabelMap.DAYouNotYetChooseProduct}!");
			   bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
				  "label" : "OK",
				  "class" : "btn-small btn-primary",
			   }]); */
			var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseProduct}!</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
		}
	}
</script>
<script type="text/javascript">
	$(function() {
		$('[data-rel=tooltip]').tooltip();
		$(".wizard-steps li").click(function(e){
			var target = $(this).attr('data-target');
			var wiz = $('#fuelux-wizard').data('wizard');
			currentStep = wiz.currentStep;
			if (currentStep == 1) {
				return;
			} else if (currentStep == 2) {
				if (target == "#step1") {
				    $("#step-title").html("${uiLabelMap.DAInputInfoOrder}");
				}
			}
		});
		
		$("#btnNextWizard2").on("click", function(){
			$('#container').empty();
			if(!$('#initOrderEntry').jqxValidator('validate')) return false;
			createCartAsync();
		});
		
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
				/* $('#container').empty();
				   if(!$('#initOrderEntry').jqxValidator('validate')) return false;
				   var strValue = createCart();
				   if (strValue != true) {
					   return false;
				   } */
			} else if ((info.step == 2) && (info.direction == "previous")) {
				jQuery('#btnNextWizard').css("display", "none");
        		jQuery('#btnNextWizard2').css("display", "inline-block");
        		$("#step-title").html("${uiLabelMap.DAInputInfoOrder}");
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result) {
				if(result) {
					$("#btnPrevWizard").addClass("disabled");
					$("#btnNextWizard").addClass("disabled");
					window.location.href = "processOrderDis";
				}
			});
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
		
		$('#initOrderEntry').jqxValidator({
        	rules: [
        		{input: '#orderId', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
        		{input: '#currencyUomId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $('#currencyUomId').val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#salesmanId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#partyId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#desiredDeliveryDate2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $('#desiredDeliveryDate2').jqxDateTimeInput('getDate');
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#desiredDeliveryDate2', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						now.setHours(0,0,0,0);
		        		if(input.jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				}
        	]
        });
	})
</script>