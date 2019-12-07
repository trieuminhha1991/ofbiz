<#assign id1="personContacts"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<script>
	<#include "initContact.ftl" />
</script>
<#include "initRowDetailContact.ftl"/>	
<div id="person">
	<#assign dataField="[{ name: 'partyId', type: 'string' },
			 { name: 'firstName', type: 'string'},
			 { name: 'middleName', type: 'string'},
			 { name: 'lastName', type: 'string'},
			 { name: 'birthDate', type: 'string'},
			 { name: 'contactNumber', type: 'string'},
			 { name: 'permanentResidence', type: 'string'},
			 { name: 'assign', type: 'string'},
			 ]"/>

	<#assign columnlist="{ text: '${uiLabelMap.CustomerID}', filtertype: 'input', datafield: 'partyId', editable: false, hidden: true },
				   	 { text: '${uiLabelMap.customerName}', filtertype: 'input', datafield: 'firstName', width: '200',
				   	 	cellsrenderer: function (row, column, value) {
							var data = $('#personContacts').jqxGrid('getrowdata', row);
							var first = data.firstName ? data.firstName : '';
							var middle = data.middleName ?  data.middleName : '';
							var last = data.lastName ? data.lastName : '';
				    		return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+ last+ ' ' + middle + ' ' + first  +\"</div>\";
                      	}
				   	 },
                     { text: '${uiLabelMap.birthday}', filtertype: 'input', datafield: 'birthDate', hidden: true, filterable: false},
                     { text: '${uiLabelMap.phoneNumber}', filtertype: 'input', datafield: 'contactNumber', width: '250'},
                     { text: '${uiLabelMap.email}', filtertype: 'input', datafield: 'email', columntype:'custom', filterable: false, width: 200,
                     	cellsrenderer : function(row, column, value){
                     		var str = \"<div class='cell-custom-grid'>\";
                     		str += value.emailAddress ? value.emailAddress : '';
                     		str += \"</div>\";
                     		return str;
                     	}
                     },
                     { text: '${uiLabelMap.address}', filtertype: 'input', datafield: 'permanentResidence', columntype:'custom', filterable: false,
                     	cellsrenderer : function(row, column, value){
                     		var str = \"<div class='cell-custom-grid'>\";
                     		str += value.address1 ? value.address1 : '';
                     		str += \"</div>\";
                     		return str;
                     	}
                     }"/>
                      
	<@jqGrid id="${id1}" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
		showtoolbar="true" autoMeasureHeight="true" autoheight="false" scrollmode='logical'
		selectionmode="singlerow"
		filtersimplemode="true"  
		editable="false" 
		editrefresh ="true"
		editmode="click" 
		showtoolbar = "true" deleterow="true" autorowheight="true"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="300"
		url="jqxGeneralServicer?sname=getListContacts"
		removeUrl="jqxGeneralServicer?sname=deleteCustomerCrm&jqaction=D" deleteColumn="partyId"
		createUrl="jqxGeneralServicer?jqaction=C&sname=createCustomerCrm" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" 			
		addColumns="lastName;middleName;firstName;gender;birthPlace;birthDate;height;weight;referredByPartyId;idNumber;idIssuePlace;idIssueDate;maritalStatus;numberChildren;ethnicOrigin;religion;nativeLand;sourceTypeId;homeTel;diffTel;mobile;email;prAddress;prCountry;prProvince;prDistrict;prWard;crAddress;crCountry;crProvince;crDistrict;crWard" 
		addrefresh="true" customcontrol1="fa fa-envelope-o@${uiLabelMap.sendEmail}@javascript: void(0);@OpenPopupSendEmail()"
		/>
</div>
<script type="text/javascript" src="/delys/images/js/crm/newIssue.js"></script>
<#include "initModalContact.ftl"/>
<#include "initIssueForm.ftl"/>
<#include "popupAddIssue.ftl"/>

