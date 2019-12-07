$(function(){
	OlbSettingUpdateProductStoreCatalog.init();
});

var OlbSettingUpdateProductStoreCatalog = (function(){
	var eventMenu = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action"); 
            if (action == 'update') {
            	var wtmp = window;
        	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
        	   	var tmpwidth = $('#alterpopupWindowEdit').jqxWindow('width');
        	   	$('#alterpopupWindowEdit').jqxWindow('open');
    		   	if (rowindex >= 0) {
    		   		openPSCatalogEdit();
    		   	}
            }
		});
		
		function openPSCatalogEdit(){
			$("#thruDateEdit").jqxDateTimeInput({disabled: false});
			$("#sequenceNumEdit").jqxNumberInput({disabled: false});
			$("#fromDateEdit").jqxDateTimeInput("setDate", null);
			$("#thruDateEdit").jqxDateTimeInput("setDate", null);
			$("#alterSave2").jqxButton({disabled: false});
			$("#alterCancel2").jqxButton({disabled: false});
			
			var indexSeleted = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid("getrowdata", indexSeleted);
			if (data != null) {
				if (data.prodCatalogId != null) $("#prodCatalogIdEdit").val(data.prodCatalogId);
				if (data.fromDate != null) $("#fromDateEdit").jqxDateTimeInput("setDate", data.fromDate);
				if (data.sequenceNum != null) $("#sequenceNumEdit").jqxNumberInput('setDecimal', data.sequenceNum);
				if (data.thruDate != null) {
					$("#thruDateEdit").jqxDateTimeInput("setDate", data.thruDate);
					var thruDate0 = new Date($("#thruDateEdit").jqxDateTimeInput("getDate"));
					var nowDate0 = new Date("${nowTimestamp}");
					if (thruDate0 < nowDate0) {
						$("#thruDateEdit").jqxDateTimeInput({disabled: true});
		    			$("#alterSave2").jqxButton({disabled: true});
		    			$("#sequenceNumEdit").jqxNumberInput({disabled: true});
					}
				}else if (data.thruDate = null){
					$("#thruDateEdit").jqxDateTimeInput("setDate", nowDate0);
				}
				$("#alterpopupWindowEdit").jqxWindow("open");
			}
		}
	});
	
	var initWindow = (function(){
		$('#alterpopupWindowEdit').jqxWindow({width: 500, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, title: updatePopup});
		$('#alterpopupWindowEdit').jqxWindow('resizable', false);
	});
	
	var initInput = (function(){
		jOlbUtil.dateTimeInput.create("#fromDateEdit", {width: '100%', height: 28, allowNullDate: false, showFooter: true, disabled: true});
		jOlbUtil.dateTimeInput.create("#thruDateEdit", {width: '100%', height: 28, allowNullDate: true, showFooter: true});
		$("#sequenceNumEdit").jqxNumberInput({width: '100%', height: 28, spinButtons: false, digits: 3, inputMode: 'simple', textAlign: "right", decimalDigits: 0 });
	});
	
	var initDropDownList = (function(){
		$("#prodCatalogIdEdit").jqxDropDownList({ source: catalogList, width: '100%', height: '28px', displayMember: "catalogName", valueMember: "prodCatalogId", dropDownHeight: 200, disabled: true});
	});
	
	var eventUpdateProductStoreCatalog = (function(){
		$('#alterSave2').click(function () {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		   	if (rowindex >= 0) {
			   	editPSCatalog();
	           	$('#alterpopupWindowEdit').jqxWindow('hide');
	           	$('#alterpopupWindowEdit').jqxWindow('close');
		   	}
	    });
		
		function editPSCatalog(){
			var editSuccess = "${StringUtil.wrapString(uiLabelMap.BSSuccessK)}";
			var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
			var success = editSuccess;
			var cMemberr = new Array();
				var data3 = $("#jqxgrid").jqxGrid('getrowdata', row);
				var map = {};
				map['prodCatalogId'] = data3.prodCatalogId;
				map['productStoreId'] = data3.productStoreId;
				map['sequenceNum'] = $("#sequenceNumEdit").val();
				map['fromDate'] = data3.fromDate.getTime();
				map['thruDate'] = $("#thruDateEdit").jqxDateTimeInput("getDate");
				cMemberr = map;
			if (cMemberr.length <= 0){
				return false;
			} else {
				cMemberr = JSON.stringify(cMemberr);
				jQuery.ajax({
			        url: 'updateProductStoreCatalog',
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
			        }
			    });
			}
		}
	});
	
	var init = (function(){
		eventMenu();
		initWindow();
		initInput();
		initDropDownList();
		eventUpdateProductStoreCatalog();
	});
	
	return {
		init: init,
	}
}());


	
//	$("#fromDateEdit").jqxDateTimeInput({ width: '198px', height: '23px', disabled: true });	
//	$("#thruDateEdit").jqxDateTimeInput({ width: '198px', height: '23px' });	
//	$("#thruDateEdit").val(null);
//	$("#prodCatalogIdEdit").jqxDropDownList({ source: catalogList, width: '198px', height: '23px', displayMember: "catalogName", valueMember: "prodCatalogId", dropDownHeight: 200, disabled: true});
//	$("#sequenceNumEdit").jqxNumberInput({width: 198, height: 23, spinButtons: false, digits: 3, inputMode: 'simple', textAlign: "left", decimalDigits: 0 });
	
	
	
	