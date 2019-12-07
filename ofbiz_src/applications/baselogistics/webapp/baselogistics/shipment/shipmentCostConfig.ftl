<#include "script/shipCostConfigScript.ftl"/>
<#assign dataField="[{ name: 'shipmentCostEstimateId', type: 'string'},
					{ name: 'shipmentMethodTypeId', type: 'string'},
					{ name: 'carrierPartyId', type: 'string'},
					{ name: 'carrierRoleTypeId', type: 'string'},
					{ name: 'productStoreShipMethId', type: 'string'},
					{ name: 'productStoreId', type: 'string'},
					{ name: 'partyId', type: 'string'},
					{ name: 'roleTypeId', type: 'string'},
					{ name: 'geoIdTo', type: 'string'},
					{ name: 'geoIdFrom', type: 'string'},
					{ name: 'weightBreakId', type: 'string'},
					{ name: 'weightUomId', type: 'string'},
					{ name: 'weightUnitPrice', type: 'number'},
					{ name: 'quantityBreakId', type: 'string'},
					{ name: 'quantityUomId', type: 'string'},
					{ name: 'quantityUnitPrice', type: 'number'},
					{ name: 'priceBreakId', type: 'string'},
					{ name: 'priceUomId', type: 'string'},
					{ name: 'priceUnitPrice', type: 'number'},
					{ name: 'orderFlatPrice', type: 'number'},
					{ name: 'orderPricePercent', type: 'number'},
					{ name: 'orderItemFlatPrice', type: 'number'},
					{ name: 'shippingPricePercent', type: 'number'},
					{ name: 'productFeatureGroupId', type: 'string'},
					{ name: 'oversizeUnit', type: 'number'},
					{ name: 'oversizePrice', type: 'number'},
					{ name: 'featurePercent', type: 'number'},
					{ name: 'featurePrice', type: 'number'},
					{ name: 'orderFlatPrice', type: 'number'},
					{ name: 'descShipmentMethod', type: 'string'},
					{ name: 'groupName', type: 'string'},
					{ name: 'storeName', type: 'string'},
					
				   ]"/>
<#assign columnlist="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ConfigId)}', datafield: 'shipmentCostEstimateId', pinned: true, width:150,},
	{ text: '${StringUtil.wrapString(uiLabelMap.FlatPercent)}', datafield: 'orderFlatPrice', width:150, cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
			return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ShipmentMethod)}', datafield: 'shipmentMethodTypeId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value){
	 			for (var i = 0; i < shipmentMethodData.length; i++){
	 				if (value == shipmentMethodData[i].shipmentMethodTypeId){
	 					return '<span>'+shipmentMethodData[i].description+'</span>';
	 				}
	 			}
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.CarrierParty)}', datafield: 'groupName', width:150,
		cellsrenderer: function (row, colum, value){
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductStore)}', datafield: 'storeName', width:150,
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.WeightBreak)}', datafield: 'weightBreakId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value){
	 			var wMax = data.wMax;
	 			var wMin = data.wMin;
	 			return '<span style=\"text-align: right\">'+wMin.toLocaleString('${localeStr}')+' - '+wMax.toLocaleString('${localeStr}')+'</span>';
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.WeightUomId)}', datafield: 'weightUomId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value && weightUomData.length > 0){
	 			for (var i = 0; i < weightUomData.length; i++){
	 				if (value == weightUomData[i].uomId){
	 					return '<span style=\"text-align: right\">'+weightUomData[i].description+'</span>';
	 				}
	 			}
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.WeightUnitPrice)}', datafield: 'weightUnitPrice', width:150,
		cellsrenderer: function (row, colum, value){
			return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.QuantityBreak)}', datafield: 'quantityBreakId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value){
	 			var qMax = data.qMax;
	 			var qMin = data.qMin;
	 			return '<span style=\"text-align: right\">'+qMin.toLocaleString('${localeStr}')+' - '+qMax.toLocaleString('${localeStr}')+'</span>';
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'quantityUomId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value && quantityUomData.length > 0){
	 			for (var i = 0; i < quantityUomData.length; i++){
	 				if (value == quantityUomData[i].uomId){
	 					return '<span style=\"text-align: right\">'+quantityUomData[i].description+'</span>';
	 				}
	 			}
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.QuantityUnitPrice)}', datafield: 'quantityUnitPrice', width:150,
		cellsrenderer: function (row, colum, value){
			return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.PriceBreak)}', datafield: 'priceBreakId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value){
	 			var qMax = data.qMax;
	 			var qMin = data.qMin;
	 			return '<span style=\"text-align: right\">'+qMin.toLocaleString('${localeStr}')+' - '+qMax.toLocaleString('${localeStr}')+'</span>';
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.CurrencyUomId)}', datafield: 'priceUomId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipEstCost').jqxGrid('getrowdata', row);
	 		if (value && priceUomData.length > 0){
	 			for (var i = 0; i < priceUomData.length; i++){
	 				if (value == priceUomData[i].uomId){
	 					return '<span style=\"text-align: right\">'+priceUomData[i].description+'</span>';
	 				}
	 			}
	 		} else {
 				return '<span style=\"text-align: right\">_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.PriceUnitPrice)}', datafield: 'priceUnitPrice', width:150,
		cellsrenderer: function (row, colum, value){
			return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
		}
	},
	"/>
<div id='menu' class="hide">
	<ul>
    	<li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
    	<li><i class="fa fa-trash red"></i>${uiLabelMap.Delete}</li>
	</ul>
</div>

<div id="notifyContainer" >
	<div id="notifyContainer">
	</div>
</div>
<div>	
	<@jqGrid filtersimplemode="true" id="jqxgridShipEstCost" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getShipmentCostEstimated&payToPartyId=${company}" addColumns="flatPercent(java.math.BigDecimal);pmin(java.math.BigDecimal);pmax(java.math.BigDecimal);pprice(java.math.BigDecimal);qprice(java.math.BigDecimal);qmax(java.math.BigDecimal);qmin(java.math.BigDecimal);wprice(java.math.BigDecimal);wmax(java.math.BigDecimal);wmin(java.math.BigDecimal);weightUnitPrice(java.math.BigDecimal);quantityUnitPrice(java.math.BigDecimal);priceUnitPrice(java.math.BigDecimal);wuom;quom;puom;productStoreId;carrierPartyId;carrierRoleTypeId;shipmentMethodId"
		createUrl="jqxGeneralServicer?sname=createShipmentEstimate&jqaction=C" mouseRightMenu="true" contextMenuId="menu" jqGridMinimumLibEnable="false"
		showlist="true" customTitleProperties="ListShipmentCostEstimate"
	/>	
</div>
<div id="alterpopupWindow" class='hide popup-bound'>
	<div>${uiLabelMap.FacilityInformation}</div>
	<div class='form-window-container' id="content">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.FacilityId} </div>
						</div>
						<div class="span7">	
							<input id="facilityId"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.Owner} </div>
						</div>
						<div class="span7">	
							<div id="ownerPartyId" style="width: 100%; margin-left: 0px !important" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.OwnFromDate} </div>
						</div>
						<div class="span7">	
							<div id="fromDate" style="width: 100%;" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.OwnThruDate} </div>
						</div>
						<div class="span7">	
							<div id="thruDate" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.BSPSSalesChannel} </div>
						</div>
						<div class="span7">	
							<div id="productStoreId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Country} </div>
						</div>
						<div class="span7">	
							<div id="countryGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Provinces} </div>
						</div>
						<div class="span7">	
							<div id="provinceGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.County} </div>
						</div>
						<div class="span7">	
							<div id="districtGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.Address} </div>
						</div>
						<div class="span7">	
							<input id="address"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.PhoneNumber} </div>
						</div>
						<div class="span7">	
							<input id="phoneNumber"></input>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.FacilityName} </div>
						</div>
						<div class="span7">	
							<input id="facilityName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.Storekeeper} </div>
						</div>
						<div class="span7">	
							<div id="managerPartyId" style="width: 100%; margin-left: 0px !important" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.ManageFromDate} </div>
						</div>
						<div class="span7">	
							<div id="fromDateManager" style="width: 100%;" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.ManageThruDate} </div>
						</div>
						<div class="span7">	
							<div id="thruDateManager" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.OpenedDate} </div>
						</div>
						<div class="span7">	
							<div id="openedDate" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.FacilityDirectlyUnder} </div>
						</div>
						<div class="span7">	
							<div id="parentFacilityId" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.SquareFootage}</div>
						</div>
						<div class="span7">
							<div id="facilitySizeAdd"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Unit}</div>
						</div>
						<div class="span7">
							<div id="facilitySizeUomId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Avatar}</div>
						</div>
						<div class="span7" style="height: 20px !important">
							<div id="idImagesPath">
								<input type="file" id="imagesPath" name="uploadedFile" class="green-label" accept="image/*"/>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Description}</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block; margin-bottom: 3px;"><input id="description"></input></div>
						</div>
					</div>
				</div>
		   	<div class="form-action popup-footer">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
</div>