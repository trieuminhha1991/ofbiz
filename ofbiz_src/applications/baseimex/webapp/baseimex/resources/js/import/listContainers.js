$(document).ready(function() {
	ObjConts.init();
});
var ObjConts = (function() {
	var grid = $("#jqxGridContainers");
	var gridFacility = $("#gridFacilityContainer");
	var gridBOL = $("#jqxGridBOL");
	var validatorVAL = null;
	var contSelected = null;
	addWindow = $("#popupWindowContainer");
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidate();
	};
	
	var initInput = function() { 
		$("#billOfLading").jqxDropDownButton({width: 300, theme: theme}); 
		$("#containerNumber").jqxInput({width: 295, height: 24, theme: theme}); 
		$("#sealNumber").jqxInput({width: 295, height: 24, theme: theme}); 
		$("#description").jqxInput({width: 300, theme: theme}); 
		$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#facilityContainer").jqxDropDownButton({width: 300, theme: theme});
		$('#facilityContainer').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		if (billSelected != null){
			$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+billSelected.billNumber+'</div>');
			$("#billOfLading").jqxDropDownButton({disabled: true});
		}
		
		$("#jqxContextMenu").jqxMenu({ width: 320, autoOpenPopup: false, mode: 'popup', theme: theme});
		
		$("#containerTypeId").jqxDropDownList({placeHolder : uiLabelMap.PleaseSelectTitle, source: containerTypeData, displayMember: 'description', valueMember: 'containerTypeId', theme: theme, width: '300', height: '25'});
		
		addWindow.jqxWindow({
			width : 600,
			height : 380,
			resizable : false,
			cancelButton : $("#alterCancelContainer"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
	}
	
	var initElementComplex = function() {
		initGridContainers(grid);
		initFacilityGrid(gridFacility);
		if (!billSelected){
			initGridBillOfLading(gridBOL);
		}
	}
	
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities&primaryFacilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '150',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityName, datafield: 'facilityName', minwidth: '200',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			];
		
		var config = {
				width: 450, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				useUrl: true,
				url: url,                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridContainers = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEContainerNumber, dataField: 'containerNumber', width: 140, 
				cellsrenderer: function (row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjConts.showDetailContainer('+data.containerId+')"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIEContainerType, dataField: 'containerTypeId', width: 250,  filtertype: 'checkedlist',
				cellsrenderer: function (row, column, value) {
					return '<span>' + getContainerTypeDesc(value) +'</span>';
			    },
			    createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(containerTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'containerTypeId', valueMember: 'containerTypeId',
						renderer: function(index, label, value){
				        	if (containerTypeData.length > 0) {
				        		return getContainerTypeDesc(value);
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', width: 140, 
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span><a href="javascript:ObjConts.showDetailBill('+data.billId+')\"> ' + value  + '</a></span>';
				}
			},
			{ text: uiLabelMap.BIESealNumber, dataField: 'sealNumber', width: 140, 
			},
			{ text: uiLabelMap.BIEDepartureDate, dataField: 'departureDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEArrivalDate, dataField: 'arrivalDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.Description, dataField: 'description', minwidth: 100, },
        ];
		
		var datafield = [
         	{ name: 'containerId', type: 'string'},
         	{ name: 'containerNumber', type: 'string'},
         	{ name: 'containerTypeId', type: 'string'},
         	{ name: 'description', type: 'string'},
			{ name: 'billNumber', type: 'string'},
			{ name: 'sealNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'departureDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEContainer + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjConts.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var url = "jqGetContainers";
		if (billId){
			url = "jqGetContainers&billId=" + billId;
		}
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#jqxContextMenu"), false);
	}
	
	var initGridBillOfLading = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', minwidth: 140, 
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BIEDepartureDate, dataField: 'departureDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEArrivalDate, dataField: 'arrivalDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			];
		
		var datafield = [
			{ name: 'billNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'departureDate', type: 'date', other: 'Timestamp'},
			{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
			]
		
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetBillOfLading',                
				source: {pagesize: 15}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var initEvents = function() {
		addWindow.on('close', function (event) {
			 gridBOL.jqxGrid('clearSelection');
			 $("#containerTypeId").jqxDropDownList('clearSelection'); 
			 $("#containerTypeId").jqxDropDownList('uncheckAll'); 
			 $('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
			 if (billSelected != null){
				$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+billSelected.billNumber+'</div>');
				$("#billOfLading").jqxDropDownButton({disabled: true});
			 }
			 validatorVAL.hide();
			 $('#containerNumber').jqxInput('clear');
			 $('#sealNumber').jqxInput('clear');
			 $('#description').jqxInput('clear');
			 contSelected = null;
		 });
		
		gridBOL.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridBOL.jqxGrid('getrowdata', rowBoundIndex);
	        billSelected = {};
	        billSelected = $.extend({}, rowData);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.billNumber +'</div>';
	        $('#billOfLading').jqxDropDownButton('setContent', dropDownContent);
	        $("#billOfLading").jqxDropDownButton('close');
	    });
		
		gridBOL.on('bindingcomplete', function (event) {
			if (billSelected != null){
				var rows = gridBOL.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].billId == billSelected.billId){
							var index = gridBOL.jqxGrid('getrowboundindexbyid', rows[i].uid);
							gridBOL.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
		
		$("#alterSaveContainer").on('click', function (event) {
			var resultValidate = validatorVAL.validate();
			if(!resultValidate) return false;
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){
							var data = {
								containerNumber: $("#containerNumber").jqxInput('val'),
								billId: billSelected.billId,
								containerTypeId: $("#containerTypeId").jqxDropDownList('val'),
								sealNumber: $("#sealNumber").jqxInput('val'),
								description: $("#description").jqxInput('val')
				    		};
							if (contSelected != null){
								data.containerId = contSelected.containerId;
							}
							if (facilitySelected != null){
								data.facilityId = facilitySelected.facilityId;
							}
							$.ajax({
					    		url: "updateContainer",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			addWindow.jqxWindow('close');
					    			grid.jqxGrid('updatebounddata');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
		
		$("#jqxContextMenu").on('itemclick', function (event) {
			var liId = event.args.id;
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			var containerId = data.containerId;
			if (liId == "addContainer"){
				openPopupAdd();
			}
			
			if (liId == "editContainer"){
				openPopupEdit(data);
			}
			
			if (liId == "createInvoice"){
				 window.location.href = "CreateInvoice?containerId=" + containerId;
			}
			
			if (liId == "viewQuarantine"){
				QADocumentation.open(uiLabelMap.quarantineDocument);
				var documentCustomsId="";
				var registerNumber = "";
				var registerDate = "";
				var sampleSendDate = "";
				jQuery.ajax({
				        url: "getDocumentCustomsByContainer",
				        type: "POST",
				        async: false,
				        data: {documentCustomsTypeId: "QUARANTINE", containerId: data.containerId},
				        dataType: 'json',
				        success: function(res){
				        	documentCustomsId = res.resultListDoc.documentCustomsId;
				        	registerNumber = res.resultListDoc.registerNumber;
				        	registerDate = res.resultListDoc.registerDate;
				        	sampleSendDate = res.resultListDoc.sampleSendDate;
				        }
			     });
				 $('#documentCustomsId').val(documentCustomsId);
				 $('#containerCustomsId').val(data.containerId);
				 $('#documentCustomsTypeId').val("QUARANTINE");
				 $('#registerNumber').val(registerNumber);
				 $('#registerDate').jqxDateTimeInput('val', registerDate);
				 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
				 $('#customsTypeId').text(uiLabelMap.quarantineDocument);
			}
			
			if (liId == "viewTested"){
				QADocumentation.open(uiLabelMap.testedDocument);
				 var documentCustomsId="";
				 var registerNumber = "";
				 var registerDate = "";
				 var sampleSendDate = "";
				 jQuery.ajax({
				        url: "getDocumentCustomsByContainer",
				        type: "POST",
				        async: false,
				        data: {documentCustomsTypeId: "TESTED", containerId: data.containerId},
				        dataType: 'json',
				        success: function(res){
				        	documentCustomsId = res.resultListDoc.documentCustomsId;
				        	registerNumber = res.resultListDoc.registerNumber;
				        	registerDate = res.resultListDoc.registerDate;
				        	sampleSendDate = res.resultListDoc.sampleSendDate;
				        }
				 });
				 $('#documentCustomsId').val(documentCustomsId);
				 $('#containerCustomsId').val(data.containerId);
				 $('#documentCustomsTypeId').val("TESTED");
				 $('#registerNumber').val(registerNumber);
				 $('#registerDate').jqxDateTimeInput('val', registerDate);
				 $('#sampleSentDate').jqxDateTimeInput('val', sampleSendDate);
				 $('#customsTypeId').text(uiLabelMap.testedDocument);
			}
			
			if (liId == "agreementToQuarantineChild"){
				window.location.href = "exportAgreementToQuarantine?containerId=" + containerId;
			}
			
			if (liId == "agreementToValidationChild"){
				window.location.href = "exportAgreementToValidation?containerId=" + containerId;
			}
			
			if (liId == "refreshGrid"){
				grid.jqxGrid('updatebounddata');
			}
		});
		
		gridFacility.on('rowselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			facilitySelected = $.extend({}, rowData);
			var description = uiLabelMap.PleaseSelectTitle; 
			if (facilitySelected) {
				description = facilitySelected.facilityName;
			}
			
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#facilityContainer').jqxDropDownButton('setContent', dropDownContent);
		});
		
		gridFacility.on('rowdoubleclick', function (event) { 
			$('#facilityContainer').jqxDropDownButton('close');
		});
		
		gridFacility.on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = gridFacility.jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						var index = gridFacility.jqxGrid('getrowboundindexbyid', data1.uid);
						if (data1.facilityId == facilitySelected.facilityId){
							gridFacility.jqxGrid('selectrow', index);
						} else {
							gridFacility.jqxGrid('unselectrow', index);
						}
					}
				}
			}
		});
	}
	
	var initValidate = function() {
		var extendRules = [
			{
				input: '#billOfLading', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	if (billSelected == null){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#containerTypeId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var x = $("#containerTypeId").jqxDropDownList('val');
					if (x === undefined || x === null || x === ""){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#containerNumber', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($("#popupWindowContainer"), mapRules, extendRules, {position: 'right'});
	}
	
	var showDetailBill = function(billId) {
		location.href = "viewDetailBillOfLading?billId=" + billId;
	}
	
	var openPopupAdd = function() {
		addWindow.jqxWindow('open');
	}
	
	var openPopupEdit = function(data) {
		billSelected = {
			billId: data.billId,
			billNumber: data.billNumber
		}
		contSelected = {
			containerId : data.containerId,
		}
		$("#containerNumber").jqxInput('val', data.containerNumber); 
		$("#sealNumber").jqxInput('val', data.sealNumber); 
		$("#description").jqxInput('val', data.description); 
		$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+data.billNumber+'</div>');
		$("#containerTypeId").jqxDropDownList('val', data.containerTypeId);
		
		var rows = gridBOL.jqxGrid('getrows');
		if (rows && rows.length > 0){
			for (var i in rows){
				if (rows[i].billId == billSelected.billId){
					var index = gridBOL.jqxGrid('getrowboundindexbyid', rows[i].uid);
					gridBOL.jqxGrid('selectrow', index);
					break;
				}
			}
		}
		
		addWindow.jqxWindow('open');
	}
	
	var showDetailContainer = function (containerId){
		location.href = "viewDetailContainer?containerId=" + containerId;
	}
	
	return {
		init : init,
		showDetailBill: showDetailBill,
		openPopupAdd: openPopupAdd,
		showDetailContainer: showDetailContainer,
	}
}());