if (typeof (ModuleDiagram) == "undefined") {
	var ModuleDiagram = (function() {
		var init = function() {
			DataAccess.executeAsync({
				url: "loadModuleDiagram",
				data: {applicationId: moduleId}
				}, ModuleDiagram.render);
		};
		var render = function(data) {
			if (!_.isEmpty(data)) {
				data = _.filter(data.diagram, function(obj){
					obj.parent = obj.parent == null?"#":obj.parent;
					return obj;
				});
				$("#digModuleDiagram").jstree({
					core: {
						data: data
					}
				}).bind("ready.jstree", function (event, data) { 
					data.instance._open_to(moduleId);
					$.jstree.reference(this).select_node(moduleId);
				});
			}
		};
		return {
			init: init,
			render: render
		}
	})();
}