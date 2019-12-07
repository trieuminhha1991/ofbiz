<#assign dataFields =  "[{name: 'partyId', type: 'string'},
						  {name: 'emplName', type: 'string'},						  
						  {name: 'partyIdFrom', type:'string'},
						  {name: 'emplPositionTypeId', type: 'string'}]">

<#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId' ,filtertype: 'input', editable: false, cellsalign: 'left', width: 130},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'emplName', filtertype: 'input', editable: false, cellsalign: 'left', width: 130},
						{text: '${uiLabelMap.CommonDepartment}', datafield: 'partyIdFrom', filtertype: 'input', editable: false, cellsalign: 'left', width: 130},
						{text: '${uiLabelMap.CommonEmplPositionType}', datafield: 'emplPositionTypeId', filtertype: 'input', editable: false, cellsalign: 'left'}"/>
						
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"  
		 filterable="false"  editable="false" addrow="false" selectionmode='multiplerows' bindresize="false" width="'100%'" viewSize="10"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JqxEmplListInOrg&partyId=${parameters.partyId}" id="jqxgridListEmpl" removeUrl="" deleteColumn=""
		 updateUrl="" jqGridMinimumLibEnable="false" 
		 editColumns=""
	/>						

						
<div class="row-fluid">
	<div class="" style="text-align: center;">
		<input type="button" value="${uiLabelMap.CommonSubmit}" id='submitParty' />
		<!-- <input type="button" value="${uiLabelMap.CommonClose}" id='cancelSubmit' /> -->
	</div>
</div>
<style>
<!--
	.customZIndex{z-index: 20000}
-->
</style>
<script type="text/javascript">
	$(document).ready(function () {
		$("#submitParty").click(function(){
			
			bootbox.dialog({
				  message: "${StringUtil.wrapString(uiLabelMap.NotifyForEmployee)}",
				  buttons: {
				    success: {
				      label: "${StringUtil.wrapString(uiLabelMap.CommonSend)}",
				      className: "btn-primary btn-mini icon-ok",
				      callback: function() {
				    	submitData(true);	  						    	  
				      }
				     },
				     cancel:{
				    	 label: "${StringUtil.wrapString(uiLabelMap.CommonNotSend)}",
					      className: "btn-danger btn-mini btn-remove",
					      callback: function() {
					    	submitData(false);	  						    	  
					      } 
				     }
				  },
				  className: "customZIndex"
				});	
		});
		$("#submitParty").jqxButton({width: '150', height: '30', theme: 'olbius'});	   
	});
	function submitData(notify){
		var partyListSubmit = new Array();
		var indexsSelected = $('#jqxgridListEmpl').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < indexsSelected.length; i++){
			var data = $('#jqxgridListEmpl').jqxGrid('getrowdata', indexsSelected[i]);
			partyListSubmit.push({partyId: data.partyId});
		}
			
		
		if(partyListSubmit.length > 0){
			var trainingCourseId = "${parameters.trainingCourseId}";
			$('#submitParty').jqxButton({disabled: true });
			$.ajax({
				url: "<@ofbizUrl>${parameters.ajaxUrl}</@ofbizUrl>",
				type: 'POST',
				data:{trainingCourseId: trainingCourseId, partyIds: JSON.stringify(partyListSubmit), sendNotify: notify},
				success: function(data){
					window.location.reload();
				},
				complete: function(jqXHR, textStatus){
					$('#submitParty').jqxButton({disabled: false });
				}
			});
		}
	}
</script>							  