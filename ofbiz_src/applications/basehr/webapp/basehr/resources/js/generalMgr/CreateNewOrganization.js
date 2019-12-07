	var actionCreateNewPartyGroup = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		var defaultData = {};
		var init = function(){
	    		initInputEle();
	    		initDropDownList();
	    		initDropDownListEvent();
	    		//initJqxEditor();
	    		btnEvent();
	    		initJqxValidator();
	    		initAceInput();
	    		initJqxWindow();
	    		initJqxTree();
	    		initJqxTreeEvent();
    	};
    	var initJqxTree = function(){
    		var config = {dropDownBtnWidth: 245, treeWidth: 245};
    		globalObject.createJqxTreeDropDownBtn($("#jqxTreeOrgUnitCreateNew"), $("#belongOrgUnitButtonCreateNew"), globalObject.rootPartyArr, "treeNew", "treeChildNew", config);
    		config.textKey = "description";
    		config.valueKey = "roleTypeId";
    		config.url = "getListRoleTypeByParent";
    		config.parentKey = "parentTypeId";
    		globalObject.createJqxTreeDropDownBtn($("#jqxTreeRoleTypeNew"), $("#roleTypeListDropDownButton"), roleTypeData, "treeRole", "treeChildRole", config);
    	};
    	var initJqxTreeEvent = function(){
    		setJqxTreeDropDownSelectEvent($("#jqxTreeOrgUnitCreateNew"), $("#belongOrgUnitButtonCreateNew"));
    		setJqxTreeDropDownSelectEvent($("#jqxTreeRoleTypeNew"), $("#roleTypeListDropDownButton"));
    	};
    	
    	var initAceInput = function(){
    		$('#uploadedFile').ace_file_input({
    	  		no_file:'No File ...',
    			btn_choose: uiLabelMap.CommonChooseFile,
    			droppable:false,
    			onchange:null,
    			thumbnail:false,	
    			width: '100px',
    			whitelist:'gif|png|jpg|jpeg',
    			preview_error : function(filename, error_code) {
    			}
    	
    		}).on('change', function(){
    			var x = $('.ace-file-input');
    			var y = x.children();
    			$(y[1]).css('width', '90%');
    			$(y[2]).css('margin-right', '10px');
    		});
    		$('a.remove').click(function(){
    			var x = $('.ace-file-input');
    			var y = x.children();
    			$(y[1]).css('width', '98%');
    			$(y[2]).css('margin-right', '0px');
    		});
    	};
    	
		var initInputEle = function(){
			$("#orgUnitIdCreateNew").jqxInput({theme: 'olbius', height: 20, width: '96%'});
			$("#orgUnitNameCreateNew").jqxInput({theme: 'olbius', height: 20, width: '96%'});
			$("#addressOrgUnitCreateNew").jqxInput({theme: 'olbius', height: 20, width: '96%'});
			//$("#addressOrgUnitCreateNew").on('change', function(){});
		};
		
		var initJqxEditor = function(){
			$('#commentsNewPartyGroup').jqxEditor({ 
        		width: '100%',
	            theme: 'olbiuseditor',
	            tools: 'datetime | clear | backcolor | font | bold italic underline',
	            height: 200,
	        });	
        	//$('#commentsNewPartyGroup').val("");
		};
		
		var initJqxWindow = function(){
			var initContent = function(){
				initJqxEditor();
			};
			createJqxWindow($("#newPartyGroupWindow"), 800, 500, initContent);
			$("#newPartyGroupWindow").on('close', function(event){
				clearDropDownContent($("#jqxTreeOrgUnitCreateNew"), $("#belongOrgUnitButtonCreateNew"));
				clearAceInputFile($("#uploadedFile"));
				clearWindowSelection();
			});
			$("#newPartyGroupWindow").on('open', function(event){
				if(globalObject.rootPartyArr.length > 0){
					$("#jqxTreeOrgUnitCreateNew").jqxTree('selectItem', $("#" + globalObject.rootPartyArr[0].partyId + "_treeNew")[0]);
				}
			});
		}
		
		var initDropDownList = function(){
			//createJqxDropDownList(roleTypeData, $("#roleTypeList"), "roleTypeId", "description", 25, "98%");
			createJqxDropDownList([], $("#wardPartyGroupNew"), "geoId", "geoName", 25, "98%");
			createJqxDropDownList([], $("#districtPartyGroupNew"), "geoId", "geoName", 25, "98%");
			createJqxDropDownList([], $("#statePartyGroupNew"), "geoId", "geoName", 25, "98%");
			createJqxDropDownList(countryData, $("#countryPartyGroupNew"), "geoId", "geoName", 25, "98%");
		};
		
		var initDropDownListEvent = function(){
			$('#countryPartyGroupNew').on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {countryGeoId: value};
					var url = 'getAssociatedStateListHR';
					globalObject.updateSourceJqxDropdownList($("#statePartyGroupNew"), data, url);
				}
			});
			
			$("#statePartyGroupNew").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {stateGeoId: value};
					var url = 'getAssociatedCountyListHR';
					globalObject.updateSourceJqxDropdownList($("#districtPartyGroupNew"), data, url);
				}
			});
			$("#districtPartyGroupNew").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {districtGeoId: value};
					var url = 'getAssociatedWardListHR';
					globalObject.updateSourceJqxDropdownList($("#wardPartyGroupNew"), data, url);
				}
			});
		};
		
		var btnEvent = function(){
			$("#partyGroupCreateNewSave").click(function(event){
				var valid = $("#newPartyGroupForm").jqxValidator('validate');
				if(!valid){
					return false;
				}
				$(this).attr("disabled", "disabled");
				bootbox.dialog(uiLabelMap.AddNewRowConfirm,
					[
						{
						    "label" : uiLabelMap.CommonSubmit,
						    "class" : "icon-ok btn btn-small btn-primary",
						    "callback": function() {
						    	actionCreateNewPartyGroup.createPartyGroup();
						    	$("#partyGroupCreateNewSave").removeAttr("disabled");
						    }
						},
						{
							  "label" : uiLabelMap.CommonCancel,
				    		   "class" : "btn-danger icon-remove btn-small",
				    		   "callback": function() {
				    			   $("#partyGroupCreateNewSave").removeAttr("disabled");
				    		   }
						}
					]		
				);
			});
			$("#partyGroupCreateNewCancel").click(function(event){
				actionCreateNewPartyGroup.cancelCreatePartyGroup();
			});
			$("#newPartyGroupWindow").on('open', function(event){
				if(typeof(globalObject.countryGeoIdDefault) != 'undefined'){
					$("#countryPartyGroupNew").jqxDropDownList('clearSelection');
					$("#countryPartyGroupNew").jqxDropDownList('selectItem', globalObject.countryGeoIdDefault);
				}
			});
		};
		
		var initJqxValidator = function(){
			$("#newPartyGroupForm").jqxValidator({
				rules: [
					{input: '#jqxTreeOrgUnitCreateNew', message: uiLabelMap.CommonRequired, action: 'blur', 
						rule:  function (input, commit) {
							var value = $("#jqxTreeOrgUnitCreateNew").jqxTree('getSelectedItem').value;
							if(!value){
								return false;
							}
							return true;
						}
					},
					{input: '#orgUnitNameCreateNew', message: uiLabelMap.CommonRequired, action: 'blur', rule: 'required'},
					{input: '#orgUnitIdCreateNew', message: uiLabelMap.CommonRequired, action: 'blur', rule: 'required'},
					{input: '#roleTypeListDropDownButton', message: uiLabelMap.CommonRequired, action: 'blur',
						 rule: function(input, commit){
							var value = $("#jqxTreeRoleTypeNew").jqxTree('getSelectedItem');
							if(!value){
								return false;
							}
							return true;
						 }	
					},
					{input: '#countryPartyGroupNew', message: uiLabelMap.CommonRequired, action: 'blur',
						rule: function(input, commit){
							var address = $("#addressOrgUnitCreateNew").val();
							if(address && !input.val()){
								return false;
							}
							return true;
						}	
					},
					{input: '#statePartyGroupNew', message: uiLabelMap.CommonRequired, action: 'blur',
						rule: function(input, commit){
							var address = $("#addressOrgUnitCreateNew").val();
							if(address && !input.val()){
								return false;
							}
							return true;
						}	
					},
					{
						input : '#orgUnitIdCreateNew', message : uiLabelMap.OnlyContainInvalidChar, action : 'blur',
						rule: function (input, commit){
				    		   var value = input.val();
				    		   if(value){
				    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
				    				    return false;
				    				}
				    		   }
				    		   return true;
				    	    }
					},
					{
						input : '#orgUnitNameCreateNew', message : uiLabelMap.InvalidChar, action : 'blur',
						rule : function(input, commit){
							var value = input.val();
							if(value){
								if(validationNameWithoutHtml(value)){
									return false;
								}
							}
							return true;
						}
					},
				]
			});
		};
		
		var createPartyGroup = function(){
			var form = jQuery("#upLoadFileForm");
			var file = form.find('input[type=file]').eq(0);
			if(file.data('ace_input_files')){
				var fileUpload = $('#uploadedFile')[0].files[0];
				jQuery("#_uploadedFile_fileName").val(fileUpload.name);
				jQuery("#_uploadedFile_contentType").val(fileUpload.type);
				var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);	
			}else{
				var dataSubmit = new FormData();				
			}
			dataSubmit.append("partyCode", $("#orgUnitIdCreateNew").val());
			dataSubmit.append("groupName", $("#orgUnitNameCreateNew").val());
			dataSubmit.append("comments", $("#commentsNewPartyGroup").val());
			var address1 = $("#addressOrgUnitCreateNew").val();
			if(address1 && address1.length > 0){
				dataSubmit.append("address1", address1); 
				dataSubmit.append("countryGeoId", $("#countryPartyGroupNew").jqxDropDownList('val'));
				dataSubmit.append("stateProvinceGeoId", $("#statePartyGroupNew").jqxDropDownList('val'));
				dataSubmit.append("districtGeoId", $("#districtPartyGroupNew").jqxDropDownList('val'));
				dataSubmit.append("wardGeoId", $("#wardPartyGroupNew").jqxDropDownList('val'));
			}
			dataSubmit.append("parentPartyGroupId", $("#jqxTreeOrgUnitCreateNew").jqxTree('getSelectedItem').value);
			dataSubmit.append("partyRoleTypeId", $("#jqxTreeRoleTypeNew").jqxTree('getSelectedItem').value);
			
			$.ajax({
				url: "createOrganizationalUnit",
				data: dataSubmit,
				type: 'POST',
				cache: false,			        
		        processData: false, // Don't process the files
		        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
				success: function(data){
					//console.log(data);
					if(data._EVENT_MESSAGE_){
						$('#jqxNotification').jqxNotification('closeLast');
						$('#jqxNotification').jqxNotification({ template: 'info'});
	                	$("#notificationContentNtf").text(data._EVENT_MESSAGE_);
	                	$("#jqxNotification").jqxNotification("open");	
						$("#treePartyGroupGrid").jqxTreeGrid('updateBoundData');
					}else{
						$('#jqxNotification').jqxNotification('closeLast');
						$('#jqxNotification').jqxNotification({template: 'error'});
	                	$("#notificationContentNtf").text(data._ERROR_MESSAGE_);
	                	$("#jqxNotification").jqxNotification("open");						
					}
				},
				complete: function(){
					$("#partyGroupCreateNewSave").removeAttr("disabled");
				}
			});
			$("#newPartyGroupWindow").jqxWindow('close');
		};
		var cancelCreatePartyGroup = function(){
			$("#newPartyGroupWindow").jqxWindow('close');
		};
		
		return {
			init: init,
	    	createPartyGroup: createPartyGroup,
	    	cancelCreatePartyGroup: cancelCreatePartyGroup
		 };
	}());
	
	
	$(document).ready(function(){
		actionCreateNewPartyGroup.init();
	});
	function clearWindowSelection(){
		Grid.clearForm($("#newPartyGroupWindow"));
		$("#jqxTreeRoleTypeNew").jqxTree('selectItem', null);
		$("#jqxTreeRoleTypeNew").jqxTree('collapseAll');;
	}
