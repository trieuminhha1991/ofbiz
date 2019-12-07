if (typeof (UserInModule) == "undefined") {
	var UserInModule = (function() {
		var mainGrid;
		var initJqxElements = function() {
			$("#txtPermissions").jqxComboBox({ source: AdministrationConfig.SecurityPermission.array, multiSelect: true, width: "99%", height: 25, theme: theme });
		};
		var setPermission = function(item) {
			$("#txtPermissions").jqxComboBox("selectItem", item);
		};
		var handleEvents = function() {
			$("#txtPermissions").on("change", function (event) {
				UserInModule.reloadGrid();
			});
		};
		var getPermissions = function() {
			return _.pluck($("#txtPermissions").jqxComboBox("getSelectedItems"), "value");
		};
		var reloadGrid = function() {
			var adapter = mainGrid.jqxGrid("source");
			if(adapter){
				adapter.url = "jqxGeneralServicer?sname=JQGetListUserLoginInModule&applicationId=" + moduleId + "&permissionId=" + UserInModule.getPermissions();
				adapter._source.url = adapter.url;
				mainGrid.jqxGrid("source", adapter);
			}
		};
		return {
			init: function() {
				mainGrid = $("#jqxgridUserInModule");
				initJqxElements();
				handleEvents();
				AddUserToModule.init();
				AddPermission.init();
			},
			getPermissions: getPermissions,
			reloadGrid: reloadGrid
		}
	})();
}