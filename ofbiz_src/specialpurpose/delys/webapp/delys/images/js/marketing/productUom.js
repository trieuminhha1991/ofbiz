var productChosen = [];
(function() {
	/*for list product chosen & get product chosen detail*/
	$(document).ready(function() {
		// $("#productSampling").chosen();
		initProductChosen();
		$("#addProduct").click(function() {
			var productType = $("#productSampling");
			var id = productType.val();
			var name = productType.find(":selected").text();
			if(!$(".address-row").length){
				alert(uiLabelMap.addOneAddress);
				return false;
			}
			addProduct({
				id : id,
				name : name
			});
		});
		if (prdS) {
			initProductData(prdS);
		}
	});
	function initProductData(data) {
		var productId = "";
		var form = $("#product-form");
		var currentPr = "";
		var currentCtm = "";
		
		for (var y in data) {
			var obj = data[y];
			var id = "";
			if (obj.productTypeId == "MARKETING_SPL") {
				id = "productSampling";
			} else {
				id = "productSelling";
			}
			
			if(!currentPr || currentPr != obj.productId){
				currentPr = obj.productId;
				currentCtm = obj.contactMechId;
				addProduct({
					id : obj.productId,
					name : obj.productName
				});
				addAddress(obj.productId, {
					address: obj.address1,
					quantity: obj.quantity,
					uomId: obj.uomId,
					productTypeId: obj.productTypeId
				});
				
			}else if(!currentCtm || currentCtm != obj.contactMechId){
				currentCtm = obj.contactMechId;
				addAddress(obj.productId, {
					address: obj.address1,
					quantity: obj.quantity,
					uomId: obj.uomId,
					productTypeId: obj.productTypeId
				});	
			}else{
				(function(){
					var tmp = $("#product-container"+obj.productId + " ."+ id + "Container");
					var len = parseInt($("#product-container"+obj.productId + " ."+ id + "Container").length) - 1;
					setTimeout(function(){
						addProductType(id, $("#"+id+obj.productId + len + " button"), obj.productId, {
							quantity: obj.quantity,
							uomId: obj.uomId
						});	
					}, 50);
				})();
			};
		}
	}

	function initProductChosen() {
		var productInput = $("#productSampling");
		productInput.val('');
		productInput.chosen();
	}
	/*add product to container*/
	function addProduct(type) {
		var form = $("#product-form");
		var id = type.id;
		// var current = $(".product-row");
		/*check if product has been added*/
		for (var x in productChosen) {
			var obj = productChosen[x];
			if (obj == id) {
				hint($('#product-row'+id));
				return;
			}
		}
		productChosen.push(id);
		if (!addRowLabel) {
			var addRowLabel = "Add new";
		}
		renderProductContainer(form, id, type.name);
		// addProductRow("productSelling" + id);
		// addProductRow("productSampling" + id);
	}
	/*render product container*/
	function renderProductContainer(form, id, name) {
		var tmp = $("input[name='address']");
		var addr = new Array();
		for (var x = 0; x < tmp.length; x++) {
			var val = $(tmp[x]).val();
			if (val) {
				addr.push(val);
			}
		}
		if (!addr.length) {
			return;
		}
		var str = "<div class='row-al paddingtop-10 product-row row-mk' id='product-row"+id+"'>" 
			+ "<div class='col-al3 aligncenter' id='title" + id + "'>" 
			+ "<div class='title-inner'>" + name + "</div>" 
			+ "</div>" 
			+ "<div class='col-al9'>" 
			+ "<div class='row-al'>" 
			+ "<div class='col-al12 product-container' id='product-container"+id+"' data-id='" + id + "'></div>" 
			+ "</div>"
			+ "<div class='row-al paddingtop-10'>" 
			// + "<div class='col-al12'>" + "<button class='add-new-row' id='address-product" + id + "'>+</button>" + "</div>" 
			+ "</div>" 
			+ "</div>";
		"</div>";
		form.append(str);
		addAddress(id);
		// $("#address-product" + id).click(function() {
			// addAddress(id);
		// });
		return str;
	}
	/* add places sampling for each product */
	function addAddress(id, value) {
		var addChosen = $("#product-container" + id + " .addressChosen");
		var tmp = $("input[name='address']");
		var addr = new Array();
		for (var x = 0; x < tmp.length; x++) {
			var val = $(tmp[x]).val();
			if (val) {
				addr.push(val);
			}
		}
		for (var y = 0; y < addChosen.length; y++) {
			var val = $(addChosen[y]).val();
			var ind = addr.indexOf(val);
			// console.log(ind);
			if (ind > -1) {
				addr.splice(ind, 1);
			}
		}
		var len1 = $("#product-container" + id + " .productSamplingContainer").length;
		var len2 = $("#product-container" + id + " .productSellingContainer").length;
		if (addr.length) {
			var str = "<div class='row-al borderleft cost-row'>"
					+"<div class='col-al4 paddingtop-5'>";
			str += "<select class='fullwidth addressChosen'>";
			for (var j in addr) {
				str += "<option value='"+addr[j]+"'>" + addr[j] + "</option>";
			}
			str += "</select>"
				+"</div>"
				+"<div class='col-al4 borderleft productSamplingContainer' id='productSampling"+id+len1+"' data-id='"+id+"'>"
				+"<div class='row-al productSampling'>"
				+"<div class='col-al6'>"
				+"<input type='number' name='quantity' class='fullwidth productSamplingInput' ";
			if(value && value.productTypeId == "MARKETING_SPL"){
				str += "value='" + value.quantity +  "'";
			}
			var uom = value && value.uomId ? value.uomId : "";
			str += "/>"
				+"</div>"
				+"<div class='col-al6 paddingtop-5'>"
				+ renderUom(uom)
				+"</div>"
				+"</div>"
				// +"<button class='add-new-row'>+</button>"
				+"</div>"
				+"<div class='col-al4 borderleft productSellingContainer' id='productSelling"+id+len2+"'>"
				+"<div class='row-al productSelling'>"
				+"<div class='col-al6'>"
				+"<input type='number' name='quantity' class='fullwidth productSellingInput'/>"
				+"</div>"
				+"<div class='col-al6 paddingtop-5'>"
				+ renderUom()
				+"</div>"
				+"</div>"
				// +"<button class='add-new-row'>+</button>"
				+"</div>"
				+"</div>";
			var row = $("#product-container"+id);
			row.append(str);
			if(value && value.address){
				row.find(".addressChosen").val(value.address);	
			}
			var prspl = $("#productSampling"+id+len1);
			prspl.find("button").click(function(){
				addProductType("productSampling", $(this), id);
			});
			var prsl = $("#productSelling"+id+len1); 
			prsl.find("button").click(function(){
				addProductType("productSelling", $(this), id);
			});
			initHotKey(prspl.find("input.productSamplingInput"), prspl, "productSampling");
			initHotKey(prsl.find("input.productSellingInput"), prsl, "productSelling");
			reloadHeight(id);
		}
	}
	function initHotKey(obj, parent, type){
		/*add product type*/
		obj.bind("keydown","Shift+return", function(e){
			var par = $(this).parents(".product-container");
			addProductType(type, parent, par.data("id"));
		});
		/*remove product type*/
		obj.bind("keydown","ctrl+d", function(e){
			e.preventDefault();
			var par = $(this).parents("." + type);
			var id = $(this).parents(".product-container").data('id');
			if(id){
				reloadHeight(id);
			}
			var grnpar =  $(this).parents("." + type+"Container");
			if(grnpar.children().length > 1){
				par.remove();	
			}
		});
		/*add new product row for adddress*/
		obj.bind("keydown","ctrl+shift+return", function(e){
			var par = $(this).parents(".product-container");
			addAddress(par.data("id"));
		});
		/*remove current product row*/
		obj.bind("keydown","ctrl+shift+d", function(e){
			var par = $(this).parents(".cost-row");
			var id = $(this).parents(".product-container").data('id');
			if(id){
				reloadHeight(id);
			}
			removeProductRow(par);
		});
	}
	function removeProductRow(obj) {
		if (obj) {
			var par = obj.parents(".product-container");
			var gr = obj.parents(".product-row");
			var id = par.data("id");
			if(par.children().length == 1){
				gr.remove();
				for(var x in productChosen){
					if(productChosen[x] == id){
						productChosen.splice(x, 1);
						return;
					}
				}
			}else{
				obj.remove();
			}
		}
	}
	function reloadHeight(id){
		var height = $("#product-row" + id).height();
		var title = $("#title" + id);
		title.height(height);
	}
	/*add product type for each product: sampling or selling*/
	function addProductType(type, obj, productId, value){
		var uom = value && value.uomId ? value.uomId : "";
		var str = "<div class='row-al "+type+"'>"
				+"<div class='col-al6'>"
				+"<input type='number' name='quantity' class='fullwidth " +type+ "Input' ";
		if(value && value.quantity){
			str += "value='" + value.quantity + "'";
		}
		str += "/>"
			+"</div>"
			+"<div class='col-al6 paddingtop-5'>"
			+ renderUom(uom)
			+"</div>"
			+"</div>";
		obj.append(str);
		reloadHeight(productId);
		var tmp = obj.find("."+type).last();
		initHotKey(tmp.find("input."+type+"Input"), obj, type);
	}
	function renderUom(value){
		var str = "<select name='uom' class='fullwidth'>";
		if(listUom){
			for(var x in listUom){
				str += "<option value='"+listUom[x].uomId+"' ";
				if(value && value==listUom[x].uomId){
					str += "selected";		
				} 
				str += ">" + listUom[x].description + "</option>";
			}	
		}
		str += "</select>";
		return str;
	}
	/*add new product row to product list*/
	function addProductRow(id, obj) {
		var len = $("#" + id).children().length;
		if (!len) {
			len = 0;
		}
		var end = len + 1;
		var res = getProductRow(id, end, obj);
		$("#" + id).append(res);
		$("#remove-" + id + end).click(function() {
			removeRow($(this).parent());
		});
		// var height = $("#" + id).height();
		// var title = $("#title" + id);
		// title.height(height);
	}

	/*get product row value*/
	function getProductRow(id, seq, obj) {
		var quantity = obj && obj.quantity ? obj.quantity : 0;
		var uom = obj && obj.uom ? obj.uom : "";
		var str = "<div class='row-al row-product-" + id + "'>" + "<div class='col-al6 borderleft'><input class='fullwidth' type='number' name='quantity' value='" + quantity + "'/></div>" + "<div class='col-al6'><select name='uom' class='fullwidth' value='" + uom + "'>";
		if (listUom) {
			for (var x in listUom) {
				str += "<option value='" + listUom[x].uomId + "'>" + listUom[x].description + "</option>";

			}
		}
		str += "</select></div>";
		str += "<div class='remove-row' id='remove-" + id + seq + "'>x</div>";
		str += "</div>";
		return str;
	}

	

})();
function getProductList() {
	if (productChosen.length) {
		var id = "";
		var products = $(".product-container");
		var res = new Array();
		for (var x = 0; x < products.length; x++) {
			var con = $(products[x]);
			var list = con.find(".productSampling");
			var list2 = con.find(".productSelling");
			var geo = con.find(".addressChosen");
			id = con.data("id");
			var cur = [];
			for (var y = 0; y < list.length; y++) {
				var row = $(list[y]);
				var q1 = row.find("input[name='quantity']").val();
				if (q1) {
					var obj = {
						quantity : q1,
						uom : row.find("select[name='uom']").val(),
						type : "MARKETING_SPL"
					};
					cur.push(obj);
				}
			}
			for (var z = 0; z < list.length; z++) {
				var row = $(list2[z]);
				var q = row.find("input[name='quantity']").val();
				if (q) {
					var obj = {
						quantity : q,
						uom : row.find("select[name='uom']").val(),
						type : "MARKETING_SL"
					};
					cur.push(obj);
				}
			}

			if (cur.length && geo.val()) {
				res.push({
					id : id,
					content : cur,
					geoId: geo.val()
				});
			}
		}
		return res;
	}
}