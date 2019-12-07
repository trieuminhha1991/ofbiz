<script type="text/javascript">
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${description?if_exists}";
		uomData[${item_index}] = row;
	</#list>
</script>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<div class="row-fluid margin-top10">
	<div class="span12" style="text-align: center;">
		<#if transfer.transferTypeId == "TRANS_INTERNAL">
			<h4 style="text-transform:uppercase;"><b>${uiLabelMap.TransferInternalDetail}</b></h4>
		<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
			<h4 style="text-transform:uppercase;"><b>${uiLabelMap.TransferSalesChannelDetail}</b></h4>
		</#if>
		${uiLabelMap.createDate}: <div class="green-label">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(transfer.createDate, "dd/MM/yyyy", locale, timeZone)!}</div>
	</div>
	<#assign transferShipGroup = delegator.findList("TransferItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId)), null, null, null, false)>
	<#assign originCTM = transferShipGroup.get(0).originContactMechId>
	<#assign destCTM = transferShipGroup.get(0).destContactMechId>
	<#assign originFacilityAddress = delegator.findOne("PostalAddress", {"contactMechId" : originCTM}, true) />
	<#assign destFacilityAddress = delegator.findOne("PostalAddress", {"contactMechId" : destCTM}, true) />
	<#assign originFacility = delegator.findOne("Facility", {"facilityId" : transfer.originFacilityId}, true) />
	<#assign destFacility = delegator.findOne("Facility", {"facilityId" : transfer.destFacilityId}, true) />
	<#assign status = delegator.findOne("StatusItem", {"statusId" : transfer.statusId}, true) />
    <div class="span12 mgt20" style="margin: 0px auto;">
    	<div class="span3">
    	</div>
    	<div class="span10">
			<div class="span6 widget-container-span">
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
    					<div>${uiLabelMap.TransferId}: </div>
					</div>
					<div class="span7">
						<div class="green-label"><b>${transfer.transferId}</b></div>
					</div>
				</div>
				<div class="row-fluid">
    				<div class="span5" style="text-align: right">
        				<div>${uiLabelMap.FacilityFrom}: </div>
    				</div>
    				<div class="span7">
	    				<#if transfer.transferTypeId == "TRANS_INTERNAL">
	    					<div class="green-label">${originFacility.get("facilityName",locale)}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${originFacility.get("facilityName",locale)} GT</div>
		    			</#if>
    				</div>
				</div>
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
	    				<div>${uiLabelMap.FacilityAddress}: </div>
					</div>
					<div class="span7">
	    				<div class="green-label">${originFacilityAddress.get("address1",locale)}</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
	    				<div>${uiLabelMap.EstimatedStartTransfer}: </div>
					</div>
					<div class="span7">
					<div class="green-label">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(transfer.estimatedStartDate, "dd/MM/yyyy", locale, timeZone)!}</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
					    <div>${uiLabelMap.Status}:</div>
					</div>
					<div class="span7">
						<div class="green-label">${status.get("description",locale)}</div>
					</div>
				</div>
				<div class="row-fluid">
    				<div class="span5" style="text-align: right">
    				    <div>${uiLabelMap.FacilityTo}:</div>
    				</div>
    				<div class="span7">
	    				<#if transfer.transferTypeId == "TRANS_INTERNAL">
	    					<div class="green-label">${destFacility.get("facilityName",locale)}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${destFacility.get("facilityName",locale)} MT</div>
		    			</#if>
    				</div>
				</div>
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
	    				<div>${uiLabelMap.FacilityAddress}: </div>
					</div>
					<div class="span7">
	    				<div class="green-label">${destFacilityAddress.get("address1",locale)}</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span5" style="text-align: right">
	    				<div>${uiLabelMap.EstimatedFinishTransfer}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(transfer.estimatedCompletedDate, "dd/MM/yyyy", locale, timeZone)!}</div>
					</div>
				</div>
			</div>
		</div>
    	<div class="span2">
    	</div>
	</div>
	<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, pinned:true, editable: false, groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
	        cellsrenderer: function (row, column, value) {
	            return '<span>' + (value + 1) + '</span>';
	        }
		},
        { text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 150, pinned:true,
			 },
			 { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 150,
			 },
			 { text: '${uiLabelMap.RequiredQuantity}', dataField: 'quantity', width: 120,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
					} else {
						return '<span></span>';
					}
				},
			 },
			 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 80,
				 cellsrenderer: function (row, column, value){
					 for(var i = 0; i < uomData.length; i++){
						if(uomData[i].uomId == value){
							return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
						}
					}
				 }
			 },
			 { text: '${uiLabelMap.Packing}', dataField: 'productPacking', width: 80, filterable: false,
				 cellsrenderer: function(row, column, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					return '<span title=' +'1x'+ data.convertNumber + '>' + '1x'+ data.convertNumber + '</span>'
				},
			 },
			 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'date'},
			 { text: '${uiLabelMap.QuantityDelivered}', dataField: 'quantityDelivered', width: 120,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 { text: '${uiLabelMap.QuantityShipping}', dataField: 'quantityShipping', width: 120,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 { text: '${uiLabelMap.QuantityRemain}', dataField: 'quantityRemain', width: 120,
				 cellsrenderer: function(row, column, value){
					if (value){
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
					} else {
						return '<span style=\"text-align: right\">'+0+'</span>';
					}
				},
			 },
			 "/>
 <#assign dataField="[{ name: 'productId', type: 'string'},
   { name: 'productName', type: 'string'},
   { name: 'quantityUomId', type: 'string'},
   { name: 'productPacking', type: 'string'},
   { name: 'expireDate', type: 'date', other: 'Timestamp'},
   { name: 'quantity', type: 'number'},
   { name: 'quantityDelivered', type: 'number'},
   { name: 'quantityRemain', type: 'number'},
   { name: 'quantityShipping', type: 'number'},
   { name: 'baseQuantityUomId', type: 'string'},
   { name: 'convertNumber', type: 'string'},
   { name: 'transferId', type: 'string'},
   { name: 'transferItemSeqId', type: 'string'},
   ]"/>
	<@jqGrid customTitleProperties="ProductListProduct" filtersimplemode="true" sortdirection="desc" defaultSortColumn="expireDate" sortable="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" addrefresh="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		url="jqxGeneralServicer?sname=getListTransferItem&transferId=${parameters.transferId?if_exists}"
		otherParams="convertNumber:S-getProductConvertNumber(productId,quantityUomId,baseQuantityUomId)<convertNumber>;quantityDelivered,quantityShipping,quantityRemain:S-getCurrentTransferQuantityStatus(transferId,transferItemSeqId)<quantityDelivered,quantityShipping,quantityRemain>"/>
</div>