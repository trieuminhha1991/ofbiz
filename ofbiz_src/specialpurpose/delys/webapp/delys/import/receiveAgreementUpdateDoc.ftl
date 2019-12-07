<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<style>
	.bootbox{
		z-index: 20001 !important;
	}
	.modal-backdrop{
		z-index: 20000 !important;
	}
	.green1 {
        color: #black;
        background-color: #F0FFFF;
    }
    .yellow1 {
        color: black\9;
        background-color: yellow\9;
    }
    .red1 {
        color: black\9;
        background-color: #e83636\9;
    }
    .green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #F0FFFF;
    }
    .yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: yellow;
    }
    .red1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #e83636;
    }
    #updateofflinejqxgridPackingListDetail{
    	display: none;
    }
</style>
<#assign listAgreementNotBill = parameters.listAgreementNotBill !>
<#assign billId = parameters.billId !>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<div id='jqxMenu' style="display: none;">
	<ul>
	    <li id="viewDetailCont"><i class="icon-edit"></i><a>&nbsp;&nbsp;${uiLabelMap.CommonUpdate} ${uiLabelMap.Detail} container</a></li>
    	<li id="viewVouchers"><i class="fa-file-text-o"></i>&nbsp;&nbsp;<a>${uiLabelMap.UpdateVouchers}</a>
    		<ul style="width: 350px !important;">
	    		<li id="createInvoice"><i class="icon-plus"></i>&nbsp;&nbsp;<a>${uiLabelMap.accCreateInvoice}</a></li>
	    		<li id="viewQuarantine"><i class="icon-download-alt"></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadquarantineDocument}</a></li>
	    		<li id="viewTested"><i class="icon-download-alt"></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadtestedDocument}</a></li>
	    		<li id='agreementToQuarantineChild'><i class='icon-download-alt'></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadAgreementToQuarantine}</a></li>
				<li id='agreementToValidationChild'><i class='icon-download-alt'></i>&nbsp;&nbsp;<a>${uiLabelMap.DownloadAgreementToValidation}</a></li>
    		</ul>
    	</li>
	</ul>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script>
$('#jqxMenu').jqxMenu({ width: '220px', height: '56px', autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
$(document).on('click', function (e) {
	$('#jqxMenu').jqxMenu('close');
});
$.notify.addStyle('happyblue', {
	  html: "<div>(^.^)<span data-notify-text/>(-.-)</div>",
	  classes: {
	    base: {
	      "white-space": "nowrap",
	      "background-color": "lightblue",
	      "padding": "5px",
	    },
	    superblue: {
	      "color": "white",
	      "background-color": "blue"
	    }
	  }
	});
</script>
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
	var columns = [
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
	               	{ text: '${uiLabelMap.AgreementName}', datafield: 'agreementName', editable: false,
	               		cellclassname: function (row, column, value, data) {
	   					    return 'green1';
	               		}
	               	},
                 	{ text: '${uiLabelMap.containerId}', datafield: 'containerId', width: '150px', editable: false, hidden: true},
					{ text: '${uiLabelMap.containerNumber}', datafield: 'containerNumber', width: '150px', editable: true, hidden: false
					},
					{ text: '${uiLabelMap.orderNumberSupp}', datafield: 'externalOrderNumber', width: '250px', editable: false,
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit\"><b>' + value+ '</b></div>';
						},
						cellclassname: function (row, column, value, data) {
	   					    return 'green1';
						}
					},
					{ text: '${uiLabelMap.sealNumber}', datafield: 'sealNumber', editable: true, width: '120px'},
					{ text: '${uiLabelMap.packingUnits}', datafield: 'packingUnitTotal', width: '90px', editable: false, columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + value.toLocaleString('${localeStr}') + ' (KAR)</div>';
						},
						cellclassname: function (row, column, value, data) {
	   					    return 'green1';
	   					 }
					},
					{ text: '${uiLabelMap.totalNetWeight}', datafield: 'netWeightTotal', width: '150px', editable: false,columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + value.toLocaleString('${localeStr}') + ' (KG)</div>';
						},
						cellclassname: function (row, column, value, data) {
	   					    return 'green1';
	   					 }
					},
					{ text: '${uiLabelMap.totalGrossWeight}', datafield: 'grossWeightTotal', width: '150px', editable: false, columntype: 'numberinput', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<div style=\"margin: inherit; text-align: right; float: right;\">' + value.toLocaleString('${localeStr}') + ' (KG)</div>';
						},
						cellclassname: function (row, column, value, data) {
	   					   	return 'green1';
	   					 }
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
					 { name: 'partyIdTo', type: 'string'}
					 ]"/>
<#assign columnlist="		 
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.billId}', datafield: 'billId', width: '200px', editable: false, filterable: false },
					{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', editable: true, filterable: true},
					{ text: 'ShippingLine', datafield: 'partyIdFrom', editable: false, hidden: true, filterable: true},
					{ text: '${uiLabelMap.departureDate}', datafield: 'departureDate', editable: true, columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.arrivalDate}', datafield: 'arrivalDate', editable: true,  columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy'}
"/>

<div>		
		<@jqGrid filtersimplemode="true" alternativeAddPopup="alterpopupWindow" addType="popup" initrowdetails = "true" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow"
			editable="true" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true"
		 	url="jqxGeneralServicer?sname=JQGetListBillAndContainer&billId=${billId}" updateUrl="jqxGeneralServicer?sname=updateBillOfLadingSimple&jqaction=U"
			createUrl="jqxGeneralServicer?sname=updateBillOfLadingSimple&jqaction=C" rowdetailsheight="255"
		 	addColumns="billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
		 	editColumns="billId;billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
	 		contextMenuId="contextMenu" mouseRightMenu="true"
		/>
</div>
<div id="myMenuPlace"></div>

<#-- begin alterPopupWindow -->
<div id="alterpopupWindow" style="display:none; overflow: hidden;">
    <div>${uiLabelMap.AddNewBillOfLading}</div>
    <div style="overflow: hidden;">
    	<div class="row-fluid">
			<div class="span12 margin-top8">
	 			<div class="span4" style="text-align: right; margin: inherit;">${uiLabelMap.BillNumber}<span style="color:red;"> *</span>:</div>
	 			<div class="span8"><input type='text' id="txtBillNumber" /></div>
 			</div>
 		</div>
		<div class="row-fluid">
 			<div class="span12 margin-top10">
	 			<div class="span4" style="text-align: right; margin: inherit;">${uiLabelMap.FromShippingLine}<span style="color:red;"> *</span>:</div>
	 			<div class="span8"><div type='text' id="txtpartyIdFrom"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12 margin-top10">
	 			<div class="span4" style="text-align: right; margin: inherit;">${uiLabelMap.departureDate}<span style="color:red;"> *</span>:</div>
    	 		<div class="span8"><div type='text' id="txtdepartureDate"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12 margin-top10">
	 			<div class="span4" style="text-align: right; margin: inherit;">${uiLabelMap.arrivalDate}<span style="color:red;"> *</span>:</div>
	 			<div class="span8"><div type='text' id="txtarrivalDate"></div></div>
 			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
	 		<div class="span12 margin-top10">
	 			<button id='alterCancel' class="btn btn-mini btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
	 			<button id='alterSave' class="btn btn-mini btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
            </div>
         </div>
    </div>
</div>

<#-- END alterPopupWindow-->

<#-- BEGIN POPUP FOR DOCUMENT QA -->
<div id="popupDocQA" style="display:none; overflow: hidden;">
	<div id="headerDoc">${uiLabelMap.AddNewBillOfLading}</div>
	<div style="overflow: hidden;">
			<input type="hidden" id="documentCustomsId"/>
			<input type="hidden" id="containerCustomsId"/>
			<input type="hidden" id="documentCustomsTypeId"/>
			<div class="row-fluid">
				<div class="span12 margin-top8">
		 			<div class="span4" style="text-align: right; margin-top: 8px;">${uiLabelMap.documentCustomsTypeId}<span style="color:red;"> *</span>:</div>
		 			<div class="span8"><div id="customsTypeId" style="margin-top: 8px; color: green"></div></div>
	 			</div>
 			</div>
			<div class="row-fluid">
	 			<div class="span12 margin-top10">
		 			<div class="span4" style="text-align: right; margin-top: 7px;">${uiLabelMap.registerNumber}<span style="color:red;"> *</span>:</div>
		 			<div class="span8"><input id="registerNumber" style="width: 218px; height:26px; font-size: 13px;"/></div>
	 			</div>
			</div>
			<div class="row-fluid">
	 			<div class="span12 margin-top10">
		 			<div class="span4" style="text-align: right; margin-top: 8px;">${uiLabelMap.registerDate}<span style="color:red;"> *</span>:</div>
	    	 		<div class="span8"><div id="registerDate"></div></div>
	 			</div>
			</div>
			<div class="row-fluid">
	 			<div class="span12 margin-top10">
		 			<div class="span4" style="text-align: right; margin-top: 8px;">${uiLabelMap.sampleSendDate}<span style="color:red;"> *</span>:</div>
		 			<div class="span8"><div id="sampleSentDate"></div></div>
	 			</div>
			</div>
			<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
			<div class="row-fluid">
		 		<div class="span12 margin-top10">
		 			<button id='cancelDoc' class="btn btn-mini btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
		 			<button id='saveDocAndDownload' class="btn btn-mini btn-primary form-action-button pull-right"><i class='icon-download-alt'></i>${uiLabelMap.CommonSaveAndDownload}</button>
		 			<button id='saveDoc' class="btn btn-mini btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		        </div>
	        </div>
	</div>
</div>

<#-- END POPUP FOR DOCUMENT QA -->
<#-- BEGIN popupWindowContainer -->
	<div id="popupWindowContainer" style="display:none; overflow: hidden;">
		<div>${uiLabelMap.accCreateNew}</div>
		<div style="overflow: hidden;">
			<div>
				<input type="hidden" id="containerId" value=""></input>
				<input type="hidden" id="billId"></input>
				<input type="hidden" id="packingListId"></input>
				<input type="hidden" id="gridDetailId"/>
				<input type="hidden" id="indexGridDetail"/>
				<h1 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.updateContainer} &frasl; ${uiLabelMap.updatePackingList}
				</h1>
				<div class="row-fluid">
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.containerNumber}<span style="color:red;"> *</span>:</div>
						<div class="span5" id="container"><input type='text' id="containerNumber" /></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.sealNumber}:</div>
						<div class="span5"><input type='text' id="sealNumber" /></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.orderPurchaseId}<span style="color:red;"> *</span>:</div>
						<div class="span5"><div id="orderPurchaseId"></div></div>
						<#-- <div class="span2" style="float: right; margin: inherit; margin-right: -10px; cursor: pointer;"><a id="loadOrderItem" class="icon-save">tai ve</a></div> -->
					</div>
				</div>
				<h2 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				</h2>
				<div class="row-fluid">
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" id="divPLNumber" style="text-align: right; margin: inherit;">${uiLabelMap.packingListNumber}<span style="color:red;"> *</span>:</div>
						<div class="span6"><div id="packingListNumber"></div></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.packingListDate}<span style="color:red;"> *</span>:</div>
						<div class="span6"><div id="packingListDate"></div></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.orderNumberSupp}<span style="color:red;"> *</span>:</div>
						<div class="span6"><input type='text' id="orderNumberSupp" /></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.orderTypeSupp}<span style="color:red;"> *</span>:</div>
						<div class="span6"><div id="orderTypeSupp"></div></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.invoiceNumber}<span style="color:red;"> *</span>:</div>
						<div class="span6"><input type='text' id="invoiceNumber" /></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.invoiceDate}<span style="color:red;"> *</span>:</div>
						<div class="span6"><div id="invoiceDate"></div></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.totalNetWeight}<span style="color:red;"> *</span>:</div>
						<div class="span6"><input type='text' id="totalNetWeight" /></div>
					</div>
					<div class="row-fluid span4" style="margin-top:5px;">
						<div class="span5" style="text-align: right; margin: inherit;">${uiLabelMap.totalGrossWeight}<span style="color:red;"> *</span>:</div>
						<div class="span6"><input type='text' id="totalGrossWeight"/></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class=""  style="margin-left: 20px !important;"><#include "listPackingListDetail.ftl"/></div>
				</div>
				<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
				<div class="row-fluid" style="margin-top: 4px;">
	    	 		<div class="">
		    	 		<button id='alterCancelContainer' class="btn btn-mini btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
		    	 		<button id="saveAndContinueContainer" title="${uiLabelMap.saveAndContinueCont}" class='btn btn-mini btn-success form-action-button pull-right' style='height: 30px'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
		    	 		<button id='alterSaveContainer' title="${uiLabelMap.saveAndContinuePl}" class="btn btn-mini btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			        </div>
		        </div>
	        </div>
		</div>
	</div>
<#-- END popupWindowContainer -->
			    
<div id="containerPopupAdder" style="width: 100%"></div>
<div id="jqxNotificationPopupAdder">
    <div id="notificationContentAdder">
    </div>
</div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id="QADocumentation"><i class="fa-file-text-o"></i>&nbsp;&nbsp;${uiLabelMap.QADocumentation}
			<ul style='width:350px;'>
				<li id='CreateInvoiceTotal'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateInvoiceTotal}</li>
				<li id='agreementToQuarantine'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreementToQuarantine}</li>
				<li id='agreementToValidation'><i class='icon-download-alt'></i>&nbsp;&nbsp;${uiLabelMap.DownloadAgreementToValidation}</li>
				<li id='CreateListAttachments'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateListAttachments}</li>
			</ul>
		</li>
		<li id='Advances'><i class="icon-exchange"></i>&nbsp;&nbsp;${uiLabelMap.Advances}</li>
		<li id='SentTwoNotifice'><i class="icon-bullhorn"></i>&nbsp;&nbsp;${uiLabelMap.SentNotifyForImportSpecialist}</li>
	</ul>
</div>
		        
<#assign status = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />

<script type="text/javascript" src="/delys/images/js/import/packingList.js"></script>
<script type="text/javascript">
<#-- BEGIN EDIT BY DAT -->
//BEGIN function return index and boundIndex
var indexParentGrid;
var indexChildGrid;
function returnIndexForMenuClick(index, boundIndex){
	indexParentGrid = index;
	indexChildGrid = boundIndex;
}
//END function return index and boundIndex
$('#viewTested').on('click', function(){
	 $('#popupDocQA').jqxWindow('open');
	 $('#headerDoc').text("${StringUtil.wrapString(uiLabelMap.testedDocument)}");
	 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	 var documentCustomsId="";
	 var registerNumber = "";
	 var registerDate = "";
	 var sampleSendDate = "";
	 jQuery.ajax({
	        url: "getDocumentCustomsByContainer",
	        type: "POST",
	        async: false,
	        data: {documentCustomsTypeId: "TESTED", containerId: dataRow.containerId},
	        dataType: 'json',
	        success: function(res){
	        	documentCustomsId = res.resultListDoc.documentCustomsId;
	        	registerNumber = res.resultListDoc.registerNumber;
	        	registerDate = res.resultListDoc.registerDate;
	        	sampleSendDate = res.resultListDoc.sampleSendDate;
	        }
	 });
	 $('#documentCustomsId').val(documentCustomsId);
	 $('#containerCustomsId').val(dataRow.containerId);
	 $('#documentCustomsTypeId').val("TESTED");
	 $('#registerNumber').val(registerNumber);
	 $('#registerDate').jqxDateTimeInput('val', registerDate);
	 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
	 $('#customsTypeId').text('${StringUtil.wrapString(uiLabelMap.testedDocument)}');
});
$("#createInvoice").on("click", function() {
	 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	 var containerId = dataRow.containerId;
	 window.location.href = "CreateInvoice?containerId=" + containerId;
});
$("#agreementToQuarantineChild").on("click", function() {
	var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	var containerId = dataRow.containerId;
	window.location.href = "exportAgreementToQuarantine?containerId=" + containerId;
});
$("#agreementToValidationChild").on("click", function() {
	var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	var containerId = dataRow.containerId;
	window.location.href = "exportAgreementToValidation?containerId=" + containerId;
});
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
$('#viewQuarantine').on('click', function(){
	 $('#popupDocQA').jqxWindow('open');
	 $('#headerDoc').text("${StringUtil.wrapString(uiLabelMap.quarantineDocument)}");
	 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
	 var documentCustomsId="";
	 var registerNumber = "";
	 var registerDate = "";
	 var sampleSendDate = "";
	 jQuery.ajax({
	        url: "getDocumentCustomsByContainer",
	        type: "POST",
	        async: false,
	        data: {documentCustomsTypeId: "QUARANTINE", containerId: dataRow.containerId},
	        dataType: 'json',
	        success: function(res){
	        	documentCustomsId = res.resultListDoc.documentCustomsId;
	        	registerNumber = res.resultListDoc.registerNumber;
	        	registerDate = res.resultListDoc.registerDate;
	        	sampleSendDate = res.resultListDoc.sampleSendDate;
	        }
     });
	 $('#documentCustomsId').val(documentCustomsId);
	 $('#containerCustomsId').val(dataRow.containerId);
	 $('#documentCustomsTypeId').val("QUARANTINE");
	 $('#registerNumber').val(registerNumber);
	 $('#registerDate').jqxDateTimeInput('val', registerDate);
	 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
	 $('#customsTypeId').text('${StringUtil.wrapString(uiLabelMap.quarantineDocument)}');
});
$('#popupDocQA').jqxValidator({
    rules: [
            { input: '#registerNumber', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
            { input: '#registerDate', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
            	rule: function (input, commit) {
            		var value = $('#registerDate').jqxDateTimeInput('getDate');
            		if (value > 0) {
            			return true;
					}
            		return false;
            	}
            },
            { input: '#sampleSentDate', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
            	rule: function (input, commit) {
            		var value = $('#sampleSentDate').jqxDateTimeInput('getDate');
            		if (value > 0) {
            			return true;
            		}
            		return false;
            	}
            }
           ]
});
$('#saveDocAndDownload').on('click', function(){
	if ($('#popupDocQA').jqxValidator('validate')) {
		var documentCustomsId = $('#documentCustomsId').val();
		var containerId = $('#containerCustomsId').val();
		var documentCustomsTypeId = $('#documentCustomsTypeId').val();
		var registerNumber = $('#registerNumber').val();
		var registerDate = $('#registerDate').jqxDateTimeInput('getDate').getTime();
		var sampleSendDate = $('#sampleSentDate').jqxDateTimeInput('getDate').getTime();
		jQuery.ajax({
	        url: 'updateDocumentCustomsAjax',
	        type: 'POST',
	        async: false,
	        data: {containerId: containerId, documentCustomsId: documentCustomsId, documentCustomsTypeId: documentCustomsTypeId, registerNumber: registerNumber, registerDate: registerDate, sampleSendDate: sampleSendDate},
	        dataType: 'json',
	        success: function(res){
	        	$('#popupDocQA').jqxWindow('close');
	        }
		}).done(function() {
			switch (documentCustomsTypeId) {
			case "TESTED":
				window.location.href = "exportDocumentTested?containerId=" + containerId;
				break;
			case "QUARANTINE":
				window.location.href = "exportDocumentQuarantine?containerId=" + containerId;
				break;
			default:
				break;
			}
		});
	}
});
//Init Source for partyShipping line
			var source_partyShippingLine = new Array(
			<#list listPartyShipping as item>
				<#if item_index + 1 == listPartyShipping.size()>
					{name:'${item.description}', value:'${item.partyId}'}
				<#else>
					{name:'${item.description}', value:'${item.partyId}'},
				</#if>
			</#list>
			);
					
			$("#txtpartyIdFrom").jqxComboBox({
					source: source_partyShippingLine,
					placeHolder: '',
					displayMember: 'name',
					valueMember: 'value',
					theme: 'olbius',
					width: '220px',
					height: '30px',
					searchMode: 'containsignorecase',
		   	 		autoOpen: true,
		   	 		autoComplete: true
			});
// End Source for partyShipping line
			
//Init Function renderToolbar
		var source_listAgreementNotBill = new Array(
			<#list listAgreementNotBill as item>
				<#if item_index + 1 == listAgreementNotBill.size()>
					{attrValue:'${item.attrValue}', agreementId:'${item.agreementId}'}
				<#else>
					{attrValue:'${item.attrValue}', agreementId:'${item.agreementId}'},
				</#if>
			</#list>
		);
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
//	       	myToolBar += '<div class="span4" style="float: right;"><a id="'+removeId+'" style="margin-top: 10px; margin-right: 2px; cursor: pointer; float: right;" title="${uiLabelMap.removeAgree}" class="icon-trash open-sans">${uiLabelMap.CommonDelete} container</a></div>';
	       	myToolBar += '</div></div>';
	       	container.append(myToolBar);
	       	toolbar.append(container);
	       	
	       	$('#'+addAgreeId).on('click', function(){
	       		$('#containerId').val('');
	       		$('#packingListId').val('');
	       		$('#indexGridDetail').val('');
//	       		$('#containerNumber').removeAttr('disabled');
//	       		$('#sealNumber').removeAttr('disabled');
	       		$('#orderPurchaseId').jqxComboBox({ disabled: false });
	       		$('#packingListNumber').jqxComboBox('clear');
	       		$('#packingListNumber').jqxComboBox({searchMode: 'none'});
	       		AddAgreementToRow(index, gridId);
	       		$('#saveAndContinueContainer').css('display', 'block');
	       		$('#customcontrol1jqxgridPackingListDetail').css('display', 'block');
	       	});
	       	$('#'+removeId).on('click', function(){
	       		deleteAgreementOfBill(gridId);
	       	});
	       	
       	}
//End Function rederToolbar
//BEGIN INIT SOURCE FOR selectAgreementindex
// ENDINIT SOURCE FOR selectAgreementindex
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

//BEGIN FUNCTION deleteAgreementOfBill
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
//					console.log(res.listAgreementNotBill);
		        	$(".selectAgreement").jqxComboBox({source: source_listAgreementNotBill});
		        	for(var i = 0; i < rowindexes.length; i++){
						var data = $('#'+gridId).jqxGrid('getrowdata', i);
					}
		        	$("#"+gridId).jqxGrid('deleterow', rowIDs);
		        	$('#'+gridId).jqxGrid('clearselection');
		        }
		    });
		}

	var product = [
				<#if listProducts?exists>
					<#list listProducts as product>
					{
						productId: "${product.productId?if_exists}",
						description: "${StringUtil.wrapString(product.internalName?if_exists)}"
					},
					</#list>
				</#if>
	               ];
	var mapProducts = {
					<#if listProducts?exists>
						<#list listProducts as product>
							"${product.productId?if_exists}": "${StringUtil.wrapString(product.internalName?if_exists)}",
						</#list>
					</#if>
					};
			
// Show Menu for one Bill
//			da xoa roi
// End Show menu for one bill
			

// show window add one bill
			$("#alterpopupWindow").jqxWindow({
	            width: 450, minHeight: '265px', maxHeight: '300px', resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: 'olbius'
	        });
			var wtmp = window;
			var tmpwidth = $('#alterpopupWindow').jqxWindow('width');
			$('#alterpopupWindow').jqxWindow({
			    position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
			});
			$("#alterpopupWindow").on('close', function (event) {
				$('#alterpopupWindow').jqxValidator('hide');
			});
			$("#alterpopupWindow").on('open', function (event) {
				$("#txtBillNumber").val("");
				$("#txtpartyIdFrom").val("");
				$("#txtarrivalDate").jqxDateTimeInput('val', null);
			});
			//POPUP DOC QA
			$("#popupDocQA").jqxWindow({
	            width: 450, minHeight: '265px', maxHeight: '300px', resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelDoc"), modalOpacity: 0.7, theme: 'olbius'
	        });
// end show window add one bill

// show window of container
			$("#popupWindowContainer").jqxWindow({
	            width: 1280, zIndex:1003, maxWidth:1280, height: 580, maxHeight: 620, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelContainer"), modalOpacity: 0.7, theme: 'olbius'
	        });
			initGridjqxgridPackingListDetail();
			var wtmp = window;
			var tmpwidth = $('#popupWindowContainer').jqxWindow('width');
			$('#popupWindowContainer').jqxWindow({
				position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 15 }
			});
			$("#popupWindowContainer").on('close', function (event) {
				$('#popupWindowContainer').jqxValidator('hide');
			});
// end show window of container
	    	var checkFromDate = $('#txtdepartureDate').val();
	        var dateFRM = checkFromDate.split('/');
	        checkFromDate = new Date(dateFRM[2], dateFRM[1] - 1, dateFRM[0], 0, 0, 0, 0);
	    	$('#txtdepartureDate').on('close', function (event)
	        		{
	        		    var jsDate = event.args.date;
	        		    checkFromDate = jsDate;
	        		});
	        $('#txtarrivalDate').on('close', function (event)
	        		{
	        		    var jsDate = event.args.date;
	        		    if (checkFromDate <= jsDate) {
						} else {
							$("#txtarrivalDate").jqxDateTimeInput('val', null);
						}
	        		});
//Start function Create new Bill
		        $("#alterSave").click(function () {
		        	if ($('#alterpopupWindow').jqxValidator('validate')) {
		        		var billNumber = $('#txtBillNumber').val();
			        	var item = $("#txtpartyIdFrom").jqxComboBox('getSelectedItem');
						var row;
						var fromPartyIdShippingLine = item.value;
			            row = {
			            		billNumber: billNumber,
			            		partyIdFrom: fromPartyIdShippingLine,
			            		departureDate: $('#txtdepartureDate').jqxDateTimeInput('getDate'),
			            		arrivalDate: $('#txtarrivalDate').jqxDateTimeInput('getDate'),
			            };
			            
			    	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			            $("#jqxgrid").jqxGrid('clearSelection');
			            $("#jqxgrid").jqxGrid('selectRow', 0);
			            $("#alterpopupWindow").jqxWindow('close');
//			            $('#jqxgrid').jqxGrid('updatebounddata');
		        	}
		        });
		        $('#alterpopupWindow').jqxValidator({
			        rules: [
			                { input: '#txtBillNumber', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
			                { input: '#txtpartyIdFrom', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
			                	rule: function (input, commit) {
			                		var value = $("#txtpartyIdFrom").val();
			                		if (value) {
			                			return true;
			                		}
			                		return false;
			                	}
			                },
			                { input: '#txtdepartureDate', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
			                	rule: function (input, commit) {
			                		var value = $("#txtdepartureDate").val();
			                		if (value) {
			                			return true;
									}
			                		return false;
			                	}
			                },
			                { input: '#txtarrivalDate', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
			                	rule: function (input, commit) {
			                		var value = $("#txtarrivalDate").val();
			                		if (value) {
			                			return true;
			                		}
			                		return false;
			                	}
			                },
			               ]
			    });
// End function Create new Bill      
<#-- END EDIT BY DAT -->
		var statusList = [
						<#if status?exists>
							<#list status as item>
							{
								statusId: "${item.statusId?if_exists}",
								description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
							},
							</#list>
						</#if>
		                  ];
		var mapStatus = {
						<#if status?exists>
							<#list status as item>
								"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>
					};
		
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
        var orderIdGlobal="";
		function prepareEventDbClick(gridId) {
			$("#" + gridId).on("celldoubleclick", function (event)
					{
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
//			    		console.log(qualityPublication);
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
//			           	 	$("#myImage").html();
            });
            $("#alterSave5").click(function () {
            	createNotification(productId, "QA_QUALITY_MANAGER", header, expireDateProduct);
            	$('#window01').jqxWindow('close');
//			            	$("#myImage").html();
            });
           
        	$('#window01').jqxWindow({ height: 180, width: 700, resizable: false, maxWidth: 1200, isModal: true, modalOpacity: 0.7 });
        	$('#window01').on('close', function (event) {
            	 $('#window01').jqxWindow('destroy');
//			            	 $("#myImage").html();
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
    		initGridjqxgridPackingListDetail();
//        	$("#jqxNotificationNested").jqxNotification({ width: "1358px", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
        	getProductShelfLife();
        	$('#customcontrol2jqxgridPackingListDetail:first-child').css('color', 'red');
        	$('#customcontrol2jqxgridPackingListDetail').css('color', 'red');
//        	$('#customcontrol2jqxgridPackingListDetail')
    	});
    	var getLocalization = function () {
    	    var localizationobj = {};
    	    localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
    	    localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
    	    localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
    	    localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
    	    localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
    	    localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
    	    localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
    	    localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
    	    localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}";
    	    localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
    	    localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
    	    localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
    	    localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}";
    	    return localizationobj;
    	}
</script>
<div id="myImage"></div>
<#include "validateWindowContainer.ftl"/>