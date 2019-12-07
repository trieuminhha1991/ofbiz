var contactCommonInfoObj = (function(){
	var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
    	$.ajax({
    		url: url,
    		data: data,
    		type: 'POST',
    		success: function(response){
    			var listGeo = response.listReturn;
    			if(listGeo && listGeo.length > -1){
    				accutils.updateSourceDropdownlist(dropdownlistEle, listGeo);        				
    				if(selectItem != 'undefinded'){
    					dropdownlistEle.jqxDropDownList('selectItem', selectItem);
    				}
    			}
    		}
    	});
    };
    return{
    	updateSourceJqxDropdownList: updateSourceJqxDropdownList
    }
}());

var contextMenuObj = (function(){
	var init = function(){
		accutils.createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgridListCustomer").jqxGrid('getselectedrowindex');
			var data = $("#jqxgridListCustomer").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				editCustomerObj.openWindow(data);
			}else if(action == "expire"){
				bootbox.dialog(uiLabelMap.ExpireCustomerRelationshipConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								expireRelationship(data.customerTimePaymentId);
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}
		});
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgridListCustomer").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridListCustomer").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "PARTY_ENABLED"){
				$(this).jqxMenu('disable', "editCustomerMenu", true);
				$(this).jqxMenu('disable', "expireCustomerMenu", true);
				$(this).jqxMenu('disable', "activeCustomerMenu", false);
			}else{
				$(this).jqxMenu('disable', "editCustomerMenu", false);
				$(this).jqxMenu('disable', "expireCustomerMenu", false);
				$(this).jqxMenu('disable', "activeCustomerMenu", true);
			}
		});
	};
	
	var expireRelationship = function(customerTimePaymentId){
		$.ajax({
			url: 'expireCustomerTimePayment',
			data: {customerTimePaymentId: customerTimePaymentId},
			type: 'POST',
			success: function(response){
    			if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
    				var message = typeof(response._ERROR_MESSAGE_) != "undefined"? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST_[0] 
    				bootbox.dialog(message,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
    			}else{
                    $("#notificationAddSuccess").text(uiLabelMap.CreateSuccessfully);
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
    				$("#jqxgridListCustomer").jqxGrid('updatebounddata');
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			Loading.hide('loadingMacro');
    		}
		});
	};
	return{
		init: init
	}
}());

var editCustomerObj = (function(){
	var _partyId = '';
	var _dueDay = '';
	var _customerTimePaymentId = '';
	var _isEdit = false;
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
        $("#dueDay").jqxNumberInput({ width: '96%', inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
		$("#fromDate").jqxDateTimeInput({width: '96%', height: 25});
        $("#enumPartyTypeId").val("CUSTOMER_PTY_TYPE");
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewCustomerTimePaymentWindow"), 650, 260);
	};
	var initEvent = function(){
        $("#enumPartyTypeId").on('select', function(event){
            if(_isEdit) return;
            var args = event.args;
            if (args) {
                var item = args.item;
                var value = item.value;
                var grid;
                grid = $("#partyGrid");
                $("#partyId").val("");
                var source = grid.jqxGrid('source');
                source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
                grid.jqxGrid('source', source);
            }
        });
		$("#addNewCustomerTimePaymentWindow").on('open', function(){
			if(!_isEdit){
				var date = new Date();
				$("#fromDate").val(date);

				$("#fromDate").jqxDateTimeInput({disabled: false});
                $('#partyId').jqxDropDownButton({disabled: false });
                $("#enumPartyTypeId").jqxDropDownList({ disabled: false });

                $("#enumPartyTypeId").val("CUSTOMER_PTY_TYPE");
                $('#newCustomerLabel > div:first-child').html("");
                $('#newCustomerLabel > div:first-child').text(uiLabelMap.BACCCreateNew);
			}else{
				$("#fromDate").jqxDateTimeInput({disabled: true});
                $("#enumPartyTypeId").jqxDropDownList({ disabled: true });
                $('#partyId').jqxDropDownButton({disabled: true });

                $('#newCustomerLabel > div:first-child').html("");
                $('#newCustomerLabel > div:first-child').text(uiLabelMap.editCustomerTimePayment);
			}
		});
		$("#addNewCustomerTimePaymentWindow").on('close', function(){
            Grid.clearForm($("#addNewCustomerTimePaymentWindow"));
            _isEdit = false;
		});
		
		$("#cancelAddCustomerTimePayment").click(function(event){
			$("#addNewCustomerTimePaymentWindow").jqxWindow('close');
		});
		$("#saveAddCustomerTimePayment").click(function(event){
			var valid = $("#addNewCustomerTimePaymentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(!_isEdit){
				bootbox.dialog(uiLabelMap.CreateCustomerConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createNewCustomer();	
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}else{
				updateCustomer();
			}
		});
	};
	var getData = function(){
		var data = {};
		data.partyId = $('#partyId').attr('data-value');
		data.dueDay = $("#dueDay").val();
		var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
		data.fromDate = fromDate.getTime();
		return data;
	};
	
	var updateCustomer = function(){
		Loading.show('loadingMacro');
		var data = getData();
        data.customerTimePaymentId = _customerTimePaymentId;
		$.ajax({
			url: 'updateCustomerTimePayment',
			data: data,
			type: 'POST',
			success: function(response){
    			if(response.responseMessage == 'success'){
                    $("#notificationAddSuccess").text(uiLabelMap.CreateSuccessfully);
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
    				$("#addNewCustomerTimePaymentWindow").jqxWindow('close');
    				$("#jqxgridListCustomer").jqxGrid('updatebounddata');
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			Loading.hide('loadingMacro');
    		}
		});
	};
	
	var createNewCustomer = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createCustomerTimePayment',
			data: data,
			type: 'POST',
			success: function(response){
			    console.log(response);
    			if(response.responseMessage == 'success'){
                    $("#notificationAddSuccess").text(uiLabelMap.CreateSuccessfully);
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
    				$("#addNewCustomerTimePaymentWindow").jqxWindow('close');
    				$("#jqxgridListCustomer").jqxGrid('updatebounddata');
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			Loading.hide('loadingMacro');
    		}
		});
	};
	var initValidator = function(){
		$("#addNewCustomerTimePaymentWindow").jqxValidator({
			scroll: false,
			rules: [
			        {
			        	input: '#partyId',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
		        			if(!input.val()){
		        				return false;
		        			}
		        			return true;
			        	}
			        },
			        {
			        	input: '#dueDay',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			]
		});
	};
	var openWindow = function(data){
		_isEdit = true;
		_partyId = data.partyId;
		_dueDay = data.dueDay;
        _customerTimePaymentId = data.customerTimePaymentId;
        $("#partyId").val(_partyId);
        $("#dueDay").val(_dueDay);
        $("#fromDate").val(data.fromDate);
        $.ajax({
            url: "getPartyTypeFromPartyId",
            data: {
                partyId: _partyId
            },
            type: 'POST',
            success: function(response){
                $("#enumPartyTypeId").val(response.partyTypeId);
                accutils.openJqxWindow($("#addNewCustomerTimePaymentWindow"));
            }
        });
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	editCustomerObj.init();
	contextMenuObj.init();
});