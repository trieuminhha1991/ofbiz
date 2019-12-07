<#assign getAlGlAccount="getAll"/>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label>${uiLabelMap.quantity}</label>
					</div>  
					<div class="span7">
						<div id="quantityAdd"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.invoiceItemTypeId}</label>
					</div>  
					<div class="span7">
						<div id="invoiceItemTypeIdAdd"></div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label>${uiLabelMap.accProductName}</label>
					</div>  
					<div class="span7">
						<div id="productIdAdd">
							<div id="jqxProductGrid"></div>
						</div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label>${uiLabelMap.accOverrideGlAccountId}</label>
					</div>  
					<div class="span7">
						<div id="overrideGlAccountIdAdd">
							<div id="jqxgridGlAccount"></div>
						</div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.unitPrice}</label>
					</div>  
					<div class="span7">
						<div id="amountAdd"></div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label>${uiLabelMap.description}</label>
					</div>  
					<div class="span7">
						<textarea id="descriptionAdd" class='text-popup' style="width: 230px"></textarea>
			   		</div>
			   	</div>
			</div>
			<div class="form-action">
				<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
				<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var actionAddProducts = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;  
		var width = 240;
		var popup = $('#alterpopupWindow');
	    var initWindow = function(){
	    	popup.jqxWindow({
			    width: 550, height: 350, isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
			});
			popup.on('close', function (event) {
				popup.jqxValidator("hide");
			});
	    };
	    
	    var initElement = function(){
	    	//$('#overrideGlAccountIdAdd').jqxDropDownList({placeHolder: '${uiLabelMap.chooseAccOverrideGlAccountId}', theme:theme, source: glAccountOACsData, displayMember: "description", valueMember: "overrideGlAccountId", width: width, dropDownWidth: 400, filterable: true});
	    	$("#invoiceItemTypeIdAdd").jqxDropDownList({placeHolder: '${uiLabelMap.chooseInvoiceItemTypeId}', source: dataItt, displayMember: "description", valueMember: "invoiceItemTypeId", width: width, dropDownWidth: 400, filterable: true, filterable: true, });
	    	$("#quantityAdd").jqxNumberInput({ width:  width, decimalDigits: 0, max : 999999999999999999, digits: 18,spinButtons: false, min: 0});
	    	$("#amountAdd").jqxNumberInput({ width:  width, decimalDigits: 0, max : 999999999999999999, digits: 18,spinButtons: false, min: 0});
	    	initProductSelect($("#productIdAdd"), $("#jqxProductGrid"));
	    	initDropDown($('#overrideGlAccountIdAdd'),$('#jqxgridGlAccount'));
	    };
	    
	    var initProductSelect = function(dropdown, grid){
			var datafields = [{ name: 'productId', type: 'string' },
				{ name: 'brandName', type: 'string' },
				{ name: 'internalName', type: 'string' },
			 	{ name: 'productTypeId', type: 'string' }];
	        var columns = [{ text: '${uiLabelMap.FormFieldTitle_productId}', datafield: 'productId', width: 150 },
			  	{ text: '${uiLabelMap.ProductBrandName}', datafield: 'brandName',width: 200 },
			  	{ text: '${uiLabelMap.ProductInternalName}', datafield: 'internalName',width: 200 },
			  	{ text: '${uiLabelMap.ProductProductType}', datafield: 'productTypeId',width: 200}];
	    	GridUtils.initDropDownButton({url: "getListProduct", autorowheight: false, filterable: true,width : 430, dropdown:{width: width,dropDownHorizontalAlignment : true},source : {pagesize : 5,cache : false}},datafields,columns, null, grid,dropdown, "productId");
		};
		
		var initDropDown = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData&getAlGlAccount=${getAlGlAccount?if_exists}',source : {pagesize  :5,cache : false},autoshowloadelement : true,width : 400,filterable : true,dropdown : {width : 240,dropDownHorizontalAlignment : true}},
			[
				{name : 'glAccountId',type : 'string',width : '15%'},
				{name : 'accountName',type : 'string'},
				{name : 'accountCode',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.accountCode}',datafield : 'accountCode'},
				{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
			]
			, null, grid,dropdown,'glAccountId');
		}
		
	    var initRule = function(){
	    	popup.jqxValidator({
		        rules: [{
		        	input: "#invoiceItemTypeIdAdd", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		}
		   		]
		    });
	    };  
	    var bindEvent = function(){
	    	$("#save").click(function () {
			    if(!actionAddProducts.save()){
			    	return;
			    }
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $("#saveAndContinue").click(function () {
		    	actionAddProducts.save();
		    });	
		    $("#cancel").click(function(){
		    	actionAddProducts.clear();
				popup.jqxValidator('hide');
			});
			//$('#productIdAdd').on('change',function(){
				//var product = $('#productIdAdd').jqxDropDownButton('val');
				//if(product) getPrice(product);
			//})
			$('#jqxProductGrid').on('rowselect',function(){
				var product = $('#productIdAdd').jqxDropDownButton('val');
				if(product) getPrice(product);
			})
	    };
	    
	    var getPrice = function(product){
	    	$.ajax({
	    		url : 'getPriceProductAcc',
	    		data : {
	    			productId : product,
	    			currencyDefaultUomId : '${defaultCurrencyUomId?if_exists?default('VND')}'
	    		},
	    		datatype : 'json',
	    		type : 'POST',
	    		async : false,
	    		success : function(response){
	    			$('#amountAdd').jqxNumberInput('val',response.price ? response.price : '');
	    		},
	    		error : function(){
	    		
	    		}
	    	})		
	    }
	    
	    var save = function(){
	    	if(!popup.jqxValidator("validate")){
	    		return false;
	    	}
	    	var index = $('#overrideGlAccountIdAdd').jqxDropDownButton('val');
	    	var overrideGlAccountId = index ? index  : "";
	    	index = $('#invoiceItemTypeIdAdd').jqxDropDownList('getSelectedItem');
	    	var invoiceItemTypeId = index && index.value ? index.value : "";
	    	index = $("#jqxProductGrid").jqxGrid("getselectedrowindex");
			var product = $("#jqxProductGrid").jqxGrid("getrowdata", index);
			var productId = product && product.productId ? product.productId : "";
	    	var row = { 
	    		description: $('#descriptionAdd').val(),         		  	
	    		invoiceItemTypeId:invoiceItemTypeId,
	    		overrideGlAccountId:overrideGlAccountId,
	    		quantity: $("#quantityAdd").jqxNumberInput('val'),
	    		amount:$("#amountAdd").jqxNumberInput('val'),
	    		productId: productId
		  	};
		  	clearForm();
		  	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        return true;
	    };
	    var clearForm = function(){
	    	$('#overrideGlAccountIdAdd').jqxDropDownButton('val','');
	    	$("#invoiceItemTypeIdAdd").jqxDropDownList('clearSelection');
	    	$("#quantityAdd").jqxNumberInput('clear');
	    	$("#amountAdd").jqxNumberInput('clear');
	    	$("#descriptionAdd").val('');
	    	GridUtils.clearDropDownButton($("#productIdAdd"), $("#jqxProductGrid"));
	    	GridUtils.clearDropDownButton($("#overrideGlAccountIdAdd"), $("#jqxgridGlAccount"));
		};
	    return {
	    	init: function(){
	    		initElement();
	    		initWindow(); 
	    		initRule();
	    		bindEvent();
	    	},
	    	save: save,
	    	clear: clearForm
	    };
	}());
	$(document).ready(function(){
		actionAddProducts.init();
	});
	
</script>