$.getScript( "/logresources/js/facility/addLocationFacilityChildren.js", function( data, textStatus, jqxhr ) {
});

$.getScript( "/logresources/js/facility/updateProductInLocation.js", function( data, textStatus, jqxhr ) {
});

$(function(){
	LocationFacilityObj.init();
});

var LocationFacilityObj = (function(){
	var init = function(){
		initInput();
		initEvents();
		initValidateForm();
		renderTreeGridLocationFacility();
		getLocationTypeParent();
	};
	
	var updateMode = false;
	var rowKey = null;
	
	// MOVE PRODUCT FROM LOCATION TO LOCATION
	var listlocationFacilityTemp = [];
	var arrayLocationIdFrom = [];
	var arrayLocationIdTo = [];
	var moveMode = false;
	
	var initInput = function(){
		// KHOI TAO POPUP AND INPUT
		$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow({theme: 'olbius',
		    width: 500, maxWidth: 1200, height: 280, minHeight: 200, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderFacilityLocationArea"), modalOpacity: 0.7
		});
		$('#locationDescription').jqxInput({width: 195, height: '20'});
		$('#txtFacility').jqxInput({width: 195, height: '20'});
		$('#txtLocationCode').jqxInput({width: 195, height: '20'});
		//$("#txtLocationType").jqxDropDownList({ source: arrayLocationTypeParent, placeHolder: uiLabelMap.PleaseSelectTitle, displayMember: 'locationFacilityTypeId', valueMember: 'locationFacilityTypeId', autoDropDownHeight: true , width: '200', height: '25', theme: 'olbius'});
		
		// KHOI TAO POPUP AND INPUT UPDATE LOCATION FACILITY
		$("#jqxwindowEditor").jqxWindow({theme: 'olbius',
		    width: 500, maxWidth: 1200, minHeight: 100, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
		});
		$('#txtLocationCodeEditor').jqxInput({theme: 'olbius', width: 200, height: '20'});
	};
	
	
	var initValidateForm = function(){
		//VALIDATE ADD LOCATION FACILITY
		$('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator({
	        rules: [
						{ input: '#txtLocationType', message: uiLabelMap.FieldRequired, action: 'change', 
							rule: function (input, commit) {
								var value = $("#txtLocationType").val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: '#txtLocationCode', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
						{ input: '#txtLocationCode', message: uiLabelMap.DuplicateLocationCode, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCode").val();
								if (_.indexOf(arrayLocationCodeAvalible, value) != -1) {
									return false;
								}
								return true;
							}
						},
						{ input: '#txtLocationCode', message: uiLabelMap.ContainSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCode").val();
								if (!value.containSpecialChars()) {
									return true;
								}
								return false;
							}
						}
	               ]
	    });
		
		//VALIDATE UPDATE LOCATION FACILITY
		$('#jqxwindowEditor').jqxValidator({
	        rules: [
						{ input: '#txtLocationCodeEditor', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
						{ input: '#txtLocationCodeEditor', message: uiLabelMap.DuplicateLocationCode, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCodeEditor").val();
								if (_.indexOf(arrayLocationCodeAvalible, value) != -1) {
									return false;
								}
								return true;
							}
						},
						{ input: '#txtLocationCodeEditor', message: uiLabelMap.ContainSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCodeEditor").val();
								if (!value.containSpecialChars()) {
									return true;
								}
								return false;
							}
						}
	               ]
	    });
	};
	
	var initEvents = function(){
		// ADD LOCATION FACILITY
		$("#alterSaveAdderFacilityLocationArea").click(function () {
			if ($('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator('validate')) {
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
				    		var data = {};
							var description = $("#locationDescription").val();
							description = description.trim();
							data.facilityId = facilityIdGlobal;
							data.parentLocationId = null;
							data.locationCode = $("#txtLocationCode").val();
							data.description = description;
							data.locationFacilityTypeId = $("#txtLocationType").val();
							$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow('close');
							$("#treeGrid").jqxTreeGrid('addRow', null, data, 'last');
							$("#treeGrid").jqxTreeGrid('updateBoundData');
							Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);	
			}
		});
		
		$("#jqxwindowPopupAdderFacilityLocationArea").on('open', function (event) {
			$("#txtFacility").jqxInput({disabled: true });
			disableScrolling();
		});
		
		$("#jqxwindowPopupAdderFacilityLocationArea").on('close', function (event) {
			enableScrolling();
			$('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator('hide');
		});
		
		$("#btnReset").click(function () {
			listlocationFacility = JSON.parse(listlocationFacilityTemp);
			renderTreeGridLocationFacility();
			reset();
			arrayLocationIdFrom = [];
			arrayLocationIdTo = [];
			moveMode = false;
			updateMode = false;
			$("#taskBarMove").css("display", "none");
			if (isNeedReposition) {
				$("#divUpdateProductTo").css("display", "block");
			}
		});
		
		// EVENT UPDATE LOCATION FACILITY
		$("#jqxwindowEditor").on('open', function (event) {
			disableScrolling();
		});
		$("#jqxwindowEditor").on('close', function (event) {
			setTimeout(function(){
	    		$("#viewProduct").attr('disabled', true);
	    		$("#addProduct").attr('disabled', true);
	    		$("#moveProductTo").attr('disabled', true);
	    		$("#btnCancelReset").attr('disabled', true);
	    	}, 500);
			bindArrayLocationCode();
			enableScrolling();
			$('#jqxwindowEditor').jqxValidator('hide');
		});
		
		$("#saveEdit").click(function () {
			if ($('#jqxwindowEditor').jqxValidator('validate')) {
				bootbox.dialog(uiLabelMap.AreYouSureSave, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){		
				    		var txtLocationCodeEditor = $('#txtLocationCodeEditor').val();
							var tarDescriptionEditor = $('#tarDescriptionEditor').val().replaceAll("<div><br></div>"," ");
							updateDescription(txtLocationCodeEditor, tarDescriptionEditor);
							$("#jqxwindowEditor").jqxWindow('close');
				    	Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);	
			}
		});
		
		// EVENT ROWSELET JQX TREE GIRD
		$('#treeGrid').on('rowSelect', function (event){
	    	if(contextMenuGirdLocationFacility)contextMenuGirdLocationFacility.jqxMenu('close');
	    	var args = event.args;
	        rowKey = args.key;
		});
	    $('#treeGrid').on('rowCheck', function (event){
	    	$("#viewProduct").attr('disabled', false);
			$("#addProduct").attr('disabled', false);
			$("#moveProductTo").attr('disabled', false);
			$("#btnCancelReset").attr('disabled', false);
			$("#moveProduct").attr('disabled', false);
			$("#btnReset").attr('disabled', false);
		});
	    $('#treeGrid').on('rowUncheck', function (event){
	    	$("#viewProduct").attr('disabled', true);
			$("#addProduct").attr('disabled', true);
			$("#moveProductTo").attr('disabled', true);
			$("#btnCancelReset").attr('disabled', true);
			$("#moveProduct").attr('disabled', true);
			$("#btnReset").attr('disabled', true);
		});
	    
	    //EVENT CLOSE MENE TREE GIRD
	    $("#contextMenuGirdLocationFacility").on('closed', function () {
			contextMenuGirdLocationFacility.jqxMenu('destroy');
		});
	    
	 // EVENT ROWSELET JQX TREE GIRD
		$("#treeGrid").on('contextmenu', function (e) {
		    return false;
		});
	    $("#treeGrid").on('rowClick', function (event) {
	        var args = event.args;
	        if (args.originalEvent.button == 2) {
	            setTimeout(function() {
	            	renderMenu(event);
				}, 0);
	            return false;
	        }
	    });
	};
	
	function createLocationFacilityAjax(data) {
    	var result = {};
    	var locationId;
    	var success = true;
    	$.ajax({
    		  url: "createLocationFacilityAjax",
    		  type: "POST",
    		  data: data,
    		  dataType: "json",
    		  async: false,
    		  success: function(res) {
    			  locationId = res["locationId"];
    		  }
    	}).done(function() {
    		getGeneralQuantity();
    		$('#jqxNotificationNested').jqxNotification('closeLast');
    		if (locationId == undefined) {
    			success = false;
    			$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text(uiLabelMap.UpdateError);
              	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'}); 
              	$("#notificationContentNested").text(uiLabelMap.wgaddsuccess);
              	$("#jqxNotificationNested").jqxNotification("open");
			}
    	});
    	result.locationId = locationId;
    	result.success = success;
    	return result;
    }
	
	//RENDER TREE GRID LOCATION FACILITY
	var renderTreeGridLocationFacility = function() {
    	var source =
        {
            dataType: "json",
            dataFields: [
					{ name: 'locationId', type: 'string' },         
					{ name: 'facilityId', type: 'string' }, 
					{ name: 'productId', type: 'number' }, 
					{ name: 'quantity', type: 'number' }, 
					{ name: 'locationCode', type: 'string' },
					{ name: 'parentLocationId', type: 'string' },
					{ name: 'locationFacilityTypeId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'locationStatus', type: 'string' },
            ],
            hierarchy:
            {
                keyDataField: { name: 'locationId' },
                parentDataField: { name: 'parentLocationId' }
            },
            id: 'locationId',
            localdata: listlocationFacility,
             addRow: function (rowID, rowData, position, parentID, commit) {
            	 var result = createLocationFacilityAjax(rowData);
            	 if (result.success) {
            		 rowData.locationId = result.locationId;
            		 listlocationFacility.push(rowData);
            		 bindArrayLocationCode();
            		 if (warningMode) {
            			 setTimeout(function() {
            				 location.reload();
						}, 100);
					}
				}
            	 commit(result.success);
                 newRowID = rowID;
             },
             updateRow: function (rowID, rowData, commit) {
            	 commit(true);
             },
             deleteRow: function (rowID, commit) {
            	 
            	 commit(true);
             }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#treeGrid").jqxTreeGrid({
            source: dataAdapter,
            localization: getLocalization(),
            width: '100%',
            sortable: true,
            theme: 'olbius',
            columnsReorder: true,
            checkboxes: true,
            autoRowHeight: false,
            altRows: true,
            pageable: true,
            pagerMode: 'advanced',
            selectionMode: 'multipleRows',
            hierarchicalCheckboxes: true,
            rendered: function () {
            	if (isNeedReposition) {
        			$('#divLocationStatus').text(uiLabelMap.NeedReposition);
        			if (!updateMode && !moveMode) {
        				$('#divUpdateProductTo').css('display', 'block');
        			}
        		}else {
        			$('#divLocationStatus').text('');
        			$('#divUpdateProductTo').css('display', 'none');
        		}
            },
            columns: [ 
                      { text: uiLabelMap.Location, dataField: 'locationCode', align: 'left', pinned: true, width: 500, cellclassname: cellclassnameNeedReposition,
                    	  cellsrenderer: function (row, column, value, rowData) {
                    		  var productId = rowData.productId;
                    		  var quantity = rowData.quantity;
                    		  if(productId != null){
                    			  return '<span>' + value +" ( " + uiLabelMap.Contain +": "+productId+ " ("+uiLabelMap.LogProduct+") "+uiLabelMap.QuantitySumTotal+": "+quantity+" )"+ '</span>';
                    		  }else{
                    			  return '<span>' + value + '</span>';
                    		  }
                    	  }
                      }, 
                      { text: uiLabelMap.LocationType, dataField: 'locationFacilityTypeId', align: 'left', width: 250, cellclassname: cellclassnameNeedReposition,
                      },
                      { text: uiLabelMap.Description	, dataField: 'description', align: 'left', minWidth: 150, cellclassname: cellclassnameNeedReposition },
                    ]
        });
	};
	
	var listlocationFacility;
    function getListlocationFacility(facilityId) {
    	$.ajax({
    		url: "getLocationFacilityAjax",
    		type: "POST",
    		data: {facilityId: facilityId},
    		dataType: "json",
    		success: function(res) {
    			listlocationFacility = res["listlocationFacility"];
    		}
    	}).done(function() {
    		if (listlocationFacility.length == 0) {
    		}
    		renderTreeGridLocationFacility();
    		getLocationHasChild();
    		bindArrayLocationCode();
    	});
    }
    
    // checkHasChild
    function checkHasChild(locationId) {
		for ( var x in listlocationFacility) {
			var parentLocationId = listlocationFacility[x].parentLocationId;
			if (parentLocationId == null) {
				continue;
			}
			if (parentLocationId == locationId) {
				return false;
			}
		}
		return true;
	}
    
	// checkChildNeedUpdate
    function checkChildNeedUpdate(parentLocationId) {
    	var result = false;
		for ( var x in listlocationFacility) {
			var thisParentLocationId = listlocationFacility[x].parentLocationId;
			if (thisParentLocationId == parentLocationId) {
				var locationId = listlocationFacility[x].locationId;
		    	if (!LocationFacilityObj.checkHasChild(locationId)) {
					if (mapHasInventoryInLocation[locationId]) {
						arrayLocationHasProductNeedReposition.push(locationId);
						arrayLocationHasProductNeedReposition = _.uniq(arrayLocationHasProductNeedReposition);
						result = true;
					}else {
						if (!result) {
							result = checkChildNeedUpdate(locationId);
						}
					}
				}
			}
		}
		return result;
	}
    
    var isNeedReposition = false;
    var arrayLocationHasProductNeedReposition = [];
    var cellclassnameNeedReposition = function (row, column, value, data) {
    	var locationId = data.locationId;
    	if (!LocationFacilityObj.checkHasChild(locationId)) {
			if (mapHasInventoryInLocation[locationId]) {
				arrayLocationHasProductNeedReposition.push(locationId);
				arrayLocationHasProductNeedReposition = _.uniq(arrayLocationHasProductNeedReposition);
				isNeedReposition = true;
				return "needReposition";
			}
			if (checkChildNeedUpdate(locationId)) {
	    		isNeedReposition = true;
				return "needReposition";
			}
		}
        return "";
    };
    
    // getLocationHasChild
    var listLocationHasChild = [];
    function getLocationHasChild() {
    	listLocationHasChild = [];
		for ( var g in listlocationFacility) {
			var locationId = listlocationFacility[g].locationId;
			if (!LocationFacilityObj.checkHasChild(locationId)) {
				listLocationHasChild.push(listlocationFacility[g]);
			}
		}
	}
    
    // clearLocationWasLootAllChild
    function clearLocationWasLootAllChild() {
		var listLocationWasLootAllChild = [];
		for ( var x in listlocationFacility) {
			var locationId = listlocationFacility[x].locationId;
			if (_.indexOf(allRowsChecked, locationId) != -1) {
				listLocationWasLootAllChild.push(listlocationFacility[x]);
			}
		}
		listlocationFacility = _.difference(listlocationFacility, listLocationWasLootAllChild);
	}
    
    // getParentLocation
    function getParentLocation(locationId) {
		var parentLocationId = "";
		var originalList = [];
		if (listlocationFacilityTemp.length == 0) {
			originalList = listlocationFacility;
		} else {
			 originalList = JSON.parse(listlocationFacilityTemp);
		}
		for ( var x in originalList) {
			var thisLocationId = originalList[x].locationId;
			if (locationId == thisLocationId) {
				parentLocationId = originalList[x].parentLocationId;
				return parentLocationId;
			}
		}
	}
    
    // getPathArea
    function getPathArea(locationId, parentLocationId, path) {
		if (parentLocationId == 'null' || parentLocationId == null) {
			path = getLocationCode(locationId);
		}else {
			for ( var x in listlocationFacility) {
				var thisLocationId = listlocationFacility[x].locationId;
				if (thisLocationId == parentLocationId) {
					var thisParentLocationId = listlocationFacility[x].parentLocationId;
					path = getLocationCode(locationId);
					path = getPathArea(thisLocationId, thisParentLocationId, path) + " - " + path;
					break;
				}
			}
		}
		
		return path;
	}
    
    // getLocationCode
	function getLocationCode(locationId) {
		var locationCode = "";
		var originalList = [];
		if (listlocationFacilityTemp.length == 0) {
			originalList = listlocationFacility;
		} else {
			 originalList = JSON.parse(listlocationFacilityTemp);
		}
		
		for ( var x in originalList) {
			var thisLocationId = originalList[x].locationId;
			if (thisLocationId == locationId) {
				locationCode = originalList[x].locationCode;
			}
		}
		return locationCode;
	}
    
	// getListChild
	function getListChild(locationId, listChild) {
    	for ( var x in listlocationFacility) {
    		var parentLocationId = listlocationFacility[x].parentLocationId;
    		var thislocationId = listlocationFacility[x].locationId;
    		if (parentLocationId == locationId) {
    			listChild.push(thislocationId);
    			getListChild(thislocationId, listChild);
    		}
    	}
    	return listChild;
    }
    
	//  bindArrayLocationCode
    var arrayLocationCodeAvalible = [];
    function bindArrayLocationCode() {
    	arrayLocationCodeAvalible = [];
		for ( var x in listlocationFacility) {
			arrayLocationCodeAvalible.push(listlocationFacility[x].locationCode);
		}
	}
    
    // RENDER MENU FOR JQXTREE
	var contextMenuGirdLocationFacility;
    function renderMenu(event) {
		if (contextMenuGirdLocationFacility)contextMenuGirdLocationFacility.jqxMenu('close');
    	var scrollTop = $(window).scrollTop();
        var scrollLeft = $(window).scrollLeft();
        var selection = $("#treeGrid").jqxTreeGrid('getSelection');
    	if (selection.length != 0) {
    		var productId = null;
    		for(var i in selection){
    			productId = selection[i].productId;
    		}
    		menuHeight = '58px';
        	var locationFacilityTypeId = selection[selection.length - 1].locationFacilityTypeId;
        	var child = getChildOfLocation(locationFacilityTypeId);
        	var menu = "";
        	menu += "<div id='contextMenuGirdLocationFacility'><ul>";
        	var locationIdParam = selection[selection.length - 1].locationId;
        	var parentLocationIdParam = selection[selection.length - 1].parentLocationId;
        	if (child.length != 0) {
        		menuHeight = '82px';
        		menu += "<li><i class='fa-expand'></i>&nbsp;&nbsp;"+uiLabelMap.DivideLocation+"<ul id='menuAddNew' style='width:250px;'>";
        		for ( var x in child) {
    				var locationName = getLocationFacilityType(child[x]).split("[")[0];
    				menu += "<li style=\"color: #0b6cbc\" locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert=" + child[x] + " ><i class='fa-plus-circle open-sans'></i>&nbsp;&nbsp;"+uiLabelMap.AddChildLocationType+": "+"<b>" + child[x] + "</b></li>";
    				continue;
    			}
        		menu += "</ul></li>";
			}
        	menu += "<li locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert='functionEDIT'><i class='icon-edit'></i>&nbsp;&nbsp;"+uiLabelMap.Edit+"</li>";
        	if(productId == null){
        		menu += "<li locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert='functionDELETE'><i class='icon-trash' style=\"color: rgb(229, 2, 2)\"></i>&nbsp;&nbsp;"+uiLabelMap.CommonRemove+"</li>";
        	}
        	menu += "</ul></div>";
        	$("#menu").html(menu);
		}
        
    	contextMenuGirdLocationFacility = $("#contextMenuGirdLocationFacility").jqxMenu({ theme: 'olbius', width: '150px', height: menuHeight, autoOpenPopup: false, mode: 'popup'});
        contextMenuGirdLocationFacility.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
        
        contextMenuGirdLocationFacility.on('itemclick', function (event){
    	    if (event.args.attributes.typeInsert == undefined) {
				return;
			}
    	    var typeInsert = event.args.attributes.typeInsert.value;
    	    var locationId = event.args.attributes.locationId.value;
    	    var parentLocationId = null;
    	    if (event.args.attributes.parentLocationId){
    	    	parentLocationId = event.args.attributes.parentLocationId.value;
    	    }
    	    if (typeInsert == "functionDELETE") {
    	    	confirmDeleteLocation(locationId);
				return;
			}
    	    if (typeInsert == "functionEDIT") {
    	    	editDescription(locationId);
				return;
			}
    	    $("#titleAdder").text(uiLabelMap.CreateNewLocationType+":" + typeInsert);
    	    AddLocationFacilityChildren.addFacilityLocationAreaInArea(locationId, parentLocationId, typeInsert);
    	});
	}
    
    // Edit Description
    function editDescription(locationId) {
    	locationIdGlobal = locationId;
    	var wtmp = window;
    	var tmpwidth = $('#jqxwindowEditor').jqxWindow('width');
    	$("#jqxwindowEditor").jqxWindow('open');
    	var descriptionEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'description');
    	var locationCodeEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'locationCode');
    	var locationIdEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'locationId');
    	var parentLocationIdEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'parentLocationId');
    	var index = _.indexOf(arrayLocationCodeAvalible, locationCodeEdit);
    	arrayLocationCodeAvalible.splice(index, 1);
    	$('#tarDescriptionEditor').val(descriptionEdit);
    	$('#txtLocationCodeEditor').val(locationCodeEdit);
	}
    
    // Update Description
    function updateDescription(txtLocationCodeEditor, tarDescriptionEditor) {
		var result;
		$.ajax({
  		  url: "updateLocationFacilityAjax",
  		  type: "POST",
  		  data: {locationId: locationIdGlobal, locationCode: txtLocationCodeEditor, description: tarDescriptionEditor},
  		  dataType: "json",
  		  success: function(res) {
  			result = res["success"];
  		  }
	  	}).done(function() {
	  		$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (result) {
	  			getListlocationFacility(facilityIdGlobal);
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text(uiLabelMap.wgupdatesuccess);
              	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text(uiLabelMap.UpdateError);
              	$("#jqxNotificationNested").jqxNotification("open");
			}
	  	});
	}
    
	// DELETE LOCATION FACILITY
	var arrayLocationIdNeedDelete = [];
    function confirmDeleteLocation(locationId) {
    	arrayLocationIdNeedDelete= [];
		if (checkHasChild(locationId)) {
			arrayLocationIdNeedDelete.push(locationId);
			bootbox.dialog(uiLabelMap.AreYouSureDelete, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){		
			    		deleteLocationAjax(arrayLocationIdNeedDelete);
			    	Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);	
		} else {
			var arrayLocationIdFirst = [];
			arrayLocationIdFirst.push(locationId);
			arrayLocationIdNeedDelete = _.uniq(getListChild(locationId, arrayLocationIdFirst));
			bootbox.dialog(uiLabelMap.ConfirmDeleteLocationDetails, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){		
			    		deleteLocationAjax(arrayLocationIdNeedDelete);
			    	Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);	
		}
	}
    
    function deleteLocationAjax(arrayLocationId) {
    	var result;
    	$.ajax({
    		  url: "deleteLocationAjax",
    		  type: "POST",
    		  data: {arrayLocationId: arrayLocationId, facilityId: facilityIdGlobal},
    		  dataType: "json",
    		  async: true,
    		  success: function(res) {
    			  result = res["result"];
    			  setTimeout(function(){
    		    		$("#viewProduct").attr('disabled', true);
    		    		$("#addProduct").attr('disabled', true);
    		    		$("#moveProductTo").attr('disabled', true);
    		    		$("#btnCancelReset").attr('disabled', true);
    		    	}, 500);
    		  }
  	  	}).done(function() {
  	  		getListlocationFacility(facilityIdGlobal);
  	  		getGeneralQuantity();
  	  		checkHasInventoryInLocation();
  	  		reset();
  	  		$('#jqxNotificationNested').jqxNotification('closeLast');
  	  		if (result == "success") {
  	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
            	$("#notificationContentNested").text(uiLabelMap.wgupdatesuccess);
            	$("#jqxNotificationNested").jqxNotification("open");
            	isNeedReposition = false;
  			} else {
  				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
      			$("#notificationContentNested").text(uiLabelMap.UpdateError);
                $("#jqxNotificationNested").jqxNotification("open");
  			}
  	  	});
	}
    
    var arrayLocationTypeParent = [];
	var getLocationTypeParent = function(){
		for ( var x in listLocationFacilityType) {
			var parentLocationFacilityTypeId = listLocationFacilityType[x].parentLocationFacilityTypeId;
			if (parentLocationFacilityTypeId == "") {
				arrayLocationTypeParent.push(listLocationFacilityType[x]);
			}
		}
		return arrayLocationTypeParent;
	}
	
	function addFacilityLocationArea(){
		clearPopup();
		$("#txtFacility").val(facilityIdGlobal);
		$("#txtLocationType").jqxDropDownList({ source: arrayLocationTypeParent, placeHolder: uiLabelMap.PleaseSelectTitle, displayMember: 'locationFacilityTypeId', valueMember: 'locationFacilityTypeId', autoDropDownHeight: true , width: '200', height: '25', theme: 'olbius'});
		var wtmp = window;
		var tmpwidth = $('#jqxwindowPopupAdderFacilityLocationArea').jqxWindow('width');
		$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow('open');
	};
	
	return {
		init: init,
		getListlocationFacility: getListlocationFacility,
		checkHasChild : checkHasChild,
		checkChildNeedUpdate: checkChildNeedUpdate,
		renderMenu: renderMenu,
		editDescription: editDescription,
		updateDescription: updateDescription,
		confirmDeleteLocation: confirmDeleteLocation,
		deleteLocationAjax: deleteLocationAjax,
		addFacilityLocationArea: addFacilityLocationArea,
		getPathArea: getPathArea,
		bindArrayLocationCode: bindArrayLocationCode,
		arrayLocationCodeAvalible: arrayLocationCodeAvalible,
		rowKey: rowKey,
		renderTreeGridLocationFacility: renderTreeGridLocationFacility,
		updateMode: updateMode,
		arrayLocationHasProductNeedReposition: arrayLocationHasProductNeedReposition,
		listlocationFacility: listlocationFacility,
		moveMode: moveMode,
	};
	
}());
