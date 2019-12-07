<div id="copyPopupWindow" style="display:none;">
    <div>${uiLabelMap.PageTitleCopyAgreement}</div>
    <div style="overflow: hidden;">
    	<div style='width: 400px; margin:0 auto;'>
        	<table style = "margin: auto;">
        		<tr>
        			<td align="right">${uiLabelMap.DAAgreementId}:</td>
 					<td align="left">
 						<input id='agreementIdCopy' style='margin-left: 10px; float: left;'></input>
 					</td>
	 			</tr>
	 			<tr>
	 				<td align="right">${uiLabelMap.AccountingAgreementTerms}:</td>
 					<td align="left">
 						<div id='copyAgreementTerms' style='margin-left: 10px; float: left; margin-left: 10px !important;'></div>
 					</td>
	 			</tr>
	 			<tr>
	 				<td align="right">${uiLabelMap.ProductProducts}:</td>
 					<td align="left">
 						<div id='copyAgreementProducts' style='margin-left: 10px; float: left; margin-left: 10px !important;'></div>
 					</td>
	 			</tr>
	 			<tr>
	 				<td align="right">${uiLabelMap.Party}:</td>
 					<td align="left">
 						<div id='copyAgreementParties' style='margin-left: 10px; float: left; margin-left: 10px !important;'></div>
 					</td>
	 			</tr>
	 			<tr>
	 				<td align="right">${uiLabelMap.ProductFacilities}:</td>
 					<td align="left">
 						<div id='copyAgreementFacilities' style='margin-left: 10px; float: left; margin-left: 10px !important;'></div>
 					</td>
	 			</tr>
	 			<tr>
	 				<td align="right"><input type="button" id="alterCancel" value="${uiLabelMap.CommonCancel}" /></td>
 					<td align="left"><input type="button" id="alterCopy" value="${uiLabelMap.CommonCopy}" /></td>
	 			</tr>
	 		</table>	
    	</div>
    </div>
</div>
<script>
	//Create agreementIdCopy
	$("#agreementIdCopy").jqxInput({ width: 120, height: 25, disabled: true});
	
	//Create copyAgreementTerms
	$("#copyAgreementTerms").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementProducts
	$("#copyAgreementProducts").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementParties
	$("#copyAgreementParties").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementFacilities
	$("#copyAgreementFacilities").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create Copy popup
	$("#copyPopupWindow").jqxWindow({
       width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7         
    });
    
    $("#alterCancel").jqxButton();
    $("#alterCopy").jqxButton();
    
    function copyAgreement(agreementId){
    	agreementId
    	$('#agreementIdCopy').val(agreementId);
    	$('#copyPopupWindow').jqxWindow('open');
    }
    
    // update the edited row when the user clicks the 'Save' button.
    $("#alterCopy").click(function () {
    	var agreementId = $('#agreementIdCopy').val();
    	var copyAgreementTerms = $('#copyAgreementTerms').val();
    	var copyAgreementProducts = $('#copyAgreementProducts').val();
    	var copyAgreementParties = $('#copyAgreementParties').val();
    	var copyAgreementFacilities = $('#copyAgreementFacilities').val();
    	var request = $.ajax({
		  	url: "copyAgreement",
		  	type: "POST",
		  	data: {agreementId : agreementId, copyAgreementTerms: copyAgreementTerms, copyAgreementProducts: copyAgreementProducts, copyAgreementParties: copyAgreementParties, copyAgreementFacilities: copyAgreementFacilities},
		  	dataType: "html"
		});
		$("#jqxgrid").jqxGrid('updatebounddata');
		request.done(function(data) {
		  	if(data.responseMessage == "error"){
            	$('#jqxNotification').jqxNotification({ template: 'error'});
            	$("#jqxNotification").text(data.errorMessage);
            	$("#jqxNotification").jqxNotification("open");
            }else{
            	$('#container').empty();
            	$('#jqxNotification').jqxNotification({ template: 'info'});
            	$("#jqxNotification").text("Thuc thi thanh cong!");
            	$("#jqxNotification").jqxNotification("open");
            }
		});
		request.fail(function(jqXHR, textStatus) {
		  	alert( "Request failed: " + textStatus );
		});
        $("#copyPopupWindow").jqxWindow('close');
    });
</script>