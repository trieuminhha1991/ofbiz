<script type="text/javascript" src="/salesmtlresources/js/distributor/Salesman.js?v=1.0.0"></script>

<div id="jqxwindowViewListSalesman" class='hide'>
	<div>${uiLabelMap.BSListSalesman}</div>
	<div style="overflow: hidden;">
	<div class='form-window-content' style="height: 350px;">
			<div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.DADistributor}:</div> <div class="distributorInfo jqxwindowTitle" style="display: inline-block;"></div></div>
			<div class="pull-right margin-bottom10">
				<a href='javascript:void(0)' onclick='AddSalesman.open()'><i class='icon-plus open-sans'></i>${uiLabelMap.accAddNewRow}</a>
				&nbsp;&nbsp;&nbsp;<a href='javascript:void(0)' onclick='Salesman._delete()' class='red'><i class='icon-trash open-sans'></i>${uiLabelMap.DmsDelete}</a>
			</div>
			<div id="jqxgridViewListSalesman"></div>
		</div>
		<div class="form-action">
			<button id="cancelViewListSalesman" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>


<div id="jqxwindowAddSalesman" class='hide'>
	<div>${uiLabelMap.BSListSalesman}</div>
	<div style="overflow: hidden;">
	<div class='form-window-content' style="height: 350px;">
			<div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.DADistributor}:</div> <div class="distributorInfo jqxwindowTitle"></div></div>
			<div id="jqxgridAddSalesman"></div>
		</div>
		<div class="form-action">
			<button id="cancelAddSalesman" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
			<button id="saveListSalesman" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationSalesman">
	<div id="notificationContentSalesman">
	</div>
</div>
