<#assign count = 1/>
<#assign colum = 1/>
<#assign row = 1/>
<div id="mydiv">
<table id="devideTbl" class="table table-striped table-bordered table-hover">
	<thead>
	<tr class="sf-product">
		<td rowspan = "2">Calendar week</td>
		<td rowspan = "2">Month</td>
		<td rowspan = "2">So Container</td>
		<td rowspan = "2">So pallet con lai de chan container</td>
		<#list result.product as re>
			<td colspan = "2" class="sf-months">${re.productId}</td>
		</#list>
	</tr>
	<tr class="sf-product">
		<#list result.product as re>	
			<td class="sf-months">${uiLabelMap.PalletQuantity}</td>
			<td class="sf-months">${uiLabelMap.ProductQuantity}</td>
		</#list>
	</tr>
	</thead>
<tbody>
	<tr>
		<td></td>
		<td>${result.month.periodName}</td>
		<td>${result.container}</td>
		<td></td>
		<#list result.product as re>
			<td>${re.quantityPallet}</td>
			<td>${re.quantity}</td>
		</#list>
	</tr>
	<#list result.week as week>
	<#assign row = count/>
	<tr>
		<td>${week.weekOfYear}</td>
		<td><label id="week_${count}" customTime="${week.timeWeek.customTimePeriodId}">${week.timeWeek.periodName}</label></td>
		
		<#if week.product?size == 0>
			<td><label id="cont_${count}">0</label></td>
			<td><label id="re_${count}">0</label></td>
		<#else>
			<#assign sumPallet = 0/>
			<#list result.product as re>
				<#list week.product as pro>
					<#if re.productId == pro.productId>
						<#assign palletPro = (pro.planQuantity/re.quantityConvert)/>
						<#assign sumPallet = sumPallet +palletPro/>
					</#if>
				</#list>
			</#list>
			<#assign sumCont = (sumPallet/33)?floor/>
			<#assign sumRe = 33-(sumPallet%33)/>
			<td><label id="cont_${count}">${sumCont}</label></td>
			<#if sumRe == 33>
				<td><label id="re_${count}">0</label></td>
				<#else>
					<td><label id="re_${count}">${sumRe}</label></td>
			</#if>
		</#if>
		
		<#assign col = 1/>
		<#list result.product as re>
			<#assign colum = col/>
			<#if week.product?size == 0>
				<td>
					<input id="pallet_${col}_${count}" colunm = "${col}" class = "pallet" convert="${re.quantityConvert}" count = "${count}" productId = "${col}" type="text"/>
				</td>
				<td><label class = "quantity" quantityUomId = "${re.quantityUomId}" productId = "${re.productId}" id="${col}_${count}">0</label></td>
				<#else>
					<#list week.product as pro>
						<#if re.productId == pro.productId>
							<#assign pl = 0/>
							<#assign pl = (pro.planQuantity/re.quantityConvert) />
							<td>
								<input id="pallet_${col}_${count}" colunm = "${col}" value="${pl}" class = "pallet" convert="${re.quantityConvert}" count = "${count}" productId = "${col}" type="text"/>
							</td>
							<td><label class = "quantity" quantityUomId = "${re.quantityUomId}" productId = "${re.productId}" id="${col}_${count}">${pro.planQuantity}</label></td>
						</#if>
					</#list>
			</#if>
			<#assign col = col +1 />
		</#list>
	</tr>
	<#assign count = count +1 />
	</#list>
	<tr>
		<td colspan="4">Tong so container</td>
		<#assign colPallet = 1/>
		<#list result.product as re>
			<td colspan="2"><label id = "sumPallet_${colPallet}">0</label></td>
			<#assign colPallet = colPallet + 1/>
		</#list>
	</tr>
</tbody>
</table>
</div>
<button id="submit" class="btn btn-primary btn-small open-sans icon-ok">Submit</button>

<script type="text/javascript">
	
	$('.pallet').on('keypress', function(){
// alert(event.charCode);
// alert($('.pallet').val());
		return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0));
		
	});
	$('.pallet').on('keyup', function(){
// alert(event.charCode);
		var count = $(this).attr("count");
		var colunm = $(this).attr("colunm");
		var productId = $(this).attr("productId");
		var convert = $(this).attr("convert");
		var val = convert*$(this).val();
		$('#'+productId+'_'+count).text(val);
		var sum = 0;
		var arr = [];
		var arrCol = [];
		var sumCol = 0;
		for(var i = 1; i <= ${colum}; i++){
			arr[i-1] = 0;
			var pallet = $('#pallet_'+i+'_'+count).val();
			if($.trim($('#pallet_'+i+'_'+count).val()) != ''){
				arr[i-1] = pallet;	
			}	
		}
		
		for(var i=0;i<arr.length;i++){
        if(parseInt(arr[i]))
            sum += parseInt(arr[i]);
		}
		
		for(var j = 1; j<= ${row}; j++){
			arrCol[j-1] = 0;
			var pallet = $('#pallet_'+colunm+'_'+j).val();
			if($.trim($('#pallet_'+colunm+'_'+j).val()) != ''){
				arrCol[j-1] = pallet;	
			}
		}
// alert(pallet);
		for(var k=0;k<arrCol.length;k++){
	        if(parseInt(arrCol[k]))
	        	sumCol += parseInt(arrCol[k]);
		}
    	var cont = parseInt(sum/33);
    	var remain = 33-parseInt(sum%33);
    	if(remain == 33) remain = 0;
		$('#cont_'+count).text(cont);
		$('#re_'+count).text(remain);
		$('#sumPallet_'+colunm).text(sumCol);
// alert(sumCol);
		
// return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode ==
// 0));
		
	});
	
	$('#submit').on('click', function(){
		
		var jsonArr = [];
		for(var i = 1; i <= ${row}; i++){
			var productPlanHeader = ${productPlanHeader};
			var customTimePeriodId = $('#week_'+i).attr('customTime');
			var productPlanName = $('#week_'+i).text();
			var json = [];
			for(var j = 1; j <= ${colum}; j++){
				var quantity = $('#'+j+'_'+i).text();
				var productId = $('#'+j+'_'+i).attr('productId');
				var quantityUomId = $('#'+j+'_'+i).attr('quantityUomId');
// alert(productPlanName);
				json.push({
					quantity: quantity,
					productId: productId,
					quantityUomId: quantityUomId
				});
			}
			
			jsonArr.push({
				productPlanHeader: productPlanHeader,
				customTimePeriodId: customTimePeriodId,
				productPlanName: productPlanName,
				product: json
			});
			
		}
		
		$.ajax({
			url: 'createImportPlanWeek',
        	type: "POST",
        	data: {json: JSON.stringify(jsonArr)},
        	success: function(data) {
        	// var json = res[data];
        	}
			})// end ajax
		
		
	});
	
	$(document).ready(function(){
		
		for(var i = 1; i <= ${colum}; i++){
			var sumCol2 = 0;
			var arrCol2 = [];
			for(var j = 1; j<= ${row}; j++){
				arrCol2[j-1] = 0;
				var pallet2 = $('#pallet_'+i+'_'+j).val();
				if($.trim($('#pallet_'+i+'_'+j).val()) != ''){
					arrCol2[j-1] = pallet2;	
				}
			}
	// alert(pallet);
			for(var k=0;k<arrCol2.length;k++){
		        if(parseInt(arrCol2[k]))
		        	sumCol2 += parseInt(arrCol2[k]);
			}
			

			$('#sumPallet_'+i).text(sumCol2);
		}
		
	});
	
	
</script>