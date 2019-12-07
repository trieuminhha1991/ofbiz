<#-- upgrade from ..basesalesmtl/supervisor/createAgent/generalInfo.ftl -->

<div class="step-pane active" id="generalInfo">
	<input type="hidden" id="partyId" value="${parameters.partyId?if_exists}"/>
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span2">
			<div class="row-fluid">
				<div class="span12">
					<div class="logo-company">
						<input type="file" id="logoImageUrl" style="visibility:hidden;" accept="image/*"/>
						<img src="/salesmtlresources/logo/LOGO_demo.png" id="logoImage"/>
					</div>
				</div>
			</div>
		</div>
		<div class="span5">
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSAgentId}</label></div>
				<div class="span7"><input type="text" id="partyCode" tabindex="1"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSAgentName}</label></div>
				<div class="span7"><input type="text" id="groupName" tabindex="2"/></div>
			</div>
			<#--<#if parameters.partyId?exists>-->
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
				<div class="span7"><input type="text" id="txtPhoneNumber" tabindex="3"/></div>
			</div>
			<#--</#if>-->
			<#--<#if parameters.partyId?exists>-->
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="email" id="txtEmailAddress" tabindex="4"/></div>
			</div>
			<#--</#if>-->
		</div><!--.span5-->
		<div class="span5">
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSCurrencyUomId}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><div id="currencyUomId"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_officeSiteName}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="officeSiteName" tabindex="5"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSTaxCode}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="taxAuthInfos" tabindex="6"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSDescription}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="comments" tabindex="7"/></div>
			</div>
		</div><!--.span5-->
	</div><!--.form-horizontal-->
	<hr/>
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span12">
			<div class="span2"></div>
			<div class="span5">
				<div class="row-fluid">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSDistributor}</label></div>
					<div class="span7">
						<div id="divDistributor">
							<div style="border-color: transparent;" id="jqxgridDistributor"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label>${uiLabelMap.BSSalesman}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7">
						<div id="divSalesman">
							<div id="jqxgridSalesman"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="span5">
				<#if !parameters.partyId?exists>
					<div class="row-fluid">
						<div class="span5"><label>${uiLabelMap.BSRoute}</label></div>
						<div class="span7">
							<div id="divRoute">
								<div id="jqxgridRoute"></div>
							</div>
						</div>
					</div>
				</#if>
                <div class="row-fluid" hidden>
                    <div class="span5"><label class="text-right">${uiLabelMap.BSVisitFrequency}&nbsp;&nbsp;&nbsp;</label></div>
                    <div class="span7"><div id="visitFrequencyTypeId"></div></div>
                </div>
				<#--
				<#if !parameters.partyId?exists>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSProductStore}</label></div>
					<div class="span7"><div id="txtProductStore"></div></div>
				</div>
				</#if>
				-->
			</div>
		</div>
	</div>
</div>

<#assign defaultCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<#assign defaultVisitId = "F0"/>
<script type="text/javascript">
	$(function(){
		OlbOutletNewInfo.init();
	});
	var OlbOutletNewInfo = (function(){
		var distributorDDB;
		var salesmanDDB;
		var routerDDB;
		var validatorVAL;

		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			$("#partyCode").focus();
			jOlbUtil.input.create($("#partyCode"), {width: '98%'});
			jOlbUtil.input.create($("#groupName"), {width: '98%'});
			jOlbUtil.input.create($("#comments"), {width: '98%'});
			jOlbUtil.input.create($("#txtPhoneNumber"), {width: '98%'});
			jOlbUtil.input.create($("#txtEmailAddress"), {width: '98%'});
			jOlbUtil.input.create($("#officeSiteName"), {width: '98%'});
			jOlbUtil.input.create($("#taxAuthInfos"), {width: '98%'});
		};
		var initElementComplex = function(){
			var configCurrency = {
				width: '100%',
				dropDownWidth: '280px',
				dropDownHeight: '200px',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				key: 'uomId',
				value: 'description',
				autoDropDownHeight: false,
				displayDetail: true,
			}
			new OlbComboBox($("#currencyUomId"), listCurrencyUom, configCurrency, ["${defaultCurrencyUomId?if_exists}"]);
            var configVisit = {
                width: '100%',
                dropDownWidth: '280px',
                dropDownHeight: '200px',
                placeHolder: uiLabelMap.BSClickToChoose,
                useUrl: false,
                url: '',
                key: 'visitFrequencyTypeId',
                value: 'description',
                autoDropDownHeight: false,
                displayDetail: true,
                disabled: true,
            }
            new OlbComboBox($("#visitFrequencyTypeId"), visitFrequencyTypes, configVisit, ["${defaultVisitId?if_exists}"]);

			if (!UpdateMode) {
				var configRouter = {
					useUrl: true,
					root: 'results',
					widthButton: '100%',
					showdefaultloadelement: false,
					autoshowloadelement: false,
					datafields: [
						{name: "routeId", type: "string"},
		            	{name: "routeCode", type: "string"},
		            	{name: "routeName", type: "string"}
					],
					columns: [
						{text: multiLang.BsRouteId, datafield: "routeCode", width: 150},
		               	{text: multiLang.BSRouteName, datafield: "routeName"}
					],
					url: '',
					useUtilFunc: true,

					key: 'routeId',
					description: ['routeName'],
					autoCloseDropDown: true,
					filterable: true,
					sortable: true,
					autorowheight: false,
					filterable: true,
					showfilterrow: true,
					dropDownHorizontalAlignment: 'right',
				};
				routerDDB = new OlbDropDownButton($("#divRoute"), $("#jqxgridRoute"), null, configRouter, []);
			}

			var configDistributor = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: "partyId", type: "string" },
                  	{name: "partyCode", type: "string" },
                  	{name: "groupName", type: "string" }
				],
				columns: [
					{text: multiLang.DADistributorId, datafield: "partyCode", width: 150},
					{text: multiLang.DADistributorName, datafield: "groupName"}
				],
				url: 'JQGetListDistributor&sD=N',
				useUtilFunc: true,

				key: 'partyId',
                keyCode: 'partyCode',
				description: 'groupName',
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				autorowheight: false,
				filterable: true,
				showfilterrow: true,
			};
			distributorDDB = new OlbDropDownButton($("#divDistributor"), $("#jqxgridDistributor"), null, configDistributor, []);

			var configSalesman = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: "partyId", type: "string"},
                  	{name: "partyCode", type: "string"},
                  	{name: "firstName", type: "string"},
                  	{name: "middleName", type: "string"},
                  	{name: "lastName", type: "string"},
                    {name: "fullName", type: "string"},
                  	{name: "department", type: "string"}
				],
				columns: [
					{text: multiLang.salesmanId, datafield: "partyCode", width: 150},
	               	{text: multiLang.DmsPartyLastName, datafield: "lastName", width: 100},
	               	{text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 100},
	               	{text: multiLang.DmsPartyFirstName, datafield: "firstName", width: 100},
	               	{text: multiLang.CommonDepartment, datafield: "department", width: 150}
				],
				url: '',
				useUtilFunc: true,

				key: 'partyId',
                keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				autorowheight: false,
				filterable: true,
				showfilterrow: true,
			};
			salesmanDDB = new OlbDropDownButton($("#divSalesman"), $("#jqxgridSalesman"), null, configSalesman, []);
		}
		var initEvent = function(){
			$("#logoImage").click(function() {
				$("#logoImageUrl").click();
			});
			$("#logoImageUrl").change(function(){
				Images.readURL(this, $("#logoImage"));
			});

			distributorDDB.getGrid().rowSelectListener(function(rowData){
		    	var distributorId = rowData['partyId'];

		    	if(routerDDB) routerDDB.clearAll(true);

				salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetListSalesmanAssigned&partyIdFrom=" + distributorId, null, function(){
					//salesmanDDB.selectItem(null, 0);
					$("#jqxgridSalesman").jqxGrid("selectrow", 0);
				});
		    });

		    salesmanDDB.getGrid().rowSelectListener(function(rowData){
		    	var salesmanId = rowData['partyId'];

		    	if (routerDDB) {
		    		routerDDB.updateSource("jqxGeneralServicer?sname=JQGetListRoutes&distinct=Y&partyId=" + salesmanId, null, function(){
		    			routerDDB.selectItem(null, 0);
		    		});
		    	}
		    });
		};
		var initValidateForm = function(){
			var extendRules = [];
			if (UpdateMode) {
				extendRules = [
						{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
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
			            },
                        {input: "#txtPhoneNumber", message: multiLang.BSPhoneIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(), multiLang.BSCheckPhone);
                            }
                        },
                        {input: "#txtEmailAddress", message: multiLang.BSEmailIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckEmail);
                            }
                        },
                        {input: "#officeSiteName", message: multiLang.BSWebsiteIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckWebsite);
                            }
                        },
                        {input: "#taxAuthInfos", message: multiLang.BSTaxCodeIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckTaxCode);
                            }
                        },
			    	];
	    	} else {
	    		extendRules = [
	    				{input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
		          		},
						{input: '#partyCode', message: multiLang.DmsPartyCodeAlreadyExists, action: 'change',
							rule: function (input, commit) {
								var check = DataAccess.getData({
										url: "checkPartyCode",
										data: {partyId: partyIdPram, partyCode: input.val()},
										source: "check"});
								if ("false" == check) {
									return false;
								}
								if (!UpdateMode) {
									var userLoginId = input.val();
									if (userLoginId) {
										var check = DataAccess.getData({
											url: "checkUserLoginId",
											data: {userLoginId: userLoginId},
											source: "check"});
										if ("false" == check) {
											 return false;
										}
									}
								}
								return true;
							}
						},
						{input: '#partyCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
			            {input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
			            {input: "#divDistributor", message: multiLang.fieldRequired, action: "close",
							rule: function (input, commit) {
								var value = input.jqxDropDownButton("val").trim();
								if (value) {
									return true;
								}
								return false;
							}
						},
				        {input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: 'blur',
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
		          		},
                        {input: "#txtPhoneNumber", message: multiLang.BSPhoneIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(), multiLang.BSCheckPhone);
                            }
                        },
                        {input: "#txtEmailAddress", message: multiLang.BSEmailIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckEmail);
                            }
                        },
                        {input: "#officeSiteName", message: multiLang.BSWebsiteIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckWebsite);
                            }
                        },
                        {input: "#taxAuthInfos", message: multiLang.BSTaxCodeIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRex(input.val(), multiLang.BSCheckTaxCode);
                            }
                        },
	    		]
	    	}
			var mapRules = [];

			validatorVAL = new OlbValidator($("#generalInfo"), mapRules, extendRules, {position: "bottom", scroll: true});
		};

		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#partyCode").val(data.partyCode);
				$("#groupName").val(data.groupName);
				$("#groupName").val(data.groupNameLocal);
				$("#officeSiteName").val(data.officeSiteName);
				$("#comments").val(data.comments);
				$("#currencyUomId").jqxComboBox("val", data.currencyUomId);
				$("#visitFrequencyTypeId").jqxComboBox("val", data.visitFrequencyTypeId);
				$("#taxAuthInfos").val(data.taxAuthInfos);
				$("#txtPhoneNumber").val(data.contactNumber);
				$("#txtEmailAddress").val(data.infoString);

				<#--OlbDistributorNewOwner.setValue(data.representative);
				extendId.contactNumberId = data.contactNumberId;
				extendId.infoStringId = data.infoStringId;
				extendId.addressId = data.addressId;
				extendId.taxAuthInfosfromDate = data.taxAuthInfosfromDate;
				extendId.partyId = data.partyId;
				-->

				if (data.logoImageUrl) {
					$("#logoImage").attr("src", data.logoImageUrl);
				}
				Grid.setDropDownValue($("#divDistributor"), data.distributorId, data.distributor);
				Grid.setDropDownValue($("#divSalesman"), data.salesmanId, data.salesman);
			}
		};
		var getValue = function() {
			if (UpdateMode) {
				var value = {
					partyId: $("#partyId").val(),
					partyCode: $("#partyCode").val(),
					groupName: $("#groupName").val(),
					groupNameLocal: $("#groupName").val(),
					officeSiteName: $("#officeSiteName").val(),
					comments: $("#comments").val(),
					currencyUomId: $("#currencyUomId").jqxComboBox("val"),
                    visitFrequencyTypeId: $("#visitFrequencyTypeId").jqxComboBox("val"),
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
					currencyUomId: $("#currencyUomId").jqxComboBox("val"),
                    visitFrequencyTypeId: $("#visitFrequencyTypeId").jqxComboBox("val"),
					taxAuthInfos: $("#taxAuthInfos").val(),
					contactNumber: $("#txtPhoneNumber").val(),
					infoString: $("#txtEmailAddress").val(),
					distributorId: OlbDropDownButtonUtil.getAttrDataValue($("#divDistributor")),
					salesmanId: OlbDropDownButtonUtil.getAttrDataValue($("#divSalesman")),
					routeId: OlbDropDownButtonUtil.getAttrDataValue($("#divRoute"))
				};
			}

			return value;
		};
		var getValueDesc = function(){
			var value = getValue();
			if (!UpdateMode) {
				var extentValue = {};

				// get address full name
				var toName = $("#wn_toName").val();
				var attnName = $("#wn_attnName").val();
				var postalCode = $("#wn_postalCode").val();

				var addressFullName = toName ? toName + " " : "";
				if (!_.isEmpty(attnName)) addressFullName += "(" + attnName + ") ";
				else if (addressFullName != "") addressFullName += ", ";
				addressFullName += $("#wn_address1").val();

				var wardGeoItem = $("#wn_wardGeoId").jqxComboBox("getSelectedItem");
				var wardGeoStr = typeof("wardGeoItem") != "undefined" ? wardGeoItem.label : "";
				if (OlbCore.isNotEmpty(wardGeoStr)) addressFullName += ", " + wardGeoStr;
				var countyGeoItem = $("#wn_countyGeoId").jqxComboBox("getSelectedItem");
				var countyGeoStr = typeof("countyGeoItem") != "undefined" ? countyGeoItem.label : "";
				if (OlbCore.isNotEmpty(countyGeoStr)) addressFullName += ", " + countyGeoStr;
				var stateProvinceGeoItem = $("#wn_stateProvinceGeoId").jqxComboBox("getSelectedItem");
				var stateProvinceGeoStr = typeof("stateProvinceGeoItem") != "undefined" ? stateProvinceGeoItem.label : "";
				if (OlbCore.isNotEmpty(stateProvinceGeoStr)) addressFullName += ", " + stateProvinceGeoStr;
				var countryGeoItem = $("#wn_countryGeoId").jqxComboBox("getSelectedItem");
				var countryGeoStr = typeof("countryGeoItem") != "undefined" ? countryGeoItem.label : "";
				if (OlbCore.isNotEmpty(countryGeoStr)) addressFullName += ", " + countryGeoStr;

				// process value
				var extendValue = {
					addressFullName: addressFullName,
					distributorDesc: $("#divDistributor").jqxDropDownButton("val"),
					salesmanDesc: $("#divSalesman").jqxDropDownButton("val"),
					routeDesc: $("#divRoute").jqxDropDownButton("val"),
				};

				value = _.extend(value, extendValue);
			}
			else {
				var extendValue = {
					distributorDesc: $("#divDistributor").jqxDropDownButton("val"),
					salesmanDesc: $("#divSalesman").jqxDropDownButton("val"),
				};

				value = _.extend(value, extendValue);
			}

			return value;
		};
		return {
			init: init,
			getValue: getValue,
			setValue: setValue,
			getValueDesc: getValueDesc,
		};
	}());

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
</script>