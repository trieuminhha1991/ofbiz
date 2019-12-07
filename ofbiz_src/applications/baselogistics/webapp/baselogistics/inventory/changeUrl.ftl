<script type="text/javascript">
	function getData(){
		var data = new Array();
		$.ajax({
			url: 'getInventoryItemPhysicalDetail',
			type: "POST",
			data: {
				facilityId: $("#facilityId").jqxDropDownList('val'),
			},
			dataType: 'json',
			success : function(rep) {
				data = rep['listInventoryItems'];
				renderGridData(data);
				$('#jqxgridItemPhysical').show();
				$("#jqxgridItemPhysical").jqxGrid('updatebounddata');
				Loading.hide('loadingMacro');
			},
			beforeSend: function(){
				$('#jqxgridItemPhysical').hide();
				Loading.show('loadingMacro');
			}
			
		});
		return data;
	}
	
	var renderGridData = function(data){
		var grid = $('#jqxgridItemPhysical');
		var adapter = grid.jqxGrid('source');
		if(adapter){
			adapter.localdata = data;
			adapter._source.localdata = data;
			grid.jqxGrid('source', adapter);
		}
		
	};
</script>