<div id="BillingForm" class="margin-top10">
	<div class="span12 no-left-margin boder-all-profile">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class="asterisk">${uiLabelMap.BillNumber}</span>
					</div>
					<div class="span7">
						<input id="txtBillNumber"></input>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span5 align-right'>
						<span class="">${uiLabelMap.FromShippingLine}</span>
					</div>
					<div class="span7">	
						<div id="shippingParty">
							<div id="jqxGridPartyShipping">
							</div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span5 align-right'>
						<span class=" asterisk">${uiLabelMap.departureDate}</span>
					</div>
					<div class="span7">
						<div id="txtdepartureDate"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span5 align-right'>
						<span class=" asterisk">${uiLabelMap.arrivalDate}</span>
					</div>
					<div class="span7">
						<div id="txtarrivalDate"></div>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span class="">${uiLabelMap.Description}</span>
					</div>
					<div class="span7">
						<textarea id="billDescription" data-maxlength="250" rows="4" style="resize: vertical;margin-top:0px" class="span12"></textarea>
			   		</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/imexresources/js/import/createImportDocBillOfLading.js"></script>