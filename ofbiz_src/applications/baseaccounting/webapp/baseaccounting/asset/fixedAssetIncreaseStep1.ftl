<form class="form-horizontal form-window-content-custom" id="fixedAssetIncreaseStep1">
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BSSupplier}</label>
					</div>
					<div class="span8">
						<div id="supplierDropDown">
							<div id="supplierGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.CommonAddress1}</label>
					</div>
					<div class="span8">
						<input id="address" type="text"/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.POCommunicationPartyId}</label>
					</div>
					<div class="span8">
						<input id="contactPerson" type="text"/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span8">
						<input id="description" type="text"/>
			   		</div>
				</div>
			</div><!-- ./span6 -->	
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.EmployeePuchasing}</label>
					</div>
					<div class="span7">
						<div id="employeeDropDown">
							<div id="employeeGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPayments}</label>
					</div>
					<div class="span7">
						<div id="paymentMethodTypeEnumId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPayeeName}</label>
					</div>
					<div class="span7">
						<input type="text" id="moneyReceiver">
			   		</div>
				</div>
				
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.PaidAccount}</label>
					</div>
					<div class="span7">
						<div id="accountPayer"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.ReceiveAccount}</label>
					</div>
					<div class="span7">
						<input type="text" id="accountReceiver">
			   		</div>
				</div>
			</div><!-- ./span6 -->	
		</div><!-- ./span12 -->		
	</div><!-- ./row-fluid -->
	<div class="legend-container">
		<span>${uiLabelMap.CommonVoucher}</span>
		<hr/>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.DateArising}</label>
					</div>
					<div class="span8">
						<div id="dateArising"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.PayableVoucherPaymentOrder}</label>
					</div>
					<div class="span8">
						<input type="text" id="paymentVoucherNbr">
			   		</div>
				</div>
			</div><!-- ./span6 -->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCDueDate}</label>
					</div>
					<div class="span7">
						<div id="dueDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCCurrencyUom}</label>
					</div>
					<div class="span7">
						<div id="currencyUomId"></div>
			   		</div>
				</div>
			</div><!-- ./span6 -->
		</div><!-- ./span12 -->
	</div><!-- ./row-fluid -->
</form>