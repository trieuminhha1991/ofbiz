<style>
	#stepScroll {
        overflow: auto;
        position: relative;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxprogressbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<#assign agreementId = parameters.agreementId !>
<div id="show-Aggree">
<div>
		${uiLabelMap.EditAgreementToSendSupp}
</div>
<div class="ps-container ps-active-x ps-active-y" id="stepScroll">
	
	<div class="row-fluid">
		<div id="fuelux-wizard" class="row-fluid">
		  <ul class="wizard-steps">
			<li data-target="#step1" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.EditUpdateAgreement}</span></li>
			<li data-target="#step2"><span class="step">2</span> <span class="title">${uiLabelMap.ViewAgreement}</span></li>
			<li data-target="#step3"><span class="step">3</span> <span class="title">${uiLabelMap.SendAccountant}</span></li>
		  </ul>
		</div>
		
		<hr />
		
		<div class="step-content row-fluid position-relative">
			<div class="step-pane active" id="step1">
				${screens.render("component://delys/widget/import/ImportScreens.xml#EditPurchaseAgreement2")}
			</div>
			
			<div class="step-pane" id="step2">
				<div class="row-fluid">
					<div id="test">
					</div>
				</div>
			</div>
			
			<div class="step-pane" id="step3" style="height: 325px;">
				<div id="emailSuccess"></div>
				<div class="center" style="margin-left: 0px!important;">
					<h3 class="blue lighter" style="padding-left: 100px;float: left;">${uiLabelMap.sendAgreementForAcc}</h3>
					<div class='row-fluid'>
						<div class='span12 no-left-margin'>
							<div class='span2 align-right'><label style="margin-top: 4px;">${uiLabelMap.dateSend}<span style="color:red;"> *</span></label></div>
							<div class='span8'><div id='ngayGui'></div></div>
						</div>
					</div>
					<input type="hidden" id="agrIdAcc"/>
				</div>
			</div>
		</div>
		
		<hr />
		
		<div class="row-fluid wizard-actions">
			<button class="btn btn-prev"><i class="icon-arrow-left"></i> Prev</button>
			<button class="btn btn-success btn-next" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
		</div>
	</div>

</div>

</div>

<script type="text/javascript">


$(function() {

	var agr= '';
	var $validation = false;
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	if(info.step == 1) {
		$('#step1').jqxValidator('validate');
		if ($('#step1').jqxValidator('validate')) {
			var rows = $('#jqxgridProductAgreement').jqxGrid('getRows');
			var orderItems = [];
			for(var i = 0; i < rows.length; i++){
				var num = rows[i].unitPrice;
				var numLocale = num.toLocaleString('en');
				var row = {orderItemSeqId: rows[i].orderItemSeqId, orderId: rows[i].orderId, unitPrice: String((rows[i].unitPrice).toLocaleString('${localeStr}')), lotQuantity: rows[i].lotQuantity.toString(), productId: rows[i].productId, amount: String((rows[i].goodValue).toLocaleString('${localeStr}'))};
				orderItems.push(row);
			}
	// var agreementId = $("input[name=agreementId]").val();
			var agreementId = $("#EditPurchaseAgreement2_agreementId").val();
			var agreementTypeId = $("input[name=agreementTypeId]").val();
// $('#packingListDate').jqxDateTimeInput('getDate').getTime()
// var agreementDate = $("input[name=agreementDate]").val();
			var agreementDate = $("#EditPurchaseAgreement2_agreementDate").jqxDateTimeInput('getDate').getTime();
			var agreementName = $("input[name=agreementName]").val();
// var fromDate = $("input[name=fromDate]").val();
			var fromDate = $("#EditPurchaseAgreement2_fromDate").jqxDateTimeInput('getDate').getTime();
// var thruDate = $("input[name=thruDate]").val();
			var thruDate = $("#EditPurchaseAgreement2_thruDate").jqxDateTimeInput('getDate').getTime();
			var weekETD = $("input[name=weekETD]").val();
			var partyIdFrom = "";
			var itemFrom = $('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList('getSelectedItem');
			if(itemFrom){
				partyIdFrom = itemFrom.value;
			}
			
			var roleTypeIdFrom = $('#EditPurchaseAgreement2_roleTypeIdFrom').val();

			var representPartyIdFrom = "";
			var representFrom = $('#EditPurchaseAgreement2_representPartyIdFrom').jqxDropDownList('getSelectedItem');
			if(representFrom){
				representPartyIdFrom = representFrom.value;
			}
			
			var addressIdFrom = "";
			var addressFrom = $('#EditPurchaseAgreement2_addressIdFrom').jqxDropDownList('getSelectedItem');
			if(addressFrom){
				addressIdFrom = addressFrom.value;
			}
			
			var telephoneIdFrom = "";
			var telephoneFrom = $('#EditPurchaseAgreement2_telephoneIdFrom').jqxDropDownList('getSelectedItem');
			if(telephoneFrom){
				telephoneIdFrom = telephoneFrom.value;
			}
			
			var faxNumberIdFrom = "";
			var faxNumberFrom = $('#EditPurchaseAgreement2_faxNumberIdFrom').jqxDropDownList('getSelectedItem');
			if(faxNumberFrom){
				faxNumberIdFrom = faxNumberFrom.value;
			}
			
			var finAccountIdFroms = "";
			var finAccountFrom = $('#EditPurchaseAgreement2_finAccountIdFroms').jqxDropDownList('getSelectedItem');
			if(finAccountFrom){
				finAccountIdFroms = finAccountFrom.value;
			}
			
			var partyIdTo = "";
			var partyTo = $('#EditPurchaseAgreement2_partyIdTo').jqxDropDownList('getSelectedItem');
			if(partyTo){
				partyIdTo = partyTo.value;
			}
			
			var roleTypeIdTo = $('#EditPurchaseAgreement2_roleTypeIdTo').val();
			
			var addressIdTo = "";
			var addressTo = $('#EditPurchaseAgreement2_addressIdTo').jqxDropDownList('getSelectedItem');
			if(addressTo){
				addressIdTo = addressTo.value;
			}
			
			var finAccountIdTos = "";
			var finAccountTo = $('#EditPurchaseAgreement2_finAccountIdTos').jqxDropDownList('getSelectedItem');
			if(finAccountTo){
				finAccountIdTos = finAccountTo.value;
			}
			
			var currencyUomIds = "";
			var currencyUomTo = $('#EditPurchaseAgreement2_currencyUomIds').jqxDropDownList('getSelectedItem');
			if(currencyUomTo){
				currencyUomIds = currencyUomTo.value;
			}
			var emailAddressIdTo ="";
			var emailAddressTo = $('#EditPurchaseAgreement2_emailAddressIdTo').jqxDropDownList('getSelectedItem');
			if(emailAddressTo){
				emailAddressIdTo = emailAddressTo.value;
			}
			
			var description = $("input[name=description]").val();
// var textData = $("input[name=textData]").val();
			var lotId = $("input[name=lotId]").val();
			var productPlanId = $('#EditPurchaseAgreement2_productPlanId').val();
			
			var portOfDischargeId = "";
			var portOfDischarge = $('#EditPurchaseAgreement2_portOfDischargeId').jqxDropDownList('getSelectedItem');
			if(portOfDischarge){
				portOfDischargeId = portOfDischarge.value;
			}
			
// var facilityId = $("select[name=facilityId]").val();
// var productStoreId = $("select[name=productStoreId]").val();
			var contactMechId = "";
			var contactMech = $('#EditPurchaseAgreement2_contactMechId').jqxDropDownList('getSelectedItem');
			if(contactMech){
				contactMechId = contactMech.value;
			}
			
			var transshipment = "";
			var transshipmentSl = $('#EditPurchaseAgreement2_transshipment').jqxDropDownList('getSelectedItem');
			if(transshipmentSl){
				transshipment = transshipmentSl.value;
			}
			
			var partialShipment = "";
			var partialShipmentSl = $('#EditPurchaseAgreement2_partialShipment').jqxDropDownList('getSelectedItem');
			if(partialShipmentSl){
				partialShipment = partialShipmentSl.value;
			}
			
			var statusId = $("input[name=statusId]").val();
			if(agreementName == null || agreementName == '') return false;
			onLoadData();
		// $('#test').html('<h4>'+productPlanId+'</h4></hr><h6>'+agreementName+'</h6>');
			$.ajax({
				url: 'updatePurchaseAgreement2',
		    	type: "POST",
		    	data: {agreementId: agreementId, agreementTypeId: agreementTypeId, agreementDate:agreementDate, fromDate: fromDate, thruDate: thruDate,
		    		weekETD: weekETD, partyIdFrom: partyIdFrom, roleTypeIdFrom: roleTypeIdFrom, representPartyIdFrom: representPartyIdFrom, addressIdFrom: addressIdFrom,
		    		telephoneIdFrom: telephoneIdFrom, faxNumberIdFrom: faxNumberIdFrom, finAccountIdFroms: finAccountIdFroms, partyIdTo: partyIdTo, roleTypeIdTo: roleTypeIdTo,
		    		addressIdTo: addressIdTo, finAccountIdTos: finAccountIdTos, currencyUomIds: currencyUomIds, emailAddressIdTo: emailAddressIdTo, description: description,
		    		lotId: lotId, agreementName: agreementName, productPlanId: productPlanId, portOfDischargeId: portOfDischargeId,
		    		contactMechId: contactMechId, transshipment: transshipment, partialShipment: partialShipment, statusId: "AGREEMENT_CREATED", orderItems: JSON.stringify(orderItems)
		    	},
		    	async: false,
		    	success: function(data) {
		    		$('#test').html(data);
		    	},
		    	error: function(data){
		    		$('#test').html(data);
		    	}
				}).done(function() {
			    	onLoadDone();
				});
		}else {
			return false;
		}
	}
	
	if(info.step == 2){
		var agr = $('#agrId').val();
		$('#agrIdAcc').val(agr);
		$("#stepScroll").scrollTop(0);
	}
	}).on('finished', function(e) {
		$("#stepScroll").scrollTop(0);
		$('#step3').jqxValidator('validate');
		if ($('#step3').jqxValidator('validate')) {
			var agrAcc = $('#agrIdAcc').val();
			var header = "${StringUtil.wrapString(uiLabelMap.ImportAgreement)} " + agrAcc + " ${StringUtil.wrapString(uiLabelMap.ImportNeedapproval)}";
			var openTime = $('#ngayGui').jqxDateTimeInput('getDate');
			createQuotaNotification2(agrAcc, "DELYS_ACCOUNTANTS", header, openTime);
		}
	});

});
$('#step1').jqxValidator({
    rules: [
				{ input: '#EditPurchaseAgreement2_agreementName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' }
				]
});
$('#step3').jqxValidator({
	rules: [
			{ input: '#ngayGui', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
				rule: function (input, commit) {
					var value = $("#ngayGui").val().toMilliseconds();
					if (value > 0) {
						return true;
					}
					return false;
				}
			}
	        ]
});
$("#ngayGui").jqxDateTimeInput({theme: "olbius"});
$("#ngayGui").jqxDateTimeInput('val', null);
function createQuotaNotification2(agreementId, roleTypeId, messages, openTime) {
	// HEADOFCOM
		var targetLink = "agreementId=" + agreementId;
		var action = "getPendingAgreements";
		var header = messages;
		openTime = getDateTimestamp(openTime);
		var jsonObject = {roleTypeId: roleTypeId,
							header: header,
							openTime: openTime,
							action: action,
							targetLink: targetLink};
		jQuery.ajax({
	        url: "createNotification",
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	
	        }
	    }).done(function() {
	    	onLoadDone();
	    	var message = "<div id='contentMessages' class='alert alert-success'>" +
			"<p id='thisP' onclick='hiddenClick()'>" + '${uiLabelMap.SendSuccess}' + "</p></div>";
	    	$("#emailSuccess").html(message);
	    	setTimeout(function() {
	    		$('#show-Aggree').jqxWindow('close');
			}, 1000);
		});
	}
	function getDateTimestamp(dateNotify) {
		var getFullYear = dateNotify.getFullYear();
		var getDate = dateNotify.getDate();
		var getMonth = dateNotify.getMonth() + 1;
		if (getDate < 10) {
			getDate = '0' + getDate;
		}
		if (getMonth < 10) {
			getMonth = '0' + getMonth;
		}
		dateNotify = getFullYear + '-' + getMonth + '-' + getDate;
		return dateNotify;
	}
	$('#show-Aggree').jqxWindow({
	    showCollapseButton: false, theme:'olbius', resizable: false,
	    isModal: true, autoOpen: false, height: 600, width: 1200, maxWidth: '90%', position: 'center', modalOpacity: 0.7
	});
	var wtmp = window;
	var tmpwidth = $('#show-Aggree').jqxWindow('width');
	// $('#show-Aggree').jqxWindow({
	  // position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
	// });
	
	$('#show-Aggree').on('close', function (event) {
		$('#pos-show-hold-cart').css('display','block');
		$('#step1').jqxValidator('hide');
		$('#step3').jqxValidator('hide');
		$('#show-Aggree').jqxWindow('destroy');
		// $('#pos-show-hold-cart').modal('show');
		$('#jqxgridContainerWeek').jqxGrid('updateBoundData');
		
	});
	$('#show-Aggree').on('open', function() {
// $('#pos-show-hold-cart').jqxWindow('open');
		<#if agreementId == '0'>
			$('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList('selectIndex', 0);
			$('#EditPurchaseAgreement2_partyIdTo').jqxDropDownList('selectIndex', 0);
			$('#EditPurchaseAgreement2_portOfDischargeId').jqxDropDownList('selectIndex', 0);
			$('#EditPurchaseAgreement2_partialShipment').jqxDropDownList('selectIndex', 0);
			$('#EditPurchaseAgreement2_transshipment').jqxDropDownList('selectIndex', 0);
			<#else>
				$("#EditPurchaseAgreement2_partyIdFrom").jqxDropDownList('selectItem','${partyIdFrom}');
				$("#EditPurchaseAgreement2_partyIdTo").jqxDropDownList('selectItem','${partyIdTo}');
				$('#EditPurchaseAgreement2_portOfDischargeId').jqxDropDownList('selectItem', '${currentPortTerm}');
				$('#EditPurchaseAgreement2_transshipment').jqxDropDownList('selectItem', '${transshipment}');
				$('#EditPurchaseAgreement2_partialShipment').jqxDropDownList('selectItem', '${partialShipment}');
		</#if>
//		$('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('val', null);
//		$('#EditPurchaseAgreement2_agreementId').val('${agreementId}');
	});

// $('#show-Aggree').on('hide', function(){
	// $('#pos-show-hold-cart').modal('show');
// });

$(document).ready(function(){
	$('#stepScroll').perfectScrollbar({
      wheelSpeed: 1,
      wheelPropagation: false
    });
});
</script>
