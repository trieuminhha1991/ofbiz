<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/aceadmin/jqw/jqwidgets/jqxcombobox2.full.js"></script>

<#assign resultContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "CONTACTED"), null, null, null, false) />
<#assign resultUnContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "UNCONTACTED"), null, null, null, false) />
<#assign resultInbound = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "COMM_INBOUND_RESULT"), null, null, null, false) />
<#assign commmEventType = delegator.findList("CommunicationEventType", null, null, null, null, false) />

<script>
var resultContacted = [<#if resultContacted?exists><#list resultContacted as item>{
		enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.description)}"
	},</#list></#if>];
var mapReasonContacted = {<#if resultContacted?exists><#list resultContacted as item>
		"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
var resultUnContacted = [<#if resultUnContacted?exists><#list resultUnContacted as item>{
		enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.description)}"
	},</#list></#if>];
var mapReasonUnContacted = {<#if resultUnContacted?exists><#list resultUnContacted as item>
		"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
var resultInbound = [<#if resultInbound?exists><#list resultInbound as item>{
	enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.description)}"
},</#list></#if>];
var mapResultInbound = {<#if resultInbound?exists><#list resultInbound as item>
"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.description?if_exists)}",
</#list></#if>};
var commmEventType = [<#list commmEventType as item>{
		communicationEventTypeId: "${item.communicationEventTypeId?if_exists}",
		description : "${StringUtil.wrapString(item.description?if_exists)}"
	},</#list>];
var results = _.union(resultContacted, resultUnContacted, resultInbound);
var resonsMap = _.extend(mapReasonContacted, mapReasonUnContacted, mapResultInbound);
	var partyId = "${userLogin.getString("partyId")}";
	var outFilterCondition = "";
	function changeSourceGrid(callerPartyId){
		var tmpS = $("#ListAllCommunication").jqxGrid("source");
		tmpS._source.url = "jqxGeneralServicer?sname=JqxGetListCommunication&onlyCaller=Y&callerPartyId=" + callerPartyId;
		$("#ListAllCommunication").jqxGrid("source", tmpS);
	}
	function convertToString(data) {
		var dataStr = "";
		for ( var x in data) {
			dataStr += data[x] + "|LOVE|";
		}
		return dataStr;
	}
	var initEmployee = function(element) {
		element.on("change", function (event){
			var args = event.args;
			if (args) {
				changeSourceGrid(convertToString(LocalUtil.getValueSelectedJqxComboBox(element)));
			}
		});
		var source = {
				datatype: "json",
				datafields: [
					{ name: "partyId" },
					{ name: "partyDetail" }
				],
				url: "getCallCenterEmployee"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		element.jqxComboBox({
			source: dataAdapter, theme: theme, multiSelect: true,
			displayMember: "partyDetail", valueMember: "partyId",
			width: 500, height: 25, autoDropDownHeight: true,
			placeHolder: "${StringUtil.wrapString(uiLabelMap.PersonCommunicate)}"
		});
	};
	var FilterByEmployee = function(container){
		var str = "<div id='EmployeeCallcenter' class='pull-right margin-top5'></div>";
		container.append(str);
		var obj = $("#EmployeeCallcenter");
		initEmployee($("#EmployeeCallcenter"));
		obj.on("change", function () {
			outFilterCondition = ChangeFilterCondition($(this).val());
		});
	};
	var ChangeFilterCondition = function(val){
		var str = "|OLBIUS|callerPartyId"
				+ "|SUIBLO|" + val
				+ "|SUIBLO|" + "EQUAL"
				+ "|SUIBLO|" + "or";
		return str;
	};
</script>
<#assign dataField="[{ name: 'communicationEventId', type: 'string' },
					{ name: 'entryDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'communicationEventTypeId', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyFullNameFrom', type: 'string' },
					{ name: 'partyFullNameTo', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'content', type: 'string' },
					{ name: 'subjectEnumId', type: 'string' },
					{ name: 'subjectEnumCode', type: 'string' },
					{ name: 'subjectEnumDescription', type: 'string' },
					{ name: 'enumCode', type: 'string' },
					{ name: 'enumDescription', type: 'string' },
					{ name: 'resultEnumTypeId', type: 'string' },
					{ name: 'partyCodeFrom', type: 'string' },
					{ name: 'partyCodeTo', type: 'string' },
					{ name: 'currentBrandName', type: 'string' },
					{ name: 'productDiscussedId', type: 'string' },
					{ name: 'productDiscussedName', type: 'string' },
					{ name: 'currentProductName', type: 'string' },
					{ name: 'communicationEventTypeId', type: 'string' },
					{ name: 'isCallOut', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSCustomerId}', datafield: 'customerPartyId', width: 150, sortable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							var str = \"<div class='custom-cell-grid'>\";
							if(data.isCallOut != 'Y'){
								str += data.partyCodeFrom ? data.partyCodeFrom : '';
							}else{
								str += data.partyCodeTo ? data.partyCodeTo : ''
							}
							str += '</div>';
							return str;
						}
					},
					{ text: '${uiLabelMap.BSCustomer}',datafield: 'fullName', width: 200, sortable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							var partyIdFrom = data.partyIdFrom;
							var str = \"<div class='custom-cell-grid'>\";
							if(data.isCallOut != 'Y'){
								str += '<a target=\"_blank\" href=\"Callcenter?partyId='+data.partyIdFrom+'\">';
								str += data.partyFullNameFrom ? data.partyFullNameFrom : '';
							}else{
								str += '<a target=\"_blank\" href=\"Callcenter?partyId='+data.partyIdTo+'\">';
								str += data.partyFullNameTo ? data.partyFullNameTo : ''
							}
							str += '</a></div>';
							return str;
						}
					},
					{ text: '${uiLabelMap.CommunicationDirection}', datafield: 'isCallOut', width: 150, sortable: false, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value, a, b, data){
							var str = \"<div class='custom-cell-grid'>\";
							if(data.isCallOut == 'Y'){
								str += '<b>${uiLabelMap.callout}</b>';
							}else{
								str += '${uiLabelMap.callin}';
							}
							str += '</div>';
							return str;
						},
						createfilterwidget: function (column, columnElement, widget) {
							var data = [{
								value : 'N',
								description: '${uiLabelMap.callin}'
							}, {
								value : 'Y',
								description: '${uiLabelMap.callout}'
							}];
							widget.jqxDropDownList({width: 140, source: data, displayMember: 'description', valueMember : 'value', autoDropDownHeight: true});
						}
					},
					{ text: '${uiLabelMap.CalledDate}', datafield: 'entryDate', width: 150, cellsformat: 'HH:mm:ss dd/MM/yyyy', filtertype: 'range'},
					{ text: '${uiLabelMap.ResultEnumId}', datafield: 'resultEnumTypeId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value?(resonsMap[value]?resonsMap[value]:value):value) + '</div>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: results, displayMember: 'enumTypeId', valueMember: 'enumTypeId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return resonsMap[value];
								}
							});
						}
					},
					{ text: '${uiLabelMap.ReasonEnumId}', datafield: 'enumCode', width: 200,
						cellsrenderer: function(row, column, value, a, b, data){
							if (value) {
								value = '<b>[' + value + ']</b>' + data.enumDescription;
							}
							return '<div style=margin:4px;>' + value + '</div>';
						}	
					},
					{ text: '${uiLabelMap.CommunicationEventType}', datafield: 'communicationEventTypeId', filtertype: 'checkedlist', width: 100,
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
					{ text: '${uiLabelMap.ProductDiscussing}', datafield: 'productDiscussedName', width: 250 },
					{ text: '${uiLabelMap.Subject}', datafield: 'subjectEnumCode', width: 200,
						cellsrenderer: function(row, column, value, a, b, data){
							if (value) {
								value = '<b>[' + value + ']</b>' + data.subjectEnumDescription;
							}
							return '<div style=margin:4px;>' + value + '</div>';
						}
					},
					{ text: '${uiLabelMap.DAContent}', datafield: 'content', minwidth: 200 }"/>

<@jqGrid filtersimplemode="false" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" id="ListAllCommunication" customtoolbaraction="FilterByEmployee"
	url="jqxGeneralServicer?sname=JqxGetListCommunication&onlyCaller=Y"/>
