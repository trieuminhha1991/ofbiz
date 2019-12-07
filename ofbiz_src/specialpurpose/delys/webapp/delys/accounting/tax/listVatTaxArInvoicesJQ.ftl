<script type="text/javascript">
	<#assign itlength = product.size()/>
    <#if product?size gt 0>
	    <#assign vaProduct="var vaProduct = ['" + StringUtil.wrapString(product.get(0).productId?if_exists) + "'"/>
		<#assign vaProductValue="var vaProductValue = ['" + StringUtil.wrapString(product.get(0).productName?if_exists) + " [" + StringUtil.wrapString(product.get(0).productId?if_exists) +"]" + "'"/>
		<#if product?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaProduct=vaProduct + ",'" + StringUtil.wrapString(product.get(i).productId?if_exists) + "'"/>
				<#assign vaProductValue=vaProductValue + ",\"" + StringUtil.wrapString(product.get(i).productName?if_exists) + " [" + StringUtil.wrapString(product.get(i).productId?if_exists) +"]" + "\""/>
			</#list>
		</#if>
		<#assign vaProduct=vaProduct + "];"/>
		<#assign vaProductValue=vaProductValue + "];"/>
	<#else>
    	<#assign vaProduct="var vaProduct = [];"/>
    	<#assign vaProductValue="var vaProductValue = [];"/>
    </#if>
	${vaProduct}
	${vaProductValue}	
	var dataProduct = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["productId"] = vaProduct[i];
        row["productName"] = vaProductValue[i];
        dataProduct[i] = row;
    }
    
	


</script>


<script type="text/javascript">
	var linkProductrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < vaProduct.length; i++){
        	if(vaProduct[i] == data.productId){
        		return "<span>" + vaProductValue[i] + "</span>";
        	}
        }
        return "";
    }
</script>

<#assign dataField="[{ name: 'invoiceSeqId', type: 'string' },
					 { name: 'invoiceTemplate', type: 'string'},
					 { name: 'invoiceCode', type: 'string'},
					 { name: 'invoiceId', type: 'string'},
					 { name: 'invoiceDate', type: 'date'},
					 { name: 'partyId', type: 'string'},
					 { name: 'partyNameResultFrom', type: 'string'}, 
					 { name: 'partyTaxId', type: 'string'},					 
					 { name: 'productId', type: 'string'},
					 { name: 'totalNoTax', type: 'string'},
					 { name: 'totalTax', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'conditionType', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.accSTT}', width:100, datafield: 'invoiceSeqId'},
					 { text: '${uiLabelMap.accInvoiceTemplate}', width:150, datafield: 'invoiceTemplate', columngroup: 'InvoiceDetails'},
					 { text: '${uiLabelMap.accInvoiceCode}', width:150, datafield: 'invoiceCode', columngroup: 'InvoiceDetails' },   
 					 { text: '${uiLabelMap.accInvoiceId}', width:150, datafield: 'invoiceId',  columngroup: 'InvoiceDetails', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},                                              
					 { text: '${uiLabelMap.accInvoiceDate}', width:200, datafield: 'invoiceDate', columngroup: 'InvoiceDetails', cellsformat:'dd-MM-yyyy'},
					 { text: '${uiLabelMap.accToParty}', width:500, datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyId + ']' + \"</span>\";
					 	}
					 },

					 { text: '${uiLabelMap.accArPartyTaxId}', width:150, datafield: 'partyTaxId'},
					 { text: '${uiLabelMap.accProduct}', width:400, datafield: 'productId', cellsrenderer:linkProductrenderer},
					  { text: '${uiLabelMap.accArTotalNoTax}', width:250, datafield: 'totalNoTax'},					   
					    { text: '${uiLabelMap.accTotalTax}', width:250, datafield: 'totalTax'},
					 { text: '${uiLabelMap.accArTaxDescription}', width:200, datafield: 'description'},
					 { text: '${uiLabelMap.accConditionType}', width:200, datafield: 'conditionType'}"
					 />	
<#assign columngrouplist = "{ text: '${uiLabelMap.accArInvoice}', align: 'center', name: 'InvoiceDetails' }" />					 	
<@jqGridMinimumLib/>
<div id="jqxPanel" style="width:100%;">
	<table style="margin:0 auto;margin-top:10px;width:100%;position:relative;">	
		<tr>
			<td width="150">${uiLabelMap.FormFieldTitle_invoiceDate}</td>
			<td>
		        <div id="filterType4"></div>
			</td>
			<td>
				<div id="invoiceDate1"></div>
			</td>
			<td>
				<div id="filterType5"></div>
			</td>
			<td>
				<div id="invoiceDate2"></div>
			</td>
		</tr>
		<tr>
			<td colspan="7" align="center">
		       <input type="button" value="${uiLabelMap.filter}" id='jqxButton' style="margin-left:8px;"/>
		    </td>
		</tr>
	</table>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	var date = new Date();
	var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
	var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
	lastDay.setHours(23,59,59,999);
		
	$('#filterType4').jqxDropDownList({ selectedIndex: 0, width:200,  source: dataDatetimeFilterType, displayMember: "description", valueMember: "datetimeFilterType", theme: theme});
	$('#filterType4').jqxDropDownList('val','GREATER_THAN_OR_EQUAL');
	$('#filterType5').jqxDropDownList({ selectedIndex: 0, width:200,  source: dataDatetimeFilterType, displayMember: "description", valueMember: "datetimeFilterType", theme: theme});
	$('#filterType5').jqxDropDownList('val','LESS_THAN_OR_EQUAL');
	$("#invoiceDate1").jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#invoiceDate1").jqxDateTimeInput('val', firstDay);
	$("#invoiceDate2").jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#invoiceDate2").jqxDateTimeInput('val', lastDay);
 	if($("#invoiceDate1").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate1').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType4').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
        if($("#invoiceDate2").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate2').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType5').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
	$("#jqxPanel").jqxPanel({ height: 120, theme:theme});
	$("#jqxButton").jqxButton({ width: '154', height: '30', theme:theme});
	$("#jqxButton").on('click', function () {
        if($("#invoiceDate1").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate1').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType4').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
        if($("#invoiceDate2").val() != ""){
        	outFilterCondition += "|OLBIUS|invoiceDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#invoiceDate2').val();
	        outFilterCondition += "|SUIBLO|" + $('#filterType5').val();
	        outFilterCondition += "|SUIBLO|" + "and";
        }
		$('#jqxgrid').jqxGrid('updatebounddata');
    });    
    
</script>
<style type="text/css">
	#jqxPanel td{
		padding:5px;
	}
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
	.jqx-grid-groups-header.jqx-grid-groups-header-olbius {
		display:none !important;
	}	
	#wrapperjqxgrid {
	top: -35px!important;
	}	
</style>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListTaxARInvoice" dataField=dataField columnlist=columnlist columngrouplist=columngrouplist jqGridMinimumLibEnable="false" filterable="true" groupsexpanded="true" filtersimplemode="true" addrow="true" addType="popup" groupable="true" groups="conditionType"  defaultSortColumn ="conditionType"
		 otherParams="partyNameResultFrom:S-getPartyNameForDate(partyId{partyId},compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 addColumns="invoiceId" showtoolbar="false" alternativeAddPopup="alterpopupWindow"/>
