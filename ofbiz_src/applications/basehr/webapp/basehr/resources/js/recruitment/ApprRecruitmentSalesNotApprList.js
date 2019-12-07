var apprRecSalesEmplObj = (function(){
	var _recruitmentSalesOfferId = null;
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerApprRec"));
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'recruitmentPlanSalesId', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'emplPositionTypeDesc', type: 'string'},
		                 {name: 'enumRecruitmentTypeId', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'isAccepted', type: 'bool'}
		                 ];
		var columns = [{datafield: 'recruitmentPlanSalesId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'emplPositionTypeId', hidden: true},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '15%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '17%', editable: false},
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
		               {text: uiLabelMap.CommonSubmit, datafield: 'isAccepted', columntype: 'checkbox', width: '17%', editable: true}
		               ];
		var grid = $('#recruitmentSalesApprGrid');
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "recruitmentSalesApprGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListSalesmanNotApproval + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				source: {
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#recSalesEmplListNotApprWindow"), 900, 550);
		$("#recSalesEmplListNotApprWindow").on('close', function(event){
			_recruitmentSalesOfferId = null;
		});
	};
	var initEvent = function(){
		$("#cancelApprRecSalesEmpl").click(function(event){
			$("#recSalesEmplListNotApprWindow").jqxWindow('close');
		});
		$("#saveApprRecSalesEmpl").click(function(event){
			bootbox.dialog(uiLabelMap.ApprRecruitmentSaleEmplConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "open-sans btn-primary btn-small icon-ok",
						"callback": function() {
							approvalRecSalesEmpl();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "open-sans btn-danger btn-small icon-remove",
					}]		
			);
		});
	};
	var approvalRecSalesEmpl = function(){
		var rows = $("#recruitmentSalesApprGrid").jqxGrid('getrows');
		var partyIds = [];
		for(var i = 0; i < rows.length; i++){
			var rowData = rows[i];
			if(rowData.isAccepted){
				partyIds.push(rowData.partyId);
			}
		}
		$("#loadingApprRec").show();
		disableAll();
		$.ajax({
			url: 'approvalRecruitmentSalesEmpl',
			data: {recruitmentSalesOfferId: _recruitmentSalesOfferId, partyIds: JSON.stringify(partyIds)},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#recSalesEmplListNotApprWindow").jqxWindow('close');
    			}else{
    				bootbox.dialog(response.errorMessage,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
    			}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
				$("#loadingApprRec").hide();
			}
		});
	};
	var openWindow = function(){
		openJqxWindow($("#recSalesEmplListNotApprWindow"));
	};
	var setData = function(data){
		_recruitmentSalesOfferId = data.recruitmentSalesOfferId;
		prepareData();
	};
	var prepareData = function(){
		$("#recruitmentSalesApprGrid").jqxGrid('showloadelement');
		disableAll();
		var localdata = [];
		$.ajax({
			url: 'getRecruitmentSalesEmplListNotAppr',
			data: {recruitmentSalesOfferId: _recruitmentSalesOfferId},
			type: 'POST', 
			success: function(response){
				if(response.listReturn){
					var data = response.listReturn;
					for(var i = 0; i < data.length; i++){
						data[i].isAccepted = true;
					}
					localdata = data;
				}
			},
			complete: function(jqXHR, textStatus){
				updateGridData(localdata);
				enableAll();
				$("#recruitmentSalesApprGrid").jqxGrid('hideloadelement');
			}
		});
	};
	var updateGridData = function(data){
		var source = $("#recruitmentSalesApprGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#recruitmentSalesApprGrid").jqxGrid('source', source);
	};
	var disableAll = function(){
		$("#cancelApprRecSalesEmpl").attr("disabled", "disabled");
		$("#saveApprRecSalesEmpl").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#cancelApprRecSalesEmpl").removeAttr("disabled");
		$("#saveApprRecSalesEmpl").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	apprRecSalesEmplObj.init();
});