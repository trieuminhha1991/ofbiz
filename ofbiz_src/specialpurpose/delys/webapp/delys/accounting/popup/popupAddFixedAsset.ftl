<style>
	.ui-autocomplete.ui-menu.ui-widget.ui-widget-content.ui-corner-all {
		z-index:18005!important;
	}
	.view-calendar input,.field-lookup input {
		width:167px;
	}
</style>
<div id="alterpopupWindow" class='hide'>
    <div>${uiLabelMap.NewFixedAsset}</div>
    <div class='form-window-container'>
    	<div class='form-window-content'>
    		<!--asset basic info-->
    		<form name="AddFixedAsset">
    	 	<div class="row-fluid">
		 		<div class="span6">
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label class='asterisk'>${uiLabelMap.AccountingFixedAssetName}</label>		
 						</div>
		 				<div class='span7'>
		 					<input id="fixedAssetNameAdd"/>
		 				</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label>${uiLabelMap.AccountingFixedAssetTypeId}</label>		
 						</div>
		 				<div class='span7'>
		 					<div id="fixedAssetTypeIdAdd"></div>
		 				</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label>${uiLabelMap.Owner}</label>		
 						</div>
		 				<div class='span7'>
		 					<@htmlTemplate.lookupField name="ownerPartyId" id="ownerPartyId" value='' size="14" width="1150" height="700" zIndex="18005"
								formName="AddFixedAsset"  fieldFormName="LookupJQOwner" title="${uiLabelMap.CommonSearch} ${uiLabelMap.Owner}" />
		 				</div>
		 			</div>
	 			</div>
		 		<div class="span6">
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label>${uiLabelMap.AccountingFixedAssetId}</label>		
 						</div>
		 				<div class='span7'>
		 					<input id="fixedAssetIdAdd"/>
		 				</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label>${uiLabelMap.FormFieldTitle_parentFixedAssetId}</label>		
 						</div>
		 				<div class='span7'>
		 					<@htmlTemplate.lookupField name="parentFixedAssetIdAdd" id="parentFixedAssetIdAdd" value='' size="14" width="1150" height="700" zIndex="18005"
								formName="AddFixedAsset" fieldFormName="LookupJQParentFixedAssets" title="${uiLabelMap.CommonSearch} ${uiLabelMap.FormFieldTitle_parentFixedAssetId}" />
		 				</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
	 						<label>${uiLabelMap.accRoleTypeId}</label>		
 						</div>
		 				<div class='span7'>
		 					<div id="roleTypeIdAdd"></div>
		 				</div>
		 			</div>
		 		</div>
	 		</div> 
	 		<!--asset info-->
	 		<div class='row-fluid'>
	 			<div class='span12 bordertop'>
	 				<div class='row-fluid'>
	 					<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
									<label class='asterisk'>${uiLabelMap.serialNumber}</label>		
								</div>
				 				<div class='span7'>
				 					<input id="serialNumberAdd"/>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
									<label>${uiLabelMap.FormFieldTitle_productionCapacity}</label>		
								</div>
				 				<div class='span7'>
				 					<input id="productionCapacityAdd"/>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5  align-right'>
			 						<label>${uiLabelMap.DAUom}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="uomIdAdd"></div>
				 				</div>
				 			</div>
			 			</div>
			 			<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
									<label>${uiLabelMap.FormFieldTitle_acquireOrderId}</label>		
								</div>
				 				<div class='span7'>
				 					<input id="acquireOrderId"/>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
			 						<label class="asterisk">${uiLabelMap.FormFieldTitle_dateAcquired}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="dateAcquired"></div>
				 				</div>
				 			</div>
			 			</div>
	 				</div>
	 			</div>
	 		</div>
	 		<!--asset location info-->
	 		<div class='row-fluid'>
	 			<div class='span12 bordertop'>
	 				<div class='row-fluid'>
	 					<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
			 						<label class=" align-right">${uiLabelMap.accLocatedAtFacilityId}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="locatedAtFacilityIdAdd"></div>
				 				</div>
				 			</div>
			 			</div>
			 			<div class='span6'>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
			 						<label class=" align-right">${uiLabelMap.accLocatedAtLocationSeqId}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="locatedAtLocationSeqIdAdd"></div>
				 				</div>
				 			</div>
			 			</div>
	 				</div>
	 			</div>
	 		</div>
	 		<!--asset date fixed ...-->
	 		<div class='row-fluid'>
	 			<div class='span12 bordertop'>
			 		<div class='row-fluid'>
			 			<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
			 						<label>${uiLabelMap.FormFieldTitle_dateLastServiced}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="dateLastServiced"></div>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right '>
			 						<label class="asterisk">${uiLabelMap.FormFieldTitle_expectedEndOfLife}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="expectedEndOfLife"></div>
				 				</div>
				 			</div>
			 			</div>
			 			<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5 align-right'>
			 						<label>${uiLabelMap.FormFieldTitle_dateNextService}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="dateNextService"></div>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
			 						<label class=" align-right">${uiLabelMap.FormFieldTitle_actualEndOfLife}</label>		
		 						</div>
				 				<div class='span7'>
				 					<div id="actualEndOfLife"></div>
				 				</div>
				 			</div>
			 			</div>
			 		</div>
		 		</div>
	 		</div>
	 		<!--asset value info-->
	 		<div class='row-fluid'>
	 			<div class='span12 bordertop'>
			 		<div class='row-fluid'>
			 			<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
			 						<label class=" align-right">${uiLabelMap.FormFieldTitle_salvageValue}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="salvageValue"></div>
				 				</div>
				 			</div>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
									<label class=" align-right">${uiLabelMap.FormFieldTitle_purchaseCost}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="purchaseCost"></div>
				 				</div>
				 			</div>
			 			</div>
			 			<div class='span6'>
			 				<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
			 						<label class=" align-right">${uiLabelMap.AccountingDepreciation}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="depreciation"></div>
				 				</div>
				 			</div>
				 			<div class='row-fluid margin-bottom10'>
				 				<div class='span5'>
									<label class=" align-right">${uiLabelMap.FormFieldTitle_purchaseCostUomId}</label>		
								</div>
				 				<div class='span7'>
				 					<div id="purchaseCostUomIdAdd"></div>
				 				</div>
				 			</div>
			 			</div>
			 		</div>
			 	</div>
	 		</div>
        </div>
        </form>
        <div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div>
</div>

<script type="text/javascript">
	var fixedAsset = (function createFixedAssetInstance(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}
		var form = $("#alterpopupWindow");
		var grid = $("#jqxgrid");
		var initWindow = function(){
			form.jqxWindow({
		        width: 900, maxWidth: 1000,maxHeight : 1000, height: 650, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme         
		    });
		    form.on('close', function(){
		    	form.jqxValidator("hide");
		    });
		};
		var initElement = function(){
			$("#fixedAssetIdAdd").jqxInput({width: '202px', height: '22px'});	
			$("#fixedAssetNameAdd").jqxInput({width: '202px', height: '22px'});	
			$("#acquireOrderId").jqxInput({width: '202px', height: '22px'});	
			$("#serialNumberAdd").jqxInput({width: '202px', height: '22px'});	
			$("#productionCapacityAdd").jqxInput({width: '202px', height: '22px'});
			$("#expectedEndOfLife").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl, clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#actualEndOfLife").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl, clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#expectedEndOfLife").jqxDateTimeInput("val",null);
			$("#actualEndOfLife").jqxDateTimeInput("val",null);
			$("#dateAcquired").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl, clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#dateLastServiced").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl, clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#dateNextService").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl, clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#dateAcquired").jqxDateTimeInput("val",null);
			$("#dateLastServiced").jqxDateTimeInput("val",null);
			$("#dateNextService").jqxDateTimeInput("val",null); 
			$("#fixedAssetTypeIdAdd").jqxDropDownList({source: fixedAssetTypeData, width: '208px', dropDownWidth: 350, displayMember:"description",valueMember: "fixedAssetTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#uomIdAdd").jqxDropDownList({source: dataUomAndTypeListView,  filterable: true,  width: '208px', dropDownWidth: 350, displayMember:"description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#roleTypeIdAdd").jqxDropDownList({source: dataRoleTypeListView,  filterable: true,  width: '208px', displayMember:"description", valueMember: "roleTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#purchaseCostUomIdAdd").jqxDropDownList({source: dataUomCurrencyListView,  filterable: true,  width: '208px', dropDownHeight: 100, displayMember:"description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#locatedAtFacilityIdAdd").jqxDropDownList({source: dataFacilityListView,  width: '208px', displayMember:"facilityName", valueMember: "facilityId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#salvageValue").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });
			$("#depreciation").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });
			$("#purchaseCost").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });
			$("#locatedAtLocationSeqIdAdd").jqxDropDownList({source: [],  width: '208px', displayMember:"description",valueMember: "locationId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		};
		var ChangeLocationFacility = function(){
			$('#locatedAtFacilityIdAdd').on('select', function (event) {
		        var args = event.args;
		        var item = $('#locatedAtFacilityIdAdd').jqxDropDownList('getItem', args.index);
		        if (item != null) {            	
		        	var facilityId = item.value;
					$.ajax({
						url: "<@ofbizUrl>getLocationFacility</@ofbizUrl>",
						type: "POST",
						dataType: 'json',
						async:false,
						data: {
							facilityId: facilityId
						},
				    	success: function(data) {
				    		var listLocatedAtLocationSeq = data.listIterator;
			            	var dataLocatedAtLocationSeqListView = new Array();
			            	var row = {};
			            	row['locationId'] = '';
			            	row['description'] = '';
			            	dataLocatedAtLocationSeqListView[0] = row;
			            	for (i = 0; i < listLocatedAtLocationSeq.length; i++)
			            		{
		    	            		var temp = listLocatedAtLocationSeq[i];
		    	            		var row = {};
		    	            		row['locationId'] = temp['locationId'];
		    	            		row['description'] = temp['description'];
		    	            		dataLocatedAtLocationSeqListView[i + 1] = row;
			            		}
			            	$("#locatedAtLocationSeqIdAdd").jqxDropDownList({source: dataLocatedAtLocationSeqListView});
				    		
				    	},
				    	error: function(data){
				    	}    				
					});    			
		        }
		    });
		};
		var bindEvent = function(){
			form.on('close',function(){
				$("input[name='ownerPartyId']").css('background-color','#ffff');
				$("input[name='ownerPartyId']").val('');
				$("input[name='parentFixedAssetIdAdd']").css('background-color','#ffff');
				$("input[name='parentFixedAssetIdAdd']").val('');
			});
			$("#alterSave").click(function () {
			    var row = getData();
			    if(!saveAction()) return;  
	       		form.jqxWindow('close');
		    });
		    
		    $("#saveAndContinue").click(function () {
			    var row = getData();
			    saveAction();
		    });
		};
		var saveAction = function(){
			if(!form.jqxValidator('validate')){
			    return false;	
	        }
	        var row = getData();
			grid.jqxGrid('addRow', null, row, "first");
	        grid.jqxGrid('clearSelection');                        
	        grid.jqxGrid('selectRow', 0);
	        GridUtils.clearForm(form);
	        return true;  
		};
		var getData = function(){
			var row = { 
        		expectedEndOfLife:$('#expectedEndOfLife').jqxDateTimeInput('getDate'),
        		actualEndOfLife:$('#actualEndOfLife').jqxDateTimeInput('getDate'),
        		serialNumber:$('#serialNumberAdd').val(),
        		acquireOrderId:$('#acquireOrderId').val(),
        		fixedAssetName:$('#fixedAssetNameAdd').val(),
        		fixedAssetId:$('#fixedAssetIdAdd').val(),
        		fixedAssetTypeId:$('#fixedAssetTypeIdAdd').val(),
        		parentFixedAssetId:$('input[name=parentFixedAssetIdAdd]').val(),
        		partyId: $('input[name=ownerPartyId]').val(),
        		roleTypeId:$('#roleTypeIdAdd').val(),
        		dateAcquired: $('#dateAcquired').jqxDateTimeInput('getDate'),  
        		dateLastServiced: $('#dateLastServiced').jqxDateTimeInput('getDate'),
        		dateNextService: $('#dateNextService').jqxDateTimeInput('getDate'),
        		productionCapacity:$('#productionCapacityAdd').val(),
        		uomId:$('#uomIdAdd').val(),
        		locatedAtFacilityId:$('#locatedAtFacilityIdAdd').val(),
        		locatedAtLocationSeqId:$('#locatedAtLocationSeqId').val(),
        		salvageValue:$('#salvageValue').val(),
        		depreciation:$('#depreciation').val(),
        		purchaseCost:$('#purchaseCost').val(),
        		purchaseCostUomId:$('#purchaseCostUomIdAdd').val()
    	   };
    	   return row;
		};
		var initRule = function(){
			form.jqxValidator({
				position: 'topleft',
		        rules: [{
		        		input: "#fixedAssetNameAdd", message: '${uiLabelMap.CommonRequired}', action: 'blur', rule: 'required'
		        	},{ input: '#fixedAssetTypeIdAdd', message: '${uiLabelMap.CommonRequired}', action: 'change', 
			        	rule: function(input){
		            	   var val = input.jqxDropDownList('getSelectedIndex');
		            	  	return val != -1;
		            	}
	               },{ input: '#expectedEndOfLife', message: '${uiLabelMap.CommonRequired}', action: 'change', 
			        	rule: function(input){
		            	   var val = $("#expectedEndOfLife").jqxDateTimeInput('val');
		            	   if(!val){
		            	  	return false;
		            	   }
		            	   return true;
		               }
	               },{ input: '#expectedEndOfLife', message: '${uiLabelMap.ExprectedLifeLargerThanDateAcquired}', action: 'change', 
			        	rule: function(input){
		            	   var val = input.jqxDateTimeInput('val');
		            	   var from = $("#dateAcquired").jqxDateTimeInput('val');
		            	   if(val && from && from > val){
		            	   		return false;
		            	   }
		            	   return true;
		               }
	               },{ input: '#dateAcquired', message: '${uiLabelMap.CommonRequired}', action: 'change', 
	            	    rule: function(input){
	                	   var val = input.jqxDateTimeInput('val');
	                	   if(val==""){
	                	   return false;
	                	   }
	                	   return true;
	              	  }
	  	  			},{
	                   input: '#dateNextService', 
	                   message: '${StringUtil.wrapString(uiLabelMap.faDateNextServicesLTDateLastServices)}', 
	                   action: 'change', 
	                   rule: function (input, commit) {
	                       var dateNextService = input.jqxDateTimeInput('value');
	                       var dateLastService = $('#dateLastServiced').jqxDateTimeInput('value');
	                       if (!dateNextService || dateNextService > dateLastService)
	                    	   return true;
	                       return false;
	                   }
	               },{ input: '#dateLastServiced', message: '${uiLabelMap.DateLastServicedLargerThanDateAcquired}', action: 'change', 
			        	rule: function(input){
		            	   var val = input.jqxDateTimeInput('val');
		            	   var from = $("#dateAcquired").jqxDateTimeInput('val');
		            	   if(val && from && from >= val){
		            	   		return false;
		            	   }
		            	   return true;
		               }
	               },{ input: '#dateNextService', message: '${uiLabelMap.DateNextServicedLargerThanDateAcquired}', action: 'change', 
			        	rule: function(input){
		            	   var val = input.jqxDateTimeInput('val');
		            	   var from = $("#dateAcquired").jqxDateTimeInput('val');
		            	   if(val && from && from >= val){
		            	   		return false;
		            	   }
		            	   return true;
		               }
	               }
           		]
	    	});	
		};
		return {
			init: function(){
				initWindow();
				initElement();
				ChangeLocationFacility();
				bindEvent();
				initRule();
			}
		};
	})();
    $(document).ready(function(){
    	fixedAsset.init();
    });
</script>	