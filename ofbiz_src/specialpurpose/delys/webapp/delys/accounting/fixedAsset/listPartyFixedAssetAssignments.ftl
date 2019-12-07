<script type="text/javascript">	
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	var dataRoleTypeListView = new Array();
	var row = {};
	row['roleTypeId'] = '';
	row['description'] = '';
	dataRoleTypeListView[0] = row;
	<#list roleTypeList as roleType >
		var row = {};
		row['roleTypeId'] = '${roleType.roleTypeId?if_exists}';
		row['description'] = '${roleType.get('description',locale)?if_exists}';
		dataRoleTypeListView[${roleType_index} + 1] = row;
	</#list>	

	<#assign statusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "PRTYASGN_STATUS"), null, null, null, false) />
	var dataStatusListView = new Array();
	<#list statusList as status >
		var row = {};
		row['statusId'] = '${status.statusId?if_exists}';
		row['description'] = '${status.get('description',locale)?if_exists}';
		dataStatusListView[${status_index}] = row;
	</#list>	
	
 	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    }
</script>

<#assign params="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=listPartyFixedAssetsAssignmentsJqx">
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'roleTypeId', type: 'string'},
					 { name: 'fixedAssetId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'},
					 { name: 'allocatedDate', type: 'date', other: 'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'comments', type: 'string'},
					 { name: 'partyNameResult', type: 'string'}
				   ]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.PartyId)}', datafield: 'partyId', width: '20%', editable: false, cellsrenderer:
				       function(row, colum, value){
					        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        var name = data.partyNameResult ?  data.partyNameResult : '';
					        return \"<span> <a href='/partymgr/control/viewprofile?partyId=\" + data.partyId + \"'>\" + name + \" [\" + data.partyId + \"]</a></span>\";
			         }},
					 { text: '${StringUtil.wrapString(uiLabelMap.accRoleTypeId)}', datafield: 'roleTypeId', width: '10%', editable: false, filterable: false, cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataRoleTypeListView.length; i++){
	        							if(data.roleTypeId == dataRoleTypeListView[i].roleTypeId){
	        								return '<span title=' + value +'>' + name  + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
					 	},
	                    { text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', width:150, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, columntype: 'datetimeinput'},
	                    { text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', width:220, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput',
						 	createeditor: function (row, column, editor) {
	                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' });
	                     		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                     		if (!data.thruDate)
	                     		editor.jqxDateTimeInput('setDate', null);
	                     	}},
	                    { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_expectedEndOfLife)}', width:220, datafield: 'allocatedDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput',
						 	createeditor: function (row, column, editor) {
	                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' });	                     		
	                     		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                     		if (!data.allocatedDate)
	                     		editor.jqxDateTimeInput('setDate', null);	                     		
	                     	}},
	                    { text: '${StringUtil.wrapString(uiLabelMap.Status)}', datafield: 'statusId', width: '10%', filtertype: 'checkedlist', cellclassname: cellclass, columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataStatusListView.length; i++){
	        							if(data.statusId == dataStatusListView[i].statusId){
	        								return '<span title=' + value +'>' + dataStatusListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataStatusListView,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < uniqueRecords2.length; i++){
										if(uniqueRecords2[i].statusId == value){
											return uniqueRecords2[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			},			   			
				   				createeditor: function (row, column, editor) {
	                            editor.jqxDropDownList({source: dataStatusListView, displayMember:\"description\", valueMember: \"statusId\",
		                            renderer: function (index, label, value) {
					                    var datarecord = dataStatusListView[index];
					                    return datarecord.description;
					                  }
		                        });}
				   			},
						{ text: '${uiLabelMap.comments}', width: '20%', datafield: 'comments'}					
					"/>
	
<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"	 deleterow="true"	
		url=params addColumns="fixedAssetId[${parameters.fixedAssetId}];partyId;roleTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);allocatedDate(java.sql.Timestamp);statusId;comments"
		createUrl="jqxGeneralServicer?sname=createPartyFixedAssetAssignment&jqaction=C" 
		updateUrl="jqxGeneralServicer?sname=updatePartyFixedAssetAssignment&fixedAssetId=${parameters.fixedAssetId}&jqaction=U"
		editColumns="fixedAssetId[${parameters.fixedAssetId}];partyId;roleTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);allocatedDate(java.sql.Timestamp);statusId;comments"
		removeUrl="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=deletePartyFixedAssetAssignment&jqaction=D"
		deleteColumn="fixedAssetId[${parameters.fixedAssetId}];partyId;roleTypeId;fromDate(java.sql.Timestamp)"
		otherParams="partyNameResult:S-getPartyNameForDate(partyId)<fullName>;"
		showlist="true"
	/>	
	
<#include "component://delys/webapp/delys/accounting/popup/popupFixedAssetAssignments.ftl"/>