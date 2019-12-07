var CategoryDetail = (function(){
	var prepareDataForm = function(){

	};
	var initFilter = function(obj, input){
		obj.click(function(){
			var val = $(this).attr('data-id');
			input.val(val);
			$('input[name="VIEW_INDEX"]').val(0);
			$('#orderForm').submit();
		});
	};
	var bindEvent = function(){
		$('.clearfilter').click(function(){
			var id = $(this).data('id');
			console.log($('input[name="'+id+'"]'));
			var ob = $('input[name="'+id+'"]').val('');
			$('#orderForm').submit();
		});

		$('#products-orderby').change(function(){
			var value = $(this).val();
			$('input[name="orb"]').val(value);
			$('input[name="VIEW_INDEX"]').val(0);
			$('#orderForm').submit();
		});
		initFilter($('.gender'), $('input[name="genderFilter"]'));
		initFilter($('.supplier'), $('input[name="brandFilter"]'));
		initFilter($('.origin'), $('input[name="originFilter"]'));

		$('.navigate').click(function(e){
			e.preventDefault();
			var href = $(this).attr('href');
			var tmp = href.split("VIEW_INDEX=");
			var index = 0;
			if(tmp.length == 2){
				index = tmp[1];
			}
			$('input[name="VIEW_INDEX"]').val(index);
			$('#orderForm').submit();
		});
	};
	var init = function(){
		bindEvent();
	};
	return {
		init : init
	};
})();
$(document).ready(function(){
	CategoryDetail.init();
});
