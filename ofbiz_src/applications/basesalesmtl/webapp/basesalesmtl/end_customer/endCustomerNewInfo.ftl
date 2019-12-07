<#-- upgrade from .../basesalesmtl/supervisor/createCustomer/generalInfo.ftl -->

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
				<div class="span5"><label class="text-right">${uiLabelMap.CustomerID}</label></div>
				<div class="span7"><input type="text" id="partyCode" class="no-space" tabindex="2"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSCustomerName}</label></div>
				<div class="span7"><input type="text" id="groupName" tabindex="1"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.PartyTaxAuthInfos}</label></div>
				<div class="span7"><input type="text" id="taxAuthInfos" tabindex="4"/></div>
			</div>
			<#if !parameters.partyId?exists>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSPSProductStore}</label></div>
				<div class="span7">
					<div id="wn_productStoreId">
	                    <div id="wn_productStoreGrid"></div>
	                </div>
				</div>
			</div>
			</#if>
			<#if !parameters.partyId?exists>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsPartyFromType}</label></div>
				<div class="span7">
					<div id="txtPartyType"></div>
				</div>
			</div>
			<#else>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}</label></div>
				<div class="span7">
					<input type="email" id="txtEmailAddress" tabindex="6"/>
				</div>
			</div>
			</#if>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSDescription}</label></div>
				<div class="span7"><input type="text" id="comments" tabindex="5"/></div>
			</div>
		</div><!--.span5-->
		<div class="span5">
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSCurrencyUomId}</label></div>
				<div class="span7"><div id="currencyUomId"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_officeSiteName}</label></div>
				<div class="span7"><input type="text" id="officeSiteName" tabindex="3"/></div>
			</div>
			<#--
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSSupervisor}</label></div>
				<div class="span7">
					<div id="divSupervisor">
						<div style="border-color: transparent;" id="jqxgridSupervisor"></div>
					</div>
				</div>
			</div>
			-->
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSSalesman}</label></div>
				<div class="span7">
					<div id="divSalesman">
						<div style="border-color: transparent;" id="jqxgridSalesman"></div>
					</div>
				</div>
			</div>
			<#if !parameters.partyId?exists>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.BSRoute}</label></div>
				<div class="span7">
					<div id="divRoute">
						<div style="border-color: transparent;" id="jqxgridRoute"></div>
					</div>
				</div>
			</div>
			</#if>
			<div class="row-fluid hide" id="divRepresentativeOfficeId">
				<div class="span5"><label class="text-right">${uiLabelMap.CommonRepresentativeOffice}</label></div>
				<div class="span7">
					<div id="divRepresentativeOffice">
						<div style="border-color: transparent;" id="jqxgridRepresentativeOffice"></div>
					</div>
				</div>
			</div>
			<#if !parameters.partyId?exists>
			<div class="row-fluid" id="divConsigneeId">
				<div class="span5"><label class="text-right">${uiLabelMap.BSConsignee}</label></div>
				<div class="span7">
					<div id="divConsignee">
			            <div style="border-color: transparent;" id="jqxgridConsignee"></div>
			        </div>
				</div>
			</div>
			<#else>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.PhoneNumber}</label></div>
				<div class="span7"><input type="text" id="txtPhoneNumber" tabindex="7"/></div>
			</div>
			</#if>
		</div><!--.span5-->
	</div><!--.form-horizontal-->
</div>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<script type="text/javascript">
	var listCurrencyUom = [
	<#if listCurrencyUom?exists>
		<#list listCurrencyUom as item>{
			uomId: "${item.uomId?if_exists}",
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},</#list>
	</#if>
	];
	<#--
	<#assign listPartyType = delegator.findByAnd("PartyType", {"parentTypeId": "PARTY_GROUP_CUSTOMER"}, null, false)!/>
	var listPartyType = [
	<#if listPartyType?exists>
		<#list listPartyType as item>{
			partyTypeId: '${item.partyTypeId?if_exists}',
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},</#list>
	</#if>
	];
	-->
	
	if (typeof (OlbCustomerMTNewInfo) == "undefined") {
		var OlbCustomerMTNewInfo = (function(){
			var validatorVAL;
			var currencyUomDDL;
			var partyTypeDDL;
			var routeDDB;
			var consigneeDDB;
			//var supervisorDDB;
			var salesmanDDB;
			var representativeOfficeDDB;
			var productStoreDDB;
			var productStores = [];
			var init = function(){
				initElement();
				initElementComplex();
				initValidateForm();
				initEvent();
			};
			var initElement = function(){
				jOlbUtil.input.create('#partyCode', {width: '98%'});
				jOlbUtil.input.create('#officeSiteName', {width: '98%'});
				jOlbUtil.input.create('#groupName', {width: '98%'});
				jOlbUtil.input.create('#taxAuthInfos', {width: '98%'});
				jOlbUtil.input.create('#comments', {width: '98%'});
				if (UpdateMode) jOlbUtil.input.create('#txtEmailAddress', {width: '98%'});
				if (UpdateMode) jOlbUtil.input.create('#txtPhoneNumber', {width: '98%'});
			};
			var initElementComplex = function(){
				var configCurrencyUom = {
				    width: '100%',
				    placeHolder: uiLabelMap.BSClickToChoose,
				    useUrl: false,
				    url: '',
				    key: 'uomId',
				    value: 'description',
				    autoDropDownHeight: true
				}
				currencyUomDDL = new OlbDropDownList($("#currencyUomId"), listCurrencyUom, configCurrencyUom, ["VND"]);
				
				/*var configSupervisor = {
				    useUrl: true,
				    root: 'results',
				    widthButton: '100%',
				    showdefaultloadelement: false,
				    autoshowloadelement: false,
				    datafields: [{ name: "partyId", type: "string" },
	                        	{ name: "partyCode", type: "string" },
	                        	{ name: "groupName", type: "string" }],
				    columns: [
				    	{text: uiLabelMap.BSCompanyId, datafield: "partyCode", width: 150},
	                    {text: uiLabelMap.BSCompanyName, datafield: "groupName"}
				    ],
				    url: 'JQGetListMTSupervisorDepartment',
				    useUtilFunc: true,
				    
				    key: 'partyId',
				    keyCode: 'partyCode',
				    description: ['groupName'],
				    autoCloseDropDown: true,
				    filterable: true,
				    sortable: true,
				    displayDetail: true,
				    dropDownHorizontalAlignment: 'right',
				};
				supervisorDDB = new OlbDropDownButton($("#divSupervisor"), $("#jqxgridSupervisor"), null, configSupervisor, []);
				*/
				
				var configSalesman = {
					useUrl: true,
					root: 'results',
					widthButton: '100%',
					showdefaultloadelement: false,
					autoshowloadelement: false,
					datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
					columns: [
						{text: uiLabelMap.BSId, datafield: 'partyCode', width: '30%'},
						{text: uiLabelMap.BSFullName, datafield: 'fullName', width: '69%'},
					],
					url: '', //'JQGetListSalesmanByDistributor&distributorCode=' + currentParty.productStoreId,
					useUtilFunc: true,
					
					key: 'partyId',
					keyCode: 'partyCode',
					description: ['fullName'],
					autoCloseDropDown: true,
					filterable: true,
					sortable: true,
					dropDownHorizontalAlignment: 'right',
				};
				salesmanDDB = new OlbDropDownButton($("#divSalesman"), $("#jqxgridSalesman"), null, configSalesman, []);
				
				var configRepresentativeOffice = {
	                useUrl: true,
	                root: 'results',
	                widthButton: '100%',
	                showdefaultloadelement: false,
	                autoshowloadelement: false,
	                dropDownHorizontalAlignment: "right",
	                datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}],
	                columns: [
	                    {text: uiLabelMap.BSCompanyId, datafield: 'partyCode', width: '30%'},
	                    {text: uiLabelMap.BSCompanyName, datafield: 'groupName', width: '70%'},
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
				
				if (!UpdateMode) {
					var configPartyType = {
						width: '100%',
						placeHolder: uiLabelMap.BSClickToChoose,
						useUrl: true,
						url: '',
						key: 'partyTypeId',
						value: 'description',
						autoDropDownHeight: true
					}
					partyTypeDDL = new OlbDropDownList($("#txtPartyType"), null, configPartyType, []);
					
					var configRoute = {
						useUrl: true,
						root: 'results',
						widthButton: '100%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'routeId', type: 'string'}, {name: 'routeCode', type: 'string'}, {name: 'routeName', type: 'string'}],
						columns: [
							{text: uiLabelMap.BSId, datafield: 'routeCode', width: '30%'},
							{text: uiLabelMap.BSFullName, datafield: 'routeName', width: '69%'},
						],
						url: '', //'JQGetListRouteBySalesman&salesmanId=' + currentParty.salesmanId,
						useUtilFunc: true,
						
						key: 'routeId',
						keyCode: 'routeCode',
						description: ['routeName'],
						autoCloseDropDown: true,
						filterable: true,
						sortable: true,
						dropDownHorizontalAlignment: 'right',
					};
					routeDDB = new OlbDropDownButton($("#divRoute"), $("#jqxgridRoute"), null, configRoute, []);
					
					var configConsignee = {
					    useUrl: true,
					    root: 'results',
					    widthButton: '100%',
					    showdefaultloadelement: false,
					    autoshowloadelement: false,
					    datafields: [{ name: "partyId", type: "string" },
		                        	{ name: "partyCode", type: "string" },
		                        	{ name: "groupName", type: "string" }],
					    columns: [
					    	{text: uiLabelMap.BSCompanyId, datafield: "partyCode", width: 150},
		                    {text: uiLabelMap.BSCompanyName, datafield: "groupName"}
					    ],
					    url: 'JQGetListMTConsignee&statusId=PARTY_ENABLED',
					    useUtilFunc: true,
					    
					    key: 'partyId',
					    description: ['groupName'],
					    autoCloseDropDown: true,
					    filterable: true,
					    sortable: true,
					    displayDetail: true,
					    dropDownHorizontalAlignment: 'right',
					};
					consigneeDDB = new OlbDropDownButton($("#divConsignee"), $("#jqxgridConsignee"), null, configConsignee, []);
					
					var configProductStore = {
						useUrl: true,
						root: 'results',
						widthButton: '100%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'productStoreId', type: 'string'}, {name: 'storeName', type: 'string'}, {name: 'payToPartyId', type: 'string'}, {name: 'salesMethodChannelEnumId', type: 'string'}],
						columns: [
							{text: uiLabelMap.BSProductStoreId, datafield: 'productStoreId', width: '25%'},
							{text: uiLabelMap.BSStoreName, datafield: 'storeName', width: '25%'},
							{text: uiLabelMap.BSOwner, datafield: 'payToPartyId', width: '25%'},
							{text: uiLabelMap.BSSalesChannelEnumId, datafield: 'salesMethodChannelEnumId', width: '24%'},
						],
						url: 'JQGetProductStoreForRequestNewCustomer&salesMethodChannelEnumId=SMCHANNEL_MT',
						useUtilFunc: true,
						
						key: 'productStoreId',
						description: ['storeName'],
						autoCloseDropDown: true,
						filterable: true,
						sortable: true,
					};
					productStoreDDB = new OlbDropDownButton($("#wn_productStoreId"), $("#wn_productStoreGrid"), null, configProductStore, []);
				}
			};
			var initEvent = function(){
				$("#logoImage").click(function() {
					$("#logoImageUrl").click();
				});
				$("#logoImageUrl").change(function(){
					Images.readURL(this, $("#logoImage"));
				});
				
				if (!UpdateMode) {
					// is create new
				    partyTypeDDL.selectListener(function(itemData){
				    	var partyTypeId = itemData.value;
				    	if (partyTypeId == "SUPERMARKET") {
                            $("#divRepresentativeOfficeId").show();
                        } else {
                            $("#divRepresentativeOfficeId").hide();
                            representativeOfficeDDB.clearAll();
                        }
				    });
				    productStoreDDB.getGrid().rowSelectListener(function(rowData){
						partyTypeDDL.updateSource("jqxGeneralServicer?sname=JQGetPartyTypeByChannel&salesMethodChannelEnumId=" + rowData.salesMethodChannelEnumId + "&pagesize=0&isEndCustomer=Y");
						salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetSalesmanByProductStore&productStoreId=" + rowData.productStoreId);
						routeDDB.clearAll();
		            });
		            salesmanDDB.getGrid().rowSelectListener(function(rowData){
						routeDDB.updateSource("jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + rowData.partyId);
		            });
				}
			}
			var getValue = function() {
				var returnData;
				if (UpdateMode) { // is update
					returnData = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: currencyUomDDL.getValue(),
						taxAuthInfos: $("#taxAuthInfos").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
						salesmanId: salesmanDDB.getValue(),
						representativeOfficeId: representativeOfficeDDB.getValue(),
						//supervisorId: supervisorDDB.getValue(),
					};
				} else { // is create new
					productStores.push(productStoreDDB.getValue())
					returnData = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: currencyUomDDL.getValue(),
						partyTypeId: partyTypeDDL.getValue(),
						taxAuthInfos: $("#taxAuthInfos").val(),
						salesmanId: salesmanDDB.getValue(),
						representativeOfficeId: representativeOfficeDDB.getValue(),
						//supervisorId: supervisorDDB.getValue2(),
						productStores: productStores,
						routeId: routeDDB.getValue(),
						consigneeId: consigneeDDB.getValue(),
					};
					productStores = [];
				}
				return returnData;
			};
			var setValue = function(data){
				if (!_.isEmpty(data)) {
					if (data.logoImageUrl) {
						$("#logoImage").attr("src", data.logoImageUrl);
					}
					
					$("#partyCode").val(data.partyCode);
					$("#groupName").val(data.groupName);
					$("#groupName").val(data.groupNameLocal);
					$("#officeSiteName").val(data.officeSiteName);
					$("#comments").val(data.comments);
					$("#taxAuthInfos").val(data.taxAuthInfos);
					currencyUomDDL.selectItem([data.currencyUomId]);
					
					$("#txtPhoneNumber").val(data.contactNumber);
					$("#txtEmailAddress").val(data.infoString);
					salesmanDDB.selectItem([data.salesmanId]); //data.salesman
					//supervisorDDB.selectItem([data.supervisorId]); //data.supervisor
					salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetSalesmanByProductStore&productStoreId=" + data.productStoreId, null, function(){
						salesmanDDB.selectItem([data.salesmanId]);
					});
					
					if (!UpdateMode) { // is create new
						partyTypeDDL.updateSource("jqxGeneralServicer?sname=JQGetPartyTypeByChannel&salesMethodChannelEnumId=" + data.salesMethodChannelEnumId + "&pagesize=0", null, function(){
							partyTypeDDL.selectItem([data.partyTypeId]);
						});
						routeDDB.updateSource("jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + data.salesmanId, null, function(){
							routeDDB.selectItem([data.routeId]);
						});
					}
					
					if (data.partyTypeId == "SUPERMARKET"){
	                    $("#divRepresentativeOfficeId").show();
	                    representativeOfficeDDB.selectItem([data.representativeOfficeId]);
	                }
				}
			};
			var initValidateForm = function(){
				var extendRules = [
						{input: '#partyCode', message: validFieldRequire, action: 'keyup', 
							rule: function(input, commit){
								if (UpdateMode) return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
								else return true;
							}
						},
						{input: "#txtPhoneNumber", message: uiLabelMap.BSPhoneIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                                if (UpdateMode) return OlbValidatorUtil.validElement(input, commit, "validPhoneNumber");
                                else return true;
                            }
                        },
                        {input: "#txtEmailAddress", message: uiLabelMap.BSEmailIsNotValid, action: "keyup, blur",
                            rule: function (input, commit) {
                            	if (UpdateMode) return OlbValidatorUtil.validElement(input, commit, "validEmailAddress");
                            	else return true;
                            }
                        },
		           ];
				var mapRules = [
			            {input: '#partyCode', type: 'validCannotSpecialCharactor'},
						{input: '#groupName', type: 'validInputNotNull'},
		            ];
		        if (!UpdateMode) {
		        	mapRules.push({input: '#txtPartyType', type: 'validObjectNotNull', objType: 'dropDownList'});
		        	mapRules.push({input: '#wn_productStoreId', type: 'validObjectNotNull', objType: 'dropDownButton'});
	        	}
				validatorVAL = new OlbValidator($('#generalInfo'), mapRules, extendRules, {position: 'bottom', scroll: true});
			};
			var getValidator = function() {
				return validatorVAL;
			};
			return {
				init: init,
				getValidator: getValidator,
				getValue: getValue,
				setValue: setValue,
			}
		}());
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
</script>
