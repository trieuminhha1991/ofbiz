$(function(){
	OlbGlAccountOrg.init();
})
var OlbGlAccountOrg = (function(){
		var validatorVAL;
		var accCm = new accCommon();
		var init = function(){
			accCm.DropDownUtils.initDropDownGlAccountOrg('getListGLAccountChart',$('#glAccountId2'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 250,labels : uiLabelMap});
			initjqxWindow();
			initValidator();
			bindEvent();
		}
		
		var updateListGL = function(){
			$('#jqxgridGlAccount').jqxGrid('updatebounddata');
		}
		
		var save = function(){
			if(validatorVAL.validate()){
			    		var row;
				        row = {
				        		glAccountId:$('#glAccountId2').val(),
				        		organizationPartyId  : organizationPartyId
				        	  };
					   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
				       $("#alterpopupWindow").jqxWindow('close');
			    	}else{
			    		return;
			    	}
		}
		
		var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
		        width: 470, height: 150, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		var bindEvent = function(){
			 // update the edited row when the user clicks the 'Save' button.
			$('#alterpopupWindow').on('close', function (event) {
			    	$('#alterpopupWindow').jqxValidator('hide');
			    });
		    $("#alterSave").click(function () {
			    	save();
			    });
			 $('#alterpopupWindow').on('close',function(){
			 	 $('#glAccountId2').jqxDropDownButton('val','');
			 	 $('#jqxgridGlAccount').jqxGrid('clearSelection');
			 })   
		}
		var initValidator = function(){
			var mapRules = [
			                {input: '#glAccountId2', type: 'validInputNotNull',action : 'change,close'}
		                ];
			validatorVAL = new OlbValidator($('#alterpopupWindow'),mapRules,null,{position : 'bottom'});
		}
        var dataPost;
		var deleteGlAccountOrganization = function () {
            var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            if(OlbCore.isNotEmpty(dataRecord)){
                bootbox.dialog(uiLabelMap.BACCConfirmDelete, [ {
                    "label" : uiLabelMap.BACCCancel,
                    "icon" : "fa fa-remove",
                    "class" : "btn  btn-danger form-action-button pull-right",
                    "callback" : function() {
                        bootbox.hideAll();
                        dataPost = null;
                    }
                }, {
                    "label" : uiLabelMap.BACCOK,
                    "icon" : "fa-check",
                    "class" : "btn btn-primary form-action-button pull-right",
                    "callback" : function() {
                        dataPost = {"glAccountId":dataRecord.glAccountId,"organizationPartyId":organizationPartyId};
                        deleteGlAccountOrganizationAjax(dataPost);
                    }
                } ]);
            }
        }

        var deleteGlAccountOrganizationAjax = function (dataPost) {
            $.ajax({
                type : "POST",
                url : "deleteGlAccountOrg",
                data : dataPost,
                beforeSend : function() {
                    $("#loader_page_common").show();
                },
                success : function(data) {
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                            $('#jqxNotification').jqxNotification('closeLast');
                            $('#jqxNotification').jqxNotification({ template: 'error'});
                            $("#jqxNotification").html(uiLabelMap.CannotDeleteRow);
                            $("#jqxNotification").jqxNotification("open");
                            return false;
                        }, function(){
                            $('#jqxNotification').jqxNotification('closeLast');
                            $('#jqxNotification').jqxNotification({ template: 'info'});
                            $("#jqxNotification").html(uiLabelMap.wgdeletesuccess);
                            $("#jqxNotification").jqxNotification("open");
                            $("#jqxgrid").jqxGrid('updatebounddata');
                        }
                    );
                },
                error : function(data) {
                    alert("Send request is error");
                },
                complete : function(data) {
                    $("#loader_page_common").hide();
                },
            });
        }
		
		return {
			init : init,
			updateListGL : updateListGL,
            deleteGlAccountOrganization : deleteGlAccountOrganization
		}
	}())
