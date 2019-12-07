<#include "script/ViewListVoucherInvoiceScript.ftl"/>
<#assign datafield = "[
					   {name: 'voucherId', type: 'string'},
					   {name: 'invoiceId', type: 'string'},	
					   {name: 'invoiceTypeId', type: 'string'},
					   {name: 'invoiceTypeDesc', type: 'string'},
					   {name: 'voucherForm', type: 'string'},	
					   {name: 'voucherSerial', type: 'string'},
					   {name: 'voucherNumber', type: 'string'},
					   {name: 'issuedDate', type: 'date',other: 'Timestamp'},
					   {name: 'voucherCreatedDate', type: 'date', other: 'Timestamp'},
					   {name: 'invoiceId', type: 'string'},
					   {name: 'newStatusId', type: 'string'},
					   {name: 'fullNameFrom', type: 'string'},
					   {name: 'fullNameTo', type: 'string'},
					   {name: 'taxProductCategoryId', type: 'string'},							  
					   {name: 'dataResourceId', type: 'string'},
					   {name: 'dataResourceName', type: 'string'},
					   {name: 'objectInfo', type: 'string'},
					   {name: 'amount', type: 'number'},
					   {name: 'taxAmount', type: 'number'},	
					   {name: 'totalAmount', type: 'number'},	
					   {name: 'currencyUomId', type: 'string'},		
					   {name: 'productStoreId', type: 'string'},		
					   {name: 'storeName', type: 'string'},
					   {name: 'facilityId', type: 'string'},
					   {name: 'facilityName', type: 'string'},
					   {name: 'orderId', type: 'string'},
                       {name: 'externalId', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'partyIdFrom', type: 'string'},
					   {name: 'partyIdTo', type: 'string'}
					 ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.VoucherForm)}', datafield: 'voucherForm', width: '12%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.VoucherSerial)}', datafield: 'voucherSerial', width: '11%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.VoucherNumber)}', datafield: 'voucherNumber', width: '11%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}', datafield: 'issuedDate', width: '12%', cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput', filtertype: 'range', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.PublicationReceivingDate)}', datafield: 'voucherCreatedDate', width: '13%', cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput', filtertype: 'range', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}', datafield: 'invoiceId', width: '13%', editable:false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(value && value.length > 0){
									var invoiceIdArr = value.split(', ');
									var invoiceIdStr = '';
									for(var i = 0; i < invoiceIdArr.length; i++){
										invoiceIdStr += '<a href=\"ViewInvoice?invoiceId=' + invoiceIdArr[i] + '\">'  + invoiceIdArr[i] + '</a>';
										if(i < invoiceIdArr.length - 1){
											invoiceIdStr += ', ';
										}
									}
									return '<div>' + invoiceIdStr + '</div>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}', datafield: 'invoiceTypeId', width: '16%', editable: false,
						    filtertype: 'checkedlist', columntype: 'dropdownlist',
						    cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0 ; i < globalVar.invoiceTypeArr.length ; i++ ){
				  					if(globalVar.invoiceTypeArr[i].invoiceTypeId == value){
				  						return '<span>' + globalVar.invoiceTypeArr[i].description  + '</span>';
			  						}
			 					}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								accutils.createJqxDropDownList(widget, globalVar.invoiceTypeArr, {valueMember: 'invoiceTypeId', displayMember: 'description'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'newStatusId', width: '15%', 
							filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0 ; i < globalVar.statusArr.length ; i++ ){
				  					if(globalVar.statusArr[i].statusId == value){
				  						return '<span>' + globalVar.statusArr[i].description  + '</span>';
			  						}
			 					}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								accutils.createJqxDropDownList(widget, globalVar.statusArr, {valueMember: 'statusId', displayMember: 'description'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceFromParty)}', datafield: 'fullNameFrom', width: '16%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceToParty)}', datafield: 'fullNameTo', width: '16%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreId)}', datafield: 'productStoreId', width: '10%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreDemension)}', datafield: 'storeName', width: '20%', editable: false},
                        {text: '${StringUtil.wrapString(uiLabelMap.BSFacilityId)}', datafield: 'facilityId', width: '10%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BSFacilityName)}', datafield: 'facilityName', width: '20%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCOrderId)}', datafield: 'orderId', width: '20%', editable: false,
						     cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties) {
						        var x = '';
						        if(value && value.length > 0) {
                                    var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                                    var invoiceType = data.invoiceTypeId;
                                    if(invoiceType === 'COMMISSION_INVOICE' || invoiceType === 'CUST_RTN_INVOICE' || invoiceType === 'IMPORT_INVOICE' || invoiceType === 'PAYROL_INVOICE' || invoiceType === 'PURCHASE_INVOICE' || invoiceType === 'SETTLEMENT_INVOICE')
    						            x = '<a href=\"viewDetailPO?orderId=' + value + '\">' + value + '</a>';
                                    else if(invoiceType === 'CANCEL_INVOICE' || invoiceType === 'GIFTS_INVOICE' || invoiceType === 'INTEREST_INVOICE' || invoiceType === 'PAY_SETTLEMENT_INV' || invoiceType === 'PURC_RTN_INVOICE' || invoiceType === 'SALES_INVOICE' || invoiceType === 'SALES_INVOICE_TOTAL') {
                                        x = '<a href=\"viewOrder?orderId=' + value + '\">' + value + '</a>';
                                    }
						        }
						        return '<div>' + x + '</div>';
						     }
                         },
						{text: '${StringUtil.wrapString(uiLabelMap.OrderExternalId)}', datafield: 'externalId', width: '20%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAttactFile)}', datafield: 'dataResourceName', width: '18%', editable: false, hidden: true,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
						   		return '<span><a href=\"javascript:void(0)\" onclick=\"javascript: viewListVoucherObj.changeLinkImg(' + row + ')\">' + value + '</a></span>'
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCDescription)}', datafield: 'description', width: '25%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}', datafield: 'amount', columntype: 'numberinput',
						   filtertype: 'number', width: '14%', editable: false,
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								var	decimalseparator = ',';
								var thousandsseparator = '.';
								if(globalVar.currencyUomId == 'USD'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }else if(globalVar.currencyUomId == 'EUR'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, 
									groupSeparator: thousandsseparator, decimalSeparator: decimalseparator, max: 999999999999, digits: 12, inputMode: 'advanced'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonTax)}', datafield: 'taxAmount', columntype: 'numberinput',
						    filtertype: 'number', width: '13%', editable: false,
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								var	decimalseparator = ',';
								var thousandsseparator = '.';
								if(globalVar.currencyUomId == 'USD'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }else if(globalVar.currencyUomId == 'EUR'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, 
									groupSeparator: thousandsseparator, decimalSeparator: decimalseparator, max: 999999999999, digits: 12, inputMode: 'advanced'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonTotal)}', datafield: 'totalAmount', columntype: 'numberinput',
							filtertype: 'number', width: '14%', editable: false,
						    cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}   
						},"/>
</script>

<#assign customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"/>

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true" isSaveFormData="true" formData="filterObjData"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="addNewVoucherWindow" addrow="false" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListVoucher" 
		selectionmode="checkbox" sourceId="voucherId"
		customcontrol1=customcontrol1
		editable="true" mouseRightMenu="true" contextMenuId="contextMenu"/>		
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="createARPayment">
			<i class="fa fa-arrow-circle-right"></i>${uiLabelMap.BACCCreateARPayment}
        </li>
		<li action="createAPPayment">
			<i class="fa fa-arrow-circle-left"></i>${uiLabelMap.BACCCreateAPPayment}
        </li>
	</ul>
</div>		
<#include "createPaymentForVoucherInvoice.ftl"/>					   
<a class="hide" data-rel='colorbox' href="javascript:void(0)" id="viewImgVoucher"></a>
<script type="text/javascript" src="/accresources/js/invoice/ViewListVoucherInvoice.js?v=0.0.2"></script>
<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgrid").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportListVoucherInvoiceNewExcel"; 
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField1 = document.createElement("input");
				hiddenField1.setAttribute("type", "hidden");
				hiddenField1.setAttribute("name", key);
				hiddenField1.setAttribute("value", value);
				form.appendChild(hiddenField1);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
	}
</script>					  