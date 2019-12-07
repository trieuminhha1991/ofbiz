<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accFromUomId}
    				</div>
    				<div class='span7'>
    					<div id="uomId"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk' >
    					${uiLabelMap.accToUomId}
    				</div>
    				<div class='span7'>
						<div id="uomIdTo"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.CommonPurpose}
    				</div>
    				<div class='span7'>
    					<div id="purposeEnumId"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right asterisk'>
						${uiLabelMap.BACCBankConversion}
					</div>
                    <div class="span7">
                        <div id="bankId">
                            <div id="bankGrid"></div>
                        </div>
                    </div>
				</div>
				<#--<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accConversionFactor}
    				</div>
    				<div class='span7'>
    					<div id="conversionFactor"></div>
    				</div>
				</div>-->
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right asterisk'>
						${uiLabelMap.BACCPurchaseRate}
					</div>
					<div class='span7'>
						<div id="purchaseRate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right asterisk'>
						${uiLabelMap.BACCSellingRate}
					</div>
					<div class='span7'>
						<div id="sellingRate"></div>
					</div>
				</div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span5 align-right asterisk'>
                    ${uiLabelMap.BACCConversionFactor}
                    </div>
                    <div class='span7'>
                        <div id="conversionFactor"></div>
                    </div>
                </div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.fromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDate"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accThruDate}
    				</div>
    				<div class='span7'>
    					<div id="thruDate"></div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
<script src="/accresources/js/utils/date.utils.js"></script>
<script src="/accresources/js/setting/addForeignExchangeRates.js"></script>