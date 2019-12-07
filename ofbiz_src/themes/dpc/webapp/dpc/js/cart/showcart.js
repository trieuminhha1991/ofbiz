var Cart = (function() {
	var notifyEmail = "Email đã được sử dụng, <br/>vui lòng sử dụng email khác hoặc <br/>đăng nhập sử dụng email <a href='"+baseUrl+"'>này</a>";
	var isEmailUsed = false;
	var getGeo = function(geoId, obj) {
		$.ajax({
			url : 'autoCompleteGeo',
			data : {
				geoTypeId : 'DISTRICT',
				geoId : geoId,
			},
			success : function(res) {
				if (res.listGeo && res.listGeo.length) {
					renderDistrict(res.listGeo);
					enableSelect(obj);
					enableSelect($('select[name="district"]'));
				}
			},
			beforeSend : function() {
				disableSelect($('select[name="district"]'));
				disableSelect(obj);
			}
		});
	};
	var enableSelect = function(obj) {
		obj.removeAttr('disabled');
	};
	var disableSelect = function(obj) {
		obj.attr('disabled', 'disabled');
	};
	var renderDistrict = function(data) {
		var element = $('select[name="district"]');
		var str = "";
		for ( var x in data) {
			str += "<option value='" + data[x].geoId + "'>" + data[x].geoName
					+ "</option>";
		}
		element.html(str);
	};
	var checkToggle = function(e) {
		var cform = document.cartform;
		if (e.checked) {
			var len = cform.elements.length;
			var allchecked = true;
			for (var i = 0; i < len; i++) {
				var element = cform.elements[i];
				if (element.name == "selectedItem" && !element.checked) {
					allchecked = false;
				}
				cform.selectAll.checked = allchecked;
			}
		} else {
			cform.selectAll.checked = false;
		}
	};
	var toggleAll = function(e) {
		var cform = document.cartform;
		var len = cform.elements.length;
		for (var i = 0; i < len; i++) {
			var element = cform.elements[i];
			if (element.name == "selectedItem" && element.checked != e.checked) {
				toggle(element);
			}
		}
	};
	var toggle = function(e) {
		e.checked = !e.checked;
	};
	var removeSelected = function() {
		var cform = document.cartform;
		cform.removeSelected.value = true;
		cform.submit();
	};
	var bindEvent = function() {
		$('select[name="city"]').change(function() {
			var obj = $(this);
			var x = obj.val();
			getGeo(x, obj);
		});
		$('#confirmcart').submit(function(e){
			if(!validateForm()){
				e.preventDefault();
				return false;
			}else{
				processShippingDate();
				return true;
			}
		});
		$('#updatecart').click(function() {
			removeSelected();
		});
		$('#reject').click(function() {
			$('input[name="selectAll"]').click();
		});
		$('input[name="email"]').on('keyup blur', function(){
			var val = $(this).val();
			var party = $('#partyId').val();
			if(!party){
				if(Util.validateEmail(val)){
					checkUserExist(val);
				}
			}
		});
	};
	var processShippingDate = function(){
		var shipDate = new Date(parseInt($('#date').val()));
		var h = $('#hour').val();
		shipDate.setHours(h);
		shipDate.setMinutes(0);
		shipDate.setSeconds(0);
		shipDate.setMilliseconds(0);
		$('input[name="shipBeforeDate"]').val(shipDate.getTime());
	};
	var initValidate = function() {
		var obj = $('#confirmcart');
		obj.jqxValidator({
			rules : [{
				input : '#name',
				message : 'Nhập họ tên của bạn',
				action : 'keyup',
				rule : 'required'
			}, {
				input : '#phone',
				message : 'Nhập số điện thoại',
				action : 'keyup',
				rule : function(input, label){
					var num = input.val();
					if ( /^\+?[0-9]+$/.test(num)) {
						return true;
					}
					return false;
				}
			}, {
				input : '#email',
				message : 'Địa chỉ email không hợp lệ',
				action : 'change, focus',
				rule : 'email'
			}, {
				input : '#email',
				message : 'Nhập email để nhận thông tin đặt hàng',
				action : 'change, focus',
				rule : 'required'
			}, {
				input : '#city',
				message : 'Chọn tỉnh thành phố',
				action : 'keyup',
				rule : function(input, val){
					if(input.val()){
						return true;
					}
					return false;
				}
			}, {
				input : '#district',
				message : 'Chọn quận huyện',
				action : 'keyup',
				rule : function(input, val){
					if(input.val()){
						return true;
					}
					return false;
				}
			}, {
				input : '#address',
				message : 'Nhập địa chỉ nhận hàng',
				action : 'keyup',
				rule : 'required'
			}]
		});
	};
	var validateForm = function(){
		var formValidate = $('#confirmcart').jqxValidator('validate');
		if(formValidate && !isEmailUsed)
			return true;
		if(isEmailUsed){
			renderMessage($('#email'), notifyEmail);
		}
		return false;
	};
	var checkUserExist = function(email){
		$.ajax({
			url : 'checkUserExist',
			data: {
				userLoginId : email
			},
			success: function(res){
				if(res.status && res.status == 'success'){
					renderMessage($('#email'), notifyEmail);
					isEmailUsed = true;
				}else{
					isEmailUsed = false;
				}
			}
		});
	};
	var autoCompleteAddress = function(address){
		$.ajax({
			url : 'autoCompleteAddress',
			data: {
				address : address
			},
			success: function(res){
				if(res.listAddress){

				}
			}
		});
	};
	var renderMessage = function(obj, msg){
		obj.jqxTooltip({ content: msg, position: 'right', autoHide: true, trigger: "none", closeOnClick: true, theme:'validation', autoHideDelay : 5000 });
		obj.jqxTooltip('open');
	};
	var init = function() {
		bindEvent();
		initValidate();
	};
	return {
		init : init,
		checkToggle : checkToggle,
		toggleAll : toggleAll
	};
})();
$(document).ready(function() {
	Cart.init();
});
