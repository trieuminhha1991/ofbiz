
<script type="text/javascript" src="/hrolbius/images/js/fuelux.spinner.js">
	$('#spinner1').ace_spinner({value:0,min:0,max:100,step:10, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
				.on('change', function(){
					//alert(this.value)
				});
</script>
	<div class="widget-main">
		<div class="row-fluid">
			<div class="step-content row-fluid position-relative">
				<form name="AddInternalPurchaseLimit" method="post"  action="<@ofbizUrl>createInternalPurchaseLimit</@ofbizUrl>">
					<div class="step-pane active" id="step1">
					   	<table>
					   		<tbody>
								<tr>
									<td class="paddng-right15"><label  for="emplPositionTypeId">${uiLabelMap.CommonEmplPositionTypeId}</label></td>
									<td>	
										<select name="emplPositionTypeId">
											  <#list listEmplPositionTypeId as list>
									           		<option value="${list.emplPositionTypeId}">${list.description?if_exists}</option>
									          </#list>
										</select>
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>		
								<tr>
									<td class="padding-right15" for="amountLimit"><label>${uiLabelMap.AmountLimit}</label></td>
									<td>
										<input name="amountLimit" style="width: 215px;"/>
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td class="padding-right15"><label  for="internalPurchasePrice">${uiLabelMap.InternalPurchasePrice}</label></td>
									<td>
										
										<div class="ace-spinner">
											<input type="text" name="internalPurchasePrice" class="input-mini spinner-input" id="spinner1" maxlength="3" style="width: 180px;">
											<div class="spinner-buttons btn-group btn-group-vertical">						
												<span class="btn spinner-up btn-mini btn-info">						
													<i class="icon-chevron-up"></i>						
												</span>						
												<span class="btn spinner-down btn-mini btn-info">				
													<i class="icon-chevron-down"></i>						
												</span>						
											</div>
										</div> 
										
										<lable style="font-size:20px;">(${uiLabelMap.InternalPurchasePriceCalculated})</lable>						
									</td>
								</tr>
								<tr>
									<td></td>
									<td>
										<button class="btn btn-small btn-primary" type="submit">
											 <i class="icon-ok"></i>
	              							${uiLabelMap.CommonCreate}
	          							</button>
          							</td>
								</tr>
							</tbody>
						</table>
					</div>
				</form>
			</div>
		</div>
	</div>