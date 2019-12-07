var recruitmentBoardObj = (function(){
	var _gridEle = null;
	var init = function(){
		//initJqxGrid();
		initJqxInput();
		initEvent();
		initJqxGridSearchEmpl();
		initValidator();
		initJqxWindow();
	};
	
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var initJqxGrid = function(gridId){
		var datafieldBoard = [{name: 'partyId', type: 'string'},
		                 {name: 'partyName', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'roleDescription', type: 'string'}];
		
		var columnBoard = [
		    {datafield: 'partyId', hidden: true, editable: false},
		    {text : uiLabelMap.HRFullName, datafield : 'partyName', width : '30%', editable: false},
		    {text : uiLabelMap.HRCommonJobTitle, datafield : 'jobTitle', width : '30%', editable: false},
		    {text : uiLabelMap.CommonRole, datafield : 'roleDescription', width : '40%', editable: true},
		];
		
		var gridBoard = $("#" + gridId);
		var rendertoolbarBoard = function (toolbar){
			toolbar.html("");
			var id = gridId;
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.RecruitmentBoardList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		gridBoard, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addNewRecruitmentBoard"),
		        	}
		        );
	        Grid.createDeleteRowButton(gridBoard, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbarBoard,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: true,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {id: 'partyId', pagesize : 10}
		};
		Grid.initGrid(config, datafieldBoard, columnBoard, null, gridBoard);
	};
	
	var initJqxInput = function(){
		$("#partyIdBoard").jqxInput({width: '83%', height: 20, disabled: true, valueMember: 'partyId', displayMember: 'partyName'});
		$("#jobTitle").jqxInput({width: '96%', height: 20});
		$("#roleDescription").jqxInput({width: '96%', height: 20});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addNewRecruitmentBoard"), 450, 230);
		$("#addNewRecruitmentBoard").on('close', function(event){
			Grid.clearForm($(this));
		});
		
		createJqxWindow($('#popupWindowEmplList'), 900, 525, initJqxSplitter);
		$('#popupWindowEmplList').on('open', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    $('#popupWindowEmplList').jqxWindow('close');
		    $("#partyIdBoard").jqxInput('val', {label: data.fullName + " [" + data.partyCode +"]", value: data.partyId});
		    $("#jobTitle").val(data.emplPositionType);
		});
	};
	
	var initEvent = function(){
		$("#alterCancelBoard").click(function(event){
			$("#addNewRecruitmentBoard").jqxWindow('close');
		});
		$("#alterSaveBoard").click(function(event){
			var valid = $("#addNewRecruitmentBoard").jqxValidator('validate');
			if(!valid){
				return;
			}
			addRowData();
			$("#addNewRecruitmentBoard").jqxWindow('close');
		});
		$("#saveAndContinueBoard").click(function(event){
			var valid = $("#addNewRecruitmentBoard").jqxValidator('validate');
			if(!valid){
				return;
			}
			addRowData();
		});
		$("#searchPartyNewBtn").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
			$("#addNewRecruitmentBoard").jqxValidator('hide');
		});
	};
	
	var addRowData = function(){
		var party = $("#partyIdBoard").val();
		var row = {
				partyId: party.value,
				partyName: party.label,
				jobTitle: $("#jobTitle").val(),
				roleDescription: $("#roleDescription").val()
		};
		var checkData = _gridEle.jqxGrid('getrowdatabyid', row.partyId);
		if(checkData){
			bootbox.dialog(uiLabelMap.EmployeeAddedToBoard,
					[{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			return;
		}
		_gridEle.jqxGrid('addrow', null, row, 'first');
	};
	
	var initValidator = function(){
		$("#addNewRecruitmentBoard").jqxValidator({
			rules: [
				{input : '#searchPartyNewBtn', message : uiLabelMap.FieldRequired, action : 'valuechanged',
					rule : function(input, commit){
						var value = $("#partyIdBoard").val(); 
						if(!value){
							return false;
						}
						return true;
					}
				},
				{input : '#jobTitle', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
            ]
		});
	};
	
	var resetData = function(){
		var source = _gridEle.jqxGrid('source');
		source._source.localdata = [];
		_gridEle.jqxGrid('source', source);
	};
	
	var getData = function(){
		var rows = _gridEle.jqxGrid('getrows');
		var data = [];
		for(var i = 0; i < rows.length; i++){
			data.push({partyId: rows[i].partyId, jobTitle: rows[i].jobTitle, roleDescription: rows[i].roleDescription});
		}
		return {recruitmentBoardList: JSON.stringify(data)};
	};
	var getGridRowData = function(){
		var rows = _gridEle.jqxGrid('getrows');
		return rows;
	};
	var setGridEle = function(gridEle){
		_gridEle = gridEle;
	};
	var validate = function(){
		var rowData = _gridEle.jqxGrid('getrows');
		if(rowData.length <= 0){
			bootbox.dialog(uiLabelMap.RecruitPlanBoardIsNotEmpty,
					[
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			return false;
		}
		return true;
	};
	return{
		init: init,
		getData: getData,
		resetData: resetData,
		initJqxGrid: initJqxGrid,
		setGridEle: setGridEle,
		validate: validate,
		getGridRowData: getGridRowData
	}
}());