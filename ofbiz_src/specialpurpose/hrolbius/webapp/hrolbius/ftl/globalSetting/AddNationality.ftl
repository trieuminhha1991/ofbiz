<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1" >
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.CommonAdd} ${uiLabelMap.Nationality}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<form action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" id="${formId}" class="form-horizontal" method="post" name="${formId}">
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				
					<div class="controls">
						<input type="text" name="nationalityId" class="required">
					</div>
				</div>					
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonDescription}</label>
					<div class="controls">
						<input type="text" name="description" class="required">
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
	/* jQuery("div[role='dialog']").css("zIndex", "2000"); */
});
</script>