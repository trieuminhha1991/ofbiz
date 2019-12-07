<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#include "popup/popupGridPartyFilter.ftl"/>	
<#include "popup/popupGridPartyGeneralFilter.ftl"/>	
<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript">
    var dataInvoiceType = [];
	<#list listInvoiceType as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.invoiceTypeId = '${item.invoiceTypeId}';
	    tmpOb.description = '${description}';
	    dataInvoiceType[${item_index}] = tmpOb;
	</#list>
</script> 

<#assign dataField="[{name: 'invoiceItemSeqId', type: 'string' },
	                 {name: 'invoiceItemTypeName', type: 'string'},
	                 { name: 'inventoryItemId', type: 'string' },
	                 { name: 'productId', type: 'string' },
	                 { name: 'partyId', type: 'string' },
	                 { name: 'invoiceId', type: 'string' },
	                 { name: 'partyIdFrom', type: 'string' },
	                 { name: 'invoiceTypeId', type: 'string' },
	                 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
	                 { name: 'currencyUomId', type: 'string' },
	                 { name: 'quantity', type: 'number' },
			 		 { name: 'amount', type: 'number' },
					 { name: 'description', type: 'string' },
					 { name: 'overrideGlAccountId', type: 'string' },
	   				 { name: 'orderId', type: 'string' },
					 { name: 'partyNameResultFrom', type: 'string'},
					 { name: 'partyNameResultTo', type: 'string'},	   				 
	   				 { name: 'total', type: 'number'}]"/>
 <#assign columnlist="{ text: '${uiLabelMap.invoiceItemSeqId}', dataField: 'invoiceItemSeqId', width: 80 },
 					 { text: '${uiLabelMap.invoiceItemTypeName}', dataField: 'invoiceItemTypeName', width: 150 },
					 { text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyId', cellsrenderer:
						 	function(row, colum, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
						 	},
							createfilterwidget: function (column, columnElement, widget) {
								widget.width(140);
							}},
						 { text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyIdFrom', cellsrenderer:
						 	function(row, colum, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
						 	},
							createfilterwidget: function (column, columnElement, widget) {
								widget.width(490);
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
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},	
					 { text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', pinned: true, cellsrenderer:
	                     	function(row, colum, value)
	                        {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
	                        }},				 
                     { text: '${uiLabelMap.DAInventoryItemId}', dataField: 'inventoryItemId', width: 150 },
                     { text: '${uiLabelMap.AccountingProduct}', dataField: 'productId', width: 200, cellsrenderer:
	                 	function(row, colum, value)
	                    {
                    		return \"<span><a href='/catalog/control/EditProduct?productId=\" + value + \"'>\" + value + \"</span>\";
	                    }},
                     { text: '${uiLabelMap.uomId}', dataField: 'currencyUomId', width: 100 },
		    		 { text: '${uiLabelMap.quantity}', dataField: 'quantity', cellsformat: 'D',  width: 150, cellsrenderer:
 	                 	function(row, colum, value)
	                    {
	                    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                    	if(data.quantity == null){
	                    		return \"<span>\" + 1 + \"</span>\";
                    		}
                    		return \"<span>\" + data.quantity + \"</span>\";
	                    }},                    	                     	 
		    		 { text: '${uiLabelMap.unitPrice}', dataField: 'amount', width: 150,
	                    	cellsrenderer:
	     					 	function(row, colum, value){
	     					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);					 		
	     					 		return \"<span>\" + formatcurrency(data.amount,data.currencyUomId) + \"</span>\";						 		
	     					 	}},	
		   		     { text: '${uiLabelMap.description}', dataField: 'description', width: 300 },
		    		 { text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: 150, cellsrenderer:
	                 	function(row, colum, value)
	                    {
                    		return \"<span><a href='/ordermgr/control/orderview?orderId=\" + value + \"'>\" + value + \"</span>\";
	                    }},
                     { text: '${uiLabelMap.ApTotal}', dataField: 'total', width: 200, cellsrenderer:
	                 	function(row, colum, value)
	                    {
	                    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                    	if(data.quantity != null && data.amount!=null){
	                    		return \"<span>\" + formatcurrency(parseFloat(data.quantity)*parseFloat(data.amount),data.currencyUomId) + \"</span>\";	
	                    		
                    		}else{
                    			return null;
                    		}
	                    }}  
                     "/>
                     
<@jqGrid url="jqxGeneralServicer?sname=JQListAPInvoiceItems" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" addrow="false" 
		otherParams="partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 id="jqxgrid" defaultSortColumn="-invoiceDate" usecurrencyfunction="true" clearfilteringbutton="true" autorowheight="true" viewSize="5" />

