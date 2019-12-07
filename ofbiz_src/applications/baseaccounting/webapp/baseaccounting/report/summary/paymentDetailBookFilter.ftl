<script>
var globalVar = {};
<#assign organizationParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", '${userLogin.lastOrg}'), false)>
globalVar.groupName = '${StringUtil.wrapString(organizationParty.get("groupName", locale))}';
globalVar.userLogin_lastOrg = '${userLogin.lastOrg}';
globalVar.enumPartyTypeArr = [
                  		<#if enumPartyTypeList?exists>
                  			<#list enumPartyTypeList as enumParty>
                  			{
                  				enumId: "${enumParty.enumId}",
                  				description: '${StringUtil.wrapString(enumParty.get("description", locale))}'
                  			},
                  			</#list>
                  		</#if>
                  	];	
</script>
<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom" >	
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
				<div class="span5">
					<label class="text-info">${uiLabelMap.BACCChoosePeriodName}</label>
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
			<div class='row-fluid'>
				<div class='span5'>
					<label class='required'>${uiLabelMap.BACCInvoiceToParty}</label>
				</div>
				<div class="span6">
					<div id="enumPartyTypeId"></div>
		   		</div>
		   		</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label class=''></label>
				</div>
			<div class="span6">
				<div id="partyId" style="float: left; display: inline-block;">
					<div id="partyGrid"></div>
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
<script type="text/javascript" src="/accresources/js/miscUtil.js"></script>
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
				url: "JqxGetListPayReceiveGlAccounts",
				useUtilFunc: true,
				
				key: 'glAccountId',
				description: ['accountName'],
			};
			
			accutils.initDropDownButton($("#glAccountId"), $("#glAccountGrid"), null, configGlAccount, []);
			accutils.setValueDropDownButtonOnly($("#glAccountId"), '${receipGlAccount.glAccountId}', '${receipGlAccount.glAccountId}');
			
			$("#dateTime").jqxDateTimeInput({ width: '80%', height: 25,  selectionMode: 'range', formatString : 'dd/MM/yyyy' });

			$("#partyId").jqxDropDownButton({
				width: '80%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			var datafield = [
			                 {name: 'partyId', type: 'string'}, 
			                 {name: 'partyCode', type: 'string'}, 
			                 {name: 'fullName', type: 'string'}
			                 ];
			var columns = [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyCode', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						];
			
			var config = {
			   		width: '100%', 
			   		virtualmode: true,
			   		showfilterrow: true,
			   		pageable: true,
			   		sortable: true,
			        filterable: true,
			        editable: false,
			        url: '', 
			        showtoolbar: false,
		        	source: {
		        		pagesize: 5,
		        	}
		   	};
			
			Grid.initGrid(config, datafield, columns, null, $("#partyGrid"));
					
			accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '80%', height: 25});
			
			accutils.setValueDropDownButtonOnly($("#partyId"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');		
			$("#enumPartyTypeId").val("SUPPLIER_PTY_TYPE");
			
			$("#enumPartyTypeId").on('select', function(event){
				var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	var grid;
		    		grid = $("#partyGrid");
		    		$("#partyId").val("");
			    	var source = grid.jqxGrid('source');
			    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
			    	grid.jqxGrid('source', source);
			    }
			});
			
			$('#partyId').on('close',function(){
				var interval = setInterval(function(){
					$('#partyGrid').jqxGrid('clearSelection');
					clearInterval(interval);
				},10);
			});
			
			$("#partyGrid").on('rowclick', function(event){
				var args = event.args;
				var row = $("#partyGrid").jqxGrid('getrowdata', args.rowindex);
				var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
		        $("#partyId").jqxDropDownButton('setContent', dropDownContent);
		        $("#partyId").jqxDropDownButton('close');
		        accutils.setAttrDataValue('partyId', row.partyId);
			});			
		}			
		
		var bindEvent = function(){					
			$('#alterSave').on('click', function(){
				var _gridObj = $('#payDetailGrid');
				var tmpSource = _gridObj.jqxGrid('source');
				var selection = $("#dateTime").jqxDateTimeInput('getRange');

				var fromDate1 = selection.from;		
				var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) +  '-' +   fromDate1.getFullYear();
								
				var thruDate1 = selection.to;
				var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) +  '-' +   thruDate1.getFullYear();
				
				if(typeof(tmpSource) != 'undefined'){
					tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetPaymentDetailBook&glAccountId=" + $("#glAccountId").attr('data-value') + '&partyId=' + $('#partyId').attr('data-value') + '&fromDate=' + fromDate + '&thruDate=' + thruDate;
					_gridObj.jqxGrid('clearselection');
					_gridObj.jqxGrid('source', tmpSource);
				}
			});
		}
		
		return {
			initFilter: initFilter,
			bindEvent: bindEvent
		}
	}();
}
</script>