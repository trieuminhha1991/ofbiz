	<style>
	.ui-autocomplete.ui-menu.ui-widget.ui-widget-content.ui-corner-all {
		z-index:18005!important;
	}
	.view-calendar input,.field-lookup input {
		width:160px;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script> 
<script type="text/javascript" src="/delys/images/js/filterDate.js"></script>
<script>
	var initComBoBox = function(combobox){
		 		var sourceMNG =
		            {
		                datatype: "json",
		                datafields: [
		                    { name: 'partyId' },
		                    { name: 'firstName' },
		                    { name: 'middleName' },
		                    { name: 'lastName' },
		                    { name: 'fullName' },
		                    { name: 'groupName' }
		                ],
		                type: "POST",
		                root: "listParties",
		                contentType: 'application/x-www-form-urlencoded',
		                url: "fixedAssetManagerableList"
	            };
	            var dataAdapterMNG = new $.jqx.dataAdapter(sourceMNG,
	                {
	                    formatData: function (data) {
	                        if (combobox.jqxComboBox('searchString') != undefined) {
	                            data.searchKey = combobox.jqxComboBox('searchString');
	                            return data;
	                        }
	                    },
	                    downloadComplete : function(data,status,xhr){
	                    	if(data.listParties.length){
	                    	}
	                    }
	                }
	            );
	           combobox.jqxComboBox(
	            {
	                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
	                source: dataAdapterMNG,
	                remoteAutoComplete: true,
	                displayMember : "fullName",
	                valueMember: "partyId",
	                renderer: function (index, label, value) {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                    	var label ="";
	                    	label += (item.groupName != null) ? item.groupName : "";
	                    	label += (item.fullName != null) ? item.fullName : "";
	                    	label += (item.firstName != null) ? item.lastName + " ": "";
	                    	label += (item.middleName != null) ? item.middleName + " " : "";
	                    	label += (item.lastName != null) ? item.firstName +" ": "";
	                        label += " (" + item.partyId + ")";
	                        return label;
	                    }
	                    return "";
	                },
	                renderSelectedItem: function(index, item)
	                {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                        var label = "";
	                        label += (item.groupName != null) ? item.groupName : "";
	                    	label += (item.fullName != null) ? item.fullName : "";
	                    	label += (item.firstName != null) ? item.lastName + " ": "";
	                    	label += (item.middleName != null) ? item.middleName + " " : "";
	                    	label += (item.lastName != null) ? item.firstName +" ": "";
	                        return label;
	                       
	                    }
	                    return "";   
	                },
	                search: function (searchString) {
	                    dataAdapterMNG.dataBind();
	                    combobox.jqxComboBox('open');
	                }
	            });           
		 }

</script>
	<#assign dataField= "[
		{name : 'fixedAssetId',type : 'string'},
		{name : 'fromDate',type : 'date',other : 'Timestamp'},
		{name : 'thruDate',type : 'date',other : 'Timestamp'},
		{name : 'registrationDate',type : 'date',other : 'Timestamp'},
		{name : 'govAgencyPartyId',type : 'string'},
		{name : 'registrationNumber',type : 'number',other : 'BigDecimal'},
		{name : 'licenseNumber',type : 'number',other : 'BigDecimal'}
	]"/>
	
	<#assign columnlist= "
		{text : '${uiLabelMap.FormFieldTitle_fixedAssetId}',datafield : 'fixedAssetId',hidden : 'true'},
		{text : '${uiLabelMap.FormFieldTitle_fromDate}',datafield : 'fromDate',cellsformat : 'dd/MM/yyyy : hh:mm:ss',width : '20%',editable:false,filtertype : 'range'},
		{text : '${uiLabelMap.FormFieldTitle_thruDate}',datafield : 'thruDate',cellsformat : 'dd/MM/yyyy : hh:mm:ss',filtertype : 'range',columntype : 'datetimeinput',createeditor(row,column,editor){
			var data = $('#jqxgridRegistration').jqxGrid('getrowdata',row);
			editor.jqxDateTimeInput({ allowNullDate : true,value : data.thruDate ? data.thruDate : null, formatString: 'dd/MM/yyyy hh:mm:ss',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
		}},
		{text : '${uiLabelMap.FormFieldTitle_registrationDate}',datafield : 'registrationDate',cellsformat : 'dd/MM/yyyy hh:mm:ss',columntype : 'datetimeinput',filtertype : 'range',createeditor(row,column,editor){
			var data = $('#jqxgridRegistration').jqxGrid('getrowdata',row);
			editor.jqxDateTimeInput({ allowNullDate : true,value : data.registrationDate ? data.registrationDate : null, formatString: 'dd/MM/yyyy hh:mm:ss',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
		}},
		{text : '${uiLabelMap.AccountingFixedAssetGovAgencyPartyId}',datafield : 'govAgencyPartyId',width : '15%',columntype :'combobox',createeditor(row,column,editor){
			initComBoBox(editor);
		}},
		{text : '${uiLabelMap.AccountingFixedAssetRegNumber}',datafield : 'registrationNumber',columntype : 'numberinput',createeditor : function(row,column,editor){
			editor.jqxNumberInput({min: 0 ,digits : 15,decimalDigits : 0,spinButtons : true});
		}},
		{text : '${uiLabelMap.AccountingFixedAssetLicenseNumber}',datafield : 'licenseNumber',columntype : 'numberinput',createeditor : function(row,column,editor){
			editor.jqxNumberInput({min: 0 ,digits : 15,decimalDigits : 0,spinButtons : true});
		}}
	"/>
<@jqGrid id="jqxgridRegistration" url="jqxGeneralServicer?sname=JQListFixedAssetRegistration&fixedAssetId=${fixedAssetId?if_exists}" dataField=dataField columnlist=columnlist deleterow="true"
		 filtersimplemode="true" showtoolbar="true" filterable="true"
		 editable="true"  editrefresh="true" addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow" 
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createFixedAssetRegistration"
		 addColumns="fixedAssetId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);registrationDate(java.sql.Timestamp);govAgencyPartyId;registrationNumber;licenseNumber" clearfilteringbutton="true"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateFixedAssetRegistration" 
		 editColumns="fixedAssetId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);registrationDate(java.sql.Timestamp);govAgencyPartyId;registrationNumber;licenseNumber"
		 removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteFixedAssetRegistration"
		 deleteColumn="fixedAssetId;fromDate(java.sql.Timestamp)"
 /> 
 
 <#include "component://delys/webapp/delys/accounting/popup/popupFixedAssetRegistration.ftl"/>