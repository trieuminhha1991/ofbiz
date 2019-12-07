 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accGeoName}
    				</div>
    				<div class='span7'>
						<div id="geoIdAdd">
 					<div id="jqxGeoGrid"></div>
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
						      { name: 'geoId', type: 'string' },
						      { name: 'geoName', type: 'string' }
				];
				var columns = [
					{ text: '${uiLabelMap.accGeoId}', datafield: 'geoId', width: '50%'},
					{ text: '${uiLabelMap.accGeoName}', datafield: 'geoName'}
				];
				GridUtils.initDropDownButton({url: 'JQGetListGeos',width : 500,filterable : true,dropdown : {width : 250,height :25}},
					datafields,columns,null,$('#jqxGeoGrid'),$('#geoIdAdd'),'geoId');
		}
		
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
			var row;
	        row = { 
	        		geoId:$('#geoIdAdd').jqxDropDownButton('val')
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
			$("#geoIdAdd").jqxDropDownButton('val', '');	
			$('#jqxGeoGrid').jqxGrid('clearSelection'); 
		};
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#geoIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(){
						var val = $('#geoIdAdd').val();
						if(!val) return false;
						return true;
					}},
					{input : '#geoIdAdd',message : '${StringUtil.wrapString(uiLabelMap.NotiGeoonAgreement?default(''))}',action : 'change,close',rule : function(){
						if(!filterGeoAdd()) return false;
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
				geoId:$('#geoIdAdd').jqxDropDownButton('val')
			}
			return row;
		}
		
		var filterGeoAdd = function(){
			var listAgreementGeo = $('#jqxgrid').jqxGrid('getboundrows');
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
			if(listAgreementGeo && listAgreementGeo.length > 0 && typeof(dataAdd) != 'undefined' && dataAdd != null ){
				var result = false;
				$.each(listAgreementGeo,function(){
					if($(this)[0].agreementId == dataAdd.agreementId && $(this)[0].agreementItemSeqId == dataAdd.agreementItemSeqId && $(this)[0].geoId == dataAdd.geoId){
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
			filterGeoAdd : filterGeoAdd
		}
	}());
	
   $(document).ready(function(){
   		popupAction.init();
   })
</script>