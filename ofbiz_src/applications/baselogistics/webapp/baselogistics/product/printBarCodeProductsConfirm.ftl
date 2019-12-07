<#include 'script/printBarCodeProductsConfirmScript.ftl'/>
<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description">
			<div class='span12'>
				<div class='row-fluid'>
					<div class='span6'>
						<div class='row-fluid'>
							<div class='span4'>
								<div style='font-weight: bold'>${uiLabelMap.DisplayInfo}:</div>
							</div>
							<div class='span8'>
								<div id='displayInfo'></div>
							</div>
						</div>
					</div>
					<div class='span6'>
						<div class='row-fluid'>
							<div class="span4">
								<span style='font-weight: bold'>${uiLabelMap.PageSize}:</span>
							</div>
							<div class='span8'>
								<div id='pageSize'></div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class="row-fluid margin-top10">
						<div id = "jqxgridProductBarCodeConfirm"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>