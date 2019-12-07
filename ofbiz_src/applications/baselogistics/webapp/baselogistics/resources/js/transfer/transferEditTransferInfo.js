$(function () {
	TransferEditInfoObj.init();
});
var TransferEditInfoObj = (function () {
	var validatorVAL;

	var init = function () {
		initInputs();
		initValidateForm();
	};
	var initInputs = function () {
		if (originFacilityData) {
			var content;
			if (originFacilityData.facilityCode) {
				content =  '[' + originFacilityData.facilityCode + '] ' + originFacilityData.facilityName ;
			} else {
				content = '[' + originFacilityData.facilityId + '] ' + originFacilityData.facilityName ;
			}
			$('#originFacility').text(content);
		}
		if (destFacilityData) {
			var content;
			if (destFacilityData.facilityCode) {
				content = '[' + destFacilityData.facilityCode + '] ' + destFacilityData.facilityName ;
			} else {
				content = '[' + destFacilityData.facilityId + '] ' + destFacilityData.facilityName ;
			}
			$('#destFacility').text(content);
		}
		for (var i = 0; i < transferTypeData.length; i++) {
			if (transfer.transferType == transferTypeData[i].transferTypeId) {
				$('#transferTypeId').text( transferTypeData[i].description);
			}
		}
		$('#originContactMechId').text(originFacilityData.address);
		$('#destContactMechId').text(destFacilityData.address);
		$('#shipmentMethodTypeId').jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: shipmentMethodData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId' });
		var partyTmpData = [];
		$('#carrierPartyId').jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: partyTmpData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'partyId' });
		$("#shipBeforeDate").jqxDateTimeInput({ width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false });
		$("#shipBeforeDate").jqxDateTimeInput('setDate', new Date(DatetimeUtilObj.getDateTimeFullFromString(shipBeforeDate)));
		$("#shipAfterDate").jqxDateTimeInput({ width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false });
		$("#shipAfterDate").jqxDateTimeInput('setDate', new Date(DatetimeUtilObj.getDateTimeFullFromString(shipAfterDate)));
		$("#description").jqxInput({ width: 300, height: 65 });
		$("#description").jqxInput('val', transfer.description)
		if ($("#shipmentMethodTypeId").length > 0) {
			$("#shipmentMethodTypeId").val("GROUND_HOME");
			update({
				shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
			}, 'getPartyCarrierByShipmentMethodAndStore', 'listParties', 'partyId', 'fullName', 'carrierPartyId');
		}

		$('#carrierPartyId').jqxDropDownList('val', 'DLOG');
	};

	

	var initValidateForm = function () {
		var extendRules = [
			{
				input: '#shipAfterDate', message: uiLabelMap.CannotBeforeNow, action: 'blur', position: 'topcenter',
				rule: function (input, commit) {
					var value = $('#shipAfterDate').jqxDateTimeInput('getDate');
					var nowDate = new Date();
					if (value < nowDate) {
						return false;
					}
					return true;
				}
			},
			{
				input: '#shipAfterDate', message: uiLabelMap.BeforeEndDate, action: 'blur', position: 'topcenter',
				rule: function (input, commit) {
					var value1 = $('#shipAfterDate').jqxDateTimeInput('getDate');
					var value2 = $('#shipBeforeDate').jqxDateTimeInput('getDate');
					if (value2 <= value1) {
						return false;
					}
					return true;
				}
			},
			{
				input: '#shipBeforeDate', message: uiLabelMap.AfterStartDate, action: 'blur', position: 'topcenter',
				rule: function (input, commit) {
					var value1 = $('#shipAfterDate').jqxDateTimeInput('getDate');
					var value2 = $('#shipBeforeDate').jqxDateTimeInput('getDate');
					if (value2 <= value1) {
						return false;
					}
					return true;
				}
			},
			{
				input: '#shipBeforeDate', message: uiLabelMap.CannotBeforeNow, action: 'blur', position: 'topcenter',
				rule: function (input, commit) {
					var value = $('#shipBeforeDate').jqxDateTimeInput('getDate');
					var nowDate = new Date();
					if (value < nowDate) {
						return false;
					}
					return true;
				}
			},
		];
		var mapRules = [
			{ input: '#shipBeforeDate', type: 'validInputNotNull', action: 'valueChanged' },
			{ input: '#shipAfterDate', type: 'validInputNotNull', action: 'valueChanged' },
		];
		validatorVAL = new OlbValidator($('#initTransfer'), mapRules, extendRules, { position: 'right' });
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
			$("#" + id).jqxDropDownList({ source: source, selectedIndex: 0 });
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
		var x= validatorVAL.validate();
		setTimeout(function(){
			validatorVAL.hide();
		}, 2000);
		return x;
	};



	return {
		init: init,
		getValidator: getValidator,
	}
}());