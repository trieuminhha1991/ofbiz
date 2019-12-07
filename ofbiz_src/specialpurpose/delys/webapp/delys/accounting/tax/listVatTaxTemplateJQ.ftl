<script type="text/javascript">    
	


</script>

<#assign dataField="[{ name: 'vatId', type: 'string' },
					 { name: 'vatCode', type: 'string'},
					 { name: 'vatContent', type: 'string'},
					 { name: 'totalNoVatTax', type: 'string'},
					 { name: 'totalVatTax', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.accSTT}', width:50, datafield: 'vatCode'},
					 { text: '${uiLabelMap.accVatContent}', width:600, datafield: 'vatContent'},					     					 
					  { text: '${uiLabelMap.accTotalNoVatTax}', width:250, datafield: 'totalNoVatTax'},					   
					    { text: '${uiLabelMap.accTotalTax}', width:250, datafield: 'totalVatTax'}"
					 />					 	
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
</style>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListVatTaxTemplate" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="false" filtersimplemode="true" addType="popup"  defaultSortColumn ="vatId"		 
		 showtoolbar="false" viewSize="25" />
