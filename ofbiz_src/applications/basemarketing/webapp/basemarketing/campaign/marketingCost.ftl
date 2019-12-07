<#assign dataField="[{name: 'marketingCostId', type: 'string'},
		            {name: 'marketingCostTypeId', type: 'string'},
		            {name: 'description', type: 'string'},
		            {name: 'unitPrice', type: 'string'},
		            {name: 'currencyUomId', type: 'string'},
		            {name: 'quantity', type: 'number'},
		            {name: 'quantityUomId', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.FormTitle_marketingCostTypeId}', datafield: 'marketingCostTypeId', columntype: 'dropdownlist',
						cellsrenderer: function(row, colum, value){
							for ( var x in marketingCostTypeData) {
								if (value == marketingCostTypeData[x].marketingCostTypeId) {
									value = marketingCostTypeData[x].name;
								}
							}
							return '<span>' + value + '</span>';
						}, createeditor: function(row, column, editor){
							editor.jqxDropDownList({ autoDropDownHeight: true, source: marketingCostTypeData, displayMember: 'marketingCostTypeId', valueMember: 'marketingCostTypeId', dropDownHeight: 250, placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
					            renderer: function (index, label, value) {
					                var datarecord = marketingCostTypeData[index];
					                if(datarecord.name){
								return datarecord.name;
					                }else if( datarecord.description){
								return datarecord.name;
					                }
					                 return value;
					            },selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
										for ( var x in marketingCostTypeData) {
											if (item.value == marketingCostTypeData[x].marketingCostTypeId) {
												return marketingCostTypeData[x].description;
											}
										}
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
					            }
					        });
						}
					},
					{text: '${uiLabelMap.KDescription}', datafield: 'description', width: '150'},
					{text: '${uiLabelMap.unitPrice}', datafield: 'unitPrice', width: '150', columntype: 'numberinput',
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: '${StringUtil.wrapString(uiLabelMap.DmsPriceNotValid)}' };
							}
							return true;
						}
					},
					{text: '${uiLabelMap.DACurrencyUomId}', datafield: 'currencyUomId', width: '150', columntype: 'dropdownlist',
						cellsrenderer: function(row, colum, value){
							for ( var x in currencyUomData) {
								if (value == currencyUomData[x].uomId) {
									value = currencyUomData[x].description;
								}
							}
							return '<span>' + value + '</span>';
						}, createeditor: function(row, column, editor){
						editor.jqxDropDownList({ source: currencyUomData, filterable: true, displayMember: 'uomId', valueMember: 'uomId', dropDownHeight: 250, placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
	                            renderer: function (index, label, value) {
				                    var datarecord = currencyUomData[index];
				                    return datarecord.description;
				                },selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
								for ( var x in currencyUomData) {
											if (item.value == currencyUomData[x].uomId) {
												return currencyUomData[x].description;
											}
										}
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
				                }
			                });
			                editor.jqxDropDownList('val', 'VND');
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
					{text: '${uiLabelMap.uomId}', datafield: 'quantityUomId', width: '150', columntype: 'dropdownlist',
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
												return quantityUomData[x].description;
											}
										}
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
				                }
			                });
						}
					},
					"
					/>
<@jqGrid url="" id="MarketingCost" customLoadFunction="true" jqGridMinimumLibEnable="false"
		addrow="true" dataField=dataField columnlist=columnlist virtualmode="false"
		width="100%" autoshowloadelement="false" showdefaultloadelement="false" sortable="false" pageable="false"
		showtoolbar="true" autorowheight="true" deleterow="true"  filterable="false"  selectionmode="checkbox"
		isShowTitleProperty="false" addType="popup" alternativeAddPopup="popupAddCost" editmode="click" editable="true"
		customcontrol1="fa fa-bolt@${uiLabelMap.QuickAdd}@javascript: void(0);@MKCost.quickAddCost()"/>
