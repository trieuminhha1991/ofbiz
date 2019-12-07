<div id="alterpopupWindow1" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="CreatePriorityForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSPriorityId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="priorityIdAdd" />
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSDescription}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="descriptionAdd" />
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSIdAbbrev}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="enumCodeAdd" />
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSSequenceNumber}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="sequenceIdAdd"></div>
						</div>
					</div>
				</div>
			</div>

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
		</form>
	</div>
</div>

<script type="text/javascript">
	var notEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var validateSequence = '${StringUtil.wrapString(uiLabelMap.BSValidateSequence)}';
	var addNew8 = '${StringUtil.wrapString(uiLabelMap.BSAddNewPriority)}';
</script>
<@jqOlbCoreLib />
<script type="text/javascript">
	$(function(){
		OlbSettingPrio.init();
	});
	var OlbSettingPrio = (function(){
		var initWindowPrio = (function(){
			$('#alterpopupWindow1').jqxWindow({ width: 500, height : 250,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, title: addNew8});
		});
		
		var initInput = (function(){
			jOlbUtil.input.create('#priorityIdAdd',{width: '98%', height: 24, minLength: 1});
			jOlbUtil.input.create('#descriptionAdd',{width: '98%', height: 24, minLength: 1});
			jOlbUtil.input.create('#enumCodeAdd',{width: '98%', height: 24, minLength: 1});
			$('#sequenceIdAdd').jqxNumberInput({width: '100%', height: 28, min: 1, spinButtons: true, decimalDigits: 0, inputMode: 'simple', digits: 2 });
		});
		
		var initValidate = function(){
			$('#CreatePriorityForm').jqxValidator({
				rules : [
					{input: '#priorityIdAdd', message: notEmpty, action: 'blur', 
						rule: function (input, commit) {
							var value = $(input).val();
							value = value.replace(/[^\w]/gi, '');
							var res = '';
							for(var x in value){
								res += value[x].toUpperCase();
							}
							var result = $('#priorityIdAdd').val(res);
							if(/^\s*$/.test(result)){
								return false;
							}
							return true;
						}
					},
					{input: '#descriptionAdd', message:notEmpty, action: 'blur', rule: 
						function (input, commit) {
							var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
			        {input: '#enumCodeAdd', message: notEmpty, action: 'blur', rule: 
						function (input, commit) {
							var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
					{input: '#sequenceIdAdd', message: validateSequence, action: 'blur', rule: 
						function (input, commit) {
							var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
					{input: '#sequenceIdAdd', message: validateSequence, action: 'blur', rule: 
						function (input, commit) {
							if($('#sequenceIdAdd').jqxNumberInput('getDecimal') > 0) {
								return true;
							}
							return false;
						}
					},
				]
			});
		};
		
		var eventAdd = function(){
			$('#alterSave1').click(function(){
				$('#CreatePriorityForm').jqxValidator('validate');
			});
			
			$('#CreatePriorityForm').on('validationSuccess',function(){
				var row = {};
				row = {
					enumId : $('#priorityIdAdd').val(),
					description : $('#descriptionAdd').val(),
					sequenceId : $('#sequenceIdAdd').val(),
					enumCode : $('#enumCodeAdd').val(),
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				$("#jqxgrid").jqxGrid('clearSelection');                        
				$("#jqxgrid").jqxGrid('selectRow', 0);  
				$("#alterpopupWindow1").jqxWindow('close');
			});
		};
		
		var eventClose = function(){
			$('#alterpopupWindow1').on('close',function(){
				$('#CreatePriorityForm').jqxValidator('hide');
				$('#jqxgrid').jqxGrid('refresh');
				$('#priorityIdAdd').val(null);
				$('#descriptionAdd').val(null);
				$('#enumCodeAdd').val(null);
				$('#sequenceIdAdd').val(0);
			});
		};
		
		var init = (function(){
			initWindowPrio();
			initInput();
			initValidate();
			eventAdd();
			eventClose();
		});
		
		return {
			init: init,
		}
	}());
</script>