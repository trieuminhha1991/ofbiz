var ShippingInfo = (function($){
	var bindEvent = function(){
		$("#ShippingBeforeDate").on("change", function(event){
			var date = event.args.date;
			var time = date.getTime();
			$('input[name="shipBeforeDate"]').val(time);
			setUserInfo();
		});
		$('#fullname').change(setUserInfo);
		$('#city').change(setUserInfo);
		$('#district').change(setUserInfo);
		$('#address').change(setUserInfo);
		$('#phone').change(setUserInfo);
		$('#email').change(setUserInfo);
		$('#input[name="gender"]').change(setUserInfo);
		$('#confirmcart').submit(function(){
			if(!$(this).jqxValidator('validate')){
				return false;
			}
			setUserInfo();
			setFullAddress();
			return true;
		});
	};
	var initElement = function(){
		var spd = $("#ShippingBeforeDate");
		spd.jqxDateTimeInput({
			theme: 'megamall',
			formatString: "dd/MM/yyyy HH:mm",
			showTimeButton: true,
			width: 'calc(100% - 2px)',
			min: new Date(),
			height: '35px'
		});
		var d = new Date();
		var date = d.getDate();
		d.setDate(date + 3);
		setTimeout(function(){
			spd.jqxDateTimeInput('setDate', d);
		},200);
	};
	var initUserInfo = function(){
		var name = LocalStorage.getItem(partyId,"userFullName");
		if(name){
			$('#fullname').val(name);
		}
		var gender = LocalStorage.getItem(partyId,"userGender");
		if(gender){
			$('input[name="gender"][value="'+gender+'"]').prop('checked', 'checked');
		}
		var userPhone = LocalStorage.getItem(partyId,"userPhone");
		if(CommonUtils.validatePhone(userPhone)){
			$('#phone').val(userPhone);
		}
		var userEmail = LocalStorage.getItem(partyId,"userEmail");
		if(CommonUtils.validateEmail(userEmail)){
			$('#email').val(userEmail);
		}

		var ct = LocalStorage.getItem(partyId,'userCity');
		if(ct && ct != $('#city').val()){
			$('#city').val(ct).change();
		}else{
			setDistrictValue();
		}
		var addr = LocalStorage.getItem(partyId,'userAddress');
		if(addr){
			$('#address').val(addr);
		}
		var ship = LocalStorage.getItem(partyId,'userShipBeforeDate');
		if(ship){
			var date = new Date(ship);
			if(date)
				$('#ShippingBeforeDate').jqxDateTimeInput('val', date);
		}
	};
	var setUserInfo = function(){
		var name = $('#fullname').val();
		if(name){
			LocalStorage.setItem(partyId,"userFullName", name);
		}
		var gender = $('input[name="gender"]').val();
		if(gender){
			LocalStorage.setItem(partyId,"userGender", gender);
		}
		var userPhone = $('#phone').val();
		if(CommonUtils.validatePhone(userPhone)){
			LocalStorage.setItem(partyId,"userPhone", userPhone);
		}
		var userEmail = $('#email').val();
		if(CommonUtils.validateEmail(userEmail)){
			LocalStorage.setItem(partyId,"userEmail", userEmail);
		}

		var ct = $('#city').val();
		if(ct){
			LocalStorage.setItem(partyId,'userCity', ct);
		}
		var dt = $('#district').val();
		if(dt){
			LocalStorage.setItem(partyId,'userDistrict', dt);
		}
		var addr = $('#address').val();
		if(addr){
			LocalStorage.setItem(partyId,'userAddress', addr);
		}
		var ship = $('#ShippingBeforeDate').jqxDateTimeInput('getDate');
		if(ship){
			var time = ship.getTime();
			if(time)
				LocalStorage.setItem(partyId,'userShipBeforeDate', time);
		}
	};
	var setDistrictValue = function(){
		var dis = LocalStorage.getItem(partyId,'userDistrict');
		var options = $('#district option');
		for(var x = 0; x < options.length; x++){
			var cur = $(options[x]).val();
			if(cur == dis){
				$('#district').val(dis);
				break;
			}
		}
	};
	var setFullAddress = function(){
		var ct = $('#city option:checked').text();
		var dt = $('#district option:checked').text();
		var addr = $('#address').val();
		var fullAddr = "";
		if(addr){
			fullAddr = addr;
		}
		if(dt){
			fullAddr += ', ' + dt;
		}
		if(ct){
			fullAddr += ', ' + ct;
		}
		LocalStorage.setItem(partyId,'userFullAddress', fullAddr);
	};
	var initValidate = function(){
		if ($(window).width() < 750) {
			$('#confirmcart').jqxValidator({ position: 'bottom' });
		}
		$('#confirmcart').jqxValidator({
		    rules: [
				{ input: '#fullname', message: uiLabelMap.BERequired, action: 'blur', rule: 'required' },
				{ input: '#phone', message: uiLabelMap.BERequired, action: 'blur', rule: 'required' },
				{ input: '#phone', message: uiLabelMap.BEPhoneInvalid, action: 'blur', rule: function(input, label){
					var val = input.val();
					return CommonUtils.validatePhone(val);
				} },
				{ input: '#email', message: uiLabelMap.BERequired, action: 'blur', rule: 'required' },
				{ input: '#email', message: uiLabelMap.BERequired, action: 'blur', rule: 'email' },
				{ input: '#city', message: uiLabelMap.BERequired, action: 'blur', rule: function(input, label){
					var val = input.val();
					if(val){
						return true;
					}
					return false;
				} },
				{ input: '#district', message: uiLabelMap.BERequired, action: 'blur', rule: function(input, label){
					var val = input.val();
					if(val){
						return true;
					}
					return false;
				} },
				{ input: '#ShippingBeforeDate', message: uiLabelMap.BEShippingLargerThanNow, action: 'change', rule: function(input, label){
					var val = input.jqxDateTimeInput('val', 'date');
					var date = new Date();
					var sub = val.getTime() - date.getTime();
					if(sub > 0){
						return true;
					}
					return false;
				} },
				{ input: '#address', message: uiLabelMap.BERequired, action: 'blur', rule: 'required' }
            ]
		});
	};
	var init = function(){
		initElement();
		initUserInfo();
		bindEvent();
		initValidate();
		ChangeDistrict.init($("#city"), $("#district"), setDistrictValue);
	};
	return {
		init : init
	};
})(jQuery);
jQuery(document).ready(function(){
	ShippingInfo.init();
});
