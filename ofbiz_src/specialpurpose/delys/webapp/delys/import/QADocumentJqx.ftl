<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<#assign dataField="[{ name: 'documentCustomsId', type: 'string'},
					{ name: 'documentCustomsTypeId', type: 'string'},
					{ name: 'registerNumber', type: 'string'},
					{ name: 'resultNumber', type: 'string'},
					{ name: 'containerId', type: 'string'},
					{ name: 'registerDate', type: 'date', other: 'date'},
					{ name: 'sampleSendDate', type: 'date', other: 'date'},
					{ name: 'resultDate', type: 'date', other: 'date'},
					{ name: 'dateSendToOtherParty', type: 'date', other: 'date'},
					{ name: 'clearanceDate', type: 'date', other: 'date'},
					{ name: 'accountantReceiveDate', type: 'date', other: 'date'}
					]"/>

<#assign columnlist="
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.documentCustomsId)}', datafield: 'documentCustomsId', width: 150, align: 'center', editable:false},
					{ text: '${StringUtil.wrapString(uiLabelMap.documentCustomsTypeId)}', datafield: 'documentCustomsTypeId', editable:false, width: 200, align: 'center',
						cellsrenderer: function(row, colum, value){
					        return '<span>' + mapDocumentCustomsType[value] + '</span>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.registerNumber)}', datafield: 'registerNumber', editable:false, width: 200, align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.registerDate)}', datafield: 'registerDate', editable:false, width: 200, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.sampleSendDate)}', datafield: 'sampleSendDate', width: 200, editable:false, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.resultNumber)}', datafield: 'resultNumber', width: 250, align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.resultDate)}', datafield: 'resultDate', width: 200, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.dateSendToOtherParty)}', datafield: 'dateSendToOtherParty', width: 200, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.clearanceDate)}', datafield: 'clearanceDate', width: 200, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${StringUtil.wrapString(uiLabelMap.ContainerId)}', datafield: 'containerId', width: 200, align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.accountantReceiveDate)}', datafield: 'accountantReceiveDate', width: 200, align: 'center', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype: 'range' }
					"/>

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
	url="jqxGeneralServicer?sname=JQGetListDocumentCustoms" viewSize="15"
	updateUrl="jqxGeneralServicer?sname=updateDocumentCustoms&jqaction=U"
	editColumns="documentCustomsId;documentCustomsTypeId;registerNumber;resultNumber;containerId;registerDate(java.sql.Date);sampleSendDate(java.sql.Date);resultDate(java.sql.Date);dateSendToOtherParty(java.sql.Date);clearanceDate(java.sql.Date);accountantReceiveDate(java.sql.Date)"
	contextMenuId="contextMenu" mouseRightMenu="true"
/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='saveFile'><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
		<li id='viewFile'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
	</ul>
</div>

<div id="jqxwindowUploadFile" style="display:none;">
	<div>${uiLabelMap.SaveFileScan}</div>
	<div>
		<div class="row-fluid">
			<div class="span12" style="overflow-y: hidden;overflow-y: auto;">
				<input multiple type="file" id="id-input-file-3"/>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity:0.2;">
		<div class="form-action">
	    	<div class="row-fluid">
	    		<div class="span12 margin-top10">
	    			<button id='cancelUpload' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
	    			<button id='btnUploadFile' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	    		</div>
	    	</div>
		</div>
	</div>
</div>

<div id="jqxwindowViewFile" style="display:none;">
	<div>${uiLabelMap.DocumentScanFile}</div>
	<div>
		<div class="row-fluid">
			<div class="span12" style="overflow-y: hidden;overflow-y: auto;" id="contentViewerFile">
			</div>
		</div>
		<div class="form-action">
	    	<div class="row-fluid">
	    		<div class="span12 margin-top10">
	    			<button id='cancelViewer' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.close}</button>
	    		</div>
	    	</div>
		</div>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign listDocumentCustomsTypes = delegator.findList("DocumentCustomsType", null, null, null, null, false) />
<script>
var listDocumentCustomsTypes = [
				<#if listDocumentCustomsTypes?exists>
					<#list listDocumentCustomsTypes as item>
					{
						documentCustomsTypeId: "${item.partyId?if_exists}",
						description: "${StringUtil.wrapString(item.description?if_exists)}"
					},
					</#list>
				</#if>
			       ];
var mapDocumentCustomsType = {
			        <#if listDocumentCustomsTypes?exists>
			        		<#list listDocumentCustomsTypes as item>
			        			"${item.documentCustomsTypeId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
			        		</#list>
			       </#if>
					};

$("#jqxNotificationNested").jqxNotification({ width: "100%", theme: 'olbius', appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 58, autoOpenPopup: false, mode: 'popup'});
$("#jqxgrid").on('contextmenu', function () {
    return false;
});
$("#saveFile").on("click", function() {
	$("#jqxwindowUploadFile").jqxWindow("open");
});
$("#viewFile").on("click", function() {
	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
	var documentCustomsId = rowData.documentCustomsId;
	getFileScan(documentCustomsId);
});
$("#jqxwindowUploadFile").jqxWindow({ theme: 'olbius',
    width: 480, maxWidth: 1845, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"), modalOpacity: 0.7
});
var listImage = [];
$(document).ready(function() {
	$('#id-input-file-3').ace_file_input({
    		style:'well',
    		btn_choose:'Drop files here or click to choose',
    		btn_change:null,
    		no_icon:'icon-cloud-upload',
    		droppable:true,
    		onchange:null,
    		thumbnail:'small',
    		before_change:function(files, dropped) {
    			listImage = [];
    			for (var int = 0; int < files.length; int++) {
    				var imageName = files[int].name;
    				var hashName = imageName.split(".");
    				var extended = hashName.pop().toLowerCase();
    				if (extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
    					listImage.push(files[int]);
    				}else {
						return false;
					}
    			}
    			return true;
    		},
    		before_remove : function() {
    			listImage = [];
    			return true;
    		}
	}).on('change', function(){
		
	});
});
$("#btnUploadFile").click(function () {
	if (listImage.length == 0) {
		alert("${StringUtil.wrapString(uiLabelMap.ChooseImagesToUpload)}");
	}
	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
	var documentCustomsId = rowData.documentCustomsId;
	var newFolder = "/delys/DocumentFile_" + documentCustomsId;
	var success = true;
	for ( var d in listImage) {
		var file = listImage[d];
		var dataResourceName = file.name;
		var path = "";
		var form_data= new FormData();
		form_data.append("uploadedFile", file);
		form_data.append("folder", newFolder);
		jQuery.ajax({
			url: "uploadDemo",
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
	$('#jqxNotificationNested').jqxNotification('closeLast');
	if (success) {
		$("#jqxNotificationNested").jqxNotification({ template: 'info'});
      	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
      	$("#jqxNotificationNested").jqxNotification("open");
	}else {
		$("#jqxNotificationNested").jqxNotification({ template: 'error'});
		$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
      	$("#jqxNotificationNested").jqxNotification("open");
	}
	$("#jqxwindowUploadFile").jqxWindow('close');
});

$("#jqxwindowViewFile").jqxWindow({ theme: 'olbius',
    width: 700, maxWidth: 1845, height: "100%", resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelViewer"), modalOpacity: 0.7
});
function getFileScan(documentCustomsId) {
	var path = '/DELYS/delys/DocumentFile_' + documentCustomsId;
	var jsonObject = {nodePath : path,}
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
function test() {
	setTimeout(function() {
	}, 1000);
	setTimeout(function() {
	}, 100);
	_.delay(function() {
	}, 1500);
}
function con() {
}
</script>