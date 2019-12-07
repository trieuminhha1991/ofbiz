<div id="jqxwindowStores" style="display:none;">
	<div>${uiLabelMap.BSViewListStoresAssigned}</div>
	<div>
		
		<div style="height: 405px;overflow-y: auto;">
			<div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.Employee}:</div> <div class="salesmanInfo jqxwindowTitle" style="display: inline-block;"></div></div>
			<#assign urlStores = ""/>
			<#assign customLoadFunction = "true"/>
			<#include "../listStoresAssigned.ftl"/>
		</div>
		
		<div class="form-action row-fluid">
			<div class="span12">
				<button id="btnCloseStores" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		ListStores.init();
		initGridjqxgridStores();
	});
	var ListStores = (function() {
		var initJqxElements = function() {
			$("#jqxwindowStores").jqxWindow({
				theme: "olbius", maxWidth: 1000, width: 1000, height: 560, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#btnCloseStores"), modalOpacity: 0.7
			});
		};
		var open = function(partyId) {
			var adapter = $("#jqxgridStores").jqxGrid('source');
			if(adapter){
				adapter.url = "jqxGeneralServicer?sname=JQGetListStores&partyId=" + partyId;
				adapter._source.url = "jqxGeneralServicer?sname=JQGetListStores&partyId=" + partyId;
				$("#jqxgridStores").jqxGrid('source', adapter);
			}
			var wtmp = window;
	    	var tmpwidth = $("#jqxwindowStores").jqxWindow("width");
	        $("#jqxwindowStores").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	$("#jqxwindowStores").jqxWindow("open");
		}
		return {
			init: function() {
				initJqxElements();
			},
			open: open
		};
	})();
</script>