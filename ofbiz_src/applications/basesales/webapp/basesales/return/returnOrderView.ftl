<#if returnHeader?exists>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)!/>
<#assign reasonList = delegator.findByAnd("ReturnReason", null, null, false)!/>
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, false)!/>
<#assign statusExpectedList = delegator.findByAnd("StatusItem", {"statusTypeId" : "INV_SERIALIZED_STTS"}, null, false) />
<#assign fromPartyNameView = delegator.findOne("PartyFullNameDetailSimple", {"partyId" : returnHeader.fromPartyId}, false)!/>
<#assign toPartyNameView = delegator.findOne("PartyFullNameDetailSimple", {"partyId" : returnHeader.toPartyId}, false)!/>
<#assign checkDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, returnHeader.destinationFacilityId)/>
<#assign grandTotalReturn = Static["com.olbius.basesales.returnorder.ReturnWorker"].getGrandTotalReturn(delegator, returnHeader.returnId)/>
<#assign currencyUomId = returnHeader.currencyUomId?default("")/>
<script type="text/javascript">
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
			{'uomId': '${uomItem.uomId}', 'description': "${StringUtil.wrapString(uomItem.get("description", locale))}"},
		</#list>
	</#if>
	];
	var reasonData = [
	<#if reasonList?exists>
		<#list reasonList as reasonItem>
			{'reasonId': '${reasonItem.returnReasonId}', 'description': "${StringUtil.wrapString(reasonItem.get("description", locale))}"},
		</#list>
	</#if>
	];
	var statusData = [
	<#if statusList?exists>
		<#list statusList as statusItem>
			{'statusId': '${statusItem.statusId}', 'description': "${StringUtil.wrapString(statusItem.get("description", locale))}"},
		</#list>
	</#if>
	];
	var statusExpectedData = [
	<#if statusExpectedList?exists>
		<#list statusExpectedList as statusItem>
			{'statusId': '${statusItem.statusId}', 'description': "${StringUtil.wrapString(statusItem.get("description", locale))}"},
		</#list>
	</#if>
	];
	
	<#assign jqxGridId = "jqxgridReturn3">
	var itemsTabCellclass = function (row, columnfield, value) {
 		var data = $('#${jqxGridId}').jqxGrid('getrowdata', row);
        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
            return 'background-promo';
        }
    }
</script>
<div id="contentNotificationAddSuccess">
</div>
<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-decrease-padding">
				<div class="span12 row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSReturnId}:</label>
							<div class="controls-desc">
								<span><b>${returnHeader.returnId}</b></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.ReturnFrom}:</label>
							<div class="controls-desc">
								<span><b>${fromPartyNameView.fullName?if_exists}</b></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.Receiver}:</label>
							<div class="controls-desc">
								<span><b>${toPartyNameView.fullName?if_exists}</b></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSStatus}:</label>
							<div class="controls-desc">
								<#assign currentStatusId = returnHeader.statusId/>
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content><b>${currentStatus.get("description",locale)}</b></#if>
				                </#if>
							</div>
						</div>
						<#if currentStatusId?exists && currentStatusId?has_content>
							<#assign returnStatuses = Static["com.olbius.basesales.returnorder.ReturnWorker"].getReturnHeaderStatuses(delegator, returnHeader.returnId)/>
							<#if returnStatuses?has_content>
								<div class="control-group">
									<div class="controls-desc">
					                  	<#list returnStatuses as returnStatus>
						                    <#assign loopStatusItem = delegator.findOne("StatusItem", {"statusId" : returnStatus.statusId}, false)/>
						                    <#assign statusUserLogin = delegator.findOne("UserLogin", {"userLoginId" : returnStatus.changeByUserLoginId}, false)/>
						                    <div>
						                      	${loopStatusItem.get("description",locale)} <#if returnStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
						                      	&nbsp;
						                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> 
						                      	[${returnStatus.changeByUserLoginId}]
						                    </div>
					                  	</#list>
									</div>
								</div>
							</#if>
						</#if>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSSalesOrderId}:</label>
							<div class="controls-desc">
								<span>
									<#assign returnItems = delegator.findByAnd("ReturnItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("returnId", returnHeader.returnId, "returnTypeId", "RTN_REFUND"), null, false)!/>
							        <#if returnItems?has_content>
							        	<#assign orderId = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(returnItems)).getString("orderId")!/>
							        	<b><a href="<@ofbizUrl>viewOrder?orderId=${orderId?if_exists}</@ofbizUrl>" target="_blank">${orderId?if_exists}</a></b>
									</#if>
								</span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSReturnDate}:</label>
							<div class="controls-desc">
								<span>
									<#if returnHeader.entryDate?has_content>
										<b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnHeader.entryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</b>
									</#if>
								</span>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSOriginContactMechId}:</label>
							<div class="controls-desc">
								<span>
									<b>${returnHeader.originContactMechId?if_exists}</b>
								</span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.FacilityToReceive}:</label>
							<div class="controls-desc">
								<span>
									<#if returnHeader.destinationFacilityId?exists>
										<#assign destFacility = delegator.findOne("Facility", {"facilityId" : returnHeader.destinationFacilityId}, false)!/>
										<b><#if destFacility?exists>${destFacility.facilityName?if_exists} [${returnHeader.destinationFacilityId?if_exists}]<#else>${returnHeader.destinationFacilityId?if_exists}</#if></b>
									</#if>
								</span>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSGrandTotal}:</label>
							<div class="controls-desc">
								<span>
									<#if grandTotalReturn?exists>
										<b>
											<@ofbizCurrency amount=grandTotalReturn isoCode=currencyUomId/>
										</b>
									</#if>
								</span>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.span12-->
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12">
				<#assign dataField="[{ name: 'reqItemSeqId', type: 'string' },
			               		{ name: 'productId', type: 'string' },
			               		{ name: 'productCode', type: 'string' },
			               		{ name: 'expiredDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'quantityUomId', type: 'string'},
			               		{ name: 'returnQuantity', type: 'number', formatter: 'integer'},
			               		{ name: 'returnPrice', type: 'number'},
			               		{ name: 'returnReasonId', type: 'string'},
			               		{ name: 'statusId', type: 'string'},
			               		{ name: 'expectedItemStatus', type: 'string'},
			               		{ name: 'receivedQuantity', type: 'number', formatter: 'integer'},
			               		{ name: 'isPromo', type: 'string'},
		                	]"/>
		        <#--{ text: '${uiLabelMap.BSExpireDate}', dataField: 'expiredDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},-->
				<#assign columnlist="{ text: '${uiLabelMap.BSSTT}', dataField: '', width: '5%', columntype: 'number', cellclassname: itemsTabCellclass, 
				                      cellsrenderer: function (row, column, value) {
				                          return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
				                      }
								},
							 	{ text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '12%', cellclassname: itemsTabCellclass},
							 	{ text: '${uiLabelMap.BSReason}', dataField: 'returnReasonId', minwidth: '20%', cellclassname: itemsTabCellclass,
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < reasonData.length; i++){
			    							if (value == reasonData[i].reasonId){
			    								return '<span title = ' + reasonData[i].description +'>' + reasonData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
							 	},
							 	{ text: '${uiLabelMap.BSUom}', dataField: 'quantityUomId', width: '10%', cellclassname: itemsTabCellclass,
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < uomData.length; i++){
			    							if (value == uomData[i].uomId){
			    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
							 	},
							 	{ text: '${uiLabelMap.BSReturnPrice}', dataField: 'returnPrice', width: '10%', cellsalign: 'right', cellsformat: 'c', cellclassname: itemsTabCellclass, 
								 	cellsrenderer: function(row, column, value) {
								 		var str = '<div class=\"innerGridCellContent align-right\">';
								 		var currencyUomId = \"${returnHeader.currencyUomId?if_exists}\";
								 		if (typeof(currencyUomId) != 'undefined') {
									 		str += formatcurrency(value, currencyUomId);
								 		} else {
											str += value;
										}
										str += '</div>';
										return str;
								 	}
				 				},
				 				{ text: '${uiLabelMap.BSQuantity}', dataField: 'returnQuantity', width: '10%', cellsalign: 'right', cellclassname: itemsTabCellclass},
							 	{ text: '${uiLabelMap.BSReceivedQty}', dataField: 'receivedQuantity', width: '10%', cellsalign: 'right', cellclassname: itemsTabCellclass},
		              		"/>
				<#if checkDistributor><#assign customcontrol1="fa-download@${uiLabelMap.StockIn}@javascript:receiveFacilityDistributor()"/><#else><#assign customcontrol1=""/></#if>
				 	<#if returnHeader.statusId != "RETURN_RECEIVED">
				 		<@jqGrid id=jqxGridId clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
						viewSize="30" showtoolbar="false" editmode="click" filtersimplemode="true" showtoolbar="true"
						customTitleProperties="BSListProduct"	
						url="jqxGeneralServicer?sname=JQGetListReturnItem&returnId=${returnHeader.returnId?if_exists}" 
						customcontrol1=customcontrol1
						mouseRightMenu="true" contextMenuId="contextMenu"/>
					<#else>
						<@jqGrid id=jqxGridId clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
						viewSize="30" showtoolbar="false" editmode="click" filtersimplemode="true" showtoolbar="true"
						customTitleProperties="BSListProduct"	
						url="jqxGeneralServicer?sname=JQGetListReturnItem&returnId=${returnHeader.returnId?if_exists}" 
						mouseRightMenu="true" contextMenuId="contextMenu"/>
				 	</#if>
			</div>
		</div>
		
		<#if security.hasPermission("RETURNORD_LOOSE_ROLE_UPDATE", session) && returnHeader.statusId == "RETURN_REQUESTED">
			<div class="row-fluid" style="margin-top:15px">
				<div style="text-align:right">
					<span class="widget-toolbar none-content">
						<a class="btn btn-danger btn-mini" href="javascript:enterCancelReturn();" 
		              		style="font-size:13px; padding:0 8px">
							<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
						<a class="btn btn-primary btn-mini" href="javascript:enterAcceptReturn();" 
							style="font-size:13px; padding:0 8px">
							<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
		              	<form name="acceptReturn" method="post" action="<@ofbizUrl>updateReturnOrder</@ofbizUrl>">
				          	<#if returnHeader.returnHeaderTypeId?starts_with("CUSTOMER_")>
				            	<#assign statusId = "RETURN_ACCEPTED">
				          	<#else>
				            	<#assign statusId = "SUP_RETURN_ACCEPTED">
				          	</#if>
				          	<input type="hidden" name="returnId" value="${returnHeader.returnId}" />
				          	<input type="hidden" name="statusId" value="${statusId}" />
				          	<input type="hidden" name="needsInventoryReceive" value="Y" />
				        </form>
				        <form name="cancelReturn" method="post" action="<@ofbizUrl>updateReturnOrder</@ofbizUrl>">
				          	<#if returnHeader.returnHeaderTypeId?starts_with("CUSTOMER_")>
				            	<#assign statusId = "RETURN_CANCELLED">
				          	<#else>
				            	<#assign statusId = "SUP_RETURN_CANCELLED">
				          	</#if>
				          	<input type="hidden" name="returnId" value="${returnHeader.returnId}" />
				          	<input type="hidden" name="statusId" value="${statusId}" />
				          	<input type="hidden" name="needsInventoryReceive" value="${returnHeader.needsInventoryReceive!"N"}" />
				        </form>
					</span>
				</div>
			</div>
		</#if>
	</div>
</div>
<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<div id="jqxNotificationAddSuccess" >
	<div id="notificationAddSuccess"> 
	</div>
</div>
<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.CommonReceive} ${uiLabelMap.DAReturnOrder} [${returnHeader.returnId?if_exists}] ${uiLabelMap.CommonTo} ${uiLabelMap.DAFacility} <span id="nameFacilitySpan"><#if facility?has_content>"${facility.facilityName?default("Not Defined")}" [${facilityId?if_exists}]</#if></span> <span id="nameShipmentSpan">${uiLabelMap.CommonBy} ${uiLabelMap.Shipment} ${shipmentId?if_exists}</span></div>
	<div style="overflow: hidden; position:relative">
		<div id="info_loader" style="overflow: hidden; position: absolute; display: none; left: 45%; top: 25%; " class="jqx-rc-all jqx-rc-all-olbius">
			<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
				<div style="float: left;">
					<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
					<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
				</div>
			</div>
		</div>
		<div id="alterpopupContent" style="overflow-y:auto; height:570px; margin-bottom:15px">
			<#--${screens.render("component://delys/widget/sales/SalesScreens.xml#ReceiveReturnAjax")}-->
		</div>
	</div>
</div>
<script type="text/javascript">
	function receiveProductToFacilityPopup() {
		$("#alterpopupWindow").jqxWindow('open');
	}
</script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	function enterAcceptReturn() {
		bootbox.confirm("${uiLabelMap.DAReasonAcceptReturnOrder}", function(result) {
			if(result) {
				document.acceptReturn.submit();
			}
		});
	}
	function enterCancelReturn() {
		bootbox.confirm("${uiLabelMap.DAReasonCancelReturnOrder}", function(result) {
			if(result) {
				document.cancelReturn.submit();
			}
		});
	}
</script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgridReturn3").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgridReturn3").jqxGrid('updatebounddata');
        }
	});
	// Create Window
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 580, maxHeight: 1200, width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	if ($("#alterCancel").length > 0) {
		$("#alterCancel").jqxButton({width: 100, theme: theme});
	}
	$('#alterpopupWindow').on('close', function (event) {
		//$("#jqxgridReturn3").jqxGrid('updatebounddata');
		location.reload();
	});
</script>
<#include 'receiveFacilityDistributors.ftl'/>
<#else>
	<div class="alert alert-info">
		<label>${uiLabelMap.BSThisRequirementNotAvaiable}</label>
	</div>
</#if>
<#--
{ text: '${uiLabelMap.BSReqQtyUom}', dataField: 'alterQuantityUomId',width: '10%',
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < uomData.length; i++){
			    							if (value == uomData[i].uomId){
			    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
								},
//								{ text: '${uiLabelMap.BSStatus}', dataField: 'statusId',
//							 		cellsrenderer: function(row, column, value){
//										for(var i = 0; i < statusData.length; i++){
//											if(statusData[i].statusId == value){
//												return '<span title=' + value + '>' + statusData[i].description + '</span>'
//											}
//										}
//									}
//							 	},
//							 	{ text: '${uiLabelMap.BSExpectedStatus}', dataField: 'expectedItemStatus',
//							 		cellsrenderer: function(row, column, value){
//										for(var i = 0; i < statusExpectedData.length; i++){
//											if(statusExpectedData[i].statusId == value){
//												return '<span title=' + value + '>' + statusExpectedData[i].description + '</span>'
//											}
//										}
//									}
//							 	},
-->