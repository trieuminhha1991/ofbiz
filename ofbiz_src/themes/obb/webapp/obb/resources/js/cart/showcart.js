var Cart = (function() {
	var enableSelect = function(obj) {
		obj.removeAttr('disabled');
	};
	var disableSelect = function(obj) {
		obj.attr('disabled', 'disabled');
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
		$('#promotion-code').keyup(function(){
			var val = $(this).val();
			val = val.replace(/[^\w-]/gi, '');
			var res = '';
			for(var x = 0; x < val.length; x++){
				res += val[x].toUpperCase();
			}
			$(this).val(res);
		});
		$('#updatecart').click(function() {
			removeSelected();
		});
		$('#reject').click(function() {
			$('input[name="selectAll"]').click();
		});
	};

	var init = function() {
		bindEvent();
	};
	return {
		init : bindEvent,
		checkToggle : checkToggle,
		toggleAll : toggleAll
	};
})();
$(document).ready(function() {
	Cart.init();
});
