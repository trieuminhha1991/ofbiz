<div>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.ProductId}</th>
								<th class="center">${uiLabelMap.ProductProductName}</th>
								<th class="center">${uiLabelMap.Quantity}</th>
								<th class="center">${uiLabelMap.QuantityUomId}</th>
							</tr>
						</thead>
						<tbody>
						<#list listInventoryItem as ls>
							<tr>
								<td>${ls_index + 1}</td>
								<td>${ls.productId?if_exists}</td>
								<td>${ls.internalName?if_exists}</td>
								<td>${ls.quantity?if_exists}</td>
  								<td>
									${ls.uomId?if_exists}
									<#--
									<#list listUom as lsUom>
						           		<file value="${ls.uomId}">${lsUom.description}</option>
						       		</#list> -->
								</td>
							</tr>
						</#list>
						</tbody>
						
					</table>
</div>