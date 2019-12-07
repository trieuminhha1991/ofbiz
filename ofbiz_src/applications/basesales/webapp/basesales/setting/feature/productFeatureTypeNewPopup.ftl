<div id="alterpopupWindow1" style="display:none">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewFeatureType)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSFeatureType}</label>
						</div>
						<div class='span7'>
							<input id="featureTypeIdAdd"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSParentFeatureTypeId}</label>
						</div>
						<div class='span7'>
							<div id="parentTypeIdAdd"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="descriptionAdd"/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave1" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel1" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPageFeatureTypeNew.init();
	});
	
	var OlbPageFeatureTypeNew = (function(){
		var partyTypeIdDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidatorForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#featureTypeIdAdd"), {placeHolder: "${StringUtil.wrapString(uiLabelMap.BSFeatureType)}", width: '96%', minLength: 1});
			jOlbUtil.input.create($("#descriptionAdd"), {placeHolder: "${StringUtil.wrapString(uiLabelMap.BSDescription)}", width: '96%', minLength: 1});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindow1"), {width: 500, height: 230, cancelButton: $("#alterCancel1")});
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
			partyTypeIdDDL = new OlbDropDownList($("#parentTypeIdAdd"), featureTypeList, configPartyType, []);
		};
		var initValidatorForm = function(){
			var mapRules = [
				{input: '#featureTypeIdAdd', type: 'validInputNotNull'},
				{input: '#featureTypeIdAdd', type: 'validCannotSpecialCharactor'},
				{input: '#descriptionAdd', type: 'validInputNotNull'},
			];
			var extendRules = [];
			validatorVAL = new OlbValidator($('#alterpopupWindow1'), mapRules, extendRules);
		};
		var initEvent = function(){
			$('#alterSave1').click(function(){
				if (!validatorVAL.validate()) return false;
				
				createFeatureTypee();
				$("#alterpopupWindow1").jqxWindow('close');
			});
			
			$('#alterpopupWindow1').on('close',function(){
				$('#featureTypeIdAdd').val(null);
				$('#descriptionAdd').val(null);
				partyTypeIdDDL.clearAll();
			});
		};
		
		function createFeatureTypee(){
			var aFeatureType = {
				'productFeatureTypeId': $('#featureTypeIdAdd').val(),
				'parentTypeId': partyTypeIdDDL.getValue(),
				'description': $('#descriptionAdd').val()
			};
			if (aFeatureType.length <= 0){
				return false;
			} else {
				aFeatureType = JSON.stringify(aFeatureType);
				jQuery.ajax({
			        url: 'createFeatureType2',
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
								}
						);
			        },
			        error: function(e){
			        	alert("Send request is error");
			        }
			    });
			}
		}
		
		var openWindowNew = function(){
			$('#alterpopupWindow1').jqxWindow('open');
		};
		
		return {
			init: init,
			openWindowNew: openWindowNew
		};
	}());
</script>