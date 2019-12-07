$(function(){
	pageViewFileScan.init();
});


var pageViewFileScan = (function(){
	var init = function(){
		initElement();
		initEvent();
		initAttachFile();
	};
	
	var initElement = function() {
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false, theme:'olbius',cancelButton: $("#uploadCancelButton")});
	};
	
	var initEvent = function(){
		$('#uploadOkButton').click(function(){
			saveFileUpload();
		});
		
		$('#uploadImages').on('click', function(){
			$('#jqxFileScanUpload').jqxWindow('open');
		});
		
		$('#alterpopupWindow').on('close', function (event) { 
			alterpopupWindowClose();
		}); 
		
		$('#alterSave').click(function(){
			createCostLogistics();
		});
	};
	
	var initAttachFile = function (){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose: 'click here',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50){
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                }]
			            );
			            return false;
					} else {
						if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
							listImage.push(files[int]);
						}
					}
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	};
	
	var txtImage = null;
	var form_data = new FormData();
	var checkSaveUpload = false;
	var saveFileUpload = function(){
		checkSaveUpload = true;
		Loading.show('loadingMacro');
    	setTimeout(function(){
			var folder = "/baseLogistics/logisticsCost";
			for ( var d in listImage) {
				var file = listImage[d];
				if (file){
					var dataResourceName = file.name;
					txtImage = dataResourceName;
					if (dataResourceName && dataResourceName.length > 50){
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                }]
			            );
			            return false;
					}
					form_data.append("uploadedFile", file);
					form_data.append("folder", folder);
				}
			}
			document.getElementById("txtImage").innerHTML = txtImage;
			Loading.hide('loadingMacro');
			$('#jqxFileScanUpload').jqxWindow('close');
    	}, 500);
	};
	
	var createCostLogistics = function(){
		if ($('#alterpopupWindow').jqxValidator('validate')){
			form_data.append("costAccBaseId", $("#costAccBaseId").val());
			form_data.append("partyId", Grid.getDropDownValue($("#partyId")).trim());
			form_data.append("costAccDate", $('#costAccDate').jqxDateTimeInput('getDate').getTime());
			form_data.append("description", $("#description").val());
			form_data.append("costPriceActual", $("#costPriceActual").val());
			form_data.append("currencyUomId", $("#currencyUomId").val());
			bootbox.dialog(uiLabelMap.AreYouSureCreate,
				[{"label": uiLabelMap.CommonCancel,
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        },
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	createLogCostAccouting();
		            }
		        }
		   ]);
		}
	};
	
	function createLogCostAccouting(){
		$.ajax({
			type: "POST",
			url: "createCostAccDepartmentCustom",
			cache : false,
			contentType : false,
			processData : false,
			data: form_data,
			success: function (res){
				$("#jqxgridCost").jqxGrid("updatebounddata");
				displayEditSuccessMessage('jqxgridCost');
			}
		});
    	$('#alterpopupWindow').jqxWindow('close');
	}
	
	var alterpopupWindowClose = function(){
		form_data = null;
		form_data = new FormData();
		var dateCurrent = new Date();
		$("#costAccBaseId").jqxDropDownList('clearSelection');
		$('#costPriceActual').val(0);
		$('#costAccDate').jqxDateTimeInput('setDate', dateCurrent);
		document.getElementById("txtImage").innerHTML = "";
		$("#description").val("");
		$('#partyId').jqxDropDownButton('setContent', '');
		checkSaveUpload = false;
	};
	
	return {
		init: init
	};
}());