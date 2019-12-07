var costChosen = [];
(function() {
	/*for list cost chosen & get cost chosen detail*/
	$(document).ready(function() {
		$("#addCost").click(function() {
			var costType = $("#costTypeList");
			var id = costType.val();
			var name = costType.find(":selected").text();
			addCost({
				id : id,
				name : name
			});

		});
		initCostChosen();
		if (costs) {
			initCostData(costs);
		}
	});
	function initCostChosen() {
		var productInput = $("#costTypeList");
		productInput.val('');
		productInput.chosen();
	}

	function initCostData(data) {
		var currentType = "";
		var form = $("#cost-form");
		for (var x in data) {
			var obj = data[x];
			if (currentType != obj.marketingCostTypeId) {
				currentType = obj.marketingCostTypeId;
				var str = "<div class='row-al paddingtop-10 cost-row' data-id='" + currentType + "'>" + "<div class='col-al4 aligncenter' id='title" + currentType + "'><div class='title-inner'>" + obj.name + "</div></div>" + "<div class='col-al8'><div class='row-al'><div class='col-al12' id='" + currentType + "'></div>" + "<div class='col-al12 aligncenter add-new-container'><button class='add-new-row' id='action" + currentType + "'>+</button></div></div></div>" + "</div>";
				form.append(str);
			}
			addCostRow(currentType, {
				quantity : obj.quantity,
				price : obj.unitPrice,
				description : obj.description
			});
		}
	}

	function addCost(type) {
		var form = $("#cost-form");
		var id = type.id;
		var current = $(".cost-row");
		for (var x = 0; x < current.length; x++) {
			var obj = $(current[x]).data("id");
			if (obj == id) {
				return;
			}
		}
		costChosen.push(id);
		if (!addRowLabel) {
			var addRowLabel = "Add new";
		}
		var str = "<div class='row-al paddingtop-10 cost-row' data-id='" + id + "'>";
		str += "<div class='col-al4 aligncenter' id='title" + id + "'><div class='title-inner'>" + type.name + "</div></div>" + "<div class='col-al8'><div class='row-al'><div class='col-al12' id='" + id + "'></div>"
			// + "<div class='col-al12 aligncenter add-new-container'>"
			// +"<button class='add-new-row' id='action" + id + "'>+</button></div>"
			+"</div></div></div>";
		form.append(str);
		// $("#action" + id).click(function() {
			// addCostRow(id);
		// });
		addCostRow(id);
	}

	/*add new cost row to cost list*/
	function addCostRow(id, obj) {
		var len = $("#" + id).children().length;
		if (!len) {
			len = 0;
		}
		var end = len + 1;
		var res = getCostRow(id, end, obj);
		$("#" + id).append(res);
		bindInput(id, end);
		var height = $("#" + id).height();
		var title = $("#title" + id);
		title.height(height);		
	}

	/*get cost row value*/
	function getCostRow(id, seq, obj) {
		var quan = obj && obj.quantity ? obj.quantity : "";
		var price = obj && obj.price ? obj.price : "";
		var description = obj && obj.description ? obj.description : "";
		var total = quan && price ? parseInt(quan) * parseFloat(price) : 0;
		var str = "<div class='row-al cost-row-content row-cost-" + id + "' id='" + id + seq + "'>" + "<div class='col-al4 borderleft'><input class='fullwidth' type='text' name='description' value='" + description + "'/></div>";
		str += "<div class='col-al2 borderleft'><input class='fullwidth' type='text' name='quantity' value='" + quan + "' placeholder='0'/></div>"
			+ "<div class='col-al3 borderleft'><input class='fullwidth' type='text' name='price' value='" + price + "' placeholder='0'/>"
			+"<input type='hidden' name='price'/></div>"
			+ "<div class='col-al3 borderleft' id='total" + id + "'>" + "<input class='fullwidth' type='text' name='total' disabled  value='" + total + "'/>" + "</div>";
		// str += "<div class='remove-row' onclick=\"removeRow('" + id + seq + "')\">x</div>";
		str += "</div>";
		return str;
	}

	/*bind input change*/
	function bindInput(id, seq) {
		var obj = $("#" + id + seq);
		var des = obj.find("input[name='description']");
		var quan = obj.find("input[name='quantity']");
		var price = obj.find("input[name='price'][type='text']");
		var total = obj.find("input[name='total']");	
		quan.on("change", function(){
			var val = $(this).val();
			if(isNaN(val)){
				$(this).val("");
			}
			var num = Utils.currencyToNumber(price.val(),currency) * parseInt(quan.val());
			if (!num) {
				num = 0;
			}
			total.val(Utils.formatcurrency(num));
		});
		price.on("change", function(){
			var sb = $(this).siblings();
			var val = $(this).val();
			var tmp = Utils.currencyToNumber(val, currency);
			var cur = Utils.formatcurrency(tmp, currency);
			if(isNaN(tmp)){
				$(this).val("");	
			}else{
				$(this).val(cur);	
			}
			sb.val(tmp);
			var num = parseFloat(tmp) * parseInt(quan.val());
			if (!num) {
				num = 0;
			}
			total.val(Utils.formatcurrency(num));
		});
		price.on("keydown",function() {
			if($(this).hasClass("active")){
				$(this).removeClass("active");
				$(this).val("");
			}
			var val = $(this).val();
		});
		initHotKey(des, id);
		initHotKey(quan, id);
		initHotKey(price, id);
	}
	function initHotKey(obj, id){
		obj.bind("keydown","Shift+return", function(e){
			e.preventDefault();
			addCostRow(id);
		});
		obj.bind("keydown","ctrl+d", function(e){
			e.preventDefault();
			console.log($(obj).parents(".cost-row-content"));
			removeRow($(obj).parents(".cost-row-content"), id);
		});
	}
	function reloadHeight(id){
		var height = $("#" + id).height();
		var title = $("#title" + id);
		title.height(height);
	}
	function removeRow(obj, id) {
		if (obj && obj.length) {
			obj.remove();
			reloadHeight(id);
		}
	}
})();
function getCostList() {
	if (costChosen.length) {
		var id = "";
		var costs = [];
		for (var x in costChosen) {
			id = costChosen[x];
			var list = $(".row-cost-" + id);
			var cur = [];
			for (var y = 0; y < list.length; y++) {
				(function() {
					var row = $(list[y]);
					var obj = {
						description : row.find("input[name='description']").val(),
						quantity : row.find("input[name='quantity']").val(),
						unitPrice : row.find("input[name='price']").val()
					};
					cur.push(obj);
				})(y);
			}
			if (cur.length) {
				costs.push({
					id : id,
					content : cur
				});
			}
		}
		return costs;
	}
}