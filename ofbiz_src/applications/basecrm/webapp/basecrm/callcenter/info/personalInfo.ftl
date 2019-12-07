<div class="info-container cio-custom" id="familyInfoEditable">
	<div class="cio-title"><i class="fa fa-user fa-lg" title="${uiLabelMap.BasicInfo}"></i></div>
	<div class="row-fluid margin-bottom5">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCustomerId}:</label>
				</div>
				<div class="span7">
					<input type="text" id="txtPartyCode" class="info-input no-margin no-space" tabindex="1"/>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label class="asterisk">${uiLabelMap.FullName}:</label>
				</div>
				<div class="span7">
					<input type="text" id="txtFulName" class="no-margin" tabindex="3"/>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid margin-bottom5">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCustomerType}:</label>
				</div>
				<div class="span7">
					<div id="BPartyId" style="margin-top: 3px;"></div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsOwnerEmployee}:</label>
				</div>
				<div class="span7">
					<#if security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
						<div id="OwnerEmployee"></div>
					<#else>
						<b id="OwnerEmployee"></b>
					</#if>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid margin-bottom5">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsPartyBirthDate}:</label>
				</div>
				<div class="span7">
					<div id="txtBirthDate" tabindex="4"></div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsPartyGender}:</label>
				</div>
				<div class="span7">
					<div id="txtGender" tabindex="5"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid margin-bottom5">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsIdentification}:</label>
				</div>
				<div class="span7">
					<input id="identification" type="number" class="info-input no-margin" tabindex="6" />
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsProvideDate}:</label>
				</div>
				<div class="span7">
					<div id="identityDate" tabindex="7"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid margin-bottom5">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.DmsProvidePlace}:</label>
				</div>
				<div class="span7">
					<div id="providePlace" tabindex="8"></div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.PartyDatasource}:</label>
				</div>
				<div class="span7">
					<input id="partyDataSource" class="no-space" tabindex="10"/>
				</div>
			</div>
		</div>
		<#--
		<div class="span6">
			<div class="row-fluid">
				<div class="span5">
					<label class="text-right">${uiLabelMap.Facebook}</label>
				</div>
				<div class="span7">
					<input id="txtFacebook" tabindex="9"/>
				</div>
			</div>
		</div>
		-->
	</div>
</div>