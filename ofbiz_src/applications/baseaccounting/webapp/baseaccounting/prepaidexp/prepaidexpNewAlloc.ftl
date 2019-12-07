<div id="newAllocParty" style="display:none">
	<div>${uiLabelMap.BACCNewAllocParty}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewAllocParty">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCAllocPartyId}</label>
							</div>
							<div class='span7'>
								<div id="allocPartyId">
									<div id="allocPartyGrid"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCAllocRate}</label>
							</div>
							<div class='span7'>
								<div id="allocRate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCAllocGlAccoutId}</label>
							</div>
							<div class="span7">
								<div id="allocGlAccountId">
									<div id="allocGlAccountGrid"></div>
								</div>
					   		</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave2" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel2" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBNewAllocParty = function(){
	}
	OLBNewAllocParty.prototype.initWindow = function(){
		$("#newAllocParty").jqxWindow({
			width: '50%', height: 300, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				var configParty = {
						useUrl: true,
						root: 'results',
						widthButton: '80%',
						dropDownHorizontalAlignment: 'right',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						],
						url: "JqxGetParties",
						useUtilFunc: true,
						
						key: 'partyId',
						description: ['fullName'],
				};
				accutils.initDropDownButton($("#allocPartyId"), $("#allocPartyGrid"), null, configParty, []);
				
				$("#allocRate").jqxNumberInput({digits: 5, inputMode: 'simple', decimalDigits: 0, theme: THEME, spinButtons: true, width: '80%'});
				
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
				accutils.initDropDownButton($("#allocGlAccountId"), $("#allocGlAccountGrid"), null, configGlAccount, []);
				
				$("#allocPartyGrid").on('rowselect', function (event) {
					var args = event.args;
					var row = $("#allocPartyGrid").jqxGrid('getrowdata', args.rowindex);
		    		partyId = row['partyId'];
				 	if (partyId) {
				 		for(var i = 0; i < partyGlAccountData.length; i++){
					    	if(partyGlAccountData[i].partyId == partyId){
					    		accutils.setValueDropDownButtonOnly($("#allocGlAccountId"), partyGlAccountData[i].glAccountId, partyGlAccountData[i].accountName);
					    		break;
					    	}
					    }
				 	}
				});
			}
		});
		
		OLBNewAllocParty.initValidator();
	}
	
	OLBNewAllocParty.prototype.openWindow = function(){
		$("#newAllocParty").jqxWindow('open');
	}
	
	OLBNewAllocParty.prototype.closeWindow = function(){
		$("#newAllocParty").jqxWindow('close');
	}
	
	OLBNewAllocParty.validateForm = function(){
		$('#formNewAllocParty').jqxValidator('validate');
	}
	
	OLBNewAllocParty.initValidator = function(){
		// initialize validator.
	    $('#formNewAllocParty').jqxValidator({
	        rules: [
	       			{ input: '#allocPartyId', message: '${uiLabelMap.validFieldRequire}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#allocRate', message: '${uiLabelMap.validFieldRequire}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#allocGlAccountId', message: '${uiLabelMap.validFieldRequire}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			}
               ]
	    });
	}
	
	OLBNewAllocParty.prototype.bindEvent = function(){
		$('#alterSave2').on('click', function(){
			OLBNewAllocParty.validateForm();
		});
		$('#formNewAllocParty').on('validationSuccess', function(){
			var row = {};
			row['seqId'] = alloc.attr.SEQ;
			row['allocPartyId'] = $('#allocPartyId').attr('data-value');
			row['allocRate'] = $('#allocRate').val();
			row['allocGlAccountId'] = $('#allocGlAccountId').attr('data-value');
			alloc.attr.ITEM_DATA[alloc.attr.INDEX] = row;
			source.localdata = alloc.attr.ITEM_DATA;
			$("#newAllocGrid").jqxGrid('updatebounddata');
			alloc.attr.INDEX++;
			alloc.attr.SEQ++;
			allocParty.closeWindow();
		});
		
		$("#newAllocParty").on('close', function(){
		});
	}
</script>