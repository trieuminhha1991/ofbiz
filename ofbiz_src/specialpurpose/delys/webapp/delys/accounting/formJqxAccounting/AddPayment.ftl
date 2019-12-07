<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<#include "component://delys/webapp/delys/accounting/popup/popupGridPartyFilter.ftl" />
<script src="/delys/images/js/generalUtils.js"></script>
<@jqGridMinimumLib/>
<div id="notificationadd"></div>

<div id="popupAddPayment" class='hide'>
	<div>${uiLabelMap.AccountingAssignPaymentToInvoice}</div>
	<div class='form-window-container'>
	<div id="container" style="width : 100%;"></div>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_paymentId}</label>
				</div>  
				<div class="span7">
					<div id="dropdownPayment">
						<div id="jqxgridAddPayment"></div>
					</div>
		   		</div>		
			</div>
			<div id="renderAmount" class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_amountToApply}</label>
				</div>  
				<div class="span7">
					<div id="amountToApply"></div>
		   		</div>		
			</div>
		</div>
		<div class="form-action">
			<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.FormFieldTitle_applyButton}</button>
			<button id="updatePm" class='btn btn-primary form-action-button pull-right hide'><i class='fa-check-square-o'></i> ${uiLabelMap.CommonUpdate}</button>
		</div>
	</div>
</div>




<script type="text/javascript">
$.jqx.theme = 'olbius';
theme = $.jqx.theme;
	var actionAddPayment = (function(){
		var initElement = function(){
			$('#popupAddPayment').jqxWindow({
			    width: 550, height: 200, isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
			});	
			$('#amountToApply').jqxNumberInput({width : '250px',height : '25px',theme : theme,spinButtons : true,digits : 15,min : 0,max : 999999999});
			$('#dropdownPayment').jqxDropDownButton({theme : theme,width : '250px',height : '25px'});
			$('#notificationadd').jqxNotification({appendContainer : '#container',autoClose : true,autoCloseDelay : 5000,opacity : 1});
		}
		var initGridDropDown = function(){
			var datafields = [
				{name : 'paymentId',type : 'string'},
				{name : 'partyIdFrom',type : 'string'},
				{name : 'partyIdTo',type : 'string'},
				{name : 'fullNameTo',type : 'string'},
				{name : 'fullNameFrom',type : 'string'},
				{name : 'groupNameTo',type : 'string'},
				{name : 'groupNameFrom',type : 'string'},
				{name : 'partyTypeFrom',type : 'string'},
				{name : 'partyTypeTo',type : 'string'},
				{name : 'effectiveDate',type : 'date',other : 'Timestamp'},
				{name : 'amount',type : 'number'},
				{name : 'currencyUomId',type : 'string'}
			]
			var columns = [
				{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',width : '30%'},
				{text : '${uiLabelMap.FormFieldTitle_partyIdFrom}',datafield : 'partyIdFrom',filtertype: 'olbiusdropgrid',width : '70%',cellsrenderer : function(row){
					var data = $("#jqxgridAddPayment").jqxGrid('getrowdata',row);
					if(data.partyTypeFrom == 'PERSON'){
						return '<span>' + ((data.fullNameFrom != null ) ?data.fullNameFrom : '') +'[' + data.partyIdFrom +']'+'</span>';
					}else if(data.partyTypeFrom == 'PARTY_GROUP'){
						return '<span>' + ((data.groupNameFrom != null)?data.groupNameFrom : '')  +'[' + data.partyIdFrom +']'+'</span>';
					}
					return data.partyIdFrom;
				}},
				{text : '${uiLabelMap.FormFieldTitle_partyIdTo}',datafield : 'partyIdTo',filtertype: 'olbiusdropgrid',width : '70%',cellsrenderer : function(row){
					var data = $("#jqxgridAddPayment").jqxGrid('getrowdata',row);
					if(data.partyTypeTo == 'PERSON'){
						return '<span>' + ((data.fullNameTo != null) ? data.fullNameTo : '')+'[' + data.partyIdTo +']'+'</span>';
					}else if(data.partyTypeFrom == 'PARTY_GROUP'){
						return '<span>' + ((data.groupNameTo != null) ? data.groupNameTo : '') +'[' + data.partyIdTo +']'+'</span>';
					}
					return data.partyIdTo;
				}},
				{text : '${uiLabelMap.FormFieldTitle_effectiveDate}',datafield : 'effectiveDate',width : '30%',filtertype : 'range',cellsformat : 'yyyy/MM/dd'},
				{text : '${uiLabelMap.FormFieldTitle_amount}',datafield : 'amount',width : '30%',cellsrenderer : function(row){
					var data = $('#jqxgridAddPayment').jqxGrid('getrowdata',row);
					if(data) return '<span>' + formatcurrency(data.amount,null) + '</span>';
				}},
				{text : '${uiLabelMap.FormFieldTitle_currencyUomId}',datafield : 'currencyUomId',width : '30%'}
			]
			GridUtils.initDropDownButton({url : 'jqGetListPaymentDetail',width : 450,filterable : true,source : {pagesize :5,cache : false},dropdown : {width : 250,popupZIndex: 18000}},datafields,columns,null,$('#jqxgridAddPayment'),$('#dropdownPayment'),"paymentId");
		}
		
		var bindEvent = function(){
			$('#jqxwindowpartyIdFrom').on('close',function(){
				$('#dropdownPayment').jqxDropDownButton('open');
			})
			$('#jqxwindowpartyIdTo').on('close',function(){
				$('#dropdownPayment').jqxDropDownButton('open');
			})
			$('#save').click(function(){
				if(!actionAddPayment.apply()){
					return;
				}else actionAddPayment.apply();
			})
			
			$('#updatePm').click(function(){
				actionAddPayment.apply();
			});
			$('#popupAddPayment').bind('close',function(){
				$('#dropdownPayment').jqxDropDownButton('val','');
				$('#jqxgridAddPayment').jqxGrid('clearfilters');
				$('#renderAmount').css('display','block');
				$('#amountToApply').jqxNumberInput('clear');
				$('#updatePm').attr('disabled',true);
				$('#save').attr('disabled',false);
			});
		}
		
		var apply = function(action){
			var row = {
				invoiceId : '${invoiceId?if_exists}',
				paymentId: $('#dropdownPayment').jqxDropDownButton('val') ? $('#dropdownPayment').jqxDropDownButton('val') : '',
				amountApplied:$('#amountToApply').jqxNumberInput('val')
			}
			if(row){
				$.ajax({
					url :  'accApupdateInvoiceApplicationJS',
					type : 'POST',
					datatype : 'json',
					data : row,
					async : false,
					cache : false,
					success : function(response){
						if(response._ERROR_MESSAGE_LIST_ || response._ERROR_MESSAGE_){
								$('#container').empty();
								$('#notificationadd').jqxNotification({template : 'error'});
								$('#notificationadd').text(response._ERROR_MESSAGE_LIST_ ? response._ERROR_MESSAGE_LIST_  : response._ERROR_MESSAGE_  );
								$('#notificationadd').jqxNotification('open');
							}else{
								$('#container').empty();
								$('#notificationadd').jqxNotification({template : 'success'});
								$('#notificationadd').text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
								$('#notificationadd').jqxNotification('open');
							}
						objInvoices.reloadGrid();
						$('#popupAddPayment').jqxWindow('close');
					},
					error : function(err){
						
					}
				})
			}
		};
		
		return {
				init : function(){
					initElement();
					initGridDropDown();
					bindEvent();
				},
				apply : apply
			}
		
	}());
	$(document).ready(function(){
		actionAddPayment.init();
	})
</script>
