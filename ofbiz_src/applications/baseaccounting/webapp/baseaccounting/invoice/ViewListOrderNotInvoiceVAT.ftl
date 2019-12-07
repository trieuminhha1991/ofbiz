<#include "script/ViewListOrderNotInvoiceVATScript.ftl"/>

<#assign datafield = "[{name: 'orderId', type: 'string'},
					   {name: 'productStoreId', type: 'string'},
					   {name: 'statusId', type: 'string'},
					   {name: 'orderDate', type: 'date', other: 'Timestamp'},
					   {name: 'customerId', type: 'string'},
					   {name: 'customerCode', type: 'string'},
					   {name: 'customerFullName', type: 'string'},
					   {name: 'grandTotal', type: 'number'},
					   {name: 'currencyUom', type: 'string'},
					   {name: 'agreementId', type: 'string'},
					   {name: 'agreementCode', type: 'string'},	
					   ]"/>
					   
<script type="text/javascript">
var cellClass = function (row, columnfield, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		if ("ORDER_CANCELLED" == data.statusId) {
			return "background-cancel";
		} else if ("ORDER_CREATED" == data.statusId) {
			return "background-important-nd";
		} else if ("ORDER_APPROVED" == data.statusId) {
			return "background-prepare";
		}
	}
}
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', width: '10%', pinned: true,  
							cellsrenderer: function(row, colum, value) {
								return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
							}
					   },
					   {text: '${uiLabelMap.BSSalesChannel}', dataField: 'productStoreId', width: '18%', cellClassName: cellClass, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								if (globalVar.productStoreData.length > 0) {
									for(var i = 0 ; i < globalVar.productStoreData.length; i++){
		    							if (value == globalVar.productStoreData[i].productStoreId){
		    								return '<span title =\"' + globalVar.productStoreData[i].storeName +'\">' + globalVar.productStoreData[i].storeName + '</span>';
		    							}
		    						}
								}
								return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
						 		accutils.createJqxDropDownList(widget, globalVar.productStoreData, {valueMember: 'productStoreId', displayMember: 'storeName'});
				   			}
						},
						{text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '17%', cellClassName: cellClass, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								if (globalVar.orderStatusData.length > 0) {
									for(var i = 0 ; i < globalVar.orderStatusData.length; i++){
		    							if (value == globalVar.orderStatusData[i].statusId){
		    								return '<span title =\"' + globalVar.orderStatusData[i].description +'\">' + globalVar.orderStatusData[i].description + '</span>';
		    							}
		    						}
								}
								return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
						 		accutils.createJqxDropDownList(widget, globalVar.orderStatusData, {valueMember: 'statusId', displayMember: 'description'});
				   			}
						},
						{text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: '18%', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
						
						{text: '${uiLabelMap.BSCustomer}', dataField: 'customerCode', width: '15%', cellClassName: cellClass,},
						{text: '${uiLabelMap.BSCustomerName}', dataField: 'customerFullName', width: '19%', cellClassName: cellClass},
						{text: '${uiLabelMap.CommonAmount}', dataField: 'grandTotal', width: '14%', cellClassName: cellClass, columntype: 'numberinput', filtertype: 'number', 
							cellsrenderer: function(row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
								}						
							}
						},
						{text: '${uiLabelMap.BSAgreementCode}', dataField: 'agreementCode', width: '15%', cellClassName: cellClass},
					   "/>
</script>
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow" sortable="true"
				addrow="false" customControlAdvance="<div id='orderDateSearch'></div>"
				mouseRightMenu="true" contextMenuId="contextMenu"
				selectionmode="checkbox" url="" jqGridMinimumLibEnable="false" isSaveFormData="true" formData="globalVar.formData"/>

<div id='contextMenu' class="hide">
	<ul>
		<li action="createVATInvoice">
			<i class="fa-file-text-o open-sans"></i>${uiLabelMap.CreateNewVATInvoiceForOrder}
        </li>        
		<li action="createOrderInvoiceNote">
			<i class="fa fa-info-circle open-sans"></i>${uiLabelMap.BACCTransferToOrderRequireInvoiceVAT}
        </li>        
	</ul>
</div>

<div class="hide" id="createInvoiceVATWindow">
	<div>${uiLabelMap.CreateVATInvoice}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="invoiceItemProductGrid"></div>
			</div>
			<div class="row-fluid">
				<div class='span12 margin-top10'>
					<div class='span6'>
						<div class='row-fluid margin-bottom5'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCInvoiceDate)}</label>
							</div>
							<div class="span7">
								<div id="invoiceDate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom5'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
							</div>
							<div class="span7">
								<input type="text" id="voucherForm" >
							</div>
						</div>
						<div class='row-fluid margin-bottom5'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
							</div>
							<div class="span7">
								<input type="text" id="voucherNumber">
							</div>
						</div>
					</div>
					<div class='span6'>
						<div class='row-fluid margin-bottom5'>
							<div class="span5 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BACCDescription)}</label>
							</div>
							<div class="span7">
								<input type="text" id="description">
							</div>
						</div>
						<div class='row-fluid margin-bottom5'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
							</div>
							<div class="span7">
								<input type="text" id="voucherSerial">
							</div>
						</div>
						<input type="hidden" id="productStoreIdHidden" />
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelCreateInvoiceVAT">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateInvoiceVAT">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="orderInvoiceNoteInfoWindow" class="hide">
	<div>${uiLabelMap.BACCVATInvoiceInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BSOrder}</label>
					</div>
					<div class="span7">
						<div id="orderListVATDropDown">
							<div id="orderListGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCCustomerName}</label>
					</div>
					<div class="span7">
						<input type="text" id="customerNameVAT">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCCompanyName}</label>
					</div>
					<div class="span7">
						<input type="text" id="companyNameVAT">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTaxCode}</label>
					</div>
					<div class="span7">
						<input type="text" id="taxInfoIdVAT">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPayments}</label>
					</div>
					<div class="span7">
						<div id="enumPaymentMethodVAT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCAccountNumber}</label>
					</div>
					<div class="span7">
						<input type="text" id="bankIdVAT">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.CommonAddress1}</label>
					</div>
					<div class="span7">
						<textarea id="addressVAR" class="text-popup" style="width: 92% !important; height: 70px"></textarea>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelCreateOrderInvoiceNote">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateOrderInvoiceNote">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
				
<script type="text/javascript" src="/accresources/js/invoice/ViewListOrderNotInvoiceVAT.js?v=0.0.4"></script>				