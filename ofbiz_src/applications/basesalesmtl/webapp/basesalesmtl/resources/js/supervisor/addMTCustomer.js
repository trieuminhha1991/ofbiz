Loading.show();
var UpdateMode = false;
$(document).ready(function() {
	if (partyIdPram) {
		UpdateMode = true;
	}
	AddMTCustomer.init();
	Validator.init();
	if (partyIdPram) {
		AddMTCustomer.letUpdate(partyIdPram);
		$("#account-info").addClass('hide');
	}
	setTimeout(function() {
		Loading.hide();
	}, 2000);
});

if (typeof (Validator) == "undefined") {
	var Validator = (function() {
		var init = function() {
			if (UpdateMode) {
				$("#generalInfo").jqxValidator({
				    rules: [{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
						rule: function (input, commit) {
							if (!UpdateMode) {
								return true;
							}
							if (input.val()) {
								return true;
							}
							return false;
						}
	          		},
					{ input: '#partyCode', message: multiLang.DmsPartyCodeAlreadyExists, action: 'change', 
						rule: function (input, commit) {
							var check = DataAccess.getData({
									url: "checkPartyCode",
									data: {partyId: partyIdPram, partyCode: input.val()},
									source: "check"});
							if ("false" == check) {
								return false;
							}
							return true;
						}
					},
					{ input: '#partyCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					/*{ input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
						rule: function (input, commit) {
							var value = input.jqxDropDownButton("val").trim();
							if (value) {
								return true;
							}
							return false;
						}
					},*/
		            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
		            { input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: "keyup, blur",
				    	rule: function (input, commit) {
							if (input.val()) {
								return true;
							}
							return false;
						}
		            }],
		           position: 'bottom'
				});
				$("#representativeInfo").jqxValidator({
				    rules: [{ input: '#txtRBirthDate', message: multiLang.dateNotValid, action: 'valueChanged', 
					           	rule: function (input, commit) {
					           		var currentTime = new Date().getTime();
					           		var value = 0;
					           		input.jqxDateTimeInput('getDate')?value=input.jqxDateTimeInput('getDate').getTime():value;
					           		if (value == 0) {
										return true;
									}
					           		if (currentTime > value) {
					           			return true;
					           		}
					           		return false;
					           	}
					        }],
				           position: 'bottom'
				});
			} else {
				$("#generalInfo").jqxValidator({
				    rules: [{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
						rule: function (input, commit) {
							if (!UpdateMode) {
								return true;
							}
							if (input.val()) {
								return true;
							}
							return false;
						}
	          		},
					{ input: '#partyCode', message: multiLang.DmsPartyCodeAlreadyExists, action: 'change', 
						rule: function (input, commit) {
							var check = DataAccess.getData({
									url: "checkPartyCode",
									data: {partyId: partyIdPram, partyCode: input.val()},
									source: "check"});
							if ("false" == check) {
								return false;
							}
							return true;
						}
					},
					{ input: '#partyCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					/*{ input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
						rule: function (input, commit) {
							var value = input.jqxDropDownButton("val").trim();
							if (value) {
								return true;
							}
							return false;
						}
					},*/
					{ input: "#txtPartyType", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							var value = input.jqxDropDownList("val");
							if (value) {
								return true;
							}
							return false;
						}
					},
		            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
		            { input: '#txtProductStore', message: multiLang.fieldRequired, action: 'change', 
			        	rule: function (input, commit) {
			        		var values = input.jqxComboBox('getSelectedItems');
			        		if (_.isEmpty(values)) {
			        			return false;
			        		}
			        		return true;
			        	}
			        }],
		            position: 'bottom'
				});
				$("#contactInfo").jqxValidator({
				    rules: [{ input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: "keyup, blur",
						    	rule: function (input, commit) {
									if (input.val()) {
										return true;
									}
									return false;
								}
				            },
				            { input: "#txtCountry", message: multiLang.fieldRequired, action: "change",
						    	rule: function (input, commit) {
									if (input.val()) {
										return true;
									}
									return false;
								}
				            },
				            { input: "#txtProvince", message: multiLang.fieldRequired, action: "change",
				            	rule: function (input, commit) {
				            		if (input.val()) {
				            			return true;
				            		}
				            		return false;
				            	}
				            },
				            { input: "#tarAddress", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" }],
				           position: 'bottom'
				});
				$("#representativeInfo").jqxValidator({
				    rules: [{ input: '#txtRBirthDate', message: multiLang.dateNotValid, action: 'valueChanged', 
					           	rule: function (input, commit) {
					           		var currentTime = new Date().getTime();
					           		var value = 0;
					           		input.jqxDateTimeInput('getDate')?value=input.jqxDateTimeInput('getDate').getTime():value;
					           		if (value == 0) {
										return true;
									}
					           		if (currentTime > value) {
					           			return true;
					           		}
					           		return false;
					           	}
					        }],
				           position: 'bottom'
				});
			}
		};
		var hide = function() {
			if (UpdateMode) {
				$("#generalInfo").jqxValidator("hide");
				$("#representativeInfo").jqxValidator("hide");
			} else {
				$("#generalInfo").jqxValidator("hide");
				$("#contactInfo").jqxValidator("hide");
				$("#representativeInfo").jqxValidator("hide");
			}
		};
		var validate = (function() {
			var generalInfo = function() {
				return $("#generalInfo").jqxValidator("validate");
			};
			var representativeInfo = function() {
				return $("#representativeInfo").jqxValidator("validate");
			};
			var contactInfo = function() {
				if (UpdateMode) {
					return true;
				} else {
					return $("#contactInfo").jqxValidator("validate");
				}
			};
			return {
				generalInfo: generalInfo,
				contactInfo: contactInfo,
				representativeInfo: representativeInfo
			}
		})();
		return {
			init: init,
			hide: hide,
			validate: validate
		}
	})();
}

if (typeof (AddMTCustomer) == "undefined") {
	var AddMTCustomer = (function() {
	    var countryCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
        var wardGeoCBB;
	    var representativeOfficeDDB;
		var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId, productStoreIds = new Array();
		var initJqxElements = function() {
			
			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		        if (info.step == 1 && (info.direction == "next")) {
		        	return Validator.validate.generalInfo();
		        } else if (info.step == 2 && (info.direction == "next")) {
		        	return Validator.validate.contactInfo();
		        }
		        if(info.direction == "previous"){
		        	Validator.hide();
		        }
		    }).on('finished', function(e) {
		    	if (!Validator.validate.representativeInfo()){
		    		return false;
		    	}
		    	if (UpdateMode) {
					mesConfirm = multiLang.UpdateConfirm;
				} else {
					mesConfirm = multiLang.CreateNewConfirm;
				}
		    	bootbox.dialog(mesConfirm,
					[{
						"label" : multiLang.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok",
		    		    "callback": function() {
		    		    	Loading.show();
							var url = UpdateMode?"updateMTCustomer":"createMTCustomer";
							var logoImageUrl;
							if ($('#logoImageUrl').prop('files')[0]) {
								logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
							}
							var data = AddMTCustomer.getValue();
							data.logoImageUrl = logoImageUrl;
							DataAccess.execute({ url: url, data: data }, AddMTCustomer.notify);
		    		    }
					},
					{
		    		    "label" : multiLang.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove",
		    		}]
				);
		    });
			
			$("#currencyUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listCurrencyUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#currencyUomId").jqxDropDownList("val", "VND");
			if (!UpdateMode) {
				$("#txtPartyType").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listPartyType, displayMember: "description", valueMember: "partyTypeId", placeHolder: multiLang.filterchoosestring, dropDownHeight: 150});
				wardGeoCBB = InternalUtilNew.initComboboxGeo("", "WARD", "txtWard");
			    districtGeoCBB = InternalUtilNew.initComboboxGeo("", "DISTRICT", "txtCounty");
			    stateProvinceGeoCBB = InternalUtilNew.initComboboxGeo("", "PROVINCE", "txtProvince");
			    countryCBB = InternalUtilNew.initComboboxGeo("", "COUNTRY", "txtCountry");
			    var source = { datatype: "json",
						datafields: [{ name: "productStoreId" },
						             { name: "storeName" }],
						             url: "loadProductStores?getAll=Y"};
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txtProductStore").jqxComboBox({ theme: 'olbius', source: dataAdapter, width: 218, height: 30, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true, dropDownHeight: 150});
				
				var initRouteDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "routeId", type: "string" },
					                  { name: "groupName", type: "string" }];
					var columns = [{text: multiLang.BsRouteId, datafield: "routeId", width: 150},
					               {text: multiLang.BSRouteName, datafield: "groupName"}];
					GridUtils.initDropDownButton({
						url: "", filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "routeId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridRoute").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
					}, datafields, columns, null, grid, dropdown, "routeId", function(row){
						var str = row.groupName + "[" + row.routeId + "]";
						return str;
					});
				};
				initRouteDrDGrid($("#divRoute"),$("#jqxgridRoute"), 600);

                var initConsigneeGrid = function(dropdown, grid, width){
                    var datafields = [{ name: "partyId", type: "string" },
                        { name: "partyCode", type: "string" },
                        { name: "groupName", type: "string" }];
                    var columns = [{text: multiLang.BSCompanyId, datafield: "partyCode", width: 150},
                        {text: multiLang.BSCompanyName, datafield: "groupName"}];
                    GridUtils.initDropDownButton({
                        url: "JQGetListMTConsignee&statusId=PARTY_ENABLED", autorowheight: true, filterable: true, showfilterrow: true,
                        width: width ? width : 600, source: {id: "partyId", pagesize: 5},
                        handlekeyboardnavigation: function (event) {
                            var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                            if (key == 70 && event.ctrlKey) {
                                $("#jqxgridConsignee").jqxGrid("clearfilters");
                                return true;
                            }
                        }, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
                    }, datafields, columns, null, grid, dropdown, "partyId", function(row){
                        var str = row.groupName + "[" + row.partyCode + "]";
                        return str;
                    });
                };
                initConsigneeGrid($("#divConsignee"),$("#jqxgridConsignee"), 600);
			}
		    
			var initSupervisorDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{text: multiLang.BSSupervisorId, datafield: "partyCode", width: 150},
				               {text: multiLang.BSSupervisor, datafield: "groupName"}];
				GridUtils.initDropDownButton({
					url: "JQGetListMTSupervisorDepartment", autorowheight: true, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridSupervisor").jqxGrid("clearfilters");
								return true;
							}
						}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var str = row.groupName + "[" + row.partyCode + "]";
					return str;
				});
			};
			initSupervisorDrDGrid($("#divSupervisor"),$("#jqxgridSupervisor"), 600);
			
			var initSalesmanDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "firstName", type: "string" },
				                  { name: "middleName", type: "string" },
				                  { name: "lastName", type: "string" },
				                  { name: "department", type: "string" }];
				var columns = [{text: multiLang.salesmanId, datafield: "partyId", width: 150},
				               {text: multiLang.DmsPartyLastName, datafield: "lastName", width: 100},
				               {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 100},
				               {text: multiLang.DmsPartyFirstName, datafield: "firstName", width: 100},
				               {text: multiLang.CommonDepartment, datafield: "department", width: 150}];
				GridUtils.initDropDownButton({
					url: "", autorowheight: true, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {id: "partyId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridSalesman").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 218, height: 30, dropDownHorizontalAlignment: "left"}, clearOnClose: 'Y'
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var first = row.firstName ? row.firstName : "";
					var mid = row.middleName ? row.middleName : "";
					var last = row.lastName ? row.lastName : "";
					var str = last + " " + mid + " " + first + "[" + row.partyCode + "]";
					return str;
				});
			};
			initSalesmanDrDGrid($("#divSalesman"),$("#jqxgridSalesman"), 600);

			var configRepresentativeOffice = {
                            useUrl: true,
                            root: 'results',
                            widthButton: '82%',
                            showdefaultloadelement: false,
                            autoshowloadelement: false,
                            dropDownHorizontalAlignment: "right",
                            datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}],
                            columns: [
                                {text: multiLang.BSCompanyId, datafield: 'partyCode', width: '30%'},
                                {text: multiLang.BSCompanyName, datafield: 'groupName', width: '70%'},
                            ],
                            url: "JQGetListMTRepresentativeOffices&statusId=PARTY_ENABLED",
                            useUtilFunc: true,
                            key: 'partyId',
                            keyCode: 'partyCode',
                            description: ['groupName'],
                            autoCloseDropDown: true,
                            filterable: true,
                            sortable: true,
                        };
            representativeOfficeDDB = new OlbDropDownButton($("#divRepresentativeOffice"), $("#jqxgridRepresentativeOffice"), null, configRepresentativeOffice, []);

		    $("#jqxNotificationNestedSlide").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
        };
		var handleEvents = function() {
			$("#logoImage").click(function() {
				$("#logoImageUrl").click();
			});
			$("#logoImageUrl").change(function(){
				Images.readURL(this, $("#logoImage"));
			});
			if (!UpdateMode) {
                countryCBB.selectListener(function(itemData, index){
                    if(itemData){
        			stateProvinceGeoCBB.clearAll();
        			districtGeoCBB.clearAll();
        			wardGeoCBB.clearAll();
                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'PROVINCE', stateProvinceGeoCBB, "");
                    }
                });
                stateProvinceGeoCBB.selectListener(function(itemData, index){
                    if(itemData){
        			districtGeoCBB.clearAll();
        			wardGeoCBB.clearAll();
                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'DISTRICT', districtGeoCBB, "");
                    }
                });
                districtGeoCBB.selectListener(function(itemData, index){
        			wardGeoCBB.clearAll();
                    if(itemData){
                        InternalUtilNew.updateComboBoxGeo(itemData.value, 'WARD', wardGeoCBB, "");
                    }
                });
				$("#divSalesman").on("close", function (event) {
					var value = Grid.getDropDownValue($("#divSalesman")).trim();
					if (value) {
						var adapter = $("#jqxgridRoute").jqxGrid('source');
						if(adapter){
							adapter.url = "jqxGeneralServicer?sname=JQGetListRoutes&distinct=Y&partyId=" + value;
							adapter._source.url = "jqxGeneralServicer?sname=JQGetListRoutes&distinct=Y&partyId=" + value;
							$("#jqxgridRoute").jqxGrid('source', adapter);
						}
						Grid.cleanDropDownValue($("#divRoute"));
					}
				});
				$("#txtPartyType").on('change', function (event){
				    var args = event.args;
                    if (args) {
                    // index represents the item's index.
                        var index = args.index;
                        var item = args.item;
                        // get item's label and value.
                        var label = item.label;
                        var value = item.value;
                        var type = args.type; // keyboard, mouse or null depending on how the item was selected.
                        if (value == "SUPERMARKET"){
                            $("#divRepresentativeOfficeId").show();
                        }else{
                            $("#divRepresentativeOfficeId").hide();
                            representativeOfficeDDB.clearAll();
                        }
                    }
				});
			}
		};
		var getValue = function() {
			if (UpdateMode) {
				var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim(),
						representativeOfficeId: representativeOfficeDDB.getValue()?representativeOfficeDDB.getValue():"",
						supervisorId: Grid.getDropDownValue($("#divSupervisor")).trim(),
				};
			} else {
				var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						partyTypeId: $("#txtPartyType").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						countryGeoId: OlbCore.isNotEmpty(countryCBB.getValue())?countryCBB.getValue():"",
						stateProvinceGeoId:OlbCore.isNotEmpty(stateProvinceGeoCBB.getValue())?stateProvinceGeoCBB.getValue():"",
						districtGeoId: OlbCore.isNotEmpty(districtGeoCBB.getValue())?districtGeoCBB.getValue():"",
						wardGeoId: OlbCore.isNotEmpty(wardGeoCBB.getValue())?wardGeoCBB.getValue():"",
						address1: $("#tarAddress").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim(),
						representativeOfficeId: representativeOfficeDDB.getValue()?representativeOfficeDDB.getValue():"",
						supervisorId: Grid.getDropDownValue($("#divSupervisor")).trim(),
						productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore")),
						routeId: Grid.getDropDownValue($("#divRoute")).trim(),
						consigneeId: Grid.getDropDownValue($("#divConsignee")).trim()
				};
			}
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			value = _.extend(value, Representative.getValue());
			return value;
		};
		var extendId = new Object();
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#partyCode").val(data.partyCode);
				$("#groupName").val(data.groupName);
				$("#groupName").val(data.groupNameLocal);
				$("#officeSiteName").val(data.officeSiteName);
				$("#comments").val(data.comments);
				$("#currencyUomId").jqxDropDownList("val", data.currencyUomId);
				$("#taxAuthInfos").val(data.taxAuthInfos);
				
				if (!UpdateMode) {
					$("#txtCountry").jqxComboBox("val", data.countryGeoId);
					$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
					$("#txtCounty").jqxComboBox("val", data.districtGeoId);
					$("#txtWard").jqxComboBox("val", data.wardGeoId);
					$("#txtPartyType").jqxDropDownList("val", data.partyTypeId),
					stateProvinceGeoId = data.stateProvinceGeoId;
					districtGeoId = data.districtGeoId;
					wardGeoId = data.wardGeoId;
					$("#tarAddress").val(data.address1);
				}
				Representative.setValue(data.representative);
				$("#txtPhoneNumber").val(data.contactNumber);
				$("#txtEmailAddress").val(data.infoString);
				extendId.contactNumberId = data.contactNumberId;
				extendId.infoStringId = data.infoStringId;
				extendId.addressId = data.addressId;
				extendId.taxAuthInfosfromDate = data.taxAuthInfosfromDate;
				extendId.partyId = data.partyId;
				if (data.logoImageUrl) {
					$("#logoImage").attr("src", data.logoImageUrl);
				}
				//console.log(data.representativeOfficeId);
				Grid.setDropDownValue($("#divSalesman"), data.salesmanId, data.salesman);
				Grid.setDropDownValue($("#divSupervisor"), data.supervisorId, data.supervisor);
				if (data.partyTypeId == "SUPERMARKET"){
                    $("#divRepresentativeOfficeId").show();
                    representativeOfficeDDB.selectItem([data.representativeOfficeId]);
                }
			}
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadMTCustomerInfo",
				data: {partyId: partyId},
				source: "MTCustomerInfo"});
			AddMTCustomer.setValue(data);
		};
		var notify = function(res) {
			Loading.hide();
			$('#jqxNotificationNestedSlide').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'error'});
				$("#notificationContentNestedSlide").text(multiLang.updateError);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'info'});
				$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				setTimeout(function() {
					if (res) {
						if (res['partyId']) {
							location.href = "MTCustomerDetail?partyId=" + res['partyId'];
						}
					}
				}, 2000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				Representative.init();
			},
			getValue: getValue,
			setValue: setValue,
			letUpdate: letUpdate,
			notify: notify
		};
	})();
}
if (typeof (InternalUtilNew) == "undefined") {
	var InternalUtilNew = (function() {
		var initComboboxGeo = function(geoId, geoTypeId, elementObj){
			var url = "";
			if(geoTypeId != "COUNTRY" && geoId){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			}else if(geoTypeId == "COUNTRY"){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
			}

			var configGeo = {
			    width: 218, height: 30, dropDownHeight: 150,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: url,
				key: 'geoId',
				value: 'geoName',
				root: 'listGeo',
				autoDropDownHeight: false,
				datafields: [{name: "geoId"}, {name: "geoName"}],
			}
			return new OlbComboBox($("#" + elementObj), null, configGeo, []);
		};
		var updateComboBoxGeo = function(geoId, geoTypeId, comboBoxOLB, defaultValue){
			comboBoxOLB.updateSource('autoCompleteGeoAjax?geoTypeId=' + geoTypeId + "&geoId=" + geoId, null, function(){
				if (defaultValue) {
					comboBoxOLB.selectItem([defaultValue]);
				}
			});
		};
		return {
			initComboboxGeo: initComboboxGeo,
			updateComboBoxGeo: updateComboBoxGeo
		};
	})();
}

if (typeof (InternalUtil) == "undefined") {
	var InternalUtil = (function() {
		var initComboboxGeo = function(geoId, geoTypeId, element) {
			var url = "";
			if(geoTypeId != "COUNTRY" && geoId){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			}else if(geoTypeId == "COUNTRY"){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
			}
			var source = { datatype: "json",
					datafields: [{ name: "geoId" },
					             { name: "geoName" }],
					             url: url,
					             cache: true };
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#" + element).jqxComboBox({ source: dataAdapter, theme: "olbius", displayMember: "geoName", valueMember: "geoId",
				width: 218, height: 30, dropDownHeight: 150});
		};
		var initEventComboboxGeo = function (geoTypeId, element, elementAffected, elementParents, thisGeoTypeId) {
			$("#" + element).on("change", function (event) {
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    if (item) {
				    	var label = item.label;
					    var value = item.value;
					    if (elementAffected) {
					    	initComboboxGeo(value, geoTypeId, elementAffected);
						}
					}
				}
			});
		};
		return {
			initComboboxGeo: initComboboxGeo,
			initEventComboboxGeo: initEventComboboxGeo
		};
	})();
}
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            img.attr("src", e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		return {
			readURL: readURL,
		};
	})();
}