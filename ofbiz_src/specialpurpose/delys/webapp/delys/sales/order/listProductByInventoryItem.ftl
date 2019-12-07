<style type="text/css">
	#horizontalScrollBarjqxgridListProduct{
	  	visibility: inherit !important;
	}
	#contentjqxgridListProduct > div > div{
		margin-left:0!important;
	}
</style>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script  type="text/javascript">
	var orderId = '${parameters.orderId}';
	var deliveryId = '${parameters.deliveryId}';
	
	<#assign productList = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false) />
	var productData = 
	[
		<#list productList as product>
		{
			'productId': "${product.productId}",
			'description':  "${StringUtil.wrapString(product.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	var mapProductData = {
		<#list productList as product>
			"${product.productId}": "${StringUtil.wrapString(product.get('description', locale)?if_exists)}",
		</#list>
	};
	
	function getDescriptionByProductId(productId) {
		for ( var x in mapProductData) {
			if (productId == mapProductData[x].productId) {
				return mapProductData[x].description;
			}
		}
	}
	
	
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = 
	[
		<#list facilityList as facility>
		{
			'facilityId': "${facility.facilityId}",
			'facilityName':  "${StringUtil.wrapString(facility.get('facilityName', locale)?if_exists)}"
		},
		</#list>
	];
	
	
	var mapFacilityData = {
		<#list facilityList as facility>
			"${facility.facilityId}": "${StringUtil.wrapString(facility.get('facilityName', locale)?if_exists)}",
		</#list>
	};
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in facilityData) {
			if (facilityId == facilityData[x].facilityId) {
				return facilityData[x].facilityName;
			}
		}
	}
	
	
    <#assign uomList = delegator.findList("Uom", null, null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
	
	<#assign uomListByUomTypeId = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var uomDataByUomTypeId = 
	[
		<#list uomListByUomTypeId as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
    ];
	
	var mapUomData = {
		<#list uomListByUomTypeId as uom>
			"${uom.uomId}": "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}",
		</#list>
	};
	
</script>	
<style>
	div#buttonHidden{
	    visibility: hidden;
	}
</style>
<div>
	<div id="contentNotificationUpdateProductByInventoryItem" style="width:100%">
	</div>
	<#assign dataField="[
		{ name: 'productId', type: 'string'},
		{ name: 'inventoryItemId', type: 'string'},
		{ name: 'expireDate', type: 'date', other: 'Timestamp'},
		{ name: 'quantityAccepted', type: 'string'},
		{ name: 'uomId', type: 'string'},
		{ name: 'orderId', type: 'string'},
		{ name: 'receiptId', type: 'string'},
		{ name: 'facilityId', type: 'string'},
		{ name: 'inventoryItemDetailSeqId', type: 'string'},
	]"/>
	<#assign columnlist="
		{ text: '${StringUtil.wrapString(uiLabelMap.accProductName)}', datafield: 'productId', editable:true, columntype: 'template',
		    cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	    		if(value != ''){
	    			return  '<span>' + mapProductData[value] + '</span>';
	    		}
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ExpirationDate)}', datafield: 'expireDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:true, columntype: 'datetimeinput', cellsalign: 'right'},
		{ text: '${StringUtil.wrapString(uiLabelMap.quantity)}', datafield: 'quantityAccepted', editable:false, cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
				if(value){
					return '<span style=\"text-align:right\">' + value.toLocaleString('${locale}') + '</span>';
				}
		    }, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'uomId', editable:false, columntype: 'template', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
				if(value){
					return '<span style=\"text-align:right\">' +  mapUomData[value] + '</span>';
				}
		    },  
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.DAInventory)}', datafield: 'facilityId', editable:false, columntype: 'template', cellsalign: 'right', 
			cellsrenderer: function(row, colum, value){
				if(value){
					return '<span style=\"text-align:right\">' +  mapFacilityData[value] + '</span>';
				}
		    },  
		},
	"/>
	<@jqGrid filtersimplemode="true" id="jqxgridInventoryItem" filterable="true"  dataField=dataField columnlist=columnlist clearfilteringbutton="true" editable = "true" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQXGetProductListInInventoryItem&orderId=${parameters.orderId}"  editmode="click" pageable="true"
		customcontrol1="icon-plus-sign open-sans@${uiLabelMap.AddNewProductIdForLocation}@javascript:updateProductByInventoryItem()"	usecurrencyfunction="true"	
	/>
</div>


<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.LogAddProductWidthActuallyImported}</div>
	<div>
		<div id="contentNotificationUpdateProductByInventoryItemExists" class="popup-notification">
		</div>
		<div id="contentNotificationAddProductByInventoryItem" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.accProductName)}</label>
			</div>  
			<div class="span7">
				<div id="productId">
					<div id="jqxgridListProduct">
		            </div>
				</div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ExpirationDate)}</label>
			</div>  
			<div class="span7">
				<div id="expireDate"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.quantity)}</label>
			</div>  
			<div class="span7">
				<div id="quantityOnHandTotal"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.QuantityUomId)}</label>
			</div>  
			<div class="span7">
				<div id="quantityUomId"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.DAInventory)}</label>
			</div>  
			<div class="span7">
				<div id="facilityId"></div>
			</div>
		</div>
	   	<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationUpdateProductByInventoryItem" >
	<div id="notificationContentUpdateProductByInventoryItem">
	</div>
</div>

<div id="jqxNotificationUpdateProductByInventoryItemErrorExists" >
	<div id="notificationContentUpdateProductByInventoryItemErrorExists">
	</div>
</div>

<div id="jqxNotificationAddProductByInventoryItem" > 
	<div id="notificationContentAddProductByInventoryItem">
	</div>
</div>

 
<script>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var check = 0;
	$("#jqxNotificationUpdateProductByInventoryItem").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateProductByInventoryItem", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationUpdateProductByInventoryItemErrorExists").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateProductByInventoryItemExists", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationAddProductByInventoryItem").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddProductByInventoryItem", opacity: 0.9, autoClose: true, template: "success" });
	
	$( document ).ready(function() {
		loadFacilityIdByPartyId();
	});
	$("#alterpopupWindow").jqxWindow({
        width: 550, resizable: false, height: 320 ,isModal: true, autoOpen: false, modalOpacity: 0.7, cancelButton: $("#alterCancel"), theme:'olbius'           
    });
    $("#expireDate").jqxDateTimeInput({width: '210', clearString: 'Clear'});
    $("#quantityUomId").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}', disabled: true, width: '210'})
    $("#quantityOnHandTotal").jqxNumberInput({spinButtons: true , spinMode: 'simple',  min:0, decimalDigits: 0, width:'210'});
// $("#alterSave").jqxButton();
// $("#saveAndContinue").jqxButton({height: 30 });
    $("#productId").jqxDropDownButton({width: 210});
// $("#alterCancel").jqxButton();
     
    $('#alterpopupWindow').jqxValidator({
        rules: [
	               { input: '#productId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var productId = $('#productId').val();
		            	    if(productId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } , 
	               { input: '#quantityUomId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var uomId = $('#quantityUomId').val();
		            	    if(uomId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               },
	               { input: '#expireDate', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var expireDate = $('#expireDate').val();
		            	    if(expireDate == "" && check == 1){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               },
	               { input: '#quantityOnHandTotal', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var quantityOnHandTotal = $('#quantityOnHandTotal').val();
		            	    if(quantityOnHandTotal == '' && check == 1){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } ,
	               { input: '#facilityId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var facilityId = $('#facilityId').val();
		            	    if(facilityId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               },
	           ]
    });
	
    $('#alterpopupWindow').on('open', function (event) { 
    	listProductByProductId();
    	if(alterpopupWindowOpen == 0){
    		check = 0;
    		$('#alterpopupWindow').jqxValidator('hide');
        	$('#expireDate ').jqxDateTimeInput('setDate', null);
        	$("#quantityUomId").jqxDropDownList('clearSelection'); 
        	$("#quantityUomId").jqxDropDownList('clearSelection'); 
        	$("#quantityUomId").jqxDropDownList({disabled: true}); 
        	$("#facilityId").jqxDropDownList('clearSelection'); 
        	$('#jqxgridListProduct').jqxGrid('clearSelection');
        	$('#productId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
        	$('#quantityOnHandTotal').jqxNumberInput('clear');
    	}else{
    		$("#quantityUomId").jqxDropDownList({disabled: false}); 
    	}
    	document.getElementById("alterSave").disabled = false;
		document.getElementById("saveAndContinue").disabled = false;
    }); 
    $('#alterpopupWindow').on('close', function (event) { 
    	$('#alterpopupWindow').jqxValidator('hide');
    }); 
    
    function listProductByProductId(){
    	var listProduct;
    	$.ajax({
			url: "loadListProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listProduct = data["listProduct"];
			bindingDataToJqxGirdProductList(listProduct);
		});
    }
    
    function bindingDataToJqxGirdProductList(listProduct){
 	    var sourceP2 =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'productName', type: 'string'},
         				],
 	        localdata: listProduct,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridListProduct").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.DAProductId}', datafield: 'productId'},
 	          			{text: '${uiLabelMap.DAProductName}', datafield: 'productName'},
 	        		]
 	    });
    }
    
    var productIdBySelectGird;
    $("#jqxgridListProduct").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridListProduct").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByProductId(row['productId']) +'</div>';
        $('#productId').jqxDropDownButton('setContent', dropDownContent);
        productIdBySelectGird = row['productId'];
    });
    
	function loadFacilityIdByPartyId(){
		var listFacility; 
		$.ajax({
			url: "loadFacilityIdByPartyId",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listFacility = data["listFacility"];
			$("#facilityId").jqxDropDownList({source: listFacility, placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'facilityName', valueMember: 'facilityId', autoDropDownHeight: true ,width: '210'})
		});
	}
	
	function checkProductInInventoryItemExists(productId, expireDate){
		$.ajax({
			url: "checkProductInInventoryItemExists",
			type: "POST",
			data: {productId: productId, expireDate: convertDate(expireDate), facilityId: facilityId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			if(data["value"] == "notExits"){
				$("#quantityUomId").jqxDropDownList({ disabled: false }); 
				$("#quantityOnHandTotal").jqxNumberInput({ disabled: false, width: '210'}); 
				$('#alterSave').jqxButton({disabled: false });	
				$('#saveAndContinue').jqxButton({disabled: false });	
			}
			if(data["value"] == "exits"){
				$("#notificationContentUpdateProductByInventoryItemErrorExists").text('${StringUtil.wrapString(uiLabelMap.LogUpdateProductByInventoryItemExits)}');
		     	$("#jqxNotificationUpdateProductByInventoryItemErrorExists").jqxNotification('open');
				$("#quantityUomId").jqxDropDownList({ disabled: true}); 
				$("#quantityOnHandTotal").jqxNumberInput({ disabled: true, width: '210'}); 
				$('#alterSave').jqxButton({disabled: true});	
				$('#saveAndContinue').jqxButton({disabled: true });	
			}
		});	
	}
	
	
	$('#productId').on('close', function () {
		if(productIdBySelectGird != undefined){
			loadUomIdData(productIdBySelectGird);
		}
	}); 
	
	function loadUomIdData(productIdBySelectGird){
		var uomIdListData = [];
		$.ajax({
			url: "loadUomIdByProductId",
			type: "POST",
			data: {},
			dataType: "json",
			async: false,
			success: function(data) {
				var list = data["mapProductWithUom"];
				for(var key in list){
					if(key == productIdBySelectGird){
						uomIdListData = list[key];
					}
				}
			}
		}).done(function(data) {
		});
		bindingDataUomIdToDropDownList(uomIdListData);
	}
	
	function bindingDataUomIdToDropDownList(uomIdListData){
		$("#quantityUomId").jqxDropDownList('clearSelection'); 
		var uomIdDataList = [];
		for(var x in uomIdListData){
			var uomIdObject = {
				uomId: uomIdListData[x],
				description: getDescriptionByUomId(uomIdListData[x])
			};
			uomIdDataList.push(uomIdObject);
		}
		$("#quantityUomId").jqxDropDownList({source: uomIdDataList, autoDropDownHeight: true, disabled: false, placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'uomId', width: '210'})
	}
	
	function notifiBootBox(productId, expireDate, quantity, uomId, facilityId){
	}
	
	var checkSaveAndSound;
	$("#alterSave").click(function (){
		var productId = $('#productId').val();
		var expireDate = $('#expireDate').val();
		var quantity = $('#quantityOnHandTotal').val();
		var facilityId = $('#facilityId').val();
		var uomId = $('#quantityUomId').val();
		check = 1;
		checkSaveAndSound = 0;
		
		var errorMessage = '<div class="row-fluid margin-bottom8 padding-top8">'+
								'<label style="font-style: italic; font-size:20px; color: red;">${StringUtil.wrapString(uiLabelMap.LogNotificationImportProductByNPP)}</label>'+
						   	'</div>'+
								'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.accProductName)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+productId+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.ExpirationDate)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+expireDate+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.quantity)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+quantity+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.QuantityUomId)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+getDescriptionByUomId(uomId)+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.DAInventory)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+getDescriptionByFacilityId(facilityId)+
								'</div>'+
							'</div>';
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			$('#alterpopupWindow').jqxWindow('close');
			bootbox.confirm(errorMessage, function(result) {
	            if(result) {
	            	addNewProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId);
	            }else{
	            	editProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId);
	            }
			});
		}
	});
	
	function editProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId){
		alterpopupWindowOpen = 1;
		check = 0;
		$('#productId').jqxDropDownButton('setContent', productId); 
		$('#expireDate').val(new Date(expireDate));
		$('#quantityOnHandTotal').jqxNumberInput('val', quantity);
		$("#quantityUomId").jqxDropDownList('setContent', getDescriptionByUomId(uomId)); 
		$("#quantityUomId").jqxDropDownList('val', uomId);
		$("#facilityId").jqxDropDownList('setContent', getDescriptionByFacilityId(facilityId)); 
		$("#facilityId").jqxDropDownList('val', facilityId);
		$('#alterpopupWindow').jqxWindow('open');
	}
	
	$("#saveAndContinue").click(function (){
		check = 1;
		checkSaveAndSound = 1;
		var productId = $('#productId').val();
		var expireDate = $('#expireDate').val();
		var quantity = $('#quantityOnHandTotal').val();
		var uomId = $('#quantityUomId').val();
		var facilityId = $('#facilityId').val();
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		
		var errorMessage = '<div class="row-fluid margin-bottom8 padding-top8">'+
								'<label style="font-style: italic; font-size:20px; color: red;">${StringUtil.wrapString(uiLabelMap.LogNotificationImportProductByNPP)}</label>'+
						   	'</div>'+
								'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.accProductName)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+productId+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.ExpirationDate)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+expireDate+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.quantity)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+quantity+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.QuantityUomId)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+getDescriptionByUomId(uomId)+
								'</div>'+
							'</div>'+
							'<div class="row-fluid margin-bottom8 padding-top8">' +
							   	'<div class="span5 text-algin-right">'+
									'<label>${StringUtil.wrapString(uiLabelMap.DAInventory)}:</label>'+
								'</div>'+
								'<div class="span7">'
									+getDescriptionByFacilityId(facilityId)+
								'</div>'+
							'</div>';
		if(validate != false){
			$('#alterpopupWindow').jqxWindow('close');
			bootbox.confirm(errorMessage, function(result) {
	            if(result) {
	            	addNewProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId);
	            }else{
	            	editProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId);
	            }
			});
		}
	});
	
	$("#alterCancel").click(function (){
		$('#alterpopupWindow').jqxWindow('close');
	});
	
	
	
	function addNewProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId){
		var expireDateConvert = convertDate(expireDate);
		$.ajax({
			url: "addNewProductByInventoryItem",
			type: "POST",
			data: {orderId: orderId, deliveryId: deliveryId, productId: productIdBySelectGird, expireDate: expireDateConvert, quantity: quantity, uomId: uomId, facilityId: facilityId},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			if(data["value"] == "success"){
				if(checkSaveAndSound == 0){
					$('#jqxgridInventoryItem').jqxGrid('updatebounddata');
					$("#notificationContentUpdateProductByInventoryItem").text('${StringUtil.wrapString(uiLabelMap.NotifiAddSucess)}');
			     	$("#jqxNotificationUpdateProductByInventoryItem").jqxNotification('open');
			     	$('#alterpopupWindow').jqxWindow('close');
				}
		     	if(checkSaveAndSound == 1){
		     		editProductByInventoryItem(productId, expireDate, quantity, uomId, facilityId);
		     		$("#notificationContentAddProductByInventoryItem").text('${StringUtil.wrapString(uiLabelMap.NotifiAddSucess)}');
			     	$("#jqxNotificationAddProductByInventoryItem").jqxNotification('open');
		     	} 
			}
			if(data["value"] == "exits"){
				$("#notificationContentUpdateProductByInventoryItemErrorExists").text('${StringUtil.wrapString(uiLabelMap.checkInventoryItemExits)}');
		     	$("#jqxNotificationUpdateProductByInventoryItemErrorExists").jqxNotification('open');
			}
		});
		
		check = 0;
		$('#jqxgridInventoryItem').jqxGrid('updatebounddata');
		$('#alterpopupWindow').jqxValidator('hide');
    	$('#expireDate ').jqxDateTimeInput('setDate', null);
    	$("#quantityUomId").jqxDropDownList('clearSelection'); 
    	$("#facilityId").jqxDropDownList('clearSelection'); 
    	$('#quantityOnHandTotal').jqxNumberInput('clear');
    	$('#productId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
    	$('#jqxgridListProduct').jqxGrid('clearSelection');
    	document.getElementById("alterSave").disabled = false;
		document.getElementById("saveAndContinue").disabled = false;
	}
	
	var alterpopupWindowOpen = 0;
	function updateProductByInventoryItem(){
		alterpopupWindowOpen = 0;
		$('#alterpopupWindow').jqxValidator('hide');
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	function convertDate(date){
	     if (date == "" || date == null) {
	      return "";
	     }
	     var splDate = date.split('/');
	     if (splDate[2] != null) {
	      var d = new Date(splDate[2], splDate[1] - 1, splDate[0]);
	      return d.getTime();
	     }
	     date = new Date(date);
	     return date.getTime();
	}
	
</script>
