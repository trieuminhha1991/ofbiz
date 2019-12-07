<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<#assign commmEventType = delegator.findList("CommunicationEventType", null, null, null, null, false) />
<#assign dataField="[{name: 'fullName', type: 'string'},
					{name: 'partyFullNameFrom', type: 'string'},
					{name: 'partyFullNameTo', type: 'string'},
					{name: 'subjectEnumId', type: 'string'},
					{name: 'content', type: 'string'},
					{name: 'partyIdFrom', type: 'string'},
					{name: 'partyIdTo', type: 'string'},
					{name: 'communicationEventTypeId', type: 'string'},
		            {name: 'entryDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'}]"/>

		            
<#assign columnlist = "{text: '${uiLabelMap.KTime}', datafield: 'entryDate', cellsalign: 'left', width: 200, cellsformat: 'HH:mm:ss dd/MM/yyyy', filtertype: 'range'},
					{text: '${uiLabelMap.DmsCustomer}',datafield: 'partyFullNameFrom', width: 200,
						cellsrenderer : function(row, col, value, x, y, data){
							var str = '<div class=\"cell-custom-grid\"><a href=\"AgentDetail?partyId=' + data.partyIdFrom+ '\" target=\"_blank\">'+value+'</a></div>';
							return str;
						}
					},
					{text: '${uiLabelMap.BSEmployee}',datafield: 'partyFullNameTo', width: 200},
					{text: '${uiLabelMap.CommunicationEventType}',datafield: 'communicationEventTypeId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function (row, column, value) {
							for(var x in commmEventType){
								if(commmEventType[x].communicationEventTypeId==value){
									return '<div style=margin:4px;><b>' + commmEventType[x].description + '</b></div>';
								}
							}
					        return '<div style=margin:4px;><b>' + value + '</b></div>';
					    }, createfilterwidget: function (column, htmlElement, editor) {
					    	editor.jqxDropDownList({ autoDropDownHeight: true, source: commmEventType, displayMember: 'description', valueMember: 'communicationEventTypeId'});
						}
					},
					{text: '${uiLabelMap.DAContent}',datafield: 'content'},"/>
<@jqGrid id="ScheduleCommunicationList" url="jqxGeneralServicer?sname=JqxGetListFeedbacks"
		filtersimplemode="true" filterable="true" addrow="false" dataField=dataField columnlist=columnlist
		clearfilteringbutton="true" width="100%" showtoolbar=showtoolbar clearfilteringbutton="true"/>

<script>
	var commmEventType = [<#list commmEventType as item>{
		communicationEventTypeId: "${item.communicationEventTypeId?if_exists}",
		description : "${StringUtil.wrapString(item.description?if_exists)}"},
	</#list>];
	
	function checkCallSchedule() {
		return DataAccess.execute({
					url: "checkCallSchedule",
					data: {}
					});
	}
</script>