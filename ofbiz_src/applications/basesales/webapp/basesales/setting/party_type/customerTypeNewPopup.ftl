<div id="alterpopupWindowCustomerTypeNew" style="display : none;">
	<div>${uiLabelMap.BSAddNewCustomerType}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCustomerTypeId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_partyTypeId" class="span12" maxlength="20" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCustomerTypeName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_description" class="span12" value=""/>
				   		</div>
					</div>
				</div><!--.span12-->
			</div>
			
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_alterCancel" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="wn_alterSave" class='btn btn-primary form-action-button pull-right'>
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
		OlbCustomerTypeNew.init();
	});
	var OlbCustomerTypeNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initEvent();
			initValidator();
		};
		var initElement = function(){
			jOlbUtil.input.create("#wn_partyTypeId", {width:'93%', height: 26});
			jOlbUtil.input.create("#wn_description", {width:'93%', height:26});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowCustomerTypeNew"), {width: 520, height : 175, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#wn_alterCancel"), modalOpacity: 0.7});
		};
		var initEvent = function(){
			$('#wn_alterSave').click(function(){
				if(!validatorVAL.validate()) return false;
				
				var rowData = {
					partyTypeId : $('#wn_partyTypeId').val(),
					description : $('#wn_description').val(),
				};
				$("#jqxgridCustomerType").jqxGrid('addRow', null, rowData, "first");
				$("#jqxgridCustomerType").jqxGrid('clearSelection');
				$("#jqxgridCustomerType").jqxGrid('selectRow', 0);
				
				$("#alterpopupWindowCustomerTypeNew").jqxWindow('close');
				$('#jqxgridCustomerType').jqxGrid('updatebounddata');
				$('#wn_partyTypeId').val(null);
				$('#wn_description').val(null);
			});
			
			$('#alterpopupWindowCustomerTypeNew').on('close', function(){
				$('#alterpopupWindowCustomerTypeNew').jqxValidator('hide');
			});
		};
		var initValidator = function(){
			var mapRules = [
					{input: "#wn_partyTypeId", type: "validCannotSpecialCharactor"},
					{input: "#wn_partyTypeId", type: "validInputNotNull"},
					{input: "#wn_description", type: "validInputNotNull"}
				];
			validatorVAL = new OlbValidator($("#alterpopupWindowCustomerTypeNew"), mapRules);
		};
		
		return {init: init}
	}());
</script>