<div id="alterpopupWindow1" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="FeatureForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSProductFeatureCategoryId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="productFeatureCategoryIdAdd" />
						</div>
					</div>

					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSDescription}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="descriptionAdd" />
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.BSCancel}
						</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.BSSave}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script>
	var notEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var addNew = '${StringUtil.wrapString(uiLabelMap.BSAddNewFeatureType)}';
</script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/salesresources/js/setting/settingNewFeature.js"></script>