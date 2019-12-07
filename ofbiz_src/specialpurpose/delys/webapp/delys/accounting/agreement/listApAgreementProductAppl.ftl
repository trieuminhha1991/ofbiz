<#if parameters.agreementId?exists && parameters.agreementItemSeqId?exists>
<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'productId', type: 'string'},
					 { name: 'price', type: 'number'},
					 { name: 'productName', type: 'string'}			 
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accProductName}', datafield: 'productId', width: '60%', editable: false, 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return \"<span>\" + data.productId + '[' + data.productName + ']' + \"</span>\";
    					}										 		
					 },
					 { text: '${uiLabelMap.unitPrice}', width: '40%', datafield: 'price',cellsrenderer : function(row,column,value){
					 	var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
					 	var format =  formatcurrency(data.price,'${defaultCurrencyUomId?if_exists}');
					 		return '<span>' + format + '</span>';
					 }}
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField addrefresh="true" columnlist=columnlist clearfilteringbutton="true" usecurrencyfunction="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementProductAppl&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=U&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=C&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=D&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId;price"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId;price"
		 otherParams="productName:S-getProductName(productId{productId})<productName>"		 
		 />
<script src="/delys/images/js/generalUtils.js"></script>		 
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementProductAppl.ftl"/>
<#else>
	


</#if>