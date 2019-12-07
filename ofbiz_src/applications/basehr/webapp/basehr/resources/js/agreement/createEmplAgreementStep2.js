var createEmplAgrObjectstep2 = (function(){
	var _listNewContentId = [];
	var _listExistsContentId = [];
	var init = function(){
		initBtnEvent();
		initJqxInput();
		initJqxDropDownList();
		initJqxNumberInput();
		initAceInputFile();
		initJqxGrid();
		initJqxPanel();
		initJqxWindow();
	};
	
	var initJqxPanel = function(){
		$("#jqxPanelStep2").jqxPanel({width: '100%', height: 360, scrollBarSize: 15});
	};
	
	var initJqxGrid = function(){
		var datafield =  [
      		{name: 'code', type: 'string'},
      		{name: 'name', type: 'string'},
      		{name: 'value', type: 'number'},
      	];
      	var columnlist = [
          {datafield: 'code', hidden: true},
      	  {text: uiLabelMap.AllowancesType, datafield: 'name', editable: false, cellsalign: 'left', width: '50%', filterable: false},
      	  {text: uiLabelMap.HRCommonAmount, datafield: 'value', editable: false, cellsalign: 'right', filterable: false,
      		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 			  return '<span>' + formatcurrency(value) + '</span>';
	    	  }
      	  },
      	];
      	var rendertoolbar = function (toolbar){
    		var jqxheader = $("<div id='toolbarcontainerAlowance' class='widget-header'><h5><b>" + uiLabelMap.HREmplAllowances + "</b></h5><div id='toolbarButtonContainerAllowance' class='pull-right'></div></div>");
    		toolbar.append(jqxheader);
    		var container = $('#toolbarButtonContainerAllowance');
    		var grid = $("#allowanceGrid");
    		if(globalVar.hasPermission){
    			Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addNewAllowanceWindow")});
    			Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
    					"", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
    		}
    	};
      	
      	var config = {
      		width: '100%', 
      		height: 220,
      		autoheight: false,
      		virtualmode: false,
      		showfilterrow: false,
      		showtoolbar: true,
      		rendertoolbar: rendertoolbar,
      		pageable: true,
      		sortable: false,
      		filterable: false,
      		editable: false,
      		selectionmode: 'singlerow',
      		url: '',
      		source: {pagesize: 5, id: 'code'}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#allowanceGrid"));
	};
	
	var getData = function(){
		var dataSubmit = {};
		var partyIdRep = $("#partyIdRep" + globalVar.suffix).val();
		if(partyIdRep){
			dataSubmit.partyIdRep = partyIdRep.value;
		}
		var rows = $("#allowanceGrid").jqxGrid('getrows');
		if(rows.length > 0){
			dataSubmit.allowance = JSON.stringify(rows);
		}
		if(_listNewContentId.length > 0){
			dataSubmit.listContentId = JSON.stringify(_listNewContentId);
		}
		return dataSubmit;
	};
	
	var fillData = function(data){
		$("#partyIdRep" + globalVar.suffix).jqxInput('val', {label: data.fullName, value:data.partyId});
		var callback = function(listSource){
			updateSourceDropdownlist($("#representEmplPosition" + globalVar.suffix), listSource);
			$("#representEmplPosition" + globalVar.suffix).jqxDropDownList('selectIndex', 0);
		}
	    actionObject.getPositionOfEmplDropDownList({partyId: data.partyId}, callback);
	    if(globalVar.editFlag && (typeof(data.agreementId) != 'undefined')){
	    	$("#ajaxLoading").show();
	    	$("#btnNext").attr("disabled", "disabled");
	    	$("#btnPrev").attr("disabled", "disabled");
	    	$("#uploadFileAgrBtn").attr("disabled", "disabled");
	    	$.when(
    			getAgreementContent(data.agreementId),
    			getAgreementAllowance(data.agreementId)
	    	).done(function(){
	    		$("#btnNext").removeAttr("disabled");
	    		$("#btnPrev").removeAttr("disabled");
	    		$("#uploadFileAgrBtn").removeAttr("disabled");
	    		$("#ajaxLoading").hide();
	    	});
	    }
	};
	
	var getAgreementContent = function(agreementId){
		$.ajax({
    		url: 'getAgreementContent',
    		data: {agreementId: agreementId},
    		type: 'POST',
    		success: function(response){
    			if(response._EVENT_MESSAGE_){
    				var listContent = response.listReturn;
    				if(listContent.length > 0){
    					for(var i = 0; i < listContent.length; i++){
    						addFileAgreementToList(listContent[i].dataResourceName, listContent[i].objectInfo, listContent[i].contentId);
    						_listExistsContentId.push(listContent[i].contentId);
    					}
    				}
    			}else{
    				bootbox.dialog(uiLabelMap.ErrorOccurWhenUpdateAgreement + ": " + response._ERROR_MESSAGE_,
    						[{
    							"label" : uiLabelMap.CommonClose,
    							"class" : "btn-danger btn-small icon-remove open-sans",
    						}]		
    				);
    			}
    		},
    		error: function(jqXHR, textStatus, errorThrown){
    			
    		},
    		complete: function(jqXHR, textStatus){
    			
    		}
    	});
	};
	
	var getAgreementAllowance = function(agreementId){
		$.ajax({
			url: 'getAgreementAllowance',
			data: {agreementId: agreementId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					var listReturn = response.listReturn;
					var source = $("#allowanceGrid").jqxGrid('source');
					var localdata = [];
					for(var i = 0; i < listReturn.length; i++){
						localdata.push({value: listReturn[i].termValue, code: listReturn[i].attrValue, name: listReturn[i].name});
					}
					source._source.localdata = localdata;
					$("#allowanceGrid").jqxGrid('source', source);
				}else{
					bootbox.dialog(uiLabelMap.ErrorOccurWhenUpdateAgreement + ": " + response._ERROR_MESSAGE_,
    						[{
    							"label" : uiLabelMap.CommonClose,
    							"class" : "btn-danger btn-small icon-remove open-sans",
    						}]		
    				);
				}
			}
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#representEmplPosition" + globalVar.suffix), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.payrollParamArr, $("#allowance" + globalVar.suffix), "code", "name", 25, '98%');
	};
	
	var initJqxInput = function(){
		$("#partyIdRep" + globalVar.suffix).jqxInput({width: '85%', height: 20, disabled: true, valueMember: 'partyId', displayMember: 'fullName'});
	};
	
	var initBtnEvent = function(){
		$("#searchRepresent").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
			initWizard.setFunctionAfterChooseEmpl(functionAfterChooseEmpl);
		});
		$("#alterCancel").click(function(event){
			$("#addNewAllowanceWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var selectItem = $("#allowance" + globalVar.suffix).jqxDropDownList('getSelectedItem');
			if(!selectItem){
				return;
			}
			addNewAllowance(true);
		});
		
		$("#saveAllowanceAndContinue").click(function(event){
			var selectItem = $("#allowance" + globalVar.suffix).jqxDropDownList('getSelectedItem');
			if(!selectItem){
				return;
			}
			addNewAllowance(false);
		});
		
		$("#uploadFileAgrBtn").click(function(event){
			var form = jQuery("#upLoadFileForm");
			var file = form.find('input[type=file]').eq(0);
			if(file.data('ace_input_files')){
				var fileUpload = $('#uploadedFile')[0].files[0];
				var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);
				$("#ajaxLoading").show();
				$("#btnNext").attr("disabled", "disabled");
				$("#btnPrev").attr("disabled", "disabled");
				$("#uploadFileAgrBtn").attr("disabled", "disabled");
				$.ajax({
					url: 'uploadAgreementFile',
					type: 'POST',
					data: dataSubmit,
					cache: false,			        
			        processData: false, // Don't process the files
			        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
					success: function(response){
						if(response._EVENT_MESSAGE_){
							_listNewContentId.push(response.contentId);
							addFileAgreementToList(fileUpload.name, response.path, response.contentId);
							clearAceInputFile($("#uploadedFile"));
						}else{
							bootbox.dialog(uiLabelMap.ErrorOccurWhenUpdateAgreement + ": " + response._ERROR_MESSAGE_,
								[{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",
					    		}]		
							);
						}
					},
					error: function(jqXHR, textStatus, errorThrown){
						
					},
					complete: function(jqXHR, textStatus){
						$("#btnNext").removeAttr("disabled");
						$("#btnPrev").removeAttr("disabled");
						$("#uploadFileAgrBtn").removeAttr("disabled");
						$("#ajaxLoading").hide();
					}
				});
			}
		});
	};
	
	var addNewAllowance = function(isCloseWindow){
		var selectItem = $("#allowance" + globalVar.suffix).jqxDropDownList('getSelectedItem');
		var code = selectItem.value;
		var checkAllowanceExists = $("#allowanceGrid").jqxGrid('getrowdatabyid', code);
		var row = {
				code: code,
				name: selectItem.label,
				value: $("#allowanceAmount" + globalVar.suffix).val()
		};
		if(checkAllowanceExists){
			$("#allowanceGrid").jqxGrid('updaterow', code, row);
		}else{
			$("#allowanceGrid").jqxGrid('addrow', null, row);
		}
		if(isCloseWindow){
			$("#addNewAllowanceWindow").jqxWindow('close');
		}
	};
	
	var addFileAgreementToList = function(fileName, url, contentId){
		var agreementFileList = $("#agreementFileList");
		var divContain = $("<div class='row-fluid marginBottom10' id='newFileUpload_" + contentId +"'></div>");
		var fileLink = $("<a href='" + url + "'>" + fileName + "</a>");
		var deleteFileBtn = $("<a href='javascript:createEmplAgrObjectstep2.deleteContenAgreement(\"" + contentId + "\")' class='grid-action-button marginOnlyLeft10'>" 
				+"<i class='icon-only icon-trash'></i></a>");
		divContain.append(fileLink);
		if(globalVar.hasPermission){
			divContain.append(deleteFileBtn);
		}
		agreementFileList.append(divContain);
	};
	
	var deleteContenAgreement = function(contentId){
		if(globalVar.editFlag && initWizard.getAgreementId() != null && _listExistsContentId.indexOf(contentId) > -1){
			$("#ajaxLoading").show();
			$("#btnNext").attr("disabled", "disabled");
			$("#btnPrev").attr("disabled", "disabled");
			$("#uploadFileAgrBtn").attr("disabled", "disabled");
			var contentRemoveIndex = _listExistsContentId.indexOf(contentId);
			$.ajax({
				url: 'deleteAgreementContent',
				data: {contentId: contentId, agreementId: initWizard.getAgreementId()},
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						$("#newFileUpload_" + contentId).remove();
						_listExistsContentId.splice(contentRemoveIndex, 1);
					}else{
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",
					    		}]		
							);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#btnNext").removeAttr("disabled");
					$("#btnPrev").removeAttr("disabled");
					$("#uploadFileAgrBtn").removeAttr("disabled");
					$("#ajaxLoading").hide();
				}
			});
		}else{
			var contentRemoveIndex = _listNewContentId.indexOf(contentId);
			if(contentRemoveIndex > -1){
				_listNewContentId.splice(contentRemoveIndex, 1);
				$("#newFileUpload_" + contentId).remove();
			}
		}
	};
	
	var initJqxNumberInput = function(){
		$("#allowanceAmount" + globalVar.suffix).jqxNumberInput({width: '98%', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 12, max: 999999999999});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addNewAllowanceWindow"), 400, 200);
		$("#addNewAllowanceWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	
	var initAceInputFile = function(){
		$('#uploadedFile').ace_file_input({
	  		no_file:'No File ...',
			btn_choose: uiLabelMap.CommonChooseFile,
			droppable:false,
			onchange:null,
			thumbnail:false,	
			width: '100%',
			whitelist:'gif|png|jpg|jpeg',
			preview_error : function(filename, error_code) {
			}

		}).on('change', function(){
		});
	};	
	var functionAfterChooseEmpl = function(data){
		fillData(data);
	};
	var resetData = function(){
		$("#partyIdRep" + globalVar.suffix).val("");
		$("#representEmplPosition" + globalVar.suffix).jqxDropDownList('clearSelection');
		updateSourceDropdownlist($("#representEmplPosition" + globalVar.suffix), []);
		clearAceInputFile($("#uploadedFile"));
		var tmpSource = $("#allowanceGrid").jqxGrid('source');
		tmpSource._source.localdata = [];
		$("#allowanceGrid").jqxGrid('source', tmpSource);
		_listNewContentId = [];
		_listExistsContentId = [];
		$("#agreementFileList").empty();
	};
	return{
		init: init,
		getData: getData,
		resetData: resetData,
		fillData: fillData,
		deleteContenAgreement: deleteContenAgreement
	}
}());