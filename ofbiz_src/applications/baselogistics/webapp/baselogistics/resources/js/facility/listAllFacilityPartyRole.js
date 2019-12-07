$(function(){
	FacilityInfoObj.init();
});
var FacilityInfoObj = (function() {
	var validatorVAL = null;
	var partySelectedId = null;
	var facilitySelectedId = null;
	var listRoleTypeSelectedIds = [];
	var rowDetailObj = null;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		var facilityData = [];
		var partyData = [];
		var roleTypeData = [];
		$("#fromDate").jqxDateTimeInput({width: '300px', height: '25px', theme: theme});
		$("#thruDate").jqxDateTimeInput({width: '300px', height: '25px', theme: theme});
		$("#fromDate").jqxDateTimeInput("clear");
		$("#thruDate").jqxDateTimeInput("clear");
		
		$("#contextMenuParent").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#contextMenuChild").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#AddPartyRoleFacility").jqxWindow({
			maxWidth: 800, minWidth: 300, width: 540, height: 320, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 10000, zIndex: 10000, autoOpen: false, cancelButton: $("#addCancel"), modalOpacity: 0.7, theme:theme           
		});
		$("#partyId").jqxDropDownButton({width: 300, theme: theme}); 
		$('#partyId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#roleTypeId").jqxDropDownButton({width: 300, theme: theme}); 
		$('#roleTypeId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
	};
	
	var initElementComplex = function() {
		initGridFacilityRole();
		initGridParty();
		initGridRoleType();
	};
	
	var initGridRoleType = function(){
		var grid = $("#jqxgridListRoleType");
		var datafield =  [
      		{ name: 'roleTypeId', type: 'string'},
      		{ name: 'description', type: 'string'},
      	];
      	var columnlist = [
          { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			    	return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.Role, datafield: 'description', align: 'left', minwidth: 220, pinned: true, editable: false,
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor: pointer;">' + value + '</div>';
			    }
			},
      	];
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
	        selectionmode: 'checkbox',
	        useUrl: true,
	        url: '',                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridParty = function(){
		var grid = $("#jqxgridListParty");
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
		                		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.CommonPartyId, datafield: 'employeePartyCode', align: 'left', width: 120, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<div style="cursor: pointer;">' + value + '</div>';
		                		  }
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.CommonPartyName, datafield: 'fullName', align: 'left', width: 150, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.Role, dataField: 'description', editable: false, align: 'left', minwidth: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left',
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  ];
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
				url: 'jqGetPartyAndPositionInDepartment',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initEvents = function() {
		$("#contextMenuParent").on('itemclick', function (event) {
			var data = $('#jqxgridFacilityRole').jqxGrid('getRowData', $("#jqxgridFacilityRole").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			facilitySelectedId = data.facilityId;
			if(tmpStr == uiLabelMap.AddRole){
				$("#facilityName").text(data.facilityName);
				getPartyAndPositionInDepartment(data.facilityId);
				openPopupAddRole(data.facilityId);
			}
		});
		
		$("#jqxgridListRoleType").on('rowclick', function (event) {
			var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        if (rowBoundIndex >= 0){
	        	$("#jqxgridListRoleType").jqxGrid('selectrow', rowBoundIndex);
	        }
		});
		
		$("#contextMenuChild").on('itemclick', function (event) {
			bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){
			    		var x = new Date(rowDetailObj.fromDate);
						$.ajax({
				    		url: "removePartyRoleWithFacility",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			facilityId: rowDetailObj.facilityId,
				    			partyId: rowDetailObj.partyId,
				    			roleTypeId: rowDetailObj.roleTypeId,
				    			fromDate: x.getTime(),
				    		},
				    		success: function (res){
				    			$("#jqxgridFacilityRole").jqxGrid('updatebounddata');
				    		}
				    	});
					Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);
		});
		
		$("#jqxgridListParty").on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = $("#jqxgridListParty").jqxGrid('getrowdata', rowBoundIndex);
	        partySelectedId = rowData.employeePartyId;
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+rowData.fullName+ ' - ' + rowData.employeePartyId +'</div>';
	        $('#partyId').jqxDropDownButton('setContent', dropDownContent);
	        $('#roleTypeId').jqxDropDownButton('setContent', null);
		    $("#jqxgridListRoleType").jqxGrid('clear');
		    $("#jqxgridListRoleType").jqxGrid('clearselection');
		    listRoleTypeSelectedIds = [];
	        getRoleTypeByPartyId(rowData.employeePartyId, facilitySelectedId);
	        $("#jqxgridListParty").jqxGrid('selectrow', rowBoundIndex);
	        $("#partyId").jqxDropDownButton('close');
	    });
		
		$("#jqxgridListRoleType").on('rowselect', function (event) {
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var rowData = args.row;
			listRoleTypeSelectedIds.push(rowData);
			var dropDownContent = "";
			if (listRoleTypeSelectedIds.length > 1){
				dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+listRoleTypeSelectedIds[0].description+', ... ('+formatnumber(listRoleTypeSelectedIds.length)+')</div>';
			} else {
				dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+listRoleTypeSelectedIds[0].description+'</div>';
			}
			$('#roleTypeId').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#jqxgridListRoleType").on('rowunselect', function (event) {
		    var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
		    if (rowData){
		    	var roleTypeId = rowData.roleTypeId;
			    for (var i = 0; i < listRoleTypeSelectedIds.length; i ++){
			    	if (listRoleTypeSelectedIds[i].roleTypeId == roleTypeId){
			    		listRoleTypeSelectedIds.splice(i, 1);
		    	    	break;
			    	}
			    }
		    }
		});
		
		$("#jqxgridFacilityRole").on('close', function(){
			$("#jqxgridListParty").jqxGrid('clear');
		    $("#jqxgridListParty").jqxGrid('clearselection');
		    $("#jqxgridListRoleType").jqxGrid('clear');
		    $("#jqxgridListRoleType").jqxGrid('clearselection');
		    $("#fromDate").jqxDateTimeInput("clear");
			$("#thruDate").jqxDateTimeInput("clear");
		});
		
		$("#addSave").on('click', function (event) {
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
				    		var mapfac = {
				    				facilityId: facilitySelectedId,
				    		}
				    		var listFacilities = [];
				    		listFacilities.push(mapfac);
				    		listFacilities = JSON.stringify(listFacilities);
				    		
				    		var mappty = {
				    				partyId: partySelectedId,
				    		}
				    		var listParties = [];
				    		listParties.push(mappty);
				    		listParties = JSON.stringify(listParties);
				    		
				    		var listRoleTypes = [];
				    		for (var i = 0; i < listRoleTypeSelectedIds.length; i ++){
				    			var row = {
				    					roleTypeId: listRoleTypeSelectedIds[i].roleTypeId,
				    			}
				    			listRoleTypes.push(row);
				    		}
				    		listRoleTypes = JSON.stringify(listRoleTypes);
				    		
							$.ajax({
					    		url: "createFacilityParties",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			listFacilities: listFacilities,
					    			listParties: listParties,
					    			listRoleTypes: listRoleTypes,
					    		},
					    		success: function (res){
					    			$("#AddPartyRoleFacility").jqxWindow('close');
					    			$("#jqxgridFacilityRole").jqxGrid('updatebounddata');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		});
	};
	
	var getRoleTypeByPartyId = function(partyId, facilityId){
		var tmpS = $("#jqxgridListRoleType").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=jqGetRoleTypeByPartyIdNotInFacility&facilityId="+facilityId+"&partyId="+partyId;
	    $("#jqxgridListRoleType").jqxGrid('source', tmpS);
	};
	
	var getPartyAndPositionInDepartment = function(){
		var tmpS = $("#jqxgridListParty").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=jqGetPartyAndPositionInDepartment";
	    $("#jqxgridListParty").jqxGrid('source', tmpS);
	};
	
	var openPopupAddRole = function(facilityId){
		$("#facilityId").val(facilityId);
		$("#AddPartyRoleFacility").jqxWindow('open');
	};
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#partyId', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	var parties = $("#jqxgridListParty").jqxGrid('getselectedrowindexes'); 
			    	if (parties.length <= 0){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#roleTypeId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var roles = $("#jqxgridListRoleType").jqxGrid('getselectedrowindexes'); 
					if (roles.length <= 0){
						return false;
					}
					return true;
				}
			},
			{
				input: '#thruDate', 
				message: uiLabelMap.CannotBeforeNow, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
	     		   	var nowDate = new Date();
				   	if ((typeof(thruDate) != 'undefined' && thruDate != null && !(/^\s*$/.test(thruDate)))) {
			 		    if (thruDate < nowDate) {
			 		    	return false;
			 		    }
				   	}
	     		   	return true;
				}
			},
			{
				input: '#thruDate', 
				message: uiLabelMap.MustAfterEffectiveDate, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
					var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
					if ((typeof(thruDate) != 'undefined' && thruDate != null && !(/^\s*$/.test(thruDate)))) {
						if (thruDate < fromDate) {
							return false;
						}
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#fromDate', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($('#initAddFacilityParty'), mapRules, extendRules, {position: 'right'});
	};
    
    var getValidator = function(){
    	return validatorVAL;
    };
    
    var initGridFacilityRole = function(){
		var datafield =  [
      		{ name: 'facilityId', type: 'string'},
      		{ name: 'facilityCode', type: 'string'},
      		{ name: 'facilityName', type: 'string'},
      		{ name: 'rowDetail', type: 'string'},
            { name: 'openedDate', type: 'date', other: 'Timestamp'},
            { name: 'closedDate', type: 'date', other: 'Timestamp'},
      	];
      	var columnlist = [
          { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.FacilityId, datafield: 'facilityCode', align: 'left', width: 200, pinned: true, editable: false,
				cellsrenderer: function (row, column, value) {
			    }
			},
			{ text: uiLabelMap.FacilityName, datafield: 'facilityName', align: 'left', minwidth: 200, pinned: true, editable: false,
				cellsrenderer: function (row, column, value) {
			    }
			},
			{ text: uiLabelMap.OpenedDate, dataField: 'openedDate', editable: false, align: 'left', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function (row, column, value) {
			    }
			},
			{ text: uiLabelMap.ClosedDate, dataField: 'closedDate', editable: false, align: 'left', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function (row, column, value) {
			    }
			},
      	];
      	var rendertoolbar = function (toolbar){
   			toolbar.html("");
   			var id = "jqxgridFacilityRole";
   			var me = this;
   			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.FacilityRoleManagerment + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
   			toolbar.append(jqxheader);
   	     	var container = $('#toolbarButtonContainer' + id);
   	        var maincontainer = $("#toolbarcontainer" + id);
   		}; 
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
	        rowdetails: true,
	        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 222, rowdetailshidden: true },
	        initrowdetails: initrowdetails,
	        useUrl: true,
	        url: 'jqGetAllFacilityParty',                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridFacilityRole"));
      	if (adminPermission) {
      		Grid.createContextMenu($("#jqxgridFacilityRole"), $("#contextMenuParent"), false);
      	}
	};
	
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridFacilityRole'+index);
		var listChilds = [];
		partyAndRoles = [];
		if (datarecord.rowDetail){
			partyAndRoles = datarecord.rowDetail;
		}
		for (var m = 0; m < partyAndRoles.length; m ++){
			var child = partyAndRoles[m];
			var rowDetail = {};
			rowDetail['partyId'] = child.partyId;
			rowDetail['facilityId'] = datarecord.facilityId;
			rowDetail['partyCode'] = child.partyCode;
			rowDetail['fullName'] = child.fullName;
			rowDetail['fromDate'] = child.fromDate;
			rowDetail['thruDate'] = child.thruDate;
			rowDetail['roleTypeId'] = child.roleTypeId;
			rowDetail['description'] = child.description;
			listChilds.push(rowDetail);
		}
		var sourceGridDetail =
	    {
	        localdata: listChilds,
	        datatype: 'local',
	        datafields:
		        [
		        { name: 'facilityId', type: 'string' },
		        { name: 'partyId', type: 'string' },
				{ name: 'partyCode', type: 'string' },
				{ name: 'fullName', type: 'string' },
				{ name: 'roleTypeId', type: 'string' },
				{ name: 'description', type: 'string' },
				{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			 	{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				]
	    };
	    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	    grid.jqxGrid({
	        width: '98%',
	        height: 200,
	        theme: 'olbius',
	        localization: getLocalization(),
	        source: dataAdapterGridDetail,
	        sortable: true,
	        pagesize: 5,
	 		pageable: true,
	 		editable: true,
	 		columnsresize: true,
	        selectionmode: 'singlerow',
	        columns: [{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: uiLabelMap.BLRoles, dataField: 'description', minwidth: 150, filtertype:'input', editable: false,},
						{ text: uiLabelMap.CommonPartyCode, dataField: 'partyCode', width: 200, filtertype:'input', editable: false,
							cellsrenderer: function (row, column, value) {
								var data = grid.jqxGrid('getrowdata', row);
								if (!value) {
									return '<div>' + data.partyId + '</div>';
								}
						    }
						},
						{ text: uiLabelMap.PartyOrg, dataField: 'fullName', width: 200, filtertype:'input', editable: false,},
						{ text: uiLabelMap.BLEffectiveDate, dataField: 'fromDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						},
						{ text: uiLabelMap.BLExpiryDate, dataField: 'thruDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						},
					]
	        });
	    if (adminPermission) {
	    	Grid.createContextMenu(grid, $("#contextMenuChild"), false);
	    }
	    grid.on('rowselect', function(event){
	    	var args = event.args;
			var rowBoundIndex = args.rowindex;
			var rowData = args.row;
	    	rowDetailObj = rowData;
	    });
	    
	    grid.mousedown(function (event) {
            // get the clicked cell.
            var cell = grid.jqxGrid('getcellatposition', event.pageX, event.pageY);
            //select row.
            var item;
            if (cell != null && cell.row != undefined) {
               item = grid.jqxGrid('getrowdata', cell.row);
            }
            var rightClick = isRightClick(event);	
            if (rightClick) {
                var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                if(item){
                	if(item.thruDate){
                    	$("#contextMenuChild").jqxMenu({disabled: true});
                    }else{
                    	$("#contextMenuChild").jqxMenu({disabled: false});
                    }
                }
                return false;
            }
        });
	}
	
	var isRightClick = function(event) {
        var rightclick;
        if (!event) var event = window.event;
        if (event.which) rightclick = (event.which == 3);
        else if (event.button) rightclick = (event.button == 2);
        return rightclick;
    }
	return {
		init: init,
		getValidator: getValidator,
	}
}());