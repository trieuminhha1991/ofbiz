var recruitmentCandidateListObj = (function(){
	var _recruitmentPlanId = "";
	var init = function(){
		initJqxNotification();
		initContextMenu();
		initJqxGrid();
		initJqxWindow();
	};
	
	var initJqxNotification = function(){
		$("#updateNotificationCandidate").jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotificationCandidate",
	        autoOpen: false, autoClose: true
	    });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#recruitCandidateListWindow"), 960, 590);
		$("#recruitCandidateListWindow").on('close', function(event){
			refreshBeforeReloadGrid($("#recruitCandidateListGrid"));
		});
		$("#recruitCandidateListWindow").on('open', function(event){
			$("#updateNotificationCandidate").jqxNotification('closeAll');
			if(globalVar.hasPermissionAdmin){
				wizardEditCandidate.setRoundOrder(null);
				wizardEditCandidate.setFunctionAfterCreateCandidate(functionAfterCreateCandidate);
			}
		});
	};
	
	var initJqxGrid = function(){
		var datafield = candidateRecUtilObj.getDataFieldCandidateGrid();
		var columns = candidateRecUtilObj.getColumnsCandidateGrid();
		var grid = $("#recruitCandidateListGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitCandidateListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCandidatesList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.hasPermissionAdmin){
	        	Grid.createAddRowButton(
	        			grid, container, uiLabelMap.CommonAddNew, {
	        				type: "popup",
	        				container: $("#addRecruitCandidateWindow"),
	        			}
	        	);
	        }
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenuCandidateList"), false);
	};
	
	var initContextMenu = function(){
		var liElement = $("#contextMenuCandidateList>ul>li").length;
		var contextMenuHeight = 30 * liElement;
		$("#contextMenuCandidateList").jqxMenu({ width: 220, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});
		$("#contextMenuCandidateList").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#recruitCandidateListGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#recruitCandidateListGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "moveCandidateToRound"){
            	var partyId = dataRecord.partyId;
            	disabledJqxGrid($("#recruitCandidateListGrid"));
            	$.ajax({
            		url: 'moveCandidateToFirstRound',
            		data: {partyId: partyId, recruitmentPlanId: _recruitmentPlanId},
            		type: 'POST',
            		success: function(response){
            			$("#updateNotificationCandidate").jqxNotification('closeLast');
            			if(response.responseMessage == 'success'){
            				$("#notificationTextCandidate").text(response.successMessage);
        					$("#updateNotificationCandidate").jqxNotification({ template: 'info' });
            			}else{
            				$("#notificationTextCandidate").text(response.errorMessage);
        					$("#updateNotificationCandidate").jqxNotification({ template: 'error' });
            			}
            			$("#updateNotificationCandidate").jqxNotification('open');
    					$("#recruitCandidateListGrid").jqxGrid('updatebounddata');
            		},
            		complete: function(jqXHR, textStatus){
            			enableJqxGrid($("#recruitCandidateListGrid"));
            		}
            	});
            }else if(action == "editCandidateInfo"){
            	editCandidateInfoObj.openWindow();//editCandidateInfoObj is denfined in RecruitmenEditCandidateInfo.js
            	editCandidateInfoObj.setData(dataRecord);
            	editCandidateInfoObj.setFunctionAfterUpdateCandidate(recruitmentCandidateListObj.renderMessage);
            }else if(action == "editCandidateContact"){
            	editCandidateContactMechs.openWindow();//editCandidateContactMechs is denfined in RecruitmenEditCandidateInfo.js
            	editCandidateContactMechs.setData(dataRecord);
            }else if(action == "recruitCandidateProcess"){
            	recruitCandidateProcessObj.setData(dataRecord);//recruitCandidateProcessObj is defined in RecruitmenCandidateProcess.js
            	recruitCandidateProcessObj.openWindow();//recruitCandidateProcessObj is defined in RecruitmenCandidateProcess.js
            }
		});
	};
	
	var refreshGridData = function(recruitmentPlanId){
		refreshBeforeReloadGrid($("#recruitCandidateListGrid"));
		var tempS = $("#recruitCandidateListGrid").jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQGetListCandidateInRecruitment&recruitmentPlanId=" + recruitmentPlanId;
		$("#recruitCandidateListGrid").jqxGrid('source', tempS);
		setRecruitmentPlanId(recruitmentPlanId);
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitCandidateListWindow"));
	};
	
	var setRecruitmentPlanId = function(recruitmentPlanId){
		_recruitmentPlanId = recruitmentPlanId;
	};
	
	var getRecruitmentPlanId = function(){
		return _recruitmentPlanId;
	};
	
	var functionAfterCreateCandidate = function(message){
		renderMessage(message);
	};
	
	var renderMessage = function(message){
		$("#updateNotificationCandidate").jqxNotification('closeLast');
		$("#notificationTextCandidate").text(message);
		$("#updateNotificationCandidate").jqxNotification({ template: 'info' });
		$("#updateNotificationCandidate").jqxNotification('open');
		$("#recruitCandidateListGrid").jqxGrid('updatebounddata');
	};
	
	return{
		init: init,
		refreshGridData: refreshGridData,
		getRecruitmentPlanId: getRecruitmentPlanId, 
		setRecruitmentPlanId: setRecruitmentPlanId,
		renderMessage: renderMessage,
		openWindow: openWindow
	}
}());

var candidateRecUtilObj = (function(){
	var getDataFieldCandidateGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'recruitCandidateId', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'candidateId', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'educationSystemTypeId', type: 'string'},
		                 {name: 'majorDesc', type: 'string'},
		                 {name: 'classificationTypeId', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'roundName', type: 'string'},
		                 {name: 'dateInterview', type: 'date'},
		                 ];
		return datafield;
	};
	var getColumnsCandidateGrid = function(){
		var columns = [{datafield: 'recruitmentPlanId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'dateInterview', hidden: true},
		               {text: uiLabelMap.RecruitmentCandidateId, datafield: 'recruitCandidateId', width: '12%'},
		               {text: uiLabelMap.HRFullName, datafield: 'fullName', width: '15%'},
		               {text: uiLabelMap.PartyGender, datafield: 'gender', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.genderArr.length; i++){
		            			   if(value == globalVar.genderArr[i].genderId){
		            				   return '<span>' + globalVar.genderArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.genderArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.PartyBirthDate, datafield: 'birthDate', width: '15%', columntype: 'datetimeinput', filtertype: 'range', cellsformat:'dd/MM/yyyy'},
		               {text: uiLabelMap.DegreeTraining, datafield: 'educationSystemTypeId', width: '15%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.educationSystemTypeArr.length; i++){
		            			   if(value == globalVar.educationSystemTypeArr[i].educationSystemTypeId){
		            				   return '<span>' + globalVar.educationSystemTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.educationSystemTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'educationSystemTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.HRSpecialization, datafield: 'majorDesc', width: '15%'},
		               {text: uiLabelMap.HRCommonClassification, datafield: 'classificationTypeId', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.degreeClassTypeArr.length; i++){
		            			   if(value == globalVar.degreeClassTypeArr[i].classificationTypeId){
		            				   return '<span>' + globalVar.degreeClassTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.degreeClassTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'classificationTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist', filterable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.statusCandidateRoundArr.length; i++){
		            			   if(value == globalVar.statusCandidateRoundArr[i].statusId){
		            				   return '<span>' + globalVar.statusCandidateRoundArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		               },
		               {text: uiLabelMap.RecruitmentRound, datafield: 'roundName', width: '15%', filterable: false},
		               ];
		return columns;
	};
	
	var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
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
    
    var getDataTypeForDropDownList = function(object, property, dropDownEle, serviceName, condition, selectItem){
    	if(!object.hasOwnProperty(property)){
    		var dataSubmit = {serviceName: serviceName};
    		if(typeof(condition) != 'undefined' && condition != null){
    			dataSubmit.condition = condition;
    		}
			$.ajax({
				url: 'getDataTypeGeneralService',
				type: 'POST',
				data: dataSubmit,
				success: function(response){
					if(response.responseMessage == "success"){
						object[property] = response.listReturn;
						updateSourceDropdownlist(dropDownEle, object[property]);
						if(typeof(selectItem) != 'undefined'){
							dropDownEle.jqxDropDownList('selectItem', selectItem);
						}
					}
				},
				complete: function(jqXHR, textStatus){
					
				}
			});
		}else{
			var source = dropDownEle.jqxDropDownList('source');
			if(source.totalrecords <= 0){
				updateSourceDropdownlist(dropDownEle, object[property]);
			}
			if(typeof(selectItem) != 'undefined'){
				dropDownEle.jqxDropDownList('selectItem', selectItem);
			}
		}
    };
	
	return{
		getDataFieldCandidateGrid: getDataFieldCandidateGrid,
		getColumnsCandidateGrid: getColumnsCandidateGrid,
		updateSourceJqxDropdownList: updateSourceJqxDropdownList,
		getDataTypeForDropDownList: getDataTypeForDropDownList,
		
	}
}());

$(document).ready(function(){
	recruitmentCandidateListObj.init();
});