<h3>${uiLabelMap.createRequestPayment}</h3>
<div class="row-fluid">
	<form class="form-horizontal" />
		<div class="control-group">
			<label class="control-label" for="cooler">${uiLabelMap.chooseCooler}</label>
			<div class="controls">
				<select id="cooler" data-placeholder="${uiLabelMap.chooseCooler}" class="span6"  autocomplete="off">
					<#if listCooler?has_content>
						<#list listCooler as cooler>
							<option value="${cooler.fixedAssetId}">${cooler.fixedAssetName + " - serial: " + cooler.serialNumber}</option>
						</#list>
					</#if>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">${uiLabelMap.cashAdvance}</label>
			<div class="controls">
				<input id="cashAdvance" placeholder="" type="number" class="span6"  autocomplete="off"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">${uiLabelMap.neededDate}</label>
			<div class="controls">
				<div class="calendaral">
					<input id="neededDate" autocomplete="off"/>
					<i class="icon-calendar calendarInput">&nbsp;</i>
				</div>
			</div>
		</div>
		<div class="form-actions">
			<div class="notification">
				<div id="success" class="notify-text">${uiLabelMap.sendRequestSuccess}</div>
				<div id="error" class="notify-text">${uiLabelMap.sendRequestError}</div>
			</div>
			<button class="btn btn-info" type="button" id="submitRequest" style="float: right">
				<i class="icon-ok bigger-110"></i>
				${uiLabelMap.sendRequest}
			</button>
		</div>
	</form>
</div>