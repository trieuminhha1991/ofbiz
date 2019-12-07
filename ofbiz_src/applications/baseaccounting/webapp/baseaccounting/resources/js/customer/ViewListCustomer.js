var viewListCustomerObj = (function(){
	var init = function(){
		initEvent();
	};
	var initEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			accutils.createJqxDropDownList($("#customerStatus"), [{statusId: 'active', description: 'Active'}, {statusId: 'inactive', description: 'Inactive'}], 
					{valueMember: 'statusId', displayMember: 'description', width: 140, height: 25});
			$("#customerStatus").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var source = $("#jqxgrid").jqxGrid('source');
					source._source.url = 'jqxGeneralServicer?sname=JQGetListCustomer&statusId=' + value;
					$("#jqxgrid").jqxGrid('source', source);
				}
			});
			$("#customerStatus").jqxDropDownList('selectItem', 'active');
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	viewListCustomerObj.init();
});