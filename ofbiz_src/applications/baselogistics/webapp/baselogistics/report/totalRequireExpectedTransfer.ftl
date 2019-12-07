<script>
<#assign transferHeaders = delegator.findList("TransferHeader", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "TRANSFER_APPROVED")), null, null, null, true) />
var listAllTransfer = [];
var listTransferExpired = [];
var listTransferTomorrow = [];
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
			listAllTransfer.push(item);
			<#if (header.transferDate?exists && nowTimestamp.after(header.transferDate)) || (item.shipAfterDate?exists && nowTimestamp.after(item.shipAfterDate))>
				listTransferExpired.push(item);
			</#if>
			<#if ((header.transferDate?exists && nowTimestamp.before(header.transferDate)) || (item.shipAfterDate?exists && nowTimestamp.before(item.shipAfterDate)) || (item.shipBeforeDate?exists && nowTimestamp.before(item.shipBeforeDate)))>
				var date1 = new Date("${header.transferDate?if_exists}");
				var date2 = new Date("${header.shipBeforeDate?if_exists}");
				var date3 = new Date("${header.shipAfterDate?if_exists}");
				var tomorrow = new Date();
				var x = tomorrow.getDate();
				tomorrow.setDate(x + 1);
				tomorrow.setHours(0);
				tomorrow.setMinutes(0);
				tomorrow.setSeconds(0);
				if ((date1 && date1.getDate() == tomorrow.getDate()) || (date2 && date2.getDate() == tomorrow.getDate()) || (date3 && date3.getDate() == tomorrow.getDate())){
					listTransferTomorrow.push(item);
				}
			</#if>
		</#if>
		<#break>
	</#list>
</#list>
</script>
<div>
	<div class="titleTextTransfer">
		<i class="fa-exchange"></i> <lable style="cursor: auto;">${uiLabelMap.Transfer}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.TotalNotTransfer}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListTransfer('TotalNotTransfer')">
								<div id="totalTransferNeedReceive"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.NotTimelyTransferRequest}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListTransfer('NotTimelyTransferRequest')" >
								<div id="totalNotTransferTime"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.RequestTransferTomorrow}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListTransfer('RequestTransferTomorrow')" >
								<div id="totalTransferTomorrow"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="alterpopupWindowTransfer" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListTranferRequireToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridTransfer"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelTransfer" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	var transferTypeData = [];
	if (LocalData != undefined && LocalData.object != undefined && LocalData.object.transferTypeData != undefined){
		transferTypeData = LocalData.object.transferTypeData;
	}
	$(document).ready(function() {
		$("#totalTransferTomorrow").html(listTransferTomorrow.length.toLocaleString(locale));
		$("#totalTransferNeedReceive").html(listAllTransfer.length.toLocaleString(locale));
		$("#totalNotTransferTime").html(listTransferExpired.length.toLocaleString(locale));
		
		$("#alterpopupWindowTransfer").jqxWindow($.extend( {cancelButton: $("#btnCancelTransfer")}, LocalData.config.jqxwindow ));
	});

	function showListTransfer(valueTransfer){
		if (valueTransfer == "TotalNotTransfer") { 
			$("#alterpopupWindowTransfer").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.TotalNotTransfer)}");
			bindingDataTransferTodayView(listAllTransfer);
		} else  if (valueTransfer == "NotTimelyTransferRequest") {
			$("#alterpopupWindowTransfer").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.NotTimelyTransferRequest)}");
			bindingDataTransferTodayView(listTransferExpired);
		} else if (valueTransfer == "RequestTransferTomorrow") {
			$("#alterpopupWindowTransfer").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.RequestTransferTomorrow)}");
			bindingDataTransferTodayView(listTransferTomorrow);
		}
	}

function bindingDataTransferTodayView(listTransfer) {
	var sourceOrderItem =
	{
		datafields:
		[
			{name: "transferId", type: "string" },
			{name: "transferTypeId", type: "string" },
			{name: "transferDate", type: "date" },
			{name: "transferType", type: "string" }
		],
		localdata: listTransfer,
		datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(sourceOrderItem);
	$("#jqxgridTransfer").jqxGrid($.extend({
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
			{ text: "${uiLabelMap.TransferId}", datafield: "transferId", width: "150",
				cellsrenderer: function(row, column, value){
					return "<span><a target=\"_blank\" href=\"" + "viewDetailTransfer?transferId=" + value + "\">" + value + " <i class=\"fa fa-exchange\"></i></a></span>";
				}
			},
			{ text: "${uiLabelMap.TransferType}", datafield: "transferTypeId", width: 200, filtertype: "checkedlist",
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
			{ text: "${uiLabelMap.TransferDate}", dataField: "transferDate", cellsformat: "dd/MM/yyyy", filtertype:"range" }
		] 
	}, LocalData.config.jqxgrid));
	var wtmp = window;
	var tmpwidth = $("#alterpopupWindowTransfer").jqxWindow("width");
	$("#alterpopupWindowTransfer").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	$("#alterpopupWindowTransfer").jqxWindow("open");
}
</script>