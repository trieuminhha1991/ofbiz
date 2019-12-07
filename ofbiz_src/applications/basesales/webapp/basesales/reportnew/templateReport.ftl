<script type="text/javascript" id="olbiusFacility">
    $(function() {
        var config = {
            title: "Ten bao cao",
            //service: "inventoryItem",
            //button: true,
            id: "olbiusFacility", //unknow
            url: "olapTemplateReport", //serviceName
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
            ],
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime"
                }
            ],
            apply: function (grid, popup) {
                return $.extend({
                	
                }, popup.group("dateTime").val());
            }
        };

        var grid = OlbiusUtil.grid(config);
    });
</script>
