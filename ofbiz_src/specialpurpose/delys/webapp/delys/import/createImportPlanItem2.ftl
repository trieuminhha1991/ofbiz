<#assign productName = productName!>
<table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
      <thead>
			<tr class="sf-product">
                	<td>Nhan Hang:</td>
                	<td><b>${productName}</b></td>
                	<td>dvt: ${packing}</td>
                	<td colspan="3">Quy cach pallet</td>
                	<td>${pallet}</td>
            </tr>
            <tr class="sf-product-child">
                	<td></td>
                	<td>Sales order</td>
                	<td>Import volume</td>
                	<td>Ton thang truoc</td>
                	<td>Ton Thang nay</td>
                	<td>Ton du bao</td>
                	<td>Luong ton kho quy doi <br/> thanh so ngay ban hang cua Sales(ngay)</td>
            </tr>
       </thead>
       <tbody>
            <#list listProductSalesForcast as month>
            	<tr>
            		<td>${month.periodName}-${year}</td>
            		<#if month.quantity != 0>
            		<td>${month.quantity?string(",##0")}</td>
					<td onclick = 'hoanm()'>${month.planImport?string(",##0")}</td>
            		<#if month.tonTruoc != -1>
            		<td>${month.tonTruoc?string(",##0")}</td>
            		<#else>
            		<td>0</td>
            		</#if>
            		<td>${month.inventoryOfMonth?string(",##0")}</td>
            		<td>${month.tonCuoiThang?string(",##0")}</td>
            		<td>${month.banCuaNgay?string(".##")}</td>
            		<#else>
					<td>0</td>
					<td>0</td>
					<#if month.tonTruoc != -1>
            		<td>${month.tonTruoc?string(",##0")}</td>
            		<#else>
            		<td>0</td>
            		</#if>
            		<td>${month.inventoryOfMonth?string(",##0")}</td>
            		<td>0</td>
            		<td>0</td>
            		</#if>
            		
            	</tr>
            </#list>
       </tbody>
</table>