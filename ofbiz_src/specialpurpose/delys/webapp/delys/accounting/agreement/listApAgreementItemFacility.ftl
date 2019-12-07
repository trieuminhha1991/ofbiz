<script>
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = new Array();
	<#list facilityList as facility>
		var row = {};			
		row['facilityId'] = '${facility.facilityId?if_exists}';
		row['facilityName'] = '${facility.facilityName?if_exists}';
		facilityData[${facility_index}] = row; 
	</#list>
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'facilityId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accFacilityName}', datafield: 'facilityId', editable: false, 
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < facilityData.length; i++){
					 			if(value == facilityData[i].facilityId){
					 				return \"<span>\" + facilityData[i].facilityId + '[' + facilityData[i].facilityName + ']' + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}	
					 }
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" addrefresh="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementFacilityAppl&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementFacilityAppl&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementFacilityAppl&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementFacilityAppl&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementItemFacility.ftl"/>