<#assign currencyUomIdDefault = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!>

<form class="form-horizontal form-window-content-custom" id="initReturnWithoutOrderEntry" name="initReturnWithoutOrderEntry" method="post" action="">
	<div class="row-fluid">
		<div class="span6">
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSCustomerId}</label>
				</div>
				<div class="span7">
					<div id="customerId">
						<div id="customerGrid"></div>
					</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSCustomerAddress}</label>
				</div>
				<div class="span7">
					<div id="originContactMechId">
						<div id="originContactMechGrid"></div>
					</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSDescription}</label>
				</div>
				<div class="span7">
					<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
		   		</div>
			</div>
		</div><!--.span6-->
		<div class="span6">
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSCurrencyUomId}</label>
				</div>
				<div class="span7">
					<input id="currencyUomId"/>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.LOGEnterWarehouses}</label>
				</div>
				<div class="span7">
					<div id="destinationFacilityId">
						<div id="destinationFacilityGrid"></div>
					</div>
		   		</div>
			</div>
		</div><!--.span6-->
	</div><!--.row-fluid-->
</form>

<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSCustomerId = "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BSContactMechId = "${StringUtil.wrapString(uiLabelMap.BSContactMechId)}";
	uiLabelMap.BSAddress = "${StringUtil.wrapString(uiLabelMap.BSAddress)}";
	uiLabelMap.BSReceiverName = "${StringUtil.wrapString(uiLabelMap.BSReceiverName)}";
	uiLabelMap.BSOtherInfo = "${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}";
	uiLabelMap.BSFacilityId = "${StringUtil.wrapString(uiLabelMap.BSFacilityId)}";
	uiLabelMap.BSFacilityName = "${StringUtil.wrapString(uiLabelMap.BSFacilityName)}";
	
	$(function(){
		OlbReturnWithoutOrderInfo.init();
	});
	var OlbReturnWithoutOrderInfo = (function(){
		var customerDDB;
		var originContactMechDDB;
		var destinationFacilityDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#currencyUomId"), {disabled: true, width: '68.5%'});
			
			$("#currencyUomId").jqxInput("val", "${currencyUomIdDefault?if_exists}");
		};
		var initElementComplex = function(){
			var configCustomer = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.BSFullName, datafield: 'fullName', width: '70%'}
				],
				url: 'JQGetCustomersBySeller',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				dropDownHorizontalAlignment: 'left'
			};
			customerDDB = new OlbDropDownButton($("#customerId"), $("#customerGrid"), null, configCustomer, null);
			
			var configOriginContactMech = {
				widthButton: '100%',
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'contactMechId', type: 'string'}, 
					{name: 'toName', type: 'string'}, 
					{name: 'attnName', type: 'string'},
					{name: 'address1', type: 'string'},
					{name: 'city', type: 'string'},
					{name: 'stateProvinceGeoId', type: 'string'},
					{name: 'postalCode', type: 'string'},
					{name: 'countryGeoId', type: 'string'},
					{name: 'districtGeoId', type: 'string'},
					{name: 'wardGeoId', type: 'string'},
					{name: 'fullName', type: 'string'},
				],
				columns: [
					{text: uiLabelMap.BSAddress, datafield: 'fullName', width: '60%'},
					{text: uiLabelMap.BSReceiverName, datafield: 'toName', width: '20%'},
					{text: uiLabelMap.BSOtherInfo, datafield: 'attnName', width: '20%'},
				],
				useUrl: true,
				root: 'results',
				url: '', //JQGetShippingAddressFullNameByReceiver
				useUtilFunc: true,
				selectedIndex: 0,
				key: 'contactMechId', 
				description: ['toName', 'attnName', 'fullName'], 
				autoCloseDropDown: false,
				filterable: true,
				sortable: true,
			};
			originContactMechDDB = new OlbDropDownButton($("#originContactMechId"), $("#originContactMechGrid"), null, configOriginContactMech, null);
			
			var configDestinationFacility = {
				widthButton: '70%',
				filterable: true,
				pageable: true,
				showfilterrow: true,
				dropDownHorizontalAlignment: 'right',
				datafields: [{name: 'facilityId', type: 'string'}, {name: 'facilityCode', type: 'string'}, {name: 'facilityName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSFacilityId, datafield: 'facilityCode', width: '25%'},
					{text: uiLabelMap.BSFacilityName, datafield: 'facilityName'}
				],
				useUtilFunc: true,
				useUrl: true,
				root: 'results',
				url: 'jqGetFacilities&facilityGroupId=FACILITY_INTERNAL',
				
				selectedIndex: 0,
				key: 'facilityId',
				keyCode: 'facilityCode',
				description: ['facilityName'],
			};
			destinationFacilityDDB = new OlbDropDownButton($("#destinationFacilityId"), $("#destinationFacilityGrid"), null, configDestinationFacility, []);
		};
		var initEvent = function(){
			customerDDB.getGrid().rowSelectListener(function(rowData){
		    	var customerId = rowData['partyId'];
				originContactMechDDB.updateSource("jqxGeneralServicer?sname=JQGetShippingAddressFullNameByReceiver&partyId="+customerId, null, function(){
					originContactMechDDB.selectItem(null, 0);
				});
		    });
		    
		    $('#destinationFacilityGrid').on('rowdoubleclick', function (event) { 
				$('#destinationFacilityId').jqxDropDownButton('close');
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#customerId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#originContactMechId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#destinationFacilityId', type: 'validObjectNotNull', objType: 'dropDownButton'},
	            ];
			validatorVAL = new OlbValidator($('#initReturnWithoutOrderEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var getValidator = function() {
			return validatorVAL;
		};
		var getValue = function(){
			return {
				customerId: customerDDB.getValue(),
				originContactMechId: originContactMechDDB.getValue(),
				destinationFacilityId: destinationFacilityDDB.getValue(),
				description: $("#description").val(),
				currencyUomId: $("#currencyUomId").val(),
			}
		};
		return {
			init: init,
			getValidator: getValidator,
			getValue: getValue,
		};
	}());
</script>
