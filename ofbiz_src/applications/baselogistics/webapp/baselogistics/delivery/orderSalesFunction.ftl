<#include "component://baselogistics/webapp/baselogistics/delivery/script/orderSalesFunctionScript.ftl"/>
<div id="reasonPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ChangeOrderStatusToHeld}</div>
	<div class='form-window-container'>
		<div class='form-window-content padding-top20'>
	        <div class="row-fluid margin-bottom10">
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.Reason} </div>
				</div>
				<div class="span7">
					<div id="enumId" style="width: 100%;" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 10px !important;"><a href="javascript:SalesFunctionObj.addReason();"><i class="icon-plus-sign"></i></a></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4" style="text-align: right">
					<div><div class="asterisk">${uiLabelMap.Description}</div></div>
				</div>
				<div class="span7">	
					<div class="green-label">
						<textarea rows="3" id="statusDescription" style="resize: none;margin-top: 0px !important; width: 288px"></textarea>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
	            <button id="holdCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            <button id="holdSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="returnOrderPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ReceiveReturn}</div>
	<div class='form-window-container'>
		<div class='form-window-content padding-top10'>
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.DAOrderId}</div></div>
					<div class="span7"><div id="orderId" class="green-label">${orderHeader.orderId}</div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.Reason}</div></div>
					<div class="span7"><div id="orderReturnReasonId" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.ProductStatus}</div></div>
				    <div class="span7"><div id="orderInventoryStatusId" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReturnToFacility}</div></div>
					<div class="span7"><div id="orderFacilityId" class="green-label"></div></div>
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.ActualDeliveredDate}</div></div>
				    <div class="span7"><div id="deliveredReturnDate" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="row-fluid">
					<div class="span4" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReceivedDate}</div></div>
				    <div class="span7"><div id="orderReturnDate" class="green-label"></div></div>
				</div>
			</div>
			<div class="form-action popup-footer">
	            <button id="returnCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            <button id="returnSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="addReasonWindow" class="hide popup-bound">
	<div> ${uiLabelMap.AddNewOrderHoldingReason} </div>
	<input id="enumTypeId" value="ORDER_PENDING_CODE" type="hidden"/>
	<div class='form-window-container'>
		<div class='form-window-content padding-top20'>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.ReasonCode} </div>
				</div>
				<div class="span7">	
					<input id="enumCode" style="width: 100%;" type="text"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div style="margin-right: 10px"> ${uiLabelMap.Description} </div>
				</div>
				<div class="span7">	
					<textarea id="reasonDescription" rows='3' style="resize: none;margin-top: 0px !important; width: 293.5px"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
            <button id="addReasonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="addReasonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="selectSupplierWindow" class="hide popup-bound">
	<div> ${uiLabelMap.SelectSupplier} </div>
	<div class='form-window-container'>
		<div class='form-window-content padding-top20'>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.Supplier} </div>
				</div>
				<div class="span7">	
					<div id="supplierId"><div id="supplierPartyGrid" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.Facility} </div>
				</div>
				<div class="span7">	
					<div id="supplierFacilityId"><div id="supplierFacilityGrid" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.Address} </div>
				</div>
				<div class="span7">	
					<div id="supplierFacilityCTMId"><div id="contactMechGrid" class="green-label"></div></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.RequireDeliveryDate} </div>
				</div>
				<div class="span7">	
					<div id="requiredDeliveryDate"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.StartShipDate} </div>
				</div>
				<div class="span7">	
					<div id="startShipDateFromVendor"></div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
            <button id="chooseCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="chooseSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<@jqOlbCoreLib hasCore=false hasGrid=true hasDropDownButton=true/>
<script type="text/javascript">
	var configSupplierDelivery = {
		widthButton: '300px',
		showdefaultloadelement: false,
		autoshowloadelement: false,
		dropDownHorizontalAlignment: 'left',
		filterable: false,
		pageable: true,
		showfilterrow: false,
		datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
		columns: [
			{text: '${StringUtil.wrapString(uiLabelMap.SupplierId)}', datafield: 'partyId', width: '30%'},
			{text: '${StringUtil.wrapString(uiLabelMap.SupplierName)}', datafield: 'fullName'}
		],
		useUrl: true,
		root: 'results',
		url: 'JQGetPartiesSupplier',
		useUtilFunc: true,
		selectedIndex: 0,
		key: 'partyId',
		description: 'fullName',
	};
	new OlbDropDownButton($("#supplierId"), $("#supplierPartyGrid"), null, configSupplierDelivery, []);
	
	$("#supplierPartyGrid").on("bindingcomplete", function (event) {
		var configSupplierFacility = {
			widthButton: '300px',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'left',
			filterable: false,
			pageable: true,
			showfilterrow: false,
			datafields: [{name: 'facilityId', type: 'string'}, {name: 'facilityName', type: 'string'}],
			columns: [
				{text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityId', width: '30%'},
				{text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName'}
			],
			useUrl: true,
			root: 'results',
			url: 'getListFacilityBySupplier&orderId='+orderId+'&supplierPartyId=' + jOlbUtil.getAttrDataValue('supplierId'),
			useUtilFunc: true,
			selectedIndex: 0,
			key: 'facilityId',
			description: 'facilityName',
		};
		new OlbDropDownButton($("#supplierFacilityId"), $("#supplierFacilityGrid"), null, configSupplierFacility, []);
		
		$("#supplierFacilityGrid").on("bindingcomplete", function (event) {
			var configSupplierDelivery = {
				widthButton: '300px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'left',
				filterable: false,
				pageable: true,
				showfilterrow: false,
				datafields: [{name: 'contactMechId', type: 'string'}, {name: 'address1', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.ContactMechId)}', datafield: 'contactMechId', width: '30%'},
					{text: '${StringUtil.wrapString(uiLabelMap.Address)}', datafield: 'address1'}
				],
				useUrl: true,
				root: 'results',
				url: 'JQGetFacilityContactMechs&facilityId='+ jOlbUtil.getAttrDataValue('supplierFacilityId')+'&contactMechPurposeTypeId=SHIP_ORIG_LOCATION',
				useUtilFunc: true,
				selectedIndex: 0,
				key: 'contactMechId',
				description: 'address1',
			};
			new OlbDropDownButton($("#supplierFacilityCTMId"), $("#contactMechGrid"), null, configSupplierDelivery, []);
		});
	});
	if (orderItemData == undefined){
		<#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId?if_exists)), null, null, null, false) />
		var orderItemData = new Array();
		<#list orderItems as item>
			var row = {};
			row['orderId'] = "${item.orderId}";
			var tmp = null;
			<#if item.estimatedDeliveryDate?has_content>
				row['estimatedDeliveryDate'] = "${item.estimatedDeliveryDate.getTime()}";
				tmp = "${item.estimatedDeliveryDate.getTime()}";
			<#else>
				if (tmp){
					row['estimatedDeliveryDate'] = tmp;
				} else {
					<#if item.shipBeforeDate?has_content>
						row['estimatedDeliveryDate'] = '${item.shipBeforeDate.getTime()}';
					<#else>
						row['estimatedDeliveryDate'] = null;
					</#if>
				}
			</#if>
			var tmp1 = null;
			<#if item.shipBeforeDate?has_content>
				row['shipBeforeDate'] = '${item.shipBeforeDate.getTime()}';	
				tmp1 = '${item.shipBeforeDate.getTime()}';
			<#else>
				if (tmp1){
					row['shipBeforeDate'] = tmp1;	
				} else {
					row['shipBeforeDate'] = null;	
				}
			</#if>
			orderItemData.push(row);
		</#list>
	}
	var requireDeliveryDate = new Date(parseInt(orderItemData[0].estimatedDeliveryDate));
</script>
<script type="text/javascript" src="/logresources/js/delivery/orderSalesFunction.js"></script>
