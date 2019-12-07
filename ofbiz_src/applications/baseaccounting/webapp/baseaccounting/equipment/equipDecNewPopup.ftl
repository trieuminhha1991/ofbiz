<div id="newEquipDec" style="display:none">
	<div>${uiLabelMap.BACCNewEquipDecrement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewDepPeriod">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label >${uiLabelMap.BACCDecreasedDate}</label>
							</div>
							<div class='span7'>
								<div id="decreasedDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCDescription}</label>
							</div>
							<div class='span7'>
								<textarea id="comments" class="text-popup" style="width: 78% !important"></textarea>
					   		</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBNewEquipDec = function(){
	}
	OLBNewEquipDec.initWindow = function(){
		$("#newEquipDec").jqxWindow({
			width: '600', height: 220, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				$("#decreasedDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: THEME});
			}
		});
		OLBNewEquipDec.bindEvent();
	}
	
	OLBNewEquipDec.openWindow = function(){
		$("#newEquipDec").jqxWindow('open');
	}
	
	OLBNewEquipDec.closeWindow = function(){
		$("#newEquipDec").jqxWindow('close');
	}
	
	OLBNewEquipDec.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var submitedData = {};
			submitedData['decrementTypeId'] = 'EQUIP_DECREMENT';
			var decreasedDate = ($('#decreasedDate').jqxDateTimeInput('getDate'));
			submitedData['decreasedDate'] = accutils.getTimestamp(decreasedDate);
			submitedData['description'] = $('#comments').val();
			//Send Ajax Request
			$.ajax({
				url: 'createDecrement',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBNewEquipDec.closeWindow();
						$('#equipDecGrid').jqxGrid('updatebounddata');
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		});
		
		$("#newEquipDec").on('close',function(){
			Grid.clearForm($("#newEquipDec"));
		})
	}
	
	$(function(){
		OLBNewEquipDec.initWindow();
	})
</script>