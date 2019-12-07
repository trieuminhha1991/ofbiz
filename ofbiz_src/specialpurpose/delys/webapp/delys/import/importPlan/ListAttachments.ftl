 <script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
 <form id='CreateListAttachmentsPDF' action="CreateListAttachmentsPDF" method="post">
<div  id="tableListAttachments" >
<#if listOrder?exists>
<#assign listSize= listOrder?size />
<#assign hasColor= true />
<table class='table table-bordered dataTable' cellspacing='0'>
	<thead>
		<tr role='row' class='header-row'>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.accSTT}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.CommodityName}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.CommodityCode}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.KARNumber}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.CommodityQuantity}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.currency}</th>
			<th colspan="2" class='hidden-phone'>${uiLabelMap.weight}(${weightUnit})</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.ContractNumber}</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.Value}(${currencyUnit})</th>
			<th rowspan="2" class='hidden-phone'>${uiLabelMap.Origin}</th>
		</tr>
		<tr role='row' class='header-row'>
			<th class='hidden-phone'>${uiLabelMap.NetWeight}</th>
			<th class='hidden-phone'>${uiLabelMap.ShipmentTotalWeight}</th>
		</tr>
	</thead>
	<tbody>
	<#assign OneTurn = true />
	<#list listOrder as order>
		<#assign index= order_index + 1 />
				<#if OneTurn>
						<tr>
								<td>${index}</td>
								<td><input type="text" class="" id="txtinternalName_${order_index}" value="${order.internalName}" /></td>
								<td><input type="text" class="" id="txtCommodityCode_${order_index}" placeholder="${uiLabelMap.CommodityCode}" /></td>
								<td><input type="text" class="numbers" onchange="txtKARChange()" onkeypress='return event.charCode >= 48 && event.charCode <= 57' id="txtKAR_${order_index}" placeholder="KAR" /></td>
								<td><input type="text" class="numbers" onchange="txttxtquantityChange()" onkeypress='return event.charCode >= 48 && event.charCode <= 57' id="txtquantity_${order_index}" value="${order.quantity?string("##,000")}" /></td>
								<td><input type="text" class="" id="txtquantityUomId_${order_index}" value="${order.quantityUomId}" /></td>
								<td><input type="text" class="numbers" onchange="txtNetWeightChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtNetWeight_${order_index}" value="${order.productWeight?string("##,000.00##")}" /></td>
								<td><input type="text" class="numbers" onchange="txtGrossWeightChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtShipmentTotalWeight_${order_index}" value="${order.weight?string("##,000.00##")}" /></td>
								<td rowspan="${listOrder?size}"><input type="text" class="" name="txtContractNumber" placeholder="${uiLabelMap.ContractNumber}" /></td>
								<td><input type="text" class="numbers" onchange="txtValueChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtValue_${order_index}" value="${order.grandTotal?string("##,000")}" /></td>
								<td><input type="text" class="" id="txtbrandName_${order_index}" value="${order.brandName}" /></td>
						</tr>
					<#else>
						<tr>
								<td>${index}</td>
								<td><input type="text" class="" id="txtinternalName_${order_index}" value="${order.internalName}" /></td>
								<td><input type="text" class="" id="txtCommodityCode_${order_index}" placeholder="${uiLabelMap.CommodityCode}" /></td>
								<td><input type="text" class="numbers" onchange="txtKARChange()" onkeypress='return event.charCode >= 48 && event.charCode <= 57' id="txtKAR_${order_index}" placeholder="KAR" /></td>
								<td><input type="text" class="numbers" onchange="txttxtquantityChange()" onkeypress='return event.charCode >= 48 && event.charCode <= 57' id="txtquantity_${order_index}" value="${order.quantity?string("##,000")}" /></td>
								<td><input type="text" class="" id="txtquantityUomId_${order_index}" value="${order.quantityUomId}" /></td>
								<td><input type="text" class="numbers" onchange="txtNetWeightChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtNetWeight_${order_index}" value="${order.productWeight?string("##,000.00##")}" /></td>
								<td><input type="text" class="numbers" onchange="txtGrossWeightChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtShipmentTotalWeight_${order_index}" value="${order.weight?string("##,000.00##")}" /></td>
								<td><input type="text" class="numbers" onchange="txtValueChange()" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtValue_${order_index}" value="${order.grandTotal?string("##,000")}" /></td>
								<td><input type="text" class="" id="txtbrandName_${order_index}" value="${order.brandName}" /></td>
						</tr>
				</#if>
				
			<#assign OneTurn = false />
	</#list>
	
		<tr class="color">
			<td colspan="2">${uiLabelMap.GrandTotal}</td>
			<td></td>
			<td><input type="text" class="numbers" name="txtKARTotal" id="txtKARTotal" /></td>
			<td><input type="text" class="numbers" name="txtquantityTotal" id="txtquantityTotal" value="${quantityTotal?string("##,000")}" /></td>
			<td></td>
			<td><input type="text" class="numbers" name="txttxtNetWeightTotal" id="txttxtNetWeightTotal" value="${productWeightTotal?string("##,000.00##")}" /></td>
			<td><input type="text" class="numbers" name="txtShipmentTotalWeightTotal" id="txtShipmentTotalWeightTotal" value="${weightTotal?string("##,000.00##")}" /></td>
			<td></td>
			<td><input type="text" class="numbers" name="txttxtValueTotal" id="txttxtValueTotal" value="${grandTotalAll?string("##,000")}" /></td>
			<td></td>
		</tr>
	
	</tbody>
</table>
</#if>
</div>
<div style="width: 100%;">
	<input type="button" style="float: right;" value="Print" name="btnPrint"/>
</div>
<input type="hidden" id="myList" name="myList" />
<input type="hidden" id="weightUnit" name="weightUnit" value="${weightUnit}" />
<input type="hidden" id="currencyUnit" name="currencyUnit" value = "${currencyUnit}"/>
</form>
<style>
#tableListAttachments {
	overflow-x: auto;
}

.input10 {
	width: 40px !important;
	font-size: 12px !important;
	height: 15px !important;
}
.input30 {
	width: 50px !important;
	font-size: 10px;
	height: 15px !important;
}
.input50 {
	width: 120px !important;
	height: 15px !important;
}
.input50Red {
	width: 120px !important;
	font-size: 10px;
	height: 15px !important;
	color: red !important;
}
.input40 {
	width: 80px !important;
	font-size: 10px;
	height: 15px !important;
}
.color {
	background: #dae5f4;
}
.color1 {
	background: #FFDEAD;
}
</style>
<script>
	$("input[name='btnPrint']").click(function() {
		var listProduct = new Array();
		for (var int = 0; int < listSize; int++) {
			var thisProduct = {};
			thisProduct.index = int + 1;
			thisProduct.txtinternalName = $("#txtinternalName_" + int).val();
			thisProduct.txtCommodityCode = $("#txtCommodityCode_" + int).val();
			thisProduct.txtKAR = $("#txtKAR_" + int).val();
			thisProduct.txtquantity = $("#txtquantity_" + int).val();
			thisProduct.txtquantityUomId = $("#txtquantityUomId_" + int).val();
			thisProduct.txtNetWeight = $("#txtNetWeight_" + int).val();
			thisProduct.txtShipmentTotalWeight = $("#txtShipmentTotalWeight_" + int).val();
			thisProduct.txtValue = $("#txtValue_" + int).val();
			thisProduct.txtbrandName = $("#txtbrandName_" + int).val();
			listProduct.push(thisProduct);
		}
		var param =  JSON.stringify(listProduct);
		$("#myList").val(param);
		$("input[name='btnPrint']").hide( "slow", function() {
			$("#CreateListAttachmentsPDF").submit();
		});
	});

var locale ='vi';
	$(document).ready(function () {

	});

	var listInputNumber = $('.numbers');
	for (var int = 0; int < listInputNumber.length; int++) {
		listInputNumber[int].addEventListener('blur', function () {
			this.setAttribute('type', 'text');
		    this.value = String(this.value).toDecimalFormat();
		});
		listInputNumber[int].addEventListener('focus', function () {
			this.setAttribute('type', 'text');
			this.value = String(this.value).toDecimal();
		});
	}
	
	var listSize = '${listSize}';
	listSize = listSize.toInt();
	
	function txtValueChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtValue_" + int).val();
			if (value == "") {
				value = "0";
			}else {
				
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txttxtValueTotal").val(total.toDecimalFormat());
	}
	function txtNetWeightChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtNetWeight_" + int).val();
			if (value == "") {
				value = "0";
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txttxtNetWeightTotal").val(total.toDecimalFormat());
	}
	function txtGrossWeightChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtShipmentTotalWeight_" + int).val();
			if (value == "") {
				value = "0";
			}else {
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txtShipmentTotalWeightTotal").val(total.toDecimalFormat());
	}
	function txttxtquantityChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtquantity_" + int).val();
			if (value == "") {
				value = "0";
			}else {
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txtquantityTotal").val(total.toDecimalFormat());
	}
	function txtKARChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtKAR_" + int).val();
			if (value == "") {
				value = "0";
			}else {
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txtKARTotal").val(total.toDecimalFormat());
	}
</script>