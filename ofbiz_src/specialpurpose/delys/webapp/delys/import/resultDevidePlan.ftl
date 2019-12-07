<#if result?exists && listProduct?exists >
<input type="hidden" id="planId" value="${parameters.productPlanHeader}"></input>
<div id="mydiv">
<table id="devideTbl" class="table table-striped table-bordered table-hover">
	<thead>
	<tr class="sf-product">
		<td rowspan = "2">${uiLabelMap.PlanCode}</td>
		<td rowspan = "2">${uiLabelMap.Contract}</td>
		<#list listProduct as product>	
				<td colspan = "2" class="sf-months">${product.productId}</td>
		</#list>
		<td rowspan = "2">${uiLabelMap.ContainerQuantity}</td>
		
	</tr>
	<tr class="sf-product">
		<#list listProduct as product>	
			<td class="sf-months">${uiLabelMap.PalletQuantity}</td>
			<td class="sf-months">${uiLabelMap.ProductQuantity}</td>
		</#list>
	</tr>
	</thead>
	<tbody>
		<#list result.only as only>
			<tr>
				<td class = "planId">${parameters.productPlanHeader}</td>
				<td><a href="editPurchaseAgreement?orderMode=PURCHASE_ORDER" style="cursor: pointer;" class = "agreementId">HD</a></td>
					
					<#list listProduct as pro>
					<#if pro.productId == only.productId>
						<td class = "pallet">
							<input id = "txt2" type="text" class="pallet"/>
						</td>
						<td class="productId">
						<input disabled = "disabled" id = "txt"  class="quantity" type="text" value="${only.quantity}"/>
						<input type="hidden" class="product" value="${pro.productId}"/>
						<input type="hidden" class="seqItemId" value="${pro.seqItemId}"/>
						</td>
						<#else>
						<td class = "pallet">
							<input type="text" class="pallet"/>
						</td>
						<td class="productId"><input disabled = "disabled" class="quantity" type="text" value="0"/>
						<input type="hidden" class="product" value="${pro.productId}"/>
						<input type="hidden" class="seqItemId" value="${pro.seqItemId}"/>
						</td>
						
					</#if>
				</#list>
				<td>1</td>
			</tr>
		</#list>
		<#assign count= 0/>
		<#list result.more as more>
			<tr>
				<td class = "planId">${parameters.productPlanHeader}</td>	
				<td><a  href="editPurchaseAgreement?orderMode=PURCHASE_ORDER" class = "agreementId">HD</a></td>
				<#list listProduct as pro>
						<#assign value = 0/>
						<#assign thisPallet = 0/>
						<#list more.qq as qq>
							<#if pro.productId == qq.productId>
								<#assign value =qq.quantity />
								<#assign thisPallet = qq.remainPallet />
							</#if>
						</#list>

					<td class = "pallet">
							<input type="text" value="${thisPallet}" id="txtPalet${count}${pro.productId}" class="pallet" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' onkeyup='txtImportChange("${count}${pro.productId}")'/>
					</td>
					<td class="productId">
						
						<input class="quantity" id="txtQuantity${count}${pro.productId}" type="text" disabled = "disabled" value="${value}"/>
						<input type="hidden" class="product" value="${pro.productId}"/>
						<input type="hidden" class="seqItemId" value="${pro.seqItemId}"/>
						<input type="hidden" id="hddPacking${count}${pro.productId}" class="seqItemId" value="${pro.quantityConvert}"/>
					</td>
				</#list>
				<td>1</td>
			</tr>
		<#assign count = count + 1/>
		</#list>
		
	</tbody>
</table>
</div>
	<button class="btn btn-primary btn-small open-sans icon-save" id="saveLot">${uiLabelMap.Save}</button>


<script type="text/javascript">
	$('.agreementId').on('click', function(){
		//alert();
		var row = $(this).closest('tr');
		var val = row.find('.productId');
		
			var jsonarr = [];
		$.each(val, function(){
			var quantity = $(this).find('.quantity').val();
			var productId = $(this).find('.product').val();
			//alert('is:' +quantity + '' + productId);
			jsonarr.push({
				quantity: quantity,
				productId: productId
			});
		});
			//alert(jsonarr);		
		var json = [];
		json.push({
			pro: jsonarr
		});
		alert(JSON.stringify(jsonarr));

		$.ajax({
			url: 'devideToAgreement',
        	type: "POST",
        	data: {pro: JSON.stringify(jsonarr)},
        	success: function(res) {
        	//var json = res[data];
        	}
		});
		
	});
	
	$('#saveLot').on('click', function(){
		//alert();
		var i = 0;
		var json = [];
			$('#devideTbl tbody tr').each(function(){
				var cellProduct = $(this).find('.productId');
				var jsonarr = [];
				$.each(cellProduct, function(){
					var quantity = $(this).find('.quantity').val();
					var productId = $(this).find('.product').val();
					var seqItemId = $(this).find('.seqItemId').val();
					jsonarr.push({
					quantity: quantity,
					productId: productId,
					seqItemId: seqItemId
					});
					
				});		
					json.push({
					pro: jsonarr
				});
			});	
			alert($('#planId').val());	
			alert(JSON.stringify(json));
			$.ajax({
			url: 'createProductPlanAndLot',
        	type: "POST",
        	data: {pro: JSON.stringify(json), planId: $('#planId').val()},
        	success: function(res) {
        	//var json = res[data];
        	}
			});//end ajax
			
		});
		function txtImportChange(count) {
			var thisPacking = $("#hddPacking" + count).val();
			var txtPalet = $("#txtPalet" + count).val();
			var quantity = 0;
			if(txtPalet != ""){
				quantity = parseInt(thisPacking) * parseInt(txtPalet);
			}
			$("#txtQuantity" + count).val(quantity);
		}
</script>
<style>
#mydiv {
	width: 1140px;
    overflow-x: scroll;
}
</style>
</#if>
