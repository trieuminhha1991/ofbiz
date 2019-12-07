<div id="alterpopupWindowStep3" style="display:none">
	<div>${uiLabelMap.BSSetupHrManager}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="wn_step3_partyId"/>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSEmployeeId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step3_partyCode"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSEmployeeName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step3_partyName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class='span7'>
							<input type="tel" id="wn_step3_phoneNumber"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.EmailAddress}</label>
						</div>
						<div class='span7'>
							<input type="email" id="wn_step3_emailAddress"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSBirthDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_birthDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.PartyGender}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_gender"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSIsManagerOf}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_organizationId"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_step3_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAddress1}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step3_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_step3_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_step3_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_step3_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	<#assign genderList = delegator.findList("Gender", null , null, orderBy,null, false)!>
	var genderData = [
	<#if genderList?exists>
		<#list genderList as item>
		{"genderId": "${item.genderId}", "description": "${item.description?if_exists}"}, 
		</#list>
	</#if>
	];
	
	var OlbWizardStep3 = (function(){
		var loadOlbWizardStep3 = false;
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var validatorVAL;
		var organizationDDL;
		var genderDDL;
		
		var init = function(){
			initWindow();
		};
		var initContent = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowStep3"), {maxWidth: 960, width: 960, height: 460, cancelButton: $("#wn_step3_alterCancel")});
			
			$("#step3").on("click", function(){
				openWindow();
			});
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_step3_partyCode"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step3_partyName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step3_phoneNumber"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step3_emailAddress"), {width: '98%'});
			jOlbUtil.dateTimeInput.create($("#wn_step3_birthDate"), {width: '100%', allowNullDate: true, formatString: OlbCore.formatDate});

			jOlbUtil.input.create("#wn_step3_address1", {maxLength: 255});
			
			setTimeout(function(){
				$("#wn_step3_birthDate").val(null);
			}, 300);
		};
		var initElementComplex = function(){
			var configGender = {
				width: '100%',
				height: 25,
				key: "genderId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
			};
			genderDDL = new OlbDropDownList($("#wn_step3_gender"), genderData, configGender, null);
			
			var configOrganization = {
				width: '100%',
				height: 25,
				key: "partyId",
	    		value: "fullName",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				multiSelect: true,
				checkboxes: true,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: 'wizardGetListPartyOrg',
				<#--
				renderer: function (index, label, value) {
					var datasource = $("#wn_step3_organizationId").jqxDropDownList("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.abbreviation + "]";
							}
						}
					}
					return label;
		        },
				-->
			};
			organizationDDL = new OlbDropDownList($("#wn_step3_organizationId"), null, configOrganization, null);
			
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
			countryGeoCBB = new OlbComboBox($("#wn_step3_countryGeoId"), null, configCountry, ["${countryGeoId?if_exists}"]);
		    
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
		    stateProvinceGeoCBB = initDropDownGeoState($("#wn_step3_stateProvinceGeoId"), urlDropDownStateProvince, [stateProvinceGeoId]);
		    districtGeoCBB = initDropDownGeoState($("#wn_step3_countyGeoId"), '&pagesize=0&geoId=' + stateProvinceGeoId);
		    wardGeoCBB = initDropDownGeoState($("#wn_step3_wardGeoId"), '&pagesize=0');
		};
		var initEvent = function(){
			$('#wn_step3_countryGeoId').on('change', function (event){
				getAssociatedState($('#wn_step3_stateProvinceGeoId'), event);
			});
			$('#wn_step3_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step3_countyGeoId'), event);
			});
			$('#wn_step3_countyGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step3_wardGeoId'), event);
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
			
			$('#wn_step3_alterSaveAndContinue').on("click", function(){
				createFinishStep3(true);
			});
			
			$('#wn_step3_alterSave').on('click', function(){
				createFinishStep3();
			});
		};
		var createFinishStep3 = function(isContinue){
			if(!validatorVAL.validate()) return false;
			
        	var dataMap = {
				partyId: $('#wn_step3_partyId').val(),
				partyCode: $('#wn_step3_partyCode').val(),
				partyName: $('#wn_step3_partyName').val(),
				phoneNumber: $('#wn_step3_phoneNumber').val(),
				emailAddress: $('#wn_step3_emailAddress').val(),
				gender: genderDDL.getValue(),
				organizationIds: organizationDDL.getValue(),
				
				countryGeoId: countryGeoCBB.getValue(),
				stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
				districtGeoId: districtGeoCBB.getValue(),
				wardGeoId: wardGeoCBB.getValue(),
				address1: $('#wn_step3_address1').val(),
			};
			var birthDateValue = $('#wn_step3_birthDate').jqxDateTimeInput('getDate');
			if (OlbCore.isNotEmpty(birthDateValue)) {
				dataMap.birthDate = birthDateValue.getTime();
			};
			$.ajax({
                type: "POST",
                url: "wizardSetupHrManager",
                data: dataMap,
                beforeSend: function(){
                    $("#loader_page_common").show();
                    var step3 = $("#step3");
		        	step3.removeClass("alert-error");
		        	step3.removeClass("alert-success");
	        		var itemI = step3.find("i");
	        		if (itemI) itemI.remove();
                }, 
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	var step3 = $("#step3");
			        	step3.addClass("alert-error");
		        		step3.prepend($('<i class="icon-remove"></i>'));
		        		
			        	return false;
					}, function(data){
                    	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data.partyId) {
			        		$("#wn_step3_partyId").val(data.partyId);
			        		
			        		var step3 = $("#step3");
			        		step3.addClass("alert-success");
			        		step3.prepend($('<i class="icon-ok green"></i>'));
			        		
			        		$('#alterpopupWindowStep3').jqxWindow("close");
			        		
			        		if (isContinue) {
			        			OlbWizardStep4.openWindow();
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
			$('#wn_step3_partyCode').val("");
			$('#wn_step3_partyName').val("");
			$('#wn_step3_phoneNumber').val("");
			$('#wn_step3_emailAddress').val("");
			$('#wn_step3_birthDate').val("");
			genderDDL.clearAll();
			organizationDDL.clearAll();
			currencyUomCCB.clearAll();
			countryGeoCBB.selectItem(null, 0);
			stateProvinceGeoCBB.selectItem(null, 0);
			districtGeoCBB.selectItem(null, 0);
			wardGeoCBB.selectItem(null, 0);
			$("#wn_step3_address1").jqxInput("val", null);
		};
		var setValue = function(data){
			if (data.partyId) $('#wn_step3_partyId').val(data.partyId);
			if (data.partyCode) $('#wn_step3_partyCode').val(data.partyCode);
			if (data.partyName) $('#wn_step3_partyName').val(data.partyName);
			if (data.phoneNumber) $('#wn_step3_phoneNumber').val(data.phoneNumber);
			if (data.emailAddress) $('#wn_step3_emailAddress').val(data.emailAddress);
			if (data.birthDate) $('#wn_step3_birthDate').val(data.birthDate);
			if (data.gender) genderDDL.selectItem([data.gender]);
			if (data.countryGeoId) countryGeoCBB.selectItem([data.countryGeoId]);
			if (data.stateProvinceGeoId) stateProvinceGeoCBB.selectItem([data.stateProvinceGeoId]);
			if (data.districtGeoId) districtGeoCBB.selectItem([data.districtGeoId]);
			if (data.wardGeoId) wardGeoCBB.selectItem([data.wardGeoId]);
			if (data.address1) $("#wn_step3_address1").val(data.address1);
			if (data.organizationIds) organizationDDL.selectItem(data.organizationIds);
		};
		
		var initValidateForm = function(){
			var mapRules = [
		        {input: '#wn_step3_partyCode', type: 'validInputNotNull'},
		        {input: '#wn_step3_partyCode', type: 'validCannotSpecialCharactor'},
		        {input: '#wn_step3_partyName', type: 'validInputNotNull'},
		        {input: '#wn_step3_phoneNumber', type: 'validInputNotNull'},
		        {input: '#wn_step3_organizationId', type: 'validObjectNotNull', objType: 'dropDownList'},
		        {input: '#wn_step3_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step3_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step3_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step3_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step3_address1', type: 'validInputNotNull'},
		    ];
	   		validatorVAL = new OlbValidator($('#alterpopupWindowStep3'), mapRules);
		};
		var openWindow = function(){
			$("#alterpopupWindowStep3").jqxWindow("open");
			if (!loadOlbWizardStep3) {
				initContent();
				loadOlbWizardStep3 = true;
			}
		};
		var refreshWindow = function(){
			organizationDDL.updateBoundData();
		}
		return {
			init: init,
			setValue: setValue,
			openWindow: openWindow,
			refreshWindow: refreshWindow,
		};
	}());
</script>