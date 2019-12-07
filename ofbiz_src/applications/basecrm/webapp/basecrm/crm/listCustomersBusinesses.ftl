<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'salerId', type: 'string' },
					{ name: 'saler', type: 'string' },
					{ name: 'partyType', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'representativeMember', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'emailAddress', type: 'string' },
					{ name: 'address1', type: 'string' }]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyType)}', datafield: 'partyType', filtertype: 'checkedlist', width: 150,
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: ['INDIVIDUAL_CUSTOMER', 'CUSTOMER'] });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStaffContract)}', datafield: 'saler', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsCorporationName)}', datafield: 'groupName', minWidth: 200,
						cellsrenderer: function(row, column, value, a, b, data){
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"Callcenter?partyId='+data.partyId+'\">'+value+'</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', minWidth: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsRepresent)}', datafield: 'representativeMember', width: 200 }"/>

<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	url="jqxGeneralServicer?sname=JQGetListCustomersBusinesses" sortable="false"
	contextMenuId="contextMenu" mouseRightMenu="true" />

<#include "component://basecrm/webapp/basecrm/crm/changeSaler.ftl"/>
<script>
	$(document).ready(function() {
		$("#jqxgrid").jqxGrid({ enabletooltips: true });
	});
</script>