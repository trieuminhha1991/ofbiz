<#include "component://widget/templates/jqwLocalization.ftl" />
<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.custom.pos.css" type="text/css" />
<style>
	#showCartJQ .jqx-widget-header.jqx-grid-header {
		height: 31px !important; 
	}
	#showCartJQ .jqx-grid-pager {
    	display: none;
    }
    body{overflow: hidden;}
	#verticalScrollBarjqxgrid {z-index:9999;} 
	.green {
	    color: black\9;
	    background-color: green\9;
	}
	.green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
	    color: #333!important;
	    background-color: #b4ff55;
	}
	#showCartJqxgrid {
		border-left: none;
		border-top: none; 
	}
</style>
<script>
var cellclassPromo = function (row, columnfield, value) {
	var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
	if (data.isPromo == 'Y'){
		return 'green';
	}
}
<#assign productUomShowList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) !>
var pusData = new Array();
<#if productUomShowList?exists>
	<#list productUomShowList as itemUomShow >
		var row = {};
		row['quantityUomId'] = '${itemUomShow.uomId?if_exists}';
		row['description'] = '${itemUomShow.description?if_exists}';
		pusData[${itemUomShow_index}] = row;
	</#list>
</#if>
</script>
<input type="hidden" id="selectedItem" name="selectedItem" value="${selectedItem?default(0)}"/>
<input type="hidden" id="cartSize" name="cartSize" value="${shoppingCartSize?default(0)}"/>
<div class="row-fluid">
	<div id="showCartJQ">
		<div id="showCartJqxgrid" style="width: 100%">
		</div>
	</div>
</div>
<div id="popupWindow" style="display: none;">
	<div>${uiLabelMap.BPOSAddColumns}</div>
	<div style="overflow: hidden;">
	    <div style="float: left;" id="jqxlistbox"></div>
	</div>
</div>
<div id='Menu' style="display: none;">
	<ul>
	    <li>${uiLabelMap.BPOSAddColumns}</li>
	</ul>
</div>
<script type="text/javascript">
var sourceItem ={};
$(document).ready(function () {
	var renderUom = function(row, colum, value){
 		for(var i = 0; i < pusData.length; i++){
 			if(value == pusData[i].quantityUomId){
 				return "<div class='jqx-align-center'>" + pusData[i].description + "</div>";
 			}
 		}
 		return "<div class='jqx-align-center'>" + value + "</div>";
 	}
	
	var columnsrenderer = function (value) {
		return '<div style="text-align: center; margin-top: 10px;">' + '<b>' + value + '</b>' + '</div>';
	}
	
	var data = getCartInfo();
	sourceItem= {
			localdata: data,
			dataType: "array",
			datafields:[
			   {name: 'count', type: 'number'},
			   {name: 'productId', type: 'string'},
			   {name: 'productCode', type: 'string'},
			   {name: 'internalName', type: 'string'},
			   {name: 'productName', type: 'string'},
			   {name: 'quanATP', type:'number'},
			   {name: 'uomId', type:'string'},
			   {name: 'uomList', type:'array'},
			   {name: 'quantityProduct', type:'string'},
			   {name: 'price', type:'number'},
			   {name: 'amount', type:'number'},
			   {name: 'barcode', type:'string'},
			   {name: 'currency', type:'string'},
			   {name: 'discount', type:'number'},
			   {name: 'cartLineIndex', type:'number'},
			   {name: 'discountPercent', type:'number'},
			   {name: 'subTotal', type:'number'},
			   {name: 'isPromo', type:'string'},
			   {name: 'largeImageUrl', type:'string'}
			   ]
		};
	
	var dataAdapterItem = new $.jqx.dataAdapter(sourceItem);
	jQuery("#showCartJqxgrid").jqxGrid({
		width: '100%',
		height: 430,
		source: dataAdapterItem,
		rowsheight: 60,
		pageable: true,
        sortable: false,
        altrows: true,
        enabletooltips: true,
        editable: true,
        columnsresize: false,
        columnsreorder: false,
        autorowheight: true,
        pagesize: 50,
        enablekeyboarddelete: false,
        localization: getLocalization(),
        pagesizeoptions: ['50', '100'],
        selectionmode: 'singlerow',
        editmode: 'dblclick',
        localization: getLocalization('${locale}'),
        handlekeyboardnavigation: function (event) {
            var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
            if (key == 46){
            	event.preventDefault();
    			return false;
            }
        },
        columns:[
     			 {text: '#', datafield:'count', editable: false, cellsalign: 'center', renderer: columnsrenderer, pinned: true, width:'2%', cellclassname: cellclassPromo},
     			 {text: '${StringUtil.wrapString(uiLabelMap.BPOSId)}', datafield:'productCode', editable: false, cellsalign: 'center', renderer: columnsrenderer, width:'15%', cellclassname: cellclassPromo},
     			 {text: '${StringUtil.wrapString(uiLabelMap.BPOSProduct)}', datafield:'productName', editable: false, cellsalign: 'left', renderer: columnsrenderer, cellclassname: cellclassPromo,
     				cellsrenderer: function (row, column, value) {
      					var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
      					if (data && data.productName){
      						var productName = data.productName;
      						if (productName.length > 80){
      							productName = productName.substring(0,80);
          						productName = productName + '...';
      						}	
      						if (productName.length > 40){
      							return "<div class='jqx-align-left1'>" + productName + "</div>";
  							} else {
  								return "<div class='jqx-align-left'>" + productName + "</div>";
  							}
      					}
      				}	 
     			 },
     			 {text: '${StringUtil.wrapString(uiLabelMap.BPOSBarcodeProduct)}', datafield:'barcode', editable: false, cellsalign: 'center', renderer: columnsrenderer, width:'14%', cellclassname: cellclassPromo},
     			 {text: '${StringUtil.wrapString(uiLabelMap.BPOSQuantityOnHand)}', datafield:'quanATP', editable: false, cellsalign: 'right', renderer: columnsrenderer, width:'7%', cellclassname: cellclassPromo,
     				cellsrenderer: function (row, column, value) {
     					var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
     					if (data && data.quanATP){
     						return "<div class='jqx-align-right'>" + data.quanATP + "</div>";
     					} else {
     						return "<div class='jqx-align-right'>" + 0 + "</div>";
     					}
     				}	 
     			 },
     			 {text: '${StringUtil.wrapString(uiLabelMap.BPOSUom)}', datafield: 'uomId', editable: false, columntype: 'dropdownlist', cellsalign: 'center', renderer: columnsrenderer, width:'8%', cellclassname: cellclassPromo,
     				cellsrenderer: renderUom,
     				initeditor: function (row, cellvalue, editor) {
                        var packingUomData = new Array();
                        var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
                        var itemSelected = data['uomId'];
                        var packingUomIdArray = data['uomList'];
                        for (var i = 0; i < packingUomIdArray.length; i++) {
     	                    var packingUomIdItem = packingUomIdArray[i];
     	                    var row = {};
     	                    row['uomId'] = '' + packingUomIdItem.quantityUomId;
     	                    row['description'] = '' + packingUomIdItem.description;
     	                    packingUomData[i] = row;
                        }
                        var sourceDataPacking =
                        {
     	                   localdata: packingUomData,
     	                   datatype: "array"
                        };
                        var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
                        editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId', selectedIndex: 0,
                     	   renderer: function (index, label, value) {
                     		    if (packingUomData){
                     		    	var datarecord = packingUomData[index];
                     		    	if (datarecord){
                     		    		return datarecord.description;
                     		    	}
                     		    }
                     		    return value;
     		                }
                        });
                        editor.jqxDropDownList('selectItem', itemSelected);
                    }
                 },
                 {text: '${StringUtil.wrapString(uiLabelMap.BPOSQu)}', datafield: 'quantityProduct', editable: false, cellsalign: 'right', renderer: columnsrenderer, width:'5%', cellclassname: cellclassPromo,
                	 cellsrenderer: function (row, column, value) {
      					var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
      					if (data && data.quantityProduct){
      						return "<div class='jqx-align-right'>" + data.quantityProduct + "</div>";
      					}
      				}	 
                 },
                 {text: '${StringUtil.wrapString(uiLabelMap.BPOSUnitPrice)}', datafield:'price', editable: false, cellsalign: 'right', renderer: columnsrenderer, width:'12%', cellclassname: cellclassPromo,
                 	cellsrenderer: function (row, column, value) {
     					var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
     					if (data && data.price){
     						var price = Math.round(data.price);
     						return "<div class='jqx-align-right'>" + formatcurrency(price, data.currency) + "</div>";
     					} else {
     						return "<div class='jqx-align-right'>" + formatcurrency(0, data.currency) + "</div>";
     					}
     				}
                 },
                 {text: '${StringUtil.wrapString(uiLabelMap.BPOSItemTotal)}', datafield:'amount', editable: false, cellsalign: 'center', renderer: columnsrenderer, width:'14%', cellclassname: cellclassPromo,
                 	cellsrenderer: function (row, column, value) {
     					var data = $('#showCartJqxgrid').jqxGrid('getrowdata', row);
     					if (data && data.amount){
     						var amount = Math.round(data.amount);
     						return "<div class='jqx-align-right'>" + formatcurrency(amount, data.currency) + "</div>";
     					} else {
     						return "<div class='jqx-align-right'>" + formatcurrency(0, data.currency) + "</div>";
     					}
     				}
                 },
                 {text: '${StringUtil.wrapString(uiLabelMap.BPOSCurrencyUom)}', datafield:'currency', editable: false, cellsalign: 'center', renderer: columnsrenderer, hidden: true, cellclassname: cellclassPromo},
                 {text: '${StringUtil.wrapString(uiLabelMap.BPOSUom)}', datafield:'uomList', editable: false, cellsalign: 'center', renderer: columnsrenderer, hidden: true, cellclassname: cellclassPromo},
             ],
        ready: function(){  
        	$('#showCartJqxgrid').jqxGrid('autoresizecolumns');
        }
	});
	
	$('#showCartJqxgrid').jqxGrid('updatebounddata');
	
	$('#showCartJqxgrid').on('rowselect', function (event) {
		var args = event.args;
		var rowBoundIndex = args.rowindex;
		var rowData = args.row;
		if (rowData) {
			if (flagPopup){
				$("#jqxProductList").jqxComboBox('clearSelection');
				$("#jqxPartyList").jqxComboBox('clearSelection');
				updateCartItemSelected(rowBoundIndex);
			}
		}
	});
	$("#showCartJqxgrid").on("bindingcomplete", function (event) {
		$('#showCartJqxgrid').jqxGrid('selectrow', 0);
	});
	// create context menu
    var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 30, autoOpenPopup: false, mode: 'popup'});
    $("#showCartJqxgrid").on('contextmenu', function () {
        return false;
    });
    var listSource = [
					  { label: '${uiLabelMap.BPOSId}', value: 'productCode', checked: true},
                      { label: '${uiLabelMap.BPOSProduct}', value: 'productName', checked: true },
                      { label: '${uiLabelMap.BPOSWarehouse}', value: 'quanATP', checked: true },
                      { label: '${uiLabelMap.BPOSUom}', value: 'uomId', checked: true },
                      { label: '${uiLabelMap.BPOSQu}', value: 'quantityProduct', checked: true},
                      { label: '${uiLabelMap.BPOSUnitPrice}', value: 'price', checked: true},
                      { label: '${uiLabelMap.BPOSItemTotal}', value: 'amount', checked: true},
                      { label: '${uiLabelMap.BPOSBarcodeProduct}', value: 'barcode', checked: false},
                      ];
    
    $("#jqxlistbox").jqxListBox({ source: listSource, width: 237, height: 160,  checkboxes: true });
    $("#popupWindow").jqxWindow({ width: 250, height: 200, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.01 });
    $("#jqxlistbox").on('checkChange', function (event) {
        $("#showCartJqxgrid").jqxGrid('beginupdate');
        if (event.args.checked) {
            $("#showCartJqxgrid").jqxGrid('showcolumn', event.args.value);
        }
        else {
            $("#showCartJqxgrid").jqxGrid('hidecolumn', event.args.value);
        }
        $("#showCartJqxgrid").jqxGrid('endupdate');
    });
    var items = $("#jqxlistbox").jqxListBox('getItems');
    for (var i = 0; i < items.length; i++){
    	if (items[i].checked == false){
    		$("#showCartJqxgrid").jqxGrid('hidecolumn', items[i].value);
    	}
    }
    
    // handle context menu clicks.
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.BPOSAddColumns)}") {
            var offset = $("#showCartJqxgrid").offset();
            $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 300, y: parseInt(offset.top) + 60} });
            // show the popup window.
            $("#popupWindow").jqxWindow('show');
        } 
    });
    
	//first load page
    var listCartItem =  $("#showCartJqxgrid").jqxGrid('getrows');
    if(listCartItem && listCartItem.length >0){
		$("#showCartJqxgrid").jqxGrid('selectrow', 0);
    }
  	productToSearchFocus();
  	$('#showCartJqxgrid').on('rowclick', function (event){
	    var args = event.args;
	    var boundIndex = args.rowindex;
	    var visibleIndex = args.visibleindex;
	    var rightclick = args.rightclick; 
	    var ev = args.originalEvent;   
  		productToSearchFocus();
	});
  	
  	//event change uom list
  	$("#showCartJqxgrid").on('cellendedit', function(event){
  	    var rowBoundIndex = event.args.rowindex;
  	    var value = args.value;
  	    var oldvalue = args.oldvalue;
  	 	var param = 'cartLineIndex=' + rowBoundIndex + '&uomToId=' + value + '&uomFromId=' + oldvalue;
  	 	if (oldvalue != value){
  	 		jQuery.ajax({url: 'UpdateUomWhenSale',
  		        data: param,
  		        type: 'post',
  		        async: false,
  		        success: function(data) {
  	          		getResultOfUpdateUom(data);
  	          	},
  	          	error: function(data) {
  	          		getResultOfUpdateUom(data);
  	          	}
  	      	});
  	      	event.preventDefault();
  	 	}
  	});
  	$("#showCartJqxgrid").on('mousedown', function (event) {
        if (event.which == 3) {
	        var scrollTop = $(window).scrollTop();
	        var scrollLeft = $(window).scrollLeft();
	        $("#Menu").jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
	        return false; 
        } else {
        	$("#Menu").jqxMenu('close');
        }
    });
  	
  	$("#showCartJqxgrid").on('keydown', function (event) {
  		if (event.keyCode == 46){
            return false;
  		}
    });
});

function getResultOfUpdateUom(data){
	var rowdata = $('#showCartJqxgrid').jqxGrid('getrowdata', $('#showCartJqxgrid').jqxGrid('getselectedrowindex'));
	// ShowCartList data
	data = data.cartItem;
	rowdata.amount = data.subTotal;
	rowdata.quanATP = data.quantityATP;
	rowdata.quantityProduct = data.quantity;
	rowdata.price = data.unitPrice;
	$('#showCartJqxgrid').jqxGrid('updaterow', rowdata.uid, rowdata);
	// Update input control
	$('#productSelectedQuantity').val(data.quantity);
	$('#productSelectedUnitPrice').maskMoney('mask', data.unitPrice);
	// Update CartHeader info
	updateCartHeader();
}

function updateCartItemSelected(lineIndex) {
    if (lineIndex == null) {
        lineIndex = 0;
    }
    selectedWebPOS = lineIndex;
    var cartItem = $('#showCartJqxgrid').jqxGrid('getrowdata', selectedWebPOS);
    if(cartItem){
		var productName = cartItem.productName;
		var productNameTmp = productName;
		if (productName.length > 40){
			productNameTmp = productName.substring(0,40);
			productNameTmp = productNameTmp + '...';
			$("#productSelectedName").jqxTooltip({ content: '<b style="font-size: 16px">' + productName + '</b>', position: 'mouse', width: 400});
		}
		var quantity = cartItem.quantityProduct;
		var unitPrice = cartItem.price;
		var discount = cartItem.discount;
		var discountPercent = cartItem.discountPercent;
		var subTotal = cartItem.subTotal;
		var quantityUomId = cartItem.quantityUomId;
		var cartLineIdx = cartItem.cartLineIndex;
		$("#productSelectedImage").attr("src", cartItem.largeImageUrl);
		$("#productSelectedName").html(productNameTmp);
		$("#productSelectedUnitPrice").maskMoney('mask', unitPrice);
		$("#productSelectedQuantity").val(quantity);
		$("#productSelectedQuantityTmp").val(quantity);
		if(discount){
			$("#productSelectedDiscountAmount").maskMoney('mask', discount);
		}else{
			$("#productSelectedDiscountAmount").maskMoney('mask', 0.0);
		}
		
		if(discountPercent){
			$("#productSelectedDiscountPercent").val(discountPercent);
		}else{
			$("#productSelectedDiscountPercent").val(0);
		}
		$("#itemSubTotal").val(subTotal);
		$("#uomToId").val(quantityUomId);
		$("#cartLineIdx").val(cartLineIdx);
		quantity = parseFloat(quantity);
		if (quantity < 0) {
			$("#productSelectedDiscountPercent").maskMoney('mask', 0.0);
			$("#productSelectedDiscountAmount").prop('disabled', true);
			$("#productSelectedDiscountPercent").prop('disabled', true);
		} else {
			$("#productSelectedDiscountAmount").prop('disabled', false);
			$("#productSelectedDiscountPercent").prop('disabled', false);
		}
		checkReturnIsAllItem();
	}else{
		resetSelectCartItem();
	}
}

function checkReturnIsAllItem() {
	var rowsShowCartJqxgrid = $('#showCartJqxgrid').jqxGrid('getboundrows');
	var isAllReturn = true;
	for ( var r in rowsShowCartJqxgrid) {
		var amount = rowsShowCartJqxgrid[r].amount;
		if (amount > 0) {
			isAllReturn = false;
			break;
		}
	}
	if (isAllReturn) {
		flagReturn = true;
		$("#discountWholeCart").maskMoney('mask', 0);
		$("#discountWholeCartPercent").val(0);
		$("#discountWholeCart").prop('disabled', true);
		$("#discountWholeCartPercent").prop('disabled', true);
	} else {
		$("#discountWholeCart").prop('disabled', false);
		$("#discountWholeCartPercent").prop('disabled', false);
	}
}	
</script>
