<div id="popupPartyRelContactNew" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNew)}</div>
	<div style="overflow: hidden;">
		<form id="partyRelContactNewForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSRoleTypeId}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<div id="wn_roleTypeId"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSLastName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="wn_lastName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSMiddleName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="wn_middleName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSFirstName}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="wn_firstName" value=""/>
						</div>
					</div>
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSRepresentativeGender}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<div id="wn_gender"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span4 line-height-25 asterisk align-right">${uiLabelMap.BSPhoneNumber}</label>
						<div class="span8" style="margin-bottom: 10px;">
							<input id="wn_phoneNumber" value=""/>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'>
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
		OlbCustomerMTRelContactNew.init();
	});
	
	var OlbCustomerMTRelContactNew = (function(){
		var genderDDL;
		var roleTypeIdAddCBB;
		var validatorVAL;
		
		var init = (function(){
			initElement();
			initElementComplex();
			initValidatorForm();
			initEvent();
		});
		
		var initElement = (function(){
			jOlbUtil.input.create("#wn_firstName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#wn_middleName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#wn_lastName", {width: '96%', maxLength: 100});
			jOlbUtil.input.create("#wn_phoneNumber", {width: '96%', maxLength: 100});
			
			jOlbUtil.windowPopup.create($("#popupPartyRelContactNew"), {width: 560, height: 360, cancelButton: $("#alterCancel1")});
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
			genderDDL = new OlbDropDownList($("#wn_gender"), genderData, configGender, null);
			
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
			roleTypeIdAddCBB = new OlbComboBox($("#wn_roleTypeId"), roleTypeData, configRoleType, []);
		});
		
		var initEvent = (function(){
			$('#alterSave1').click(function(){
				if (!validatorVAL.validate()) return false;
				
				var row = {};
				row = {
					"customerId": "${modernTradeInfo.partyId?if_exists}",
					"roleTypeId": roleTypeIdAddCBB.getValue(),
					"firstName": $('#wn_firstName').val(),
					"middleName": $('#wn_middleName').val(),
					"lastName": $('#wn_lastName').val(),
					"gender": genderDDL.getValue(),
					"phoneNumber": $('#wn_phoneNumber').val()
				};
				$("#jqxPartyRelContact").jqxGrid('addRow', null, row, "first");
				$("#jqxPartyRelContact").jqxGrid('clearSelection');                        
				$("#jqxPartyRelContact").jqxGrid('selectRow', 0);  
				$("#popupPartyRelContactNew").jqxWindow('close');
			});
			
			$("#popupPartyRelContactNew").on('open', function(){
				
			});
			$('#popupPartyRelContactNew').on('close',function(){
				$('#partyRelContactNewForm').jqxValidator('hide');
				$('#jqxPartyRelContact').jqxGrid('refresh');
				genderDDL.clearAll();
				roleTypeIdAddCBB.clearAll();
				$('#wn_firstName').val("");
				$('#wn_middleName').val("");
				$('#wn_lastName').val("");
				$('#wn_phoneNumber').val("");
			});
		});
		
		var initValidatorForm = function(){
			var mapRules = [
	            {input: '#wn_gender', type: 'validObjectNotNull', objType: 'dropDownList'},
	            {input: '#wn_roleTypeId', type: 'validObjectNotNull', objType: 'comboBox'},
	            {input: '#wn_phoneNumber', type: 'validPhoneNumber'},
	            {input: '#wn_firstName', type: 'validInputNotNull'},
	            {input: '#wn_middleName', type: 'validInputNotNull'},
	            {input: '#wn_lastName', type: 'validInputNotNull'},
	        ];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#partyRelContactNewForm'), mapRules, extendRules);
		};
		
		return{
			init: init,
		}
	}());
</script>
