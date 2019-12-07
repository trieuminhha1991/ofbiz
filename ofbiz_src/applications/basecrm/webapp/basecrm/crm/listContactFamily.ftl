<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail' + index);
	var sourceGridDetail =
	{
		localdata: datarecord.rowDetail,
		datatype: 'local',
		datafields:
		[
			{ name: 'familyId', type: 'string' },
			{ name: 'partyId', type: 'string' },
			{ name: 'partyFullName', type: 'string' },
			{ name: 'gender', type: 'string' },
			{ name: 'roleTypeFrom', type: 'string' },
			{ name: 'roleTypeIdFrom', type: 'string' },
			{ name: 'birthDate', type: 'date', other: 'date' },
			{ name: 'idNumber', type: 'string' },
			{ name: 'contactNumber', type: 'string' },
			{ name: 'emailAddress', type: 'string' }
		],
		id: 'partyId',
		addrow: function (rowid, rowdata, position, commit) {
			commit(true);
		},
		deleterow: function (rowid, commit) {
			var data = grid.jqxGrid('getrowdatabyid', rowid);
			commit(deleteMember(data));
		},
		updaterow: function (rowid, newdata, commit) {
			commit(true);
		}
	};
	var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	grid.jqxGrid({
		localization: getLocalization(),
		width: '98%',
		height: '92%',
		theme: theme,
		source: dataAdapterGridDetail,
		sortable: true,
		editable: false,
		editmode: 'selectedrow',
		pagesize: 5,
		pageable: true,
		selectionmode: 'singlerow',
		columns:[
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberName)}', datafield: 'partyFullName', minWidth: 200 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberType)}', datafield: 'roleTypeIdFrom', width: 200, columntype: 'dropdownlist',
					cellsrenderer: function (row, column, value) {
						value?value=mapRoleType[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}' , datafield: 'gender', width: 200, columntype: 'dropdownlist',
					cellsrenderer: function (row, column, value) {
						value?value=mapGender[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyBirthDate)}', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range', columntype: 'datetimeinput',
					cellsrenderer: function (row, column, value) {
						value?value=new Date(value).toTimeOlbius()+getPersonAge(value):value;
						return '<div style=margin:4px;>' + value + '</div>';
					}
				}]
	});
}"/>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'partyFullName', type: 'string' },
					{ name: 'gender', type: 'string' },
					{ name: 'birthDate', type: 'date', other: 'date' },
					{ name: 'idNumber', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'emailAddress', type: 'string' },
					{ name: 'familyId', type: 'string' },
					{ name: 'familyName', type: 'string' },
					{ name: 'address1', type: 'string' },
					{ name: 'districtGeoName', type: 'string' },
					{ name: 'resultEnumTypeId', type: 'string' },
					{ name: 'entryDate', type: 'date', other:'Timestamp' },
					{ name: 'marketingCampaignId', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'roleTypeIdFrom', type: 'string' },
					{ name: 'roleTypeIdTo', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'fromDate', type: 'string' },
					{ name: 'partyType', type: 'string' },
					{ name: 'rowDetail', type: 'string' }]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},"/>
					<#if !security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
<#assign columnlist=columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.CampaignId)}', datafield: 'marketingCampaignId', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ResultEnumId)}', datafield: 'resultEnumTypeId', width: 100, filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;><b>' + value + '</b></div>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: false, source: reasons, displayMember: 'enumTypeId', valueMember: 'enumTypeId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return resonsMap[value];
								}
							});
						}
					},
					{ text: '${uiLabelMap.NextCallSchedule}', datafield: 'entryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyType)}', datafield: 'partyType', filtertype: 'checkedlist', width: 150,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['CONTACT', 'INDIVIDUAL_CUSTOMER', 'CUSTOMER']});
						}
					},"/>
					<#else>
<#assign columnlist=columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150 },"/>
					</#if>
<#assign columnlist=columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFamily)}', datafield: 'partyFullName', width: 200,
						cellsrenderer: function(row, column, value, a, b, data){
							var color = '';
							switch (data.statusId) {
							case 'CONTACT_COMPLETED':
								color = 'black';
								break;
							case 'CONTACT_INPROGRESS':
								color = 'green';
								break;
							case 'CONTACT_FAIL':
								color = 'red';
								break;
							default:
								color = '#08c;';
								break;
							}
							var link = 'Callcenter?partyId=' + data.partyId + '&familyId=' + data.familyId;
							if (data.marketingCampaignId) {
								link += '&marketingCampaignId=' + data.marketingCampaignId;
								if (data.roleTypeIdFrom) {
									link += '&roleTypeIdFrom=' + data.roleTypeIdFrom;
								}
								if (data.roleTypeIdTo) {
									link += '&roleTypeIdTo=' + data.roleTypeIdTo;
								}
								if (data.fromDate) {
									link += '&fromDate=' + data.fromDate;
								}
							}
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"' + link
							+ '\" style=\"color: ' + color + '\">' + value + '</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', minWidth: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsCounty)}', datafield: 'districtGeoName', minWidth: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsIdentification)}', datafield: 'idNumber', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200 }"/>
				
<#if security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
		<#assign url="jqxGeneralServicer?sname=JQGetListContactFamily" />
<#else>
		<#assign url="jqxGeneralServicer?sname=JQGetListContactFamilyByCampaign&campaignId=any" />
</#if>
<@jqGrid addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	url=url sortable="false" sortable="false"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"/>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign reasonContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "CONTACTED"), null, null, null, false) />
<#assign reasonUnContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "UNCONTACTED"), null, null, null, false) />
<script>
	$(document).ready(function() {
		$("#jqxgrid").jqxGrid({ enabletooltips: true });
	});
	var reasonContacted = 
		[<#if reasonContacted?exists>
			{enumTypeId: "47b56994cbc2b6d10aa1be30f70165adb305a41a", description: "${StringUtil.wrapString(uiLabelMap.NoResults)}"},
			<#list reasonContacted as item>{
				enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},</#list>
		</#if>];
	var mapReasonContacted =
	{<#if reasonContacted?exists>
		"47b56994cbc2b6d10aa1be30f70165adb305a41a": "<b>${StringUtil.wrapString(uiLabelMap.NoResults)}</b>",
		<#list reasonContacted as item>
			"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list>
	</#if>};
	var reasonUnContacted = 
		[<#if reasonUnContacted?exists>
		<#list reasonUnContacted as item>{
			enumTypeId: "${item.enumTypeId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
		},</#list>
		</#if>];
	var mapReasonUnContacted =
	{<#if reasonUnContacted?exists>
		<#list reasonUnContacted as item>
			"${item.enumTypeId?if_exists}": "<b>[${item.enumTypeId?if_exists}]</b>" +  "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list>
	</#if>};
	var reasons = _.union(reasonContacted, reasonUnContacted);
	var resonsMap = _.extend(mapReasonContacted, mapReasonUnContacted);
	
	var listRoleType = [<#if listRoleType?exists><#list listRoleType as item>{
		roleTypeId: "${item.roleTypeId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];
	var mapRoleType = {<#if listRoleType?exists><#list listRoleType as item>
		"${item.roleTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	
	function fixPartyCode() {
		return DataAccess.execute({
			url: "fixPartyCode",
			data: {}});
	}
</script>