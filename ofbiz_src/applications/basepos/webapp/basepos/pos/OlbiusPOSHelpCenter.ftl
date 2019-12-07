<script type="text/javascript" src="/posresources/js/OlbiusPOSHelpCenter.js"></script>

<div id="jqxwindowHelpCenter" style="display:none;">
	<div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.POSListOfShortcuts}</div>
	<div>
		<div class="row-fluid form-window-content">
			<div class="span6">
				<#if hasOlbPermission("MODULE", "POS_ORDER", "VIEW")>
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSOrder}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F3</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSPayment}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F4</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSContinuePrint}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F5</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSChangeQuantity}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F6</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSChangeDiscount}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F8</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSCancelOrder}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F9</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSHoldCart}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F10</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSShowHoldCart}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>+/-</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSAdjustQuantity}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>Shift + Delete</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSRemoveProductInOrder}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + P</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSPrintOrder}</h5>
					</div>
				</div>
				<#if hasOlbEntityPermission("POS_ORDER_CTRL_S", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + S</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSCreateOrderAndShipment}</h5>
					</div>
				</div>
				</#if>
				<#if hasOlbEntityPermission("POS_ORDER_CTRL_H", "VIEW")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + H</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSSalesHistory}</h5>
					</div>
				</div>
				</#if>
				</#if>
				
				<#if hasOlbPermission("MODULE", "POS_CUSTOMER", "CREATE")>
				<hr>
				
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSCustomer}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<#if hasOlbEntityPermission("POS_CUSTOMER_F7", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>F7</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSCreateCustomer}</h5>
					</div>
				</div>
				</#if>
				</#if>
			</div>
			<div class="span6">
				
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSSearch}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F1</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSSearchProduct}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>F2</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSSearchCustomer}</h5>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>Alt + F2</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSAdvancedSearchCustomer}</h5>
					</div>
				</div>
				
				<#if hasOlbPermission("MODULE", "POS_RETURN", "CREATE")>
				<hr>
			
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSReturn}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<#if hasOlbEntityPermission("POS_RETURN_CTRL_ENTER", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + Enter</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSReturnReceiptsOrder}</h5>
					</div>
				</div>
				</#if>
				<#if hasOlbEntityPermission("POS_RETURN_CTRL_BACKSPACE", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + Backspace</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSReturnOrder}</h5>
					</div>
				</div>
				</#if>
				</#if>
				
				<#if hasOlbPermission("MODULE", "POS_PROMOTION", "VIEW")>
				<hr>
			
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSPromotions}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<#if hasOlbEntityPermission("POS_PROMOTION_CTRL_L", "VIEW")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + L</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSProductStorePromos}</h5>
					</div>
				</div>
				</#if>
				<#if hasOlbEntityPermission("POS_PROMOTION_CTRL_C", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + C</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSPromotionCode}</h5>
					</div>
				</div>
				</#if>
				</#if>
				
				<#if hasOlbPermission("MODULE", "POS_PAID", "CREATE")>
				<hr>
				
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSPaidInPaidOut}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<#if hasOlbEntityPermission("POS_PAID_CTRL_I", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + I</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSPaidIn}</h5>
					</div>
				</div>
				</#if>
				<#if hasOlbEntityPermission("POS_PAID_CTRL_O", "CREATE")>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + O</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSPaidOut}</h5>
					</div>
				</div>
				</#if>
				</#if>
				
				<hr>
				
				<div class="row-fluid">
					<div class="span8">
						<h2>${uiLabelMap.BPOSWorkingShift}</h2>
					</div>
					<div class="span4"></div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<h4>Ctrl + E</h4>
					</div>
					<div class="span8">
						<h5>${uiLabelMap.BPOSFinishWorkingShift}</h5>
					</div>
				</div>
			</div>
		</div>
			
		<div class="form-action">
			<div class="pull-right">
				<button id="btnCancelHelpCenter" tabindex="9" class="btn btn-danger form-action-button"><i class="fa icon-remove"></i> ${uiLabelMap.BSClose}</button>
			</div>
		</div>
	</div>
</div>