<script type="text/javascript" id="recruimentPlanBoard">
    $(function() {
        var textView = OLBIUS.textView({
            id :'recruimentPlanBoard',
            url: 'getRecruitmentPlanBoardSchedule',
            icon: 'fa fa-bullhorn',
            data: {},
            renderTitle: function(data) {
                return '${StringUtil.wrapString(uiLabelMap.RecruitmentSchedule)}'
            },
            renderValue: function(data) {
                if(data){
                    return formatnumber(data.total);
                } else {
                    return "0";
                }
            }
        }).init();
    });
</script>