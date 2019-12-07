var recruitmentOfferObj = (function(){
	var _recruitmentPlanSalesId = null;
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerOfferRec"));
	};
	var initJqxWindow = function(){
		createJqxWindow($("#recruitmentSalesOfferWindow"), 900, 550);
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
		                 {name: 'isOffer', type: 'bool'}
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
		               {text: uiLabelMap.RecruitmentOffer, datafield: 'isOffer', columntype: 'checkbox', width: '17%', editable: true}
		               ];
		var grid = $('#recruitmentSalesOfferGrid');
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "recruitmentSalesOfferGrid";
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
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				source: {
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var openWindow = function(){
		openJqxWindow($("#recruitmentSalesOfferWindow"));
	};
	var setData = function(data){
		_recruitmentPlanSalesId = data.recruitmentPlanSalesId;
		prepareData();
	};
	var initEvent = function(){
		$("#cancelOfferRec").click(function(event){
			$("#recruitmentSalesOfferWindow").jqxWindow('close');
		});
		$("#saveOfferRec").click(function(event){
			bootbox.dialog(uiLabelMap.RecruitmentSaleEmplOfferConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "open-sans btn-primary btn-small icon-ok",
						"callback": function() {
							recruitmentSalesOffer();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "open-sans btn-danger btn-small icon-remove",
					}]		
			);
		});
	};
	var prepareData = function(){
		$("#loadingOfferRec").show();
		disableAll();
		$.ajax({
			url: 'getRecruitmentSalesEmplOffer',
			data: {recruitmentPlanSalesId: _recruitmentPlanSalesId},
			type: 'POST', 
			success: function(response){
				if(response.listReturn){
					var data = response.listReturn;
					for(var i = 0; i < data.length; i++){
						data[i].isOffer = true;
					}
					updateGridData(data);
				}else{
					updateGridData([]);
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
				$("#loadingOfferRec").hide();
			}
		});
	};
	
	var recruitmentSalesOffer = function(){
		var rows = $("#recruitmentSalesOfferGrid").jqxGrid('getrows');
		var partyIds = [];
		for(var i = 0; i < rows.length; i++){
			var rowData = rows[i];
			if(rowData.isOffer){
				partyIds.push(rowData.partyId);
			}
		}
		$("#loadingOfferRec").show();
		disableAll();
		$.ajax({
			url: 'createRecruitmentSalesOfferBySUP',
			data: {recruitmentPlanSalesId: _recruitmentPlanSalesId, partyIds: JSON.stringify(partyIds)},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
    				$("#containerNtf").empty();
    				$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#jqxNotificationNtf").jqxNotification({ autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#recruitmentSalesOfferWindow").jqxWindow('close');
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
				$("#loadingOfferRec").hide();
			}
		});
	};
	var updateGridData = function(data){
		var source = $("#recruitmentSalesOfferGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#recruitmentSalesOfferGrid").jqxGrid('source', source);
	};
	var disableAll = function(){
		$("#cancelOfferRec").attr("disabled", "disabled");
		$("#saveOfferRec").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#cancelOfferRec").removeAttr("disabled");
		$("#saveOfferRec").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	recruitmentOfferObj.init();
});