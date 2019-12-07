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
				  return '<span title=' + value + '><a href=RecruitmentPlanDT?partyId=' + value + '&year=' + dataRow['year'] + '>' + partyName + '</a></span>';
			}
		},
		{ text: '${uiLabelMap.Year}', datafield: 'year', width: 150, editable: false, filter: yearColumnFilter},
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
		{ text: '${uiLabelMap.comment}', datafield: 'comment', editable: true, width: 150}
	"/>
<@jqGrid id="jqxgrid" addType="popup" jqGridMinimumLibEnable="false" editable="true" addrefresh="true" filtersimplemode="true" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListRecruitmentPlanDTHeader" dataField=dataField columnlist=columnlist 
		/>
<script>
</script>