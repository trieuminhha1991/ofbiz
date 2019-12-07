<script src="/crmresources/js/generalUtils.js"></script>
<div id="alterpopupWindow1" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewSalesChannel)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSChannelId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_productStoreId" class="span12" maxlength="20" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSChannelName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_storeName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPayToParty}</label>
						</div>
						<div class='span7'>
							<div id="wn_payToPartyId">
								<div id="wn_payToPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSalesChannelType}</label>
						</div>
						<div class='span7'>
							<div id="wn_salesMethodChannelEnumId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSalesChannelEnumId}</label>
						</div>
						<div class='span7'>
							<div id="wn_defaultSalesChannelEnumId"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5'>
							<label>${uiLabelMap.BSShowPricesWithVatTax}</label>
						</div>
						<div class='span7'>
							<div id="wn_showPricesWithVatTax"></div>
				   		</div>
					</div>
				</div><!-- .span6 -->
				<div class="span6 form-window-content-custom">
					<#--<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSTitle}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_title" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSubtitle}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_subtitle" class="span12" value=""/>
				   		</div>
					</div>-->
					<div class='row-fluid' style="display:none">
						<div class='span5'>
							<label>${uiLabelMap.BSPaymentMethod}</label>
						</div>
						<div class='span7'>
							<div id="wn_storeCreditAccountEnumId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthParty}</label>
						</div>
						<div class='span7'>
							<div id="wn_vatTaxAuthPartyId">
								<div id="wn_vatTaxAuthPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthGeo}</label>
						</div>
						<div class='span7'>
							<div id="wn_vatTaxAuthGeoId">
								<div id="wn_vatTaxAuthGeoGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSDefaultCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_defaultCurrencyUomId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSIncludeCustomerOtherSalesChannel}</label>
						</div>
						<div class='span7'>
							<div id="wn_includeOtherCustomer"></div>
				   		</div>
					</div>
				</div><!-- .span6 -->
			</div><!-- .row-fluid -->
			<div class="row-fluid">
				<div class="legend-container">
					<span>${uiLabelMap.BSFacility}</span>
					<hr/>
				</div>
				<div class="row-fluid">
					<div class="span6 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSFacilityDelivery}</label>
							</div>
							<div class='span7'>
								<div id="wn_inventoryFacilityId">
									<div id="wn_inventoryFacilityGrid"></div>
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
								<div id="wn_reserveOrderEnumId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BSRequireInventory}</label>
							</div>
							<div class='span7'>
								<div id="wn_requireInventory"></div>
					   		</div>
						</div>
					</div><!--.span6-->
				</div>
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="wn_alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		OlbProductStoreNewPS.init();
	});
	var OlbProductStoreNewPS = (function(){
		var salesMethodChannelDDL;
		var defaultSalesChannelDDL;
		var reserveOrderEnumDDL;
		var showPricesWithVatTaxDDL;
		var includeOtherCustomerDDL;
		var validatorVAL;
		var payToPartyIdDDB;
		var defaultCurrencyUomIdCBB;
		var inventoryFacilityIdDDB;
		var storeCreditAccountEnumIdDDL;
		var vatTaxAuthPartyIdDDB;
		var vatTaxAuthGeoIdDDB;
		var requireInventoryDDL;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create("#wn_productStoreId", {width:'93%', height: 26});
			jOlbUtil.input.create("#wn_storeName", {width:'93%', height:26});
			<#--jOlbUtil.input.create("#wn_title", {width:'93%', height:26});
			jOlbUtil.input.create("#wn_subtitle", {width:'93%', height:26});-->
			
			jOlbUtil.windowPopup.create($("#alterpopupWindow1"), {maxWidth:1100, width: 1050, height : 440, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#wn_alterCancel1")});
		};
		var initComplexElement = function(){
			var configOrganization = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'}, {name: 'baseCurrencyUomId', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'partyId', width: '26%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}', datafield: 'baseCurrencyUomId', width: '18%'}
				],
				url: 'JQListOrganizationPartyS',
				useUtilFunc: true,
				key: 'partyId',
				description: ['groupName']
			};
			payToPartyIdDDB = new OlbDropDownButton($("#wn_payToPartyId"), $("#wn_payToPartyGrid"), null, configOrganization, [<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
		
			var configCurrencyUom = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'uomId',
				value: 'descriptionSearch',
				width: '99%',
				height: '28px',
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: 'containsignorecase',
				renderer : null,
				renderSelectedItem : null,
			};
			defaultCurrencyUomIdCBB = new OlbComboBox($("#wn_defaultCurrencyUomId"), currencyUomData, configCurrencyUom, [<#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'</#if>]);
			
			var configInventoryFacility = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'facilityId', type: 'string'},
					{name: 'facilityCode', type: 'string'},
                    {name: 'facilityTypeId', type: 'string'},
			   		{name: 'descriptionType', type: 'string'},
			   		{name: 'facilityName', type: 'string'},
			      	{name: 'groupName', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSFacilityId}', datafield: 'facilityCode', width: 120},
                    {
                        text: '${uiLabelMap.BSFacilityTypeId}', datafield: 'facilityTypeId', width: 120,
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
					{text: '${uiLabelMap.BSFacilityName}', datafield: 'facilityName', width: 220},
					{text: '${uiLabelMap.BSOrganization}', datafield: 'groupName', minwidth: 100}
				],
				url: 'JQGetListFacilityAvailable',
				useUtilFunc: true,
				key: 'facilityId',
				description: ['facilityName'],
				pagesize: 5
			};
			inventoryFacilityIdDDB = new OlbDropDownButton($("#wn_inventoryFacilityId"), $("#wn_inventoryFacilityGrid"), null, configInventoryFacility, []);
			
			var configCreditAccount = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: false,
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex:0,
				width:'99%',
				height: '30px',
			}
			storeCreditAccountEnumIdDDL = new OlbDropDownList($("#wn_storeCreditAccountEnumId"), storeCreditAccountEnumList, configCreditAccount, ["BILLING_ACCOUNT"]);
			
			var configTaxAuthParty = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{name: 'taxAuthPartyId', type: 'string'},
			   		{name: 'taxAuthGeoId', type: 'string'},
			      	{name: 'groupName', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSPartyId}', datafield: 'taxAuthPartyId', width: '20%'},
					{text: '${uiLabelMap.BSTaxAuthGeoId}', datafield: 'taxAuthGeoId', width: '20%'},
					{text: '${uiLabelMap.BSGroupName}', datafield: 'groupName'}
				],
				url: 'JQGetListTaxAuthority',
				useUtilFunc: true,
				key: 'taxAuthPartyId',
				description: ['groupName']
			};
			vatTaxAuthPartyIdDDB = new OlbDropDownButton($("#wn_vatTaxAuthPartyId"), $("#wn_vatTaxAuthPartyGrid"), null, configTaxAuthParty, ["VNM_TAX"]);
			
			var configTaxAuthGeo = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{name: 'geoId', type: 'string'},
			   		{name: 'geoTypeId', type: 'string'},
			   		{name: 'geoName', type: 'string'},
			      	{name: 'geoCode', type: 'string'},
			      	{name: 'geoSecCode', type: 'string'},
			      	{name: 'abbreviation', type: 'string'},
			      	{name: 'wellKnownText', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSGeoId}', datafield: 'geoId', width: '10%'},
					{text: '${uiLabelMap.BSGeoTypeId}', datafield: 'geoTypeId', width: '15%',
						cellsrenderer: function(column, row, value){
							for(var i = 0;  i < geoTypeList.length; i++){
								if(geoTypeList[i].geoTypeId == value){
									return '<span title=' + value + '>' + geoTypeList[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>'
						},
					},
					{text: '${uiLabelMap.BSGeoName}', datafield: 'geoName'},
					{text: '${uiLabelMap.BSGeoCode}', datafield: 'geoCode', width: '10%'},
					{text: '${uiLabelMap.BSGeoSecCode}', datafield: 'geoSecCode', width: '10%'},
					{text: '${uiLabelMap.BSAbbreviation}', datafield: 'abbreviation', width: '13%'},
					{text: '${uiLabelMap.BSWellKnownText}', datafield: 'wellKnownText', width: '15%'}
				],
				url: 'JQGetListGeos',
				useUtilFunc: true,
				key: 'geoId',
				description: ['geoName']
			};
			vatTaxAuthGeoIdDDB = new OlbDropDownButton($("#wn_vatTaxAuthGeoId"), $("#wn_vatTaxAuthGeoGrid"), null, configTaxAuthGeo, ["VNM"]);
			
			var configSalesMethodChannel = {
				width: '99%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			salesMethodChannelDDL = new OlbDropDownList($("#wn_salesMethodChannelEnumId"), salesMethodChannelData, configSalesMethodChannel, []);
			
			var configDefaultSalesChannel = {
				width: '99%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			defaultSalesChannelDDL = new OlbDropDownList($("#wn_defaultSalesChannelEnumId"), defaultSalesChannelData, configDefaultSalesChannel, []);
			
			var configReserveOrderEnum = {
				width: '99%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			reserveOrderEnumDDL = new OlbDropDownList($("#wn_reserveOrderEnumId"), reserveOrderEnumData, configReserveOrderEnum, []);
			
			var configShowPricesWithVatTax = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	    		
			}
			showPricesWithVatTaxDDL = new OlbDropDownList($('#wn_showPricesWithVatTax'), dataYesNoChoose, configShowPricesWithVatTax, ["N"]);
			
			var configIncludeOtherCustomer = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	    		
			}
			includeOtherCustomerDDL = new OlbDropDownList($('#wn_includeOtherCustomer'), dataYesNoChoose, configIncludeOtherCustomer, ["N"]);
			
			var configRequireInventory = {
				width: "99%",
	    		key: "id",
	    		value: "description",
	    		autoDropDownHeight: true,
	    		displayDetail: false,
	    		placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	    		
			}
			requireInventoryDDL = new OlbDropDownList($('#wn_requireInventory'), dataYesNoChoose, configRequireInventory, []);
			
		};
		
		var initEvent = function(){
			defaultSalesChannelDDL.selectListener(function(itemData, index){
				var value = itemData.value;
				var parentQuantityObj = $("#wn_showPricesWithVatTax").closest(".row-fluid");
				if (parentQuantityObj) {
					if ("POS_SALES_CHANNEL" == value) {
						parentQuantityObj.show();
					} else {
						parentQuantityObj.hide();
					}
				}
				
			});
			
			$('#wn_alterSave1').click(function(){
				$('#alterpopupWindow1').jqxValidator('validate');
			});
			
			$('#alterpopupWindow1').on('validationSuccess',function(){
				var row = {
						productStoreId : $('#wn_productStoreId').val(),
						storeName : $('#wn_storeName').val(),
						payToPartyId : payToPartyIdDDB.getValue(),
						salesMethodChannelEnumId : salesMethodChannelDDL.getValue(),
						reserveOrderEnumId: reserveOrderEnumDDL.getValue(),
						<#--
						title: $('#wn_title').val(),
						subtitle: $('#wn_subtitle').val(),
						-->
						defaultCurrencyUomId : defaultCurrencyUomIdCBB.getValue(),
						defaultSalesChannelEnumId : defaultSalesChannelDDL.getValue(),
						storeCreditAccountEnumId : storeCreditAccountEnumIdDDL.getValue(),
						vatTaxAuthGeoId: vatTaxAuthGeoIdDDB.getValue(),
						vatTaxAuthPartyId: vatTaxAuthPartyIdDDB.getValue(),
						inventoryFacilityId : inventoryFacilityIdDDB.getValue(),
						showPricesWithVatTax: showPricesWithVatTaxDDL.getValue(),
						includeOtherCustomer: includeOtherCustomerDDL.getValue(),
						requireInventory: requireInventoryDDL.getValue(),
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				$("#jqxgrid").jqxGrid('clearSelection');                        
				$("#jqxgrid").jqxGrid('selectRow', 0);  
				$("#alterpopupWindow1").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			});
			
			$('#alterpopupWindow1').on('close',function(){
				$('#alterpopupWindow1').jqxValidator('hide');
				$('#jqxgrid').jqxGrid('refresh');
				$('#wn_productStoreId').val(null);
				$('#wn_storeName').val(null);
				<#--
				$('#wn_title').val(null);
				$('#wn_subtitle').val(null);
				-->
				payToPartyIdDDB.selectItem([<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
				salesMethodChannelDDL.clearAll();
				reserveOrderEnumDDL.clearAll();
				defaultCurrencyUomIdCBB.selectItem([<#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'</#if>]);
				defaultSalesChannelDDL.clearAll();
				storeCreditAccountEnumIdDDL.selectItem(["BILLING_ACCOUNT"]);
				vatTaxAuthGeoIdDDB.selectItem("VNM");
				inventoryFacilityIdDDB.clearAll();
				showPricesWithVatTaxDDL.selectItem("N");
				includeOtherCustomerDDL.selectItem("N");
				requireInventoryDDL.clearAll();
			});
		};
		
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
		            {input: '#wn_productStoreId', type: 'validInputNotNull'},
		            {input: '#wn_productStoreId', type: 'validCannotSpecialCharactor'},
		            {input: '#wn_storeName', type: 'validInputNotNull'},
		            {input: '#wn_salesMethodChannelEnumId', type: 'validInputNotNull', objType: 'dropDownList'},
		            {input: '#wn_payToPartyId', type: 'validInputNotNull', objType: 'dropDownButton'},
		            <#--{input: '#wn_inventoryFacilityId', type: 'validInputNotNull'},-->
					{input: '#wn_requireInventory', type: 'validInputNotNull', objType: 'dropDownList'},
				];
			validatorVAL = new OlbValidator($('#alterpopupWindow1'), mapRules, extendRules);
		};
		
		return {
			init: init
		}
	}());
</script>