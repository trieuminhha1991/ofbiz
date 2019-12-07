<div id="editFixedAssetDepreciationWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<form class="form-horizontal form-window-content-custom">
				<div class="row-fluid">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCDatePurchase}</label>
								</div>
								<div class="span7">
									<div id="datePurchase"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.DateOfIncrease}</label>
								</div>
								<div class="span7">
									<div id="dateOfIncrease"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCDateAcquired}</label>
								</div>
								<div class="span7">
									<div id="dateAcquired"></div>
						   		</div>
					   		</div>	
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCPurCostAcc}</label>
								</div>
								<div class="span7">
									<div id="costGlAccountId">
										<div id="costGlAccountGrid"></div>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class="asterisk">${uiLabelMap.BACCDepAccount}</label>
								</div>
								<div class="span7">
									<div id="accDepGlAccountId">
										<div id="accDepGlAccountGrid"></div>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class="asterisk">${uiLabelMap.BACCAllocGlAccoutId}</label>
								</div>
								<div class="span7">
									<div id="depGlAccountId">
										<div id="depGlAccountGrid"></div>
									</div>
						   		</div>
						   	</div>							
						</div><!-- ./span6 -->
					</div><!-- ./span12 -->
				</div><!-- ./row-fluid -->
				<div class="legend-container">
					<span>${uiLabelMap.OriginCostAndDepreciation}</span>
					<hr/>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCPurchaseCost}</label>
								</div>
								<div class="span7">
									<div id="purchaseCost">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCDepreciation}</label>
								</div>
								<div class="span7">
									<div id="lifeDepAmount"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCUsedTime}</label>
								</div>
								<div class="span7">
									<div id="usedPeriod" style="display: inline-block; float: left;"></div>
									<div id="usedQuantity" style="display: inline-block; float: left; margin-left: 2%;"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCYearlyDepRate}</label>
								</div>
								<div class="span7">
									<div id="yearlyDepRate"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCMonthlyDepRate}</label>
								</div>
								<div class="span7">
									<div id="monthlyDepRate"></div>
						   		</div>
							</div>
						</div><!--span6-->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCAnnualDepAmount}</label>
								</div>
								<div class="span7">
									<div id="annualDepAmount"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCMonthlyDepAmount}</label>
								</div>
								<div class="span7">
									<div id="monthlyDepAmount"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCAccumulatedDep}</label>
								</div>
								<div class="span7">
									<div id="accumulatedDep">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BACCRemainingValue}</label>
								</div>
								<div class="span7">
									<div id="remainingValue">
									</div>
						   		</div>
							</div>
						</div><!--span6-->
					</div><!--.span12-->
				</div><!--.row-fluid-->
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditFixedAssetDepre">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditFixedAssetDepre">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetEditDepreciation.js?v=1.0.2"></script>