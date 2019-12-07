<#assign dataField = "[
		{name : 'partyId', type : 'string'},
		{name : 'roleDescription', type : 'string'},
		{name : 'recruitmentFromDate', type : 'date', other : 'Timestamp'},
		{name : 'emplPositionTypeId', type : 'string'},
		{name : 'descriptionEmplPos', type : 'string'},
		{name : 'recruitmentThruDate', type : 'date', other: 'Timestamp'},
		{name : 'recruitmentFormTypeId', type : 'string'},
		{name : 'descriptionFormType', type : 'string'},
		{name : 'recruitmentPlanName', type : 'string'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '20%'},
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanBoardPos)}', datafield : 'roleDescription', width : '20%'},
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentFromDate)}', datafield : 'recruitmentFromDate', width : '20%', 
				cellsformat : 'dd/MM/yyyy',columntype: 'datetimeinput', filtertype: 'range'},
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentThruDate)}', datafield : 'recruitmentThruDate', width : '20%',
				cellsformat : 'dd/MM/yyyy', columntype: 'datetimeinput', filtertype: 'range'},
		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentForm)}', datafield : 'descriptionFormType'}
	
	"/>
		
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>		
<link rel="stylesheet" type="text/css" href="../../hrresources/css/report/ReportStyle.css" />

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
	textView.click(function() {
		showRecruitmentPlanBoard()
	});
});
</script>	
	
<!-- <div class="recruimentPlanBoard">
	<div>
		<i class="fa fa-bullhorn"></i> <lable>${uiLabelMap.RecruitmentSchedule}</lable>
	</div>
	<a style="text-decoration: underline !important; color: #fff;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick='showRecruitmentPlanBoard()'>
		<div id="recruimentPlanBoard">
			<script type="text/javascript">
				$(document).ready(function(){
					$.ajax({url: 'getRecruitmentPlanBoardSchedule',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var total = data.total;
					    	$("#recruimentPlanBoard").html(total);	
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

<div id="alterpopupWindow_planBoard" class="hide">
	<div>${uiLabelMap.RecruitmentPlanTime}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<@jqGrid filtersimplemode="true" id="jqxgrid_planBoard" filterable="true" addrefresh="true" dataField=dataField columnlist=columnlist  
				clearfilteringbutton="true" sortable="true" autoheight="false" height="300" customTitleProperties="${uiLabelMap.RecruitmentPlanTime}"
				customLoadFunction="true" url="jqxGeneralServicer?sname=getRecruitmentPlanTime"/>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var recruit_planBoard = (function(){
		var init = function(){
			initWindow();
		};
		
		var initWindow = function(){
			createJqxWindow($('#alterpopupWindow_planBoard'), 600, 400);
		};
		
		return {
			init : init
		}
	}())
	
	function showRecruitmentPlanBoard(){
		initGridjqxgrid_planBoard();
		openJqxWindow($('#alterpopupWindow_planBoard'));
	};
	
	$(document).ready(function(){
		recruit_planBoard.init();
	})
</script>