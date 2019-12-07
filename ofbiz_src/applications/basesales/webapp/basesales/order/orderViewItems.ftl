<#if orderHeader?has_content>
	<style type="text/css">
		.background-promo {
			color: #009900 !important;
	  		background: #f1ffff !important;
		}
		#jqxgridOrderItems .jqx-tabs-headerWrapper.jqx-tabs-header {
			height:25px;
		}
		#jqxgridOrderItems .jqx-tabs-headerWrapper.jqx-tabs-header ul.jqx-tabs-title-container {
			height: 25px;
		}
		#jqxgridOrderItems .jqx-tabs-headerWrapper.jqx-tabs-header ul.jqx-tabs-title-container li.jqx-tabs-title {
			height: 13px;
		}
		#jqxgridOrderItems .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-grid.jqx-widget {
			border:none;
		}
		#jqxgridOrderItems .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-grid-header {
			border-width: 1px 1px 1px 1px;
		}
		#jqxgridOrderItems .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-widget-content .jqx-grid-cell{
			border-width: 0px 1px 1px 1px;
		}
		.jqx-window-olbius .jqx-window-content table.table-left-width250 tr td.td-left {
		  	width: 250px;
		  	min-width: 250px;
		  	max-width: 250px;
		}
		.ui-dialog.ui-widget.ui-widget-content {
			z-index:18005 !important;
		}
		.ui-widget-overlay {
			z-index:18004 !important;
		}
	</style>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
	<script type="text/javascript">
		var itemsTabCellclass = function (row, columnfield, value) {
	 		var data = $('#jqxgridOrderItems').jqxGrid('getrowdata', row);
	        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
	            return 'background-promo';
	        }
	    }
	    
	    <#assign itemStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_ITEM_STATUS"}, null, true)!/>
	    <#assign quantityUomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, true)!/>
	    var itemsTabItemStatusData = {
    	<#if itemStatusList?exists>
			<#list itemStatusList as itemStatus>
			"${itemStatus.statusId}": "${StringUtil.wrapString(itemStatus.get("description", locale)?default(""))}",
			</#list>
		</#if>
	    };

		var itemsTabQuantityUomData = {
		<#if quantityUomList?exists>
			<#list quantityUomList as quantityUom>
			"${quantityUom.uomId}": "${StringUtil.wrapString(quantityUom.get("description", locale)?default(""))}",
			</#list>
		</#if>
		};
	</script>
	
<div class="tab-pane<#if activeTab?exists && activeTab == "items-tab"> active</#if>" id="items-tab">
    <h4 class="smaller green">${uiLabelMap.ListProduct}</h4>
    <div class="row-fluid">
    	<div class="span12">
			<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
				var tabsDiv = $($(parentElement).children()[0]);
			    if (tabsDiv != null) {
			        var loadingStr = '<div id=\"info_loader_' + index + '\" style=\"overflow: hidden; position: absolute; display: none; left: 45%; top: 25%;\" class=\"jqx-rc-all jqx-rc-all-olbius\">';
			        loadingStr += '<div style=\"z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;\" ';
			        loadingStr += ' class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
			        loadingStr += '<div style=\"float: left;\"><div style=\"float: left; overflow: hidden; width: 32px; height: 32px;\" class=\"jqx-grid-load\"></div>';
			        loadingStr += '<span style=\"margin-top: 10px; float: left; display: block; margin-left: 5px;\">${uiLabelMap.DALoading}...</span></div></div></div>';
			        var notescontainer = $(loadingStr);
			        $(tabsDiv).append(notescontainer);
			        
			        var orderId = datarecord.orderId;
			        var orderItemSeqId = datarecord.orderItemSeqId;
			        
			        var loadPage = function (url, tabClass, data, index) {
			            $.ajax({
						  	type: 'POST',
						  	url: url,
						  	data: data,
						  	beforeSend: function () {
								$(\"#info_loader_\" + index).show();
							}, 
							success: function(data){
								var tabActive = tabsDiv.find('.' + tabClass);
								var container2 = $('<div style=\"margin: 5px;\">' + data + '</div>');
						        container2.appendTo($(tabActive));
							},
							error: function(e){
							}, 
				            complete: function() {
						        $(\"#info_loader_\" + index).hide();
						    }
						});
			        }
			        loadPage('getDetailOrderItemAjax', 'contentTab1', {'orderId' : orderId, 'orderItemSeqId' : orderItemSeqId}, index);
			    }
			 }"/>
			<#assign rowdetailstemplateAdvance = "<div class='contentTab1'></div>"/>
			
			<#assign dataField="[
							{ name: 'orderId', type: 'string'},
							{ name: 'orderItemSeqId', type: 'string'},
							{ name: 'productId', type: 'string'},
							{ name: 'productCode', type: 'string'},
							{ name: 'productName', type: 'string'},
							{ name: 'isPromo', type: 'string'},
							{ name: 'statusId', type: 'string'},
							{ name: 'expireDate', type: 'date', other:'Timestamp'},
							{ name: 'defaultQuantityUomId', type: 'string'},
							{ name: 'quantity', type: 'string'},
							{ name: 'cancelQuantity', type: 'string'},
							{ name: 'alternativeQuantity', type: 'string'},
							{ name: 'unitPrice', type: 'number', formatter: 'float'}, 
			 		 	]"/>
			<#assign columnlist="
							{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: 160, cellclassname: itemsTabCellclass},
							{text: '${uiLabelMap.BSProductName}', dataField: 'productName', minwidth: 120, cellclassname: itemsTabCellclass},
					 		{text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: 120, cellclassname: itemsTabCellclass, filtertype: 'checkedlist', 
								cellsrenderer: function(row, column, value){
							 		var data = itemsTabItemStatusData[value];
		    						if (typeof(data) == 'undefined') data = '';
		    						return '<span>' + data + '</span>';
							 	}
					   		},
						 	{text: '${uiLabelMap.BSUom}', dataField: 'defaultQuantityUomId', width: 120, cellclassname: itemsTabCellclass, filtertype: 'checkedlist', 
								cellsrenderer: function(row, column, value){
							 		var data = itemsTabQuantityUomData[value];
							 		if (typeof(data) == 'undefined') data = '';
		    						return '<span>' + data + '</span>';
							 	}
						   	},
						 	{text: '${uiLabelMap.BSQuantity}', dataField: 'quantity', width: 120, cellclassname: itemsTabCellclass},
						 	{text: '${uiLabelMap.BSCancelQuantity}', dataField: 'cancelQuantity', width: 120, cellclassname: itemsTabCellclass},
						"/>
			<@jqGrid id="jqxgridOrderItems" url="jqxGeneralServicer?sname=JQGetListOrderItemDetail&orderId=${orderHeader.orderId?if_exists}" dataField=dataField columnlist=columnlist 
					filterable="false" clearfilteringbutton="true"
				 	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="false" addrow="false" addType="popup" deleterow="false" editable="false" 
				 	mouseRightMenu="false" contextMenuId="" showtoolbar="false" 
				 	initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"  rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="300"
			 />
			<#--initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" -->
	    </div>
    </div>
</div>
</#if>