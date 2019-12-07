<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "DeliveryPlan_rowDetail.ftl"/>

<#assign dataField = "[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'planId', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'postalAddressId', type: 'string' },
					{ name: 'shippingAddress', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.CustomerId)}', dataField: 'partyCode', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.CustomerName)}', datafield: 'groupName' },
					{ text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'contactNumber', width: 170 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ReceiveAddress)}', datafield: 'shippingAddress', width: 380,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return '<div style=\"margin:4px;\">' + value.addressAdjusting() + '</div>';
						}
					}"/>

<@jqGrid id="jqxgridDeliveryPlan" addrow="false" clearfilteringbutton="true" editable="false"
	columnlist=columnlist dataField=dataField selectionmode="multiplerows"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
	url="jqxGeneralServicerAndInitPicker?sname=JQGetListCurrentDeliveryPlan&facilityId=${(parameters.get('facilityId'))?if_exists}&planId=${planId?if_exists}" contextMenuId="contextMenu" mouseRightMenu="true"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="mnuPrepareProduct"><i class="fa fa-file-excel-o"></i>${uiLabelMap.BLSGCPrepareProduct}</li>
	</ul>
</div>

<#include "SelectFacility.ftl"/>

<script>
	var mainGrid;
	var facilities = Object.freeze(${StringUtil.wrapString(facilities!"[]")});
	const facilityId = "${(parameters.get('facilityId'))?if_exists}";
	$(document).ready(function() {
		mainGrid = $("#jqxgridDeliveryPlan");
		mainGrid.on("bindingcomplete", function (event) {
			if (!mainGrid.find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {
				mainGrid.find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");
			}
		});
		mainGrid.on("rowclick", function (event) {
			if (event.args.rightclick) {
				mainGrid.jqxGrid("selectrow", event.args.rowindex);
			}
		});
		var selectedAddress = new Array();
		mainGrid.on("rowselect", function (event) {
			var args = event.args;
			var rowData = args.row;
			selectedAddress.push(rowData.postalAddressId);
		});
		mainGrid.on("rowunselect", function (event) {
			var args = event.args;
			var rowData = args.row;
			if (rowData) {
				selectedAddress = _.reject(selectedAddress, function(v){ return v == rowData.postalAddressId });
			}
		});
		var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 200, autoOpenPopup: false, mode: "popup"});
		contextmenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "mnuPrepareProduct":
				if (!_.isEmpty(selectedAddress)) {
					location.href = "PrepareProductExcel?selectedAddress=" + _.uniq(selectedAddress) + "&planId=${planId?if_exists}&facilityId=" + facilityId;
				}
				break;
			default:
				break;
			}
		});
	});
</script>