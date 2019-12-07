<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BSCreateNewPartyClassificationGroupParties} [${parameters.partyClassificationGroupId?if_exists}]</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_partyId" class="required">${uiLabelMap.BSPartyId}</label>
						</div>
						<div class='span7'>
							<div id="wn_partyId">
								<div id="wn_partyGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSFromDate}</label>
						</div>
						<div class="span7">
							<div id="wn_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSThruDate}</label>
						</div>
						<div class="span7">
							<div id="wn_thruDate"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPagePCGPNew.init();
	});
	var OlbPagePCGPNew = (function(){
		var partyIdDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindow"), {width: 580, height: 240, cancelButton: $("#wn_alterCancel")});
			jOlbUtil.dateTimeInput.create($("#wn_fromDate"), {width: '99%', allowNullDate: true, value: null});
			jOlbUtil.dateTimeInput.create($("#wn_thruDate"), {width: '99%', allowNullDate: true, value: null});
		};
		var initElementComplex = function(){
			var configParty = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'partyId', type: 'string'}, 
					{name: 'partyCode', type: 'string'},
					{name: 'fullName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSPartyId)}', datafield: 'partyCode', width: '26%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'fullName'},
				],
				url: 'JQListPartyFullName',
				useUtilFunc: true,
				dropDownHorizontalAlignment: 'right',
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			partyIdDDB = new OlbDropDownButton($("#wn_partyId"), $("#wn_partyGrid"), null, configParty, []);
		};
		var initEvent = function(){
			$("#wn_alterSave").on('click', function(){
				if(!validatorVAL.validate()) return false;
				var fromDate = $('#wn_fromDate').jqxDateTimeInput('getDate');
				var thruDate = $('#wn_thruDate').jqxDateTimeInput('getDate');
				if (fromDate){
					fromDate = fromDate.getTime();
				}
				if (thruDate){
					thruDate = thruDate.getTime();
				}
		    	var dataMap = {
		    		"partyId": partyIdDDB.getValue(),
		    		"fromDate": fromDate,
		    		"thruDate": thruDate,
		    		"partyClassificationGroupId": '${parameters.partyClassificationGroupId?if_exists}',
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'createPartyClassificationAjax',
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
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
								}
						);
						
						resetWindowPopupCreate();
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
						$("#alterpopupWindow").jqxWindow('close');
						$("#jqxPartyClassificationGroupParties").jqxGrid('updatebounddata');
					},
				});
		    });
			
		};
		var resetWindowPopupCreate = function(){
	    	partyIdDDB.clearAll();
	    	$('#wn_fromDate').val(null);
	    	$('#wn_thruDate').val(null);
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: '#wn_partyId', type: 'validInputNotNull'},
			];
			var extendRules = [
				{input: '#wn_fromDate, #wn_thruDate', message: '${uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate}', action: 'valueChanged', 
					rule: function(input, commit){
						return OlbValidatorUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1 : "wn_fromDate", paramId2 : "wn_thruDate"});
					}
				},
			];
			validatorVAL = new OlbValidator($('#alterpopupWindow'), mapRules, extendRules, {position: 'bottom'});
		};
		var openCreateNew = function(){
			$("#alterpopupWindow").jqxWindow('open');
		};
		return {
			init: init,
			openCreateNew: openCreateNew,
		}
	}());
</script>