<div class="product-info">
    <div class="row-fluid">
        <div class="col-md-2 co-lg-2 col-xs-2 col-sm-2 pos-left">
            <div class="product-image">
                <img id="productSelectedImage"></img>
            </div>
        </div>
        <div class="col-md-10 co-lg-10 col-xs-10 col-sm-10 pos-right">
		<input type="hidden" name="uomToId" id="uomToId" value=""/>
            <input type="hidden" name="itemSubTotal" id="itemSubTotal" value=""/>
            <input type="hidden" name="productSelectedQuantityTmp" id="productSelectedQuantityTmp" value=""/>
            <p class="product-name-active" id="productSelectedName" style="margin-bottom: 10px;">${uiLabelMap.BPOSProductName}</p>
            <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-left" >
                <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-left" >
                    <p class="product-info-active bold">${uiLabelMap.BPOSUnitPrice}:</p>
                    <p class="product-info-active bold">${uiLabelMap.BPOSQuantity}:</p>
                </div>
                <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-right">
                    <p class="product-info-active blue bold" style="text-align: right;"><input style="text-align: right;" type="text" class="form-control product-info-active blue bold" id="productSelectedUnitPrice" disabled="disabled" value=""></p>
                    <p class="product-info-active blue bold"><input style="text-align: right;" type="text" class="form-control product-info-active blue bold" id="productSelectedQuantity" value="0" onfocus="this.select();" onkeypress="return (event.charCode >= 48 && event.charCode <= 57) || (event.which == 13 || event.keyCode == 13)"></p>
                </div>
            </div>
            <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-right">
                <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-left">
                    <p class="product-info-active bold">${uiLabelMap.BPOSDiscount}:</p>
                    <p class="product-info-active bold">${uiLabelMap.BPOSDiscount} (%):</p>
                </div>
                <div class="col-md-6 co-lg-6 col-xs-6 col-sm-6 pos-right">
                    <p class="product-info-active blue bold"><input style="text-align: right;" type="text" class="form-control product-info-active blue bold" value="" id="productSelectedDiscountAmount" onfocus="this.select();"></p>
                    <p class="product-info-active blue bold"><input style="text-align: right;" type="text"  class="form-control product-info-active blue bold" value="0" id="productSelectedDiscountPercent" onfocus="this.select();"></p>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/posresources/js/Common.js"></script>	
<script type="text/javascript">
	var productName = "${StringUtil.wrapString(uiLabelMap.BPOSProductName)}";
	var BPOSReturnItemIsNotAllowedWithDiscount = "${StringUtil.wrapString(uiLabelMap.BPOSReturnItemIsNotAllowedWithDiscount)}";
	$(document).ready(function (){
		$('#productSelectedDiscountAmount').keypress(function(event) {
	        code = event.keyCode ? event.keyCode : event.which;
	        if (code.toString() == 13) {
	        	$('#productSelectedDiscountAmount').unbind("change");
	        	productSelectedDiscountPercent = false;
	        	itemDiscount();
	            return false;
	        }
		});
	    $('#productSelectedDiscountPercent').change(function() {
	    	productSelectedDiscountPercent = true;
	    	itemDiscount();
	    });
	});

	function resetSelectCartItem(){
		$('#productSelectedName').jqxTooltip('destroy');
		$("#productSelectedName").text(productName);
		$("#productSelectedUnitPrice").val(0);
		$("#productSelectedQuantity").val(0);
		$("#productSelectedDiscountAmount").val(0);
		$("#productSelectedDiscountPercent").val(0);
		$("#productSelectedQuantityTmp").val(0);
		$("#itemSubTotal").val("");
		$("#uomToId").val("");
		$("#cartLineIdx").val("");
		$("#productSelectedDiscountAmount").prop('disabled', false);
		$("#productSelectedDiscountPercent").prop('disabled', false);
	}
</script>