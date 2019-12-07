<!-- JQX JS LIB -->

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<#include "component://widget/templates/jqwLocalization.ftl" />
<#include "component://bi-x/widget/ftl/olbius.application.config.ftl" />

<!-- HIGHCHARTS JS LIB -->

<script type="text/javascript" src="/highcharts/assets/4.2.3/highcharts.js"></script>
<script type="text/javascript" src="/highcharts/assets/4.2.3/highcharts-3d.js"></script>
<script type="text/javascript" src="/highcharts/assets/4.2.3/highcharts-more.js"></script>
<script type="text/javascript" src="/highcharts/assets/4.2.3/modules/exporting.js"></script>

<!-- OLBIUS OLAP JS LIB -->

<script type="text/javascript" src="/images/bi-x/olbius.config.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.popup.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.new.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.grid.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.textview.js"></script>

<!-- OLBIUS OLAP JS LIB V2-->

<script type="text/javascript" src="/images/bi-x/v2/jquery.actual.min.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.filedownload.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.utils.js?v=0.0.2"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.popup.v2.js?v=0.0.2"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.popup.element.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.popup.element.group.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.data.export.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.grid.export.js"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.grid.v2.js?v=0.0.3"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.treegrid.js?v=0.0.2"></script>
<script type="text/javascript" src="/images/bi-x/v2/olbius.chart.js"></script>

<script type="text/javascript">
    var date_type_source = [{
        text: '${StringUtil.wrapString(uiLabelMap.olap_day)}',
        value: 'DAY'
    }, {text: '${StringUtil.wrapString(uiLabelMap.olap_week)}', value: 'WEEK'},
        {
            text: '${StringUtil.wrapString(uiLabelMap.olap_month)}',
            value: 'MONTH'
        }, {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}', value: 'QUARTER'},
        {text: '${StringUtil.wrapString(uiLabelMap.olap_year)}', value: 'YEAR'}];
    var cur_date = new Date();
    var past_date = new Date(cur_date);
    past_date.setMonth(past_date.getMonth() - 11);
    past_date.setDate(1);
    $(function () {
        Highcharts.setOptions({
            lang: {
                loading: '${StringUtil.wrapString(uiLabelMap.data_not_found)}',
                config: '${StringUtil.wrapString(uiLabelMap.olap_configuration)}',
                download: '${StringUtil.wrapString(uiLabelMap.olap_download)}'
            }
        });
        OlbiusUtil.putVariable('jqxLocalization', getLocalization());
        OlbiusUtil.putVariable('dateTimeFormat', '${StringUtil.wrapString(uiLabelMap.dateTimeFormat)}');
        OlbiusUtil.putVariable('dateTimeFullFormat', '${StringUtil.wrapString(uiLabelMap.dateTimeFullFormat)}');
        OlbiusUtil.putVariable('monthFormat', '${StringUtil.wrapString(uiLabelMap.monthFormat)}');
        OlbiusUtil.putVariable('tickInterval', parseInt('${tickInterval}'));
        OlbiusUtil.gridLabel({
            warnGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_warn_grid)}',
            okGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_ok_grid)}',
            cancelGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_cancel_grid)}',
            lastUpdatedGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_lastupdated_grid)}',
            configLabel: '${StringUtil.wrapString(uiLabelMap.olap_configuration)}',
            aggregateLabel: '${StringUtil.wrapString(uiLabelMap.AggregateLabel)}'
        });
        OlbiusUtil.treeGridLabel({
            warnGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_warn_grid)}',
            okGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_ok_grid)}',
            cancelGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_cancel_grid)}',
            lastUpdatedGridLabel: '${StringUtil.wrapString(uiLabelMap.olap_lastupdated_grid)}',
            configLabel: '${StringUtil.wrapString(uiLabelMap.olap_configuration)}',
            aggregateLabel: '${StringUtil.wrapString(uiLabelMap.AggregateLabel)}'
        });
        OlbiusUtil.popupLabel({
            configuration: '${StringUtil.wrapString(uiLabelMap.olap_configuration)}',
            button: '${StringUtil.wrapString(uiLabelMap.olap_ok)}'
        });
        OlbiusUtil.groupDateTimeLable({
            dayType: '${StringUtil.wrapString(uiLabelMap.DayLabel)}',
            weekType: '${StringUtil.wrapString(uiLabelMap.WeekLabel)}',
            monthType: '${StringUtil.wrapString(uiLabelMap.MonthLabel)}',
            quarterType: '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}',
            yearType: '${StringUtil.wrapString(uiLabelMap.YearLabel)}',
            otherType: '${StringUtil.wrapString(uiLabelMap.OtherLabel)}',
            groupType: '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
            dateType: '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
            fromDate: '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
            thruDate: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
            date: '${StringUtil.wrapString(uiLabelMap.olap_day)}',
            month: '${StringUtil.wrapString(uiLabelMap.olap_month)}',
            week: '${StringUtil.wrapString(uiLabelMap.olap_week)}',
            quarter: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}',
            year: '${StringUtil.wrapString(uiLabelMap.olap_year)}'
        });
        OLBIUS.setTickInterval(parseInt('${tickInterval}'));
        OLBIUS.setDateTimeFormat('${StringUtil.wrapString(uiLabelMap.dateTimeFormat)}');
        OLBIUS.setDateTimeFullFormat('${StringUtil.wrapString(uiLabelMap.dateTimeFullFormat)}');
        OLBIUS.setMonthFormat('${StringUtil.wrapString(uiLabelMap.monthFormat)}');
        OLBIUS.setCompany('${company}');
        OLBIUS.setConfiguration('${StringUtil.wrapString(uiLabelMap.olap_configuration)}');
        OLBIUS.setOKText('${StringUtil.wrapString(uiLabelMap.olap_ok)}');
        OLBIUS.setTheme('olbius');
        OLBIUS.setOkGridLable('${StringUtil.wrapString(uiLabelMap.olap_ok_grid)}');
        OLBIUS.setCancelGridLable('${StringUtil.wrapString(uiLabelMap.olap_cancel_grid)}');
        OLBIUS.setWarnGridLable('${StringUtil.wrapString(uiLabelMap.olap_warn_grid)}');
        OLBIUS.setLastupdatedGridLable('${StringUtil.wrapString(uiLabelMap.olap_lastupdated_grid)}');
        OLBIUS.setWeekLable('${StringUtil.wrapString(uiLabelMap.olap_week_label)}');
        OLBIUS.setQuarterLable('${StringUtil.wrapString(uiLabelMap.olap_quarter_label)}');
    });
</script>
