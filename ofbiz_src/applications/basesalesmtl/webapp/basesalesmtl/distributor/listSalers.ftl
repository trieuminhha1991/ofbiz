<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<div id="jqxgridSaler"></div>

<script>
$().ready(function() {
	Saler.render();
});
if (typeof (Saler) == "undefined") {
	var Saler = (function($) {
		var render = function(partyId) {
			var source =
	        {
	            datatype: "json",
	            datafields:
	            [{ name: "storeName", type: "string" },
	            { name: "partyId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "partyName", type: "string" },
				{ name: "contactNumber", type: "string" },
				{ name: "emailAddress", type: "string" }],
	            url: "getSalersOfDistributor?partyId=${userLogin.partyId}",
	            async: false,
	            id: "partyId"
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#jqxgridSaler").jqxGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            showfilterrow: true,
	            filterable: true,
				width: "100%",
				autoheight: true,
	            pagesize: 10,
	            pageable: true,
	            theme: "olbius",
	            sortable: true,
	            selectionmode: "singlerow",
	            columns: [
						{text: "${StringUtil.wrapString(uiLabelMap.BSProductStore)}", datafield: "storeName", width: 200},
						{text: "${StringUtil.wrapString(uiLabelMap.EmployeeId)}", datafield: "partyId", width: 150},
						{text: "${StringUtil.wrapString(uiLabelMap.EmployeeName)}", datafield: "partyName", width: 200},
						{text: "${StringUtil.wrapString(uiLabelMap.PhoneNumber)}", datafield: "contactNumber", width: 200},
						{text: "${StringUtil.wrapString(uiLabelMap.Email)}", datafield: "emailAddress"}],
	            handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
		                	$("#jqxgridSaler").jqxGrid("clearfilters");
							return true;
		                }
					}
	        });
		};
		return {
			render: render
		};
	})(jQuery);
}
</script>