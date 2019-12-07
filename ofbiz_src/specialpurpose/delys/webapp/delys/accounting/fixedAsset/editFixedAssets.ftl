<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<style>
	.ui-autocomplete.ui-menu.ui-widget.ui-widget-content.ui-corner-all {
		z-index:18005!important;
	}
	.view-calendar input,.field-lookup input {
		width:167px;
	}
</style>
<@jqGridMinimumLib/>
<script type="text/javascript">
	<#assign uomAndTypeList = delegator.findList("UomAndType", null, null, null, null, false) />
	var dataUomAndTypeListView = new Array();
	var row = {};
	row['uomId'] = '';
	row['description'] = '';
	dataUomAndTypeListView[0] = row;
	<#list uomAndTypeList as uomAndType >
		var row = {};
		row['uomId'] = '${uomAndType.uomId?if_exists}';
		row['description'] = "[" + '${uomAndType.typeDescription?if_exists}'  + "] " + '${uomAndType.description?if_exists}';
		dataUomAndTypeListView[${uomAndType_index} + 1] = row;
	</#list>	

	<#assign uomCurrencyList = delegator.findList("UomAndType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("typeUomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "CURRENCY_MEASURE"), null, null, null, false) />
	var dataUomCurrencyListView = new Array();
	var row = {};
	row['uomId'] = '';
	row['description'] = '';
	dataUomCurrencyListView[0] = row;
	<#list uomCurrencyList as uomCurrency >
		var row = {};
		row['uomId'] = '${uomCurrency.uomId?if_exists}';
		row['description'] = '${uomCurrency.description?if_exists}' + ' [' + '${uomCurrency.uomId?if_exists}' + ']';
		dataUomCurrencyListView[${uomCurrency_index} + 1] = row;
	</#list>	
	
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	var dataRoleTypeListView = new Array();
	var row = {};
	row['roleTypeId'] = '';
	row['description'] = '';
	dataRoleTypeListView[0] = row;
	<#list roleTypeList as roleType >
		var row = {};
		row['roleTypeId'] = '${roleType.roleTypeId?if_exists}';
		row['description'] = '${roleType.description?if_exists}';
		dataRoleTypeListView[${roleType_index} + 1] = row;
	</#list>	

	<#assign facilityList = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "WAREHOUSE"), null, null, null, false) />
	var dataFacilityListView = new Array();
	var row = {};
	row['facilityId'] = '';
	row['facilityName'] = '';
	dataFacilityListView[0] = row;
	<#list facilityList as facility >
		var row = {};
		row['facilityId'] = '${facility.facilityId?if_exists}';
		row['facilityName'] = '${facility.facilityName?if_exists}';
		dataFacilityListView[${facility_index} + 1] = row;
	</#list>	
	
	<#assign fixedAssetTypeList = delegator.findList("FixedAssetType",  null, null, null, null, false) />
	var fixedAssetTypeData = new Array();
	<#list fixedAssetTypeList as fixedAssetType>
		<#assign description = StringUtil.wrapString(fixedAssetType.get("description", locale)) />
		var row = {};
		row['description'] = "${description}";
		row['fixedAssetTypeId'] = "${fixedAssetType.fixedAssetTypeId}";
		fixedAssetTypeData[${fixedAssetType_index}] = row;
	</#list>	

 	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    }
</script>

<div class="basic-form form-horizontal" style="margin-top: 10px">
<form name="editFixedAsset" id="editFixedAsset">
	<div class="row-fluid" >
		<div class="span12">
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.AccountingFixedAssetName}:</label>
					<div class="controls">
						<input id="fixedAssetName"> </input>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.AccountingFixedAssetTypeId}:</label>
					<div class="controls">
						<div id="fixedAssetTypeId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.Owner}:</label>
					<div class="controls">
						<@htmlTemplate.lookupField name="ownerPartyId" id="ownerPartyId" value='' size="14" width="1150" height="700" zIndex="18005"
								formName="editFixedAsset"  fieldFormName="LookupJQOwner" title="${uiLabelMap.CommonSearch} ${uiLabelMap.Owner}" />
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_acquireOrderId}:</label>
					<div class="controls">
						<input id="acquireOrderId"></input>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_dateLastServiced}:</label>
					<div class="controls">
						<div id="dateLastServiced"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_expectedEndOfLife}:</label>
					<div class="controls">
						<div id="expectedEndOfLife"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.serialNumber}:</label>
					<div class="controls">
						<input id="serialNumber"></input>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_productionCapacity}:</label>
					<div class="controls">
						<input id="productionCapacity"></input>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.accLocatedAtFacilityId}:</label>
					<div class="controls">
						<div id="locatedAtFacilityId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_salvageValue}:</label>
					<div class="controls">
						<div id="salvageValue"></div>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_purchaseCost}:</label>
					<div class="controls">
						<div id="purchaseCost"></div>
					</div>
				</div>					
			</div>
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.AccountingFixedAssetId}:</label>
					<div class="controls">
						<input id="fixedAssetId"> </input>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_parentFixedAssetId}:</label>
					<div class="controls">
						<@htmlTemplate.lookupField name="parentFixedAssetId" id="parentFixedAssetId" value='' size="14" width="1150" height="700" zIndex="18005"
							formName="editFixedAsset" fieldFormName="LookupJQParentFixedAssets" title="${uiLabelMap.CommonSearch} ${uiLabelMap.FormFieldTitle_parentFixedAssetId}" />
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.accRoleTypeId}:</label>
					<div class="controls">
						<div id="roleTypeId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_dateAcquired}:</label>
					<div class="controls">
						<div id="dateAcquired"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_dateNextService}:</label>
					<div class="controls">
						<div id="dateNextService"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_actualEndOfLife}:</label>
					<div class="controls">
						<div id="actualEndOfLife"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<labelclass="control-label">  </label>
					<div class="controls"></div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.DAUom}:</label>
					<div class="controls">
						<div id="uomId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.accLocatedAtLocationSeqId}:</label>
					<div class="controls">
						<div id="locatedAtLocationSeqId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.AccountingDepreciation}:</label>
					<div class="controls">
						<div id="depreciation"></div>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.FormFieldTitle_purchaseCostUomId}:</label>
					<div class="controls">
						<div id="purchaseCostUomId"></div>
					</div>
				</div>					
			</div>			
		</div>
	</div>
</form>
</div>	    	 	

<div class="row-fluid" >
	<div class="span12" style="text-align: center">
		<button type="button" id='submit' class="btn btn-primary btn-small" >${uiLabelMap.CommonUpdate} <i class="icon-arrow-right"></i></button>
	</div>
</div>


	<script type="text/javascript">
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;

		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}		
		
		$("#fixedAssetId").jqxInput({width: '208px', height: '22px'});	
		$("#fixedAssetId").jqxInput({disabled: true });
		$("#fixedAssetName").jqxInput({width: '208px', height: '22px'});	
		$("#acquireOrderId").jqxInput({width: '208px', height: '22px'});	
		$("#serialNumber").jqxInput({width: '208px', height: '22px'});	
		$("#productionCapacity").jqxInput({width: '208px', height: '22px'});
		
		$("#expectedEndOfLife").jqxDateTimeInput({width: '208px', height: '25px',  formatString: 'dd/MM/yyyy', culture: tmpLcl});
		$("#actualEndOfLife").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
		$("#dateAcquired").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
		$("#dateLastServiced").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
		$("#dateNextService").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
		$("#fixedAssetTypeId").jqxDropDownList({source: fixedAssetTypeData, width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "fixedAssetTypeId"});
		$("#uomId").jqxDropDownList({source: dataUomAndTypeListView,  filterable: true,  width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "uomId"});
		$("#roleTypeId").jqxDropDownList({source: dataRoleTypeListView,  filterable: true,  width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});
		$("#purchaseCostUomId").jqxDropDownList({source: dataUomCurrencyListView,  filterable: true,  width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "uomId"});
		$("#locatedAtFacilityId").jqxDropDownList({source: dataFacilityListView,  width: '208px', displayMember:"facilityName", selectedIndex: 0 ,valueMember: "facilityId"});
		$("#salvageValue").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });		
		$("#depreciation").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });
		$("#purchaseCost").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, spinButtons: true });	

		$('#locatedAtFacilityId').on('select', function (event) {
            var args = event.args;
            var item = $('#locatedAtFacilityId').jqxDropDownList('getItem', args.index);
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
    	            	$("#locatedAtLocationSeqId").jqxDropDownList({source: dataLocatedAtLocationSeqListView,  width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "locationId"});
    		    		
    		    	},
    		    	error: function(data){
    		    	}    				
    			});    			
            }
        });

		<#assign fixedAssetToFill = delegator.findOne("FixedAsset", {"fixedAssetId" : '${parameters.fixedAssetId}'}, true) />	
		<#if (fixedAssetToFill.partyId?exists)>
			$('input[name=ownerPartyId]').val('${fixedAssetToFill.partyId?if_exists}');
		<#else>$('input[name=ownerPartyId]').val('')</#if>
		<#if (fixedAssetToFill.parentFixedAssetId?exists)>
			$('input[name=parentFixedAssetId]').val('${fixedAssetToFill.parentFixedAssetId?if_exists}');
		<#else>$('input[name=parentFixedAssetId]').val('')</#if>
		
		var expectedEndOfLife = '${fixedAssetToFill.expectedEndOfLife?if_exists}';		
		<#if (fixedAssetToFill.expectedEndOfLife?exists)>
			$("#expectedEndOfLife").val(new Date(${fixedAssetToFill.expectedEndOfLife.getTime()}));
		<#else> $("#expectedEndOfLife").val(null)</#if>;
				
		var actualEndOfLife = '${fixedAssetToFill.actualEndOfLife?if_exists}';
		<#if (fixedAssetToFill.actualEndOfLife?exists)>
			$("#actualEndOfLife").val(new Date(${fixedAssetToFill.actualEndOfLife.getTime()}));
		<#else> $("#actualEndOfLife").val(null)</#if>;

		var dateAcquired = '${fixedAssetToFill.dateAcquired?if_exists}';	
		<#if (fixedAssetToFill.dateAcquired?exists)>
			$("#dateAcquired").val(new Date(${fixedAssetToFill.dateAcquired.getTime()}));
		<#else> $("#dateAcquired").val(null)</#if>;

		var dateLastServiced = '${fixedAssetToFill.dateLastServiced?if_exists}';
		<#if (fixedAssetToFill.dateLastServiced?exists)>
			$("#dateLastServiced").val(new Date(${fixedAssetToFill.dateLastServiced.getTime()}));
		<#else> $("#dateLastServiced").val(null)</#if>;		
		
		var dateNextService = '${fixedAssetToFill.dateNextService?if_exists}';
		<#if (fixedAssetToFill.dateNextService?exists)>
			$("#dateNextService").val(new Date(${fixedAssetToFill.dateNextService.getTime()}));
		<#else> $("#dateNextService").val(null)</#if>;			
		
		
		$("#salvageValue").jqxNumberInput("val","${fixedAssetToFill.salvageValue?if_exists}");
		$("#depreciation").jqxNumberInput("val","${fixedAssetToFill.depreciation?if_exists}");
		$("#purchaseCost").jqxNumberInput("val","${fixedAssetToFill.purchaseCost?if_exists}");
		$("#fixedAssetName").jqxInput('val', "${StringUtil.wrapString(fixedAssetToFill.fixedAssetName?if_exists)}");	
		$("#fixedAssetId").jqxInput('val', "${fixedAssetToFill.fixedAssetId?if_exists}");	
		$("#serialNumber").jqxInput('val', "${fixedAssetToFill.serialNumber?if_exists}"); 
		$("#acquireOrderId").jqxInput('val', "${fixedAssetToFill.acquireOrderId?if_exists}");
		$("#productionCapacity").jqxInput('val', "${StringUtil.wrapString(fixedAssetToFill.productionCapacity?if_exists)}");
		$("#fixedAssetTypeId").jqxDropDownList('val','${fixedAssetToFill.fixedAssetTypeId?if_exists}');
		$("#uomId").jqxDropDownList('val','${fixedAssetToFill.uomId?if_exists}');
		$("#roleTypeId").jqxDropDownList('val','${fixedAssetToFill.roleTypeId?if_exists}');
		$("#purchaseCostUomId").jqxDropDownList('val','${fixedAssetToFill.purchaseCostUomId?if_exists}');
		$("#locatedAtFacilityId").jqxDropDownList('val','${fixedAssetToFill.locatedAtFacilityId?if_exists}');
		var locatedAtFacilityId = '${fixedAssetToFill.locatedAtFacilityId?if_exists}';
		if (locatedAtFacilityId)
			{
				$.ajax({
					url: "<@ofbizUrl>getLocationFacility</@ofbizUrl>",
					type: "POST",
					dataType: 'json',
					async:false,
					data: {
						facilityId: locatedAtFacilityId
					},
			    	success: function(data) {
			    		var listLocatedAtLocationSeq = data.listIterator;
		            	var dataLocatedAtLocationSeqListView = new Array();
		            	var locatedAtLocationSeq;
		            	var row = {};
		            	row['locationId'] = '';
		            	row['description'] = '';
		            	dataLocatedAtLocationSeqListView[0] = row;
		            	if (row['locationId'] == '${fixedAssetToFill.locationId?if_exists}') locatedAtLocationSeq = 0;
		            	for (i = 0; i < listLocatedAtLocationSeq.length; i++)
		            		{
	    	            		var temp = listLocatedAtLocationSeq[i];
	    	            		var row = {};
	    	            		row['locationId'] = temp['locationId'];
	    	            		row['description'] = temp['description'];
	    	            		dataLocatedAtLocationSeqListView[i + 1] = row;
	    	            		if (row['locationId'] == '${fixedAssetToFill.locationId?if_exists}') locatedAtLocationSeq = i + 1;
		            		}
		            	$("#locatedAtLocationSeqId").jqxDropDownList({source: dataLocatedAtLocationSeqListView,  width: '208px', displayMember:"description", selectedIndex: locatedAtLocationSeq ,valueMember: "locationId"});
			    		
			    	},
			    	error: function(data){
			    	}    				
				});    	
			}	 

		// update the edited row when the user clicks the 'Save' button.
	    $("#submit").click(function(){
	    	var row;
	        row = {				
	        		expectedEndOfLife: $('#expectedEndOfLife').jqxDateTimeInput('getDate') ? (new Date($('#expectedEndOfLife').jqxDateTimeInput('getDate'))).format("yyyy-mm-dd") : null,
	        		actualEndOfLife: $('#actualEndOfLife').jqxDateTimeInput('getDate') ? (new Date($('#actualEndOfLife').jqxDateTimeInput('getDate'))).format("yyyy-mm-dd") : null,
	        		serialNumber:$('#serialNumber').val(),
	        		acquireOrderId:$('#acquireOrderId').val(),
	        		fixedAssetName:$('#fixedAssetName').val(),
	        		fixedAssetId:$('#fixedAssetId').val(),
	        		fixedAssetTypeId:$('#fixedAssetTypeId').val(),
	        		parentFixedAssetId:$('input[name=parentFixedAssetId]').val(),
	        		partyId: $('input[name=ownerPartyId]').val(),
	        		roleTypeId:$('#roleTypeId').val(),
	        		dateAcquired: $('#dateAcquired').jqxDateTimeInput('getDate') ? (new Date($('#dateAcquired').jqxDateTimeInput('getDate'))).format("yyyy-mm-dd hh:MM:ss") : null,  
	        		dateLastServiced: $('#dateLastServiced').jqxDateTimeInput('getDate') ? (new Date($('#dateLastServiced').jqxDateTimeInput('getDate'))).format("yyyy-mm-dd hh:MM:ss") : null,
	        		dateNextService: $('#dateNextService').jqxDateTimeInput('getDate') ?  (new Date($('#dateNextService').jqxDateTimeInput('getDate'))).format("yyyy-mm-dd hh:MM:ss") : null,
	        		productionCapacity:$('#productionCapacity').val(),
	        		uomId:$('#uomId').val(),
	        		locatedAtFacilityId:$('#locatedAtFacilityId').val(),
	        		locatedAtLocationSeqId:$('#locatedAtLocationSeqId').val(),
	        		salvageValue:$('#salvageValue').val(),
	        		depreciation:$('#depreciation').val(),
	        		purchaseCost:$('#purchaseCost').val(),
	        		purchaseCostUomId:$('#purchaseCostUomId').val()
	        	  };
	        if($('#editFixedAsset').jqxValidator('validate')){
				$.ajax({
					url: 'updateFixedAssetJSON',
					type: "POST",
					data: row,
					dataType: 'json',
					async: false,
					success : function(data) {
			        },
				});
			}
	    });
	    $('#editFixedAsset').jqxValidator({
	        rules: [
	                   //{ input: '#fixedAssetId', message: '${uiLabelMap.CommonRequired}', action: 'keyup, blur', rule: 'required' },
	                   //{ input: '#expectedEndOfLife', message: '${uiLabelMap.CommonRequired}', action: 'keyup, blur', rule: 'required' },
	                   { input: '#expectedEndOfLife', message: '${uiLabelMap.CommonRequired}', action: 'select', rule: function(input){
	                	   var val = $("#expectedEndOfLife").jqxDateTimeInput('val');
	                	   if(val==""){
	                	   return false;
	                	   }
	                	   return true;
	                   }},
		                { input: '#dateAcquired', message: '${uiLabelMap.CommonRequired}', action: 'select', rule: function(input){
		                	   var val = $("#dateAcquired").jqxDateTimeInput('val');
		                	   if(val==""){
		                	   return false;
		                	   }
		                	   return true;
		                }},	                	
	                   {
	                       input: '#dateNextService', message: '${StringUtil.wrapString(uiLabelMap.faDateNextServicesLTDateLastServices)}', action: 'valueChanged', rule: function (input, commit) {
	                           var dateNextService = $('#dateNextService').jqxDateTimeInput('value');
	                           var dateLastService = $('#dateLastServiced').jqxDateTimeInput('value');
	                           if (!dateNextService)  return true;
	                           if(dateNextService > dateLastService){
	                        	   return false;
	                           }
	                           return true;
	                       }
	                   }
	               ]
	    });	    
	</script>