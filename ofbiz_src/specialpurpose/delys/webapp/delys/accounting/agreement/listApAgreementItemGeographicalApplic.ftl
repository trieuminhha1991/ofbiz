<script>
	<#assign geoList = delegator.findList("Geo", null, null, null, null, false) />
	var geoData = new Array();
	<#list geoList as geo>
		var row = {};			
		row['geoId'] = '${geo.geoId?if_exists}';
		row['geoName'] = '${geo.geoName?if_exists}';
		geoData[${geo_index}] = row; 
	</#list>
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'geoId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accGeoName}', datafield: 'geoId', editable: false, 
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < geoData.length; i++){
					 			if(value == geoData[i].geoId){
					 				return \"<span>\" + geoData[i].geoId + ' [' + geoData[i].geoName + ']' + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}	
					 }
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementGeographicalApplic&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementGeographicalApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementGeographicalApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementGeographicalApplic&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementItemGeoGraphicalApplic.ftl"/>