<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<link rel="stylesheet" type="text/css" href="/imexresources/css/bl-css.1.0.0.css">
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<script>
	var source_partyShippingLine = [];
	<#list listPartyShipping as item>
		var item = {
			"name":"${StringUtil.wrapString(item.groupName?if_exists)}", 
			"value":"${item.partyId}",
		}
		source_partyShippingLine.push(item);
	</#list>
	
	var source_listAgreementNotBill = new Array(
		<#list listAgreementNotBill as item>
			<#if item_index + 1 == listAgreementNotBill.size()>
				{attrValue:'${item.attrValue}', agreementId:'${item.agreementId}'}
			<#else>
				{attrValue:'${item.attrValue}', agreementId:'${item.agreementId}'},
			</#if>
		</#list>
	);
	
	<#assign status = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />

	var statusList = [<#if status?exists><#list status as item>{
		statusId: "${item.statusId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];
	var mapStatus = {<#if status?exists><#list status as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
</script>

<#assign listAgreementNotBill = parameters.listAgreementNotBill !>
<#assign billId = parameters.billId !>
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var billNumberGlobal = datarecord.billNumber;
	var billIdGlobal = datarecord.billId;
	var datafields = [
	       		        { name: 'containerId', type: 'string' },
						{ name: 'containerNumber', type: 'string' },
						{ name: 'sealNumber', type: 'string'},
						{ name: 'externalOrderNumber', type: 'string'},
						{ name: 'agreementName', type: 'string'},
						{ name: 'netWeightTotal', type: 'number'},
						{ name: 'grossWeightTotal', type: 'number'},
						{ name: 'packingUnitTotal', type: 'number'},
						{ name: 'agreementId', type: 'string'}
	        		];
	var columns = [{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
	               	{ text: '${uiLabelMap.AgreementName}', datafield: 'agreementName', editable: false,
	               	},
                 	{ text: '${uiLabelMap.containerId}', datafield: 'containerId', width: '150px', editable: false, hidden: true},
					{ text: '${uiLabelMap.containerNumber}', datafield: 'containerNumber', width: '150px', editable: true, hidden: false },
					{ text: '${uiLabelMap.orderNumberSupp}', datafield: 'externalOrderNumber', width: '250px', editable: false,
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit\"><b>' + value+ '</b></div>';
						},
					},
					{ text: '${uiLabelMap.sealNumber}', datafield: 'sealNumber', editable: true, width: '120px'},
					{ text: '${uiLabelMap.packingUnits}', datafield: 'packingUnitTotal', width: 100, editable: false, columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + formatnumber(value) + ' (KAR)</div>';
						},
					},
					{ text: '${uiLabelMap.totalNetWeight}', datafield: 'netWeightTotal', width: '150px', editable: false,columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + formatnumber(value) + ' (Kg)</div>';
						},
					},
					{ text: '${uiLabelMap.totalGrossWeight}', datafield: 'grossWeightTotal', width: '150px', editable: false, columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + formatnumber(value) + ' (Kg)</div>';
						},
					}
				];
	var boundIndex = -1;
	
//START DATARECORD NULL
	if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
		var recordDataSource = {
   		 	datafields: datafields,
        	localdata: new Array(),
        	updaterow: function (rowid, newdata, commit) {
        		commit(true);
        		var containerId = newdata.containerId;
        		var containerNumber = newdata.containerNumber;
        		var sealNumber = newdata.sealNumber;
        		$.ajax({
                   type: \"POST\",                      
                   url: 'updateContainer',
                   data: {containerId: containerId, containerNumber: containerNumber, sealNumber: sealNumber},
                   async: false,
                   success: function (data, status, xhr) {
                   },
                   error: function () {
                       commit(false);
                   }
               });
        	}
        }
        var nestedGridAdapter = new $.jqx.dataAdapter(recordDataSource);
		var grid = $($(parentElement).children()[0]);
        $(grid).attr('id','jqxgridDetail'+index);
        if (grid != null) {
            grid.jqxGrid({
                source: nestedGridAdapter,
                localization: getLocalization(),
                width: '96%', 
                height: '92%',
		 		editable:true,
		 		editmode:\"click\",
		 		showheader: true,
		 		showtoolbar: true,
		 		pagesize: 5,
		 		pageable: true,
		 		toolbarheight: '30',
		 		selectionmode:\"singlerow\",
		 		theme: 'olbius',
                columns: columns,
                columnsresize: true,
                rendertoolbar: function (toolbar) {
                	renderToolbar(toolbar, index, billNumberGlobal, billIdGlobal);
                }
            });
       	 	grid.on('rowclick', function (event) {
       		 	var args = event.args;
	    		    boundIndex = args.rowindex;
	    		    var rightclick = args.rightclick; 
	             if (rightclick) {
	            	 var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    $('#jqxMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    grid.jqxGrid('clearselection');
                    grid.jqxGrid('selectrow', boundIndex);
                    //call back function return index and boundIndex
                    returnIndexForMenuClick(index, boundIndex);
	                return false;
	             }else {
	            	 $('#jqxMenu').jqxMenu('close');
				}
       	 	});
        }
        return false;
	}
//END DATARECORD NULL
	var dataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
    var recordData = dataAdapter.records;
		var nestedGrids = new Array();
        var id = datarecord.uid.toString();
        var grid = $($(parentElement).children()[0]);
        $(grid).attr('id','jqxgridDetail'+index);
        nestedGrids[index] = grid;
        var recordDataById = [];
        for (var ii = 0; ii < recordData.length; ii++) {
        	recordDataById.push(recordData[ii]);
        }
         var recordDataSource = {
        		datafields: datafields,
             	localdata: recordDataById,
             	updaterow: function (rowid, newdata, commit) {
            		commit(true);
            		var containerId = newdata.containerId;
            		var containerNumber = newdata.containerNumber;
            		var sealNumber = newdata.sealNumber;
            		$.ajax({
                       type: \"POST\",                        
                       url: 'updateContainer',
                       data: {containerId: containerId, containerNumber: containerNumber, sealNumber: sealNumber},
                       async: false,
                       success: function (data, status, xhr) {
                       },
                       error: function () {
                           commit(false);
                       }
                   });
            	},
             	deleterow: function (rowid, commit) {
                    commit(true);
                }
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(recordDataSource);
         if (grid != null) {
             grid.jqxGrid({
                 source: nestedGridAdapter, 
                 localization: getLocalization(),
                 width: '96%',
                 height: '92%',
		 		 editable:true,
		 		 pagesize: 5,
		 		 pageable: true,
		 		 editmode:\"click\",
		 		 selectionmode:\"singlerow\",
		 		 theme: 'olbius',
		 		 toolbarheight: '30',
		 		 showheader: true,
                 columns: columns,
                 showtoolbar: true,
                 columnsresize: true,
                 rendertoolbar: function (toolbar) {
                	 renderToolbar(toolbar, index, billNumberGlobal, billIdGlobal);
                 }
             });
        	 grid.on('rowclick', function (event) {
        		 	var args = event.args;
	    		    boundIndex = args.rowindex;
	    		    var rightclick = args.rightclick; 
	             if (rightclick) {
	            	 var scrollTop = $(window).scrollTop();
                     var scrollLeft = $(window).scrollLeft();
                     $('#jqxMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                     grid.jqxGrid('clearselection');
                     grid.jqxGrid('selectrow', boundIndex);
                     //call back function return index and boundIndex
                     returnIndexForMenuClick(index, boundIndex);
	                 return false;
	             }
        	 });
         }
 }"/>

<#assign dataField="[{ name: 'billId', type: 'string' },
					 { name: 'billNumber', type: 'string'},
					 { name: 'departureDate', type: 'date', other: 'Timestamp'},
					 { name: 'arrivalDate', type: 'date', other: 'Timestamp'},
					 { name: 'rowDetail', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'}]"/>

<#assign columnlist="		 
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.billId}', datafield: 'billId', width: '200px', editable: false, filterable: false },
					{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', editable: true, filterable: true,
						validation: function (cell, value) {
				            if (!value) {
				            	return { result: false, message: '${uiLabelMap.BPOPleaseSelectAllInfo}' };
				            }
				            return true;
				    	}
					},
					{ text: 'ShippingLine', datafield: 'partyIdFrom', editable: false, hidden: true, filterable: true},
					{ text: '${uiLabelMap.departureDate}', datafield: 'departureDate', editable: true, columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy',
						validation: function (cell, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
							var today = new Date();
				            if (!value) {
				            	return { result: false, message: '${uiLabelMap.BPOPleaseSelectAllInfo}' };
				            }
				            if (value > data.arrivalDate) {
				            	return { result: false, message: '${uiLabelMap.validStartDateMustLessThanOrEqualFinishDate}' };
				            }
				            if (value < today) {
				            	return { result: false, message: '${uiLabelMap.validRequiredValueGreatherOrEqualDateTimeToDay}' };
				            }
				            return true;
				    	}
					},
					{ text: '${uiLabelMap.arrivalDate}', datafield: 'arrivalDate', editable: true,  columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy',
						validation: function (cell, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
							var today = new Date();
				            if (!value) {
				            	return { result: false, message: '${uiLabelMap.BPOPleaseSelectAllInfo}' };
				            }
				            if (value < data.departureDate) {
				            	return { result: false, message: '${uiLabelMap.validStartDateMustLessThanOrEqualFinishDate}' };
				            }
				            if (value < today) {
				            	return { result: false, message: '${uiLabelMap.validRequiredValueGreatherOrEqualDateTimeToDay}' };
				            }
				            return true;
				    	}
					}"/>

<div>		
<@jqGrid filtersimplemode="true" alternativeAddPopup="alterpopupWindow" addType="popup" initrowdetails = "true" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow"
		editable="true" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true"
	 	url="jqxGeneralServicer?sname=JQGetListBillAndContainer&billId=${billId}" updateUrl="jqxGeneralServicer?sname=updateBillOfLading&jqaction=U"
		createUrl="jqxGeneralServicer?sname=updateBillOfLading&jqaction=C" rowdetailsheight="255"
	 	addColumns="billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
	 	editColumns="billId;billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
 		contextMenuId="contextMenu" mouseRightMenu="true"/>
</div>

<#include "popup/addBillOfLading.ftl"/>

<#include "popup/QADocumentation.ftl"/>

<#include "popup/containerManager.ftl"/>

<div id="containerPopupAdder" style="width: 100%"></div>
<div id="jqxNotificationPopupAdder">
    <div id="notificationContentAdder">
    </div>
</div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='EditBill'><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.Edit}</li>
		<li id='CreateInvoiceTotal'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateInvoiceTotal}</li>
		<li id='agreementToQuarantine'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreementToQuarantine}</li>
		<li id='agreementToValidation'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreementToValidation}</li>
		<li id='CreateListAttachments'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateListAttachments}</li>
	</ul>
</div>

<script type="text/javascript">
	var indexParentGrid;
	var indexChildGrid;
	function returnIndexForMenuClick(index, boundIndex){
		indexParentGrid = index;
		indexChildGrid = boundIndex;
	}
	
	$("#agreementToQuarantine").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		var billId = rowData.billId;
		window.location.href = "exportAgreementToQuarantine?billId=" + billId;
	});
	
	$("#agreementToValidation").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		var billId = rowData.billId;
		window.location.href = "exportAgreementToValidation?billId=" + billId;
	});
	$("#CreateInvoiceTotal").on("click", function() {
			var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
			var billId = rowData.billId;
			window.location.href = "CreateInvoiceTotal?billId=" + billId;
	});
	 $("#CreateListAttachments").on("click", function() {
		 var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		 var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		 var billId = rowData.billId;
		 window.location.href = "CreateListAttachments?billId=" + billId;
	 });

		
	function renderToolbar(toolbar, index, billNumberGlobal, billIdGlobal){
	 	var gridId = 'jqxgridDetail'+index;
	 	var comboBoxId = 'selectAgreement'+index;
	 	var comboBoxClass = 'selectAgreement';
	 	var addAgreeId = 'addAgree'+index;
	 	var removeId = "removeAgree"+index;
	 	var container = $("<div style='overflow: hidden;'></div>");
       	var myToolBar = '<div class="row-fluid">';
	 	myToolBar +='<div class="span9"></div>';
       	myToolBar += '<div class="span3">';
       	myToolBar += '<div class="span12"><a id="'+addAgreeId+'" style="float: right; margin-top: 10px; margin-left: 0px; margin-right: 10px;cursor: pointer;" class="icon-plus-sign open-sans" title="${uiLabelMap.addNewAgree}">${uiLabelMap.CommonAdd} container</a></div>';
       	myToolBar += '</div></div>';
       	container.append(myToolBar);
       	toolbar.append(container);
       	
       	$('#'+addAgreeId).on('click', function(){
       		$('#containerId').val('');
       		$('#packingListId').val('');
       		$('#indexGridDetail').val('');
       		$('#orderPurchaseId').jqxComboBox({ disabled: false });
       		AddAgreementToRow(index, gridId);
       		$('#customcontrol1jqxgridPackingListDetail').css('display', 'block');
       	});
       	$('#'+removeId).on('click', function(){
       		deleteAgreementOfBill(gridId);
       	});
       	
   	}
	function AddAgreementToRow(index, gridId){
		$("#popupWindowContainer").jqxWindow('open');
		jQuery.ajax({
	        url: "getExternalOrderType",
	        type: "POST",
	        async: false,
	        data: {},
	        dataType: 'json',
	        success: function(res){
	        	var row = $('#jqxgrid').jqxGrid('getrowdata', index);
	        	$('#billId').val(row.billId);
	        	$('#gridDetailId').val(gridId);
	        	$("#orderPurchaseId").jqxComboBox({
	        		source: res.listAgreementNotBill
	        	});
	        	$("#orderTypeSupp").jqxComboBox({
	    			source: res.listOrderType
	        	});
	        }
	    });
	}

	function deleteAgreementOfBill(gridId){
		var rowindexes = $('#'+gridId).jqxGrid('getselectedrowindexes');
		var dataArr = [];
		var rowIDs = new Array();
		for(var i = 0; i < rowindexes.length; i++){
			var data = $('#'+gridId).jqxGrid('getrowdata', rowindexes[i]);
			dataArr.push(data);
			rowIDs.push(data.uid);
		}
		jQuery.ajax({
	        url: "deleteAgreementFromBillAjax",
	        type: "POST",
	        async: false,
	        data: {data: JSON.stringify(dataArr)},
	        dataType: 'json',
	        success: function(res){
	        	var source_listAgreementNotBill = res.listAgreementNotBill;
	        	$(".selectAgreement").jqxComboBox({source: source_listAgreementNotBill});
	        	for(var i = 0; i < rowindexes.length; i++){
					var data = $('#'+gridId).jqxGrid('getrowdata', i);
				}
	        	$("#"+gridId).jqxGrid('deleterow', rowIDs);
	        	$('#'+gridId).jqxGrid('clearselection');
	        }
	    });
	}

    var orderIdGlobal="";
	function prepareEventDbClick(gridId) {
		$("#" + gridId).on("celldoubleclick", function (event) {
		    var args = event.args;
		    var dataField = args.datafield;
		    if (dataField == 'orderId') {
		    	 var valueDbcl = args.value;
		    	 orderIdGlobal=valueDbcl;
		    	 $("#jqxwindowOrderViewer").jqxWindow('open');
		    }
		});
	}
    var productIdComboBoxCell = "";

    var lastTimeChoice = 0;
    function executeQualityPublication(data, value, productIdComboBoxCell) {
		var productId = productIdComboBoxCell;
		var datetimeManufactured = 0;
		lastTimeChoice==0?datetimeManufactured=data.datetimeManufactured:datetimeManufactured=lastTimeChoice;
		var expireDate = value;
		var validateDate = expireDate.getTime() - datetimeManufactured.getTime();
		validateDate = Math.ceil(validateDate/86400000);
		var qualityPublication = [];
		qualityPublication = hasQualityPublication(productId);
		if (qualityPublication == "null") {
			var header = "${StringUtil.wrapString(uiLabelMap.CreateProductQuality)} " + mapProducts[productId] + " [" + productId + "]";
			var message = "<h4>${uiLabelMap.QualityPublicationNotFound} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQAInsertQualityPublication}</h4>";
			confirmInsertQualityPublication(productId, message, header, "");
		}else {
			var thruDate = qualityPublication.thruDate;
			var timeNow = new Date();
			thruDate = thruDate.time;
			timeNow = timeNow.getTime();
			var leftTime = thruDate - timeNow;
			leftTime = Math.ceil(leftTime/86400000);
			if (0 < leftTime && leftTime < 10) {
					var header = "${StringUtil.wrapString(uiLabelMap.ImportQuantityPublication)} " + mapProducts[productId] + " [" + productId + "] ${StringUtil.wrapString(uiLabelMap.ImportExpiring)}";
					var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationPreExpire}</h4>";
					confirmInsertQualityPublication(productId, message, header, "");
			}
			if(leftTime < 0){
				var header = "${StringUtil.wrapString(uiLabelMap.ImportQuantityPublication)} " + mapProducts[productId] + " [" + productId + "] ${StringUtil.wrapString(uiLabelMap.ImportExpire)}";
				var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationExpire}</h4>";
				confirmInsertQualityPublication(productId, message, header, "");
			}
			var expireDateProduct = qualityPublication.expireDate;
			if (validateDate != expireDateProduct) {
				var header = "${StringUtil.wrapString(uiLabelMap.ImportQuantityPublication)} " + mapProducts[productId] + " [" + productId + "] ${StringUtil.wrapString(uiLabelMap.HaveChanged)}";
				var message = "${uiLabelMap.QualityPublicationNotFound} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.hasChangeProductShelfLife}";
				confirmInsertQualityPublication(productId, message, header, validateDate);
			}
		}
		lastTimeChoice = 0;
		return true;
	}
    function confirmInsertQualityPublication(productId, message, header, expireDateProduct) {
		var wd = "";
    	wd += "<div id='window01'><div>${uiLabelMap.SentNotify}</div><div>";
    	wd += message;
    	wd += "<hr style='margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;'>";
    	wd += "<div class='row-fluid'>" +
			"<div class='span12' style='margin-top: 10px;'>" +
				"<button id='alterCancel5' class='btn btn-danger form-action-button pull-right'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button><button id='alterSave5' class='btn btn-primary form-action-button pull-right'><i class='icon-bullhorn'></i>${uiLabelMap.SentNotify}</button>" +
			"</div>";
    	wd += "</div></div>";
    	$("#myImage").html(wd);
    	$("#alterCancel5").jqxButton({template: "danger", theme: null });
        $("#alterSave5").jqxButton({template: "primary", theme: null });
        $("#alterCancel5").click(function () {
       	 	$('#window01').jqxWindow('close');
        });
        $("#alterSave5").click(function () {
        	createNotification(productId, "QA_QUALITY_MANAGER", header, expireDateProduct);
        	$('#window01').jqxWindow('close');
        });
       
    	$('#window01').jqxWindow({ height: 180, width: 700, resizable: false, maxWidth: 1200, isModal: true, modalOpacity: 0.7 });
    	$('#window01').on('close', function (event) {
        	 $('#window01').jqxWindow('destroy');
         });
	}
	function createNotification(productId, roleTypeId, messages, expireDateProduct) {
		var targetLink = "productId=" + productId + ";expireDateProduct=" + expireDateProduct;
		if (expireDateProduct == "") {
			targetLink = "productId=" + productId;
		}
		var action = "CreateProductQuality";
		var header = messages;
		var jsonObject = {roleTypeId: roleTypeId,
						header: header,
						openTime: null,
						action: action,
						targetLink: targetLink};
		jQuery.ajax({
	        url: "createNotification",
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	
	        }
	    }).done(function() {
	    	
		});
	}
	function hasQualityPublication(productId) {
		var result = "null";
		if (productId != null) {
			for ( var x in listProductShelfLife) {
				if (productId == listProductShelfLife[x].productId) {
					result = listProductShelfLife[x];
					return result;
				}
			}
		} else {
			result = "productIdnull";
		}
		return result;
	}
	var listProductShelfLife = [];
	function getProductShelfLife() {
		$.ajax({
            url: "getProductShelfLifeAjax",
            type: "POST",
            data: {},
            async: false,
            success: function(res) {
            	listProductShelfLife = res["listProductShelfLife"];
            }
        }).done(function() {
        	
        });
	}
    	
	$(document).ready(function(){
    	getProductShelfLife();
    	$('#customcontrol2jqxgridPackingListDetail:first-child').css('color', 'red');
    	$('#customcontrol2jqxgridPackingListDetail').css('color', 'red');
	});
	
</script>
<script type="text/javascript" src="/imexresources/js/import/packingList.js"></script>
<div id="myImage"></div>
<#include "validateWindowContainer.ftl"/>