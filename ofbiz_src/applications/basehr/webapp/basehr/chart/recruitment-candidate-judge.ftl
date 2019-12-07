<#assign dataField = "[
		{name : 'recruitmentPlanId', type : 'string'},
		{name : 'recruitmentPlanName', type : 'string'},
		{name : 'partyId', type : 'string'},
		{name : 'fullName', type : 'string'},
		{name : 'roundOrder', type : 'number', other : 'Long'},
		{name : 'roundName', type : 'string'},
	
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '25%'},
		{text : '${StringUtil.wrapString(uiLabelMap.CandidateName)}', datafield : 'fullName', width : '25%'},
		{text : '${StringUtil.wrapString(uiLabelMap.RoundOrder)}', datafield : 'roundOrder', width : '25%',columntype: 'numberinput', filtertype: 'number'},
		{text : '${StringUtil.wrapString(uiLabelMap.RoundName)}', datafield : 'roundName', width : '25%'},
	"/>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>	
<link rel="stylesheet" type="text/css" href="../../hrresources/css/report/ReportStyle.css" />

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
	textView.click(function() {
		showRecruitmentCandidateJudge();
	});
});
</script>
	
<!--	<div class="recruimentCandidateJudge">
	<div>
		<i class="fa fa-bullhorn"></i> <lable>${uiLabelMap.JudgeRecruitmentCandidate}</lable>
	</div>
	<a style="text-decoration: underline !important; color: #fff;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick='showRecruitmentCandidateJudge()'>
		<div id="recruimentCandidateJudge">
			<script type="text/javascript">
				$(document).ready(function(){
					$.ajax({url: 'getRecruitmentCandidateJudge',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var total = data.total;
					    	$("#recruimentCandidateJudge").html(total);	
					    },
					    error: function(data) {
					    	alert('Error !!');
					    }
					});
				})
			</script>
		</div>
	</a>
</div> -->
<div id="alterpopup_candidate_judge">
	<div>${uiLabelMap.RecruitmentCandidateWating}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<@jqGrid filtersimplemode="true" id="jqxgrid_candidate_judge" filterable="true" addrefresh="true" dataField=dataField columnlist=columnlist  
				clearfilteringbutton="true" sortable="true" autoheight="false" height="300" customTitleProperties="${uiLabelMap.RecruitmentCandidateWating}"
				customLoadFunction="true" url="jqxGeneralServicer?sname=getRecruitmentCandidateWating"/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var recruit_candidate = (function(){
		var init = function(){
			initWindow();
			initGridEvent();
		};
		
		var initWindow = function(){
			createJqxWindow($('#alterpopup_candidate_judge'), 600, 400);
		};
		
		var initGridEvent = function(){
			$('#jqxgrid_candidate_judge').on('rowdoubleclick', function(event){
				var index = event.args.rowindex;
				var data = $('#jqxgrid_candidate_judge').jqxGrid('getrowdata', index);
				var url = window.location.href;
				var last = url.lastIndexOf("/");
				var new_url = url.substring(0, last + 1) + "ViewListCandidateEvaluatedInRecPlan";
				window.open(new_url, '_blank');
			})
		};
		
		return {
			init : init
		}
	}());
	$(document).ready(function(){
		recruit_candidate.init();
	});
	
	function showRecruitmentCandidateJudge(){
		initGridjqxgrid_candidate_judge();
		openJqxWindow($('#alterpopup_candidate_judge'));
	}
</script>