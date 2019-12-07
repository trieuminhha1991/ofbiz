var organizationObjectHasSecurity = (function(){
		var _partyIdFromOld = null;
		var _defaultData = {};
		var _editContactMechData = {};
		var _comments = null;
		var _partyId = null;
		var config = {dropDownBtnWidth: 350, treeWidth: 350};
		var init = function(){
            initJqxNotification();
            initJqxWindow();
			initTreeGridEvent();
            initGridEmplInOrg();
			initBtnEvent();
			initJqxDropDownList();
			initJqxDropDownListEvent();
			initJqxInput();

			globalObject.createJqxTreeDropDownBtn($("#jqxTreeOrgUnit"), $("#belongOrgUnitButton"), globalObject.rootPartyArr, "tree", "treeChild", config);
			initJqxValidator();
		};
		var initJqxNotification = function(){
			$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#jqxNotifyContainer"});
		};
		
		var initJqxWindow = function(){
			var initContent = function () {            	
	        	initInput();
	        };
			createJqxWindow($("#partyGroupDetailWindow"), 570, 440, initContent);
            createJqxWindow($("#popupWindowEmplListInOrg"), 920, 500);
			createJqxWindow($("#partyContactMechWindow"), 500, 300);
			$("#partyContactMechWindow").on('open', function(event){
				if(isAddrOfOrgSet()){
					$("#prCountry").val(_defaultData.countryGeoId);
					$("#prAddress").val(_defaultData["address1"]);
				}else if(globalObject.countryGeoIdDefault){
					$("#prCountry").val(globalObject.countryGeoIdDefault);
				}
			});
			$("#partyGroupDetailWindow").on('close', function(event){
				_partyIdFromOld = null;
				_defaultData = {};
				_comments = null;
				_partyId = null;
				clearDropDownContent($("#jqxTreeOrgUnit"), $("#belongOrgUnitButton"));
				Grid.clearForm($(this));
			});
			$("#partyGroupDetailWindow").on('open', function(event){
				if(_comments){
					$("#comments").jqxEditor('val', _comments);
				}
			});
		};
		
		var initTreeGridEvent = function(){
            var contextMenu = $("#contextMenu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup' });
            $("#treePartyGroupGrid").on('contextmenu', function () {
                return false;
            });
            $("#treePartyGroupGrid").on('rowClick', function (event) {
                var args = event.args;
                if (args.originalEvent.button == 2) {
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
            $("#contextMenu").on('itemclick', function (event) {
                var args = event.args;
                var action = $(args).attr("action");
                var rowSelected = $("#treePartyGroupGrid").jqxTreeGrid('getSelection');
                if(action == 'editCommentInfo'){
                    _comments = rowSelected[0].comments;
                    _partyId = rowSelected[0].partyId;
                    openJqxWindow($("#partyGroupDetailWindow"));
                    setJqxWindowContent(rowSelected[0]);

                }else if(action == "emplInOrgList"){
                    _partyId = rowSelected[0].partyId;
                    openJqxWindow($("#popupWindowEmplListInOrg"));
                    updateGridUrl($("#emplListInOrgGrid"), "jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=" + _partyId);
                }
            });
			$('#jqxTreeOrgUnit').on('select', function(event){			
				var id = event.args.element.id;
			    var item = $('#jqxTreeOrgUnit').jqxTree('getItem', event.args.element);
		    	setDropdownContent(event.args.element, $('#jqxTreeOrgUnit'), $("#belongOrgUnitButton"));
		     });
		};
        var initGridEmplInOrg = function(){
            var grid = $("#emplListInOrgGrid");
            var datafields = [{name: 'partyId', type: 'string'},
                {name: 'partyCode', type: 'string'},
                {name: 'fullName', type: 'string'},
                {name: 'emplPositionType', type: 'string'},
                {name: 'department', type: 'string'},
            ];
            var columns = [
                {text: uiLabelMap.EmployeeId, datafield: 'partyCode' , editable: false, cellsalign: 'left', width: '15%', filterable: true},
                {text: uiLabelMap.EmployeeName, datafield: 'fullName', editable: false, cellsalign: 'left', width: '35%', filterable: true},
                {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: '25%', filterable: true},
                {text: uiLabelMap.CommonDepartment, datafield: 'department', editable: false, cellsalign: 'left', width: '25%', filterable: true},
            ];
            var config = {
                width: '100%',
                virtualmode: true,
                showfilterrow: true,
                showtoolbar: false,
                selectionmode: 'singlerow',
                pageable: true,
                sortable: true,
                filterable: true,
                editable: false,
                url: '',
                source: {
                    pagesize: 10
                }
            };
            Grid.initGrid(config, datafields, columns, null, grid);
            $("#popupWindowEmplListInOrg").on('close', function(event){
                $("#emplListInOrgGrid").jqxGrid('clearfilters');
                $("#emplListInOrgGrid").jqxGrid('clearselection');
                $("#emplListInOrgGrid").jqxGrid('gotopage', 0);
                updateGridUrl($("#emplListInOrgGrid"), '');
            });
            $("#closeEmplListInOrg").click(function(e){
                $("#popupWindowEmplListInOrg").jqxWindow('close');
            });
        };
		
		var initJqxValidator = function(){
			$('#partyContactMechWindow').jqxValidator({
				rules : [
			         {
			        	 input : '#prAddress', message : uiLabelMap.CommonRequired, action : 'blur',
			        	 rule : 'required'
			         },
			         {
			        	 input : '#prCountry', message : uiLabelMap.CommonRequired, action : 'blur',
			        	 rule: function (input, commit){
			        		 if(!input.val()){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#prProvince', message : uiLabelMap.CommonRequired, action : 'blur',
			        	 rule: function (input, commit){
			        		 if(!input.val()){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
	         ]
			})
			$('#partyGroupDetailWindow').jqxValidator({
				rules : [
			         {
			        	 input : '#belongOrgUnitButton', message : uiLabelMap.CommonRequired, action : 'blur',
			        	 rule : function(input,commit){
			        		 if(!input.val() && _partyIdFromOld != -1){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#belongOrgUnitButton', message : uiLabelMap.PartyIdCannotIsChildOfItSelf, action : 'blur',
			        	 rule : function(input,commit){
			        		 var parentNew = $("#jqxTreeOrgUnit").jqxTree('getSelectedItem');
			        		 if(_partyIdFromOld != -1 && parentNew){
			        			 var parentNewPartyId = parentNew.value;
			        			 if(_partyId === parentNewPartyId){
			        				 return false;
			        			 }
			        		 }
			        		 return true;
			        	 }
			         }
		         ]
			})
		}
		var initBtnEvent = function(){
				$("#contactMechCancel").click(function(){
					$("#partyContactMechWindow").jqxWindow('close');
				});
				$("#contactMechSave").click(function(){
					editPartyContactMech();
				});
				$("#partyGroupDetailClose").click(function(){
					$("#partyGroupDetailWindow").jqxWindow('close');
				});
				$("#partyGroupSave").click(function(){
					$(this).attr("disabled", "disabled");
					updatePartyGroup();
				});
				$("#addrowbutton").click(function(){
					addNewPartyGroup();
				});
				$("#partyContactMechWindow").bind('close', function(){
					$('#prAddress').jqxInput('val', null);
					$('#prCountry').jqxDropDownList('clearSelection');
					$('#prProvince').jqxDropDownList('clearSelection');
					$('#prDistrict').jqxDropDownList('clearSelection');
					$('#prWard').jqxDropDownList('clearSelection');
				})
		};
		
		var addNewPartyGroup = function(){
			openJqxWindow($("#newPartyGroupWindow"));
		};
		
		var updatePartyGroup = function(){
			if($('#partyGroupDetailWindow').jqxValidator('validate')){
				var data = {};
				data["partyId"] = _partyId;
				data["groupName"] = $("#orgUnitName").val();
				data["comments"] = $("#comments").jqxEditor('val');
				if(_partyIdFromOld != -1){
					data["partyIdFromOld"] = _partyIdFromOld;			
					data["partyIdFromNew"] = $("#jqxTreeOrgUnit").jqxTree('getSelectedItem').value;
				}
				data = $.extend({}, data, _defaultData);
				$.ajax({
					url: "updatePartyOrgInfo",
					type: 'POST',
					data: data,
					success: function(response){
						$("#jqxNotifyContainer").empty();
						$("#jqxNotification").jqxNotification('closeLast');
						if(response.responseMessage == "success"){
							$("#jqxNotification").jqxNotification({template: 'info'});
							$("#notificationContentNtf").text(response.successMessage);
							$("#treePartyGroupGrid").jqxTreeGrid('updateBoundData');			
						}else{
							$("#notificationContentNtf").text(response.errorMessage);
							$("#jqxNotification").jqxNotification({template: 'error'});
						}
						$("#jqxNotification").jqxNotification('open');
					},
					complete: function(){
						$("#partyGroupSave").removeAttr("disabled");
						$("#partyGroupDetailWindow").jqxWindow('close');
					}
				});
			}else{
				$("#partyGroupSave").removeAttr("disabled");
				return;
			}
		};
		
		var setJqxWindowContent = function setJqxWindowContent(row){
			$("#orgUnitId").val(row.partyCode);
	        $("#orgUnitName").val(row.partyName);
	        var partyIdFrom = row.partyIdFrom;
	        _partyIdFromOld = partyIdFrom; 
	        if(partyIdFrom != -1){
	        	$("#belongOrgUnitButton").jqxDropDownButton({disabled:false});
	        	$('#jqxTreeOrgUnit').jqxTree({disabled:false});
	        	$.ajax({
	    			url: 'getAncestorTreeOfPartyGroup',
	    			data: {partyId: row.partyId},
	    			type: 'POST',
	    			success: function(response){
	    				if(response.ancestorTree && response.ancestorTree.length > 0){
	    					for(var i = 0; i < response.ancestorTree.length; i++){
	    						$("#jqxTreeOrgUnit").jqxTree('expandItem', $("#" + response.ancestorTree[i] + "_tree")[0]);
	    					}
	    				}
	    			},
	    			complete: function(jqXHR, textStatus){
	    				if(partyIdFrom){
	    					$("#jqxTreeOrgUnit").jqxTree('selectItem', $("#" + partyIdFrom + "_tree")[0]);
	    				}
	    			}
	    		});
	        }else{
	        	$("#belongOrgUnitButton").jqxDropDownButton({disabled:true});
	        	$('#jqxTreeOrgUnit').jqxTree({disabled:true});
	        	$('#jqxTreeOrgUnit').jqxTree('selectItem', $("#" + row.partyId + "_tree")[0]);
	        }
	        var contactMechId = row.contactMechId;
	        if(contactMechId && !isAddrOfOrgSet()){
	        	_defaultData.postalAddressDesc = row.postalAddress;
	        	getPartyContactMech(contactMechId);
	        }else{
	        	setOrgPostalAddressInfo($("#orgAddress"));
	        }
		};
		
		var isAddrOfOrgSet = function(){
			if(_defaultData.hasOwnProperty("address1") && _defaultData.hasOwnProperty("countryGeoId") && _defaultData.hasOwnProperty("stateProvinceGeoId")){
				return true;
			}else{
				return false;
			}
		};
		
		var setOrgPostalAddressInfo = function(divEle){		
			divEle.empty();
	        if(isAddrOfOrgSet()){
	        	var postalAddressDes = _defaultData.postalAddressDesc;
	        	divEle.append('<a href="javascript:void(0);" onclick="organizationObjectHasSecurity.updatePartyContactMech()" title="' + uiLabelMap.ClickToEdit + '">' + postalAddressDes + '</a>');        	
	        }else{
	        	var buttonEdit = $('<button class="jqx-rc-all-olbius jqx-button jqx-button-olbius ' 
		 						   + 'jqx-widget jqx-widget-olbius jqx-fill-state-pressed '
		 						   +  'jqx-fill-state-pressed-olbius" title="' + uiLabelMap.CommonEdit + '" onclick="organizationObjectHasSecurity.createPartyContactMech()" style="cursor: pointer;">'
		 						   +  '<i class="icon-edit blue"></i></button>');
	        	divEle.append("<span>" + uiLabelMap.HRCommonNotSetting + "<span>");
	        	divEle.append(buttonEdit);
	        }
		};
		var getPartyContactMech = function(contactMechId){
			_defaultData.contactMechId = contactMechId;
			$.ajax({
				url: 'getPostalAddressGeoDetail',
				data: {contactMechId: contactMechId},
				type: 'POST',
				success: function(data){
					if(data.responseMessage == "success"){
						var postalAddress = data.postalAddress;
						if(postalAddress.address1){
							$("#prAddress").val(postalAddress.address1);
						}
						if(postalAddress.countryGeoId){
							$("#prCountry").jqxDropDownList('clearSelection')
							 _defaultData = $.extend({}, _defaultData, postalAddress);
							$("#prCountry").jqxDropDownList('selectItem', postalAddress.countryGeoId);	
						}						
					}
				},
				complete: function(){
					$("#partyContactMechWindow").jqxWindow({disabled: false});		
					setOrgPostalAddressInfo($("#orgAddress"))
				}
			});
		};	
		var updatePartyContactMech = function(){
			$('#partyContactMechWindow').jqxWindow('setTitle', uiLabelMap.UpdateAddress);
			openJqxWindow($("#partyContactMechWindow"));
		};
		
		var createPartyContactMech = function(partyId){
			$('#partyContactMechWindow').jqxWindow('setTitle', uiLabelMap.DAAddNewAddress);
			openJqxWindow($("#partyContactMechWindow"));
		};
		
		var editPartyContactMech = function(){
			if($('#partyContactMechWindow').jqxValidator('validate')){
				_defaultData["address1"] = $("#prAddress").val();
				var countrySelected = $("#prCountry").jqxDropDownList('getSelectedItem');
				var stateProvinceSelected = $("#prProvince").jqxDropDownList('getSelectedItem');
				var districtSelected = $("#prDistrict").jqxDropDownList('getSelectedItem');
				var wardGeoSelected = $("#prWard").jqxDropDownList('getSelectedItem');
				_defaultData["countryGeoId"] = countrySelected.value;
				_defaultData["stateProvinceGeoId"] = stateProvinceSelected.value;
				var postalAddressDesc = $("#prAddress").val();
				if(wardGeoSelected){
					_defaultData["wardGeoId"] = wardGeoSelected.value;
					postalAddressDesc += ", " + wardGeoSelected.label;
				}else{
					delete _defaultData["wardGeoId"];
				}
				if(districtSelected){
					_defaultData["districtGeoId"] = districtSelected.value;
					postalAddressDesc += ", " + districtSelected.label;
				}else{
					delete _defaultData["districtGeoId"];
				}
				postalAddressDesc += ", " + stateProvinceSelected.label;
				postalAddressDesc += ", " + countrySelected.label;
				_defaultData.postalAddressDesc = postalAddressDesc;
				setOrgPostalAddressInfo($("#orgAddress"));
				$('#partyContactMechWindow').jqxWindow('close');
			}else{
				return false;
			}
		};
		
		var initJqxDropDownList = function(){
			createJqxDropDownList(countryData, $("#prCountry"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#prProvince"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#prDistrict"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#prWard"), "geoId", "geoName", 25, "97%");
		};
		
		var initJqxDropDownListEvent = function(){
			$('#prCountry').on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {countryGeoId: value};
					var url = 'getAssociatedStateListHR';
					updateSourceDropdownlist($("#prDistrict"), []);
					updateSourceDropdownlist($("#prWard"), []);
					globalObject.updateSourceJqxDropdownList($("#prProvince"), data, url, _defaultData.stateProvinceGeoId);
				}
			});
			
			$("#prProvince").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {stateGeoId: value};
					var url = 'getAssociatedCountyListHR';
					globalObject.updateSourceJqxDropdownList($("#prDistrict"), data, url, _defaultData.districtGeoId);
				}
			});
			$("#prDistrict").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {districtGeoId: value};
					var url = 'getAssociatedWardListHR';
					globalObject.updateSourceJqxDropdownList($("#prWard"), data, url, _defaultData.wardGeoId);
				}
			});
		};
		
		var initJqxInput = function(){			
			$("#prAddress").jqxInput({theme: 'olbius', height: 20, width: '97%'});
		};
		var initInput = function(){
			$("#orgUnitId").jqxInput({height: 20, width: '96%', theme: 'olbius', disabled: true});  
			$("#orgUnitName").jqxInput({height: 20, width: '96%', theme: 'olbius'});
			$('#comments').jqxEditor({            
		        width: '98%',
		        theme: 'olbiuseditor',
		        tools: 'datetime | clear | backcolor | font | bold italic underline',
		        height: 160,
		    });
		};
    var updateGridUrl = function(grid, url){
        var source = grid.jqxGrid('source');
        source._source.url = url;
        grid.jqxGrid('source', source);
    };

		return{
			init: init,
			updatePartyContactMech: updatePartyContactMech,
			createPartyContactMech: createPartyContactMech
		}
	}());

$(document).ready(function () {
	organizationObjectHasSecurity.init();
});

