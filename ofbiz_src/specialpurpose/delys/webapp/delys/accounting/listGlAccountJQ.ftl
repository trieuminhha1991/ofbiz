<#assign dataField="[{ name: 'glAccountId', type: 'string' },
               		{ name: 'glAccountTypeId', type: 'string' },
               		{ name: 'glAccountClassId', type: 'string' },
               		{ name: 'glResourceTypeId', type: 'string' },
               		{ name: 'glXbrlClassId', type: 'string' },
               		{ name: 'glTaxFormId', type: 'string' },
               		{ name: 'parentGlAccountId', type: 'string' },
                	{ name: 'accountCode', type: 'string' },
                	{ name: 'accountName', type: 'string' },
                	{ name: 'description', type: 'string' },
                	{ name: 'productId', type: 'string' },
                	{ name: 'externalId', type: 'string' },
                	{ name: 'postedBalance', type: 'string' }]"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 170, editable:false},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountTypeId}', dataField: 'glAccountTypeId', width: 200, columntype: 'dropdownlist',
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
					 { text: '${uiLabelMap.FormFieldTitle_glAccountClassId}', dataField: 'glAccountClassId', width: 150, columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceGlac =
				            {
				                localdata: dataGLAC,
				                datatype: \"array\"
				            };
				            var dataAdapterGlac = new $.jqx.dataAdapter(sourceGlac);
                            editor.jqxDropDownList({source: dataAdapterGlac, displayMember:\"glAccountClassId\", valueMember: \"glAccountClassId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLAC[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
					 
					 { text: '${uiLabelMap.FormFieldTitle_glResourceTypeId}', dataField: 'glResourceTypeId', width: 180, columntype: 'dropdownlist',
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
              		 { text: '${uiLabelMap.glTaxFormAccountId}', dataField: 'glTaxFormId', width: 150, columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceTFAI =
				            {
				                localdata: dataTFAI,
				                datatype: \"array\"
				            };
				            var dataAdapterTFAI = new $.jqx.dataAdapter(sourceTFAI);
                            editor.jqxDropDownList({source: dataAdapterTFAI, displayMember:\"glTaxFormId\", valueMember: \"glTaxFormId\",                           
                            renderer: function (index, label, value) {
			                    var datarecord = dataTFAI[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},					 
              		 { text: '${uiLabelMap.FormFieldTitle_parentGlAccountId}', dataField: 'parentGlAccountId', width: 150, columntype: 'dropdownlist',
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
					 }},
              		 { text: '${uiLabelMap.accountCode}', dataField: 'accountCode', width: 150 },
              		 { text: '${uiLabelMap.accountName}', dataField: 'accountName', width: 300 },
              		 { text: '${uiLabelMap.description}', dataField: 'description', width: 300 },
              		 { text: '${uiLabelMap.FormFieldTitle_postedBalance}', dataField: 'postedBalance', width: 300,editable:false }"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQGetListChartOfAccount" defaultSortColumn="glAccountId" columnlist=columnlist 
			  dataField=dataField showtoolbar="true" clearfilteringbutton="true" editable="true"
			  addColumns="externalId;productId;description;accountName;accountCode;glTaxFormId;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"
			  createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccount" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
			  updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccount" editColumns="externalId;productId;description;glTaxFormId;accountName;accountCode;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<form id="alterpopupWindowform">
	<div id="alterpopupWindow">
	    <div>${uiLabelMap.accCreateNew}</div>
	    <div style="overflow: hidden;">
	        <table>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.FormFieldTitle_glAccountId}:</td>
		 			<td align="left"><input type="text" id="glAccountId2"></input></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.FormFieldTitle_glAccountTypeId}:</td>
		 			<td align="left"><div id="glAccountTypeId2"></div></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.FormFieldTitle_glAccountClassId}:</td>
		 			<td align="left"><div id="glAccountClassId2"></div></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.FormFieldTitle_glResourceTypeId}:</td>
		 			<td align="left"><div id="glResourceTypeId2"></div></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.glTaxFormId}:</td>
		 			<td align="left"><div id="glTaxFormId2"></div></td>
	    	 	</tr>		    	 	
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.FormFieldTitle_parentGlAccountId}:</td>
		 			<td align="left"><div id="parentGlAccountId2"></div></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.accountCode}:</td>
		 			<td align="left"><input type="text" id="accountCode2"></input></td>
	    	 	</tr>	    	 	
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.accountName}:</td>
		 			<td align="left"><input type="text" id="accountName2"></input></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="left">${uiLabelMap.description}:</td>
		 			<td align="left"><input type="text" id="description2"></input></td>
	    	 	</tr>	    	 	
	            <tr>
	                <td align="right"></td>
	                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	            </tr>
	        </table>
	    </div>
	</div>
</form>
<script type="text/javascript" language="Javascript">
    <#assign itlength = listGlAccountType.size()/>
    <#if listGlAccountType?size gt 0>
	    <#assign lglat="var lglat = ['" + StringUtil.wrapString(listGlAccountType.get(0).glAccountTypeId?if_exists) + "'"/>
		<#assign lglatValue="var lglatValue = [\"" + StringUtil.wrapString(listGlAccountType.get(0).glAccountTypeId?if_exists) + ":" + StringUtil.wrapString(listGlAccountType.get(0).description?if_exists) +"\""/>
		<#if listGlAccountType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lglat=lglat + ",'" + StringUtil.wrapString(listGlAccountType.get(i).glAccountTypeId?if_exists) + "'"/>
				<#assign lglatValue=lglatValue + ",\"" + StringUtil.wrapString(listGlAccountType.get(i).glAccountTypeId?if_exists) + ":" + StringUtil.wrapString(listGlAccountType.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lglat=lglat + "];"/>
		<#assign lglatValue=lglatValue + "];"/>
	<#else>
    	<#assign lglat="var lglat = [];"/>
    	<#assign lglatValue="var lglatValue = [];"/>
    </#if>
	${lglat}
	${lglatValue}	
	var dataGLAT = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountTypeId"] = lglat[i];
        row["description"] = lglatValue[i];
        dataGLAT[i] = row;
    }
    <#assign itlength = listGlAccountClass.size()/>
    <#if listGlAccountClass?size gt 0>
	    <#assign lglac="var lglac = ['" + StringUtil.wrapString(listGlAccountClass.get(0).glAccountClassId?if_exists) + "'"/>
		<#assign lglacValue="var lglacValue = [\"" + StringUtil.wrapString(listGlAccountClass.get(0).glAccountClassId?if_exists) + ":" + StringUtil.wrapString(listGlAccountClass.get(0).description?if_exists) +"\""/>
		<#if listGlAccountType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lglac=lglac + ",'" + StringUtil.wrapString(listGlAccountClass.get(i).glAccountClassId?if_exists) + "'"/>
				<#assign lglacValue=lglacValue + ",\"" + StringUtil.wrapString(listGlAccountClass.get(i).glAccountClassId?if_exists) + ":" + StringUtil.wrapString(listGlAccountClass.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lglac=lglac + "];"/>
		<#assign lglacValue=lglacValue + "];"/>
	<#else>
    	<#assign lglac="var lglac = [];"/>
    	<#assign lglacValue="var lglacValue = [];"/>
    </#if>
	${lglac}
	${lglacValue}	
	var dataGLAC = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountClassId"] = lglac[i];
        row["description"] = lglacValue[i];
        dataGLAC[i] = row;
    }
    <#assign itlength = listGlResourceType.size()/>
    <#if listGlResourceType?size gt 0>
	    <#assign lgrt="var lgrt = ['" + StringUtil.wrapString(listGlResourceType.get(0).glResourceTypeId?if_exists) + "'"/>
		<#assign lgrtValue="var lgrtValue = [\"" + StringUtil.wrapString(listGlResourceType.get(0).glResourceTypeId?if_exists) + ":" + StringUtil.wrapString(listGlResourceType.get(0).description?if_exists) +"\""/>
		<#if listGlAccountType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgrt=lgrt + ",'" + StringUtil.wrapString(listGlResourceType.get(i).glResourceTypeId?if_exists) + "'"/>
				<#assign lgrtValue=lgrtValue + ",\"" + StringUtil.wrapString(listGlResourceType.get(i).glResourceTypeId?if_exists) + ":" + StringUtil.wrapString(listGlResourceType.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lgrt=lgrt + "];"/>
		<#assign lgrtValue=lgrtValue + "];"/>
	<#else>
    	<#assign lgrt="var lgrt = [];"/>
    	<#assign lgrtValue="var lgrtValue = [];"/>
    </#if>
	${lgrt}
	${lgrtValue}	
	var dataGRT = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glResourceTypeId"] = lgrt[i];
        row["description"] = lgrtValue[i];
        dataGRT[i] = row;
    }
    <#assign itlength = listGlXbrlClass.size()/>
    <#if listGlXbrlClass?size gt 0>
	    <#assign lgc="var lgc = ['" + StringUtil.wrapString(listGlXbrlClass.get(0).glXbrlClassId?if_exists) + "'"/>
		<#assign lgcValue="var lgcValue = [\"" + StringUtil.wrapString(listGlXbrlClass.get(0).glXbrlClassId?if_exists) + ":" + StringUtil.wrapString(listGlXbrlClass.get(0).description?if_exists) +"\""/>
		<#if listGlXbrlClass?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgc=lgc + ",'" + StringUtil.wrapString(listGlXbrlClass.get(i).glXbrlClassId?if_exists) + "'"/>
				<#assign lgcValue=lgcValue + ",\"" + StringUtil.wrapString(listGlXbrlClass.get(i).glXbrlClassId?if_exists) + ":" + StringUtil.wrapString(listGlXbrlClass.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lgc=lgc + "];"/>
		<#assign lgcValue=lgcValue + "];"/>
	<#else>
    	<#assign lgc="var lgc = [];"/>
    	<#assign lgcValue="var lgcValue = [];"/>
    </#if>
	${lgc}
	${lgcValue}	
	var dataGC = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glXbrlClassId"] = lgc[i];
        row["description"] = lgcValue[i];
        dataGC[i] = row;
    }
    <#assign itlength = listGlAccount.size()/>
    <#if listGlAccount?size gt 0>
	    <#assign lgla="var lgla = [\"" + StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + "\""/>
		<#assign lglaValue="var lglaValue = [\"" + StringUtil.wrapString(listGlAccount.get(0).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccount.get(0).accountName?if_exists) + "["+ StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + "]\""/>
		<#if listGlAccount?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgla=lgla + ",\"" + StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + "\""/>
				<#assign lglaValue=lglaValue + ",\"" + StringUtil.wrapString(listGlAccount.get(i).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccount.get(i).accountName?if_exists) + "["+ StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + "]\""/>
			</#list>
		</#if>
		<#assign lgla=lgla + "];"/>
		<#assign lglaValue=lglaValue + "];"/>
	<#else>
    	<#assign lgla="var lgla = [];"/>
    	<#assign lglaValue="var lglaValue = [];"/>
    </#if>
	${lgla}
	${lglaValue}	
	var dataGLA = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountId"] = lgla[i];
        row["description"] = lglaValue[i];
        dataGLA[i] = row;
    }
    
    <#assign itlength = listTaxFormId.size()/>
    <#if listTaxFormId?size gt 0>
	    <#assign lgTF="var lgTF = [\"" + StringUtil.wrapString(listTaxFormId.get(0).enumId?if_exists) + "\""/>
		<#assign lgTFValue="var lgTFValue = [\"" + StringUtil.wrapString(listTaxFormId.get(0).enumId?if_exists) + "-" + StringUtil.wrapString(listTaxFormId.get(0).description?if_exists) + "["+ StringUtil.wrapString(listTaxFormId.get(0).enumId?if_exists) + "]\""/>
		<#if listTaxFormId?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgTF=lgTF + ",\"" + StringUtil.wrapString(listTaxFormId.get(i).enumId?if_exists) + "\""/>
				<#assign lgTFValue=lgTFValue + ",\"" + StringUtil.wrapString(listTaxFormId.get(i).enumId?if_exists) + "-" + StringUtil.wrapString(listTaxFormId.get(i).description?if_exists) + "["+ StringUtil.wrapString(listTaxFormId.get(i).enumId?if_exists) + "]\""/>
			</#list>
		</#if>
		<#assign lgTF=lgTF + "];"/>
		<#assign lgTFValue=lgTFValue + "];"/>
	<#else>
    	<#assign lgTF="var lgTF = [];"/>
    	<#assign lgTFValue="var lgTFValue = [];"/>
    </#if>
	${lgTF}
	${lgTFValue}	
	var dataTFAI = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glTaxFormId"] = lgTF[i];
        row["description"] = lgTFValue[i];
        dataTFAI[i] = row;
    }    
</script>			      
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
    var sourceGlat2 =
    {
        localdata: dataGLAT,
        datatype: "array"
    };
    var dataAdapterGlat2 = new $.jqx.dataAdapter(sourceGlat2);
    
    var sourceGlac2 =
    {
        localdata: dataGLAC,
        datatype: "array"
    };
    var dataAdapterGlac2 = new $.jqx.dataAdapter(sourceGlac2);
    
    var sourceGrt2 =
    {
        localdata: dataGRT,
        datatype: "array"
    };
    var dataAdapterGrt2 = new $.jqx.dataAdapter(sourceGrt2);
    
    var sourceGc2 =
    {
        localdata: dataGC,
        datatype: "array"
    };
    var dataAdapterGc2 = new $.jqx.dataAdapter(sourceGc2);
    
    var sourceGla2 =
    {
        localdata: dataGLA,
        datatype: "array"
    };
    var dataAdapterGla2 = new $.jqx.dataAdapter(sourceGla2);
    
    var dataTFAI2 =
    {
        localdata: dataTFAI,
        datatype: "array"
    };
    var dataAdapterTFAI2 = new $.jqx.dataAdapter(dataTFAI2);    
    
    $('#glAccountId2').jqxInput({width:195, theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.glAccountId)}"});
//    $('#externalId2').jqxInput({theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.externalId)}"});
//    $('#productId2').jqxInput({theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.productId)}"});
    $('#accountName2').jqxInput({width:195,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.accountName)}"});
    $('#accountCode2').jqxInput({width:195,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.accountCode)}"});
    $('#description2').jqxInput({width:195,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.description)}"});
    $('#glAccountTypeId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGlat2, displayMember: "description", valueMember: "glAccountId"});
    $('#glAccountClassId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGlac2, displayMember: "description", valueMember: "glAccountClassId"});
    $('#glResourceTypeId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGrt2, displayMember: "description", valueMember: "glResourceTypeId"});
//    $('#glXbrlClassId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGc2, displayMember: "description", valueMember: "glXbrlClassId"});
    $('#parentGlAccountId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGla2, displayMember: "description", valueMember: "parentGlAccountId"});
    $('#glTaxFormId2').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterTFAI2, displayMember: "description", valueMember: "glTaxFormId"});
	$("#alterpopupWindow").jqxWindow({
        width: 550, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
    });

    $("#alterCancel").jqxButton({theme:theme});
    $("#alterSave").jqxButton({theme:theme});

    // update the edited row when the user clicks the 'Save' button.
    $('#alterpopupWindow').on('close', function (event) {
    	$('#alterpopupWindowform').jqxValidator('hide');
    });
    $("#alterSave").click(function () {
    	if($('#alterpopupWindowform').jqxValidator('validate')){
    		var row;
	        row = {
	        		glAccountId:$('#glAccountId2').val(),
	        		glAccountTypeId: $('#glAccountTypeId2').val(),
	        		glResourceTypeId: $('#glResourceTypeId2').val(),
	        		//glXbrlClassId: $('#glXbrlClassId2').val(),
	        		parentGlAccountId: $('#parentGlAccountId2').val(),
	        		glTaxFormId: $('#glTaxFormId2').val(),
	        		//accountCode: $('#accountCode2').val(),
	        		accountName: $('#accountName2').val(),
	        		description: $('#description2').val(),
	        		//productId: $('#productId2').val(),
	        		//externalId: $('#externalId2').val(),
	        		glAccountClassId:$('#glAccountClassId2').val()              
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	       $("#alterpopupWindow").jqxWindow('close');
    	}else{
    		return;
    	}
    });
    $('#alterpopupWindowform').jqxValidator({
        rules: [
                   { input: '#glAccountId2', message: 'glAccountId is required!', action: 'keyup, blur', rule: 'required' }
               ]
    });
</script>            	