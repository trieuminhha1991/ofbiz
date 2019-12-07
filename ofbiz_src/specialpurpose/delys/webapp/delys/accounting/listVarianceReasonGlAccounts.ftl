<script type="text/javascript">
	<#assign itlength = varianceReasons.size()/>
    <#if varianceReasons?size gt 0>
	    <#assign vaReason="var vaReason = ['" + StringUtil.wrapString(varianceReasons.get(0).varianceReasonId?if_exists) + "'"/>
		<#assign vaReasonValue="var vaReasonValue = ['" + StringUtil.wrapString(varianceReasons.get(0).description?if_exists) +"'"/>
		<#if varianceReasons?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaReason=vaReason + ",'" + StringUtil.wrapString(varianceReasons.get(i).varianceReasonId?if_exists) + "'"/>
				<#assign vaReasonValue=vaReasonValue + ",\"" + StringUtil.wrapString(varianceReasons.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign vaReason=vaReason + "];"/>
		<#assign vaReasonValue=vaReasonValue + "];"/>
	<#else>
    	<#assign vaReason="var vaReason = [];"/>
    	<#assign vaReasonValue="var vaReasonValue = [];"/>
    </#if>
	${vaReason}
	${vaReasonValue}	
	var dataVR = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["varianceReasonId"] = vaReason[i];
        row["description"] = vaReasonValue[i];
        dataVR[i] = row;
    }
    <#assign itlength = glAccountOrganizationAndClasses.size()/>
    <#if glAccountOrganizationAndClasses?size gt 0>
	    <#assign lgaoac="var lgaoac = ['" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(0).glAccountId?if_exists) + "'"/>
		<#assign lgaoacValue="var lgaoacValue = [\"" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(0).accountCode?if_exists) +"-" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(0).accountName?if_exists) + "\""/>
		<#if glAccountOrganizationAndClasses?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgaoac=lgaoac + ",'" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(i).glAccountId?if_exists) + "'"/>
				<#assign lgaoacValue=lgaoacValue + ",\"" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(i).accountCode?if_exists) +"-" + StringUtil.wrapString(glAccountOrganizationAndClasses.get(i).accountName?if_exists) + "\""/>
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
	var linkVRrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < vaReason.length; i++){
        	if(vaReason[i] == data.varianceReasonId){
        		return "<span>" + vaReasonValue[i] + "</span>";
        	}
        }
        return "";
    }
    var linkGOACrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < lgaoac.length; i++){
        	if(lgaoac[i] == data.glAccountId){
        		return "<span>" + lgaoacValue[i] + "</span>";
        	}
        }
        return "";
    }
</script>
<#assign dataField="[{ name: 'varianceReasonId', type: 'string' },
					 { name: 'glAccountId', type: 'string'}]
					 "/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_varianceReasonId}', datafield: 'varianceReasonId', cellsrenderer:linkVRrenderer},
					 { text: '${uiLabelMap.AccountingGlAccountId}', datafield: 'glAccountId', cellsrenderer:linkGOACrenderer, columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceGAOAC =
				            {
				                localdata: dataGAOAC,
				                datatype: \"array\"
				            };
				            var dataAdapterGAOAC = new $.jqx.dataAdapter(sourceGAOAC);
                            editor.jqxDropDownList({source: dataAdapterGAOAC, displayMember:\"glAccountId\", valueMember: \"glAccountId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGAOAC[index];
			                    return datarecord.description;
			                } 
                        });
					 }
					 }"/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="true"
		 url="jqxGeneralServicer?sname=JQGetListVarianceReasonGlAccounts&organizationPartyId=${parameters.organizationPartyId}" 
		 createUrl="jqxGeneralServicer?sname=createVarianceReasonGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}" addColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 removeUrl="jqxGeneralServicer?sname=removeVarianceReasonGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}" deleteColumn="varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 updateUrl="jqxGeneralServicer?sname=updateVarianceReasonGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}" editColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 />
		 
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_varianceReasonId}:</td>
	 			<td align="left"><div id="varianceReasonIdPop"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingGlAccountId}:</td>
	 			<td align="left"><div id="glAccountIdPop"></div></td>
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
    var addsourceGAOAC =
    {
        localdata: dataGAOAC,
        datatype: "array"
    };
    var addDataAdapterGAOAC = new $.jqx.dataAdapter(addsourceGAOAC);
    $('#glAccountIdPop').jqxDropDownList({ selectedIndex: 0,  source: addDataAdapterGAOAC, displayMember: "description", valueMember: "glAccountId"});
    
    var sourceVR =
    {
        localdata: dataVR,
        datatype: "array"
    };
    var dataAdapterVR = new $.jqx.dataAdapter(sourceVR);
    $('#varianceReasonIdPop').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterVR, displayMember: "description", valueMember: "varianceReasonId"});   
	    
    $("#alterpopupWindow").jqxWindow({
        width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7           
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		varianceReasonId:$('#varianceReasonIdPop').val(),
        		glAccountId:$('#glAccountIdPop').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>