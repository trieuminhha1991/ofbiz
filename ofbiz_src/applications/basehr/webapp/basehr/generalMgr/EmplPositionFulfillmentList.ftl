<div id="positionFulfillmentWindow" class="hide">
	<div>${uiLabelMap.EmplPositionList}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.EmployeePartyIdTo}:</label>
							</div>
							<div class='span7'>
								<div class="row-fluid">
									<label style="color: #037c07; font-weight: bold;" id="positionFulfillPartyCode"></label>
								</div>
					   		</div>
						</div>
					</div><!-- ./span6 -->
					<div class="span6">
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.EmployeeName}:</label>
							</div>
							<div class='span7'>
								<div class="row-fluid">
									<label style="color: #037c07; font-weight: bold;" id="positionFulfillPartyName"></label>
								</div>
					   		</div>
						</div>
					</div><!-- ./span6 -->
				</div>
			</div>
			<div class="row-fluid">
				<div id="positionFulfillmentGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="closePositionFulfillmentList">
				<i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>	
</div>
<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
<div id="positionListMenu" class="hide">
	<ul>
		<li action="expireImmediately">
			<i class="fa fa-clock-o"></i>${uiLabelMap.ThruDateImmediately}
        </li>
		<li action="expireOnDay">
			<i class="fa fa-calendar"></i>${uiLabelMap.ThruDateOnDay}....
        </li>
	</ul>
</div>
</#if>
<div id="expirationDateWindow" class="hide">
	<div>${uiLabelMap.HRSelectCommon}...</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HRCommonThruDate}</label>
				</div>
				<div class='span7'>
					<div class="row-fluid">
						<div id="expirationDatePosition"></div>
					</div>
		   		</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right open-sans' id="cancelExpireEmplPosition">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right open-sans' id="saveExpireEmplPosition">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/hrresources/js/generalMgr/EmplPositionFulfillmentList.js?v=0.0.2"></script>