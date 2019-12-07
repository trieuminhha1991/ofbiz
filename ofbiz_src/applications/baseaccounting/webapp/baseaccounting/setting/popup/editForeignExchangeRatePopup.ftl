<div id="editPopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formEdit">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accFromUomId}
    				</div>
    				<div class='span7'>
    					<div id="uomIdEdit"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk' >
    					${uiLabelMap.accToUomId}
    				</div>
    				<div class='span7'>
						<div id="uomIdToEdit"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.CommonPurpose}
    				</div>
    				<div class='span7'>
    					<div id="purposeEnumIdEdit"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right asterisk'>
						${uiLabelMap.BACCBankConversion}
					</div>
                    <div class="span7">
                        <div id="bankIdEdit">
                            <div id="bankGridEdit"></div>
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
						<div id="purchaseRateEdit"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right asterisk'>
						${uiLabelMap.BACCSellingRate}
					</div>
					<div class='span7'>
						<div id="sellingRateEdit"></div>
					</div>
				</div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span5 align-right asterisk'>
                    ${uiLabelMap.BACCConversionFactor}
                    </div>
                    <div class='span7'>
                        <div id="conversionFactorEdit"></div>
                    </div>
                </div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.fromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDateEdit"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accThruDate}
    				</div>
    				<div class='span7'>
    					<div id="thruDateEdit"></div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelEdit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
<script src="/accresources/js/setting/popup/editForeignExchangeRates.js"></script>