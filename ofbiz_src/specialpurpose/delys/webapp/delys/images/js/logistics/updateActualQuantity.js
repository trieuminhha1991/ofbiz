$(document).ready(function() {
	function init() {
		var list = $("input[name^='lackQuantity']");
		for (var x = 0; x < list.length; x++) {
			(function(x) {
				var cur = $(list[x]);
				cur.change(function() {
					update(cur, cur.val(), "lackQuantity");
				});
				;
			})(x);
		}

		var list = $("input[name^='quantityRejected']");
		for (var x = 0; x < list.length; x++) {
			(function(x) {
				var cur = $(list[x]);
				cur.change(function() {
					update2(cur, cur.val(), "quantityRejected");
				});
				;
			})(x);
		}
	}

	function update(obj, val, inputid) {
		var id = "actualQuantity";
		var lackQuantity = $("input[name='lackQuantity']");
		var td = obj.parent().siblings();
		if (td.length > 13) {
			var total = processNumber($(td[4]).text());
			var test = processNumber($(td[7]).text());
			var sample = processNumber($(td[8]).text());
			var inspect = processNumber($(td[9]).text());
			var name = obj.attr("name");
			var end = name.substring(inputid.length, (name.length));
			var rejected = processNumber($("input[name='quantityRejected" + end + "']").val());
			var cal = total - test - sample - inspect - parseInt(val) - rejected;
			$("input[name='" + id + end + "']").val(cal);
		}
	}

	function update2(obj, val, inputid) {
		var id = "actualQuantity";
		var lackQuantity = $("input[name='quantityRejected']");
		var td = obj.parent().siblings();
		if (td.length > 13) {
			var total = processNumber($(td[4]).text());
			var test = processNumber($(td[7]).text());
			var sample = processNumber($(td[8]).text());
			var inspect = processNumber($(td[9]).text());
			var name = obj.attr("name");
			var end = name.substring(inputid.length, (name.length));
			var lack = processNumber($("input[name='lackQuantity" + end + "']").val());
			var cal = total - test - sample - inspect - parseInt(val) - lack;
			$("input[name='" + id + end + "']").val(cal);
		}
	}

	function processNumber(text) {
		if (text) {
			var val = text.replace(',', '');
			val = val.replace('.', '');
			return parseInt(val);
		}
		return 0;
	}

	function renderHtml(data, key, value, id) {
		var y = "";
		var out = $("select[name='" + id + "']");
		for (var x in data) {
			y += "<option value='" + data[x][key] + "'>";
			y += data[x][value] + "</option>";
			if (x == 0) {
				out.val(data[x][key]);
			}
		}
		out.html(y);
	}

	init();
}); 