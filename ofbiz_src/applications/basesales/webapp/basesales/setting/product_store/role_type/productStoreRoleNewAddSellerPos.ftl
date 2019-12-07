<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_NEW", "")>
<div id="alterpopupWindowAddSellerPos" style="display:none">
	<div>${uiLabelMap.BSAddPosSeller}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<input type="hidden" id="wn_sepos_productStoreId" value="${parameters.productStoreId?if_exists}">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_sepos_partyId">${uiLabelMap.BSEmployeeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_sepos_partyId">
								<div id="wn_sepos_partyGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_sepos_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_sepos_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<script type="text/javascript">
	var OlbAddSellerPos = (function(){
		var employeeDDB;
		var validatorVAL;
		
		var init = function(){
			initElementComplex();
			initWindow();
			initEvent();
			initValidateForm();
		};
		var initWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowAddSellerPos"), {width: 540, height: 160, cancelButton: $("#wn_sepos_alterCancel")});
		};
		var initElementComplex = function(){
			var configEmployee = {
				useUrl: true,
				widthButton: '98%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}', datafield: 'partyCode', width: '30%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'fullName'}
				],
				url: 'JQGetListEmployeeByOrg',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true
			};
			employeeDDB = new OlbDropDownButton($("#wn_sepos_partyId"), $("#wn_sepos_partyGrid"), null, configEmployee, []);
		};
		var initEvent = function(){
			$('#wn_sepos_alterSave').on('click', function(){
				if(!validatorVAL.validate()) return false;
				
				jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToAdd}", 
						function(){
							$.ajax({
								type: 'POST',
								data: {
									partyId: employeeDDB.getAttrDataValue(),
									productStoreId: $("#wn_sepos_productStoreId").val()
								},
								url: 'addPosSellerOfStore',
								beforeSend: function(){
									$("#loader_page_common").show();
								},
								success: function(data){
									jOlbUtil.processResultDataAjax(data, "default",  function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	$('#alterpopupWindowAddSellerPos').jqxWindow("close");
								        	$('body').trigger('addSellerPosComplete');
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
			var mapRules = [{input: '#wn_sepos_partyId', type: 'validInputNotNull'}];
			validatorVAL = new OlbValidator($('#alterpopupWindowAddSellerPos'), mapRules, [], {position: 'bottom'});
		};
		var openWindowAdd = function() {
			$('#alterpopupWindowAddSellerPos').jqxWindow("open");
		};
		return {
			init: init,
			openWindowAdd: openWindowAdd
		};
	}());
	
	$(function(){
		OlbAddSellerPos.init();
	});
</script>
</#if>