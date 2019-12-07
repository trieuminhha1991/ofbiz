<div id='contextMenu' style="display:none;">
	<ul>
	    <li><i class="icon-ok"></i>${StringUtil.wrapString(uiLabelMap.CommonRefresh)}</li>
	    <li>${StringUtil.wrapString(uiLabelMap.CommonRun)}
	    	<ul style="width:250px;">
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingPrintInvoices)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToApproved)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToSent)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToReady)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToPaid)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToWriteoff)}</a></li>
                <li><a href="#">${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToCancelled)}</a></li>
            </ul>
	    </li>
	</ul>
</div>
<script type="text/javascript">
	$("#contextMenu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.CommonRefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingPrintInvoices)}") {
        	var selectedrowindexes = $('#jqxgrid').jqxGrid('selectedrowindexes');
        	var row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[0]);
			var arrInvoiceIds = row.invoiceId;
			for(i = 1; i < selectedrowindexes.length; i++){
				row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[i]);
				arrInvoiceIds += "," + row.invoiceId;
			}
        	var request = $.ajax({
			  url: "<@ofbizUrl>PrintInvoices</@ofbizUrl>",
			  type: "POST",
			  data: {
			  		 organizationPartyId: '${defaultOrganizationPartyId?if_exists}', 
			  		 partyIdFrom: '${parameters.partyIdFrom?if_exists}', 
			  		 statusId: '${parameters.statusId?if_exists}',
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
			  		displayMessage('error',data.errorMessage,'open');
	            }else displayMessage('success','${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}','open');	
			});
			
			request.fail(function(jqXHR, textStatus) {
			  alert( "Request failed: " + textStatus );
			});
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToApproved)}") {
        	commonRun("massInvoicesToApprove");
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToSent)}") {
        	commonRun("massInvoicesToSent");
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToReady)}") {
        	commonRun("massInvoicesToReady");
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToPaid)}") {
        	commonRun("massInvoicesToPaid");
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToWriteoff)}") {
        	commonRun("massInvoicesToWriteoff");
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceStatusToCancelled)}") {
        	commonRun("massInvoicesToCancel");
        }
	});
</script>