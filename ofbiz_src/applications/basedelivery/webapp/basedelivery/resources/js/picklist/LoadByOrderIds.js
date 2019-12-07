var LoadByOrderIds = (function() {
	var load = function(facilityId, facilityName, orderIds) {
		if (facilityId) {
			Grid.setDropDownValue($("#txtPickingFacility"), facilityId, facilityName);
			$("#txtPickingFacility").trigger("close");
		}
		if (orderIds) {
			CreatePicklist.setOrderIds(orderIds);
			Grid.initTooltipDropdown($("#txtPickingOrder"), orderIds.length + " " + multiLang.BSOrder);
			$("#txtPickingOrder").jqxDropDownButton({ disabled: true });
		}
	};
	return {
		load: load
	}
})();