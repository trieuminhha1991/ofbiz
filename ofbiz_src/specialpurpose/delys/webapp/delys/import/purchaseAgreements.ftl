<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>

<#assign dataField="[{ name: 'agreementId', type: 'string'},
						   { name: 'attrValue', type: 'string'},
						   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
						   { name: 'partyIdFrom', type: 'string'},
						   { name: 'partyIdTo', type: 'string'},
						   { name: 'description', type: 'string'},
						   { name: 'statusId', type: 'string'}
						   ]"/>
<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', align: 'center', width: 120, editable: false,
						cellsrenderer: function(row, colum, value){
					        var link = 'detailPurchaseAgreement?agreementId=' + value;
					        return '<span><a href=\"' + link + '\">' + value + '</a></span>';
						}
					},
			        { text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', align: 'center', width: 200, editable: false},
					{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.Supplier}', datafield: 'partyIdTo', align: 'center', width: 200, filtertype: 'checkedlist', editable: false,
						cellsrenderer: function(row, colum, value){
							return '<span>' + mapPartyNameView[value] + '</span>';
						},createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(partyNameView), displayMember: 'partyId', valueMember: 'partyId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapPartyNameView[value];
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
						}
    		        },
					{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center', minwidth: 150, editable: false },
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'center', width: 150, editable: true, columntype: 'dropdownlist', filtertype: 'checkedlist',
			        	createeditor: function(row, column, editor){
			        		editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
				                    var datarecord = listStatus[index];
				                    return datarecord.description;
				                },selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
			  							return '<span title=' + item.value +'>' + mapStatus[item.value] + '</span>';
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
				                }
			                });
						},cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                        if (newvalue == '') return oldvalue;
	                    },cellsrenderer: function(row, colum, value){
	    			        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    			        var statusId = data.statusId;
	    			        var status = mapStatus[statusId];
	    			        return '<span>' + status + '</span>';
	    		        },createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatus), displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapStatus[value];
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
	                    }
    		        }
					"/>
					
    <@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editmode="dblclick"
						showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" 
						customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@getImportPlanToCreateAgreement"
						url="jqxGeneralServicer?sname=JQGetListPurchaseAgreements"
						updateUrl="jqxGeneralServicer?sname=updateAgreementStatus&jqaction=U"
						createUrl="jqxGeneralServicer?sname=createPurchaseAgreements&jqaction=C"
						addColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
						editColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
						contextMenuId="contextMenu" mouseRightMenu="true"
					/>
    		        
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='saveFile'><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
		<li id='viewFile'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
		<li id='editDescription'><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.EditorDescripton}</li>
	</ul>
</div>
				
<div id="jqxwindowEditor" style="display:none;">
    <div>${uiLabelMap.EditorDescripton}</div>
    <div style="overflow-x: hidden;">
    	<div class="row-fluid">
    		<div class="span12">
    			<textarea id="tarDescriptionEditor"></textarea>
    		</div>
    	</div>
    	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
    	<div class="row-fluid">
    		<div class="span12 margin-top10">
    			<button id='cancelEdit' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
    			<button id='saveEdit' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
    		</div>
    	</div>
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

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
			
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script>
		$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 84, autoOpenPopup: false, mode: 'popup'});
		$("#jqxgrid").on('contextmenu', function () {
		    return false;
		});
		$("#saveFile").on("click", function() {
			$("#jqxwindowUploadFile").jqxWindow("open");
		});
		$("#viewFile").on("click", function() {
			var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
			var agreementId = rowData.agreementId;
			getFileScan(agreementId);
		});
		$("#editDescription").on("click", function() {
			rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	    	var cell = $('#jqxgrid').jqxGrid('getcell', rowIndexEditing, "description" );
	    	$("#jqxwindowEditor").jqxWindow('open');
	    	$('#tarDescriptionEditor').jqxEditor({
		        theme: 'olbiuseditor'
		    });
	    	$('#tarDescriptionEditor').val(cell.value);
		});

		$("#jqxwindowEditor").jqxWindow({ theme: 'olbius',
		    width: 550, maxWidth: 1845, minHeight: 310, height: 340, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
		});
		$("#saveEdit").click(function () {
			var newValue = $('#tarDescriptionEditor').val();
			$("#jqxgrid").jqxGrid('setCellValue', rowIndexEditing, "description", newValue);
			$("#jqxwindowEditor").jqxWindow('close');
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
		
		var listStatus = [
						<#if listStatus?exists>
							<#list listStatus as item>
								{
									description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
									statusId: "${item.statusId?if_exists}"
								},
							</#list>
						</#if>
		                  ];
		
		var mapStatus = {
						<#if listStatus?exists>
							<#list listStatus as item>
								"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
							</#list>
						</#if>
						};
		var partyNameView = [
							<#if partyNameView?exists>
								<#list partyNameView as item>
								{
									description: "${item.firstName?if_exists} " + "${item.middleName?if_exists} " + "${item.lastName?if_exists} " + "${item.groupName?if_exists}",
									partyId: "${item.partyId?if_exists}"
								},
								</#list>
							</#if>
		                     ];
		var mapPartyNameView = {
							<#if partyNameView?exists>
								<#list partyNameView as item>
									"${item.partyId?if_exists}": "${item.firstName?if_exists} " + "${item.middleName?if_exists} " + "${item.lastName?if_exists} " + "${item.groupName?if_exists}",
								</#list>
							</#if>
							};
		
        function getFileScan(agreementId) {
        	var path = '/DELYS/delys/agreementFile_' + agreementId;
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
<style type="text/css">
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>