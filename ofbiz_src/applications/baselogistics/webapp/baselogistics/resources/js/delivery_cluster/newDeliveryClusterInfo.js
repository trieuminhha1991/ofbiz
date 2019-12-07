$(function(){
	OlbDeliveryClusterInfoObj.init();
});
var OlbDeliveryClusterInfoObj = (function() {
	var validatorVAL;
	var shipperDDB;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
        jOlbUtil.input.create("#deliveryClusterCode", {width: '97%'});
        jOlbUtil.input.create("#deliveryClusterName", {width: '97%'});
        jOlbUtil.input.create("#description", {width: '97%'});
	};
	var initElementComplex = function() {
        var configShipper = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
            columns: [
                {text: uiLabelMap.BLShipperCode, datafield: 'partyCode', width: '30%'},
                {text: uiLabelMap.FullName, datafield: 'fullName'},
            ],
            url: 'JQGetListShipperCluster',
            useUtilFunc: true,
            key: 'partyId',
            keyCode: 'partyCode',
            description: ['fullName'],
            autoCloseDropDown: true,
            filterable: true,
            sortable: true,
            dropDownHorizontalAlignment: 'right',
        };
        shipperDDB = new OlbDropDownButton($("#shipper"), $("#jqxGridShipper"), null, configShipper, []);
	};
	var initEvents = function() {

	};
	
	var initValidateForm = function(){
		var extendRules = [

          ];
   		var mapRules = [
				{input: '#deliveryClusterCode', type: 'validInputNotNull'},
   				{input: '#deliveryClusterName', type: 'validInputNotNull'},
                {input: '#shipper', type: 'validObjectNotNull', objType: 'dropDownButton'},
               ];
   		validatorVAL = new OlbValidator($('#initDeliveryCluster'), mapRules, extendRules, {position: 'topcenter'});
	};

    var getValidator = function(){
    	return validatorVAL;
    };
    var getShipperDDB = function(){
    	return shipperDDB;
    };
	return {
		init: init,
		getValidator: getValidator,
        getShipperDDB: getShipperDDB,
	}
}());