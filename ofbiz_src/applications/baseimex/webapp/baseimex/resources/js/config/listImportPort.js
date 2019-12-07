$(document).ready(function() {
	ObjPort.init();
});
var ObjPort = (function() {
	var grid = $("#jqxGridImportPort");
	var popupAdd = $("#AddImportPort");
	var validatorVAL = null;
	
	var countrySelected = null;
	var provinceSelected = null;
	var districtSelected = null;
	var wardSelected = null;
	
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();

	};
	
	var initInput = function() { 
		$("#facilityCode").jqxInput({width: 295, height: '24px', theme: theme});
		$("#facilityName").jqxInput({width: 295, height: '24px', theme: theme});
		$("#address").jqxInput({width: 295, height: '24px', theme: theme});
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		popupAdd.jqxWindow({
			maxWidth: 800, minWidth: 300, width: 600, height: 400, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 10000, zIndex: 10000, autoOpen: false, cancelButton: $("#addCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		var config = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'countryGeoId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		createJqxDropDownGeo($("#countryGeoId"), countryData, config);
		
		var config = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'provinceGeoId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		createJqxDropDownGeo($("#provinceGeoId"), [], config);
		
		var config = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'districtGeoId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		createJqxDropDownGeo($("#districtGeoId"), [], config);
		
		var config = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'wardGeoId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		createJqxDropDownGeo($("#wardGeoId"), [], config);
	}
	
	var initElementComplex = function() {
		initGridPartyPeriod(grid);
	}
	
	var initGridPartyPeriod = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
			    }
			},
      		{text: uiLabelMap.BIEPortCode, datafield: 'facilityCode', width: '200',
				cellsrenderer: function (row, column, value) {
			        return '<div style="cursor:pointer;">' + (value) + '</div>';
			    }
			},
			{text: uiLabelMap.BIEPortName, datafield: 'facilityName', minwidth: '200',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
        ];
		
		var datafield = [
         	{ name: 'facilityId', type: 'string'},
         	{ name: 'facilityCode', type: 'string'},
         	{ name: 'facilityName', type: 'string'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "ImportPort";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.PortOfDischarge + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjPort.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
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
	        rowdetails: false,
	        useUrl: true,
	        url: 'jqGetFacilities&facilityTypeId=PORT',                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenu"), false);
	}
	var initEvents = function() {
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.AddNew){
				openPopupAdd();
			}
			if(tmpStr == uiLabelMap.Edit){
				// openPopupEdit();
			}
			if(tmpStr == uiLabelMap.BSRefresh){
				grid.jqxGrid('updatebounddata');
			}
		});
		
		$("#countryGeoId").on('change', function(event){
			countrySelected = {};
            if (event.args) {
                var item = event.args.item;
                if (item) {
                	countrySelected = item.originalItem;
                }
            }
			update({
				geoId: countrySelected.geoId,
				geoAssocTypeId: "REGIONS",
				geoTypeId: "PROVINCE",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'provinceGeoId');
		});
		
		$("#provinceGeoId").on('change', function(event){
			provinceSelected = {};
            if (event.args) {
                var item = event.args.item;
                if (item) {
                	provinceSelected = item.originalItem;
                }
            }
			update({
				geoId: provinceSelected.geoId,
				geoAssocTypeId: "REGIONS",
				geoTypeId: "DISTRICT",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'districtGeoId');
		});
		
		$("#districtGeoId").on('change', function(event){
			districtSelected = {};
            if (event.args) {
                var item = event.args.item;
                if (item) {
                	districtSelected = item.originalItem;
                }
            }
			update({
				geoId: districtSelected.geoId,
				geoAssocTypeId: "REGIONS",
				geoTypeId: "WARD",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'wardGeoId');
		});
		
		$("#wardGeoId").on('change', function(event){
			wardSelected = {};
			if (event.args) {
				var item = event.args.item;
				if (item) {
					wardSelected = item.originalItem;
				}
			}
		});
		
		popupAdd.on('close', function (event) {
			grid.jqxGrid('updatebounddata');
			validatorVAL.hide();
			
			countrySelected = null;
			provinceSelected = null;
			districtSelected = null;
			wardSelected = null;
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
							var data = {
								facilityName: $("#facilityName").jqxInput('val'),
								facilityCode: $("#facilityCode").jqxInput('val'),
								address: $("#address").jqxInput('val'),
								countryGeoId: countrySelected.geoId,
								provinceGeoId: provinceSelected.geoId,
								districtGeoId: districtSelected.geoId,
								wardGeoId: wardSelected.geoId,
				    		};
							$.ajax({
					    		url: "createPortOfDischarge",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			popupAdd.jqxWindow('close');
					    			grid.jqxGrid('updatebounddata');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
	}
	
	var thruDateRelation = function (partyId, periodTypeId){
		var d = new Date();
		var data = {
				partyId: partyId,
				periodTypeId: periodTypeId,
    			thruDate: d.getTime(),
		};
		
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
					$.ajax({
			    		url: "updatePartyPeriod",
			    		type: "POST",
			    		async: false,
			    		data: data,
			    		success: function (res){
			    			grid.jqxGrid('updatebounddata');
			    		}
			    	});
					Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#countryGeoId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (countrySelected == null){
						return false;
					}
					return true;
				}
			},
			{
				input: '#provinceGeoId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (provinceSelected == null){
						return false;
					}
					return true;
				}
			},
			{
				input: '#districtGeoId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (districtSelected == null){
						return false;
					}
					return true;
				}
			},
			{
				input: '#wardGeoId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (wardSelected == null){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#facilityName', type: 'validInputNotNull'},
			{input: '#address', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator(popupAdd, mapRules, extendRules, {position: 'right'});
	};
	
	var getValidator = function(){
    	return validatorVAL;
    };
    
    function createJqxDropDownGeo(elemenDiv, sourceArr, config){
    	var source = {
    			localdata: sourceArr,
    	        datatype: "array"	
    	};
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	
    	config.source = dataAdapter;
    	elemenDiv.jqxDropDownList(config);
    	if(typeof renderer != "undefined"){
    		elemenDiv.jqxDropDownList({renderer: renderer});
    	};
    	
    	if(sourceArr.length < 8){
    		elemenDiv.jqxDropDownList({autoDropDownHeight: true});
    	}
    }
    
    var openPopupAdd = function (){
    	popupAdd.jqxWindow('open');
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
    
	return {
		init : init,
		openPopupAdd: openPopupAdd,
	}
}());