<#assign currentStatusId = promoSettlement.statusId>

<div class="row-fluid">
	<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;">
		<div class="row margin_left_10 row-desc">
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSPromoSettlementId}:</label>
					<div class="controls-desc">
						<b>${promoSettlement.promoSettlementId?if_exists}</b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSPromoSettlementName}:</label>
					<div class="controls-desc">
						<b>${promoSettlement.promoSettlementName?if_exists}</b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSStatusId}:</label>
					<div class="controls-desc">
						<b>
						<#if currentStatusId?exists && currentStatusId?has_content>
							<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
							<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
		                </#if>
						</b>
					</div>
				</div>
				<div class="control-group">
					<div class="controls-desc">
						<#if currentStatusId?exists && currentStatusId?has_content>
							<#assign promoStatuses = promoSettlement.getRelated("ProductPromoSettlementStatus", null, null, false)>
							<#if promoStatuses?has_content>
			                  	<#list promoStatuses as promoStatus>
				                    <#assign loopStatusItem = promoStatus.getRelatedOne("StatusItem", false)>
				                    <#assign userlogin = promoStatus.getRelatedOne("UserLogin", false)>
				                    <div>
				                      	${loopStatusItem.get("description",locale)} <#if promoStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(promoStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
				                      	&nbsp;
				                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${promoStatus.statusUserLogin}]
				                      	<#if promoStatus.statusId == "PSETTLE_CANCELLED">
				                      		&nbsp;
				                      		${uiLabelMap.BSReason} - ${promoStatus.changeReason?if_exists}
				                      	</#if>
				                    </div>
			                  	</#list>
		                	</#if>
						</#if>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSProductPromoId}:</label>
					<div class="controls-desc">
						<b>${promoSettlement.productPromoId?if_exists}</b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSProductPromoExtId}:</label>
					<div class="controls-desc">
						<b>${promoSettlement.productPromoExtId?if_exists}</b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSFromDate}:</label>
					<div class="controls-desc">
						<b><#if promoSettlement.fromDate?exists>${promoSettlement.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSThruDate}:</label>
					<div class="controls-desc">
						<b><#if promoSettlement.thruDate?exists>${promoSettlement.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSCreatedDate}:</label>
					<div class="controls-desc">
						<b><#if promoSettlement.createdDate?exists>${promoSettlement.createdDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></b>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.BSCreatedBy}:</label>
					<div class="controls-desc">
						<b>${promoSettlement.createdBy?if_exists}</b>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid margin-bottom20">
	<div class="span12">
		<#assign customcontrol1 = ""/>
		<#assign customcontrol2 = ""/>
		<#assign editable = "false"/>
		<#assign editable1 = "false"/>
		<#assign editable2 = "false"/>
		<#if security.hasEntityPermission("PROMOSETTLEMENT_RESULT", "_APPROVE", session) && promoSettlement.statusId == "PSETTLE_PROCESSING">
			<#assign customcontrol1 = "icon-save open-sans@${uiLabelMap.BSSaveNumberApprove}@javascript: void(0);@OlbPromoSettleView.updateNumberApproveSettleResult()">
			<#assign editable1 = "true"/>
			<#assign editable = "true"/>
		</#if>
		<#if security.hasEntityPermission("PROMOSETTLEMENT_RESULT", "_ACCEPT", session) && promoSettlement.statusId == "PSETTLE_PROCESSING">
			<#assign customcontrol2 = "icon-save open-sans@${uiLabelMap.BSSaveNumberAccept}@javascript: void(0);@OlbPromoSettleView.updateNumberAcceptSettleResult()">
			<#assign editable2 = "true"/>
			<#assign editable = "true"/>
		</#if>
		<#assign dataFieldResult = "[
					{name: 'promoSettlementResultId', type: 'string'}, 
					{name: 'promoSettlementId', type: 'string'}, 
					{name: 'productPromoId', type: 'string'}, 
					{name: 'productPromoRuleId', type: 'string'}, 
					{name: 'productPromoActionSeqId', type: 'string'}, 
					{name: 'partyId', type: 'string'}, 
					{name: 'partyCode', type: 'string'}, 
					{name: 'productId', type: 'string'}, 
					{name: 'productCode', type: 'string'}, 
					{name: 'quantity', type: 'number'}, 
					{name: 'amount', type: 'number'}, 
					{name: 'quantityApprove', type: 'number'}, 
					{name: 'amountApprove', type: 'number'}, 
					{name: 'quantityAccept', type: 'number'}, 
					{name: 'amountAccept', type: 'number'}, 
					{name: 'comment', type: 'string'}, 
				]"/>
		<#assign columnlistResult = "
					{text: '${uiLabelMap.BSPromoSettlementResultId}', dataField: 'promoSettlementResultId', width: '12%', editable: false},
					{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: '12%', editable: false},
					{text: '${uiLabelMap.BSPromoLevel}', dataField: 'productPromoRuleId', width: '6%', editable: false},
					{text: '${uiLabelMap.BSAction}', dataField: 'productPromoActionSeqId', width: '8%', editable: false},
					{text: '${uiLabelMap.BSPartyId}', dataField: 'partyCode', width: '12%', editable: false},
					{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '12%', editable: false},
					{text: '${uiLabelMap.BSQuantity}', dataField: 'quantity', width: '12%', editable: false, cellsalign: 'right', cellsformat: 'd'},
					{text: '${uiLabelMap.BSAmount}', dataField: 'amount', width: '12%', editable: false, cellsalign: 'right', cellsformat: 'd'},
					{text: '${uiLabelMap.BSQuantityApprove}', dataField: 'quantityApprove', width: '12%', editable: ${editable1}, columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   				returnVal += formatnumber(value);
			   				returnVal += '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					},
					{text: '${uiLabelMap.BSAmountApprove}', dataField: 'amountApprove', width: '12%', editable: ${editable1}, columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   				returnVal += formatnumber(value);
			   				returnVal += '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					},
					{text: '${uiLabelMap.BSQuantityAccept}', dataField: 'quantityAccept', width: '12%', editable: ${editable2}, columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   				returnVal += formatnumber(value);
			   				returnVal += '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					},
					{text: '${uiLabelMap.BSAmountAccept}', dataField: 'amountAccept', width: '12%', editable: ${editable2}, columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   				returnVal += formatnumber(value);
			   				returnVal += '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					},
					{text: '${uiLabelMap.BSComment}', dataField: 'comment', width: '12%', editable: ${editable}},
		  		"/>
		<@jqGrid id="jqxPromoSettelementResult" customTitleProperties="${uiLabelMap.BSAggregateResult}" 
				url="jqxGeneralServicer?sname=JQListProductPromoSettlementResult&promoSettlementId=${promoSettlement.promoSettlementId}" columnlist=columnlistResult dataField=dataFieldResult 
				viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" editable=editable  editmode="click" selectionmode="multiplecellsadvanced" 
				customcontrol1=customcontrol1 customcontrol2=customcontrol2 mouseRightMenu="false" contextMenuId="contextMenu" jqGridMinimumLibEnable="false"/>
	</div>
</div>

<div class="row-fluid">
	<div class="span12">
		<#assign dataField = "[
					{name: 'promoSettlementId', type: 'string'}, 
					{name: 'promoSettlementDetailId', type: 'string'}, 
					{name: 'isPay', type: 'string'}, 
					{name: 'quantity', type: 'string'}, 
					{name: 'quantityApprove', type: 'string'}, 
					{name: 'comment', type: 'string'}, 
					{name: 'orderId', type: 'string'}, 
					{name: 'orderItemSeqId', type: 'string'}, 
					{name: 'productId', type: 'string'}, 
					{name: 'productCode', type: 'string'}, 
					{name: 'productPromoId', type: 'string'}, 
					{name: 'productPromoRuleId', type: 'string'}, 
					{name: 'productPromoActionSeqId', type: 'string'}, 
					{name: 'sellerId', type: 'string'}, 
					{name: 'sellerCode', type: 'string'}, 
					{name: 'customerId', type: 'string'}, 
					{name: 'customerCode', type: 'string'}, 
					{name: 'fromDate', type: 'string'}, 
					{name: 'createdDate', type: 'string'}, 
					{name: 'createdBy', type: 'string'}, 
				]"/>
		<#assign columnlist = "
					{text: '${uiLabelMap.BSPromoSettlementDetailId}', dataField: 'promoSettlementDetailId', width: '12%'},
					{text: '${uiLabelMap.BSDistributorId}', dataField: 'sellerCode', width: '12%'},
					{text: '${uiLabelMap.BSCustomerId}', dataField: 'customerCode', width: '12%'},
					{text: '${uiLabelMap.BSIsPay}', dataField: 'isPay', width: '6%'},
					{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '12%'},
					{text: '${uiLabelMap.BSQuantity}', dataField: 'quantity', width: '12%', cellsalign: 'right', cellsformat: 'd'},
					{text: '${uiLabelMap.BSQuantityApprove}', dataField: 'quantityApprove', width: '12%', cellsalign: 'right', cellsformat: 'd'},
					{text: '${uiLabelMap.BSComment}', dataField: 'comment', width: '12%'},
					{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: '12%'},
					{text: '${uiLabelMap.BSPromoLevel}', dataField: 'productPromoRuleId', width: '6%'},
					{text: '${uiLabelMap.BSAction}', dataField: 'productPromoActionSeqId', width: '8%'},
					{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '13%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
						}
					},
					{text: '${uiLabelMap.BSCreatedDate}', dataField: 'createdDate', width: '13%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
						}
					},
					{text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', width: '12%'},
		  		"/>
		<@jqGrid id="jqxPromoSettelementDetail" customTitleProperties="${uiLabelMap.BSDetailItem}" 
				url="jqxGeneralServicer?sname=JQListProductPromoSettlementDetail&promoSettlementId=${promoSettlement.promoSettlementId}" columnlist=columnlist dataField=dataField 
				viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
				customcontrol1="" mouseRightMenu="false" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"/>
	</div>
</div>

<div style="text-align:right">
	<#if hasOlbPermission("MODULE", "PROMOSETTLEMENT_APPROVE", "") && ("PSETTLE_CANCELLED" != promoSettlement.statusId)>
		<div class="row-fluid container-approve">
			<div class="span6">
				<#if currentStatusId?exists>
					<#if currentStatusId == "PSETTLE_CREATED">
						<span class="widget-toolbar none-content">
							<#--
							<a class="btn btn-primary btn-mini" href="javascript:OlbPromoSettleView.processProductPromo();">
								<i class="fa fa-check">${uiLabelMap.BSProcess}</i></a>
			              	<a class="btn btn-danger btn-mini" href="javascript:OlbPromoSettleView.cancelProductPromo();">
								<i class="fa fa-times">${uiLabelMap.BSApproveCancel}</i></a>
							<button id="btnCalculateResult" class="btn btn-small btn-primary"><i class="fa fa-calculator"></i>${uiLabelMap.BSCalculate}</button>
							-->
							<form name="PromoProcess" method="post" action="<@ofbizUrl>changePromoSettleStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="PSETTLE_PROCESSING">
				                <input type="hidden" name="promoSettlementId" value="${promoSettlement.promoSettlementId?if_exists}">
			              	</form>
							<form name="PromoCancel" method="post" action="<@ofbizUrl>changePromoSettleStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="PSETTLE_CANCELLED">
				                <input type="hidden" name="promoSettlementId" value="${promoSettlement.promoSettlementId?if_exists}">
				                <input type="hidden" name="changeReason" id="changeReason" value="" />
			              	</form>
						</span>
					<#elseif currentStatusId == "PSETTLE_PROCESSING">
						<span class="widget-toolbar none-content">
							<#--
							<a class="btn btn-primary btn-mini" href="javascript:OlbPromoSettleView.approveProductPromo();">
								<i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
			              	<a class="btn btn-danger btn-mini" href="javascript:OlbPromoSettleView.cancelProductPromo();">
								<i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
							<button id="btnCalculateResult" class="btn btn-small btn-primary"><i class="fa fa-calculator"></i>${uiLabelMap.BSCalculate}</button>
							-->
							<form name="PromoApprove" method="post" action="<@ofbizUrl>changePromoSettleStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="PSETTLE_APPROVED">
				                <input type="hidden" name="promoSettlementId" value="${promoSettlement.promoSettlementId?if_exists}">
			              	</form>
							<form name="PromoCancel" method="post" action="<@ofbizUrl>changePromoSettleStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="PSETTLE_CANCELLED">
				                <input type="hidden" name="promoSettlementId" value="${promoSettlement.promoSettlementId?if_exists}">
				                <input type="hidden" name="changeReason" id="changeReason" value="" />
			              	</form>
						</span>
					<#elseif currentStatusId == "PSETTLE_APPROVED">
						<span class="widget-toolbar none-content">
							<#--<a class="btn btn-primary btn-mini" href="javascript:OlbPromoSettleView.completeProductPromo();">
								<i class="icon-ok open-sans">${uiLabelMap.BSApproveComplete}</i></a>
							<button id="btnCalculateResult" class="btn btn-small btn-primary"><i class="fa fa-calculator"></i>${uiLabelMap.BSCalculate}</button>-->
							<form name="PromoComplete" method="post" action="<@ofbizUrl>changePromoSettleStatus</@ofbizUrl>">
			                	<input type="hidden" name="statusId" value="PSETTLE_COMPLETED">
				                <input type="hidden" name="promoSettlementId" value="${promoSettlement.promoSettlementId?if_exists}">
			              	</form>
						</span>
					</#if>
				</#if>
			</div><!--.span6-->
		</div>
	</#if>
</div>

<#if hasOlbPermission("MODULE", "PROMOSETTLEMENT_ADDITEM", "") && currentStatusId?exists && currentStatusId == "PSETTLE_PROCESSING">
<#include "promotionSettlementExtAddItemPopup.ftl"/>
</#if>

<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>


<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true/>
<script type="text/javascript">
	var OlbPromoSettleView = (function(){
		var calculateResult = function(){
			$.ajax({
				type: 'POST',
				url: 'calculatePromoSettlement',
				data: {
					promoSettlementId: "${promoSettlement.promoSettlementId}"
				},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, "default", function(){
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	$("#jqxPromoSettelementResult").jqxGrid("updatebounddata");
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		var updateNumberApproveSettleResult = function() {
			var listProd = getListProductAll();
			if (listProd.length > 0) {
				var dataMap = {
					promoSettlementId: "${promoSettlement.promoSettlementId}"
				};
				dataMap.listItems = JSON.stringify(listProd);
				
				$.ajax({
					type: 'POST',
					url: 'updateNumberApproveSettleResult',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(){
							$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
				        	$("#jqxNotification").jqxNotification("open");
				        	
				        	$("#jqxPromoSettelementResult").jqxGrid("updatebounddata");
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				return false;
			}
		};
		var updateNumberAcceptSettleResult = function() {
			var listProd = getListProductAll2();
			if (listProd.length > 0) {
				var dataMap = {
					promoSettlementId: "${promoSettlement.promoSettlementId}"
				};
				dataMap.listItems = JSON.stringify(listProd);
				
				$.ajax({
					type: 'POST',
					url: 'updateNumberAcceptSettleResult',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(){
							$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
				        	$("#jqxNotification").jqxNotification("open");
				        	
				        	$("#jqxPromoSettelementResult").jqxGrid("updatebounddata");
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				return false;
			}
		};
		var getListProductAll = function(){
			var data = $("#jqxPromoSettelementResult").jqxGrid("getboundrows");
			if (typeof(data) == 'undefined') {
				jOlbUtil.alert.info("Error check data");
			}
			
			var listProd = [];
			$.each(data, function (key, value){
				if (typeof(value) != 'undefined' && typeof(value.promoSettlementResultId) != 'undefined') {
					var prodItem = {
						promoSettlementResultId: value.promoSettlementResultId,
						quantityApprove: OlbCore.isNotEmpty(value.quantityApprove) ? value.quantityApprove : 0,
						amountApprove: OlbCore.isNotEmpty(value.amountApprove) ? value.amountApprove : 0,
						comment: OlbCore.isNotEmpty(value.comment) ? value.comment : "",
					};
					listProd.push(prodItem);
				}
			});
			return listProd;
		};
		var getListProductAll2 = function(){
			var data = $("#jqxPromoSettelementResult").jqxGrid("getRows");
			if (typeof(data) == 'undefined') {
				jOlbUtil.alert.info("Error check data");
			}
			
			var listProd = [];
			$.each(data, function (key, value){
				if (typeof(value) != 'undefined' && typeof(value.promoSettlementResultId) != 'undefined') {
					var prodItem = {
						promoSettlementResultId: value.promoSettlementResultId,
						productId: value.productId,
						partyId: value.partyId,
						quantityAccept: typeof(value.quantityAccept) != 'undefined' && value.quantityAccept != null ? value.quantityAccept : 0,
						amountAccept: typeof(value.amountAccept) != 'undefined' && value.amountAccept != null ? value.amountAccept : 0,
						comment: typeof(value.comment) != 'undefined' && value.comment != null ? value.comment : "",
					};
					listProd.push(prodItem);
				}
			});
			return listProd;
		};
		var processProductPromo = function(){
			jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToProcess}", function() {
            	document.PromoProcess.submit();
            });
			
		};
		var approveProductPromo = function(){
			jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToApprove}", function() {
				var data = $("#jqxPromoSettelementResult").jqxGrid("getboundrows");
				if (typeof(data) == 'undefined') {
					jOlbUtil.alert.info("Error check data");
				}
				
				var hasQuantity = false;
				$.each(data, function (key, value){
					if (typeof(value) != 'undefined' && typeof(value.promoSettlementResultId) != 'undefined') {
						if (OlbCore.isNotEmpty(value.quantityAccept) || OlbCore.isNotEmpty(value.amountAccept)) {
							hasQuantity = true;
							return false;
						}
					}
				});
				if (!hasQuantity) {
					jOlbUtil.alert.error("${uiLabelMap.BSAcceptValueIsEmpty}");
				} else {
					document.PromoApprove.submit();
				}
            });
			
		};
		var completeProductPromo = function(){
			jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToComplete}", function() {
            	document.PromoComplete.submit();
            });
		};
		var cancelProductPromo = function(){
			jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToCancelNotAccept}", function() {
            	document.PromoCancel.submit();
            });
		};
		var openWindowAddPromoSettleItem = function(){
			$("#alterpopupWindowPromoSettleAddItem").jqxWindow("open");
		};
		return {
			calculateResult: calculateResult,
			processProductPromo: processProductPromo,
			approveProductPromo: approveProductPromo,
			cancelProductPromo: cancelProductPromo,
			completeProductPromo: completeProductPromo,
			openWindowAddPromoSettleItem: openWindowAddPromoSettleItem,
			updateNumberApproveSettleResult: updateNumberApproveSettleResult,
			updateNumberAcceptSettleResult: updateNumberAcceptSettleResult,
		}
	}());
</script>