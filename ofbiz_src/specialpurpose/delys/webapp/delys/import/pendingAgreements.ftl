<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div id="container"></div>
<#assign onlyOne = true/>
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
					{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', align: 'center', width: 150, editable: false},
					{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.Supplier}', datafield: 'partyIdTo', align: 'center', width: 220, filtertype: 'checkedlist', editable: false,
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
					{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center', minwidth: 250, editable: false },
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'center', width: 150, editable: false, columntype: 'dropdownlist', filtertype: 'checkedlist',
						createeditor: function(row, column, editor){
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
								renderer: function (index, label, value) {
							        var datarecord = listStatus[index];
							        return datarecord.description;
								}
							});
						},
						cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
							if (newvalue == '') return oldvalue;
						},
						cellsrenderer: function(row, colum, value){
							return '<span>' + mapStatus[value] + '</span>';
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

<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
<#assign onlyOne = false/>
						<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
							showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
							sourceId="agreementId" viewSize="10"
							customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@getImportPlanToCreateAgreement"
							customcontrol2="fa-envelope open-sans@${uiLabelMap.SentEmail}@javascript: void(0);@sentEmailClick()"
							url="jqxGeneralServicer?sname=JQGetListPendingAgreements" updateUrl="jqxGeneralServicer?sname=updateAgreementStatus&jqaction=U"
							createUrl="jqxGeneralServicer?sname=createPurchaseAgreements&jqaction=C"
							addColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							editColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							contextMenuId="contextMenu" mouseRightMenu="true"
						/>

	<div id='contextMenu' style="display:none;">
		<ul>
			<li id='menuSentEmail'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreement}</li>
			<li id='editDescription'><i class='icon-edit'></i>&nbsp;&nbsp;${uiLabelMap.EditorDescripton}</li>
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

    <div id="alterpopupWindowEmail" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.From}</label></div>
	        	<div class="span9"><input type="text" id="txtUserName" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3"><label class="text-right asterisk">${uiLabelMap.FormFieldTitle_pwd}</label></div>
			    <div class="span9"><input type="text" id="txtUserPassword" /></div>
		    </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3"><label class="text-right asterisk">${uiLabelMap.To}</label></div>
			    <div class="span9"><input type="text" id="txtTo" /></div>
		    </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3"><label class="text-right asterisk">${uiLabelMap.Subject}</label></div>
			    <div class="span9"><input type="text" id="txtSubject" /></div>
		    </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3"><label class="text-right asterisk">${uiLabelMap.Content}</label></div>
			    <div class="span9"><textarea style='resize:none' cols='30' rows='5' id='txtContent'></textarea></div>
		    </div>
	    </div>
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelEmail" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveEmail" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.SentEmail}</button>
				</div>
			</div>
		</div>
	</div>
</div>
    
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
    
	<div style="width:100%;display:none;">
		<button id="btnSentEmail"><i class="icon-arrow-right"></i>${uiLabelMap.SentEmail}</button>
		<button id="btnCancelSent"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
	</div>
	<button id="btnCancelSentEmail" class="btn btn-danger form-action-button pull-right" style="display:none;"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
<script type="text/javascript">
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 58, autoOpenPopup: false, mode: 'popup'});
    $("#jqxgrid").on('contextmenu', function () {
        return false;
    });
    $("#menuSentEmail").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		var agreementId = rowData.agreementId;
		window.location.href = "exportData?agreementId=" + agreementId;
	});
    var rowIndexEditing = null;
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
	    width: 550, maxWidth: 1845, minHeight: 330, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
	});
	$("#saveEdit").click(function () {
		var newValue = $('#tarDescriptionEditor').val();
		$("#jqxgrid").jqxGrid('setCellValue', rowIndexEditing, "description", newValue);
		$("#jqxwindowEditor").jqxWindow('close');
	});
	$("#btnCancelSent").jqxButton({template: "danger"});
	$("#btnCancelSent").css('visibility', 'hidden');
	$("#btnSentEmail").jqxToggleButton({theme: "arctic", toggled: true });
	$("#btnSentEmail").on('click', function () {
        var toggled = $("#btnSentEmail").jqxToggleButton('toggled');
        if (toggled) {
        	sentEmail();
        }else {
        	prepareAgreement();
        	$("#btnCancelSentEmail").css('visibility', 'visible');
		}
    });
	$("#btnCancelSentEmail").on('click', function () {
		$('#clearfilteringbuttonjqxgrid').trigger('click');
    	$('#jqxgrid').jqxGrid({ selectionmode: 'singlerow'});
    	$("#btnCancelSentEmail").css("display", "none");
    	fisrtEmailSentClick = true;
	});
//	$("#btnCancelSentEmail").on('click', function () {
//		$('#clearfilteringbuttonjqxgrid').trigger('click');
//		$('#jqxgrid').jqxGrid({ selectionmode: 'singlerow'});
//		$("#btnCancelSentEmail").css("display", "none");
//		fisrtEmailSentClick = true;
//	});
	var fisrtEmailSentClick = true;
	function sentEmailClick() {
		if (fisrtEmailSentClick) {
			agreementIdList = [];
			$('#jqxgrid').jqxGrid('clearSelection');
			prepareAgreement();
			$("#btnCancelSentEmail").css("display", "block");
		} else {
			if (agreementIdList.length == 0) {
				bootbox.alert("${uiLabelMap.NoAgreementSelected}");
				return;
			}
			sentEmail();
		}
		fisrtEmailSentClick = !fisrtEmailSentClick;
	}
	function prepareAgreement() {
		var filtergroup = new $.jqx.filter();
		var filter_or_operator = 1;
  		var filtervalue = "AGREEMENT_APPROVED";
  		var filtercondition = 'equal';
  		var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);              			
        filtergroup.addfilter(filter_or_operator, filter1);
        $("#jqxgrid").jqxGrid('addfilter', 'statusId', filtergroup);
        $("#jqxgrid").jqxGrid('applyfilters');
        $('#jqxgrid').jqxGrid({ selectionmode: 'checkbox'});
	}
</script>
</#if>

<#if security.hasEntityPermission("AGREEMENT_PURCHASE", "_APPROVE", session) && onlyOne>
   <#assign onlyOne = false/>
					<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
							showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
							customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@getImportPlanToCreateAgreement"
						<#--	customcontrol2="icon-check open-sans@${uiLabelMap.Approved}@javascript: void(0);@approvedAgreementClick()" -->
							url="jqxGeneralServicer?sname=JQGetListPendingAgreements" updateUrl="jqxGeneralServicer?sname=updateAgreementStatus&jqaction=U"
							createUrl="jqxGeneralServicer?sname=createPurchaseAgreements&jqaction=C"
							addColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							editColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							contextMenuId="contextMenu" mouseRightMenu="true"
						/>
		<div style="width:100%;display:none;">
			<button id="btnApproved"><i class="icon-ok"></i>${uiLabelMap.Approved}</button>
			<button id="btnCancel"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
		</div>
		<#-- <button id="btnCancelSentEmail" class="btn btn-danger form-action-button pull-right" style="display:none;"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button> -->
		
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='approvedAgreement'><i class='icon-check'></i>&nbsp;&nbsp;${uiLabelMap.ApprovedAgreement}</li>
	</ul>
</div>
<script type="text/javascript">
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 30, autoOpenPopup: false, mode: 'popup'});
		$("#jqxgrid").on('contextmenu', function () {
		    return false;
		});
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
		$("#btnCancel").jqxButton({template: "danger", theme: "arctic" });
		$("#btnCancel").css('visibility', 'hidden');
		$("#btnApproved").jqxToggleButton({toggled: true, theme: "arctic"});
		$("#btnApproved").on('click', function () {
            var toggled = $("#btnApproved").jqxToggleButton('toggled');
            if (toggled) {
            	approvedAgreement();
            }else {
            	prepareAgreement();
            	$("#btnCancel").css('visibility', 'visible');
			}
        });
		$("#btnCancel").on('click', function () {
			$('#clearfilteringbuttonjqxgrid').trigger('click');
        	$('#jqxgrid').jqxGrid({ selectionmode: 'singlerow'});
        	$("#btnCancel").css('visibility', 'hidden');
        	$('#btnApproved').jqxToggleButton({ toggled: true });
		});
//		$("#btnCancelSentEmail").on('click', function () {
//			$('#clearfilteringbuttonjqxgrid').trigger('click');
//			$('#jqxgrid').jqxGrid({ selectionmode: 'singlerow'});
//			$("#btnCancelSentEmail").css("display", "none");
//			firstClick = true;
//		});
		var firstClick = true;
		function approvedAgreementClick() {
			if (firstClick) {
				agreementIdList = [];
				$('#jqxgrid').jqxGrid('clearSelection');
				prepareAgreement();
//				$("#btnCancelSentEmail").css("display", "block");
			} else {
				if (agreementIdList.length == 0) {
					bootbox.alert("${uiLabelMap.NoAgreementSelected}");
					return;
				}
				updateAgreementStatus();
			}
			firstClick = !firstClick;
		}
		function prepareAgreement() {
			var filtergroup = new $.jqx.filter();
			var filter_or_operator = 1;
      		var filtervalue = "AGREEMENT_CREATED";
      		var filtercondition = 'equal';
      		var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);              			
	        filtergroup.addfilter(filter_or_operator, filter1);
	        $("#jqxgrid").jqxGrid('addfilter', 'statusId', filtergroup);
	        $("#jqxgrid").jqxGrid('applyfilters');
	        $('#jqxgrid').jqxGrid({ selectionmode: 'checkbox'});
		}
</script>
</#if>

<#if !onlyOne>
	   <div id ="myEditor"></div>
	   <div id ="myImage"></div>
		   
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script>
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

	var agreementIdList = [];
	$('#jqxgrid').on('rowSelect', function (event){
			var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
		    var agreementId = rowData.agreementId;
		    agreementIdList.push(agreementId);
	});
	$('#jqxgrid').on('rowUnselect', function (event){
			var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
		    if (rowData) {
		    	var agreementId = rowData.agreementId;
			    var index = _.indexOf(agreementIdList, agreementId);
			    agreementIdList.splice(index, 1);
			}
	});
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	$("#alterpopupWindowEmail").jqxWindow({
	    width: 450, maxWidth: 1000, theme: "olbius", minHeight: 390, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEmail"), modalOpacity: 0.7
	});
	$('#alterpopupWindowEmail').on('close', function () {
		$('#alterpopupWindowEmail').jqxValidator('hide');
	});
	$("#alterSaveEmail").click(function () {
		 if ($('#alterpopupWindowEmail').jqxValidator('validate')) {
			var myJson = {agreementId: agreementIdList,
					authUser: $("#txtUserName").val(),
					authPass: $("#txtUserPassword").val(),
					subject: $("#txtSubject").val(),
					sendTo: $("#txtTo").val(),
					bodyText: $("#txtContent").val()};
			var result;
			jQuery.ajax({
				url: "createEmailAgrrementHoanm",
				type: "POST",
				data: myJson,
				success: function(res) {
					result = res;
				}
			}).done(function() {
				$('#jqxNotificationNested').jqxNotification('closeLast');
				if (result['_ERROR_MESSAGE_']) {
					$("#jqxNotificationNested").jqxNotification({ template: 'error'});
	              	$("#notificationContentNested").text(result['_ERROR_MESSAGE_']);
	              	$("#jqxNotificationNested").jqxNotification("open");
				} else {
					$("#jqxNotificationNested").jqxNotification({ template: 'info'});
	    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.SendSuccess)}");
	              	$("#jqxNotificationNested").jqxNotification("open");
	              	$('#btnCancelSentEmail').click();
				}
			});
			$("#alterpopupWindowEmail").jqxWindow('close');
		 }
    });
	$('#alterpopupWindowEmail').jqxValidator({
 	    rules: [
 					{ input: '#txtUserName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
 					{ input: '#txtUserPassword', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
 					{ input: '#txtTo', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
 					{ input: '#txtSubject', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
 					{ input: '#txtContent', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
 	           ]
 	});
	function sentEmail() {
		$("#alterpopupWindowEmail").jqxWindow('open');
	}
	function hiddenClick() {
		$('#contentMessages').css('display','none');
	}
	function removeA(arr) {
	    var what, a = arguments, L = a.length, ax;
	    while (L > 1 && arr.length) {
	        what = a[--L];
	        while ((ax= arr.indexOf(what)) !== -1) {
	            arr.splice(ax, 1);
	        }
	    }
	    return arr;
	}
</script>
<style>
	.text-right{
		margin-top: 7px;
	}
</style>
		<#else>
   			<h2>Do not have permission</h2>
   </#if>	