<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<#assign dataField="[{ name: 'productId', type: 'string'},
						   { name: 'contentId', type: 'string'},
						   { name: 'qualityPublicationName', type: 'string'},
						   { name: 'fromDate', type: 'date', other: 'Timestamp'},
						   { name: 'thruDate', type: 'date', other: 'Timestamp'},
						   { name: 'expireDate', type: 'number'},
						   ]"/>
<#assign columnlist="
			        { text: '${uiLabelMap.productQualityName}' ,filterable: true, sortable: true, datafield: 'qualityPublicationName', editable: false},
			        { text: '${uiLabelMap.ProductName}' ,filterable: true, sortable: true, datafield: 'productId', width: 220, filtertype: 'checkedlist', editable: false,
						cellsrenderer: function(row, colum, value){
	    			        return '<span>' + mapProduct[value] + '</span>';
	    		        },createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listProduct), displayMember: 'productId', valueMember: 'productId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapProduct[value];
				                } });
	    		        	editor.jqxDropDownList('checkAll');
	                    }
	    		    },
			        { text: '${uiLabelMap.shelfLife}' ,filterable: false, sortable: true, datafield: 'expireDate', width: 190, editable: false,
                    	cellsrenderer: function(row, colum, value){
						        return '<span>' + value + ' ${uiLabelMap.DayLowercase}' + '</span>';
                    	}
                    },
					{ text: '${uiLabelMap.fromDateOfPubich}', datafield: 'fromDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.thruDateOfPubich}', datafield: 'thruDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					"/>
					
    <@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
						showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
						customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CreateProductQuality}@CreateProductQuality"
						url="jqxGeneralServicer?sname=JQGetListQualityPublication"
						contextMenuId="contextMenu" mouseRightMenu="true"
							/>
    		        
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
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
<div>${uiLabelMap.AgreementScanFile}</div>
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

<div id='contextMenu' style="display:none;">
<ul>
	<li id='saveFile'><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
	<li id='viewFile'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
</ul>
</div>

<#assign listProduct = delegator.findList("Product", null, null, null, null, false) />
<script>
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
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
		var productId = rowData.productId;
		productId += rowData.fromDate.toTimeOlbius();
		productId += rowData.thruDate.toTimeOlbius();
		getFileScan(productId);
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
		var productId = rowData.productId;
		productId += rowData.fromDate.toTimeOlbius();
		productId += rowData.thruDate.toTimeOlbius();
		var newFolder = "/delys/QualityPublicationFile_" + productId;
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
    var listProduct = [
					<#if listProduct?exists>
						<#list listProduct as item>
						{
							productId: "${item.productId?if_exists}",
							internalName: "${item.internalName?if_exists}"
						},
						</#list>
					</#if>
                   ];
	var mapProduct = {
		<#if listProduct?exists>
			<#list listProduct as item>
				"${item.productId?if_exists}": "${item.internalName?if_exists}",
			</#list>
		</#if>
	};
	function getFileScan(productId) {
    	var path = '/DELYS/delys/QualityPublicationFile_' + productId;
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
    function fixSelectAll(dataList) {
        	var sourceST = {
			        localdata: dataList,
			        datatype: "array"
			    };
			var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
            var uniqueRecords2 = filterBoxAdapter2.records;
			uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
</script>