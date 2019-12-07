<@jqGridMinimumLib/>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<style>
	#groupsheader {
		display: none;
	}
	#toolbarjqxgrid {
		display: none;
	}
	.widget-header {
		margin-bottom: -35px;
	}
</style>
<div id='container'></div>
<div class="row-fluid">
	<div class="span12">
		<div id="jqxgrid"></div>
	</div>
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

<#if security.hasEntityPermission("AGREEMENT_PURCHASE", "_APPROVE", session)>
	<div id='contextMenu' style="display:none;">
		<ul>
			<li id='approvedAgreement'><i class='icon-check'></i>&nbsp;&nbsp;${uiLabelMap.ApprovedAgreement}</li>
		</ul>
	</div>
	<script>
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 30, autoOpenPopup: false, mode: 'popup'});
		contextMenu.on('shown', function () {
			var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
			var statusId = rowData.statusId;
			if (statusId == "AGREEMENT_CREATED") {
				contextMenu.jqxMenu('disable', "approvedAgreement", false);
			}else {
				contextMenu.jqxMenu('disable', "approvedAgreement", true);
			}
		});
		contextMenu.on('itemclick', function (event){
		    var element = event.args;
		    var idElement = $(element).attr( "id" );
		    switch (idElement) {
			case "approvedAgreement":
				updateAgreementStatus();
				break;
			default:
				break;
			}
		});
	</script>
<#else>
	<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
	<div id='contextMenu' style="display:none;">
		<ul>
			<li id='saveFile'><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
			<li id='viewFile'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
			<li id='menuSentEmail'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreement}</li>
		</ul>
	</div>
	<script>
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 84, autoOpenPopup: false, mode: 'popup'});
		$("#menuSentEmail").on("click", function() {
			var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
			var agreementId = rowData.agreementId;
			window.location.href = "exportData?agreementId=" + agreementId;
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
			$('#tarDescriptionEditor').jqxEditor('val', cell.value);
		});
		
	</script>
	</#if>
</#if>
<script>
	function prepareAgreementToEmail() {
		var filtergroup = new $.jqx.filter();
		var filter_or_operator = 1;
		var filtervalue = "AGREEMENT_APPROVED";
		var filtercondition = 'equal';
		var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);              			
	    filtergroup.addfilter(filter_or_operator, filter1);
	    $("#jqxgrid").jqxGrid('addfilter', 'statusId', filtergroup);
	    $("#jqxgrid").jqxGrid('applyfilters');
	}
	function prepareAgreementToApprove() {
		var filtergroup = new $.jqx.filter();
		var filter_or_operator = 1;
		var filtervalue = "AGREEMENT_CREATED";
		var filtercondition = 'equal';
		var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);              			
		filtergroup.addfilter(filter_or_operator, filter1);
		$("#jqxgrid").jqxGrid('addfilter', 'statusId', filtergroup);
		$("#jqxgrid").jqxGrid('applyfilters');
	}
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	
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
	
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	$("#jqxgrid").on('rowclick', function (event) {
		$('#jqxgrid').jqxGrid('clearselection');
        if (event.args.rightclick) {
            $("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
            var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
            if (rowindex == -1) {
            	return false;
			}
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
            return false;
        }
    });
	var productPlanId = "${parameters.productPlanId?if_exists}";
	$(document).ready(function () {
		if (!productPlanId) {
			window.location.href = "getImportPlans";
		} else {
			getListAgreementAjax(productPlanId, bindDataViewListAgreement);
		}
	});
	function getListAgreementAjax(productPlanId, callback) {
		var listAgreements = [];
		var yearName = "";
		$.ajax({
			  url: "getListAgreementAjax",
			  type: "POST",
			  data: {productPlanId: productPlanId},
			  success: function(res) {
				  listAgreements = res["listAgreements"];
				  yearName = res["yearName"];
			  }
	  	}).done(function() {
		  	callback(listAgreements);
	  	});
	}
	function bindDataViewListAgreement(listAgreements) {
		for ( var x in listAgreements) {
			listAgreements[x].agreementDate?listAgreements[x].agreementDate=listAgreements[x].agreementDate['time']:listAgreements[x].agreementDate;
 		}
		var source =
        {
            localdata: listAgreements,
            datatype: "local",
            datafields:
            [
               { name: 'agreementId', type: 'string'},
			   { name: 'attrValue', type: 'string'},
			   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
			   { name: 'partyIdFrom', type: 'string'},
			   { name: 'partyIdTo', type: 'string'},
			   { name: 'description', type: 'string'},
			   { name: 'statusId', type: 'string'},
			   { name: 'week', type: 'string'},
			   { name: 'month', type: 'string'}
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(true);
            },
            deleterow: function (rowid, commit) {
                
                commit(true);
            },
            updaterow: function (rowid, newdata, commit) {
            	
                commit(true);
            }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#jqxgrid").jqxGrid({
            source: dataAdapter,
            localization: {groupsheaderstring: "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}", filterselectstring: "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}", emptydatastring: "${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}"},
            showfilterrow: true,
            filterable: true,
            editable:false,
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	$('#jqxgrid').jqxGrid('clearfilters');
                	return true;
                }
                if (key == 69 && event.ctrlKey) {
                	prepareAgreementToEmail();
                	return true;
                }
                if (key == 65 && event.ctrlKey) {
                	prepareAgreementToApprove();
                	return true;
                }
		 	},
		 	groupable: true,
		 	groups: ['month', 'week'],
		 	width: '100%',
            height: 500,
            theme: 'olbius',
            sortable: true,
            selectionmode: 'singlerow',
            columns: [
					{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', align: 'center', width: 120, editable: false, groupable: false,
						cellsrenderer: function(row, colum, value){
					        var link = 'detailPurchaseAgreement?agreementId=' + value;
					        return '<div style=\"margin-top: 5px;\"><a style=\"margin: 4px;\" href=\"' + link + '\">' + value + '</a></div>';
						}
					},
					{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', align: 'center', width: 200, editable: false, groupable: false},
					{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', align: 'center', width: 200, editable: false, groupable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.Supplier}', datafield: 'partyIdTo', align: 'center', width: 200, filtertype: 'checkedlist', editable: false, groupable: false,
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin-top: 5px;\">' + mapPartyNameView[value] + '</div>';
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
					{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center', minwidth: 150, editable: false, groupable: false },
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'center', width: 150, editable: true, groupable: false, columntype: 'dropdownlist', filtertype: 'checkedlist',
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
					        return '<div style=\"margin-top: 5px;\">' + status + '</div>';
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
					},
					{ text: '${uiLabelMap.Week}', datafield: 'week', align: 'center', width: 150, editable: false, hidden: true },
					{ text: '${uiLabelMap.Month}', datafield: 'month', align: 'center', width: 150, editable: false, hidden: true }
	            ]
        });
        $('#pagerjqxgrid').addClass('hidden');
	}
	$("#jqxgrid").on('bindingComplete', function(){
		$('.jqx-icon-close-olbius').off('click');
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
	$('.jqx-icon-close').unbind('click');
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
	function updateAgreementStatus() {
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var agreementId = rowData.agreementId;
		$("#jqxgrid").jqxGrid('setcellvalue', rowindex	, "statusId", "AGREEMENT_APPROVED");
		var listAgreementId = new Array();
		listAgreementId.push(agreementId);
			var jsonObject = { agreementId: listAgreementId, statusId: "AGREEMENT_APPROVED"};
			jQuery.ajax({
			    url: "updateAgreementOnlyStatus",
			    type: "POST",
			    data: jsonObject,
			    success : function(res) {
			    }
			}).done(function() {
				var header = "${StringUtil.wrapString(uiLabelMap.ImportAgreement)} " + agreementId + " ${StringUtil.wrapString(uiLabelMap.ImportWasapproved)}";
				createNotification(agreementId, "importadmin", header);
			});
	}
	function createNotification(agreementId, partyId, messages) {
			var targetLink = "agreementId=" + agreementId;
			var action = "getPendingAgreements";
			var header = messages;
			var d = new Date();
			var newDate = d.getTime() - (0*86400000);
			var dateNotify = new Date(newDate);
			var getFullYear = dateNotify.getFullYear();
			var getDate = dateNotify.getDate();
			var getMonth = dateNotify.getMonth() + 1;
			dateNotify = getFullYear + "-" + getMonth + "-" + getDate;
			var jsonObject = {partyId: partyId,
								header: header,
								openTime: dateNotify,
								action: action,
								targetLink: targetLink};
			jQuery.ajax({
		        url: "createNotification",
		        type: "POST",
		        data: jsonObject,
		        success: function(res) {
		        	
		        }
		    }).done(function() {
		    	$("#btnCancel").click();
			});
	}
</script>