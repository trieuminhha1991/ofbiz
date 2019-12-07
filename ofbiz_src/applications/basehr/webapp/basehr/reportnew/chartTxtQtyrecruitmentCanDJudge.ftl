<script type="text/javascript" id="recruimentCandidateJudge">
    $(function() {
        var textView = OLBIUS.textView({
            id :'recruimentCandidateJudge',
            url: 'getRecruitmentCandidateJudge',
            icon: 'fa fa-bullhorn',
            data: {},
            renderTitle: function(data) {
                return '${StringUtil.wrapString(uiLabelMap.JudgeRecruitmentCandidate)}'
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