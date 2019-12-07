<div id="alterPopupProdPackUomNew" style="display:none">
	<div>${uiLabelMap.BSAddNewProductPackingUom}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid form-window-content-custom">
				<div class="row-fluid">
					<div class="span4">
						<label >${uiLabelMap.BSUomId}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_uomId" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSAbbreviation}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_abbreviation" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_ppu_description" value=""/>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="wn_ppu_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_ppu_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	
	$(function(){
		OlbPageProdPackingUomNew.init();
	});
	
	var OlbPageProdPackingUomNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterPopupProdPackUomNew"), {width: 450, height: 230, cancelButton: $("#wn_ppu_alterCancel")});
			
			jOlbUtil.input.create($("#wn_ppu_uomId"));
			jOlbUtil.input.create($("#wn_ppu_abbreviation"));
			jOlbUtil.input.create($("#wn_ppu_description"));
		};
		var initEvent = function(){
			<#if jqxGridId?has_content && addInnerRow?exists && addInnerRow>
			$("#wn_ppu_alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				var rowDataNew = {
					"uomId": $("#wn_ppu_uomId").val(),
					"abbreviation": $("#wn_ppu_abbreviation").val(),
					"description": $("#wn_ppu_description").val(),
				};
				$("#${jqxGridId}").jqxGrid("addRow", null, rowDataNew, "first"); 
				$("#alterPopupProdPackUomNew").jqxWindow("close");
			});
			</#if>
			
			$("#alterPopupProdPackUomNew").on("close", function(){
				$("#wn_ppu_uomId").val("");
				$("#wn_ppu_abbreviation").val("");
				$("#wn_ppu_description").val("");
			});
			$("#alterPopupProdPackUomNew").on("open", function(){
				$("#wn_ppu_uomId").focus();
			});
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: "#wn_ppu_abbreviation", type: "validInputNotNull"},
				{input: "#wn_ppu_description", type: "validInputNotNull"},
			];
			validatorVAL = new OlbValidator($("#alterPopupProdPackUomNew"), mapRules);
		};
		var openWindowProdUomNew = function(){
			$("#alterPopupProdPackUomNew").jqxWindow("open");
		};
		var closeWindowProdUomNew = function(){
			$("#alterPopupProdPackUomNew").jqxWindow("close");
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var getValue = function(){
			var rowDataNew = {
				"uomId": $("#wn_ppu_uomId").val(),
				"abbreviation": $("#wn_ppu_abbreviation").val(),
				"description": $("#wn_ppu_description").val(),
			};
			return rowDataNew;
		};
		return {
			init: init,
			openWindowProdUomNew: openWindowProdUomNew,
			closeWindowProdUomNew: closeWindowProdUomNew,
			getValidator: getValidator,
			getValue: getValue,
		}
	}());
</script>