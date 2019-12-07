<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_NEW", "")>
<div id="alterpopupWindowAddCustomerEmployee" style="display:none">
	<div>${uiLabelMap.DAAddEmployeeIsCustomerIntoStore}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<input type="hidden" id="wn_ce_productStoreId" value="${parameters.productStoreId?if_exists}">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_ce_partyId">${uiLabelMap.DAEmployeeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_ce_partyId">
								<div id="wn_ce_partyGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_ce_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_ce_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.DALoading}...</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/resources/js/sales/ett/olb.core.js"></script>
<script type="text/javascript" src="/resources/js/sales/ett/olb.util.js"></script>
<script type="text/javascript" src="/resources/js/sales/ett/olb.grid.js"></script>
<script type="text/javascript" src="/resources/js/sales/ett/olb.dropdownbutton.js"></script>
<script type="text/javascript" src="/resources/js/sales/ett/olb.validator.js"></script>
<script type="text/javascript">
	$(function(){
		OlbAddCustomerEmployee.init();
	});
	var OlbAddCustomerEmployee = (function(){
		var employeeDDB;
		var validatorVAL;
		
		var init = function(){
			initElementComplex();
			initWindow();
			initEvent();
			initValidateForm();
		};
		var initWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowAddCustomerEmployee"), {width: 540, height: 200, cancelButton: $("#wn_ce_alterCancel")});
		};
		var initElementComplex = function(){
			var configEmployee = {
				useUrl: true,
				widthButton: '98%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.DAEmployeeId)}', datafield: 'partyId', width: '30%'},
					{text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield: 'fullName'}
				],
				url: 'JQGetListEmployeeByOrg',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true
			};
			employeeDDB = new OlbDropDownButton($("#wn_ce_partyId"), $("#wn_ce_partyGrid"), null, configEmployee, []);
		};
		var initEvent = function(){
			$('#wn_ce_alterSave').on('click', function(){
				if(!validatorVAL.validate()) return false;
				
				jOlbUtil.confirm.dialog("${uiLabelMap.DAAreYouSureYouWantAdd}?", 
						function(){
							$.ajax({
								type: 'POST',
								data: {
									partyId: employeeDDB.getAttrDataValue(),
									productStoreId: $("#wn_ce_productStoreId").val()
								},
								url: 'addEmployeeToCustomerOfStore',
								beforeSend: function(){
									$("#loader_page_common").show();
								},
								success: function(data){
									jOlbUtil.processResultDataAjax(data, "default",  function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	$('#alterpopupWindowAddCustomerEmployee').jqxWindow("close");
								        	$('body').trigger('addCustomerEmployeeComplete');
										});
								},
								error: function(data){
									alert("Send request is error");
									$("#btnPrevWizard").removeClass("disabled");
									$("#btnNextWizard").removeClass("disabled");
								},
								complete: function(data){
									$("#loader_page_common").hide();
								},
							});
						}
				);
			});
		};
		var initValidateForm = function(){
			<#--$('#alterpopupWindowAddCustomerEmployee').jqxValidator({
				position: 'bottom',
	        	rules: [
	        		{input: '#wn_ce_partyId', message: '${uiLabelMap.DARequired}', action: 'change', 
						rule: function(input, commit){
							var config = {objType: 'comboBox'};
							return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', config);
						}
					},
				]
	        });-->
			validatorVAL = new OlbValidator($('#alterpopupWindowAddCustomerEmployee'), [{input: '#wn_ce_partyId', type: 'validInputNotNull'}], [], {position: 'bottom'});
		};
		var openWindowAdd = function() {
			$('#alterpopupWindowAddCustomerEmployee').jqxWindow("open");
		};
		return {
			init: init,
			openWindowAdd: openWindowAdd
		};
	}());
</script>
</#if>