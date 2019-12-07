<#-- upgrade from ../create/generalInfo.ftl -->
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
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSDistributorId}</label></div>
				<div class="span7"><input type="text" id="partyCode" tabindex="1"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DADistributorName}</label></div>
				<div class="span7"><input type="text" id="groupName" tabindex="2"/></div>
			</div>
			<#--<#if parameters.partyId?exists>-->
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
				<div class="span7"><input type="tel" id="txtPhoneNumber" tabindex="3"/></div><#--type="number"-->
			</div>
			<#--</#if>-->
			<#--<#if parameters.partyId?exists>-->
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="email" id="txtEmailAddress" tabindex="4"/></div>
			</div>
			<#--</#if>-->
		</div>
		<div class="span5">
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSCurrencyUomId}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><div id="currencyUomId"></div></div>
			</div>
			<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_officeSiteName}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><input type="text" id="officeSiteName" tabindex="6"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSTaxCode}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="taxAuthInfos" tabindex="7"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSDescription}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="comments" tabindex="8"/></div>
			</div>
			<div class="row-fluid hide">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSDistributorId}</label></div>
				<div class="span7"><input type="text" id="infoStringId" value=""/></div>
			</div>
		</div>
	</div>
	<hr/>
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span12">
			<div class="span2"></div>
			<div class="span5">
				<div class="row-fluid">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSSupervisor}</label></div>
					<div class="span7">
						<div id="divSupervisor">
							<div style="border-color: transparent;" id="jqxgridSupervisor"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="span5">
				<#if !parameters.partyId?exists>
					<div class="row-fluid">
						<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSInSalesChannel}</label></div>
						<div class="span7"><div id="txtProductStore"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSCatalogWillSale}</label></div>
						<div class="span7"><div id="txtCatalog"></div></div>
					</div>
				</#if>
			</div>
		</div>
	</div>
</div>

<#assign defaultCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<script type="text/javascript">
	$(function(){
		OlbDistributorNewInfo.init();
	});
	var OlbDistributorNewInfo = (function(){
		var validatorVAL;
		var supDDB;
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
			
			if (!UpdateMode) {
				var configProductStore = {
					width: '100%',
					placeHolder: uiLabelMap.BSClickToChoose,
					useUrl: true,
					url: 'loadProductStores?getAll=N',
					key: 'productStoreId',
					value: 'storeName',
					autoDropDownHeight: false,
					dropDownHeight: 200,
					multiSelect: true,
					root: 'productStores',
				}
				new OlbComboBox($("#txtProductStore"), null, configProductStore, []);
				
				var configCatalog = {
					width: '100%',
					placeHolder: uiLabelMap.BSClickToChoose,
					useUrl: true,
					url: 'loadProdCatalogs',
					key: 'prodCatalogId',
					value: 'catalogName',
					autoDropDownHeight: true,
					dropDownHeight: 200,
					multiSelect: true,
					root: 'prodCatalogs',
				}
				new OlbComboBox($("#txtCatalog"), null, configCatalog, []);
			}
			
			var configSupervisor = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: "partyId", type: "string" },
                  	{name: "partyCode", type: "string" },
                  	{name: "fullName", type: "string" }
				],
				columns: [
					{text: multiLang.BSSupervisorId, datafield: "partyCode", width: 150},
					{text: multiLang.BSSupervisor, datafield: "fullName"}
				],
				url: 'JQGetListGTSupervisor',
				useUtilFunc: true,
				
				key: 'partyId',
                keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				autorowheight: true, 
				filterable: true, 
				showfilterrow: true,
				selectedIndex: 0,
			};
            supDDB = new OlbDropDownButton($("#divSupervisor"), $("#jqxgridSupervisor"), null, configSupervisor, []);
			<#--
			GridUtils.initDropDownButton({
					url: "JQGetListGTSupervisorDepartment", autorowheight: true, filterable: true, showfilterrow: true,
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
			-->
			
		}
		var initEvent = function(){
			if (!UpdateMode) {
				$("#partyCode").on('keyup', function() {
					$("#wn_acc_userLoginId").val($("#partyCode").val());
				});
			}
			$("#logoImage").click(function() {
				$("#logoImageUrl").click();
			});
			$("#logoImageUrl").change(function(){
				Images.readURL(this, $("#logoImage"));
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			if (UpdateMode) {
				extendRules = [
						{input: "#partyCode", message: multiLang.fieldRequired, action: 'keyup, blur',
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
                        {input: "#partyCode", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(),multiLang.BSCheckId);
                            }
                        },
						{input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
							rule: function (input, commit) {
								var value = (input.jqxDropDownButton("val")+"").trim();
								if (value) {
									return true;
								}
								return false;
							}
						},
			            {input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
                        {input: "#groupName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(),multiLang.BSCheckSpecialCharacter);
                            }
                        },
			            {input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: "keyup, blur",
					    	rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
			            },
                        {input: "#txtPhoneNumber", message: multiLang.BSPhoneIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(),multiLang.BSCheckPhone);
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
	    				{input: "#partyCode", message: multiLang.fieldRequired, action: 'keyup, blur',
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
                        {input: "#partyCode", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(),multiLang.BSCheckId);
                            }
                        },
						{input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
							rule: function (input, commit) {
								var value = (input.jqxDropDownButton("val")+"").trim();
								if (value) {
									return true;
								}
								return false;
							}
						},
			            {input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
                        {input: "#groupName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                return checkRex(input.val(),multiLang.BSCheckSpecialCharacter);
                            }
                        },
				        {input: '#txtProductStore', message: multiLang.fieldRequired, action: 'change', 
				        	rule: function (input, commit) {
				        		var values = input.jqxComboBox('getSelectedItems');
				        		if (_.isEmpty(values)) {
				        			return false;
				        		}
				        		return true;
				        	}
				        },
				        {input: '#txtCatalog', message: multiLang.fieldRequired, action: 'change', 
				        	rule: function (input, commit) {
				        		var values = input.jqxComboBox('getSelectedItems');
				        		if (_.isEmpty(values)) {
				        			return false;
				        		}
				        		return true;
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
				$("#taxAuthInfos").val(data.taxAuthInfos);
				$("#txtPhoneNumber").val(data.contactNumber);
				$("#txtEmailAddress").val(data.infoString);
				$("#infoStringId").val(data.infoStringId);
                supDDB.selectItem([data.supervisorId]);
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
					taxAuthInfos: $("#taxAuthInfos").val(),
					contactNumber: $("#txtPhoneNumber").val(),
					infoString: $("#txtEmailAddress").val(),
					infoStringId: $("#infoStringId").val(),
					supervisorId: (Grid.getDropDownValue($("#divSupervisor"))+"").trim(),
				};
			} else {
				var value = {
					partyCode: $("#partyCode").val(),
					groupName: $("#groupName").val(),
					groupNameLocal: $("#groupName").val(),
					officeSiteName: $("#officeSiteName").val(),
					comments: $("#comments").val(),
					currencyUomId: $("#currencyUomId").jqxComboBox("val"),
					taxAuthInfos: $("#taxAuthInfos").val(),
					toName: $("#wn_toName").val(),
					attnName: $("#wn_attnName").val(),
					postalCode: $("#wn_postalCode").val(),
					countryGeoId: $("#wn_countryGeoId").jqxComboBox("val"),
					stateProvinceGeoId: $("#wn_stateProvinceGeoId").jqxComboBox("val"),
					districtGeoId: $("#wn_countyGeoId").jqxComboBox("val"),
					wardGeoId: $("#wn_wardGeoId").jqxComboBox("val"),
					address1: $("#wn_address1").val(),
					contactNumber: $("#txtPhoneNumber").val(),
					infoString: $("#txtEmailAddress").val(),
					currentPassword: $("#wn_acc_password").val(),
					currentPasswordVerify: $("#wn_acc_passwordVerify").val(),
					supervisorId: (Grid.getDropDownValue($("#divSupervisor"))+"").trim(),
					productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore")),
					productCatalogs: LocalUtil.getValueSelectedJqxComboBox($("#txtCatalog")),
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
				
				// get product stores
				var productStoresDesc = "";
				var productStoresSelected = $("#txtProductStore").jqxComboBox("getSelectedItems");
				if (!_.isEmpty(productStoresSelected)) {
					var length = productStoresSelected.length;
					for (var i = 0; i < length; i++) {
						var item = productStoresSelected[i];
						if (!_.isEmpty(item.label)) {
							if (i > 0) productStoresDesc += ", ";
							productStoresDesc += item.label;
						}
					}
				}
				
				// get product catalog
				var productCatalogsDesc = "";
				var productCatalogsSelected = $("#txtCatalog").jqxComboBox("getSelectedItems");
				if (!_.isEmpty(productCatalogsSelected)) {
					var length = productCatalogsSelected.length;
					for (var i = 0; i < length; i++) {
						var item = productCatalogsSelected[i];
						if (!_.isEmpty(item.label)) {
							if (i > 0) productCatalogsDesc += ", ";
							productCatalogsDesc += item.label;
						}
					}
				}
				
				// process value
				var extendValue = {
					addressFullName: addressFullName,
					supervisorDesc: $("#divSupervisor").jqxDropDownButton("val"),
					productStoresDesc: productStoresDesc,
					productCatalogsDesc: productCatalogsDesc,
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

<#--
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
							location.href = "DistributorDetail?partyId=" + res['partyId'];
						}
					}
				}, 2000);
			}
		};
-->
