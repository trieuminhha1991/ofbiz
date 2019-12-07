$(function(){
	OlbSettingFeature.init();
});
var OlbSettingFeature = (function(){
	var init = (function(){
		initPopup();
		initInputs();
		initEventsAdd();
		initEventsClose();
		initValidate();
	});
	
	var initPopup = (function(){
		$('#alterpopupWindow1').jqxWindow({ width: 500, height : 180,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, title: addNew});
		$('#alterpopupWindow1').jqxWindow('resizable', false);
	});
	
	var initInputs = (function(){
		jOlbUtil.input.create("#productFeatureCategoryIdAdd");
		jOlbUtil.input.create("#descriptionAdd");
	});
	
	var initValidate = function(){
//		var mapRules = [
//		                {input: '#productFeatureCategoryIdAdd', type: 'validCannotSpecialCharactor'},
//		                {input: '#productFeatureCategoryIdAdd', type: 'validInputNotNull'},
//				        {input: '#descriptionAdd', type: 'validInputNotNull'},
//	    ];
//		new OlbValidator($('#FeatureForm'), mapRules, [], {});
		$('#FeatureForm').jqxValidator({
			rules : [
				{input: '#productFeatureCategoryIdAdd', message: notEmpty, action: 'blur', 
					rule: function (input, commit) {
						var value = $(input).val();
						value = value.replace(/[^\w]/gi, '');
						var res = '';
						for(var x in value){
							res += value[x].toUpperCase();
						}
						var result = $('#productFeatureCategoryIdAdd').val(res);
						if(/^\s*$/.test(result)){
							return false;
						}
						return true;
					}
				},
				{input: '#descriptionAdd', message: notEmpty, action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
			]
		});
		
	};
	
	var initEventsAdd = function(){
		$('#alterSave1').click(function(){
			$('#FeatureForm').jqxValidator('validate');
		});
		$('#FeatureForm').on('validationSuccess',function(){
//			var row = {};
//			row = {
//				productFeatureCategoryId : $('#productFeatureCategoryIdAdd').val(),
//				description : $('#descriptionAdd').val(),
//			};
//			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
//			$("#jqxgrid").jqxGrid('clearSelection');                        
//			$("#jqxgrid").jqxGrid('selectRow', 0);
			create();
			$("#alterpopupWindow1").jqxWindow('close');
		});
	};
	
	var initEventsClose = function(){
		$('#alterpopupWindow1').on('close',function(){
			$('#FeatureForm').jqxValidator('hide');
			$('#jqxgrid').jqxGrid('refresh');
			$('#productFeatureCategoryIdAdd').val(null);
			$('#descriptionAdd').val(null);
		});
	};
	
	var create = function(){
		var success = successK;
		var cMemberr = new Array();
			var map = {};
			map['productFeatureCategoryId'] = $('#productFeatureCategoryIdAdd').val();
			map['description'] = $('#descriptionAdd').val();
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
