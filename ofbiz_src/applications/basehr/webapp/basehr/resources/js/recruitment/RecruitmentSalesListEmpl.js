var recruitedSaleEmplListObj = (function(){
	var _recruitmentPlanSalesId = null;
	var init = function(){
		initJqxGrid();
		initJqxWindow();
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'idNumber', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'nativeLand', type: 'string'},
		                 {name: 'currResidenceAddr', type: 'string'},
		                 {name: 'primaryPhone', type: 'string'},
		                 {name: 'emailAddress', type: 'string'}
		                 ];
		var columns = [{datafield: 'partyId', hidden: true},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '15%'},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '17%'},
		               {text: uiLabelMap.Sexual, datafield: 'gender', width: '9%',
		            	   cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.genderList.length; i++){
									if(globalVar.genderList[i].genderId == value){
										return '<span>' + globalVar.genderList[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.genderList,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
		               },
		               {text: uiLabelMap.certProvisionId, datafield: 'idNumber', width: '15%'},
		               {text: uiLabelMap.BirthDate, datafield: 'birthDate', width: '12%', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'},
		               {text: uiLabelMap.NativeLand, datafield: 'nativeLand', width: '15%',},
		               {text: uiLabelMap.CurrentResidence, datafield: 'currResidenceAddr', width: '25%'},
		               {text: uiLabelMap.PhoneNumber, datafield: 'primaryPhone', width: '14%'},
		               {text: uiLabelMap.HRCommonEmail, datafield: 'emailAddress', width: '15%'},
		               ];
		var grid = $('#listEmplRecruitedGrid');
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "listEmplRecruitedGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListEmplRecruited + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        	grid, container, uiLabelMap.CommonAddNew, {
	        		type: "popup",
	        		container: $("#RecruitmentSaleAddEmplWindow"),
	        	}
	        );
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				source: {
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#recruitSaleEmplListWindow"), 900, 550);
		$("#recruitSaleEmplListWindow").on('close', function(event){
			_recruitmentPlanSalesId = null;
		});
	};
	var setData = function(data){
		_recruitmentPlanSalesId = data.recruitmentPlanSalesId;
		resfreshGridData(_recruitmentPlanSalesId);
	};
	var resfreshGridData = function(recruitmentPlanSalesId){
		var source = $("#listEmplRecruitedGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListRecruitmentSalesEmpl&recruitmentPlanSalesId=" + recruitmentPlanSalesId;
		$("#listEmplRecruitedGrid").jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#recruitSaleEmplListWindow"));
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	recruitedSaleEmplListObj.init();
});