$(function(){
	if (typeof(dataSelected) == "undefined") var dataSelected = [];
	OlbQuotationConfirm.init();
});
var OlbQuotationConfirm = (function(){
	var init = function(){
		initElementComplex();
	};
	var initElementComplex = function(){
		var configProductList = {
			showdefaultloadelement: true,
			autoshowloadelement: true,
			dropDownHorizontalAlignment: 'right',
			datafields: dataFieldItemsProdConfirm,
			columns: columnListItemsProdConfirm,
			//columngroups: columngrouplistProductItems,
			groupable: false,
			useUrl: false,
			editable: false,
			filterable: true,
			showfilterrow: true,
			pagesize: 15,
			pageable: true,
			showtoolbar: false,
			showgroupsheader: false,
			width: '100%',
			showtoolbar: false,
			bindresize: false,
			virtualmode: false,
			bindresize: true,
		};
		new OlbGrid($("#jqxgridProdSelected"), dataSelected, configProductList, []);
	};
	return {
		init: init
	};
}());