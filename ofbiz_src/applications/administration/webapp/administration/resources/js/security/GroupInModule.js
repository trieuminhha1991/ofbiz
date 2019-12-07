if (typeof (GroupInModule) == "undefined") {
	var GroupInModule = (function() {
		var mainGrid;
		var initJqxElements = function() {
			$("#txtGroupPermissions").jqxComboBox({ source: AdministrationConfig.SecurityPermission.array, multiSelect: true, width: "99%", height: 25, theme: theme });
		};
		var setPermission = function(item) {
			$("#txtGroupPermissions").jqxComboBox("selectItem", item);
		};
		var handleEvents = function() {
			$("#txtGroupPermissions").on("change", function (event) {
				GroupInModule.reloadGrid();
			});
		};
		var getPermissions = function() {
			return _.pluck($("#txtGroupPermissions").jqxComboBox("getSelectedItems"), "value");
		};
		var reloadGrid = function() {
			var adapter = mainGrid.jqxGrid("source");
			if(adapter){
				adapter.url = "jqxGeneralServicer?sname=JQGetListGroupInModule&applicationId=" + moduleId + "&permissionId=" + GroupInModule.getPermissions();
				adapter._source.url = adapter.url;
				mainGrid.jqxGrid("source", adapter);
			}
		};
		return {
			init: function() {
				mainGrid = $("#jqxgridGroupInModule");
				initJqxElements();
				handleEvents();
				AddGroupToModule.init();
			},
			getPermissions: getPermissions,
			reloadGrid: reloadGrid
		}
	})();
}