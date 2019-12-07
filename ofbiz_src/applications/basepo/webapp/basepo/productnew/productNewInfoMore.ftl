<style type="text/css">
	#toolbarcontainerjqxgridProductConfigItem{
		padding-left:0;
	}
	#toolbarcontainerjqxgridProductConfigItem h4{
		font-size: 11pt;
	    line-height: 20px;
	}
	#toolbarcontainerjqxgridProductConfigItem .custom-control-toolbar{
		margin-top:0;
		margin-bottom:0;
		padding-top:0;
		padding-bottom:0;
	}
	.form-window-content-custom.content-description .row-fluid > div:last-child > span{
		color: #333;
		font-weight:normal;
	}
</style>
<#assign uomTypeIds = ["WEIGHT_MEASURE"]/><#--, "PRODUCT_PACKING"-->
<#assign listUomType = delegator.findList("UomType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, uomTypeIds), null, null, null, false)!/>
<style type="text/css">
	#horizontalScrollBarjqxGridAlterUom {
		visibility: hidden !important;
	}
</style>
<script type="text/javascript">
	var uomTypeData = [
		<#if listUomType?exists>
			<#list listUomType as item>
			{	uomTypeId: "${item.uomTypeId}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
</script>
<div id="form-product-info-more" class="row-fluid">
	<div class="span4">
		<div class="form-horizontal form-window-content-custom label-text-left content-description">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSProductType}:</label>
						</div>
						<div class="div-inline-block">
							<span id="sc2_productTypeId">___</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSProductId}:</label>
						</div>
						<div class="div-inline-block">
							<span id="sc2_productCode">___</span>
						</div>
					</div>
					<hr class="small-margin"/>
					<div class="row-fluid">
						<div class="span4">
							<label>${uiLabelMap.BSSalesDiscountinuationDate}</label>
						</div>
						<div class="span8">
							<div id="salesDiscontinuationDate"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span4">
							<label>${uiLabelMap.BSPurchaseDiscountinuationDate}</label>
						</div>
						<div class="span8">
							<div id="purchaseDiscontinuationDate"></div>
						</div>
					</div>
					<hr class="small-margin"/>
					<div class="row-fluid">
						<div class="span4">
							<label>${uiLabelMap.BSAmountUomType}</label>
						</div>
						<div class="span8">
							<div id="amountUomTypeId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span4">
							<label>${uiLabelMap.BSPLUCode}</label>
						</div>
						<div class="span8">
							<input id="idPLUCode" type="text"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div><!--.span4-->
	<div class="span8">
		<#assign dataFieldProductConfigItem = "[
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
					{name: 'productName', type: 'string'},
					{name: 'quantity', type: 'number'},
					{name: 'amount', type: 'number'},
					{name: 'sequenceNum', type: 'number'}
				]"/>
		<#assign columnlistProductConfigItem = "
					{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '24%', editable: false}, 
					{text: '${uiLabelMap.BSProductName}', dataField: 'productName', editable: false}, 
					{text: '${uiLabelMap.BSQuantity}', dataField: 'quantity', width: '20%',
						columntype: 'numberinput', cellsformat: 'd',
				 		cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				   			returnVal += formatnumber(value) + '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 1) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 1, decimal: 1, inputMode: 'simple'});
						}
					}, 
					{text: '${uiLabelMap.BSWeight}', dataField: 'amount', width: '20%',
						columntype: 'numberinput', cellsformat: 'd',
				 		cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				   			returnVal += formatnumber(value, '${locale}', 3) + '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: true, decimalDigits: 3, min: 0, inputMode: 'simple'});
						}
					}, 
					{text: '${uiLabelMap.BSSequenceNum}', dataField: 'sequenceNum', width: '10%',
						columntype: 'numberinput', cellsformat: 'd',
				 		cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				   			returnVal += formatnumber(value) + '</div>';
			   				return returnVal;
					 	},
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, allowNull: true, inputMode: 'simple'});
						}
					}
				"/>
		<div id="prodConfigItemContainer" style="<#if updateMode && product.productTypeId == "AGGREGATED"><#else>display:none</#if>">
			<div id="jqxgridProductConfigItem"></div>
		</div>
	</div>
</div>

<#if !updateMode || (updateMode && product.productTypeId == "AGGREGATED")>
<#include "productNewProdConfigItemPopup.ftl">
</#if>

<script type="text/javascript">
	$(function(){
		OlbProductInfoMoreNew.init();
	});
	var OlbProductInfoMoreNew = (function(){
		var amountUomTypeDDL;
		var validatorVAL;
		var salesDiscontinuationDateOrigin;
		var purchaseDiscontinuationDateOrigin;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			<#if updateMode>initUpdateMode();</#if>
		};
		<#if updateMode>
		var initUpdateMode = function(){
			amountUomTypeDDL.selectItem(["${product.amountUomTypeId?if_exists}"]);
		};
		</#if>
		var initElement = function(){
			jOlbUtil.dateTimeInput.create($("#salesDiscontinuationDate"), {"allowNullDate": true, showFooter: true});
			jOlbUtil.dateTimeInput.create($("#purchaseDiscontinuationDate"), {"allowNullDate": true, showFooter: true});
			jOlbUtil.input.create($("#idPLUCode"), {width: 213, maxLength: 5});
			
			<#if updateMode>
			$("#salesDiscontinuationDate").jqxDateTimeInput("val", <#if product.salesDiscontinuationDate?exists>"${product.salesDiscontinuationDate}"<#else>null</#if>);
			$("#purchaseDiscontinuationDate").jqxDateTimeInput("val", <#if product.purchaseDiscontinuationDate?exists>"${product.purchaseDiscontinuationDate}"<#else>null</#if>);
			$("#idPLUCode").jqxInput("val", <#if prodPlucode?exists>"${prodPlucode?if_exists}"<#else>null</#if>);
			<#if product.salesDiscontinuationDate?exists>salesDiscontinuationDateOrigin = (new Date("${product.salesDiscontinuationDate}"));</#if>
			<#if product.purchaseDiscontinuationDate?exists>purchaseDiscontinuationDateOrigin = new Date("${product.purchaseDiscontinuationDate}");</#if>
			<#else>
			$("#salesDiscontinuationDate").jqxDateTimeInput("val", null);
			$("#purchaseDiscontinuationDate").jqxDateTimeInput("val", null);
			$("#idPLUCode").jqxInput("val", null);
			</#if>
		};
		var initElementComplex = function(){
			var configGridProductConfigItem = {
				theme: 'olbius',
				datafields: ${dataFieldProductConfigItem},
				columns: [${columnlistProductConfigItem}],
				width: '100%',
				height: 200,
				editable: true,
				sortable: false,
				filterable: false,
				pageable: true,
				pagesize: 10,
				showfilterrow: false,
				useUtilFunc: false,
				<#if updateMode && product.productTypeId == "AGGREGATED">
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProdConfigItemProduct',
				<#if !copyMode>
				dataMap: {"productId": "${product.productId?if_exists}", "checkActive": "Y"},
				</#if>
				<#else>
				useUrl: false,
				url: '',
				</#if>
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: false,
				toolbarheight: 22,
				showtoolbar:true,
				rendertoolbar: function(toolbar){
					<#assign customcontrol1 = "fa fa-plus@@javascript: void(0);@OlbProductInfoMoreNew.addProdConfigItem()">
					<#assign customcontrol2 = "fa fa-minus@@javascript: void(0);@OlbProductInfoMoreNew.removeProdConfigItem()">
					<@renderToolbar id="jqxgridProductConfigItem" isShowTitleProperty="true" customTitleProperties="${StringUtil.wrapString(uiLabelMap.BSProductItem)}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="alterpopupWindow" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="false" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3="" customtoolbaraction=""/>
				},
			};
			new OlbGrid($("#jqxgridProductConfigItem"), null, configGridProductConfigItem, []);
			
			var configAmountUomType = {
				//width:'100%',
				//height: 25,
				key: "uomTypeId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
				addNullItem: true,
			};
			amountUomTypeDDL = new OlbDropDownList($("#amountUomTypeId"), uomTypeData, configAmountUomType, []);
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#idPLUCode', message: validFieldRequire, action: 'keyup', 
					rule: function(input, commit){
						var amountUomTypeId = amountUomTypeDDL.getValue();
						if (OlbCore.isNotEmpty(amountUomTypeId)) {
							return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
						}
						return true;
					}
				},
				{input: '#salesDiscontinuationDate', message: validRequiredValueGreatherOrEqualDateTimeToDay, action: 'valueChanged', 
					rule: function(input, commit){
						var toDay = new Date();
						if (salesDiscontinuationDateOrigin < toDay) {
							return true;
						}
						return OlbValidatorUtil.validElement(input, commit, 'validDateTimeCompareToday');
					}
				},
				{input: '#purchaseDiscontinuationDate', message: validRequiredValueGreatherOrEqualDateTimeToDay, action: 'valueChanged', 
					rule: function(input, commit){
						var toDay = new Date();
						if (purchaseDiscontinuationDateOrigin < toDay) {
							return true;
						}
						return OlbValidatorUtil.validElement(input, commit, 'validDateTimeCompareToday');
					}
				},
			];
			var mapRules = [
				{input: '#idPLUCode', type: 'validOnlyContainCharacterNumber'},
			];
			validatorVAL = new OlbValidator($('#form-product-info-more'), mapRules, extendRules, {position: 'bottom'});
		};
		var addProdConfigItem = function(){
			$("#alterpopupWindowProdConfigItemNew").jqxWindow("open");
		};
		var removeProdConfigItem = function(){
			var rowIndex = $("#jqxgridProductConfigItem").jqxGrid('getselectedrowindex');
			if (rowIndex == null || rowIndex < 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
			} else {
				var rowData = $("#jqxgridProductConfigItem").jqxGrid('getrowdata', rowIndex);
				if (rowData) {
					$("#jqxgridProductConfigItem").jqxGrid('deleterow', rowData.uid);
				}
			}
		};
		var updateInfoScreen2 = function(){
			var productVirtualTypeObj = OlbProductNewInfo.getObj().productVirtualTypeDDL;
			var sc1_productType = "___";
			if (productVirtualTypeObj) sc1_productType = productVirtualTypeObj.getLabel();
			$("#sc2_productTypeId").text(sc1_productType);
			
			var sc1_productCode = $("#productCode").val();
			if (OlbCore.isEmpty(sc1_productCode)) sc1_productCode = "___";
			$("#sc2_productCode").text(sc1_productCode);
		};
		var getValueProdConfigItem = function() {
			var data = $("#jqxgridProductConfigItem").jqxGrid('getboundrows');
			if (data) {
				for ( var x in data) {
					if (data[x].productName) {
						data[x].productName = "";
					}
				}
				return JSON.stringify(data);
			}
			return "";
		};
		var getValue = function(){
			var dataMap = {};
			var prodConfigItems = getValueProdConfigItem();
			if (OlbCore.isNotEmpty(prodConfigItems)) {
				dataMap.productConfigItem = prodConfigItems;
			}
			var salesDiscontinuationDate = $('#salesDiscontinuationDate').jqxDateTimeInput('getDate');
			if (typeof(salesDiscontinuationDate) != 'undefined' && salesDiscontinuationDate != null) {
				dataMap.salesDiscontinuationDate = salesDiscontinuationDate.getTime();
			}
			var purchaseDiscontinuationDate = $('#purchaseDiscontinuationDate').jqxDateTimeInput('getDate');
			if (typeof(purchaseDiscontinuationDate) != 'undefined' && purchaseDiscontinuationDate != null) {
				dataMap.purchaseDiscontinuationDate = purchaseDiscontinuationDate.getTime();
			}
			dataMap.amountUomTypeId = amountUomTypeDDL.getValue();
			dataMap.idPLUCode = $("#idPLUCode").val();
			return dataMap;
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var getObj = function(){
			return {
				amountUomTypeDDL: amountUomTypeDDL
			};
		};
		return {
			init: init,
			addProdConfigItem: addProdConfigItem,
			removeProdConfigItem: removeProdConfigItem,
			updateInfoScreen2: updateInfoScreen2,
			getValueProdConfigItem: getValueProdConfigItem,
			getValue: getValue,
			getValidator: getValidator,
			getObj: getObj,
		};
	}());
</script>