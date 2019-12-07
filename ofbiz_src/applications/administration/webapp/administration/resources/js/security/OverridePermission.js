if (typeof (OverridePermission) == "undefined") {
	var OverridePermission = (function() {
		var jqxwindow, jqxtabs, jqxgrid1, jqxgrid2;
		var initJqxElements = function() {
			String.prototype = $.extend(String.prototype, {
				viewOverridePermission: function() {
					var data = JSON.parse(this.toString());
//					var str = "<div class='cell-custom-grid'><a style='cursor: pointer;' title='" + multiLang.ADOverridePermission + "' href='javascript:OverridePermission.open(" + '\"' + data.applicationId + '\", \"' + data.permissionId + '\"' + ")'>" + data.permissionId +  "</a></div>";
					return data.permissionId;
				}
			});
			jqxwindow.jqxWindow({
				theme: theme, width: 950, maxWidth: 1000, height: 485, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelOverridePermission"), modalOpacity: 0.7
			});
			jqxtabs.jqxTabs({ width: "100%", height: 250, position: "top"});
			
			var datafield = [
				{ name: "applicationId", type: "string" },
				{ name: "applicationType", type: "string" },
				{ name: "application", type: "string" },
				{ name: "name", type: "string" },
				{ name: "permissionId", type: "string" },
				{ name: "moduleId", type: "string" }
			];
			var columnlist = [
				{ text: multiLang.ADApplicationId, datafield: "applicationId", width: 250,
					cellsrenderer: function(row, column, value, a, b, data){
						var link = "ModuleDetail?moduleId=" + value;
						return "<a href=\"" + link + "\">" + value + "</a>";
					}
				},
				{ text: multiLang.ADApplication, dataField: "application", width: 250 },
				{ text: multiLang.ADApplicationName, dataField: "name", minwidth: 300 },
				{ text: multiLang.ADPermissionDefault, dataField: "permissionId", width: 130 }
			];
			var config = {
				showfilterrow: true,
				filterable: true,
				editable: false,
				width: "100%",
				height: 250,
				pageable: true,
				sortable: true,
				virtualmode : true,
				autoheight: true,
				selectionmode: "singlerow",
				url: "",
				source: {
					pagesize: 10
				}
			};
			Grid.initGrid(config, datafield, columnlist, null, jqxgrid1);
			
			columnlist = [
				{ text: multiLang.ADActionId, datafield: 'applicationId', width: 250 },
				{ text: multiLang.ADAction, dataField: 'application', width: 250 },
				{ text: multiLang.ADActionName, dataField: 'name', minwidth: 300 },
				{ text: multiLang.ADPermissionDefault, dataField: 'permissionId', width: 130 }
			];
			Grid.initGrid(config, datafield, columnlist, null, jqxgrid2);
		};
		var loadGrid = function(applicationId, permissionId) {
			var adapter = jqxgrid1.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListApplicationOverride&applicationType=MODULE&applicationId=" + applicationId + "&permissionId=" + permissionId;
				adapter._source.url = adapter.url;
				jqxgrid1.jqxGrid("source", adapter);
			}
			
			var adapter = jqxgrid2.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListApplicationOverride&applicationType=ENTITY&applicationId=" + applicationId + "&permissionId=" + permissionId;
				adapter._source.url = adapter.url;
				jqxgrid2.jqxGrid("source", adapter);
			}
		};
		var open = function(applicationId, permissionId) {
			if (applicationId, permissionId) {
				loadGrid(applicationId, permissionId);
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowOverridePermission");
				jqxtabs = $("#jqxTabsOverridePermission");
				jqxgrid1 = $("#jqxgrid1OverridePermission");
				jqxgrid2 = $("#jqxgrid2OverridePermission");
				initJqxElements();
			},
			open: open
		};
	})();
}