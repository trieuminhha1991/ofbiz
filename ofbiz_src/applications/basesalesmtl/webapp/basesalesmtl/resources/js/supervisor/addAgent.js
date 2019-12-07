/* TODO deleted */

Loading.show();
var UpdateMode = false;
$(document).ready(function() {
	if (partyIdPram) {
		UpdateMode = true;
	}
	AddAgent.init();
	Validator.init();
	if (partyIdPram) {
		AddAgent.letUpdate(partyIdPram);
		$("#account-info").addClass('hide');
	} else {
		AdditionalContact.initGrid([]);
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
				    rules: [
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
			            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
			            { input: '#txtProductStore', message: multiLang.fieldRequired, action: 'change',
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
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


if (typeof (AddAgent) == "undefined") {
	var AddAgent = (function() {
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
							var url = UpdateMode?"updateAgent":"createAgent";
							var logoImageUrl;
							if ($('#logoImageUrl').prop('files')[0]) {
								logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
							}
							var data = AddAgent.getValue();
							data.logoImageUrl = logoImageUrl;
							DataAccess.execute({ url: url, data: data }, AddAgent.notify);
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
				InternalUtil.initComboboxGeo("", "WARD", "txtWard");
			    InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
			    InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
			    InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
			    var source = { datatype: "json",
						datafields: [{ name: "productStoreId" },
						             { name: "storeName" }],
						             url: "loadProductStores?getAll=Y"};
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txtProductStore").jqxComboBox({ theme: 'olbius', source: dataAdapter, width: 218, height: 30, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true, dropDownHeight: 150});
				
				var initRouteDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "routeId", type: "string" },
					                  { name: "RoutePartyCode", type: "string" },
					                  { name: "groupName", type: "string" }];
					var columns = [{text: multiLang.BsRouteId, datafield: "RoutePartyCode", width: 150},
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
						var str = row.groupName + "[" + row.RoutePartyCode + "]";
						return str;
					});
				};
				initRouteDrDGrid($("#divRoute"),$("#jqxgridRoute"), 600);
			}
		    var initDistributorDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{text: multiLang.DADistributorId, datafield: "partyCode", width: 150},
				               {text: multiLang.DADistributorName, datafield: "groupName"}];
				GridUtils.initDropDownButton({
					url: "JQGetListDistributor&sD=N", autorowheight: true, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridDistributor").jqxGrid("clearfilters");
								return true;
							}
						}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var str = row.groupName + "[" + row.partyCode + "]";
					return str;
				});
			};
			initDistributorDrDGrid($("#divDistributor"),$("#jqxgridDistributor"), 600);
			
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
				InternalUtil.initEventComboboxGeo("PROVINCE", "txtCountry", "txtProvince", "", "COUNTRY");
				InternalUtil.initEventComboboxGeo("DISTRICT", "txtProvince", "txtCounty", "txtCountry", "PROVINCE");
				InternalUtil.initEventComboboxGeo("WARD", "txtCounty", "txtWard", "txtProvince", "DISTRICT");
				InternalUtil.initEventComboboxGeo("", "txtWard", null, "txtCounty", "WARD");
				$("#txtCountry").on("bindingComplete", function (event) {
		    		if (countryGeoId) {
		    			$("#txtCountry").jqxComboBox("val", countryGeoId);
		    			countryGeoId = "VNM";
		    		}
		    	});
				$("#txtProvince").on("bindingComplete", function (event) {
					if (stateProvinceGeoId) {
						$("#txtProvince").jqxComboBox("val", stateProvinceGeoId);
						if (!$("#txtProvince").jqxComboBox("getSelectedItem")) {
							$("#txtProvince").jqxComboBox("clearSelection");
							stateProvinceGeoId = null;
						}
					}
				});
				$("#txtCounty").on("bindingComplete", function (event) {
					if (districtGeoId) {
						$("#txtCounty").jqxComboBox("val", districtGeoId);
						if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
							$("#txtCounty").jqxComboBox("clearSelection");
							districtGeoId = null;
						}
					}
				});
				$("#txtWard").on("bindingComplete", function (event) {
					if (wardGeoId) {
						$("#txtWard").jqxComboBox("val", wardGeoId);
						if (!$("#txtWard").jqxComboBox("getSelectedItem")) {
							$("#txtWard").jqxComboBox("clearSelection");
							wardGeoId = null;
						}
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
				$("#divDistributor").on("close", function (event) {
					var value = Grid.getDropDownValue($("#divDistributor")).trim();
					if (value) {
						var dataSelected = LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore"));
						$("#txtProductStore").jqxComboBox('clearSelection');
						var data = DataAccess.getData({
							url: "loadProductStores",
							data: {payToPartyId: value, getAll: "N"},
							source: "productStores"});
						var productStores = new Array();
						for ( var x in data) {
							productStores.push(data[x].productStoreId);
						}
						dataSelected = _.union(dataSelected, productStores);
						productStoreIds = _.difference(productStoreIds, productStores);
						dataSelected = _.difference(dataSelected, productStoreIds);
						for ( var x in dataSelected) {
							$("#txtProductStore").jqxComboBox('selectItem', dataSelected[x]);
						}
						for ( var x in data) {
							productStoreIds.push(data[x].productStoreId);
						}
						
						
						var adapter = $("#jqxgridSalesman").jqxGrid('source');
						if(adapter){
							var url = "jqxGeneralServicer?sname=JQGetListSalesmanAssigned&partyIdFrom=" + value;
							adapter.url = url;
							adapter._source.url = url;
							$("#jqxgridSalesman").jqxGrid('source', adapter);
						}
						Grid.cleanDropDownValue($("#divSalesman"));
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
						distributorId: Grid.getDropDownValue($("#divDistributor")).trim(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim()
				};
			} else {
				var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						countryGeoId: $("#txtCountry").jqxComboBox("val"),
						stateProvinceGeoId: $("#txtProvince").jqxComboBox("val"),
						districtGeoId: $("#txtCounty").jqxComboBox("val"),
						wardGeoId: $("#txtWard").jqxComboBox("val"),
						address1: $("#tarAddress").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
						distributorId: Grid.getDropDownValue($("#divDistributor")).trim(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim(),
						productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore")),
						routeId: Grid.getDropDownValue($("#divRoute")).trim()
				};
			}
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			value = _.extend(value, Representative.getValue(), AdditionalContact.getValue());
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
				Grid.setDropDownValue($("#divDistributor"), data.distributorId, data.distributor);
				Grid.setDropDownValue($("#divSalesman"), data.salesmanId, data.salesman);
			}
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadAgentInfo",
				data: {partyId: partyId},
				source: "agentInfo"});
			AddAgent.setValue(data);
			if (data.contacts) {
				AdditionalContact.initGrid(data.contacts);
			}
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
				if (UpdateMode) {
					$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				} else {
					$("#notificationContentNestedSlide").text(multiLang.addSuccess);
				}
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				setTimeout(function() {
					if (res) {
						if (res['partyId']) {
							location.href = "AgentDetail?partyId=" + res['partyId'];
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
				AdditionalContact.init();
			},
			getValue: getValue,
			setValue: setValue,
			letUpdate: letUpdate,
			notify: notify
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