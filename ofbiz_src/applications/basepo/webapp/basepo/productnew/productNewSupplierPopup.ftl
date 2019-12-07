<#assign listPartyRelationship = delegator.findList("ListPartySupplierByRole", null, null, null, null, false)!/>
<#assign defaultCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<#assign defaultCountryGeoId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCountryGeo(delegator)!/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)!/>
<script type="text/javascript">
	var partyRelationshipData = [
		<#if listPartyRelationship?exists>
			<#list listPartyRelationship as partyRelationship>
			{	partyId: "${partyRelationship.partyId?if_exists}",
				preferredCurrencyUomId: "${partyRelationship.preferredCurrencyUomId?if_exists}",
				groupName: "${partyRelationship.groupName?if_exists}"
			},
			</#list>
		</#if>
	];
	var canDropShipData = [
		{id:'Y', description:"${uiLabelMap.CommonYes}"},
		{id:'N', description:"${uiLabelMap.CommonNo}"}
	];
	var multiLang = {
		"filterchoosestring": "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}"
	};
	var currencyUomData = [
	<#if currencyUom?exists>
		<#list currencyUom as uomItem>
		{	uomId : "${uomItem.uomId}",
			descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
		},
		</#list>
	</#if>
	];
</script>

<div id="alterpopupWindowSupplierProductNew" style="display:none">
	<div>${uiLabelMap.BSAddSupplierProvideProduct}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSupplier}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_supplierId">
								<div id="wn_sprod_supplierGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSProductIdOfSupplier}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sprod_supplierProductId" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSMinimumOrderQuantity}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_minimumOrderQuantity"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.FormFieldTitle_availableFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_availableFromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.FormFieldTitle_availableThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_availableThruDate"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_productCurrencyUomId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSBuyPrice}</label><#--FormFieldTitle_lastPrice-->
						</div>
						<div class='span7'>
							<div id="wn_sprod_lastPrice"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5'>
							<label>${uiLabelMap.FormFieldTitle_shippingPrice}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_shippingPrice"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSComment}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sprod_comments" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.FormFieldTitle_canDropShip}</label>
						</div>
						<div class='span7'>
							<div id="wn_sprod_canDropShip"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_sprod_btnSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_sprod_btnCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "productNewSupplierQuickNew.ftl"/>

<script type="text/javascript">
	$(function(){
		OlbSupplierProductAddNew.init();
	});
	var OlbSupplierProductAddNew = (function(){
		var supplierDDB;
		var currencyUomDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_sprod_supplierProductId"), {width: '98%'});
			jOlbUtil.input.create($("#wn_sprod_comments"), {width: '96%'});
			jOlbUtil.numberInput.create($("#wn_sprod_minimumOrderQuantity"), {width: '100%', spinButtons: true, decimalDigits: 0, min: 1, decimal: 1, inputMode: 'simple'});
			jOlbUtil.numberInput.create($("#wn_sprod_lastPrice"), {width: '98%', spinButtons: false, digits: 8, decimalDigits: 3, allowNull: true, min: 0});
			jOlbUtil.numberInput.create($("#wn_sprod_shippingPrice"), {width: '98%', spinButtons: false, digits: 8, decimalDigits: 3, allowNull: true, min: 0});
			jOlbUtil.dateTimeInput.create($("#wn_sprod_availableFromDate"), {width: '100%', allowNullDate: true, showFooter: true});
			jOlbUtil.dateTimeInput.create($("#wn_sprod_availableThruDate"), {width: '100%', allowNullDate: true, showFooter: true});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowSupplierProductNew"), {maxWidth: 960, width: 960, height: 310, cancelButton: $("#wn_sprod_btnCancel")});
			
			$("#wn_sprod_availableFromDate").jqxDateTimeInput("setDate", new Date());
			$("#wn_sprod_availableThruDate").jqxDateTimeInput("setDate", null);
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#wn_sprod_lastPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#wn_sprod_shippingPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
				}
			}, 50);
		};
		var initElementComplex = function(){
			<#--
			var configSupplier = {
				width: '100%',
				dropDownWidth: '300px',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				datafields: [{name: 'partyId', type: 'string'}, {name: 'preferredCurrencyUomId', type: 'string'}, {name: 'groupName', type: 'string'}],
				useUrl: true,
				url: 'jqxGeneralServicer?sname=jqGetListPartySupplier',
				key: 'partyId',
				value: 'groupName',
				autoDropDownHeight: true,
				showAddButton: true
			}
			supplierDDL = new OlbDropDownList($("#wn_sprod_supplierId"), null, configSupplier, []); //partyRelationshipData
			-->
			var configSupplier = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'partyId', type: 'string'}, 
					{name: 'partyCode', type: 'string'}, 
					{name: 'preferredCurrencyUomId', type: 'string'}, 
					{name: 'groupName', type: 'string'}
				],
				columns: [
					{text: "${StringUtil.wrapString(uiLabelMap.BSSupplierId)}", datafield: 'partyCode', width: '30%'},
					{text: "${StringUtil.wrapString(uiLabelMap.BSFullName)}", datafield: 'groupName', width: '70%'}
				],
				url: 'jqGetListPartySupplier',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['groupName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				displayDetail: false
			};
			supplierDDB = new OlbDropDownButton($("#wn_sprod_supplierId"), $("#wn_sprod_supplierGrid"), null, configSupplier, []);
			
			var configCurrencyUom = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'uomId',
				value: 'descriptionSearch',
				width: '98%',
				height: '28px',
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: 'containsignorecase',
				renderer : null,
				renderSelectedItem : null,
			};
			currencyUomDDL = new OlbDropDownList($("#wn_sprod_productCurrencyUomId"), currencyUomData, configCurrencyUom, []);
			
			var configCanDropShip = {
				width: '98%',
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				useUrl: false,
				url: '',
				key: 'id',
				value: 'description',
				autoDropDownHeight: true
			}
			new OlbDropDownList($("#wn_sprod_canDropShip"), canDropShipData, configCanDropShip, ["N"]);
		};
		var initEvent = function(){
			$("#wn_sprod_btnSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				
				var uid = $("#jqxgridSupplierProduct").data("uid");
				if (uid) {
					$('#jqxgridSupplierProduct').jqxGrid('updaterow', uid, getValue());
				} else {
					$('#jqxgridSupplierProduct').jqxGrid('addrow', null, getValue());
				}
				$("#alterpopupWindowSupplierProductNew").jqxWindow("close");
			});
			
			$('#alterpopupWindowSupplierProductNew').on('close', function (event) {
				$('#alterpopupWindowSupplierProductNew').jqxValidator('hide');
				supplierDDB.clearAll();
				$("#wn_sprod_lastPrice").jqxNumberInput('setDecimal', 0);
				$("#wn_sprod_shippingPrice").jqxNumberInput('setDecimal', 0);
				$("#wn_sprod_productCurrencyUomId").jqxDropDownList('clearSelection');
				$("#wn_sprod_minimumOrderQuantity").jqxNumberInput('setDecimal', 1);
				$("#wn_sprod_availableThruDate").jqxDateTimeInput('setDate', null);
				$("#wn_sprod_canDropShip").jqxDropDownList('val', "N");
				$("#wn_sprod_supplierProductId").val("");
				$("#wn_sprod_comments").val("");
				$("#alterpopupWindowSupplierProductNew").data("uid", null);
			});
			
			supplierDDB.getGrid().rowSelectListener(function(itemData){
				var preferredCurrencyUomId = itemData.preferredCurrencyUomId;
				if (preferredCurrencyUomId) currencyUomDDL.selectItem([preferredCurrencyUomId]);
				else currencyUomDDL.clearAll(false);
			});
			
			$("#wn_sprod_supplierId_addBtn").on("click", function(){
				$("#alterPopupSupplierNew").jqxWindow("open");
			});
		};
		var initValidateForm = function(){
			var extendRules = [
					{input: '#wn_sprod_availableFromDate, #wn_sprod_availableThruDate', message: "${uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate}", action: 'valueChanged', 
						rule: function(input, commit){
							return OlbValidatorUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1: "wn_sprod_availableFromDate", paramId2: "wn_sprod_availableThruDate"});
						}
					}
	           ];
			var mapRules = [
		            {input: '#wn_sprod_supplierProductId', type: 'validCannotSpecialCharactor'},
					{input: '#wn_sprod_supplierProductId', type: 'validInputNotNull'},
					{input: '#wn_sprod_supplierId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#wn_sprod_minimumOrderQuantity', type: 'validInputNotNull'},
					{input: '#wn_sprod_productCurrencyUomId', type: 'validInputNotNull'},
					{input: '#wn_sprod_availableFromDate', type: 'validInputNotNull'},
					{input: '#wn_sprod_availableFromDate', type: 'validDateCompareToday'},
					{input: '#wn_sprod_availableThruDate', type: 'validDateCompareToday'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowSupplierProductNew'), mapRules, extendRules, {position: 'bottom'});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				supplierDDB.selectItem([data.partyId]);
				$("#wn_sprod_productCurrencyUomId").jqxDropDownList('val', data.currencyUomId);
				$("#wn_sprod_comments").val(data.comments);
				$("#wn_sprod_supplierProductId").val(data.supplierProductId);
				$("#wn_sprod_canDropShip").jqxDropDownList('val', data.canDropShip);
				if (data.availableFromDate) {
					if (data.availableFromDate.time) {
						$('#wn_sprod_availableFromDate').jqxDateTimeInput('setDate', new Date(data.availableFromDate.time));
					} else if (data.availableFromDate) {
						$('#wn_sprod_availableFromDate').jqxDateTimeInput('setDate', new Date(data.availableFromDate));
					}
				}
				if (data.availableThruDate) {
					if (data.availableThruDate.time) {
						$('#wn_sprod_availableThruDate').jqxDateTimeInput('setDate', new Date(data.availableThruDate.time));
					} else if (data.availableThruDate) {
						$('#wn_sprod_availableThruDate').jqxDateTimeInput('setDate', new Date(data.availableThruDate));
					}
				}
				$("#wn_sprod_lastPrice").jqxNumberInput('setDecimal', data.lastPrice);
				$("#wn_sprod_shippingPrice").jqxNumberInput('setDecimal', data.shippingPrice);
				$("#wn_sprod_minimumOrderQuantity").jqxNumberInput('setDecimal', data.minimumOrderQuantity);
				
				$("#wn_sprod_productCurrencyUomId").jqxDropDownList({ disabled: true });
				$('#wn_sprod_availableFromDate').jqxDateTimeInput({disabled: true});
				$("#wn_sprod_minimumOrderQuantity").jqxNumberInput({disabled: true});
			}
		};
		var getValue = function() {
			var value = new Object();
			value.partyId = supplierDDB.getValue();
			value.groupName = $("#wn_sprod_supplierId").val();
			value.currencyUomId = $("#wn_sprod_productCurrencyUomId").jqxDropDownList('val');
			value.comments = $("#wn_sprod_comments").val();
			value.supplierProductId = $("#wn_sprod_supplierProductId").val();
			value.canDropShip = $("#wn_sprod_canDropShip").jqxDropDownList('val');
			var availableFromDate;
			$('#wn_sprod_availableFromDate').jqxDateTimeInput('getDate')?availableFromDate=$('#wn_sprod_availableFromDate').jqxDateTimeInput('getDate').getTime():availableFromDate;
			value.availableFromDate = availableFromDate;
			var availableThruDate;
			$('#wn_sprod_availableThruDate').jqxDateTimeInput('getDate')?availableThruDate=$('#wn_sprod_availableThruDate').jqxDateTimeInput('getDate').getTime():availableThruDate;
			value.availableThruDate = availableThruDate;
			value.lastPrice = $("#wn_sprod_lastPrice").jqxNumberInput('getDecimal');
			value.shippingPrice = $("#wn_sprod_shippingPrice").jqxNumberInput('getDecimal');
			value.minimumOrderQuantity = $("#wn_sprod_minimumOrderQuantity").jqxNumberInput('getDecimal');
			return value;
		};
		var getObj = function(){
			return {
				"supplierDDB": supplierDDB
			}
		};
		return {
			init: init,
			getObj: getObj,
		}
	}());
</script>

