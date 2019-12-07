<script type="text/javascript" src="/aceadmin/assets/js/Underscore1.8.3.js"></script>

<#assign dataField="[{name: 'productId', type: 'string'},
					{name: 'marketingProductId', type: 'string'},
		            {name: 'marketingPlaceId', type: 'string'},
		            {name: 'productTypeId', type: 'string'},
		            {name: 'uomId', type: 'string'},
		            {name: 'quantity', type: 'number'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsProductId}',datafield: 'productId', columntype: 'dropdownlist', cellsalign: 'left',
							cellsrenderer: function(row, colum, value, a,b, data){
								var item = _.findWhere(listProsucts, {productId : value});
								if(item){
									return '<span>['+item.productId+'] ' + item.productName + '</span>';
								}
							}, createeditor: function(row, column, editor){
								editor.jqxDropDownList({
									source: listProsucts,
									filterable: true,
									displayMember: 'productId',
									valueMember: 'productId',
									autoBind: true,
									searchMode: 'contains',
									placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
									selectionRenderer: function (a, b, c) {
						                var item = editor.jqxDropDownList('getSelectedItem');
						                if (item) {
											return '[' + item.value  + ']' + ' ' + item.originalItem.productName;
						                }
						                return '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}';
						            },
						            renderer: function (index, label, value) {
								        var item = listProsucts[index];
								        return '[' + item.productId  + ']' + ' ' + item.productName;
								    }
						        });
							}
					},
					{text: '${uiLabelMap.quantity}', datafield: 'quantity', width: '150', columntype: 'numberinput',
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: '${StringUtil.wrapString(uiLabelMap.DmsQuantityNotValid)}' };
							}
							return true;
						}
					},
					{text: '${uiLabelMap.uomId}', datafield: 'uomId', width: '150', columntype: 'dropdownlist',
						cellsrenderer: function(row, colum, value){
							for ( var x in quantityUomData) {
								if (value == quantityUomData[x].uomId) {
									value = quantityUomData[x].description;
								}
							}
							return '<span>' + value + '</span>';
						}, createeditor: function(row, column, editor){
							editor.jqxDropDownList({ autoDropDownHeight: true, source: quantityUomData, displayMember: 'uomId', valueMember: 'uomId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
					            renderer: function (index, label, value) {
					                var datarecord = quantityUomData[index];
					                return datarecord.description;
					            },selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
								for ( var x in quantityUomData) {
											if (item.value == quantityUomData[x].uomId) {
												item.label = quantityUomData[x].description;
											}
										}
											return '<span title=' + item.value +'>' + item.label + '</span>';
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
					            }
					        });
						}
					},
					{text: '${uiLabelMap.ProductType}', datafield: 'productTypeId', columntype: 'dropdownlist', width: '150',
						cellsrenderer: function(row, colum, value){
							for ( var x in productType) {
								if (value == productType[x].productTypeId) {
									value = productType[x].description;
								}
							}
							return '<span>' + value + '</span>';
						}, createeditor: function(row, column, editor){
						editor.jqxDropDownList({ autoDropDownHeight: true,
							source: productType,
							displayMember: 'productTypeId',
							valueMember: 'productTypeId',
							width: 250,
							placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
								autoBind: true,
	                            renderer: function (index, label, value) {
				                    var datarecord = productType[index];
				                    return datarecord.description;
				                },
				                selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
									for ( var x in productType) {
											if (item.value == productType[x].productTypeId) {
												return productType[x].description;
											}
										}
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
				                }
			                });
						}
					},
					{text: '${uiLabelMap.CommonPlace}', datafield: 'marketingPlaceId', width: '150', hidden: true}"/>
<@jqGrid url="" id="MarketingProductGrid" customLoadFunction="true" jqGridMinimumLibEnable="false" selectionmode="checkbox"
		addrow="true" dataField=dataField columnlist=columnlist virtualmode="false" pageable="false" editable="true"
		width="100%" autoshowloadelement="false" showdefaultloadelement="false"
		showtoolbar="true" autorowheight="true" deleterow="true"  filterable="false" editmode="click"
		isShowTitleProperty="false" addType="popup" alternativeAddPopup="popupAddProduct" sortable="false"
		customcontrol1="fa fa-bolt@${uiLabelMap.QuickAdd}@javascript: void(0);@MKProduct.quickAddProduct()"/>
