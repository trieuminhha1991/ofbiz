<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accPartyName}
	    				</div>
	    				<div class='span7'>
							 <div id="jqxdropdownbuttonToParty">
						       	 <div id="jqxgridToParty"></div>
					       	</div>
	    				</div>
					</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	

<script>
	$.jqx.theme  = 'olbius';
	theme = $.jqx.theme;
	var popupAction = (function(){
		var initElement = function(){
			$("#alterpopupWindow").jqxWindow({
			        theme : theme,width: 600,height : 140, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
		    initDropdownParty();
		}
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#jqxdropdownbuttonToParty',message : '${StringUtil.wrapString(uiLabelMap.NotiPartyonAgreement?default(''))}',action : 'change,close',rule : function(){
						if(!filterPartyAdd()) return false;
						return true;
					}},
					{input : '#jqxdropdownbuttonToParty',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(){
						var val = $('#jqxdropdownbuttonToParty').val();
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var initDropdownParty = function(){
			var datafields = 
		        [
		            { name: 'partyId', type: 'string' },
		            { name: 'partyTypeId', type: 'string' },
		            { name: 'firstName', type: 'string' },
		            { name: 'lastName', type: 'string' },
		            { name: 'groupName', type: 'string' }
		        ];
				var columns = [
					{ text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width :'20%'},
			          { text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width :'20%'},
			          { text: '${uiLabelMap.accFirstName}', datafield: 'firstName', width :'20%'},
			          { text: '${uiLabelMap.accLastName}', datafield: 'lastName', width :'20%'},
			          { text: '${uiLabelMap.accGroupName}', datafield: 'groupName'}
				];
				GridUtils.initDropDownButton({url: 'getFromParty',width : 500,filterable : true,dropdown : {width : 250,height :25}},
					datafields,columns,null,$('#jqxgridToParty'),$('#jqxdropdownbuttonToParty'),'partyId');
		}
		
		var save = function(){
		if(!$('#formAdd').jqxValidator('validate')){return;}
			var row;
	        row = { 
	        		partyId:$('#jqxdropdownbuttonToParty').jqxDropDownButton('val')
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
			return true;
		}
		
		var saveAndContinue  = function(){
			save();
			clear();
		}
		
		var clear = function(){
			$("#jqxdropdownbuttonToParty").jqxDropDownButton('val', '');	
			$('#jqxgridToParty').jqxGrid('clearSelection'); 
		};
		
		var getDataAdd = function(){
			var row = {
				partyId:$('#jqxdropdownbuttonToParty').jqxDropDownButton('val')
			}
			return row;
		}
		
		var filterPartyAdd = function(){
			var listParty = $('#jqxgrid').jqxGrid('getboundrows');
			var dataAdd = getDataAdd();
			<#if parameters.agreementId?exists>
				dataAdd.agreementId = '${parameters.agreementId?if_exists}';
			<#else>
				dataAdd.agreementId = "";
			</#if>
			<#if parameters.agreementItemSeqId?exists>
				dataAdd.agreementItemSeqId = '${parameters.agreementItemSeqId?if_exists}';
			<#else>
				dataAdd.agreementItemSeqId = "";	
			</#if>
			if(listParty && listParty.length > 0 && typeof(dataAdd) != 'undefined' && dataAdd != null ){
				var result = false;
				$.each(listParty,function(){
					if($(this)[0].agreementId == dataAdd.agreementId && $(this)[0].agreementItemSeqId == dataAdd.agreementItemSeqId && $(this)[0].partyId == dataAdd.partyId){
						result = true;
						return false;
					}
				})
			}
			if(result) return false;
			return true;
		}
		
		var bindEvent = function(){
		    $("#save").click(function () {
		    	if(!save()){
		    		return false;
		    	}
		    	clear();
		    	$("#alterpopupWindow").jqxWindow('close');
		    });
			
			$('#saveAndContinue').click(function(){
				saveAndContinue();
			});
			
			$('#cancel').click(function(){
				clear();
			})
		}
		
		return {
			 init : function(){
			 	initElement();
			 	bindEvent();
			 	initRules();
			 }
		 }
	}());
	$(document).ready(function(){
		popupAction.init();
	})
</script>