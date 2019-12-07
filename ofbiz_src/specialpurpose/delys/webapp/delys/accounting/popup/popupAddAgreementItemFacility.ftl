 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accFacilityName}
	    				</div>
	    				<div class='span7'>
							<div id="facilityIdAdd">
	 							<div id="jqxFacilityGrid"></div>
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

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var popupAction = (function(){
		var initElement = function(){
			$("#alterpopupWindow").jqxWindow({
			        width: 600,height : 140, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
			initGrid();
		}
		var initGrid = function(){
				var datafields = [
						      { name: 'facilityId', type: 'string' },
						      { name: 'facilityName', type: 'string' }
				];
				var columns = [
					{ text: '${uiLabelMap.accFacilityId}', datafield: 'facilityId', width: '50%'},
				{ text: '${uiLabelMap.accFacilityName}', datafield: 'facilityName'}
				];
				GridUtils.initDropDownButton({url: 'JQGetListFacilitys',width : 500,filterable : true,dropdown : {width : 250,height :25}},
					datafields,columns,null,$('#jqxFacilityGrid'),$('#facilityIdAdd'),'facilityId');
		}
		
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
			var row;
	        row = { 
	        		facilityId:$('#facilityIdAdd').jqxDropDownButton('val')
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
			return true;
		}
		
		var saveAndContinue  = function(){
			save();
			clear();
		}
		var clear = function(){
			$("#facilityIdAdd").jqxDropDownButton('val', '');	
			$('#jqxFacilityGrid').jqxGrid('clearSelection'); 
		};
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#facilityIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(){
						var val = $('#facilityIdAdd').val();
						if(!val) return false;
						return true;
					}},
					{input : '#facilityIdAdd',message : '${StringUtil.wrapString(uiLabelMap.NotiFacilityonAgreement?default(''))}',action : 'change,close',rule : function(){
						if(!filterFacilityAdd()) return false;
						return true;
					}}
				]
			})
			
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
		
		var getDataAdd = function(){
			var row = {
				facilityId:$('#facilityIdAdd').jqxDropDownButton('val')
			}
			return row;
		}
		
		var filterFacilityAdd = function(){
			var listAgreementFacility = $('#jqxgrid').jqxGrid('getboundrows');
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
			if(listAgreementFacility && listAgreementFacility.length > 0 && typeof(dataAdd) != 'undefined' && dataAdd != null ){
				var result = false;
				$.each(listAgreementFacility,function(){
					if($(this)[0].agreementId == dataAdd.agreementId && $(this)[0].agreementItemSeqId == dataAdd.agreementItemSeqId && $(this)[0].facilityId == dataAdd.facilityId){
						result = true;
						return false;
					}
				})
			}
			if(result) return false;
			return true;
		}
		
		
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			},
			filterFacilityAdd : filterFacilityAdd
		}
	}());
	
   $(document).ready(function(){
   		popupAction.init();
   })
</script>