<#include "script/ViewKeyPerfIndPartyTargetDetailScript.ftl"/>
<style>
.enableCell{
	color: #222222 !important;
}
.disableCell{
	color: #999 !important;
}
</style>
<#assign datafield = "[{name: 'partyTargetId', type: 'string'},
                       {name: 'keyPerfIndicatorId', type: 'string'},
                       {name: 'keyPerfIndicatorName', type: 'string'},
                       {name: 'weight', type: 'number'},
                       {name: 'target', type: 'number'},
                       {name: 'uomId', type: 'string'},
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.KeyPerfIndicator)}', datafield: 'keyPerfIndicatorName', width: '30%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}', datafield: 'weight', columntype: 'numberinput', width: '15%',
							cellsrenderer: function (row, column, value){
					 		   if(typeof(value) == 'number'){
					 			   return '<span>' + value + '%</span>'
					 		   }
					 	   }
					   },	
					   {text: '${StringUtil.wrapString(uiLabelMap.HRTarget)}', datafield: 'target', columntype: 'numberinput', width: '40%',
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span>' + formatNumber(value) + '</span>'
		            		   }
		            	   }
		               },
		               {text: '${StringUtil.wrapString(uiLabelMap.HRCommonMeasure)}', datafield: 'uomId', width: '15%', columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value){
					 		   for(var i = 0; i< globalVar.uomArr.length; i++){
					 			   if(value == globalVar.uomArr[i].uomId){
					 				   return '<span>' + globalVar.uomArr[i].abbreviation + '</span>';
					 			   }
					 		   }
					 		   return '<span>' + value + '</span>';
					 	    },
		               }
						"/>
</script>
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
	clearfilteringbutton="true" showlist="true" 
	addType="popup" alternativeAddPopup="AddNewKeyPerfIndicatorWindow" addrow="false" 
	editable="false" deleterow="false" 
	url="jqxGeneralServicer?sname=JQGetListKeyPerfIndPartyTargetItem&partyTargetId=${partyTargetId}" jqGridMinimumLibEnable="false"
 	updateUrl=""
	editColumns=""/>
					   
<div class="row-fluid" style="border-bottom: 1px dotted #e2e2e2; margin-bottom: 20px"></div>
<#if (listDirectChildDept?size > 0)>
<#assign datafieldOrg = "[{name: 'partyTargetId', type: 'string'},
	                       {name: 'keyPerfIndicatorId', type: 'string'},
	                       {name: 'partyId', type: 'string'},
	                       {name: 'partyCode', type: 'string'},
	                       {name: 'groupName', type: 'string'},
	                       {name: 'keyPerfIndicatorName', type: 'string'},
	                       {name: 'weight', type: 'number', other: 'Double'},
	                       {name: 'target', type: 'number'},
	                       {name: 'uomId', type: 'string'},
	                       {name: 'partyTargetName', type: 'string'},
						   ]"/>
<#assign columnlistOrg = "
						   {text: '${StringUtil.wrapString(uiLabelMap.DepartmentCode)}', datafield: 'partyCode', width: '11%'},
						   {text: '${StringUtil.wrapString(uiLabelMap.OrgUnitName)}', datafield: 'groupName', width: '18%'},
						   {text: '${StringUtil.wrapString(uiLabelMap.HRTarget)}', datafield: 'target', columntype: 'numberinput', width: '22%',
						   		filtertype: 'number',
			            	   cellsrenderer: function (row, column, value){
			            		   if(typeof(value) == 'number'){
			            			   return '<span>' + formatNumber(value) + '</span>'
			            		   }
			            	   }
			               },
			               {text: '${StringUtil.wrapString(uiLabelMap.HRCommonMeasure)}', datafield: 'uomId', width: '11%', columntype: 'dropdownlist',
								cellsrenderer: function (row, column, value){
						 		   for(var i = 0; i< globalVar.uomArr.length; i++){
						 			   if(value == globalVar.uomArr[i].uomId){
						 				   return '<span>' + globalVar.uomArr[i].abbreviation + '</span>';
						 			   }
						 		   }
						 		   return '<span>' + value + '</span>';
						 	    },
			               },
			               {text: '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}', datafield: 'weight', columntype: 'numberinput', width: '13%',
			               		filtertype: 'number',
								cellsrenderer: function (row, column, value){
						 		   if(typeof(value) == 'number'){
						 			   return '<span>' + value + '%</span>'
						 		   }
						 	   }
						   },	
			                {text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'partyTargetName'},
					"/>						   
<div class="row-fluid">
	<div class="widget-box">
		<div class="widget-header widget-header-flat">
			<h4 class="smaller"><i class="fa-hand-peace-o"></i>${uiLabelMap.TargetAllocationOrgization}</h4>
			<div class="widget-toolbar">
			</div>
		</div>
		<div class="widget-body" style="padding-top: 0px">
			<div style="position: relative;">
				<div class="row-fluid">
					<div class="span12" style="padding: 10px 5px">
						<#if keyPerfIndPartyTargetItemList?has_content>
							<#list keyPerfIndPartyTargetItemList as keyPerfIndPartyTargetItem>
								<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0)@addKeyPerfIndChildOrgObj.openWindow('${keyPerfIndPartyTargetItem.keyPerfIndicatorId}')"/>
								<div class="row-fluid">
									<#assign customTitleProperties = keyPerfIndPartyTargetItem.keyPerfIndicatorName/>
									<@jqGrid id="childOrg${keyPerfIndPartyTargetItem.keyPerfIndicatorId}" filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" 
										dataField=datafieldOrg columnlist=columnlistOrg  
										clearfilteringbutton="true" showlist="true" 
										customTitleProperties=customTitleProperties
										addrow="false" customcontrol1=customcontrol1
										editable="false" deleterow="false" 
										url="jqxGeneralServicer?sname=JQGetKeyPerfIndPartyTargetOfOrgChild&parentPartyTargetId=${partyTargetId}&keyPerfIndicatorId=${keyPerfIndPartyTargetItem.keyPerfIndicatorId}" 
										jqGridMinimumLibEnable="false"
									 	updateUrl=""
										editColumns=""/>
								</div>
								<#if keyPerfIndPartyTargetItem_has_next>
									<div class="row-fluid" style="border-bottom: 1px dotted #e2e2e2; margin-bottom: 20px"></div>
								</#if>
							</#list>
						</#if>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingAllocateOrg" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAllocateOrg"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<hr/>
</#if>

<#if (listDirectEmpl?size > 0)>
<#assign datafieldEmpl = "[{name: 'partyTargetId', type: 'string'},
	                       {name: 'keyPerfIndicatorId', type: 'string'},
	                       {name: 'partyId', type: 'string'},
	                       {name: 'partyCode', type: 'string'},
	                       {name: 'fullName', type: 'string'},
	                       {name: 'keyPerfIndicatorName', type: 'string'},
	                       {name: 'weight', type: 'number', other: 'Double'},
	                       {name: 'target', type: 'number'},
	                       {name: 'uomId', type: 'string'},
	                       {name: 'partyTargetName', type: 'string'},
						   ]"/>
<#assign columnlistEmpl = "
						   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '11%'},
						   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '18%'},
						   {text: '${StringUtil.wrapString(uiLabelMap.HRTarget)}', datafield: 'target', columntype: 'numberinput', width: '22%',
						   		filtertype: 'number',
			            	   cellsrenderer: function (row, column, value){
			            		   if(typeof(value) == 'number'){
			            			   return '<span>' + formatNumber(value) + '</span>'
			            		   }
			            	   }
			               },
			               {text: '${StringUtil.wrapString(uiLabelMap.HRCommonMeasure)}', datafield: 'uomId', width: '11%', columntype: 'dropdownlist',
								cellsrenderer: function (row, column, value){
						 		   for(var i = 0; i< globalVar.uomArr.length; i++){
						 			   if(value == globalVar.uomArr[i].uomId){
						 				   return '<span>' + globalVar.uomArr[i].abbreviation + '</span>';
						 			   }
						 		   }
						 		   return '<span>' + value + '</span>';
						 	    },
			               },
			               {text: '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}', datafield: 'weight', columntype: 'numberinput', width: '13%',
			               		filtertype: 'number',
								cellsrenderer: function (row, column, value){
						 		   if(typeof(value) == 'number'){
						 			   return '<span>' + value + '%</span>'
						 		   }
						 	   }
						   },	
			                {text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'partyTargetName'},
					"/>			
<div class="row-fluid" >
	<div class="widget-box">
		<div class="widget-header widget-header-flat">
			<h4 class="smaller"><i class="fa-hand-peace-o"></i>${uiLabelMap.TargetAllocationEmpl}</h4>
			<div class="widget-toolbar">
				
			</div>
		</div>
		<div class="widget-body" style="padding-top: 0px">
			<div style="position: relative;">
				<div class="row-fluid">
					<div class="span12" style="padding: 10px 5px">
						<#if keyPerfIndPartyTargetItemList?has_content>
							<#list keyPerfIndPartyTargetItemList as keyPerfIndPartyTargetItem>
								<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0)@addKeyPerfIndEmplObj.openWindow('${keyPerfIndPartyTargetItem.keyPerfIndicatorId}')"/>
								<div class="row-fluid">
									<#assign customTitleProperties = keyPerfIndPartyTargetItem.keyPerfIndicatorName/>
									<@jqGrid id="employee${keyPerfIndPartyTargetItem.keyPerfIndicatorId}" filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" 
										dataField=datafieldEmpl columnlist=columnlistEmpl  
										clearfilteringbutton="true" showlist="true" 
										customTitleProperties=customTitleProperties
										addrow="false" customcontrol1=customcontrol1
										editable="false" deleterow="false" 
										url="jqxGeneralServicer?sname=JQGetKeyPerfIndPartyTargetOfEmpl&parentPartyTargetId=${partyTargetId}&keyPerfIndicatorId=${keyPerfIndPartyTargetItem.keyPerfIndicatorId}" 
										jqGridMinimumLibEnable="false"
									 	updateUrl=""
										editColumns=""/>
								</div>
								<#if keyPerfIndPartyTargetItem_has_next>
									<div class="row-fluid" style="border-bottom: 1px dotted #e2e2e2; margin-bottom: 20px"></div>
								</#if>
							</#list>
						</#if>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingAllocateEmpl" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAllocateEmpl"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#if>

<#if (listDirectChildDept?size > 0)>
<div id="AddAllocateForOrgWindow" class="hide">
	<div>${uiLabelMap.accAddNewRow}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.CommonDepartment}</label>
						</div>
						<div class='span8'>
							<div id="partyGroupAllocateList"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.KeyPerfIndicator}</label>
						</div>
						<div class='span8'>
							<div id="keyPerfIndAllocateOrg"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRTarget}</label>
						</div>
						<div class='span8'>
							<div id="targetChildOrg"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.ParentOrganization}</label>
						</div>
						<div class='span8'>
							<div id="targetParentOrg"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.HRCommonMeasure}</label>
						</div>
						<div class='span8'>
							<div id="uomChildOrg"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.KPIWeigth}</label>
						</div>
						<div class='span8'>
							<div id="weightChildOrg"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.CommonDescription}</label>
						</div>
						<div class='span8'>
							<input type="text" id="keyPerfIndicatorNameOrgAdd">
						</div>
					</div>	
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddAllocateForChildOrg" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddAllocateForChildOrg"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddAllocateChildOrg" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddAllocateChildOrg" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/AddKeyPerfIndChildOrg.js"></script>
</#if>
<#if (listDirectEmpl?size > 0)>
<div id="AddAllocateForEmplWindow" class="hide">
	<div>${uiLabelMap.accAddNewRow}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.CommonEmployee}</label>
						</div>
						<div class='span8'>
							<div id="employeeDirectList"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.KeyPerfIndicator}</label>
						</div>
						<div class='span8'>
							<div id="keyPerfIndAllocateEmpl"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRTarget}</label>
						</div>
						<div class='span8'>
							<div id="targetEmpl"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.HRCommonUnit}</label>
						</div>
						<div class='span8'>
							<div id="targetParentEmpl"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.HRCommonMeasure}</label>
						</div>
						<div class='span8'>
							<div id="uomEmpl"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.KPIWeigth}</label>
						</div>
						<div class='span8'>
							<div id="weightEmpl"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.CommonDescription}</label>
						</div>
						<div class='span8'>
							<input type="text" id="keyPerfIndicatorNameEmplAdd">
						</div>
					</div>	
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddAllocateForEmpl" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddAllocateForEmpl"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddAllocateEmpl" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddAllocateEmpl" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/AddKeyPerfIndEmpl.js"></script>
</#if>

<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewKeyPerfIndPartyTargetDetail.js"></script>