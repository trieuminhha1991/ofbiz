$(document).ready(function (){
	$('#alterpopupWindowCreateOrder').jqxWindow({
		width: '560', height:'310', resizable: true,draggable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius', cancelButton: $('#alterCancel')
	});
	
	$("#amount").maskMoney({precision:2,thousands: '.', decimal: ','});
	
	$("#amount").keypress(function (e) {
	    //if the letter is not digit then display error and don't type anything
	    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 13 && e.which != 16 && e.which != 46) {
	    	return false;
	    } else {
	    	var amount = $("#amount").maskMoney('unmasked')[0];
	    	var totalDue = $("#totalDue").val();
	    	totalDue = parseFloat(totalDue);
	    	var totalDueOrigin = totalDue;
	    	if(totalDue < 0){
	    		totalDueOrigin = totalDue * (-1);
	    	}
	    	amount = parseFloat(amount);
	    	if(amount > totalDueOrigin){
	    		$('#alterpopupWindowCreateOrder').jqxWindow('close');
	    		bootbox.hideAll();
				bootbox.alert(BPOSAmountBiggerTotalDue, function(){
					$('#alterpopupWindowCreateOrder').jqxWindow('open');
					$('#amount').val('0');
				});
	    	}
	    }
	});
	
	$("#deliveredDate").jqxDateTimeInput({ width: '99%', height: '30', formatString: 'dd/MM/yyyy HH:mm:ss'});
	
	$('#alterpopupWindowCreateOrder').jqxValidator({
        rules: [
	                { input: '#shippingContactMechId', message: BPOSValidateRequired, action: 'keyup, blur', 
						rule: function (input, commit) {
							if (input.val()) {
								return true;
							}
							return false;
						}	
					}
               ]
	 });
});

function initDropDownListPaymentMethod(defaultPartyId, defaultProductStoreId){
	var urlPaymentMethodType = '';
	if (defaultPartyId != null && defaultProductStoreId != null) {
		urlPaymentMethodType = 'jqxGeneralServicer?sname=JQGetPaymentMethodByCustomerAndStore&partyId=' + defaultPartyId + '&productStoreId=' + defaultProductStoreId;
	}
	
	var sourcePayment =
    {
        datatype: "json",
        datafields: [
            { name: 'paymentMethodTypeId' },
            { name: 'description' }
        ],
        url: urlPaymentMethodType,
        async: false
    };
    var dataAdapterPayment = new $.jqx.dataAdapter(sourcePayment);
    $("#checkOutPaymentId").jqxDropDownList({ source: dataAdapterPayment, height: '30', selectedIndex: 1, 
		displayMember: 'description', valueMember: 'paymentMethodTypeId', width: '99%',
	});
}

function initDropDownListShippingMethod(defaultPartyId, defaultProductStoreId){
	var urlShippingMethod = '';
	if (defaultPartyId != null && defaultProductStoreId != null) {
		urlShippingMethod = 'jqxGeneralServicer?sname=JQGetShippingMethodByCustomerAndStore&partyId=' + defaultPartyId + '&productStoreId=' + defaultProductStoreId;
	}
	
	var sourceShipping =
    {
        datatype: "json",
        datafields: [
            { name: 'shippingMethod' },
            { name: 'description' }
        ],
        url: urlShippingMethod,
        async: false
    };
    var dataAdapterShipping = new $.jqx.dataAdapter(sourceShipping);
    $("#shippingMethodTypeId").jqxDropDownList({ source: dataAdapterShipping, height: '30', selectedIndex: 1, 
		displayMember: 'description', valueMember: 'shippingMethod', width: '99%',
	});
}

function initDropDownButtonShippingAddress(defaultPartyId, defaultProductStoreId){
	var sourceAddress =
	{
			datafields:
				[
					{name: 'contactMechId', type: 'string'}, 
					{name: 'toName', type: 'string'}, 
					{name: 'attnName', type: 'string'},
					{name: 'address1', type: 'string'},
					{name: 'city', type: 'string'},
					{name: 'stateProvinceGeoId', type: 'string'},
					{name: 'postalCode', type: 'string'},
					{name: 'countryGeoId', type: 'string'},
					{name: 'districtGeoId', type: 'string'},
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceAddress.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#shippingContactMechGrid").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#shippingContactMechGrid").jqxGrid('updatebounddata');
			},
			sortcolumn: 'contactMechId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:15,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=JQGetShippingAddressByPartyReceive&partyId=' + defaultPartyId,
	};
	
	var dataAdapterAddress = new $.jqx.dataAdapter(sourceAddress);
	$("#shippingContactMechId").jqxDropDownButton({ width: 305, height: 30, dropDownHorizontalAlignment: 'right'});
	$("#shippingContactMechGrid").jqxGrid({
		source: dataAdapterAddress,
		filterable: true,
		showfilterrow: true,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		width: 800,
		showfilterrow: true,
		columnsresize: true,
		localization: getLocalization(),
		rendergridrows: function(obj)
		{
			return obj.data;
		},
		columns: [
			{text: BSContactMechId, datafield: 'contactMechId', width: '20%'},
			{text: BSReceiverName, datafield: 'toName', width: '20%'},
			{text: BSOtherInfo, datafield: 'attnName', width: '20%'},
			{text: BSAddress, datafield: 'address1', width: '20%'},
			{text: BSCity, datafield: 'city', width: '20%'},
			{text: BSStateProvince, datafield: 'stateProvinceGeoId', width: '15%'},
			{text: BSCountry, datafield: 'countryGeoId', width: '15%'},
			{text: BSCounty, datafield: 'districtGeoId', width: '15%'},
		]
	});
	
	$("#shippingContactMechGrid").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#shippingContactMechGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 1px; margin-top: 8px;\">' + '[' + row['contactMechId'] + '] ' + row['address1'] + ' - ' + row['city'] +'</div>';
		$('#shippingContactMechId').jqxDropDownButton('setContent', dropDownContent);
		$('#shippingContactMechId').jqxDropDownButton('close'); 
	});
	
	$('#shippingContactMechId').on('open', function(){
		$("#shippingContactMechGrid").jqxGrid('unselectrow', 0);
	});
	
}

function showPopupCreateOrder(){
	var partyId = $("#partyIdTmp").val();
	var rows = $("#showCartJqxgrid").jqxGrid('getRows');
	if(rows.length == 0){
		$('#alterpopupWindowCreateOrder').jqxWindow('close');
		bootbox.hideAll();
		bootbox.alert(BPOSNoAnyItemInCart);
	} else {
		if(partyId == null || partyId ==""){
			$('#alterpopupWindowCreateOrder').jqxWindow('close');
			bootbox.hideAll();
			bootbox.alert(BPOSPleaseSelectCustomer, function() {
				$('#jqxPartyList').jqxComboBox('focus');
			});
		}else{
			$("#alterpopupWindowCreateOrder").jqxWindow('open');
		}
	}
}

function createOrderToLog(){
	var validate = $('#alterpopupWindowCreateOrder').jqxValidator('validate');
	if (validate){
		var shippingMethodTypeId = $('#shippingMethodTypeId').val();
		var checkOutPaymentId = $('#checkOutPaymentId').val();
		var deliveredDate = $('#deliveredDate').jqxDateTimeInput('getDate').getTime(); 
		var shippingContactMechId = $('#shippingContactMechId').val();
		var index = shippingContactMechId.indexOf("]");
		shippingContactMechId = shippingContactMechId.substring(1,index);
		//$('#alterpopupWindowCreateOrder').jqxWindow('close');
		var amount = $("#amount").maskMoney('unmasked')[0];
		amount = parseFloat(amount);
		bootbox.confirm(BPOSAreYouCertainlyCreated, function(result) {
			if(result){
				var param = "shipping_contact_mech_id=" + shippingContactMechId + "&shipping_method="+shippingMethodTypeId  
					+ "&checkOutPaymentId=" + checkOutPaymentId + "&desiredDeliveryDate="+deliveredDate + "&productStoreId="+defaultProductStoreId
					+ "&CASH_amount=" + amount;
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({url: 'CreateOrderToLog',
    					data: param,
    					type: 'post',
    					async: false,
    					success: function(data) {
    					    getResultOfCreateOrderToLog(data);
    					},
    					error: function(data) {
    						getResultOfCreateOrderToLog(data);
    					}
    				});
            		Loading.hide('loadingMacro');
            	}, 500);
			} else {
				$("#amount").focus();
			}
		});
	}
}

function getResultOfCreateOrderToLog(data){
	var serverError = getServerError(data);
    if (serverError != "") {
    	$('#alterpopupWindowCreateOrder').jqxWindow('close');
    	flagPopup = false;
    	bootbox.alert(serverError, function() {
    		flagPopup = true;
    		$('#alterpopupWindowCreateOrder').jqxWindow('open');
		});
    } else {
    	$('#alterpopupWindowCreateOrder').jqxWindow('close');
    	updateParty();
        updateCartWebPOS();
    }
}