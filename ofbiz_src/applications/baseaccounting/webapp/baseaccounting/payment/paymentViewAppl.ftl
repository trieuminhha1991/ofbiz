<#include "paymentViewApplInvoice.ftl"/>
<#include "paymentViewApplPay.ftl"/>
<#include "paymentViewApplTax.ftl"/>
<script>
	$(document).ready(function () {
		var olbPayAppl = new  OLBPayAppl();
		olbPayAppl.initGrids();
	});
	var OLBPayAppl = function(){};
	OLBPayAppl.prototype.initGrids = function(){
		//var olbPayApplInv = new OLBPayApplInv();
		var olbPayApplPay = new OLBPayApplPay();
		var olbPayApplTax = new OLBPayApplTax();
		//olbPayApplInv.initGrid();
		olbPayApplPay.initGrid();
		olbPayApplTax.initGrid();
	};
</script>