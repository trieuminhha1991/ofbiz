var recruitCalListObj = (function(){
	var _recruitmentPlanId = null;
	var _roundOrder = null;
	var init = function(){
		initJqxGird();
		initJqxWindow();
		$("#jqxNotificationrecruitCalListGrid").jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#containerrecruitCalListGrid",
	        autoOpen: false, autoClose: true
	    });
	};
	var initJqxGird = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'roundOrder', type: 'number'},
		                 {name: 'recruitCandidateId', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'dateInterview', type: 'date'},
		                 {name: 'interviewOrder', type: 'number'},
		                 {name: 'emailAddress', type: 'string'},
		                 {name: 'contactNumber', type: 'string'},
		                 {name: 'areaCode', type: 'string'},
		                 {name: 'countryCode', type: 'string'},
		                 ];
		var columns = [{datafield: 'recruitmentPlanId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'roundOrder', hidden: true},
		               {datafield: 'areaCode', hidden: true},
		               {datafield: 'countryCode', hidden: true},
		               {text: uiLabelMap.InterviewOrderShort, datafield: 'interviewOrder', width: '10%', columntype: 'numberinput', filtertype: 'number',
		            	   cellsalign: 'right',
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		            		   editor.jqxNumberInput({width: cellwidth, height: cellwidth, spinButtons: true, min: 0, decimalDigits: 0});
		            	   }
		               },
		               {text: uiLabelMap.RecruitmentCandidateId, datafield: 'recruitCandidateId', width: '12%', editable: false},
		               {text: uiLabelMap.HRFullName, datafield: 'fullName', width: '15%', editable: false},
		               {text: uiLabelMap.PartyGender, datafield: 'gender', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
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
		               {text: uiLabelMap.PartyBirthDate, datafield: 'birthDate', width: '15%', columntype: 'datetimeinput', filtertype: 'range', cellsformat:'dd/MM/yyyy', editable: false},
		               {text: uiLabelMap.RecruitmentTimeInterview, datafield: 'dateInterview', width: '18%', columntype: 'datetimeinput', filtertype: 'range', cellsformat:'dd/MM/yyyy HH:mm'},
		               {text: uiLabelMap.HRCommonEmail, datafield: 'emailAddress', width: '15%', editable: false},
		               {text: uiLabelMap.PhoneNumber, datafield: 'contactNumber', width: '15%', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   var data = $("#recruitCalListGrid").jqxGrid('getrowdata', row);
		            		   var telStr = "";
		            		   if(data){
		            			   if(data.countryCode){
		            				   telStr += "(" + data.countryCode + ") ";
		            			   }
		            			   if(data.areaCode){
		            				   telStr += areaCode + " ";
		            			   }
		            			   telStr += value;
		            		   }else{
		            			   telStr = value;
		            		   }
		            		   return '<span>' + telStr + '</span>';
		            	   }
		               },
		               ];
		
		var grid = $("#recruitCalListGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitCalListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.CandidateInterviewList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
        		grid, container, uiLabelMap.HRCalendarScheduleInterview, {
	        		type: "popup",
	        		container: $("#scheduleInterviewCandidateWindow"),
	        	}
	        );
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				editmode: 'dblclick',
				localization: getLocalization(),	
				source: {
					pagesize: 10, updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentRoundCandidate",
					editColumns: "partyId;recruitmentPlanId;roundOrder(java.lang.Long);interviewOrder(java.lang.Long);dateInterview(java.sql.Timestamp)",
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#recruitCalendarScheduleWindow"), 900, 450);
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitCalendarScheduleWindow"));
	};
	
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
		_roundOrder = data.roundOrder;
		refreshGridData(_roundOrder, _recruitmentPlanId);
	};
	
	var getData = function(){
		return {roundOrder: _roundOrder, recruitmentPlanId: _recruitmentPlanId};
	};
	
	var refreshGridData = function(roundOrder, recruitmentPlanId){
		refreshBeforeReloadGrid($("#recruitCalListGrid"));
		var tmpS = $("#recruitCalListGrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListCandidateInterviewOrder&hasrequest=Y&roundOrder=" + roundOrder + "&recruitmentPlanId=" + recruitmentPlanId;
		$("#recruitCalListGrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init,
		openWindow: openWindow,
		setData: setData,
		getData: getData
	}
}());

var scheduleCalCandidateObj = (function(){
	var _updateRecruitCalListGrid = false;
	var init = function(){
		initWizard();
		initJqxGrid();
		initJqxCheckBox();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initJqxWindow();
		create_spinner($("#spinnerAjaxScheduleCandidate"));
		$("#jqxNotificationorderInterviewCandidateGrid").jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#containerorderInterviewCandidateGrid",
	        autoOpen: false, autoClose: true
	    });
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'roundOrder', type: 'number'},
		                 {name: 'interviewOrder', type: 'number'},
		                 {name: 'recruitCandidateId', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 ];
		var columns = [{datafield: 'recruitmentPlanId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'roundOrder', hidden: true},
		               {text: uiLabelMap.InterviewOrderShort, datafield: 'interviewOrder', width: '30%', columntype: 'numberinput', filtertype: 'number',
		            	   cellsalign: 'right',
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		            		   editor.jqxNumberInput({width: cellwidth, height: cellwidth, spinButtons: true, min: 0, decimalDigits: 0});
		            	   }
		               },
		               {text: uiLabelMap.RecruitmentCandidateId, datafield: 'recruitCandidateId', width: '30%', editable: false},
		               {text: uiLabelMap.HRFullName, datafield: 'fullName', width: '40%', editable: false},
		               ];
		var grid = $("#orderInterviewCandidateGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "orderInterviewCandidateGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.InterviewOrder + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				editmode: 'dblclick',
				localization: getLocalization(),	
				source: {
					pagesize: 10,
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentRoundCandidate",
					editColumns: "partyId;recruitmentPlanId;roundOrder(java.lang.Long);interviewOrder(java.lang.Long)",
					functionAfterUpdate: function(){
						_updateRecruitCalListGrid = true;
					}
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxDateTimeInput = function(){
		$("#startInterviewTime").jqxDateTimeInput({width: '100%', height: 25, formatString: 'dd/MM/yyyy HH:mm'});
		$("#interviewMorningFrom").jqxDateTimeInput({width: '100%', height: 25, showCalendarButton: false, formatString: 'HH:mm'});
		$("#interviewMorningTo").jqxDateTimeInput({width: '100%', height: 25, showCalendarButton: false, formatString: 'HH:mm'});
		$("#interviewAfternoonFrom").jqxDateTimeInput({width: '100%', height: 25, showCalendarButton: false, formatString: 'HH:mm'});
		$("#interviewAfternoonTo").jqxDateTimeInput({width: '100%', height: 25, showCalendarButton: false, formatString: 'HH:mm'});
	};
	
	var initJqxNumberInput = function(){
		$("#nbrApplicantForInterview").jqxNumberInput({ width: '100%', height: '25px',  spinButtons: true, min: 1, decimalDigits: 0, inputMode: 'simple'});
		$("#timeForInterview").jqxNumberInput({ width: '97%', height: '25px',  spinButtons: true, min: 1, decimalDigits: 0, inputMode: 'simple'});
		$("#overlapTimeInterview").jqxNumberInput({ width: '97%', height: '25px',  spinButtons: true, min: 1, decimalDigits: 0, inputMode: 'simple'});
		
	};
	
	var initJqxCheckBox = function(){
		$("#isInterviewMorning").jqxCheckBox({ width: 120, height: 25, checked: true});
		$("#isInterviewAfternoon").jqxCheckBox({ width: 120, height: 25, checked: true});
		$("#isInterviewMorning").on('change', function(event){
			$("#interviewMorningFrom").jqxDateTimeInput({disabled: !event.args.checked});
			$("#interviewMorningTo").jqxDateTimeInput({disabled: !event.args.checked});
		});
		$("#isInterviewAfternoon").on('change', function(event){
			$("#interviewAfternoonFrom").jqxDateTimeInput({disabled: !event.args.checked});
			$("#interviewAfternoonTo").jqxDateTimeInput({disabled: !event.args.checked});
		});
	};
	
	var initWizard = function(){
		$('#wizardCalendarSchedule').ace_wizard().on('change' , function(e, info){
	        
	    }).on('finished', function(e) {
	    	bootbox.dialog(uiLabelMap.ConfirmSchduleInterviewCandidate,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function(){
							scheduleInterviewCandidate();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
	    }).on('stepclick', function(e){
	    	
	    });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#scheduleInterviewCandidateWindow"), 700, 500);
		$("#scheduleInterviewCandidateWindow").on('open', function(event){
			if(globalVar.hasOwnProperty("startDate")){
				var tempDate = new Date(globalVar.startDate);
				$("#startInterviewTime").jqxDateTimeInput('val', tempDate);
				$("#interviewMorningFrom").jqxDateTimeInput('val', tempDate);
				$("#interviewMorningTo").jqxDateTimeInput('val', tempDate);
				$("#interviewAfternoonFrom").jqxDateTimeInput('val', tempDate);
				$("#interviewAfternoonTo").jqxDateTimeInput('val', tempDate);
				$("#nbrApplicantForInterview").val(1);
				$("#timeForInterview").val(1);
				$("#overlapTimeInterview").val(1);
				$("#isInterviewMorning").jqxCheckBox({checked: true});
				$("#isInterviewAfternoon").jqxCheckBox({checked: true});
				var data = recruitCalListObj.getData();
				refreshGridData(data.roundOrder, data.recruitmentPlanId);
			}
		});
		$("#scheduleInterviewCandidateWindow").on('close', function(event){
			$("#wizardCalendarSchedule").wizard('previous');
			if(_updateRecruitCalListGrid){
				$("#recruitCalListGrid").jqxGrid('updatebounddata');
			}
			_updateRecruitCalListGrid = false;
		});
	};
	
	var refreshGridData = function(roundOrder, recruitmentPlanId){
		refreshBeforeReloadGrid($("#orderInterviewCandidateGrid"));
		var tmpS = $("#orderInterviewCandidateGrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListCandidateInterviewOrder&hasrequest=Y&roundOrder=" + roundOrder + "&recruitmentPlanId=" + recruitmentPlanId;
		$("#orderInterviewCandidateGrid").jqxGrid('source', tmpS);
	};
	
	var scheduleInterviewCandidate = function(){
		var dataSubmit = $.extend({}, getData(), recruitCalListObj.getData());
		$("#ajaxLoadingScheduleCandidate").show();
		disabledBtn();
		$.ajax({
			url: 'recruitmentScheduleInterviewCandidate',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					$("#scheduleInterviewCandidateWindow").jqxWindow('close');
					$("#jqxNotificationrecruitCalListGrid").jqxNotification('closeLast');
					$("#notificationContentrecruitCalListGrid").text(response.successMessage);
					$("#jqxNotificationrecruitCalListGrid").jqxNotification({ template: 'info' });
					$("#jqxNotificationrecruitCalListGrid").jqxNotification('open');
					$("#recruitCalListGrid").jqxGrid('updatebounddata');
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
				$("#ajaxLoadingScheduleCandidate").hide();
				enableBtn();
			}
		});
	};
	
	var disabledBtn = function(){
		$("#btnNextScheduleCandidate").attr("disabled", "disabled");
		$("#btnPrevScheduleCandidate").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#btnNextScheduleCandidate").removeAttr("disabled");
		$("#btnPrevScheduleCandidate").removeAttr("disabled");
	};
	
	var getData = function(){
		var data = {};
		var startTimeInterview = $("#startInterviewTime").jqxDateTimeInput('val', 'date');
		if(startTimeInterview){
			data.startTimeInterview = startTimeInterview.getTime();
		}
		var isInterviewMorning = $("#isInterviewMorning").jqxCheckBox('checked');
		if(isInterviewMorning){
			data.isInterviewMorning = "Y";
			data.interviewMorningFrom = $("#interviewMorningFrom").jqxDateTimeInput('val', 'date').getTime();
			data.interviewMorningTo = $("#interviewMorningTo").jqxDateTimeInput('val', 'date').getTime();
		}else{
			data.isInterviewMorning = "N";
		}
		var isInterviewAfternoon = $("#isInterviewAfternoon").jqxCheckBox('checked');
		if(isInterviewAfternoon){
			data.isInterviewAfternoon = "Y";
			data.interviewAfternoonFrom = $("#interviewAfternoonFrom").jqxDateTimeInput('val', 'date').getTime();
			data.interviewAfternoonTo = $("#interviewAfternoonTo").jqxDateTimeInput('val', 'date').getTime();
		}else{
			data.isInterviewAfternoon = "N";
		}
		data.nbrApplicantForInterview = $("#nbrApplicantForInterview").val();
		data.timeForInterview = $("#timeForInterview").val();
		data.overlapTimeInterview = $("#overlapTimeInterview").val();
		return data;
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	recruitCalListObj.init();
	scheduleCalCandidateObj.init();
});