<script>
<#assign transferHeaders = delegator.findList("TransferHeader", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "TRANSFER_APPROVED")), null, null, null, true) />

var listTransferToday = [];
<#list transferHeaders as header>
	<#assign transferItems = delegator.findList("TransferItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", header.transferId?if_exists)), null, null, null, true) />
	var transferId = "${header.transferId}";
	<#list transferItems as item>
		<#if header.transferDate?exists || (item.shipBeforeDate?exists && item.shipAfterDate?exists)>
			var item = {
				transferId: transferId,
				transferDate: "${header.transferDate?if_exists}",
				shipBeforeDate: "${item.shipBeforeDate?if_exists}",
				shipAfterDate: "${item.shipAfterDate?if_exists}",
				transferTypeId: "${header.transferTypeId?if_exists}",
			};
			<#if (header.transferDate?exists && nowTimestamp.before(header.transferDate))>
				var date = new Date("${header.transferDate}");
				var today = new Date();
				var date1 = today.getDate();
				if (date.getDate() == today.getDate()){
					listTransferToday.push(item);
				}
			<#elseif (item.shipAfterDate?exists && nowTimestamp.before(item.shipAfterDate))>
				var date = new Date("${item.shipAfterDate}");
				var today = new Date();
				if (date.getDate() == today.getDate()){
					listTransferToday.push(item);
				}
			</#if>
		</#if>
		<#break>
	</#list>
</#list>
</script>
<div class="totalValueTranferToday">
	<i class="fa-truck"></i> <lable>${uiLabelMap.RequestsTransferredToday}</lable>
	<a href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListTransferToday()" >
		<div id="valueTranferToday"></div>
	</a>
</div> 
<div id="alterpopupWindowTransferToday" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListTranferRequireToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridTransferToday"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelTransferToday" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	var transferTypeData = [];
	if (LocalData != undefined && LocalData.object != undefined && LocalData.object.transferTypeData != undefined){
		transferTypeData = LocalData.object.transferTypeData;
	}
	$(document).ready(function() {
		$("#valueTranferToday").html(listTransferToday.length.toLocaleString(locale));
		$("#alterpopupWindowTransferToday").jqxWindow($.extend( {cancelButton: $("#btnCancelTransferToday")}, LocalData.config.jqxwindow ));
		var source =
		{
				datafields:
				[
					{ name: "transferId", type: "string" },
					{ name: "transferTypeId", type: "string" },
					{ name: "transferDate", type: "date" },
					{ name: "transferTypeId", type: "string" }
				],
				localdata: listTransferToday,
				datatype: "array",
		}; 
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridTransferToday").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: "", columntype: "number", width: 50,
					cellsrenderer: function (row, column, value) {
						return "<div style=margin:4px;>" + (value + 1) + "</div>";
					}
				},
				{ text: "${uiLabelMap.TransferId}", datafield: "transferId", width: 120,
					cellsrenderer: function(row, column, value){
						return "<span><a target=\"_blank\" href=\"" + "viewDetailTransfer?transferId=" + value + "\">" + value + " <i class=\"fa fa-exchange\"></i></a></span>";
					}
				},
				{ text: "${uiLabelMap.TransferType}", datafield: "transferTypeId", minwidth: 200, filtertype: "checkedlist",
					cellsrenderer: function(row, column, value){
						if (value){
							for (var i = 0; i < transferTypeData.length; i ++){
								if (value == transferTypeData[i].transferTypeId){
									return "<span>" + transferTypeData[i].description + "<span>";
								}
							}
						}
						return "<span>" + value + "<span>";
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(transferTypeData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'transferTypeId', valueMember: 'transferTypeId',
							renderer: function(index, label, value){
					        	if (transferTypeData.length > 0) {
									for(var i = 0; i < transferTypeData.length; i++){
										if(transferTypeData[i].transferTypeId == value){
											return '<span>' + transferTypeData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: "${uiLabelMap.TransferDate}", dataField: "transferDate", cellsformat: "dd/MM/yyyy", filtertype:"range", width: 200 },
			]
		}, LocalData.config.jqxgrid));
	});


function showListTransferToday(){
	var wtmp = window;
	var tmpwidth = $("#alterpopupWindowTransferToday").jqxWindow("width");
	$("#alterpopupWindowTransferToday").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	$("#alterpopupWindowTransferToday").jqxWindow("open");
}
</script>
