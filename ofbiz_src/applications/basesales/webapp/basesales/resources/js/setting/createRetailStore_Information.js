if (typeof (RetailStoreInfo) == "undefined") {
	var RetailStoreInfo = (function() {
		//var defaultCurrencyUomIdCBB, vatTaxAuthPartyIdDDB, vatTaxAuthGeoIdDDB, includeOtherCustomerDDL, managerDDB, salesmanDDB, prodCatalogDDL, reserveOrderEnumDDL, idMap= new Object();;
		var defaultCurrencyUomIdCBB, vatTaxAuthPartyIdDDB, payToPartyIdDDB, salesMethodChannelDDL, defaultSalesChannelDDL, vatTaxAuthGeoIdDDB, includeOtherCustomerDDL, managerDDB, prodCatalogDDL, reserveOrderEnumDDL, idMap= new Object();;
		var initJqxElements = function() {
			jOlbUtil.input.create("#wn_productStoreId", { width:"91.5%", height: 26 });
			jOlbUtil.input.create("#wn_storeName", { width:"91.5%", height: 26 });
			jOlbUtil.input.create("#wn_EmployeeId", { width:"91.5%", height: 26 });
			jOlbUtil.input.create("#wn_EmployeeName", { width:"91.5%", height: 26 });
			jOlbUtil.input.create("#wn_userLoginId", { width:"91.5%", height: 26 });
			//jOlbUtil.input.create("#wn_EmployeeId_Salesman", { width:"91.5%", height: 26 });
			//jOlbUtil.input.create("#wn_EmployeeName_Salesman", { width:"91.5%", height: 26 });
			//jOlbUtil.input.create("#wn_userLoginId_Salesman", { width:"91.5%", height: 26 });
			var configCurrencyUom =
			{
				placeHolder: multiLang.BSClickToChoose,
				key: "uomId",
				value: "descriptionSearch",
				width: "99%",
				height: 26,
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: "containsignorecase",
				renderer : null,
				renderSelectedItem : null
			};
			defaultCurrencyUomIdCBB = new OlbComboBox($("#wn_defaultCurrencyUomId"), currencyUomData, configCurrencyUom, ["VND"]);
				
			var configTaxAuthParty =
			{
				useUrl: true,
				root: "results",
				widthButton: "99%",
				heightButton: 26,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: "right",
				datafields:
				[
					{ name: "taxAuthPartyId", type: "string" },
					{ name: "taxAuthGeoId", type: "string" },
					{ name: "groupName", type: "string" }
				],
				columns:
				[
					{ text: multiLang.BSPartyId, datafield: "taxAuthPartyId", width: "20%" },
					{ text: multiLang.BSTaxAuthGeoId, datafield: "taxAuthGeoId", width: "20%" },
					{ text: multiLang.BSGroupName, datafield: "groupName" }
				],
				url: "JQGetListTaxAuthority",
				pagesize: 5,
				useUtilFunc: true,
				autoCloseDropDown: true,
				key: "taxAuthPartyId",
				description: ["groupName"]
			};
			vatTaxAuthPartyIdDDB = new OlbDropDownButton($("#wn_vatTaxAuthPartyId"), $("#wn_vatTaxAuthPartyGrid"), null, configTaxAuthParty, ["VNM_TAX"]);
			
			var configOrganization = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'}, {name: 'baseCurrencyUomId', type: 'string'}],
				columns: [
					{text: multiLang.BSOrganizationId, datafield: 'partyId', width: '26%'},
					{text: multiLang.BSFullName, datafield: 'groupName'},
					{text: multiLang.BSCurrencyUomId, datafield: 'baseCurrencyUomId', width: '18%'}
				],
				url: 'JQListOrganizationPartyS',
				useUtilFunc: true,
				key: 'partyId',
				description: ['groupName']
			};
			payToPartyIdDDB = new OlbDropDownButton($("#wn_payToPartyId"), $("#wn_payToPartyGrid"), null, configOrganization, [currentOrganizationPartyId]);
			
			var configSalesMethodChannel = {
				width: '99%',
				placeHolder: multiLang.BSClickToChoose,
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			salesMethodChannelDDL = new OlbDropDownList($("#wn_salesMethodChannelEnumId"), salesMethodChannelData, configSalesMethodChannel, []);
			
			var configDefaultSalesChannel = {
				width: '99%',
				placeHolder: multiLang.BSClickToChoose,
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				useUrl: false,
			};
			defaultSalesChannelDDL = new OlbDropDownList($("#wn_defaultSalesChannelEnumId"), defaultSalesChannelData, configDefaultSalesChannel, []);
			
			var configTaxAuthGeo =
			{
				useUrl: true,
				root: "results",
				widthButton: "99%",
				heightButton: 26,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: "right",
				datafields:
				[
					{ name: "geoId", type: "string" },
					{ name: "geoTypeId", type: "string" },
					{ name: "geoName", type: "string" },
					{ name: "geoCode", type: "string" },
					{ name: "geoSecCode", type: "string" },
					{ name: "abbreviation", type: "string" },
					{ name: "wellKnownText", type: "string" }
				],
				columns:
				[
					{ text: multiLang.BSGeoId, datafield: "geoId", width: "10%" },
					{ text: multiLang.BSGeoTypeId, datafield: "geoTypeId", width: "15%",
						cellsrenderer: function(column, row, value) {
							for (var i = 0;  i < geoTypeList.length; i++) {
								if (geoTypeList[i].geoTypeId == value) {
									return "<span title=" + value + ">" + geoTypeList[i].description + "</span>"
								}
							}
							return "<span>" + value + "</span>"
						}
					},
					{ text: multiLang.BSGeoName, datafield: "geoName" },
					{ text: multiLang.BSGeoCode, datafield: "geoCode", width: "10%" },
					{ text: multiLang.BSGeoSecCode, datafield: "geoSecCode", width: "10%" },
					{ text: multiLang.BSAbbreviation, datafield: "abbreviation", width: "13%" },
					{ text: multiLang.BSWellKnownText, datafield: "wellKnownText", width: "15%" }
				],
				url: "JQGetListGeos",
				pagesize: 5,
				useUtilFunc: true,
				autoCloseDropDown: true,
				key: "geoId",
				description: ["geoName"]
			};
			vatTaxAuthGeoIdDDB = new OlbDropDownButton($("#wn_vatTaxAuthGeoId"), $("#wn_vatTaxAuthGeoGrid"), null, configTaxAuthGeo, ["VNM"]);
			
			var configIncludeOtherCustomer =
			{
				width: "98%",
				key: "id",
				value: "description",
				autoDropDownHeight: true,
				displayDetail: false,
				placeHolder: multiLang.BSClickToChoose
			}
			includeOtherCustomerDDL = new OlbDropDownList($("#wn_includeOtherCustomer"), dataYesNoChoose, configIncludeOtherCustomer, ["N"]);
			
			var configManager =
			{
				useUrl: true,
				widthButton: "93%",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields:
				[
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "fullName", type: "string" }
				],
				columns:
				[
					{ text: multiLang.BSEmployeeId, datafield: "partyCode", width: "30%" },
					{ text: multiLang.BSFullName, datafield: "fullName" }
				],
				url: "JQGetListEmployeeByOrg",
				pagesize: 5,
				useUtilFunc: true,
				key: "partyId",
				keyCode: "partyCode",
				description: ["fullName"],
				autoCloseDropDown: true,
				filterable: true
			};
			managerDDB = new OlbDropDownButton($("#wn_Manager"), $("#wn_ManagerGrid"), null, configManager, []);
			
			/*var configSalesman =
			{
				useUrl: true,
				widthButton: "93%",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: "right",
				datafields:
				[
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "fullName", type: "string" }
				],
				columns:
				[
					{ text: multiLang.BSEmployeeId, datafield: "partyCode", width: "30%" },
					{ text: multiLang.BSFullName, datafield: "fullName" }
				],
				url: "JQGetListEmployeeByOrg",
				pagesize: 5,
				useUtilFunc: true,
				key: "partyId",
				keyCode: "partyCode",
				description: ["fullName"],
				autoCloseDropDown: true,
				filterable: true
			};
			salesmanDDB = new OlbDropDownButton($("#wn_Salesman"), $("#wn_SalesmanGrid"), null, configSalesman, []);*/
			
			var configProdCatalog =
			{
				width: "99%",
				placeHolder: multiLang.BSClickToChoose,
				useUrl: true,
				url: "jqxGeneralServicer?sname=JQGetListProdCatalog",
				key: "prodCatalogId",
				value: "catalogName",
				autoDropDownHeight: true,
				selectedIndex: 0
			}
			prodCatalogDDL = new OlbDropDownList($("#wn_pscata_prodCatalogId"), null, configProdCatalog, []);
			
			var configReserveOrderEnum = 
			{
	                width: "99%",
	                placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
	                key: "enumId",
	                value: "description",
	                autoDropDownHeight: true,
	                useUrl: false,
	                selectedIndex: 0
			};
			reserveOrderEnumDDL = new OlbDropDownList($("#wn_reserveOrderEnumId"), reserveOrderEnumData, configReserveOrderEnum, []);
		};
		var handleEvents = function() {
			$("#quickAddNewManager").click(function() {
				if ($("#ManagerNew").css("display") == "none") {
					$("#ManagerNew").fadeIn();
					managerDDB.getButtonObj().jqxDropDownButton({ disabled: true });
					managerDDB.clearAll();
					$(this).html("<i class=\"fa fa-times\"></i>");
				} else {
					$("#ManagerNew").fadeOut();
					managerDDB.getButtonObj().jqxDropDownButton({ disabled: false });
					$(this).html("<i class=\"fa fa-plus\"></i>");
				}
			});
			/*$("#quickAddNewSalesman").click(function() {
				if ($("#SalesmanNew").css("display") == "none") {
					$("#SalesmanNew").fadeIn();
					salesmanDDB.getButtonObj().jqxDropDownButton({ disabled: true });
					salesmanDDB.clearAll();
					$(this).html("<i class=\"fa fa-times\"></i>");
				} else {
					$("#SalesmanNew").fadeOut();
					salesmanDDB.getButtonObj().jqxDropDownButton({ disabled: false });
					$(this).html("<i class=\"fa fa-plus\"></i>");
				}
			});*/
		};
		var initValidator = function() {
			$("#step1").jqxValidator({
				rules: [{ input: "#wn_productStoreId", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_productStoreId", message: multiLang.containSpecialSymbol, action: "blur, change",
							rule: function (input, commit) {
								return OlbValidatorUtil.validElement(input, commit, "validCannotSpecialCharactor");
							}
						},
						{ input : "#wn_productStoreId", message : multiLang.BSCodeAlreadyExists, action : "change",
							rule : function(input, commit) {
								var productStoreId = input.val();
								if (productStoreId) {
									var check = DataAccess.getData({
										url : "checkProductStoreId",
										data : { productStoreId : productStoreId },
										source : "check"
									});
									if ("false" == check) {
										return false;
									}
								}
								return true;
							}
						},
						{ input: "#wn_storeName", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_defaultCurrencyUomId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxComboBox("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_pscata_prodCatalogId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_payToPartyId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownButton("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_salesMethodChannelEnumId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_defaultSalesChannelEnumId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_reserveOrderEnumId", message: multiLang.fieldRequired, action: "change", 
                            rule: function (input, commit) {
                                if (input.jqxDropDownList("val")) {
                                    return true;
                                }
                                return false;
                            }
                        },
						{ input: "#wn_Manager", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								if (OlbDropDownButtonUtil.getAttrDataValue(input) || managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						/*{ input: "#wn_Salesman", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								if (OlbDropDownButtonUtil.getAttrDataValue(input) || salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},*/
						{ input: "#wn_EmployeeId", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_EmployeeId", message: multiLang.containSpecialSymbol, action: "blur, change",
							rule: function (input, commit) {
								if (!managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return OlbValidatorUtil.validElement(input, commit, "validCannotSpecialCharactor");
							}
						},
						{ input : "#wn_EmployeeId", message : multiLang.BSCodeAlreadyExists, action : "change",
							rule : function(input, commit) {
								if (!managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								var partyCode = input.val();
								if (partyCode) {
									var check = DataAccess.getData({
										url : "checkPartyCode",
										data : { partyCode : partyCode },
										source : "check"
									});
									if ("false" == check) {
										return false;
									}
								}
								return true;
							}
						},
						{ input: "#wn_EmployeeName", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_userLoginId", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input : "#wn_userLoginId", message : multiLang.BSCodeAlreadyExists, action : "change",
							rule : function(input, commit) {
								if (!managerDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								var userLoginId = input.val();
								if (userLoginId) {
									var check = DataAccess.getData({
										url: "checkUserLoginId",
										data: { userLoginId: userLoginId },
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						}
						/*{ input: "#wn_EmployeeId_Salesman", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_EmployeeId_Salesman", message: multiLang.containSpecialSymbol, action: "blur, change",
							rule: function (input, commit) {
								if (!salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return OlbValidatorUtil.validElement(input, commit, "validCannotSpecialCharactor");
							}
						},
						{ input : "#wn_EmployeeId_Salesman", message : multiLang.BSCodeAlreadyExists, action : "change",
							rule : function(input, commit) {
								if (!salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								var partyCode = input.val();
								if (partyCode) {
									var check = DataAccess.getData({
										url : "checkPartyCode",
										data : { partyCode : partyCode },
										source : "check"
									});
									if ("false" == check) {
										return false;
									}
								}
								return true;
							}
						},
						{ input: "#wn_EmployeeName_Salesman", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#wn_userLoginId_Salesman", message: multiLang.fieldRequired, action: "blur, change",
							rule: function (input, commit) {
								if (input.val() || !salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								return false;
							}
						},
						{ input : "#wn_userLoginId_Salesman", message : multiLang.BSCodeAlreadyExists, action : "change",
							rule : function(input, commit) {
								if (!salesmanDDB.getButtonObj().jqxDropDownButton("disabled")) {
									return true;
								}
								var userLoginId = input.val();
								if (userLoginId) {
									var check = DataAccess.getData({
										url: "checkUserLoginId",
										data: { userLoginId: userLoginId },
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						}*/
					],
					position: "topcenter",
					scroll: false
			});
		};
		var validate = function() {
			return $("#step1").jqxValidator("validate") && $("#initFacilityAddress").jqxValidator("validate");
		};
		var setValue = function(res) {
            idMap = new Object();
            var data = res["storeDetail"];
            if (!_.isEmpty(data)) {
                $("#wn_productStoreId").jqxInput("val", data.productStoreId);
                $("#wn_storeName").jqxInput("val", data.storeName);
                defaultCurrencyUomIdCBB.selectItem([data.defaultCurrencyUomId]);
                vatTaxAuthGeoIdDDB.selectItem([data.vatTaxAuthGeoId]);
                vatTaxAuthPartyIdDDB.selectItem([data.vatTaxAuthPartyId]);
                payToPartyIdDDB.selectItem([data.payToPartyId]);
                salesMethodChannelDDL.selectItem([data.salesMethodChannelEnumId]);
                defaultSalesChannelDDL.selectItem([data.defaultSalesChannelEnumId]);
                
                includeOtherCustomerDDL.selectItem([data.includeOtherCustomer]);
                prodCatalogDDL.selectItem(data.prodCatalogId);
                reserveOrderEnumDDL.selectItem([data.reserveOrderEnumId]);
                managerDDB.selectItem(data.managerId);
                //salesmanDDB.selectItem(data.salesmanId);
                setTimeout(function() {
                    $("#countryGeoId").jqxDropDownList("val", data.countryGeoId);
                    $("#provinceGeoId").jqxDropDownList("val", data.provinceGeoId);
                    $("#districtGeoId").jqxDropDownList("val", data.districtGeoId);
                    $("#wardGeoId").jqxDropDownList("val", data.wardGeoId);
                    $("#phoneNumber").jqxInput("val", data.phoneNumber);
                    $("#address").val(data.address);
                }, 100);
                idMap = data.idMap;
                $("#wn_productStoreId").jqxInput({disabled: RetailStore.UpdateMode });
                $("#wn_productStoreId").parent().parent().show();
                $("#wn_productStoreId_label").parent().parent().show();
                $("#wn_vatTaxAuthPartyId").jqxDropDownButton({ disabled: RetailStore.UpdateMode });
                $("#wn_vatTaxAuthGeoId").jqxDropDownButton({ disabled: RetailStore.UpdateMode });
                $("#wn_payToPartyId").jqxDropDownButton({ disabled: RetailStore.UpdateMode });
                
            }
        };
		var getValue = function() {
			var value =
			{
				productStoreId : $("#wn_productStoreId").jqxInput("val"),
				storeName : $("#wn_storeName").jqxInput("val"),
				defaultCurrencyUomId : defaultCurrencyUomIdCBB.getValue(),
				vatTaxAuthGeoId: vatTaxAuthGeoIdDDB.getValue(),
				payToPartyId: payToPartyIdDDB.getValue(),
				vatTaxAuthPartyId: vatTaxAuthPartyIdDDB.getValue(),
				includeOtherCustomer: includeOtherCustomerDDL.getValue(),
				prodCatalogId: prodCatalogDDL.getValue(),
				reserveOrderEnumId: reserveOrderEnumDDL.getValue(),
				salesMethodChannelEnumId: salesMethodChannelDDL.getValue(),
				defaultSalesChannelEnumId: defaultSalesChannelDDL.getValue(),
				managerId: managerDDB.getValue(),
				managerCode: $("#wn_EmployeeId").jqxInput("val"),
				managerName: $("#wn_EmployeeName").jqxInput("val"),
				managerLoginId: $("#wn_userLoginId").jqxInput("val"),
				//salesmanId: salesmanDDB.getValue(),
				//salesmanCode: $("#wn_EmployeeId_Salesman").jqxInput("val"),
				//salesmanName: $("#wn_EmployeeName_Salesman").jqxInput("val"),
				//salesmanLoginId: $("#wn_userLoginId_Salesman").jqxInput("val"),
				countryGeoId: $("#countryGeoId").jqxDropDownList("val"),
				provinceGeoId: $("#provinceGeoId").jqxDropDownList("val"),
				districtGeoId: $("#districtGeoId").jqxDropDownList("val"),
				wardGeoId: $("#wardGeoId").jqxDropDownList("val"),
				phoneNumber : $("#phoneNumber").jqxInput("val"),
				address : $("#address").val()
			};
			//return value;
			return RetailStore.UpdateMode?_.extend(value, idMap):value;
		};
		var getDetail = function() {
			var value =
			{
				productStoreId : $("#wn_productStoreId").jqxInput("val"),
				storeName : $("#wn_storeName").jqxInput("val"),
				defaultCurrencyUomId : $("#wn_defaultCurrencyUomId").jqxComboBox("getSelectedItem").label,
				vatTaxAuthGeoId: vatTaxAuthGeoIdDDB.getButtonObj().val(),
				payToPartyId: payToPartyIdDDB.getButtonObj().val(),
				vatTaxAuthPartyId: vatTaxAuthPartyIdDDB.getButtonObj().val(),
				includeOtherCustomer: $("#wn_includeOtherCustomer").jqxDropDownList("getSelectedItem").label,
				prodCatalogId: $("#wn_pscata_prodCatalogId").jqxDropDownList("getSelectedItem").label,
				reserveOrderEnumId: $("#wn_reserveOrderEnumId").jqxDropDownList("getSelectedItem").label,
				salesMethodChannelEnumId: $("#wn_salesMethodChannelEnumId").jqxDropDownList("getSelectedItem").label,
				defaultSalesChannelEnumId: $("#wn_defaultSalesChannelEnumId").jqxDropDownList("getSelectedItem").label,
				
				managerLoginId: $("#wn_userLoginId").jqxInput("val"),
				managerDetail: managerDDB.getValue()?managerDDB.getButtonObj().val():$("#wn_EmployeeName").jqxInput("val") + " [" + $("#wn_EmployeeId").jqxInput("val") + "]",
				//salesmanLoginId: $("#wn_userLoginId_Salesman").jqxInput("val"),
				//salesmanDetail: salesmanDDB.getValue()?salesmanDDB.getButtonObj().val():$("#wn_EmployeeName_Salesman").jqxInput("val") + " [" + $("#wn_EmployeeId_Salesman").jqxInput("val") + "]",
				countryGeoName: $("#countryGeoId").jqxDropDownList("getSelectedItem").label,
				provinceGeoName: $("#provinceGeoId").jqxDropDownList("getSelectedItem").label,
				districtGeoName: $("#districtGeoId").jqxDropDownList("getSelectedItem").label,
				wardGeoName: $("#wardGeoId").jqxDropDownList("getSelectedItem").label,
				phoneNumber : $("#phoneNumber").jqxInput("val"),
				address : $("#address").val()
			};
			return value;
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			validate: validate,
			setValue: setValue,
			getValue: getValue,
			getDetail: getDetail
		}
	})();
}