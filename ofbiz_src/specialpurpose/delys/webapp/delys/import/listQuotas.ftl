<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<div id="myAlert" style="width:100%"></div>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
		nestedGridAdapter = new Array();
	}else {
		var dataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
	    var recordData = dataAdapter.records;
			var nestedGrids = new Array();
	         nestedGrids[index] = grid;
	         var recordDataById = [];
	         for (var ii = 0; ii < recordData.length; ii++) {
	             recordDataById.push(recordData[ii]);
	         }
	         var recordDataSource = { datafields: [	
						{ name: 'quotaId', type: 'string' },
						{ name: 'quotaItemSeqId', type: 'string' },
						{ name: 'productId', type: 'string' },
						{ name: 'productName', type: 'string'},
						{ name: 'quotaQuantity', type: 'string'},
						{ name: 'quantityAvailable', type: 'string'},
						{ name: 'quantityUomId', type: 'string'},
						{ name: 'fromDate', type: 'date', other: 'Timestamp'},
						{ name: 'thruDate', type: 'date', other: 'Timestamp'}
	         		],
	         		updaterow: function (rowid, rowdata, commit) {
	           			commit(true);
	           			var quotaId = rowdata.quotaId;
	           			var quotaItemSeqId = rowdata.quotaItemSeqId;
	           			var quantityAvailable = rowdata.quantityAvailable;
	           			if (quantityAvailable != null && quantityAvailable != undefined) {
	           				var data = {'quotaId': quotaId, 'quotaItemSeqId': quotaItemSeqId, 'quantityAvailable' : quantityAvailable};
		            		$.ajax({
		                        type: 'POST',
		                        url: 'updateQuotaItemAjax',
		                        data: data,
		                        success: function (res) {
		                        	res['_ERROR_MESSAGE_'] == undefined?saveSuccess=true:saveSuccess=false;
		                        },
		                        error: function () {
		                            commit(false);
		                        }
		                    }).done(function() {
						    	if (saveSuccess) {
						    		commit(true);
	                            	$('#notificationContent').text(\"${StringUtil.wrapString(uiLabelMap.DAUpdateSuccessful)}\");
	                            	$('#jqxNotification').jqxNotification(\"open\");
								}else {
									commit(false);
	                            	$('#jqxNotification').jqxNotification({ template: 'error'});
	                            	$('#notificationContent').text('${StringUtil.wrapString(uiLabelMap.DAUpdateError)}');
	                            	$('#jqxNotification').jqxNotification(\"open\");;
								}
							});
	           			} else {
	           				bootbox.dialog('${uiLabelMap.DAQuantityAcceptedNotValid}!', [{
								'label' : 'OK',
								'class' : 'btn-small btn-primary',
								}]
							);
							commit(false);
	           			}
	                },
	             	localdata: recordDataById
	         }
	         var nestedGridAdapter = new $.jqx.dataAdapter(recordDataSource);
	}
	
	 grid.jqxGrid({
	     source: nestedGridAdapter, 
	     width: '96%', 
	     autoheight: true,
	     showtoolbar:false,
		 editable:true,
		 pagesize: 5,
		 pageable: true,
		 editmode:\"click\",
		 showheader: true,
		 selectionmode:\"singlerow\",
		 theme: 'olbius',
	     columns: [
					{ text: '${uiLabelMap.accProductId}', datafield: 'productId', width: '250px', editable:false, align: 'center'},
					{ text: '${uiLabelMap.QuotaQuantity}', datafield: 'quotaQuantity', width: '300px', editable:false, align: 'center', cellsalign: 'right'},
					{ text: '${uiLabelMap.QuantityAvailable}', datafield: 'quantityAvailable', columntype: 'numberinput', align: 'center', cellsalign: 'right',
						 validation: function (cell, value) {
	                		   lastTimeChoice = value;
	                		   var thisRow = cell.row;
	                		   var data = grid.jqxGrid('getrowdata', thisRow);
	            		       var quotaQuantity = data.quotaQuantity;
	                           if (quotaQuantity < value) {
	                        	   return { result: false, message: '${uiLabelMap.QuantityAvailableBigerThanQuotaQuantity}' };
	                            }
	                	       return true;
	                }},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: '150px', align: 'center', editable:false,
	                	cellsrenderer: function(row, colum, value){;
							return '<span>' + mapUoms[value] + '</span>';
	                	}
	                },
	     ]
	 });
 }"/>

<#assign dataField="[{ name: 'quotaId', type: 'string' },
					 { name: 'quotaName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'},
					 { name: 'rowDetail', type: 'string'}
					 ]"/>
<#assign columnlist="					 
						{ text: '${uiLabelMap.QuotaId}', datafield: 'quotaId', width: '150px', align: 'center'},
						{ text: '${uiLabelMap.QuotaName}', datafield: 'quotaName', width: '200px', align: 'center'},
						{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center'},
						{ text: '${uiLabelMap.AvailableFromDate}', datafield: 'fromDate', columntype: 'datetimeinput', align: 'center', filtertype: 'range', width: '200px', cellsformat: 'dd/MM/yyyy'},
						{ text: '${uiLabelMap.AvailableThruDate}', datafield: 'thruDate', columntype: 'datetimeinput', align: 'center', filtertype: 'range', width: '200px', cellsformat: 'dd/MM/yyyy'}
					 "/>

						
		<@jqGrid filtersimplemode="true" alternativeAddPopup="alterpopupWindow" initrowdetails = "true" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow"
			editable="false" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false"
			customcontrol1="icon-plus-sign open-sans@${uiLabelMap.AddQuotas}@AddQuotas"
		 	url="jqxGeneralServicer?sname=JQGetListQuotas" rowdetailsheight="247" 
	 		contextMenuId="contextMenu" mouseRightMenu="true"
	 		/>
		
	<div id="jqxNotification">
        <div id="notificationContent"></div>
    </div>
        
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
		
		<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
<script>
		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: 'info'});
		var listUom = [
					<#if listUoms?exists>
						<#list listUoms as item>
						{
							uomId: "${item.uomId?if_exists}",
							description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
						},
						</#list>
					</#if>
		               ];
		var mapUoms = {
			<#if listUoms?exists>
				<#list listUoms as item>
					"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>
		};
		
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
			var quotaId = rowData.quotaId;
			getFileScan(quotaId);
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
			var quotaId = rowData.quotaId;
			var newFolder = "/delys/QuotaFile_" + quotaId;
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
			$('#jqxNotification').jqxNotification('closeLast');
			if (success) {
				$("#jqxNotification").jqxNotification({ template: 'info'});
              	$("#notificationContent").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: 'error'});
    			$("#notificationContent").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotification").jqxNotification("open");
			}
			$("#jqxwindowUploadFile").jqxWindow('close');
		});
		
		$("#jqxwindowViewFile").jqxWindow({ theme: 'olbius',
		    width: 700, maxWidth: 1845, height: "100%", resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelViewer"), modalOpacity: 0.7
		});
		function getFileScan(quotaId) {
        	var path = '/DELYS/delys/QuotaFile_' + quotaId;
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
</script>