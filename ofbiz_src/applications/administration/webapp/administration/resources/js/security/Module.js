$(document).ready(function() {
	Module.init();
});
if (typeof (Module) == "undefined") {
	var Module = (function() {
		var mainGrid;
		var initJqxElements = function() {
			var source =
			{
				dataType: "json",
				dataFields: [
					{ name: "applicationId", type: "string" },
					{ name: "applicationType", type: "string" },
					{ name: "application", type: "string" },
					{ name: "name", type: "string" },
					{ name: "permissionId", type: "string" },
					{ name: "moduleId", type: "string" }
				],
				updateRow: function (rowID, rowData, commit) {
					commit(DataAccess.execute({
						url: "updateOlbiusApplication",
						data: rowData},
						Module.notify));
	            },
				timeout: 10000,
				hierarchy:
				{
					keyDataField: { name: "applicationId" },
					parentDataField: { name: "moduleId" }
				},
				id: "applicationId",
				root: "value",
				url: "loadApplicationModuleTree?moduleId=" + moduleId
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			mainGrid.jqxTreeGrid({
				source: dataAdapter,
				localization: getLocalization(),
				width: "100%",
				sortable: true,
				theme: "olbius",
				columnsResize: true,
				columnsReorder: true,
				pageable: true,
				pageSize: 10,
				pageSizeOptions: ["5", "10", "15"],
				editable: true,
				pagerMode: "advanced",
				columns: [
					{ text: multiLang.ADApplicationId, dataField: "applicationId", width: 300, editable: false,
						cellsrenderer: function(row, column, value, a, b, data){
							var link = 'ModuleDetail?moduleId=' + value;
							return '<a href=\"' + link + '\">' + value + '</a>';
						}
					},
					{ text: multiLang.ADApplication, dataField: "application", width: 250, editable: false },
					{ text: multiLang.ADApplicationName, dataField: "name", minwidth: 300,
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: multiLang.fieldRequired };
						}
					},
					{ text: multiLang.ADPermissionDefault, dataField: "permissionId", width: 150, editable: false }
				]
			});
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true });
		};
		var handleEvents = function() {
			
		};
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(multiLang.updateError);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
			}
		};
		return {
			init: function() {
				mainGrid = $("#treeGridModule");
				initJqxElements();
				handleEvents();
			},
			notify: notify
		}
	})();
}