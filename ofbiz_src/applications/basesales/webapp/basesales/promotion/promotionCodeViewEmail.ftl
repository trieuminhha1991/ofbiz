<#assign idProductPromoCodeEmail = "jqxProductPromoCodeEmail">
<div id="container${idProductPromoCodeEmail}" class="container-noti"></div>
<div id="jqxNotification${idProductPromoCodeEmail}">
    <div id="notificationContent${idProductPromoCodeEmail}">
    </div>
</div>

<div id="${idProductPromoCodeEmail}"></div>

<div id="alterpopupWindowAddNewEmail" style="display:none">
	<div>${uiLabelMap.AddNewEmail}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
		    <input type="hidden" id="wn_productPromoCodeIdAE" value="${productPromoCode.productPromoCodeId}"/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_emailAddress" class="required">${uiLabelMap.BSEmailAddress}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_emailAddress"/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSaveAddEmail" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancelAddEmail" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		if (typeof(OlbProductCodeEmail) == "undefined") {
			var OlbProductCodeEmail = (function(){
				var validatorVAL;
				
				var init = function(){
					initElement();
					initElementAdvance();
					initValidateForm();
					initEvent();
				};
				var initElement = function(){
					jOlbUtil.input.create("#wn_emailAddress", {width: '96%'});
					jOlbUtil.windowPopup.create($("#alterpopupWindowAddNewEmail"), {maxWidth: 960, width: 600, height: 150, cancelButton: $("#wn_alterCancelAddEmail")});
					jOlbUtil.notification.create($("#container${idProductPromoCodeEmail}"), $("#jqxNotification${idProductPromoCodeEmail}"));
				};
				var initElementAdvance = function(){
					var datafields = [
						{name: 'productPromoCodeId', type: 'string'},
						{name: 'emailAddress', type: 'string'},
					];
					var columns = [
						{text: '${StringUtil.wrapString(uiLabelMap.BSVoucherCode)}', dataField: 'productPromoCodeId', width: '26%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BSEmailAddress)}', dataField: 'emailAddress'},
					];
					var configProductList = {
						width: '100%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						dropDownHorizontalAlignment: 'right',
						datafields: datafields,
						columns: columns,
						useUrl: true,
						root: 'results',
						url: 'JQGetListProductPromoCodeEmail&productPromoCodeId=${productPromoCode.productPromoCodeId?if_exists}',
						useUtilFunc: true,
						editable: false,
						pagesize: 10,
						showtoolbar: true,
						createUrl: "jqxGeneralServicer?sname=createProductPromoCodeEmail&jqaction=C", 
						addColumns: "productPromoCodeId;emailAddress", 
						removeUrl: "jqxGeneralServicer?sname=deleteProductPromoCodeEmail&jqaction=C", 
						deleteColumns: "productPromoCodeId;emailAddress", 
						rendertoolbar: function(toolbar){
							<@renderToolbar id=idProductPromoCodeEmail isShowTitleProperty="true" customTitleProperties="" isCollapse="false" showlist="false" 
								customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
								addrow="true" addType="popup" alternativeAddPopup="alterpopupWindowAddNewEmail" 
								deleterow="true" deleteConditionFunction="" deleteConditionMessage="" 
								virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
								updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
								customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
		                },
					};
					new OlbGrid($("#${idProductPromoCodeEmail}"), null, configProductList, []);
				};
				var initEvent = function(){
					// update the edited row when the user clicks the 'Save' button.
					$("#wn_alterSaveAddEmail").click(function () {
						if(!validatorVAL.validate()) return false;
						var row = {
							productPromoCodeId: $("#wn_productPromoCodeIdAE").val(),
							emailAddress: $("#wn_emailAddress").val()
					  	};
						$("#${idProductPromoCodeEmail}").jqxGrid('addRow', null, row, "first");
						//$("#${idProductPromoCodeEmail}").jqxGrid('updatebounddata');
						
						$("#wn_emailAddress").val("");
						
				    	$("#alterpopupWindowAddNewEmail").jqxWindow('close');
					});
				};
				var initValidateForm = function(){
					var mapRules = [
						{input: '#wn_emailAddress', type: 'validInputNotNull'},
					];
					var extendRules = [
						{input: '#wn_emailAddress', message: "${StringUtil.wrapString(uiLabelMap.BSInvalidEmail)}", action: 'keyup', rule: 'email'}
					];
					validatorVAL = new OlbValidator($("#alterpopupWindowAddNewEmail"), mapRules, extendRules, []);
				};
				return {
					init: init,
				};
			}());
		}
		
		OlbProductCodeEmail.init();
	})
</script>