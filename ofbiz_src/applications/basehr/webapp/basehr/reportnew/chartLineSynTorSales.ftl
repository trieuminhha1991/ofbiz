<script type="text/javascript">
    var chartRenderTypeColumn = function(config){
        var $color = typeof(config.color) != 'undefined' ? config.color : 0;
        return function(data, obj) {
            var tmp = {
                labels: {
                    enabled: true
                },
                categories: data.xAxis
            };

            obj._chart.xAxis[0].update(tmp, false);

            while (obj._chart.series.length > 0) {
                obj._chart.series[0].remove(false);
            }

            var color = $color;
            for (var i in data.yAxis) {
                obj._chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++]
                }, false);
            }

            obj._chart.redraw();

            return !!(data.xAxis && data.xAxis.length == 0);
        };
    };

    var dataCctpFilterTopArr = ["5", "10", "15", "20"];
    var dataCctpFilterSortArr = [
        {'text': '${StringUtil.wrapString(uiLabelMap.BSTurnoverHighest)}', 'value': 'DESC'},
        {'text': '${StringUtil.wrapString(uiLabelMap.BSTurnoverLowest)}', 'value': 'ASC'}
    ];
    var dataTopQtyFilterSortArr = [
        {'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityHighest)}', 'value': 'DESC'},
        {'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityLowest)}', 'value': 'ASC'}
    ];
    var dataCctpFilterTypeArr = [
        {'text': '${StringUtil.wrapString(uiLabelMap.BSProduct)}', 'value': 'PRODUCT'},
        {'text': '${StringUtil.wrapString(uiLabelMap.BSSalesExecutive)}', 'value': 'SALES_EXECUTIVE'}
    ];
</script>
