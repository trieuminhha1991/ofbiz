if (typeof (ChangeDistrict) == "undefined") {
	var ChangeDistrict = (function($) {
		var init = function(province, district, callback) {
			province.change(function() {
				district.val("");
				var data = DataAccess.getData({
							url: "autoCompleteGeoAjax",
							data: {geoId: province.val(),
									geoTypeId: "DISTRICT"},
							source: "listGeo"});
				renderDistrict(data, district, callback);
			});
		};
		var renderDistrict = function(data, element, callback) {
			var option = "<option value=''>" + uiLabelMap.BEChooseDistrict + "</option>";
			for ( var x in data) {
				option += "<option value='" + data[x].geoId + "'>" + data[x].geoName + "</option>";
			}
			element.html(option);
			if(callback && typeof(callback) == 'function'){
				callback();
			}
		};
		return {
			init: init
		}
	})(jQuery);
}