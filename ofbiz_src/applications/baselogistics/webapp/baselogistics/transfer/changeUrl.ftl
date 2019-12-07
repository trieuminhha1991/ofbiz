<script type="text/javascript">
	$("#jqxgridProduct").on('bindingComplete', function (event) {
		var tmpS = $("#jqxgridProduct").jqxGrid('source');
		if (tmpS._source.url === '' || tmpS._source.url === undefined || tmpS._source.url === null){
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetListProductByOrganiztion&facilityId=" + $("#originFacilityId").jqxDropDownList('val');
			$("#jqxgridProduct").jqxGrid('source', tmpS);
			$("#jqxgridProduct").jqxGrid('updatebounddata');
		}
		return false;
	});
</script>