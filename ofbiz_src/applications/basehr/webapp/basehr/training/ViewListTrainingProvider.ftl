<#include "script/ViewListTrainingProviderScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'postalAddress', type: 'string'},
						{name: 'primaryPhone', type: 'string'},
						{name: 'emailAddress', type: 'string'},
						{name: 'websiteUrl', type: 'string'},
						{name: 'addressContactMechId', type: 'string'},
						{name: 'countryGeoId', type: 'string'},
						{name: 'stateProvinceGeoId', type: 'string'},
						{name: 'districtGeoId', type: 'string'},
						{name: 'wardGeoId', type: 'string'},
						{name: 'address1', type: 'string'},
						{name: 'emailContactMechId', type: 'string'},
						{name: 'phoneContactMechId', type: 'string'},
						{name: 'countryCode', type: 'string'},
						{name: 'areaCode', type: 'string'},
						{name: 'contactNumber', type: 'string'},
						{name: 'websiteContactMechId', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'timestamp'},
						{name: 'thruDate', type: 'date', other: 'timestamp'},
						]"/>
<script type="text/javascript">
<#assign columnlist = "
						{
						    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.TrainingProviderName)}', datafield: 'groupName', minwidth: 150, cellClassName: cellClass, pinned: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonAddress)}', datafield: 'postalAddress', cellClassName: cellClass, width: 150},
						{text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'primaryPhone', cellClassName: cellClass, width: 150},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonEmail)}', datafield: 'emailAddress', cellClassName: cellClass, width: 150},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonWebsite)}', datafield: 'websiteUrl', cellClassName: cellClass, width: 150},
						{ text: '${uiLabelMap.FromRelationship}', dataField: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy', cellClassName: cellClass, filtertype: 'range', editable:false, cellsalign: 'right',
							 cellsrenderer: function(row, column, value){
							 }, 
						},
						{ text: '${uiLabelMap.ThruRelationship}', dataField: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy', cellClassName: cellClass, filtertype: 'range', editable:false, cellsalign: 'right',
							 cellsrenderer: function(row, column, value){
							 }, 
						},
						"/>
</script>
<#assign customcontrol = "icon-filter open-sans@${uiLabelMap.HRCommonRemoveFilter}@javascript: void(0);@RemoveFilter()">
<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="false" showtoolbar="true"  
		 filterable="true" alternativeAddPopup="newTrainingProviderWindow" deleterow="false" editable="false" addrow="true"
		 url="jqxGeneralServicer?sname=JQListTrainingProvider" id="jqxgrid" removeUrl="" deleteColumn="" showlist="true"
		 updateUrl="" jqGridMinimumLibEnable="false" customcontrol1 = customcontrol  mouseRightMenu="true" contextMenuId="menuListTraining"
	/>
	
<div id='menuListTraining' style="display:none;">
	<ul>
    	<li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
    	<li><i class="fa fa-minus-square red"></i>${uiLabelMap.ExpiredRelationship}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="newTrainingProviderWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container' >
		<div class="form-window-content" style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.TrainingProviderName}</label>
							</div>
							<div class="span8">
								<input type="text" id="trainingProviderNameAddNew">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.CommonCountry}</label>
							</div>
							<div class="span8">
								<div id="countryGeoId"></div>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.CommonCity}</label>
							</div>
							<div class="span8">
								<div id="stateProvinceGeoId"></div>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
							</div>
							<div class="span8">
								<div id="districtGeoId"></div>
							</div>
						</div>		
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.PartyWardGeoId}</label>
							</div>
							<div class="span8">
								<div id="wardGeoId"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.CommonAddress1}</label>
							</div>
							<div class="span8">
								<input type="text" id="trainingProviderAddr">
							</div>
						</div>		
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.FromRelationship}</label>
							</div>
							<div class="span8">
								<div id="fromDate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.ThruRelationship}</label>
							</div>
							<div class="span8">
								<div id="thruDate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonEmail}</label>
							</div>
							<div class="span8">
								<input type="text" id="providerEmail">
							</div>
						</div>		
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.PhoneNumber}</label>
							</div>
							<div class="span8">
							<#--	 	<!-- <div class="span3">
											<input type="text" id="phoneCountryCode">
										</div>
										<div class="span3">
											<input type="text" id="phoneAreaCode">
										</div>
							-->
							<input type="text" id="phoneNbr">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonWebsite}</label>
							</div>
							<div class="span8">
								<input type="text" id="providerWebUrl">
							</div>
						</div>	
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner-ajax"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>	

<script type="text/javascript" src="/hrresources/js/training/ViewListTrainingProvider.js"></script>