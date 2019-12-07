if (typeof (ConfigCategory) == "undefined") {
	var ConfigCategory = (function() {
		var initJqxElements = function() {
			$("#txtProductId").jqxDropDownList({
				theme : theme,
				width : 420,
				source : listProduct,
				displayMember : "productName",
				valueMember : "productId",
				placeHolder : multiLang.BSSelectProduct,
				dropDownHeight : 250,
				filterable : true
			});
		};
		var handleEvents = function() {
			$("#txtProductId")
					.on(
							"change",
							function(event) {
								var args = event.args;
								if (args) {
									var index = args.index;
									var item = args.item;
									var label = item.label;
									var value = item.value;
									if (value !== productIdParam) {
										window.location.href = "ConfigProductAndCategories?productId="
												+ value;
									}
								}
							});
		};
		var fixme = function() {
			return DataAccess.execute({
				url : "fixCategory",
				data : {}
			});
		};
		var setValue = function(data) {
			$("#txtProductId").jqxDropDownList("val", data.productId);
			setTimeout(
					function() {
						var url = "jqxGeneralServicer?sname=JQListCategoriesOfProduct&productId="
								+ data.productId;
						var adapter = $("#productAndCategory")
								.jqxGrid("source");
						if (adapter) {
							adapter.url = url;
							adapter._source.url = url;
							$("#productAndCategory").jqxGrid("source", adapter);
						}
					}, 100);
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			fixme : fixme,
			setValue : setValue
		}
	})();
}