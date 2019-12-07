<style type="text/css">
	.contain-view-calendar {
		display: block;
	}
</style>
<#if hasOlbPermission("MODULE", "PRODQUOTATION_APPROVE", "")>
	<#assign hasApproved = true>
<#else>
	<#assign hasApproved = false>
</#if>
<#assign currentStatusId = productQuotation.statusId?if_exists>

<#assign hasThruDate = false/>
<#assign isThruDate = false/>
<#if ("QUOTATION_CANCELLED" == productQuotation.statusId) || (productQuotation.thruDate?exists && productQuotation.thruDate &lt; nowTimestamp)>
	<#assign isThruDate = true/>
</#if>
<#if hasApproved && !isThruDate>
	<#assign hasThruDate = true/>
</#if>

<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productQuotation.productQuotationId?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationName}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.quotationName?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block" style="width:75px; vertical-align: top">
						<label style="line-height: 20px;">${uiLabelMap.BSStatus}:</label>
					</div>
					<div class="div-inline-block" style="width:calc(100% - 80px)">
						<span>
							<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
								<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
			                </#if>
						</span>
						<#if isThruDate> (<span style="color:#D7432E">${uiLabelMap.BSThisQuotationHasExpired}</span>)</#if>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label></label>
					</div>
					<div class="div-inline-block">
						<span style="font-weight:normal">
						<#if currentStatusId?exists && currentStatusId?has_content>
							<#assign quotationStatuses = productQuotation.getRelated("ProductQuotationStatus", null, ["statusDatetime"], false)>
							<#if quotationStatuses?has_content>
			                  	<#list quotationStatuses as quotationStatus>
				                    <#assign loopStatusItem = quotationStatus.getRelatedOne("StatusItem", false)>
				                    <#assign userlogin = quotationStatus.getRelatedOne("UserLogin", false)>
				                    <div class="margin-left20">
				                      	${loopStatusItem.get("description",locale)} <#if quotationStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(quotationStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
				                      	&nbsp;
				                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${quotationStatus.statusUserLogin}]
				                      	<#if quotationStatus.changeReason?exists>
				                      		&nbsp;
				                      		${uiLabelMap.BSReason} - ${quotationStatus.changeReason?if_exists}
				                      	</#if>
				                    </div>
			                  	</#list>
		                	</#if>
						</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.description?if_exists}</span>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSCurrencyUomId}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.currencyUomId?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSFromDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.fromDate?exists>${productQuotation.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSThruDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.thruDate?exists>${productQuotation.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<#--
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productQuotation.salesMethodChannelEnumId?exists>
								<#assign salesMethodChannel = delegator.findOne("Enumeration", {"enumId" : productQuotation.salesMethodChannelEnumId}, false)/>
								<#if salesMethodChannel?exists>
									${salesMethodChannel.get("description", locale)}
								</#if>
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if roleTypesSelected?has_content>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list roleTypesSelected as roleTypeSelected>
									<li style="margin-bottom: 0; margin-top:0">
										<i class="icon-user green"></i>
										<#if roleTypeSelected.description?exists>${roleTypeSelected.description}<#else>${roleTypeSelected.roleTypeId}</#if>
									</li>
								</#list>
							</ul>
							</#if>
						</span>
					</div>
				</div>
				-->
				<#if partyIdApply?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSCustomer}:</label>
						</div>
						<div class="div-inline-block">
							<#assign partyApply = delegator.findOne("PartyFullNameDetailSimple", {"partyId": partyIdApply}, false)!/>
							<span>${partyApply?if_exists.fullName} (${partyIdApply})</span>
						</div>
					</div>
				</#if>
				<#if partyGroupIdApply?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSCustomer}:</label>
						</div>
						<div class="div-inline-block">
							<#assign partyGroupApply = delegator.findOne("PartyFullNameDetailSimple", {"partyId": partyGroupIdApply}, false)!/>
							<span>${partyGroupApply?if_exists.fullName} (${partyGroupIdApply})</span>
						</div>
					</div>
				</#if>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productStoreAppls?exists>
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStoreAppls as itemProductStoreAppl>
										<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
										<li style="margin-bottom: 0; margin-top:0">
											<#--<i class="icon-angle-right green"></i>-->
											<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
										</li>
										<#if itemProductStoreAppl_index &gt; 1 && productStoreAppls?size &gt; 2>
											<a href="javascript:void(0)" id="showProductStoreViewMore">${uiLabelMap.BSViewMore} (${productStoreAppls?size - 3})</a>
											<#break/>
										</#if>
									</#list>
								</ul>
								<div style="display:none" id="productStoreViewMore">
									<label>${uiLabelMap.BSTotal}: ${productStoreAppls?size}</label>
									<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
										<#list productStoreAppls as itemProductStoreAppl>
											<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
											<li style="margin-bottom: 0; margin-top:0">
												<i class="icon-angle-right green"></i>
												<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
											</li>
										</#list>
									</ul>
								</div>
							</#if>
						</span>
					</div>
				</div>
				<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyIdApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list listPartyIdApply as partyIdSelected>
									<li style="margin-bottom: 0; margin-top:0">
										<i class="icon-user green"></i>
										${partyIdSelected}
									</li>
								</#list>
							</ul>
						</span>
					</div>
				</div>
				</#if>
			</div><!--.span6-->
		</div><!--.row-fluid-->
	</div><!--.form-horizontal-->
</div>
<div class="row-fluid">
	<div class="span12">
		<div style="text-align:right">
			<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.BSListProduct}</b></h5>
		</div>
		<div style="clear:both"></div>
		
		<div id="list-product-price-rules">
			<#if productQuotation.productQuotationTypeId == "PROD_CAT_PRICE_FOD">
				<#if listProductQuotationRuleData?exists && listProductQuotationRuleData?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:20px">${uiLabelMap.BSSTT}</th>
								<th class="center" style="width:200px">${uiLabelMap.BSProductCategoryId}</th>
								<th class="center">${uiLabelMap.BSCategoryName}</th>
								<th class="center" style="width:200px">${uiLabelMap.BSAmount}</th>
							</tr>
						</thead>
						<tbody>
						<#list listProductQuotationRuleData as quotationRule>
				        	<tr>
				        		<td>${quotationRule_index + 1}</td>
				        		<td>${quotationRule.productCategoryId?default("")}</td>
				        		<td>${quotationRule.categoryName?if_exists}</td>
				        		<td class="align-right">
				        			<#if quotationRule.amount?exists>
		        						<@ofbizCurrency amount=quotationRule.amount isoCode=productQuotation.currencyUomId/>
				                	</#if>
				        		</td>
				        	</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.BSNoQuotationItemsToDisplay}</div>
				</#if>
			<#else>
				<#if listProductQuotationRuleData?exists && listProductQuotationRuleData?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th rowspan="2" style="width:10px">${uiLabelMap.BSSTT}</th>
								<th colspan="2" class="center">${uiLabelMap.BSProduct}</th>
								<th rowspan="2" class="center" style="width: 10%">${uiLabelMap.BSUom}</th>
								<th rowspan="2" class="center" style="width: 12%">${uiLabelMap.BSPacking}</th>
								<#--<th rowspan="2" class="center" style="width:40px">${uiLabelMap.BSPackingPerTray}</th>-->
								<th colspan="2" class="center">${uiLabelMap.BSPriceToCustomer}</th>
								<#--<th colspan="2" class="center">${uiLabelMap.BSPricesProposalForConsumer}</th>-->
							</tr>
							<tr>
								<th class="center" style="width: 12%">${uiLabelMap.BSProductId}</th>
								<th class="center">${uiLabelMap.BSProductName}</th>
								
								<th class="center" style="width: 14%">${uiLabelMap.BSBeforeVAT}</th>
								<th class="center" style="width: 14%">${uiLabelMap.BSAfterVAT}</th>
								<#--
								
								<th class="center" style="width:80px">${uiLabelMap.BSAfterVATPerPacking}</th>
								<th class="center" style="width:80px">${uiLabelMap.BSAfterVATPerTray}</th>
								-->
							</tr>
						</thead>
						<tbody>
						<#list listProductQuotationRuleData as quotationRule>
				        	<tr>
				        		<td>${quotationRule_index + 1}</td>
				        		<td>${quotationRule.productCode?default(quotationRule.productId?default(""))}</td>
				        		<td>${quotationRule.productName?if_exists}</td>
				        		<td><#if quotationRule.quantityUomId?exists>${quotationRule.quantityUomDesc?if_exists}</#if></td>
				        		<td><#if quotationRule.productWeightStr?exists>${quotationRule.productWeightStr}</#if></td>
				        		<#--<td><#if quotationRule.productQuantityPerTray?exists>${quotationRule.productQuantityPerTray}</#if></td>-->
				        		<td class="align-right">
				        			<#if quotationRule.priceToDistNormal?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToDistNormal isoCode=productQuotation.currencyUomId/>
				                	</#if>
				        		</td>
				        		<td class="align-right">
				        			<#if quotationRule.priceToDistNormalAfterVAT?exists>
		        						<@ofbizCurrency amount=quotationRule.priceToDistNormalAfterVAT?round isoCode=productQuotation.currencyUomId/>
		            				</#if>
				        		</td>
		            			<#--
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
		            			-->
				        	</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.BSNoQuotationItemsToDisplay}</div>
				</#if>
			</#if>
		</div>
		<div style="text-align:right">
			<#if hasApproved && ("QUOTATION_CANCELLED" != productQuotation.statusId)>
				<#if hasThruDate>
					<div class="row-fluid container-approve">
						<div class="span6">
							<#if currentStatusId?exists && (currentStatusId == "QUOTATION_CREATED" || currentStatusId == "QUOTATION_MODIFIED")>
								<span class="widget-toolbar none-content">
									<a id="acceptQuotationBtn" class="btn btn-primary btn-mini" href="javascript:void(0);" 
										style="font-size:13px; padding:0 8px">
										<i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
									<a id="cancelQuotationBtn" class="btn btn-danger btn-mini" href="javascript:void(0);" 
					              		style="font-size:13px; padding:0 8px">
										<i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
									<#--
									<form name="QuotationAccept" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
					                	<input type="hidden" name="statusId" value="QUOTATION_ACCEPTED">
					                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						                <input type="hidden" name="productQuotationId" value="${productQuotation.productQuotationId?if_exists}">
					              	</form>
									<form name="QuotationCancel" method="post" action="<@ofbizUrl>changeQuotationStatus</@ofbizUrl>">
					                	<input type="hidden" name="statusId" value="QUOTATION_CANCELLED">
					                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						                <input type="hidden" name="productQuotationId" value="${productQuotation.productQuotationId?if_exists}">
						                <input type="hidden" name="changeReason" id="changeReason" value="" />
					              	</form>
									-->
								</span>
							</#if>
						</div><!--.span6-->
						<div class="span6" style="padding-top: 5px">
							<div class="row-fluid">
								<div class="span3 text-right">
									<label for="thruDate" style="line-height: 30px;">${uiLabelMap.BSThruDate}:</label>
								</div>
								<div class="span9">
									<form name="updateQuotationThruDate" id="updateQuotationThruDate" method="POST" action="<@ofbizUrl>updateQuotationThruDate</@ofbizUrl>" 
										style="float:left">
										<input type="hidden" name="productQuotationId" value="${productQuotation.productQuotationId?if_exists}" />
										<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${productQuotation.thruDate?if_exists}" event="" action="" className="" alert="" 
											title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
											timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
											classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
											pmSelected="" compositeType="" formName=""/>
									</form>
									<button id="updateQuotationThruDateBtn" class="btn btn-primary btn-mini" type="button" style="float:left; margin-left:5px">
										<i class="icon-ok open-sans"></i>${uiLabelMap.BSUpdate}
									</button>
								</div>
							</div>
						</div><!--.span6-->
					</div>
				</#if>
			</#if>
		</div>
	</div>
</div>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>
<div class="container_loader">
	<div id="info_loader" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var labelwgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	var labelwgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	
	$(function(){
		OlbQuotationView.init();
	});
	var OlbQuotationView = (function(){
		<#if hasThruDate>
			var validatorThruDateVAL;
			
			var init = function(){
				initElement();
				initEvent();
				initValidateForm();
			};
			var initElement = function(){
				jOlbUtil.notification.create("#container", "#jqxNotification");
			};
			var initEvent = function(){
				$("#showProductStoreViewMore").click(function(){
					var dataViewMore = $("#productStoreViewMore").html();
					jOlbUtil.alert.info(dataViewMore);
				});
				$("#acceptQuotationBtn").click(function(){
					jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToAccept)}", function() {
			        	//document.QuotationAccept.submit()
			        	$("#acceptQuotationBtn").addClass("disabled");
						$("#cancelQuotationBtn").addClass("disabled");
			        	$.ajax({
							url: "changeQuotationStatus",
							type: "POST",
							async: true,
							data: {
								productQuotationId: "${productQuotation.productQuotationId?if_exists}",
								statusId: "QUOTATION_ACCEPTED", 
								ntfId: "${parameters.ntfId?if_exists}"
							},
							beforeSend: function(){
								$("#info_loader").show();
							},
							success: function(data) {
								jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
										$("#acceptQuotationBtn").removeClass("disabled");
										$("#cancelQuotationBtn").removeClass("disabled");
										
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
							        	$("#jqxNotification").jqxNotification("open");
							        	if (data.productQuotationId != undefined && data.productQuotationId != null) {
							        		location.reload();
							        	}
									}
								);
				       	  	},
				       	  	error: function(data){
								alert("Send request is error");
								$("#acceptQuotationBtn").removeClass("disabled");
								$("#cancelQuotationBtn").removeClass("disabled");
							},
							complete: function(data){
								$("#info_loader").hide();
							},
						});
			        });
				});
				$("#cancelQuotationBtn").click(function(){
					jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCancelNotAccept)}", function() {
			        	//document.QuotationCancel.submit();
			        	$("#acceptQuotationBtn").addClass("disabled");
						$("#cancelQuotationBtn").addClass("disabled");
			        	$.ajax({
							url: "changeQuotationStatus",
							type: "POST",
							async: true,
							data: {
								productQuotationId: "${productQuotation.productQuotationId?if_exists}",
								statusId: "QUOTATION_CANCELLED", 
								ntfId: "${parameters.ntfId?if_exists}",
								changeReason: "",
							},
							beforeSend: function(){
								$("#info_loader").show();
							},
							success: function(data) {
								jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
										$("#acceptQuotationBtn").removeClass("disabled");
										$("#cancelQuotationBtn").removeClass("disabled");
										
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
							        	$("#jqxNotification").jqxNotification("open");
							        	if (data.productQuotationId != undefined && data.productQuotationId != null) {
							        		location.reload();
							        	}
									}
								);
				       	  	},
				       	  	error: function(data){
								alert("Send request is error");
								$("#acceptQuotationBtn").removeClass("disabled");
								$("#cancelQuotationBtn").removeClass("disabled");
							},
							complete: function(data){
								$("#info_loader").hide();
							},
						});
			        });
				});
				$("#updateQuotationThruDateBtn").click(function(){
					<#--if(!$('#updateQuotationThruDate').valid()) {-->
					if(!validatorThruDateVAL.validate()) {
						return false;
					}
					
					<#-- bootbox.confirm("${uiLabelMap.BSAreYouSureYouWantToCreateThruDate}", function(result){
						if(result){
							document.getElementById("updateQuotationThruDate").submit();
						}
					}); -->
					jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreateThruDate)}?", function(){
						//document.getElementById("updateQuotationThruDate").submit();
						var dataMap = $("#updateQuotationThruDate").serialize();
						$("#updateQuotationThruDateBtn").addClass("disabled");
			        	$.ajax({
							url: "updateQuotationThruDate",
							type: "POST",
							async: true,
							data: dataMap,
							beforeSend: function(){
								$("#info_loader").show();
							},
							success: function(data) {
								jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
										$("#updateQuotationThruDateBtn").removeClass("disabled");
										
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
							        	$("#jqxNotification").jqxNotification("open");
							        	if (data.productQuotationId != undefined && data.productQuotationId != null) {
							        		location.reload();
							        	}
									}
								);
				       	  	},
				       	  	error: function(data){
								alert("Send request is error");
								$("#updateQuotationThruDateBtn").removeClass("disabled");
							},
							complete: function(data){
								$("#info_loader").hide();
							},
						});
					}, "${StringUtil.wrapString(uiLabelMap.wgcancel)}", "${StringUtil.wrapString(uiLabelMap.wgok)}");
				});
			};
			var initValidateForm = function(){
				<#--
				$.validator.addMethod('validateToDay',function(value,element){
					var now = new Date();
					now.setHours(0,0,0,0);
					return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= now;
				},'Greather than today');
				
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
							validateToDay: "${StringUtil.wrapString(uiLabelMap.BSRequiredValueGreatherOrEqualToDay)}"
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
				-->
				setUiLabelMap("validFieldRequire", "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}");
				var extendRules = [
						{input: '#thruDate_i18n', message: "${StringUtil.wrapString(uiLabelMap.BSRequiredValueGreatherOrEqualToDay)}", action: 'valueChanged', 
							rule: function(input, commit){
								var value = $(input).val();
								var dateValueL = getDateFromFormat(value, "dd/MM/yyyy HH:mm:ss");
								if (OlbCore.isNotEmpty(value)) {
									var now = new Date();
									now.setHours(0,0,0,0);
									var dateValue = new Date(dateValueL);
						    		if(dateValue < now){return false;}
								}
					    		return true;
							}
						},
					];
				var mapRules = [
						{input: "#thruDate_i18n", type: "validInputNotNull"},
					];
				validatorThruDateVAL = new OlbValidator($('#updateQuotationThruDate'), mapRules, extendRules);
			};
			
			function getDateFromFormat(val,format) {
				val=val+"";
				format=format+"";
				var i_val=0;
				var i_format=0;
				var c="";
				var token="";
				var token2="";
				var x,y;
				var now=new Date();
				var year=now.getYear();
				var month=now.getMonth()+1;
				var date=1;
				var hh=now.getHours();
				var mm=now.getMinutes();
				var ss=now.getSeconds();
				var ampm="";
				
				while (i_format < format.length) {
					// Get next token from format string
					c=format.charAt(i_format);
					token="";
					while ((format.charAt(i_format)==c) && (i_format < format.length)) {
						token += format.charAt(i_format++);
						}
					// Extract contents of value based on format token
					if (token=="yyyy" || token=="yy" || token=="y") {
						if (token=="yyyy") { x=4;y=4; }
						if (token=="yy")   { x=2;y=2; }
						if (token=="y")    { x=2;y=4; }
						year=_getInt(val,i_val,x,y);
						if (year==null) { return 0; }
						i_val += year.length;
						if (year.length==2) {
							if (year > 70) { year=1900+(year-0); }
							else { year=2000+(year-0); }
							}
						}
					else if (token=="MMM"||token=="NNN"){
						month=0;
						for (var i=0; i<MONTH_NAMES.length; i++) {
							var month_name=MONTH_NAMES[i];
							if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
								if (token=="MMM"||(token=="NNN"&&i>11)) {
									month=i+1;
									if (month>12) { month -= 12; }
									i_val += month_name.length;
									break;
									}
								}
							}
						if ((month < 1)||(month>12)){return 0;}
						}
					else if (token=="EE"||token=="E"){
						for (var i=0; i<DAY_NAMES.length; i++) {
							var day_name=DAY_NAMES[i];
							if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
								i_val += day_name.length;
								break;
								}
							}
						}
					else if (token=="MM"||token=="M") {
						month=_getInt(val,i_val,token.length,2);
						if(month==null||(month<1)||(month>12)){return 0;}
						i_val+=month.length;}
					else if (token=="dd"||token=="d") {
						date=_getInt(val,i_val,token.length,2);
						if(date==null||(date<1)||(date>31)){return 0;}
						i_val+=date.length;}
					else if (token=="hh"||token=="h") {
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<1)||(hh>12)){return 0;}
						i_val+=hh.length;}
					else if (token=="HH"||token=="H") {
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<0)||(hh>23)){return 0;}
						i_val+=hh.length;}
					else if (token=="KK"||token=="K") {
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<0)||(hh>11)){return 0;}
						i_val+=hh.length;}
					else if (token=="kk"||token=="k") {
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<1)||(hh>24)){return 0;}
						i_val+=hh.length;hh--;}
					else if (token=="mm"||token=="m") {
						mm=_getInt(val,i_val,token.length,2);
						if(mm==null||(mm<0)||(mm>59)){return 0;}
						i_val+=mm.length;}
					else if (token=="ss"||token=="s") {
						ss=_getInt(val,i_val,token.length,2);
						if(ss==null||(ss<0)||(ss>59)){return 0;}
						i_val+=ss.length;}
					else if (token=="a") {
						if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
						else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
						else {return 0;}
						i_val+=2;}
					else {
						if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
						else {i_val+=token.length;}
						}
					}
				// If there are any trailing characters left in the value, it doesn't match
				if (i_val != val.length) { return 0; }
				// Is date valid for month?
				if (month==2) {
					// Check for leap year
					if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
						if (date > 29){ return 0; }
						}
					else { if (date > 28) { return 0; } }
					}
				if ((month==4)||(month==6)||(month==9)||(month==11)) {
					if (date > 30) { return 0; }
					}
				// Correct hours value
				if (hh<12 && ampm=="PM") { hh=hh-0+12; }
				else if (hh>11 && ampm=="AM") { hh-=12; }
				var newdate=new Date(year,month-1,date,hh,mm,ss);
				return newdate.getTime();
			}
			function _isInteger(val) {
				var digits="1234567890";
				for (var i=0; i < val.length; i++) {
					if (digits.indexOf(val.charAt(i))==-1) { return false; }
					}
				return true;
			}
			function _getInt(str,i,minlength,maxlength) {
				for (var x=maxlength; x>=minlength; x--) {
					var token=str.substring(i,i+x);
					if (token.length < minlength) { return null; }
					if (_isInteger(token)) { return token; }
					}
				return null;
			}
			
			<#--var cancelProductQuotation = function(){
				bootbox.prompt("${uiLabelMap.BSReasonCancelQuotation}:", 
					"${StringUtil.wrapString(uiLabelMap.wgcancel)}",
					"${StringUtil.wrapString(uiLabelMap.wgok)}",
					function(result) {
						if(result === null) {
						} else {
							document.getElementById('changeReason').value = "" + result;
							document.QuotationCancel.submit();
						}
					}
			    );
				bootbox.prompt("<span style='font-size:13px; padding:0; margin: -10px; display:block; height:25px'>${uiLabelMap.BSReasonCancelQuotation}:</span>", function(result) {
					if(result === null) {
					} else {
						document.getElementById('changeReason').value = "" + result;
						document.QuotationCancel.submit();
					}
				});
				[
	                {"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }, 
			        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}", "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": callback
					}
			    ]
			}-->
			return {
				init: init,
			};
		<#else>
			var init = function(){
				initEvent();
			};
			var initEvent = function(){
				$("#showProductStoreViewMore").click(function(){
					var dataViewMore = $("#productStoreViewMore").html();
					jOlbUtil.alert.info(dataViewMore);
				});
			};
			return {
				init: init,
			};
		</#if>
	}());
</script>
