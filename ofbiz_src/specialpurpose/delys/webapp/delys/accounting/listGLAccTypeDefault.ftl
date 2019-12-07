<script type="text/javascript">
	<#assign itlength = glAccountTypeDefaults.size()/>
    <#if glAccountTypeDefaults?size gt 0>
	    <#assign lgatp="var lgatp = ['" + StringUtil.wrapString(glAccountTypeDefaults.get(0).glAccountTypeId?if_exists) + "'"/>
		<#assign lgatpValue="var lgatpValue = ['" + StringUtil.wrapString(glAccountTypeDefaults.get(0).description?if_exists) +"'"/>
		<#if glAccountTypeDefaults?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgatp=lgatp + ",'" + StringUtil.wrapString(glAccountTypeDefaults.get(i).glAccountTypeId?if_exists) + "'"/>
				<#assign lgatpValue=lgatpValue + ",\"" + StringUtil.wrapString(glAccountTypeDefaults.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lgatp=lgatp + "];"/>
		<#assign lgatpValue=lgatpValue + "];"/>
	<#else>
    	<#assign lgatp="var lgatp = [];"/>
    	<#assign lgatpValue="var lgatpValue = [];"/>
    </#if>
	${lgatp}
	${lgatpValue}	
	var dataGATP = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountTypeId"] = lgatp[i];
        row["description"] = lgatpValue[i];
        dataGATP[i] = row;
    }
    <#assign itlength = glAccountOrganizationAndClass.size()/>
    <#if glAccountOrganizationAndClass?size gt 0>
	    <#assign lgaoac="var lgaoac = ['" + StringUtil.wrapString(glAccountOrganizationAndClass.get(0).glAccountId?if_exists) + "'"/>
		<#assign lgaoacValue="var lgaoacValue = [\"" + StringUtil.wrapString(glAccountOrganizationAndClass.get(0).accountCode?if_exists) +"-" + StringUtil.wrapString(glAccountOrganizationAndClass.get(0).accountName?if_exists) + "\""/>
		<#if glAccountTypeDefaults?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgaoac=lgaoac + ",'" + StringUtil.wrapString(glAccountOrganizationAndClass.get(i).glAccountId?if_exists) + "'"/>
				<#assign lgaoacValue=lgaoacValue + ",\"" + StringUtil.wrapString(glAccountOrganizationAndClass.get(i).accountCode?if_exists) +"-" + StringUtil.wrapString(glAccountOrganizationAndClass.get(i).accountName?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lgaoac=lgaoac + "];"/>
		<#assign lgaoacValue=lgaoacValue + "];"/>
	<#else>
    	<#assign lgaoac="var lgaoac = [];"/>
    	<#assign lgaoacValue="var lgaoacValue = [];"/>
    </#if>
	${lgaoac}
	${lgaoacValue}	
	var dataGAOAC = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["glAccountId"] = lgaoac[i];
        row["description"] = lgaoacValue[i];
        dataGAOAC[i] = row;
    }
</script>
<script type="text/javascript">
	var linkGATrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < lgatp.length; i++){
        	if(lgatp[i] == data.glAccountTypeId){
        		return "<span>" + lgatpValue[i] + "</span>";
        	}
        }
        return "";
    }
    var linkGOACrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < lgaoac.length; i++){
        	if(lgaoac[i] == data.glAccountId){
        		return "<span>" + lgaoacValue[i] + "&nbsp;<a href='/delys/control/GlAccountNavigate?glAccountId=" + lgaoac[i] + "' target='_blank' >[" + lgaoac[i] + "]</a></span>";
        	}
        }
        return "";
    }
</script>
<#assign dataField="[{ name: 'glAccountId', type: 'string' },
					 { name: 'glAccountTypeId', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountType}', datafield: 'glAccountTypeId', cellsrenderer:linkGATrenderer},
					 { text: '${uiLabelMap.AccountingGlAccountId}', datafield: 'glAccountId', cellsrenderer:linkGOACrenderer}
					"/>					
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGLAccountTypeDedault" columnlist=columnlist dataField=dataField
		 filtersimplemode="false" clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createGlAccountTypeDefault"
		 removeUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=D&sname=removeGlAccountTypeDefault"
		 deleteColumn="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]"
		 addColumns="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]" addType="popup" id="jqxgrid"/>

<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_glAccountType}:</td>
	 			<td align="left"><div id="glAccountId"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingGlAccountId}:</td>
	 			<td align="left"><div id="glAccountTypeId"></div></td>
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
    var sourceGAOAC =
    {
        localdata: dataGAOAC,
        datatype: "array"
    };
    var dataAdapterGAOAC = new $.jqx.dataAdapter(sourceGAOAC);
    $('#glAccountId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterGAOAC, displayMember: "description", valueMember: "glAccountId"});
    
    var sourceGATP =
    {
        localdata: dataGATP,
        datatype: "array"
    };
    var dataAdapterGATP = new $.jqx.dataAdapter(sourceGATP);
    $('#glAccountTypeId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterGATP, displayMember: "description", valueMember: "glAccountTypeId"});
    
    $("#alterpopupWindow").jqxWindow({
        width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
    });
    $("#alterCancel").jqxButton({theme:theme});
    $("#alterSave").jqxButton({theme:theme});

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		glAccountId:$('#glAccountId').val(),
        		glAccountTypeId:$('#glAccountTypeId').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>