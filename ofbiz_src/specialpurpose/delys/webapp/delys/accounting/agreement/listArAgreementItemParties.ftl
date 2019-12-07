<@jqGridMinimumLib/>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'groupName', type: 'string'},
					 { name: 'fullName', type: 'string'}
					 ]"/>

<#assign columnlist="
					{text : '${uiLabelMap.FormFieldTitle_agreementId}',datafield : 'agreementId'},
					{ text: '${uiLabelMap.accPartyName}', filtertype: 'olbiusdropgrid', datafield: 'partyId', editable: false,
                     	 cellsrenderer: function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	var name  = (data.groupName!=null) ? data.groupName : data.fullName;
							return '<a style = \"margin-left: 10px\" href=' + '/delys/control/accApEditAgreementItemParty?partyId=' + data.partyId + '&' + 'agreementId=' + data.agreementId + '&' + 'agreementItemSeqId=' + data.agreementItemSeqId + '>' + \"[\" + data.partyId + \"]\"  + \" \" + name  + '</a>';
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width('100%');
			   			}}
					 "/>

<@jqGrid  filterable="true" addrefresh="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false" jqGridMinimumLibEnable="false"
		 	url="jqxGeneralServicer?sname=JQListAgreementItemParties&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	createUrl="jqxGeneralServicer?sname=createAgreementPartyApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	removeUrl="jqxGeneralServicer?sname=removeAgreementPartyApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];partyId"
		 	deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];partyId"
		 />
<script src="/delys/images/js/generalUtils.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>	 
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementItemParties.ftl"/>
<#include "component://delys/webapp/delys/accounting/popup/popupGridPartyGeneralFilter.ftl"/>