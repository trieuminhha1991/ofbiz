<#if returnHeader?exists>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">

	var uomData = [<#list uomList as uomItem>{
		uomId: '${uomItem.uomId}',
		description: "${StringUtil.wrapString(uomItem.get("description", locale))}"
	},</#list>];
	
	<#assign reasonList = delegator.findByAnd("ReturnReason", null, null, false)/>
	var reasonData = [<#list reasonList as reasonItem>{
		reasonId: '${reasonItem.returnReasonId}',
		description: "${StringUtil.wrapString(reasonItem.get("description", locale))}"
	},</#list>];
	
	
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, false) />
	var statusData = [<#list statusList as statusItem>{
		statusId: '${statusItem.statusId}',
		description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list>];
	
	
	<#assign statusExpectedList = delegator.findByAnd("StatusItem", {"statusTypeId" : "INV_SERIALIZED_STTS"}, null, false) />
	var statusExpectedData = [<#list statusExpectedList as statusItem>{
		statusId: '${statusItem.statusId}',
		description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list>];
</script>
<#assign fromPartyNameView = delegator.findOne("PartyNameView", {"partyId" : returnHeader.fromPartyId}, false)/>
<#assign toPartyNameView = delegator.findOne("PartyNameView", {"partyId" : returnHeader.toPartyId}, false)/>
<div class="row-fluid">
	<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
		<div class=" span12 row margin_left_10 row-desc">
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.ReturnOrderId}:</label>
					<div class="controls-desc">
						<span>${returnHeader.returnId}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.ReturnFrom}:</label>
					<div class="controls-desc">
						<span>${fromPartyNameView.groupName?if_exists} ${fromPartyNameView.firstName?if_exists} ${fromPartyNameView.middleName?if_exists} ${fromPartyNameView.lastName?if_exists}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.Receiver}:</label>
					<div class="controls-desc">
					<span>${toPartyNameView.groupName?if_exists} ${toPartyNameView.firstName?if_exists} ${toPartyNameView.middleName?if_exists} ${toPartyNameView.lastName?if_exists}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAStatus}:</label>
					<div class="controls-desc">
						<#assign currentStatusId = returnHeader.statusId/>
						<#if currentStatusId?exists && currentStatusId?has_content>
							<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
							<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
		                </#if>
					</div>
				</div>
				<#if currentStatusId?exists && currentStatusId?has_content>
					<#assign returnStatuses = returnHeader.getRelated("ReturnStatus", null, null, false)>
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
					<label class="control-label-desc">${uiLabelMap.DAEntryDate}:</label>
					<div class="controls-desc">
						<span>
							<#if returnHeader.entryDate?has_content>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnHeader.entryDate, "dd/MM/yyyy", locale, timeZone)!}
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.ReturnAddress}:</label>
					<div class="controls-desc">
						<span>
							${returnHeader.originContactMechId?if_exists}
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.FacilityToReceive}:</label>
					<div class="controls-desc">
						<span>
							${returnHeader.destinationFacilityId?if_exists}
						</span>
					</div>
				</div>
				<#assign returnReqsGV = delegator.findByAnd("ReturnRequirementCommitment", {"returnId" : returnHeader.get("returnId")}, null, false)/>
				<#if returnReqsGV?exists>
				<#assign returnReqs = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(returnReqsGV, "requirementId", true)>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAReturnRequirement}:</label>
					<div class="controls-desc">
						<span>
							<#list returnReqs as returnReq>
								<a href="<@ofbizUrl>viewReturnProductReq?requirementId=${returnReq}</@ofbizUrl>">${returnReq}</a><#if returnReq_has_next>, </#if>
							</#list>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DACreatedBy}:</label>
					<div class="controls-desc">
						<span>${returnHeader.createdBy?if_exists}</span>
					</div>
				</div>
				</#if>
			</div><!--.span6-->
		</div><!--.row-fluid-->
		<div class="row-fluid">
			<div class="span12">
				<#assign dataField="[{ name: 'reqItemSeqId', type: 'string' },
			               		{ name: 'productId', type: 'string' },
			               		{ name: 'expireDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'quantityUomId', type: 'string'},
			               		{ name: 'returnQuantity', type: 'number', formatter: 'integer'},
			               		{ name: 'quantityAccepted', type: 'number', formatter: 'integer'},
			               		{ name: 'returnReasonId', type: 'string'},
			               		{ name: 'statusId', type: 'string'},
			               		{ name: 'expectedItemStatus', type: 'string'},
			               		{ name: 'receivedQuantity', type: 'number', formatter: 'integer'},
			               		{ name: 'alterQuantity', type: 'number', formatter: 'integer'},
			               		{ name: 'alterQuantityUomId', type: 'string'}]"/>
				<#assign columnlist="{ text: '${uiLabelMap.DASeqId}', dataField: '', width: '5%', columntype: 'number', 
				                      cellsrenderer: function (row, column, value) {
				                          return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
				                      }
								},
							 	{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '15%'},
							 	{ text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
							 	{ text: '${uiLabelMap.DAQuantity}', dataField: 'returnQuantity', width: '12%'},
							 	{ text: '${uiLabelMap.DAQuantityUomId}', dataField: 'quantityUomId', width: '7%',
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < uomData.length; i++){
			    							if (value == uomData[i].uomId){
			    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
							 	},
							 	{ text: '${uiLabelMap.DAReason}', dataField: 'returnReasonId', minwidth: '20%',
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < reasonData.length; i++){
			    							if (value == reasonData[i].reasonId){
			    								return '<span title = ' + reasonData[i].description +'>' + reasonData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
							 	},
							 	{ text: '${uiLabelMap.DAReceivedQuantity}', dataField: 'receivedQuantity', width: '12%'},
							 	{ text: '${uiLabelMap.DAReqQuantity}', dataField: 'alterQuantity', width: '10%'},
							 	{ text: '${uiLabelMap.DAReqQuantityUom}', dataField: 'alterQuantityUomId',width: '7%',
							 		cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < uomData.length; i++){
			    							if (value == uomData[i].uomId){
			    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									}
								}"/>
				<@jqGrid clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
						viewSize="30" showtoolbar="false" editmode="click" filtersimplemode="true" 
						url="jqxGeneralServicer?sname=JQGetListReturnItem&returnId=${returnHeader.returnId?if_exists}" 
						mouseRightMenu="true" contextMenuId="contextMenu"/>
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
			<#if companyId == returnHeader.toPartyId>
				${screens.render("component://basesalesmtl/widget/DistributorScreens.xml#ReceiveReturnAjax")}
			<#else>
				${screens.render("component://basesalesmtl/widget/DistributorScreens.xml#ReceiveReturnDisAjax")}
			</#if>
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
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
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
		//$("#jqxgrid").jqxGrid('updatebounddata');
		location.reload();
	});
</script>
<#else>
	<div class="alert alert-info">
		<label>${uiLabelMap.DAThisRequirementNotAvaiable}</label>
	</div>
</#if>