<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#include "data/invoice.ftl"/>
<#include "popup/popupBillingAccountFilter.ftl"/>
<#include "popup/popupGridPartyFilter.ftl"/>
<#include "popup/popupGridPartyGeneralFilter.ftl"/>
<#include "contextmenu/invoiceContextMenu.ftl"/>
<#assign typeIV = "AR"/>
<script type="text/javascript">
	var dataInvoiceType = [];
	<#list listInvoiceType as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.invoiceTypeId = '${item.invoiceTypeId?if_exists}';
	    tmpOb.description = '${StringUtil.wrapString(description?if_exists)}';
	    dataInvoiceType[${item_index}] = tmpOb;
	</#list>
</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'billingAccountId', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'fullNameTo', type: 'string'},
					 { name: 'fullNameFrom', type: 'string'},
					 { name: 'groupNameFrom', type: 'string'},
					 { name: 'groupNameTo', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', pinned: true, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='accArinvoiceOverviewGlobal?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},
                     { text: '${uiLabelMap.accBillingAccountId}', filtertype: 'olbiusdropgrid', width:150, datafield: 'billingAccountId', hidden: true,
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
					 { text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		var name = (data.groupNameTo != null) ? data.groupNameTo : (data.fullNameTo != null ? data.fullNameTo : '');
					 		return \"<span>\" + name + '[' + data.partyId + ']' + \"</span>\";
					 	},
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
					 { text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:300, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		var name = (data.groupNameFrom != null) ? data.groupNameFrom : (data.fullNameFrom != null ? data.fullNameFrom : '');
					 		return \"<span>\" + name + '[' + data.partyIdFrom + ']' + \"</span>\";
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
					 { text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < dataStatusType.length;i++){
                        		if(value==dataStatusType[i].statusId){
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
			   				widget.jqxDropDownList({source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId', renderer: function (index, label, value) 
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
					 { text: '${uiLabelMap.FormFieldTitle_total}', sortable:false, filterable: false, width:200, datafield: 'total', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable:false, filterable: false, width:200, datafield: 'amountToApply', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
					 	}}"
					/>		

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<div id="jqxPanel" style="width:40%;display:none;" >
	<table style="margin:0 auto;margin-top:10px;width:100%;position:relative;">
		<tr>
			<td>
				<div id="servicelist"></div>
			</td>
			<td align="left">
		       <input type="button" value="${uiLabelMap.CommonRun}" id='jqxButtonExecute' style="margin-left:8px;"/>
		    </td>
		</tr>
	</table>
</div>	 
<script type="text/javascript">
	function commonRun(serviceName){
		var selectedrowindexes = $('#jqxgrid').jqxGrid('selectedrowindexes');
		/*if($("#servicelist").val() == null || $("#servicelist").val() == ""){
			alert('Please choose action');
		}else if(selectedrowindexes.length == 0){
			alert('Please select invoices');
		}else{*/
		var row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[0]);
		var arrInvoiceIds = row.invoiceId;
		for(i = 1; i < selectedrowindexes.length; i++){
			row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[i]);
			arrInvoiceIds += "," + row.invoiceId;
		}
		if (serviceName == 'massInvoicesToApprove') {
            var statusIdType = "INVOICE_APPROVED";
        } else if (serviceName == 'massInvoicesToSent') {
            var statusIdType = "INVOICE_SENT";
        } else if (serviceName == 'massInvoicesToReady') {
            var statusIdType = "INVOICE_READY";
        } else if (serviceName == 'massInvoicesToPaid') {
            var statusIdType = "INVOICE_PAID";
        } else if (serviceName == 'massInvoicesToWriteoff') {
            var statusIdType = "INVOICE_WRITEOFF";
        } else if (serviceName == 'massInvoicesToCancel') {
            var statusIdType = "INVOICE_CANCELLED";
        }
		var request = $.ajax({
		  url: "massChangeInvoiceStatus",
		  type: "POST",
		  data: {serviceName : serviceName, 
		  		 organizationPartyId: '${defaultOrganizationPartyId?if_exists}', 
		  		 partyIdFrom: '${parameters.partyIdFrom?if_exists}', 
		  		 //statusId: '${parameters.statusId?if_exists}',
		  		 statusId : statusIdType,
		  		 fromInvoiceDate: '${parameters.fromInvoiceDate?if_exists}',
		  		 thruInvoiceDate: '${parameters.thruInvoiceDate?if_exists}',
		  		 fromDueDate: '${parameters.fromDueDate?if_exists}',
		  		 thruDueDate: '${parameters.thruDueDate?if_exists}',
		  		 invoiceStatusChange: '<@ofbizUrl>massChangeInvoiceStatus</@ofbizUrl>',
		  		 invoiceIds: arrInvoiceIds
		  		 },
		  dataType: "html"
		});
		
		request.done(function(data) {
		  	if(data.responseMessage == "error"){
            	$('#jqxNotification').jqxNotification({ template: 'error'});
            	$("#jqxNotification").text(data.errorMessage);
            	$("#jqxNotification").jqxNotification("open");
            }
            $("#jqxgrid").jqxGrid('updatebounddata');
		});
		
		request.fail(function(jqXHR, textStatus) {
		  alert( "Request failed: " + textStatus );
		});
		//}
	};
</script>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListARInvoice" autorowheight="true" addrow="true" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true"  addType="popup"
		 otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" id="jqxgrid" selectionmode="checkbox" altrows="true" mouseRightMenu="true" contextMenuId="contextMenu"
		 defaultSortColumn="-invoiceDate" usecurrencyfunction="true" clearfilteringbutton="true" autorowheight="true" viewSize="5"/>
<#include "component://delys/webapp/delys/accounting/popup/popupAddInvoices.ftl"/>