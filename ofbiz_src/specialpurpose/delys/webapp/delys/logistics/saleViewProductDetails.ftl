<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxprogressbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>

<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>

<#assign dataFieldPrice="[{ name: 'productId', type: 'string'},
						{ name: 'productPriceTypeId', type: 'string'},
						{ name: 'productPricePurposeId', type: 'string'},
						{ name: 'currencyUomId', type: 'string'},
						{ name: 'productStoreGroupId', type: 'string'},
						{ name: 'fromDate', type: 'date', other: 'Timestamp'},
						{ name: 'thruDate', type: 'date', other: 'Timestamp'},
						{ name: 'price', type: 'number'},
						{ name: 'termUomId', type: 'string'},
						{ name: 'priceWithoutTax', type: 'number'},
						{ name: 'priceWithTax', type: 'number'},
						{ name: 'taxAmount', type: 'number'},
						{ name: 'createdDate', type: 'string'},
						{ name: 'createdByUserLogin', type: 'string'},
						{ name: 'lastModifiedDate', type: 'date'},
						{ name: 'lastModifiedByUserLogin', type: 'string'},
						{ name: 'taxAuthPartyId', type: 'string'},
						{ name: 'taxAuthGeoId', type: 'string'},
						]"/>

<#assign columnlistPrice="
						{ text: '${uiLabelMap.ProductPriceType}', datafield: 'productPriceTypeId', align: 'center', width: 150, editable: false,
							cellsrenderer: function(row, colum, value){
								return '<span>' + mapProductPriceType[value] + '</span>';
							}
						},
						{ text: '${uiLabelMap.CommonPurpose}', datafield: 'productPricePurposeId', align: 'center', width: 100, editable: false},
						{ text: '${uiLabelMap.ProductCurrency}', datafield: 'currencyUomId', align: 'center', width: 100, editable: false},
						{ text: '${uiLabelMap.ProductProductStoreGroup}', datafield: 'productStoreGroupId', align: 'center', width: 120, editable: false},
						{ text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', align: 'center', width: 140, editable: false, cellsformat: 'dd/MM/yyyy',
							validation: function (cell, value) {
								var thisRow = cell.row;
								var thruDate = $('#jqxgridPrice').jqxGrid('getCell', thisRow, 'thruDate').value;
								if (thruDate == null) {
									return true;
								}
								thruDate = thruDate.getTime();
								value = value.getTime();
								if (thruDate < value) {
									$('#inputdatetimeeditorjqxgridthruDate').val('');
								}
								newFromDate = value;
								return true;
							}
						},
						{ text: '${uiLabelMap.CommonThruDate}',  datafield: 'thruDate', align: 'center', width: 140, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
							validation: function (cell, value) {
								if (value == null){
									return true;
								}
								if (!value) {
									value = value.getTime();
								}
								var thisRow = cell.row;
								var fromDate = $('#jqxgridPrice').jqxGrid('getCell', thisRow, 'fromDate').value.getTime();
								newFromDate == 0 ? fromDate = fromDate : fromDate = newFromDate;
								if (fromDate > value) {
									return { result: false, message: '${StringUtil.wrapString(uiLabelMap.ThruDateNotValid)}' };
								}
								return true;
							}
						},
						{ text: '${uiLabelMap.ProductCostPrice}',  datafield: 'price', align: 'center', width: 140, editable: true, columntype: 'numberinput', cellsalign: 'right',
							validation: function (cell, value) {
								if (value < 0) {
									return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
								}
								return true;
							},
							createeditor: function(row, column, editor){
								editor.jqxNumberInput({ theme: 'olbius', inputMode: 'simple', decimalDigits: 3 });
							}
						},
						{ text: '${uiLabelMap.FormFieldTitle_termUomId}',  datafield: 'termUomId', align: 'center', width: 160, editable: true},
						{ text: '${uiLabelMap.FormFieldTitle_priceWithoutTax}',  datafield: 'priceWithoutTax', align: 'center', width: 160, editable: true},
						{ text: '${uiLabelMap.FormFieldTitle_customPriceCalcService}',  datafield: 'priceWithTax', align: 'center', width: 170, editable: true},
						{ text: '${uiLabelMap.FormFieldTitle_taxPercentage}',  datafield: 'taxAmount', align: 'center', width: 140, editable: true},
						{ text: '${uiLabelMap.AccountingTaxAuthority}',  datafield: 'taxAuthPartyId', align: 'center', minwidth: 140, editable: true},
						{ text: '${uiLabelMap.AccountingTaxAuthorityGeo}',  datafield: 'taxAuthGeoId', align: 'center', width: 160, editable: true},
						{ text: '${uiLabelMap.ProductLastModifiedBy}',  datafield: 'lastModifiedByUserLogin', align: 'center', width: 140, editable: false}
						"/>

						
	<#assign dataFieldPacking="[{ name: 'productId', type: 'string'},
							{ name: 'uomFromId', type: 'string'},
							{ name: 'uomToId', type: 'string'},
							{ name: 'quantityConvert', type: 'number', other: 'BigDecimal'},
							{ name: 'fromDate', type: 'date', other: 'Timestamp'},
							{ name: 'thruDate', type: 'date', other: 'Timestamp'},
							{ name: 'description', type: 'string'}
							]"/>

	<#assign columnlistPacking="
						{ text: '${uiLabelMap.uomFromId}', datafield: 'uomFromId', align: 'center', width: 120, editable: false, 
							cellsrenderer: function(row, colum, value){
								mapQuantityUom[value]==undefined?mapQuantityUom[value]='Pallet':mapQuantityUom[value]=mapQuantityUom[value]
								return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
							}
						},
						{ text: '${uiLabelMap.uomToId}', datafield: 'uomToId', align: 'center', width: 120, editable: false, 
							cellsrenderer: function(row, colum, value){
								return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
							}
				        },
						{ text: '${uiLabelMap.QuantityConvert}', datafield: 'quantityConvert', align: 'center', width: 150, editable: true, columntype: 'numberinput', cellsalign: 'right', editable: false,
				        	validation: function (cell, value) {
			            		   if (value < 0) {
			            			   return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
			            		   }
			            		   return true;
			            	   }
						},
						{ text: '${uiLabelMap.description}',  datafield: 'description', align: 'center', minwidth: 250, editable: false },
				        { text: '${uiLabelMap.AvailableFromDate}',  datafield: 'fromDate', align: 'center', width: 200, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
							validation: function (cell, value) {
								var thisRow = cell.row;
								var thruDate = $('#jqxgridPacking').jqxGrid('getCell', thisRow, 'thruDate').value;
								if (thruDate == null) {
			     		    	   return true;
								}
								thruDate = thruDate.getTime();
								value = value.getTime();
								if (thruDate < value) {
			                 	   $('#inputdatetimeeditorjqxgridthruDate').val('');
			                    }
								newFromDatePacking = value;
								return true;
			         	    }
				        },
						{ text: '${uiLabelMap.AvailableThruDate}',  datafield: 'thruDate', align: 'center', width: 200, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', 
				        	validation: function (cell, value) {
				        		if (value == null){
			                    	return true;
			                    }
				        		value = value.getTime();
					        	var thisRow = cell.row;
					        	var fromDate = $('#jqxgridPacking').jqxGrid('getCell', thisRow, 'fromDate').value.getTime();
					        	newFromDatePacking == 0 ? fromDate = fromDate : fromDate = newFromDatePacking;
			                    if (fromDate > value) {
			                         return { result: false, message: '${StringUtil.wrapString(uiLabelMap.ThruDateNotValid)}' };
			                    }
			                    return true;
			                 }
			             },
						"/>	

 <#assign dataFieldSupplier="[{ name: 'productId', type: 'string'},
		         	{ name: 'partyId', type: 'string'},
		         	{ name: 'supplierProductId', type: 'string'},
		         	{ name: 'supplierProductName', type: 'string'},
		         	{ name: 'availableFromDate', type: 'date', other: 'Timestamp'},
		         	{ name: 'availableThruDate', type: 'string', other: 'Timestamp'},
		         	{ name: 'minimumOrderQuantity', type: 'number'},
		         	{ name: 'orderQtyIncrements', type: 'number'},
		         	{ name: 'agreementId', type: 'string'},
		         	{ name: 'lastPrice', type: 'number'},
		         	{ name: 'currencyUomId', type: 'string'},
		         	{ name: 'shippingPrice', type: 'number'}, 
		         	{ name: 'agreementItemSeqId', type: 'string'},
		         	{ name: 'canDropShip', type: 'string'},
		         	{ name: 'comments', type: 'string'},
		         	{ name: 'quantityUomId', type: 'string'},
		         	]"/>
 <#assign columnlistSupplier="
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_supplierProductId)}', datafield: 'supplierProductId', align: 'center', width: 150, editable: false },
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_productId)}', datafield: 'productId', align: 'center', width: 150, editable: false },
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_supplierProductName)}', datafield: 'supplierProductName', align: 'center', width: 150, editable: false },
	         		{ text: '${StringUtil.wrapString(uiLabelMap.Supplier)}', datafield: 'partyId', align: 'center', filtertype: 'checkedlist', width: 150, editable: false,
	         			cellsrenderer: function(row, colum, value){
	         			        return '<span>' + mapPartyGroup[value] + '</span>';
	         		    },
	         		    createfilterwidget: function (column, htmlElement, editor) {
	         	        	editor.jqxDropDownList({ dropDownHeight: 250, source: fixSelectAll(listParty), displayMember: 'partyId', valueMember: 'partyId' ,
	                             renderer: function (index, label, value) {
	                             	if (index == 0) {
	                             		return value;
	         						}
	         					    return mapPartyGroup[value];
	         	                } });
	         	        	editor.jqxDropDownList('checkAll');
	                     }
	         		},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_minimumOrderQuantity)}', dataField: 'minimumOrderQuantity', align: 'center', width: 120, editable: false, cellsalign: 'right', filtertype: 'number'},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.ProductPackingUom)}', datafield: 'quantityUomId', align: 'center', filtertype: 'checkedlist', width: 100, editable: false,
	         			cellsrenderer: function(row, colum, value){
	         		        return '<span>' + mapPackingUoms[value] + '</span>';
	         		    },
	         		    createfilterwidget: function (column, htmlElement, editor) {
	         	        	editor.jqxDropDownList({ dropDownHeight: 250, source: fixSelectAll(listPackingUoms), displayMember: 'uomId', valueMember: 'uomId' ,
	         	                renderer: function (index, label, value) {
	         	                	if (index == 0) {
	         	                		return value;
	         						}
	         					    return mapPackingUoms[value];
	         	                } });
	         	        	editor.jqxDropDownList('checkAll');
	         	        }
	         		},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_lastPrice)}', dataField: 'lastPrice', align: 'center', width: 120, columntype:'numberinput', cellsalign: 'right', filtertype: 'number',
	         			createeditor: function(row, column, editor){
	         				editor.jqxNumberInput({ theme: 'olbius', inputMode: 'simple', decimalDigits: 6 });
	         			},validation: function (cell, value) {
	         				if (value >= 0) {
	         					return true;
	         				}
	         				return { result: false, message: '${uiLabelMap.QuantityNotValid}' };
	         			}
	         		},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_shippingPrice)}',  datafield: 'shippingPrice', align: 'center', width: 120, columntype:'numberinput', cellsalign: 'right', filtertype: 'number',
	         			createeditor: function(row, column, editor){
	         				editor.jqxNumberInput({ theme: 'olbius', inputMode: 'simple', decimalDigits: 6 });
	         			},validation: function (cell, value) {
	         				if (value >= 0) {
	         					return true;
	         				}
	         				return { result: false, message: '${uiLabelMap.QuantityNotValid}' };
	         			}
	         		},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.currencyUomId)}',  datafield: 'currencyUomId', filtertype: 'checkedlist', width: 100, editable: false,
	         			cellsrenderer: function(row, colum, value){
	         		        return '<span>' + mapCurrencyUoms[value] + '</span>';
	         		    },
	         		    createfilterwidget: function (column, htmlElement, editor) {
	         	        	editor.jqxDropDownList({ dropDownHeight: 250, source: fixSelectAll(listCurrencyUoms), displayMember: 'uomId', valueMember: 'uomId' ,
	         	                renderer: function (index, label, value) {
	         	                	if (index == 0) {
	         	                		return value;
	         						}
	         					    return mapCurrencyUoms[value];
	         	                } });
	         	        	editor.jqxDropDownList('checkAll');
	         	        }
	         		},
	         		{ text: '${uiLabelMap.accComments}', datafield: 'comments', align: 'center', width: 200, editable: true, columntype:'input' },
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableFromDate)}', datafield: 'availableFromDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
	         		{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableThruDate)}', datafield: 'availableThruDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'}
	         		"/>
			             
<form method="post" action="" class="basic-form form-horizontal">
		<div class='row-fluid'>
		<div class='span2'>
			<div class='row-fluid margin-bottom10'>
				<div>
					<div id="productImageViewerTotal">
						<img src="/delys/images/logo/product_demo.png" height="180" width="170" style="max-width: none;"/>
					</div>
				</div>
			</div>
		</div>
		
		<div class='span10'>
		
				<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductProductId}</label>
						    <div class="controls" id="productIdTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.productCategoryIdAdd}</label>
						    <div class="controls" id= "productCategoryIdTotal"></div>
					    </div>
    	 			</div>
	 			</div>
	 			</div>
    	 			
	 			<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductInternalName}</label>
						    <div class="controls" id="productInternalNameTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductProductName}</label>
						    <div class="controls" id= "productNameTotal"></div>
					    </div>
    	 			</div>
	 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductBrandName}</label>
						    <div class="controls" id="productBrandNameTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.description}</label>
						    <div class="controls" id= "descriptionTotal"></div>
					    </div>
				    </div>
	 			</div>
	 			</div>
 			
	 			<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.Weight}</label>
						    <div class="controls" id="weightTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.NetWeight}</label>
						    <div class="controls" id= "netWeightTotal"></div>
					    </div>
    	 			</div>
	 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductWeightUomId}</label>
						    <div class="controls" id="weightUomIdTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.UnitLess}</label>
						    <div class="controls" id= "quantityUomIdTotal"></div>
					    </div>
    	 			</div>
	 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
        		<div class="span12">
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductAvailableFromDate}</label>
						    <div class="controls" id= "txtFromDateTotal"></div>
					    </div>
    	 			</div>
    	 			<div class="span6">
	    	 			<div class="control-group no-left-margin ">
						    <label>${uiLabelMap.ProductAvailableThruDate}</label>
						    <div class="controls" id= "txtThruDateTotal"></div>
					    </div>
				    </div>
	 			</div>
	 			</div>
 			</div>
		</div>
</form>

<div class="tabbable" style="margin-top:40px;">
    <ul class="nav nav-tabs" id="myTab">
      <li class="active"><a data-toggle="tab" href="#price"><i class="green fa-usd"></i> ${uiLabelMap.ProductAggregatedPrice}</a></li>
      <li class=""><a data-toggle="tab" href="#packing"> <i class="green fa-dropbox"></i> ${uiLabelMap.DAPacking}</a></li>
      <li class=""><a data-toggle="tab" href="#supplier"> <i class="green fa-shield"></i> ${uiLabelMap.PartySupplier}</a></li>
    </ul>
    <div class="tab-content" style="overflow:hidden;">
	  	<div id="price" class="tab-pane active">
			<div style="width: 100%!important;">
				<#assign addPricePermission = "false"/>
				<#if security.hasPermission("PRODUCT_PRICE_ADMIN", session)>
					<#assign addPricePermission = "true"/>
				</#if>
				<@jqGrid id="jqxgridPrice" filtersimplemode="false" addType="popup" dataField=dataFieldPrice columnlist=columnlistPrice clearfilteringbutton="false"
					showtoolbar="true" addrow=addPricePermission deleterow="false" alternativeAddPopup="alterpopupWindowPrice" editable="true" 
					url="jqxGeneralServicer?sname=JQGetListProductPrice&productId=${productId}" updateUrl="jqxGeneralServicer?sname=updateProductPrice&jqaction=U"
					createUrl="jqxGeneralServicer?sname=createProductPrice&jqaction=C" jqGridMinimumLibEnable="false"
					editColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
					addColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;priceWithoutTax;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
				/>
			</div>
		</div>
	  	<div id="packing" class="tab-pane">
	  			<#assign addPackingPermission = "false"/>
				<#if security.hasPermission("PRODUCT_PRICE_ADMIN", session)>
					<#assign addPackingPermission = "true"/>
				</#if>
			  	<@jqGrid id="jqxgridPacking" filtersimplemode="false" addType="popup" dataField=dataFieldPacking columnlist=columnlistPacking clearfilteringbutton="false"
					showtoolbar="true" addrow=addPackingPermission deleterow="false" alternativeAddPopup="alterpopupWindowPacking" editable="true"
					url="jqxGeneralServicer?sname=JQGetListProductConfigPacking&productId=${productId}" updateUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=U"
					createUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=C"
					editColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);description;productId;uomFromId;uomToId"
					addColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);description;productId;uomFromId;uomToId"
					/>
		</div>
		<div id="supplier" class="tab-pane">
			<@jqGrid id="jqxgridSupplier" filtersimplemode="true" addType="popup" dataField=dataFieldSupplier columnlist=columnlistSupplier clearfilteringbutton="true" viewSize="15"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
				url="jqxGeneralServicer?sname=JQGetListProductSupplier&productId=${productId}"
				/>
		</div>
    </div>
</div>

				
				
<div id="alterpopupWindowPrice" style="display:none;">
    <div>${uiLabelMap.AddNewProductPrice}</div>
    <div style="overflow-y: scroll;" id="contentField">
    	<div class="row-fluid">
    		<div class="span12" style="margin-top: 8px;">
	 			<div class="span2" style="text-align: right;">${uiLabelMap.ProductCostPrice}<span style="color:red;"> *</span></div>
	 			<div class="span3"><div id="ProductCostPrice1"></div></div>
	 			<div class="span3" style="text-align: right;">${uiLabelMap.FormFieldTitle_taxPercentage}:</div>
	 			<div class="span2"><div id="taxPercentage1"></div></div>
 			</div>
 			</div>
 			<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span2" style="text-align: right;">${uiLabelMap.ProductPriceType}<span style="color:red;"> *</span></div>
	 			<div class="span3"><div id="ProductPriceType1"></div></div>
	 			<div class="span3" style="text-align: right;">${uiLabelMap.AccountingTaxAuthority}:</div>
	 			<div class="span2"><input type='text' id="AccountingTaxAuthority1"/></div>
 			</div>
 			</div>
 			<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span2" style="text-align: right;">${uiLabelMap.CommonPurpose}<span style="color:red;"> *</span></div>
    	 		<div class="span3"><div id="CommonPurpose1"></div></div>
    	 		<div class="span3" style="text-align: right;">${uiLabelMap.AccountingTaxAuthorityGeo}:</div>
	 			<div class="span2"><input type='text' id="AccountingTaxAuthorityGeo1"/></div>
 			</div>
 			</div>
 			<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span2" style="text-align: right;">${uiLabelMap.ProductCurrencyUomId}<span style="color:red;"> *</span></div>
    	 		<div class="span3"><div id="currencyUomId1"></div></div>
    	 		<div class="span3" style="text-align: right;">${uiLabelMap.TaxInPrice}:</div>
    	 		<div class="span2"><div id="TaxInPrice1"></div></div>
 			</div>
 			</div>
 			<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span2" style="text-align: right;">${uiLabelMap.ProductProductStoreGroup}:</div>
    	 		<div class="span3"><div id="ProductProductStoreGroup1"></div></div>
    	 		<div class="span3" style="text-align: right;">${uiLabelMap.CommonFromDateTime}<span style="color:red;"> *</span></div>
    	 		<div class="span2"><div id="CommonFromDateTime1"></div></div>
	 		</div>
	 		</div>
	 		<div class="row-fluid">
	 		<div class="span12" style="margin-top: 8px;">
	 			<div class="span2"></div>
    	 		<div class="span3"></div>
    	 		<div class="span3" style="text-align: right;">${uiLabelMap.CommonThruDateTime}:</div>
    	 		<div class="span2"><div id="CommonThruDateTime1"></div></div>
    	 	</div>
    	 	</div>
    	 	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
    	 	<div class="row-fluid">
    	 		<div class="span12 margin-top10">
                    <button id='alterCancel' class="btn btn-primary form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
                    <button id='alterSave' class="btn btn-danger form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
                </div>
            </div>
         </div>
         </div>
    </div>
</div>
				
<div id="alterpopupWindowPacking"  style="display:none;">
<div>${uiLabelMap.AddNewProductPacking}</div>
<div class="form-window-content" style="overflow-y: hidden;">
	<div class='row-fluid'>
		<div class='span6'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomFromId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomFromId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.QuantityConvert}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="quantityConvert1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="fromDate1"></div></div>
			</div>
		</div>
		
		<div class='span5'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomToId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomToId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'></div>
				<div class='span7'></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableThruDate}&nbsp;&nbsp;</div>
				<div class='span7'><div id="thruDate1"></div></div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12" style="margin-top: 8px;">
	 		<div class="span3" style="padding-left: 125px;">${uiLabelMap.description}&nbsp;&nbsp;&nbsp;</div>
 			<div class="span9 no-left-margin" style="margin-left: -21px!important;"><textarea id="tarDescription"></textarea></div>
		</div>
	</div>
    <div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancelPacking" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSavePacking" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>

</div>
</div>


<div id="jqxwindowEditor" style="display:none;">
<div>${uiLabelMap.EditorDescripton}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12">
			<textarea id="tarDescriptionEditor"></textarea>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 margin-top10">
				<button id='cancelEdit' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='saveEdit' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>

<script>
	var newFromDate = 0;
	var newFromDatePacking = 0;
	<#assign listPartyRole = delegator.findList("PartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "SUPPLIER"), null, null, null, false)>
	<#assign listProduct = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false)>
	<#assign listPartyGroup = delegator.findList("PartyGroup", null, null, null, null, false) />
	
	var mapPartyGroup = {
	        <#if listPartyGroup?exists>
	        		<#list listPartyGroup as item>
	        			"${item.partyId?if_exists}": "${StringUtil.wrapString(item.groupName?if_exists)}",
	        		</#list>
	       </#if>
			};
var listParty = [
		<#if listPartyRole?exists>
			<#list listPartyRole as item>
			{
				partyId: '${item.partyId?if_exists}',
				groupName: mapPartyGroup['${item.partyId?if_exists}']
			},
			</#list>
		</#if>
           ];
var listProduct = [
         <#if listProduct?exists>
             <#list listProduct as item>
             {
            	 productId: '${item.productId?if_exists}',
            	 productName: '${item.productName?if_exists}'
             },
             </#list>
         </#if>
         ];

var listCurrencyUoms = [
			<#if listCurrencyUoms?exists>
				<#list listCurrencyUoms as item>
				{
					uomId: '${item.uomId?if_exists}',
					description: "${StringUtil.wrapString(item.description)}"
				},
				</#list>
			</#if>
                 ];

var mapCurrencyUoms = {
	        <#if listCurrencyUoms?exists>
	        		<#list listCurrencyUoms as item>
	        			"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	        		</#list>
	       </#if>
			};
var listPackingUoms = [
                <#if listPackingUoms?exists>
                <#list listPackingUoms as item>
                {
                	uomId: '${item.uomId?if_exists}',
                	description: "${StringUtil.wrapString(item.description)}"
                },
                </#list>
                </#if>
                ];

var mapPackingUoms = {
			<#if listPackingUoms?exists>
				<#list listPackingUoms as item>
				"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
				</#list>
			</#if>
			};
	
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [
						<#if quantityUoms?exists>
							<#list quantityUoms as item> 
							{
								quantityUomId: "${item.uomId?if_exists}",
								description: "${StringUtil.wrapString(item.description?if_exists)}"
							},
							</#list>
						</#if>
	                    ];
	
	var mapQuantityUom = {
						<#if quantityUoms?exists>
							<#list quantityUoms as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
								</#list>
							</#if>
							};
	
	var mapProductPriceType = {
						<#if listProductPriceType?exists>
							<#list listProductPriceType as item>
								"${item.productPriceTypeId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
							</#list>
						</#if>
						};
	
	var listProductPriceType = [
								<#if listProductPriceType?exists>
									<#list listProductPriceType as item> 
									{
										productPriceTypeId: "${item.productPriceTypeId?if_exists}",
										description: "${StringUtil.wrapString(item.description?if_exists)}"
									},
									</#list>
								</#if>
	                            ];
	
	
	
	var listProductPricePurpose = [
								<#if listProductPricePurpose?exists>
									<#list listProductPricePurpose as item>
									{
										productPricePurposeId: "${item.productPricePurposeId?if_exists}",
										description: "${StringUtil.wrapString(item.description?if_exists)}"
									},
									</#list>
								</#if>
	                               ];
	
	var listCurrencyUom = [
						<#if listCurrencyUom?exists>
							<#list listCurrencyUom as item>
							{
								uomId: "${item.uomId?if_exists}",
								description: "${StringUtil.wrapString(item.description?if_exists)}"
							},
							</#list>
						</#if>
	                       ];
	var listProductStoreGroup = [
								<#if listProductStoreGroup?exists>
									<#list listProductStoreGroup as item>
									{
										productStoreGroupId: "${item.productStoreGroupId?if_exists}",
										productStoreGroupName: "${StringUtil.wrapString(item.productStoreGroupName?if_exists)}"
									},
									</#list>
								</#if>
	                             ];
	
	
	
	$("#uomFromId1").jqxDropDownList({ source: quantityUomData, width: '220px', displayMember: "description", valueMember: "quantityUomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#uomToId1").jqxDropDownList({ source: [], width: '220px', displayMember: "description", valueMember: "quantityUomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	
	$('#uomFromId1').on('change', function (event){     
		    var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var value = item.value;
			    var dataAvalible = mapRalationPacking[value];
			    var soucreToId = [];
			    for ( var x in quantityUomData) {
					if (_.indexOf(dataAvalible, quantityUomData[x].quantityUomId) == -1) {
						soucreToId.push(quantityUomData[x]);
					}
				}
			    $("#uomToId1").jqxDropDownList({ source: soucreToId });
			}
	});
	
	$("#quantityConvert1").jqxNumberInput({ inputMode: 'simple', spinButtons: true, theme: "olbius", width: '220px', decimalDigits: 0, min: 0  });
	$("#fromDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
	$("#thruDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
	$("#alterpopupWindowPacking").jqxWindow({
        width: 900, maxWidth: 1000, theme: "olbius", minHeight: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterpopupWindowPacking"), modalOpacity: 0.7
    });
	$('#alterpopupWindowPacking').on('open', function () {
		$('#thruDate1').val(null);
		$("#uomFromId1").jqxDropDownList('clearSelection');
		$("#uomToId1").jqxDropDownList('clearSelection');
		$("#quantityConvert1").jqxNumberInput('val', 0);
		$('#tarDescription').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '98%'
	    });
		$("#tarDescription").jqxEditor('val', "");
	});
	$('#alterpopupWindowPacking').on('close', function () {
		$('#alterpopupWindowPacking').jqxValidator('hide');
	});
	
    $("#alterSavePacking").click(function () {
        if ($('#alterpopupWindowPacking').jqxValidator('validate')) {
        	var row = {};
        	row.uomFromId = $("#uomFromId1").val();
    		row.uomToId = $("#uomToId1").val();
			row.quantityConvert = $("#quantityConvert1").val();
			row.thruDate = $("#thruDate1").val().toMilliseconds();
			row.fromDate = $("#fromDate1").val().toMilliseconds();
			row.description = $("#tarDescription").val();
			row.productId = "${productId}";
        	$("#jqxgridPacking").jqxGrid('addRow', null, row, "first");
            $("#jqxgridPacking").jqxGrid('clearSelection');
            $("#jqxgridPacking").jqxGrid('selectRow', 0);
            $("#alterpopupWindowPacking").jqxWindow('close');
            getListProductConfigPackingAjax();
            setTimeout(function(){
            	$("#jqxgridPacking").jqxGrid('updatebounddata');
            }, 500);
        }
    });
    $('#alterpopupWindowPacking').jqxValidator({
        rules: [
                { input: '#uomFromId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#uomFromId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#uomToId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#uomToId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#quantityConvert1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#quantityConvert1").val();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#fromDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#fromDate1").val().toMilliseconds();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#thruDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var thruDate = $("#thruDate1").val().toMilliseconds();
                		if (!thruDate) {
                			return true;
						}
                		var fromDate = $("#fromDate1").val().toMilliseconds();
                		if (fromDate <= thruDate) {
                			return true;
						}
                		return false;
                	}
                }
               ]
    });
    $("#jqxgridPacking").on("cellDoubleClick", function (event){
    		    var args = event.args;
    		    var rowBoundIndex = args.rowindex;
    		    var rowVisibleIndex = args.visibleindex;
    		    var rightClick = args.rightclick; 
    		    var ev = args.originalEvent;
    		    var columnIndex = args.columnindex;
    		    var dataField = args.datafield;
    		    var value = args.value;
    		    if (dataField == "description") {
    		    	openPoupEditDescription(rowBoundIndex, value);
				}
	});
    var rowIndexEditing;
    function openPoupEditDescription(rowBoundIndex, value) {
    	rowIndexEditing = rowBoundIndex;
    	$("#jqxwindowEditor").jqxWindow('open');
    	$('#tarDescriptionEditor').jqxEditor({
	        theme: 'olbiuseditor'
	    });
    	$("#tarDescriptionEditor").jqxEditor('val', value);
	}
    
    $("#jqxwindowEditor").jqxWindow({ theme: 'olbius',
	    width: 550, maxWidth: 1845, minHeight: 330, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
	});
	$("#saveEdit").click(function () {
		var newValue = $('#tarDescriptionEditor').val();
		$("#jqxgridPacking").jqxGrid('setCellValue', rowIndexEditing, "description", newValue);
		$("#jqxwindowEditor").jqxWindow('close');
	});
    $(document).ready(function() {
    	$('#thruDate1').val(null);
    	getListProductConfigPackingAjax();
    });
    var productId = "${productId}";
    var mapRalationPacking = {};
    function getListProductConfigPackingAjax() {
    	$.ajax({
  		  url: "getListProductConfigPackingAjax",
  		  type: "POST",
  		  data: {productId: productId},
  		  success: function(data) {
  			mapRalationPacking = data["mapRalationPacking"];
  		  }
  	  	}).done(function() {
  	  		
  	  	});
	}
	
	
	
	var listTaxInPrice = ['Y', 'N'];

	$("#ProductCostPrice1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '218px', decimalDigits: 0, min: 0 });
	$("#taxPercentage1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '218px', decimalDigits: 0, min: 0 });
	
	$("#ProductPriceType1").jqxDropDownList({ source: listProductPriceType, displayMember: "description", valueMember: "productPriceTypeId", width: '218px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#CommonPurpose1").jqxDropDownList({ source: listProductPricePurpose, displayMember: "description", valueMember: "productPricePurposeId", width: '218px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	
	$("#currencyUomId1").jqxDropDownList({ source: listCurrencyUom,displayMember: "description", valueMember: "uomId", width: '218px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#ProductProductStoreGroup1").jqxDropDownList({ source: listProductStoreGroup, displayMember: "productStoreGroupId", valueMember: "productStoreGroupId", width: '218px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#TaxInPrice1").jqxDropDownList({ source: listTaxInPrice, width: '218px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	
	$("#CommonFromDateTime1").jqxDateTimeInput({theme: "olbius", width: '218px' });
	$("#CommonThruDateTime1").jqxDateTimeInput({theme: "olbius", width: '218px' });
	
	$("#alterpopupWindowPrice").jqxWindow({ theme: 'olbius',
        width: 850, maxWidth: 1000, minHeight: 340, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
	$('#alterpopupWindowPrice').on('close', function () {
		$('#contentField').jqxValidator('hide');
	});
	
    $("#alterSave").click(function () {
        if ($('#contentField').jqxValidator('validate')) {
        	var row;
        	var fromDate = $('#CommonFromDateTime1').val().toMilliseconds();
	        var thruDate = $('#CommonThruDateTime1').val().toMilliseconds();
	        var taxPercentage = $('#taxPercentage1').val();
	        
	        var price = $('#ProductCostPrice1').val();
	        var productPriceTypeId = $('#ProductPriceType1').val();
	        var productPricePurposeId = $('#CommonPurpose1').val();
	        var currencyUomId = $('#currencyUomId1').val();
            row = {
            		productPriceTypeId: productPriceTypeId,
            		productPricePurposeId: productPricePurposeId,
            		currencyUomId: currencyUomId,
            		productStoreGroupId:$('#ProductProductStoreGroup1').val(),
            		taxAuthPartyId:$('#AccountingTaxAuthority1').val(),
            		taxAuthGeoId:$('#AccountingTaxAuthorityGeo1').val(),
            		price: price,
            		fromDate: fromDate,
            		thruDate: thruDate,
            		productId:'${productId}',
            		taxPercentage: $('#taxPercentage1').val(),
            	  };
        	$("#jqxgridPrice").jqxGrid('addRow', null, row, "first");
            $("#jqxgridPrice").jqxGrid('clearSelection');
            $("#jqxgridPrice").jqxGrid('selectRow', 0);
            $("#alterpopupWindowPrice").jqxWindow('close');
            setTimeout(function(){
            	$("#jqxgridPrice").jqxGrid('updatebounddata');
            }, 500);
		}
    });
    
    $('#contentField').jqxValidator({
        rules: [
                { input: '#ProductCostPrice1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#ProductCostPrice1").val();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#ProductPriceType1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductPriceType1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#CommonPurpose1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#CommonPurpose1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#currencyUomId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#currencyUomId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#CommonFromDateTime1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#CommonFromDateTime1").val().toMilliseconds();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#CommonThruDateTime1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var thruDate = $("#CommonThruDateTime1").val().toMilliseconds();
                		if (!thruDate) {
                			return true;
						}
                		var fromDate = $("#CommonFromDateTime1").val().toMilliseconds();
                		if (fromDate <= thruDate) {
                			return true;
						}
                		return false;
                	}
                }
               ]
    });

<#if thisProduct?exists>
$(document).ready(function() {
	getProductImages("${thisProduct.detailImageUrl?if_exists}");
	updateMode = true;
	$(".widget-header").find('h4').text("${StringUtil.wrapString(thisProduct.description?if_exists)}" + " [${StringUtil.wrapString(thisProduct.productId?if_exists)}]");
	$("#productIdTotal").text("${StringUtil.wrapString(productId?if_exists)}");
	$("#productCategoryIdTotal").text('${thisProduct.primaryProductCategoryId?if_exists}');
	$("#productInternalNameTotal").text("${StringUtil.wrapString(thisProduct.internalName?if_exists)}");
	$("#productNameTotal").text("${StringUtil.wrapString(thisProduct.productName?if_exists)}");
	$("#productBrandNameTotal").text("${StringUtil.wrapString(thisProduct.brandName?if_exists)}");
	$("#descriptionTotal").html('${StringUtil.wrapString(thisProduct.description?if_exists)}');
	
	var fromDate = "${thisProduct.fromDate?if_exists}";
	fromDate?fromDate=fromDate.timeStampToTimeOlbius():fromDate = "";
	$('#txtFromDateTotal').text(fromDate);
	
	var thruDate = "${thisProduct.thruDate?if_exists}";
	thruDate?thruDate=thruDate.timeStampToTimeOlbius():thruDate = "";
	$('#txtThruDateTotal').text(thruDate);
	
	var weight = "${thisProduct.weight?if_exists}";
	weight = weight.replace(",", ".");
	$("#weightTotal").text(weight);
	
	var productWeight = "${thisProduct.productWeight?if_exists}";
	productWeight = productWeight.replace(",", ".");
	$("#netWeightTotal").text(productWeight);
	
	$("#weightUomIdTotal").text('${thisProduct.weightUomId?if_exists}');
	$("#quantityUomIdTotal").text('${thisProduct.quantityUomId?if_exists}');
});
function getProductImages(nodePath) {
	if (nodePath) {
		var fileUrl = [];
    	jQuery.ajax({
            url: "getFileScanAjax",
            type: "POST",
            data: {nodePath : nodePath },
            success: function(res) {
            	fileUrl = res["childNodes"];
            }
        }).done(function() {
        	if (fileUrl) {
        		showProductImages(fileUrl);
			}
    	});
	}
}
function showProductImages(fileUrl) {
	if($('.jqxScrollView').length > 0){
		$('.jqxScrollView').jqxScrollView("destroy");
	}
	htmlImage = "<div class='jqxScrollView'>";
	for ( var i in fileUrl) {
		var link = "/webdav/repository/default" + "/DELYS/delys/productImage" + "${StringUtil.wrapString(thisProduct.productId?if_exists)}" + "/" + fileUrl[i];
		link = encodeURI(link);
		htmlImage += "<div><div class='photo' style='background-image:url(\"" + link + "\")'></div></div>";
	}
	setTimeout(function() {
		htmlImage += "</div>";
		$("#productImageViewerTotal").html(htmlImage);
		$('.jqxScrollView').jqxScrollView({ width: 170, height: 180, buttonsOffset: [0, 0]});
	}, 100);
}
</#if>
function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
	    };
	var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
	uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	return uniqueRecords2;
}
</script>
<style type="text/css">
.photo
{
    width: 170px;
    height: 180px;
    background-color: white;
    background-position: center;
    background-repeat: no-repeat;
	background-size: contain;
}
body {
	  -webkit-user-select: none;
	     -moz-user-select: -moz-none;
	      -ms-user-select: none;
	          user-select: none;
}
.jqx-grid-toolbar-olbius {
	width : 100%!important;
}
.jqx-grid-olbius {
	width : 100%!important;
}
</style>