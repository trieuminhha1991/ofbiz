<#include 'script/listLocationFacilityTypeScript.ftl'>
<script>
	<#assign locationFacilityTypes = delegator.findList("LocationFacilityType", null, null, null, null, false) />
	
	var locationFacilityTypeDataSoure = [
   		<#if locationFacilityTypes?exists>
   			<#list locationFacilityTypes as item>
   				{
   					locationFacilityTypeId: "${item.locationFacilityTypeId?if_exists}",
   					description: "${item.description?if_exists}",
   				},
   			</#list>
   		</#if>
   	];
	
	var mapLocationFacilityTypeData = {
			<#if locationFacilityTypes?exists>
				<#list locationFacilityTypes as item>
					<#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
					"${item.locationFacilityTypeId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
</script>

<div>
	<div id="contentNotification" style="width:100%">
	</div>
	<div id="jqxTreeGirdLocation">
	</div>
	<div id='Menu' class="hide" style="width: 150px !important">
		<ul>
		    <li><i class="fa-edit open-sans"></i> ${uiLabelMap.Edit}</li>
		    <li><i class="fa-trash open-sans" style="color: rgb(229, 2, 2)"></i> ${uiLabelMap.Delete}</li>
		</ul>
	</div>
</div>

<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.CreateNewLocationType}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LocationType}: </label>
					</div>
					<div class="span7">
						<input id="locationFacilityTypeId">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.DirectlyUnder}</label>
					</div>
					<div class="span7">
						<div id="parentLocationFacilityTypeId"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.BLRepresentCharacters}</label>
					</div>
					<div class="span7">
						<input id="prefixCharacters"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.BLDefaultChildNumber}</label>
					</div>
					<div class="span7">
						<div id="defaultChildNumber"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.Description}</label>
					</div>
					<div class="span7">
						<input id="description"></input>
					</div>
				</div>
			</div>
	    </div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>	

<div id="jqxNotification" >
	<div id="notificationContent"> 
	</div>
</div>

<div id="jqxNotificationWindow" >
	<div id="notificationContentWindow">
	</div>
</div>