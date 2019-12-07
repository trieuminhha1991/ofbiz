<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsreorder.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-3d.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-more.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/exporting.js"></script>
<#--<script type="text/javascript" src="/images/bi-x/olbius.js"></script>
<script type="text/javascript" src="/images/bi-x/olbiusWindow.js"></script>
<script type="text/javascript" src="/images/bi-x/olbiusOLap.js"></script>-->
<script type="text/javascript" src="/images/bi-x/olbius.config.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.popup.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.new.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.grid.js"></script>
<script type="text/javascript">
var date_type_source = [{text: '${StringUtil.wrapString(uiLabelMap.olap_day)}', value: 'DAY'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_week)}', value: 'WEEK'},
        {text:'${StringUtil.wrapString(uiLabelMap.olap_month)}', value: 'MONTH'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}', value: 'QUARTER'},
        {text: '${StringUtil.wrapString(uiLabelMap.olap_year)}', value: 'YEAR'}];
var cur_date = new Date();
var past_date = new Date(cur_date);
past_date.setDate(1);
</script>