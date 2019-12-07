<div id="alterpopupWindowCustomerMTCreateNew" style="display:none">
	<div>${uiLabelMap.BSAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSGroupId}</label>
						</div>
						<div class='span9'>
							<input type="text" id="wn_partyCode" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="wn_MTName">${uiLabelMap.BSGroupName}</label>
						</div>
						<div class='span9'>
							<input type="text" id="wn_groupName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label  >${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span9'>
							<textarea id="wn_description" ></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbCustomerMTNew.init();
	});
	var OlbCustomerMTNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initEvent();
			initValidator();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowCustomerMTCreateNew"), {width: 480, height: 230, cancelButton: $("#wn_alterCancel")});
			
			$("#wn_partyCode").jqxInput({width: "295", height: "23px", theme: theme});
			$("#wn_groupName").jqxInput({width: "295", height: "23px", theme: theme});
			$("#wn_description").jqxTextArea({width: "295", height: "46px", theme: theme});
			
		};
		var initEvent = function(){
			$('#wn_alterSave').click(function(){
				if(!validatorVAL.validate()) return false;
				
				var rowData = {
					partyCode : $('#wn_partyCode').val(),
					groupName : $('#wn_groupName').val(),
					description : $('#wn_description').val(),
				};
				$("#jqxgrid").jqxGrid('addRow', null, rowData, "first");
				$("#jqxgrid").jqxGrid('clearSelection');
				$("#jqxgrid").jqxGrid('selectRow', 0);
				$("#alterpopupWindowCustomerMTCreateNew").jqxWindow('close');
				$('#jqxgrid').jqxGrid('refresh');
				$('#jqxgrid').jqxGrid('updatebounddata');
				
			});
			
			$('#alterpopupWindowCustomerMTCreateNew').on('open', function(){
				$('#wn_partyCode').val(null);
				$('#wn_groupName').val(null);
				$('#wn_description').val(null);
			});
		};
		var initValidator = function(){
			var mapRules = [
					{input: "#wn_partyCode", type: "validCannotSpecialCharactor"},
					{input: "#wn_groupName", type: "validInputNotNull"}
				];
			validatorVAL = new OlbValidator($("#alterpopupWindowCustomerMTCreateNew"), mapRules);
		};
		
		return {
			init: init
		};
	}());
</script>