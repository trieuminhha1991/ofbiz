function reponsiveRowDetails(grid, parentElement) {
	$(window).bind("resize", function() {
		$(grid).jqxGrid({
			width : "96%"
		});
	});
	$("#sidebar").bind("resize", function() {
		$(grid).jqxGrid({
			width : "96%"
		});
	});
}

function createSupplierProduct() {
	$("#alterpopupWindowAddSupplierProduct").jqxWindow("open");
}
