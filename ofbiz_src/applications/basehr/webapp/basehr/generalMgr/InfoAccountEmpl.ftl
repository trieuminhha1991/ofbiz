<div id="infoAccountEmployee" class="hide">
	<div>${uiLabelMap.HRInfoAccountEmployee}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<div class="row-fluid" style="position: relative;">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
							</div>
							<div class="span7">
							</div>
						</div>
						
						<hr style="margin: 10px 0 20px">	
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HREnabled}</label>
							</div>
							<div class="span7">
								<b><div id="enabledStatusAct" class="green-label" style="text-align: left;"></div></b>
							</div>
						</div>
						<div class='row-fluid margin-bottom10' id="datetimeContainer" value="hide">
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRDisabledDateTime}</label>
							</div>
							<div class="span7">
								<div id="btDisabledDateTime" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRHasLoggedOutStatus}</label>
							</div>
							<div class="span7">
								<div id="viewHasLoggedOut" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRRequirePassChange}</label>
							</div>
							<div class="span7">
								<div id="viewRequirePwdChange" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
					</div>
					
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
							</div>
							<div class="span7">
								<button type="button" class='btn btn-primary form-action-button pull-right' id="btnenabledStatusUnLock" value='hide'>
								<i class='fa fa-Unlock'></i>${uiLabelMap.HRUnLock}</button>
								<button type="button" class='btn btn-primary form-action-button pull-right' id="btnenabledStatusLock" value='show'>
								<i class='fa fa-lock'></i>${uiLabelMap.HRLock}</button>
							</div>
						</div>
						
						<hr style="margin: 10px 0 20px">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRUserLoginId}</label>
							</div>
							<div class="span7">
								<div id="viewUserLoginId" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRPartyCode}</label>
							</div>
							<div class="span7">
								<div id="viewPartyCode" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRLastLocale}</label>
							</div>
							<div class="span7">
								<div id="viewLastLocale" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRSuccessiveFailedLogins}</label>
							</div>
							<div class="span7">
								<div id="viewSuccessiveFailedLogins" class="green-label" style="text-align: left;"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelChangeEnableStatus" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		<#--	<button type="button" class='btn btn-primary form-action-button pull-right' id="saveChangeEnableStatus">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button> -->
		</div>
	</div>
</div>
<#include "EditDateTimeDisable.ftl"/>
<script type="text/javascript" src="/hrresources/js/generalMgr/InfoAccountEmpl.js?v=0.0.1"></script>