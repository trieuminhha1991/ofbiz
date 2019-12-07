<#--IMPORT LIB-->
<@jqGridMinimumLib />
<#--/IMPORT LIB-->
<script>
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;

	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "RPH_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = "${description}";
		statusData[${item_index}] = row;
	</#list>

 	<#assign listEmplPositionTypes = delegator.findList("DepPositionTypeView", null, null, null, null, false) >
    var positionTypeData = new Array();
	<#list listEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['partyId'] = '${item.deptId}';
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = "${description}";
		positionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare for role type data
	<#assign listRoleTypes = delegator.findList("RoleType", null, null, null, null, false)>
	var roleTypeData = new Array();
	<#list listRoleTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['roleTypeId'] = '${item.roleTypeId}';
		row['description'] = "${description}";
		roleTypeData[${item_index}] = row;
	</#list>
	
	var deptColumnFilter = function () {
         var filtergroup = new $.jqx.filter();
         var filter_or_operator = 1;
         if('${parameters.partyId?if_exists}'){
        	 var filtervalue = '${parameters.partyId?if_exists}';
             var filtercondition = 'contains';
             var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
             filtergroup.addfilter(filter_or_operator, filter);
             return filtergroup;
         }else{
        	 return false;
         }
     }();
     
     var yearColumnFilter = function () {
         var filtergroup = new $.jqx.filter();
         var filter_or_operator = 1;
         if('${parameters.year?if_exists}'){
        	 var filtervalue = '${parameters.year?if_exists}';
             var filtercondition = 'contains';
             var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
             filtergroup.addfilter(filter_or_operator, filter);
             return filtergroup;
         }else{
        	 return false;
         }
     }();
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'year', type: 'string' },
					 { name: 'scheduleDate', type: 'date', other: 'Timestamp' },
					 { name: 'reason', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'actorPartyId', type: 'string' },
					 { name: 'actorRoleTypeId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.Department}', datafield: 'partyId', editable: false, filter: deptColumnFilter, width: 200,
						cellsrenderer: function(row, column, value){
						  var dataRow = $('#jqxgrid').jqxGrid('getrowdata', row);
						  var partyName = value;
						  $.ajax({
								url: 'getPartyName',
								type: 'POST',
								data: {partyId: value},
								dataType: 'json',
								async: false,
								success : function(data) {
									if(!data._ERROR_MESSAGE_){
										partyName = data.partyName;
									}
						        }
							});
						  return '<span title=' + value + '><a href=SaleRecruitmentPlan?partyId=' + value + '&year=' + dataRow['year'] + '>' + partyName + '</a></span>';
						}
					 },
                     { text: '${uiLabelMap.Year}', datafield: 'year', width: 100, editable: false, filter: yearColumnFilter},
                     { text: '${uiLabelMap.sheduleDate}', datafield: 'scheduleDate', width: 150, cellsformat:'d', filtertype: 'range', editable: false},
                     { text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: true, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						},
					    cellbeginedit: function (row, datafield, columntype) {
					    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					    	if (isHeadOfDept && (data.statusId == 'RPH_PROPOSED' || data.statusId == 'RPH_APPROVED' || data.statusId == 'RPH_ACCEPTED'))
					            return false;
					    	if (isAdmin && (data.statusId == 'RPH_APPROVED'))
					            return false;
					    },
					    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDropDownList({source: tmpCreateStatusData, valueMember: 'statusId', displayMember:'description' });
					    }
                     },
                     { text: '${uiLabelMap.actorPartyId}', datafield: 'actorPartyId', editable: true, width: 150,
                    	 cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						}
                     },
                     { text: '${uiLabelMap.actorRoleTypeId}', datafield: 'actorRoleTypeId', editable: true, width: 150,
                    	 cellsrenderer: function(row, column, value){
   							for(var i = 0; i < roleTypeData.length; i++){
   								if(value == roleTypeData[i].roleTypeId){
   									return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
   								}
   							}
   							return '<span title=' + value + '>' + value + '</span>';
   						}
                     },
                     { text: '${uiLabelMap.comment}', datafield: 'reason', editable: true, width: 250}
					 "/>
<@jqGrid id="jqxgrid" addType="popup" editable="true" addrefresh="true" filtersimplemode="true" addrow="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListSaleRecruitmentPlanHeader" dataField=dataField columnlist=columnlist alternativeAddPopup="alterpopupNewRPH"
		 createUrl="jqxGeneralServicer?sname=createRecruitmentPlanHeader&jqaction=C" addColumns="partyId;year;scheduleDate(java.sql.Timestamp);reason"
	/>
<#--=========================================================Create Context Menu ===============================================================-->
<#--<div id='contextMenu' style="display: none;">
	<ul>
	    <li id="editPlan"><i class="fa fa-paper-plane"></i>&nbsp;${uiLabelMap.editPlan}</li>
	    <li id="proposeToCheck"><i class="fa fa-check-circle"></i>&nbsp;${uiLabelMap.proposeToCheck}</li>
	</ul>
</div>
-->
<#--====================================================Create new popup window==========================================================-->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewRPH">
			<div id="windowHeaderNewRPH">
	            <span>
	               ${uiLabelMap.AddRecruitmentPlan}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 15px" id="windowContentNewPlan">
			<div class="basic-form form-horizontal" style="margin-top: 10px">
				<form name="createNewRPH" id="createNewRPH">
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.Department}:</label>
							<div class="controls" id="first-input">
								<div id="partyIdLabel"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.Year}:</label>
							<div class="controls">
								<div id="yearAdd"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonDescription}:</label>
							<div class="controls">
								<input id="reasonAdd" ></input>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.sheduleDate}:</label>
							<div class="controls">
								<div id="scheduleDate" ></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">&nbsp</label>
							<div class="controls">
								<button type="button" class="btn btn-mini btn-success" id="alterSave"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
								<button type="button" class="btn btn-mini btn-danger" id="alterCancel"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
							</div>
						</div>
					</div>
				</div>
				</form>
			</div>
	        </div>
		</div>
	</div>
</div>
<#--====================================================/Create new popup window==========================================================-->
<script>
	$(document).ready(function(){
		<#if Static["com.olbius.util.PartyUtil"].isAdmin(delegator, userLogin)>
			//Prepare for status data
			<#assign listGroup = delegator.findList("PartyGroupAndPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["INTER_CONTROL_DEPT", "ASSIST_DEPT", "REWARD_DEPT", "DR_DEPARTMENT", "RENT_FACILITY", "HR_DEPARTMENT"]), null, null, null, false)>
			var groupData = new Array();
			<#list listGroup as item>
				var row = {};
				<#assign description = StringUtil.wrapString(item.groupName)>
				row['partyId'] = '${item.partyId}';
				row['description'] = '${description}';
				groupData[${item_index}] = row;
			</#list>
			$("#first-input").append( "<div id='partyIdAdd'></div>" );
			$("#partyIdAdd").jqxDropDownList({selectedIndex: 0, source: groupData, valueMember: 'partyId', displayMember: 'description'});
		<#else>
			//Create partyId
			<#assign partyId = Static["com.olbius.util.PartyUtil"].getOrgByManager(userLogin, delegator) >
			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
			jQuery("#partyIdLabel").text('${StringUtil.wrapString(partyName)}');
			$("#first-input").append( "<input id='partyIdAdd' type='hidden'></input>" );
			$("#partyIdAdd").val('${partyId}');
		</#if>
		//Create year
		<#assign currentYear = Static["com.olbius.util.DateUtil"].getCurrentYear() >
		$("#yearAdd").jqxNumberInput({spinButtons: true, decimal: '${currentYear}', inputMode: 'simple', decimalDigits: 0});

		//Create scheduleDate
		jQuery("#scheduleDate").jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd/MM/yyyy', theme: theme });
		
		//Create reason
		jQuery("#reasonAdd").jqxInput({ width: '195px',theme: theme });

		//Create window
		$("#alterpopupNewRPH").jqxWindow({
	        showCollapseButton: false, maxHeight: 550, autoOpen: false, maxWidth: "50%", minHeight: 300, minWidth: '40%', width: "80%", isModal: true,
	        theme:theme, collapsed:false, cancelButton: "#alterCancel"
	    });

		//update the edited row when the user clicks the 'Save' button.
	    $("#alterSave").click(function () {
		var row;
	        row = {
				scheduleDate:$('#scheduleDate').jqxDateTimeInput('getDate').getTime(),
				partyId:$("#partyIdAdd").val(),
				year:$("#yearAdd").val(),
				reason:$("#reasonAdd").val(),
			  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#alterpopupNewRPH").jqxWindow('close');
	    });
	});
</script>