<div class="info-container cio-custom">
	<h4 class="smaller cio-title"><i class="fa fa-money"></i>${uiLabelMap.CustomerAccount}</h4>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid">
				<div class="span8 text-algin-right">
					<label>${uiLabelMap.TotalPayableCustomer}&nbsp; <i class="fa fa-money"></i></label>
				</div>
				<div class="span4" id="totalPayable">${totalPayable?if_exists}</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span8 text-algin-right">
					<label>${uiLabelMap.TotalReceivableCustomer}&nbsp; <i class="fa fa-credit-card"></i></label>
				</div>
				<div class="span4" id="totalReceivable">${totalReceivable?if_exists}</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid">
				<div class="span8 text-algin-right">
					<label>${uiLabelMap.TotalLiability}&nbsp; <i class="fa fa-exchange"></i></label>
				</div>
				<div class="span4" id="totalLiability">${totalLiability?if_exists}</div>
			</div>
		</div>
	</div>
</div>

<div class="info-container cio-custom">
	<h4 class="smaller cio-title"><i class="fa fa-flag"></i>${uiLabelMap.Loyalty}</h4>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid">
				<div class="span8">
					<label>${uiLabelMap.BSCustomerType}&nbsp;&nbsp;<i class="fa fa-key"></i></label>
				</div>
				<div class="span4" id="customerType"></div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span8">
					<label>${uiLabelMap.TotalLoyaltyPoint}&nbsp;&nbsp;<i class="fa fa-database"></i></label>
				</div>
				<div class="span4" id="totalLoyalty">${loyaltyPoint?if_exists}</div>
			</div>
		</div>
	</div>
</div>