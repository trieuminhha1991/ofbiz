<@jqGridMinimumLib />
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-header">
			<h4>${uiLabelMap.AccountingPaymentGroupOverview}</h4>
		</div>
	</div>
	<table>
		<tr>
			<td align="right">
				<span>${uiLabelMap.paymentGroupId}</span>
			</td>
			<td align="left">
				<span>
					<div id="paymentGroupId" class="green-label"></div>
				</span>
			</td>
		</tr>
		<tr>
			<td align="right">
				<span>${uiLabelMap.paymentGroupTypeId}</span>
			</td>
			<td align="left">
			    <span><div id="paymentGroupTypeId" name="paymentGroupTypeId" class="green-label"></div></span>
			</td>
		</tr>
		<tr>
			<td align="right">
				<span>${uiLabelMap.paymentGroupName}</span>
			</td>
			<td align="left">
			    <span><div id="paymentGroupName" name="paymentGroupName" class="green-label"/></span>
			</td>
		</tr>
	</table>
</div>
<script>
	//Create paymentGroupId
	<#assign paymentGroupId = paymentGroup.paymentGroupId?if_exists />
	$('#paymentGroupId').text('${paymentGroupId}');
	//Create paymentGroupName
	<#assign paymentGroupName = paymentGroup.paymentGroupName?if_exists />
	$('#paymentGroupName').text('${paymentGroupName}');
	//Create paymentGroupTypeId
	<#assign paymentGroupTypeId = paymentGroup.paymentGroupTypeId?if_exists />
	<#assign paymentGroupType = delegator.findOne("PaymentGroupType", {"paymentGroupTypeId" : paymentGroupTypeId}, true)>
	$('#paymentGroupTypeId').text('${paymentGroupType.description}');
</script>
<style type="text/css">
td span{
	margin-bottom: 10px;
	display: block;
	padding-right: 15px;
}
#main-content > #page-content,#main-content>#page-content {
	min-height:0px !important;
}
</style>