<form class="form-horizontal">
	<div class="control-group">
		<label class="control-label">${uiLabelMap.directionComm}:</label>
		<div class="controls">
			<select id="directionCommGeneral">
				<option value="send">${uiLabelMap.send}</option>
				<option value="receive">${uiLabelMap.receive}</option>
			</select>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">${uiLabelMap.CommunicationType}:</label>
		<div class="controls">
			<select id="supportGeneral"> <#if communicationEventTypes?exists>
				<#list communicationEventTypes as type>
				<option value="${type.communicationEventTypeId}">${type.description}</option>
				</#list> </#if>
			</select>
		</div>
	</div>
	<div class="control-group margin-top">
		<div style="padding: 0 10px;">
			<textarea id="issueGeneral" class="resize-none"></textarea>
		</div>
	</div>
</form>
