<div id="alterpopupWindowStep1" style="display:none">
	<div>${uiLabelMap.BSSetupOrganization}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="wn_step1_partyId" value=""/>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSOrganizationName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step1_partyName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class='span7'>
							<input type="tel" id="wn_step1_phoneNumber"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.EmailAddress}</label>
						</div>
						<div class='span7'>
							<input type="email" id="wn_step1_emailAddress"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.FormFieldTitle_officeSiteName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step1_officeSiteName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSTaxCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step1_taxAuthInfos"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_step1_currencyUomId"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_step1_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_step1_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_step1_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_step1_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAddress1}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step1_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_step1_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	   			<button id="wn_step1_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_step1_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var OlbWizardStep1 = (function(){
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowStep1"), {maxWidth: 960, width: 960, height: 460, cancelButton: $("#wn_step1_alterCancel")});
			jOlbUtil.input.create($("#wn_step1_partyName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step1_phoneNumber"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step1_emailAddress"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step1_officeSiteName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step1_taxAuthInfos"), {width: '98%'});

			jOlbUtil.input.create("#wn_step1_address1", {maxLength: 255});
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
			currencyUomCCB = new OlbComboBox($("#wn_step1_currencyUomId"), listCurrencyUom, configCurrency, ["${defaultCurrencyUomId?if_exists}"]);
			
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
			countryGeoCBB = new OlbComboBox($("#wn_step1_countryGeoId"), null, configCountry, ["${countryGeoId?if_exists}"]);
		    
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
		    stateProvinceGeoCBB = initDropDownGeoState($("#wn_step1_stateProvinceGeoId"), urlDropDownStateProvince, [stateProvinceGeoId]);
		    districtGeoCBB = initDropDownGeoState($("#wn_step1_countyGeoId"), '&pagesize=0&geoId=' + stateProvinceGeoId);
		    wardGeoCBB = initDropDownGeoState($("#wn_step1_wardGeoId"), '&pagesize=0');
		};
		var initEvent = function(){
			$("#step1").on("click", function(){
				$("#alterpopupWindowStep1").jqxWindow("open");
			});
			
			$('#wn_step1_countryGeoId').on('change', function (event){
				getAssociatedState($('#wn_step1_stateProvinceGeoId'), event);
			});
			$('#wn_step1_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step1_countyGeoId'), event);
			});
			$('#wn_step1_countyGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step1_wardGeoId'), event);
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
			$('#wn_step1_alterSaveAndContinue').on("click", function(){
				createFinishStep1(true);
			});
			
			$('#wn_step1_alterSave').on('click', function(){
				createFinishStep1();
			});
		};
		var createFinishStep1 = function(isContinue){
			if(!validatorVAL.validate()) return false;
			
        	var dataMap = {
				partyId: $('#wn_step1_partyId').val(),
				partyName: $('#wn_step1_partyName').val(),
				phoneNumber: $('#wn_step1_phoneNumber').val(),
				emailAddress: $('#wn_step1_emailAddress').val(),
				officeSiteName: $('#wn_step1_officeSiteName').val(),
				taxAuthInfos: $('#wn_step1_taxAuthInfos').val(),
				currencyUomId: $('#wn_step1_currencyUomId').val(),
				
				countryGeoId: countryGeoCBB.getValue(),
				stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
				districtGeoId: districtGeoCBB.getValue(),
				wardGeoId: wardGeoCBB.getValue(),
				address1: $('#wn_step1_address1').val(),
			};
			$.ajax({
                type: "POST",
                url: "wizardSetupOrganization",
                data: dataMap,
                beforeSend: function(){
                    $("#loader_page_common").show();
                    var step1 = $("#step1");
		        	step1.removeClass("alert-error");
		        	step1.removeClass("alert-success");
	        		var itemI = step1.find("i");
	        		if (itemI) itemI.remove();
                }, 
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	var step1 = $("#step1");
			        	step1.addClass("alert-error");
		        		step1.prepend($('<i class="icon-remove"></i>'));
		        		
			        	return false;
					}, function(data){
                    	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data.partyId) {
			        		$("#wn_step1_partyId").val(data.partyId);
			        		
			        		var step1 = $("#step1");
			        		step1.addClass("alert-success");
			        		step1.prepend($('<i class="icon-ok green"></i>'));
			        		
			        		$('#alterpopupWindowStep1').jqxWindow("close");
			        		
			        		if (isContinue) {
			        			OlbWizardStep2.openWindow();
			        		}
			        	}
                    });
                },
                error: function(){
                    alert("Send to server is false!");
                },
                complete: function(){
                	$("#loader_page_common").hide();
                }
            });
		};
		var clearWindowData = function(){
			$('#wn_step1_partyId').val("");
			$('#wn_step1_partyName').val("");
			$('#wn_step1_phoneNumber').val("");
			$('#wn_step1_emailAddress').val("");
			$('#wn_step1_officeSiteName').val("");
			$('#wn_step1_taxAuthInfos').val("");
			currencyUomCCB.clearAll();
			countryGeoCBB.selectItem(null, 0);
			stateProvinceGeoCBB.selectItem(null, 0);
			districtGeoCBB.selectItem(null, 0);
			wardGeoCBB.selectItem(null, 0);
			$("#wn_step1_address1").jqxInput("val", null);
		};
		var setValue = function(data){
			if (data.partyId) $('#wn_step1_partyId').val(data.partyId);
			if (data.partyName) $('#wn_step1_partyName').val(data.partyName);
			if (data.phoneNumber) $('#wn_step1_phoneNumber').val(data.phoneNumber);
			if (data.emailAddress) $('#wn_step1_emailAddress').val(data.emailAddress);
			if (data.officeSiteName) $('#wn_step1_officeSiteName').val(data.officeSiteName);
			if (data.taxAuthInfos) $('#wn_step1_taxAuthInfos').val(data.taxAuthInfos);
			if (data.currencyUomId) currencyUomCCB.selectItem([data.currencyUomId]);
			if (data.countryGeoId) countryGeoCBB.selectItem([data.countryGeoId]);
			if (data.stateProvinceGeoId) stateProvinceGeoCBB.selectItem([data.stateProvinceGeoId]);
			if (data.districtGeoId) districtGeoCBB.selectItem([data.districtGeoId]);
			if (data.wardGeoId) wardGeoCBB.selectItem([data.wardGeoId]);
			if (data.address1) $("#wn_step1_address1").val(data.address1);
		};
		
		var initValidateForm = function(){
			var mapRules = [
		        {input: '#wn_step1_partyName', type: 'validInputNotNull'},
		        {input: '#wn_step1_phoneNumber', type: 'validInputNotNull'},
		        {input: '#wn_step1_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step1_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step1_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step1_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step1_address1', type: 'validInputNotNull'},
		    ];
	   		validatorVAL = new OlbValidator($('#alterpopupWindowStep1'), mapRules);
		};
		return {
			init: init,
			setValue: setValue
		};
	}());
</script>