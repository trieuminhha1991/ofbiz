<div id="alterpopupWindowNewFacility" style="display:none">
	<div>${uiLabelMap.BSAddNewWarehouseForStore}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.LogWarehouse}</label>
						</div>
						<div class='span7'>
							<div id="wn_psfac_facilityId">
								<div id="wn_psfac_facilityGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_psfac_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_psfac_thruDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSequenceNumber}</label>
						</div>
						<div class='span7'>
							<div id="wn_psfac_sequenceNum"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_psfac_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_psfac_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProductStoreNewCatalog.init();
	});
	
	var OlbProductStoreNewCatalog = (function(){
		var facilityDDB;
		var validatorVAL;
		
		var init = (function(){
			initElement();
			initDropDownList();
			initValidateForm();
			initEvent();
		});
		
		var initElement = (function(){
			jOlbUtil.dateTimeInput.create("#wn_psfac_fromDate", {width: '99%', showFooter: true, height:28});
			jOlbUtil.dateTimeInput.create("#wn_psfac_thruDate", {width: '99%', showFooter: true, height:28, allowNullDate: true});
			jOlbUtil.numberInput.create($("#wn_psfac_sequenceNum"), {width: '99%', spinButtons: false, digits: 3, inputMode: 'simple', decimalDigits: 0, min: 0, allowNull: true});
			
			$('#wn_psfac_fromDate').val(new Date());
			$('#wn_psfac_thruDate').val(null);
			$('#wn_psfac_sequenceNum').val(null);
			
			jOlbUtil.windowPopup.create($('#alterpopupWindowNewFacility'), {width: 500, height: 260, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#wn_psfac_alterCancel")});
		});
		
		var initDropDownList = (function(){
			var configFacility = {
				useUrl: true,
				root: 'results',
				widthButton: '98%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'facilityId', type: 'string'}, 
					{name: 'facilityCode', type: 'string'}, 
					{name: 'facilityName', type: 'string'}, 
	             	{name: 'groupName', type: 'string'}
	             ],
				columns: [
					{text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityCode', width: '24%'},
					{text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName', width: '40%'},
					{text: "${StringUtil.wrapString(uiLabelMap.Owner)}", datafield: 'groupName', width: '36%'},
				],
				url: 'JQGetListFacilityAvailable&productStoreId=${productStoreId?if_exists}',
				useUtilFunc: true,
				
				key: 'facilityId',
				description: ['facilityName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			facilityDDB = new OlbDropDownButton($("#wn_psfac_facilityId"), $("#wn_psfac_facilityGrid"), null, configFacility, []);
		});
		
		var initValidateForm = function(){
			var extendRules = [];
	   		var mapRules = [
		                {input: '#wn_psfac_facilityId', type: 'validObjectNotNull', objType: 'dropDownButton'},
		                {input: '#wn_psfac_fromDate', type: 'validDateTimeInputNotNull'},
		                {input: '#wn_psfac_fromDate', type: 'validDateCompareToday'},
		                {input: '#wn_psfac_fromDate, #wn_psfac_thruDate', type: 'validCompareTwoDate', paramId1 : "wn_psfac_fromDate", paramId2 : "wn_psfac_thruDate"},
	                ];
	   		validatorVAL = new OlbValidator($('#alterpopupWindowNewFacility'), mapRules, extendRules, {position: 'bottom'});
		}
		
		var initEvent = function(){
			$('#wn_psfac_alterSave').click(function(){
				if (!validatorVAL.validate()) {
					return false;
				}
				var row = {
					productStoreId: "${productStoreId?if_exists}",
					facilityId: facilityDDB.getValue(),
					fromDate: $('#wn_psfac_fromDate').jqxDateTimeInput('val', 'date'),
					thruDate: $('#wn_psfac_thruDate').jqxDateTimeInput('val', 'date'),
					sequenceNum: $('#wn_psfac_sequenceNum').val()
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				$("#jqxgrid").jqxGrid('clearSelection');                        
				//$("#jqxgrid").jqxGrid('selectRow', 0);  
				$("#alterpopupWindowNewFacility").jqxWindow('close');
			});
			
			$('#alterpopupWindowNewFacility').on('open', function(){
				$('#wn_psfac_fromDate').val(new Date());
			});
			
			$('#alterpopupWindowNewFacility').on('close', function(){
				$('#jqxgrid').jqxGrid('refresh');
				facilityDDB.clearAll();
				$('#wn_psfac_thruDate').val(null);
				$('#wn_psfac_sequenceNum').val(null);
				
				setTimeout(function(){validatorVAL.hide();}, 100);
			});
		};
		
		return {
			init: init,
		}
	}());
		
</script>
