<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, false) />
	var acctgTransTypesData =  [<#list acctgTransTypes as item>{ <#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)> 'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",'description' : "${description}"},</#list>];
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, false) />
	var glFiscalTypesData =  [<#list glFiscalTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)> 'glFiscalTypeId' : "${item.glFiscalTypeId?if_exists}", 'description' : "${description}"},</#list>];
	<#assign roleTypes = delegator.findList("RoleType", null, null, null, null, false) />
	var roleTypesData =  [<#list roleTypes as item>{ <#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)> 'roleTypeId' : "${item.roleTypeId?if_exists}", 'description' : "${description}"},</#list>];
	<#assign fixedAssets = delegator.findList("FixedAsset", null, null, null, null, false) />
	var fixedAssetsData =  [<#list fixedAssets as item>{fixedAssetId: "${item.fixedAssetId?if_exists}",description: "${StringUtil.wrapString(item.description?if_exists)}"},</#list>];
	<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glAccountOACsData =  [<#list glAccountOACs as item>{glAccountId : "${item.glAccountId?if_exists}",description : "${StringUtil.wrapString( item.accountName?if_exists)}"},</#list>];
	<#assign glJournals = delegator.findList("GlJournal", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glJournalsData =  [<#list glJournals as item>{glJournalId : "${item.glJournalId?if_exists}",description : "${StringUtil.wrapString(item.glJournalName?if_exists)}"},</#list>];
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, false) />
	var statusItemsData =  [<#list statusItems as item>{ <#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)> 'statusId' : "${item.statusId?if_exists}", 'description' : "${description}"},</#list>];
	var isPostedData = [{isPosted : 'Y',description : '${uiLabelMap.accPostted}'},{isPosted : 'N',description : '${uiLabelMap.accNoPostted}'}];	
</script>
<@jqGridMinimumLib />
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PageTitleEditTransaction}</h4>
	</div>
	<div class='widget-body'>
		<form action="<@ofbizUrl>updateAcctgTrans</@ofbizUrl>" method="post" id="updateAcctgTrans">
			<div class="row-fluid">
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.acctgTransId}</label>
						</div>  
						<div class="span7">
							<div id="acctgTransId" class='highlight-color'></div>
							<input id="acctgTransIdInput" name="acctgTransId" type="hidden"/>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FormFieldTitle_acctgTransTypeId}</label>
						</div>  
						<div class="span7">
							<div id="acctgTransTypeId"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.glJournalId}</label>
						</div>  
						<div class="span7">
							<div id="glJournalId"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.accPartyId}</label>
						</div>  
						<div class="span7">
							<div id="partyId">
								<div id="jqxGridPartyId"></div>
							</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.transactionDate}</label>
						</div>  
						<div class="span7">
							<div id="transactionDate"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_isPosted}</label>
						</div>  
						<div class="span7">
							<div id="isPosted"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_physicalInventoryId}</label>
						</div>  
						<div class="span7">
							<input id="physicalInventoryId" name="physicalInventoryId"/>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_receiptId}</label>
						</div>  
						<div class="span7">
							<input id="receiptId" name="receiptId"/>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_voucherRef}</label>
						</div>
						<div class="span7">
							<input id="voucherRef" name="voucherRef" />
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_invoiceId}</label>
						</div>
						<div class="span7">
					       <div id="invoiceId" name="invoiceId">
					       		<div id="jqxGridInvoice"></div>
					       </div>
					    </div>
	 			    </div>
	 			    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.accProductId}</label>
						</div>
						<div class="span7">
					       <span>
					       		<div id="productId" name="productId">
					       			<div id="jqxGridProd"></div>
					       		</div>
					       </span>
					    </div>
				    </div>
				    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_shipmentId}</label>
						</div>
						<div class="span7">
					       <span>
					       		<div id="shipmentId" name="shipmentId">
					       			<div id="jqxGridShip"></div>
					       		</div>
					       </span>
					    </div>
				    </div>
				
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.description}</label>
						</div>
						<div class="span7">
							<input id="description" name="description"/>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class="span7">&nbsp;
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.glFiscalTypeId}</label>
						</div>  
						<div class="span7">
							<div id="glFiscalTypeId"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_reconcileStatus}</label>
						</div>  
						<div class="span7">
							<div id="groupStatusId"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.roleTypeId}</label>
						</div>  
						<div class="span7">
							<div id="roleTypeId"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_scheduledPostingDate}</label>
						</div>  
						<div class="span7">
							<div id="scheduledPostingDate"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.postedDate}</label>
						</div>  
						<div class="span7">
							<div id="postedDate"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_inventoryItemId}</label>
						</div>  
						<div class="span7">
							<input id="inventoryItemId" name="inventoryItemId"/>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_theirAcctgTransId}</label>
						</div>  
						<div class="span7">
							<input id="theirAcctgTransId" name="theirAcctgTransId"/>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_voucherDate}</label>
						</div>
						<div class="span7">
							<div id="voucherDate" name="voucherDate"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
				    	<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.paymentId}</label>
						</div>
						<div class="span7">
							<span>
								<div id="paymentId">
				       				<div id="jqxGridPay"></div>
				       			</div>
				       		</span>
				       	</div>
			       </div>
			       <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_workEffortId}</label>
						</div>
						<div class="span7">
							<div id="workEffortId">
			       				<div id="jqxGridWE"></div>
			       			</div>
				       	</div>
			        </div>
			        <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FixedAssetId}</label>
						</div>
						<div class="span7">
							<div id="fixedAssetId" name="fixedAssetId">
								<div id="fixedAssetJqx"></div>
							</div>
						</div>
					</div>
				
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_finAccountTransId}</label>
						</div>
						<div class="span7">
							<input id="finAccountTransId" name="finAccountTransId"/>
						</div>
					</div>
				</div>
			</div>
			<div class='pull-right'>
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right' type="button"><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' type="button"><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</form>
	</div>
</div>
<script>
	 var formatcurrency = function(num, uom){
                if(num == null){
                    return "";
                }
                decimalseparator = ",";
                thousandsseparator = ".";
                currencysymbol = "đ";
                if(typeof(uom) == "undefined" || uom == null){
                    uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
                }
                if(uom == "USD"){
                    currencysymbol = "$";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }else if(uom == "EUR"){
                    currencysymbol = "€";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }
                var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
                if(str.indexOf(".") > 0) {
                    parts = str.split(".");
                    str = parts[0];
                }
                str = str.split("").reverse();
                for(var j = 0, len = str.length; j < len; j++) {
                    if(str[j] != ",") {
                        output.push(str[j]);
                        if(i%3 == 0 && j < (len - 1)) {
                            output.push(thousandsseparator);
                        }
                        i++;
                    }
                }
                formatted = output.reverse().join("");
                return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
            };
</script>	
	
	
<script type="text/javascript">
	
	<#assign fixedAssetTypeList = delegator.findList("FixedAssetType",  null, null, null, null, true) />
	var fixedAssetTypeData = [
		<#list fixedAssetTypeList as fixedAssetType>
			<#assign description = StringUtil.wrapString(fixedAssetType.get("description", locale)) />
			{
				description: "${description?if_exists?default("")}",
				fixedAssetTypeId: "${fixedAssetType.fixedAssetTypeId}"
			},
		</#list>
	];
	
	<#assign listInv = delegator.findByAnd("InvoiceType",null,null,false) !>
	 var listInvoiceType = [
	 	<#list listInv as inv>
	 	{
	 		'invoiceTypeId' : '${inv.invoiceTypeId?if_exists}',
	 		'description'  : '${StringUtil.wrapString(inv.get('description',locale))}'
	 	},
	 	</#list>
	 ]
	 
	 <#assign listShipmentType =  delegator.findByAnd("ShipmentType",null,null,false) !>
	 var listShipmentType = [
	 	<#list listShipmentType as inv>
	 	{
	 		'shipmentTypeId' : '${inv.shipmentTypeId?if_exists}',
	 		'description'  : "${StringUtil.wrapString(inv.get('description',locale))}"
	 	},
	 	</#list>
	 ]
	 
	  <#assign listSttShipment =  delegator.findByAnd("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId","PURCH_SHIP_STATUS"),null,false) !>
	 var listSttShipment = [
	 	<#list listSttShipment as inv>
	 	{
	 		'statusId' : '${inv.statusId?if_exists}',
	 		'description'  : "${StringUtil.wrapString(inv.get('description',locale))}"
	 	},
	 	</#list>
	 ]
	 
	  <#assign listInvStatus =  delegator.findByAnd("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId","INVOICE_STATUS"),null,false) !>
	 var listInvStatus = [
	 	<#list listInvStatus as inv>
	 	{
	 		'statusId' : '${inv.statusId?if_exists}',
	 		'description'  : "${StringUtil.wrapString(inv.get('description',locale))}"
	 	},
	 	</#list>
	 ]

	//Set acctgTransTypeId
	<#assign acctgTransTypeId = acctgTrans.acctgTransTypeId?if_exists />
	var acctgTransTypeIdIndex = 0;
	for(i = 0; i < acctgTransTypesData.length; i++){
		if(acctgTransTypesData[i].acctgTransTypeId == '${acctgTransTypeId}'){
			acctgTransTypeIdIndex = i;
			break;
		}
	}
	
	//Set glFiscalTypeId
	<#assign glFiscalTypeId = acctgTrans.glFiscalTypeId?if_exists />
	var glFiscalTypeIdIndex = 0;
	for(i = 0; i < glFiscalTypesData.length; i++){
		if(glFiscalTypesData[i].glFiscalTypeId == '${glFiscalTypeId}'){
			glFiscalTypeIdIndex = i;
			break;
		}
	}
	
	//Set glJournalId
	<#assign glJournalId = acctgTrans.glJournalId?if_exists />
	var glJournalIdIndex = 0;
	for(i = 0; i < glJournalsData.length; i++){
		if(glJournalsData[i].glJournalId == '${glJournalId}'){
			glJournalIdIndex = i;
			break;
		}
	}
	
	//Set groupStatusId
	<#assign groupStatusId = acctgTrans.groupStatusId?if_exists />
	var groupStatusIdIndex = 0;
	for(i = 0; i < statusItemsData.length; i++){
		if(statusItemsData[i].groupStatusId == '${groupStatusId}'){
			groupStatusIdIndex = i;
			break;
		}
	}
	
	//Set roleTypeId
	<#assign roleTypeId = acctgTrans.roleTypeId?if_exists />
	var roleTypeIdIndex = 0;
	for(i = 0; i < roleTypesData.length; i++){
		if(roleTypesData[i].roleTypeId == '${roleTypeId}'){
			roleTypeIdIndex = i;
			break;
		}
	}
	
	//Set transactionDate
	<#assign transactionDate = acctgTrans.transactionDate?if_exists />
	var transactionDate = new Date();
	if('${transactionDate}' != null && '${transactionDate}' != ''){
		transactionDate = new Date('${transactionDate}');
	}
	//Set scheduledPostingDate
	<#assign scheduledPostingDate = acctgTrans.scheduledPostingDate?if_exists />
	var scheduledPostingDate = new Date();
	if('${scheduledPostingDate}' != null && '${scheduledPostingDate}' != ''){
		scheduledPostingDate = new Date('${scheduledPostingDate}');
	}
	
	//Set isPosted
	<#assign isPosted = acctgTrans.isPosted?if_exists />
	var isPostedIndex = 0;
	for(i = 0; i < isPostedData.length; i++){
		if(isPostedData[i].isPosted == '${isPosted}'){
			isPostedIndex = i;
			break;
		}
	}
	
	//Set postedDate
	<#assign postedDate = acctgTrans.postedDate?if_exists />
	var postedDate = new Date();
	if('${postedDate}' != null && '${postedDate}' != ''){
		postedDate = new Date('${postedDate}');
	}
	
	//Set inventoryItemId
	<#assign inventoryItemId = acctgTrans.inventoryItemId?if_exists />
	var inventoryItemId = '${inventoryItemId}';
	
	//Set inventoryItemId
	<#assign physicalInventoryId = acctgTrans.physicalInventoryId?if_exists />
	var physicalInventoryId = '${physicalInventoryId}';
	
	//Set receiptId
	<#assign receiptId = acctgTrans.receiptId?if_exists />
	var receiptId = '${receiptId}';
	
	//Set theirAcctgTransId
	<#assign theirAcctgTransId = acctgTrans.theirAcctgTransId?if_exists />
	var theirAcctgTransId = '${theirAcctgTransId}';
	
	//Set voucherRef
	<#assign voucherRef = acctgTrans.voucherRef?if_exists />
	var voucherRef = '${voucherRef}';
	
	//Set voucherDate
	<#assign voucherDate = acctgTrans.voucherDate?if_exists />
	var voucherDate = new Date();
	if('${voucherDate}' != null && '${voucherDate}' != ''){
		voucherDate = new Date('${voucherDate}');
	}
	
	//Set description
	<#assign description = acctgTrans.description?if_exists />
	var description = '${description}';
	
	//Set description
	<#assign finAccountTransId = acctgTrans.finAccountTransId?if_exists />
	var finAccountTransId = '${finAccountTransId}';
	
	//Set isPosted
	<#assign fixedAssetId = acctgTrans.fixedAssetId?if_exists />
	var fixedAssetIdIndex = 0;
	for(i = 0; i < fixedAssetsData.length; i++){
		if(fixedAssetsData[i].isPosted == '${fixedAssetId}'){
			fixedAssetIdIndex = i;
			break;
		}
	}
	
	//Set createdDate
	<#assign createdDate = acctgTrans.createdDate?if_exists />
	var createdDate = new Date();
	if('${createdDate}' != null && '${createdDate}' != ''){
		createdDate = new Date('${createdDate}');
	}
	
	//Set lastModifiedDate
	<#assign lastModifiedDate = acctgTrans.lastModifiedDate?if_exists />
	var lastModifiedDate = new Date();
	if('${lastModifiedDate}' != null && '${lastModifiedDate}' != ''){
		lastModifiedDate = new Date('${lastModifiedDate}');
	}
	
	//Set acctgTransId
	<#assign acctgTransId = acctgTrans.acctgTransId?if_exists />
	
	$('#acctgTransId').text('${acctgTransId}');
	
	$('#acctgTransIdInput').val('${acctgTransId}');
	
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	var initElement = function(){
		$("#acctgTransTypeId").jqxDropDownList({ theme: theme, source: acctgTransTypesData, displayMember: "description", valueMember: "acctgTransTypeId", selectedIndex: acctgTransTypeIdIndex, width: '200', height: '25'});
		$("#glFiscalTypeId").jqxDropDownList({ theme: theme, source: glFiscalTypesData, displayMember: "description", valueMember: "glFiscalTypeId", selectedIndex: glFiscalTypeIdIndex, width: '200', height: '25'});
		$("#glJournalId").jqxDropDownList({ theme: theme, source: glJournalsData, autoDropDownHeight: true, displayMember: "description", valueMember: "glJournalId", selectedIndex: glJournalIdIndex, width: '200', height: '25'});
		$("#groupStatusId").jqxDropDownList({ autoDropDownHeight : true,theme: theme, source: statusItemsData, displayMember: "description", valueMember: "statusId", selectedIndex: groupStatusIdIndex, width: '200', height: '25'});
		$("#finAccountTransId").jqxInput({width:200, height: 25});
		$("#roleTypeId").jqxDropDownList({ theme: theme, source: roleTypesData, displayMember: "description", valueMember: "roleTypeId", selectedIndex: roleTypeIdIndex, width: '200', height: '25'});
		$('#inventoryItemId').jqxInput({width:200, height: 25}).jqxInput('val', inventoryItemId);
		$('#physicalInventoryId').jqxInput({width:200, height: 25}).jqxInput('val', physicalInventoryId);
		$('#receiptId').jqxInput({width:200, height: 25}).jqxInput('val', receiptId);
		$('#theirAcctgTransId').jqxInput({width:200, height: 25}).jqxInput('val', theirAcctgTransId);
		$('#voucherRef').jqxInput({width:200, height: 25}).jqxInput('val', voucherRef);
		$("#voucherDate").jqxDateTimeInput({value: voucherDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
		$("#isPosted").jqxDropDownList({ theme: theme, source: isPostedData, displayMember: "description", valueMember: "isPosted", selectedIndex: isPostedIndex, width: '200', height: '25'});
		$("#postedDate").jqxDateTimeInput({value: postedDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
		//$("#fixedAssetId").jqxDropDownList({ theme: theme, source: fixedAssetsData, autoDropDownHeight: true, displayMember: "fixedAssetId", valueMember: "fixedAssetId", selectedIndex: fixedAssetIdIndex, width: '200', height: '25'});
		$("#transactionDate").jqxDateTimeInput({value: transactionDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
		$("#scheduledPostingDate").jqxDateTimeInput({value: scheduledPostingDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
		$('#description').jqxInput({width: 200, height: 25}).jqxInput('val', description);
		$('#finAccountTransId').jqxInput({width: 200, height: 25}).jqxInput('val', finAccountTransId);
		
	};
	
	var initPartySelect = function(){
		var datafields = [{ name: 'partyId', type: 'string' },
    		{ name: 'partyTypeId', type: 'string' },
        	{ name: 'firstName', type: 'string' },
        	{ name: 'lastName', type: 'string' },
        	{ name: 'groupName', type: 'string' }];
        var columns = [{ text: '${uiLabelMap.accApInvoice_partyId}', datafield: 'partyId', width: 200, pinned: true},
       		{ text: '${uiLabelMap.accApInvoice_partyTypeId}', datafield: 'partyTypeId', width: 200, 
				cellsrenderer: function(row, columns, value){
					var group = "${uiLabelMap.PartyGroup}";
					var person = "${uiLabelMap.Person}";
					if(value == "PARTY_GROUP"){
						return "<div class='custom-cell-grid'>"+group+"</div>";
					}else if(value == "PERSON"){
						return "<div class='custom-cell-grid'>"+person+"</div>";
					}
					return value;
				}
			},
			{ text: '${uiLabelMap.accAccountingFromParty}', datafield: 'groupName', width: 200},
			{ text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width: 200, 
				cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
					var first = rowdata.firstName ? rowdata.firstName : "";
					var last = rowdata.lastName ? rowdata.lastName : "";
					return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
				}
			}];
    	GridUtils.initDropDownButton({url: "getFromParty", autorowheight: true, filterable: true, source: {cache: true, pagesize: 5}},datafields,columns, null, $("#jqxGridPartyId"), $("#partyId"), "partyId");
	};
	
	var initInvoiceSelect = function(dropdown, grid){
			var datafields = [{ name: 'invoiceId', type: 'string' },
				{ name: 'invoiceTypeId', type: 'string' },
				{ name: 'partyIdFrom', type: 'string' },
				{ name: 'partyId', type: 'string' },
				{ name: 'fullNameFrom', type: 'string' },
				{ name: 'fullNameTo', type: 'string' },
				{ name: 'groupNameFrom', type: 'string' },
				{ name: 'groupNameTo', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'description', type: 'string'}];
            var columns = [{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 150 },
			  	{ text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}',filtertype:'checkedlist', datafield: 'invoiceTypeId', width: 150,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
			  		for(var i = 0 ;i < listInvoiceType.length;i++){
			  			if(listInvoiceType[i].invoiceTypeId == data.invoiceTypeId){
			  				return '<span>' + listInvoiceType[i].description +'</span>';
			  			}
			  		}
			  		return '<span>' + data.invoiceTypeId +'</span>';
			  	} ,createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : listInvoiceType,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'invoiceTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
									{
										for(i=0;i < listInvoiceType.length; i++){
											if(listInvoiceType[i].invoiceTypeId == value){
												return listInvoiceType[i].description;
											}
										}
									    return value;
									}});
				    }},
			  	{ text: '${uiLabelMap.partyIdFrom}', datafield: 'groupNameFrom', width: 150 ,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
					if(data.groupNameFrom){
						return '<span>' + data.groupNameFrom + '</span>';
					}else if(data.fullNameFrom){
						return '<span>' + data.fullNameFrom + '</span>';
					}else return ''; 
			  	}},
			  	{ text: '${uiLabelMap.partyIdTo}', datafield: 'groupNameTo', width: 150 ,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
					if(data.groupNameTo){
						return '<span>' + data.groupNameTo + '</span>';
					}else if(data.fullNameTo){
						return '<span>' + data.fullNameTo + '</span>';
					}else return ''; 
			  	}},
			  	{ text: '${uiLabelMap.statusId}', datafield: 'statusId',filtertype : 'checkedlist', width: 150,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
			  		for(var key in listInvStatus){
			  			if(listInvStatus[key].statusId == data.statusId){
			  				return '<span>' + listInvStatus[key].description +'</span>';
			  			}
			  		}
			  		return '<span>' + data.statusId + '</span>';
		  		},createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : listInvStatus,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'statusId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
							{
								for(i=0;i < listInvStatus.length; i++){
									if(listInvStatus[i].statusId == value){
										return listInvStatus[i].description;
									}
								}
							    return value;
							}});
				    } },
			  	{ text: '${uiLabelMap.description}', datafield: 'description', width: 200}];
	    	GridUtils.initDropDownButton({url: "getListInvoice",autoshowloadelement : true, autorowheight: true, filterable: true, source: {cache: true, pagesize: 3}},
    									datafields,columns, null, grid, dropdown, "invoiceId");
		};
		
	var initPaymentSelect = function(dropdown, grid){
		var datafields = [{ name: 'paymentId', type: 'string' },
			{ name: 'partyIdFrom', type: 'string' },
			{ name: 'partyIdTo', type: 'string' },
			{ name: 'fullNameTo', type: 'string' },
			{ name: 'fullNameFrom', type: 'string' },
			{ name: 'groupNameTo', type: 'string' },
			{ name: 'groupNameFrom', type: 'string' },
			{ name: 'effectiveDate', type: 'date' ,other: 'Timestamp'},
			{ name: 'amount', type: 'number' },
			{ name: 'currencyUomId', type: 'string'}];
        var columns = [{ text: '${uiLabelMap.paymentId}', datafield: 'paymentId', width: 150 },
			{ text: '${uiLabelMap.paymentFrom}', datafield: 'groupNameFrom', width: 150 ,cellsrenderer : function(row){
				var data = grid.jqxGrid('getrowdata',row);
				if(data.groupNameFrom){
					return '<span>' + data.groupNameFrom + '</span>';
				}else if(data.fullNameFrom){
					return '<span>' + data.fullNameFrom + '</span>';
				}else return ''; 
			}},
			{ text: '${uiLabelMap.paymentTo}', datafield: 'groupNameTo', width: 150 ,cellsrenderer : function(row){
				var data = grid.jqxGrid('getrowdata',row);
				if(data.groupNameTo){
					return '<span>' + data.groupNameTo + '</span>';
				}else if(data.fullNameTo){
					return '<span>' + data.fullNameTo + '</span>';
				}else return ''; 
			}},
			{ text: '${uiLabelMap.effectiveDate}', datafield: 'effectiveDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range', width: 150 },
			{ text: '${uiLabelMap.amount}', datafield: 'amount', width: 150 ,filtertype : 'number',cellsformat : 'd'},
			{ text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 150}
        ];
    	GridUtils.initDropDownButton({url: "getListPayment",autoshowloadelement : true, autorowheight: true, filterable: true, width: 400, source: {cache: true, pagesize: 5}},
									datafields,columns, null, grid, dropdown, "paymentId");
	};	
		
	var initProductSelect = function(){
		var datafields = [{ name: 'productId', type: 'string' },
			{ name: 'brandName', type: 'string' },
			{ name: 'internalName', type: 'string' },
		 	{ name: 'productTypeId', type: 'string' }];
        var columns = [{ text: '${uiLabelMap.FormFieldTitle_productId}', datafield: 'productId', width: 150 },
		  	{ text: '${uiLabelMap.ProductBrandName}', datafield: 'brandName' },
		  	{ text: '${uiLabelMap.ProductInternalName}', datafield: 'internalName' },
		  	{ text: '${uiLabelMap.ProductProductType}', datafield: 'productTypeId'}];
    	GridUtils.initDropDownButton({url: "getListProduct", autorowheight: true, filterable: true,source : { pagesize : 5,cache : false}},datafields,columns, null, $("#jqxGridProd"),$("#productId"), "productId");
	};
	var initWorkEffortSelect = function(dropdown, grid){
			var datafields = [{ name: 'workEffortId', type: 'string' },
				{ name: 'workEffortName', type: 'string' },
				{ name: 'workEffortTypeId', type: 'string' },
				{ name: 'contactMechTypeId', type: 'string' }];
				
            var columns = [{ text: '${uiLabelMap.FormFieldTitle_workEffortId}', datafield: 'workEffortId', width: 150 },
				{ text: '${uiLabelMap.FormFieldTitle_workEffortName}', datafield: 'workEffortName', width: 150 },
				{ text: '${uiLabelMap.FormFieldTitle_workEffortTypeId}', datafield: 'workEffortTypeId', width: 150 },
				{ text: '${uiLabelMap.FormFieldTitle_contactMechTypeId}', datafield: 'contactMechTypeId', width: 150 }];
	    	GridUtils.initDropDownButton({url: "getListWorkEffort",autoshowloadelement : true ,autorowheight: true, filterable: true, width: 400, source: {cache: true, pagesize: 5}},
	    								datafields,columns, null, grid, dropdown, "workEffortId");
		};
	
	
	var initShipmentSelect = function(dropdown, grid){
			var datafields = [{ name: 'shipmentId', type: 'string' },
				{ name: 'shipmentTypeId', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'partyIdFrom', type: 'string' },
				{ name: 'partyIdTo', type: 'string' },
				{ name: 'fullNameTo', type: 'string' },
				{ name: 'fullNameFrom', type: 'string' },
				{ name: 'groupNameTo', type: 'string' },
				{ name: 'groupNameFrom', type: 'string' }
				];
            var columns = [{ text: '${uiLabelMap.ShipmentId}', datafield: 'shipmentId', width: 150 },
				{ text: '${uiLabelMap.FormFieldTitle_shipmentTypeId}', datafield: 'shipmentTypeId',filtertype : 'checkedlist', width: 150 ,cellsrenderer : function(row){
					var data = grid.jqxGrid('getrowdata',row);
					for(var key in listShipmentType){
						if(listShipmentType[key].shipmentTypeId == data.shipmentTypeId){
							return  '<span>' + listShipmentType[key].description +'</span>';
						}
					}
					return data.shipmentTypeId;
				},createfilterwidget : function(row,column,widget){
					var filterBox = new $.jqx.dataAdapter(listShipmentType,{autoBind : true});
					var records = filterBox.records;
					records.splice(0,0,'(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})'); 
					widget.jqxDropDownList({source : records,displayMember : 'description',valueMember : 'shipmentTypeId'});	
				}},
				{ text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 150 ,filtertype : 'checkedlist',cellsrenderer : function(row){
					var data = grid.jqxGrid('getrowdata',row);
					for(var key in listSttShipment){
						if(listSttShipment[key].statusId == data.statusId){
							return '<span>' + listSttShipment[key].description + '</span>';
						}
					}
					return '<span>' + data.statusId + '</span>';
				},createfilterwidget : function(row,column,widget){
					var filterBox = new $.jqx.dataAdapter(listSttShipment,{autoBind : true});
					var records = filterBox.records;
					records.splice(0,0,'(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})'); 
					widget.jqxDropDownList({source : records,displayMember : 'description',valueMember : 'statusId'});	
				}},
				{ text: '${uiLabelMap.partyIdFrom}', datafield: 'groupNameFrom', width: 150 ,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
					if(data.groupNameFrom){
						return '<span>' + data.groupNameFrom + '</span>';
					}else if(data.fullNameFrom){
						return '<span>' + data.fullNameFrom + '</span>';
					}else return ''; 
			  	}},
				{ text: '${uiLabelMap.partyIdTo}', datafield: 'groupNameTo', width: 150 ,cellsrenderer : function(row){
			  		var data = grid.jqxGrid('getrowdata',row);
					if(data.groupNameTo){
						return '<span>' + data.groupNameTo + '</span>';
					}else if(data.fullNameTo){
						return '<span>' + data.fullNameTo + '</span>';
					}else return ''; 
			  	}}];
	    	GridUtils.initDropDownButton({url: "getListShipment",autoshowloadelement : true, autorowheight: true, filterable: true, source: {cache: true, pagesize: 5}},
	    								datafields,columns, null, grid, dropdown, "shipmentId");
		};	
		
	var initAssetsSelect = function(dropdown, grid){
		var datafields = [{ name: 'fixedAssetId', type: 'string'},
					 	 { name: 'fixedAssetName', type: 'string'},
					 	 { name: 'fixedAssetTypeId', type: 'string'},
					 	 { name: 'parentFixedAssetId', type: 'string'},
					 	 { name: 'dateAcquired', type: 'date', other: 'Timestamp'},
						 { name: 'expectedEndOfLife', type: 'date', other: 'Timestamp'},
						 { name: 'purchaseCost', type: 'number'},
						 { name: 'salvageValue', type: 'number'},
						 { name: 'depreciation', type: 'number'},
						 { name: 'plannedPastDepreciationTotal', type: 'number'}];
        var columns = [{ text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetId)}', datafield: 'fixedAssetId', width: 150, cellsrenderer:
				       function(row, colum, value){
					        var data = grid.jqxGrid('getrowdata', row);
			        		return '<span><a href="' + 'EditFixedAsset?fixedAssetId=' + data.fixedAssetId + '">' + data.fixedAssetId + '</a></span>';
			         }},
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetName)}', datafield: 'fixedAssetName', width: 200},
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetTypeId)}', datafield: 'fixedAssetTypeId', width: 150, filtertype: 'checkedlist',
						 	cellsrenderer: function (row, column, value) {
								var data = grid.jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < fixedAssetTypeData.length; i++){
	        							if(data.fixedAssetTypeId == fixedAssetTypeData[i].fixedAssetTypeId){
	        								return '<span title=' + value +'>' + fixedAssetTypeData[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(fixedAssetTypeData,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'fixedAssetTypeId', valueMember : 'fixedAssetTypeId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < uniqueRecords2.length; i++){
										if(uniqueRecords2[i].roleTypeId == value){
											return uniqueRecords2[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			}
					 	},
	                    { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_dateAcquired)}', width:150, datafield: 'dateAcquired', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
	                    { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_expectedEndOfLife)}', width:220, datafield: 'expectedEndOfLife', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_purchaseCost)}', width:150,filtertype  : 'number', datafield: 'purchaseCost', cellsrenderer:
							 	function(row, colum, value){
							 		var data = grid.jqxGrid('getrowdata', row);							 		
							 		return "<span>" + formatcurrency(data.purchaseCost,data.purchaseCostUomId) + "</span>";
							 	}},
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_salvageValue)}', width:150,filtertype : 'number', datafield: 'salvageValue', cellsrenderer:
						 	function(row, colum, value){
						 		var data = grid.jqxGrid('getrowdata', row);
						 		return "<span>" + formatcurrency(data.salvageValue,data.purchaseCostUomId) + "</span>";
						 	}},
						{ text: '${StringUtil.wrapString(uiLabelMap.AccountingDepreciation)}', width:150,filtertype :'number', datafield: 'depreciation', cellsrenderer:
						 	function(row, colum, value){
						 		var data = grid.jqxGrid('getrowdata', row);
						 		return "<span>" + formatcurrency(data.depreciation,data.purchaseCostUomId) + "</span>";
						 	}},							 							 	
	                    { text: '${StringUtil.wrapString(uiLabelMap.accPlannedPastDepreciationTotal)}', width:200,filtertype : 'number', datafield: 'plannedPastDepreciationTotal', cellsrenderer:
						 	function(row, colum, value){
					 		var data = grid.jqxGrid('getrowdata', row);
					 		return "<span>" + formatcurrency(data.plannedPastDepreciationTotal,data.purchaseCostUomId) + "</span>";
					 	}}
        ];
    	GridUtils.initDropDownButton({url: "listFixedAssetsJqx",autoshowloadelement : true, autorowheight: true, filterable: true, width: 400, source: {cache: true, pagesize: 3}},
									datafields,columns, null, grid, dropdown, "fixedAssetId");
	};	
		
	var initContent = function(){
		$('#partyId').jqxDropDownButton('setContent', "acctgTrans.partyId?if_exists");
		$('#invoiceId').jqxDropDownButton('setContent', "${acctgTrans.invoiceId?if_exists}");
		$('#paymentId').jqxDropDownButton('setContent', "${acctgTrans.paymentId?if_exists}");
		$('#productId').jqxDropDownButton('setContent', "${acctgTrans.productId?if_exists}");
		$('#workEffortId').jqxDropDownButton('setContent', "${acctgTrans.workEffortId?if_exists}");
		$('#shipmentId').jqxDropDownButton('setContent', "${acctgTrans.shipmentId?if_exists}");
	};
	var initRule = function(){
		var form = $('#updateAcctgTrans');
		form.jqxValidator({
	        	rules: [{
	        	input: "#acctgTransTypeId", message: "${uiLabelMap.CommonRequired}", action: 'blur change', 
	        	rule: function (input, commit) {
	                var index = input.jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	   		},{
	        	input: "#partyId", message: "${uiLabelMap.CommonRequired}", action: 'change,close', 
	        	rule: function (input, commit) {
	                var index = input.jqxDropDownButton('val');
	                if(!index) return false;
	                return true;
	            }
	   		},{
	        	input: "#paymentId", message: "${uiLabelMap.CommonRequired}", action: 'change,close', 
	        	rule: function (input, commit) {
	                 var index = input.jqxDropDownButton('val');
		                if(!index) return false;
		                return true;
	            }
	   		},{
	        	input: "#glFiscalTypeId", message: "${uiLabelMap.CommonRequired}", action: 'blur change', 
	        	rule: function (input, commit) {
	                var index = input.jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	   		},{
	        	input: "#transactionDate", message: "${uiLabelMap.CommonRequired}", action: 'blur change', 
	        	rule: function (input, commit) {
	                var index = input.jqxDateTimeInput('getDate');
	                if(index){
	                	return true;
	                }
	                return false;
	            }
	   		}]
	    });
	};
	
	var initGrid = function(){
		initPartySelect();
		initInvoiceSelect($("#invoiceId"),$("#jqxGridInvoice"));
		initProductSelect();
		initPaymentSelect($("#paymentId"), $("#jqxGridPay"));
		initShipmentSelect( $("#shipmentId"),$("#jqxGridShip"));
		initWorkEffortSelect($("#workEffortId"),$("#jqxGridWE"));
		initAssetsSelect($("#fixedAssetId"), $("#fixedAssetJqx"));	
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">' + fixedAssetsData[fixedAssetIdIndex].fixedAssetId ? fixedAssetsData[fixedAssetIdIndex].fixedAssetId : '' + '</div>';
		$("#fixedAssetId").jqxDropDownButton('setContent', dropDownContent);
	};
	
	var init = function(){
		initRule();
		initElement();
		initGrid();
	};
	
	
	$(document).ready(function(){
		init();
	})
	$("#alterSave").click(function () {
		if($('#updateAcctgTrans').jqxValidator('validate')){
			$('#updateAcctgTrans').submit();	
		}
	});
	$("#alterCancel").click(function () {
		window.location.reload();
	});  
</script>
