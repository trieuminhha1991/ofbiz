<style>
.langerInput {
	width: 500px !important;
}
#myImg { 
	position : fixed;
	left: 654px;
	top: 266px;
	visibility: hidden;
}
</style>
<table class='table-bordered table dataTable' cellspacing='0'>
	<tr>
		<td>${uiLabelMap.QuotaName} : </td>
		<td><input type="text" class="langerInput" id="txtQuotaName" onchange="anyOneChange()"></td>
	</tr>
	<tr>
		<td>${uiLabelMap.Supplier}: </td>
		<td>
			<select id="SupplierSelect" onchange="anyOneChange()">
			<option>-Select-</option>
				<#list listSuppliers as listSupplier>
					<option>${listSupplier.partyId}</option>
				</#list>
			</select>
		</td>
	</tr>
	<tr>
		<td>${uiLabelMap.fromDate}: </td>
		<td><input type="text" id="txtFromDate"></td>
	</tr>
	<tr>
		<td>${uiLabelMap.thruDate}: </td>
		<td><input type="text" id="txtThruDate"></td>
	</tr>
</table>
<h3>${uiLabelMap.FileScan}</h3>
<input multiple type="file" id="id-input-file-3" onchange="anyOneChange()"/>

<input type="submit" class="btn btn-primary" onclick="scan()" value="${uiLabelMap.Upload}">
<img id="myImg" src="/delys/images/css/import/ajax-loader.gif">

<script type="text/javascript">
var listImage = [];
$(function() {
	 var txtFromDate;
	 $( "#txtFromDate" ).datepicker({ 
//		 dateFormat: 'yy-mm-dd',
		 onSelect: function(dateStr) {
			 txtFromDate = $(this).datepicker('getDate');
		 },
	 });
	 $( "#txtThruDate" ).datepicker({ 
//		 dateFormat: 'yy-mm-dd',
		 onSelect: function(dateStr) {
			 var txtThruDate = $(this).datepicker('getDate');
			 var newDate = txtThruDate.getTime() - txtFromDate.getTime();
			 if (newDate < (10*86400000)) {
				 $("#txtThruDate").notify("Date less more 10 day!!!", {className: 'error', position:"right" });
				 $.datepicker._clearDate(this);
			}else {
				$("#txtThruDate").notify("Done!!!", {className: 'success', position:"right" });
			}
		 },
	 });
	 
	$('#id-input-file-3').ace_file_input({
		style:'well',
		btn_choose:'Drop files here or click to choose',
		btn_change:null,
		no_icon:'icon-cloud-upload',
		droppable:true,
		onchange:null,
		thumbnail:'small',
		before_change:function(files, dropped) {
			var count = files.length;
			for (var int = 0; int < files.length; int++) {
				var imageName = files[int].name;
				var hashName = imageName.split(".");
				var extended = hashName.pop();
				if (extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
					listImage.push(files[int]);
				}
			}
			return true;
		},
		before_remove : function() {
			listImage = [];
			return true;
		}

	}).on('change', function(){
//		listImage = [];
	});
});
</script>