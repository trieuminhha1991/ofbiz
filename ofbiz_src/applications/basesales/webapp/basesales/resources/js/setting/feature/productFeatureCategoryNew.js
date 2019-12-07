$(function(){
	OlbSettingFeature.init();
});
var OlbSettingFeature = (function(){
	var validatorVAL;
	
	var init = (function(){
		initPopup();
		initInputs();
		initEventsAdd();
		initEventsClose();
		initValidate();
	});
	
	var initPopup = (function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindow1"), {width: 500, height: 175, cancelButton: $("#alterCancel1")});
	});
	
	var initInputs = (function(){
		jOlbUtil.input.create($("#wn_productFeatureCategoryId"), {width: '96%'});
		jOlbUtil.input.create($("#wn_description"), {width: '96%'});
	});
	
	var initValidate = function(){
		var extendRules = [];
		var mapRules = [
	            {input: '#wn_productFeatureCategoryId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_productFeatureCategoryId', type: 'validInputNotNull'},
				{input: '#wn_description', type: 'validInputNotNull'},
            ];
		validatorVAL = new OlbValidator($('#alterpopupWindow1'), mapRules, extendRules, {position: 'bottom'});
	};
	
	var initEventsAdd = function(){
		$('#alterSave1').click(function(){
			$('#alterpopupWindow1').jqxValidator('validate');
		});
		$('#alterpopupWindow1').on('validationSuccess',function(){
			create();
			$("#alterpopupWindow1").jqxWindow('close');
		});
	};
	
	var initEventsClose = function(){
		$('#alterpopupWindow1').on('close',function(){
			$('#alterpopupWindow1').jqxValidator('hide');
			$('#jqxgrid').jqxGrid('refresh');
			$('#wn_productFeatureCategoryId').val(null);
			$('#wn_description').val(null);
		});
	};
	
	var create = function(){
		var success = successK;
		var cMemberr = new Array();
			var map = {};
			map['productFeatureCategoryId'] = $('#wn_productFeatureCategoryId').val();
			map['description'] = $('#wn_description').val();
			cMemberr = map;
		if (cMemberr.length <= 0){
			return false;
		} else {
			cMemberr = JSON.stringify(cMemberr);
			jQuery.ajax({
		        url: 'createFeature',
		        type: 'POST',
		        async: true,
		        data: {
		        		'cMemberr': cMemberr,
	        		},
		        success: function(res) {
		        	var message = '';
					var template = '';
					if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
						if(res._ERROR_MESSAGE_LIST_){
							message += res._ERROR_MESSAGE_LIST_;
						}
						if(res._ERROR_MESSAGE_){
							message += res._ERROR_MESSAGE_;
						}
						template = 'error';
					}else{
						message = success;
						template = 'success';
						$("#jqxgrid").jqxGrid('updatebounddata');
		        		$("#jqxgrid").jqxGrid('clearselection');
					}
					updateGridMessage('jqxgrid', template ,message);
		        },
		        error: function(e){
		        	console.log(e);
		        }
		    });
		}
	}
	
	return {
		init: init,
	}
}());
