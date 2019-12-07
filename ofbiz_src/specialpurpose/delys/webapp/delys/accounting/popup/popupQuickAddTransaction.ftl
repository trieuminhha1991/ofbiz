<div id="popupQuickAddTransaction" class='hide'>
	<div>
		${uiLabelMap.accCreateNew}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FormFieldTitle_acctgTransTypeId}</label>
						</div>
						<div class="span7">
							<div id="acctgTransTypeIdTranQuick"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.glFiscalTypeId}</label>
						</div>
						<div class="span7">
							<div id="glFiscalTypeIdTranQuick"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.accPartyId}</label>
						</div>
						<div class="span7">
							<div id="partyIdTranQuick">
								<div id="jqxGridPartyIdTranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.roleTypeId}</label>
						</div>
						<div class="span7">
							<div id="roleTypeIdTranQuick">
								<div id="jqxgridRoleTypeIdTranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.FormFieldTitle_transactionDate}</label>
						</div>
						<div class="span7">
							<div id="transactionDateTranQuick"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_invoiceId}</label>
						</div>
						<div class="span7">
							<div id="invoiceIdTranQuick">
								<div id="jqxGridInvoiceTranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.accProductId}</label>
						</div>
						<div class="span7">
							<div id="productIdTranQuick">
								<div id="jqxGridProdTranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_shipmentId}</label>
						</div>
						<div class="span7">
							<div id="shipmentIdTranQuick">
								<div id="jqxGridShipTranQuick"></div>
							</div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.paymentId}</label>
						</div>
						<div class="span7">
							<div id="paymentIdTranQuick">
								<div id="jqxGridPayTranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_workEffortId}</label>
						</div>
						<div class="span7">
							<div id="workEffortIdTranQuick" >
								<div id="jqxGridWETranQuick"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FixedAssetId}</label>
						</div>
						<div class="span7">
							<div id="fixedAssetIdTranQuick">
								<div id="fixedAssetQuickJqx"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FormFieldTitle_debitGlAccountId}</label>
						</div>
						<div class="span7">
							<div id="debitGlAccountIdTranQuick">
								<div id="jqxgridDebitGl"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FormFieldTitle_creditGlAccountId}</label>
						</div>
						<div class="span7">
							<div id="creditGlAccountIdTranQuick">
								<div id="jqxgridCreditGl"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.amount}</label>
						</div>
						<div class="span7">
							<div id="amountTranQuick"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.CommonDescription}</label>
						</div>
						<div class="span7">
							<textarea id="descriptionTranQuick" class='text-popup' rows="3"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelQuick" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}
			</button>
			<button id="saveAndContinueQuick" class='btn btn-success form-action-button pull-right'>
				<i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}
			</button>
			<button id="saveQuick" class='btn btn-primary form-action-button pull-right'>
				<i class='fa-check'></i> ${uiLabelMap.CommonSave}
			</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	var quickAddTransaction = function() {
		var form = $('#popupQuickAddTransaction');
		form.jqxWindow("open");
	};
	var accQuick = ( function() {
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			var width = 200;
			var outFilterCondition = "";
			var organizationPartyId = "${parameters.organizationPartyId?if_exists}";
			var form = $('#popupQuickAddTransaction');
			var initWindow = function() {
				form.jqxWindow({
					width : 800,
					height : 415,
					maxWidth : 1000,
					resizable : true,
					isModal : true,
					autoOpen : false,
					cancelButton : $("#cancelQuick"),
					modalOpacity : 0.7,
					theme : theme
				});
				form.on("close", function() {
					clearForm();
					form.jqxValidator("hide");
				});
			};
			
			
			var initDropDownButtonAddQuick = function(){
				initDropDown($('#debitGlAccountIdTranQuick'),$('#jqxgridDebitGl'));
				initDropDown($('#creditGlAccountIdTranQuick'),$('#jqxgridCreditGl'));
				GridUtils.initDropDownButton({url : 'getListRoleType',autoshowloadelement : false,width : 400,filterable : true},
				[	
					{name : 'roleTypeId',type : 'string'},
					{name : 'description',type : 'string'}
				], 
				[
					{text : '${uiLabelMap.roleTypeId}',datafield : 'roleTypeId',width : '40%'},
					{text : '${uiLabelMap.description}',datafield : 'description'}
				]
				, null, $('#jqxgridRoleTypeIdTranQuick'),$('#roleTypeIdTranQuick'),'roleTypeId');
			}
			var initElement = function() {
				$("#acctgTransTypeIdTranQuick").jqxDropDownList({
					theme : theme,
					source : acctgTransTypesData,
					displayMember : "description",
					valueMember : "acctgTransTypeId",
					width : '200',
					height : '25',
					placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'
				});
				$("#glFiscalTypeIdTranQuick").jqxDropDownList({
					theme : theme,
					source : glFiscalTypesData,
					displayMember : "description",
					valueMember : "glFiscalTypeId",
					width : '200',
					height : '25',
					placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'
				});
				
				$("#transactionDateTranQuick").jqxDateTimeInput({
					width : '200px',
					height : '25px',
					formatString : 'dd-MM-yyyy',
					clearString : '${uiLabelMap.Clear}',
					todayString : '${uiLabelMap.Today}',
					showFooter : true
				});
				$("#transactionDateTranQuick").jqxDateTimeInput('val', null);
				$("#amountTranQuick").jqxNumberInput({
					width : width,
					max : 999999999999999999,
					digits : 18,
					decimalDigits : 2,
					spinButtons : false,
					min : 0
				});
				setTimeout(function() {
					initPartySelect($("#partyIdTranQuick"), $("#jqxGridPartyIdTranQuick"));
					initInvoiceSelect($("#invoiceIdTranQuick"), $("#jqxGridInvoiceTranQuick"));
					initPaymentSelect($("#paymentIdTranQuick"), $("#jqxGridPayTranQuick"));
					initShipmentSelect($("#shipmentIdTranQuick"), $("#jqxGridShipTranQuick"));
					initWorkEffortSelect($("#workEffortIdTranQuick"), $("#jqxGridWETranQuick"));
					initProductSelect($("#productIdTranQuick"), $("#jqxGridProdTranQuick"));
					initAssetsSelect($("#fixedAssetIdTranQuick"), $("#fixedAssetQuickJqx"));	
					initDropDownButtonAddQuick();
				}, 1000);
			};

			var bindEvent = function() {
				$("#saveQuick").click(function() {
					if (!accQuick.save()) {
						return;
					}
					form.jqxWindow('close');
				});
				$("#saveAndContinueQuick").click(function() {
					if (!accQuick.save()) {
						return;
					}
					clearForm();
				});
			};
			var save = function() {
				if (!validateForm()) {
					return;
				}
				var row = getFormData();
				var grid = $("#listTran");
				var columns = "organizationPartyId;acctgTransTypeId;glFiscalTypeId;partyId;roleTypeId;"+
							"invoiceId;paymentId;productId;workEffortId;shipmentId;fixedAssetId;debitGlAccountId;"+
							"creditGlAccountId;transactionDate(java.sql.Timestamp);description;amount(java.math.BigDecimal)";
				var createUrl = "jqxGeneralServicer?sname=quickCreateAcctgTransAndEntries&jqaction=C";
				var data = GridUtils.processData(row, columns);
				var callback = function(){
					grid.jqxGrid('updatebounddata');
				};
				GridUtils.sendRequest(grid, createUrl, data, null, wgaddsuccess, callback);
				return true;
			};
			var validateForm = function() {
				return form.jqxValidator("validate");
			};
			var getFormData = function() {
				var index = $('#acctgTransTypeIdTranQuick').jqxDropDownList("getSelectedItem");
				var acctgTransTypeId = index && index.value ? index.value : "";
				index = $('#debitGlAccountIdTranQuick').jqxDropDownButton("val");
				var debitGlAccountId = index ? index : "";
				index = $('#creditGlAccountIdTranQuick').jqxDropDownButton('val');
				var creditGlAccountId = index  ? index : "";
				index = $('#glFiscalTypeIdTranQuick').jqxDropDownList("getSelectedItem");
				var glFiscalTypeId = index && index.value ? index.value : "";
				index = $("#jqxGridPayTranQuick").jqxGrid("getselectedrowindex");
				var payment = $("#jqxGridPayTranQuick").jqxGrid("getrowdata", index);
				var paymentId = payment && payment.paymentId ? payment.paymentId : "";
				index = $("#jqxGridProdTranQuick").jqxGrid("getselectedrowindex");
				var product = $("#jqxGridProdTranQuick").jqxGrid("getrowdata", index);
				var productId = product && product.productId ? product.productId : "";
				index = $("#jqxGridWETranQuick").jqxGrid("getselectedrowindex");
				var workEffort = $("#jqxGridWETranQuick").jqxGrid("getrowdata", index);
				var workEffortId = workEffort && workEffort.workEffortId ? workEffort.workEffortId : "";
				index = $("#jqxGridShipTranQuick").jqxGrid("getselectedrowindex");
				var shipment = $("#jqxGridShipTranQuick").jqxGrid("getrowdata", index);
				var shipmentId = shipment && shipment.shipmentId ? shipment.shipmentId : "";
				index = $('#fixedAssetIdTranQuick').jqxDropDownList("getSelectedItem");
				var fixedAssetId = index && index.value ? index.value : "";
				index = $("#jqxGridInvoiceTranQuick").jqxGrid("getselectedrowindex");
				var invoice = $("#jqxGridInvoiceTranQuick").jqxGrid("getrowdata", index);
				var invoiceId = invoice && invoice.invoiceId ? invoice.invoiceId : "";
				index = $('#roleTypeIdTranQuick').jqxDropDownList("getSelectedItem");
				var roleTypeId = index && index.value ? index.value : "";
				index = $("#jqxGridPartyIdTranQuick").jqxGrid("getselectedrowindex");
				var party = $("#jqxGridPartyIdTranQuick").jqxGrid("getrowdata", index);
				var partyId = party && party.partyId ? party.partyId : "";
				var transactionDate = $('#transactionDateTranQuick').jqxDateTimeInput('getDate');
				var amount = $("#amountTranQuick").jqxNumberInput("val");
				return { 
				 	organizationPartyId: organizationPartyId,
	        		acctgTransTypeId: acctgTransTypeId, 
	        		glFiscalTypeId: glFiscalTypeId,
	        		partyId:partyId,
	        		roleTypeId:roleTypeId,
	        		invoiceId:invoiceId,
	        		paymentId:paymentId,
	        		productId:productId,
	        		workEffortId:workEffortId,
	        		shipmentId:shipmentId,
	        		fixedAssetId:fixedAssetId,
	        		debitGlAccountId:debitGlAccountId,
	        		creditGlAccountId:creditGlAccountId,
	        		amount: amount,
	        		transactionDate: transactionDate ? transactionDate.getTime() : "",
	        		description:$('#descriptionTranQuick').val()
			    };
			};
			var clearForm = function() {
				$("#acctgTransTypeIdTranQuick").jqxDropDownList('clearSelection');
				$("#glFiscalTypeIdTranQuick").jqxDropDownList('clearSelection');
				$("#roleTypeIdTranQuick").jqxDropDownList('clearSelection');
				$("#transactionDateTranQuick").jqxDateTimeInput('val', null);
				$("#fixedAssetIdTranQuick").jqxDropDownList('clearSelection');
				$("#debitGlAccountIdTranQuick").jqxDropDownButton('val','');
				$("#creditGlAccountIdTranQuick").jqxDropDownButton('val','');
				$('#jqxgridDebitGl').jqxGrid('updatebounddata');
				$('#jqxgridCreditGl').jqxGrid('updatebounddata');
				$('#amountTranQuick').jqxNumberInput('clear');
				$('#shipmentIdTranQuick').val('');
				$('#productIdTranQuick').val('');
				$('#invoiceIdTranQuick').val('');
				$('#roleTypeIdTranQuick').val('');
				$('#partyIdTranQuick').val('');
				$('#paymentIdTranQuick').val('');
				$('#workEffortIdTranQuick').val('');
				$('#fixedAssetIdTranQuick').val('');
				
			};
			var initRule = function() {
				form.jqxValidator({
					rules : [{
						input : "#acctgTransTypeIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'blur change',
						rule : function(input, commit) {
							var index = input.jqxDropDownList('getSelectedIndex');
							return index != -1;
						}
					}, {
						input : "#partyIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'click',
						rule : function(input, commit) {
							var index = $("#jqxGridPartyIdTranQuick").jqxGrid('getselectedrowindex');
							return index != -1;
						}
					}, {
						input : "#paymentIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'click',
						rule : function(input, commit) {
							var index = $("#jqxGridPayTranQuick").jqxGrid('getselectedrowindex');
							return index != -1;
						}
					}, {
						input : "#debitGlAccountIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'click,change',
						rule : function(input, commit) {
							var index = input.jqxDropDownButton('val');
							if(!index) return false;
							return true;
						}
					}, {
						input : "#creditGlAccountIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'click,change',
						rule : function(input, commit) {
							var index = input.jqxDropDownButton('val');
							if(!index) return false;
							return true;
						}
					}, {
						input : "#glFiscalTypeIdTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'blur change',
						rule : function(input, commit) {
							var index = input.jqxDropDownList('getSelectedIndex');
							return index != -1;
						}
					}, {
						input : "#amountTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'blur change',
						rule : function(input, commit) {
							var index = input.jqxNumberInput('val');
							if(index != ""){
								return true;
							}
							return false;
						}
					}, {
						input : "#transactionDateTranQuick",
						message : "${uiLabelMap.CommonRequired}",
						action : 'blur change',
						rule : function(input, commit) {
							var index = input.jqxDateTimeInput('getDate');
							if (index) {
								return true;
							}
							return false;
						}
					}]
				});
			};
			return {
				init : function() {
					initWindow();
					initElement();
					initRule();
					bindEvent();
				},
				save : save,
				getFormData: getFormData
			};
		}());
	$(document).ready(function() {
		accQuick.init();
	}); 
</script>