<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
	 
<#assign dataField="[{ name: 'productId', type: 'string'},
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
					   { name: 'taxAuthGeoId', type: 'string'}
					   ]"/>

<#assign columnlist="
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
					var thruDate = $('#jqxgrid').jqxGrid('getCell', thisRow, 'thruDate').value;
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
				   var fromDate = $('#jqxgrid').jqxGrid('getCell', thisRow, 'fromDate').value.getTime();
		        	
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
        	   }
		   },
		   { text: '${uiLabelMap.FormFieldTitle_termUomId}',  datafield: 'priceWithoutTax', align: 'center', width: 160, editable: true},
		   { text: '${uiLabelMap.FormFieldTitle_customPriceCalcService}',  datafield: 'priceWithTax', align: 'center', width: 170, editable: true},
		   { text: '${uiLabelMap.FormFieldTitle_taxPercentage}',  datafield: 'taxAmount', align: 'center', width: 140, editable: true},
		   { text: '${uiLabelMap.AccountingTaxAuthority}',  datafield: 'taxAuthPartyId', align: 'center', minwidth: 140, editable: true},
		   { text: '${uiLabelMap.AccountingTaxAuthorityGeo}',  datafield: 'taxAuthGeoId', align: 'center', width: 160, editable: true},
		   { text: '${uiLabelMap.ProductLastModifiedBy}',  datafield: 'lastModifiedByUserLogin', align: 'center', width: 140, editable: false}
		   "/>
			   
<#if security.hasEntityPermission("PRODUCT_PRICE", "_ADMIN", session)>
   <@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductPrice&productId=${productId}"
		updateUrl="jqxGeneralServicer?sname=updateProductPrice&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createProductPrice&jqaction=C"
		editColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
		addColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;priceWithoutTax;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
	/>
<#else>
   <@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
		url="jqxGeneralServicer?sname=JQGetListProductPrice&productId=${productId}"
		updateUrl="jqxGeneralServicer?sname=updateProductPrice&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createProductPrice&jqaction=C"
		editColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
		addColumns="productId;productPriceTypeId;productPricePurposeId;currencyUomId;productStoreGroupId;priceWithoutTax;taxAuthPartyId;taxAuthGeoId;price(java.math.BigDecimal);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp)"
		/>
</#if>
			   
<div id="alterpopupWindow" style="display:none;">
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
		    
<script>
    var newFromDate = 0;
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
    	$('#CommonThruDateTime1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
    	$('#CommonFromDateTime1').on('valueChanged', function (event){  
    		var jsDate = event.args.date; 
    		$('#CommonThruDateTime1 ').jqxDateTimeInput('setMinDate', jsDate);
    	});
    	$("#alterpopupWindow").jqxWindow({ theme: 'olbius',
            width: 850, maxWidth: 1000, minHeight: 340, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
        });
    	$('#alterpopupWindow').on('close', function () {
    		$('#contentField').jqxValidator('hide');
    		$('#CommonThruDateTime1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
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
            	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	            $("#jqxgrid").jqxGrid('clearSelection');
	            $("#jqxgrid").jqxGrid('selectRow', 0);
	            $("#alterpopupWindow").jqxWindow('close');
	            setTimeout(function(){
	            	$("#jqxgrid").jqxGrid('updatebounddata');
	            }, 500);
			}
        });
        $(document).ready(function() {
			$('#CommonThruDateTime1').val(null);
//			var mytab = "<li><span class='divider'><i class='icon-angle-right'></i></span>${uiLabelMap.ListProductPrice}</li>";
//			$(".breadcrumb").append(mytab);
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
</script>