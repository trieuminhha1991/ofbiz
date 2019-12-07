<script src="/administrationresources/js/security/UserInGroup.js"></script>

<div id="jqxwindowUserInGroup" style="display:none;">
	<div>${uiLabelMap.ADListUser}</div>
	<div>
	
		<div class="pull-right margin-bottom10">
			<a href="javascript:void(0)" onclick="AddUserToGroup.open()"><i class="icon-plus open-sans"></i>${uiLabelMap.accAddNewRow}</a>
		</div>
	
		<div class="row-fluid" id="jqxgridUserInGroup"></div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
				<button id="btnCancelUserInGroup" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
				</div>
			</div>
		</div>
		
	</div>
</div>

<div id="contextMenuUserInGroup" style="display:none;">
	<ul>
		<li id="deleteUserInGroup"><i class="fa fa-trash red"></i>${uiLabelMap.CommonDelete}</li>
	</ul>
</div>

<#include "addUserToGroup.ftl"/>