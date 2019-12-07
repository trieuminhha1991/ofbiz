<script>
	<#assign listTransEntryType = delegator.findByAnd("AcctgTransType", null, Static["org.ofbiz.base.util.UtilMisc"].toList("acctgTransTypeId DESC"), false)>
	var entryTypeData = [
	   <#if listTransEntryType?exists>
		   	<#list listTransEntryType as entry>
		   		{
		   			acctgTransTypeId : "${entry.acctgTransTypeId}",
		   			description : "${StringUtil.wrapString(entry.get('description',locale))}",
					},
			</#list>
	   </#if>
	  ];	
</script>
<@jqOlbCoreLib />
<div id="voucherDeclarationGrid"></div>
<script type="text/javascript">
$(document).ready(function() {
	var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCVoucherDeclaration)}',
            service: 'acctgTransTotal',
            button: true,
            id: "voucherDeclarationGrid",
            olap: "getGeneralJournal",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            pagesizeoptions: [15, 20, 30, 50, 100],
            pagesize: 15,
            columns: [
  					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					}, 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCTimeLabel)}', datafield: {name: 'dateTime', type: 'string'}, width: 120, filterable: false},                                          
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherDate2)}', datafield: {name: 'documentDate', type: 'date'}, width: 120, 
						cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput', filterable: false
						
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherId)}', datafield: {name: 'documentId', type: 'string'}, width: 130, },
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: {name: 'voucherCode', type: 'string'}, width: 130,},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumberSystem)}', datafield: {name: 'documentNumber', type: 'string'}, width: 150,},						 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}', datafield: {name: 'partyId', type: 'string'}, width: 110,},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}', datafield: {name: 'partyName', type: 'string'}, width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCDescription)}', datafield: {name: 'description', type: 'string'}, width: 220,},						 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCAcctgTransTypeId)}', datafield: {name: 'acctgTransTypeId', type: 'string'}, width: 150,
						 cellsrenderer: function(row, column, value){							
							 for(var i = 0; i < entryTypeData.length; i++){
								 if(value ==  entryTypeData[i].acctgTransTypeId){
									 return '<span title=' + value + '>' + entryTypeData[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountCode)}', datafield: {name: 'accountCode', type: 'string'}, width: 120,},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountName)}', datafield: {name: 'accountName', type: 'string'}, width: 200,},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCRecipGlAccountCode)}', datafield: {name: 'accountRecipCode', type: 'string'}, width: 120},
					{ text: '${uiLabelMap.BACCRecipGlAccountName}', datafield: {name: 'accountRecipName', type: 'string'}, width: 200,},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCDebitAmount)}', datafield: {name: 'drAmount', type: 'number'}, width: 150,
						filtertype: 'number', columntype: 'numberinput',
						 cellsrenderer: function(row, column, value){
							return '<span class=align-right>' + formatnumber(value) + '</span>';
						 }
					},					 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCreditAmount)}', datafield: {name: 'crAmount', type: 'number'}, width: 150,
						filtertype: 'number', columntype: 'numberinput',
						 cellsrenderer: function(row, column, value){
						  		return '<span class=align-right>' + formatnumber(value) + '</span>';
						 }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: {name: 'orderId', type: 'string'}, width: 120},
					{text: '${StringUtil.wrapString(uiLabelMap.BSReturnId)}', datafield: {name: 'returnId', type: 'string'}, width: 150,},
					{text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}', datafield: {name: 'productCode', type: 'string'}, width: 150,},
					{text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}', datafield: {name: 'productName', type: 'string'}, width: 250,},
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: {name: 'salesMethodChannelName', type: 'string'}, width: 200,},	
					{ datafield: {name: 'currencyId', type: 'string'}, hidden: true},	     
            ],
            popup: [
				{
				    group: "dateTime",
				    id: "dateTime",
				    params: {
                    	index: 0
                    }
				},        
            ],
            excel: function(obj){
				var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value) {
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.open("exportVoucherDeclarationExcel?" + otherParam, "_blank");
			},
            exportFileName: 'BANG_KE_KHAI_CHUNG_TU_' + (new Date()).formatDate("ddMMyyyy"),
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	return dateTimeData;
            }
	};
	var grid = OlbiusUtil.grid(config);
});
</script>