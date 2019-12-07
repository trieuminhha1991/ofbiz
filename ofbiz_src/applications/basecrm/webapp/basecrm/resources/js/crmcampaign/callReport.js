var CallReport = (function() {
	var initChart = function(element, data) {
		element
				.highcharts({
					chart : {
						plotBackgroundColor : null,
						plotBorderWidth : null,
						plotShadow : false,
						type : 'pie',
					},
					title : {
						text : uiLabelMap.ContactAssignChart
					},
					tooltip : {
						pointFormat : '{point.percentage:.1f}%'
					},
					plotOptions : {
						pie : {
							allowPointSelect : true,
							cursor : 'pointer',
							dataLabels : {
								enabled : true,
								format : '<b>{point.name}</b>: {point.percentage:.1f} %',
								style : {
									color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
											|| 'black'
								}
							}
						}
					},
					series : [ {
						name : "Contact",
						colorByPoint : true,
						data : data
					} ]
				});
	};
	var bindTabClick = function() {
		$('#AssignResourceTab').on('shown.bs.tab', function(e) {
			var href = $(e.target).attr('href');
			switch (href) {
			case "#report":
				var obj = $("#report");
				if (!obj.data('init')) {
					obj.data('init', true);
					getContactCampaignReport();
					initGridListEmployeeContact();
				}
				// $("#ListEmployeeContact").jqxGrid('updatebounddata');
				break;
			}
		});
	};
	var getContactCampaignReport = function() {
		var renderReportContact = function(res) {
			var total = res.total ? res.total : 0;
			var completed = res.completed ? res.completed : 0;
			var uncompleted = res.uncompleted ? res.uncompleted : 0;
			var assigned = res.assigned ? res.assigned : 0;
			var notassigned = res.notassigned ? res.notassigned : 0;
			$('#totalContact').html(total);
			$('#assignedCon').html(assigned);
			$('#notAssignedContact').html(notassigned);
			$('#completedContact').html(completed);
			$('#notCompletedContact').html(uncompleted);
			var assper = assigned / total * 100;
			var unassper = 100 - assper;
			var comper = completed / total * 100;
			var uncomper = 100 - comper;
			var data = [ {
				name : uiLabelMap.Assigned,
				y : assper
			}, {
				name : uiLabelMap.NotAssigned,
				y : unassper,
				sliced : true,
				selected : true
			} ];
			var data2 = [ {
				name : uiLabelMap.Completed,
				y : comper
			}, {
				name : uiLabelMap.NotCompleted,
				y : uncomper,
				sliced : true,
				selected : true
			} ];
			initChart($('#AssignedContactChart'), data);
			initChart($('#CompletedContactChart'), data2);
		};
		$.ajax({
			url : 'getContactCampaignReport',
			data : {
				marketingCampaignId : campaignId
			},
			type : 'POST',
			success : function(res) {
				if (res.result) {
					renderReportContact(res.result);
				}
			}
		});
	};

	var bindEvent = function() {
		$('#UpdateContactCampaignReport').click(function() {
			getContactCampaignReport();
		});
	};
	var init = function() {
		bindEvent();
		bindTabClick();
	};
	return {
		init : init
	};
})();
$(document).ready(function() {
	CallReport.init();
});
