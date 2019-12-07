<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.BACCCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCAssetGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="assetGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCDepGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="accDepGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCdepGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="depGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCProfitGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="profitGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCLossGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="lossGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCFixedAssetTypeId}
    				</div>
    				<div class='span7'>
						<div id="fixedAssetTypeId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCFixedAssetId}
    				</div>
    				<div class='span7'>
						<div id="fixedAssetId">
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script src="/accresources/js/setting/addFixedAssetGlAccounts.js?v=1.0.0"></script>