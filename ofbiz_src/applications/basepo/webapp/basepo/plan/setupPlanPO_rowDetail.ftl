<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id', 'jqxIndices' + index);
	
	SetupSupporter.load(grid, datarecord.productId);
	
}"/>