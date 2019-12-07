var recruitmentRoundObject = (function(){
	var _listSubjectOfRecruitment = {};
	var _isEditMode = false;
	var _gridEle = null;
	var _listPartyIdInterviewer = {};
	//var _listPartyNameInterviewer = {};
	var init = function(){
		var enumRoundTypeId = null;
		initJqxInput();
		initJqxNumberInput();
		initGridSubject();
		initDropDown();
		initJqxWindow();
		initEvent();
		initJqxNotification();
		initValidator();
	};
	var initGridSubject = function(){
		//create grid subject
		var gridSubject = $("#recruitmentRoundSubjectGrid");
		var datafieldSubject = [{name: 'subjectId', type: 'string'},
		                        {name: 'subjectName', type: 'string'},
								{name: 'ratio', type: 'number'}];
		var columnSubject = [{datafield: 'subjectId', hidden: true},
		                     {text: uiLabelMap.RecruitmentSubjectName, width: '60%', datafield: 'subjectName', editable: false},
		                     {text: uiLabelMap.HRCommonRatio, width: '40%', datafield: 'ratio', editable: true, columntype: 'numberinput',
		                    	 cellsalign: 'right',
		                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		                    	        editor.jqxNumberInput({width: cellwidth, height: cellwidth,  spinButtons: true, min: 0, max: 1, decimalDigits: 1});
		                    	 }
		                     }];
		
		var rendertoolbarSubject = function (toolbar){
			toolbar.html("");
			var id = "recruitmentRoundSubjectGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentSubject + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		gridSubject, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#recruitmentSubjectListWindow"),
		        	}
		        );
	        Grid.createDeleteRowButton(gridSubject, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				url: '',
				height: 180,
				rendertoolbar : rendertoolbarSubject,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: true,
				editmode: 'click',
				localization: getLocalization(),
				pagesizeoptions: [5, 10, 15, 20],
				source: {id: 'subjectId', pagesize : 5, localdata: []}
		};
		Grid.initGrid(config, datafieldSubject, columnSubject, null, gridSubject);
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.roundTypeEnumArr, $("#roundTypeEnumNew"), "enumId", "description", 25, '96%');
		$("#interviewMarkerNew").jqxComboBox({source: [], displayMember: "partyName", valueMember: "partyId", width: '98%', height: 25, checkboxes: true});
		$("#roundTypeEnumNew").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if(value){
					if(value == "RECRUIT_ROUND_TEST"){
						recruitmentSubjectListObj.enableGrid();//recruitmentSubjectListObj is defined in RecruitCreateRecruitSubject.js
					}else{
						recruitmentSubjectListObj.disableGrid();
					}
				}
			}
		});
	};
	var initJqxNotification = function(){
		$("#jqxNotificationrecruitSubjectListGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitSubjectListGrid", opacity: 0.9, template: "info" });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addNewRecruitmentRound"), 530, 480);
		$("#addNewRecruitmentRound").on('open', function(event){
			if(!_isEditMode){
				var lastRound = getLastRoundRecruitment();
				$("#roundOrderNew").val(lastRound + 1);
				$(this).jqxWindow('setTitle', uiLabelMap.AddNewRecruitmentRound);
				$("#roundTypeEnumNew").jqxDropDownList({selectedIndex: 0});
			}else{
				$(this).jqxWindow('setTitle', uiLabelMap.EditRecruitmentRound);
			}
		});
		$("#addNewRecruitmentRound").on('close', function(event){
			_isEditMode = false;
			$("#roundNameNew").val("");
			$("#commentRoundNew").val("");
			var subjectSource = $("#recruitmentRoundSubjectGrid").jqxGrid('source');
			subjectSource._source.localdata = [];
			$("#recruitmentRoundSubjectGrid").jqxGrid('source', subjectSource);
			$("#interviewMarkerNew").jqxComboBox('clearSelection');
			$("#interviewMarkerNew").jqxComboBox('uncheckAll');
			$("#roundTypeEnumNew").jqxDropDownList('clearSelection');
		});
	};
	
	var getLastRoundRecruitment = function(){
		var records = _gridEle.jqxGrid('getrows');
		var lastRound = 0;
		for(var i = 0; i < records.length; i++){
			var roundOrder = records[i].roundOrder;
			if(roundOrder > lastRound){
				lastRound = roundOrder;
			}
		}
		return lastRound;
	};
	
	var initJqxInput = function(){
		$("#roundNameNew").jqxInput({width: '97%', height: 20});
		$("#commentRoundNew").jqxInput({width: '97%', height: 20});
	};
	
	var initJqxNumberInput = function(){
		$("#roundOrderNew").jqxNumberInput({width: '97%', height: '25px', spinButtons: true,  inputMode: 'simple', decimalDigits: 0, min: 1});
	};
	
	var initEvent = function(){
		$("#alterCancelRound").click(function(event){
			$("#addNewRecruitmentRound").jqxWindow('close');
		});
		
		$("#alterSaveRound").click(function(event){
			var valid = $("#addNewRecruitmentRound").jqxValidator('validate');
			if(!valid){
				return;
			}
			var selectedItems = $("#interviewMarkerNew").jqxComboBox('getCheckedItems');
			var tempListPartyId = [];
			var tempListPartyName = [];
			for(var i = 0; i < selectedItems.length; i++){
				tempListPartyId.push(selectedItems[i].value);
				tempListPartyName.push(selectedItems[i].label);
			}
			if(!_isEditMode){
				addNewRecruitmentRound(tempListPartyId, tempListPartyName);
			}else{
				updateRecruitmentRound(tempListPartyId, tempListPartyName);
			}
			$("#addNewRecruitmentRound").jqxWindow('close');
		});
		$("#saveAndContinueRound").click(function(event){
			var valid = $("#addNewRecruitmentRound").jqxValidator('validate');
			if(!valid){
				return;
			}
			var selectedItems = $("#interviewMarkerNew").jqxComboBox('getCheckedItems');
			var tempListPartyId = [];
			var tempListPartyName = [];
			for(var i = 0; i < selectedItems.length; i++){
				tempListPartyId.push(selectedItems[i].value);
				tempListPartyName.push(selectedItems[i].label);
			}
			if(!_isEditMode){
				addNewRecruitmentRound(tempListPartyId, tempListPartyName);
				var roundOrder = $("#roundOrderNew").val();
				$("#roundOrderNew").val(roundOrder + 1);
			}else{
				updateRecruitmentRound(tempListPartyId, tempListPartyName);
			}
		});
	};
	
	var updateRecruitmentRound = function(partyIds, partyNames){
		var roundOrder = $("#roundOrderNew").val();
		var checkRoundOrderExists = _gridEle.jqxGrid('getrowdatabyid', roundOrder);
		if(!checkRoundOrderExists){
			addNewRecruitmentRound(partyIds, partyNames);
			return;
		}
		var row = {
				roundOrder: roundOrder,
				roundName: $("#roundNameNew").val(),
				comment: $("#commentRoundNew").val(),
				enumRoundTypeId: $("#roundTypeEnumNew").val(),
				interviewerNames: partyNames.join(", ")
		};
		_gridEle.jqxGrid('updaterow', roundOrder, row);
		_listPartyIdInterviewer[roundOrder] = partyIds;
		if("RECRUIT_ROUND_TEST" == enumRoundTypeId){
			_listSubjectOfRecruitment[roundOrder] = $("#recruitmentRoundSubjectGrid").jqxGrid('getrows'); 
		}
	};
	
	var addNewRecruitmentRound = function(partyIds, partyNames){
		var roundOrder = $("#roundOrderNew").val();
		var checkRoundOrderExists = _gridEle.jqxGrid('getrowdatabyid', roundOrder);
		if(checkRoundOrderExists){
			bootbox.dialog(uiLabelMap.RecruitmentRoundIsCreated,
					[{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			return;
		}
		enumRoundTypeId = $("#roundTypeEnumNew").val();
		var row = {
				roundOrder: roundOrder,
				roundName: $("#roundNameNew").val(),
				comment: $("#commentRoundNew").val(),
				enumRoundTypeId: enumRoundTypeId,
				interviewerNames: partyNames.join(", ")
		};
		_listPartyIdInterviewer[roundOrder] = partyIds;
		_gridEle.jqxGrid('addrow', null, row, 'first');
		if("RECRUIT_ROUND_TEST" == enumRoundTypeId){
			_listSubjectOfRecruitment[roundOrder] = $("#recruitmentRoundSubjectGrid").jqxGrid('getrows'); 
		}
	};
	
	var initValidator = function(){
		$("#addNewRecruitmentRound").jqxValidator({
			rules: [
				{input : '#roundNameNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#roundTypeEnumNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#interviewMarkerNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						var selectedItems = $("#interviewMarkerNew").jqxComboBox('getCheckedItems'); 
						if(selectedItems.length == 0){
							return false;
						}
						return true;
					}
				},
				{
					input : '#roundNameNew', message : uiLabelMap.InvalidChar, action : 'blur',
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
					input : '#commentRoundNew', message : uiLabelMap.InvalidChar, action : 'blur',
					rule : function(input, commit){
						var val = input.val();
						if(val){
							if(validationNameWithoutHtml(val)){
								return false;
							}
						}
						return true;
					}
				}
			]
		});
	};
	
	var initJqxGrid = function(gridId){
		var datafieldRecRound = [{name: 'roundOrder', type: 'number'},
		                         {name: 'roundName', type: 'string'},
		                         {name: 'interviewerNames', type: 'string'},
		                         {name: 'enumRoundTypeId', type: 'string'},
		                         {name: 'comment', type: 'string'}];
		var columnRecRound = [{datafield: 'enumRoundTypeId', hidden: true},
		                      {text : uiLabelMap.RoundOrder, datafield : 'roundOrder', width : '12%', columntype: 'numberinput'},
		          		      {text : uiLabelMap.RoundName, datafield : 'roundName', width : '20%'},
		          		      {text : uiLabelMap.RecruitmentInterviewerMarker, datafield : 'interviewerNames', width : '30%'},
		        		      {text : uiLabelMap.HRNotes, datafield : 'comment', width : '38%', editable: true},];
		var gridRound = $("#" + gridId);
		var rendertoolbarRound = function (toolbar){
			toolbar.html("");
			var id = gridId;
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.RecruitmentRoundList+ "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		gridRound, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addNewRecruitmentRound"),
		        	}
		        );
	        var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+uiLabelMap.wgdelete +'</span></button>';
            container.append(str);
            var obj = $("#deleterowbutton" + id);
            obj.jqxButton();
            obj.on('click', function () {
            	var indexSelect = gridRound.jqxGrid('getselectedrowindex');
	        	var data = gridRound.jqxGrid('getrowdata', indexSelect);
	        	if(indexSelect > -1){
	        		if(data.roundOrder == 0){
	        			bootbox.dialog(uiLabelMap.CannotDeleteDefaultRecruitmentRound,
	        					[{
	        						"label" : uiLabelMap.CommonClose,
	        						"class" : "btn-danger btn-small icon-remove open-sans",
	        					}]		
	        			);
	        			return ;
	        		}
	        		var rowid = gridRound.jqxGrid('getrowid', indexSelect);
	        		if(_listSubjectOfRecruitment.hasOwnProperty(data.roundOrder)){
	        			delete _listSubjectOfRecruitment[rowid];
	        		}
	        		if(_listPartyIdInterviewer.hasOwnProperty(data.roundOrder)){
	        			delete _listPartyIdInterviewer[rowid];
	        		}
	        		gridRound.jqxGrid('deleterow', rowid);
	        	}
            });
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbarRound,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: false,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {id: 'roundOrder', pagesize : 10, localdata: [{roundOrder: 0, roundName: uiLabelMap.RecruitmentPassed, comment: uiLabelMap.RecruitmentPassedNote}]}
		};
		Grid.initGrid(config, datafieldRecRound, columnRecRound, null, gridRound);
		
		gridRound.on('rowdoubleclick', function(event){
			var args = event.args;
			var data = args.row.bounddata;
			_isEditMode = true;
			if(data.roundOrder != 0){
				$("#roundNameNew").val(data.roundName);
				$("#commentRoundNew").val(data.comment);
				$("#roundOrderNew").val(data.roundOrder);
				$("#roundTypeEnumNew").val(data.enumRoundTypeId);
				if(_listSubjectOfRecruitment.hasOwnProperty(data.roundOrder)){
					var subjectSource = $("#recruitmentRoundSubjectGrid").jqxGrid('source');
					subjectSource._source.localdata = _listSubjectOfRecruitment[data.roundOrder];
					$("#recruitmentRoundSubjectGrid").jqxGrid('source', subjectSource);
				}
				if(_listPartyIdInterviewer.hasOwnProperty(data.roundOrder)){
					var tempPartyIds = _listPartyIdInterviewer[data.roundOrder];
					for(var i = 0; i < tempPartyIds.length; i++){
						$("#interviewMarkerNew").jqxComboBox('checkItem', tempPartyIds[i]);
					}
				}
				openJqxWindow($("#addNewRecruitmentRound"));
			}
		});
	};
	
	var getData = function(){
		var retData = {};
		var data = [];
		var rows = _gridEle.jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			var roundOrder = rows[i].roundOrder;
			data.push({roundName: rows[i].roundName, roundOrder: roundOrder, comment: rows[i].comment, enumRoundTypeId: rows[i].enumRoundTypeId});
		}
		retData.roundList = JSON.stringify(data);
		retData.roundSubjectList = JSON.stringify(_listSubjectOfRecruitment);
		retData.roundInterviewerList = JSON.stringify(_listPartyIdInterviewer);
		return retData;
	};
	
	var resetData = function(){
		var source = _gridEle.jqxGrid('source');
		source._source.localdata = [{roundOrder: 0, roundName: uiLabelMap.RecruitmentPassed, comment: uiLabelMap.RecruitmentPassedNote}];
		_gridEle.jqxGrid('source', source);
		_listSubjectOfRecruitment = {};
		_listPartyIdInterviewer = {};
		//_listPartyNameInterviewer = [];
	};
	var setInterviewerMakerData = function(data){
		$("#interviewMarkerNew").jqxComboBox({source: data});
		if(data.length < 8){
			$("#interviewMarkerNew").jqxComboBox({autoDropDownHeight: true});
		}else{
			$("#interviewMarkerNew").jqxComboBox({autoDropDownHeight: false});
		}
	};
	var setGridEle = function(gridEle){
		_gridEle = gridEle;
	};
	return{
		init: init,
		resetData: resetData,
		getData: getData,
		initJqxGrid: initJqxGrid,
		setGridEle: setGridEle,
		setInterviewerMakerData: setInterviewerMakerData,
	}
}());

