<#include "script/ViewJobPositionScript.ftl"/>

<#assign datafield ="[{name: 'partyId', type: 'string'},
					  {name: 'partyName', type: 'string'},
					  {name: 'emplPositionTypeId', type: 'string'},
					  {name: 'emplPositionTypeDesc', type: 'string'},
					  {name: 'totalEmplPositionId', type: 'number'},
					  {name: 'totalEmplPosNotFulfill', type: 'number'}]"/>

<script type="text/javascript">
	<#assign columnlist = "{text: '${uiLabelMap.HROrganization}', datafield: 'partyName', width: '22%'},
						   {text: '${uiLabelMap.HREmplPositionTypeId}', datafield: 'emplPositionTypeId', width: '20%'},
	   					   {text: '${uiLabelMap.CommonDescription}', datafield: 'emplPositionTypeDesc', width: '25%'},
						   {text: '${uiLabelMap.TotalEmplPositionId}', datafield: 'totalEmplPositionId', width: '14%', cellsalign: 'right', filterType : 'number'},
						   {text: '${uiLabelMap.TotalEmplPosNotFulfill}', datafield: 'totalEmplPosNotFulfill', cellsalign: 'right', filterType : 'number'}" />
   	
</script>	
<div class="row-fluid" id="jqxNotifyContainer">
	<div id="jqxNotify">
		<div id="ntfContent"></div>
	</div>
</div>			
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.JobPositionCurrent}</h4>
		<div class="widget-toolbar none-content">
			<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
				<button id="createEmplPosition" class="grid-action-button icon-plus open-sans" style="font-size: 14px !important"><span>${uiLabelMap.AddNewEmplPosition}</span></button>	
			</#if>	
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="margin-top: 5px;" class="pull-right">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
							<button id="removeFilter" class="grid-action-button icon-filter open-sans pull-right">${uiLabelMap.HRCommonRemoveFilter}</button>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
				<#assign mouseRightMenu = "true"/>
			<#else>	
				<#assign mouseRightMenu = "false"/>
			</#if>
			<@jqGrid url="" dataField=datafield columnlist=columnlist sortable="true"
				clearfilteringbutton="true" id="jqxgrid" initrowdetails="true" initrowdetailsDetail=rowsDetails filterable="true"
				editable="false" groupable="true"  groupsexpanded="false" rowdetailsheight="260" showlist="false"
				showtoolbar="false" deleterow="true" jqGridMinimumLibEnable="false" 
			/>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
	<div id="addNewPositionWindow" class="hide">
		<div>${uiLabelMap.addEmplPosition}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.EmplPositionAddNew}
						</label>
					</div>
					<div class="span7">
						<div id="positionTypeDropDownBtn">
							 <div style="border-color: transparent;" id="positionTypeGrid"></div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">
							${uiLabelMap.HRCommonQuantity}
						</label>
					</div>
					<div class="span7">
						<div id="quantityPosition"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.OrganizationMange}
						</label>
					</div>
					<div class="span7">
						<div id="dropDownButtonAddNew">
							<div id="jqxTreeAddNew">
		   					</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.PositionActualFromDate}
						</label>
					</div>
					<div class="span7">
						<div id="actualFromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">
							${uiLabelMap.CommonThruDate}
						</label>
					</div>
					<div class="span7">
						<div id="actualThruDate"></div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAjax"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
		   		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>
		   		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		   	</div>
		</div>
	</div>
</#if>

<#if security.hasEntityPermission("HR_DIRECTORY", "_CREATE", session)>	
	<div id="assignEmplPosFulWindow" class="hide">
		<div>${uiLabelMap.AssignPosForEmpl}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>	
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">
							${uiLabelMap.HrCommonPosition}
						</label>
					</div>
					<div class="span7">
						<input type="text" id="emplPositionTypeIdFulfill">
						<input type="hidden" id="emplPositionId">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.EmployeeName}
						</label>
					</div>
					<div class="span7">
						<input type="text" id="partyIdFulfill">
						<img alt="search" id="searchBtnId" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
							style="
							   border: #d5d5d5 1px solid;
							   padding: 5.5px;
							   border-bottom-right-radius: 3px;
							   border-top-right-radius: 3px;
							   margin-left: -4px;
							   background-color: #f0f0f0;
							   border-left: 0px;
							   cursor: pointer;
							"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.FromDateFulfillment}
						</label>
					</div>
					<div class="span7">
						<div id="fulfillFromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">
							${uiLabelMap.CommonThruDate}
						</label>
					</div>
					<div class="span7">
						<div id="fulfillThruDate"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
		   		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancelFulfillment">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>
		   		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSaveFulfillment">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		   	</div>
		</div>	
	</div>

</#if>		

<div id="jqxWindowPositionDetail" class="hide">
	<div id="windowHeader"></div>
	<div style="overflow: hidden;">
	 	<div id="jqxGridEmplPosition"></div>
	</div>
</div>

<#if security.hasEntityPermission("HR_DIRECTORY", "_CREATE", session)>
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</#if>

<script type="text/javascript" src="/hrresources/js/generalMgr/viewJobPosition.js"></script>
<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
	<script type="text/javascript" src="/hrresources/js/generalMgr/createNewJobPosition.js"></script>
</#if>
<#if security.hasEntityPermission("HR_DIRECTORY", "_CREATE", session)>
	<script type="text/javascript" src="/hrresources/js/generalMgr/createNewJobPositionFulfillment.js"></script>
</#if>