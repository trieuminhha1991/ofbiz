<#include "script/ViewOrderRequestVATInvScript.ftl"/>
<#assign datafield = "[{name: 'orderId', type: 'string'},
					   {name: 'orderDate', type: 'date'},
					   {name: 'invoiceId', type: 'string'},
					   {name: 'isCreatedVatInv', type: 'bool'},
					   {name: 'customerName', type: 'string'},
					   {name: 'companyName', type: 'string'},
					   {name: 'taxInfoId', type: 'string'},	
					   {name: 'address', type: 'string'},	
					   {name: 'paymentMethod', type: 'string'},	
					   {name: 'bankId', type: 'string'},	
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId', width: '10%', pinned: true,
							cellsrenderer: function(row, colum, value) {
								return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BSCreateDate)}', datafield: 'orderDate', width: '18%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}', datafield: 'invoiceId', width: '11%',
						   cellsrenderer: function(row, colum, value) {
								return \"<span><a href='ViewARInvoice?invoiceId=\" + value + \"'>\" + value + \"</a></span>\";
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CreatedVATInvoice)}', datafield: 'isCreatedVatInv', width: '11%', columntype: 'checkbox'},
					   {text: '${StringUtil.wrapString(uiLabelMap.VATInvoiceCustomerName)}', datafield: 'customerName', width: '16%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCCompanyName)}', datafield: 'companyName', width: '17%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}', datafield: 'taxInfoId', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BPOSAddress)}', datafield: 'address', width: '20%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCPayments)}', datafield: 'paymentMethod', width: '17%', columntype: 'dropdownlist',
						   filtertype: 'checkedlist',
						   cellsrenderer: function(row, colum, value) {
								for(var i = 0; i < globalVar.enumPaymentMethodArr.length; i++){
									if(globalVar.enumPaymentMethodArr[i].enumId == value){
										return '<span>' + globalVar.enumPaymentMethodArr[i].description + '</span>';
									}
								}	
							},
							createfilterwidget: function (column, columnElement, widget) {
						 		accutils.createJqxDropDownList(widget, globalVar.enumPaymentMethodArr, {valueMember: 'enumId', displayMember: 'description'});
				   			}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}', datafield: 'bankId', width: '15%'}, 	 					   
					  "/>
</script>		

<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow" sortable="true"
				addrow="false" mouseRightMenu="true" contextMenuId="contextMenu"
				url="jqxGeneralServicer?sname=JQGetListOrderInvoiceNote" jqGridMinimumLibEnable="false"/>			
				
<div id='contextMenu' class="hide">
	<ul>
		<li action="setCreatedVATInv" id="setCreatedVATInv">
			<i class="fa-bookmark open-sans"></i>${uiLabelMap.SettingCreatedInvoiceVAT}
        </li>
        <li action="editVATInv" id="editVATInv">
			<i class="fa-edit open-sans"></i>${uiLabelMap.CommonEdit}
        </li>        
	</ul>
</div>

<div id="createdInvoiceVATWindow" class="hide">
	<div>${uiLabelMap.CommonInformation} ${uiLabelMap.CommonVoucher}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
					</div>
					<div class="span7">
						<input type="text" id="voucherForm" >
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
					</div>
					<div class="span7">
						<input type="text" id="voucherSerial">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
					</div>
					<div class="span7">
						<input type="text" id="voucherNumber">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}</label>
					</div>
					<div class="span7">
						<div id="issuedDate"></div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelCreatedInvoiceVAT">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreatedInvoiceVAT">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>	

<div id="editOrderInvoiceNoteInfoWindow" class="hide">
	<div>${uiLabelMap.CommonEdit} ${uiLabelMap.BACCVATInvoiceInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BSOrder}</label>
					</div>
					<div class="span7">
						<input type="text" id="orderIdVATEdit">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCCustomerName}</label>
					</div>
					<div class="span7">
						<input type="text" id="customerNameVATEdit">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCCompanyName}</label>
					</div>
					<div class="span7">
						<input type="text" id="companyNameVATEdit">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTaxCode}</label>
					</div>
					<div class="span7">
						<input type="text" id="taxInfoIdVATEdit">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPayments}</label>
					</div>
					<div class="span7">
						<div id="enumPaymentMethodVATEdit"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCAccountNumber}</label>
					</div>
					<div class="span7">
						<input type="text" id="bankIdVATEdit">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.CommonAddress1}</label>
					</div>
					<div class="span7">
						<textarea id="addressVAREdit" class="text-popup" style="width: 92% !important; height: 70px"></textarea>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelCreateOrderInvoiceNoteEdit">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateOrderInvoiceNoteEdit">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>	

<script type="text/javascript" src="/accresources/js/invoice/ViewOrderRequestVATInv.js?v=0.0.2"></script>		   