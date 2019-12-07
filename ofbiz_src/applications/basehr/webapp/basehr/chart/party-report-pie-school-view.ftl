<#--<div id="container" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>-->

<script type="text/javascript" id="partyOLapPieSchool">

    $(function () {

        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_pie_school_title)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            }
        };

        var configPopup = {
            'jqxTree' : {
                action : 'addJQXTree',
                params : [{
                    id : 'jqxTree',
                    label : '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
                }]
            },
            'schoolId': {
                action : 'addDropDownList',
                params : [{
                    id : 'school_type',
                    label : '${StringUtil.wrapString(uiLabelMap.party_schoolType)}',
                    data : ["EDU_SYS", "CLASSIFICATION", "SCHOOL", "STUDY_MODE", "MAJOR"],
                    index: 0
                }]
            }
        };

        var partyOLap = OLBIUS.oLapChart('partyOLapPieSchool', config, configPopup, 'school', true, true, OLBIUS.defaultPieFunc, 0.8);

        partyOLap.funcUpdate(function(oLap) {
            oLap.update({
                'group' : oLap.val('jqxTree'),
                'type': oLap.val('school_type')
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>