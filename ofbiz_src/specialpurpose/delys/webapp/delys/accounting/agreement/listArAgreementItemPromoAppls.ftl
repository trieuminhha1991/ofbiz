<script>
	<#assign productPromoList = delegator.findList("ProductPromo", null, null, null, null, false) />
	var ppData = new Array();
	<#list productPromoList as pp>
		var row = {};
		<#assign promoName = StringUtil.wrapString(pp.promoName)/>
		row['promoName'] = '${promoName}';
		row['productPromoId'] = '${pp.productPromoId}';
		ppData[${pp_index}] = row;
	</#list> 
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'productPromoId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'sequenceNum', type: 'string'}				 
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accProductPromoId}', datafield: 'productPromoId', width: '30%', editable: false, 
						cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < ppData.length; i++){
					 			if(value == ppData[i].productPromoId){
					 				return \"<span>\" + '[' + ppData[i].productPromoId + '] ' + ppData[i].promoName + \"</span>\";
					 			}}
					 		}					 		
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy ', columntype: 'template', width: '25%', editable: false,
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({formatString: 'yyyy/MM/dd' });
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '25%', cellsformat: 'dd/MM/yyyy', columntype: 'template',
					 	createeditor: function (row, column, editor) {
					 			editor.jqxDateTimeInput({formatString: 'yyyy/MM/dd' });
                     	},validation : function(cell,value){
                     		var data  = $('#jqxgrid').jqxGrid('getrowdata',cell.row);
                     		if(data.fromDate > value){
                     			return {result : false,message : \"${StringUtil.wrapString(uiLabelMap.NotiDateInvalid)}\"};
                     		}
                     		return true;
                     	}
					 },
					 { text: '${uiLabelMap.SequenceId}', width: '20%', datafield: 'sequenceNum'},
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" addrefresh="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementPromoAppls&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=U&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=C&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=D&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp)"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"		 
		 />
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementPromoAppls.ftl"/>