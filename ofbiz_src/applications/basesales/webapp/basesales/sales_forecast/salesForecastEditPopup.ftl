<#if !currentOrganizationPartyId?exists><#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator)/></#if>
<#if !currentCurrencyUomId?exists><#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/></#if>
<#if !currencyUom?exists>
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
</#if>
<div id="alterpopupWindowEdit" style="display:none">
	<div>${uiLabelMap.BSEditSalesForecast}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerSalesForecastEdit" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationSalesForecastEdit" style="margin-bottom:5px">
		        <div id="notificationContentSalesForecastEdit">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="we_salesForecastId">${uiLabelMap.BSSalesForecastId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="we_salesForecastId" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="we_organizationPartyId" class="required">${uiLabelMap.BSOrganizationId}</label>
						</div>
						<div class='span7'>
							<div id="we_organizationPartyId">
								<div id="we_organizationPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="we_internalPartyId" class="required">${uiLabelMap.BSInternalPartyId}</label>
						</div>
						<div class='span7'>
							<div id="we_internalPartyId">
								<div id="we_internalPartyGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<#--<div class='row-fluid'>
						<div class='span5'>
							<label for="we_parentSalesForecastId">${uiLabelMap.BSParentSalesForecastId}</label>
						</div>
						<div class='span7'>
							<div class="container-add-plus">
								<div id="we_parentSalesForecastId">
									<div id="we_parentSalesForecastGrid"></div>
								</div>
								<a href="javascript:OlbPageSalesForecastEdit.resetValueDropDownButton('we_parentSalesForecastId', 'we_parentSalesForecastGrid');" class="add-quickly"><i class="fa fa-trash red"></i></a>
							</div>
				   		</div>
					</div>-->
					<div class='row-fluid'>
						<div class='span5'>
							<label for="we_customTimePeriodId" class="required">${uiLabelMap.BSSalesCustomTimePeriodId}</label>
						</div>
						<div class='span7'>
							<div id="we_customTimePeriodId">
								<div id="we_customTimePeriodGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="we_currencyUomId">${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="we_currencyUomId"></div>
				   		</div>
					</div>
				</div>
				<#--
				<div class="span5 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_quotaAmount">${uiLabelMap.BSQuotaAmount}</label>
						</div>
						<div class='span6'>
							<div id="we_quotaAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_forecastAmount">${uiLabelMap.BSForecastAmount}</label>
						</div>
						<div class='span6'>
							<div id="we_forecastAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_bestCaseAmount">${uiLabelMap.BSBestCaseAmount}</label>
						</div>
						<div class='span6'>
							<div id="we_bestCaseAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_closedAmount">${uiLabelMap.BSClosedAmount}</label>
						</div>
						<div class='span6'>
							<div id="we_closedAmount"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_percentOfQuotaForecast">${uiLabelMap.BSAbbPercentOfQuotaForecast}</label>
						</div>
						<div class='span6'>
							<div id="we_percentOfQuotaForecast"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_percentOfQuotaClosed">${uiLabelMap.BSAbbPercentOfQuotaClosed}</label>
						</div>
						<div class='span6'>
							<div id="we_percentOfQuotaClosed"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span6'>
							<label for="we_pipelineAmount">${uiLabelMap.BSPipelineAmount}</label>
						</div>
						<div class='span6'>
							<div id="we_pipelineAmount"></div>
				   		</div>
					</div>
				</div>
				-->
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSaveEdit" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancelEdit" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
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
		OlbPageSalesForecastEdit.init();
	});
	var OlbPageSalesForecastEdit = (function(){
		var organizationPartyIdDDB;
		var internalPartyIdDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowEdit"), {maxWidth: 1000, width: 1000, height: 250, cancelButton: $("#alterCancelEdit")});
			jOlbUtil.input.create($("#we_salesForecastId"), {maxLength: 20, disabled: true});
			jOlbUtil.notification.create($("#containerSalesForecastEdit"), $("#jqxNotificationSalesForecastEdit"));
			
			<#--
			$("#we_quotaAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_quotaAmount").jqxNumberInput('val', null);
			$("#we_forecastAmount").jqxNumberInput({width: '98%', height: 25,  spinButtons:true, decimalDigits: 0});
			$("#we_forecastAmount").jqxNumberInput('val', null);
			$("#we_bestCaseAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_bestCaseAmount").jqxNumberInput('val', null);
			$("#we_closedAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_closedAmount").jqxNumberInput('val', null);
			$("#we_percentOfQuotaForecast").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_percentOfQuotaForecast").jqxNumberInput('val', null);
			$("#we_percentOfQuotaClosed").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_percentOfQuotaClosed").jqxNumberInput('val', null);
			$("#we_pipelineAmount").jqxNumberInput({width: '98%', height: 25, spinButtons:true, decimalDigits: 0});
			$("#we_pipelineAmount").jqxNumberInput('val', null);
			-->
		};
		var initElementComplex = function(){
			<#--
			var configSalesForecast = {
				widthButton: '98%',
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
			parentSalesForecastIdDDB = new OlbDropDownButton($("#we_parentSalesForecastId"), $("#we_parentSalesForecastGrid"), null, configSalesForecast, []);
			-->

			var configOrganization = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
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
				description: ['groupName']
			};
			organizationPartyIdDDB = new OlbDropDownButton($("#we_organizationPartyId"), $("#we_organizationPartyGrid"), null, configOrganization, [<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			
			var configInternalParty = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
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
			internalPartyIdDDB = new OlbDropDownButton($("#we_internalPartyId"), $("#we_internalPartyGrid"), null, configInternalParty, [<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			
			var configCustomTimePeriod = {
				useUrl: true,
				root: 'results',
				widthButton: '98%',
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
			};
			customTimePeriodIdDDB = new OlbDropDownButton($("#we_customTimePeriodId"), $("#we_customTimePeriodGrid"), null, configCustomTimePeriod, []);

			var configCurrencyUom = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'uomId',
				value: 'descriptionSearch',
				width: '98%',
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: 'containsignorecase',
				renderer : null,
				renderSelectedItem : null,
			};
			currencyUomIdCBB = new OlbComboBox($("#we_currencyUomId"), currencyUomData, configCurrencyUom, [<#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'</#if>]);
		};
		var initEvent = function(){
		    $("#alterSaveEdit").on('click', function(){
		    	if(!$('#alterpopupWindowEdit').jqxValidator('validate')) return false;
		    	<#--var we_parentSalesForecastId = parentSalesForecastIdDDB.getValue();-->
		    	var we_organizationPartyId = organizationPartyIdDDB.getValue();
		    	var we_internalPartyId = internalPartyIdDDB.getValue();
		    	var we_customTimePeriodId = customTimePeriodIdDDB.getValue();
		    	var dataMap = {
		    		salesForecastId: $('#we_salesForecastId').val(),
		    		<#--parentSalesForecastId: typeof(we_parentSalesForecastId) != 'undefined' ? we_parentSalesForecastId : '',-->
		    		organizationPartyId: typeof(we_organizationPartyId) != 'undefined' ? we_organizationPartyId : '',
		    		internalPartyId: typeof(we_internalPartyId) != 'undefined' ? we_internalPartyId : '',
		    		customTimePeriodId: typeof(we_customTimePeriodId) != 'undefined' ? we_customTimePeriodId : '',
		    		currencyUomId: currencyUomIdCBB.getValue(),
		    		
		    		<#--
		    		quotaAmount: $('#we_quotaAmount').val(),
		    		forecastAmount: $('#we_forecastAmount').val(),
		    		closedAmount: $('#we_closedAmount').val(),
		    		percentOfQuotaForecast: $('#we_percentOfQuotaForecast').val(),
		    		percentOfQuotaClosed: $('#we_percentOfQuotaClosed').val(),
		    		pipelineAmount: $('#we_pipelineAmount').val(),
		    		-->
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'updateSalesForecastAjax',
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
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	resetWindowPopupCreate();
						        	reloadSalesForecastTreeGrid();
						        	return true;
								}, function(){
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
						$("#alterpopupWindowEdit").jqxWindow('close');
					},
				});
		    });
		};
		var resetWindowPopupCreate = function(){
			$("#we_salesForecastId").jqxInput("val", null);
			
			organizationPartyIdDDB.clearAll();
			organizationPartyIdDDB.selectItem([<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
		    
			internalPartyIdDDB.clearAll();
			internalPartyIdDDB.selectItem([<#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>]);
			
			customTimePeriodIdDDB.clearAll();
			currencyUomIdCBB.selectItem([<#if currentCurrencyUomId?exists>"${currentCurrencyUomId}"</#if>]);
			
			<#--
			parentSalesForecastIdDDB.clearAll();
			$("#we_quotaAmount").jqxNumberInput('val', null);
			$("#we_forecastAmount").jqxNumberInput('val', null);
			$("#we_bestCaseAmount").jqxNumberInput('val', null);
			$("#we_closedAmount").jqxNumberInput('val', null);
			$("#we_percentOfQuotaForecast").jqxNumberInput('val', null);
			$("#we_percentOfQuotaClosed").jqxNumberInput('val', null);
			$("#we_pipelineAmount").jqxNumberInput('val', null);
			
			$("#we_parentSalesForecastGrid").jqxTreeGrid('updateBoundData');
			-->
		};
		var reloadSalesForecastTreeGrid = function(){
			$("#jqxSalesForecast").jqxGrid('updateBoundData');
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: '#we_salesForecastId', type: 'validCannotSpecialCharactor'},
				{input: '#we_organizationPartyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				{input: '#we_internalPartyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				{input: '#we_customTimePeriodId', type: 'validObjectNotNull', objType: 'dropDownButton'},
			];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#alterpopupWindowEdit'), mapRules, extendRules, {position: 'bottom'});
		};
		var editSalesForecast = function(){
			var idGrid = "#jqxSalesForecast";
			var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
			//var selection = $(idGrid).jqxTreeGrid('getSelection');
			//if (selection.length <= 0) {
			if (rowindex < 0) {
				var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.BSYouNotYetChooseRow}!</span>";
				bootbox.dialog(messageError, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
				}]);
				return false;
			}
        	//rowData = selection[0];
        	var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
        	if (rowData) {
        		$("#we_salesForecastId").jqxInput("val", rowData.salesForecastId);
        		
		    	<#--parentSalesForecastIdDDB.selectItem([rowData.parentSalesForecastId]);-->
		    	organizationPartyIdDDB.selectItem([rowData.organizationPartyId]);
		    	internalPartyIdDDB.selectItem([rowData.internalPartyId]);
		    	customTimePeriodIdDDB.selectItem([rowData.customTimePeriodId]);
        		
        		<#--
        		$("#we_quotaAmount").jqxNumberInput("val", rowData.quotaAmount);
        		$("#we_forecastAmount").jqxNumberInput("val", rowData.forecastAmount);
        		$("#we_closedAmount").jqxNumberInput("val", rowData.closedAmount);
        		$("#we_percentOfQuotaForecast").jqxNumberInput("val", rowData.percentOfQuotaForecast);
        		$("#we_percentOfQuotaClosed").jqxNumberInput("val", rowData.percentOfQuotaClosed);
        		$("#we_pipelineAmount").jqxNumberInput("val", rowData.pipelineAmount);
        		-->
        		
        		$("#alterpopupWindowEdit").jqxWindow("open");
        	}
		};
		return {
			init: init,
			editSalesForecast: editSalesForecast,
		};
	}());
</script>