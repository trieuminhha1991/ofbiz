
<div id="alterpopupWindowEdit" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSUpdateSalesChannel)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class="row-fluid">
						<div class='span5'>
							<label class="asterisk">${uiLabelMap.BSPSChannelId}</label>
						</div>
						<div class="span7">
							<input id="we_productStoreId"/>
						</div>
					</div>
					<div class="row-fluid">
						<div class='span5'>
							<label class="asterisk">${uiLabelMap.BSPSChannelName}</label>
						</div>
						<div class="span7">
							<input id="we_storeName"/>
						</div>
					</div>
					<div class="row-fluid">
						<div class='span5'>
							<label class="asterisk">${uiLabelMap.BSPayToParty}</label>
						</div>
						<div class="span7">
							<input id="we_payToPartyId"/>
						</div>
					</div>
					<#--
					<div class="row-fluid">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSTitle}</label>
						<div class="span7">
							<input id="we_titleEdit"/>
						</div>
					</div>
					-->
					<div class="row-fluid">
						<div class="span5">
							<label>${uiLabelMap.BSSalesChannelType}</label>
						</div>
						<div class="span7">
							<div id="we_salesMethodChannelEnumId"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSalesChannelEnumId}</label>
						</div>
						<div class='span7'>
							<div id="we_defaultSalesChannelEnumId"></div>
				   		</div>
					</div>
					<#--
					<div class="row-fluid">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSVatTaxAuthGeoId}</label>
						<div class="span7">
							<div id="vatTaxAuthGeoIdEdit">
								<div id="jqxVatTaxGrid"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSSubtitle}</label>
						<div class="span7">
							<input id="we_subtitle" />
						</div>
					</div>
					-->
					<div class='row-fluid hide'>
						<div class='span5'>
							<label>${uiLabelMap.BSShowPricesWithVatTax}</label>
						</div>
						<div class='span7'>
							<div id="we_showPricesWithVatTax"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<#--
					<div class="row-fluid">
						<div class='span5'>
							<label>${uiLabelMap.BSPaymentMethod}</label>
						</div>
						<div class="span7">
							<div id="we_storeCreditAccountEnumId"></div>
						</div>
					</div>
					-->
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthParty}</label>
						</div>
						<div class='span7'>
							<input id="we_vatTaxAuthPartyId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthGeo}</label>
						</div>
						<div class='span7'>
							<input id="we_vatTaxAuthGeoId"/>
				   		</div>
					</div>
					<div class="row-fluid">
						<div class='span5'>
							<label>${uiLabelMap.BSDefaultCurrencyUomId}</label>
						</div>
						<div class="span7">
							<div id="we_defaultCurrencyUomId"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSIncludeCustomerOtherSalesChannel}</label>
						</div>
						<div class='span7'>
							<div id="we_includeOtherCustomer"></div>
				   		</div>
					</div>
					<#--
					<div class="row-fluid">
						<label class="span5 align-right line-height-25">${uiLabelMap.BSVatTaxAuthPartyId}</label>
						<div class="span7">
							<div id="vatTaxAuthPartyIdEdit">
								<div id="jqxPayToPartyGrid" ></div>
							</div>
						</div>
					</div>
					-->
				</div>
			</div>
			<div class="row-fluid">
				<div class="legend-container">
					<span>${uiLabelMap.BSFacility}</span>
					<hr/>
				</div>
				<div class="row-fluid">
					<div class="span6 form-window-content-custom">
						<div class='row-fluid'>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BSFacilityDelivery}</label>
								</div>
								<div class='span7'>
									<div id="we_inventoryFacilityId">
										<div id="we_inventoryFacilityGrid"></div>
									</div>
						   		</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSReserveOrderEnum}</label>
							</div>
							<div class='span7'>
								<div id="we_reserveOrderEnumId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BSRequireInventory}</label>
							</div>
							<div class='span7'>
								<div id="we_requireInventory"></div>
					   		</div>
						</div>
					</div><!--.span6-->
				</div>
			</div><!--.row-fluid-->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="we_alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i>${uiLabelMap.Cancel}
						</button>
						<button type="button" id="we_alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'></i>${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	<#--
	var configInventoryFacility2 = {
		useUrl: true,
		root: 'results',
		widthButton: '100%',
		heightButton: 30,
		showdefaultloadelement: false,
		autoshowloadelement: false,
		dropDownHorizontalAlignment: 'left',
		datafields: [
			{name: 'facilityId', type: 'string'},
	   		{name: 'descriptionType', type: 'string'},
	   		{name: 'facilityName', type: 'string'},
	      	{name: 'groupName', type: 'string'}
	    ],
		columns: [
			{text: '${uiLabelMap.BSFacilityId}', datafield: 'facilityId', width: '25%'},
			{text: '${uiLabelMap.BSFacilityTypeId}', datafield: 'descriptionType', width: '25%',},
			{text: '${uiLabelMap.BSFacilityName}', datafield: 'facilityName', width: '25%'},
			{text: '${uiLabelMap.BSGroupName}', datafield: 'groupName'}
		],
		url: 'JQGetListFacilityAvailable',
		useUtilFunc: true,
		key: 'facilityId',
		description: ['facilityName']
	};
	-->
	
	$(function(){
		OlbProductStoreEdit.init();
	});
	var OlbProductStoreEdit = (function(){
		var currencyUomDDL;
		var defaultSalesChannelDDL;
		<#--var storeCreditAccountDDL;-->
		var salesMethodChannelDDL;
		var inventoryFacilityDDB;
		var reserveOrderEnumDDL;
		var showPricesWithVatTaxDDL;
		var includeOtherCustomerDDL;
		var validatorVAL;
		var requireInventoryDDL;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
			initValidateForm();
		};
		
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowEdit"), {maxWidth:1100, width: 1050, height: 440, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#we_alterCancel2")});
			jOlbUtil.input.create($("#we_productStoreId"), {placeHolder: "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}", height: 24, width: '99%', minLength: 1, disabled: true});
			jOlbUtil.input.create($("#we_storeName"), {placeHolder: "${StringUtil.wrapString(uiLabelMap.BSStoreName)}", height: 24, width: '99%', minLength: 1});
			jOlbUtil.input.create($("#we_vatTaxAuthPartyId"), {height: 24, width: '96%', minLength: 1, disabled: true});
			jOlbUtil.input.create($("#we_vatTaxAuthGeoId"), {height: 24, width: '96%', minLength: 1, disabled: true});
			jOlbUtil.input.create($("#we_payToPartyId"), {height: 24, width: '99%', minLength: 1, disabled: true});
			
			<#--
			jOlbUtil.input.create($("#we_titleEdit"), {height: 24, width: '99%', minLength: 1});
			jOlbUtil.input.create($("#we_subtitle"), {height: 24, width: '99%', minLength: 1});
			-->
		};
		
		var initComplexElement = function(){
			var configDefaultSalesChannel = {
				width: '100%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
				disabled: true,
			};
			defaultSalesChannelDDL = new OlbDropDownList($("#we_defaultSalesChannelEnumId"), defaultSalesChannelData, configDefaultSalesChannel, []);
			
			var configCurrencyUom = {
				width: '98%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'uomId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex: 0,
				displayDetail: true,
				dropDownHorizontalAlignment: 'right',
				useUrl: false,
				url: "",
			};
			currencyUomDDL = new OlbDropDownList($("#we_defaultCurrencyUomId"), currencyUomData, configCurrencyUom, ["${currencyUomId?if_exists}"]);
			
			<#--
			var configStoreCreditAccount = {
				width: '98%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex: 0,
				displayDetail: true,
				dropDownHorizontalAlignment: 'right',
				useUrl: false,
				url: "",
			};
			storeCreditAccountDDL = new OlbDropDownList($("#we_storeCreditAccountEnumId"), storeCreditAccountEnumList, configStoreCreditAccount, []);
			-->
			var configSalesMethodChannel = {
				width: '100%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				//selectedIndex: 0,
				displayDetail: true,
				dropDownHorizontalAlignment: 'right',
				useUrl: false,
				url: "",
				disabled: true,
			};
			salesMethodChannelDDL = new OlbDropDownList($("#we_salesMethodChannelEnumId"), salesMethodChannelData, configSalesMethodChannel, []);

			var configInventoryFacility = {
				useUrl: true,
				root: 'results',
				widthButton: '98%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{name: 'facilityId', type: 'string'},
                    {name: 'facilityTypeId', type: 'string'},
			   		{name: 'descriptionType', type: 'string'},
			   		{name: 'facilityName', type: 'string'},
			      	{name: 'groupName', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSFacilityId}', datafield: 'facilityId', width: '20%'},
                    {
                        text: '${uiLabelMap.BSFacilityTypeId}', datafield: 'facilityTypeId', width: '20%',
                        cellsrenderer: function (row, column, value) {
                            if (facilityTypeDescData.length > 0) {
                                for (var i = 0; i < facilityTypeDescData.length; i++) {
                                    if (value == facilityTypeDescData[i].facilityTypeId) {
                                        return '<span title =\"' + facilityTypeDescData[i].description + '\">' + facilityTypeDescData[i].description + '</span>';
                                    }
                                }
                            }
                            return '<span title=' + value + '>' + value + '</span>';
                        },
                    },
					{text: '${uiLabelMap.BSFacilityName}', datafield: 'facilityName', width: '20%'},
					{text: '${uiLabelMap.BSOrganization}', datafield: 'groupName'}
				],
				url: 'JQGetListFacilityAvailable',
				useUtilFunc: true,
				key: 'facilityId',
				description: ['facilityName']
			};
			inventoryFacilityDDB = new OlbDropDownButton($("#we_inventoryFacilityId"), $("#we_inventoryFacilityGrid"), null, configInventoryFacility, []);
			
			var configReserveOrderEnum = {
				width: '99%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			reserveOrderEnumDDL = new OlbDropDownList($("#we_reserveOrderEnumId"), reserveOrderEnumData, configReserveOrderEnum, []);
			
			var configShowPricesWithVatTax = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: uiLabelMap.BSClickToChoose,
			}
			showPricesWithVatTaxDDL = new OlbDropDownList($('#we_showPricesWithVatTax'), dataYesNoChoose, configShowPricesWithVatTax, ["N"]);
			
			var configIncludeOtherCustomer = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: uiLabelMap.BSClickToChoose,
			}
			includeOtherCustomerDDL = new OlbDropDownList($('#we_includeOtherCustomer'), dataYesNoChoose, configIncludeOtherCustomer, ["N"]);
			
			var configRequireInventory = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	    		
			}
			requireInventoryDDL = new OlbDropDownList($('#we_requireInventory'), dataYesNoChoose, configRequireInventory, []);
			
		};
		
		var initEvent = function(){
			$('#we_alterSave2').click(function () {
			   	editProductStore();
		       	$('#alterpopupWindowEdit').jqxWindow('hide');
		       	$('#alterpopupWindowEdit').jqxWindow('close');
		    });
		    
		    $('#alterpopupWindowEdit').on('close',function(){
				$('#alterpopupWindowEdit').jqxValidator('hide');
				$('#productStoreIdAdd').val(null);
				$('#storeNameAdd').val(null);
				$('#titleAdd').val(null);
				$('#subtitleAdd').val(null);
			});
			
			var editProductStore = function(){
				//var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
				//var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
				//if (!rowData) {
				//	jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseRow)}!");
				//	return false;
				//}
				if (!validatorVAL.validate()) return false;
				
				var aProductStore = {
					'productStoreId': $("#we_productStoreId").val(),
					'storeName': $("#we_storeName").val(),
					'payToPartyId': $("#we_payToPartyId").val(),
					'defaultCurrencyUomId': currencyUomDDL.getValue(),
					'salesMethodChannelEnumId': salesMethodChannelDDL.getValue(),
					'inventoryFacilityId': inventoryFacilityDDB.getValue(),
					'reserveOrderEnumId': reserveOrderEnumDDL.getValue(),
					'showPricesWithVatTax': showPricesWithVatTaxDDL.getValue(),
					'includeOtherCustomer': includeOtherCustomerDDL.getValue(),
					'requireInventory': requireInventoryDDL.getValue()
				};
				
				if (aProductStore.length <= 0){
					return false;
				} else {
					aProductStore = JSON.stringify(aProductStore);
					jQuery.ajax({
				        url: 'editProductStoreChannel',
				        type: 'POST',
				        async: true,
				        data: {'aProductStore': aProductStore},
				        success: function(data) {
				        	jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'error'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.BSEditSuccess)}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	if ($("#jqxgrid").length > 0) {
							        		$("#jqxgrid").jqxGrid('updatebounddata');
				        					$("#jqxgrid").jqxGrid('clearselection');
							        	} else {
							        		location.reload();
							        	}
									}
							);
				        },
				        error: function(e){
				        	alert("Send request is error");
				        }
				    });
				}
				
				<#--
				aProductStore['title'] = $('#we_titleEdit').val();
				aProductStore['subtitle'] = $('#we_subtitle').val();
				aProductStore['storeCreditAccountEnumId'] = storeCreditAccountDDL.getValue();
				-->
			}
		};
		
		var setValue = function(data) {
			if (data) {
				if (data.productStoreId != null) $("#we_productStoreId").val(data.productStoreId);
				else $("#we_productStoreId").val("");
				
				if (data.storeName != null) $("#we_storeName").val(data.storeName);
				else $("#we_storeName").val("");
				
				if (data.payToPartyId != null) $("#we_payToPartyId").val(data.payToPartyId);
				else $("#we_payToPartyId").val("");
				
				if (data.defaultCurrencyUomId != null) currencyUomDDL.selectItem([data.defaultCurrencyUomId]);
				else currencyUomDDL.clearAll();
				
				if (data.defaultSalesChannelEnumId != null) defaultSalesChannelDDL.selectItem([data.defaultSalesChannelEnumId]);
				else defaultSalesChannelDDL.clearAll();
				
				if (data.vatTaxAuthPartyId != null) $("#we_vatTaxAuthPartyId").jqxInput("val", data.vatTaxAuthPartyId);
				else $("#we_vatTaxAuthPartyId").jqxInput("val", "");
				
				if (data.vatTaxAuthGeoId != null) $("#we_vatTaxAuthGeoId").jqxInput("val", data.vatTaxAuthGeoId);
				else $("#we_vatTaxAuthGeoId").jqxInput("val", "");
				
				if (data.salesMethodChannelEnumId != null) salesMethodChannelDDL.selectItem([data.salesMethodChannelEnumId]);
				else salesMethodChannelDDL.clearAll();
				
				if (data.inventoryFacilityId != null) inventoryFacilityDDB.selectItem([data.inventoryFacilityId]);
				else inventoryFacilityDDB.clearAll();
				
				if (data.reserveOrderEnumId != null) reserveOrderEnumDDL.selectItem([data.reserveOrderEnumId]);
				else reserveOrderEnumDDL.clearAll();
				
				if (data.showPricesWithVatTax != null) showPricesWithVatTaxDDL.selectItem([data.showPricesWithVatTax]);
				else showPricesWithVatTaxDDL.clearAll();
				
				if (data.includeOtherCustomer != null) includeOtherCustomerDDL.selectItem([data.includeOtherCustomer]);
				else includeOtherCustomerDDL.clearAll();
				
				if (data.requireInventory != null) requireInventoryDDL.selectItem([data.requireInventory]);
				else requireInventoryDDL.clearAll();
				
				var parentQuantityObj = $("#we_showPricesWithVatTax").closest(".row-fluid");
				if (parentQuantityObj) {
					if (data.defaultSalesChannelEnumId == "POS_SALES_CHANNEL") {
						parentQuantityObj.show();
					} else {
						parentQuantityObj.hide();
					}
				}
				<#--
				if (data.title != null) $("#we_titleEdit").val(data.title);
				if (data.subtitle != null) $("#we_subtitle").val(data.subtitle);
				if (data.storeCreditAccountEnumId != null) storeCreditAccountDDL.selectItem([data.storeCreditAccountEnumId]);
				-->
			}
		};
		var openWindow = function(data) {
			if (data) setValue(data);
			$("#alterpopupWindowEdit").jqxWindow("open");
		};
		
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
		            {input: '#we_productStoreId', type: 'validInputNotNull'},
		            {input: '#we_productStoreId', type: 'validCannotSpecialCharactor'},
		            {input: '#we_storeName', type: 'validInputNotNull'},
		            {input: '#we_salesMethodChannelEnumId', type: 'validInputNotNull', objType: 'dropDownList'},
		            {input: '#we_payToPartyId', type: 'validInputNotNull', objType: 'dropDownButton'},
		            <#--{input: '#we_inventoryFacilityId', type: 'validInputNotNull'},-->
					{input: '#we_requireInventory', type: 'validInputNotNull', objType: 'dropDownList'},
				];
			validatorVAL = new OlbValidator($('#alterpopupWindowEdit'), mapRules, extendRules);
		};
		
		return {
			init: init,
			setValue: setValue,
			openWindow: openWindow,
		};
	}());
</script>