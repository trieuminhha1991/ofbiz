<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
 <script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 <#assign dataField="[{ name: 'productId', type: 'string'},
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
<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
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
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		url="jqxGeneralServicer?sname=JQGetListProductSupplier&productId=${productId?if_exists}" updateUrl="jqxGeneralServicer?sname=updateSupplierProduct&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createSupplierProduct&jqaction=C" groupable="false"
		editColumns="agreementId;agreementItemSeqId;availableFromDate(java.sql.Timestamp);availableThruDate(java.sql.Timestamp);canDropShip;comments;currencyUomId;lastPrice(java.math.BigDecimal);minimumOrderQuantity(java.math.BigDecimal);orderQtyIncrements(java.math.BigDecimal);partyId;productId;quantityUomId;shippingPrice(java.math.BigDecimal);supplierProductId;supplierProductName"
		addColumns="agreementId;agreementItemSeqId;availableFromDate(java.sql.Timestamp);availableThruDate(java.sql.Timestamp);canDropShip;comments;currencyUomId;lastPrice(java.math.BigDecimal);minimumOrderQuantity(java.math.BigDecimal);orderQtyIncrements(java.math.BigDecimal);partyId;productId;quantityUomId;shippingPrice(java.math.BigDecimal);supplierProductId;supplierProductName"
		removeUrl="jqxGeneralServicer?sname=removeSupplierProduct&jqaction=D" deleteColumn="currencyUomId;minimumOrderQuantity(java.math.BigDecimal);availableFromDate(java.sql.Timestamp);partyId;productId"
		/>

<div id="alterpopupWindow" style="display:none;">
	<div style="font-size:18px!important;">${uiLabelMap.AddNewProductSupplier}</div>
	<div style="overflow-y: hidden;">
				<div id="notifyStatus"></div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 18px;">
			 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.ProductSupplier}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="ProductSupplier1"></div></div>
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_productId}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="ProductId1"></div></div>
		 			</div>
				</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
			 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_supplierProductId}<span style="color:red;"> *</span></label></div>
				 		<div class="span2"><input type='text' id="supplierProductId1"></input></div>
				 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_supplierProductName}<span style="color:red;"> *</span></label></div>
		    	 		<div class="span2"><input type='text' id="supplierProductName1"></input></div>
		 			</div>
				</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
		    	 		<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_shippingPrice}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="shippingPrice1"></div></div>
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_lastPrice}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="lastPrice1"></div></div>
		 			</div>
				</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
			 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.ProductCurrencyUomId}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="ProductCurrencyUomId1"></div></div>
		    	 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.ProductPackingUom}<span style="color:red;"> *</span></label></div>
				 		<div class="span2"><div id="quantityUomId1"></div></div>
		 			</div>
				</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
			 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_minimumOrderQuantity}</label></div>
				 		<div class="span2"><div id="minimumOrderQuantity1"></div></div>
				 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.accComments}</label></div>
				 		<div class="span2"><input type='text' id="comments1" /></div>
			 		</div>
		 		</div>
		 		<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
			 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_availableFromDate}<span style="color:red;"> *</span></label></div>
			 			<div class="span2"><div id="availableFromDate1"></div></div>
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_availableThruDate}</label></div>
				 		<div class="span2"><div id="AvailableThruDate1"></div></div>
			 		</div>
		 		</div>
		 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		 		<div class="row-fluid">
	            <div class="span12 margin-top10">
	            	<div class="span12">
	            		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.btnCancel}</button>
	            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	        		</div>
	            </div>
	        </div>
	</div>
</div>


<div id="jqxNotificationStatus">
	<div id="notificationContentStatus">
	</div>
</div>

<#assign listPartyRole = delegator.findList("PartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "SUPPLIER"), null, null, null, false)>
<#assign listProduct = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false)>
<#assign listPartyGroup = delegator.findList("PartyGroup", null, null, null, null, false) />
<script>
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
	$(document).ready(function() {
		$('#AvailableThruDate1').val(null);
		if (!"${productId?if_exists}") {
			window.location.href = "listProducts";
		}
	});
	 
	$("#jqxNotificationStatus").jqxNotification({ width: "100%", appendContainer: "#notifyStatus", opacity: 0.9, autoClose: true, template: "info" });
	$("#ProductSupplier1").jqxDropDownList({ source: listParty, displayMember: 'groupName', valueMember: 'partyId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#ProductCurrencyUomId1").jqxDropDownList({ source: listCurrencyUoms, displayMember: 'description', valueMember: 'uomId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#quantityUomId1").jqxDropDownList({ source: listPackingUoms, displayMember: 'description', valueMember: 'uomId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#ProductId1").jqxDropDownList({ source: listProduct, displayMember: 'productName', valueMember: 'productId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#availableFromDate1").jqxDateTimeInput({width: '200px', theme: "olbius"});
	$("#AvailableThruDate1").jqxDateTimeInput({width: '200px', theme: "olbius"});
	$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	$('#availableFromDate1').on('valueChanged', function (event){  
		var jsDate = event.args.date; 
		$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', jsDate);
	});
	
	$("#ProductId1").jqxDropDownList("val", "${productId?if_exists}");
	$("#lastPrice1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 6, min: 0 });
	$("#shippingPrice1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 6, min: 0 });
	$("#minimumOrderQuantity1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 0, min: 0 });
	$("#alterpopupWindow").jqxWindow({ theme:'olbius',
        width: 1150, maxWidth: 1000, height: 430, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
	$('#alterpopupWindow').on('close', function () {
		$('#alterpopupWindow').jqxValidator('hide');
		$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	});
	$('#alterpopupWindow').jqxValidator({
        rules: [
                { input: '#ProductSupplier1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductSupplier1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#ProductId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#supplierProductId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#supplierProductName1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#shippingPrice1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#shippingPrice1").val();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#lastPrice1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#lastPrice1").val();
                		if (value > 0) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#ProductCurrencyUomId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductCurrencyUomId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#quantityUomId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#quantityUomId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#availableFromDate1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#availableFromDate1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                }
               ]
    });
    $("#alterSave").click(function () {
    	var row;
    	var tempFrDate = $('#availableFromDate1').val().toMilliseconds();
        var tempThrDate = $('#AvailableThruDate1').val().toMilliseconds();
        var partyId = $('#ProductSupplier1').val();
        var currencyUomId = $('#ProductCurrencyUomId1').val();
        var lastPrice = $('#lastPrice1').val();
        var supplierProductName = $('#supplierProductName1').val();
        var shippingPrice = $('#shippingPrice1').val();
        var supplierProductId = $('#supplierProductId1').val();
        var productId = $("#ProductId1").val();
        var quantityUomId = $("#quantityUomId1").val();
        if ($('#alterpopupWindow').jqxValidator('validate')) {
        	row = {
            		productId: productId,
            		comments: $('#comments1').val(),
            		currencyUomId: currencyUomId,
            		quantityUomId: quantityUomId,
            		lastPrice: lastPrice,
            		minimumOrderQuantity: $('#minimumOrderQuantity1').val(),
            		partyId: partyId,
            		shippingPrice: shippingPrice,
            		supplierProductId: supplierProductId,
            		supplierProductName: supplierProductName,
            		availableFromDate: tempFrDate,
            		availableThruDate: tempThrDate,
            	  };
    	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
            $("#jqxgrid").jqxGrid('clearSelection');
            $("#jqxgrid").jqxGrid('selectRow', 0);
            $("#alterpopupWindow").jqxWindow('close');
		}
    });
	
    $('#ProductSupplier1').on('change', function (event) {
    	var args = event.args;
        if (args) {
	        var index = args.index;
	        var item = args.item;
	        var label = item.label;
	        var value = item.value;
	        var productId = $("#ProductId1").val();
	        checkHasDataSupplierProuductAjax(value, productId);
        }
    });
    $('#ProductId1').on('change', function (event) {
    	var args = event.args;
        if (args) {
	        var index = args.index;
	        var item = args.item;
	        var label = item.label;
	        var value = item.value;
	        var partyId = $("#ProductSupplier1").val();
	        checkHasDataSupplierProuductAjax(partyId, value);
        }
    });
	function checkHasDataSupplierProuductAjax(partyId, productId) {
		if (!partyId || !productId) {
			return;
		}
		var hasData = false;
		$.ajax({
	  		  url: "checkHasDataSupplierProuductAjax",
	  		  type: "POST",
	  		  data: {partyId: partyId, productId: productId},
	  		  dataType: "json",
	  		  success: function(res) {
	  			hasData = res["hasData"];
	  		  }
		  	}).done(function() {
		  		if (hasData) {
					$("#jqxNotificationStatus").jqxNotification({ template: 'error'});
					$("#notificationContentStatus").text("${StringUtil.wrapString(uiLabelMap.HasDataSupplierProuduct)}");
		          	$("#jqxNotificationStatus").jqxNotification("open");
				}else {
					$('#jqxNotificationStatus').jqxNotification('closeLast');
				}
		  	});
	}
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
<style>
	.span12 {
		  min-height: 40px!important;
	}
	.span2 {
		margin-left: 10px!important;
	}
	input {
		width: 190px;
		height: 20px;
		padding-top: 3px;
		padding-bottom: 3px;
		padding-left: 4px;
		padding-right: 4px;
		margin-bottom: 5px;
		margin-top: 5px;
	}
	label {
		margin-bottom: 10px;
		margin-top: 10px;
		height: 20px;
		padding-top: 0px;
		padding-bottom: 0px;
	}
</style>
		<#else>   
				<h2> You do not have permission</h2>
</#if>