<div id="alterpopupWindow1" style="display: none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="ChannelForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSChannelId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="channelIdA" />
						</div>
					</div>

					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSChannelCode}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="channelCodeA" />
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSDescription}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="descriptionA" />
						</div>
					</div>
				
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSSequenceNum}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="sequenceA"></div>
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
	$(function(){
		OlbChannelNewC.init();
	});
	
	var OlbChannelNewC = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initValidateForm();
			initEvent();
		};
		
		var initElement = function(){
			jOlbUtil.input.create("#channelIdA", {width: '99%'});
			jOlbUtil.input.create("#channelCodeA", {width: '99%'});
			jOlbUtil.input.create("#descriptionA", {width: '99%'});
			jOlbUtil.numberInput.create("#sequenceA", {spinButtons: false, digits: 3, decimalDigits: 0, width: '98%'});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindow1"), {maxWidth:600, width:500, height: 250, cancelButton: $("#alterCancel1")});
		};
		
		var initValidateForm = function(){
			var mapRules = [
				{input: '#channelIdA', type: 'validInputNotNull'},
				{input: '#channelIdA', type: 'validCannotSpecialCharactor'},
				{input: '#descriptionA', type: 'validInputNotNull'},
			];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#ChannelForm'), mapRules, extendRules);
		};
		
		var initEvent = function(){
			$('#alterSave1').click(function(){
				if (!validatorVAL.validate()) return false;
				
				var row = {
					enumId : $('#channelIdA').val(),
					enumCode : $('#channelCodeA').val(),
					sequenceId : $('#sequenceA').val(),
					description : $('#descriptionA').val(),
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				$("#jqxgrid").jqxGrid('clearSelection');                        
				$("#jqxgrid").jqxGrid('selectRow', 0);  
				$("#alterpopupWindow1").jqxWindow('close');
			});
			
			$('#alterpopupWindow1').on('close',function(){
				$('#jqxgrid').jqxGrid('updatebounddata');
				
				$('#channelIdA').val(null);
				$('#channelCodeA').val(null);
				$('#sequenceA').val(null);
				$('#descriptionA').val(null);
			});
		};
		
		return {init: init}
	}());
</script>