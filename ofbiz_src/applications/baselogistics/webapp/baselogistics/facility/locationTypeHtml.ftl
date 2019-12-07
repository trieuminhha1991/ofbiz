<div>
	<div id="contentNotificationDeleteLocationFacilityTypeError" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCheckDescription" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeDeleteSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateError" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateErrorParent" style="width:100%">
	</div>
	<div id="contentNotificationSelectTreeGird" style="width:100%">
	</div>
	<div id="jqxTreeGirdLocationFacilityType">
	</div>
	<div id='Menu' class="hide" style="width: 150px !important">
		<ul>
		    <li><i class="fa-edit open-sans"></i> ${uiLabelMap.Edit}</li>
		    <li><i class="fa-trash open-sans" style="color: rgb(229, 2, 2)"></i> ${uiLabelMap.Delete}</li>
		</ul>
	</div>
</div>

<div id="alterpopupWindow" class='hide popup-bound'>
	<div>${uiLabelMap.CreateNewLocationType}</div>
	<div>
		<div id="contentNotificationCreateLocationFacilityType" class="popup-notification">
		</div>
		<div id="contentNotificationCreateLocationFacilityTypeExits" class="popup-notification">
		</div>
		<div id="contentNotificationCreateLocationFacilityTypeError" class="popup-notification">
		</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10 margin-top20'>
			   		<div class='span4 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.LocationType)}</div>
					</div>  
					<div class="span7">
						<input id="locationFacilityTypeId">
						</input>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span4 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.LocationParentType)}</div>
					</div>  
					<div class="span7">
						<div id="parentLocationFacilityTypeId"></div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span4 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Description)}</div>
					</div>  
					<div class="span7">
						<input id="description"></input>
			   		</div>
			   	</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="alterpopupWindowEdit" class='hide popup-bound'>
	<div>${uiLabelMap.EditLocationTypeInfo}</div>
	<div>
		<div id="contentNotificationEditLocationFacilityTypeError" class="popup-notification">
		</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
		        <div class="row-fluid margin-top20">
					<div class='row-fluid margin-bottom8 padding-top8'>
				   		<div class='span4 text-algin-right'>
							<div class="asterisk">${StringUtil.wrapString(uiLabelMap.LocationType)}:</div>
						</div>  
						<div class="span7">
							<input id="locationFacilityTypeIdEdit">
							</input>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom8 padding-top8'>
				   		<div class='span4 text-algin-right'>
							<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.LocationParentType)}:</div>
						</div>  
						<div class="span7">
							<div id="parentLocationFacilityTypeIdEdit"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom8 padding-top8'>
				   		<div class='span4 text-algin-right'>
							<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Description)}:</div>
						</div>  
						<div class="span7">
							<input id="descriptionEdit"></input>
				   		</div>
				   	</div>
				   	<div class="form-action popup-footer">
						<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotificationCreate" >
	<div id="notificationContentCreate">
	</div>
</div>

<div id="jqxNotificationEditLocationFacilityTypeError" >
	<div id="notificationContentEditLocationFacilityTypeError">
	</div>
</div>

<div id="jqxNotificationCreateSuccess" >
	<div id="notificationContentCreateSuccess">
	</div>
</div>

<div id="jqxNotificationCreateError" >
	<div id="notificationContentCreateError">
	</div>
</div>

<div id="jqxNotificationCreateExists" >
	<div id="notificationContentCreateExists">
	</div>
</div>

<div id="jqxNotificationUpdateSuccess" >
	<div id="notificationContentUpdateSuccess">
	</div>
</div>

<div id="jqxNotificationDeleteSuccess" >
	<div id="notificationContentDeleteSuccess">
	</div>
</div>

<div id="jqxNotificationUpdateError" >
	<div id="notificationContentUpdateError">
	</div>
</div>

<div id="jqxNotificationUpdateErrorParent" >
	<div id="notificationContentUpdateErrorParent">
	</div>
</div>

<div id="jqxMessageNotificationSelectMyTree">
	<div id="notificationContentSelectMyTree">
	</div>
</div>

<div id="jqxMessageNotificationCheckDescription">
	<div id="notificationContentCheckDescription">
	</div>
</div>

<div id="jqxNotificationDeleteLocationFacilityTypeError">
	<div id="notificationContentDeleteLocationFacilityTypeError">
	</div>
</div>