<div id="alterPopupSupplierNew" style="display:none">
	<div>${uiLabelMap.POAddSupplierManager}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSupplierId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sup_partyCode" tabindex="5"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSupplierName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sup_partyName" tabindex="6"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BACCTaxCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sup_taxCode" tabindex="7"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_currencyUomId" tabindex="9"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthParty}</label>
						</div>
				   		<div class='span7'>
							<div id="wn_sup_taxAuthPartyId">
								<div id="wn_sup_taxAuthPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSVatTaxAuthGeo}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_taxAuthGeoId">
								<div id="wn_sup_taxAuthGeoGrid"></div>
							</div>
				   		</div>
					</div>
				</div><!--.span6-->
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSEmail}</label>
						</div>
						<div class='span7'>
							<input type="email" id="wn_sup_email" tabindex="11"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSPhoneNumber}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sup_telecomNumber" tabindex="12"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_sup_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAddress1}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_sup_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
				</div><!--.span6-->
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_sup_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_sup_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script tyle="text/javascript">
	multiLang = _.extend(multiLang, {
		POAddSupplierManager: "${StringUtil.wrapString(uiLabelMap.POAddSupplierManager)}",
		POUpdateSupplierManager: "${StringUtil.wrapString(uiLabelMap.POUpdateSupplierManager)}",
		DmsAddAddress: "${StringUtil.wrapString(uiLabelMap.DmsAddAddress)}",
		DmsEditAddress: "${StringUtil.wrapString(uiLabelMap.DmsEditAddress)}",
	});
	
	<#assign geoTypeList = delegator.findList("GeoType", null , null, orderBy, null, false)!/>
	var geoTypeList = [
	<#if geoTypeList?exists>
	    <#list geoTypeList as geoTypeL>
	    {	geoTypeId: "${geoTypeL.geoTypeId}",
	    	description: "${StringUtil.wrapString(geoTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	$(function(){
		OlbSupplierNew.init();
	});
	
	var OlbSupplierNew = (function(){
		var taxAuthPartyIdDDB;
		var currencyUomDDL;
		var validatorVAL;
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var windowObj = $("#alterPopupSupplierNew");
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_sup_partyCode"), {width: '97%'});
			jOlbUtil.input.create($("#wn_sup_partyName"), {width: '97%'});
			jOlbUtil.input.create($("#wn_sup_taxCode"), {width: '97%'});
			jOlbUtil.input.create($("#wn_sup_email"), {width: '97%'});
			jOlbUtil.input.create($("#wn_sup_telecomNumber"), {width: '97%'});
			
			jOlbUtil.windowPopup.create(windowObj, {maxWidth: 960, width: 960, height: 400, cancelButton: $("#wn_sup_alterCancel")});
		};
		var initElementComplex = function(){
			var configCurrencyUom = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'uomId',
				value: 'descriptionSearch',
				width: '98%',
				height: '28px',
				dropDownHeight: 200,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				searchMode: 'containsignorecase',
				renderer: null,
				renderSelectedItem : null,
			};
			currencyUomDDL = new OlbDropDownList($("#wn_sup_currencyUomId"), currencyUomData, configCurrencyUom, ["${defaultCurrencyUomId?if_exists}"]);
			
			var configTaxAuthParty = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'taxAuthPartyId', type: 'string'},
			   		{name: 'taxAuthGeoId', type: 'string'},
			      	{name: 'groupName', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSPartyId}', datafield: 'taxAuthPartyId', width: '20%'},
					{text: '${uiLabelMap.BSTaxAuthGeoId}', datafield: 'taxAuthGeoId', width: '20%'},
					{text: '${uiLabelMap.BSGroupName}', datafield: 'groupName'}
				],
				url: 'JQGetListTaxAuthority',
				useUtilFunc: true,
				key: 'taxAuthPartyId',
				description: ['groupName']
			};
			taxAuthPartyIdDDB = new OlbDropDownButton($("#wn_sup_taxAuthPartyId"), $("#wn_sup_taxAuthPartyGrid"), null, configTaxAuthParty, ["VNM_TAX"]);
			
			var configTaxAuthGeo = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				heightButton: '28px',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'geoId', type: 'string'},
			   		{name: 'geoTypeId', type: 'string'},
			   		{name: 'geoName', type: 'string'},
			      	{name: 'geoCode', type: 'string'},
			      	{name: 'geoSecCode', type: 'string'},
			      	{name: 'abbreviation', type: 'string'},
			      	{name: 'wellKnownText', type: 'string'}
			    ],
				columns: [
					{text: '${uiLabelMap.BSGeoId}', datafield: 'geoId', width: '10%'},
					{text: '${uiLabelMap.BSGeoTypeId}', datafield: 'geoTypeId', width: '15%',
						cellsrenderer: function(column, row, value){
							for(var i = 0;  i < geoTypeList.length; i++){
								if(geoTypeList[i].geoTypeId == value){
									return '<span title=' + value + '>' + geoTypeList[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>'
						},
					},
					{text: '${uiLabelMap.BSGeoName}', datafield: 'geoName'},
					{text: '${uiLabelMap.BSGeoCode}', datafield: 'geoCode', width: '10%'},
					{text: '${uiLabelMap.BSGeoSecCode}', datafield: 'geoSecCode', width: '10%'},
					{text: '${uiLabelMap.BSAbbreviation}', datafield: 'abbreviation', width: '13%'},
					{text: '${uiLabelMap.BSWellKnownText}', datafield: 'wellKnownText', width: '15%'}
				],
				url: 'JQGetListGeos',
				useUtilFunc: true,
				key: 'geoId',
				description: ['geoName']
			};
			taxAuthGeoIdDDB = new OlbDropDownButton($("#wn_sup_taxAuthGeoId"), $("#wn_sup_taxAuthGeoGrid"), null, configTaxAuthGeo, ["VNM"]);
			
			var configCountry = {
	    		width: "99%",
	    		height: 25,
	    		key: "geoId",
	    		value: "geoName",
	    		displayDetail: true,
	    		placeHolder: uiLabelMap.BSClickToChoose,
	    		useUrl: true, url: "getListCountryGeo",
	    		datafields: [{name: 'geoId'}, {name: 'geoName'}],
		        autoComplete: true, searchMode: 'containsignorecase', renderer: null, renderSelectedItem: null, 
		        selectedIndex: 0, 
	    	};
			countryGeoCBB = new OlbComboBox($("#wn_sup_countryGeoId"), null, configCountry, ["${defaultCountryGeoId?if_exists}"]);
		    
		    var initDropDownGeoState = function(elementObj, sname, selectArr){
		    	var url = "jqxGeneralServicer?sname=" + sname;
		    	var configGeo = {
		    		width: "99%", 
		    		height: 25, 
		    		key: "geoId", 
		    		value: "geoName", 
		    		displayDetail: true,
		    		placeHolder: uiLabelMap.BSClickToChoose,
		    		useUrl: true, url: url,
		    		datafields: [{name: 'geoId'}, {name: 'geoName'}],
			        autoComplete: true, searchMode: 'containsignorecase', renderer: null, renderSelectedItem: null,
			        selectedIndex: 0, 
		    	};
		    	return new OlbComboBox(elementObj, null, configGeo, selectArr);
		    };
		    
		    var stateProvinceGeoId = "";
		    var urlDropDownStateProvince = 'JQGetAssociatedStateListGeo';
		    <#if defaultCountryGeoId?exists>
		    	urlDropDownStateProvince += "&geoId=" + "${defaultCountryGeoId?if_exists}" + "&pagesize=0&pagenum=1";
		    </#if>
		    stateProvinceGeoCBB = initDropDownGeoState($("#wn_sup_stateProvinceGeoId"), urlDropDownStateProvince, [stateProvinceGeoId]);
		    districtGeoCBB = initDropDownGeoState($("#wn_sup_countyGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0&geoId=' + stateProvinceGeoId);
		    wardGeoCBB = initDropDownGeoState($("#wn_sup_wardGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0');
		};
		var initEvent = function(){
			$('#wn_sup_countryGeoId').on('change', function (event){
				getAssociatedState($('#wn_sup_stateProvinceGeoId'), event);
			});
			$('#wn_sup_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($('#wn_sup_countyGeoId'), event);
			});
			$('#wn_sup_countyGeoId').on('change', function (event){
			    getAssociatedState($('#wn_sup_wardGeoId'), event);
			});
			
			var getAssociatedState = function(comboBoxObj, event) {
				var args = event.args;
			    if (args) {
				    var item = args.item;
				    if (item) {
				    	var geoId = item.value;
				    	if (geoId) {
				    		var tmpSource = $(comboBoxObj).jqxComboBox('source');
							if(typeof(tmpSource) != 'undefined'){
								tmpSource._source.url = "jqGetAssociatedStateOtherListGeo?geoId=" + geoId + "&pagesize=0&pagenum=1";
								$(comboBoxObj).jqxComboBox('clearSelection');
								$(comboBoxObj).jqxComboBox('source', tmpSource);
								$(comboBoxObj).jqxComboBox("selectIndex", 0);
							}
				    	}
				    }
				}
			};
			
			$("#wn_sup_alterSave").click(function() {
				if (!validatorVAL.validate()) return false;
				
				var dataMap = getValue();
				$.ajax({
					type: 'POST',
					url: "createPartySupplierQuick",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	if (data.partyId != undefined && data.partyId != null) {
						        		var supplierDDB = OlbSupplierProductAddNew.getObj().supplierDDB;
						        		if (typeof(supplierDDB) != "undefined") {
						        			supplierDDB.getGrid().updateBoundData();
						        		}
						        		windowObj.jqxWindow("close");
						        	}
								}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
		            {input: '#wn_sup_partyCode', type: 'validCannotSpecialCharactor'},
					{input: '#wn_sup_partyName', type: 'validInputNotNull'},
					{input: '#wn_sup_currencyUomId', type: 'validObjectNotNull', objType: 'dropDownList'},
					
					{input: '#wn_sup_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_sup_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_sup_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_sup_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_sup_address1', type: 'validInputNotNull'},
	            ];
			validatorVAL = new OlbValidator(windowObj, mapRules, extendRules, {position: 'bottom'});
		};
		var clean = function() {
			$("#wn_sup_partyCode").val("");
			$("#wn_sup_partyName").val("");
			$("#wn_sup_taxCode").val("");
			$("#wn_sup_email").val("");
			$("#wn_sup_telecomNumber").val("");
			$("#wn_sup_address").val("");
		};
		var setValue = function(data) {
			if (data) {
				$("#wn_sup_partyCode").val(data.partyCode);
				$("#wn_sup_partyName").val(data.groupName);
				$("#wn_sup_taxCode").val(data.taxCode);
				$("#wn_sup_taxAuthPartyId").jqxComboBox("val", data.taxAuthPartyId);
				$("#wn_sup_taxAuthGeoId").jqxComboBox("val", data.taxAuthGeoId);
				$("#wn_sup_currencyUomId").jqxComboBox("val", data.preferredCurrencyUomId);
				$("#wn_sup_email").val(data.emailAddress);
				$("#wn_sup_telecomNumber").val(data.phoneNumber);
				
				countryGeoCBB.selectItem([data.countryGeoId]);
				stateProvinceGeoCBB.selectItem([data.stateProvinceGeoId]);
				districtGeoCBB.selectItem([data.districtGeoId]);
				wardGeoCBB.selectItem([data.wardGeoId]);
			}
		};
		var getValue = function() {
			var value = {
				partyCode: $("#wn_sup_partyCode").val(),
				groupName: $("#wn_sup_partyName").val(),
				taxCode: $("#wn_sup_taxCode").val(),
				taxAuthGeoId: taxAuthGeoIdDDB.getValue(),
				taxAuthPartyId: taxAuthPartyIdDDB.getValue(),
				preferredCurrencyUomId: currencyUomDDL.getValue(),
				emailAddress: $("#wn_sup_email").val(),
				phoneNumber: $("#wn_sup_telecomNumber").val(),
				
				countryGeoId: countryGeoCBB.getValue(),
				stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
				districtGeoId: districtGeoCBB.getValue(),
				wardGeoId: wardGeoCBB.getValue(),
				address1: $('#wn_sup_address1').val(),
			};
			return value;
		};
		return {
			init: init
		};
	}());
</script>
