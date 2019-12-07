<style>
.larger {
	width: 100% !important;
}
#myButton {
	visibility: hidden;
}
.count {
	background-color: #dff0d8;
	font-size: 100%;
}
.total {
	background-color: #fcf8e3;
	font-size: 120%;
}
.totalPrice {
 font-weight: bolder;
 font-size: 100%;
}
td {
	font-size: 130%;
}
td label {
	font-size: 130%;
}
input[type=text] {
  font-size: 97%;
}
body {
  -webkit-user-select: none;
     -moz-user-select: -moz-none;
      -ms-user-select: none;
          user-select: none;
}
.textareaLarger {
	  margin: 10px 0px !important;
		width: 944px !important;
		height: 121px;
}
.textareaLarger2 {
	  margin: 10px 0px !important;
		width: 889px !important;
		height: 121px;
}
</style>
<script type="text/javascript">
function newButton() {
	setTimeout(function(){ $("#myButton").html("<button class='btn btn-primary btn-small'><i class='icon-ok'></i>${uiLabelMap.CreateToPDF}</button>"); }, 3000);
	
}
var count = 0;
function renderTable(totalWeightAll, totalPriceAll) {
	var listError = [];
	table = "";
	table += "<table class='table-bordered table dataTable' cellspacing='0'><thead><tr role='row' class='header-row'><th class='hidden-phone'>No.</th><th class='hidden-phone'>" + "${uiLabelMap.description}" + "</th><th class='hidden-phone'>" + "${uiLabelMap.PackingUnit}" + "</th><th class='hidden-phone'>" + "${uiLabelMap.QuantityImport}" + "</th><th class='hidden-phone'>" + "${uiLabelMap.PriceUnit} (" + currencyUomId + ")</th><th class='hidden-phone'>" + "${uiLabelMap.QuantityWeight}" + "</th><th class='hidden-phone'>" + "${uiLabelMap.UnitWeight}" + "</th><th class='hidden-phone'>" + "${uiLabelMap.totalPrice}(" + currencyUomId + ")</th></tr></thead><tbody>";
	for ( var x in listProductInMonths) {
		var primaryProductCategoryId = listProductInMonths[x].primaryProductCategoryId;
		count  = parseInt(x) + 1;
		table +=  "<tr><input type='hidden' id ='quantityUomId" + count + "' value='" + listProductInMonths[x].quantityUomId +"'><input type='hidden' id ='productId" + count + "' value='" + listProductInMonths[x].productId +"'><td class='count'>" + count + "</td><td><b>" + primaryProductCategoryId + "</b> " + listProductInMonths[x].internalName + "</td><td>" + listProductInMonths[x].productPackingUomId + "</td><td><input type='text' onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id='txtImport" + count + "' onkeyup='txtImportChange(" + count + ")' value='" + listProductInMonths[x].quantityImport + "'/></td><td><label id='txtPrice" + count + "'>" + listProductInMonths[x].lastPrice + "</label></td><td id='totalWeight" + count + "'>" + listProductInMonths[x].totalWeight + "</td><td>" + listProductInMonths[x].weightUomId + "</td><td><label class='totalPrice' id='txtTotalPrice" + count + "'>" + listProductInMonths[x].totalPrice + "</label></td></tr>";
		if (listProductInMonths[x].totalWeight == 0) {
			var messageInfo = listProductInMonths[x].messageInfo;
			var thisError = new error(count, messageInfo);
			listError.push(thisError);
		}
	}
	table +=  "<tr class='total'><td></td><td><b>${uiLabelMap.Total} </b></td><td></td><td></td><td></td><td><b>" + totalWeightAll + "</b></td>" + "<td><b>" + listProductInMonths[1].weightUomId + "</b></td>" + "<td><label class='totalPrice' id='myTotal'>" + totalPriceAll + "</label></td></tr>";
	table += "</tbody></table>";
	$("#myTotalTable").html(table);
	setTimeout(function(){ renderError(listError), $("#myButton").css("visibility", "visible"); }, 500);
}
</script>
<form action="CreateQuotasPDF" method="POST">
<input type="hidden" name="currencyUomId" id ="currencyUomId">
<input type="hidden" name="totalWeightAll" id ="totalWeightAll">
<input type="hidden" name="totalPriceAll" id ="totalPriceAll">
<table class="table table-striped table-bordered dataTable" cellspacing="0">
	<tr id='namePlan'>
		<td>${uiLabelMap.QuotasName}: </td>
		<td colspan="5"><input name="txtNamePlan" id="txtNamePlan" type="text" required autofocus onchange="anyOneChange()"/></td>
	</tr>
	<tr id="SupplierChoice">
		<td>${uiLabelMap.Supplier}: </td>
		<td colspan="5">
		    <select name="supplier" id="supplier" size="1">
				<option>-Select-</option>
				<#list listSuppliers as listSupplier>
					<option>${listSupplier.partyId}</option>
				</#list>
			</select>
		</td>
	</tr>
	<tr id="dateChoice">
		<td>${uiLabelMap.Year} </td>
		<td id='111'>
		    <div style='float: left;' id='jqxYear'>
            </div>
		</td>
		<td>${uiLabelMap.Month} </td>
		<td colspan="1" id='222'>
			<div style='float: left;' id='jqxMonth'>
            </div>
		</td>
		
		<td><button class="btn btn-primary btn-small" id="btnCreate" onclick="btnCreateClick();return false;anyOneChange();"><i class='icon-ok'></i>${uiLabelMap.Create}</button></td>
		<td><button  class="btn btn-danger btn-small" onclick="btnCancelClick();return false;"><i class='icon-remove'></i>${uiLabelMap.Cancel}</button></td>
	</tr>
</table>
<input type='text' name= 'madeOn' id="madeOn">
<table padding= '15px' border= '1px solid blue' width= '650px'>
	<tr>
		<td><b>Between</b></td>
		<td>
			<table>
				<tr>
					<td colspan='2'><input class='larger' type='text' name= 'txtCompanyName1' required></td>
				</tr>
					<tr><td>Address: </td>
					<td><input type='text' name= 'txtAddress1'></td>
				</tr>
				<tr>
					<td>Tel: </td>
					<td><input type='text' name= 'txtTel1'></td>
				</tr>
				<tr>
					<td>Fax: </td>
					<td><input type='text' name= 'txtFax1'></td>
				</tr>
				<tr>
					<td colspan='2'><input class='larger' type='text' name= 'txtOther1'></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><b>And</b></td>
		<td>
			<table>
				<tr>
					<td colspan='2'><input class='larger' type='text' name= 'txtCompanyName2'></td>
				</tr>
				<tr>
					<td>Address: </td>
					<td><input type='text' name= 'txtAddress2'></td>
				</tr>
				<tr>
					<td colspan='2'><input class='larger' type='text' name= 'txtOther2'></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
	<h2>1. CONTRACT VALUE</h2>
<div id='myTotalTable'></div>
	<table>
		<tr>
			<td>Supplier bank: </td>
			<td><textarea rows="4" name="txtSupplierBank" class="textareaLarger"></textarea></td>
		</tr>
		<tr>
			<td>Beneficiary: </td>
			<td><textarea rows="4" name="txtBeneficiary" class="textareaLarger"></textarea></td>
		</tr>
	</table>
<h2>2. TERMS OF DELIVERY</h2>
	<table>
		<tr>
			<td>The date of shipment: </td>
			<td><textarea rows="4" name="txtTheDateOfShipment" class="textareaLarger2"></textarea></td>
		</tr>
		<tr>
			<td>Port of discharging: </td>
			<td><textarea rows="4" name="txtPortOfDischarging" class="textareaLarger2"></textarea></td>
		</tr>
	</table>
<h2>3. PACKING</h2>
<textarea rows="4" name="txtPacking" required class="textareaLarger"></textarea>
<h2>4. DOCUMENTATION</h2>
<textarea rows="4" name="txtDocumentation" class="textareaLarger"></textarea>
<h2>5. TRANSPORTATION</h2>
<textarea rows="4" name="txtTransportation" class="textareaLarger"></textarea><br/>
<input type="hidden" name="myList" id ="myList">
<div id="myButton">
<button class="btn btn-primary btn-small" onclick="storeProductToList();return false;"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
</div>
</form>