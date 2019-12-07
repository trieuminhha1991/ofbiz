<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_VIEW", "")>

<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/crmresources/js/progressing.js"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<#if !agreementUrl?exists>
	<#assign agreementUrl="jqxGeneralServicer?sname=JQGetListAgreementsOfPartner&partyIdFrom=${partyIdFrom?if_exists}&agreementId=${parameters.agreementId?if_exists}"/>
</#if>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					{ name: 'agreementCode', type: 'string' },
					{ name: 'partyFrom', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'agreementTypeId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'remainDays', type: 'number' },
					{ name: 'agrementValue', type: 'number' },
					{ name: 'paymentMethod', type: 'string' },
					{ name: 'paymentFrequen', type: 'string' },
					{ name: 'deliverDateFrequen', type: 'string' },
					{ name: 'grandTotalOrder', type: 'number' },
					{ name: 'grandTotalAgreement', type: 'number' },
					{ name: 'moneyRemain', type: 'number' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp' },
					{ name: 'thruDate', type: 'date', other: 'Timestamp' }]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', editable: false, pinned: true, groupable: false, sortable: false, filterable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAgreementId)}', datafield: 'agreementCode', width: 120, editable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#listAgreementCustomer').jqxGrid('getrowdata', 0);
							return '<div style=margin:4px;><a href=CreateAgreement?agreementId=' + data.agreementId + '>' + value + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyFrom)}', datafield: 'partyFrom', align: 'left', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', align: 'left', filtertype: 'checkedlist', width: 150, editable: true,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapStatusItem[value];
								}
							});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsRemainDays)}', datafield: 'remainDays', width: 150, filtertype: 'number' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsValue)}' , datafield: 'agrementValue', width: 150, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsGrandTotalAgreement)}' , datafield: 'grandTotalAgreement', width: 150, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsGrandTotalOrder)}' , datafield: 'grandTotalOrder', width: 150, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsMoneyRemain)}' , datafield: 'moneyRemain', width: 150, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPaymentFormality)}' , datafield: 'paymentMethod', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPaymentMethod)}' , datafield: 'paymentFrequen', filtertype: 'checkedlist', width: 150,
						cellsrenderer: function(row, colum, value){
							value?value=mapPaymentMethod[value]:value;
							return '<span >' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listPaymentMethod, displayMember: 'description', valueMember: 'termDays' ,
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapPaymentMethod[value];
								}
							});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDeliveryTime)}' , datafield: 'deliverDateFrequen', width: 250 }"/>

<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_NEW", "")>
	<#if partyId?exists && partyId != "">
		<#assign isShowTitleProperty="false"/>
		<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.CreateNewAgreement}@CreateAgreement?partyId=${partyId}$target='_blank'"/>
	<#else>
		<#assign isShowTitleProperty="true"/>
		<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.CreateNewAgreement}@CreateAgreement"/>
	</#if>
</#if>
<#if !timeout?exists>
	<#assign timeout="0"/>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
	<#assign jqGridMinimumLibEnable=""/>
</#if>

<@jqGrid id="listAgreementCustomer" filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editmode="dblclick"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
	customcontrol1="${tmpCreateUrl?if_exists}" isShowTitleProperty=isShowTitleProperty autorowheight="true"
	url=agreementUrl timeout=timeout customLoadFunction=customLoadFunction jqGridMinimumLibEnable=jqGridMinimumLibEnable
	contextMenuId="contextMenu" mouseRightMenu="true"/>

<#assign height=84 />
<div id="contextMenu" style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_EDIT", "")>
		<#assign height=110 />
		<li id="approveAgreement"><i class="fa-check"></i>&nbsp;&nbsp;${uiLabelMap.DmsApprove}</li>
		</#if>
		<li id="createOrder"><i class="fa-cart-plus"></i>&nbsp;&nbsp;${uiLabelMap.DACreateOrder}</li>
		<li id="saveFile"><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
		<li id="viewFile"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
	</ul>
</div>

<div id="jqxwindowUploadFile" style="display:none;">
	<div>${uiLabelMap.SaveFileScan}</div>
	<div>
		<div class="row-fluid">
			<div class="span12" style="overflow-y: hidden;overflow-y: auto;">
				<input multiple type="file" id="id-input-file-3" accept="image/*"/>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity:0.2;">
		<div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="cancelUpload" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
					<button id="btnUploadFile" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="jqxwindowViewFile" style="display:none;">
<div>${uiLabelMap.AgreementScanFile}</div>
<div>
	<div class="row-fluid">
		<div class="span12" style="overflow-y: hidden;overflow-y: auto;" id="contentViewerFile">
		</div>
	</div>
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelViewer" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.close}</button>
			</div>
		</div>
	</div>
</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />
<script>

	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: "${item.statusId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	var mapPaymentMethod = {"COD": "Sau khi nhận hàng", 0: "1 lần", 30: "Theo tháng", 90: "Theo quý"};
	var listPaymentMethod = [{termDays: "COD", description: "Sau khi nhận hàng"}, {termDays: 0, description: "1 lần"},
	                         {termDays: 30, description: "Theo tháng"}, {termDays: 90, description: "Theo quý"}];
	
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	$("#contextMenu").jqxMenu({ theme: theme, width: 170, height: "${height}", autoOpenPopup: false, mode: "popup"});
	$("#listAgreementCustomer").on("contextmenu", function () {
		return false;
	});
	$("#contextMenu").on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		switch (itemId) {
		case "createOrder":
			var rowIndexSelected = $("#listAgreementCustomer").jqxGrid("getSelectedRowindex");
			var rowData = $("#listAgreementCustomer").jqxGrid("getrowdata", rowIndexSelected);
			window.open("newSalesOrder?partyId=" + rowData.partyIdFrom + "&agreementId=" + rowData.agreementId, "_blank");
			break;
		case "saveFile":
			$("#jqxwindowUploadFile").jqxWindow("open");
			break;
		case "viewFile":
			var rowIndexSelected = $("#listAgreementCustomer").jqxGrid("getSelectedRowindex");
			var rowData = $("#listAgreementCustomer").jqxGrid("getrowdata", rowIndexSelected);
			var agreementId = rowData.agreementId;
			getFileScan(agreementId);
			break;
		case "approveAgreement":
			var rowIndexSelected = $("#listAgreementCustomer").jqxGrid("getSelectedRowindex");
			var rowData = $("#listAgreementCustomer").jqxGrid("getrowdata", rowIndexSelected);
			var agreementId = rowData.agreementId;
			var result = DataAccess.execute({
					url: "approveAgreement",
					data: {agreementId: agreementId}
					});
			if (result) {
				$("#listAgreementCustomer").jqxGrid("setcellvaluebyid", rowData.uid, "statusId", "AGREEMENT_APPROVED");
				$("#listAgreementCustomer").jqxGrid("refreshdata");
			}
			break;
		default:
			break;
		}
	});
	$("#contextMenu").on("shown", function () {
		var rowIndexSelected = $("#listAgreementCustomer").jqxGrid("getSelectedRowindex");
		var rowData = $("#listAgreementCustomer").jqxGrid("getrowdata", rowIndexSelected);
		var statusId = rowData.statusId;
		if (statusId == "AGREEMENT_CREATED" || statusId == "AGREEMENT_MODIFIED") {
			$("#contextMenu").jqxMenu("disable", "approveAgreement", false);
		}else {
			$("#contextMenu").jqxMenu("disable", "approveAgreement", true);
		}
	});
	function reloadAgeement() {
		$("#listAgreementCustomer").jqxGrid("updatebounddata");
	}
	$("#jqxwindowUploadFile").jqxWindow({ theme: theme,
		width: 480, maxWidth: 1845, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"), modalOpacity: 0.7
	});
	var listImage = [];
	$(document).ready(function() {
		if(getCookie().checkContainValue("newContact")){
			deleteCookie("newContact");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
			$("#jqxNotificationNested").jqxNotification("open");
		}
		if(getCookie().checkContainValue("updateContact")){
			deleteCookie("updateContact");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			$("#jqxNotificationNested").jqxNotification("open");
		}
		$("#id-input-file-3").ace_file_input({
			style:"well",
			btn_choose:"Drop files here or click to choose",
			btn_change:null,
			no_icon:"icon-cloud-upload",
			droppable:true,
			onchange:null,
			thumbnail:"small",
			before_change:function(files, dropped) {
				listImage = [];
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop().toLowerCase();
					if (extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
						listImage.push(files[int]);
					} else {
						return false;
					}
				}
					return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		}).on("change", function(){
			
		});
	});
	$("#btnUploadFile").click(function () {
		if (listImage.length == 0) {
			alert("${StringUtil.wrapString(uiLabelMap.ChooseImagesToUpload)}");
		}
		var rowIndexSelected = $("#listAgreementCustomer").jqxGrid("getSelectedRowindex");
		var rowData = $("#listAgreementCustomer").jqxGrid("getrowdata", rowIndexSelected);
		var agreementId = rowData.agreementId;
		var newFolder = "/delys/agreementFile_" + agreementId;
		var success = true;
		for ( var d in listImage) {
			var file = listImage[d];
			var dataResourceName = file.name;
			var path = "";
			var form_data= new FormData();
			form_data.append("uploadedFile", file);
			form_data.append("folder", newFolder);
			jQuery.ajax({
				url: "jackrabbitUploadFile",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				async: false,
				success: function(res) {
					path = res["path"];
				}
			}).done(function() {
				if (!path) {
					success = false;
				}
			});
		}
		$("#jqxNotificationNested").jqxNotification("closeLast");
		if (success) {
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			$("#jqxNotificationNested").jqxNotification("open");
		} else {
			$("#jqxNotificationNested").jqxNotification({ template: "error"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
			$("#jqxNotificationNested").jqxNotification("open");
		}
		$("#jqxwindowUploadFile").jqxWindow("close");
	});
	
	$("#jqxwindowViewFile").jqxWindow({ theme: theme,
		width: 700, height: "100%", resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancelViewer"), modalOpacity: 0.7
	});
	function getFileScan(agreementId) {
		var path = "/DELYS/delys/agreementFile_" + agreementId;
		var jsonObject = {nodePath : path};
		var fileUrl = [];
		jQuery.ajax({
			url: "getFileScanAjax",
			type: "POST",
			data: jsonObject,
			success: function(res) {
				fileUrl = res["childNodes"];
			}
		}).done(function() {
			if (!fileUrl) {
				$("#contentViewerFile").html("<h1><p style='color: #999999'>${uiLabelMap.FileNotFound}!</p></h1>");
			}else {
				var imagesContentHtml = "";
				for ( var er in fileUrl) {
					var link = "/webdav/repository/default" + path + "/" + fileUrl[er];
					imagesContentHtml += "<img src='" + link + "'><br/>";
				}
				$("#contentViewerFile").html(imagesContentHtml);
			}
			$("#jqxwindowViewFile").jqxWindow("open");
		});
	}
	function fixAgreement() {
		var data = DataAccess.getData({
						url: "fixAgreement",
						data: {},
						source: "done"});
		return data;
	}
</script>

	<#else>
	<h2> You do not have permission</h2>
</#if>