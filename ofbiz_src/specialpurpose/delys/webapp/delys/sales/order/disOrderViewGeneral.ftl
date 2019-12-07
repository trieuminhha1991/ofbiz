<div id="orderoverview-tab" class="tab-pane active">
	<div><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.DASalesOrderFormTitle}<#--DAOrderFormTitle-->
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
					<div class="row margin_left_10 row-desc">
						<div class="span6">
							<div class="row-fluid margin-bottom10">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderId}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b><i>${orderHeader.orderId?if_exists}</i></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderName}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b>${orderHeader.orderName?if_exists}</b></span>
								</div>
							</div>
							<#--<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DACurrency}:</label>
								<div class="controls-desc">
									<span><b>${orderHeader.currencyUom?if_exists}</b></span>
								</div>
							</div>-->
							<div class="row-fluid margin-bottom10">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderDate}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b><#if orderHeader.orderDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DADesiredDeliveryDate}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b><#if desiredDeliveryDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DACustomer}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b>${displayPartyNameResult?if_exists}</b></span>
								</div>
							</div>
						</div><!-- .span6 -->
						<div class="span6">
							<div class="row-fluid margin-bottom10">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.OrderDestination}:</label>
								</div>
								<div class="span8 controls-desc"><b>
									<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
									<ul class="unstyled spaced" style="margin: 0 0 0 0">
									<#list orderContactMechValueMaps as orderContactMechValueMap>
							          	<#assign contactMech = orderContactMechValueMap.contactMech>
							          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
						              	<#-- <span>&nbsp;${contactMechPurpose.get("description",locale)}</span> -->
						              	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
							                <#assign postalAddress = orderContactMechValueMap.postalAddress>
							                <#if postalAddress?has_content>
							                	<li style="margin-bottom:0; margin-top:0">
													<#--<#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${postalAddress.toName}<br /></#if>
										            <#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${postalAddress.attnName}<br /></#if>
										            <#if postalAddress.address1?has_content>${postalAddress.address1}<br /></#if>
										            <#if postalAddress.address2?has_content>${postalAddress.address2}<br /></#if>
										            <#if postalAddress.city?has_content>${postalAddress.city}</#if>
										            <#if postalAddress.stateProvinceGeoId?has_content>&nbsp;
												      	<#assign stateProvince = postalAddress.getRelatedOne("StateProvinceGeo", true)>
											      		${stateProvince.abbreviation?default(stateProvince.geoId)}
										            </#if>
										            <#if postalAddress.postalCode?has_content>, ${postalAddress.postalCode?if_exists}</#if>
										            <#if postalAddress.countryGeoId?has_content><br />
												      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
												      	${country.get("geoName", locale)?default(country.geoId)}
											    	</#if>-->
											    	<#if postalAddress.toName?has_content>${postalAddress.toName}<#if postalAddress.attnName?has_content> (${postalAddress.attnName})</#if>.</#if>
										            <#if postalAddress.address1?has_content> ${postalAddress.address1}.</#if>
										            <#if postalAddress.address2?has_content> ${postalAddress.address2}.</#if>
										            <#if postalAddress.city?has_content> ${postalAddress.city}</#if>
										            <#if postalAddress.countryGeoId?has_content>, 
												      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
												      	${country.get("geoName", locale)?default(country.geoId)}
											    	</#if>
												</li>
							                </#if>
						                </#if>
					                </#list>
					                </ul></b>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.accReceivableToApplyTotal}:</label>
								</div>
								<div class="span8 controls-desc">
									<span><b style="color:#d6412b">
										<@ofbizCurrency amount=0 isoCode=currencyUomId rounding=0/>
										<#--
										<#if totalToApply?exists && totalToApply?has_content>
						                	<@ofbizCurrency amount=totalToApply isoCode=currencyUomId rounding=0/>
								        </#if>
										-->
								    </b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.DASUP}:</label>
								</div>
								<div class="span8 controls-desc">
									<#if listSup?exists>
										<span><b><#list listSup as supName>
											${supName}<#if supName_has_next>, </#if>
										</#list></b></span>
									</#if>
									<#--<span><b>${displaySUPsNameResult?if_exists}</b></span>-->
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.DASalesman}:</label>
								</div>
								<div class="span8 controls-desc">
									<#if listSalesman?exists>
										<span><b><#list listSalesman as salesmanName>
											${salesmanName}<#if salesmanName_has_next>, </#if>
										</#list></b></span>
									</#if>
								</div>
							</div>
						</div><!-- .span6 -->
					</div><!-- .row-fluid -->
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form" style="display: block;">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td rowspan="2" class="center">${uiLabelMap.DASeqId}</td>
								<td colspan="3" class="center">${uiLabelMap.DAProduct}</td>
								<td rowspan="2" style="width:10px">${uiLabelMap.DAPackingPerTray}</td>
								<td rowspan="2" style="width:20px">${uiLabelMap.DAQuantityUomId}</td>
								<td rowspan="2" align="center" class="center">${uiLabelMap.DAQuantity}</td>
								<td rowspan="2">${uiLabelMap.DASumTray}</td>
							  	<td rowspan="2" align="center" class="align-center" style="width:60px">${uiLabelMap.DAPriceBeforeVAT}</td>
							  	<td rowspan="2" align="center" class="align-center">${uiLabelMap.DAAdjustment}</td>
								<td rowspan="2" align="center" class="align-center">${uiLabelMap.DASubTotal} <br />${uiLabelMap.DAParenthesisBeforeVAT}</td>
								<#--<td rowspan="2" style="width:60px">${uiLabelMap.DAPriceAfterVAT}</td>
								<td colspan="2" class="color-red">${uiLabelMap.DAInvoicePrice}</td>-->
							</tr>
							<tr style="font-weight: bold;">
								<td colspan="2" class="align-center">${uiLabelMap.DAProductId} - ${uiLabelMap.DAProductName}</td>
								<#--<td>${uiLabelMap.DABarcode}</td>-->
								<td class="align-center">${uiLabelMap.DAAbbExpireDate}</td>
								<#--
								<td>${uiLabelMap.DAOrdered}</td>
								<td>${uiLabelMap.DAPromos}</td>
								<td>${uiLabelMap.DASum}</td>
								-->
								<#--
								<td class="color-red">${uiLabelMap.DAPrice}</td>
								<td class="color-red">${uiLabelMap.OrderSubTotal}</td>
								-->
							</tr>
						</thead>
						<tbody>
						<#--<#assign taxTotalOrderItems = 0/>
						<#assign subAmountExportOrder = 0.00/>
						<#assign subAmountExportInvoice = 0.00/>-->
						<#list listItemLine as itemLine>
	            			<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	            			
	            			<#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
		                    <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
		                    <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
	            			<tr>
	            				<#assign productId = itemLine.productId?if_exists/>
	            				<#assign product = itemLine.product?if_exists/>
								<td>${itemLine.seqId?if_exists}</td>
		                        <#if productId?exists && productId == "shoppingcart.CommentLine">
					                <td colspan="9" valign="top">
					                  	<div><b> &gt;&gt; ${itemLine.itemDescription?if_exists}</b></div>
					                </td>
		              			<#else>
		            				<td valign="top" colspan="2">
					                  	<div>
				                  		<#if itemLine.supplierProductId?has_content>
	                                        ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
	                                    <#elseif productId?exists>
	                                        <a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>">${productId}</a>  - ${itemLine.itemDescription?if_exists}
	                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
	                                            <br /><span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
	                                        </#if>
	                                    <#elseif itemLine.orderItemType?exists>
	                                        ${itemLine.orderItemType.description} - ${itemLine.itemDescription?if_exists}
	                                    <#else>
	                                        ${itemLine.itemDescription?if_exists}
	                                    </#if>
					                  	</div>
		               				</td>
		                			<#--<td>${itemLine.barcode?if_exists}</td>-->
		                			<td>
			                			<#if itemLine.expireDate?has_content>
			                				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(itemLine.expireDate, "dd/MM/yyyy", locale, timeZone)!}
		                				</#if>
		                			</td>
		                			<td class="align-center"><#-- Quy cach / khay -->
		                				${itemLine.packingPerTray?if_exists}
	                				</td>
		                			<td align="right" class="align-center" valign="top">
		                				${itemLine.quantityUomDescription?if_exists}
					                </td>
					                <td align="right" class="align-right" valign="top">
					                  	${itemLine.quantity?if_exists}
					                </td>
					                <td class="align-right"><#-- Tong so khay-->
						                <#if itemLine.sumTray?exists>
						                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.sumTray, "#0.00", locale)}
			        					</#if>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Unit price -->
					                  	<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Adjustment -->
										<@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/>
					                </td>
					                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
				                  		<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId rounding=0/>
					                </td>
		          				</#if>
							</tr>
							<#-- show info from workeffort -->
							<#-- show linked order lines -->
							<#-- show linked requirements -->
							<#-- show linked quote -->
							<#-- now show adjustment details per line item -->
							<#-- now show price info per line item -->
							<#-- now show survey information per line item -->
		                    <#-- display the ship estimated/before/after dates -->
		                    <#-- now show ship group info per line item -->
		                    <#-- now show inventory reservation info per line item -->
		                    <#--
		                    <#if orderItemShipGrpInvResList?exists && orderItemShipGrpInvResList?has_content>
		                        <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <span >${uiLabelMap.CommonInventory}</span>&nbsp;
		                                    <a class="btn btn-mini btn-primary" href="/facility/control/EditInventoryItem?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}${externalKeyParam}"
		                                       class="buttontext">${orderItemShipGrpInvRes.inventoryItemId}</a>
		                                    <span >${uiLabelMap.OrderShipGroup}</span>&nbsp;${orderItemShipGrpInvRes.shipGroupSeqId}
		                                </td>
		                                <td align="center">
		                                    ${orderItemShipGrpInvRes.quantity?string.number}&nbsp;
		                                </td>
		                                <td>
		                                    <#if (orderItemShipGrpInvRes.quantityNotAvailable?has_content && orderItemShipGrpInvRes.quantityNotAvailable > 0)>
		                                        <span style="color: red;">
		                                            [${orderItemShipGrpInvRes.quantityNotAvailable?string.number}&nbsp;${uiLabelMap.OrderBackOrdered}]
		                                        </span>
		                                        //<a href="<@ofbizUrl>balanceInventoryItems?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;orderId=${orderId}&amp;priorityOrderId=${orderId}&amp;priorityOrderItemSeqId=${orderItemShipGrpInvRes.orderItemSeqId}</@ofbizUrl>" class="buttontext" style="font-size: xx-small;">Raise Priority</a> 
		                                    </#if>
		                                    &nbsp;
		                                </td>
		                                <td colspan="1">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    <#-- now show planned shipment info per line item -->
		                    <#-- now show item issuances (shipment) per line item -->
		                    <#-- now show item issuances (inventory item) per line item -->
		                    <#-- now show shipment receipts per line item -->
						</#list>
						
						<#-- display tax prices sum -->
						<#list listTaxTotal as taxTotalItem>
							<tr>
								<td align="right" class="align-right" colspan="10">
									<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
								</td>
								<td class="align-right">
									<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
										(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
									<#elseif taxTotalItem.amount?exists>
										<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
									</#if>
								</td>
							</tr>
						</#list>
						
						<#list orderHeaderAdjustments as orderHeaderAdjustment>
			                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="10">
			                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
			                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
			                            <span >${adjustmentType.get("description", locale)}</span>
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                        	<#if (adjustmentAmount &lt; 0)>
	                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
			                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId rounding=0/>)
			                            <#else>
			                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=0/>
			                            </#if>
			                        </td>
			                    </tr>
			                </#if>
			            </#list>
						
						<#-- subtotal -->
	          			<tr>
	            			<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.DAOrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<#if (orderSubTotal &lt; 0)>
                            		<#assign orderSubTotalNegative = -orderSubTotal>
                            		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=0/>
                        		</#if>
	        				</td>
	          			</tr>
	          			
	          			<#-- other adjustments -->
			            <tr>
			              	<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.DATotalOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap">
			              		<#if (otherAdjAmount &lt; 0)>
                            		<#assign otherAdjAmountNegative = -otherAdjAmount>
									(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId rounding=0/>)
								<#else>
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=0/>
								</#if>
							</td>
			            </tr>
	          			
	          			<#-- tax adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				            	<#if (taxAmount &lt; 0)>
                            		<#assign taxAmountNegative = -taxAmount>
				            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId rounding=0/>)
				            	<#else>
				            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=0/>
				            	</#if>
				            </td>
			          	</tr>
	          			
	          			<#-- shipping adjustments -->
			          	<#--
			          	<tr>
				            <td align="right" colspan="9"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=0/></div></td>
			          	</tr>
			          	-->
			          	
			          	<#-- grand total -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.DATotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
				            <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
				            	<b><#if (grandTotal &lt; 0)>
                            		<#assign grandTotalNegative = -grandTotal>
				            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId rounding=0/>)
				            	<#else>
				            		<@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=0/>
				            	</#if></b>
				            </td>
			          	</tr>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>
<div style="display:none">
	${screens.render("component://delys/widget/sales/OrderScreens.xml#OrderPrintHtml")}
</div>
<script type="text/javascript" src="/delys/images/js/printarea/jquery.printarea.js"></script>
<script type="text/javascript">
	function removePaymentOrder(id) {
		var orderId = $('[name="orderId_' + id + '"]').val();
		var dataResourceId = $('[name="dataResourceId_' + id + '"]').val();
		var data = "orderId=" + orderId + "&dataResourceId=" + dataResourceId + "&userLoginId=${userLogin.userLoginId?if_exists}";
		$.ajax({
            type: "POST",                        
            url: "removePaymentOrderAjax",
            data: data,
            beforeSend: function () {
				$("#checkoutInfoLoader").show();
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
            	} else {
            		$("#display-payment-order").html(data);
            	}
            },
            error: function () {
                //commit(false);
            },
            complete: function() {
		        $("#checkoutInfoLoader").hide();
		    }
        });
	}
</script>
<script type="text/javascript">
	$(function() {
		$('#id-input-file-1 , #uploadedFile').ace_file_input({
			no_file:'No File ...',
			btn_choose:'Choose',
			btn_change:'Change',
			droppable:false,
			onchange:null,
			thumbnail:false //| true | large
			//whitelist:'gif|png|jpg|jpeg'
			//blacklist:'exe|php'
			//onchange:''
			//
		});
		$('#id-input-file-3').ace_file_input({
			style:'well',
			btn_choose:'Drop files here or click to choose',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			thumbnail:'small'
			//,icon_remove:null//set null, to hide remove/reset button
			/**,before_change:function(files, dropped) {
				//Check an example below
				//or examples/file-upload.html
				return true;
			}*/
			/**,before_remove : function() {
				return true;
			}*/
			,
			preview_error : function(filename, error_code) {
				//name of the file that failed
				//error_code values
				//1 = 'FILE_LOAD_FAILED',
				//2 = 'IMAGE_LOAD_FAILED',
				//3 = 'THUMBNAIL_FAILED'
				//alert(error_code);
			}
		
		}).on('change', function(){
			//console.log($(this).data('ace_input_files'));
			//console.log($(this).data('ace_input_method'));
		});
		
		//dynamically change allowed formats by changing before_change callback function
		$('#id-file-format').removeAttr('checked').on('change', function() {
			var before_change
			var btn_choose
			var no_icon
			if(this.checked) {
				btn_choose = "Drop images here or click to choose";
				no_icon = "icon-picture";
				before_change = function(files, dropped) {
					var allowed_files = [];
					for(var i = 0 ; i < files.length; i++) {
						var file = files[i];
						if(typeof file === "string") {
							//IE8 and browsers that don't support File Object
							if(! (/\.(jpe?g|png|gif|bmp)$/i).test(file) ) return false;
						}
						else {
							var type = $.trim(file.type);
							if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif|bmp)$/i).test(type) )
									|| ( type.length == 0 && ! (/\.(jpe?g|png|gif|bmp)$/i).test(file.name) )//for android's default browser which gives an empty string for file.type
								) continue;//not an image so don't keep this file
						}
						
						allowed_files.push(file);
					}
					if(allowed_files.length == 0) return false;
	
					return allowed_files;
				}
			}
			else {
				btn_choose = "Drop files here or click to choose";
				no_icon = "icon-cloud-upload";
				before_change = function(files, dropped) {
					return files;
				}
			}
			var file_input = $('#id-input-file-3');
			file_input.ace_file_input('update_settings', {'before_change':before_change, 'btn_choose': btn_choose, 'no_icon':no_icon})
			file_input.ace_file_input('reset_input');
		});
		
		$('#attachPaymentOrder').on('submit', function(e){
			var file = $('#uploadedFile')[0].files[0];
			$("#_uploadedFile_fileName").val(file.name);
			$("#_uploadedFile_contentType").val(file.type);
			$("#attachPaymentOrder").submit();
		});
		
		$('#print-order-btn').on('click', function() {
			if ($("#print-order-content").length != undefined && $("#print-order-content").length > 0) {
				$("#print-order-content").printArea({
					mode:"iframe",  //printable window is either iframe or browser popup: "iframe","popup"
					popHt: 500,   // popup window height
					popWd: 400,  // popup window width
					popX: 500,   // popup window screen X position
					popY: 600,  //popup window screen Y position
					popTitle: 'Print order', // popup window title element
					popClose: true,  // popup window close after printing: false,true
					strict: undefined // strict or looseTransitional html 4.01 document standard or undefined to not include at all only for popup option: undefined,true,false
				});
			}
		});
	})
</script>