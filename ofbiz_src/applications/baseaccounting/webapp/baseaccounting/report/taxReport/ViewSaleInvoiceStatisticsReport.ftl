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
					 { name: 'taxCode', type: 'string' },
					 { name: 'amount', type: 'number' },
					 { name: 'taxAmount', type: 'number' },
					 { name: 'taxPercentage', type: 'number' },
					 { name: 'description', type: 'string' }
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
		                {text: '${StringUtil.wrapString(uiLabelMap.BuyerName)}', width: '17%',
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
								return '<span class=align-right>' + formatnumber(value) + '</span>';
						 	}
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.BSAbbValueAddedTax)}',
		                    datafield: 'taxAmount',
		                    width: '14%', filtertype: 'number', 
		                    cellsrenderer: function(row, columns, value) {
								return '<span class=align-right>' + formatnumber(value) + '</span>';
						 	}
		                },
		                {text: '${StringUtil.wrapString(uiLabelMap.TaxRate)} (%)',
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
		                    width: '20%'
		                }"/>
<#assign customcontrol1 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">		                
<@jqGrid filtersimplemode="true" filterable="true" id="saleInvoiceTaxReport" dataField=dataField columnlist=columnlist
	showtoolbar="true" editable="false" deleterow="false" clearfilteringbutton="true"
	customcontrol1=customcontrol1
	isSaveFormData="true" formData="filterObjData"
	url="jqxGeneralServicer?sname=JQVoucherTaxReport&invoiceType=SALES_INVOICE"/>


<script	type="text/javascript">
	var exportExcel = function(){
		var dataGrid = $("#saleInvoiceTaxReport").jqxGrid('getrows');
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
		hiddenField0.setAttribute("value", "SALES_INVOICE");		
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