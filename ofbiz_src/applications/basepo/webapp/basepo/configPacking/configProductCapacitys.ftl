<script>
	<#assign activeTab = "tab_general"/>
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, true)>
	
	var listUoms = [<#if quantityUoms?exists><#list quantityUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.description?if_exists)}"
	},</#list></#if>];
	var mapQuantityUom = {<#if quantityUoms?exists><#list quantityUoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};

	var filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	var POPleaseSelect = "${StringUtil.wrapString(uiLabelMap.POPleaseSelect)}";
	var POProductId = "${uiLabelMap.POProductId}";
	var POProductName = "${uiLabelMap.BPOProductName}";
	var POCheckIsEmptyCreateLocationFacility = "${StringUtil.wrapString(uiLabelMap.POCheckIsEmptyCreateLocationFacility)}";

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
				<li class="<#if activeTab?exists && activeTab == "tab_general">active</#if>" id="li_general">
					<a data-toggle="tab" href="#tab_general">
						<span>${uiLabelMap.UnitPacking?if_exists}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "tab_product">active</#if>" id="li_product">
					<a data-toggle="tab" href="#tab_product">
						<span>${uiLabelMap.listProductConfigPacking?if_exists}</span>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

<div class="tab-content overflow-visible" style="padding:8px 0; border: none !important;">
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_general"> active</#if>" id="tab_general">
	
		<#include "../productuom/productUomList.ftl">
	
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_product"> active</#if>" id="tab_product">
	
		<#assign dataField="[{ name: 'productId', type: 'string' },
							{ name: 'uomFromId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'uomToId', type: 'string' },
							{ name: 'quantityConvert', type: 'number', other: 'BigDecimal' },
							{ name: 'fromDate', type: 'date', other: 'Timestamp' },
							{ name: 'thruDate', type: 'date', other: 'Timestamp' }]"/>
		
		<#assign columnlist="
					{ text: '${uiLabelMap.productId}',  datafield: 'productCode', width: 120, editable: false },
					{ text: '${uiLabelMap.ProductName}',  datafield: 'productName', editable: false },
					{ text: '${uiLabelMap.uomFromId}', datafield: 'uomFromId', width: 100, editable: false, filterable: false,
						cellsrenderer: function(row, colum, value) {
							mapQuantityUom[value]==undefined?mapQuantityUom[value]='Pallet':mapQuantityUom[value]=mapQuantityUom[value]
							return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
						}
					},
					{ text: '${uiLabelMap.uomToId}', datafield: 'uomToId', width: 100, editable: false, filterable: false,
						cellsrenderer: function(row, colum, value) {
							return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
						}
					},
					{ text: '${uiLabelMap.QuantityConvert}', datafield: 'quantityConvert', width: 150, editable: true, columntype: 'numberinput', cellsalign: 'right', filterable: false,
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
						}
					}"/>
		
		<#if hasOlbPermission("MODULE", "CONFIG_PRODPACK_NEW", "CREATE")>
			<#assign addrow = "true" />
		<#else>
			<#assign addrow = "false" />
		</#if>
		<#if hasOlbPermission("MODULE", "CONFIG_PRODPACK_DELETE", "DELETE")>
			<#assign deleterow = "true" />
		<#else>
			<#assign deleterow = "false" />
		</#if>
		<#if hasOlbPermission("MODULE", "CONFIG_PRODPACK_EDIT", "UPDATE")>
			<#assign editable = "true" />
		<#else>
			<#assign editable = "false" />
		</#if>
		
		<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
			showtoolbar="true" addrow=addrow addrefresh="true" deleterow=deleterow alternativeAddPopup="alterpopupWindow" editable=editable editmode="click"
			url="jqxGeneralServicer?sname=JQGetListUomTypeAndConfigPacking"
			updateUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=U"
			createUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=C"
			editColumns="quantityConvert(java.math.BigDecimal);productId;uomFromId;uomToId;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);"
			addColumns="quantityConvert(java.math.BigDecimal);productId;uomFromId;uomToId;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);"
			removeUrl="jqxGeneralServicer?sname=removeProductCapacitys&jqaction=D" deleteColumn="productId;uomFromId;uomToId;fromDate(java.sql.Timestamp);"
		/>
	</div>
</div>

<div id="alterpopupWindow"  style="display:none;">
	<div>${uiLabelMap.addNewConfigPacking}</div>
	<div class="" style="overflow-y: hidden;">
		<div class="row-fluid">
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span4" style="text-align: right;"><label class="asterisk" style="margin-top: 4px;">${uiLabelMap.POProduct}:</label></div>
		 		<div class="span8">
			 		<div id="productIdContainGrid" style="width: 100%" class="">
						<div id="jqxgridProduct">
						</div>
					</div>
		 		</div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span4" style="text-align: right;"><label class="asterisk" style="margin-top: 3px;">${uiLabelMap.uomFromId}</label></div>
				<div class="span8"><div id="uomFromId1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span4" style="text-align: right;"><label class="asterisk" style="margin-top: 3px;">${uiLabelMap.uomToId}</label></div>
				<div class="span8"><div id="uomFromIdBaseProduct"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span4" style="text-align: right;"><label class="asterisk" style="margin-top: 4px;">${uiLabelMap.QuantityConvert}</label></div>
				<div class="span8"><div id="quantityConvert1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px; display: none;">
				<div class="span4" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.AvailableFromDate}</label></div>
				<div class="span8"><div id="fromDate1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px; display: none;">
				<div class="span4" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.AvailableThruDate}</label></div>
				<div class="span8"><div id="thruDate1"></div></div>
			</div>
		</div>
		<hr style="margin: 10px 0px 10px 0px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
			<button id="alterCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/poresources/js/configPacking/configPacking.js"></script>