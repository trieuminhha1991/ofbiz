$(function(){
	LocationType.init();
});
var LocationType = (function() {
	var init = function() {
		initElement();
		initEvents();
		initValidateForm();
		var checkUpdate = false;
		var contextMenu = null;
		loadLocationFacilityType();
	};
	
	var initElement = function() {
		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#contentNotification", opacity: 0.9, autoClose: true, template: "success" });
		
		$("#locationFacilityTypeId").jqxInput({height: 23, width: 295}); 
		$("#description").jqxInput({height: 23, width: 295});
		$("#prefixCharacters").jqxInput({height: 23, width: 295, placeHolder: 'a-z A-Z'});
		$("#defaultChildNumber").jqxNumberInput({ width: 300, height: 25, min: 0,  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#parentLocationFacilityTypeId").jqxDropDownList({height: 26, source: locationFacilityTypeDataSoure, width: 300, autoDropDownHeight: true, placeHolder: uiLabelMap.PleaseSelectTitle, displayMember: 'description', valueMember: 'locationFacilityTypeId'});
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 800, minWidth: 550, height:300 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
		});
		$("#jqxTreeGirdLocation").jqxTreeGrid('clearSelection');
		
		contextMenu = $("#Menu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup' });
		
	};
	
	var initValidateForm = function (){
		$('#alterpopupWindow').jqxValidator({
			rules: 
			[
		        { input: '#locationFacilityTypeId', message: uiLabelMap.FieldRequired, action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var locationFacilityTypeId = $('#locationFacilityTypeId').val();
	            	    if(locationFacilityTypeId == ""){
	            	    	return false; 
	            	    }else{
	            	    	return true; 
	            	    }
	            	    return true; 
	        	    }
	            }, 
	            { input: '#prefixCharacters', message: uiLabelMap.FieldRequired, action: 'valueChanged, blur', 
	         	   rule: function () {
	         		    var prefixCharacters = $('#prefixCharacters').val();
	             	    if(prefixCharacters == "" || prefixCharacters == null || prefixCharacters == undefined){
	             	    	return false; 
	             	    }else{
	             	    	return true; 
	             	    }
	             	    return true; 
	         	    }
	             },
	             {input: '#prefixCharacters', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'blur', rule: 
	                 function (input, commit) {
	                 	var value = $(input).val();
	         			if(value && !(/^[a-zA-Z]+$/.test(value))){
	         				return false;
	         			}
	         			return true;
	             	}
	             },
		    ]
		});
	};
	
	var initEvents = function() {
		$("#Menu").on('itemclick', function (event) {
	        var args = event.args;
	        var selection = $("#jqxTreeGirdLocation").jqxTreeGrid('getSelection');
	        var rowid = selection[0].uid;
	        if ($.trim($(args).text()) == uiLabelMap.Edit) {
	        	loadParentLocationType(rowid);
	        	checkUpdate = true;
	        	bindingEditLocationFacilityType(selection[0]);
	        } else if ($.trim($(args).text()) == uiLabelMap.Delete) {
	        	deleteLocationFacilityType(rowid);
	        } else if ($.trim($(args).text()) == uiLabelMap.BSRefresh) {
	        	$("#jqxTreeGirdLocation").jqxTreeGrid('refresh');
	        }
	    });
		
		$("#jqxTreeGirdLocation").on('contextmenu', function () {
			return false;
		});
		
		$("#jqxTreeGirdLocation").on('rowClick', function (event) {
	    	var args = event.args;
	        if (args.originalEvent.button == 2) {
	        	var scrollTop = $(window).scrollTop();
	            var scrollLeft = $(window).scrollLeft();
	            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	            return false;
	        }
	    });
		
		$('#alterpopupWindow').on('close', function (event) { 
			$('#alterpopupWindow').jqxValidator('hide');
			$('#locationFacilityTypeId').jqxInput('clear');
			$('#description').jqxInput('clear');
			$('#prefixCharacters').jqxInput('clear');
			$('#defaultChildNumber').jqxNumberInput('clear');
			$('#locationFacilityTypeId').jqxInput({disabled: false });
			$("#parentLocationFacilityTypeId").jqxDropDownList('clearSelection'); 
		}); 
		
		$("#addButtonSave").click(function () {
			var locationFacilityTypeId = $('#locationFacilityTypeId').val();
			var parentLocationFacilityTypeId = $('#parentLocationFacilityTypeId').val();
			var description = $('#description').jqxInput('val');
			if (description) description = description.split('\n').join(' ');
			
			var prefixCharacters = $('#prefixCharacters').jqxInput('val');
			if (prefixCharacters) prefixCharacters = prefixCharacters.split('\n').join(' ');
			
			var defaultChildNumber = $('#defaultChildNumber').jqxNumberInput('val');
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				if(checkUpdate == true){
					bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
							[{"label": uiLabelMap.CommonCancel, 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }, 
					        {"label": uiLabelMap.OK,
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	updateLocationFacilityTypeSetting(locationFacilityTypeId, parentLocationFacilityTypeId, prefixCharacters, defaultChildNumber, description);
					        }
			        }]);
				}else{
					bootbox.dialog(uiLabelMap.AreYouSureCreate, 
							[{"label": uiLabelMap.CommonCancel, 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }, 
					        {"label": uiLabelMap.OK,
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	addLocationFacilityTypeSetting(locationFacilityTypeId, parentLocationFacilityTypeId, prefixCharacters, defaultChildNumber, description);
					        }
			        }]);
				}
			}
		});
	};
	
	function addLocationFacilityType(){
		checkUpdate = false;
		var locationFacilityTypeId = $('#locationFacilityTypeId').val();
		loadParentLocationType(locationFacilityTypeId);
		$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.CreateNewLocationType);
    	$("#alterpopupWindow").jqxWindow('open');
	}
	
	function deleteLocationFacilityType(locationFacilityTypeId){
		bootbox.dialog(uiLabelMap.AreYouSureDelete, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	$.ajax({
        			  url: "deleteLocationType",
        			  type: "POST",
        			  data: {locationFacilityTypeId: locationFacilityTypeId},
        			  dataType: "json",
        			  success: function(data) {
        				  var value = data["value"];
        				  if(value == "error"){
        					  $("#notificationContent").text(uiLabelMap.CheckLinkedData);
        					  $("#jqxNotification").jqxNotification('open');
        				  }
        				  else{
        					  $("#notificationContent").text(uiLabelMap.NotifiDeleteSucess);
        					  $("#jqxNotification").jqxNotification('open');
        					  loadLocationFacilityType();
        				  }
        			  }    
        		}).done(function(data) {
        		});
            }
		}]);
	}
	
	function loadParentLocationType(locationFacilityTypeId){
		$.ajax({
			url: "loadParentLocationTypes",
			type: "POST",
			data: {locationFacilityTypeId: locationFacilityTypeId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var listParentLocationType = data["listParentLocationTypeMap"];
			$("#parentLocationFacilityTypeId").jqxDropDownList({source: listParentLocationType});
		});
	}
	
	function addLocationFacilityTypeSetting(locationFacilityTypeId, parentLocationFacilityTypeId, prefixCharacters, defaultChildNumber, description){
		$.ajax({
			url: "createLocationType",
			type: "POST",
			data: {
				locationFacilityTypeId: locationFacilityTypeId, 
				parentLocationFacilityTypeId: parentLocationFacilityTypeId, 
				prefixCharacters: prefixCharacters, 
				defaultChildNumber: defaultChildNumber, 
				description: description
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			if (data._ERROR_MESSAGE_){
				jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				return false;
			}
			var value = data["value"];
	    	$('#alterpopupWindow').jqxWindow('close');  
	    	if(value == "error"){
				$("#notificationContent").text(uiLabelMap.NotifiUpdateError);
				$("#jqxNotification").jqxNotification('open');
			}
	    	if(value == "success"){
	    		$("#notificationContent").text(uiLabelMap.CreateSuccessfully);
				$("#jqxNotification").jqxNotification('open');
				loadLocationFacilityType();
	    	}
		});
	}
	
	
	function updateLocationFacilityTypeSetting(locationFacilityTypeId, parentLocationFacilityTypeId, prefixCharacters, defaultChildNumber, description){
		$.ajax({
			url: "updateLocationFacilityType",
			type: "POST",
			data: {
				locationFacilityTypeId: locationFacilityTypeId, 
				parentLocationFacilityTypeId: parentLocationFacilityTypeId, 
				prefixCharacters: prefixCharacters, 
				defaultChildNumber: defaultChildNumber, 
				description: description
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			if (data._ERROR_MESSAGE_){
				jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				return false;
			}
			var value = data["value"];
	    	$('#alterpopupWindow').jqxWindow('close');  
	    	if(value == "error"){
				$("#notificationContent").text(uiLabelMap.NotifiUpdateError);
				$("#jqxNotification").jqxNotification('open');
			}
	    	if(value == "success"){
	    		$("#notificationContent").text(uiLabelMap.NotifiUpdateSucess);
				$("#jqxNotification").jqxNotification('open');
				loadLocationFacilityType();
	    	}
		});
	}
	
	function loadLocationFacilityType() {
		var listlocationFacilityType;
		$.ajax({
			  url: "loadLocationFacilityType",
			  type: "POST",
			  data: {},
			  dataType: "json",
			  success: function(data) {
				  listlocationFacilityType = data["listLocationType"];
			  }    
		}).done(function(data) {
			renderTree(listlocationFacilityType);
		});
	}
	
	function bindingEditLocationFacilityType(locationFacilityType){
		$('#locationFacilityTypeId').jqxInput({disabled: true, height: 25});
		$("#locationFacilityTypeId").val(locationFacilityType.locationFacilityTypeId);
		$("#description").jqxInput('val', locationFacilityType.description);
		$("#prefixCharacters").jqxInput('val', locationFacilityType.prefixCharacters);
		$("#defaultChildNumber").jqxNumberInput('val', locationFacilityType.defaultChildNumber);
		$("#parentLocationFacilityTypeId").jqxDropDownList('setContent', mapLocationFacilityTypeData[locationFacilityType.parentLocationFacilityTypeId]); 
		$("#parentLocationFacilityTypeId").val(locationFacilityType.parentLocationFacilityTypeId);
		$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.EditLocationTypeInfo +': ' + locationFacilityType.locationFacilityTypeId);
		$('#alterpopupWindow').jqxWindow('open');
	}
	
	function renderTree(dataList) {
    	var source =
    	{
			dataType: "json",
		    dataFields: [
		        { name: 'locationFacilityTypeId', type: 'string' },         
		    	{ name: 'parentLocationFacilityTypeId', type: 'string' },
		    	{ name: 'defaultChildNumber', type: 'number' },
		    	{ name: 'prefixCharacters', type: 'string' },
		    	{ name: 'description', type: 'string' },
		    ],
		    hierarchy:
		    {
		    	keyDataField: { name: 'locationFacilityTypeId' },
		        parentDataField: { name: 'parentLocationFacilityTypeId' }
		    },
		    id: 'locationFacilityTypeId',
		    localData: dataList
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source, {
            loadComplete: function () {
            }
        });
         // create Tree Grid
	    bindingDataLocationFacility(dataAdapter);
    }  
	
	function bindingDataLocationFacility(dataAdapter){
		$("#jqxTreeGirdLocation").jqxTreeGrid(
		{
			width: '100%',
	        source: dataAdapter,
            altRows: true,
            editable:true,
            sortable: true,
            theme: 'olbius',
            autoRowHeight: false,
            pageable: true,
            pagerMode: 'advanced',
            hierarchicalCheckboxes: true,
            ready: function()
            {
            	$("#jqxTreeGirdLocation").jqxTreeGrid('expandRow', '1');
            	$("#jqxTreeGirdLocation").jqxTreeGrid('expandRow', '2');
            },
            columns: [
                       { text: uiLabelMap.LocationType, dataField: 'locationFacilityTypeId', editable:false, width: 200},
                       { text: uiLabelMap.LocationParentType ,  dataField: 'parentLocationFacilityTypeId', columnType: "template", editable:false, width: 300,
                       },
                       { text: uiLabelMap.BLRepresentCharacters, dataField: 'prefixCharacters', width: 200, editable:false },
                       { text: uiLabelMap.BLDefaultChildNumber, dataField: 'defaultChildNumber', width: 200, editable:false,
                    	   cellsrenderer: function(row, colum, value){
         				  	  return '<span class="align-right">' + formatnumber(value) + '</span>';
                    	   },
                       },
                       { text: uiLabelMap.Description, dataField: 'description', editable:false },
                    ],
		 });
	}
	
	return {
		init: init,
		addLocationFacilityType: addLocationFacilityType,
	}
}());