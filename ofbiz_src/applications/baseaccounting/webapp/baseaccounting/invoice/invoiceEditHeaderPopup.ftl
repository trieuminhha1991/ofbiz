<#include "script/invoiceData.ftl" />
<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BACCEditHeader}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerInvoice" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationInvoice" style="margin-bottom:5px">
		        <div id="notificationContentInvoice">
		        </div>
		    </div>
			<div class="row-fluid">
				<form id="formEditInvoice">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label style="font-weight : bold;">${uiLabelMap.BACCInvoiceId}</label>
							</div>
							<div class='span7'>
								<div id="invoiceId" class="label-display"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label style="font-weight : bold;">${uiLabelMap.BACCInvoiceTypeId}</label>
							</div>
							<div class='span7'>
								<div id="invoiceTypeId" class="label-display"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label style="font-weight : bold;">${uiLabelMap.BACCStatusId}</label>
							</div>
							<div class='span7'>
								<div id="statusId" class="label-display"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCInvoiceDate}</label>
							</div>
							<div class='span7'>
								<div id="invoiceDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCPaidDate}</label>
							</div>
							<div class='span7'>
								<div id="paidDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCDueDate}</label>
							</div>
							<div class='span7'>
								<div id="dueDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCInvoiceFromParty}</label>
							</div>
							<div class='span7'>
								<div id="partyIdFrom">
									<div id="partyFromGrid"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCInvoiceToParty}</label>
							</div>
							<div class='span7'>
								<div id="partyIdTo">
									<div id="partyToGrid"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCCurrencyUom}</label>
							</div>
							<div class='span7'>
								<div id="currencyUomId"></div>
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
	   			<button id="alterSaveHeader" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancelHeader" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBEditInv = function(){
	}
	OLBEditInv.prototype.initWindow = function(){
		$("#alterpopupWindow").jqxWindow({
			minWidth: 700,width : 700, maxWidth: 1200, height: 550, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancelHeader"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				var urlFrom = "JqxGetOrganizations";
				var urlTo = "JqxGetParties"
				if('${businessType}' == 'AP'){
					var urlFrom = "JqxGetParties";
					var urlTo = "JqxGetOrganizations"
				}
				var configPartyFrom = {
						useUrl: true,
						root: 'results',
						widthButton: '80%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						],
						url: urlFrom,
						useUtilFunc: true,
						pagesize  : 5,
						key: 'partyId',
						description: ['fullName'],
				};
				accutils.initDropDownButton($("#partyIdFrom"), $("#partyFromGrid"), null, configPartyFrom, []);
				<#assign partyNameFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom, false) />
				accutils.setValueDropDownButtonOnly($("#partyIdFrom"), '${invoice.partyIdFrom}', '${partyNameFrom} [${invoice.partyIdFrom}]');
				var configPartyTo = {
						useUrl: true,
						root: 'results',
						widthButton: '80%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						],
						url: urlTo,
						useUtilFunc: true,
						pagesize  : 5,
						key: 'partyId',
						description: ['fullName'],
				};
				
				accutils.initDropDownButton($("#partyIdTo"), $("#partyToGrid"), null, configPartyTo, []);
				<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyId, false) />
				accutils.setValueDropDownButtonOnly($("#partyIdTo"), '${invoice.partyId}', '${partyName} [${invoice.partyId}]');

				if('${businessType}' == 'AR'){						  		
						$("#partyIdFrom").jqxDropDownButton('disabled',true);	
				}
				else {
						$("#partyIdTo").jqxDropDownButton('disabled',true);	
				}
				
				$("#dueDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '70%', theme: THEME});
				$("#dueDate").jqxDateTimeInput('setDate', new Date('${invoice.dueDate?if_exists}'));
				
				$("#invoiceDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px' , width: '70%', theme: THEME});
				$("#invoiceDate").jqxDateTimeInput('setDate', new Date('${invoice.invoiceDate?if_exists}'));
				
				$("#paidDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px' , width: '70%', theme: THEME});
				$("#paidDate").jqxDateTimeInput('setDate', new Date('${invoice.paidDate?if_exists}'));
				
				$('#currencyUomId').jqxDropDownList({ autoDropDownHeight : (uomData.length > 10 ? false : true), source: uomData, width: '80%', theme: THEME, placeHolder: '${uiLabelMap.filterchoosestring}', filterable: true, height: '25', valueMember: 'uomId', displayMember: 'description'});
				accutils.setValueDropDownListOnly($('#currencyUomId'), '${invoice.currencyUomId?if_exists}', 'uomId', uomData);
				
				$('#statusId').text(accutils.getLabel(statusData, "statusId", "description", "${invoice.statusId}"));
				$('#invoiceTypeId').text(accutils.getLabel(invoiceTypeData, "invoiceTypeId", "description", "${invoice.invoiceTypeId}"));
				
				$('#invoiceId').text('${parameters.invoiceId}');
				
				$('#comments').text('${invoice.description?if_exists}');
			}
		});
		OLBEditInv.initNtf();
	}
	
	OLBEditInv.openWindow = function(){
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	OLBEditInv.initNtf = function(){
		if($("#jqxNtf").length > 0){
			$("#jqxNtf").jqxNotification({
			      width: "100%",
			      appendContainer: "#container_invoice_header",
			      opacity: 0.9,
			      autoClose: true,
			  });
		}
	}
	
	
	OLBEditInv.triggerNtf = function(action){
		if($("#jqxNtf").length > 0){
			$("#jqxNtf").jqxNotification('closeLast');
			setTimeout(function(){
				$("#jqxNtf_Content").empty();
				$("#container_invoice_header").empty();
				$("#jqxNtf_Content").html('${StringUtil.wrapString(uiLabelMap.BACCUpdateInvoice)}');
				$("#jqxNtf").jqxNotification(action);
			},100)
		}
	}
	
	OLBEditInv.prototype.bindEvent = function(){
		$('#alterSaveHeader').on('click', function(){
			var submitData = {};
			submitData['partyIdFrom'] = $('#partyIdFrom').attr('data-value');
			submitData['partyIdTo'] = $('#partyIdTo').attr('data-value');
			var dueDate = ($('#dueDate').jqxDateTimeInput('getDate'));
			submitData['dueDate'] = accutils.getTimestamp(dueDate);
			var invoiceDate = ($('#invoiceDate').jqxDateTimeInput('getDate'));
			submitData['invoiceDate'] = accutils.getTimestamp(invoiceDate);
			var paidDate = ($('#paidDate').jqxDateTimeInput('getDate'));
			submitData['paidDate'] = accutils.getTimestamp(paidDate);
			submitData['currencyUomId'] = $('#currencyUomId').val();
			submitData['description'] = $('#comments').val();
			submitData['invoiceId'] = '${parameters.invoiceId}';
			//Send Request update
			$.ajax({
				  url: "updateInvoice",
				  type: "POST",
				  data: submitData,
				  async: false,
				  success: function(data) {
					  if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_){
						  if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_){
								if(data._ERROR_MESSAGE_LIST_){
									accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_[0], function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
								}
								if(data._ERROR_MESSAGE_){
									accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
								}
							}
					  }else{
						  $("#alterpopupWindow").jqxWindow('close');
						  OLBEditInv.triggerNtf('open');
						  setTimeout(function(){
							  if('${businessType}' == 'AP'){
								  window.location.replace('<@ofbizUrl>ViewAPInvoice?invoiceId=' + '${parameters.invoiceId}' + '</@ofbizUrl>');
							  }else{
								  window.location.replace('<@ofbizUrl>ViewARInvoice?invoiceId=' + '${parameters.invoiceId}' + '</@ofbizUrl>');
							  }
						  },200)
					  }
				  }
		  	});
		});
	};
</script>
<style>
	.label-display{
		color: #037c07; 
		font-weight: bold;
		margin-top: 3px;
	}
</style>