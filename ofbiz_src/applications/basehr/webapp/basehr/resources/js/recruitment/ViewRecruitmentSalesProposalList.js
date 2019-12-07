var propsalSalesEmplObj = (function(){
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initEvent();
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
		var grid = $('#emplSalesListProposalGrid');
		var customControlAdvance = "<div id='monthCustomTimePeriodPpsl' style='display: inline-block; margin-right: 5px'></div><div id='yearCustomTimePeriodPpsl' style='display: inline-block;'></div>";
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "emplSalesListProposalGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.SalesmanProposalApproval + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.triggerToolbarEvent(grid, container, customControlAdvance);
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
		createJqxWindow($("#listSalesEmplPropsalWindow"), 900, 550);
		$("#listSalesEmplPropsalWindow").on('close', function(event){
			updateJqxGrid("");
			updateSourceDropdownlist($("#monthCustomTimePeriodPpsl"), []);
			$("#yearCustomTimePeriodPpsl").jqxDropDownList('clearSelection');
		});
		$("#listSalesEmplPropsalWindow").on('open', function(event){
			if($("#yearCustomTimePeriodPpsl").length){
				$("#yearCustomTimePeriodPpsl").jqxDropDownList({selectedIndex: 0});
			}
		});
	};
	var initEvent = function(){
		var newDate = new Date();
		$("#emplSalesListProposalGrid").on('loadCustomControlAdvance', function(){
			createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimePeriodPpsl"), "customTimePeriodId", "periodName", 25, 100);
			createJqxDropDownList([], $("#monthCustomTimePeriodPpsl"), "customTimePeriodId", "periodName", 25, 100);
			$("#monthCustomTimePeriodPpsl").on('select', function(event){
				var args = event.args;
				if(args){
					var value = args.item.value;
					var url = "jqxGeneralServicer?sname=JQGetListProposalSalesEmplRecruitment&customTimePeriodId=" + value;
					updateJqxGrid(url);
				}
			});
			$("#yearCustomTimePeriodPpsl").on('select', function(event){
				var args = event.args;
				if(args){
					 var value = args.item.value;
					 updateSourceDropdownlist($("#monthCustomTimePeriodPpsl"), []);
					 $.ajax({
						url: "getCustomTimePeriodByParent",
						data: {parentPeriodId: value},
						type: 'POST',
						success: function(data){
							if(data.listCustomTimePeriod){
								var listCustomTimePeriod = data.listCustomTimePeriod;
								updateSourceDropdownlist($("#monthCustomTimePeriodPpsl"), listCustomTimePeriod);
							}
						},
						complete: function(jqXHR, textStatus){
							$("#monthCustomTimePeriodPpsl").jqxDropDownList({selectedIndex: newDate.getMonth()});
						}
					});
				}
			});
			$("#yearCustomTimePeriodPpsl").jqxDropDownList({selectedIndex: 0});
		});
		$("#cancelProposalRecSalesEmpl").click(function(event){
			$("#listSalesEmplPropsalWindow").jqxWindow('close');
		});
		$("#saveProposalRecSalesEmpl").click(function(event){
			bootbox.dialog(uiLabelMap.RecruitmentSaleEmplProprosalApprConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "open-sans btn-primary btn-small icon-ok",
						"callback": function() {
							proposalApprRecruitmentSalesEmpl();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "open-sans btn-danger btn-small icon-remove",
					}]		
			);
		});
	};
	var proposalApprRecruitmentSalesEmpl = function(){
		disableAll();
		$("#emplSalesListProposalGrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'proposalApprovalRecruitmentSalesEmpl',
			type: 'POST',
			data: {customTimePeriodId: $("#monthCustomTimePeriodPpsl").val()},
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
				}else{
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#listSalesEmplPropsalWindow").jqxWindow('close');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
				$("#emplSalesListProposalGrid").jqxGrid('hideloadelement');
			}
		});
	};
	var disableAll = function(){
		$("#cancelProposalRecSalesEmpl").attr('disabled', 'disabled');
		$("#saveProposalRecSalesEmpl").attr('disabled', 'disabled');
	};
	var enableAll = function(){
		$("#cancelProposalRecSalesEmpl").removeAttr('disabled');
		$("#saveProposalRecSalesEmpl").removeAttr('disabled');
	};
	var updateJqxGrid = function(url){
		var source = $("#emplSalesListProposalGrid").jqxGrid('source');
		source._source.url = url;
		$("#emplSalesListProposalGrid").jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#listSalesEmplPropsalWindow"));
	};
	return {
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	propsalSalesEmplObj.init();
});