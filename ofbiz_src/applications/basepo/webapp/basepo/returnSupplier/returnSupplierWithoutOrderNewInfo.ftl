
<form class="form-horizontal form-window-content-custom" id="initReturnWithoutOrderEntry" name="initReturnWithoutOrderEntry" method="post" action="">
	<div class="row-fluid">
		<div class="span6">
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSSupplierId}</label>
				</div>
				<div class="span7">
					<div id="supplierId">
						<div id="supplierGrid"></div>
					</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.ExportFromFacility}</label>
				</div>
				<div class="span7">
					<div id="destinationFacilityId">
						<div id="destinationFacilityGrid"></div>
					</div>
		   		</div>
			</div>
		</div><!--.span6-->
		<div class="span6">
			<div class='row-fluid'>
				<div class='span3'>
					<label>${uiLabelMap.BSCurrencyUomId}</label>
				</div>
				<div class="span9">
					<div id="currencyUomId">
						<div id="currencyUomGrid"></div>
					</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span3'>
					<label>${uiLabelMap.BSDescription}</label>
				</div>
				<div class="span9">
					<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
		   		</div>
			</div>
			<#--
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSAddress}</label>
				</div>
				<div class="span7">
					<div id="originContactMechId">
						<div id="originContactMechGrid"></div>
					</div>
		   		</div>
			</div>
			-->
		</div><!--.span6-->
	</div><!--.row-fluid-->
</form>

<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSSupplierId = "${StringUtil.wrapString(uiLabelMap.BSSupplierId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BSId = "${StringUtil.wrapString(uiLabelMap.BSId)}";
	uiLabelMap.BSDescription = "${StringUtil.wrapString(uiLabelMap.BSDescription)}";
	uiLabelMap.BSFacilityId = "${StringUtil.wrapString(uiLabelMap.BSFacilityId)}";
	uiLabelMap.BSFacilityName = "${StringUtil.wrapString(uiLabelMap.BSFacilityName)}";
	
	$(function(){
		OlbReturnWithoutOrderInfo.init();
	});
	var OlbReturnWithoutOrderInfo = (function(){
		var supplierDDB;
		var currencyUomDDB;
		var destinationFacilityDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			
		};
		var initElementComplex = function(){
			var configSupplier = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSSupplierId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.BSFullName, datafield: 'groupName', width: '70%'}
				],
				url: 'jqGetListPartySupplier',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['groupName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				dropDownHorizontalAlignment: 'left'
			};
			supplierDDB = new OlbDropDownButton($("#supplierId"), $("#supplierGrid"), null, configSupplier, null);
			
			var configCurrencyUom = {
				//widthButton: '100%',
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'uomId', type: 'string'}, 
					{name: 'description', type: 'string'}, 
				],
				columns: [
					{text: uiLabelMap.BSId, datafield: 'uomId', width: '30%'},
					{text: uiLabelMap.BSDescription, datafield: 'description', width: '70%'},
				],
				useUrl: true,
				root: 'results',
				url: '', //JQGetSupplierCurrencyUom
				useUtilFunc: true,
				selectedIndex: 0,
				key: 'uomId', 
				description: ['description'], 
				autoCloseDropDown: false,
				filterable: true,
				sortable: true,
			};
			currencyUomDDB = new OlbDropDownButton($("#currencyUomId"), $("#currencyUomGrid"), null, configCurrencyUom, null);
			
			var configDestinationFacility = {
				widthButton: '100%',
				filterable: false,
				pageable: true,
				showfilterrow: false,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'facilityId', type: 'string'}, 
					{name: 'facilityCode', type: 'string'}, 
					{name: 'facilityName', type: 'string'}
					],
				columns: [
					{text: uiLabelMap.BSFacilityId, datafield: 'facilityCode', width: '40%'},
					{text: uiLabelMap.BSFacilityName, datafield: 'facilityName'}
				],
				useUtilFunc: true,
				useUrl: true,
				root: 'results',
				url: 'JQGetListFacilityByOrg',
				
				//selectedIndex: 0,
				key: 'facilityId',
				description: ['facilityName'],
			};
			destinationFacilityDDB = new OlbDropDownButton($("#destinationFacilityId"), $("#destinationFacilityGrid"), null, configDestinationFacility, []);
		};
		var initEvent = function(){
			supplierDDB.getGrid().rowSelectListener(function(rowData){
		    	var supplierId = rowData['partyId'];
				currencyUomDDB.updateSource("jqxGeneralServicer?sname=JQGetSupplierCurrencyUom&partyId="+supplierId, null, function(){
					currencyUomDDB.selectItem(null, 0);
				});
		    });
			destinationFacilityDDB.getGrid().rowSelectListener(function(rowData){
		    	var facilityId = rowData['facilityId'];
				OlbReturnNewItemsProdAddPop.reloadListProduct();
		    });
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#supplierId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#destinationFacilityId', type: 'validObjectNotNull', objType: 'dropDownButton'},
	            ];
			validatorVAL = new OlbValidator($('#initReturnWithoutOrderEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var getValidator = function() {
			return validatorVAL;
		};
		var getValue = function(){
			return {
				supplierId: supplierDDB.getValue(),
				//originContactMechId: null,
				destinationFacilityId: destinationFacilityDDB.getValue(),
				description: $("#description").val(),
				currencyUomId: currencyUomDDB.getValue(),
			}
		};
		var getObj = function(){
			return {
				destinationFacilityDDB: destinationFacilityDDB
			}
		};
		return {
			init: init,
			getValidator: getValidator,
			getValue: getValue,
			getObj: getObj,
		};
	}());
</script>
