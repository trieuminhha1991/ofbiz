<div id="jqxwindowRoutes" style="display:none;">
	<div>${uiLabelMap.BSViewListRoutesAssigned}</div>
	<div>
		<div style="height: 405px;overflow-y: auto;">
			<div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.Employee}:</div> <div class="salesmanInfo jqxwindowTitle" style="display: inline-block;"></div></div>
			<#assign urlRoutes = ""/>
			<#assign customLoadFunction = "true"/>
			<#include "../listRoutesAssigned.ftl"/>
		</div>
		
		<div class="form-action row-fluid">
			<div class="span12">
				<button id="btnCloseRoutes" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		ListRoutes.init();
		initGridjqxgridRoutes();
	});
	var ListRoutes = (function() {
		var initJqxElements = function() {
			$("#jqxwindowRoutes").jqxWindow({
				theme: "olbius", maxWidth: 1000, width: 1000, height: 560, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#btnCloseRoutes"), modalOpacity: 0.7
			});
		};
		var open = function(partyId) {
			var adapter = $("#jqxgridRoutes").jqxGrid('source');
			if(adapter){
				adapter.url = "jqxGeneralServicer?sname=JQGetListRoutes&distinct=Y&partyId=" + partyId;
				adapter._source.url = "jqxGeneralServicer?sname=JQGetListRoutes&distinct=Y&partyId=" + partyId;
				$("#jqxgridRoutes").jqxGrid('source', adapter);
			}
			var wtmp = window;
	    	var tmpwidth = $("#jqxwindowRoutes").jqxWindow("width");
	        $("#jqxwindowRoutes").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	$("#jqxwindowRoutes").jqxWindow("open");
		}
		return {
			init: function() {
				initJqxElements();
			},
			open: open
		};
	})();
</script>