<#assign partyAccountingPreference = dispatcher.runSync("getPartyAccountingPreferences",{"userLogin" : userLogin,"organizationPartyId" : "${organizationPartyId}"}) !>
<#if partyAccountingPreference?exists && partyAccountingPreference?has_content>
	<#assign aggregatedPartyAcctgPreference = partyAccountingPreference.partyAccountingPreference !>
</#if>

<#assign dataField="[{ name: 'glAccountId', type: 'string' },
               		{ name: 'glAccountTypeId', type: 'string' },
               		{ name: 'glAccountClassId', type: 'string' },
               		{ name: 'glResourceTypeId', type: 'string' },
               		{ name: 'glXbrlClassId', type: 'string' },
               		{ name: 'glTaxFormId', type: 'string' },
               		{ name: 'parentGlAccountId', type: 'string' },
                	{ name: 'accountCode', type: 'string' },
                	{ name: 'accountName', type: 'string' },
                	{ name: 'codeParent', type: 'string' },
                	{ name: 'description', type: 'string' },
                	{ name: 'productId', type: 'string' },
                	{ name: 'externalId', type: 'string' },
                	{ name: 'postedBalance', type: 'number' }]"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 170, editable:false, hidden: true},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountTypeId}', dataField: 'glAccountTypeId', width: 200, columntype: 'dropdownlist', hidden: true,
					 	createeditor: function (row, column, editor) {
                            var sourceGlat =
				            {
				                localdata: dataGLAT,
				                datatype: \"array\"
				            };
				            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
                            editor.jqxDropDownList({source: dataAdapterGlat, displayMember:\"glAccountTypeId\", valueMember: \"glAccountTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLAT[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountClassId}', dataField: 'glAccountClassId', width: 150, columntype: 'dropdownlist', hidden: true,
					 	createeditor: function (row, column, editor) {
                            var sourceGlac =
				            {
				                localdata: dataGLAC,
				                datatype: \"array\"
				            };
				            var dataAdapterGlac = new $.jqx.dataAdapter(sourceGlac);
                            editor.jqxDropDownList({source: dataAdapterGlac, displayMember:\"glAccountClassId\", valueMember: \"glAccountClassId\", hidden: true,
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLAC[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
					 
					 { text: '${uiLabelMap.FormFieldTitle_glResourceTypeId}', dataField: 'glResourceTypeId', width: 180, columntype: 'dropdownlist', hidden: true, 
					 	createeditor: function (row, column, editor) {
                            var sourceGrt =
				            {
				                localdata: dataGRT,
				                datatype: \"array\"
				            };
				            var dataAdapterGrt = new $.jqx.dataAdapter(sourceGrt);
                            editor.jqxDropDownList({source: dataAdapterGrt, displayMember:\"glResourceTypeId\", valueMember: \"glResourceTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGRT[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
              		 { text: '${uiLabelMap.glTaxFormAccountId}', dataField: 'glTaxFormId', width: 250, columntype: 'dropdownlist',
					 	cellsrenderer: function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	for(i=0; i<dataTFAI.length;i++){
                        		if(dataTFAI[i].glTaxFormId==value){                        			
									return \"<div class='custom-cell-grid'>\" + dataTFAI[i].desc + \"</div>\"
                    			}
                        	}
                        	return \"<div class='custom-cell-grid'>\" + value + \"</div>\";
                        },
					 	createeditor: function (row, column, editor) {
                            var sourceTFAI =
				            {
				                localdata: dataTFAI,
				                datatype: \"array\"
				            };
				            var dataAdapterTFAI = new $.jqx.dataAdapter(sourceTFAI);
                            editor.jqxDropDownList({source: dataAdapterTFAI, displayMember:\"description\", valueMember: \"glTaxFormId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataTFAI[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},					 
					 { text: '${uiLabelMap.FormFieldTitle_parentGlAccountId}', dataField: 'codeParent', width: 150 },
              		 { text: '${uiLabelMap.accountCode}', dataField: 'accountCode'},
              		 { text: '${uiLabelMap.accountName}', dataField: 'accountName', width: 300 },
              		 { text: '${uiLabelMap.description}', dataField: 'description', width: 350 },
              		 { text: '${uiLabelMap.FormFieldTitle_postedBalance}', width : 200,dataField: 'postedBalance',filtertype : 'number', editable : false,cellsrenderer : function(row){
              		 		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
              		 		var postedBalance = data.postedBalance ? data.postedBalance	: null;
              		 		return '<span>' + formatcurrency(postedBalance,\"${aggregatedPartyAcctgPreference.baseCurrencyUomId?if_exists?default(\"\")}\")  + '</span>';
              		 } }"/>
              		 <#--{ text: '${uiLabelMap.FormFieldTitle_parentGlAccountId}', dataField: 'parentGlAccountId', width: 150, columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceGla =
				            {
				                localdata: dataGLA,
				                datatype: \"array\"
				            };
				            var dataAdapterGla = new $.jqx.dataAdapter(sourceGla);
                            editor.jqxDropDownList({source: dataAdapterGla, displayMember:\"glAccountId\", valueMember: \"glAccountId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLA[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},-->
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQGetListChartOfAccountOrigination" deleterow="true" defaultSortColumn="glAccountId" columnlist=columnlist 
			  dataField=dataField showtoolbar="true" clearfilteringbutton="true" editable="false" deletesuccessfunction="updateListGL" functionAfterAddRow="updateListGL"
			  addColumns="glAccountId;organizationPartyId"
			  removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteGlAccountOrganization" deleteColumn="glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
			  createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccountOrganization" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" 
			  />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" language="Javascript">
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as type>
			{
				'glAccountTypeId' : '${type.glAccountTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${type.glAccountTypeId?if_exists} ]" + ":" +  "${StringUtil.wrapString(type.get('description',locale)?default(''))}</span>"
			},		
		</#list>
	];
	var dataGLAC = new Array();
	dataGLAC = [
		<#list listGlAccountClass as class>
			{
				'glAccountClassId' : '${class.glAccountClassId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${class.glAccountClassId?if_exists} ]" + ":" +  "${StringUtil.wrapString(class.get('description',locale)?default(''))}</span>"
			},		
		</#list>
	];
	var dataGRT = new Array();
	dataGRT = [
		<#list listGlResourceType as type>
			{
				'glResourceTypeId' : '${type.glResourceTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${type.glResourceTypeId?if_exists} ]" + ":" +  "${StringUtil.wrapString(type.get('description',locale)?default(''))}</span>"
			},		
		</#list>
	];
	var dataGC = new Array();
	dataGC = [
		<#list listGlXbrlClass as type>
			{
				'glXbrlClassId' : '${type.glXbrlClassId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${type.glXbrlClassId?if_exists} ]" + ":" +  "${StringUtil.wrapString(type.get('description',locale)?default(''))}</span>"
			},		
		</#list>
	];
	
	var dataTFAI = new Array();
	dataTFAI = [
		<#list listTaxFormId as tax>
			{
				'glTaxFormId' : '${tax.enumId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${tax.enumId?if_exists} ]" + " - " +  "${StringUtil.wrapString(tax.get('description',locale)?default(''))}" + "[" + "${StringUtil.wrapString(tax.enumId?default(''))}" + "]",
				'desc' : "${StringUtil.wrapString(tax.get('description',locale)?default(''))}</span>"
			},		
		</#list>
	];
</script>	
<#include "../../popup/popupGlAccountOraganization.ftl"/>