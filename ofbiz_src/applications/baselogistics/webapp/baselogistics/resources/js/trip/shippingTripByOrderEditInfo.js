$(function () {
    TripEditInfoObj.init();
});
var TripEditInfoObj = (function () {
    var validatorVAL;
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function () {
        $("#fromDate").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString: 'dd/MM/yyyy HH:mm'});
        $("#fromDate").jqxDateTimeInput('setDate', startDateTimeOld);
        $("#thruDate").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString: 'dd/MM/yyyy HH:mm'});
        $("#thruDate").jqxDateTimeInput('setDate', finishedDateTimeOld);
        $("#driverPartyId").jqxDropDownList({
            placeHolder: shipperPartyData['shipperName'],
            width: 300,
            theme: theme,
            disabled: true,
            height: '24px',
            dropDownHeight: 200
        });
        $("#driverPartyId").val(shipperPartyData.shipperId);
        $("#facilityId").jqxDropDownList({
            placeHolder: uiLabelMap.PleaseSelectTitle,
            width: 300,
            selectedIndex: 0,
            theme: theme,
            source: facility,
            valueMember: 'facilityId',
            displayMember: 'facilityName',
            height: '24px',
            dropDownHeight: 200,
            disabled: true,
        });
        
        var contactMechData = [];
        $("#contactMechId").jqxDropDownList({
            placeHolder: uiLabelMap.PleaseSelectTitle,
            width: 300,
            source: contactMechData,
            autoDropDownHeight: true,
            displayMember: "description",
            selectedIndex: 0,
            valueMember: "contactMechId",
            disabled: true,
        });
        $("#shipCost").jqxNumberInput({width: 295, height: 25, spinButtons: true});
        $("#shipCost").val(shipCost);
        $("#shipReturnCost").jqxNumberInput({width: 295, height: 25, spinButtons: true});
        $("#shipReturnCost").val(costCustomerPaid);
        $("#description").jqxInput({height: 40,width: 300});
        $("#description").val(description);
    };
    var initElementComplex = function () {
    }
    var initEvents = function () {
        if ($("#facilityId").val() != null && $("#facilityId").val() != undefined && $("#facilityId").val() != "") {
            update({
                facilityId: $("#facilityId").val(),
                contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
            }, 'getFacilityContactMechs', 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
        }
        $("#facilityId").on('change', function (event) {
            update({
                facilityId: $("#facilityId").val(),
                contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
            }, 'getFacilityContactMechs', 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
        });
    }
    var initValidateForm = function () {
        var extendRules = [
          {
            input: '#thruDate',
              message: uiLabelMap.EndDateMustBeAfterStartDate,
              action: 'keyup, blur',
              position: 'topcenter',
              rule: function (input) {
                var fromDateTmp = $('#fromDate').jqxDateTimeInput('getDate');
                var thruDateTmp = $('#thruDate').jqxDateTimeInput('getDate');
                if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
                  if (fromDateTmp > thruDateTmp) {
                    return false;
                  }
                }
                return true;
              }
          },
          {
            input: '#thruDate',
              message: uiLabelMap.CannotBeforeNow,
              action: 'keyup, blur',
              position: 'topcenter',
              rule: function (input) {
                var temp = new Date();
                var thruDateTmp = $('#thruDate').jqxDateTimeInput('getDate');
                if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
                  if (thruDateTmp < temp ) {
                    return false;
                  }
                }
                return true;
              }
          },
          // {
          // 	input: '#fromDate',
          //     message: uiLabelMap.CannotBeforeNow,
          //     action: 'keyup, blur',
          //     position: 'topcenter',
          //     rule: function (input) {
          //     	var temp = new Date();
          //     	var startTime = $('#fromDate').jqxDateTimeInput('getDate');
          // 	   	if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(startTime) != 'undefined' && startTime != null && !(/^\s*$/.test(startTime)))) {
          //  		    if (startTime < temp) {
          //  		    	return false;
          //  		    }
          // 	   	}
          // 	   	return true;
          //     }
          // },
          {
            input: '#shipCost',
              message: uiLabelMap.CostRequired,
              action: 'keyup, blur',
              position: 'topcenter',
              rule: function (input) {
                var tripCost = $('#shipCost').val;
                if (tripCost <= 0) {
                  return false;
                }
                return true;
              }
          },
          {
            input: '#shipReturnCost',
              message: uiLabelMap.CostRequired,
              action: 'keyup, blur',
              position: 'topcenter',
              rule: function (input) {
                var costCustomerPaid = $('#shipReturnCost').val;
                if (costCustomerPaid <= 0) {
                  return false;
                }
                return true;
              }
          }
        ];
        var mapRules = [
    			  {input: '#shipCost', type: 'validInputNotNull'},
 				  {input: '#shipReturnCost', type: 'validInputNotNull'},
 				  {input: '#fromDate', type: 'validInputNotNull'},
 				  {input: '#thruDate', type: 'validInputNotNull'},
        ];
        validatorVAL = new OlbValidator($('#editShippingTrip'), mapRules, extendRules, {position: 'bottom'});
    };

    function renderHtml(data, key, value, id) {
        var y = "";
        var source = new Array();
        var index = 0;
        for (var x in data) {
            index = source.length;
            var row = {};
            row[key] = data[x][key];
            row['description'] = data[x][value];
            source[index] = row;
        }
        if ($("#" + id).length) {
            $("#" + id).jqxDropDownList('clear');
            $("#" + id).jqxDropDownList({source: source, selectedIndex: 0});
        }
    }
    function update(jsonObject, url, data, key, value, id) {
        jQuery.ajax({
            url: url,
            type: "POST",
            data: jsonObject,
            async: false,
            success: function (res) {
                var json = res[data];
                renderHtml(json, key, value, id);
            }
        });
    }

    var getValidator = function () {
        return validatorVAL;
    };
    return {
        init: init,
        getValidator : getValidator,
    }
})();