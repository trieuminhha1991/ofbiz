<script type="text/javascript" src="/delys/images/js/bootbox.min.js">
<script type="text/javascript">
<#assign itlength = listCurrency.size()/>
<#if listCurrency?size gt 0>
    <#assign lc="var lc = ['" + StringUtil.wrapString(listCurrency.get(0).uomId?if_exists) + "'"/>
	<#assign lcValue="var lcValue = [\"" + StringUtil.wrapString(listCurrency.get(0).abbreviation?if_exists) + ":" + StringUtil.wrapString(listCurrency.get(0).description?if_exists) +"\""/>
	<#if listCurrency?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lc=lc + ",'" + StringUtil.wrapString(listCurrency.get(i).uomId?if_exists) + "'"/>
			<#assign lcValue=lcValue + ",\"" + StringUtil.wrapString(listCurrency.get(i).abbreviation?if_exists) + ":" + StringUtil.wrapString(listCurrency.get(i).description?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lc=lc + "];"/>
	<#assign lcValue=lcValue + "];"/>
<#else>
	<#assign lc="var lc = [];"/>
	<#assign lcValue="var lcValue = [];"/>
</#if>
${lc}
${lcValue}	
var dataLC = new Array();
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["uomId"] = lc[i];
    row["description"] = lcValue[i];
    dataLC[i] = row;
}

<#assign itlength = listUom.size()/>
<#if listUom?size gt 0>
    <#assign lu="var lu = ['" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "'"/>
	<#assign luValue="var luValue = [\"" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + ":" + StringUtil.wrapString(listUom.get(0).description?if_exists) +"\""/>
	<#if listUom?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lu=lu + ",'" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "'"/>
			<#assign luValue=luValue + ",\"" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + ":" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lu=lu + "];"/>
	<#assign luValue=luValue + "];"/>
<#else>
	<#assign lu="var lu = [];"/>
	<#assign luValue="var luValue = [];"/>
</#if>
${lu}
${luValue}	
var dataLU = new Array();
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["uomId"] = lu[i];
    row["description"] = luValue[i];
    dataLU[i] = row;
}

<#assign itlength = listUom.size()/>
<#if listProduct?size gt 0>
    <#assign lp="var lp = ['" + StringUtil.wrapString(listProduct.get(0).productId?if_exists) + "'"/>
	<#assign lpValue="var lpValue = [\"" + StringUtil.wrapString(listProduct.get(0).productId?if_exists) + ":" + StringUtil.wrapString(listProduct.get(0).internalName?if_exists) +"\""/>
	<#if listProduct?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lp=lp + ",'" + StringUtil.wrapString(listProduct.get(i).productId?if_exists) + "'"/>
			<#assign lpValue=lpValue + ",\"" + StringUtil.wrapString(listProduct.get(i).productId?if_exists) + ":" + StringUtil.wrapString(listProduct.get(i).internalName?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lp=lp + "];"/>
	<#assign lpValue=lpValue + "];"/>
<#else>
	<#assign lp="var lp = [];"/>
	<#assign lpValue="var lpValue = [];"/>
</#if>
${lp}
${lpValue}	
var dataLP = new Array();
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["productId"] = lp[i];
    row["description"] = lpValue[i];
    dataLP[i] = row;
}

</script>
<#assign statusId = shoppingProposalSelected.statusId?if_exists>
<#assign createdByUserLogin = shoppingProposalSelected.createdByUserLogin?if_exists>
<div class="widget-header widget-header-blue widget-header-flat">
	<h4 class="lighter">${uiLabelMap.ViewProcuremetProposal}: ${shoppingProposalSelected.requirementId?if_exists}</h4> 
	
	<span class="widget-toolbar none-content">
		<#if security.hasEntityPermission("PROCUREMENT" ,"_UPDATE", session) && roleTypeId == "DELYS_PROCURMENT">
			<#if statusId?exists && statusId != "REQ_APPROVED">
				<a href="javascript:void(0)" onclick="rejecProposalFromProcurementDepartment();">
					<i class="icon-remove open-sans">${uiLabelMap.CancelProcuremetProposal}</i>
				</a>
				<a href="<@ofbizUrl>editProcurementProposal?requirementId=${shoppingProposalSelected.requirementId?if_exists}</@ofbizUrl>">
					<i class="icon-pencil open-sans">${uiLabelMap.EditProcuremetProposal}</i>
				</a>
				<a href="<@ofbizUrl>sendProcurementProposal?requirementId=${shoppingProposalSelected.requirementId?if_exists}</@ofbizUrl>">
					<i class="icon-pencil open-sans">${uiLabelMap.SendProcurementProposal}</i>
				</a>
			</#if>
		</#if>
		<#if security.hasPermission("PROCUREMENT_ADMIN", session) && roleTypeId == "DELYS_CEO">
			<a href="<@ofbizUrl>approveProcurementProposal?requirementId=${shoppingProposalSelected.requirementId?if_exists}</@ofbizUrl>">
				<i class="icon-ok open-sans">${uiLabelMap.ApproveProcuremetProposal}</i>
			</a>
			<a href="javascript:void(0)" id="rejectProcurementProposal" onclick="rejectProcurementProposal();">
				<i class="icon-remove open-sans">${uiLabelMap.CancelProcuremetProposal}</i>
			</a>
		</#if>
		
	</span>

</div>
<div class="row-fluid">	
	<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateProcurementPropsal" name="updateProcurementPropsal" style="display: block;">
		<div class="row-fluid">
		<div class="span12"> 
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.estimatedBudget}:</label>
					<div class="controls">
						<b>
							<@ofbizCurrency amount = shoppingProposalSelected.estimatedBudget isoCode = shoppingProposalSelected.currencyUomId></@ofbizCurrency>
						</b>		
							
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.currencyUomId}:</label>
					<div class="controls">
						${shoppingProposalSelected.currencyUomId?if_exists}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.DAStatus}:</label>
					<div class="controls">
						<#if statusId?exists && statusId?has_content>
							<#assign status = delegator.findOne("StatusItem", {"statusId" : statusId}, true)>
							<#if status.statusCode?has_content>${status.get("description",locale)}</#if>
		                </#if>
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.CreatedBy}:</label>
					<div class="controls">
						${createdByUserLogin?if_exists} &nbsp;  - [<#if shoppingProposalSelected.createdDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shoppingProposalSelected.createdDate, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>]
					</div>
				</div>
			</div>
			
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.requirementStartDate}:</label>
					<div class="controls">
						<#if shoppingProposalSelected.requirementStartDate?exists>${shoppingProposalSelected.requirementStartDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.requiredByDate}:</label>
					<div class="controls">
						<#if shoppingProposalSelected.requiredByDate?exists>${shoppingProposalSelected.requiredByDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.DepartMent}:</label>
					<div class="controls">
						<#assign department = dispatcher.runSync("getDepartmentFromUserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("createdByUserLogin", createdByUserLogin, "userLogin", userLogin)) !/>
	
						<#if department?exists>
							${department.departmentName?if_exists}
						</#if>
						
					</div>
				</div>
				
			</div><!--.span6-->
			
		</div>
		<div class="span12 no-left-margin">
			<div class="control-group">
				<label class="control-label" for="description">${uiLabelMap.Description}:</label>
				<div class="controls">
					<div class="span12">
						<textarea  name="descriptionRequirement" id="descriptionRequirement" class="note-area no-resize" autocomplete="off"  value=""></textarea>
					</div>
				</div>
			</div>
		
	</div>
		
	</div><!--.row-->
	</div>
	
	<div style="clear:both"></div>
		
	<div id="list-product-procurement">
		<#assign dataField="[{ name: 'requirementId', type: 'string' },
					 { name: 'reqItemSeqId', type: 'string' },
					 { name: 'productId', type: 'string' },
					 { name: 'internalName', type: 'string' },
					 { name: 'quantity', type: 'string' },
					 { name: 'quantityUomId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'estimatedPrice' , type: 'number' },
					 { name: 'estimatedReceiveDate', type: 'date', other: 'Timestamp' },
					 { name: 'reason', type: 'string' }
					 ]
					"/>
		<#assign columnlist="{ text: '${uiLabelMap.requirementId}', datafield: 'requirementId', editable: false, width: 100, 
								cellsrenderer: function (row, column, value) {
								var data = $('#jqxgridProductProcurement').jqxGrid('getrowdata', row);
		       					return '<a style = \"margin-left: 10px\" href=' + 'viewProcurementProposal?requirementId=' + data.requirementId + '>' +  data.requirementId + '</a>'
		   						}
							 },
							 { text: '${uiLabelMap.ProductName}', datafield: 'internalName', editable: false, width: 300, datafield: 'internalName'},
							 { text: '${uiLabelMap.quantity}', datafield: 'quantity', width: 70},
							 { text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', width: 120},
							 { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 100},
							 { text: '${uiLabelMap.Reason}', datafield: 'reason'}
							
							"/>
		<@jqGrid url="jqxGeneralServicer?requirementId=${requirementId}&sname=JQListRequirementItem" dataField=dataField columnlist=columnlist
				 id="jqxgridProductProcurement" filtersimplemode="true" showtoolbar="true"
				 filterable="false" sortable="false"
				 editable="false"
		 />
	</div>

</div>	

<script type="text/javascript">
var description;
$(document).ready(function (){
	 description = CKEDITOR.replace('descriptionRequirement', {
	    height: '100px',
	    width: '87%',
	    skin: 'office2013',
	    readOnly:true
	});
	
	 var descStr = "${StringUtil.wrapString(shoppingProposalSelected.description)}";
	 description.setData(descStr);

});

</script>
<script type="text/javascript">
function rejectProcurementProposal(){
	
		bootbox.confirm("${uiLabelMap.AreyouConfirmRejectedProcuremetProposal}", function(result) {
			if(result) {
				window.location = "cancelProcurementProposal?requirementId=${shoppingProposalSelected.requirementId?if_exists}";
			}
		});

}
function rejecProposalFromProcurementDepartment(){
	
	bootbox.confirm("${uiLabelMap.AreyouConfirmRejectedProcuremetProposal}", function(result) {
		if(result) {
			window.location = "rejectedFromProcurementDepartment?requirementId=${shoppingProposalSelected.requirementId?if_exists}";
		}
	});

}
</script>