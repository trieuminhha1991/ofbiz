<script>
	<#assign payGrTypes = delegator.findList("PaymentGroupType", null, null, null, null, false) />
	var payGrTypeData = new Array();
	<#list payGrTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['paymentGroupTypeId'] = '${item.paymentGroupTypeId}';
		row['description'] = '${description}';
		payGrTypeData[${item_index}] = row;
	</#list>
	
	<#assign pmtGrMembers = delegator.findList("PaymentGroupMember", null, null, null, null, false) />
	var pmtGrMemberData = new Array();
	var pmtGrpId1;
	var index1 = 0;
	<#list pmtGrMembers as item>
		if(pmtGrpId1 != '${item.paymentGroupId}'){
			var row = {};
			row['paymentGroupId'] = '${item.paymentGroupId}';
			pmtGrMemberData[index1] = row;
			pmtGrpId1 = '${item.paymentGroupId}';
			index1 += 1;
		}
	</#list>
	
	<#assign pmtGrpMembrPaymentAndFinAcctTrans = delegator.findList("PmtGrpMembrPaymentAndFinAcctTrans", null, null, null, null, false) />
	var pmtGrpMembrPaymentAndFinAcctTransData = new Array();
	var pmtGrpId2;
	var index2 = 0;
	<#list pmtGrpMembrPaymentAndFinAcctTrans as item>
		if(pmtGrpId2 != '${item.paymentGroupId}'){
			var row = {};
			row['finAccountTransStatusId'] = '${item.finAccountTransStatusId}';
			row['paymentGroupId'] = '${item.paymentGroupId}';
			pmtGrpMembrPaymentAndFinAcctTransData[index2] = row;
			pmtGrpId2 = '${item.paymentGroupId}';
			index2 += 1; 
		}
	</#list>
</script>
<#assign columnlist="{ text: '${uiLabelMap.paymentGroupId}', dataField: 'paymentGroupId', editable: false,
					 	cellsrenderer: function(row, column, value){
					 		return '<span><a href=PaymentGroupOverview?paymentGroupId=' + value +'>' + value + '</a></span>'
					 	}
					 },
                     { text: '${uiLabelMap.paymentGroupTypeId}', dataField: 'paymentGroupTypeId', editable: false, 
                     	cellsrenderer: function(row, column, value){
                     		for(i = 0; i < payGrTypeData.length; i++){
                     			if(payGrTypeData[i].paymentGroupTypeId == value){
                     				return '<span title='+ value + '>' + payGrTypeData[i].description +'</span>'
                     			}
                     		}
                     	}
                     },
                     { text: '${uiLabelMap.paymentGroupName}', dataField: 'paymentGroupName', editable: true},
                     { text: '${uiLabelMap.DepositSlip}', 
                    	 cellsrenderer: function(row, column, value){
                    		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                    		 for(i = 0; i < pmtGrMemberData.length; i++){
                    			 if(pmtGrMemberData[i].paymentGroupId == data.paymentGroupId){
                    				 return '<span><a href=DepositSlip.pdf?paymentGroupId=' + data.paymentGroupId +'>PDF</a></span>'
                    			 }
                    		 }
                    		 return ;
                    	 }
                     },
                     { text: '${uiLabelMap.Cancel}', 
                    	 cellsrenderer: function(row, column, value){
                    		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                    		 if(pmtGrMemberData[i].paymentGroupId == data.paymentGroupId && pmtGrpMembrPaymentAndFinAcctTransData.length == 0){
                    			 return '<span><a href=deleteDepositSlip?paymentGroupId=' + data.paymentGroupId +'&finAccountId=' + data.finAccountId + '&glReconciliationId=' + data.glReconciliationId + '>Cancel</a></span>';
                    		 }else{
                    			 for(i = 0; i < pmtGrpMembrPaymentAndFinAcctTransData.length; i++){
                        			 if(pmtGrMemberData[i].paymentGroupId == data.paymentGroupId && pmtGrpMembrPaymentAndFinAcctTransData[i].paymentGroupId == data.paymentGroupId && pmtGrpMembrPaymentAndFinAcctTransData[i].finAccountTransStatusId != 'FINACT_TRNS_APPROVED'){
                        				 return '<span><a href=deleteDepositSlip?paymentGroupId=' + data.paymentGroupId +'&finAccountId=' + data.finAccountId + '&glReconciliationId=' + data.glReconciliationId + '>Cancel</a></span>';
                        			 }
                        		 }
                        		 return ;
                    		 }
                    	 }
                     }
                     "/>

<#assign dataField="[{ name: 'paymentGroupId', type: 'string' },
                 	{ name: 'paymentGroupTypeId', type: 'string' },
                 	{ name: 'paymentGroupName', type: 'string' }
					]"/>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListDepositSlip&finAccountId=${parameters.finAccountId}}" createUrl="jqxGeneralServicer?jqaction=C&sname=createPaymentGroup"
		 addColumns="paymentGroupTypeId;paymentGroupName" updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePaymentGroup" editColumns="paymentGroupId;paymentGroupName"
		/>

<div id="alterpopupWindow">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
    	<table>
	 		<tr>
	 			<td align="right">${uiLabelMap.paymentGroupTypeId}:</td>
	 			<td align="left"><div id="paymentGroupTypeIdAdd"></div></td>
	 		</tr>
	 		<tr>
	 		<td align="right">${uiLabelMap.paymentGroupName}:</td>
	 			<td align="left"><input id="paymentGroupNameAdd"></input></td>
	 		</tr>
	 		<tr>
	 			<td align="right"></td>
	 			<td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	 		</tr>
	 	</table>
	 </div>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	$("#alterpopupWindow").jqxWindow({
		width: 580, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$('#paymentGroupTypeIdAdd').jqxDropDownList({ selectedIndex: 0,  source: payGrTypeData, displayMember: "description", valueMember: "paymentGroupTypeId"});
	$('#paymentGroupNameAdd').jqxInput({width: '195px'});

	$("#alterCancel").jqxButton();
	$("#alterSave").jqxButton();

	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		row = { 
				paymentGroupTypeId:$('#paymentGroupTypeIdAdd').val(),
				paymentGroupName:$('#paymentGroupNameAdd').val()
    	  	};
	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    // select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
	});
</script>