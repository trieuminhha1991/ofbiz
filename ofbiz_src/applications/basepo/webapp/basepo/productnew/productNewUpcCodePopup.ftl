<#if product.weightUomId?exists>
	<#assign weightUomGV = delegator.findOne("Uom", {"uomId", product.weightUomId}, false)!/>
	<#if weightUomGV?exists>
		<#assign listWeightUom = delegator.findByAnd("Uom", {"uomTypeId", weightUomGV.uomTypeId}, null, false)!/>
	</#if>
</#if>
<#assign uomTypeIds = ["WEIGHT_MEASURE", "CURRENCY_MEASURE"]/><#--, "PRODUCT_PACKING"-->
<#assign listUomType = delegator.findList("UomType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, uomTypeIds), null, null, null, false)!/>
<script type="text/javascript">
	var weightUomData = [
		<#if listWeightUom?has_content>
			<#list listWeightUom as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
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
<div id="alterPopupNewProdUPC" style="display:none">
	<div>${uiLabelMap.BSAddNewProductUPCCode}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="wn_upc_productId" type="hidden" value="${product.productId?if_exists}"/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSProductId}</label>
						</div>
						<div class='span7'>
							<input id="wn_upc_productCode" type="text" value="${product.productCode?if_exists}"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPLUCode}</label>
						</div>
						<div class='span7'>
							<input id="wn_upc_idPLUCode" type="text" value="<#if prodPLUCode?exists>${prodPLUCode?if_exists}</#if>"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWeightBarcodeType}</label>
						</div>
						<div class='span7'>
							<div id="wn_upc_goodIdentificationTypeId"></div>
				   		</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label class="required">${uiLabelMap.BSAmountUomType}</label>
						</div>
						<div class="span7">
							<div id="wn_upc_amountUomTypeId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label class="required">${uiLabelMap.BSMeasureUom}</label>
						</div>
						<div class="span7">
							<div id="wn_upc_measureUomId"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSMeasureValue}</label>
						</div>
						<div class='span7'>
							<div id="wn_upc_measureValue"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_upc_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_upc_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProductNewUpcCode.init();
	});
	
	var OlbProductNewUpcCode = (function(){
		var amountUomTypeDDL;
		var weightUomDDL;
		var validatorVAL;
		var goodIdentificationTypeDDL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		
		var initElement = function(){
			jOlbUtil.input.create($("#wn_upc_productCode"), {width: '96%', disabled: true});
			jOlbUtil.input.create($("#wn_upc_idPLUCode"), {width: '96%', disabled: true});
			
			<#assign decimalsInWeightGV = delegator.findOne("SystemConfig", {"systemConfigId": "DecimalsInWeight"}, false)!/>
			<#assign decimalsInWeight = 3/>
			<#assign digitsInWeight = 2/>
			<#if decimalsInWeightGV?exists>
				<#assign decimalsInWeight = decimalsInWeightGV.getString("systemValue")?default(0)?number/>
				<#assign digitsInWeight = 5 - decimalsInWeight/>
			</#if>
			jOlbUtil.numberInput.create($("#wn_upc_measureValue"), {width: '98%', spinButtons: true, digits: ${digitsInWeight?string}, decimalDigits: ${decimalsInWeight?string}});
			
			jOlbUtil.windowPopup.create($('#alterPopupNewProdUPC'), {width: 460, height: 340, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#wn_upc_alterCancel")});
		};
		
		var initElementComplex = function(){
			var configAmountUomType = {
				width:'98%',
				key: "uomTypeId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
				//addNullItem: true,
				//disabled: false,
			};
			amountUomTypeDDL = new OlbDropDownList($("#wn_upc_amountUomTypeId"), uomTypeData, configAmountUomType, ["${product.amountUomTypeId?if_exists}"]);
			
			var goodIdentificationTypeData = [
				{"id": "UPCA", "description": "UPCA"},
				{"id": "EAN", "description": "EAN13"}
			];
			var configGoodIdentificationType = {
				width:'98%',
				key: "id",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
				selectedIndex: 1,
				//addNullItem: true,
				//disabled: false,
			};
			goodIdentificationTypeDDL = new OlbDropDownList($("#wn_upc_goodIdentificationTypeId"), goodIdentificationTypeData, configGoodIdentificationType, null);
			
			var configWeightUom = {
				width: '98%',
				height: 25,
				key: "uomId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				multiSelect: false,
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListUomByType&pagesize=0<#if product.amountUomTypeId?exists>&uomTypeId=${product.amountUomTypeId}</#if>',
				renderer: function (index, label, value) {
					var datasource = $("#wn_upc_measureUomId").jqxDropDownList("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.abbreviation + "]";
							}
						}
					}
					return label;
		        },
			};
			weightUomDDL = new OlbDropDownList($("#wn_upc_measureUomId"), null, configWeightUom, ["${product.weightUomId?if_exists}"]);
		};
		
		var initValidateForm = function(){
			var extendRules = [];
	   		var mapRules = [
		                {input: '#wn_upc_productCode', type: 'validInputNotNull'},
		                {input: '#wn_upc_idPLUCode', type: 'validInputNotNull'},
		                {input: '#wn_upc_amountUomTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
		                {input: '#wn_upc_measureUomId', type: 'validObjectNotNull', objType: 'dropDownList'},
		                {input: '#wn_upc_measureValue', type: 'validInputNotNull'},
	                ];
	   		validatorVAL = new OlbValidator($('#alterPopupNewProdUPC'), mapRules, extendRules, {position: 'bottom'});
		}
		
		var initEvent = function(){
			$('#wn_upc_alterSave').click(function(){
				if (!validatorVAL.validate()) {
					return false;
				}
				var row = {
					productId: $("#wn_upc_productId").val(),
					measureUomId: weightUomDDL.getValue(),
					measureValue: $('#wn_upc_measureValue').val(),
					goodIdentificationTypeId: goodIdentificationTypeDDL.getValue()
				};
				$("#jqxgridProductUPCCodes").jqxGrid('addRow', null, row, "first");
				$("#jqxgridProductUPCCodes").jqxGrid('clearSelection');                        
				//$("#jqxgridProductUPCCodes").jqxGrid('selectRow', 0);  
				$("#alterPopupNewProdUPC").jqxWindow('close');
			});
			
			$('#alterPopupNewProdUPC').on('close', function(){
				$('#wn_upc_measureValue').val(null);
			});
			
			amountUomTypeDDL.selectListener(function(itemData){
				var uomTypeId = itemData.value;
				weightUomDDL.updateSource("jqxGeneralServicer?sname=JQGetListUomByType&pagesize=0&uomTypeId="+uomTypeId);
				var sourceAfter = weightUomDDL.getListObj().jqxDropDownList("source");
				if (sourceAfter) {
					var numberRows = sourceAfter.totalrecords;
			    	if (numberRows && numberRows > 10) {
			    		weightUomDDL.getListObj().jqxDropDownList({"autoDropDownHeight": false, "dropDownHeight": 200});
			    	}
				}
			});
		};
		
		return {
			init: init,
		}
	}());
</script>
