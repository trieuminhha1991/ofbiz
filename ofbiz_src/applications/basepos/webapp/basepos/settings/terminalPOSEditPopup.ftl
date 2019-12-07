<div id="alterpopupWindowEdit" style="display : none;">
	<div>
		${uiLabelMap.CommonEdit}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSTerminalId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="terminalIdEdit" class="span12" maxlength="20" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSTerminalName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="terminalNameEdit" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSProductStore}</label>
						</div>
						<div class='span7'>
							<div id="productStoreIdEdit"><div id="productStoreGridEdit"></div></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSFacilityId}</label>
						</div>
						<div class='span7'>
							<div id="facilityIdEdit"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'>
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
		OlbTerminalPOSEdit.init();
	});
	var OlbTerminalPOSEdit = (function(){
		var facilityEditCBB;
		var productStoreDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create("#terminalIdEdit", {disabled: true});
			jOlbUtil.input.create("#terminalNameEdit");
			jOlbUtil.windowPopup.create($("#alterpopupWindowEdit"), {width: 520, height : 260, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7});
		};
		var initComplexElement = function(){
			var configFacility = {
				width: '99%',
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListFacilityByStore&productStoreId=' + $("#productStoreIdAdd").val(),
				key: 'facilityId',
				value: 'facilityName',
				autoDropDownHeight: true,
			}
			facilityDDL = new OlbDropDownList($("#facilityIdEdit"), null, configFacility, []);
			
			/*var configStore = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: false,
				key: 'productStoreId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex: -1,
				width:'99%'
			}
			new OlbDropDownList($("#productStoreIdEdit"), productStoreData, configStore, []);
			*/
			
			var configProductStore = {
				widthButton: '99%',
				width: '600px',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				datafields: [
					{name: 'productStoreId', type: 'string'},
					{name: 'storeName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}', datafield: 'productStoreId', width: '25%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSStoreName)}', datafield: 'storeName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQGetListProductStorePosByOrg',
				useUtilFunc: false,
				key: 'productStoreId',
				description: ['storeName'],
				autoCloseDropDown: true,
				displayDetail: true,
			};
			productStoreDDB = new OlbDropDownButton($("#productStoreIdEdit"), $("#productStoreGridEdit"), null, configProductStore, null);
		};
		var initEvent = function(){
			/*$('#productStoreIdEdit').on('select', function (event){
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var value = item.value;
				    facilityDDL.updateSource('jqxGeneralServicer?sname=JQGetListFacilityByStore&productStoreId=' + value);
				}                        
			});*/
			productStoreDDB.getGrid().rowSelectListener(function(rowData){
				if (rowData) {
					facilityDDL.updateSource('jqxGeneralServicer?sname=JQGetListFacilityByStore&productStoreId=' + rowData.productStoreId);
				}
			});
			
			$("#alterSave2").click(function () {
				if (!validatorVAL.validate()) return false;
				
				editTerminalPOS();
				$("#alterpopupWindowEdit").jqxWindow("hide");
				$("#alterpopupWindowEdit").jqxWindow("close");
			});
			
			$("#alterpopupWindowEdit").on("close", function () {
				validatorVAL.hide();
				clearValue();
			});
		};
		
		var setValue = function(data){
			if (data.posTerminalId != null) $("#terminalIdEdit").val(data.posTerminalId);
			if (data.terminalName != null) $("#terminalNameEdit").val(data.terminalName);
			if (data.productStoreId != null) {
				productStoreDDB.selectItem([data.productStoreId]);
				if (data.facilityId != null) {
					facilityDDL.updateSource('jqxGeneralServicer?sname=JQGetListFacilityByStore&productStoreId=' + data.productStoreId, null, function(){
						facilityDDL.selectItem([data.facilityId]);
					});
				}
			}
		};
		
		function clearValue() {
			$("#terminalIdEdit").val(null);
			$("#terminalNameEdit").val(null);
			$("#facilityIdEdit").jqxDropDownList("clearSelection"); 
			productStoreDDB.clearAll();
		}
		
		function editTerminalPOS() {
			var row = $("#jqxgrid").jqxGrid("getselectedrowindexes");
			var success = "${StringUtil.wrapString(uiLabelMap.BPOSUpdateSucess)}";
			var data3 = $("#jqxgrid").jqxGrid("getrowdata", row);
			var map = {};
			map["posTerminalId"] = data3.posTerminalId;
			map["terminalName"] = $("#terminalNameEdit").val();
			map["facilityId"] = $("#facilityIdEdit").val();
			jQuery.ajax({
				url: "editTerminalPOS",
				type: "POST",
				data: map,
				success: function (res) {
					var message = "";
					var template = "";
					if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
						if (res._ERROR_MESSAGE_LIST_) {
							message += res._ERROR_MESSAGE_LIST_;
						}
						if (res._ERROR_MESSAGE_) {
							message += res._ERROR_MESSAGE_;
						}
						template = "error";
					}else{
						message = success;
						template = "success";
						$("#jqxgrid").jqxGrid("updatebounddata");
						$("#jqxgrid").jqxGrid("clearselection");
					}
					updateGridMessage("jqxgrid", template, message);
				}
			});
		}
		
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#terminalIdEdit', type: 'validInputNotNull'},
					{input: '#terminalIdEdit', type: 'validCannotSpecialCharactor'},
					{input: '#terminalNameEdit', type: 'validInputNotNull'},
					{input: '#productStoreIdEdit', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#facilityIdEdit', type: 'validObjectNotNull', objType: 'dropDownList'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowEdit'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		
		return {
			init: init,
			setValue: setValue,
		}
	}());
</script>