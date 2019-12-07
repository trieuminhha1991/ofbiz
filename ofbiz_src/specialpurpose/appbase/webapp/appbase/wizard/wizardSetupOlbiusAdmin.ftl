<style type="text/css">
	#wn_step4_container_new.disabled {
		pointer-events: none;
	}
	#wn_step4_container_new.disabled label {
		color: #848484;
	}
	#wn_step4_container_new.disabled input {
	    color: #848484;
	    background-color: #eee;
	}
</style>
<div id="alterpopupWindowStep4" style="display:none" tabindex="-1">
	<div>${uiLabelMap.BSSetupOlbiusAdmin}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="wn_step4_partyId"/>
			<div class="row-fluid">
				<div class="span12"><h5 class="margin-top0 blue">${uiLabelMap.BSUseAccountExist}</h5></div>
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid margin-bottom0'>
						<div class='span12'>
							<div style="margin-left: 30px">
								<div id="wn_step4_useHrManager">${uiLabelMap.BSUseHrManager}</div>
							</div>
				   		</div>
					</div>
					<#--
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSHRManager}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_hrManager"></div>
				   		</div>
					</div>
					-->
				</div>
			</div>
			<hr class="small-margin"/>
			<div class="row-fluid">
				<div class="span12"><h5 class="margin-top0 blue">${uiLabelMap.BSCreateOtherUser}</h5></div>
			</div>
			<div class="row-fluid" id="wn_step4_container_new">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSEmployeeId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step4_partyCode"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSEmployeeName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step4_partyName"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class='span7'>
							<input type="tel" id="wn_step4_phoneNumber"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.EmailAddress}</label>
						</div>
						<div class='span7'>
							<input type="email" id="wn_step4_emailAddress"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSBirthDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_birthDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.PartyGender}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_gender"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_step4_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAddress1}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_step4_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_step4_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_step4_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var OlbWizardStep4 = (function(){
		var loadOlbWizardStep4 = false;
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var validatorVAL;
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
			jOlbUtil.windowPopup.create($("#alterpopupWindowStep4"), {maxWidth: 960, width: 960, height: 460, cancelButton: $("#wn_step4_alterCancel")});
			
			$("#step4").on("click", function(){
				openWindow();
			});
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_step4_partyCode"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step4_partyName"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step4_phoneNumber"), {width: '98%'});
			jOlbUtil.input.create($("#wn_step4_emailAddress"), {width: '98%'});
			jOlbUtil.dateTimeInput.create($("#wn_step4_birthDate"), {width: '100%', allowNullDate: true, formatString: OlbCore.formatDate});

			jOlbUtil.input.create("#wn_step4_address1", {maxLength: 255});
			
			jOlbUtil.checkBox.create("#wn_step4_useHrManager");
			
			setTimeout(function(){
				$("#wn_step4_birthDate").val(null);
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
			genderDDL = new OlbDropDownList($("#wn_step4_gender"), genderData, configGender, null);
			
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
			countryGeoCBB = new OlbComboBox($("#wn_step4_countryGeoId"), null, configCountry, ["${countryGeoId?if_exists}"]);
		    
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
		    stateProvinceGeoCBB = initDropDownGeoState($("#wn_step4_stateProvinceGeoId"), urlDropDownStateProvince, [stateProvinceGeoId]);
		    districtGeoCBB = initDropDownGeoState($("#wn_step4_countyGeoId"), '&pagesize=0&geoId=' + stateProvinceGeoId);
		    wardGeoCBB = initDropDownGeoState($("#wn_step4_wardGeoId"), '&pagesize=0');
		};
		var initEvent = function(){
			$('#wn_step4_countryGeoId').on('change', function (event){
				getAssociatedState($('#wn_step4_stateProvinceGeoId'), event);
			});
			$('#wn_step4_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step4_countyGeoId'), event);
			});
			$('#wn_step4_countyGeoId').on('change', function (event){
			    getAssociatedState($('#wn_step4_wardGeoId'), event);
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
			
			$('#wn_step4_useHrManager').on('change', function(event){
				var checked = event.args.checked;
				if (checked) {
					$("#wn_step4_container_new").addClass("disabled");
					$('#alterpopupWindowStep4').focus();
				} else {
					$("#wn_step4_container_new").removeClass("disabled");
				}
			});
			
			$('#wn_step4_alterSave').on('click', function(){
				createFinishStep4();
			});
		};
		var createFinishStep4 = function(){
			var dataMap = {};
			
			var useHrManager = $('#wn_step4_useHrManager').val();
			if (!useHrManager) {
				if(!validatorVAL.validate()) return false;
				
				dataMap = {
					partyId: $('#wn_step4_partyId').val(),
					partyCode: $('#wn_step4_partyCode').val(),
					partyName: $('#wn_step4_partyName').val(),
					phoneNumber: $('#wn_step4_phoneNumber').val(),
					emailAddress: $('#wn_step4_emailAddress').val(),
					gender: genderDDL.getValue(),
					
					countryGeoId: countryGeoCBB.getValue(),
					stateProvinceGeoId: stateProvinceGeoCBB.getValue(),
					districtGeoId: districtGeoCBB.getValue(),
					wardGeoId: wardGeoCBB.getValue(),
					address1: $('#wn_step4_address1').val(),
				};
				
				var birthDateValue = $('#wn_step4_birthDate').jqxDateTimeInput('getDate');
				if (OlbCore.isNotEmpty(birthDateValue)) {
					dataMap.birthDate = birthDateValue.getTime();
				};
			}
			dataMap.useHrManager = useHrManager;
        	
			$.ajax({
                type: "POST",
                url: "wizardSetupOlbiusAdmin",
                data: dataMap,
                beforeSend: function(){
                    $("#loader_page_common").show();
                    var step4 = $("#step4");
		        	step4.removeClass("alert-error");
		        	step4.removeClass("alert-success");
	        		var itemI = step4.find("i");
	        		if (itemI) itemI.remove();
                }, 
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	var step4 = $("#step4");
			        	step4.addClass("alert-error");
		        		step4.prepend($('<i class="icon-remove"></i>'));
		        		
			        	return false;
					}, function(data){
                    	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data.partyId) {
			        		$("#wn_step4_partyId").val(data.partyId);
			        		
			        		var step4 = $("#step4");
			        		step4.addClass("alert-success");
			        		step4.prepend($('<i class="icon-ok green"></i>'));
			        		
			        		$('#alterpopupWindowStep4').jqxWindow("close");
			        		
			        		OlbWizardSystem.checkSetupComplete();
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
			$('#wn_step4_partyCode').val("");
			$('#wn_step4_partyName').val("");
			$('#wn_step4_phoneNumber').val("");
			$('#wn_step4_emailAddress').val("");
			$('#wn_step4_birthDate').val("");
			genderDDL.clearAll();
			currencyUomCCB.clearAll();
			countryGeoCBB.selectItem(null, 0);
			stateProvinceGeoCBB.selectItem(null, 0);
			districtGeoCBB.selectItem(null, 0);
			wardGeoCBB.selectItem(null, 0);
			$("#wn_step4_address1").jqxInput("val", null);
		};
		var setValue = function(data){
			if (data.partyId) $('#wn_step4_partyCode').val(data.partyId);
			if (data.partyName) $('#wn_step4_partyName').val(data.partyName);
			if (data.phoneNumber) $('#wn_step4_phoneNumber').val(data.phoneNumber);
			if (data.emailAddress) $('#wn_step4_emailAddress').val(data.emailAddress);
			if (data.birthDate) $('#wn_step4_birthDate').val(data.birthDate);
			if (data.gender) genderDDL.selectItem([data.gender]);
			if (data.countryGeoId) countryGeoCBB.selectItem([data.countryGeoId]);
			if (data.stateProvinceGeoId) stateProvinceGeoCBB.selectItem([data.stateProvinceGeoId]);
			if (data.districtGeoId) districtGeoCBB.selectItem([data.districtGeoId]);
			if (data.wardGeoId) wardGeoCBB.selectItem([data.wardGeoId]);
			if (data.address1) $("#wn_step4_address1").val(data.address1);
		};
		
		var initValidateForm = function(){
			var mapRules = [
		        {input: '#wn_step4_partyName', type: 'validInputNotNull'},
		        {input: '#wn_step4_phoneNumber', type: 'validInputNotNull'},
		        {input: '#wn_step4_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step4_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step4_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step4_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_step4_address1', type: 'validInputNotNull'},
		    ];
	   		validatorVAL = new OlbValidator($('#alterpopupWindowStep4'), mapRules);
		};
		var openWindow = function(){
			$("#alterpopupWindowStep4").jqxWindow("open");
			if (!loadOlbWizardStep4) {
				initContent();
				loadOlbWizardStep4 = true;
			}
		};
		return {
			init: init,
			setValue: setValue,
			openWindow: openWindow
		};
	}());
</script>