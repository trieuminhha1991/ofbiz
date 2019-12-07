<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)/>
<script type="text/javascript">
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
<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BSCreateNewSalesForecast}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerSalesForecast" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationSalesForecast" style="margin-bottom:5px">
		        <div id="notificationContentSalesForecast">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_salesForecastId">${uiLabelMap.BSSalesForecastId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_salesForecastId" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_organizationPartyId" class="required">${uiLabelMap.BSOrganizationId}</label>
						</div>
						<div class='span7'>
							<div id="wn_organizationPartyId">
								<div id="wn_organizationPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_customTimePeriodId" class="required">${uiLabelMap.BSSalesCustomTimePeriodId}</label>
						</div>
						<div class='span7'>
							<div id="wn_customTimePeriodId">
								<div id="wn_customTimePeriodGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_currencyUomId">${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_currencyUomId"></div>
				   		</div>
					</div>
				</div>
				<#--
				<div class='row-fluid'>
					<div class='span5'>
						<label for="wn_internalPartyId" class="required">${uiLabelMap.BSInternalPartyId}</label>
					</div>
					<div class='span7'>
						<div id="wn_internalPartyId">
							<div id="wn_internalPartyGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="wn_parentSalesForecastId">${uiLabelMap.BSParentSalesForecastId}</label>
					</div>
					<div class='span7'>
						<div id="wn_parentSalesForecastId">
							<div id="wn_parentSalesForecastGrid"></div>
						</div>
			   		</div>
				</div>
				<div class="span5 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_quotaAmount">${uiLabelMap.BSQuotaAmount}</label>
						</div>
						<div class='span6'>
							<div id="wn_quotaAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_forecastAmount">${uiLabelMap.BSForecastAmount}</label>
						</div>
						<div class='span6'>
							<div id="wn_forecastAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_bestCaseAmount">${uiLabelMap.BSBestCaseAmount}</label>
						</div>
						<div class='span6'>
							<div id="wn_bestCaseAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_closedAmount">${uiLabelMap.BSClosedAmount}</label>
						</div>
						<div class='span6'>
							<div id="wn_closedAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_percentOfQuotaForecast">${uiLabelMap.BSAbbPercentOfQuotaForecast}</label>
						</div>
						<div class='span6'>
							<div id="wn_percentOfQuotaForecast"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_percentOfQuotaClosed">${uiLabelMap.BSAbbPercentOfQuotaClosed}</label>
						</div>
						<div class='span6'>
							<div id="wn_percentOfQuotaClosed"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="wn_pipelineAmount">${uiLabelMap.BSPipelineAmount}</label>
						</div>
						<div class='span6'>
							<div id="wn_pipelineAmount"></div>
				   		</div>
					</div>
				</div>
				-->
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	var theme = $.jqx.theme;
	$(function(){
		OlbPageSalesForecastNew.init();
	});
	var OlbPageSalesForecastNew = (function(){
		var organizationPartyIdDDB;
		var customTimePeriodIdDDB;
		var currencyUomIdCBB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindow"), {width: 620, height: 300, cancelButton: $("#alterCancel")});
			jOlbUtil.input.create($("#wn_salesForecastId"), {maxLength: 20});
			jOlbUtil.notification.create($("#containerSalesForecast"), $("#jqxNotificationSalesForecast"));
			
			<#--
			$("#wn_quotaAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_quotaAmount").jqxNumberInput('val', null);
			$("#wn_forecastAmount").jqxNumberInput({width: '98%', height: 25,  spinButtons:true, decimalDigits: 0});
			$("#wn_forecastAmount").jqxNumberInput('val', null);
			$("#wn_bestCaseAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_bestCaseAmount").jqxNumberInput('val', null);
			$("#wn_closedAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_closedAmount").jqxNumberInput('val', null);
			
			$("#wn_percentOfQuotaForecast").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_percentOfQuotaForecast").jqxNumberInput('val', null);
			$("#wn_percentOfQuotaClosed").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_percentOfQuotaClosed").jqxNumberInput('val', null);
			$("#wn_pipelineAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#wn_pipelineAmount").jqxNumberInput('val', null);
			-->
		};
		var initElementComplex = function(){
			<#--
			var configSalesForecast = {
				widthButton: '99%',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				searchId: 'salesForecastId',
				datafields: [
					{name: 'salesForecastId', type: 'string'},
					{name: 'parentSalesForecastId', type: 'string'},
					{name: 'organizationPartyId', type: 'string'},
					{name: 'internalPartyId', type: 'string'},
					{name: 'customTimePeriodId', type: 'string'},
					{name: 'periodName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}', datafield: 'salesForecastId', width: '18%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'organizationPartyId', width: '18%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSInternalPartyId)}', datafield: 'internalPartyId', width: '18%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}', datafield: 'customTimePeriodId'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}', datafield: 'periodName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListSalesForecast&pagesize=0',
				useUtilFunc: true,
				key: 'salesForecastId',
				description: ['organizationPartyId', 'internalPartyId'],
				parentKeyId: 'parentSalesForecastId',
				gridType: 'jqxTreeGrid',
			};
			parentSalesForecastIdDDB = new OlbDropDownButton($("#wn_parentSalesForecastId"), $("#wn_parentSalesForecastGrid"), null, configSalesForecast, []);
			-->

			var configOrganization = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'}, {name: 'baseCurrencyUomId', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'partyId', width: '26%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}', datafield: 'baseCurrencyUomId', width: '18%'}
				],
				url: 'JQListOrganizationPartyAcctg',
				useUtilFunc: true,
				key: 'partyId', 
				description: ['groupName'],
				autoCloseDropDown: true,
			};
			organizationPartyIdDDB = new OlbDropDownButton($("#wn_organizationPartyId"), $("#wn_organizationPartyGrid"), null, configOrganization, [<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			
			<#--
			var configInternalParty = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'partyId', width: '26%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'fullName'},
				],
				url: 'JQListPartyFullName',
				useUtilFunc: true,
				key: 'partyId', 
				description: ['fullName']
			};
			internalPartyIdDDB = new OlbDropDownButton($("#wn_internalPartyId"), $("#wn_internalPartyGrid"), null, configInternalParty, [<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			-->

			var configCustomTimePeriod = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'customTimePeriodId', type: 'string'}, 
					{name: 'parentPeriodId', type: 'string'},
					{name: 'periodNum', type: 'string'},
					{name: 'periodName', type: 'string'},
					{name: 'fromDate', type: 'date', other: 'Timestamp'},
					{name: 'thruDate', type: 'date', other: 'Timestamp'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '20%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSPeriodName)}', datafield: 'periodName'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '18%',
						cellsrenderer: function(row, colum, value) {
							return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '18%',
						cellsrenderer: function(row, colum, value) {
							return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
						}
					},
				],
				url: 'JQListCustomTimePeriodSales&periodTypeId=SALES_YEAR&pagesize=0',
				useUtilFunc: true,
				key: 'customTimePeriodId',
				description: ['periodName'],
				autoCloseDropDown: true,
			};
			customTimePeriodIdDDB = new OlbDropDownButton($("#wn_customTimePeriodId"), $("#wn_customTimePeriodGrid"), null, configCustomTimePeriod, []);
			<#--
			{text: '${StringUtil.wrapString(uiLabelMap.BSParentPeriodId)}', datafield: 'parentPeriodId', width: '20%'},
			//parentKeyId: 'parentPeriodId',
			//gridType: 'jqxTreeGrid',
			-->
			
			var configCurrencyUom = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'uomId',
				value: 'descriptionSearch',
				width: '99%',
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: 'containsignorecase',
				renderer : null,
				renderSelectedItem : null,
			};
			currencyUomIdCBB = new OlbComboBox($("#wn_currencyUomId"), currencyUomData, configCurrencyUom, [<#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'</#if>]);
		};
		var initEvent = function(){
		    $("#alterSave").on('click', function(){
		    	if(!validatorVAL.validate()) return false;
		    	
		    	<#--
		    	var wn_parentSalesForecastId = parentSalesForecastIdDDB.getValue();
		    	var wn_internalPartyId = internalPartyIdDDB.getValue();
		    	-->
		    	var wn_organizationPartyId = organizationPartyIdDDB.getValue();
		    	var wn_customTimePeriodId = customTimePeriodIdDDB.getValue();
		    	var dataMap = {
		    		salesForecastId: $('#wn_salesForecastId').val(),
		    		<#--
		    		parentSalesForecastId: typeof(wn_parentSalesForecastId) != 'undefined' ? wn_parentSalesForecastId : '',
		    		internalPartyId: typeof(wn_internalPartyId) != 'undefined' ? wn_internalPartyId : '',
		    		-->
		    		organizationPartyId: typeof(wn_organizationPartyId) != 'undefined' ? wn_organizationPartyId : '',
		    		customTimePeriodId: typeof(wn_customTimePeriodId) != 'undefined' ? wn_customTimePeriodId : '',
		    		currencyUomId: currencyUomIdCBB.getValue(),
		    		
		    		<#--
		    		quotaAmount: $('#wn_quotaAmount').val(),
		    		forecastAmount: $('#wn_forecastAmount').val(),
		    		closedAmount: $('#wn_closedAmount').val(),
		    		percentOfQuotaForecast: $('#wn_percentOfQuotaForecast').val(),
		    		percentOfQuotaClosed: $('#wn_percentOfQuotaClosed').val(),
		    		pipelineAmount: $('#wn_pipelineAmount').val(),
		    		-->
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'createSalesForecastAjax',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'error'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.CommonSuccessfullyCreated)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#alterpopupWindow").jqxWindow('close');
						        	resetWindowPopupCreate();
						        	reloadSalesForecastTreeGrid();
						        	return true;
								}, function(){
									$("#alterpopupWindow").jqxWindow('close');
					        		resetWindowPopupCreate();
					        		reloadSalesForecastTreeGrid();
					        		return true;
								}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
		    });
		};
		var resetWindowPopupCreate = function(){
			$("#wn_salesForecastId").jqxInput("val", null);
			<#-- parentSalesForecastIdDDB.clearAll(); -->
			
			organizationPartyIdDDB.clearAll();
			organizationPartyIdDDB.selectItem([<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
		    	
			<#-- internalPartyIdDDB.clearAll();
			internalPartyIdDDB.selectItem([<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			-->
			customTimePeriodIdDDB.clearAll();
			currencyUomIdCBB.selectItem([<#if currentCurrencyUomId?exists>"${currentCurrencyUomId}"</#if>]);
			
			<#--
			$("#wn_quotaAmount").jqxNumberInput('val', null);
			$("#wn_forecastAmount").jqxNumberInput('val', null);
			$("#wn_bestCaseAmount").jqxNumberInput('val', null);
			$("#wn_closedAmount").jqxNumberInput('val', null);
			$("#wn_percentOfQuotaForecast").jqxNumberInput('val', null);
			$("#wn_percentOfQuotaClosed").jqxNumberInput('val', null);
			$("#wn_pipelineAmount").jqxNumberInput('val', null);
			
			$("#wn_parentSalesForecastGrid").jqxTreeGrid('updateBoundData');
			-->
		};
		var reloadSalesForecastTreeGrid = function(){
			/*
			var tmpSource = $("#jqxSalesForecast").jqxTreeGrid('source');
			tmpSource._source.url = "jqxGeneralServicer?sname=JQListSalesForecast";
			$('#jqxSalesForecast').jqxGrid('clearselection');
			$('#jqxSalesForecast').jqxGrid('source', tmpSource);
			*/
			$("#jqxSalesForecast").jqxGrid('updateBoundData');
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: '#wn_salesForecastId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_organizationPartyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				<#--{input: '#wn_internalPartyId', type: 'validObjectNotNull', objType: 'dropDownButton'},-->
				{input: '#wn_customTimePeriodId', type: 'validObjectNotNull', objType: 'dropDownButton'},
			];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#alterpopupWindow'), mapRules, extendRules);
		};
		return {
			init: init,
		};
	}());
</script>