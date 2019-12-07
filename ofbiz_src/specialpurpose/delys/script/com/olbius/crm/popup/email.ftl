<div class="modal fade" id="emailForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title">${uiLabelMap.composeEmail}</h4>
      </div>
      <div class="modal-body">
        <form class="form-horizontal">
			<div class="control-group">
				<div class="controls">
					<input type="text" id="form-field-1" placeholder="Username">
				</div>
			</div>
		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">${uiLabelMap.close}</button>
        <button type="button" class="btn btn-primary">${uiLabelMap.send}</button>
      </div>
    </div>
  </div>
</div>