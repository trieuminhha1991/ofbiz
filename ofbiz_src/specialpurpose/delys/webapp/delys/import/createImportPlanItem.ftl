<style>
#myTotalTable {
	width: 1117px;
    height: 450px;
    overflow: scroll;	
}
.inputSmall {
	width: 50px !important;
}
#myHistoryTable {
	visibility: hidden;
}
</style>
<div id="shakeNow">
<table class="table table-striped table-hover table-bordered dataTable" cellspacing="0">
	<tr id='namePlan'>
		<td>Name Plan </td>
		<td colspan="5"><input id="txtNamePlan" type="text" required autofocus/></td>
	</tr>
	<tr id="dateChoice">
		<td>From Date </td>
		<td id='111'>
		    <div style='float: left;' id='jqxYear'>
            </div>
		</td>
		<td>Thru Date </td>
		<td colspan="1" id='222'>
			<div style='float: left;' id='jqxMonth'>
            </div>
		</td>
		<td><button class="btn btn-warning" onclick="btnCancelClick()">Cancel</button></td>
		<td><button class="btn btn-primary" id="btnCreate" onclick="btnCreateClick()">Create</button></td>
	</tr>
	<tr id='productChoice'>
		<td>Category </td>
		<td>
			<select name="productCategoryId" id="ImportPlanHeader_productCategoryId" size="1">
				<option>-Select-</option>
				<#list listCategorys as listCategory>
					<option>${listCategory.productCategoryId}</option>
				</#list>
			</select>
		</td>
		<td>Product  </td>
		<td>
			<select name="productId" id="ImportPlanHeader_productId" size="1">
				
			</select>
		</td>
		<td>Packing </td>
		<td>
			<select name="productUom" id="ImportPlanHeader_productUom" size="1">
				
			</select>
		</td>
	</tr>
</table>
<div id="myTable"></div>
</div>
<div id="myTotalTable"></div>
</div>
<div id="myHistoryTable" class="modal fade"></div>
</div>