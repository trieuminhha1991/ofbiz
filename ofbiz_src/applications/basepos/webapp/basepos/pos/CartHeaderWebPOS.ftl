<div id="CartHeaderWebPOS" class="widget-content">
	<table id="table-order-info" class="table table-striped table-bordered table-hover" >
  		<input type="hidden" id="totalDue" value="" />
    	<input type="hidden" id="grandTotalCartHidden" value="" />
		<tbody class="content-scroll">
			<tr>
		    	<td class="w50">${uiLabelMap.BPOSTransactionId}</td>
		      	<td class="w50 pos-align-right">
					<span class="bold" id="transactionId"></span>
		      	</td>
		    </tr>
		   	<tr>
		      	<td class="w50">${uiLabelMap.BPOSTotalItemSubTotal}</td>
		      	<td class="w50 pos-align-right">
					<span class="bold" id="grandTotalCart" ></span>
					<input type="hidden" id="grandTotalCartInput" />
		      	</td>
		    </tr>
		    <tr>
		      	<td class="w50">${uiLabelMap.BPOSDiscount}</td>
		      	<td class="w50 pos-align-right">
			  		<input type="text" class="form-control pos-align-right bold" id="discountWholeCart" name="discountWholeCart" value="" onfocus="this.select();"/>
		      	</td>
		    </tr>
		    <tr>
		      	<td class="w50">${uiLabelMap.BPOSDiscountPercent} (%)</td>
		      	<td class="w50 pos-align-right">
					<input type="text" class="form-control pos-align-right bold" id="discountWholeCartPercent" name="discountWholeCartPercent" value="" onfocus="this.select();"/>
		      	</td>
		    </tr>
		    <tr>
		    	<td class="w50">${uiLabelMap.BPOSLoyalty}</td>
		      	<td class="w50 pos-align-right">
					<span class="bold" id="loyaltyPoint"></span>
		      	</td>
		    </tr>
		    <#if showPricesWithVatTax == "N">
			    <tr>
			      	<td class="w50">${uiLabelMap.BPOSTotalSalesTax}</td>
			      	<td class="w50 pos-align-right">
						<span class="bold" id="totalTax" ></span>
						<input type="hidden" id="totalTaxInput" />
			     	</td>
			    </tr>
			</#if>
		    <tr class="total-checkout">
		      	<td class="w50"><span class="total-money">${uiLabelMap.BPOSTotalCost}</span></td>
		      	<td class="w50 pos-align-right">
					<span class="total-money" id="totalCart"></span>
		      	</td>
		    </tr>
		</tbody>
  	</table>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		updateCartHeader();
		$("#productSelectedDiscountPercent").prop('disabled', false);
		$('#discountWholeCart').keypress(function(event) {    	
		    code = event.keyCode ? event.keyCode : event.which;
		    if (code.toString() == 13) {
		    	$("#discountWholeCart").unbind("change");
		    	discountPercent = false;
		    	discountWholeCart();
		    	event.preventDefault();
		    	return false;
		    }
		    if (code.toString() == 27) {
		        productToSearchFocus();
		        return false;
		    }
		});
	});
	
	function getResultOfUpdateCartHeader(data){
		$("#discountWholeCart").prop('disabled', false);
		$("#discountWholeCartPercent").prop('disabled', false);
		var serverError = getServerError(data);
	    if (serverError != "") {
			productToSearchFocus();
			bootbox.alert(serverError);
			resetCartHeader();
	    } else {
			var cartHeader = data.cartHeader;
			if(cartHeader){
				var transactionId = cartHeader.transactionId;
				var totalDue = cartHeader.totalDue;
				var amountDiscount = cartHeader.amountDiscount;
				var amountPercent = cartHeader.amountPercent;
				var grandTotalCart = cartHeader.grandTotalCart;
				var totalTax = cartHeader.tax;
				var currency = cartHeader.currency;
				var totalCart = cartHeader.totalDue;
				var loyaltyPoint = cartHeader.loyaltyPoint;
				$("#totalDue").val(totalDue);
				totalDue = parseFloat(totalDue);
				if(totalDue < 0){
					$("#amountCreditCard").prop('disabled', true);
				}else{
					$("#amountCreditCard").prop('disabled', false);
				}
				$("#totalTax").html(formatcurrency(totalTax, currency));
				$("#totalTaxInput").val(totalTax);
				$("#transactionId").html(transactionId);
				$("#grandTotalCart").html(formatcurrency(grandTotalCart, currency));
				$("#grandTotalCartInput").val(grandTotalCart);
				$("#discountWholeCart").maskMoney('mask', amountDiscount);
				$("#discountWholeCartPercent").val(amountPercent);
				$("#totalCart").html(formatcurrency(totalDue, currency));
				$("#grandTotalCartHidden").val(totalDue);
				$("#loyaltyPoint").html(loyaltyPoint);
			} else {
				resetCartHeader();
			}
	    }
	}
	
	function resetCartHeader(){
		$("#totalDue").val("");
		$("#totalTax").val("");
		$("#transactionId").html("");
		$("#grandTotalCart").html("");
		$("#discountWholeCart").maskMoney('mask', 0);
		$("#discountWholeCartPercent").val(0);
		$("#totalCart").html(totalDue);
		$("#discountWholeCart").prop('disabled', false);
		$("#discountWholeCartPercent").prop('disabled', false);
		$("#loyaltyPoint").html("");
	}
</script>