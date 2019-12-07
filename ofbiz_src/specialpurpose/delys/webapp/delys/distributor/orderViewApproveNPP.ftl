
<div id="orderoverview-tab" class="tab-pane active">
	<h3 style="text-align:center;font-weight:bold; text-transform: uppercase;">${orderHeader.orderName?default("${uiLabelMap.DAOrderFormTitle}")}</h3>

	<div class="form-horizontal desc" style="padding-top:10px; padding-bottom:10px">
		<div class="span12">
			<div class="row">
				<div class="span6">
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAOrderName}:</label>
						<div class="controls"><div class="span12">${orderHeader.orderName?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAOrderId}:</label>
						<div class="controls"><div class="span12">${orderHeader.orderId?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACurrency}:</label>
						<div class="controls"><div class="span12">${orderHeader.currencyUom?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACreateOrderDate}:</label>
						<div class="controls"><div class="span12">
							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "", locale, timeZone)?default("0000-00-00 00:00:00")}&nbsp;
						</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DADesiredDeliveryDate}:</label>
						<div class="controls"><div class="span12">${orderHeader.shipBeforeDate?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAProductStore}:</label>
						<div class="controls"><div class="span12">${orderHeader.productStoreId?if_exists}&nbsp;</div></div>
					</div>
				</div><!-- .span6 -->
				<div class="span6">
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DAPersonCreate}:</label>
						<div class="controls"><div class="span12">
							<#if orderHeaderStatuses?has_content>
			                  <#list orderHeaderStatuses as orderHeaderStatus>
			                    <#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem", false)>
			                    <#assign userlogin = orderHeaderStatus.getRelatedOne("UserLogin", false)>
			                    <div>
			                      <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, orderHeaderStatus.statusUserLogin, true)}-->
			                      <#assign personCreate = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orderHeaderStatus.statusUserLogin, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
					              <#if personCreate?has_content>
					              		${personCreate.fullName?if_exists}
					              <#else>
					              		${orderHeaderStatus.statusUserLogin?if_exists}
					              </#if>
					              
			                    </div>
			                  </#list>
			                </#if>
						</div></div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACustomer}:</label>
						<div class="controls"><div class="span12">
							${orderHeader.partyId?if_exists}&nbsp;
							<#if displayParty?has_content || orderContactMechValueMaps?has_content>
								<#if displayParty?has_content>
					                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
					                ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
				              	</#if>
				              	<#-- 
				              	<#if partyId?exists>
					                &nbsp;(<a href="${customerDetailLink?if_exists}${partyId}${externalKeyParam}" target="partymgr">${partyId}</a>)
				              	</#if>
				              	-->
							</#if>
						</div></div>
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
			</div><!--.row-->
		</div><!--.span12-->			
	</div><!--.form-horizontal-->

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
						
						
					<#assign orderGrandTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderGrandTotal(orderItems, orderAdjustments)>
					
						<#assign khay = orderItem.quantity?string?number / quycach>
						<#assign allTotalQuality = allTotalQuality + orderItem.quantity?string?number>
						<#assign allTotalKhay = allTotalKhay + khay>
						<td>${khay?string(",##0.00")}</td>
						
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
						</td>
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
						</td>
						<td nowrap="nowrap" align="right" class="align-right">
							<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
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
						<b>${uiLabelMap.DATotalCart}::</b>
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
				<#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                	<#list orderItemAdjustments as orderHeaderAdjustment>
				      <tr>			      
				        <th colspan="12">${orderHeaderAdjustment.description}</th>
				        <td align="right" class="align-right" valign="bottom"> <b><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></b></td>
				        <#assign trietkhau = trietkhau + localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)>
				        <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>			        
				        <td colspan="3">&nbsp;</td>		        
				      </tr>			      
				    </#list>
				</#if>
				
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

<#if currentStatus.statusId?has_content && currentStatus.statusId == "ORDER_NPPAPPROVED">
	<span style="color:#F00; display:block; margin-bottom:20px">${uiLabelMap.DADistributorApproved}</span>
<#else>
	<#if currentStatus.statusId == "ORDER_SADAPPROVED">
		<#if hasDistributorApproved?exists && hasDistributorApproved == "TRUE">
		<form name="attachPaymentOrder" id="attachPaymentOrder" action="<@ofbizUrl>attachPaymentOrder</@ofbizUrl>" method="post" enctype="multipart/form-data">	
			<input type="hidden" name="orderId" id="orderId" value="${orderId}" />
			<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
			<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
			<input type="hidden" name="imageResize" id="imageResize" value="" />
			<h6><b>${uiLabelMap.DAAttachPaymentOrder}</b></h6>
			<div class="widget-body">
				<div class="widget-main">
					<div class="span6">
						<input type="file" id="uploadedFile" name="uploadedFile"/>
						<#--
						<input multiple="" type="file" id="id-input-file-3" />
						<label>
							<input type="checkbox" name="file-format" id="id-file-format" />
							<span class="lbl"> Allow only images</span>
						</label>
						 -->
						 
						 <button id="btn_attachPaymentOrder" type="submit" class="btn btn-small btn-primary">
				 			<i class="icon-plus"></i> ${uiLabelMap.DAAdd} </button>
					</div>
				</div>
			</div>
			<div style="clear:both"></div>
		</form>	
	
			<div class="row-fluid wizard-actions">
				<form action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>" method="post" style="display:inline-block">
					<input type="hidden" name="orderId" value="${orderId}" />
					<input type="hidden" name="statusId" value="ORDER_CANCELLED" />
					<input type="hidden" name="setItemStatus" value="Y" />
					<input type="hidden" name="changeReason" value="" />
					
					<button class="btn btn-primary" type="submit">
						<i class="icon-angle-left"></i> ${uiLabelMap.DACancel}</button>
				</form>
				<form action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>" method="post" style="display:inline-block">
					<input type="hidden" name="orderId" value="${orderId}" />
					<input type="hidden" name="statusId" value="ORDER_NPPAPPROVED" />
					<input type="hidden" name="setItemStatus" value="Y" />
					<input type="hidden" name="changeReason" value="" />
					
					<button class="btn btn-primary" type="submit"> ${uiLabelMap.DASendConfirm} <i class="icon-angle-right icon-on-right"></i></button>
				</form>
			</div>
		</#if>
	</#if>
</#if>
	<h6><b>${uiLabelMap.DAAttachPaymentOrder}</b></h6>
		<div class="span12" style="margin-bottom:20px">
				<div class="span7">
					<#if paymentOrderList?has_content>
					<div style="overflow: auto; width: auto; height:auto; max-height: 200px;overflow-y: scroll;">
					
						<#list paymentOrderList as paymentOrder>
							<div class="itemdiv commentdiv">
								<div class="user">
									<a href="${paymentOrder.objectInfo?if_exists}" target="_blank" style="max-width:42px; max-height:42px">
										<img alt="${paymentOrder.dataResourceName?if_exists}" src="${paymentOrder.objectInfo?if_exists}" style="max-width:42px; max-height:42px" />
									</a>
								</div>

								<div class="body">
									<div class="name">
										<a href="${paymentOrder.objectInfo?if_exists}" target="_blank">${paymentOrder.dataResourceName?if_exists}</a>
									</div>

									<div class="time">
										<i class="icon-time"></i>
										<span class="green">${paymentOrder.createdDate?string("yyyy-MM-dd HH:mm:ss.SSS")}</span>
									</div>

									<div class="text">
										<i class="icon-quote-left"></i>
										<#assign personAttachPaymentOrder = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", paymentOrder.createdByUserLogin, "compareDate", paymentOrder.createdDate, "userLogin", userLogin))/>
										${uiLabelMap.DAPersonCreate}: ${personAttachPaymentOrder.fullName?if_exists} [${paymentOrder.createdByUserLogin}]
									</div>
								</div>

								<div class="tools">
									<#if currentStatus.statusId?has_content && currentStatus.statusId == "ORDER_NPPAPPROVED">
									<#else>
										<a href="#" class="btn btn-minier btn-danger">
											<i class="icon-only icon-trash"></i>
										</a>
									</#if>
								</div>
							</div>
						</#list>
					
					</div><!--.comments-->
					<#else>
						${uiLabelMap.DANotFile}
					</#if>
				</div>
		</div><!--.span12-->
	

	<#--
		<h5><b>${uiLabelMap.DAAddCostOther}</b></h5>
		<div class="widget-main">
			<form method="post" class="form-horizontal desc" action="/ordermgr/control/additem" name="quickaddform" style="margin: 0;">
				<div class="span12">
					<div class="row">
						<div class="control-group">
							<label class="control-label" style="display:inline-block">${uiLabelMap.DACostName}: </label>
							<div class="controls">
								<div class="span12">
									<input type="text" class="span3">
									<a href="javascript:void(0);" id="0_lookupId_button" class="field-lookup">
									</a>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" style="display:inline-block">${uiLabelMap.DACostCategory}: </label>
							<div class="controls">
								<div class="span12">
									<select id="form-field-select-1" class="span3">
										<option value="FIST" selected="selected">${uiLabelMap.DACostCategoryOne}</option>
										<option value="LAST">${uiLabelMap.DACostCategoryTwo}</option>
									</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" style="display:inline-block">${uiLabelMap.DAAmount}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" class="span3">
									<a href="javascript:void(0);" id="0_lookupId_button" class="field-lookup">
									</a>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.DACategory}: </label>
							<div class="controls">
								<div class="span12">
									<select id="form-field-select-1" class="span3">
										<option value="FIST" selected="selected">${uiLabelMap.DAPlus}</option>
										<option value="LAST">${uiLabelMap.DAMinus}</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div style="clear:both"></div>
				
				<div class="control-group">
					<label class="control-label" for="date">&nbsp;</label>
					<div class="controls">
						<div style="display:inline-block; vertical-align:bottom; margin-left:60px">
							<button class="btn btn-primary btn-small" type="submit">
								<i class="icon-ok"></i>${uiLabelMap.DAAdd}
							</button>
						</div>
					</div>
				</div>
			</form>
			
			<div style="clear:both"></div>
		</div>
	-->
		
		
		<div style="clear:both"></div>
		<br />
		
		
</div>
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
	})
</script>