<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<#assign commmEventType = delegator.findList("CommunicationEventType", null, null, null, null, false) />
<#assign dataField="[{ name: 'fullName', type: 'string' },
					{ name: 'partyFullNameFrom', type: 'string' },
					{ name: 'partyFullNameTo', type: 'string' },
					{ name: 'subjectEnumId', type: 'string' },
					{ name: 'content', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'communicationEventTypeId', type: 'string' },
					{ name: 'entryDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.KTime}', datafield: 'entryDate', cellsalign: 'left', width: 200, cellsformat: 'HH:mm:ss dd/MM/yyyy', filtertype: 'range' },
					{ text: '${uiLabelMap.BSEmployee}', datafield: 'partyFullNameFrom', width: 250 },
					{ text: '${uiLabelMap.DmsCustomer}', datafield: 'partyFullNameTo',
						cellsrenderer: function(row, column, value, a, b, data){
							var link = 'Callcenter?partyId=' + data.partyIdTo + '&familyId=' + data.familyId;
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"' + link
							+ '\">' + value + '</a></div>';
							return str;
						}
					},
					{ text: '${uiLabelMap.CommunicationEventType}', datafield: 'communicationEventTypeId', filtertype: 'checkedlist', width: 150,
						cellsrenderer: function (row, column, value) {
							for(var x in commmEventType){
								if(commmEventType[x].communicationEventTypeId==value){
									return '<div style=margin:4px;><b>' + commmEventType[x].description + '</b></div>';
								}
							}
							return '<div style=margin:4px;><b>' + value + '</b></div>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: commmEventType, displayMember: 'description', valueMember: 'communicationEventTypeId' });
						}
					},
					{ text: '${uiLabelMap.Subject}', datafield: 'subjectEnumId', filtertype: 'checkedlist', width: 250,
						cellsrenderer: function(row, colum, value){
							value?value=mapEnumeration[value]:value;
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listEnumeration, displayMember: 'enumId', valueMember: 'enumId' ,
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapEnumeration[value];
								}
							});
						}
					}"/>
<@jqGrid id="ScheduleCommunicationList" url="jqxGeneralServicer?sname=JqxGetScheduleCommunication"
	filtersimplemode="true" filterable="true" addrow="false" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" width="100%" showtoolbar=showtoolbar clearfilteringbutton="true"/>

<#assign listEnumeration = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "COM_SCHEDULE"), null, null, null, false) />
				
<script>
	var commmEventType = [<#list commmEventType as item>{
		communicationEventTypeId: "${item.communicationEventTypeId?if_exists}",
		description : "${StringUtil.wrapString(item.description?if_exists)}"},
	</#list>];
	var listEnumeration = [<#if listEnumeration?exists><#list listEnumeration as item>{
		enumId: '${item.enumId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapEnumeration = {<#if listEnumeration?exists><#list listEnumeration as item>
		"${item.enumId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	
	function checkCallSchedule() {
		return DataAccess.execute({
			url: "checkCallSchedule",
			data: {}
		});
	}
</script>