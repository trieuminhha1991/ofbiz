<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<style type="text/css">
    .bootbox{
        z-index: 99000 !important;
    }
    .modal-backdrop{
        z-index: 89000 !important;
    }
</style>
<script>
	<#assign countryList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var countryData = new Array();
	<#list countryList as geo>
		<#assign geoId = StringUtil.wrapString(geo.geoId) />
		<#assign geoName = StringUtil.wrapString(geo.geoName) />
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName}";
		countryData[${geo_index}] = row;
	</#list>

	<#assign company = Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
	<#assign facTmps = delegator.findList("FacilityContactMechAndRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", company, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION", "stateProvinceGeoId", "VN-HN", "roleTypeId", "LOG_STOREKEEPER", "partyId", userLogin.partyId)), null, null, null, false)>
	var channelFacilityData = new Array();
	<#list facTmps as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		channelFacilityData[${item_index}] = row;
	</#list>

	var parentTypeId = '${parameters.parentTypeId?if_exists}';
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)>
	var faciData = new Array();
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		faciData[${item_index}] = row;
	</#list>
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = new Array();
	<#list currencyUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.abbreviation?if_exists)/>
		row['currencyUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${abbreviation?if_exists}';
		currencyUomData[${item_index}] = row;
	</#list>
	
	<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var packingData = new Array();
	<#list packingUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.abbreviation?if_exists)/>
		row['quantityUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${abbreviation?if_exists}';
		packingData[${item_index}] = row;
	</#list>

	<#assign transferTypes = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "${parameters.parentTypeId?if_exists}"), null, null, null, false)>
	var transferTypeData = new Array();
	<#list transferTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['transferTypeId'] = '${item.transferTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		transferTypeData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "LABEL_ITEM_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	
	<#assign productList = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false) />
	
	var mapProductData = {
		<#list productList as product>
			"${product.productId}": "${StringUtil.wrapString(product.get('description', locale)?if_exists)}",
		</#list>
	};
	
	function getDescriptionByProductId(productId) {
		for ( var x in mapProductData) {
			if (productId == mapProductData[x].productId) {
				return mapProductData[x].description;
			}
		}
	}
	
</script>
<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>
<div id="transferChannelnew" class="tab-pane">
<#assign columnlistProduct1="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 100, align: 'center', editable: false, pinned: true},
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'center', minwidth: 100, editable: false },
				 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 100, align: 'center', cellsformat: 'd', editable: false, filterable: true, filtertype:'range'},
				 { text: '${uiLabelMap.QuantityToTransfer}', dataField: 'quantity', width: 100, align: 'center', cellsalign: 'right', columntype: 'numberinput', filterable: false, editable: true,
                     validation: function (cell, value) {
                         if (value < 0) {
                             return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                         }
                         return true;
                     },
                     createeditor: function (row, cellvalue, editor) {
                         editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
                     },
                     cellsrenderer: function(row, column, value){
                    	 if (value){
                    		 return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
                    	 }
                     },
				 },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomIdToTransfer', width: 80, align: 'center', cellsalign: 'right', filterable: false, columntype: 'dropdownlist', 
					 cellsrenderer: function(row, column, value){
						for(var i = 0; i < packingData.length; i++){
							if(packingData[i].quantityUomId == value){
								return '<span title=' + value + '>' + packingData[i].description + '</span>'
							}
						}
					},
					 initeditor: function (row, cellvalue, editor) {
		                   var packingUomData = new Array();
		                   var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
		                   var packingUomIdArray = data['qtyUomIds'];
		                   for (var i = 0; i < packingUomIdArray.length; i++) {
			                    var packingUomIdItem = packingUomIdArray[i];
			                    var row = {};
			                    row['quantityUomId'] = '' + packingUomIdItem.uomId;
			                    row['description'] = '' + packingUomIdItem.description;
			                    packingUomData[i] = row;
		                   }
		                   var sourceDataPacking =
		                   {
			                   localdata: packingUomData,
			                   datatype: 'array'
		                   };
		                   var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
		                   editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'quantityUomId', placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'
		                   });
					 },
				 },
				 { text: '${uiLabelMap.SummaryATP}', dataField: 'ATP', width: 150, align: 'center', filterable: false, editable: false,
						cellsrenderer: function(row, column, value){
//							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//							for(var i = 0; i < packingData.length; i++){
//								if(packingData[i].quantityUomId == data.quantityUomId){
//									return '<span>' + value.toLocaleString('${localeStr}') +' (' + packingData[i].description +  ')</span>';
//								}
//							}
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
						},
					 },
					 { text: '${uiLabelMap.SummaryQOH}', dataField: 'QOH', width: 150, align: 'center', filterable: false, editable: false,
						cellsrenderer: function(row, column, value){
//							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//							for(var i = 0; i < packingData.length; i++){
//								if(packingData[i].quantityUomId == data.quantityUomId){
//									return '<span>' + value.toLocaleString('${localeStr}') +' (' + packingData[i].description +  ')</span>';
//								}
//							}
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
						},
					 },
				 "/>
<#assign dataFieldProduct1="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
             	{ name: 'productTypeId', type: 'string' },
				{ name: 'ATP', type: 'number' },
             	{ name: 'QOH', type: 'number' },
             	{ name: 'expireDate',  type: 'date', other: 'Timestamp'},
             	{ name: 'quantity', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
             	{ name: 'quantityUomIdToTransfer', type: 'string' },
             	{ name: 'qtyUomIds', type: 'string' }
	 		 	]"/>
</div>
<@jqGrid selectionmode="checkbox" idExisted="true" filtersimplemode="true" width="890" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" id="jqxgridProductChannel" dataField=dataFieldProduct1 columnlist=columnlistProduct1 
	clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" customTitleProperties="ListProductTransfer" rowselectfunction="rowselectfunction(event);"
	url="" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" autoheight="false" height="275" otherParams="qtyUomIds:S-getProductPackingUoms(productId)<listPackingUoms>" offmode="true"/>

<div id="transfer-new" class="tab-pane">
<#assign columnlistProduct="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				 { text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: '12%', align: 'center', editable: false, pinned: true},
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'center', minwidth: '15%', editable: false },
				 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: '12%', align: 'center', cellsformat: 'd', editable: false, filterable: true, filtertype:'range'},
				 { text: '${uiLabelMap.QuantityToTransfer}', dataField: 'quantity', width: '15%', align: 'center', cellsalign: 'right', columntype: 'numberinput', filterable: false, editable: true,
                     validation: function (cell, value) {
                         if (value < 0) {
                             return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                         }
                         return true;
                     },
                     createeditor: function (row, cellvalue, editor) {
                         editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
                     },
                     cellsrenderer: function(row, column, value){
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
                     },
				 },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomIdToTransfer', width: '10%', align: 'center', cellsalign: 'right', filterable: false, columntype: 'dropdownlist', 
					 cellsrenderer: function(row, column, value){
						for(var i = 0; i < packingData.length; i++){
							if(packingData[i].quantityUomId == value){
								return '<span title=' + value + '>' + packingData[i].description + '</span>'
							}
						}
					},
					 initeditor: function (row, cellvalue, editor) {
		                   var packingUomData = new Array();
		                   var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
		                   var packingUomIdArray = data['qtyUomIds'];
		                   for (var i = 0; i < packingUomIdArray.length; i++) {
			                    var packingUomIdItem = packingUomIdArray[i];
			                    var row = {};
			                    row['quantityUomId'] = '' + packingUomIdItem.uomId;
			                    row['description'] = '' + packingUomIdItem.description;
			                    packingUomData[i] = row;
		                   }
		                   var sourceDataPacking =
		                   {
			                   localdata: packingUomData,
			                   datatype: 'array'
		                   };
		                   var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
		                   editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'quantityUomId', placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'
		                   });
					 },
				 },
				 { text: '${uiLabelMap.SummaryATP}', dataField: 'ATP', width: '15%', align: 'center', filterable: false, editable: false,
						cellsrenderer: function(row, column, value){
//							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//							for(var i = 0; i < packingData.length; i++){
//								if(packingData[i].quantityUomId == data.quantityUomId){
//									return '<span>' + value.toLocaleString('${localeStr}') +' (' + packingData[i].description +  ')</span>';
//								}
//							}
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
						},
					 },
					 { text: '${uiLabelMap.SummaryQOH}', dataField: 'QOH', width: '15%', align: 'center', filterable: false, editable: false,
						cellsrenderer: function(row, column, value){
//							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//							for(var i = 0; i < packingData.length; i++){
//								if(packingData[i].quantityUomId == data.quantityUomId){
//									return '<span>' + value.toLocaleString('${localeStr}') +' (' + packingData[i].description +  ')</span>';
//								}
//							}
							return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +'</span>';
						},
					 },
				 "/>
<#assign dataFieldProduct="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
             	{ name: 'productTypeId', type: 'string' },
				{ name: 'ATP', type: 'number' },
             	{ name: 'QOH', type: 'number' },
             	{ name: 'expireDate',  type: 'date', other: 'Timestamp'},
             	{ name: 'quantity', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
             	{ name: 'quantityUomIdToTransfer', type: 'string' },
             	{ name: 'qtyUomIds', type: 'string' }
	 		 	]"/>
</div>
<@jqGrid selectionmode="checkbox" idExisted="true" filtersimplemode="true" width="890" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" id="jqxgridProduct" dataField=dataFieldProduct columnlist=columnlistProduct 
	clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" customTitleProperties="ListProductTransfer" rowselectfunction="rowselectfunction(event);"
	url="jqxGeneralServicer?sname=getListProducts&facilityId=" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" autoheight="false" height="275" otherParams="qtyUomIds:S-getProductPackingUoms(productId)<listPackingUoms>" offmode="true"/>

<div id="transfer-list" class="tab-pane">
<#assign dataFieldTransfer="[{ name: 'transferId', type: 'string' },
			{ name: 'transferTypeId', type: 'string' },
			{ name: 'originFacilityId', type: 'string' },
			{ name: 'originFacilityName', type: 'string' },
			{ name: 'destFacilityId', type: 'string' },
			{ name: 'destFacilityName', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'statusDesc', type: 'string' },
			{ name: 'transferTypeDesc', type: 'string' },
			{ name: 'createDate', type: 'date', other: 'Timestamp' },
			{ name: 'transferDate', type: 'date', other: 'Timestamp' },
			{ name: 'description', type: 'string' },
			]"/>
	<#assign columnlistTransfer="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
			        groupable: false, draggable: false, resizable: false,
			        datafield: '', columntype: 'number', width: 50,
			        cellsrenderer: function (row, column, value) {
			            return '<span style=margin:4px;>' + (value + 1) + '</span>';
			        }
			    },
				{ text: '${uiLabelMap.TransferId}', dataField: 'transferId', width: 120, pinned: true,
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgridTransfer').jqxGrid('getrowdata', row);
								if (data.transferTypeId == 'TRANS_INTERNAL'){
									return '<a style = \"margin-left: 10px\" href=' + 'viewTransfer?transferId=' + data.transferId + '>' +  data.transferId + '</a>';
								} else if (data.transferTypeId == 'TRANS_SALES_CHANNEL'){
									return '<a style = \"margin-left: 10px\" href=' + 'viewSalesChannelTransfer?transferId=' + data.transferId + '>' +  data.transferId + '</a>';
								}
							}
						},
					 { text: '${uiLabelMap.TransferType}', dataField: 'transferTypeId', width: '15%',
						cellsrenderer: function (row, column, value){
							for (var i = 0; i < transferTypeData.length; i++){
								if (transferTypeData[i].transferTypeId == value){
									return '<span>'+transferTypeData[i].description+'<span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityName', width: 150,
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityName', width: 150,
					 },
					 { text: '${StringUtil.wrapString(uiLabelMap.createDate)}', datafield: 'createDate', width: 150, align: 'left', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					 { text: '${uiLabelMap.TransferDate}', dataField: 'transferDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150,
						 cellsrenderer: function (row, column, value){
							 for (var i = 0; i < statusData.length; i++){
								if (statusData[i].statusId == value){
									return '<span>'+statusData[i].description+'<span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 150},
					 "/>
</div>
<#if security.hasPermission("TRANSFER_CREATE", userLogin) && "TRANS_FACILITY" == parameters.parentTypeId>
 <@jqGrid filtersimplemode="true" sortdirection="desc" defaultSortColumn="createDate" sortable="true" id="jqxgridTransfer" addType="popup" dataField=dataFieldTransfer columnlist=columnlistTransfer clearfilteringbutton="true" addrefresh="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
	 url="jqxGeneralServicer?sname=getListTransfer&parentTypeId=${parameters.parentTypeId?if_exists}&transferTypeId=${parameters.transferTypeId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransfer&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransfer&jqaction=U"
	 addColumns="shipAfterDate(java.sql.Timestamp);shipBeforeDate(java.sql.Timestamp);transferId;transferTypeId;originFacilityId;destFacilityId;statusId;description;carrierPartyId;shipmentMethodTypeId;originContactMechId;destContactMechId;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);listProducts(java.util.List);"/>
<#elseif (security.hasPermission("CHANNEL_TRANS_CREATE", userLogin) && "TRANS_OTHER" == parameters.parentTypeId?if_exists) || (security.hasPermission("CHANNEL_TRANS_CREATE", userLogin) && "TRANS_SALES_CHANNEL" == parameters.transferTypeId?if_exists)>
<@jqGrid filtersimplemode="true" sortdirection="desc" defaultSortColumn="createDate" sortable="true" id="jqxgridTransfer" addType="popup" dataField=dataFieldTransfer columnlist=columnlistTransfer clearfilteringbutton="true" addrefresh="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindowChannel" editable="false" 
	 url="jqxGeneralServicer?sname=getListTransfer&parentTypeId=${parameters.parentTypeId?if_exists}&transferTypeId=${parameters.transferTypeId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransfer&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransfer&jqaction=U"
	 addColumns="shipAfterDate(java.sql.Timestamp);shipBeforeDate(java.sql.Timestamp);transferId;transferTypeId;originFacilityId;destFacilityId;statusId;description;carrierPartyId;shipmentMethodTypeId;originContactMechId;destContactMechId;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);listProducts(java.util.List);"
 />
<#else>
<@jqGrid filtersimplemode="true" sortdirection="desc" defaultSortColumn="createDate" sortable="true" id="jqxgridTransfer" addType="popup" dataField=dataFieldTransfer columnlist=columnlistTransfer clearfilteringbutton="true" addrefresh="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="" editable="false" 
	 url="jqxGeneralServicer?sname=getListTransfer&parentTypeId=${parameters.parentTypeId}" createUrl="jqxGeneralServicer?sname=createTransfer&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransfer&jqaction=U"
	 />
</#if>

<div id="alterpopupWindowChannel" class="hide">
	<div class="row-fluid">
		${uiLabelMap.NewChannelTransfer}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.GeneralInfo}
	        </h4>
	        <div class="row-fluid">
	    		<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Facility}: </div>
    					</div>
    					<div class="span7">	
    						<div id="facilityId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.FacilityAddress}: </div>
						</div>
						<div class="span7">	
							<div id="contactMechId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.EstimatedStartDate}: </div>
    					</div>
    					<div class="span7">	
    						<div id="estimatedStartDateChannel" style="width: 100%;"></div>
    					</div>
    				</div>
	    		</div>
	    		<div class="span6 no-left-margin">
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
						</div>
						<div class="span7">	
						</div>
					</div>
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.description}:</div>
						</div>
						<div class="span7">	
							<div style="width: 195px; display: inline-block; margin-bottom:3px"><input id="descChannel"></input></div><a onclick="showEditor()" style="display: inline-block, padding-left: 10px"><i style="padding-left: 10px;" class="icon-edit"></i></a>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.EstimatedArrivalDate}:</div>
						</div>
						<div class="span7">	
							<div id="estimatedArrivalDateChannel" style="width: 100%;"></div>
						</div>
					</div>
	    		</div>
	    		<div>
	    		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProductChannel"></div></div>
	    	    </div>
		    </div>
    	</div>
    	<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addChannelCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addChannelSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>

<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.NewTransfer}
	</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
        	<input type="hidden" id="shipmentMethodTypeId" value="NO_SHIPPING"></input>
        	<input type="hidden" id="carrierPartyId" value="_NA_"></input>
        	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
                ${uiLabelMap.GeneralInfo}
            </h4>
        	<div class="row-fluid">
        		<div class="span6">
        			<div class="row-fluid margin-bottom10">	
        				<div class="span5" style="text-align: right">
        					<div> ${uiLabelMap.TransferType}: </div>
    					</div>
    					<div class="span7">
    						<div id="transferTypeId" style="width: 100%;" class="green-label"></div>
    					</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.FacilityFrom}: </div>
						</div>
						<div class="span7">
							<div id="originFacilityId" style="width: 100%" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.OriginContactMech}: </div>
						</div>
						<div class="span7">
							<div id="originContactMechId" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addOriginFacilityAddress()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.EstimatedStartTransfer}: </div>
						</div>
						<div class="span7">
							<div id="estimatedStartDate" style="width: 100%"></div>
						</div>
					</div>		
        		</div>
        		<div class="span6 no-left-margin">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.description}:</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block; margin-bottom: 3px;"><input id="description"></input></div><a onclick="showEditor()" style="display: inline-block"><i style="padding-left: 24px" class="icon-edit"></i></a>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.FacilityTo}:</div>
						</div>
						<div class="span7">
							<div id="destFacilityId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.DestinationContactMech}:</div>
						</div>
						<div class="span7">
							<div id="destContactMechId" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addDestFacilityAddress()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.EstimatedFinishTransfer}:</div>
						</div>
						<div class="span7">
							<div id="estimatedArrivalDate" style="width: 100%"></div>
						</div>
					</div>		
	    		</div>
        		<div>
        		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProduct"></div></div>
        	    </div>
    	    </div>
    	</div>
    	<div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top20" style="margin-bottom:10px;">
                    <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                </div>
            </div>
        </div>
	</div>
</div>
<div id='Menu' class="hide">
	<ul>
		<#if security.hasPermission("TRANSFER_ADMIN", userLogin)>
	    	<li id='menuAppoveTransfer'>${uiLabelMap.Approve}</li>
    	</#if>
    	<li id='menuDeleteTransfer'>${uiLabelMap.CommonDelete}</li>
	</ul>
</div>
<div id="confirmDeletePopupWindow" class="hide">
	<div>${uiLabelMap.DAAreYouSureDelete}</div>
	<div style="overflow: hidden;">
		<table>
			<tr>
			    <td align="left"><input type="hidden" id="transferId" /> </td>
			</tr>
			<tr>
			    <td style="margin-left: 10px" align="right"><input type="button" id="ConfirmDelete" value="${uiLabelMap.CommonDelete}" class="btn btn-primary"><input id="ConfirmCancel" type="button" value="${uiLabelMap.CommonCancel}" class="btn btn-primary icon-cancel"/></td>
			</tr>
		</table>
	</div>
</div>
<div id="jqxEditorWindow" style="display: none">
	<div id="windowHeader">
		<span>
		    ${uiLabelMap.Description}
		</span>
	</div>
	<div style="overflow: hidden;" id="windowContent">
		<textarea id="editor">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="addPostalAddressWindow" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.NewFacilityAddress}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Facility)}</div>
					</div>  
					<div class="span7">
						<div id="seletedFacilityId" class="green-label"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.LogCommonNational)}</div>
					</div>  
					<div class="span7">
						<div id="countryGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyCity)}</div>
					</div>  
					<div class="span7">
						<div id="stateProvinceGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.CityPostalCode)}</div>
					</div>  
					<div class="span7">
						<input id="postalCode" style=""></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyAddressLine1)}</div>
					</div>  
					<div class="span7">
						<input id="address1">
						</input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.PartyAddressLine2)}</div>
					</div>  
					<div class="span7">
						<input id="address2">
						</input>
					</div>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="newAddrCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
							<button id="newAddrOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	contactMechPurposeTypeId = null;
	$("#addPostalAddressWindow").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newAddrCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	var stateProvinceGeoData = new Array();
	$("#countryGeoId").jqxDropDownList({source: countryData, width: 200, displayMember: "geoName", valueMember: "geoId"});
	$("#countryGeoId").jqxDropDownList('val', 'VNM');
	$("#stateProvinceGeoId").jqxDropDownList({source: stateProvinceGeoData, width: 200, displayMember: "value", valueMember: "id"});
	
	$("#postalCode").jqxInput({width: 195});
	$("#address1").jqxInput({width: 195});
	$("#address2").jqxInput({width: 195});
	updateStateProvince();
	function updateStateProvince(){
		var request = $.ajax({
			  url: "loadGeoAssocListByGeoId",
			  type: "POST",
			  data: {geoId : $("#countryGeoId").val(),
				  },
			  dataType: "json",
			  success: function(data) {
				  var listcontactMechPurposeTypeMap = data["listGeoAssocMap"];
				  var contactMechPurposeTypeId = new Array();
				  var description = new Array();
				  var array_keys = new Array();
				  var array_values = new Array();
				  for(var i = 0; i < listcontactMechPurposeTypeMap.length; i++){
					  
					  for (var key in listcontactMechPurposeTypeMap[i]) {
					      array_keys.push(key);
					      array_values.push(listcontactMechPurposeTypeMap[i][key]);
					  }
					  
				  }
				  
				  var dataTest = new Array();
				  for (var j =0; j < array_keys.length; j++){
							var row = {};
							row['id'] = array_keys[j];
							row['value'] = array_values[j];
							dataTest[j] = row;
				  }
				  if (dataTest.length == 0){
					  var dataEmpty = new Array();
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: true});
					  $("#stateProvinceGeoId").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}'); 
				  } else {
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: false});
					  $("#stateProvinceGeoId").jqxDropDownList({selectedIndex: 0,  source: dataTest, displayMember: 'value', valueMember: 'id'});
					  if ("VNM" == $("#countryGeoId").val()){
						  $("#stateProvinceGeoId").jqxDropDownList('val', "VN-HN");
					  }
				  }
			  }
		}); 
	}
	$("#countryGeoId").on('change', function (event) {
		updateStateProvince();
	});
	$('#addPostalAddressWindow').jqxValidator({
		rules: [
               { input: '#address1', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               { input: '#postalCode', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               ]
    });
	
	$('#addPostalAddressWindow').on('close', function (event) {
		$('#address1').val("");
		$('#address2').val("");
		$('#postalCode').val("");
	});
	
	$("#newAddrOkButton").click(function (event) {
		if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseCountryBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseProvinceBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else {
			var validate = $('#addPostalAddressWindow').jqxValidator('validate');
			if (validate){
				var facilityIdTemp = null;
				if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
					facilityIdTemp = $("#originFacilityId").val();
				} else {
					facilityIdTemp = $("#destFacilityId").val();
				}
				if (facilityIdTemp){
					bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", "${uiLabelMap.CommonCancel}", "${uiLabelMap.CommonSave}",function(result){ 
						if(result){
							jQuery.ajax({
								url: "createFacilityContactMechPostalAddress",
								type: "POST",
								async: false,
								data: {
									facilityId: facilityIdTemp,
									contactMechTypeId: "POSTAL_ADDRESS", 
									contactMechPurposeTypeId : contactMechPurposeTypeId, 
									address1: $('#address1').val(), 
									address2: $('#address2').val(),
									countryGeoId: $('#countryGeoId').val(),
									stateProvinceGeoId: $('#stateProvinceGeoId').val(),
									postalCode: $('#postalCode').val(),
									},
								success: function(res) {
									$('#addPostalAddressWindow').jqxWindow('close');
									if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
										update({
											facilityId: $("#originFacilityId").val(),
											contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
									} else {
										update({
											facilityId: $("#destFacilityId").val(),
											contactMechPurposeTypeId: "SHIPPING_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
									}
					       	  	}
							});
						}
					});
				} else {
					bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
				}
			}
		}
	});
	
	$('#document').ready(function(){
		loadDataConfigPacking();
		$("#description").jqxInput({placeHolder: ". . .", height: 20, width: '195', minLength: 1});
		$("#descChannel").jqxInput({placeHolder: ". . .", height: 20, width: '195', minLength: 1});
		
		$("#jqxEditorWindow").jqxWindow({
			maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true,  isModal: true, autoOpen: false, initContent : function(){
				$('#editor').jqxEditor({
		            height: '85%',
		            width: '100%',
		            theme: theme,
		        });
			},
		});
	});
	
	function addOriginFacilityAddress(){
		var originFacilityId = $('#originFacilityId').val();
		if (originFacilityId){
			$('#seletedFacilityId').text($('#originFacilityId').text());
			contactMechPurposeTypeId = "SHIP_ORIG_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	
	function addDestFacilityAddress(){
		var destFacilityId = $('#destFacilityId').val();
		if (destFacilityId){
			$('#seletedFacilityId').text($('#destFacilityId').text());
			contactMechPurposeTypeId = "SHIPPING_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	
	$('#addPostalAddressWindow').on('close', function (event) { 
		$('#addPostalAddressWindow').jqxValidator('hide');
	}); 
	
	var listConfigPacking = [];
	function loadDataConfigPacking(){
		listConfigPacking = [];
		$.ajax({
			url: "loadDataConfigPacking",
			type: "POST",
			data: {},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			listConfigPacking = data["listConfigPacking"];
		});
	}
	
	function rowselectfunction(event){
		var gridId = $(event.currentTarget).attr("id");
		if(typeof event.args.rowindex != 'number'){
            var tmpArray = event.args.rowindex;
            for(i = 0; i < tmpArray.length; i++){
                if(checkRequiredTranferProductByFacilityToFacility(tmpArray[i]), gridId){
                    $('#'+gridId).jqxGrid('clearselection');
                    break; // Stop for first item
                }
            }
        }else{
            if(checkRequiredTranferProductByFacilityToFacility(event.args.rowindex, gridId)){
                $('#'+gridId).jqxGrid('unselectrow', event.args.rowindex);
            }
        }
    }
	
	function checkRequiredTranferProductByFacilityToFacility(rowindex, gridId){
		var data = $('#'+gridId).jqxGrid('getrowdata', rowindex);
		if(data == undefined){
            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#"+gridId).jqxGrid('begincelledit', rowindex, "quantity");
                    }
                }]
            );
            return true;
		}else{
			var quantity = data.quantity;
			var productId = data.productId;
			var quantityUomIdToTransfer = data.quantityUomIdToTransfer;
			var atp = data.ATP;
	    	var quantityUomId = data.quantityUomId;
	        if(quantity == 0 || quantity == undefined){
	            $('#'+gridId).jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#"+gridId).jqxGrid('begincelledit', rowindex, "quantity");
	                    }
	                }]
	            );
	            return true;
	        }else{
	        	if(quantityUomIdToTransfer == undefined){
	                $('#'+gridId).jqxGrid('unselectrow', rowindex);
	                bootbox.dialog("${uiLabelMap.LogSelectQuantiyUomId}", [{
	                    "label" : "${uiLabelMap.CommonOk}",
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    "callback": function() {
	                            $("#"+gridId).jqxGrid('begincelledit', rowindex, "quantityUomIdToTransfer");
	                        }
	                    }]
	                );
	                return true;
	            }else{
	            	if(quantityUomId == quantityUomIdToTransfer){
	            		if(quantity > atp){
	            			$('#'+gridId).jqxGrid('unselectrow', rowindex);
	            			bootbox.dialog("${uiLabelMap.LogCheckQuantityTranfer}!", 
	            				[{
		                            "label" : "${uiLabelMap.CommonOk}",
		                            "class" : "btn btn-primary standard-bootbox-bt",
		                            "icon" : "fa fa-check",
		                            "callback": function() {
	                                	$("#"+gridId).jqxGrid('begincelledit', rowindex, "quantity");
	                            	}
	                            }]
	                        );
	            			return true;
	            		}
	            	}else{
	            		var quantityConvert;
	            		for(var x in listConfigPacking){
	            			if(listConfigPacking[x].productId == productId && listConfigPacking[x].uomFromId == quantityUomIdToTransfer && listConfigPacking[x].uomToId == quantityUomId){
	            				quantityConvert = listConfigPacking[x].quantityConvert;
	            			}
	            		}
	            		if(quantityConvert != ''){
	            			var quantityTranfers = quantityConvert*quantity;
	            			if(quantityTranfers > atp){
	            				$('#'+gridId).jqxGrid('unselectrow', rowindex);
	                			bootbox.dialog("${uiLabelMap.LogCheckQuantityTranfer}!", 
	                				[{
	    	                            "label" : "${uiLabelMap.CommonOk}",
	    	                            "class" : "btn btn-primary standard-bootbox-bt",
	    	                            "icon" : "fa fa-check",
	    	                            "callback": function() {
	                                    	$("#"+gridId).jqxGrid('begincelledit', rowindex, "quantity");
	                                	}
	                                }]
	                            );
	                			return true;
	            			}
	            		}
	            	}
	            }
	        }
		}
	}
	
	$("#jqxgridProduct").on('cellBeginEdit', function (event) 
	{
	    var args = event.args;
	    var dataField = event.args.datafield;
	    var rowBoundIndex = event.args.rowindex;
	    var value = args.value;
	    var oldvalue = args.oldvalue;
	    var rowData = args.row;
	    $('#jqxgridProduct').jqxGrid('unselectrow', event.args.rowindex);
	});
	
	
	$("#okButton").click(function () {
		var des = $('#editor').val();
		var tmp = des.substring(5, des.length - 6);
		$("#description").val(tmp);
		$("#descChannel").val(tmp);
		$("#jqxEditorWindow").jqxWindow('close');
	});
	$("#cancelButton").click(function () {
		$("#jqxEditorWindow").jqxWindow('close');
	});
	function showEditor(){
		$("#jqxEditorWindow").jqxWindow('open');
	}
	var originFacilityData = new Array();
	var destFacilityToData = new Array();
	var originContactMechData = new Array();
	var destContactMechData = new Array();
	var contactMechData = new Array();
	
	$("#transferTypeId").jqxDropDownList({source: transferTypeData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "transferTypeId"});
	$("#transferTypeId").jqxDropDownList('val', "TRANS_INTERNAL");
	$("#originFacilityId").jqxDropDownList({source: faciData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "facilityId"});
	$("#destFacilityId").jqxDropDownList({source: faciData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "facilityId"});
	$("#originContactMechId").jqxDropDownList({source: originContactMechData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "contactMechId"});
	$("#destContactMechId").jqxDropDownList({source: destContactMechData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "contactMechId"});
	$("#estimatedStartDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	$("#estimatedArrivalDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	
	$("#estimatedStartDateChannel").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	$("#estimatedArrivalDateChannel").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	$("#facilityId").jqxDropDownList({source: channelFacilityData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "facilityId"});
	$("#contactMechId").jqxDropDownList({source: contactMechData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "contactMechId"});
	
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 590, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$("#alterpopupWindowChannel").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 550, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addChannelCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$('#alterpopupWindow').on('open', function (event) {
		$('#description').jqxInput('val', '');
		updateFacilityByTransferType ({
	        	transferTypeId: $("#transferTypeId").val(),
			}, 'getFacilityByTransferType' , 'listOriginFacilities', 'listDestFacilities', 'listOriginContactMechs', 'listDestContactMechs', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'originFacilityId', 'destFacilityId', 'originContactMechId', 'destContactMechId');
	});
	
	$('#alterpopupWindowChannel').on('open', function (event) {
		$('#descChannel').jqxInput('val', '');
		var tmpS = $("#jqxgridProductChannel").jqxGrid('source');
	 	var curFacilityId = $("#facilityId").val();
	 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
	 	$("#jqxgridProductChannel").jqxGrid('source', tmpS);
	 	
	 	update({
			facilityId: $("#facilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
	});
	
	$("#facilityId").on('change', function(event){
		var tmpS = $("#jqxgridProductChannel").jqxGrid('source');
	 	var curFacilityId = $("#facilityId").val();
	 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
	 	$("#jqxgridProductChannel").jqxGrid('source', tmpS);
	 	update({
			facilityId: $("#facilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
	});
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#jqxgridProduct').jqxGrid('clearSelection');
	}); 
	
	$("#addChannelSave").click(function () {
		var row;
		var selectedIndexs = $('#jqxgridProductChannel').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
		} else{
			if($('#estimatedStartDateChannel').jqxDateTimeInput('value') != '' && $('#estimatedArrivalDateChannel').jqxDateTimeInput('value') != ''){
				var beforeDate = $('#estimatedStartDateChannel').jqxDateTimeInput('value');
				var afterDate = $('#estimatedArrivalDateChannel').jqxDateTimeInput('value');
				if(afterDate.getFullYear() > beforeDate.getFullYear()){
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else if (afterDate.getMonth() > beforeDate.getMonth()) {
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else if (afterDate.getDate() > beforeDate.getDate()) {
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else{
					createChannelTranfer(selectedIndexs);
				}
			} else{
				createChannelTranfer(selectedIndexs)
			}	
		}
	});
	
	// update the edited row when the user clicks the 'Save' button.
	$("#addButtonSave").click(function () {
		var row;
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
		}else{
			if($('#estimatedStartDate').jqxDateTimeInput('value') != '' && $('#estimatedArrivalDate').jqxDateTimeInput('value') != ''){
				var estimatedStartDate = $('#estimatedStartDate').jqxDateTimeInput('value');
				var estimatedArrivalDate = $('#estimatedArrivalDate').jqxDateTimeInput('value');
				if(estimatedArrivalDate.getFullYear() > estimatedStartDate.getFullYear()){
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else if (estimatedArrivalDate.getMonth() > estimatedStartDate.getMonth()) {
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else if (estimatedArrivalDate.getDate() > estimatedStartDate.getDate()) {
					bootbox.dialog("${uiLabelMap.LogCheckDateTranferProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				} else{
					createTranferProductInFacilityToFacility(selectedIndexs);
				}
			}else{
				createTranferProductInFacilityToFacility(selectedIndexs)
			}	
		}
	}); 
	function createChannelTranfer(selectedIndexs){
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
			if(result){	
				var listProducts = new Array();
				for(var i = 0; i < selectedIndexs.length; i++){
					var data = $('#jqxgridProductChannel').jqxGrid('getrowdata', selectedIndexs[i]);
					var map = {};
					map['productId'] = data.productId;
					map['transferItemTypeId'] = "PRODUCT_TRANS_ITEM";
					map['quantity'] = data.quantity;
					if (data.quantityUomIdToTransfer != null){
						map['quantityUomId'] = data.quantityUomIdToTransfer;
					} else {
						map['quantityUomId'] = data.quantityUomId;
					}
					if (data.expireDate != null){
						map['expireDate'] = data.expireDate.getTime();
					} else {
						map['expireDate'] = "";
					}
					listProducts[i] = map;
				}
				listProducts = JSON.stringify(listProducts);
				row = { 
						transferTypeId:"TRANS_SALES_CHANNEL", 
						originFacilityId:$('#facilityId').val(),
						destFacilityId:$('#facilityId').val(),
						originContactMechId:$('#contactMechId').val(),
						shipmentMethodTypeId:"NO_SHIPPING",
						carrierPartyId: "_NA_",
						destContactMechId:$('#contactMechId').val(),
						estimatedStartDate:$('#estimatedStartDateChannel').jqxDateTimeInput('value').getTime(), 
						estimatedArrivalDate:$('#estimatedArrivalDateChannel').jqxDateTimeInput('value').getTime(),
						shipAfterDate:$('#estimatedStartDate').jqxDateTimeInput('value').getTime(),
						shipBeforeDate:$('#estimatedArrivalDate').jqxDateTimeInput('value').getTime(),
						description:$('#descChannel').jqxInput('val'),
						listProducts:listProducts
		    	  };
				$("#alterpopupWindowChannel").jqxWindow('close');
				$("#jqxgridTransfer").jqxGrid('addRow', null, row, "first");
			}
		});
	}
	
	function createTranferProductInFacilityToFacility(selectedIndexs){
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
			if(result){	
				var listProducts = new Array();
				for(var i = 0; i < selectedIndexs.length; i++){
					var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
					var map = {};
					map['productId'] = data.productId;
					map['transferItemTypeId'] = "PRODUCT_TRANS_ITEM";
					map['quantity'] = data.quantity;
					if (data.quantityUomIdToTransfer != null){
						map['quantityUomId'] = data.quantityUomIdToTransfer;
					} else {
						map['quantityUomId'] = data.quantityUomId;
					}
					if (data.expireDate != null){
						map['expireDate'] = data.expireDate.getTime();
					} else {
						map['expireDate'] = "";
					}
					listProducts[i] = map;
				}
				listProducts = JSON.stringify(listProducts);
				row = { 
						transferTypeId:$('#transferTypeId').val(),
						originFacilityId:$('#originFacilityId').val(),
						destFacilityId:$('#destFacilityId').val(),
						originContactMechId:$('#originContactMechId').val(),
						shipmentMethodTypeId: $('#shipmentMethodTypeId').val(),
						carrierPartyId: $('#carrierPartyId').val(),
						destContactMechId:$('#destContactMechId').val(),
						estimatedStartDate:$('#estimatedStartDate').jqxDateTimeInput('value').getTime(),
						estimatedArrivalDate:$('#estimatedArrivalDate').jqxDateTimeInput('value').getTime(),
						shipAfterDate:$('#estimatedStartDate').jqxDateTimeInput('value').getTime(),
						shipBeforeDate:$('#estimatedArrivalDate').jqxDateTimeInput('value').getTime(),
						description:$('#description').jqxInput('val'),
						listProducts:listProducts
		    	  };
				$("#alterpopupWindow").jqxWindow('close');
				$("#jqxgridTransfer").jqxGrid('addRow', null, row, "first");
			}
		});
	}
	
	var contextMenu = $("#Menu").jqxMenu({ width: 150, height: 58, autoOpenPopup: false, mode: 'popup'});
    $("#jqxgridTransfer").on('contextmenu', function () {
        return false;
    });
    // handle context menu clicks.
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgridTransfer").jqxGrid('getselectedrowindex');
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.Approve)}") {
            var dataRecord = $("#jqxgridTransfer").jqxGrid('getrowdata', rowindex);
            var transferId = dataRecord.transferId;
            approveTransfer({
            	transferId: transferId,
			}, 'approveTransfer', 'jqxgridTransfer');
        }
    });
    
    $("#confirmDeletePopupWindow").jqxWindow({ width: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#ConfirmCancel"), modalOpacity: 0.01 });
    $("#ConfirmCancel").jqxButton({ theme: theme });
    $("#ConfirmDelete").jqxButton({ theme: theme });
    $("#ConfirmDelete").click(function () {
    	removeTransfer({
        	transferId: $("#transferId").val(),
		}, 'deleteTransfer', 'jqxgridTransfer');
        $("#confirmDeletePopupWindow").jqxWindow('hide');
    });
    $("#ConfirmCancel").click(function () {
    	$("#confirmDeletePopupWindow").jqxWindow('hide');
    });
//    $("#jqxgrid").on('rowClick', function (event) {
//        if (event.args.rightclick) {
//        	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', event.args.rowindex);
//    		if ("TRANSFER_CREATED" == dataRecord.statusId){
//        		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
//        		$("#menuAppoveTransfer").show();
//        		$("#menuDeleteTransfer").show();
//        		$("#menuCreateDelivery").hide();
//                var scrollTop = $(window).scrollTop();
//                var scrollLeft = $(window).scrollLeft();
//                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
//                return false;
//        	} 
//        	if ("TRANSFER_APPROVED" == dataRecord.statusId){
//        		$("#menuAppoveTransfer").hide();
//        		$("#menuDeleteTransfer").hide();
//        		$("#menuCreateDelivery").show();
//        		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
//                var scrollTop = $(window).scrollTop();
//                var scrollLeft = $(window).scrollLeft();
//                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
//                return false;
//        	} 
//        }
//    });
    var tmpFlag = true;
	var tmpFlag2 = true;
	
	$("#transferTypeId").change(function(){
		updateFacilityByTransferType ({
        	transferTypeId: $("#transferTypeId").val(),
		}, 'getFacilityByTransferType' , 'listOriginFacilities', 'listDestFacilities', 'listOriginContactMechs', 'listDestContactMechs', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'originFacilityId', 'destFacilityId', 'originContactMechId', 'destContactMechId');
	});
    $("#originFacilityId").on('change', function(event){
		isNull = false;
		if ($("#originFacilityId").val() == $("#destFacilityId").val()){
			var currentIndex = $("#destFacilityId").jqxDropDownList('getSelectedIndex');
			var item = $("#destFacilityId").jqxDropDownList('getItem', currentIndex + 1);
			if (item){
				$("#destFacilityId").jqxDropDownList({selectedIndex: currentIndex + 1});
			} else {
				item = $("#destFacilityId").jqxDropDownList('getItem', currentIndex - 1);
				if (item){
					$("#destFacilityId").jqxDropDownList({selectedIndex: currentIndex - 1});
				} else {
					$("#destFacilityId").jqxDropDownList('clear');
				}
			}
			update({
				facilityId: $("#originFacilityId").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		} else {
			update({
				facilityId: $("#originFacilityId").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		}
		var tmpS = $("#jqxgridProduct").jqxGrid('source');
	 	var curFacilityId = $("#originFacilityId").val();
	 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
	 	$("#jqxgridProduct").jqxGrid('source', tmpS);
	});
	$("#destFacilityId").on('change', function(event){
		isNull = false;
		
		if ($("#originFacilityId").val() == $("#destFacilityId").val()){
			var currentIndex = $("#originFacilityId").jqxDropDownList('getSelectedIndex');
			var item = $("#originFacilityId").jqxDropDownList('getItem', currentIndex + 1);
			if (item){
				$("#originFacilityId").jqxDropDownList({selectedIndex: currentIndex + 1});
			} else {
				item = $("#originFacilityId").jqxDropDownList('getItem', currentIndex - 1);
				if (item){
					$("#originFacilityId").jqxDropDownList({selectedIndex: currentIndex - 1});
				} else {
					$("#originFacilityId").jqxDropDownList('clear');
				}
			}
			update({
				facilityId: $("#destFacilityId").val(),
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
		} else {
			update({
				facilityId: $("#destFacilityId").val(),
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
		}
	});
    function updateFacilityByTransferType(jsonObject, url, data1, data2, data3, data4, key1, value1, key2, value2, key3, value3, key4, value4, id1, id2, id3, id4) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json1 = res[data1];
	            renderHtml(json1, key1, value1, id1);
	            var json2 = res[data2];
	            renderHtml(json2, key2, value2, id2);
	            var json3 = res[data3];
	            renderHtml(json3, key3, value3, id3);
	            var json4 = res[data4];
	            renderHtml(json4, key4, value4, id4);
	            var tmpS = $("#jqxgridProduct").jqxGrid('source');
	            if (json1.length != 0){
	            	var curFacilityId = $('#originFacilityId').val();
				 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
				 	$("#jqxgridProduct").jqxGrid('source', tmpS);
	            } else {
				 	$("#jqxgridProduct").jqxGrid('clear');
	            }
	        }
	    });
	}
    function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    function approveTransfer(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$("#"+jqxgrid).jqxGrid('updatebounddata');
	        }
	    });
	}
</script>