var editRecruitReqObj = (function(){
	var _data = {};
	var _recruitmentRequireId = null;
	var _isPlanned = false;
	var _isEditable = false;
	var init = function(){
		initJqxCheckBox();
		initJqxDropDownList();
		initJqxGrid();
		initJqxWindow();
		initJqxpopover();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerRecruitReqEdit"));
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.recruitmentFormTypeArr, $("#recruitmentFormTypeEdit"), "recruitmentFormTypeId", "description", 25, '98%');
	};
	var initJqxNumberInput = function(){
		$("#quantityPlannedEdit").jqxNumberInput({width: '98%', height: 25, inputMode: 'simple', decimalDigits: 0, spinButtons: true});
		$("#quantityUnplannedEdit").jqxNumberInput({width: '93%', height: 25, disabled: true, inputMode:'simple', decimalDigits: 0, spinButtons: true});
	};
	var setData = function(data){
		_data = data;
	};
	var initJqxEditor = function(){
		/*$("#changeReasonEdit").jqxEditor({ 
    		width: '99%',
            theme: 'olbiuseditor',
            tools: '',
            height: 110,
            disabled: true
        });*/
	};
	var initJqxGrid = function(){
		var datafield = [
		                 {name: 'recruitmentReqCondTypeId', type: 'string'},
		                 {name: 'recruitmentReqCondTypeName', type: 'string'},
		                 {name: 'conditionDesc', type: 'string'},
		                 ];
		var columns = [
		               {datafield : 'recruitmentReqCondTypeId', hidden: true},
		               {text: uiLabelMap.RecruitmentCriteria, datafield: 'recruitmentReqCondTypeName', width: '40%', editable: false},
		               {text: uiLabelMap.HRCondition, datafield: 'conditionDesc', width: '60%', editable: false,
		            	   cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties){
		            		   return defaulthtml;
		            	   }
		               }
		               ];
		var grid = $("#recruitmentReqCondGridEdit");
		var renderstatusbar = function (statusbar) {
			var container = $("<div style='overflow: hidden; position: relative; background-color: whitesmoke; height: 100%'></div>");
            var addButton = $('<a id="" style="margin-left: 5px" href="javascript:void(0)" class="grid-action-button icon-plus open-sans">' + uiLabelMap.CommonAddNew + '</a>');
            var deleteButton = $('<a id="" style="margin-left: 5px" href="javascript:void(0)" class="grid-action-button icon-remove open-sans">' + uiLabelMap.wgdelete + '</a>');
            container.append(addButton);
            container.append(deleteButton);
            $(statusbar).append(container);
            deleteButton.click(function (event) {
                var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                var id = grid.jqxGrid('getrowid', selectedrowindex);
                grid.jqxGrid('deleterow', id);
            });
            addButton.click(function(event){
            	recuritmentReqCondObj.setGridEle(grid);//recuritmentReqCondObj is defined in ViewListRecruitmentRequire.js
            	$("#addRecruitmentRequireCondWindow").jqxWindow('open');
            });
		};
		var config = {
				width: '99%',
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: false,
		   		showfilterrow: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: '',    
	   			showtoolbar: false,
	   			showstatusbar: true,
	   			renderstatusbar: renderstatusbar,
	   			statusbarheight: 30,
	   			pagesizeoptions: [5],
	        	source: {pagesize: 5, id: 'recruitmentReqCondTypeId'}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
			initJqxNumberInput();
		};
		createJqxWindow($("#editRecruitmentRequireWindow"), 700, 580, initContent);
		$("#editRecruitmentRequireWindow").on('open', function(event){
			prepareData();
		});
		$("#editRecruitmentRequireWindow").on('close', function(event){
			_recruitmentRequireId = null;
			_data = {};
			_isPlanned = false;
			_isEditable = false;
		});
	};
	var prepareData = function(){
		_recruitmentRequireId = _data.recruitmentRequireId;
		var statusDesc = "";
		var emplPositionTypeDesc = "";
		for(var i = 0; i < globalVar.statusArr.length; i++){
			if(_data.statusId == globalVar.statusArr[i].statusId){
				statusDesc = globalVar.statusArr[i].description;
				break;
			}
		}
		for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
			if(_data.emplPositionTypeId == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
				emplPositionTypeDesc = globalVar.emplPositionTypeArr[i].description;
				break;
			}
		}
		renderChangeReasonData(_data.comment);
		$("#recruitmentRequireStatusEdit").html(statusDesc);
		$("#emplPositionTypeIdEdit").html(emplPositionTypeDesc);
		$("#partyIdEdit").html(_data.groupName);
		$("#monthYearEdit").html(uiLabelMap.CommonMonth + ' ' + (_data.month + 1) + '/' + _data.year);
		$("#quantityPlannedEdit").val(_data.quantity);
		$("#quantityUnplannedEdit").val(_data.quantityUnplanned);
		//$("#changeReasonEdit").jqxEditor('val', _data.change);
		$("#recruitmentFormTypeEdit").val(_data.recruitmentFormTypeId);
		if(_data.enumRecruitReqTypeId == "RECRUIT_REQUIRE_UNPLANNED"){
			$("#plannedRadioBtnEdit").jqxCheckBox({checked: false});
			$("#unplannedRadioBtnEdit").jqxCheckBox({checked: true});
			_isPlanned = false;
		}else if(_data.enumRecruitReqTypeId == "RECRUIT_REQUIRE_PLANNED"){
			$("#plannedRadioBtnEdit").jqxCheckBox({checked: true});
			$("#unplannedRadioBtnEdit").jqxCheckBox({checked: false});
			_isPlanned = true;
		}else{
			$("#plannedRadioBtnEdit").jqxCheckBox({checked: false});
			$("#unplannedRadioBtnEdit").jqxCheckBox({checked: false});
		}
		if(!_isPlanned){
			$("#quantityUnplannedEdit").jqxNumberInput({disabled: false});
		}else{
			$("#quantityUnplannedEdit").jqxNumberInput({disabled: true});
		}
		disableAll();
		$("#loadingRecruitReqEdit").show();
		$.when(
			$.ajax({
				url: 'getListRecruitmentRequireCond',
				data: {recruitmentRequireId: _recruitmentRequireId},
				type: 'POST', 
				success: function(response){
					var localdata = [];
					if(response.listReturn){
						localdata = response.listReturn;
					}
					updateGrid(response.listReturn);
				}
			}),
			getRecruitAnticipateByMonthYear(),
			checkRecruitmentRequireEditable()
		).done(function(){
			$("#loadingRecruitReqEdit").hide();
			enableAll();
		});
	};
	var getRecruitAnticipateByMonthYear = function(){
		return $.ajax({
			url: 'getRecruitAnticipateByMonthYear',
			data: {partyId: _data.partyId, emplPositionTypeId: _data.emplPositionTypeId, month: _data.month, year: _data.year},
			type: 'POST', 
			success: function(response){
				_data.quantityAppr = 0;
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		}]		
						);
					$("#recruitQtyApprovedEdit").html("______");
				}else if(response.isPlanned){
					$("#recruitQtyApprovedEdit").html(response.quantity);
					_data.quantityAppr = response.quantity;
				}else{
					$("#recruitQtyApprovedEdit").html("0");
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var checkRecruitmentRequireEditable = function(){
		return $.ajax({
			url: 'checkRecruitmentRequireEditable',
			data: {recruitmentRequireId: _data.recruitmentRequireId},
			type: 'POST',
			success: function(response){
				if(response.isEditable){
					_isEditable = response.isEditable; 
				}else{
					_isEditable = false;
				}
			},
			complete: function(jqXHR, textStatus){
				if(_isEditable){
					$("#saveEditRecruitReq").show();
					$("#recruitmentReqCondGridEdit").jqxGrid({ showstatusbar: true});
				}else{
					$("#saveEditRecruitReq").hide();
					$("#recruitmentReqCondGridEdit").jqxGrid({ showstatusbar: false});
				}
			}
		});
	};
	var renderChangeReasonData = function(comment){
		var showSeeMore = false;
		var commentDisplay = comment;
		if(comment && comment.length > 0){
			if(comment.length > 45){
				commentDisplay = comment.substring(0, 44) + "...";
				showSeeMore = true;
			}
			$("#recruitmentReasonEdit").html(commentDisplay);
		}
		if(showSeeMore){
			$("#seeMoreCommentEdit").show();
			$("#commentPopoverEditContent").html(comment);
		}else{
			$("#seeMoreCommentEdit").hide();
		}
	};
	var updateGrid = function(localdata){
		var source = $("#recruitmentReqCondGridEdit").jqxGrid('source');
		source._source.localdata = localdata;
		$("#recruitmentReqCondGridEdit").jqxGrid('source', source);
	};
	var disableAll = function(){
		$("#cancelEditRecruitReq").attr("disabled", "disabled");
		$("#saveEditRecruitReq").attr("disabled", "disabled");
		$("#quantityPlannedEdit").jqxNumberInput({disabled: true});
		if(!_isPlanned){
			$("#quantityUnplannedEdit").jqxNumberInput({disabled: true});
		}
		$("#recruitmentFormTypeEdit").jqxDropDownList({disabled: true});
	};
	var enableAll = function(){
		$("#cancelEditRecruitReq").removeAttr("disabled");
		$("#saveEditRecruitReq").removeAttr("disabled");
		$("#quantityPlannedEdit").jqxNumberInput({disabled: false});
		if(!_isPlanned){
			$("#quantityUnplannedEdit").jqxNumberInput({disabled: false});
		}
		$("#recruitmentFormTypeEdit").jqxDropDownList({disabled: false});
	};
	var getData = function(){
		var dataSubmit = {};
		dataSubmit.recruitmentRequireId = _recruitmentRequireId;
		if($("#quantityPlannedEdit").val()){
			dataSubmit.quantity = $("#quantityPlannedEdit").val();
		}
		if($("#quantityUnplannedEdit").val()){
			dataSubmit.quantityUnplanned = $("#quantityUnplannedEdit").val();
		}
		//dataSubmit.changeReason = $("#changeReasonEdit").jqxEditor('val');
		if($("#recruitmentFormTypeEdit").val().length > 0){
			dataSubmit.recruitmentFormTypeId = $("#recruitmentFormTypeEdit").val();
		}
		return dataSubmit;
	};
	var initEvent = function(){
		$("#cancelEditRecruitReq").click(function(event){
			$("#editRecruitmentRequireWindow").jqxWindow('close');
		});
		$("#saveEditRecruitReq").click(function(event){
			var valid = $("#editRecruitmentRequireWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			var condData = recuritmentReqCondObj.getData();//recuritmentReqCondObj is defined in ViewListRecruitmentRequire.js
			var dataSubmit = $.extend({}, data, condData);
			disableAll();
			$("#loadingRecruitReqEdit").show();
			$.ajax({
				url: 'updateRecruitmentRequireAndCond',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
	    				$("#editRecruitmentRequireWindow").jqxWindow('close');
	    				$('#containerNtf').empty();
						$("#jqxNotificationNtf").jqxNotification('closeLast');
						$("#notificationContentNtf").text(response.successMessage);
						$("#jqxNotificationNtf").jqxNotification('open');
						$("#jqxgrid").jqxGrid('updatebounddata');
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
	    			$("#loadingRecruitReqEdit").hide();
				}
			});
		});
	};
	var initJqxValidator = function(){
		$("#editRecruitmentRequireWindow").jqxValidator({
			rules: [
				{input : '#quantityPlannedEdit', message : uiLabelMap.ValueMustLessThanValueAppr, action: 'blur', 
					rule : function(input, commit){
						if(input.val() > _data.quantityAppr){
							return false;
						}
						return true;
					}
				},
            ]
		});
	};
	var initJqxCheckBox = function(){
		$("#plannedRadioBtnEdit").jqxCheckBox({ width: "100%", height: 22, disabled: true});
		$("#unplannedRadioBtnEdit").jqxCheckBox({ width: '100%', height: 22, disabled: true});
	};
	var openWindow = function(){
		openJqxWindow($("#editRecruitmentRequireWindow"));
		$("#editRecruitmentRequireWindow").on('close', function(event){
			Grid.clearForm($(this));
			updateGrid([]);
			$("#commentPopoverEdit").jqxPopover('close');
		});
	};
	var initJqxpopover = function(){
		$("#commentPopoverEdit").jqxPopover({offset: {left: -50, top:0}, width: 400, arrowOffsetValue: 30, title: uiLabelMap.RecruitmentReason, 
			showCloseButton: true, selector: $("#seeMoreCommentEdit"), position: "bottom", isModal: false});
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	 editRecruitReqObj.init()
});