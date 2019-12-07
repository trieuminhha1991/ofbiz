<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
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
			commit(true);
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
		columns: [
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberName)}', datafield: 'partyFullName', minwidth: 200,
					validation: function (cell, value) {
						if (!value) {
							return { result: false, message: multiLang.fieldRequired };
						}
						return true;
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberType)}', datafield: 'roleTypeFrom', width: 200},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}' , datafield: 'gender', width: 200, columntype: 'dropdownlist',
					cellsrenderer: function (row, column, value) {
						value?value=mapGender[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					},
					createeditor: function (row, column, editor) {
						editor.jqxDropDownList({ autoDropDownHeight: true, source: listGender, displayMember: 'value', valueMember: 'value',placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}' ,
							renderer: function (index, label, value) {
								var datarecord = listGender[index];
								return datarecord.label;
							},selectionRenderer: function () {
								var item = editor.jqxDropDownList('getSelectedItem');
								if (item) {
		  							return '<span title=' + item.value +'>' + mapGender[item.value] + '</span>';
								}
								return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
							}
						});
					},
					cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
						if (newvalue == '') return oldvalue;
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
					{ name: 'resultEnumTypeId', type: 'string' },
					{ name: 'marketingCampaignId', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'roleTypeIdFrom', type: 'string' },
					{ name: 'roleTypeIdTo', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'fromDate', type: 'string' },
					{ name: 'rowDetail', type: 'string' }]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ResultEnumId)}', datafield: 'resultEnumTypeId', width: 100, filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;><b>' + value + '</b></div>';
						},createfilterwidget: function (column, htmlElement, editor) {
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
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150 },
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
							}
							if (data.roleTypeIdFrom) {
								link += '&roleTypeIdFrom=' + data.roleTypeIdFrom;
							}
							if (data.roleTypeIdTo) {
								link += '&roleTypeIdTo=' + data.roleTypeIdTo;
							}
							if (data.fromDate) {
								link += '&fromDate=' + data.fromDate;
							}
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"' + link
							+ '\" style=\"color: ' + color + '\">' + value + '</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsIdentification)}', datafield: 'idNumber', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200 }
					"/>

<@jqGrid id="ListResourceAssigned" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="" sortable="false" customLoadFunction="true" isShowTitleProperty="false" jqGridMinimumLibEnable="false"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
		contextMenuId="assignedMenu" mouseRightMenu="true" selectionmode="checkbox"
	/>

<div id="assignedMenu" style="display:none;">
	<ul>
	</ul>
</div>

<#assign reasonContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "CONTACTED"), null, null, null, false) />
<#assign reasonUnContacted = delegator.findList("EnumerationType",
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "UNCONTACTED"), null, null, null, false) />
<script>
	
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

	var mapGender = {"M": multiLang.male, "F": multiLang.female};
	var listGender = [{value: "M", label: multiLang.male}, {value: "F", label: multiLang.female}];
	function getPersonAge(birthDate) {
		var birthYear = new Date(birthDate).getFullYear();
		var currentYear = new Date().getFullYear();
		var partyAge = currentYear - birthYear;
		if (partyAge < 0) {
			return "";
		} else if (partyAge < 2) {
			var birthMonth = new Date(birthDate).getMonth();
			var currentMonth = new Date().getMonth();
			partyAge = currentMonth - birthMonth + 1 + partyAge*12;
			return "<span class='green'> (" + partyAge + ") " + multiLang.DmsMonths;
		}
		partyAge += 1;
		return "<span class='green'> (" + partyAge + ") " + multiLang.age;
	}
</script>