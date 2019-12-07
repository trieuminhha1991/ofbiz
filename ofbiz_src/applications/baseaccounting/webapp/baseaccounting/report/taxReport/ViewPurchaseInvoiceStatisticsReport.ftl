<link rel="stylesheet" type="text/css" href="/accresources/css/grid.css">
<script	type="text/javascript">
	var filterObjData = new Object();
</script>
<#assign dataField="[{ name: 'voucherNumber', type: 'string' },
					{ name: 'voucherForm', type: 'string' },
					{ name: 'voucherSerial', type: 'string' },
					{ name: 'issuedDate', type: 'date',other: 'Timestamp'},
					{ name: 'voucherCreatedDate', type: 'date',other: 'Timestamp'},
					{ name: 'partyName', type: 'string'},
					{ name: 'invoiceTypeId', type: 'string'},
					{ name: 'taxCode', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'amount', type: 'number' },
					{ name: 'taxAmount', type: 'number' },
					{ name: 'taxPercentage', type: 'number' }
					]"/>
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.BACCSeqId)}', sortable: false, filterable: false, editable: false,
						    groupable: false, draggable: false, resizable: false, hidden: false,
						    datafield: '', columntype: 'number', width: '5%',
						    cellsrenderer: function (row, column, value) {
						        return '<div style=\"margin:4px;\">' + (value + 1) + '</div>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCFormInv)}',
		                    datafield: 'voucherForm',
		                    width: '10%'
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BACCSeriesInv)}',
		                    datafield: 'voucherSerial',
		                    width: '10%'
		                },		                
		                {text: '${StringUtil.wrapString(uiLabelMap.BACCInvSerialNumber)}',
		                    datafield: 'voucherNumber',
		                    width: '10%'
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}',
		                    datafield: 'issuedDate', width: '12%', cellsformat: 'dd/MM/yyyy', 
							filtertype: 'range', editable: false
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.ReceivingVoucherDate)}',
		                    datafield: 'voucherCreatedDate', width: '12%', cellsformat: 'dd/MM/yyyy', 
							filtertype: 'range', editable: false
		                },		                
		                {text: '${StringUtil.wrapString(uiLabelMap.SellerName)}', width: '19%',
		                    datafield: 'partyName'
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}',
		                    datafield: 'taxCode',
		                    width: '11%'
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.RevenueNotTax)}',
		                    datafield: 'amount',
		                    width: '15%',
		                    cellsrenderer: function(row, columns, value) {
		                    	var data = $('#purchaseInvoiceTaxReport').jqxGrid('getrowdata', row);
		                    	if (data && data.invoiceTypeId) {
		                    		if (data.invoiceTypeId == 'PURC_RTN_INVOICE') {
		                    			return '<span class=align-right>' + formatnumber(value * (-1)) + '</span>';
		                    		}
		                    	} 
		                    	return '<span class=align-right>' + formatnumber(value) + '</span>';
						 	}
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BSAbbValueAddedTax)}',
		                    datafield: 'taxAmount',
		                    width: '14%', filtertype: 'number', 
		                    cellsrenderer: function(row, columns, value) {
								var data = $('#purchaseInvoiceTaxReport').jqxGrid('getrowdata', row);
		                    	if (data && data.invoiceTypeId) {
		                    		if (data.invoiceTypeId == 'PURC_RTN_INVOICE') {
		                    			return '<span class=align-right>' + formatnumber(value * (-1)) + '</span>';
		                    		}
		                    	}
		                    	return '<span class=align-right>' + formatnumber(value) + '</span>';
						 	}
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.TaxRate)}',
		                    datafield: 'taxPercentage',
		                    width: '10%', filtertype: 'number', 
		                    cellsrenderer: function(row, columns, value) {
		                    	if(typeof(value) == 'number'){
									return '<span class=align-right>' + value + '%</span>';
		                    	}
		                    	return '<span class=align-right>' + value + '</span>';
						 	}
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BACCDescription)}',
		                    datafield: 'description',
		                    width: '15%'
		                }"/>
<#assign customcontrol1 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">		                
<@jqGrid filtersimplemode="true" filterable="true" id="purchaseInvoiceTaxReport" dataField=dataField columnlist=columnlist
	showtoolbar="true" editable="false" deleterow="false" clearfilteringbutton="true"
	customcontrol1=customcontrol1
	isSaveFormData="true" formData="filterObjData"
	url="jqxGeneralServicer?sname=JQVoucherTaxReport&invoiceType=PURCHASE_INVOICE"/>

<script	type="text/javascript">
	var exportExcel = function(){
		var dataGrid = $("#purchaseInvoiceTaxReport").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
		
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", "exportVoucherTaxReport");
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "invoiceType");
		hiddenField0.setAttribute("value", "PURCHASE_INVOICE");
		form.appendChild(hiddenField0);
		
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
	};
</script>