$(function(){
	LocationObj.init();
});
var LocationObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		$("#alterpopupWindow").jqxWindow({
		    width: 500, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:'olbius'           
		});
		
		$("#jqxNotificationCreate").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityType", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxNotificationCreateError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxNotificationCreateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#jqxNotificationCreateExists").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeExits", opacity: 0.9, autoClose: true, template: "info" });
		$("#jqxNotificationUpdateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#jqxNotificationUpdateError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateError", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxNotificationUpdateErrorParent").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateErrorParent", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxMessageNotificationSelectMyTree").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSelectTreeGird", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxNotificationDeleteSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeDeleteSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#jqxMessageNotificationCheckDescription").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckDescription", opacity: 0.9, autoClose: true, template: "error" });
		$("#jqxNotificationDeleteLocationFacilityTypeError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationDeleteLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
		$('#locationFacilityTypeId').jqxInput({width: 195, theme: 'olbius', height: 20});
		$('#description').jqxInput({width: 195, theme: 'olbius', height: 20});
		$("#locationFacilityTypeIdEdit").jqxInput();  
		$("#alterpopupWindowEdit").jqxWindow({
			width: 450, height: 255, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7           
		});
		$('#descriptionEdit').jqxInput();
		$("#jqxNotificationEditLocationFacilityTypeError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationEditLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
		contextMenu = $("#Menu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup' });
		
		$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('clearSelection');
	};
	
	var initValidateForm = function (){
		$('#alterpopupWindow').jqxValidator({
	        rules: [
	               { input: '#locationFacilityTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
	               { input: '#locationFacilityTypeId', message: uiLabelMap.CheckCharacterValidate2To20, action: 'keyup, blur', rule: 'length=2,20' },
	               { input: '#description', message: uiLabelMap.CheckCharacterValidate0To10000, action: 'keyup, blur', rule: 'length=0,10000' },
	               ]
	    });

		$('#alterpopupWindowEdit').jqxValidator({
	        rules: [
	               { input: '#locationFacilityTypeIdEdit', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
	               { input: '#locationFacilityTypeIdEdit', message: uiLabelMap.CheckCharacterValidate2To20, action: 'keyup, blur', rule: 'length=2,20' },
	               { input: '#descriptionEdit', message: uiLabelMap.CheckCharacterValidate0To10000, action: 'keyup, blur', rule: 'length=0,10000' },
	               ]
	    });
		
	}
	var initElementComplex = function(){
	};
	
	var initEvents = function(){
		
		$("#jqxTreeGirdLocationFacilityType").on('rowClick', function (event) {
	    	var args = event.args;
	        if (args.originalEvent.button == 2) {
	        	var scrollTop = $(window).scrollTop();
	            var scrollLeft = $(window).scrollLeft();
	            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	            return false;
	        }
	    });
		$("#Menu").on('itemclick', function (event) {
	        var args = event.args;
	        var selection = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getSelection');
	        var rows = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getRows');
	        var rowid = selection[0].uid;
	        var checkText = uiLabelMap.Edit;
	        if ($.trim($(args).text()) == checkText) {
	        	bindingEditLocationFacilityType(rowid);
	        } else {
	        	deleteLocationFacilityType(rowid);
	        }
	    });
		
		$("#jqxTreeGirdLocationFacilityType").on('contextmenu', function () {
			return false;
		});
		
		$('#alterpopupWindowEdit').on('close', function (event) {
			$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('clearSelection'); 
			$('#alterpopupWindowEdit').jqxValidator('hide');
			document.getElementById("alterSaveEdit").disabled = false;
		});
		$('#alterpopupWindowEdit').on('open', function (event) {
			$('#alterpopupWindowEdit').jqxValidator('hide');
			document.getElementById("alterSaveEdit").disabled = false;
		});
		
		$("#alterSaveEdit").click(function (){
			var validate = $('#alterpopupWindowEdit').jqxValidator('validate');
			if(validate != false){
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
				    		var locationFacilityTypeId = $("#locationFacilityTypeIdEdit").val();
							var parentLocationFacilityTypeId = $("#parentLocationFacilityTypeIdEdit").val();
							var description = $("#descriptionEdit").val();
							document.getElementById("alterSaveEdit").disabled = true;
							$.ajax({
					    		  url: "updateLocationType",
					    		  type: "POST",
					    		  data: {locationFacilityTypeId: locationFacilityTypeId, parentLocationFacilityTypeId: parentLocationFacilityTypeId, description: description},
					    		  dataType: "json",
					    		  success: function(data) {
					    			  var value = data["value"];
					    			  if(value == "success"){
					    				  $("#notificationContentUpdateSuccess").text(uiLabelMap.UpdateSuccessfully);
					    				  $("#jqxNotificationUpdateSuccess").jqxNotification('open');
					    				  $("#alterpopupWindowEdit").jqxWindow('close');
					    			  }
					    			  if(value == "parentError"){
					    				  $("#notificationContentEditLocationFacilityTypeError").text(uiLabelMap.NotifiUpdateError);
					    				  $("#jqxNotificationEditLocationFacilityTypeError").jqxNotification('open');
					    				  document.getElementById("alterSaveEdit").disabled = false;
					    			  }
					    			  if(value == "errorParent"){
					    				  $("#notificationContentEditLocationFacilityTypeError").text(uiLabelMap.NotifiUpdateErrorParent);
					    				  $("#jqxNotificationEditLocationFacilityTypeError").jqxNotification('open');
					    				  document.getElementById("alterSaveEdit").disabled = false;
					    			  }
					    			  if(value == "notEdit"){
					    				  $("#alterpopupWindowEdit").jqxWindow('close');
					    			  }
					    		  }
					    	}).done(function(data) {
					    		$("#JqxTreeGirdLocationFacilityType").jqxTreeGrid('updatebounddata');
					    	});
				    	Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);	
			}
		});
		
		$('#alterpopupWindowEdit').on('open', function (event) {
			$('#locationFacilityTypeIdEdit').jqxInput({disabled: true });
			loadParentLocationFacilityTypeId();
		});
		
		$("#parentLocationFacilityTypeIdEdit").on("select", function(event){
			if ($("#parentLocationFacilityTypeIdEdit").val() == "ClearSelection"){
				check = false;
				$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('clear');
				$("#parentLocationFacilityTypeIdEdit").jqxDropDownList({source: listParentTypeGl, placeHolder: uiLabelMap.PleaseSelectTitle ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211px', autoDropDownHeight: 'auto',
				});
			} else {
				if ($("#parentLocationFacilityTypeIdEdit").val() != "" && !check){
					check = true; 
					$("#parentLocationFacilityTypeIdEdit").jqxDropDownList({source: listTmp, placeHolder: uiLabelMap.PleaseSelectTitle ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211px', autoDropDownHeight: 'auto',
						renderer: function (index, label, value) 
						{
						    var datarecord = listTmp[index];
						    if(datarecord != undefined){
						    	return datarecord.description;
						    }
						}
					});
				}
			}
		});
		
		$("#alterSave").click(function (){
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
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
				    		var locationFacilityTypeId = $("#locationFacilityTypeId").val();
							var parentLocationFacilityTypeId = $("#parentLocationFacilityTypeId").val();
							var description = $("#description").val();
							createLocationFacilityType(locationFacilityTypeId, parentLocationFacilityTypeId, description);
				    	Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);	
			}
		});
		

		$('#alterpopupWindow').on('open', function (event) {
			$("#description").val("");
			loadParentLocationFacilityTypeId();
			document.getElementById("alterSave").disabled = false;
		});
		$('#alterpopupWindow').on('close', function (event) {
			if ($("#txtLocationType").length && $("#parentLocationFacilityTypeId").val() === ""){
				$("#txtLocationType").jqxDropDownList('addItem', {text: $("#locationFacilityTypeId").val(), value: $("#locationFacilityTypeId").val()});
				$("#txtLocationType").jqxDropDownList('val', $("#locationFacilityTypeId").val());
			}
			$("#locationFacilityTypeId").val("");
			$("#parentLocationFacilityTypeId").jqxDropDownList('clearSelection'); 
			$("#locationFacilityTypeId").val("");
			$('#alterpopupWindow').jqxValidator('hide');
		});
	};
	
	function bindingEditLocationFacilityType(locationFacilityTypeId){
		$.ajax({
			  url: "bindingEditLocationType",
			  type: "POST",
			  data: {locationFacilityTypeId: locationFacilityTypeId},
			  dataType: "json",
			  success: function(data) {
				 var listLocationFacilityType = data["listLocationFacilityType"];
				 renderDataToInputByEditLocationFacilityType(listLocationFacilityType);
			  }    
		}).done(function(data) {
		});
	}
	
	function renderDataToInputByEditLocationFacilityType(listLocationFacilityType){
		for(var i in listLocationFacilityType){
			$("#locationFacilityTypeIdEdit").val(listLocationFacilityType[i].locationFacilityTypeId);
			var description =  getLocationFacilityType(listLocationFacilityType[i].parentLocationFacilityTypeId);
			$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('setContent', description); 
			$("#parentLocationFacilityTypeIdEdit").val(listLocationFacilityType[i].parentLocationFacilityTypeId);
			$("#descriptionEdit").val(listLocationFacilityType[i].description);
		}
		$('#alterpopupWindowEdit').jqxWindow('open');
	}
	
	var loadDataJqxTreeGirdLocationFacilityType = function loadDataJqxTreeGirdLocationFacilityType() {
		var listlocationFacilityTypeMap;
		$.ajax({
			  url: "loadLocationTypes",
			  type: "POST",
			  data: {},
			  dataType: "json",
			  success: function(data) {
				  listlocationFacilityTypeMap = data["listLocationTypesMap"];
			  }    
		}).done(function(data) {
			renderTree(listlocationFacilityTypeMap);
		});
	}
	
	function bindingParentLocationFacilityTypeIdEdit(listParentLocationFacilityTypeMap, curParent){
		$("#parentLocationFacilityTypeIdEdit").jqxDropDownList({source: listParentLocationFacilityTypeMap, placeHolder: uiLabelMap.PleaseSelectTitle ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211px', autoDropDownHeight: 'auto',
			renderer: function (index, label, value) 
			{
			    var datarecord = listParentLocationFacilityTypeMap[index];
			    return datarecord.description;
			}
		});
		if (curParent){
			$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('val', curParent['locationFacilityTypeId']);
		}
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
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
		    		$.ajax({
		      			  url: "deleteLocationType",
		      			  type: "POST",
		      			  data: {locationFacilityTypeId: locationFacilityTypeId},
		      			  dataType: "json",
		      			  success: function(data) {
		      				  var value = data["value"];
		      				  if(value == "error"){
		      					  $("#notificationContentDeleteLocationFacilityTypeError").text(uiLabelMap.CheckLinkedData);
		      					  $("#jqxNotificationDeleteLocationFacilityTypeError").jqxNotification('open');
		      				  }
		      				  else{
		      					  $("#notificationContentDeleteSuccess").text(uiLabelMap.NotifiDeleteSucess);
		      					  $("#jqxNotificationDeleteSuccess").jqxNotification('open');
		      				  }
		      			  }    
		      		}).done(function(data) {
		      			$("#JqxTreeGirdLocationFacilityType").jqxTreeGrid('updatebounddata');
		      		});
		    	Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);	
	}
	
	function createLocationFacilityType(locationFacilityTypeId, parentLocationFacilityTypeId, description){
		$.ajax({
			url: "createLocationType",
			type: "POST",
			data: {locationFacilityTypeId: locationFacilityTypeId, parentLocationFacilityTypeId: parentLocationFacilityTypeId, description: description},
			dataType: "json",
			success: function(data) {
				if(data == "success"){
					$("#JqxTreeGirdLocationFacilityType").jqxTreeGrid('updatebounddata');
					$("#locationFacilityTypeId").val("");
					$("#description").val("");
				}
			}
		});
		$('#alterpopupWindow').jqxWindow('close');
	}
	
	var addLocationFacilityType = function addLocationFacilityType(){
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	function loadParentLocationFacilityTypeId(){
		$.ajax({
	  		  url: "loadParentLocationTypes",
	  		  type: "POST",
	  		  data: {},
	  		  dataType: "json",
	  		  async: false,
	  		  success: function(data) {
	  		  }
	  	}).done(function(data) {
	  		listParentTypeGl = new Array();
	  		var listParentLocationFacilityTypeMap = data["listParentLocationTypeMap"];
	  		bindingParentLocationFacilityTypeId(listParentLocationFacilityTypeMap);
	  		
	  		var selection = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getSelection');
	  		if (selection != null && selection.length > 0){
	  			listTmp = new Array();
	  			var row = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getRow', selection[0].uid);
		        var curParent = null;
		        for (var i = 0; i < listParentLocationFacilityTypeMap.length; i ++){
		        	if (listParentLocationFacilityTypeMap[i].locationFacilityTypeId != row.locationFacilityTypeId){
		        		var rowTmp = {};
		        		rowTmp["description"] = listParentLocationFacilityTypeMap[i].locationFacilityTypeId;
		        		rowTmp["locationFacilityTypeId"] = listParentLocationFacilityTypeMap[i].locationFacilityTypeId;
		        		listTmp.push(rowTmp);
		        		listParentTypeGl.push(rowTmp);
		        	}
		        	if (listParentLocationFacilityTypeMap[i].locationFacilityTypeId == row.parentLocationFacilityTypeId){
		        		curParent = {};
		        		curParent["description"] = listParentLocationFacilityTypeMap[i].locationFacilityTypeId; 
		        		curParent["locationFacilityTypeId"] = listParentLocationFacilityTypeMap[i].locationFacilityTypeId; 
		        	}
		        }
		        if (curParent){
		        	var clearRow = {};
			        clearRow["description"] = uiLabelMap.ClearSelection;
			        clearRow["locationFacilityTypeId"] = "ClearSelection";
			        listTmp.push(clearRow);
		        }
		  		bindingParentLocationFacilityTypeIdEdit(listTmp, curParent);
	  		}
	  	});
	}
	
	function bindingParentLocationFacilityTypeId(listParentLocationFacilityTypeMap){
		$("#parentLocationFacilityTypeId").jqxDropDownList({source: listParentLocationFacilityTypeMap, autoDropDownHeight: true ,placeHolder: uiLabelMap.PleaseSelectTitle ,displayMember: 'locationFacilityTypeId', valueMember: 'locationFacilityTypeId', width:'200', height:'25',
		});
	}
	
	function renderTree(dataList) {
    	var source =
    	{
			dataType: "json",
		    dataFields: [
		        { name: 'locationFacilityTypeId', type: 'string' },         
		    	{ name: 'parentLocationFacilityTypeId', type: 'string' },
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
	    bindingDataToJqxTreeGirdAddProductToLocation(dataAdapter);
    }  
	
	function bindingDataToJqxTreeGirdAddProductToLocation(dataAdapter){
		$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid(
		{
	     	width: '100%',
	        source: dataAdapter,
            altRows: true,
            editable:true,
            localization: getLocalization(),
            sortable: true,
            theme: 'olbius',
            autoRowHeight: false,
            pageable: true,
            pagesize: 10,
            pagerMode: 'advanced',
            hierarchicalCheckboxes: true,
            ready: function()
            {
            	$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('expandRow', '1');
            	$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('expandRow', '2');
            },
            columns: [
                       { text: uiLabelMap.LocationType, dataField: 'locationFacilityTypeId', editable:false, width: 300},
                       { text: uiLabelMap.LocationParentType,  dataField: 'parentLocationFacilityTypeId', columnType: "template", editable:false, width: 300,
                       },
                       { text: uiLabelMap.Description, dataField: 'description', editable:false },
                    ],
		 });
	}
	var getLocalization = function getLocalization() {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	
	return {
		init: init,
		addLocationFacilityType: addLocationFacilityType,
		loadDataJqxTreeGirdLocationFacilityType: loadDataJqxTreeGirdLocationFacilityType,
	};
}());