<div id="alterpopupWindow1" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSTerminalId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="terminalIdAdd" class="span12" maxlength="20" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSTerminalName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="terminalNameAdd" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSSalesChannel}</label>
						</div>
						<div class='span7'>
							<div id="productStoreIdAdd"><div id="productStoreGridAdd"></div></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BPOSFacilityId}</label>
						</div>
						<div class='span7'>
							<div id="facilityIdAdd"></div>
				   		</div>
					</div>
				</div><!-- .span12 -->
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'>
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
		OlbTerminalPOSNew.init();
	});
	var OlbTerminalPOSNew = (function(){
		var facilityCBB;
		var productStoreDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create("#terminalIdAdd");
			jOlbUtil.input.create("#terminalNameAdd");
			jOlbUtil.windowPopup.create($("#alterpopupWindow1"), {width: 540, height : 260, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7});
		};
		var initComplexElement = function(){
			var configFacility = {
				width: '99%',
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: true,
				url: '',
				key: 'facilityId',
				value: 'facilityName',
				autoDropDownHeight: true,
			}
			facilityCBB = new OlbDropDownList($("#facilityIdAdd"), null, configFacility, []);
			
			/*var configStore = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: false,
				key: 'productStoreId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex: -1,
				width:'99%'
			}
			new OlbDropDownList($("#productStoreIdAdd"), productStoreData, configStore, []);
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
			productStoreDDB = new OlbDropDownButton($("#productStoreIdAdd"), $("#productStoreGridAdd"), null, configProductStore, null);
		};
		var initEvent = function(){
			productStoreDDB.getGrid().rowSelectListener(function(rowData){
				if (rowData) {
					facilityCBB.updateSource('jqxGeneralServicer?sname=JQGetListFacilityByStore&productStoreId=' + rowData.productStoreId);
				}
			});
			
			$("#alterSave1").click(function () {
				validatorVAL.validate();
			});
			
			$("#alterpopupWindow1").on("validationSuccess",function () {
				var row = {
					posTerminalId : $("#terminalIdAdd").val(), 
					terminalName : $("#terminalNameAdd").val(),
					facilityId : $("#facilityIdAdd").val()
				};
				$("#jqxgrid").jqxGrid("addRow", null, row, "first");
				$("#alterpopupWindow1").jqxWindow("close");
			});
			
			$("#alterpopupWindow1").on("open", function () {
				$("#terminalIdAdd").jqxInput("focus");
			});
			
			$("#alterpopupWindow1").on("close", function () {
				validatorVAL.hide();
				$("#jqxgrid").jqxGrid("refresh");
				$("#terminalIdAdd").val(null);
				$("#terminalNameAdd").val(null);
				$("#facilityIdAdd").jqxDropDownList("clearSelection"); 
				productStoreDDB.clearAll();
			});
		};
		
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#terminalIdAdd', type: 'validInputNotNull'},
					{input: '#terminalIdAdd', type: 'validCannotSpecialCharactor'},
					{input: '#terminalNameAdd', type: 'validInputNotNull'},
					{input: '#productStoreIdAdd', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#facilityIdAdd', type: 'validObjectNotNull', objType: 'dropDownList'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindow1'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		
		return {init: init}
	}());
</script>