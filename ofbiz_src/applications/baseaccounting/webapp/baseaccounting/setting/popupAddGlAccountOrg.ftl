<div id="alterpopupWindow" style="display:none;" >
    <div>${uiLabelMap.BACCAssign}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
				<div class='row-fluid margin-top10'>
					<div class='span4 align-right asterisk'>
						${uiLabelMap.FormFieldTitle_glAccountId}
					</div>
					<div class='span8'>
						<div id="glAccountId2">
							<div id="jqxgridGlAccount"></div>
						</div>
					</div>
				</div>
    		</div>
    	</div>
        <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<#assign getAlGlAccount="getAll"/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/accresources/js/setting/addGlAccountOrg.js?v=1.0.1"></script>
