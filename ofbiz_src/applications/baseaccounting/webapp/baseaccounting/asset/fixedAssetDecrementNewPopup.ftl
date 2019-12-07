<div id="newFADec" style="display:none">
	<div>${uiLabelMap.BACCNewFADecrement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewDepPeriod">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCDecreasedDate}</label>
							</div>
							<div class='span7'>
								<div id="decreasedDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCDecrementTypeId}</label>
							</div>
							<div class='span7'>
								<div id="enumId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCDescription}</label>
							</div>
							<div class='span7'>
								<textarea id="comments" class="text-popup" style="width: 78% !important"></textarea>
					   		</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBNewFADec = function(){
	}
	
	OLBNewFADec.initWindow = function(){
		$("#newFADec").jqxWindow({
			width: '600', height: 300, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				$("#enumId").jqxDropDownList({ source: enumData, placeHolder: '${uiLabelMap.filterchoosestring}', theme: THEME, width: '80%', height: '25px', valueMember: 'enumId', displayMember: 'description'});
				$("#decreasedDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: THEME});
			}
		});
		
		OLBNewFADec.initValidate();
	}
	
	OLBNewFADec.openWindow = function(){
		$("#newFADec").jqxWindow('open');
	}
	
	OLBNewFADec.closeWindow = function(){
		$("#newFADec").jqxWindow('close');
	}
	
	OLBNewFADec.validateForm = function(){
		$('#newFADec').jqxValidator('validate');
	}
	
	OLBNewFADec.bindEvent = function(){
		$('#newFADec').on('close',function(){
			Grid.clearForm($('#newFADec'));
		})
		
		
		$('#alterSave').on('click', function(){
			OLBNewFADec.validateForm();
		});
		
		$('#newFADec').on('validationSuccess', function(){
			var submitedData = {};
			submitedData['decrementTypeId'] = 'FA_DECREMENT';
			submitedData['enumId'] = $("#enumId").val();
			var decreasedDate = ($('#decreasedDate').jqxDateTimeInput('getDate'));
			submitedData['decreasedDate'] = accutils.getTimestamp(decreasedDate);
			submitedData['description'] = $('#comments').val();
			//Send Ajax Request
			$.ajax({
				url: 'createDecrement',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBNewFADec.closeWindow();
						$('#faDecGrid').jqxGrid('updatebounddata');
						$('#containerfaDecGrid').empty();
                        $('#jqxNotificationfaDecGrid').jqxNotification({ template: 'success'});
                        $("#notificationContentfaDecGrid").text(wgaddsuccess);
                        $("#jqxNotificationfaDecGrid").jqxNotification("open");
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		});
	}
	
	OLBNewFADec.initValidate = function(){
		$('#newFADec').jqxValidator({
			rules : [
			         {input : '#enumId',message :'${uiLabelMap.validFieldRequire}', action: 'keyup, change, close' ,rule : function(input){
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}			         	
			         },
			         {input : '#decreasedDate',message :'${uiLabelMap.validFieldRequire}', action: 'keyup, change, close',rule : function(input){
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
			         }
			]
		})
	}	
	
	OLBNewFADec.init  = function(){
		this.initWindow();
		this.initValidate();
		this.bindEvent();
	}
	
	$(function(){
		OLBNewFADec.init();
	})
</script>