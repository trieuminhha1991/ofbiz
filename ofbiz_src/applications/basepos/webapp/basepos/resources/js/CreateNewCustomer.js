$(function(){
	OlbNewCustomerPopup.init();
});

var OlbNewCustomerPopup = (function(){
	var validatorVAL;
	var init = function(){
		initWindow();
		initOther();
	};
	var initOther = function(){
		initElementComplex();
		initEvent();
		initValidateForm();
	};
	var initWindow = function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowCreateNewCustomer"), {width: 500, height: 260, cancelButton: $("#alterCancelCustomer")});
	};
	
	var initElementComplex = function(){
	    var initDropDownGeoState = function(elementObj, sname, selectArr){
	    	var url = "jqxGeneralServicer?sname=" + sname;
	    	var configGeo = {
	    		width: "99%",
	    		theme: 'base',
	    		height: 28,
	    		key: "geoId",
	    		value: "geoName", 
	    		displayDetail: true,
	    		placeHolder: BSClickToChoose,
	    		useUrl: true,
	    		url: url,
	    		datafields: [{name: 'geoId'}, {name: 'geoName'}],
		        autoComplete: true, searchMode: 'containsignorecase', renderer: null, renderSelectedItem: null,
		        selectedIndex: 0,
		        autoOpen: true
	    	};
	    	return new OlbComboBox(elementObj, null, configGeo, selectArr);
	    };
	    
	    var urlDropDownStateProvince = "JQGetAssociatedStateListGeo&geoId=" + countryGeoId + "&pagesize=0&pagenum=1";
	    initDropDownGeoState($("#city"), urlDropDownStateProvince, [stateProvinceGeoId]);
	};
	
	var initEvent = function(){
		$('#alterpopupWindowCreateNewCustomer').on('open', function (event) { 
			flagPopup = false;
			$('#customerFullName').focus();
		});
		$('#alterpopupWindowCreateNewCustomer').keydown(function(event) {  	
	    	code = event.keyCode ? event.keyCode : event.which; 	
	    	if (code == 13){
//	    		createNewCustomer();
	       	}
		});
		$('#alterpopupWindowCreateNewCustomer').on('close', function (event) { 
			$('#customerFullName').val('');
			$('#address').val('');
			$("#city").jqxDropDownList('clearSelection'); 
			$('#phone').val('');
			$('#alterpopupWindowCreateNewCustomer').jqxValidator('hide');
			flagPopup = true;
		});
		
		$('#alterpopupWindowCreateNewCustomer').on('keydown', function(e) {
			if (e.which == 13) {
				if (!$('#alterSaveCustomer').is(":focus") && !$('#alterCancelCustomer').is(":focus")) {
					$('#alterSaveCustomer').click();
				}
			}
		});
		$('body').keydown(function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			//118 is code of F7
			if (code == 118 && POSPermission.has("POS_CUSTOMER_F7", "CREATE")) {
				if (flagPopup){
					$("#alterpopupWindowCreateNewCustomer").jqxWindow('open');
				}
				e.preventDefault();
				return false;
			}
		});
		
		$('#alterSaveCustomer').on('click', function(){
			createNewCustomer();
		})
	};
	
	var createNewCustomer = function(){
		var validate = validatorVAL.validate();
		if (validate){
			var customerName = $('#customerFullName').val();
			var address = $('#address').val();
			var city = $('#city').val(); 
			var phone = $('#phone').val();
			bootbox.confirm(BPOSAreYouCertainlyCreated, function(result) {
				if(result){
					var contactAddress = [{
						address1 : address,
						stateProvinceGeoId : city,
						countryGeoId : countryGeoId
					}];
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		$.ajax({url: 'CreateCustomerPOS',
	    					data: {
	    						customerName : customerName,
	    						contactAddress : JSON.stringify(contactAddress),
	    						phone: phone
	    					},
	    					type: 'post',
	    					async: false,
	    					success: function(data) {
	    						getResultOfCreateCustomer(data);
	    					},
	    					error: function(data) {
	    						getResultOfCreateCustomer(data);
	    					}
	    				});
	            		Loading.hide('loadingMacro');
	            	}, 500);
				} else {
					$("#alterpopupWindowCreateNewCustomer").jqxWindow('open');
				}
			});
		}
	};
	
	var getResultOfCreateCustomer = function(data){
		var serverError = getServerError(data);
	    if (serverError != "") {
	    	$('#alterpopupWindowCreateNewCustomer').jqxWindow('close');
	    	bootbox.alert(serverError, function() {
	    		$('#alterpopupWindowCreateNewCustomer').jqxWindow('open');
			});
	    } else {
	    	$('#alterpopupWindowCreateNewCustomer').jqxWindow('close');
	    	updateParty();
	        updateCartWebPOS();
	        setPartyToCart(data.partyId);
	    }
	};
	
	var initValidateForm = function(){
		var extendRules = [
		   				{ input: '#customerFullName', message: validFieldRequire, action: 'change', rule: 'required' },
		              ];
   		var mapRules = [
   				{input: '#address', type: 'validInputNotNull', message: validFieldRequire},
   				{input: '#city', type: 'validInputNotNull', message: validFieldRequire},
               ];
   		validatorVAL = new OlbValidator($('#alterpopupWindowCreateNewCustomer'), mapRules, extendRules, {position: 'bottom'});
	};
	
	return {
		init: init,
	}
}());