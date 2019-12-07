<div id="alterpopupWindowEdit" style="display:none">
	<div>${uiLabelMap.CommonAdd}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSFeatureType}</label>
						</div>
						<div class='span7'>
							<input id="featureTypeIdEdit"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSParentFeatureTypeId}</label>
						</div>
						<div class='span7'>
							<div id="parentTypeIdEdit"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="descriptionEdit"/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave2" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel2" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPageFeatureTypeEdit.init();
	});
	
	var OlbPageFeatureTypeEdit = (function(){
		var partyTypeIdDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowEdit"), {width: 500, height: 230, cancelButton: $("#alterCancel2")});
			
			jOlbUtil.input.create($("#featureTypeIdEdit"), {width: '96%', minLength: 1, disabled: true});
			jOlbUtil.input.create($("#descriptionEdit"), {width: '96%', minLength: 1, disabled: false});
		};
		var initElementComplex = function(){
			var configPartyType = {
				width: '98%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}",
				useUrl: false,
				url: '',
				key: 'productFeatureTypeId',
				value: 'description',
				autoDropDownHeight: false,
				dropDownHeight: 120,
			}
			partyTypeIdDDL = new OlbDropDownList($("#parentTypeIdEdit"), featureTypeList, configPartyType, []);
		};
		var initEvent = function(){
		    $('#alterpopupWindowEdit').on('close', function (event) {
				$("#jqxgridFeatureType").jqxTreeGrid({ disabled: false });
			});
			
			$('#alterSave2').click(function(){
				if (!validatorVAL.validate()) return false;
				
				editFeatureTypee();
				$("#alterpopupWindowEdit").jqxWindow('close');
			});
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: '#descriptionEdit', type: 'validInputNotNull'},
			];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#alterpopupWindowEdit'), mapRules, extendRules);
		};
		
		function editFeatureTypee(){
			var success = "${StringUtil.wrapString(uiLabelMap.BSSuccessK)}";
			var aFeatureType = {
				'productFeatureTypeId': $('#featureTypeIdEdit').val(),
				'parentTypeId': partyTypeIdDDL.getValue(),
				'description': $('#descriptionEdit').val()
			};
			if (aFeatureType.length <= 0){
				return false;
			} else {
				aFeatureType = JSON.stringify(aFeatureType);
				jQuery.ajax({
			        url: 'editFeatureType2',
			        type: 'POST',
			        async: true,
			        data: {'aFeatureType': aFeatureType},
			        success: function(data) {
			        	jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#jqxgridFeatureType").jqxTreeGrid('updateBoundData');
			        				$("#jqxgridFeatureType").jqxTreeGrid('clearSelection');
								}
						);
			        },
			        error: function(e){
			        	alert("Send request is error");
			        }
			    });
			}
		}
		
		return {
			init: init,
		};
	}());
</script>