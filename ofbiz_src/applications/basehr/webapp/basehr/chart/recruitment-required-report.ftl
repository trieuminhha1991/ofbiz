<#assign dataField = "[
		{name : 'emplPositionTypeId', type : 'string'},
		{name : 'description', type : 'string'},
		{name : 'emplPositionTypeId', type : 'string'},
		{name : 'emplPositionTypeId', type : 'string'},
		{name : 'quantity', type: 'nubmer'},
		{name : 'time', type : 'date', other : 'Timestamp'},
		{name : 'recruitmentFormTypeId', type : 'string'},
		{name : 'descriptionFormType', type : 'string'},
		{name : 'statusId', type : 'string'},
		{name : 'descriptionStatus', type : 'string'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.Time)}', datafield : 'time', width : '25%', cellsformat : 'MM/yyyy', filterable : 'false'},
		{text : '${StringUtil.wrapString(uiLabelMap.EmplPositionType)}', datafield : 'description', width : '25%'},
		{text : '${StringUtil.wrapString(uiLabelMap.Quantity)}', datafield : 'quantity', width : '25%'},
		{text : '${StringUtil.wrapString(uiLabelMap.WorkForm)}', datafield : 'descriptionFormType',},
	"/>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<link rel="stylesheet" type="text/css" href="../../hrresources/css/report/ReportStyle.css" />

<script type="text/javascript" id="recruitmentRequired">
$(function() {
	var textView = OLBIUS.textView({
		id :'recruitmentRequired',
		url: 'getRecruitmentReqruired',
		icon: 'fa fa-bell-o',
		data: {},
		renderTitle: function(data) {
			return '${StringUtil.wrapString(uiLabelMap.recruit_required_current_month)}'
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
		showRecruitmentRequired();
	});
});
</script>
	
<!--	<div class="recruitmentRequired">
	<div>
		<i class="fa-bell-o"></i> <lable>${uiLabelMap.recruit_required_current_month}</lable>
	</div>
	<a style="text-decoration: underline !important; color: #fff;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick='showRecruitmentRequired()'>
		<div id="recruimentRequiredEmpl">
			<script type="text/javascript">
				$(document).ready(function(){
					$.ajax({url: 'getRecruitmentReqruired',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var total = data.total;
					    	$("#recruimentRequiredEmpl").html(total);	
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

<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.recruit_required_of_depart_current_month}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<@jqGrid filtersimplemode="true" id="jqxgrid_recruit_required" filterable="true" addrefresh="true" dataField=dataField columnlist=columnlist  
				clearfilteringbutton="true" sortable="true" autoheight="false" height="300" customTitleProperties="${uiLabelMap.recruit_required_of_depart_current_month}"
				customLoadFunction="true" url="jqxGeneralServicer?sname=getRecruitmentRequiredIdDept"/>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var recruit = (function(){
		var init = function(){
			initWindow();
		}
		
		var initWindow = function(){
			createJqxWindow($('#alterpopupWindow'), 600, 400);
		};
		
		return {
			init : init
		}
	}());
	
	function showRecruitmentRequired(){
		initGridjqxgrid_recruit_required();
		openJqxWindow($('#alterpopupWindow'));
	};
	
	$(document).ready(function(){
		recruit.init();
	})
</script>