<div id="infoForm" class="margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class="asterisk ">${uiLabelMap.BIETestEventType}</span>
					</div>
					<div class="span7">
						<div id="productEventType"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class='span5 align-right'>
						<span class="">${uiLabelMap.BIETestEventCode}</span>
					</div>
					<div class="span7">
						<input id="eventCode"></input>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class='span5 align-right'>
						<span class="">${uiLabelMap.BIETestEventName}</span>
					</div>
					<div class="span7">	
						<input id="eventName"></input>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class='span5 align-right'>
						<span class=" asterisk">${uiLabelMap.BIEAgreement}</span>
					</div>
					<div class="span7">
						<div id="agreement">
							<div id="jqxGridAgreement"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class='span5 align-right'>
						<span class=" asterisk">${uiLabelMap.BIEPackingList}</span>
					</div>
					<div class="span7">
						<div id="packingList">
							<div id="jqxGridPackingList"></div>
						</div>
			   		</div>
				</div>
			</div>
			
			<div class="span6">
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span class=" asterisk">${uiLabelMap.BIEExecutedDate}</span>
					</div>
					<div class="span7">
						<div id="executedDate"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class='span3 align-right'>
						<span class="">${uiLabelMap.BIECompletedDate}</span>
					</div>
					<div class="span7">
						<div id="completedDate"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top5'>
					<div class="span3 align-right"><span class="">${uiLabelMap.Description}</span></div>
					<div class="span7"><textarea id="description" name="description" data-maxlength="250" rows="5" style="resize: vertical; margin-top:0px" class="span12"></textarea></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div>
	<div id="product">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
		<div id="jqxGridProduct">
		</div>
	</div>
	<script type="text/javascript" src="/imexresources/js/product/newProductEventProduct.js"></script>
</div>