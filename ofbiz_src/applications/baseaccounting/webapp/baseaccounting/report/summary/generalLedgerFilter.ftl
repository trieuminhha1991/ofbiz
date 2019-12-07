<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom" >	
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
				<div class="span5">
					<label class="text-info">${uiLabelMap.PeriodTypeId}</label>
				</div>
				<div class="span6">
					<div id="dateTime"></div>
		   		</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label class="text-info">${uiLabelMap.BACCGlAccountId}</label>
				</div>
				<div class="span6">
					<div id="glAccountId">
						<div id="glAccountGrid"></div>
					</div>
		   		</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
				</div>
				<div class="span6 pull-left form-window-content-custom">
					<button id="alterSave" class='btn btn-primary form-action-button' style="float: left; margin:0px !important"><i class='fa-search'></i> ${uiLabelMap.BACCOK}</button>
		   		</div>
			</div>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</div>

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
if(typeof(filter) == "undefined"){
	var filter = function(){
		var initFilter = function(){
			var configGlAccount = {
				useUrl: true,
				root: 'results',
				widthButton: '80%',
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
			
			accutils.initDropDownButton($("#glAccountId"), $("#glAccountGrid"), null, configGlAccount, []);
			accutils.setValueDropDownButtonOnly($("#glAccountId"), '${glAccount.glAccountId}', '${glAccount.accountName}');
			$("#dateTime").jqxDateTimeInput({ width: '80%', height: 25,  selectionMode: 'range', formatString : 'dd/MM/yyyy' });
		}
		
		var bindEvent = function(){
			$('#alterSave').on('click', function(){
				var _gridObj = $('#genLedgerGrid');
				var tmpSource = _gridObj.jqxGrid('source');
				var selection = $("#dateTime").jqxDateTimeInput('getRange');

				var fromDate1 = selection.from;		
				var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) +  '-' +   fromDate1.getFullYear();
								
				var thruDate1 = selection.to;
				var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) +  '-' +   thruDate1.getFullYear();
				
				if(typeof(tmpSource) != 'undefined'){
					tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetGeneralLedger&glAccountId=" + $("#glAccountId").attr('data-value') + '&fromDate=' + fromDate + '&thruDate=' + thruDate;
					_gridObj.jqxGrid('clearselection');
					_gridObj.jqxGrid('source', tmpSource);
				}
			});
			
			$('#glAccountId').on('open', function (event) {
				$('#glAccountGrid').jqxGrid('clearselection');
			});
		}
		
		return {
			initFilter: initFilter,
			bindEvent: bindEvent
		}
	}();
}
</script>