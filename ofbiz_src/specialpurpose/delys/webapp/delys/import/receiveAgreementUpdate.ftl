<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#assign listAgreementNotBill = parameters.listAgreementNotBill !>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<script>
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
//	rowsExpaded = index;
	var billNumberGlobal = datarecord.billNumber;
	var billIdGlobal = datarecord.billId;
//START DATARECORD NULL
	if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
		var recordDataSource = {
       		 datafields: [	
       		    { name: 'agreementId', type: 'string' },
					{ name: 'agreementName', type: 'string' },
					{ name: 'orderId', type: 'string'},
					{ name: 'agreementDate', type: 'date', other: 'Timestamp'},
					{ name: 'shippingLineId', type: 'string'},
					{ name: 'partyIdFrom', type: 'string'},
					{ name: 'billNumber', type: 'string'},
					{ name: 'billId', type: 'string'},
					{ name: 'containerId', type: 'string'},
					{ name: 'containerNumber', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'billAgreementId', type: 'string'},
        		],
            	localdata: new Array(),
            	updaterow: function (rowid, newdata, commit) {
            		commit(true);
            		var containerId = newdata.containerId;
            		var containerNumber = newdata.containerNumber;
            		$.ajax({
                       type: \"POST\",                        
                       url: 'updateContainerNumber',
                       data: {containerId: containerId, containerNumber: containerNumber},
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
		setTimeout(function() {
       	 prepareEventDbClick('jqxgridDetail'+index);
		 },0);
		var grid = $($(parentElement).children()[0]);
        $(grid).attr('id','jqxgridDetail'+index);
        if (grid != null) {
            grid.jqxGrid({
                source: nestedGridAdapter,
                width: '96%', 
                height: '92%',
		 		editable:true,
		 		editmode:\"click\",
		 		showheader: true,
		 		showtoolbar: true,
		 		pagesize: 5,
		 		pageable: true,
		 		toolbarheight: '40',
		 		selectionmode:\"checkbox\",
		 		theme: 'olbius',
                columns: [
                          	{ text: '${uiLabelMap.billAgreementId}', datafield: 'billAgreementId', editable: false, hidden: true},
							{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', editable: false, hidden: true},
							{ text: '${uiLabelMap.AgreementName}', datafield: 'agreementName', editable: false},
							{ text: '${uiLabelMap.OrderOrderId}', datafield: 'orderId', width: '150px', editable: false},
							{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', columntype: 'datetimeinput',width: '150px', editable: false, cellsformat: 'dd/MM/yyyy'},
							{ text: '${uiLabelMap.shippingLineId}', hidden: true, datafield: 'shippingLineId', width: '150px', editable: false},
							{ text: '${uiLabelMap.billId}', datafield: 'billId', width: '150px', editable: false, hidden: true},
							{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', width: '150px', editable: true},
							{ text: '${uiLabelMap.containerNumber}', datafield: 'containerNumber', width: '150px', editable: true},
							{ text: '${uiLabelMap.containerId}', datafield: 'containerId', width: '150px', editable: true, hidden: true},
							{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: '150px', columntype: 'dropdownlist', editable: false,
								cellsrenderer: function(row, colum, value){
									return '<span>' + mapStatus[value] + '</span>';
								}
							},
                ],
                rendertoolbar: function (toolbar) {
                	renderToolbar(toolbar, index, billNumberGlobal, billIdGlobal);
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
        		 datafields: [	
        		    { name: 'agreementId', type: 'string' },
					{ name: 'agreementName', type: 'string' },
					{ name: 'orderId', type: 'string'},
					{ name: 'agreementDate', type: 'date', other: 'Timestamp'},
					{ name: 'shippingLineId', type: 'string'},
					{ name: 'partyIdFrom', type: 'string'},
					{ name: 'billNumber', type: 'string'},
					{ name: 'billId', type: 'string'},
					{ name: 'containerId', type: 'string'},
					{ name: 'containerNumber', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'billAgreementId', type: 'string'},
         		],
             	localdata: recordDataById,
             	updaterow: function (rowid, newdata, commit) {
             		commit(true);
             		var containerId = newdata.containerId;
             		var containerNumber = newdata.containerNumber;
             		$.ajax({
                        type: \"POST\",                        
                        url: 'updateContainerNumber',
                        data: {containerId: containerId, containerNumber: containerNumber},
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
         setTimeout(function() {
        	 prepareEventDbClick('jqxgridDetail'+index);
		 },0);
         if (grid != null) {
             grid.jqxGrid({
                 source: nestedGridAdapter, 
                 width: '96%',
                 height: '92%',
		 		 editable:true,
		 		 pagesize: 5,
		 		 pageable: true,
		 		 editmode:\"click\",
		 		 selectionmode:\"checkbox\",
		 		 theme: 'olbius',
		 		 toolbarheight: '40',
		 		 showheader: true,
                 columns: [
                           	{ text: '${uiLabelMap.billAgreementId}', datafield: 'billAgreementId', editable: false, hidden: true},
                           	{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', editable: false, hidden: true},
							{ text: '${uiLabelMap.AgreementName}', datafield: 'agreementName', editable: false},
							{ text: '${uiLabelMap.OrderOrderId}', datafield: 'orderId', width: '150px', editable: false},
							{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', columntype: 'datetimeinput',width: '150px', editable: false, cellsformat: 'dd/MM/yyyy'},
							{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', width: '150px', editable: false},
							{ text: '${uiLabelMap.containerId}', datafield: 'containerId', width: '150px', editable: false, hidden: true},
							{ text: '${uiLabelMap.containerNumber}', datafield: 'containerNumber', width: '150px', editable: true},
							{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: '150px', columntype: 'dropdownlist', editable: false,
								cellsrenderer: function(row, colum, value){
									return '<span>' + mapStatus[value] + '</span>';
								}
//								createeditor: 
//									function(row, column, editor){
//									editor.jqxDropDownList({ autoDropDownHeight: true, source: statusList, displayMember: 'statusId', valueMember: 'statusId' ,
//									    renderer: function (index, label, value) {
//									    	if (index == 0) {
//				                        		return value;
//											}
//										    return mapStatus[value];
//									    } });
//									}
							},
                 ],
                 showtoolbar: true,
                 rendertoolbar: function (toolbar) {
                	 renderToolbar(toolbar, index, billNumberGlobal, billIdGlobal);
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
					 { name: 'partyIdTo', type: 'string'},
					 ]"/>
<#assign columnlist="					 
						{ text: '${uiLabelMap.billId}', datafield: 'billId', width: '200px', editable: true, filterable: false },
						{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', editable: false, filterable: true},
						{ text: 'ShippingLine', datafield: 'partyIdFrom', editable: false, hidden: true, filterable: true},
						{ text: '${uiLabelMap.departureDate}', datafield: 'departureDate', editable: false, columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy'},
						{ text: '${uiLabelMap.arrivalDate}', datafield: 'arrivalDate', editable: false,  columntype: 'datetimeinput', filtertype: 'range', width: '250px', cellsformat: 'dd/MM/yyyy'},
					 "/>

						
		<@jqGrid filtersimplemode="true" alternativeAddPopup="alterpopupWindow" addType="popup" initrowdetails = "true" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow"
			editable="false" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true"
		 	url="jqxGeneralServicer?sname=JQGetListReceiveAgreement" updateUrl="jqxGeneralServicer?sname=updateBillOfLading&jqaction=U"
			createUrl="jqxGeneralServicer?sname=createBillOfLading&jqaction=C" rowdetailsheight="255" 
		 	addColumns="billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
		 	editColumns="billId;billNumber;departureDate(java.sql.Timestamp);arrivalDate(java.sql.Timestamp);partyIdFrom;partyIdTo"
		 		contextMenuId="contextMenu" mouseRightMenu="true"
		 		/>
			<div id="myMenuPlace"></div>
			
<#-- begin alterPopupWindow -->					
						<div id="alterpopupWindow" style="display:none; overflow: hidden;">
				        <div>${uiLabelMap.AddNewBillOfLading}</div>
				        <div style="overflow: hidden;">
				        	<div class="row-fluid">
			        			<div class="span12" style="margin-top: 8px;">
			    	 			<div class="span4" style="text-align: right;">${uiLabelMap.BillNumber}<span style="color:red;"> *</span></div>
			    	 			<div class="span7"><input type='text' id="txtBillNumber" style="height: 18px;"/></div>
			    	 			</div>
			    	 		<#--	<div class="span12">
				    	 			<div class="span4" style="text-align: right;">${uiLabelMap.FormFieldTitle_partyIdTo}<span style="color:red;"> *</span></div>
				    	 			<div class="span7"><div type='text' id="txtpartyIdTo"></div></div>
				    	 			</div> -->
			    	 			</div>
		    	 			<div class="row-fluid">
			    	 			<div class="span12" style="margin-top: 8px;">
				    	 			<div class="span4" style="text-align: right;">${uiLabelMap.FromShippingLine}<span style="color:red;"> *</span></div>
				    	 			<div class="span7"><div type='text' id="txtpartyIdFrom"></div></div>
			    	 			</div>
		    	 			</div>
		    	 			<div class="row-fluid">
			    	 			<div class="span12" style="margin-top: 8px;">
				    	 			<div class="span4" style="text-align: right;">${uiLabelMap.departureDate}<span style="color:red;"> *</span></div>
				        	 		<div class="span7"><div type='text' id="txtdepartureDate"></div></div>
			    	 			</div>
		    	 			</div>
		    	 			<div class="row-fluid">
			    	 			<div class="span12" style="margin-top: 8px;">
				    	 			<div class="span4" style="text-align: right;">${uiLabelMap.arrivalDate}<span style="color:red;"> *</span></div>
				    	 			<div class="span7"><div type='text' id="txtarrivalDate"></div></div>
			    	 			</div>
		    	 			</div>
		    	 			<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		    	 			<div class="row-fluid">
			        	 		<div class="span12" style="margin-top: 8px;">
			        	 			<div class="span4"></div>
			        	 			<div class="span7" style="padding-left: 82px;"><button id='alterSave'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='alterCancel'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
				                </div>
				             </div>
				        </div>
				    </div>
				    
<#-- END alterPopupWindow-->


		<#--		  <div id="jqxwindowPopupAdder1">
			    	<div>${uiLabelMap.SelectAgreements}</div>
			    	<div style="overflow-x: hidden;">
		        		<div class="row-fluid">
			        		<div class="span12 no-left-margin">
			                	<button id="clearfilteringPopupAdder" style="float: right; cursor: default;" title="(Ctrl+F)" role="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" aria-disabled="false"><span style="color:red;font-size:80%;left:5px;position:relative;">x</span><i class="fa-filter"></i> ${uiLabelMap.accRemoveFilter}</button>
			                </div>
				 			<div class="span12 no-left-margin" id="jqxgridPopupAdder"></div>
			                <div class="span12 no-left-margin">
			                	<div class="span5"></div>
			                	<div class="span7"><button id='alterSaveAdder'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='alterCancelAdder'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
			                </div>
		                </div>
				    </div>
			    </div>  -->
			    
			    <div id="jqxwindowOrderViewer" style="display: none;">
		    	<div>${uiLabelMap.ListOrders}</div>
		    	<div style="overflow: hidden;">
	        		<div class="row-fluid" id="fluidDiv">
	        		
	        			<div id="containerOrderViewer" style="width: 100%"></div>
	        		
		        		<#-- <div class="span12 no-left-margin">
		                	<button id="clearfilteringOrderViewer" style="float: right; cursor: default;" title="(Ctrl+F)" role="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" aria-disabled="false"><span style="color:red;font-size:80%;left:5px;position:relative;">x</span><i class="fa-filter"></i> ${uiLabelMap.accRemoveFilter}</button>
		                </div> -->
			 			<div class="span12 no-left-margin" id="jqxgridOrderViewer"></div>
		               <#-- <div class="span12 no-left-margin">
		                	<div class="span5"></div>
		                	<div class="span7"><button id='alterCancelOrderViewer' style="display: none;"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
		                </div> -->
	                </div>
			    </div>
		    </div>
			    
			    <div id="containerPopupAdder" style="width: 100%"></div>
		        <div id="jqxNotificationPopupAdder">
			        <div id="notificationContentAdder">
			        </div>
		        </div>
		        
		        <div id="jqxNotificationOrderViewer">
			        <div id="notificationOrderViewer">
			        </div>
		        </div>
		        
		        <div id='contextMenu' style="display:none;">
					<ul>
						<li><i class="fa-file-text-o"></i>&nbsp;&nbsp;${uiLabelMap.QADocumentation}
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
		        
			<style>
			</style>
			<#assign status = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />

<script type="text/javascript">
		var contextMenu = $("#contextMenu").jqxMenu({ width: 340, height: 86, autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
		$("#jqxgrid").on('contextmenu', function () {
		    return false;
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
		 $("#SentTwoNotifice").on("click", function() {
			 var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
			 var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
			 var billNumber = rowData.billNumber;
			 var header = "Co van don sap ve: " + billNumber;
			 var openTime = null;
			 sentNotify(header, openTime);
			 header = "10 ngay nua van don " + billNumber + " se ve.";
			 openTime = new Date().getTime() + 10*86400000;
			 sentNotify(header, openTime);
		 });
		 $("#Advances").on("click", function() {
			 window.location.href = "billOfLadingCost?party=IMPORT_ADMIN";
		 });
		 function sentNotify(header, openTime) {
			 var jsonObject = {partyId: "ImportSpecialist",
						header: header,
						openTime: openTime,
						action: "receiveAgreement"};
			$.ajax({
			     url: "createNotification",
			     type: "POST",
			     data: jsonObject,
			     async: false,
			     success: function(res) {
			     	
			     }
			 }).done(function() {
			 	
			});
		}
		//Init button
		$("#alterCancel").jqxButton({template: "danger" });
		$("#alterSave").jqxButton({template: "primary" });
		$("#txtdepartureDate").jqxDateTimeInput({width: '220px'});
		$("#txtarrivalDate").jqxDateTimeInput({width: '220px'});
		$("#txtarrivalDate").jqxDateTimeInput('val', null);
		//End init button

<#-- BEGIN EDIT BY DAT -->
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
					height: '25px',
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
		 	myToolBar +='<div class="span7"></div>';
	       	myToolBar += '<div class="span5">';
	       	myToolBar += '<div class="span9" style="margin-left: 36px;position: absolute;"><div id="selectAgreement'+index+'" style="margin-top: 5px;" class="span8 selectAgreement") ></div><a id="'+addAgreeId+'" style="margin-top: 10px; margin-left: 10px;cursor: pointer;" class="icon-plus-sign open-sans" title="${uiLabelMap.addNewAgree}">${uiLabelMap.addNewAgree}</a></div>';
	       	myToolBar += '<div class="span3" style="float: right;"><a id="'+removeId+'" style="margin-top: 10px; margin-right: 2px; cursor: pointer; float: right;" title="${uiLabelMap.removeAgree}" class="icon-trash open-sans">${uiLabelMap.removeAgree}</a></div>';
	       	myToolBar += '</div></div>';
	       	container.append(myToolBar);
	       	toolbar.append(container);
	       	
	       	$('#'+addAgreeId).on('click', function(){
	       		AddAgreementToRow(comboBoxId, gridId, billNumberGlobal, billIdGlobal,index);
	       	});
	       	$('#'+removeId).on('click', function(){
	       		deleteAgreementOfBill(gridId);
	       	});
	       	$('#'+comboBoxId).jqxComboBox({
	       		source: source_listAgreementNotBill,
	       		displayMember: 'attrValue',
				valueMember: 'agreementId',
	   	 		theme:'energyblue',
	   	 		width: '150px',
	   	 		height: '16px',
	   	 		searchMode: 'containsignorecase',
	   	 		autoOpen: true,
	   	 		autoComplete: false,
	   	 		placeHolder: '${StringUtil.wrapString(uiLabelMap.SelectAgreement)}...'
	       	});
	       	
       	}
//End Function rederToolbar
//BEGIN INIT SOURCE FOR selectAgreementindex
// ENDINIT SOURCE FOR selectAgreementindex
//Start function AddAgreementToRow
		function AddAgreementToRow(comboBoxId, gridId, billNumberGlobal, billIdGlobal, index){
			var valueCombo = $("#"+comboBoxId).jqxComboBox('getSelectedItem');
			if(valueCombo){
//				console.log(valueCombo.value);
				var agreementId = valueCombo.value;
				var agreementName = valueCombo.label;
				jQuery.ajax({
			        url: "createAgreementToBillAjax",
			        type: "POST",
			        async: false,
			        data: {billId: billIdGlobal, agreementId: agreementId, billNumber: billNumberGlobal},
			        dataType: 'json',
			        success: function(res) {
			        	var resultListAgreement = res.resultListAgreement;
//			        	console.log(resultListAgreement);
			        	for(var i = 0; i < resultListAgreement.length; i++){
			    			var containerId = resultListAgreement[i].containerId;
			    			var billAgreementId = resultListAgreement[i].billAgreementId;
			    			var orderId = resultListAgreement[i].orderId;
			    			var date = new Date(resultListAgreement[i].agreementDate);
			    			var datarow = {billAgreementId: billAgreementId, agreementName: agreementName, agreementId :agreementId, orderId: orderId, agreementDate: date, billId: billIdGlobal, billNumber: billNumberGlobal, containerId: containerId, statusId: "AGREEMENT_PROCESSING"};
	                		$("#"+gridId).jqxGrid('addrow', null, datarow, 'first');
			        	}
			        	source_listAgreementNotBill = res.listAgreementNotBill;
			        	$(".selectAgreement").jqxComboBox({source: source_listAgreementNotBill});
			        }
			    });
				
			}
		}
		function findAndRemove(array, property,vl) {
			   for(var i = 0; i < array.length; i++){
				  if(array[i][property] == vl){
//					  console.log("dsads");
					  array.splice(i,1);
				  }
			   }
			}
//End function AddAgreementToRow
		
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
//			console.log(rowIDs);
//			console.log(dataArr);
//			$("#"+gridId).jqxGrid('deleterow', rowIDs);
//			for(var j = 0; j < rowIDs.length; j++){
//				$("#"+gridId).jqxGrid('unselectrow', rowIDs[j]);
//			}
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
//					for(var j = 0; j < rowIDs.length; j++){
//						$("#"+gridId).jqxGrid('unselectrow', rowIDs[j]);
//					}
		        }
		    });
		}
//END function deleteAgreementOfBill
		
//BEGIN function callFunctionAjax
//		function callFunctionAjax(url, data, FnSuccess, ){
//			jQuery.ajax({
//		        url: url,
//		        type: "POST",
//		        async: false,
//		        data: data,
//		        dataType: 'json',
//		        success: FnSuccess
//		    });
//		}
// END function callFunctionAjax

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
	            width: 450, height: 265, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: 'olbius'
	        });
			var wtmp = window;
			var tmpwidth = $('#alterpopupWindow').jqxWindow('width');
			$('#alterpopupWindow').jqxWindow({
			    position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
			});
			$("#alterpopupWindow").on('close', function (event) {
				$('#alterpopupWindow').jqxValidator('hide');
			});
// end show window add one bill

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
	        		    if (checkFromDate < jsDate) {
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
//					function toString(myJSObject) {
//				    	myJSObject = JSON.stringify(myJSObject);
//				    	myJSObject = myJSObject.replaceAll('"', "'");
//				    	return myJSObject;
//					}
//					var billIdStoreVarible = "";
//					var billNumberStoreVarible = "";
//					var gridStoreVarible = "";
//					function AddToRow(grid, billNumber, billId) {
//						billIdStoreVarible = billId;
//						billNumberStoreVarible = billNumber;
//						gridStoreVarible = grid;
//						loadAgreementNotHasBill();
//						$("#jqxwindowPopupAdder1").jqxWindow('open');
//					}
					
//					$("#alterSaveAdder").click(function () {
//			        	var rowindex = $("#jqxgridPopupAdder").jqxGrid('getselectedrowindexes');
//			        	var oneTurn = true;
//						for ( var r in rowindex) {
//							var thisRowData = $('#jqxgridPopupAdder').jqxGrid('getrowdata', rowindex[r]);
//							var thiscontainerId = thisRowData.containerId;
//							var thisorderId = thisRowData.orderId;
//							thisRowData.billNumber = billNumberStoreVarible;
//							var obj = {orderId: thisorderId, billId: billIdStoreVarible};
//							saveAgreementToBill(obj ,
//												"saveAgreementToBillAjax", gridStoreVarible, thisRowData, '', false, oneTurn);
//							oneTurn = false;
//						}
//						$("#jqxwindowPopupAdder1").jqxWindow('close');
//			        });
//					function bindPopup(data) {
//						var rowindexs = $("#jqxgridPopupAdder").jqxGrid('getselectedrowindexes');
//						if(!rowindexs.length == 0){
//							$('#jqxgridPopupAdder').jqxGrid('clearSelection');
//						}
//						$('#jqxgridPopupAdder').jqxGrid('clear');
//						for ( var d in data) {
//							data[d].agreementDate == undefined?data[d].agreementDate = null : data[d].agreementDate = data[d].agreementDate['time'];
//							data[d].departureDate == undefined?data[d].departureDate = null : data[d].departureDate = data[d].departureDate['time'];
//							data[d].arrivalDate == undefined?data[d].arrivalDate = null : data[d].arrivalDate = data[d].arrivalDate['time'];
//						}
//						var recordPopupDataSource = { 
//											localdata: data,
//											id : 'agreementId',
//											datafields: [
//						                  					{ name: 'agreementId', type: 'string' },
//						                  					{ name: 'orderId', type: 'string'},
//						                  					{ name: 'agreementDate', type: 'date', other: 'Timestamp'},
//						                  					{ name: 'shippingLineId', type: 'string'},
//						                  					{ name: 'partyIdFrom', type: 'string'},
//						                  					{ name: 'billNumber', type: 'string'},
//						                  					{ name: 'billId', type: 'string'},
//						                  					{ name: 'containerId', type: 'string'},
//						                  					{ name: 'containerNumber', type: 'string'},
//						                  					{ name: 'departureDate', type: 'date', other: 'Timestamp'},
//						                  					{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
//						                  					{ name: 'partyRentId', type: 'string'},
//						                  					{ name: 'statusId', type: 'string'},
//						                           		]
//						                           }
//						var PopupAdderGridAdapter = new $.jqx.dataAdapter(recordPopupDataSource);
//						$("#jqxgridPopupAdder").jqxGrid({
//			                 source: PopupAdderGridAdapter,
//			                 showfilterrow: true,
//			 		 		 filterable: true,
//			 		 		 handlekeyboardnavigation: function (event) {
//			                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
//			                    $('body').css('overflow', 'hidden');
//			                    if (key == 70 && event.ctrlKey) {
//			                    	$('#clearfilteringPopupAdder').click();
//			                    	return true;
//			                    }
//			 		 		 }
//			             });
//					}
//					var rowsExpaded;
//					function refresh() {
////						$("#jqxgridPopupAdder").jqxGrid({ filterable: false, showfilterrow: false });
////						$("#jqxgridOrderViewer").jqxGrid({ filterable: false, showfilterrow: false });
////						$('#clearfilteringbuttonjqxgrid').click();
//					}
//					function loadAgreementNotHasBill() {
//						var agreementNotHasBill = [];
//						jQuery.ajax({
//					        url: "loadAgreementNotHasBillAjax",
//					        type: "POST",
//					        async: true,
//					        data: {},
//					async: false,
//					        dataType: 'json',
//					        success: function(res) {
//					        	agreementNotHasBill = res["agreementNotHasBill"];
//					        }
//					    }).done(function() {
//					    	bindPopup(agreementNotHasBill);
//						});
//					}
//					$( "#clearfilteringPopupAdder" ).click(function() {
//						$('#jqxgridPopupAdder').jqxGrid('clearfilters');
//					});
//					$("#jqxwindowPopupAdder1").jqxWindow({theme: 'olbius',
//			            width: 880, maxWidth: 1845, minHeight: 420, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdder"), modalOpacity: 0.7
//			        });
//					$('#jqxwindowPopupAdder1').on('close', function (event) {
//						setTimeout(function(){
//							$("#jqxgridPopupAdder").jqxGrid({ filterable: false, showfilterrow: false });
//						}, 0);
//					});
//					$("#alterCancelAdder").jqxButton({template: "danger" });
//			        $("#alterSaveAdder").jqxButton({template: "primary" });
//			        function saveAgreementToBill(data, url, grid, thisRowData, rowId, isDelete, oneTurn) {
//			        	var saveSuccess = true;
//			        	jQuery.ajax({
//					        url: url,
//					        type: "POST",
//					        async: true,
//					        data: data,
//					        dataType: 'json',
//					async: false,
//					        success: function(res) {
//					        	res["_ERROR_MESSAGE_"] == undefined?saveSuccess=true:saveSuccess=false;
//					        }
//					    }).done(function() {
//					    	if (saveSuccess) {
//					    		if (oneTurn) {
//					    			$("#notificationContentAdder").html('${StringUtil.wrapString(uiLabelMap.DAUpdateSuccessful)}');
//					                $("#jqxNotificationPopupAdder").jqxNotification("open");
//								}
//				                if (isDelete) {
//				                	$('#' + grid).jqxGrid('deleterow', rowId);
//								} else {
//									$('#' + grid).jqxGrid('addrow', null, thisRowData);
//								}
//							}else {
//								$("#jqxNotificationPopupAdder").jqxNotification({template: 'error'});
//					        	$("#notificationContentAdder").html('${StringUtil.wrapString(uiLabelMap.DAUpdateError)}');
//				                $("#jqxNotificationPopupAdder").jqxNotification("open");
//							}
//						});
//					}
//			        $("#jqxNotificationPopupAdder").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: 'info'});
			        $("#jqxNotificationOrderViewer").jqxNotification({ width: "100%", appendContainer: "#containerOrderViewer", opacity: 0.9, autoClose: true, template: 'info'});
			        
//					$("#jqxgridPopupAdder").jqxGrid({
//		                source: null, 
//		                width: 870, 
//		                height: 312,
//		                selectionmode: 'checkbox',
//				 		theme: 'olbius',
//				 		pageable: true,
//				 		pagesize: 9,
//				 		sortable: true,
//				 		editable: false,
//		                columns: [
//									{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', width: '150px', filtertype: 'input'},
//									{ text: '${uiLabelMap.OrderOrderId}', datafield: 'orderId', width: '150px', filtertype: 'input'},
//									{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', columntype: 'datetimeinput',width: '200px', filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
//									{ text: '${uiLabelMap.shippingLineId}', datafield: 'shippingLineId', width: '190px', 
//										createeditor: function (row, column, editor) {
//									},
//										cellsrenderer: function(){
//											return 'HYUNDAI_COMPANY';
//										},
//									},
//									{ text: '${uiLabelMap.Status}', datafield: 'statusId', columntype: 'dropdownlist', filtertype: 'checkedlist', cellsrenderer:
//										function(row, colum, value){
//											return '<span>' + mapStatus[value] + '</span>';
//										},createfilterwidget: function (column, htmlElement, editor) {
//					    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(statusList), displayMember: 'statusId', valueMember: 'statusId' ,
//					                            renderer: function (index, label, value) {
//					                            	if (index == 0) {
//					                            		return value;
//													}
//												    return mapStatus[value];
//								                } });
//					    		        	editor.jqxDropDownList('checkAll');
//					                    }}
//		                ]
//		            });
        var orderIdGlobal="";
		function prepareEventDbClick(gridId) {
			$("#" + gridId).on("celldoubleclick", function (event)
					{
					    var args = event.args;
					    var dataField = args.datafield;
					    if (dataField == 'orderId') {
					    	 var valueDbcl = args.value;
					    	 orderIdGlobal=valueDbcl;
//								    	 setTimeout(function(){
//								    		 getListOrderItems(valueDbcl);
//								    	 }, 0);
					    	 $("#jqxwindowOrderViewer").jqxWindow('open');
						}
			});
		}
		function getListOrderItems(orderIdGlobal) {
			var listOrderItems = [];
			jQuery.ajax({
		        url: "getListOrderItemsAjax",
		        type: "POST",
		        async: false,
		        data: {orderId: orderIdGlobal},
		        dataType: 'json',
		        success: function(res) {
		        	listOrderItems = res["listOrderItems"];
		        }
		    }).done(function() {
		    	bindOrderItemsPopup(listOrderItems, orderIdGlobal);
			});
		}
		
		function bindOrderItemsPopup(listOrderItems, orderIdGlobal) {
//						$('#jqxgridOrderViewer').jqxGrid('clear');
			for ( var d in listOrderItems) {
				listOrderItems[d].datetimeManufactured == undefined?listOrderItems[d].datetimeManufactured = null : listOrderItems[d].datetimeManufactured = listOrderItems[d].datetimeManufactured['time'];
				listOrderItems[d].expireDate == undefined?listOrderItems[d].expireDate = null : listOrderItems[d].expireDate = listOrderItems[d].expireDate['time'];
			}
			var orderssource = { 
					
				  datafields: [
                              	  { name: 'orderId', type:'string' },
                                  { name: 'orderItemSeqId', type: 'string' },
                                  { name: 'productId', type: 'string' },
                                  { name: 'quantity', type: 'number' },
                                  { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
                                  { name: 'expireDate', type: 'date', other: 'Timestamp'},
			                  ],
			                                
                  localdata: listOrderItems,
                  updaterow: function (rowid, newdata, commit) {
                 	 commit(true);
                 	 var orderId = newdata.orderId;
                 	 var orderItemSeqId = newdata.orderItemSeqId;
                 	 var quantity = newdata.quantity;
                 	 var productId = newdata.productId;
                 	 var datetimeManufacturedStr = newdata.datetimeManufactured;
                 	 var datetimeManufactured = new Date(datetimeManufacturedStr);
                 	 var expireDateStr = newdata.expireDate;
                 	 var expireDate = new Date(expireDateStr);
                 	 if(typeof expireDateStr != 'undefined' || typeof datetimeManufacturedStr != 'undefined'){
                 		 $.ajax({
                              type: "POST",                        
                              url: 'updateOrderItemWhenReceiveDoc',
                              data: {orderId: orderId, orderItemSeqId: orderItemSeqId, quantity: quantity, productId: productId, datetimeManufactured: datetimeManufactured.getTime(), expireDate: expireDate.getTime()},
                              async: false,
                              success: function (data, status, xhr) {
                                  if(data.responseMessage == "error")
                                  {
                                  	commit(false);
                                  	$("#jqxNotificationOrderViewer").jqxNotification({ template: 'error'});
                                  	$("#notificationOrderViewer").text(data.errorMessage);
                                  	$("#jqxNotificationOrderViewer").jqxNotification("open");
                              			if(orderItemSeqId == null){
                              				$("#jqxgridOrderViewer").jqxGrid('setcellvaluebyid', rowid, 'orderItemSeqId', 'error');
                              			}
                                  }else{
                                  	commit(true);
                                  	$("#container").empty();
                                  	$("#jqxNotificationOrderViewer").jqxNotification({ template: 'info'});
                                  	$("#notificationOrderViewer").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                                  	$("#jqxNotificationOrderViewer").jqxNotification("open");
     	                 			 if(orderItemSeqId == null){
     	                 				$("#jqxgridOrderViewer").jqxGrid('setcellvaluebyid', rowid, 'orderItemSeqId', data.orderItemSeqId);
     	                        	 }
                                  }
                              },
                              error: function () {
                                  commit(false);
                              }
                          });
                 	 }
                 	},
                 	deleterow: function (rowid, commit) {
                        commit(true);
                    }
              }
              var OrderViewerGridAdapter = new $.jqx.dataAdapter(orderssource);
			  initOrderView(OrderViewerGridAdapter, orderIdGlobal)
		}
		$('#jqxwindowOrderViewer').on('close', function (event) {
				$("#jqxgridOrderViewer").jqxGrid({ filterable: false, showfilterrow: false });
				$("#jqxgridOrderViewer").jqxGrid('destroy');
				$("#fluidDiv").append("<div class='span12 no-left-margin' id='jqxgridOrderViewer'></div>");
		});
		$('#jqxwindowOrderViewer').on('open', function (event) {
			getListOrderItems(orderIdGlobal);
			var wtmp = window;
			var tmpwidth = $('#jqxwindowOrderViewer').jqxWindow('width');
			$('#jqxwindowOrderViewer').jqxWindow({
			    position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
			});
		});
		$("#jqxwindowOrderViewer").jqxWindow({theme: 'olbius',
            width: 920, maxWidth: 1845, minHeight: 370, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
        });
		
//					$("#alterCancelOrderViewer").jqxButton({template: "danger" });
        $( "#clearfilteringOrderViewer" ).click(function() {
			$('#jqxgridOrderViewer').jqxGrid('clearfilters');
        });
        var productIdComboBoxCell = "";
        function initOrderView(OrderViewerGridAdapter, orderIdGlobal){
	        $('#jqxgridOrderViewer').jqxGrid({
                source: OrderViewerGridAdapter,
                width: '100%',
                height: 312,
                showtoolbar: true,
                editable: true,
		 		editmode:"selectedrow",
		 		showfilterrow: true,
 		 		filterable: true,
		 		showheader: true,
		 		selectionmode:"singlerow",
		 		theme: 'olbius',
		 		pageable: true,
		 		pagesize: 9,
		 		handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    if (key == 70 && event.ctrlKey) {
                    	$('#clearfilteringOrderViewer').click();
                    	return true;
                    }    
 		 		 },
                columns: [
                   { text: "${uiLabelMap.OrderOrderId}", datafield: "orderId", editable: false, width: 140, hidden: true},
                   { text: "${uiLabelMap.orderItemSeqId}", datafield: "orderItemSeqId", editable: false, width: 140, hidden: true},
                   { text: "${uiLabelMap.ProductName}", datafield: "productId", columntype:"combobox", editable: true, minwidth: 200,
                	   cellsrenderer: function(row, colum, value){
    			        return '<span>' + mapProducts[value] + '</span>';
                	   },
	    		       createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					           editor.jqxComboBox({source: product, displayMember:"description", valueMember: "productId"});
					           editor.on('select', function (event){
			        		       var args = event.args;
			        		       if (args) {
			        		    	   var index = args.index;
			        		    	   var item = args.item;
			        		    	   productIdComboBoxCell = item.value;
			        		       }
	        		   });
    		       }
                   },
                   { text: "${uiLabelMap.OrderQuantityEdit}", datafield: "quantity", editable: true, columntype: "numberinput",width: 150, cellsalign: 'right',
                	   cellsrenderer: function(row, colum, value){
                		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
	    		        },   
                   },
                   { text: "${uiLabelMap.dateOfManufacture}", datafield: "datetimeManufactured", columntype: "datetimeinput", filtertype: 'range', width: "140", editable: true, cellsformat: 'dd/MM/yyyy',
                	   validation: function (cell, value) {
                		   lastTimeChoice = value;
                		   var thisRow = cell.row;
                		   var data = $('#jqxgridOrderViewer').jqxGrid('getrowdata', thisRow);
            		       var expireDate = data.expireDate;
            		       if (expireDate == null) {
            		    	   return true;
            		       }
                           if (expireDate < value) {
                        	   $("#inputdatetimeeditorjqxgridOrderViewerexpireDate").val('');
                            }
                	       return true;
                	    }
                   },
                   { text: "${uiLabelMap.ProductExpireDate}", datafield: "expireDate", columntype: "datetimeinput", filtertype: 'range', width: "140", editable: true, cellsformat: 'dd/MM/yyyy',
                	   validation: function (cell, value) {
                		   if (lastTimeChoice == null) {
                			   return { result: false, message: '${uiLabelMap.ChoicedatetimeManufacturedFirst}' };
                		   }
                		   var thisRow = cell.row;
                		   var data = $('#jqxgridOrderViewer').jqxGrid('getrowdata', thisRow);
            		       var datetimeManufactured = 0;
            		       lastTimeChoice==0?datetimeManufactured=data.datetimeManufactured:datetimeManufactured=lastTimeChoice;
                           if (datetimeManufactured > value) {
                        	   return { result: false, message: '${uiLabelMap.DateExpirecannotbeforeDateManufactured}' };
                            }
                	       return executeQualityPublication(data, value, productIdComboBoxCell);
                	    },
                   }
                ],
                rendertoolbar: function (toolbar) {
                	var container = $("<div style='overflow: hidden;'></div>");
                	var myToolBar = '<div class="row-fluid">';
        		 	myToolBar +='<div class="span10"></div>';
        	       	myToolBar += '<div class="span2">';
        	       	myToolBar += '<div class="span4" style="margin-top: 10px;padding-right: 10px;"><a id="addOrderItem" title="add" style="cursor: pointer; float: right;" class="icon-plus-sign open-sans">${uiLabelMap.Add}</a></div>';
        	       	myToolBar += '<div class="span4" style="margin-top: 10px;padding-right: 10px;"><a id="deleteOrderItem" title="delete" style="cursor: pointer; float: right;" class="icon-trash open-sans">${uiLabelMap.Delete}</a></div>';
        	       	myToolBar += '<div class="span4" style="margin-top: 10px;"><i style="color:red; position: absolute;margin-left: -8px;font-size: 15px;">x</i><a id="filterOrderItem" style="cursor: pointer; float: right;" class="fa-filter open-sans">${uiLabelMap.DAClearFilter}</a></div>';
        	       	myToolBar += '</div></div>';
                	container.append(myToolBar);
                	toolbar.append(container);
                	$('#addOrderItem').on('click', function(){
                		addOrderItem(orderIdGlobal);
                	});
                	$('#deleteOrderItem').on('click', function(){
                		removeOrderItem(orderIdGlobal);
                	});
                 }
             });
        }
        function removeOrderItem(orderIdGlobal){
        	var rowIndex = $('#jqxgridOrderViewer').jqxGrid('getselectedrowindex');
        	var data = $('#jqxgridOrderViewer').jqxGrid('getrowdata', rowIndex);
        	var rowId = $('#jqxgridOrderViewer').jqxGrid('getrowid', rowIndex);
//			        	console.log(data);
//			        	console.log(rowId);
//			        	$('#jqxgridOrderViewer').jqxGrid('deleterow', rowId);
        	$.ajax({
                type: "POST",
                url: 'removePurchaseOrderItem',
                data: {orderId: orderIdGlobal, orderItemSeqId: data.orderItemSeqId, quantity: data.quantity, productId: data.productId},
                async: false,
                success: function (data, status, xhr) {
                	$('#jqxgridOrderViewer').jqxGrid('deleterow', rowId);
                },
                error: function () {
//	                            commit(false);
                }
            });
        }
        
//BEGIN FUNCTION addOrderItem()
        function addOrderItem(orderIdGlobal){
			   
		   	var rowNew = {orderId: orderIdGlobal};
		   	$('#jqxgridOrderViewer').jqxGrid('addrow', null, rowNew, 'first');
//					   	console.log(rowNew);
        }
//END function addOrderItem()
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
    			var header = "Tao cong bo chat luong cho " + mapProducts[productId] + " [" + productId + "]";
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
    					var header = "Cong bo chat luong san pham " + mapProducts[productId] + " [" + productId + "] sap het han";
    					var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationPreExpire}</h4>";
    					confirmInsertQualityPublication(productId, message, header, "");
    			}
    			if(leftTime < 0){
    				var header = "Cong bo chat luong san pham " + mapProducts[productId] + " [" + productId + "] da het han";
    				var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + mapProducts[productId] + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationExpire}</h4>";
    				confirmInsertQualityPublication(productId, message, header, "");
    			}
    			var expireDateProduct = qualityPublication.expireDate;
    			if (validateDate != expireDateProduct) {
    				var header = "Cong bo chat luong san pham " + mapProducts[productId] + " [" + productId + "] co thay doi";
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
            	createNotification(productId, "qaadmin", header, expireDateProduct);
            	$('#window01').jqxWindow('close');
//			            	$("#myImage").html();
            });
           
        	$('#window01').jqxWindow({ height: 180, width: 700, resizable: false, maxWidth: 1200, isModal: true, modalOpacity: 0.7 });
        	$('#window01').on('close', function (event) {
            	 $('#window01').jqxWindow('destroy');
//			            	 $("#myImage").html();
             });
    	}
    	function createNotification(productId, partyId, messages, expireDateProduct) {
    			var targetLink = "productId=" + productId + ";expireDateProduct=" + expireDateProduct;
    			if (expireDateProduct == "") {
    				targetLink = "productId=" + productId;
    			}
    			var action = "CreateProductQuality";
    			var header = messages;
    			var jsonObject = {partyId: partyId,
    								header: header,
    								openTime: null,
    								action: action,
    								targetLink: targetLink,};
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
        	$("#jqxNotificationNested").jqxNotification({ width: "1358px", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
        	getProductShelfLife();
    	});
</script>
<style>
</style>
<div id="myImage"></div>
