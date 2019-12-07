<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id', 'jqxgridDetail' + index);
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
			{ name: 'birthDate', type: 'date', other: 'Date' },
			{ name: 'idNumber', type: 'string' },
			{ name: 'contactNumber', type: 'string' },
			{ name: 'emailAddress', type: 'string' }
		],
		id: 'partyId',
		addrow: function (rowid, rowdata, position, commit) {
			commit(addMemberAjax({partyId: rowdata.partyId, familyId: rowdata.familyId, roleTypeIdFrom: rowdata.roleTypeIdFrom}, false));
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
		pagesize: 5,
		pageable: true,
		selectionmode: 'singlerow',
		columns: [
				{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberName)}', datafield: 'partyFullName', minWidth: 200 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsMemberType)}', datafield: 'roleTypeFrom', width: 200 },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}' , datafield: 'gender', width: 200,
					cellsrenderer: function (row, column, value) {
						value?value=mapGender[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyBirthDate)}', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						value?value=new Date(value).toTimeOlbius()+getPersonAge(value):value;
						return '<div style=margin:4px;>' + value + '</div>';
					}
				}]
	});
}"/>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'salerId', type: 'string' },
					{ name: 'saler', type: 'string' },
					{ name: 'partyType', type: 'string' },
					{ name: 'partyFullName', type: 'string' },
					{ name: 'gender', type: 'string' },
					{ name: 'birthDate', type: 'date', other: 'Timestamp' },
					{ name: 'idNumber', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'emailAddress', type: 'string' },
					{ name: 'familyId', type: 'string' },
					{ name: 'familyName', type: 'string' },
					{ name: 'address1', type: 'string' },
					{ name: 'rowDetail', type: 'string' }]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyType)}', datafield: 'partyType', filtertype: 'checkedlist', width: 150,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['INDIVIDUAL_CUSTOMER', 'CUSTOMER'] });
							editor.jqxDropDownList('checkAll');
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStaffContract)}', datafield: 'saler', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFamily)}', datafield: 'partyFullName', minWidth: 200, cellsalign: 'left',
						cellsrenderer: function(row, column, value, a, b, data){
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"Callcenter?partyId='+data.partyId+ '&familyId=' + data.familyId + '\">'+value+'</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', minWidth: 250},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsIdentification)}', datafield: 'idNumber', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200}"/>

<@jqGrid addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	url="jqxGeneralServicer?sname=JQGetListCustomersFamily" sortable="false"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
	contextMenuId="contextMenu" mouseRightMenu="true"/>
<#include "component://basecrm/webapp/basecrm/crm/changeSaler.ftl"/>
<script>
$(document).ready(function() {
	$("#jqxgrid").jqxGrid({ enabletooltips: true });
});
</script>