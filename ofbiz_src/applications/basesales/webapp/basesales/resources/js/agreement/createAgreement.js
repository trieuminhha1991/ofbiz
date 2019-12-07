$(document).ready(function () {
	BaseLayer.init();
	if (partyTypeIdParam) {
		if (partyTypeIdParam == "PERSON") {
			partyTypeIdParam = "FAMILY";
		}
		PartyFromInfo.setValue({agreementType: partyTypeIdParam, partyIdFrom: partyIdParam});
	}
	$("body").ajaxStart(function() {
		$("body").css({"cursor":"progress"});
		$("a").css({"cursor":"progress"});
	}).ajaxStop(function() {
		setTimeout(function() {
			$("body").css({"cursor": "default"});
			$("a").css({"cursor":"pointer"});
		}, 1000);
	}).ajaxError(function( event, jqxhr, settings, thrownError ) {
	});
});

if (typeof (BaseLayer) == "undefined") {
	var BaseLayer = (function () {
		var initJqxElements = function () {
			$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function () {
			$("#btnSave").click(function() {
				$("#collapseOne").collapse("show");
				$("a[href='#collapseOne']").removeClass("collapsed");
				if ($("#agreementInfo").jqxValidator("validate")) {
					disableElements();
				}
				setTimeout(function() {
					if ($("#agreementInfo").jqxValidator("validate")) {
						var totalData = new Object();
						totalData.productInAgreement = JSON.stringify(ProductLayer.getProductInAgreement());
						totalData = _.extend(totalData, AgreementInfo.getValue(), PartyFromInfo.getValue(), PartyToInfo.getValue(), AgreementTerm.getValue());
						if (agreementIdParam) {
							totalData.agreementId = agreementIdParam;
							totalData = _.extend(totalData, UpdateMode.agreementTermIds);
							totalData.listProductsSaved = JSON.stringify(UpdateMode.listProductsSaved);
							executeData(totalData, "DmsUpdateAgreementAjax");
						} else {
							executeData(totalData, "DmsCreateAgreementAjax");
						}
					}
				}, 600);
			});
			$("a").on("click", function() {
				$("#agreementInfo").jqxValidator("hide");
			});
			$("#editAddessDelivery").on("click", function() {
				var partyId = Grid.getDropDownValue($("#divPartyFrom"));
				if (partyId.trim()) {
					setCookie("fromCreateAgreement=Yes");
					switch ($("#divAgreementType").val()) {
					case "BUSINESSES":
						window.open( "AddNewContactBusiness?partyId=" + partyId,"_blank");
						break;
					case "SCHOOL":
						window.open( "EditContactSchool?partyId=" + partyId,"_blank");
						break;
					case "FAMILY":
						window.open( "AddNewContactFamily?partyId=" + partyId,"_blank");
						break;
					default:
						break;
					}
				} else {
					$("#divPartyFrom").notify(multiLang.DmsPleaseChoicePartner, { position: "right bottom", className: "error" });
				}
			});
		};
		var initValidator = function () {
			$("#agreementInfo").jqxValidator({
			    rules: [{ input: "#agreementCode", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#divPartyFrom", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input);
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#we_divAddessDelivery", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input);
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divPartyTo", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input);
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divFindRepresentTo", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input);
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divStaffContract", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input);
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divNumberOfMonthsUse", message: multiLang.DmsQuantityNotValid, action: "valueChanged", 
							rule: function (input, commit) {
								var value = input.jqxNumberInput("getDecimal");
								if (value > 0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divTheMinimumSubscriptionValue", message: multiLang.DmsPriceNotValid, action: "valueChanged", 
							rule: function (input, commit) {
								var value = input.jqxNumberInput("getDecimal");
								if (value > 0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divReceiveInfomationMethod", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divStore", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = LocalUtility.getValueSelectedJqxComboBox("divStore");
								if (value.length>0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divPaymentFormality", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divPaymentMethod", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divDeliveryTime", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}	
						},
						{ input: "#divFromDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}	
						},
						{ input: "#divAgreementDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}	
						}]
			});
		};
		var executeData = function (data, url) {
			$.ajax({
				  url: url,
				  type: "POST",
				  data: data,
				  success: function() {}
			  	}).done(function(res) {
			  		var agreementId = res["agreementId"];
			  		$("body").scrollTop(0);
			  		$("#jqxNotification").jqxNotification("closeLast");
			  		if (agreementId) {
			  			if (agreementIdParam) {
			  				createNotification(multiLang.DmsTheContractWasUpdated + "[" + agreementId + "]", agreementId);
		        			setCookie("updateContact=Yes");
		        		}else {
		        			createNotification(multiLang.DmsTheNewContractWasCreated + "[" + agreementId + "]", agreementId);
		        			setCookie("newContact=Yes");
						}
						localStorage.setItem("createAgreementSuccess", true);
		        		window.location.href = "ListAgreements";
					}else {
						$("#jqxNotification").jqxNotification({ template: "error"});
		    			$("#notificationContent").text(multiLang.updateError);
		              	$("#jqxNotification").jqxNotification("open");
					}
			  	});
		};
		var createNotification = function (messages, agreementId) {
			var data = {roleTypeId: "CALLCENTER_MANAGER",
					header: messages,
					action: "ListAgreements",
					sendToSender: "Y",
					ntfType: "ONE",
					targetLink: "agreementId=" + agreementId};
			DataAccess.execute({
				url: "createNotification",
				data: data});
		}
		var initListenerVisibilityChange = function () {
			var hidden, visibilityChange; 
			if (typeof document.hidden !== "undefined") {
			  hidden = "hidden";
			  visibilityChange = "visibilitychange";
			} else if (typeof document.mozHidden !== "undefined") {
			  hidden = "mozHidden";
			  visibilityChange = "mozvisibilitychange";
			} else if (typeof document.msHidden !== "undefined") {
			  hidden = "msHidden";
			  visibilityChange = "msvisibilitychange";
			} else if (typeof document.webkitHidden !== "undefined") {
			  hidden = "webkitHidden";
			  visibilityChange = "webkitvisibilitychange";
			}
			var handleVisibilityChange = function () {
				if (document[hidden]) {
//					  console.log("leave");
				  } else {
					  if(getCookie().checkContainValue("updateContact")){
						  deleteCookie("updateContact");
						  var partyId = Grid.getDropDownValue($("#divPartyFrom"));
						  if (partyId.trim()) {
							  getPartyFromInformation({partyId: partyId});
							  reloadPartyFromAddessDrdGrid("jqxGeneralServicer?sname=jqxGetAddressPartner&contactMechPurposeTypeId=SHIPPING_LOCATION&partyId=" + partyId);
						  }
					  }
				  }
			};
			document.addEventListener(visibilityChange, handleVisibilityChange, false);
		};
		var disableElements = function () {
			 $("#btnSave").attr("disabled",true);
		};
		return {
			init: function() {
				initJqxElements();
				AgreementInfo.init();
				PartyFromInfo.init();
				PartyToInfo.init();
				AgreementTerm.init();
				ProductLayer.init();
				handleEvents();
				initValidator();
				initListenerVisibilityChange();
			}
		};
	})();
}

if (typeof (AgreementInfo) == "undefined") {
	var AgreementInfo = (function () {
		var initJqxElements = function () {
			var initStaffContractDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "firstName", type: "string" },
				                  { name: "middleName", type: "string" },
				                  { name: "lastName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "partyCode", width: 150},
				               {text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150},
				               {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150},
				               {text: multiLang.DmsPartyFirstName, datafield: "firstName"}];
				GridUtils.initDropDownButton({
					url: "JQGetListStaffContract", autorowheight: true, filterable: true, showfilterrow: true, 
					width: width ? width : 600, source: {id: "partyId"},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridStaffContract").jqxGrid("clearfilters");
								return true;
							}
						}
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var first = row.firstName ? row.firstName : "";
					var mid = row.middleName ? row.middleName : "";
					var last = row.lastName ? row.lastName : "";
					var str = last + " " + mid + " " + first;
					return str;
				});
			};
			initStaffContractDrDGrid($("#divStaffContract"),$("#jqxgridStaffContract"), 600);
			
			$("#divStore").jqxComboBox({theme: theme,source: productStores, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true, width: 200});
		};
		var handleEvents = function () {
			$("#agreementCode").keyup(function(){
				var val = $(this).val();
				val = val.replace(/[^\w]/gi, "");
				var res = "";
				for(var x = 0; x < val.length; x++){
					res += val[x].toUpperCase();
				}
				$(this).val(res);
			});
			$("#divStore").on("change", function (event) {
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    if (Grid.getDropDownValue($("#divPartyFrom"))) {
				    	AgreementTerm.reloadPaymentFormality(Grid.getDropDownValue($("#divPartyFrom")), value);
					}
				}
			});
		};
		var getValue = function () {
			var value = new Object();
			value.agreementCode = $("#agreementCode").val();
			value.description = $("#tarDescription").val();
			value.agreementTypeId = "SALES_AGREEMENT";
			value.statusId = "AGREEMENT_CREATED";
			value.productStoreId = LocalUtility.getValueSelectedJqxComboBox("divStore");
			value.staffContract = Grid.getDropDownValue($("#divStaffContract"));
			return value;
		};
		var setValue = function (data) {
			if (!_.isEmpty(data)) {
				$("#agreementCode").val(data.agreementCode);
				$("#tarDescription").val(data.description);
				LocalUtility.setValueJqxComboBox("divStore", data.listProductStoreId);
				staffContract = data.staffContract;
				Grid.setDropDownValue($("#divStaffContract"), data.staffContract, LocalUtility.getPartyName(data.staffContract));
			}
		};
		var disableElements = function () {
//			$("#divStaffContract").jqxDropDownButton({ disabled: true });
		};
		return {
			init: function () {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			disableElements: disableElements
		};
	})();
}
if (typeof (PartyFromInfo) == "undefined") {
	var PartyFromInfo = (function () {
		var initJqxElements = function () {
			var listAgreementType = [{textValue: "BUSINESSES", description: multiLang.DmsAgreementBusinesses},
			                        {textValue: "SCHOOL", description: multiLang.DmsAgreementSchool},
			                        {textValue: "FAMILY", description: multiLang.DmsAgreementSubscribers}];
			$("#divAgreementType").jqxDropDownList({ theme: theme, width: 200, height: 25, source: listAgreementType, displayMember: "description", valueMember: "textValue", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			
			var initPartyFromDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" },
				                  { name: "partyTypeId", type: "string" }];
		        var columns = [{text: multiLang.DmsPartnerId, datafield: "partyCode", width: 200},
		                       {text: multiLang.DmsPartnerName, datafield: "groupName"}];
		    	GridUtils.initDropDownButton({url: "", autorowheight: true, filterable: true, showfilterrow: true,
		    		width: width ? width : 600, source: {id: "partyId"},
		    			handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridPartyFrom").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}, dropdown: {dropDownHorizontalAlignment: "left"}}, datafields, columns, null, grid, dropdown, "partyId", "groupName");
			};
			initPartyFromDrDGrid($("#divPartyFrom"),$("#jqxgridPartyFrom"), 600);
			
			var initPartyFromAddessDrdGrid = function(dropdown, grid, width){
				var datafields = [{ name: "contactMechId", type: "string" },
					            { name: "address1", type: "string" },
					            { name: "countryGeoId", type: "string" },
					            { name: "stateProvinceGeoId", type: "string" },
					            { name: "districtGeoId", type: "string" },
					            { name: "wardGeoId", type: "string" },
					            { name: "countryGeo", type: "string" },
					            { name: "stateProvinceGeo", type: "string" },
					            { name: "districtGeo", type: "string" },
					            { name: "wardGeo", type: "string" },
					            { name: "postalCode", type: "string" }];
		        var columns = [{ text: multiLang.DmsAddress1, datafield: "address1", width: "30%"},
			   					{ text: multiLang.DmsWard, datafield: "wardGeo", width: 200},
								{ text: multiLang.DmsCounty, datafield: "districtGeo", width: 200},
								{ text: multiLang.DmsProvince, datafield: "stateProvinceGeo", width: 200},
								{ text: multiLang.DmsCountry, datafield: "countryGeo", width: 200}];
		    	GridUtils.initDropDownButton({url: "jqxGetAddressPartner&contactMechPurposeTypeId=SHIPPING_LOCATION&partyId=", autorowheight: true, filterable: true,
		    		showfilterrow: true, width: width ? width : 600, source: {id: "contactMechId"},
		    			handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#we_jqxgridAddessDelivery").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}}, datafields, columns, null, grid, dropdown, "contactMechId", "address1");
			};
			initPartyFromAddessDrdGrid($("#we_divAddessDelivery"),$("#we_jqxgridAddessDelivery"), 600);
		};
		var handleEvents = function () {
			$("#divAgreementType").on("change", function (event){
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    cleanPartyFromInfo();
				    $("#jqxgridPartyFrom").jqxGrid("clearfilters");
				    Grid.setDropDownValue($("#divPartyFrom"), null, " ");
				    switch (value) {
					case "FAMILY":
						reloadPartyFromDrDGrid("jqxGeneralServicer?sname=JQGetListPartnerFamily");
						break;
					case "BUSINESSES":
						reloadPartyFromDrDGrid("jqxGeneralServicer?sname=JQGetListPartnerBusinesses");
						break;
					case "SCHOOL":
						reloadPartyFromDrDGrid("jqxGeneralServicer?sname=JQGetListPartnerSchool");
						break;
					default:
						break;
					}
				}
			});
			$("#divPartyFrom").on("close", function (event) {
				var partyId = Grid.getDropDownValue($("#divPartyFrom"));
				if (partyId.trim()) {
					$("#collapseOne").collapse("show");
					$("a[href='#collapseOne']").removeClass("collapsed");
					getPartyFromInformation({partyId: partyId});
					reloadPartyFromAddessDrdGrid("jqxGeneralServicer?sname=jqxGetAddressPartner&contactMechPurposeTypeId=SHIPPING_LOCATION&partyId=" + partyId);
					AgreementTerm.reloadPaymentFormality(partyId, $("#divStore").jqxComboBox("val"));
				}
			});
			$("#we_jqxgridAddessDelivery").on("bindingcomplete", function (event) {
				if ($("#we_jqxgridAddessDelivery").jqxGrid("getrowdata", 0)) {
					$("#we_jqxgridAddessDelivery").jqxGrid("selectrow", 0);
				}
			});
		};
		var cleanPartyFromInfo = function () {
			$("#divRepresentFrom").text("");
			$("#divIdentification").text("");
			$("#divNgayCap").text("");
			$("#divNoiCap").text("");
			$("#divTelecomFrom").text("");
			$("#divEmailFrom").text("");
			if (Grid.getDropDownValue($("#we_divAddessDelivery")).trim()) {
				Grid.setDropDownValue($("#we_divAddessDelivery"), null, " ");
				$("#we_jqxgridAddessDelivery").jqxGrid("clearselection");
				$("#we_jqxgridAddessDelivery").jqxGrid("clear");
			}
			$("#jqxgridPartyFrom").jqxGrid("clearselection");
			$("#jqxgridPartyFrom").jqxGrid("clear");
			$("#agreementInfo").jqxValidator("hide");
		};
		var renderPartnerInformation =  function (data) {
			$("#divRepresentFrom").text(data.partyFullName);
			data.idNumber?data.idNumber=data.idNumber:data.idNumber="";
			$("#divIdentification").text(data.idNumber);
			var idIssueDate = "";
			data.idIssueDate?idIssueDate=(new Date(data.idIssueDate)).toTimeOlbius():idIssueDate;
			$("#divNgayCap").text(idIssueDate);
			$("#divNoiCap").text(data.issuePlace);
			$("#divTelecomFrom").text(data.contactNumber);
			$("#divEmailFrom").text(data.emailAddress);
		};
		var reloadPartyFromDrDGrid = function (newUrl) {
			var currentSource = $("#jqxgridPartyFrom").jqxGrid("source");
			if (currentSource) {
				currentSource._source.url = newUrl;
				$("#jqxgridPartyFrom").jqxGrid({ source: currentSource });
			}
		};
		var reloadPartyFromAddessDrdGrid = function (newUrl) {
			var currentSource = $("#we_jqxgridAddessDelivery").jqxGrid("source");
			if (currentSource) {
				currentSource._source.url = newUrl;
				$("#we_jqxgridAddessDelivery").jqxGrid({ source: currentSource });
			}
		};
		var getPartyFromInformation = function (data) {
			$.ajax({
				url: "getInformationPartner",
				type: "POST",
				async: false,
				data: data,
				success: function() {}
			}).done(function(res) {
				if (res["partnerInfo"]) {
					renderPartnerInformation(res["partnerInfo"]);
				}
			});
		}
		var getValue = function () {
			var value = new Object();
			value.agreementType = $("#divAgreementType").val();
			value.partyIdFrom = Grid.getDropDownValue($("#divPartyFrom"));
			value.roleTypeIdFrom = "CUSTOMER";
			value.termTypeId8 = "SHIPPING_ADDRESS";
			value.textValue8 = Grid.getDropDownValue($("#we_divAddessDelivery"));
			return value;
		};
		var setValue = function (data) {
			if (!_.isEmpty(data)) {
				$("#divAgreementType").jqxDropDownList("val", data.agreementType);
				Grid.setDropDownValue($("#divPartyFrom"), data.partyIdFrom, LocalUtility.getPartyName(data.partyIdFrom));
				Grid.setDropDownValue($("#we_divAddessDelivery"), data.contactMechId, data.address1);
				$("#divPartyFrom").trigger("close");
			}
		};
		var disableElements = function () {
			$("#divAgreementType").jqxDropDownList({ disabled: true });
			$("#divPartyFrom").jqxDropDownButton({ disabled: true });
			$("#we_divAddessDelivery").jqxDropDownButton({ disabled: true });
		};
		return {
			init: function () {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			disableElements: disableElements
		};
	})();
}
if (typeof (PartyToInfo) == "undefined") {
	var PartyToInfo = (function () {
		var initJqxElements = function () {
			var initPartyToDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "partyId", width: 200},
				               {text: multiLang.DmsGroupName, datafield: "groupName"}];
				GridUtils.initDropDownButton({url: "JQGetListPartyTo", autorowheight: true, filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "partyId"},
						handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridPartyTo").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}
				}, datafields, columns, null, grid, dropdown, "partyId", "groupName");
			};
			initPartyToDrDGrid($("#divPartyTo"),$("#jqxgridPartyTo"), 600);
			
			var initRepresentToDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "firstName", type: "string" },
				                  { name: "middleName", type: "string" },
				                  { name: "lastName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "partyCode", width: 150},
				               {text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150},
				               {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150},
				               {text: multiLang.DmsPartyFirstName, datafield: "firstName"}];
				GridUtils.initDropDownButton({url: "JQGetListRepresentTo", autorowheight: true, filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "partyId"},
						handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridFindRepresentTo").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}
				, dropdown: {dropDownHorizontalAlignment: "left"}}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var first = row.firstName ? row.firstName : "";
					var mid = row.middleName ? row.middleName : "";
					var last = row.lastName ? row.lastName : "";
					var str = last + " " + mid + " " + first;
					return str;
				});
			};
			initRepresentToDrDGrid($("#divFindRepresentTo"),$("#jqxgridFindRepresentTo"), 600);
		};
		var handleEvents = function () {
			$("#divFindRepresentTo").on("close", function (event) {
				var partyId = Grid.getDropDownValue($("#divFindRepresentTo"));
				$("#divRepresentTo").text("");
				$("#divTelecomTo").text("");
				$("#divEmailTo").text("");
				$("#divFax").text("");
				$("#divAddress").text("");
				if (partyId.trim()) {
					cleanPartyToInformation();
					getInformationPartyTo({ partyId: partyId });
				}
			});
		};
		var getInformationPartyTo = function (data) {
			$.ajax({
				url: "getInformationPerson",
				type: "POST",
				async: false,
				data: data,
				success: function() {}
			}).done(function(res) {
				renderPartyToInformation(res["partyInfo"]);
			});
		};
		var renderPartyToInformation = function (data) {
			$("#divRepresentTo").text(data.partyFullName);
			$("#divTelecomTo").text(data.contactNumber);
			$("#divEmailTo").text(data.emailAddress);
			$("#divFax").text("");
			var listAddress = data.listAddress;
			for ( var x in listAddress) {
				if (listAddress[x].contactMechPurposeTypeId=="Địa chỉ chính") {
					$("#divAddress").text(listAddress[x].address1);
					break;
				}
			}
		};
		var cleanPartyToInformation = function () {
			$("#divRepresentTo").text("");
			$("#divTelecomTo").text("");
			$("#divEmailTo").text("");
			$("#divFax").text("");
			$("#divAddress").text("");
		};
		var getValue = function () {
			var value = new Object();
			value.partyIdTo = Grid.getDropDownValue($("#divPartyTo"));
			value.roleTypeIdTo = "OWNER";
			value.partyId = Grid.getDropDownValue($("#divFindRepresentTo"));
			value.roleTypeId = "SALES_REP";
			return value;
		};
		var setValue = function (data) {
			if (!_.isEmpty(data)) {
				Grid.setDropDownValue($("#divPartyTo"), data.partyIdTo, LocalUtility.getPartyName(data.partyIdTo));
				Grid.setDropDownValue($("#divFindRepresentTo"), data.partyRepresentId, LocalUtility.getPartyName(data.partyRepresentId));
				$("#divFindRepresentTo").trigger("close");
			}
		};
		var disableElements = function () {
			$("#divPartyTo").jqxDropDownButton({ disabled: true });
			$("#divFindRepresentTo").jqxDropDownButton({ disabled: true });
		};
		return {
			init: function () {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			disableElements: disableElements
		};
	})();
}
if (typeof (AgreementTerm) == "undefined") {
	var AgreementTerm = (function () {
		var divPaymentFormalityDDL;
		
		var initJqxElements = function () {
			$("#divNumberOfMonthsUse").jqxNumberInput({ theme: theme, width: 180, height: 25, inputMode: "simple", spinButtons: true, decimalDigits: 0 });
			$("#divTheMinimumSubscriptionValue").jqxNumberInput({ theme: theme, width: 180, height: 25, inputMode: "advanced", spinMode: "simple", groupSeparator: ".", decimalDigits: 0, digits: 20, max: 99999999999999 });
			var listPaymentMethod = [{termDays: "COD", description: "Sau khi nhận hàng"}, {termDays: 0, description: "1 lần"},
			                         {termDays: 30, description: "Theo tháng"}, {termDays: 90, description: "Theo quý"}];
			$("#divPaymentMethod").jqxDropDownList({ theme: theme, width: 180, height: 25, source: listPaymentMethod, displayMember: "description", valueMember: "termDays", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			$("#divPaymentFormality").jqxDropDownList({ theme: theme, width: 180, height: 25, source: [], displayMember: "description", valueMember: "paymentMethodTypeId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			var listReceiveInfomationMethod = [{textValue: "SMS", description: "Qua điện thoại(SMS)"}, {textValue: "EMAIL", description: "Qua Email"}];
			$("#divReceiveInfomationMethod").jqxDropDownList({ theme: theme, width: 180, height: 25, source: listReceiveInfomationMethod, displayMember: "description", valueMember: "textValue", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			var listDeliveryMethod = [{textValue: "Weekly", description: multiLang.DmsWeekly}, {textValue: "Monthly", description: multiLang.DmsMonthly}];
			$("#deliveryMethod").jqxDropDownList({ theme: theme, width: 180, height: 25, source: listDeliveryMethod, displayMember: "description", valueMember: "textValue", placeHolder: multiLang.DmsPeriod, autoDropDownHeight: true});
			$("#divDeliveryTime").jqxDropDownList({ theme: theme, width: 100, height: 25, source: [], displayMember: "description", valueMember: "textValue", placeHolder: multiLang.filterchoosestring, searchMode: "contains"});
			$("#divDeliveryTime2").jqxDropDownList({ theme: theme, width: 100, height: 25, source: [], displayMember: "description", valueMember: "textValue", placeHolder: multiLang.DmsAnd, searchMode: "contains"});
			$("#divAgreementDate").jqxDateTimeInput({ theme: theme, width: 180, height: 25 });
			$("#divFromDate").jqxDateTimeInput({ theme: theme, width: 180, height: 25 });
		};
		var handleEvents = function () {
			var day = [{textValue: "Sun", description: multiLang.DmsSunday}, {textValue: "Mon", description: multiLang.DmsMonday},
		               {textValue: "Tue", description: multiLang.DmsTuesday}, {textValue: "Wed", description: multiLang.DmsWednesday},
		               {textValue: "Thu", description: multiLang.DmsThursday}, {textValue: "Fri", description: multiLang.DmsFriday},
		               {textValue: "Sat", description: multiLang.DmsSaturday}];
			var date = new Array();
			for (var i = 1; i < 31; i++) {
				date.push({textValue: i, description: multiLang.DmsDay + " " + i});
			}
			$("#deliveryMethod").on("change", function (event){     
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var value = item.value;
				    switch (value) {
					case "Weekly":
						$("#divDeliveryTime").jqxDropDownList({ source: day });
						$("#divDeliveryTime2").jqxDropDownList({ source: day });
						break;
					case "Monthly":
						$("#divDeliveryTime").jqxDropDownList({ source: date });
						$("#divDeliveryTime2").jqxDropDownList({ source: date });
						break;
					default:
						break;
					}
				} 
			});
		};
		var reloadPaymentFormality = function(partyId, productStoreId) {
			if (partyId && productStoreId) {
				var configPaymentMethodType = {
						placeHolder: multiLang.filterchoosestring,
						width: 180,
						height: 25,
						key: "paymentMethodTypeId",
						value: "description",
						autoDropDownHeight: true,
						selectedIndex: 0,
						displayDetail: true,
						dropDownHorizontalAlignment: "right",
						useUrl: true,
						url: "jqxGeneralServicer?sname=JQGetPaymentMethodByCustomerAndStore&partyId=" + partyId + "&productStoreId=" + productStoreId,
					};
				divPaymentFormalityDDL = new OlbDropDownList($("#divPaymentFormality"), null, configPaymentMethodType, []);
			}
		};
		var getValue = function () {
			var value = new Object();
			value.termTypeId1 = "USE_PACKAGE_NETDAYS";
			value.textValue1 = $("#divNumberOfMonthsUse").jqxNumberInput("getDecimal");
			value.termTypeId2 = "FIN_PAY_TOTAL_MIN";
			value.textValue2 = $("#divTheMinimumSubscriptionValue").jqxNumberInput("getDecimal");
			value.termTypeId3 = "FIN_PAYMENT_METHOD";
			value.textValue3 = divPaymentFormalityDDL.getValue();
			value.termTypeId4 = "FIN_PAYMENT_FREQUEN";
			value.textValue4 = $("#divPaymentMethod").val();
			value.termTypeId5 = "DELIVER_DATE_FREQUEN";
			value.textValue5 = $("#divDeliveryTime").val();
			value.termTypeId6 = "DELIVER_DATE_FREQUEN";
			value.textValue6 = $("#divDeliveryTime2").val();
			value.termTypeId7 = "SOLICITATION_METHOD";
			value.textValue7 = $("#divReceiveInfomationMethod").val();
			if ($("#divAgreementDate").jqxDateTimeInput("getDate")) {
				value.agreementDate = $("#divAgreementDate").jqxDateTimeInput("getDate").getTime();
			}
			if ($("#divFromDate").jqxDateTimeInput("getDate")) {
				value.fromDate = $("#divFromDate").jqxDateTimeInput("getDate").getTime();
			}
			return value;
		}
		var setValue = function (data) {
			if (!_.isEmpty(data)) {
				$("#divNumberOfMonthsUse").jqxNumberInput("val", data.packageNetDays);
				$("#divTheMinimumSubscriptionValue").jqxNumberInput("val", data.payTotalMin);
				$("#divPaymentFormality").jqxDropDownList("val", data.paymentMethod);
				$("#divPaymentMethod").jqxDropDownList("val", data.paymentFrequen);
				if (data.deliverDate2 || data.deliverDate) {
					if (isNaN(data.deliverDate2) || isNaN(data.deliverDate)) {
						$("#deliveryMethod").jqxDropDownList("val", "Weekly");
					} else {
						$("#deliveryMethod").jqxDropDownList("val", "Monthly");
					}
				}
				$("#divDeliveryTime").val(data.deliverDate2);
				$("#divDeliveryTime2").val(data.deliverDate);
				$("#divReceiveInfomationMethod").jqxDropDownList("val", data.solicitationMethod);
				if (data.agreementDate) {
					$("#divAgreementDate").jqxDateTimeInput("setDate", new Date(data.agreementDate.time));
				} else {
					$("#divAgreementDate").jqxDateTimeInput("setDate", null)
				}
				if (data.fromDate) {
					$("#divFromDate").jqxDateTimeInput("setDate", new Date(data.fromDate.time));
				} else {
					$("#divFromDate").jqxDateTimeInput("setDate", null);
				}
				
			}
		};
		return {
			init: function () {
				initJqxElements();
				handleEvents();
				reloadPaymentFormality();
			},
			getValue: getValue,
			setValue: setValue,
			reloadPaymentFormality: reloadPaymentFormality
		};
	})();
}
if (typeof (ProductLayer) == "undefined") {
	var ProductLayer = (function () {
		var initProductGrid = function (dataProduct, callback) {
			if(!dataProduct)return;
			$("#jqxgridProduct").jqxTreeGrid({
		           width: "100%",
		           localization: getLocalization(),
		           selectionMode: "singleCell",
		           theme: theme,
		           pagerMode: "advanced",
		           source: dataProduct,
		           pageable: true,
		           editable: true,
		           editSettings: { saveOnPageChange: true, saveOnBlur: true, saveOnSelectionChange: true, cancelOnEsc: true, saveOnEnter: true, editSingleCell: true, editOnDoubleClick: true, editOnF2: true },
		           pageSize: 10,
		           columns: [
					{ text: multiLang.DmsPackageDetails, datafield: "productName", editable: false },
					{ text: multiLang.DmsUnitBottle, datafield: "unitBottle", width: 100, cellsalign: "right", filtertype: "number", editable: false,
						cellsrenderer: function(row, column, value){
							return "<span>1</span>";
						}
					},
					{ text: multiLang.DmsUnitPrice, datafield: "price", width: 100, cellsalign: "right", filtertype: "number", editable: false,
						cellsrenderer: function(row, colum, value){
					        return "<span class=\"text-right\">" + value.toLocaleString(locale) + "</span>";
						}
					},
					{ text: multiLang.DAOrder, datafield: "order", width: 100, cellsalign: "right",
						validation: function (cell, value) {
							if (value >= 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsQuantityNotValid };
						},
						cellsrenderer: function(row, colum, value){
					        return "<span class=\"text-right\">" + value.toLocaleString(locale) + "</span>";
						}
					},
					{ text: multiLang.DmsValueCost, datafield: "valueCost", width: 150, cellsalign: "right", filtertype: "number", editable: false,
						validation: function (cell, value) {
							if (value >= 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsQuantityNotValid };
						},
						cellsrenderer: function(row, colum, value){
					        return "<span class=\"text-right\">" + value.toLocaleString(locale) + "</span>";
						}
					},
					{ text: multiLang.DmsNote, datafield: "note" }]
				});
			callback();
		};
		var _dataProduct = function() {
			var dataProduct;
			$.ajax({
				url: "getListProductForAgreement",
				type: "POST",
				async: false,
				data: {},
				success: function() {}
			}).done(function(res) {
				var listProduct = res["listProduct"];
				var sourceProduct =
			       {
			           localdata: listProduct,
			           datafields:
			           [
			               { name: "productId", type: "string" },
			               { name: "parentId", type: "string" },
			               { name: "productName", type: "string" },
			               { name: "isVirtual", type: "string" },
			               { name: "price", type: "number" },
			               { name: "unitBottle", type: "string" },
			               { name: "order", type: "number" },
			               { name: "valueCost", type: "number" },
			               { name: "note", type: "string" }
			           ],
			           hierarchy:
			           {
			               keyDataField: { name: "productId" },
			               parentDataField: { name: "parentId" }
			           },
			           id: "productId",
			       };
				   dataProduct = new $.jqx.dataAdapter(sourceProduct);
			});
			return dataProduct;
		};
		var lockRowParents = function () {
			var data = $("#jqxgridProduct").jqxTreeGrid("getRows");
			for ( var x in data) {
				if (data[x].isVirtual == "Y" || !data[x].price || data[x].price == 0) {
					$("#jqxgridProduct").jqxTreeGrid("lockRow", data[x].uid);
				}
			}
		};
		var handleEvents = function () {
			$("#jqxgridProduct").on("cellEndEdit", function (event){
			    var args = event.args;
			    var rowKey = args.key;
			    var row = args.row;
			    var columnDataField = args.dataField;
			    var columnDisplayField = args.displayField;
			    var value = args.value;
			    if (columnDataField == "order"){
			    	var price = row.price;
				    var order = row.order;
				    if (order>=0) {
				    	$("#jqxgridProduct").jqxTreeGrid("setCellValue", row.uid, "valueCost", order*price);
					}
				}
			});
		};
		var getProductGrid = function () {
			var data = $("#jqxgridProduct").jqxTreeGrid("source").records;
			var productInAgreement = new Array();
			for ( var x in data) {
				var order = data[x].order;
				if (order) {
					productInAgreement.push({productId: data[x].productId, price: data[x].price, quantity: data[x].order, note: data[x].note});
				}
			}
			return productInAgreement;
		};
		var setProductGrid = function (data) {
			var source = $("#jqxgridProduct").jqxTreeGrid("source").records;
			for ( var x in source) {
				var productId = source[x].productId;
				for ( var z in data) {
					if (productId == data[z].productId) {
						var price = source[x].price;
						$("#jqxgridProduct").jqxTreeGrid("setCellValue", productId, "order", data[z].quantity);
						$("#jqxgridProduct").jqxTreeGrid("setCellValue", productId, "valueCost", data[z].quantity*price);
						if (data[z].note != "null") {
							$("#jqxgridProduct").jqxTreeGrid("setCellValue", productId, "note", data[z].note);
						}
					}
				}
			}
		};
		return {
			init: function () {
				initProductGrid(_dataProduct(), lockRowParents);
				handleEvents();
			},
			getProductInAgreement: getProductGrid,
			setProductInAgreement: setProductGrid
		};
	})();
}
if (typeof (LocalUtility) == "undefined") {
	var LocalUtility = (function () {
		var getValueSelectedJqxComboBox = function (id) {
			var items = $("#" + id).jqxComboBox("getSelectedItems");
			var values = new Array();
			for ( var x in items) {
				values.push(items[x].value);
			}
			return values;
		};
		var setValueJqxComboBox = function (id, data) {
			for ( var x in data) {
				$("#" + id).jqxComboBox("selectItem", data[x]);
			}
		};
		var getPartyName = function (partyId) {
			var partyName;
			$.ajax({
				url: "getPartyName",
				type: "POST",
				async: false,
				data: {partyId: partyId},
				success: function() {}
			}).done(function(res) {
				partyName = res["partyName"];
			});
			return partyName;
		};
		return {
			getValueSelectedJqxComboBox: getValueSelectedJqxComboBox,
			setValueJqxComboBox: setValueJqxComboBox,
			getPartyName: getPartyName
		};
	})();
}
var UpdateMode;
$(document).ready(function () {
	if (agreementIdParam) {
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>" + multiLang.DmsEditAgreement);
		$("#btnSave").text(multiLang.CommonUpdate);
		if (typeof (UpdateMode) == "undefined") {
			UpdateMode = (function () {
				var agreementTermIds;
				var listProductsSaved;
				var loadAgreementInformation = function () {
					$.ajax({
						url: "loadAgreementInformationAjax",
						type: "POST",
						async: false,
						data: {agreementId: agreementIdParam},
						success: function() {}
					}).done(function(res) {
						if (res["agreementInformation"]) {
							AgreementInfo.setValue(res["agreementInformation"]);
							PartyFromInfo.setValue(res["agreementInformation"]);
							PartyToInfo.setValue(res["agreementInformation"]);
							AgreementTerm.setValue(res["agreementInformation"]);
							ProductLayer.setProductInAgreement(res["agreementInformation"].listProducts);
							listProductsSaved = res["listProductsSaved"];
							agreementTermIds = res["agreementTermIds"];
						} else {
							alert();
						}
					});
				};
				var disableElements = function () {
					AgreementInfo.disableElements();
					PartyFromInfo.disableElements();
					PartyToInfo.disableElements();
				};
				return {
					loadAgreementInformation: function () {
						loadAgreementInformation();
						disableElements();
					}(),
					listProductsSaved: listProductsSaved,
					agreementTermIds: agreementTermIds
				};
			})();
		}
	} else {
//		PartyToInfo.setValue({partyIdTo: organizationId, partyRepresentId: "OLBHRM"});
	}
});