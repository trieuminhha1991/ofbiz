<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.HROlbiusNewEmplPositionType}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<form action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" id="${formId}" class="form-horizontal" method="post">
			<input type="hidden" name="hasTable" value="N">				
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.EmplPositionTypeId}</label>
					<div class="controls">
						<input type="text" name="emplPositionTypeId" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_parentTypeId}</label>
					<div class="controls">
						<input type="text" name="parentTypeId" >
					</div>
				</div>					
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonDescription}</label>
					<div class="controls">
						<input type="text" name="description" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.DayLeaveRegulation}</label>
					<div class="controls">
						<input type="text" name="dayLeaveRegulation" class="required">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">
						&nbsp;  
					</label>
					<div class="controls">
						<button class="btn btn-small btn-primary"  type="submit">
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</div>					
		</form>
	</div>

</div>
<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
});
</script>