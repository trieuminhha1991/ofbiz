var apprRecSalesEmplListObj = (function(){
	var _recruitmentSalesOfferId = null;
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initEvent();
	};
	var initJqxWindow = function(){
		createJqxWindow($("#apprRecSalesEmplListWindow"), 900, 550);
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'emplPositionTypeDesc', type: 'string'},
		                 {name: 'enumRecruitmentTypeId', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'idNumber', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'nativeLand', type: 'string'},
		                 {name: 'currResidenceAddr', type: 'string'},
		                 {name: 'primaryPhone', type: 'string'},
		                 {name: 'emailAddress', type: 'string'}
		                 ];
		var columns = [{datafield: 'partyId', hidden: true},
		               {datafield: 'emplPositionTypeId', hidden: true},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '15%'},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '17%'},
		               {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '16%', columntype: 'dropdownlist',
							filtertype: 'checkedlist', editable: false,
		            	   cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.statusEmplRecArr.length; i++){
									if(globalVar.statusEmplRecArr[i].statusId == value){
										return '<span>' + globalVar.statusEmplRecArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},   
		               },
		               {text: uiLabelMap.RecruitmentPosition, datafield: 'emplPositionTypeDesc', width: '18%', editable: false},
		               {text: uiLabelMap.RecruitmentEnumType, datafield: 'enumRecruitmentTypeId', width: '16%',  columntype: 'dropdownlist',
							filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.recruitmentTypeEnumArr.length; i++){
									if(globalVar.recruitmentTypeEnumArr[i].enumId == value){
										return '<span>' + globalVar.recruitmentTypeEnumArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
		               },
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
		
		var grid = $('#listSalesEmplProposedGrid');
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "listSalesEmplProposedGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListRecruitmentOffer + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
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
	var initEvent = function(){
		$("#closeApprRecSalesEmplList").click(function(event){
			$("#apprRecSalesEmplListWindow").jqxWindow('close');
		});
	};
	var openWindow = function(){
		openJqxWindow($("#apprRecSalesEmplListWindow"));
		$("#apprRecSalesEmplListWindow").on('close', function(event){
			_recruitmentSalesOfferId = null;
		});
	};
	var setData = function(data){
		_recruitmentSalesOfferId = data.recruitmentSalesOfferId;
		var url = "jqxGeneralServicer?sname=JQGetListOfferedSalesEmplRecruitment&recruitmentSalesOfferId=" + _recruitmentSalesOfferId;
		updateGridData(url);
	};
	var updateGridData = function(url){
		var source = $("#listSalesEmplProposedGrid").jqxGrid('source');
		source._source.url = url;
		$("#listSalesEmplProposedGrid").jqxGrid('source', source);
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	apprRecSalesEmplListObj.init();
});