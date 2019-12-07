$(function(){
	FacilityObj.init();
});
var FacilityObj = (function(){
	var facilitySelected = null;
	var btnClick = false;
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		
		initGridPartyList();
		
		update({
			geoId: $("#countryGeoId").val(),
			geoAssocTypeId: "REGIONS",
			geoTypeId: "PROVINCE",
			}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'provinceGeoId');
		
		update({
			geoId: $("#provinceGeoId").val(),
			geoAssocTypeId: "REGIONS",
			geoTypeId: "DISTRICT",
			}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'districtGeoId');
	};
	var initInputs = function(){
		$("#storekeeperId").jqxDropDownButton({width: 200}); 
		$('#storekeeperId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#ownerPartyId").jqxCheckBox({height: 25, theme: theme});
		$("#facilitySizeAdd").jqxNumberInput({ width: '200px', height: '25px', inputMode: 'simple', spinButtons: true });
		$("#fromDate").jqxDateTimeInput({width: '200px', height: '25px'});
		$("#thruDate").jqxDateTimeInput({width: '200px', height: '25px'});
		$("#openedDate").jqxDateTimeInput({width: '200px', height: '25px'});
		$("#openedDate").jqxDateTimeInput("val",null);
		$("#thruDate").jqxDateTimeInput("val",null);
		$("#facilityId").jqxInput({width: 195, height: 20,});
		$("#phoneNumber").jqxInput({width: 195, height: 20,});
		$("#address").jqxInput({width: 195, height: 20,});
		$("#facilityName").jqxInput({width: 195, height: 20,});
		$("#facilityCode").jqxInput({width: 195, height: 20,});
		$("#fromDateManager").jqxDateTimeInput({width: '200px', height: '25px'});
		$("#thruDateManager").jqxDateTimeInput({width: '200px', height: '25px'});
		$("#thruDateManager").jqxDateTimeInput("val",null);
		$("#description").jqxInput({placeHolder: ". . .", height: 20, width: '195', minLength: 1});
		$("#menuForFacility").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		if ('' != curFacilityId && curFacilityId != null && curFacilityId != undefined){
			var arrayTmp = new Array();
			for (var i = 0; i < facilityData.length; i ++){
				if (facilityData[i].facilityId == curFacilityId){
					arrayTmp.push(facilityData[i]);
				}
			}
			$("#parentFacilityId").jqxDropDownList({placeHolder : uiLabelMap.PleaseSelectTitle, source: arrayTmp, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
			$("#parentFacilityId").val(curFacilityId);
		} else {
			$("#parentFacilityId").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: facilityData, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
		}
		$("#jqxNotificationUpdateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateSuccess", opacity: 0.9, autoClose: true, template: "success" });
		
		var requireLocData = [];
		requireLocData.push({
			text: uiLabelMap.LogYes,
			value: 'Y'
		});
		requireLocData.push({
			text: uiLabelMap.LogNO,
			value: 'N'
		});
		$("#requireLocation").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: requireLocData, displayMember: 'text', valueMember: 'value', theme: theme, width: '200', height: '25'});
		$("#requireLocation").jqxDropDownList('val', 'N');
		
		var requireDateData = [];
		requireDateData.push({
			text: uiLabelMap.LogYes,
			value: 'Y'
		});
		requireDateData.push({
			text: uiLabelMap.LogNO,
			value: 'N'
		});
		$("#requireDate").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: requireDateData, displayMember: 'text', valueMember: 'value', theme: theme, width: '200', height: '25'});
		$("#requireDate").jqxDropDownList('val', 'N');
		
	};
	
	var initGridPartyList = function(grid){	
		var grid = $("#managerPartyId");
		var datafield =  [
		                  { name: 'employeePartyId', type: 'string'},
		                  { name: 'employeePartyCode', type: 'string'},
		                  { name: 'fullName', type: 'string'},
		                  { name: 'emplPositionTypeId', type: 'string'},
		                  { name: 'emplPositionId', type: 'string'},
		                  { name: 'description', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeId, datafield: 'employeePartyCode', align: 'left', width: 120, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<span>' + value + '</span>';
		                		  }
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeName, datafield: 'fullName', align: 'left', minwidth: 100, pinned: true, editable: false,},
		                  ];
		var config = {
				width: 400, 
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
				url: 'jqGetPartyAndPositionInDepartment&emplPositionTypeId=LOG_STOREKEEPER',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initElementComplex = function(){
		var countryFaSizeConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'key',
			value: 'value',
			height: '25',
			width: '200',
			dropDownHeight: '200',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#facilitySizeUomId"), sourceUom, countryFaSizeConfig, ["AREA_m2"]);
			
		var countryGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '200',
			dropDownHeight: '200',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#countryGeoId"), countryData, countryGeoDataConfig, ["VNM"]);
		
		var provinceDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '200',
			dropDownHeight: '200',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#provinceGeoId"), provinceData, provinceDataConfig, []);
			
		var countyGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '200',
			dropDownHeight: '200',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#districtGeoId"), countyData, countyGeoDataConfig, []);
		
		var wardGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '200',
			dropDownHeight: '200',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#wardGeoId"), wardData, wardGeoDataConfig, []);
			
		
		
		var configOwnerParty = {
			placeHolder: uiLabelMap.SearchByNameOrId,
			key: 'partyId',
			value: 'groupName',
			width: '200',
			dropDownHeight: 200,
			height: 25,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			useUrl: true,
			root: 'listParties',
			url: 'getFacilityOwnerables', 
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
		};
		jLogCommon.initComboBox($("#ownerPartyId"), null, configOwnerParty, []);
		
	};
	
	var initGridPartyList = function(grid){	
		var grid = $("#managerPartyId");
		var datafield =  [
		                  { name: 'employeePartyId', type: 'string'},
		                  { name: 'employeePartyCode', type: 'string'},
		                  { name: 'fullName', type: 'string'},
		                  { name: 'emplPositionTypeId', type: 'string'},
		                  { name: 'emplPositionId', type: 'string'},
		                  { name: 'description', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeId, datafield: 'employeePartyCode', align: 'left', width: 120, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<span>' + value + '</span>';
		                		  }
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeName, datafield: 'fullName', align: 'left', minwidth: 100, pinned: true, editable: false,},
		                  ];
		var config = {
				width: 400, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'checkbox',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetPartyAndPositionInDepartment&emplPositionTypeId=LOG_STOREKEEPER',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	
	var initEvents = function(){
		$("#countryGeoId").on('change', function(event){
			update({
				geoId: $("#countryGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "PROVINCE",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'provinceGeoId');
		});
		$("#provinceGeoId").on('change', function(event){
			update({
				geoId: $("#provinceGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "DISTRICT",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'districtGeoId');
		});
		
		$("#districtGeoId").on('change', function(event){
			update({
				geoId: $("#districtGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "WARD",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'wardGeoId');
		});
		
		$("#managerPartyId").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (rowData){
	        	description = rowData.fullName + ' ['+rowData.employeePartyCode+']';
	        }
	        var rows = $("#managerPartyId").jqxGrid('getselectedrowindexes');
	        if (rows.length > 1){
	        	description = description + ", ... (" + rows.length + " " + uiLabelMap.BLEmployee + ")";
	        }
	        
	        var check = false;
	        for (var x in listStoreKeeperParty){
				if (rowData.employeePartyId == listStoreKeeperParty[x].partyId){
					check = true; 
					break;
				}
			}
	        if (!check){
	        	var obj = {
	        			partyId: rowData.employeePartyId,
	        	}
	        	listStoreKeeperParty.push(obj);
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#storekeeperId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#managerPartyId").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			var rows = $("#managerPartyId").jqxGrid('getselectedrowindexes');
			var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.fullName + ' ['+rowData.employeePartyCode+']';
			} else if (rows.length > 1) {
				description = rowData.fullName + ' ['+rowData.employeePartyCode+'], ... (' + rows.length + ' ' + uiLabelMap.BLEmployee +')';
			}
			for (var x in listStoreKeeperParty){
				if (rowData.employeePartyId == listStoreKeeperParty[x].partyId){
					listStoreKeeperParty.splice(0,1);
				}
			}
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#storekeeperId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#alterSave").click(function () {
			if (facilityDT === null || facilityDT === undefined){
				var facilityIdExisted = false;
				jQuery.ajax({
			        url: "checkFacilityIdExisted",
			        type: "POST",
			        data: {
			        	facilityId: $('#facilityId').val(),
			        },
			        dataType: "json",
			        async: false,
			        success: function(res) {
			        	if (res.hasExisted == true){
			        		facilityIdExisted = true;
			        	} else {
			        		facilityIdExisted = false;
			        	}
			        }
			    });
				if (facilityIdExisted == true){
					bootbox.dialog(uiLabelMap.FacilityIdExisted, [{
	                    "label" : uiLabelMap.OK,
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    }]
	                );
	                return false;
				}
			}
	    	var row;
	    	var fromDate;
	    	var thruDate;
	    	var fromDateManager;
	    	var thruDateManager;
	    	var openedDate;
	    	if ($('#fromDate').jqxDateTimeInput('getDate')){
	    		fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
	    	}
	    	if ($('#thruDate').jqxDateTimeInput('getDate')){
	    		thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
	    	}
	    	if ($('#fromDateManager').jqxDateTimeInput('getDate')){
	    		fromDateManager = $('#fromDateManager').jqxDateTimeInput('getDate').getTime();
	    	}
	    	if ($('#thruDateManager').jqxDateTimeInput('getDate')){
	    		thruDateManager = $('#thruDateManager').jqxDateTimeInput('getDate').getTime();
	    	}
	    	if ($('#openedDate').jqxDateTimeInput('getDate')){
	    		openedDate = $('#openedDate').jqxDateTimeInput('getDate').getTime();
	    	}
	    	var path = "";
	    	if ($('#imagesPath')[0]){
	    		var file = $('#imagesPath')[0].files[0];
	    		if (file){
	    			var dataResourceName = file.name;
		    		if (dataResourceName.length > 50){
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                }]
			            );
			            return false;
					}
	    			var folder = "/baseLogistics/facility";
					var form_data= new FormData();
					form_data.append("uploadedFile", file);
					form_data.append("folder", folder);
					jQuery.ajax({
						url: "uploadImages",
						type: "POST",
						async: false,
						data: form_data,
						cache : false,
						contentType : false,
						processData : false,
						
						success: function(res) {
							path = res.path;
				        }
					}).done(function() {
					});
	    		} else {
	    			path = pathScanFile;
	    		}
			} else {
				path = pathScanFile;
			}
	    	var productStoreItems = $("#productStoreId").jqxComboBox('getCheckedItems');
	    	var productStoreIds = [];
	    	for (var j = 0; j < productStoreItems.length; j ++){
	    		var item = {};
	    		item["productStoreId"] = productStoreItems[j].value; 
	    		productStoreIds.push(item);
	    	}
	    	productStoreIds = JSON.stringify(productStoreIds);
	    	var listStoreKeepers = JSON.stringify(listStoreKeeperParty);
	        row = { 
        		facilityId: facilityDT.facilityId,
        		fromDate: fromDate,
        		fromDateManager: fromDateManager,
        		ownerPartyId:$('#ownerPartyId').val(),
        		listStoreKeepers: listStoreKeepers,
        		facilityName:escapeHtml($('#facilityName').val()),
        		facilityCode:$('#facilityCode').val(),
        		parentFacilityId:$('#parentFacilityId').val(),
        		facilitySize:$('#facilitySizeAdd').val(),
        		facilitySizeUomId:$('#facilitySizeUomId').val(),
        		facilityTypeId: 'WAREHOUSE',
        		periodTypeId:$('#periodTypeId').val(),
        		thruDate: thruDate,            
        		thruDateManager: thruDateManager,
        		countryGeoId: $('#countryGeoId').val(),
        		provinceGeoId: $('#provinceGeoId').val(),
        		districtGeoId: $('#districtGeoId').val(),
        		wardGeoId: $('#wardGeoId').val(),
        		requireLocation: $('#requireLocation').val(),
        		requireDate: $('#requireDate').val(),
        		phoneNumber: $('#phoneNumber').val(),
        		address: escapeHtml($('#address').val()),
        		description: escapeHtml($('#description').val()),
        		openedDate: openedDate,
        		imagesPath: path,
        		listProductStoreId: productStoreIds,
	        };
	        if($('#alterpopupWindow').jqxValidator('validate')){
	        	if (facilityDT){
	        		bootbox.dialog(uiLabelMap.AreYouSureSave, 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					    "callback": function() {bootbox.hideAll(); btnClick = false;}
					}, 
					{"label": uiLabelMap.CommonSave,
					    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					    "callback": function() {
					    	if (!btnClick){
						    	Loading.show('loadingMacro');
						    	setTimeout(function(){		
						    		jQuery.ajax({
					    		        url: "updateFacility",
					    		        type: "POST",
					    		        data: row,
					    		        dataType: "json",
					    		        async: false,
					    		        success: function(res) {
					    		        	$("#jqxgrid").jqxGrid('updatebounddata');
					    					if ('' != curFacilityId && curFacilityId != null && curFacilityId != undefined){
					    						loadFacilityDetailByFacilityId(curFacilityId);
					    						if (res._ERROR_MESSAGE_ === uiLabelMap.BLNotifyFacilityCodeExists) {
					    							jOlbUtil.alert.error(res._ERROR_MESSAGE_);
					    						} else {
					    							$("#notifyId").jqxNotification("open");
					    						}
					    		        	} else {
					    		        		displayEditSuccessMessage('jqxgrid');
					    		        	}
					    		        }
					    		    });
					        		$("#jqxgrid").jqxGrid('clearSelection');                        
							        $("#jqxgrid").jqxGrid('selectRow', 0);  
							        $("#alterpopupWindow").jqxWindow('close');
							        window.location.reload();
						    	Loading.hide('loadingMacro');
						    	}, 500);
						    	btnClick = true;
					    	}
					    }
					}]);	
	        	} else {
	        		bootbox.dialog(uiLabelMap.AreYouSureCreate, 
    				[{"label": uiLabelMap.CommonCancel, 
    					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
    				    "callback": function() {bootbox.hideAll();}
    				}, 
    				{"label": uiLabelMap.CommonSave,
    				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
    				    "callback": function() {
    				    	Loading.show('loadingMacro');
    				    	setTimeout(function(){		
    				    		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    							$("#jqxgrid").jqxGrid('clearSelection');                        
    					        $("#jqxgrid").jqxGrid('selectRow', 0);  
    					        $("#alterpopupWindow").jqxWindow('close');
    				    	Loading.hide('loadingMacro');
    				    	}, 500);
    				    }
    				}]);	
	        	}
	        }
	    });
		$('#alterpopupWindow').on('close', function (event) {
	    	facilityDT = null;
	    	Grid.clearForm($('#alterpopupWindow'));
			$('#alterpopupWindow').jqxValidator('hide');
			$("#countryGeoId").val('VNM');
			$("#facilitySizeUomId").val('AREA_m2');
			if ('' != curFacilityId && curFacilityId != null && curFacilityId != undefined){
				var arrayTmp = new Array();
				for (var i = 0; i < facilityData.length; i ++){
					if (facilityData[i].facilityId == curFacilityId){
						arrayTmp.push(facilityData[i]);
					}
				}
				$("#parentFacilityId").jqxDropDownList({ source: arrayTmp, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
				$("#parentFacilityId").val(curFacilityId);
			} else {
				$("#parentFacilityId").jqxDropDownList({ source: facilityData, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
			}
			$("#fromDate").val(new Date());
			$("#fromDateManager").val(new Date());
			$("#address").val("");
			$("#description").val("");
			removeFile();
			$("#productStoreId").jqxComboBox('uncheckAll');  	
		}); 
		
		$("#menuForFacility").on('itemclick', function (event) {
			if (facilitySelected){
				var data = facilitySelected;
				var tmpStr = $.trim($(args).text());
				if(tmpStr == uiLabelMap.Location){
					window.location.href = "getLocations?facilityId=" + data.facilityId;
				}else if(tmpStr == uiLabelMap.Inventory){
					window.location.href = "getInventory?facilityId=" + data.facilityId;
				} else if(tmpStr == uiLabelMap.Edit){
					getFacility(data.facilityId,false);
				} else if(tmpStr == uiLabelMap.ViewDetailInNewPage){
					window.open("detailFacility?facilityId=" + data.facilityId, '_blank');
				} else if(tmpStr == uiLabelMap.BSViewDetail){
					window.location.href = "detailFacility?facilityId=" + data.facilityId;
				} else if (tmpStr == uiLabelMap.CommonDelete){	
					deleteFacility(data.facilityId);
				} else if(tmpStr == uiLabelMap.BSRefresh){
					$('#jqxgrid').jqxGrid('updatebounddata');
				}
			}
		});
		
		$('#jqxgrid').on('rowclick', function (event) {
		    var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
		    facilitySelected = data;
		}); 

	};
	
	var deleteFacility = function (facilityId){
		bootbox.dialog(uiLabelMap.AreYouSureDelete, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll(); btnClick = false;}
		}, 
		{"label": uiLabelMap.CommonSave,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	if (!btnClick){
		    		Loading.show('loadingMacro');
			    	setTimeout(function(){
						jQuery.ajax({
					        url: "logisticsDeleteFacility",
					        type: "POST",
					        data: {
					        	facilityId: facilityId
					        },
					        async: false,
					        success: function(res) {
					        	if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
									jOlbUtil.alert.error(uiLabelMap.CheckLinkedData);
									return false;
								} else {
									$('#jqxgrid').jqxGrid('updatebounddata');
								}
					        }
					    });
						Loading.hide('loadingMacro');
			    	}, 300);
			    	btnClick = true;
		    	}
		    }
		}]);
	};
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	var initValidateForm = function(){
		$('#alterpopupWindow').jqxValidator({
			position: 'bottom',
			rules: [
                   {
                       input: '#thruDateManager', message: uiLabelMap.FromDateMustBeLesserThanThruDate, action: 'valueChanged', rule: function (input, commit) {
                           var fromDate = $('#fromDateManager').jqxDateTimeInput('value');
                           var thruDate = $('#thruDateManager').jqxDateTimeInput('value');
                           if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
                        	   return true;
                           }
                           if(fromDate > thruDate){
                        	   return false;
                           }
                           return true;
                       }
                   },
                   {
                       input: '#thruDate', message: uiLabelMap.FromDateMustBeLesserThanThruDate, action: 'valueChanged', rule: function (input, commit) {
                           var fromDate = $('#fromDate').jqxDateTimeInput('value');
                           var thruDate = $('#thruDate').jqxDateTimeInput('value');
                           if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
                        	   return true;
                           }
                           if(fromDate > thruDate){
                        	   return false;
                           }
                           return true;
                       }
                   },
                   {input: '#facilityId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'keyup, blur', rule: 
                       function (input, commit) {
	                    	var value = $(input).val();
	            			if(value && !(/^[a-zA-Z0-9_]+$/.test(value))){
	            				return false;
	            			}
	            			return true;
                   		}
                   },
                   {input: '#facilitySizeAdd', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'valueChanged', rule: 
                       function (input, commit) {
	                    	var value = $(input).val();
	            			if(value && value < 0){
	            				return false;
	            			}
	            			return true;
                   		}
                   },
             	   {
           				input: '#facilityId', 
           	            message: uiLabelMap.FieldRequired,
           	            action: 'keyup, blur', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#facilityId').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#ownerPartyId', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'change', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#ownerPartyId').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#fromDate', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'valueChanged', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#fromDate').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#facilityName', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'keyup, blur', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#facilityName').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#facilityCode', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'keyup, blur', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#facilityCode').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#fromDateManager', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'ValueChanged', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#fromDateManager').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#productStoreId', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'change', 
           	            rule: function (input) {	
           	            	var items = $("#productStoreId").jqxComboBox('getCheckedItems');
           	            	if (items.length > 0){
           	            		return true;
           	            	} else {
           	            		return false;
           	            	}
           	            }
           			},
           			{
           				input: '#address', 
           	            message: uiLabelMap.FieldRequired, 
           	            action: 'keyup, blur', 
           	            rule: function (input) {	
           	         	   	var tmp = $('#address').val();
           	                return tmp ? true : false;
           	            }
           			},
           			{
           				input: '#phoneNumber', 
           	            message: uiLabelMap.PhoneNumberMustInOto9, 
           	            action: 'valueChanged', 
           	            rule: function (input) {	
           	            	var value = $(input).val();
	            			if(value && !(/^[0-9]+$/.test(value))){
	            				return false;
	            			}
    	           			return true;
           	            }
           			},
                ]
		});
	};
	function removeFile(){
		pathScanFile = null;
		$('#idImagesPath').html("");
		$('#idImagesPath').append("<input type='file' id='imagesPath' name='uploadedFile' class='green-label'/>");
		$('#id-input-file-1 , #imagesPath').ace_file_input({
			no_file: uiLabelMap.NoFile + '...',
			btn_choose: uiLabelMap.Choose,
			btn_change: uiLabelMap.Change,
			droppable:false,
			onchange:null,
			thumbnail:false
		});
	}
	
	var getFacility = function getFacility(facilityId,isParent){
		jQuery.ajax({
			url: "getFacilityDetail",
			type: "POST",
			dataType: "json",
            async: false,
			data: {
				facilityId: facilityId,
			},
			success: function(res){
				facilityDT = res;
			}
		});
		$('#facilityId').val(facilityId);
		$('#requireLocation').val(facilityDT.requireLocation);
		$('#requireDate').val(facilityDT.requireDate);
		$('#glFacilityId').val(facilityId);
		$('#facilityId').prop('disabled', true);
		$('#facilityName').val(facilityDT.facilityName);
		$('#facilityCode').val(facilityDT.facilityCode);
		$('#fromDate').val(facilityDT.ownFromDate);
		$('#thruDate').val(facilityDT.ownThruDate);
		
		// TODO select productStore
		var listProductStoreIdDT = facilityDT.listProductStoreId;
		if(listProductStoreIdDT != undefined){
			for (var i = 0; i < listProductStoreIdDT.length; i ++){
				$("#productStoreId").jqxComboBox('checkItem', listProductStoreIdDT[i]);
			}
		}else{
			$("#productStoreId").jqxComboBox('checkItem', null);
		}
		
		$('#fromDateManager').val(facilityDT.manageFromDate);
		$('#thruDateManager').val(facilityDT.manageThruDate);
		$('#contryGeoId').val(facilityDT.contryGeoId);
		$('#provinceGeoId').val(facilityDT.provinceGeoId);
		$('#districtGeoId').val(facilityDT.districtGeoId);
		$('#wardGeoId').val(facilityDT.wardGeoId);
		$('#address').val(unescapeHTML(facilityDT.address1));
		$('#phoneNumber').val(facilityDT.phoneNumber);
		$('#description').val(facilityDT.description);
		
		var listFacilityParty = facilityDT.listFacilityParty;
		var rows = $("#managerPartyId").jqxGrid("getrows");
		if (listFacilityParty && rows.length > 0){
			for (c in rows){
				for (var x in listFacilityParty){
					if (rows[c].employeePartyId == listFacilityParty[x].partyId && listFacilityParty[x].roleTypeId == 'LOG_STOREKEEPER'){
						var id = rows[c].uid;
						var index = $("#managerPartyId").jqxGrid("getrowboundindexbyid", id);
						$("#managerPartyId").jqxGrid("selectrow", index);
					}
				}
			}
		}
		
		var obj2 = {
			groupName: facilityDT.ownerName,
			partyId: facilityDT.ownerPartyId
		};
		$("#ownerPartyId").jqxComboBox('selectItem', obj2.partyId);
		
		$('#openedDate').val(facilityDT.openedDate);
		if (facilityDT.parentFacilityId){
			var tmpFac = [];
			var listChilds = getAllFacilityRelate(facilityDT.facilityId, facilityData);
			for (var m = 0; m < facilityData.length; m ++){
				if (facilityData[m].facilityId != curFacilityId){
					var check = false;
					for (var n = 0; n < listChilds.length; n ++){
						if (listChilds[n].facilityId == facilityData[m].facilityId){
							check = true;
							break;
						}
					}
					if (check == false){
						tmpFac.push(facilityData[m]);
					}
				}
			}
			$("#parentFacilityId").jqxDropDownList({ source: tmpFac, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
			$('#parentFacilityId').val(facilityDT.parentFacilityId);
		} else {
			if (isParent){
				$('#parentFacilityId').jqxDropDownList('clear');
				var arrayTmp = new Array();
				var facTmp = curFacilityId;
				for (var i = 0; i < facilityData.length; i ++){
					if (facilityData[i].facilityId != facTmp){
						arrayTmp.push(facilityData[i]);
					}
				}
				$("#parentFacilityId").jqxDropDownList({ source: arrayTmp, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
			} else {
				$('#parentFacilityId').jqxDropDownList('clear');
				var arrayTmp = new Array();
				for (var i = 0; i < facilityData.length; i ++){
					if (facilityData[i].facilityId != facilityId){
						arrayTmp.push(facilityData[i]);
					}
				}
				$("#parentFacilityId").jqxDropDownList({ source: arrayTmp, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '200', height: '25'});
			}
		}
		$('#facilitySizeAdd').val(facilityDT.facilitySize);
		$('#facilitySizeUomId').val(facilityDT.facilitySizeUomId);
		$('#alterpopupWindow').jqxWindow('open');
		if (facilityDT.imagesPath){
			$('#idImagesPath').html("");
			$('#idImagesPath').append("<a href='"+facilityDT.imagesPath+"' onclick='' target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.Avatar+"</a> <a onclick='FacilityObj.removeFile()'><i class='fa-remove'></i></a>");
		}
		pathScanFile = facilityDT.imagesPath;
	}
	var listAllChild = [];
	function getAllFacilityRelate(facilityFromId, facilityData){
		var listChilds = [];
		for (var i = 0; i < facilityData.length; i ++){
			if (facilityData[i].parentFacilityId == facilityFromId){
				listChilds.push(facilityData[i]);
				listAllChild.push(facilityData[i]);
			}
		}
		if (listChilds.length > 0){
			for (var j = 0; j < listChilds.length; j ++){
				getAllFacilityRelate(listChilds[j].facilityId, facilityData);	
			}
		}
        return listAllChild;
	}
	
	 var entityMap = {
	    "&": "&amp;",
	    "<": "&lt;",
	    ">": "&gt;",
	    '"': '&quot;',
	    "'": '&#39;',
	    "/": '&#x2F;'
	 };

	 function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	 
	var prepareCreateFacility = function (parentFacilityId){
		var href = "prepareCreateFacility";
		if (parentFacilityId){
			href = "prepareCreateFacility?parentFacilityId=" + parentFacilityId;
		}
		window.location.href = href;
	};
	
	return {
		init: init,
		getFacility: getFacility,
		prepareCreateFacility: prepareCreateFacility,
		removeFile: removeFile,
	};
}());