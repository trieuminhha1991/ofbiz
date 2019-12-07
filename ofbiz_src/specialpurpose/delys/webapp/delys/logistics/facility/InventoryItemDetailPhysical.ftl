<div>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.InventoryItemId}</th>
								<th class="center">${uiLabelMap.ProductId}</th>
								<th class="center">${uiLabelMap.DAQuantityOnHandTotal}</th>
								<th class="center">${uiLabelMap.DAAvailableToPromiseTotal}</th>
							</tr>
						</thead>
						<tbody>
						<#list listInventoryItem as ls>
							<tr>
								<td>${ls_index + 1}</td>
								<td>${ls.InventoryItemId?if_exists}</td>
								<td>${ls.ProductID?if_exists}</td>
								<td>${ls.QuantityOnHandTotal?if_exists}</td>
  								<td>${ls.AvailableToPromiseTotal?if_exists}</td>
							</tr>
						</#list>
						</tbody>
						
					</table>
</div>