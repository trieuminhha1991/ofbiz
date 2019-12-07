$(document).ready(function(){
	onLoad();
	var historyProductItem = {};
	var historyProductItemF = {};
	
	function bindProductId() {
		bindProductIdAjax(
				{facilityId: facilityId ,}
				, 'getProductId' , 'listProductId', 'productId', 'productId', 'ImportProductId', -1);
	}
	function bindProductIdAjax(jsonObject, url, data, key, value, id, index) {
		var ListProductByInventoryItemId = [];
		jQuery.ajax({
			url : url,
			type : "POST",
			data : jsonObject,
			success : function(res) {
				ListProductByInventoryItemId = res[data];
			}
		}).done(function() {
			console.log(ListProductByInventoryItemId);
			renderByJqx(ListProductByInventoryItemId, key, value, id, index);
		});
	}
	
	function renderByJqx(data, key, value, id, index) {
		$("#" + id).jqxDropDownList({
			   autoOpen: true,   
			   source: data,
		       theme: 'classic',
		       width: '200px',
		       height: '25px',
		       selectedIndex: index,
		       displayMember: value,
		       valueMember: key,
		       searchMode:'startswithignorecase',
		       autoDropDownHeight: true,
		       enableHover :true,
		       itemHeight: 35,
		       placeHolder: "Select"
		 });
	}
	function onLoad() {
		bindProductId();
	}
	
});


