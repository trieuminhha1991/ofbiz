<div id="popupAddTransactionEntry" class='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.glAccountTypeId}</label>
						</div>  
						<div class="span7">
							<div id="glAccountType"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.debitCreditFlag}</label>
						</div>  
						<div class="span7">
							<div id="debitCreditFlagEntry"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.currencyUomId}</label>
						</div>  
						<div class="span7">
							<div id="OrigCurrencyUom"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.Purpose}</label>
						</div>  
						<div class="span7">
							<div id="purposeTrans"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.accProductId}</label>
						</div>  
						<div class="span7">
							<div id="productTranEntry">
								<div id="productJqxGridEntry"></div>
							</div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.settlement}</label>
						</div>  
						<div class="span7">
							<input id="settlement"/>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.summary}</label>
						</div>  
						<div class="span7">
							<textarea id="summaryEntry" class="text-popup"></textarea>
				   		</div>
				   	</div>				   	
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.glAccountId}</label>
						</div>  
						<div class="span7">
							<div id="glAccount">
								<div id="jqxgridGlAcc"></div>
							</div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.PartyGroupName}</label>
						</div>  
						<div class="span7">
							<div id="partyTransEntry">
								<div id="jqxGridPartyEntry"></div>
							</div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.AccountingAmount}</label>	
						</div>  
						<div class="span7">
							<div id="OrigAmount"></div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_voucherRef}</label>
						</div>  
						<div class="span7">
							<input id="voucherRef"/>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_reconcileStatus}</label>
						</div>  
						<div class="span7">
							<div id="productTranEntry">
								<div id="reconileStatus"></div>
							</div>
				   		</div>
				   	</div>
				   	<div class='row-fluid margin-bottom10'>
				   		<div class='span5 text-algin-right'>
							<label>${uiLabelMap.CommonDescription}</label>
						</div>  
						<div class="span7">
							<textarea id="descriptionEntry" class="text-popup"></textarea>
				   		</div>
				   	</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelEntry" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinueEntry" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="saveEntry" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript">
	var accEntry = (function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		var outFilterCondition = "";
		var width = 200;
		var organizationPartyId = "${parameters.organizationPartyId?if_exists}";
		var initWindow = function(){
			$("#popupAddTransactionEntry").jqxWindow({
				width: 800, height: 410, isModal: true, autoOpen: false, cancelButton: $("#cancelEntry"), modalOpacity: 0.7, theme:theme           
			});
			$("#popupAddTransactionEntry").on("open", function(){
				var header = $("#popupAddTransactionEntry .jqx-window-header");
				var w = header.width();
				header.width(w + 1);
			});
			$("#popupAddTransactionEntry").on("close", function(){
				clearForm();
				if(typeof(currentGrid) != "undefined" && currentGrid){
					// currentGrid.jqxGrid("updateBoundData");	
					currentGrid = null;
				}
			});
		};
		var initDropDownGl = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',dropdown : {dropDownHorizontalAlignment : true},autoshowloadelement : false,width  :400,filterable : true},
				[
					{name : 'glAccountId',type : 'string'},
					{name : 'accountName',type : 'string'}
				], 
				[
					{text : '${uiLabelMap.glAccountId}',datafield : 'glAccountId',width : '40%'},
					{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
				]
				, null, grid,dropdown,'glAccountId');
		};
		
		var initElement = function(){
			$("#glAccountType").jqxDropDownList({ theme: theme, source: glAccountTypes, displayMember: "description", valueMember: "glAccountTypeId", width: width, dropDownWidth: 300, height: '25',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#OrigCurrencyUom").jqxDropDownList({ theme: theme, source: currency, displayMember: "description", valueMember: "glAccountId", width: width, dropDownWidth: 400, height: '25', filterable: true,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#reconileStatus").jqxDropDownList({ theme: theme, source: statusItemsData,autoDropDownHeight : true, displayMember: "description", valueMember: "glAccountId", width: width, dropDownWidth: 400, height: '25', filterable: true,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#purposeTrans").jqxDropDownList({ theme: theme, source: purpose, displayMember: "description", valueMember: "glAccountId", width: width, height: '25', filterable: true,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#OrigAmount").jqxNumberInput({ width:  width,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false, min: 0});
			$("#voucherRef").jqxInput({theme: theme, width: width });
			$("#settlement").jqxInput({theme: theme, width: width });
			initProductSelect($("#productTranEntry"), $("#productJqxGridEntry"));
			initPartySelect($("#partyTransEntry"), $("#jqxGridPartyEntry"), 400);
			initDropDownGl($("#glAccount"),$("#jqxgridGlAcc"));
			initDebitCreditFlag();
		};
	 	var initDebitCreditFlag = function(){
	 		var debit = "${StringUtil.wrapString(uiLabelMap.DEBIT)}";
	 		var credit = "${StringUtil.wrapString(uiLabelMap.CREDIT)}";
	 		var flag = [{value : "C", description: credit},{value : "D", description: debit}];
	 		$("#debitCreditFlagEntry").jqxDropDownList({ theme: theme, source: flag, autoDropDownHeight : true,displayMember: "description", valueMember: "value", width: width, height: '25',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
	 	};
		//Invoice Grid getListInvoice getListPayment getListWorkEffort getListShipment
		
		// update the edited row when the user clicks the 'Save' button.
		var bindEvent = function(){
			$("#saveEntry").click(function () {
				if(!accEntry.save()){
					return;
				}
		        $("#popupAddTransactionEntry").jqxWindow('close');
			}); 
			$("#saveAndContinueEntry").click(function () {
				if(!accEntry.save()){
					return;
				}
				clearForm();
			}); 
		};
		var save = function(){
			if(!$('#popupAddTransactionEntry').jqxValidator("validate")){
				return false;
			}
			var index = $('#glAccountType').jqxDropDownList("getSelectedItem");
    		var glAccountTypeId = index && index.value ? index.value : ""; 
    		index = $('#glAccount').jqxDropDownButton("val");
    		var glAccountId = index ? index : "";
    		index = $('#reconileStatus').jqxDropDownList("getSelectedItem");
    		var reconileStatusId = index && index.value ? index.value : "";
    		index = $('#OrigCurrencyUom').jqxDropDownList("getSelectedItem");
    		var currencyUomId = index && index.value ? index.value : ""; 
    		index = $('#purposeTrans').jqxDropDownList("getSelectedItem");
    		var purpose = index && index.value ? index.value : "";
    		index = $("#debitCreditFlagEntry").jqxDropDownList("getSelectedItem");
    		var debitCreditFlagId = index && index.value ? index.value : ""; 
			index = $("#productJqxGridEntry").jqxGrid("getselectedrowindex");
			var product = $("#productJqxGridEntry").jqxGrid("getrowdata", index);
			var productId = product ? product.productId : "";
			index = $("#jqxGridPartyEntry").jqxGrid("getselectedrowindex");
			var party = $("#jqxGridPartyEntry").jqxGrid("getrowdata", index);
			var partyId = party ? party.partyId : "";
			var grid = $("#listTransEntriesDetail");
			if((typeof(currentGrid) != "undefined" && currentGrid) || grid.length){
				var row = { 
					acctgTransId : currentAccTg,
					organizationPartyId: organizationPartyId,
					glAccountTypeId : glAccountTypeId,
					glAccountId : glAccountId,
					reconileStatusId : reconileStatusId,
					origCurrencyUomId : currencyUomId,
					purposeEnumId : purpose,
					debitCreditFlag : debitCreditFlagId,
					productId : productId,
					partyId : partyId,
					origAmount : $("#OrigAmount").jqxNumberInput('val'),
					voucherRef : $("#voucherRef").jqxInput('val'),
        			description : $("#descriptionEntry").val(),
					isSummary : $("#summaryEntry").val()
			    };
			    if(grid.length){
			    	grid.jqxGrid('addRow', null, row, "first");
		        	grid.jqxGrid('clearSelection');                        
		        	grid.jqxGrid('selectRow', 0);
			    }else{
			    	currentGrid.jqxGrid('addRow', null, row, "first");
		        	currentGrid.jqxGrid('clearSelection');                        
		        	currentGrid.jqxGrid('selectRow', 0);
			    }
		        
			}
			return true;
		};
		var clearForm = function(){
	    	$("#glAccountType").jqxDropDownList('clearSelection');
			$("#glAccount").jqxDropDownButton('val', '');
			$('#jqxgridGlAcc').jqxGrid('updatebounddata');
			$("#reconileStatus").jqxDropDownList('clearSelection');
			$("#OrigCurrencyUom").jqxDropDownList('clearSelection');
			$("#purposeTrans").jqxDropDownList('clearSelection');
			$("#debitCreditFlagEntry").jqxDropDownList('clearSelection');
		    $("#OrigAmount").jqxNumberInput('clear');
		    $("#voucherRef").jqxInput('clear');	
		    $("#settlement").jqxInput('clear');	
			$("#productTranEntry").jqxDropDownButton("setContent", "");
			$("#productJqxGridEntry").jqxGrid("clearselection");
			$("#partyTransEntry").jqxDropDownButton("setContent", "");
			$("#jqxGridPartyEntry").jqxGrid("clearselection");
			$("#descriptionEntry").val("");
			$("#summaryEntry").val("");
		};
		var initRule = function(){
			$('#popupAddTransactionEntry').jqxValidator({
	        	rules: [{
		        	input: "#glAccountType", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#glAccount", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownButton('val');
		                if(!index) return false;
		                return true;
		            }
		   		},{
		        	input: "#OrigCurrencyUom", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#debitCreditFlagEntry", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#partyTransEntry", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
		        	rule: function (input, commit) {
		                var index = $("#jqxGridPartyEntry").jqxGrid('getselectedrowindex');
		                return index != -1;
		            }
		   		},{
		        	input: "#OrigAmount", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxNumberInput('val');
		                if(!isNaN(index) && index){
		                	return true;
		                }
		                return false;
		            }
		   		}]
		    });
		};
		return {
			init : function(){
				initWindow();
				initElement();
				initRule();
				bindEvent();	
			},
			save: save
		};
	}());
	$(document).ready(function(){
		accEntry.init();
	});
</script>