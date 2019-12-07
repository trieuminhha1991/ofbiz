
<h3 style="text-align:center;font-weight:bold; text-transform: uppercase;">${cart.getOrderName()?default("${uiLabelMap.DAOrderFormTitle}")}</h3>

	<div class="form-horizontal desc" style="padding-top:10px; padding-bottom:10px">
		<div class="span12">
			<div class="row">
				<div class="span6">
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAOrderName}:</label>
						<div class="controls"><div class="span12">${cart.getOrderName()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAOrderId}:</label>
						<div class="controls"><div class="span12">${cart.getOrderId()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACurrency}:</label>
						<div class="controls"><div class="span12">${cart.getCurrency()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACreateOrderDate}:</label>
						<div class="controls"><div class="span12">${cart.getOrderDate()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DADesiredDeliveryDate}:</label>
						<div class="controls"><div class="span12">${cart.getShipBeforeDate()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAProductStore}:</label>
						<div class="controls"><div class="span12">${cart.getProductStoreId()?if_exists}&nbsp;</div></div>
					</div>
				</div><!-- .span6 -->
				
				<div class="span6">
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAPersonCreate}:</label>
						<div class="controls"><div class="span12">${parameters.userLogin.userLoginId}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DADistributor}:</label>
						<div class="controls"><div class="span12">${cart.getPartyId()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAAddressDistributor}:</label>
						<div class="controls"><div class="span12">...</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DADebt}:</label>
						<div class="controls"><div class="span12">...</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DASup}:</label>
						<div class="controls"><div class="span12">...</div>
						</div>
					</div>
				</div><!-- .span6 -->
			</div>
		</div>
	</div>


<div style="clear:both"></div>
<br />

<form method="post" action="/ordermgr/control/modifycart" name="cartform" style="margin: 0;">
	<input type="hidden" name="removeSelected" value="false">
	<div style="overflow:auto; overflow-y:hidden">
		<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered bold-head-first no-color-head">
			<thead>
				<tr>
					<td rowspan="2">${uiLabelMap.DANo}</td>
					<td rowspan="2">${uiLabelMap.DACategory}</td>
					<td colspan="3" style="text-align:center">${uiLabelMap.DAProduct}</td>
					<td rowspan="2" style="width:10px">${uiLabelMap.DAPackingPerTray}</td>
					<td align="center" colspan="3" style="text-align:center">${uiLabelMap.DAQuantity}</td>
					<td rowspan="2">${uiLabelMap.DASumTray}</td>
					<td align="right" class="align-right" rowspan="2">${uiLabelMap.DAPriceBeforeVAT}</td>
					<td align="right" class="align-right" rowspan="2">${uiLabelMap.DAAdjustment}</td>
					<td align="right" class="align-right" rowspan="2">${uiLabelMap.DASubTotalBeforeVAT}</td>
					<td rowspan="2">${uiLabelMap.DAPriceAfterVAT}</td>
					<td colspan="2" class="color-red">${uiLabelMap.DAInvoicePrice}</td>
				</tr>
				<tr>
					<td>${uiLabelMap.DAProductId}</td>
					<td>${uiLabelMap.DAName}</td>
					<td>${uiLabelMap.DABarcode}</td>
					
					<td>${uiLabelMap.DAOrdered}</td>
					<td>${uiLabelMap.DAPromos}</td>
					<td>${uiLabelMap.DASum}</td>
					
					<td class="color-red">${uiLabelMap.DAPrice}</td>
					<td class="color-red">${uiLabelMap.DASubTotal}</td>
				</tr>
			</thead>
			<tbody>
			<#assign orderItemIndex = 0>
			<#assign allTotalQuality = 0>
			<#assign allTotalKhay = 0>
			<#assign allTotalOrderSumExport = 0>
			<#list orderItems?if_exists as orderItem>
				<#assign orderItemIndex = orderItemIndex + 1>
				<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
				<tr valign="top">
					<td>
						${orderItemIndex}
					</td>
					<td>
						<#assign productCategoryMembers = delegator.findByAnd("ProductCategoryMember", {"productId" : orderItem.productId}, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum", "productCategoryId"), false) />
						<#list productCategoryMembers as productCategoryMember>
							<#assign categoryName = delegator.findOne("ProductCategory", {"productCategoryId" : productCategoryMember.productCategoryId}, false)>
							<#if categoryName?exists>
								${categoryName.categoryName?if_exists}
							</#if>
						</#list>					
					</td>
					
					<#if orderItem.productId?exists && orderItem.productId == "_?_">
						<td colspan="3" valign="top">
		                  <b><div> &gt;&gt; ${orderItem.itemDescription}</div></b>
		                </td>
					<#else>
						<#if orderItem.productId?exists>
							<td><a href="<@ofbizUrl>product?product_id=${orderItem.productId}</@ofbizUrl>">${orderItem.productId}</a></td>
							<td>${orderItem.itemDescription}</td>
						<#else>
							<td colspan="2"><b>${itemType?if_exists.description?if_exists}</b> : ${orderItem.itemDescription?if_exists}</td>
						</#if>
						
						<#-- ma vach -->
						<td>&nbsp;</td>
						
						<#assign quycach = 6>
						<td>${quycach}</td>
						<td nowrap="nowrap" align="center">
							${orderItem.quantity?string.number}
						</td>
						<td nowrap="nowrap" align="center">
							&nbsp;
						</td>
						<td>${orderItem.quantity?string.number}</td>
						
						<#assign khay = orderItem.quantity?string?number / quycach>
						<#assign allTotalQuality = allTotalQuality + orderItem.quantity?string?number>
						<#assign allTotalKhay = allTotalKhay + khay>
						<td>${khay?string(",##0.00")}</td>
						
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
						</td>
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/>
						</td>
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=localOrderReadHelper.getOrderItemSubTotal(orderItem) isoCode=currencyUomId/>
						</td>
						<td>
							<@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/>
						</td>
						
						<#if orderItem.productId == "milch_geister" >	
							<#assign unitPriceExport = 24000 >						
						<#elseif orderItem.productId == "milch_geister_straw">
							<#assign unitPriceExport = 24000 >
						<#elseif orderItem.productId == "monte_schoko">
							<#assign unitPriceExport = 36000 >
						<#elseif orderItem.productId == "monte_vani">
							<#assign unitPriceExport = 25000 >	
						<#elseif orderItem.productId == "primo">
							<#assign unitPriceExport = 29500 >
						<#else>									
							<#assign unitPriceExport = orderItem.unitPrice * 0.7>
						</#if>
						

						<#assign subTotalExport = unitPriceExport * orderItem.quantity?string?number>
						<#assign allTotalOrderSumExport = allTotalOrderSumExport + subTotalExport>
						<td class="color-red" class="align-right">
							<@ofbizCurrency amount=unitPriceExport isoCode=currencyUomId/>
						</td>
						<td class="color-red" class="align-right">
							<@ofbizCurrency amount=subTotalExport isoCode=currencyUomId/>							
						</td>
						
					</#if>
					
	
				</tr>
			</#list>
				
				<tr>
				  	<td colspan="6" align="right" valign="bottom">
						<b>Tong cong gio hang:</b>
				  	</td>
				  	<td valign="bottom"><b>${allTotalQuality}</b></td>
				  	<td valign="bottom">-</td>
				  	<td valign="bottom"><b>${allTotalQuality}</b></td>
				  	<td valign="bottom"><b>${allTotalKhay?string(",##0.00")}</b></td>
				  	<td valign="bottom">&nbsp;</td>
				  	<td valign="bottom">&nbsp;</td>
				  	<td align="right" class="align-right" valign="bottom">
					  	<b><#if orderSubTotal?exists><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></#if></b>
				  	</td>
				  	<td valign="bottom">&nbsp;</td>
				  	<td valign="bottom">&nbsp;</td>
				  	<td valign="bottom" class="color-red align-right">
				  		<b><@ofbizCurrency amount=allTotalOrderSumExport isoCode=currencyUomId/></b>
				  	</td>
				  	
				</tr>
					
                <#assign trietkhau = 0>
				<#list headerAdjustmentsToShow as orderHeaderAdjustment>
			      <tr>			      
			        <th colspan="12">${orderHeaderAdjustment.description}</th>
			        <td align="right" class="align-right" valign="bottom"> <b><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></b></td>
			        <#assign trietkhau = trietkhau + localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)>
			        <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>			        
			        <td colspan="3">&nbsp;</td>		        
			      </tr>			      
			    </#list>		
					<#assign hoahong = 0.045>
					<#assign hotroquanlyhanghoa = 0.005>
					<#if orderSubTotal?exists>
					<#else>
						<#assign orderSubTotal = 0>
					</#if>
					<#assign hoahongPrice = orderSubTotal * hoahong>
					<#assign hotroquanlyhanghoaPrice = orderSubTotal * hotroquanlyhanghoa>
				<tr>
					<#assign orderSubTotalOne = orderSubTotal + trietkhau>
				  	<td colspan="12" align="right" valign="bottom" class="align-right">
						<b>Tong tien truoc VAT - CK</b>
					</td>
				  
				  	<td align="right" class="align-right" valign="bottom">
						<b><@ofbizCurrency amount=orderSubTotalOne isoCode=currencyUomId/></b>
					</td>
				  	<td class="color-red align-right"><b>Truoc VAT</b></td>
				  	<td>&nbsp;</td>
				  	<td class="color-red align-right"><b><@ofbizCurrency amount=allTotalOrderSumExport isoCode=currencyUomId/></b></td>
				</tr>
				
				<tr>


			      <td colspan="12" align="right" valign="bottom" class="align-right"> <b>${uiLabelMap.OrderSalesTax}</b> </td>


			      <td  align="right" class="align-right" valign="bottom"> <b> <@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/> </b></td>
			      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>	


			      <td class="color-red align-right"><b>${uiLabelMap.OrderSalesTax}</b></td>		      
			      <td>&nbsp;</td>
			      <#assign allTotalOrderSumExportVAT = allTotalOrderSumExport * 0.1>
			      <td class="color-red align-right"><b><@ofbizCurrency amount=allTotalOrderSumExportVAT isoCode=currencyUomId/></b></td>
			    </tr>
				      			         							
				<#assign allTotalOrderSumExportVAT = allTotalOrderSumExport * 0.1>

				
				<tr>					


					<#assign allTotalOrderSumExportVATAfter = allTotalOrderSumExport + allTotalOrderSumExportVAT>
				  	<td colspan="12" align="right" valign="bottom" class="align-right">
						<b>Tong tien (Sau VAT)</b>
					</td> 
				  	<td align="right" class="align-right" valign="bottom">
						<b><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></b>
					</td>
				  	<td class="color-red align-right"><b>Sau VAT</b></td>
				  	<td>&nbsp;</td>
				  	<td class="color-red align-right"><b><@ofbizCurrency amount=allTotalOrderSumExportVATAfter isoCode=currencyUomId/></b></td>
				</tr>
				
				<tr>
					<td colspan="4" class="color-red align-right" align="right" valign="bottom" class="align-right">
						<b>Nop vao tai khoan</b>					
				  	<td colspan="5" align="right" valign="bottom" class="align-right">
						<b>Tai khoan cong ty Delys</b>
					</td> 
				  	<td colspan="3" align="right" class="color-red align-right" valign="bottom">
						<b><@ofbizCurrency amount=allTotalOrderSumExportVATAfter isoCode=currencyUomId/></b>
					</td>
				  	<td colspan="3""><b>Tai khoan 2 </b></td>				  	
				  	<td colspan="3"  class="color-red align-right"><b><@ofbizCurrency amount=orderGrandTotal - allTotalOrderSumExportVATAfter  isoCode=currencyUomId/></b></td>
				</tr>					
						
			</tbody>
		</table>
	</div>
</form>


<div class="row-fluid wizard-actions">
	<button class="btn btn-primary" onclick="window.location='<@ofbizUrl>dainitorderentry</@ofbizUrl>'">
		<i class="icon-angle-left"></i> ${uiLabelMap.DABack}</button>
	<button class="btn btn-primary" onclick="window.location='<@ofbizUrl>processorder</@ofbizUrl>'">
		 ${uiLabelMap.DASendOrder} <i class="icon-angle-right icon-on-right"></i></button>
</div>


<div style="clear:both"></div>
<br />


