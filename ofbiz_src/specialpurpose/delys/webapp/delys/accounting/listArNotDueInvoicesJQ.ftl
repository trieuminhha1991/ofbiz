<@jqGridMinimumLib/>
<script src="/delys/images/js/generalUtils.js"></script>
<#include "popup/popupGridPartyFilter.ftl"/>
<#include "popup/popupGridPartyGeneralFilter.ftl"/>	
<script type="text/javascript">
	var dataInvoiceType = [];
	<#list listInvoiceType as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.invoiceTypeId = "${item.invoiceTypeId}";
	    tmpOb.description = "${description}";
	    dataInvoiceType[${item_index}] = tmpOb;
	</#list>
	var dataStatusType = [];
	<#list listStatusItem as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.statusId = "${item.statusId}";
	    tmpOb.description = "${description}";
	    dataStatusType[${item_index}] = tmpOb;
	</#list>
</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'dueDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'partyNameResultFrom', type: 'string'},
					 { name: 'partyNameResultTo', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/accArinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:130, datafield: 'invoiceTypeId', cellsrenderer:
	                 	function(row, colum, value)
	                    {
	                    	for(i=0; i < dataInvoiceType.length;i++){
	                    		if(value==dataInvoiceType[i].invoiceTypeId){
	                    			return \"<span>\" + dataInvoiceType[i].description + \"</span>\";
	                    		}
	                    	}
	                    	return \"<span>\" + value + \"</span>\";
	                    },
	                    createfilterwidget: function (column, columnElement, widget) {
			   				var sourceIT =
						    {
						        localdata: dataInvoiceType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataInvoiceType.length; i++){
									if(dataInvoiceType[i].invoiceTypeId == value){
										return dataInvoiceType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},	                    
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < dataStatusType.length;i++){
                        		if(dataStatusType[i].statusId == value){
                        			return \"<span>\" + dataStatusType[i].description + \"</span>\";
                        		}
                        	}
                        	return value;
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceST =
						    {
						        localdata: dataStatusType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataStatusType.length; i++){
									if(dataStatusType[i].statusId == value){
										return dataStatusType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.description}', width:150, datafield: 'description'},
					 { text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
					 	},
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			}},
					 { text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
					 	},
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
				 	 { text: '${uiLabelMap.FormFieldTitle_dueDate}', width:130, datafield: 'dueDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.FormFieldTitle_total}', sortable: false, filterable: false, width:200, datafield: 'total', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable: false, filterable: false, width:200, datafield: 'amountToApply', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
					 	}}"
					 />		
<style type="text/css">
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListARDueSoonInvoice" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true"
		 otherParams="total:S-getARInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getAmountToApply(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		  usecurrencyfunction="true" autorowheight="true" viewSize="5"/>
