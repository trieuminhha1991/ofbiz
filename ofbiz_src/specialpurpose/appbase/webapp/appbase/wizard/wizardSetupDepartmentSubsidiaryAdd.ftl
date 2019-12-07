<div id="alterpopupWindowStep2AddSub" style="display:none">
	<div>${uiLabelMap.BSCreateNewSubsidiary}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSubsidiaryId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step2_sub_partyId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSubsidiaryName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step2_sub_partyName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class='span7'>
							<input type="tel" id="wn_step2_sub_phoneNumber"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.EmailAddress}</label>
						</div>
						<div class='span7'>
							<input type="email" id="wn_step2_sub_emailAddress"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSOfficeSiteName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step2_sub_officeSiteName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSTaxCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step2_sub_taxAuthInfos"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_step2_sub_currencyUomId"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_step2_sub_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_step2_sub_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_step2_sub_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_step2_sub_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAddress1}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step2_sub_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_step2_sub_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_step2_sub_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var loadOlbWizardStep2Sub = false;
	var OlbWizardStep2Sub = (function(){
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var validatorVAL;
		
		var init = function(){
			initWindow();
		};
		var initContent = function(){
			if (!loadOlbWizardStep2Sub) {
				initElement();
				initElementComplex();
				initEvent();
				initValidateForm();
				
				loadOlbWizardStep2Sub = true;
			}
		};
		var initWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowStep2AddSub"), {maxWidth: 960, width: 960, height: 460, cancelButton: $("#wn_step2_sub_alterCancel")});
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_step2_sub_partyId"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step2_sub_partyName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step2_sub_phoneNumber"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step2_sub_emailAddress"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step2_sub_officeSiteName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step2_sub_taxAuthInfos"), {width: '98%'});

			jOlbUtil.input.create("#wn_step2_sub_address1", {maxLength: 255});
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
			currencyUomCCB = new OlbComboBox($("#wn_step2_sub_currencyUomId"), listCurrencyUom, configCurrency, ["${defaultCurrencyUomId?if_exists}"]);
			
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
			countryGeoCBB = new OlbComboBox($("#wn_step2_sub_countryGeoId"), null, configCountry, ["${countryGeoId?if_exists}"]);
		    
		    var initDropDownGeoState = function(elementObj, param, selectArr){
		    	var url = "jqGetAssociatedStateOtherListGeo?" + param;
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
		    var urlDropDownStateProvince = '';
		    <#if countryGeoId?exists>
		    	urlDropDownStateProvince += "&geoId=" + "${countryGeoId?if_exists}" + "&pagesize=0&pagenum=1";
		    </#if>
		    stateProvinceGeoCBB = initDropDownGeoState($("#wn_step2_sub_stateProvinceGeoId"), urlDropDownStateProvince, [stateProvinceGeoId]);
		    districtGeoCBB = initDropDownGeoState($("#wn_step2_sub_countyGeoId"), '&pagesize=0&geoId=' + stateProvinceGeoId);
		    wardGeoCBB = initDropDownGeoState($("#wn_step2_sub_wardGeoId"), '&pagesize=0');
		};
		var initEvent = function(){
			$("#step1").on("click", function(){
				$("#alterpopupWindowStep2AddSub").jqxWindow("open");
			});
			
			$('#wn_step2_sub_countryGeoId').on('change', function (event){
				getAssociatedState($('#wn_step2_sub_stateProvinceGeoId'), event);
			});
			$('#wn_step2_sub_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step2_sub_countyGeoId'), event);
			});
			$('#wn_step2_sub_countyGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step2_sub_wardGeoId'), event);
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
								tmpSource._source.url = "jqxGenjqGetAssociatedStateOtherListGeo?geoId=" + geoId + "&pagesize=0&pagenum=1";
								$(comboBoxObj).jqxComboBox('clearSelection');
								$(comboBoxObj).jqxComboBox('source', tmpSource);
								$(comboBoxObj).jqxComboBox("selectIndex", 0);
							}
				    	}
				    }
				}
			};
			
			$('#wn_step2_sub_alterSave').on('click', function(){
				if(!validatorVAL.validate()) return false;
				
            	var dataMap = {
					partyId: $('#wn_step2_sub_partyId').val(),
					partyName: $('#wn_step2_sub_partyName').val(),
					phoneNumber: $('#wn_step2_sub_phoneNumber').val(),
					emailAddress: $('#wn_step2_sub_emailAddress').val(),
					officeSiteName: $('#wn_step2_sub_officeSiteName').val(),
					taxAuthInfos: $('#wn_step2_sub_taxAuthInfos').val(),
					currencyUomId: $('#wn_step2_sub_currencyUomId').val(),
					
					countryGeoId: countryGeoCBB.getValue(),
					stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
					districtGeoId: districtGeoCBB.getValue(),
					wardGeoId: wardGeoCBB.getValue(),
					address1: $('#wn_step2_sub_address1').val(),
				};
				$('#wn_step2_jqxgridSubsidiary').jqxGrid('addrow', null, getValue());
				$("#alterpopupWindowStep2AddSub").jqxWindow("close");
				<#--
				var uid = $("#wn_step2_jqxgridSubsidiary").data("uid");
				if (uid) {
					$('#wn_step2_jqxgridSubsidiary').jqxGrid('updaterow', uid, getValue());
				} else {
					$('#wn_step2_jqxgridSubsidiary').jqxGrid('addrow', null, getValue());
				}
				-->
			});
		};
		var clearWindowData = function(){
			$('#wn_step2_sub_partyId').val("");
			$('#wn_step2_sub_partyName').val("");
			$('#wn_step2_sub_phoneNumber').val("");
			$('#wn_step2_sub_emailAddress').val("");
			$('#wn_step2_sub_officeSiteName').val("");
			$('#wn_step2_sub_taxAuthInfos').val("");
			currencyUomCCB.clearAll();
			countryGeoCBB.selectItem(null, 0);
			stateProvinceGeoCBB.selectItem(null, 0);
			districtGeoCBB.selectItem(null, 0);
			wardGeoCBB.selectItem(null, 0);
			$("#wn_step2_sub_address1").jqxInput("val", null);
		};
		var setValue = function(data){
			if (data.partyId) $('#wn_step2_sub_partyId').val(data.partyId);
			if (data.partyName) $('#wn_step2_sub_partyName').val(data.partyName);
			if (data.phoneNumber) $('#wn_step2_sub_phoneNumber').val(data.phoneNumber);
			if (data.emailAddress) $('#wn_step2_sub_emailAddress').val(data.emailAddress);
			if (data.officeSiteName) $('#wn_step2_sub_officeSiteName').val(data.officeSiteName);
			if (data.taxAuthInfos) $('#wn_step2_sub_taxAuthInfos').val(data.taxAuthInfos);
			if (data.currencyUomId) currencyUomCCB.selectItem([data.currencyUomId]);
			if (data.countryGeoId) countryGeoCBB.selectItem([data.countryGeoId]);
			if (data.stateProvinceGeoId) stateProvinceGeoCBB.selectItem([data.stateProvinceGeoId]);
			if (data.districtGeoId) districtGeoCBB.selectItem([data.districtGeoId]);
			if (data.wardGeoId) wardGeoCBB.selectItem([data.wardGeoId]);
			if (data.address1) $("#wn_step2_sub_address1").val(data.address1);
		};
		var getValue = function(){
			var dataMap = {
				partyId: $('#wn_step2_sub_partyId').val(),
				partyCode: $('#wn_step2_sub_partyId').val(),
				groupName: $('#wn_step2_sub_partyName').val(),
				phoneNumber: $('#wn_step2_sub_phoneNumber').val(),
				emailAddress: $('#wn_step2_sub_emailAddress').val(),
				officeSiteName: $('#wn_step2_sub_officeSiteName').val(),
				taxAuthInfos: $('#wn_step2_sub_taxAuthInfos').val(),
				currencyUomId: $('#wn_step2_sub_currencyUomId').val(),
				
				countryGeoId: countryGeoCBB.getValue(),
				stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
				districtGeoId: districtGeoCBB.getValue(),
				wardGeoId: wardGeoCBB.getValue(),
				address1: $('#wn_step2_sub_address1').val(),
			}
			return dataMap;
		};
		
		var initValidateForm = function(){
			var mapRules = [
		        {input: '#wn_step2_sub_partyId', type: 'validInputNotNull'},
		        {input: '#wn_step2_sub_partyName', type: 'validInputNotNull'},
		        {input: '#wn_step2_sub_phoneNumber', type: 'validInputNotNull'},
		        {input: '#wn_step2_sub_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step2_sub_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step2_sub_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step2_sub_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step2_sub_address1', type: 'validInputNotNull'},
		    ];
	   		validatorVAL = new OlbValidator($('#alterpopupWindowStep2AddSub'), mapRules);
		};
		return {
			init: init,
			initContent: initContent,
			setValue: setValue
		};
	}());
</script>