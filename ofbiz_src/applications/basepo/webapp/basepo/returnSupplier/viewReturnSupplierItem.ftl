<style type="text/css">
	.profile-user-info-striped .profile-info-value {
		border-top: 1px solid #ddd;
	}
	.profile-user-info-striped .profile-info-name {
		color: #FFF;
		background: none repeat scroll 0 0 #438eb9;
	}
	.profile-user-info-striped {
		border: 1px solid #ddd;
	}
</style>

<div id="alterpopupWindowOrderItem" style="display:none">
	<div>${uiLabelMap.POSetOrderItem}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<h3 style="text-align: center;"><b>${uiLabelMap.POSetOrderItem}: <a id="orderIdReturn"></a></b></h3>
			<div id="orderItemForReturn">
			</div>
			<hr/>
			<div class="">
				<div class="pull-right form-window-content-custom">
					<button id="alterSaveReturn" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
					<button id="alterCancelReturn" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<hr style="margin: 0px; color: #4383b4;"/>

<div class="row-fluid">
	<div id="listReturnItem"></div>
</div><!--.row-fluid-->

<div id="menuReturnItem" style="display:none;">
	<ul>
		<li id="removeReturnItem"><i class="fa-trash" style="color: red;"></i>${uiLabelMap.DeleteRecord}</li>
	</ul>
</div>
<script>
<#if returnHeader.statusId != "SUP_RETURN_REQUESTED" || hasOlbPermission("MODULE", "RETURNPO", "DELETE")>
	$("#menuReturnItem").jqxMenu({disabled: true});
	$("#removeReturnItem").css("pointer-events", "none");
</#if>
</script>