<div id="alterpopupMessage" style="display:none;">
    <div>${uiLabelMap.BSDisplayErrorMessageImportData}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
				<div class='row-fluid margin-bottom10'>
					<div class='span12'>
					    <table id="displayMessageErrorId" style="margin-left: 20px;"></table>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="btnCancelMessagePopup" onclick="resetTabelMessage()" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
    $(document).ready(function() {
		$("#alterpopupMessage").jqxWindow({
            width: 600, height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#btnCancelMessagePopup"), modalOpacity: 0.7, modalZIndex: 1000, theme: 'olbius', position: 'absolute'
        });

        $('#alterpopupMessage').on('close', function (event) {
            resetTabelMessage();
        });
	});

	var resetTabelMessage = function(){
        $("#displayMessageErrorId").children().remove();
    }
</script>