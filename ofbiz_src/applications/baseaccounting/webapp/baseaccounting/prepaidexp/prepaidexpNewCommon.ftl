<form class="form-horizontal form-window-content-custom" id="newAsset" name="newAsset">
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCPrepaidExpId}</label>
					</div>
					<div class="span7">
						<input id="prepaidExpId" style="padding: 0px !important;"></input>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCPrepaidExpName}</label>
					</div>
					<div class="span7">
						<input id="prepaidExpName" style="padding: 0px !important;"></input>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCAcquiredDate}</label>
					</div>
					<div class="span7">
						<div id="acquiredDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCAmount}</label>
					</div>
					<div class="span7">
						<div id="amount"></div>
			   		</div>
				</div>
			</div><!--span6-->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCAllocPeriodNum}</label>
					</div>
					<div class="span7">
						<div id="allocPeriodNum">
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCAmountEachPeriod}</label>
					</div>
					<div class="span7">
						<div id="amountEachPeriod">
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCPrepaidExpGlAccountId}</label>
					</div>
					<div class="span7">
						<div id="prepaidExpGlAccountId">
							<div id="prepaidExpGlAccountGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCDescription}</label>
					</div>
					<div class="span7">
						<input id="description" style="padding: 0px !important;"></input>
			   		</div>
				</div>
			</div><!--span6-->
		</div><!--.span12-->
	</div><!--.row-fluid-->
</form>

<script>
	var THEME = 'olbius';
	var OLBNewCommon = function(){
	}
	OLBNewCommon.prototype.initForm = function(){
		$("#description").jqxInput({width: '100%', height: '25px' , theme: THEME});
		$("#prepaidExpId").jqxInput({width: '100%', height: '25px' , theme: THEME});
		$("#prepaidExpName").jqxInput({width: '100%', height: '25px' , theme: THEME});
		$("#acquiredDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '100%', theme: THEME});
		$("#amount").jqxNumberInput({digits: 12, inputMode: 'advanced', decimalDigits: 0, theme: THEME, spinButtons: true, width: '100%'});
		$("#allocPeriodNum").jqxNumberInput({digits: 12, inputMode: 'advanced', decimalDigits: 0, theme: THEME, spinButtons: true, width: '100%'});
		$("#amountEachPeriod").jqxNumberInput({digits: 12, disabled: true, inputMode: 'advanced', decimalDigits: 0, theme: THEME, spinButtons: true, width: '100%'});
		var configGlAccount = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'glAccountId', type: 'string'}, {name: 'accountName', type: 'string'}],
				columns: [
					{text: '${uiLabelMap.BACCGlAccountId}', datafield: 'glAccountId', width: '30%'},
					{text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName'}
				],
				url: "JqxGetListGlAccounts",
				useUtilFunc: true,
				
				key: 'glAccountId',
				description: ['accountName'],
		};
		accutils.initDropDownButton($("#prepaidExpGlAccountId"), $("#prepaidExpGlAccountGrid"), null, configGlAccount, []);
		
		$('#allocPeriodNum').on('valueChanged', function (event) {
		     var value = event.args.value;
		     var amount = $('#amount').val();
		     $('#amountEachPeriod').jqxNumberInput('setDecimal', (amount/value).toFixed(2));
		 });
	}
</script>