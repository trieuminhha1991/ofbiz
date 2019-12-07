<#assign idProductPromoCodeParty = "jqxProductPromoCodeParty">
<div id="container${idProductPromoCodeParty}" class="container-noti"></div>
<div id="jqxNotification${idProductPromoCodeParty}">
    <div id="notificationContent${idProductPromoCodeParty}">
    </div>
</div>

<div id="${idProductPromoCodeParty}"></div>

<div id="alterpopupWindowAddNewParty" style="display:none">
	<div>${uiLabelMap.AddNewParty}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
		    <input type="hidden" id="wn_pty_productPromoCodeId" value="${productPromoCode.productPromoCodeId}"/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_pty_partyId" class="required">${uiLabelMap.BSCustomerId}</label>
						</div>
						<div class='span7'>
							<div id="wn_pty_partyId">
								<div id="wn_pty_partyGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSaveAddParty" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancelAddParty" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		if (typeof(OlbProductCodeParty) == "undefined") {
			var OlbProductCodeParty = (function(){
				var partyIdDDB;
				var validatorVAL;
				
				var init = function(){
					initElement();
					initElementAdvance();
					initValidateForm();
					initEvent();
				};
				var initElement = function(){
					jOlbUtil.input.create("#wn_emailAddress", {width: '96%'});
					jOlbUtil.windowPopup.create($("#alterpopupWindowAddNewParty"), {maxWidth: 960, width: 600, height: 150, cancelButton: $("#wn_alterCancelAddParty")});
					jOlbUtil.notification.create($("#container${idProductPromoCodeParty}"), $("#jqxNotification${idProductPromoCodeParty}"));
				};
				var initElementAdvance = function(){
					var configInternalParty = {
						useUrl: true,
						root: 'results',
						widthButton: '99%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyCode', width: '26%'},
							{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'fullName'},
						],
						url: 'JQListPartyFullName',
						useUtilFunc: true,
						key: 'partyId', 
						keyCode: 'partyCode', 
						description: ['fullName'],
						autoCloseDropDown: true,
					};
					partyIdDDB = new OlbDropDownButton($("#wn_pty_partyId"), $("#wn_pty_partyGrid"), null, configInternalParty, []);
				
					var configProductList = {
						width: '100%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						dropDownHorizontalAlignment: 'right',
						datafields: [
							{name: 'productPromoCodeId', type: 'string'},
							{name: 'partyId', type: 'string'},
							{name: 'partyCode', type: 'string'},
							{name: 'fullName', type: 'string'},
						],
						columns: [
							{text: '${StringUtil.wrapString(uiLabelMap.BSVoucherCode)}', dataField: 'productPromoCodeId', width: '26%'},
							{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'partyCode', width: '26%'},
							{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName'},
						],
						useUrl: true,
						root: 'results',
						url: 'JQGetListProductPromoCodeParty&productPromoCodeId=${productPromoCode.productPromoCodeId?if_exists}',
						useUtilFunc: true,
						editable: false,
						pagesize: 10,
						showtoolbar: true,
						
						createUrl: "jqxGeneralServicer?sname=createProductPromoCodeParty&jqaction=C",
						addColumns: "productPromoCodeId;partyId", 
						removeUrl: "jqxGeneralServicer?sname=deleteProductPromoCodeParty&jqaction=C", 
						deleteColumns: "productPromoCodeId;partyId", 
						rendertoolbar: function(toolbar){
							<@renderToolbar id=idProductPromoCodeParty isShowTitleProperty="true" customTitleProperties="" isCollapse="false" showlist="false" 
								customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
								addrow="true" addType="popup" alternativeAddPopup="alterpopupWindowAddNewParty" 
								deleterow="true" deleteConditionFunction="" deleteConditionMessage="" 
								virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
								updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
								customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
		                },
					};
					new OlbGrid($("#${idProductPromoCodeParty}"), null, configProductList, []);
				};
				var initEvent = function(){
					// update the edited row when the user clicks the 'Save' button.
					$("#wn_alterSaveAddParty").click(function () {
						if(!validatorVAL.validate()) return false;
						var row = {
							productPromoCodeId: $("#wn_pty_productPromoCodeId").val(),
							partyId: partyIdDDB.getValue()
					  	};
						$("#${idProductPromoCodeParty}").jqxGrid('addRow', null, row, "first");
						//$("#${idProductPromoCodeParty}").jqxGrid('updatebounddata');
						
						partyIdDDB.clearAll();
						
				    	$("#alterpopupWindowAddNewParty").jqxWindow('close');
					});
				};
				var initValidateForm = function(){
					var mapRules = [
						{input: '#wn_pty_partyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					];
					var extendRules = [];
					validatorVAL = new OlbValidator($("#alterpopupWindowAddNewParty"), mapRules, extendRules, []);
				};
				return {
					init: init,
				};
			}());
		}
		
		OlbProductCodeParty.init();
	})
</script>