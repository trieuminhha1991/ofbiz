<div id="popupPartyRelContactEdit" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSEdit)}</div>
	<div style="overflow: hidden;">
		<form id="partyRelContactEditForm" class="form-horizontal">
			<input type="hidden" id="we_customerId" value=""/>
			<input type="hidden" id="we_partyContactId" value=""/>
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSRoleTypeId}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<div id="we_roleTypeId"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSLastName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="we_lastName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSMiddleName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="we_middleName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSFirstName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="we_firstName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSRepresentativeGender}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<div id="we_gender"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSPhoneNumber}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="we_phoneNumber" value=""/>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="we_alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="we_alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	var productStoreId = '${productStoreId?if_exists}';
</script>
<script type="text/javascript">
	$(function(){
		OlbCustomerMTRelContactEdit.init();
	});
	
	var OlbCustomerMTRelContactEdit = (function(){
		var genderDDL;
		var roleTypeIdEditCBB;
		var validatorVAL;
		
		var init = (function(){
			initElement();
			initElementComplex();
			initValidatorForm();
			initEvent();
		});
		
		var initElement = (function(){
			jOlbUtil.input.create("#we_firstName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#we_middleName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#we_lastName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#we_phoneNumber", {width: '96%', maxLength: 100});
			
			jOlbUtil.windowPopup.create($("#popupPartyRelContactEdit"), {width: 560, height: 360, cancelButton: $("#we_alterCancel2")});
		});
		
		var initElementComplex = (function(){
			var configGender = {
				width: '98%',
				height: 25,
				key: "genderId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
			};
			genderDDL = new OlbDropDownList($("#we_gender"), genderData, configGender, null);
			
			var configRoleType = {
				width: "98%",
				height: 30,
				key: "roleTypeId",
				value: "description",
				displayDetail: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				datafields: [
		            {name: 'roleTypeId'},
		            {name: 'description'}
		        ],
		        autoComplete: true,
				searchMode: 'containsignorecase',
			};
			roleTypeIdEditCBB = new OlbComboBox($("#we_roleTypeId"), roleTypeData, configRoleType, []);
		});
		
		var initEvent = function(){
			$('#we_alterSave2').click(function () {
			   	updateAction();
		       	$('#popupPartyRelContactEdit').jqxWindow('hide');
		       	$('#popupPartyRelContactEdit').jqxWindow('close');
		    });
		    
		    $('#popupPartyRelContactEdit').on('close',function(){
				$('#popupPartyRelContactEdit').jqxValidator('hide');
				$('#we_customerId').val("");
				$('#we_partyContactId').val("");
				genderDDL.clearAll();
				roleTypeIdEditCBB.clearAll();
				$('#wn_firstName').val("");
				$('#wn_middleName').val("");
				$('#wn_lastName').val("");
				$('#wn_phoneNumber').val("");
			});
			
			var updateAction = function(){
				if (!validatorVAL.validate()) return false;
				
				jQuery.ajax({
			        url: 'updateCustomerRelContact',
			        type: 'POST',
			        async: true,
			        data: {
			        	"customerId": $('#we_customerId').val(),
			        	"partyContactId": $('#we_partyContactId').val(),
						"roleTypeId": roleTypeIdEditCBB.getValue(),
						"firstName": $('#we_firstName').val(),
						"middleName": $('#we_middleName').val(),
						"lastName": $('#we_lastName').val(),
						"gender": genderDDL.getValue(),
						"phoneNumber": $('#we_phoneNumber').val()
			        },
			        success: function(data) {
			        	jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.BSEditSuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	if ($("#jqxPartyRelContact").length > 0) {
					        		$("#jqxPartyRelContact").jqxGrid('updatebounddata');
		        					$("#jqxPartyRelContact").jqxGrid('clearselection');
					        	} else {
					        		location.reload();
					        	}
							}
						);
			        },
			        error: function(e){
			        	alert("Send request is error");
			        }
			    });
			}
		};
		
		var setValue = function(data) {
			if (data) {
				if (data.partyIdFrom != null) $("#we_customerId").val(data.partyIdFrom);
				else $("#we_customerId").val("");
				
				if (data.partyId != null) $("#we_partyContactId").val(data.partyId);
				else $("#we_partyContactId").val("");
				
				if (data.gender != null) genderDDL.selectItem([data.gender]);
				else genderDDL.clearAll();
				
				if (data.roleTypeIdTo != null) roleTypeIdEditCBB.selectItem([data.roleTypeIdTo]);
				else roleTypeIdEditCBB.clearAll();
				
				if (data.firstName != null) $("#we_firstName").val(data.firstName);
				else $("#we_firstName").val("");
				
				if (data.middleName != null) $("#we_middleName").val(data.middleName);
				else $("#we_middleName").val("");
				
				if (data.lastName != null) $("#we_lastName").val(data.lastName);
				else $("#we_lastName").val("");
				
				if (data.phoneNumber != null) $("#we_phoneNumber").val(data.phoneNumber);
				else $("#we_phoneNumber").val("");
			}
		};
		var openWindow = function(data) {
			if (data) setValue(data);
			$("#popupPartyRelContactEdit").jqxWindow("open");
		};
		
		var initValidatorForm = function(){
			var mapRules = [
	            {input: '#we_gender', type: 'validObjectNotNull', objType: 'dropDownList'},
	            {input: '#we_roleTypeId', type: 'validObjectNotNull', objType: 'comboBox'},
	            {input: '#we_phoneNumber', type: 'validPhoneNumber'},
	            {input: '#we_firstName', type: 'validInputNotNull'},
	            {input: '#we_middleName', type: 'validInputNotNull'},
	            {input: '#we_lastName', type: 'validInputNotNull'},
	        ];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#partyRelContactEditForm'), mapRules, extendRules);
		};
		
		return{
			init: init,
			setValue: setValue,
			openWindow: openWindow
		}
	}());
</script>
