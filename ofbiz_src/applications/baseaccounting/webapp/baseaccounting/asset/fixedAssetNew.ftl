<div id="addNewFixedAssetWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.CreateFixedAsset)}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<div class="row-fluid" >
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
					<ul class="wizard-steps wizard-steps-square">
						<li data-target="#step1" class="active">
					        <span class="step">1. ${uiLabelMap.BACCGeneralInfo}</span>
					    </li>
					    <li data-target="#step2">
					        <span class="step">2. ${uiLabelMap.BACCCalculateDepreciation}</span>
					    </li>
					    <li data-target="#step3">
					        <span class="step">3. ${uiLabelMap.BACCIncludedAccessories}</span>
					    </li>
					</ul>
				</div><!--#fuelux-wizard-->
				<div class="step-content row-fluid position-relative" id="step-container">
					<div class="step-pane active" id="step1">
						<div class="row-fluid" style="margin-top: 15px">
							<#include "fixedAssetNewGeneralInfo.ftl">
						</div>
					</div>
					<div class="step-pane" id="step2">
						<div class="row-fluid" style="margin-top: 15px">
							<#include "fixedAssetNewDepreciation.ftl">
						</div>
					</div>
					<div class="step-pane" id="step3">
						<div class="row-fluid" style="margin-top: 15px">
							<div id="assetAccompanyGrid"></div>
						</div>
					</div>
				</div>
				<div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.BACCSave)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
			</div><!-- ./row-fluid -->
		</div>
	</div>
</div>

<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetNew.js?v=1.0.2"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetNewStep1.js?v=0.0.4"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetNewStep2.js?v=0.0.2"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetNewStep3.js?v=0.0.2"></script>
