$.fn.spin = function(opts) {
	this.each(function() {
		var $this = $(this), data = $this.data();

		if (data.spinner) {
			data.spinner.stop();
			delete data.spinner;
		}
		if (opts !== false) {
			data.spinner = new Spinner($.extend({
				color : $this.css('color')
			}, opts)).spin(this);
		}
	});
	return this;
};
function spinner_update(opts, id) {
	$('#'+id).spin(opts);
}
function showLoading(id){
	$("#"+id).show();
}
function hideLoading(id){
	$("#"+id).hide();
}
