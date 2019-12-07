var hospitalListObject = (function(){
		var _rowData = null; 
		var init = function(){
			initJqxWindow();
			initJqxInput();
			initBtnEvent();
			initJqxValidator();
			initJqxNotification();
			initJqxDropDownList();
		};
		
		var initJqxNotification = function(){
			$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerhospitalList" + globalVar.defaultSuffix});
		};
		
		var closeNotification = function(){
			$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification('closeLast');
		};
		
		var initBtnEvent = function(){
			$("#cancelCreateHospital").click(function(event){
				$("#createNewHosipitalWindow" + globalVar.defaultSuffix).jqxWindow('close');
			});
			$("#saveCreateHospital").click(function(event){
				if(!validate()){
					return;
				}
				var data = getData();
				$("#hospitalList" + globalVar.defaultSuffix).jqxGrid({disabled: true});
				$("#hospitalList" + globalVar.defaultSuffix).jqxGrid('showloadelement');
				$("#createNewHosipitalWindow" + globalVar.defaultSuffix).jqxWindow('close');
				$.ajax({
					url:'createHospital',
					data: data,
					type: 'POST',
					success: function(response){
						$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification('closeLast');
						if(response._EVENT_MESSAGE_){
							$("#notificationContentjqxgridhospitalList" + globalVar.defaultSuffix).text(response._EVENT_MESSAGE_);
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification({template: 'info'});
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification("open");
							$("#hospitalList" + globalVar.defaultSuffix).jqxGrid('updatebounddata');
						}else{
							$("#notificationContentjqxgridhospitalList" + globalVar.defaultSuffix).text(response._ERROR_MESSAGE_);
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification({template: 'error'});
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification("open");
						}
					},
					complete:  function(jqXHR, textStatus){
						$("#hospitalList" + globalVar.defaultSuffix).jqxGrid({disabled: false});
						$("#hospitalList" + globalVar.defaultSuffix).jqxGrid('hideloadelement');
					}
				});
			});
			$("#cancelChooseHospital" + globalVar.defaultSuffix).click(function(event){
				$("#hospitalListWindow").jqxWindow('close');
			});
			$("#saveChooseHospital" + globalVar.defaultSuffix).click(function(event){
				var boundIndex = $("#hospitalList" + globalVar.defaultSuffix).jqxGrid('getselectedrowindex');
				if(boundIndex > -1){
					_rowData = $("#hospitalList" + globalVar.defaultSuffix).jqxGrid('getrowdata', boundIndex);
					$("#hospitalListWindow").trigger('chooseDataHospital');
					$("#hospitalListWindow").jqxWindow('close');
				}
			});
			$("#hospitalListWindow").on('close', function(event){
				hospitalListObject.resetData();
			});
		};
		
		var initJqxInput = function(){
			$("#hospitalName" + globalVar.defaultSuffix).jqxInput({width: '96%', height: 20, theme: 'olbius'});
			$("#hospitalId" + globalVar.defaultSuffix).jqxInput({width: '96%', height: 20, theme: 'olbius'});
		};
		
		var getSelectedHospitalData = function(){
			var data = _rowData;
			_rowData = null;
			return data;
		};
		
		var getData = function(){
			var data = {};
			data.hospitalName = $("#hospitalName" + globalVar.defaultSuffix).val();
			data.hospitalCode = $("#hospitalId" + globalVar.defaultSuffix).val();
			data.stateProvinceGeoId = $("#stateProvinceHospital" + globalVar.defaultSuffix).val();
			data.districtGeoId = $("#districtHospital" + globalVar.defaultSuffix).val();
			return data;
		};
		
		var initJqxDropDownList = function(){
			createJqxDropDownList(stateProvinceGeoArr, $("#stateProvinceHospital" + globalVar.defaultSuffix), "geoId", "geoName", 25, '97%');
			createJqxDropDownList([], $("#districtHospital" + globalVar.defaultSuffix), "geoId", "geoName", 25, '97%');
			$("#stateProvinceHospital" + globalVar.defaultSuffix).on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {stateGeoId: value};
					var url = 'getAssociatedCountyListHR';
					updateSourceJqxDropdownList($('#districtHospital' + globalVar.defaultSuffix), data, url);
				}
			});
		};
		
		var updateSourceJqxDropdownList = function(dropdownlistEle, data, url, selectItem){
			$.ajax({
				url: url,
				data: data,
				type: 'POST',
				success: function(response){
					var listGeo = response.listReturn;
					if(listGeo && listGeo.length > -1){
						updateSourceDropdownlist(dropdownlistEle, listGeo);
						if(selectItem != 'undefinded'){
							dropdownlistEle.jqxDropDownList('selectItem', selectItem);
						}
					}
				}
			});
		};
		
		var initJqxWindow = function(){
			createJqxWindow($("#hospitalListWindow"), 660, 550);
			createJqxWindow($("#createNewHosipitalWindow" + globalVar.defaultSuffix), 400, 270);
			$("#createNewHosipitalWindow" + globalVar.defaultSuffix).on('close', function(event){
				Grid.clearForm($(this));
				updateSourceDropdownlist($('#districtHospital' + globalVar.defaultSuffix), []);
			});
		}; 
		
		var initJqxValidator = function(){
			$("#hospitalList" + globalVar.defaultSuffix).jqxValidator({
				rules:[
				  {
					input: "#hospitalName" + globalVar.defaultSuffix,
					message: uiLabelMapHospialList.FieldRequired,
					action : 'blur',
					rule: 'required'
				  },
				  {
					input :  "#hospitalName" + globalVar.defaultSuffix,
					message: uiLabelMapHospialList.InvalidChar,
					action : 'blur',
					rule : function(input, commit){
						var val = input.val();
						if(val){
							if(validationNameWithoutHtml(val)){
								return false;
							}
						}
						return true;
					}
				  },
				  {
					input: "#hospitalId" + globalVar.defaultSuffix,
					action : 'blur',
					message: uiLabelMapHospialList.InvalidChar,
					rule : function(input, commit){
						var val = input.val();
						if(val){
							if(validationNameWithoutHtml(val)){
								return false;
							}
						}
						return true;
					}
				  },
				  {
					  input: "#hospitalId" + globalVar.defaultSuffix,
					  action : 'blur',
					  message: uiLabelMap.InvalidChar,
					  rule: function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
				  },
				  {
					input :   "#hospitalId" + globalVar.defaultSuffix,
					action : 'blur',
					message : uiLabelMapHospialList.MustntHaveSpaceChar,
					rule : function(input,commit){
						var space = " ";
						var value = $(input).val();
						if(value.indexOf(space) > -1){
							return false;
						}
						return true;
					}
				  },
				  {
					  input: '#stateProvinceHospital' + globalVar.defaultSuffix,
					  action : 'blur',
					  message: uiLabelMapHospialList.FieldRequired,
					  rule: function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
					  }
				  },
				  {
					  input: '#districtHospital' + globalVar.defaultSuffix,
					  action : 'blur',
					  message: uiLabelMapHospialList.FieldRequired,
					  rule: function (input, commit){
						  if(!input.val()){
							  return false;
						  }
						  return true;
					  }
				  }
				]
			});
		};
		
		var validate = function(){
			return $("#hospitalList" + globalVar.defaultSuffix).jqxValidator('validate');
		};
		
		var datafield = [
				{name: 'hospitalName', type: 'string'},	              
				{name: 'hospitalId', type: 'string'},		              
				{name: 'hospitalCode', type: 'string'},
				{name: 'stateProvinceGeoId', type: 'string'},
				{name: 'stateProvinceGeoName', type: 'string'},
		];
		
		var columnlist = [
	            {text: uiLabelMapHospialList.MedicalPlace, datafield: 'hospitalName' , editable: true, width: '50%', filterable: false,
	            	   cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
	            		    $("#notificationContentjqxgridhospitalList" + globalVar.defaultSuffix).text(uiLabelMapHospialList.updateSuccessfully);
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification({template: 'info'});
							$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification("open");
	            		   return newvalue;
	            	   },
	            },
	       	  	{text: uiLabelMapHospialList.InsuranceHospital, datafield: 'hospitalCode', editable: true, width: '20%', filterable: false},
	       	  	{text: uiLabelMapHospialList.PartyState, datafield: 'stateProvinceGeoName', editable: false, width: '30%', filterable: false},
	       	  	{datafield: 'stateProvinceGeoId', hidden: true, editable: false, cellsalign: 'left', filterable: false},
	       	  	{datafield: 'hospitalId', hidden: true}
       	];
		
		var rendertoolbar = function (toolbar){
			toolbar.html('');
	        var me = this;
	        var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	        var container = $('#toolbarButtonContainer');
	     	var button = $('<button id="addrowbutton" style="margin-left:20px; cursor: pointer"><i class="icon-plus-sign"></i>' + uiLabelMapHospialList.accAddNewRow + '</button>')
	     	container.append(button);        
	     	
	        button.jqxButton();
	       
	        button.on('click', function () { 
	        	openJqxWindow($("#createNewHosipitalWindow" + globalVar.defaultSuffix));
	        });
	   	};
		
	   	var source = {
	   			pagesize : 15,
	   			updateUrl : "jqxGeneralServicer?jqaction=U&sname=updateHospitalNameCode",
	   			editColumns : "hospitalName;hospitalId;hospitalCode",
	   	};
	   	
		var config = {
	       		width: '100%', 
	       		height: 450,
	       		autoheight: false,
	       		virtualmode: true,
	       		showfilterrow: false,
	       		showtoolbar: true,
	       		rendertoolbar: rendertoolbar,
	       		pageable: true,
	       		sortable: false,
				filterable: false,
				editable: true,
				editmode: 'dblclick',
	            selectionmode: 'singlerow',
	            url: 'JQGetHospitalList',
	            source: source,
	       	};
		
		var resetData = function(){
			$("#jqxNotificationjqxgridhospitalList" + globalVar.defaultSuffix).jqxNotification('closeAll');
			 $("#hospitalList" + globalVar.defaultSuffix).jqxGrid('clearselection');
		};
		return {
			datafield: datafield,
			columnlist: columnlist,
			config: config,
			closeNotification: closeNotification,
			resetData: resetData,
			getSelectedHospitalData: getSelectedHospitalData,
			init: init
		}
}());


$(document).ready(function () {
	hospitalListObject.init();
	Grid.initGrid(hospitalListObject.config, hospitalListObject.datafield, hospitalListObject.columnlist, null, $("#hospitalList" + globalVar.defaultSuffix));
});