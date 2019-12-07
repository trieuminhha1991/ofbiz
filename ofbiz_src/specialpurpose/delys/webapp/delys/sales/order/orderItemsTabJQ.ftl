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
		var cellclass = function (row, columnfield, value) {
	 		var data = $('#jqxgridOrderItems').jqxGrid('getrowdata', row);
	        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
	            return 'background-promo';
	        }
	    }
	    <#assign itemStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_ITEM_STATUS"}, null, true)/>
	    <#assign quantityUomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, true)/>
	    <#if itemStatusList?exists>
			var itemStatusData = [
			<#list itemStatusList as itemStatus>
			{
				statusId: "${itemStatus.statusId}",
				description: "${StringUtil.wrapString(itemStatus.get("description", locale))}",
			}, 
			</#list>
			];
		<#else>
			var itemStatusData = [];
		</#if>
		<#if quantityUomList?exists>
			var quantityUomData = [
			<#list quantityUomList as quantityUom>
			{
				uomId: "${quantityUom.uomId}",
				description: "${StringUtil.wrapString(quantityUom.get("description", locale))}",
			}, 
			</#list>
			];
		<#else>
			var quantityUomData = [];
		</#if>
	</script>
	<div id="items-tab" class="tab-pane">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.DAListProduct}
		</h4>
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
		        loadPage('getDetailInformationOrderItemAjax', 'contentTab1', {'orderId' : orderId, 'orderItemSeqId' : orderItemSeqId}, index);
		    }
		 }"/>
		<#assign rowdetailstemplateAdvance = "<div class='contentTab1'></div>"/>
		
		<#assign dataField="[{ name: 'orderId', type: 'string'},
							 { name: 'orderItemSeqId', type: 'string'},
							 { name: 'productId', type: 'string'},
							 { name: 'productName', type: 'string'},
							 { name: 'isPromo', type: 'string'},
							 { name: 'statusId', type: 'string'},
							 { name: 'expireDate', type: 'date', other:'Timestamp'},
							 { name: 'quantityUomId', type: 'string'},
							 { name: 'quantity', type: 'string'},
							 { name: 'unitPrice', type: 'number', formatter: 'float'}, 
							 { name: 'adjustmentsTotal', type: 'number', formatter: 'float'}, 
							 { name: 'subTotal', type: 'number', formatter: 'float'}, 
			 		 	]"/>
		<#assign columnlist="{text: '${uiLabelMap.DAProductId}', dataField: 'productId', cellclassname: cellclass, width: '16%'},
							 {text: '${uiLabelMap.DAProductName}', dataField: 'productName', cellclassname: cellclass},
					 		 {text: '${uiLabelMap.DAStatus}', dataField: 'statusId', cellclassname: cellclass, width: '10%', filtertype: 'checkedlist', 
									cellsrenderer: function(row, column, value){
								 		var data = $('#jqxgridOrderItems').jqxGrid('getrowdata', row);
			    						for(var i = 0 ; i < itemStatusData.length; i++){
			    							if (value == itemStatusData[i].statusId){
			    								return '<span title = ' + itemStatusData[i].description +'>' + itemStatusData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
								 	}, 
								 	createfilterwidget: function (column, columnElement, widget) {
										var filterDataAdapter = new $.jqx.dataAdapter(itemStatusData, {
											autoBind: true
										});
										var records = filterDataAdapter.records;
										records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
										widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
											renderer: function(index, label, value){
												for(var i = 0; i < itemStatusData.length; i++){
													if(itemStatusData[i].statusId == value){
														return '<span>' + itemStatusData[i].description + '</span>';
													}
												}
												return value;
											}
										});
										widget.jqxDropDownList('checkAll');
						   			}
					   		 },
						 	 {text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', cellsformat: 'dd/MM/yyyy', cellclassname: cellclass, width: '10%'},
						 	 {text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', cellclassname: cellclass, width: '6%', filtertype: 'checkedlist', 
									cellsrenderer: function(row, column, value){
								 		var data = $('#jqxgridOrderItems').jqxGrid('getrowdata', row);
			    						for(var i = 0 ; i < quantityUomData.length; i++){
			    							if (value == quantityUomData[i].uomId){
			    								return '<span title = ' + quantityUomData[i].description +'>' + quantityUomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
								 	}, 
								 	createfilterwidget: function (column, columnElement, widget) {
										var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
											autoBind: true
										});
										var records = filterDataAdapter.records;
										records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
										widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'uomId',
											renderer: function(index, label, value){
												for(var i = 0; i < quantityUomData.length; i++){
													if(quantityUomData[i].uomId == value){
														return '<span>' + quantityUomData[i].description + '</span>';
													}
												}
												return value;
											}
										});
										widget.jqxDropDownList('checkAll');
						   			}
						   	 },
						 	 {text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', cellclassname: cellclass, width: '8%'},
						 	 {text: '${uiLabelMap.DAUnitPrice}', dataField: 'unitPrice', cellclassname: cellclass, cellsformat: 'c2', cellsalign: 'right', width: '10%'},
						 	 {text: '${uiLabelMap.OrderAdjustments}', dataField: 'adjustmentsTotal', cellclassname: cellclass, cellsformat: 'c2', cellsalign: 'right', width: '12%'},
						 	 {text: '${uiLabelMap.DASubTotal}', dataField: 'subTotal', cellclassname: cellclass, cellsformat: 'c2', cellsalign: 'right', width: '12%'},
						 "/>
		<@jqGrid id="jqxgridOrderItems" url="jqxGeneralServicer?sname=JQGetListOrderItemDetail&orderId=${orderHeader.orderId?if_exists}" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="false" addrow="false" addType="popup" deleterow="false" editable="false" 
		 	mouseRightMenu="true" contextMenuId="contextMenu" showtoolbar="false" 
		 	initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"  rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="300"
		 />
		<#--initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" -->
		<div id='contextMenu'>
			<ul>
			    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
			</ul>
		</div>
		
		<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
		<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
		<script type="text/javascript">
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxgridOrderItems").jqxGrid('getselectedrowindex');
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
		        	$("#jqxgridOrderItems").jqxGrid('updatebounddata');
		        }
			});
		</script>
		<#--<thead>
	            <tr valign="bottom" class="header-row">
	                <th width="33%">${uiLabelMap.ProductProduct}</th>
	                <th width="30%">${uiLabelMap.CommonStatus}</th>
	                <th width="5%">${uiLabelMap.OrderQuantity}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderUnitList}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderAdjustments}</th>
	                <th width="10%" align="right" class="align-right">${uiLabelMap.OrderSubTotal}</th>
	            </tr>
            </thead>-->
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%;">
            <#list orderHeaderAdjustments as orderHeaderAdjustment>
                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                <#if adjustmentAmount != 0>
                    <tr>
                        <td align="right" class="align-right" colspan="5">
                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
                            <span>${adjustmentType.get("description", locale)}</span>
                        </td>
                        <td width="12%" align="right" class="align-right" nowrap="nowrap">
                            <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                        </td>
                    </tr>
                </#if>
            </#list>
            <#-- subtotal -->
            <tr>
                <td colspan="6"></td>
            </tr>
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderItemsSubTotal}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- other adjustments -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.DATotalOrderAdjustments}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- shipping adjustments -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- tax adjustments -->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <span><b>${uiLabelMap.OrderTotalSalesTax}</b></span>
                </td>
                <td align="right" class="align-right" nowrap="nowrap">
                    <@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
                </td>
            </tr>
            <#-- grand total 
            <td align="right" class="align-right" colspan="10"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.DATotalAmountPayment}</b></div></td>
        	<td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">-->
            <tr>
                <td align="right" class="align-right" colspan="5">
                    <div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.DATotalAmountPayment}</b></div><#--${uiLabelMap.OrderTotalDue}-->
                </td>
                <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
                    <b><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></b>
                </td>
            </tr>
        </table>
	</div><!--#items-tab-->
	<div class="clear-all"></div>
</#if>