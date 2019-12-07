<script type="text/javascript">
	var linkGLRrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        /*for(i=0;i < vaReason.length; i++){
        	if(vaReason[i] == data.varianceReasonId){
        		return "<span>" + vaReasonValue[i] + "</span>";
        	}
        }*/
        return "";
    }
    var linkGLArenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        /*for(i=0;i < lgaoac.length; i++){
        	if(lgaoac[i] == data.glAccountId){
        		return "<span>" + lgaoacValue[i] + "</span>";
        	}
        }*/
        return "";
    }
    var linkOPIrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        /*for(i=0;i < lgaoac.length; i++){
        	if(lgaoac[i] == data.glAccountId){
        		return "<span>" + lgaoacValue[i] + "</span>";
        	}
        }*/
        return "";
    }
</script>
<script type="text/javascript">
	<#assign itlength = listGlAccount.size()/>
    <#if listGlAccount?size gt 0>
	    <#assign vaGLA="var vaGLA = ['" + StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + "'"/>
		<#assign vaGLAValue="var vaGLAValue = [\"" + StringUtil.wrapString(listGlAccount.get(0).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccount.get(0).accountName?if_exists) + "[" + StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + "]\""/>
		<#if listGlAccount?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaGLA=vaGLA + ",'" + StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + "'"/>
				<#assign vaGLAValue=vaGLAValue + ",\"" + StringUtil.wrapString(listGlAccount.get(i).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccount.get(i).accountName?if_exists) + "[" + StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + "]\""/>
			</#list>
		</#if>
		<#assign vaGLA=vaGLA + "];"/>
		<#assign vaGLAValue=vaGLAValue + "];"/>
	<#else>
    	<#assign vaGLA="var vaGLA = [];"/>
    	<#assign vaGLAValue="var vaGLAValue = [];"/>
    </#if>
	${vaGLA}
	${vaGLAValue}	
	var dataGLA = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountId"] = vaGLA[i];
        row["description"] = vaGLAValue[i];
        dataGLA[i] = row;
    }
</script>
<#assign dataField="[{ name: 'glReconciliationId', type: 'string' },
					 { name: 'glReconciliationName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'lastModifiedByUserLogin', type: 'string'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'reconciledBalance', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glReconciliationId}', datafield: 'glReconciliationId', cellsrenderer:linkGLRrenderer},
					 { text: '${uiLabelMap.FormFieldTitle_glReconciliationName}', datafield: 'glReconciliationName'},
					 { text: '${uiLabelMap.description}', datafield: 'description'},
					 { text: '${uiLabelMap.FormFieldTitle_createdByUserLogin}', datafield: 'createdByUserLogin'},
					 { text: '${uiLabelMap.FormFieldTitle_lastModifiedByUserLogin}', datafield: 'lastModifiedByUserLogin'},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountId}', datafield: 'glAccountId', cellsrenderer:linkGLArenderer},
					 { text: '${uiLabelMap.FormFieldTitle_organizationPartyId}', datafield: 'organizationPartyId', cellsrenderer:linkOPIrenderer},
					 { text: '${uiLabelMap.FormFieldTitle_reconciledBalance}', datafield: 'reconciledBalance'}"
					 />		
<@jqGridMinimumLib/>	
<style type="text/css">
	td{
		padding:10px;
	}
</style>		 			 
<div id="jqxPanel">
	<table style="width:80%;margin:0 auto;">
		<tr>
			<td>${uiLabelMap.FormFieldTitle_glAccountId}</td>
			<td>
				<div id="glAccountIdList"></div>
			</td>
			<td>${uiLabelMap.fromDate}</td>
			<td>
				<div id="fromDate"></div>
			</td>
			<td>${uiLabelMap.thruDate}</td>
			<td>
				<div id="thruDate"></div>
			</td>
		</tr>
		<tr>
			<td colspan="6" align="center">
		       <input type="button" value="${uiLabelMap.filter}" id='jqxButton' />
		    </td>
		</tr>
	</table>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	$('#glAccountIdList').jqxDropDownList({ selectedIndex: 0,  source: dataGLA, displayMember: "description", valueMember: "glAccountId", theme: theme});
	$("#fromDate").jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#thruDate").jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#jqxPanel").jqxPanel({ height: 130, theme:theme});
	$("#jqxButton").jqxButton({ width: '150', theme:theme});
	$("#jqxButton").on('click', function () {
		outFilterCondition = "|OLBIUS|glAccountId";
		outFilterCondition += "|SUIBLO|" + $('#glAccountIdList').val();
        outFilterCondition += "|SUIBLO|" + "EQUAL";
        outFilterCondition += "|SUIBLO|" + "or";
        if($("#fromDate").val() != ""){
        	outFilterCondition += "|OLBIUS|reconciledDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#fromDate').val();
	        outFilterCondition += "|SUIBLO|" + "GREATER_THAN_OR_EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
        if($("#thruDate").val() != ""){
        	outFilterCondition += "|OLBIUS|reconciledDate(Timestamp)";
			outFilterCondition += "|SUIBLO|" + $('#thruDate').val();
	        outFilterCondition += "|SUIBLO|" + "LESS_THAN_OR_EQUAL";
	        outFilterCondition += "|SUIBLO|" + "or";
        }
		$('#jqxgrid').jqxGrid('updatebounddata');
    });
    /*$("#jqxGrid").on("bindingcomplete", function (event) {
    	
	});*/
</script>

<@jqGrid url="jqxGeneralServicer?sname=JQListGlReconciliation" entityName="123" dataField=dataField columnlist=columnlist id="jqxgrid" jqGridMinimumLibEnable="false"/>