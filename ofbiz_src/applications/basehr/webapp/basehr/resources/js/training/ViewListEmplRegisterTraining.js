var listEmplRegisterObj = (function(){
	var _lock = false;
	var init = function(){
		$("#jqxNotificationjqxgrid").jqxNotification({width: "100%", opacity: 1, appendContainer: "#containerjqxgrid", autoOpen: false, autoClose: true});
		$("#jqxgrid").on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(args.rowindex instanceof Array){
				var paginginformation = $('#jqxgrid').jqxGrid('getpaginginformation');
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var startIndex = pagenum * pagesize;
				var endIndex = startIndex + pagesize;
				for(var i = startIndex; i < endIndex; i++){
					var data = $('#jqxgrid').jqxGrid('getrowdata', i);
					if(data && data.statusIdRegister != 'TCR_REGIS'){
						$('#jqxgrid').jqxGrid('unselectrow', i);
					}
				}
			}else{
				if(rowData.statusIdRegister != 'TCR_REGIS'){
					$('#jqxgrid').jqxGrid('unselectrow', args.rowindex);
				}
			}
		});
	};
	var approvalRegister = function(type){
		if(!_lock){
			_lock = true;
			var selectedRow = $('#jqxgrid').jqxGrid('getselectedrowindexes');
			if(selectedRow.length <= 0){
				_lock = false;
				return;
			}
			var warningMessage = uiLabelMap.TrainingRejectRegisterConfirm;
			var accept = "N";
			if(type == "accept"){
				accept = "Y";
				warningMessage = uiLabelMap.TrainingAcceptRegisterConfirm;
			}
			bootbox.dialog(warningMessage,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							var partyIds = [];
							selectedRow.forEach(function(rowIndex){
								var row = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
								partyIds.push(row.partyId);
							});
							executeApproval(partyIds, accept);
						}	
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
						"callback": function() {
							_lock = false;
						}
					}]		
			);
		}
	};
	var executeApproval = function(partyIds, accept){
		$('#jqxgrid').jqxGrid('showloadelement');
		$('#jqxgrid').jqxGrid({disabled: true});
		$.ajax({
			url: 'approvalEmplRegisterTraining',
			data: {partyIds: JSON.stringify(partyIds), accept: accept, trainingCourseId: globalVar.trainingCourseId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#jqxgrid").jqxGrid('clearselection');
				}else{
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				_lock = false;
				$('#jqxgrid').jqxGrid('hideloadelement');
				$('#jqxgrid').jqxGrid({disabled: false});
			}
		});
	};
	return{
		approvalRegister: approvalRegister,
		init: init
	}
}());

$(document).ready(function(){
	listEmplRegisterObj.init();
});