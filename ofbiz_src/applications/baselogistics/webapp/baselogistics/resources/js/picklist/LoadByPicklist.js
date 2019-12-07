var LoadByPicklist = (function() {
	var load = function(picklistId) {
		var data = DataAccess.getData({
			url: "loadPicklistInfo",
			data: {picklistId: picklistId},
			source: "info"});
		setValue(data);
	};
	var setValue = function(data) {
		if (!_.isEmpty(data)) {
			if (data.facility) {
				Grid.setDropDownValue($("#txtPickingFacility"), data.facility.facilityId, data.facility.facilityName);
				$("#txtPickingFacility").trigger("close");
			}
			if (data.orderIds) {
				CreatePicklist.setOrderIds(data.orderIds);
				Grid.initTooltipDropdown($("#txtPickingOrder"), data.orderIds.length + " " + multiLang.BSOrder);
			}
			if (data.facility && data.facility.facilityId) {
				var adapter = mainGrid.jqxGrid("source");
				if (adapter) {
					adapter.url = "jqxGeneralServicer?sname=JQGetListPickingItemTempData&facilityId=" + data.facility.facilityId;
					adapter._source.url = adapter.url;
					mainGrid.jqxGrid("source", adapter);
				}
			}
			$("#txtPickingFacility").jqxDropDownButton({ disabled: true });
			$("#txtPickingOrder").jqxDropDownButton({ disabled: true });
			$("#btnUpload").hide();
		}
	};
	return {
		load: load
	}
})();