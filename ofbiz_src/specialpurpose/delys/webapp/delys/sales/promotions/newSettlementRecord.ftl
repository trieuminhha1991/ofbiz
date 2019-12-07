<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">${uiLabelMap.DACreateNewSettlementPromotions}</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DACreateNewSettlementPromotions}" data-placement="bottom">1</span>
			</li>

			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAOrderConfirmation}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<#--<div class="span1 align-right" style="padding-top:5px">
		<a href="<@ofbizUrl>emptyCartDis</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAClearOrder}" data-placement="bottom" class="no-decoration"><i class="icon-trash icon-only" style="font-size:16pt"></i></a>
	</div>-->
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />

	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<form class="form-horizontal basic-custom-form form-size-mini" id="newSettlementRecord" name="newSettlementRecord" method="post" action="<@ofbizUrl>newSettlementRecord</@ofbizUrl>">
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="desiredDeliveryDate">${uiLabelMap.DAFromDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" event="" action="" 
										value="${parameters.fromDate?if_exists}" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" 
										shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
										timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
										isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="orderName">${uiLabelMap.DAToDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="toDate" id="toDate" event="" action="" 
										value="${parameters.toDate?if_exists}" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" 
										shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
										timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
										isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
								</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<button type="button" class="btn btn-small btn-primary" onClick="javaScript: updateJqxGridPOSR();"><i class="fa fa-search"> </i>${uiLabelMap.DASearch}</button>
						</div>
					</div><!--.span6-->
				</div><!--.row-fluid-->
			</form>
			
			<div class="row-fluid">
				<div class="span12">
					<#assign dataField="[{ name: 'orderId', type: 'string' },
			               		{ name: 'orderDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'orderItemSeqId', type: 'string'}, 
			               		{ name: 'productPromoId', type: 'string'},
			               		{ name: 'promoName', type: 'string'},
			               		{ name: 'customerId', type: 'string'},
			               		{ name: 'sellerId', type: 'string'},
			               		{ name: 'productId', type: 'string'},
			               		{ name: 'quantity', type: 'number'}
			                	]"/>
					<#assign columnlist="{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '100px'},
										 { text: '${uiLabelMap.DAOrderDate}', dataField: 'orderDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
										 { text: '${uiLabelMap.DAOrderItemSeqId}', dataField: 'orderItemSeqId', width: '100px'},
										 { text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '100px'}, 
					              		 { text: '${uiLabelMap.DAPromoName}', dataField: 'promoName', width: '100px'},
					              		 { text: '${uiLabelMap.DACustomerId}', dataField: 'customerId', width: '100px'},
					              		 { text: '${uiLabelMap.DASellerId}', dataField: 'sellerId', width: '160px'},
					              		 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px'},
					              		 { text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '100px'}
					              		"/>
					<#-- Promotion by order settlement record -->
					<@jqGrid id="jqxgridPOSR" defaultSortColumn="orderDate; orderItemSeqId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
							viewSize="20" showtoolbar="false" editmode="click" selectionmode="checkbox" 
							url="jqxGeneralServicer?sname=JQGetListOrderItemForSettle"/>
				</div>
			</div>
		</div>

		<div class="step-pane" id="step2">
			<div class="row-fluid">
				<div class="span12">
					<h4 class="blue">${uiLabelMap.DAListAccepted}</h4>
					<#assign dataFieldTwo="[{ name: 'orderId', type: 'string' },
			               		{ name: 'orderDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'orderItemSeqId', type: 'string'}, 
			               		{ name: 'productPromoId', type: 'string'},
			               		{ name: 'promoName', type: 'string'},
			               		{ name: 'customerId', type: 'string'},
			               		{ name: 'sellerId', type: 'string'},
			               		{ name: 'productId', type: 'string'},
			               		{ name: 'quantity', type: 'number'}
			                	]"/>
					<#assign columnlistTwo="{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '100px'},
										 { text: '${uiLabelMap.DAOrderDate}', dataField: 'orderDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
										 { text: '${uiLabelMap.DAOrderItemSeqId}', dataField: 'orderItemSeqId', width: '100px'},
										 { text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '100px'}, 
					              		 { text: '${uiLabelMap.DAPromoName}', dataField: 'promoName', width: '160px'},
					              		 { text: '${uiLabelMap.DACustomerId}', dataField: 'customerId', width: '180px'},
					              		 { text: '${uiLabelMap.DASellerId}', dataField: 'sellerId', width: '180px'},
					              		 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px'},
					              		 { text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '100px'}
					              		"/>
					<@jqGrid id="jqxgridPOSRAccept" defaultSortColumn="orderDate; orderItemSeqId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlistTwo dataField=dataFieldTwo
							viewSize="20" showtoolbar="false" editmode="click" filtersimplemode="false" 
							url="jqxGeneralServicer?sname=JQGetListOrderItemSettleAccept"/>
				</div>
			</div>
			<br />
			<div class="row-fluid">
				<div class="span12">
					<h4 class="blue">${uiLabelMap.DAListNotAcceptedAndDescription}</h4>
					<#assign dataFieldThree="[{ name: 'orderId', type: 'string' },
			               		{ name: 'orderDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'orderItemSeqId', type: 'string'}, 
			               		{ name: 'productPromoId', type: 'string'},
			               		{ name: 'promoName', type: 'string'},
			               		{ name: 'customerId', type: 'string'},
			               		{ name: 'sellerId', type: 'string'},
			               		{ name: 'productId', type: 'string'},
			               		{ name: 'quantity', type: 'number'},
			               		{ name: 'reason', type: 'string'}
			                	]"/>
					<#assign columnlistThree="{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '100px'},
										 { text: '${uiLabelMap.DAOrderDate}', dataField: 'orderDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
										 { text: '${uiLabelMap.DAOrderItemSeqId}', dataField: 'orderItemSeqId', width: '100px'},
										 { text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '100px'}, 
					              		 { text: '${uiLabelMap.DAPromoName}', dataField: 'promoName', width: '160px'},
					              		 { text: '${uiLabelMap.DACustomerId}', dataField: 'customerId', width: '180px'},
					              		 { text: '${uiLabelMap.DASellerId}', dataField: 'sellerId', width: '180px'},
					              		 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px'},
					              		 { text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '160px'},
					              		 { text: '${uiLabelMap.DAReason}', dataField: 'reason', width: '160px'}
					              		"/>
					<@jqGrid id="jqxgridPOSRReject" defaultSortColumn="orderDate; orderItemSeqId" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlistThree dataField=dataFieldThree
							viewSize="20" showtoolbar="false" editmode="click" filtersimplemode="false" 
							url="jqxGeneralServicer?sname=JQGetListOrderItemSettleReject"/>
				</div>
			</div>
		</div>
	</div>
	
	<hr />
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.DAPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>


<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/select2.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<#-- <div class="row-fluid">
	<div class="span12">
		<h4 class="smaller lighter green"><i class="icon-list"></i>${uiLabelMap.DAPromotionByOrder}</h4>
		<#assign dataField="[{ name: 'productPromoId', type: 'string'},
               		{ name: 'promoName', type: 'string'}
                	]"/>
		<#assign columnlist="{ text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '180px'}, 
		              		 { text: '${uiLabelMap.DAPromoName}', dataField: 'promoName'}
		              		"/>
		Promotion by exhibited settlement record
		<@jqGrid id="jqxgridPESR" defaultSortColumn="orderDate; orderItemSeqId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
				viewSize="20" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
				url="jqxGeneralServicer?sname=JQGetListOrderItem"/>
	</div>
</div> -->

<script type="text/javascript">
	var alterData = new Object();
	function updateJqxGridPOSR() {
		alterData.pagenum = "0";
        alterData.pagesize = "20";
        alterData.noConditionFind = "Y";
        alterData.conditionsFind = "N";
		alterData.fromDate = $("#fromDate").val();
		alterData.toDate = $("#toDate").val();
		$('#jqxgridPOSR').jqxGrid('updatebounddata');
	}
	
	$(function() {
		$('[data-rel=tooltip]').tooltip();
	
		$(".select2").css('width','150px').select2({allowClear:true})
		.on('change', function(){
			$(this).closest('form').validate().element($(this));
		}); 
	
		var $validation = false;
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
				var selectedRowIndexes = $('#jqxgridPOSR').jqxGrid('selectedrowindexes');
				if (selectedRowIndexes.length > 0) {
					var selectedListOrderId = [];
					var selectedListOrderItemSeqId = [];
					for(var index in selectedRowIndexes) {
						var data = $('#jqxgridPOSR').jqxGrid('getrowdata', selectedRowIndexes[index]);
						selectedListOrderId[index] = data.orderId;
						selectedListOrderItemSeqId[index] = data.orderItemSeqId;
					}
					alterData.pagenum = "0";
			        alterData.pagesize = "20";
			        alterData.noConditionFind = "Y";
			        alterData.conditionsFind = "N";
					alterData.dataSelectedOrderId = selectedListOrderId;
					alterData.dataSelectedOrderItemSeqId = selectedListOrderItemSeqId;
					$('#jqxgridPOSRAccept').jqxGrid('updatebounddata');
					$('#jqxgridPOSRReject').jqxGrid('updatebounddata');
				} else {
					bootbox.dialog("${uiLabelMap.DANotYetChooseItem}!", [{
						"label" : "OK",
						"class" : "btn-small btn-primary",
						}]
					);
					return false;
				}
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result) {
				if(result) {
					var acceptRows = $('#jqxgridPOSRAccept').jqxGrid('getrows');
					var rejectRows = $('#jqxgridPOSRReject').jqxGrid('getrows');
					var acceptListStr = "N";
					var rejectListStr = "N";
					for (var i = 0; i < acceptRows.length; i++) {
						acceptListStr += "|OLBIUS|" + acceptRows[i].orderId + "|SUIBLO|" + acceptRows[i].orderItemSeqId;
					}
					for (var i = 0; i < rejectRows.length; i++) {
						var itemReject = rejectRows[i];
						if (itemReject.reason != undefined && itemReject.reason != "") {
							rejectListStr += "|OLBIUS|" + itemReject.orderId + "|SUIBLO|" + itemReject.orderItemSeqId + "|SUIBLO|" + itemReject.reason;
						}
					}
					//var rejectRows = $('#jqxGridPOSRReject').jqxGrid('getrows');
					data = "acceptData=" + acceptListStr + "&rejectData=" + rejectListStr;
					$.ajax({
						type: 'POST',
						url: 'createPromoSettleRecord',
						data: data,
						success: function (data, status, xhr) {
							if(data.responseMessage == "error") {
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").text(data.errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        } else {
					        	window.location.href = "viewPromoSettleRecord?promoSettleRecordId=" + data.promoSettleRecordId;
					        }
						},
						error: function() {
							
						}
					});
				}
			});
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
	});
</script>