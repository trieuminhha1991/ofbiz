<div id="recruitmentRequired" style="height:100%"></div>
<script type="text/javascript">
    $(function() {
        var textView = OLBIUS.textView({
            id :'recruitmentRequired',
            url: 'getRecruitmentReqruired',
            icon: 'fa fa-bell-o',
            data: {
                service: "person",
                olapType: "GRID",
            },
            renderTitle: function(data) {
                return '${StringUtil.wrapString(uiLabelMap.recruit_required_current_month)}'
            },
            renderValue: function(data) {
                var dataMap = data.data;
                if(typeof dataMap == "undefined" || dataMap.length < 0 || dataMap[0] == null){
                    return "0";
                } else {
                    if (data) {
                        return formatnumber(data.total);
                    } else {
                        return "0";
                    }
                }
            }
        }).init();
    });
</script>