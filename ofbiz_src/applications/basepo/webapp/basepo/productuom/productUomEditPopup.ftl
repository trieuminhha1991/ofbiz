<div id="alterPopupProdPackUomEdit" style="display:none">
	<div>${uiLabelMap.BSEditProductPackingUom}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid form-window-content-custom">
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSUomId}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_uomId_edit" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSAbbreviation}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_abbreviation_edit" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_description_edit" value=""/>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="wn_ppu_alterSaveEdit" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_ppu_alterCancelEdit" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	
	$(function(){
		OlbPageProdPackingUomEdit.init();
	});
	
	var OlbPageProdPackingUomEdit = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterPopupProdPackUomEdit"), {width: 450, height: 230, cancelButton: $("#wn_ppu_alterCancel_edit")});
			
			jOlbUtil.input.create($("#wn_ppu_uomId_edit"));
			jOlbUtil.input.create($("#wn_ppu_abbreviation_edit"));
			jOlbUtil.input.create($("#wn_ppu_description_edit"));
		};
		var initEvent = function(){
			$("#wn_ppu_alterSaveEdit").on("click", function(){
				if (!validatorVAL.validate()) return false;
				var rowDataEdit = {
					"uomId": $("#wn_ppu_uomId_edit").val(),
					"abbreviation": $("#wn_ppu_abbreviation_edit").val(),
					"description": $("#wn_ppu_description_edit").val(),
				};
				$("#${jqxGridId}").jqxGrid('updaterow', rowDataEdit.uomId, rowDataEdit);
				$("#${jqxGridId}").jqxGrid("clearSelection");                        
				$("#alterPopupProdPackUomEdit").jqxWindow("close");
			});
			
			$("#alterPopupProdPackUomEdit").on("close", function(){
				$("#wn_ppu_uomId_edit").val("");
				$("#wn_ppu_abbreviation_edit").val("");
				$("#wn_ppu_description_edit").val("");
			});
			$("#alterPopupProdPackUomEdit").on("open", function(){
				$("#wn_ppu_uomId_edit").focus();
			});
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: "#wn_ppu_abbreviation_edit", type: "validInputNotNull"},
				{input: "#wn_ppu_description_edit", type: "validInputNotNull"},
			];
			validatorVAL = new OlbValidator($("#alterPopupProdPackUomEdit"), mapRules);
		};
		var openWindowProdUomEdit = function(){
			$("#alterPopupProdPackUomEdit").jqxWindow("open");
		};
		var closeWindowProdUomEdit = function(){
			$("#alterPopupProdPackUomEdit").jqxWindow("close");
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var getValue = function(){
			var rowDataEdit = {
				"uomId": $("#wn_ppu_uomId_id").val(),
				"abbreviation": $("#wn_ppu_abbreviation_id").val(),
				"description": $("#wn_ppu_description_id").val(),
			};
			return rowDataEdit;
		};
		return {
			init: init,
			openWindowProdUomEdit: openWindowProdUomEdit,
			closeWindowProdUomEdit: closeWindowProdUomEdit,
			getValidator: getValidator,
			getValue: getValue,
		}
	}());
</script>