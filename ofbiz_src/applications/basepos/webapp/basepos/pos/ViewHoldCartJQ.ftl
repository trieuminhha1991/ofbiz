<#include "component://widget/templates/jqwLocalization.ftl" />
<style type="text/css">
#expandCollapseShowHoldCart.jqx-fill-state-pressed{
	border-color: #aaa !important;
  	background: #efefef !important;
}

.jqx-widget-content{
	font-family: 'Roboto';
}
#showHoldCartWindow .jqx-widget-header.jqx-grid-header{
	height: 56px !important; 
}
#splitterShowHoldCart .jqx-splitter-collapse-button-vertical.jqx-fill-state-pressed, #splitterShowHoldCart .jqx-splitter-collapse-button-horizontal.jqx-fill-state-pressed{
	background: #0099cc !important;
}
#loadHoldCart{
	float: right;
	margin-right: 0px;
	margin-bottom: 10px;
	margin-top: 1.5px;
}

#deleteHoldCart{
	float: right;
	margin-right: 5px;
	margin-bottom: 10px;
	margin-top: 1.5px;
}

#detailHeldCartItemSelected .jqx-widget-header.jqx-grid-header{
	height: 25px !important; 
}
</style>
<script>
<#assign productUomList = delegator.findList("Uom", null, null, null, null, false) !>
var puData = new Array();
<#if productUomList?exists>
	<#list productUomList as itemUom >
		var row = {};
		row['quantityUomId'] = '${itemUom.uomId?if_exists}';
		row['description'] = '${itemUom.description?if_exists}';
		puData[${itemUom_index}] = row;
	</#list>
</#if>
</script>
<div id="showHoldCartWindow" style="display: none;">
	<div style="background-color: #438EB9; border-color: #0077BC; color: white; font-size: 15px; font-family: 'Open-sans'">
		${uiLabelMap.BPOSListHoldCart} 
		<input type="button" value="${uiLabelMap.BPOSExpand}" id='expandCollapseShowHoldCart' />
	</div>
	    <div style="overflow: hidden;">
			<div id="splitterShowHoldCart">
		        <div>
		            <div style="border: none;" id='showHoldCartList'>
		             
		            </div>
		        </div>
		        <div>
			        <div id="nestedSpliterShowHoldCart">
			        	<div id="overviewHoldCart">
			            	<div class="span12 row-fluid no-left-margin" style="margin-left: 10px; margin-top: 10px;">
			            		<div class="span6">
			            			<div class="grey-text beauty-label" id="holdCartId" ><b> ${uiLabelMap.BPOSHoldCartId}:</b> </div>
			            			<div class="grey-text beauty-label" id="holdCartCustomerName" ><b>${uiLabelMap.BPOSCustomer}:</b> </div>
			            			<div class="grey-text beauty-label" id="holdCartTelecomnumber" ><b>${uiLabelMap.BPOSMobile}:</b></div>
			            			<div class="grey-text beauty-label" id="holdCartAddress"><b>${uiLabelMap.BPOSAddress}:</b></div>
			            		</div>
			            		<div class="span6">
			            			<div class="grey-text beauty-label" id="holdCartCreatedTime" ><b>${uiLabelMap.BPOSTime}:</b></div>
			            			<div class="grey-text beauty-label" id="holdCartTotal"><b>${uiLabelMap.BPOSHeldOrderTotal}:</b></div>
			            			<div class="grey-text beauty-label" id="holdCartDiscount"> <b>${uiLabelMap.BPOSDiscount}:</b> </div>
			            			<#if showPricesWithVatTax == "N">
			            				<div class="grey-text beauty-label" id="holdCartTax"> <b>${uiLabelMap.BPOSTotalSalesTax}:</b> </div>
			            			</#if>
			            			<div class="grey-text beauty-label" id="holdCartGrandTotal"><b>${uiLabelMap.BPOSCartTotalNotAdj}:</b></div>
			            		</div>
			            	</div>
			            </div>
			            <div id="detailHeldCartItemSelected">
			            	
			            </div>
			        </div>
		        </div>
		    </div>
	     	<div id="showHoldCart-footer">
	     		<button type="button" id='loadHoldCart' onclick="loadHoldCart()"><i class="icon-ok"></i> ${uiLabelMap.BPOSUnhold}</button>
	    	 	<button type="button" id='deleteHoldCart' onclick="deleteHoldCart()"><i class="icon-remove"></i> ${uiLabelMap.BPOSDelete}</button>
	    	</div>
    </div>
</div>
<script type="text/javascript">
var BPOSOK = "${StringUtil.wrapString(uiLabelMap.BPOSOK)}";
var BPOSConfirmRemoveHeldCart = "${StringUtil.wrapString(uiLabelMap.BPOSConfirmRemoveHeldCart)}";
$(document).ready(function () {
	var focus = 0;
	var toggled = $("#expandCollapseShowHoldCart").jqxToggleButton('toggled');
    
    $('#splitterShowHoldCart').on('expanded', function (event) {
    	toggled = false;
    	$("#expandCollapseShowHoldCart")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
    });

    $('#splitterShowHoldCart').on('collapsed', function (event) {
    	toggled = true;
    	$("#expandCollapseShowHoldCart")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
    });
	$("#expandCollapseShowHoldCart").on('click', function () {
		if (focus == 0){
			$('#splitterShowHoldCart').jqxSplitter('expand');
            $("#expandCollapseShowHoldCart")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
            focus = 1;
		} 
        if (toggled) {
        	$('#splitterShowHoldCart').jqxSplitter('expand');
            $("#expandCollapseShowHoldCart")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
        }else {
        	$('#splitterShowHoldCart').jqxSplitter('collapse');
        	$("#expandCollapseShowHoldCart")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
        } 
    });
	
	initShowHoldCartList();
	initDetailHoldCart();
	
	$('#showHoldCartWindow').on('close', function (event) { 
		flagPopup = true;
		$("#jqxProductList").jqxComboBox({ disabled: false });
		productToSearchFocus();
	});

	$('#showHoldCartWindow').on('open', function (event) {
		Loading.hide('loadingMacro');
		flagPopup = false;
		$("#jqxProductList").jqxComboBox({ disabled: true });
	});
 });
 
var sourceHoldCart =
{
   localdata: [{}],
   dataType: "json",
   datafields:[
				{name: 'id', type: 'string'},
				{name: 'createdTime', type: 'date'},
				{name: 'firstName', type: 'string'},
				{name: 'middleName', type: 'string'},
				{name: 'lastName', type:'string'},
				{name: 'grandTotalNotAdj', type:'number'},
				{name: 'discountAmount', type:'number'},
				{name: 'salesTaxAmount', type:'number'},
				{name: 'grandTotal', type:'number'},
				{name: 'currency', type:'string'},
				{name: 'address1', type:'string'},
				{name: 'city', type:'string'},   
				{name: 'phone', type:'string'},   
      		  ]
};

function initShowHoldCartList(){
	var dataAdapterHoldCart = new $.jqx.dataAdapter(sourceHoldCart);
	jQuery("#showHoldCartList").jqxGrid({
		source: dataAdapterHoldCart,
		width: '100%',
        height: '100%',
        filterable: true,
        showfilterrow : true,
        sortable: true,
		pageable: false,
        sortable: true,
        columnsresize: true,
        columnsreorder: true,
        selectionmode: 'singlerow',
        localization: getLocalization(),
        columns: [
                     { text: '${uiLabelMap.BPOSId}', datafield: 'id', width: 70 },
                     { text: '${uiLabelMap.BPOSTime}', filtertype: 'range', datafield: 'createdTime', cellsformat: 'dd/MM/yyyy HH:mm:ss', width: 200},
                     { text: '${uiLabelMap.BPOSLastName}', datafield: 'lastName'},
                     { text: '${uiLabelMap.BPOSMiddleName}', datafield: 'middleName'},
                     { text: '${uiLabelMap.BPOSFirstName}', datafield: 'firstName', width: 200 },
                     { text: '${uiLabelMap.BPOSMobile}', datafield: 'phone'},
                 ],
        ready: function(){
        	$("#showHoldCartList").jqxGrid('selectrow', 0);
        },
        handlekeyboardnavigation: function (event) {
            var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
            if (key == 46) {
            	var opened = $("#showHoldCartWindow").jqxWindow('isOpen');
            	if (opened){
            		deleteHoldCart();
            	}
				return false;
            }
        },
	});
	
	$("#showHoldCartList").on("filter", function (event){
		var getRows = $("#showHoldCartList").jqxGrid("getrows");
		if(getRows[0]){
			var firstRow = getRows[0];
			updateOverViewHoldCart(firstRow);
			updateHoldCartItems(firstRow.uid)
		}else{
			resetOverViewHoldCart();
			var holdCartItemsData = [{}];
			updateSoruceHoldCartItems(holdCartItemsData);
		}
		    	 
	}); 
 	 
	$("#showHoldCartList").on("sort", function (event){
    	var getRows = $("#showHoldCartList").jqxGrid("getrows");
		if(getRows[0]){
			var firstRow = getRows[0];
			updateOverViewHoldCart(firstRow);
			updateHoldCartItems(firstRow.uid)
		}else{
			resetOverViewHoldCart();
			var holdCartItemsData = [{}];
			updateSoruceHoldCartItems(holdCartItemsData);
		}
   	});
	
}

var sourceItemsHoldCart = 
	{
	   localdata: [{}],
	   dataType: "json",
       datafields: [
			{name: 'id', type: 'number'},
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
			{name: 'internalName', type: 'string'},
			{name: 'productName', type: 'string'},
			{name: 'uomId', type:'string'},
			{name: 'quantity', type:'number'},
			{name: 'price', type:'number'},
			{name: 'itemAdjustments', type:'number'},
			{name: 'amount', type:'number'},
			{ name: 'currencyUom', type: 'string' },
      ]
};

function initDetailHoldCart(){
	var dataAdapterItemHoldCart = new $.jqx.dataAdapter(sourceItemsHoldCart);
	jQuery("#detailHeldCartItemSelected").jqxGrid({
		source: dataAdapterItemHoldCart,
		width: '100%',
        height: 285,
        filterable: false,
        showfilterrow : false,
        sortable: false,
		pageable: false,
        sortable: false,
        columnsresize: true,
        columnsreorder: false,
        selectionmode: 'singlerow',
        localization: getLocalization(),
        columns: [		  
					{ text: '#', datafield:'id', hidden: true},
					{ text: '${uiLabelMap.BPOSId}', datafield: 'productCode', width: 130},
					{ text: '${uiLabelMap.BPOSProductName}', datafield: 'productName', width: 250},
					{ text: '${uiLabelMap.BPOSUom}', datafield: 'uomId', width:70,
					  	cellsrenderer: function (row, column, value){
				  			for(i = 0 ; i < puData.length ; i++){
				  				if(value == puData[i].quantityUomId){
				  					return "<div style='margin-left: 5px; margin-top: 5px; text-align: left;'>" + puData[i].description + "</div>";
				  				}
				  			}
				  			
				  		 }
					},
					{ text: '${uiLabelMap.BPOSQu}', datafield: 'quantity', width: 50, cellsalign: 'right' },
					{ text: '${uiLabelMap.BPOSUnitPrice}', datafield: 'price',
						cellsrenderer: function (row, column, value){
							var data = $("#detailHeldCartItemSelected").jqxGrid('getrowdata', row);
							if (data&&data.price){
								return "<div style='margin-left: 5px; margin-top: 5px; text-align: right; margin-right: 5px;'>" + formatcurrency(data.price, data.currencyUom) + "</div>";
							}
				  		}
					},
					{ text: '${uiLabelMap.BPOSDiscount}', datafield: 'itemAdjustments', width: 100,
						cellsrenderer: function (row, column, value){
							var data = $("#detailHeldCartItemSelected").jqxGrid('getrowdata', row);
							if (data&&data.itemAdjustments){
								return "<div style='margin-left: 5px; margin-top: 5px; text-align: right; margin-right: 5px;'>" + formatcurrency(data.itemAdjustments, data.currencyUom) + "</div>";
							} else {
								return "<div style='margin-left: 5px; margin-top: 5px; text-align: right; margin-right: 5px;'>" + formatcurrency(0, data.currencyUom) + "</div>";
							}
				  		}
					},
					{ text: '${uiLabelMap.BPOSItemTotal}', datafield: 'amount',
						cellsrenderer: function (row, column, value){
							var data = $("#detailHeldCartItemSelected").jqxGrid('getrowdata', row);
							if (data&&data.amount){
								return "<div style='margin-left: 5px; margin-top: 5px; text-align: right; margin-right: 5px;'>" + formatcurrency(data.amount, data.currencyUom) + "</div>";
							}
				  		}
					},
                 ]
	});
}

function viewHoldCart() {
	Loading.show('loadingMacro');
	holdCart();
	$('#showHoldCartWindow').jqxWindow('open');
	$("#jqxProductList").jqxComboBox({ disabled: true });
	getListHoldCart();
	var allData = $("#showHoldCartList").jqxGrid('getrows');
	if (allData.length == 0){
		$('#deleteHoldCart').jqxButton({
	    	disabled: true
	    });
		$('#loadHoldCart').jqxButton({
		    disabled: true
		});
		resetOverViewHoldCart();
		var holdCartItemsData = [{}];
		updateSoruceHoldCartItems(holdCartItemsData);
	} else {
		$('#deleteHoldCart').jqxButton({
		    disabled: false
		});
		$('#loadHoldCart').jqxButton({
		    disabled: false
		});
	} 
	$('#showHoldCartList').on('rowselect', function (event) {
        var args = event.args;
        var row = event.args.row;
        if (allData.length != 0){
        	updateOverViewHoldCart(row);
        	updateHoldCartItems(row.uid)
        } else {
        	var holdCartItemsData = [{}];
			updateSoruceHoldCartItems(holdCartItemsData);
			resetOverViewHoldCart();
        }
    }); 
	$("#showHoldCartList").jqxGrid('selectrow', 0);
	$("#showHoldCartList").jqxGrid('focus');
	$('#showHoldCartWindow').jqxWindow('focus');
}

function getListHoldCart(){
	$.ajax({url: 'GetListHoldCart',
  	        type: 'post',
  	        async: false,
  	        success: function(data) {
  	            getResultOfGetListHoldCart(data);
  	        },
  	        error: function(data) {
  	        	getResultOfGetListHoldCart(data);
  	        }
	});
}
function getResultOfGetListHoldCart(data){
	var serverError = getServerError(data);
  	if (serverError != "") {
  		bootbox.alert(serverError);
  	} else {
    	sourceHoldCart.localdata = data.listHoldCarts;
    	var dataAdapterHoldCart = new $.jqx.dataAdapter(sourceHoldCart);
    	$("#showHoldCartList").jqxGrid({source: dataAdapterHoldCart});
    	$("#showHoldCartList").jqxGrid('updatebounddata');
		$('#showHoldCartWindow').jqxWindow('focus');
	}
}

function updateHoldCartItems(index){
	var param = "index=" + index;
	$.ajax({url: 'GetHoldCartItems',
        data: param,
        type: 'post',
        async: false,
        success: function(data) {
            getResultOfUpdateHoldCartItems(data);
        },
        error: function(data) {
        	getResultOfUpdateHoldCartItems(data);
        }
    });
}

function getResultOfUpdateHoldCartItems(data){
	var serverError = getServerError(data);
    if (serverError != "") {
        bootbox.alert(serverError);
    } else {
    	updateSoruceHoldCartItems(data)
	}
}

function updateSoruceHoldCartItems(data){
	sourceItemsHoldCart.localdata = data.listHoldCartItems;
    var dataAdapterItemsHoldCart = new $.jqx.dataAdapter(sourceItemsHoldCart);
    $("#detailHeldCartItemSelected").jqxGrid({source: dataAdapterItemsHoldCart});
}

function updateOverViewHoldCart(row){
	var date = new Date(row.createdTime);
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var hour = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var fullDate = day + '/' + month + '/' + year + ' ' + hour + ':' + minutes + ':' + seconds;
	var firstName = '';
	if (row.firstName){
		firstName = row.firstName;
	}
	var lastName = '';
	if (row.lastName){
		lastName = row.lastName;
	}
	var fullName = '';
	if (row.middleName){
		fullName = lastName + ' ' + row.middleName + ' ' + firstName;
	} else {
		fullName = lastName + ' ' + firstName
	}
	
	var phone = '';
	if (row.phone){
		phone = row.phone;
	}
	$('#holdCartId').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHoldCartId)}: </b> " + row.id);
	$('#holdCartCustomerName').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}: </b>" + fullName);
	$('#holdCartTelecomnumber').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSMobile)}: </b>" + phone);
	$('#holdCartAddress').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSAddress)}: </b>" + row.address1 + " " + row.city);
	$('#holdCartCreatedTime').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>" + fullDate);
	$('#holdCartTotal').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>" + formatcurrency(row.grandTotalNotAdj, row.currency) );
	$('#holdCartDiscount').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>" + formatcurrency(row.discountAmount, row.currency));
	<#if showPricesWithVatTax == "N">
		$('#holdCartTax').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTotalSalesTax)}: </b>" + formatcurrency(row.salesTaxAmount, row.currency));
	</#if>
	$('#holdCartGrandTotal').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b>" + formatcurrency(row.grandTotal, row.currency));
}

function resetOverViewHoldCart(){
	$('#holdCartId').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHoldCartId)}: </b>");
	$('#holdCartCustomerName').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}: </b>");
	$('#holdCartTelecomnumber').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSMobile)}: </b>");
	$('#holdCartAddress').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSAddress)}: </b>");
	$('#holdCartCreatedTime').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>");
	$('#holdCartTotal').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>");
	$('#holdCartDiscount').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>" );
	$('#holdCartTax').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTotalSalesTax)}: </b>" );
	$('#holdCartGrandTotal').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b>");
}

function deleteHoldCart(){
	var id = $("#showHoldCartList").jqxGrid('getselectedrowindex');
	var data = $("#showHoldCartList").jqxGrid('getrowdata', id);
	if(data){
		confirmRemoveHeldCart(data.id);
	}
}

function loadHoldCart(){
	var id = $("#showHoldCartList").jqxGrid('getselectedrowindex');
	var data = $("#showHoldCartList").jqxGrid('getrowdata', id);
	if (data){
		loadCart(data.id);
	}
}
</script>
<script type="text/javascript">
	$.jqx.theme = 'basic';  
	theme = $.jqx.theme;
	
	$("#showHoldCartWindow").jqxWindow({
	    width: "90%", maxWidth: "90%" , minHeight: "80%", resizable: false, draggable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	});
	
	$("#splitterShowHoldCart").jqxSplitter({ width: "100%", height: "95%", panels: [{size: "30%", min: "30%", collapsible: false}, { size: "60%" }] });
	$("#nestedSpliterShowHoldCart").jqxSplitter({ width: "100%",height: "100%",  orientation: 'horizontal', panels: [{ size: 150 }] });
	$("#loadHoldCart").jqxButton({width: 150, template: 'primary'});
	$("#deleteHoldCart").jqxButton({width: 70, template: 'danger'});
	$("#expandCollapseShowHoldCart").jqxToggleButton({toggled: true});
</script>