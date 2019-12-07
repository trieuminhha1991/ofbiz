<div id="alteridupc-tab" class="tab-pane<#if activeTab?exists && activeTab == "alteridupc-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<#assign dataField = "[
						{name: 'productId', type: 'string'}, 
						{name: 'productCode', type: 'string'}, 
						{name: 'measureUomId', type: 'string'}, 
						{name: 'measureValue', type: 'number'}, 
						{name: 'idValue', type: 'string'}, 
						{name: 'goodIdentificationTypeId', type: 'string'}, 
					]"/>
			<#--
			cellsrenderer: function(row, column, value) {
						 		var str = '<div class=\"innerGridCellContent align-right\">';
						 		if (typeof(value) != 'undefined') str += formatnumber(value, '${locale}', 3);
								str += '</div>';
								return str;
						 	}
			-->
			<#assign columnlist = "
						{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 220}, 
						{text: '${StringUtil.wrapString(uiLabelMap.BSWeightBarcodeType)}', dataField: 'goodIdentificationTypeId', width: 160}, 
						{text: '${StringUtil.wrapString(uiLabelMap.BSValue)}', dataField: 'measureValue', width: 120, cellsalign: 'right', cellsformat: 'd'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.BSUomId)}', dataField: 'measureUomId', width: 120, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								if (weightUomData.length > 0) {
									for(var i = 0 ; i < weightUomData.length; i++){
		    							if (value == weightUomData[i].uomId){
		    								return '<span title =\"' + weightUomData[i].description +'\">' + weightUomData[i].abbreviation + '</span>';
		    							}
		    						}
								}
								return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
						 		if (weightUomData.length > 0) {
									var filterDataAdapter = new $.jqx.dataAdapter(weightUomData, {
										autoBind: true
									});
									var records = filterDataAdapter.records;
									widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId',
										renderer: function(index, label, value){
											if (weightUomData.length > 0) {
												for(var i = 0; i < weightUomData.length; i++){
													if(weightUomData[i].uomId == value){
														return '<span>' + weightUomData[i].abbreviation + '</span>';
													}
												}
											}
											return value;
										}
									});
									widget.jqxDropDownList('checkAll');
								}
				   			}
		   				}, 
						{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', dataField: 'idValue', minWidth: 160}, 
					"/>
			
			<#assign contextMenuItemId = "ctxmnuidupccode">
			<#assign permitCreate = false>
			<#assign permitDelete = false>
			<#if hasOlbPermission("MODULE", "PRODUCTPO_NEW", "")><#assign permitCreate = true></#if>
			<#if hasOlbPermission("MODULE", "PRODUCTPO_EDIT", "")><#assign permitDelete = true></#if>
			<#assign customcontrol1 = "fa fa-file-pdf-o@${uiLabelMap.BSPrintBarcode}@javascript: void(0);@OlbProductUPCList.printBarcode()">
			<@jqGrid id="jqxgridProductUPCCodes" customTitleProperties="BSProductUPCList" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterPopupNewProdUPC" columnlist=columnlist dataField=dataField
					viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true" jqGridMinimumLibEnable="true" 
					url="jqxGeneralServicer?sname=JQGetListProductUPCCodes&productId=${product.productId?if_exists}" 
					addrow="${permitCreate?string}" createUrl="jqxGeneralServicer?jqaction=C&sname=createProductUPCCode" addColumns="productId;measureUomId;measureValue(java.math.BigDecimal);goodIdentificationTypeId" 
					deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteProductUPCCode" deleteColumn="productId;measureUomId;measureValue(java.math.BigDecimal);goodIdentificationTypeId" 
					selectionmode="multiplecellsadvanced" mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" customcontrol1=customcontrol1/>
			
			<div id="contextMenu_${contextMenuItemId}" style="display:none">
				<ul>
				    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
				    <li id="${contextMenuItemId}_delete"><i class="fa-trash-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
				</ul>
			</div>
		</div>
	</div>
	
	<#include "productNewUpcCodePopup.ftl"/>
</div>
<script type="text/javascript">
	$(function(){
		OlbProductUPCList.init();
	});
	var OlbProductUPCList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
				var args = event.args;
				var tmpId = $(args).attr('id');
				var idGrid = "#jqxgridProductUPCCodes";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case "${contextMenuItemId}_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		case "${contextMenuItemId}_delete": {
		    			$("#deleterowbuttonjqxgridProductUPCCodes").click();
		    			break;
		    		};
		    		default: break;
		    	}
		    });
		};
		var printBarcode = function(){
			var url = 'ProductUPC.pdf?productId=${product.productId?if_exists}';
			var win = window.open(url, '_blank');
			win.focus();
		};
		return {
			init: init,
			printBarcode: printBarcode
		};
	}());
</script>