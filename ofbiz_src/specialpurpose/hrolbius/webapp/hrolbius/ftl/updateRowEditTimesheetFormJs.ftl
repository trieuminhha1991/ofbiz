<script type="text/javascript">
jQuery(document).ready( function() {
	jQuery("#addNewRowEditTimeSheetForms").click(function(){
		var data = {};
		data.partyId = "${parameters.partyId?if_exists}";
		data.timesheetId = "${parameters.timesheetId?if_exists}";
		data.numberRowAdd = "${numberRowAdd?if_exists}";
		data.addRow = 1;
		jQuery.ajax({
			url: "AddNewRowEditTimeSheet",
			type: 'POST',
			data: data,
			success: function(data){
				jQuery("#EditWeekTimesheetUpdate").html(data);
			},
			error: function(err, req){
			}
		});
	});
	jQuery("#deleteNewRowEditTimeSheetForms").click(function(){
		var data = {};
		data.partyId = "${parameters.partyId?if_exists}";
		data.timesheetId = "${parameters.timesheetId?if_exists}";
		data.numberRowAdd = "${numberRowAdd?if_exists}";
		data.addRow = -1;
		<#if numberRowAdd?exists && (numberRowAdd > 0)> 
			jQuery.ajax({
				url: "AddNewRowEditTimeSheet",
				type: 'POST',
				data: data,
				success: function(data){
					jQuery("#EditWeekTimesheetUpdate").html(data);
				},
				error: function(err, req){
				}
			});
		</#if>
	});
});
</script>