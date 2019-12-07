<@jqGridMinimumLib/>
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	<#assign listTypes = ["PRODUCT_PACKING", "WEIGHT_MEASURE"]>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listTypes), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
			<#if uom.uomTypeId == "WEIGHT_MEASURE">
				{
					uomId: "${uom.uomId}",
					description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
				},
			<#else>
				{
				uomId: "${uom.uomId}",
				description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
				},
			</#if>
		</#list>
	];
	function getUomDescription(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
	
	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", ownerPartyId)), null, null, null, false) />
	var mapFacilityData = {  
			<#if facilitys?exists>
				<#list facilitys as item>
					"${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}',
				</#list>
			</#if>	
	};
	
	<#assign activeTab = "tab_1"/>
	
	$('.nav.nav-tabs li').on('click', function(){
    	// clear parameter
    	var thisHref = location.href;
    	var queryParam = thisHref.split("?");
    	var newHref = "";
    	if (queryParam != null && queryParam != undefined) {
    		newHref = queryParam[0] + "?";
    	}
    	var isAdded = false;
    	if (queryParam.length > 1) {
    		var varsParam = queryParam[1].split("&");
		    for (var i = 0; i < varsParam.length; i++) {
		        var pairParam = varsParam[i].split("=");
		        if(pairParam[0] != 'activeTab'){
		        	if (isAdded) newHref += "&";
		        	newHref += varsParam[i];
		        	isAdded = true;
		        }
		    }
    	}
    	var tabObj = $(this).find("a[data-toggle=tab]");
    	if (tabObj != null && tabObj != undefined) {
    		var tabHref = tabObj.attr("href");
    		if (tabHref.indexOf("#") == 0) {
    			var tabId = tabHref.substring(1);
    			window.history.pushState({}, "", newHref + '&activeTab=' + tabId);
    		}
    	}
    });
	
</script>


<div class="row-fluid margin-top5">
	<div class="span12">
		<div class="tabbable">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="<#if activeTab?exists && activeTab == "tab_1">active</#if>" id="li_1">
					<a data-toggle="tab" href="#tab_1">
						<span>${uiLabelMap.WebtoolsWarningLogLevel?if_exists} ${uiLabelMap.BLMissing?lower_case}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "tab_2">active</#if>" id="li_2">
					<a data-toggle="tab" href="#tab_2">
						<span>${uiLabelMap.WebtoolsWarningLogLevel?if_exists} ${uiLabelMap.BLOver?lower_case}</span>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

<#assign dataField="[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'requireAmount', type: 'string'},
				{ name: 'weightUomId', type: 'string'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'facilityName', type: 'string'},
				{ name: 'quantityOnHandTotal', type: 'number'},
				{ name: 'amountOnHandTotal', type: 'number'},
				{ name: 'availableToPromiseTotal', type: 'number'},
				{ name: 'thresholdsQuantity', type: 'number'},
				{ name: 'thresholdsQuantityMax', type: 'number'},
				{ name: 'quantityDeviation', type: 'number'}
			]"/>
<#assign columnlist="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 130,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 200,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 150,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 100, cellsalign: 'right', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
       					if (value){
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 100, cellsalign: 'right', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
       					if (value){
       						var data = $('#jqxgirdInventoryQuantityWarning').jqxGrid('getrowdata', row);
       						if (data.requireAmount && data.requireAmount == 'Y') {
       							value = data.amountOnHandTotal;
       						}
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgirdInventoryQuantityWarning').jqxGrid('getrowdata', row);
   						if (data.requireAmount && data.requireAmount == 'Y') {
   							value = data.weightUomId;
   						}
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
							renderer: function(index, label, value){
					        	if (uomData.length > 0) {
									for(var i = 0; i < uomData.length; i++){
										if(uomData[i].uomId == value){
											return '<span>' + uomData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: '${uiLabelMap.BLSafetyInventory}' + ' ${uiLabelMap.BLMin?lower_case}', datafield: 'thresholdsQuantity', width: 180, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function(row, column, value){
       				},
				},
				{ text: '${uiLabelMap.BLMissing}', datafield: 'quantityDeviation', filterable: false, width: 120, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function(row, column, value){
       				},
				},
			"/>
			
<#assign columnlist2="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 130,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 200,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 150,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 100, cellsalign: 'right', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
       					if (value){
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 100, cellsalign: 'right', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
       					if (value){
       						var data = $('#jqxgirdInventoryQuantityWarningMax').jqxGrid('getrowdata', row);
       						if (data.requireAmount && data.requireAmount == 'Y') {
       							value = data.amountOnHandTotal;
       						}
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgirdInventoryQuantityWarningMax').jqxGrid('getrowdata', row);
   						if (data.requireAmount && data.requireAmount == 'Y') {
   							value = data.weightUomId;
   						}
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
							renderer: function(index, label, value){
					        	if (uomData.length > 0) {
									for(var i = 0; i < uomData.length; i++){
										if(uomData[i].uomId == value){
											return '<span>' + uomData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: '${uiLabelMap.BLSafetyInventory}' + ' ${uiLabelMap.BLMax?lower_case}', datafield: 'thresholdsQuantityMax', width: 180, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function(row, column, value){
       				},
				},
				{ text: '${uiLabelMap.BLOver}', datafield: 'quantityDeviation', filterable: false, width: 120, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function(row, column, value){
       				},
				},
			"/>

<div class="tab-content overflow-visible" style="padding:8px 0; border: none !important;">
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_1"> active</#if>" id="tab_1">
		<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
			id="jqxgirdInventoryQuantityWarning" addrefresh="true" filterable="true" mouseRightMenu="true" contextMenuId="menuQuantityWarning"
			url="jqxGeneralServicer?sname=JQGetListInventoryQuantityWarning" 
			customTitleProperties="BLListProductLessSafeInventory" />
	</div>
		<div class="tab-pane<#if activeTab?exists && activeTab == "tab_2"> active</#if>" id="tab_2">
			<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist2 editable="false" showtoolbar="true"
			id="jqxgirdInventoryQuantityWarningMax" addrefresh="true" filterable="true" mouseRightMenu="true" contextMenuId="menuQuantityWarningMax"
			url="jqxGeneralServicer?sname=JQGetListInventoryQuantityWarning&type=MAX" 
			customTitleProperties="BLListProductGreaterSafeInventory" />
		</div>
	</div>
</div>
	
			
<div id='menuQuantityWarning' style="display:none;">
	<ul>
    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id='menuQuantityWarningMax' style="display:none;">
	<ul>
    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script>
	$(document).ready(function (){
		$("#menuQuantityWarning").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#menuQuantityWarningMax").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	$("#menuQuantityWarning").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdInventoryQuantityWarning').jqxGrid('updatebounddata');
		}
	});
	$("#menuQuantityWarningMax").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdInventoryQuantityWarningMax').jqxGrid('updatebounddata');
		}
	});
</script>