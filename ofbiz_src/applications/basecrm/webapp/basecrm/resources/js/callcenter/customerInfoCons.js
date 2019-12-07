if (typeof (CustomerInfoCons) == "undefined") {
	var CustomerInfoCons = (function() {
		var setValue = function(data) {
			clear();
			if (!_.isEmpty(data)) {
				if (data.birthDate) {
					$("#txtBirthDateCons").html(new Date(data.birthDate).toTimeOlbius() + " - " + Family.getPersonAge(data.birthDate));
				}
				$("#txtDescriptionCons").val(data.description);
			}
		};
		var getValue = function() {
			var value = {
				description: $("#txtDescriptionCons").val()
			};
			return value;
		};
		var clear = function() {
			$("#txtBirthDateCons").html("");
			$("#txtDescriptionCons").val("");
		};
		return {
			setValue: setValue,
			getValue: getValue,
			clear: clear
		}
	})();
}