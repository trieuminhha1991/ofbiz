<div id='contextMenu'>
	<ul>
    	<li><i class="icon-ok"></i>${StringUtil.wrapString(uiLabelMap.AccountingCreateAcctRecons)}</li>
    </ul>
</div>
<script type="text/javascript">
$("#contextMenu").jqxMenu({ width: 300, height: 30, autoOpenPopup: false, mode: 'popup', theme: theme});
$("#contextMenu").on('itemclick', function (event) {
	var rowindexes = $("#jqxgrid").jqxGrid('selectedrowindexes');
	var data = {};
	for(i = 0; i < rowindexes.length; i++){
		var row = $("#jqxgrid").jqxGrid('getrowdata', rowindexes[i]);
		var acctgTransId = row.acctgTransId;
		var acctgTransEntrySeqId = row.acctgTransEntrySeqId;
		var amount = row.amount;
		var glAccountId = row.glAccountId;
		var organizationPartyId = row.organizationPartyId;
		var partyId = row.partyId;
		var productId =row.productId;
		data["acctgTransId_o_" + i] = acctgTransId;
		data["acctgTransEntrySeqId_o_" + i] = acctgTransEntrySeqId;
		data["amount_o_" + i] = amount;
		data["glAccountId_o_" + i] = glAccountId;
		data["organizationPartyId_o_" + i] = organizationPartyId;
		data["partyId_o_" + i] = partyId;
		data["productId_o_" + i] = productId;
		data["_rowSubmit_o_" + i] = 'Y';
	}
	var request = $.ajax({
		  url: "<@ofbizUrl>createReconcileAccount</@ofbizUrl>",
		  type: "POST",
		  data: data,
		  dataType: "html"
		});
	request.done(function(data) {
		var dataObj = $.parseJSON(data);
		var replace = "<@ofbizUrl>EditGlReconciliation?glReconciliationId=" + dataObj['glReconciliationId'] +"&organizationPartyId=company</@ofbizUrl>"
		location.replace(replace);
	});
	request.fail(function(){
		alert("${uiLabelMap.createAccountReconciliationFail}");
	});
});
</script>