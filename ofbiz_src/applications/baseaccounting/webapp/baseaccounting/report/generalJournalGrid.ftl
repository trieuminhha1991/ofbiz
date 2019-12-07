<script type="text/javascript" src="/accresources/js/report/extend.popup.js"></script>
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
<div id="generalJournalGrid"></div>
<script type="text/javascript">
$(document).ready(function() {
	var totalCr = 0;
	var totalDr = 0;
	var fromDate, thruDate;
	var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCGeneralJournal)}',
            service: 'acctgTransTotal',
            button: true,
            id: "generalJournalGrid",
            olap: "getGeneralJournalNoShort",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            pagesizeoptions: [15, 20, 30, 50, 100, 550],
            pagesize: 15,
            showstatusbar: true,
            statusbarheight: 40,
            showaggregates: true,
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
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCAcctgTransId)}', datafield: {name: 'acctgTransId', type: 'string'}, width: 130},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherId)}', datafield: {name: 'documentId', type: 'string'}, width: 130},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: {name: 'voucherCode', type: 'string'}, width: 130},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumberSystem)}', datafield: {name: 'documentNumber', type: 'string'}, width: 150},						 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerIdShortSys)}', datafield: {name: 'partyId', type: 'string'}, width: 110},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}', datafield: {name: 'partyCode', type: 'string'}, width: 110},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}', datafield: {name: 'partyName', type: 'string'}, width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCDescription)}', datafield: {name: 'description', type: 'string'}, width: 220},						 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCAcctgTransTypeId)}', datafield: {name: 'acctgTransTypeId', type: 'string'}, width: 150, filtertype: 'checkedlist',
						 cellsrenderer: function(row, column, value){							
							 for(var i = 0; i < entryTypeData.length; i++){
								 if(value ==  entryTypeData[i].acctgTransTypeId){
									 return '<span title' + value + '>' + entryTypeData[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 },
						 createfilterwidget: function (column, columnElement, widget) {
							if (entryTypeData.length > 0) {
						       var filterDataAdapter = new $.jqx.dataAdapter(entryTypeData, {
						       		autoBind: true
						       });
						       var records = filterDataAdapter.records;
						       widget.jqxDropDownList({source: records, displayMember: 'acctgTransTypeId', valueMember: 'acctgTransTypeId',
					               renderer: function(index, label, value){
				                       if (entryTypeData.length > 0) {
				                               for(var i = 0; i < entryTypeData.length; i++){
				                                       if(entryTypeData[i].acctgTransTypeId == value){
				                                               return '<span>' + entryTypeData[i].description + '</span>';
				                                       }
				                               }
				                       }
				                       return value;
					               }
						       });
						       widget.jqxDropDownList('checkAll');
						   	}
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountCode)}', datafield: {name: 'accountCode', type: 'string'}, width: 120},					 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCDebitAmount)}', datafield: {name: 'drAmount', type: 'number'}, width: 200, filtertype: 'number', columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
							return '<span class=align-right>' + formatnumber(value) + '</span>';
						},
						aggregatesrenderer: function (aggregates, column, element) {
							var data = $('#generalJournalGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
                          	renderstring += '<div style="position: relative; margin: 6px; text-align: right; overflow: hidden;">' + '<b>' + '${uiLabelMap.BACCAmountTotal}: ' + formatcurrency(totalDr, currencyUomId) + '</b>' + '</div>';
                          	renderstring += "</div>";
                          	return renderstring;
                      	}
					},					 
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCCreditAmount)}', datafield: {name: 'crAmount', type: 'number'}, width: 200, filtertype: 'number', columntype: 'numberinput',
						cellsrenderer: function(row, column, value){
							return '<span class=align-right>' + formatnumber(value) + '</span>';
						},
						aggregatesrenderer: function (aggregates, column, element) {
							var data = $('#generalJournalGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
                          	renderstring += '<div style="position: relative; margin: 6px; text-align: right; overflow: hidden;">' + '<b>' + '${uiLabelMap.BACCAmountTotal}: ' + formatcurrency(totalCr, currencyUomId) + '</b>' + '</div>';
                          	renderstring += "</div>";
                          	return renderstring;
                      	}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountName)}', datafield: {name: 'accountName', type: 'string'}, width: 200},	     
					{ datafield: {name: 'currencyId', type: 'string'}, hidden: true },
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreId)}', datafield: {name: 'productStoreId', type:'string'}, width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreDemension)}', datafield: {name: 'productStoreName', type:'string'}, width: 200},
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
				var otherParam = "viewPartner=${parameters.viewPartner?if_exists}";
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
				window.open("exportGeneralJournalNoShortExcelNew?" + otherParam, "_blank");
			},
            exportFileName: 'SO_NHAT_KY_CHUNG_' + (new Date()).formatDate("ddMMyyyy"),
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	fromDate = dateTimeData.fromDate;
            	thruDate = dateTimeData.thruDate;
            	return dateTimeData;
            }
	};
	var grid = OlbiusUtil.grid(config);
	
	$('#generalJournalGrid').on("bindingcomplete", function (event) {
		var filterObject = grid.getFilter();
       	var otherParam = "";
       	if (filterObject && filterObject.filter) {
        	var filterData = filterObject.filter;
           	for (var i = 0; i < filterData.length; i++) {
            	otherParam += "&filter=" + filterData[i];
           	}
       	}
		jQuery.ajax({
            url: 'getTotalGeneralJournal?' + otherParam,
            async: false,
            type: 'POST',
            data: {
            	fromDate: fromDate,
            	thruDate: thruDate
            },
            success: function (data) {
                totalCr = data.totalCr;
                totalDr = data.totalDr;
            }
        });
	});  
});
</script>