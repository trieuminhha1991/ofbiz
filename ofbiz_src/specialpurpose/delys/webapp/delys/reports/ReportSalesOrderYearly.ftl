<script type="text/javascript" src="/highcharts/assets/js/highcharts.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-3d.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-more.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/exporting.js"></script>
<@jqGridMinimumLib/>

<script type="text/javascript">

    var customButtonsDemo = (function () {
        var _collapsed = false;
        function _addEventListeners() {
            $('#showWindowButton').mousedown(function () {
                $('#customWindow').jqxWindow('open');
            });

            _addCustomButtonsHandlers();
            _addSearchInputEventHandlers();
        };
        function _addCustomButtonsHandlers() {

        };
        function _addSearchInputEventHandlers() {
            $('#searchTextInput').keydown(function () {
                _searchButtonHandle();
            });
            $('#searchTextInput').change(function () {
                _searchButtonHandle();
            });
            $('#searchTextInput').keyup(function () {
                _searchButtonHandle();
            });
            $(document).mousemove(function () {
                _searchButtonHandle();
            });
        };
        function _searchButtonHandle() {
        };
        function _createElements() {
            $('#showWindowButton').jqxButton({ width: '80px'});
            $('#customWindow').jqxWindow({
                autoOpen: false,
                width: 350,
                height: 200, resizable: false,
                cancelButton: $('#cancelButton'),
                initContent: function () {
                    $('#searchTextButton').jqxButton({ width: '80px', disabled: false });
                }
            });
        };
        return {
            init: function () {
                _createElements();
                _addEventListeners();
            }
        };
    } ());
    $(document).ready(function () {
        $('#container').highcharts({
            chart: {
                type: 'line'
            },
            title: {
                text: 'OLBIUS'
            },
            subtitle: {
                text: '2015'
            },
            xAxis: {
                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
            },
            yAxis: {
                title: {
                    text: 'Doanh số (VNĐ)'
                }
            },
           
            tooltip: {
                valueSuffix: 'VNĐ'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: []
        });
        customButtonsDemo.init();
        department= $("#SearchSalesMan_department").val();
        year= $("#year").val();
        var datalist= getData(department,year);
        if(datalist){
            var chart = $('#container').highcharts();
            chart.setTitle({text:"Doanh số bán hàng phòng"+" "+$("#SearchSalesMan_department").text()},{text:"${StringUtil.wrapString(uiLabelMap.CommonYear)}"+" "+year},true);
            for(i=0;i<datalist.length;i++){
                    chart.addSeries({name: datalist[i].name, data: datalist[i].data}, true);
            }
        }
    });

</script>
</head>
<body class='default'>
<div style="position: relative;">
    <div id="checkoutInfoLoader" style="overflow: hidden; position: absolute; width: 1120px; height: 640px; display: none;" class="jqx-rc-all jqx-rc-all-olbius">
        <div style="z-index: 99999; margin-left: -66px; left: 50%; top: 15%; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div style="float: left;">
                <div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
                <span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">Loading...</span>
            </div>
        </div>
    </div>
    <div id="container" style="margin-top: 10px;">

    </div>
</div>
<div style="width: 100%; height: 650px;" id="jqxWidget">
    <input type="button" value="Option" id="showWindowButton" />
    <div id="mainDemoContainer">
        <div id="customWindow">
            <div id="customWindowHeader">
                <span id="captureContainer" style="float: left">Find </span>
            </div>
            <div id="customWindowContent" style="overflow: hidden">
                <div style="margin: 10px">
                    <form id="SearchSalesMan" name="SearchSalesMan">
                        <table border="0">
                            <tr>
                                <td>
                                    ${uiLabelMap.DASalesChannel}:
                                </td>
                                <td>
                                    <select name="chanelsales" id="SearchSalesMan_chanelsales" size="1">
                                        <option  value ="DELYS_SALESSUP_GT">${uiLabelMap.DAGTChannel}</option>
                                        <option  value ="DELYS_SALESSUP_MT">${uiLabelMap.DAMTChannel}</option>
                                    </select></br>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                  ${uiLabelMap.OlapDepartment}
                                </td>
                                <td>
                                    <select name="department" id="SearchSalesMan_department" size="1"> </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                 ${uiLabelMap.CommonYear}
                                </td>
                                <td>
                                   <#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
                                   <input type="text" style="margin-bottom:0px;"class="input-mini" id="year" name="year"/>
                                </td>
                            </tr>
                        </table>
                        <div style="text-align: center">
                            <input type="button" value="Find Next" style="margin-bottom: 5px;" id="searchTextButton" /><br />
                        </div>

                    </form>

                </div>
            </div>
        </div>
    </div>
</div>
    <#assign minYear = currentYear-15>
    <#assign maxYear = currentYear+15>
    <script type="text/javascript">
        function getData(department, year){
            var jsc= new Array();
            $("#checkoutInfoLoader").show();
            $.ajax({
                url: '<@ofbizUrl>getDataReportSalesOrderYear</@ofbizUrl>',
                type: 'POST',
                dataType: 'json',
                data:{department:department,year:year},
                async:false,
                beforeSend: function () {
						$("#checkoutInfoLoader").show();
				}, 
                success:function(data){
                    jsc=data;
                },
                complete: function() {
				        $("#checkoutInfoLoader").hide();
			    }
            });
            return jsc;
        }
         jQuery('#year').ace_spinner({value:${currentYear},min:${minYear},max:${maxYear},step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});

        $("#searchTextButton").on("click",function(){
            $('#customWindow').jqxWindow('close');
            department= $("#SearchSalesMan_department").val();
            year= $("#year").val()
           var   dataclick= getData(department,year);
            if(dataclick){
                var chart = $('#container').highcharts();
                while (chart.series.length > 0) {
                    chart.series[0].remove(true);
                }
                for(i=0;i<dataclick.length;i++){
                    chart.addSeries({name: dataclick[i].name, data: dataclick[i].data}, true);
                }
                chart.setTitle({text:"Doanh số bán hàng phòng"+" "+$("#SearchSalesMan_department").text()},{text:"${StringUtil.wrapString(uiLabelMap.CommonYear)}"+" "+year},true);
            }

        });


    </script>
    
</body>
</html>