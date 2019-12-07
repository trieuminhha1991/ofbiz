<div class="row-fluid">
	<script type="text/javascript">
		$(document).ready(function(){
			var list = $("form[name='ListDeliveryItems'] input[name^='actualQuantity']");
			for(var x = 0; x < list.length; x++){
				(function(x){
					var obj = $(list[x]);
					var name = obj.attr("name");
					var tmp = {};
					tmp[name] = {
						required: "<span style='color:red;'>${uiLabelMap.Required}</span>",
						validateId: "<span style='color:red;'>${uiLabelMap.NoWhiteSpaceNotify}</span>"
					};
					$('#ListDeliveryItems').validate({
						errorElement: 'span',
						errorClass: 'help-inline',
						focusInvalid: false,
						rules: {
							actualQuantity_o_0: {
								required: true,
								number: true
							},
						},
				
						messages: tmp,
				
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
				
						submitHandler: function (form) {
							form.submit();
						},
						invalidHandler: function (form) {
						}
					});
				})(x);
			}
			$("button[name='deliveredButton']").click(function(){
				$("input[name^='newStatusId']").val("DLV_DELIVERED");
				$("form[name='ListDeliveryItems']").attr("action", "deliveredDelivery");
			});
		});
	</script>
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-body">
            	<div style="text-align: center;">
            		<h4><b>${uiLabelMap.DeliveryNote}</b></h4>
            		
            		${uiLabelMap.deliveryDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(delivery.deliveryDate, "dd/MM/yyyy", locale, timeZone)!}
            	</div>
            	</br>
            	<div style="text-align: left;">
            		<div style="text-align: left; margin-left: 3%">
	            		<#assign address = delegator.findOne("PostalAddress", {"contactMechId" : delivery.destContactMechId}, true) />
	            		<#assign facility = delegator.findOne("Facility", {"facilityId" : delivery.originFacilityId}, true) />
	            		<#assign status = delegator.findOne("StatusItem", {"statusId" : delivery.statusId}, true) />
	            		<#assign customer = delegator.findOne("PartyNameView", {"partyId" : delivery.partyIdTo}, true) />
	            		<table>
		            		<tr>
			            		<td>${uiLabelMap.Receiver}:</td>
			            		<td style="width: 10px"></td>
			            		<td></td>
		            		</tr>
		            		<tr>
			            		<td>${uiLabelMap.Unit}:</td>
			            		<td style="width: 10px"></td>
			            		<td>${customer.get("groupName",locale)}</td>
		            		</tr>
		            		<tr>
			            		<td>${uiLabelMap.Address}:</td>
			            		<td style="width: 10px"></td>
			            		<td>${address.address1}</td>
		            		</tr>
		            		<tr>
			            		<td>${uiLabelMap.DeliveryReason}:</td>
			            		<td style="width: 10px"></td>
			            		<td>${delivery.deliveryReason?if_exists}</td>
		            		</tr>
		            		<tr>
			            		<td>${uiLabelMap.DeliveryFromFacility}:</td>
			            		<td style="width: 10px"></td>
			            		<td>${facility.facilityName}</td>
		            		</tr>
		            		<tr>
			            		<td>${uiLabelMap.Status}:</td>
			            		<td style="width: 10px"></td>
			            		<td>${status.get("description",locale)}</td>
		            		</tr>
	            		</table>
            		</div>
            	<div>
                <div class="widget-main padding-12 no-padding-left no-padding-right">
                	<form method="post" id="ListDeliveryItems" name="ListDeliveryItems" action="<@ofbizUrl>updateDeliveryItem</@ofbizUrl>">
	                	<table id="sale-forecast" class="table table-striped table-bordered table-hover">
			            	<thead>
			            		<tr class="sf-product">
			            			<td>${uiLabelMap.SequenceId}</td>
									<td>${uiLabelMap.ProductName}</td>
									<td>${uiLabelMap.ProductId}</td>
									<td>${uiLabelMap.QuantityUomId}</td>
									<td>${uiLabelMap.Quantity}</td>
									<td>${uiLabelMap.ActualReceiveQuantity}</td>
			            		</tr>
			            	</thead>
			            	<tbody>
		            			<#if listItems?has_content>
									<#assign rowCount = 0 />
									<#list listItems as item>
										<#assign orderItem = delegator.findOne("OrderItem", {"orderId" : item.fromOrderId, "orderItemSeqId" : item.fromOrderItemSeqId}, true) />
										<#assign product = delegator.findOne("Product", {"productId" : orderItem.productId}, true) />
										<tr>	
											<td>${rowCount + 1}</td>
											<input name="deliveryId_o_${rowCount}" type="hidden" value="${item.deliveryId}"/>
											<input name="deliveryItemSeqId_o_${rowCount}" type="hidden" value="${item.deliveryItemSeqId}"/>
		            			 			<td>${product.productName?if_exists}</td>
		            			 			<td>${orderItem.productId?if_exists}</td>
		            			 			<td>${orderItem.quantityUomId?if_exists}</td>
		            			 			<td>${orderItem.quantity?if_exists}</td>
		            			 			<input name="newStatusId_o_${rowCount}" type="hidden" value=""/>
		            			 			<#if delivery.statusId != 'DLV_DELIVERED'>
		            			 				<td><input name="actualQuantity_o_${rowCount}" type="text" value="${item.actualQuantity}" /><span style='color:red;'>*</span></td>
		            			 			<#else>
		            			 				<td>${item.actualQuantity}</td>
											</#if>
										</tr>			
									<#assign rowCount=rowCount + 1/>
									</#list>
								</#if>
			            	</tbody>
		            	</table>
		            	<#if delivery.statusId != 'DLV_DELIVERED'> 
					 		<button name="updateButton" type="submit" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate}</button>
					 		<button name="deliveredButton" type="submit" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.Delivered}</button>
	            		</#if>
	            	</form>
				</div>
				<div style="text-align: right; margin-right: 5%">
					${uiLabelMap.createDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(delivery.createDate, "dd/MM/yyyy", locale, timeZone)!}
				</div>
				</br>
				<div style="margin-left: 5%">
					<table style="width: 95%">
					<tr>
						<td style="width: 20%">
							${uiLabelMap.Director}</br>
							(${uiLabelMap.Sign})
						</td> 
						<td style="width: 20%">
							${uiLabelMap.accAccountings}</br>
							(${uiLabelMap.Sign}) 
						</td>
						<td style="width: 20%">
							${uiLabelMap.StoreKeeper}</br>
							(${uiLabelMap.Sign})
						</td> 
						<td style="width: 20%">
							${uiLabelMap.Deliverer}</br>
							(${uiLabelMap.Sign})
						</td>
					 	<td style="width: 20%">
							${uiLabelMap.Receiver}</br>
							(${uiLabelMap.Sign})
						</td>
					</tr>
					</table> 
				</div>
			</div>
		</div>
	</div>
</div>