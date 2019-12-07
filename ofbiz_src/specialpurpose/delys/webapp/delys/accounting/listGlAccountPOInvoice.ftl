
<script type="text/javascript">
	var cellsrendererRemove= function (row, columnfield, value, defaulthtml, columnproperties) {
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(tmpData.overrideGlAccountId != null){
    		var tmpId = 'tmpIc' + tmpData.customTimePeriodId;
    		var html = '<input type="button" onclick="removeFunction('+row+')" style="opacity: 0.99; position: absolute; top: 0%; left: 0%; padding: 0px; margin-top: 2px; margin-left: 2px; width: 96px; height: 21px;" value="${uiLabelMap.Remove}" hidefocus="true" id="' + tmpId + '" role="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-pressed jqx-fill-state-pressed-olbius" aria-disabled="false">';
    		return html;
    	}else{
    		return value;
    	}
    }
    function removeFunction(rowid){
    	var dataRecord = $('#jqxgrid').jqxGrid('getrowdata', rowid);
    	data = "primaryColumn=organizationPartyId;invoiceItemTypeId&primaryKey=${parameters.organizationPartyId}#;" + dataRecord.invoiceItemTypeId;
    	$.ajax({
            type: "POST",
            url: 'jqxGeneralServicer?jqaction=D&sname=removeInvoiceItemTypeGlAssignment',
            data:  data,
            success: function (data, status, xhr) {
                // update command is executed.
                if(data.responseMessage == "error"){
                	$('#jqxNotification').jqxNotification({ template: 'info'});
                	$("#jqxNotification").text(data.errorMessage);
                	$("#jqxNotification").jqxNotification("open");
                }else{
                	 $('#container').empty();
                	 $('#jqxgrid').jqxGrid('updatebounddata');
                	 $('#jqxNotification').jqxNotification({ template: 'info'});
                	$("#jqxNotification").text("Xoa thanh cong!");
                	$("#jqxNotification").jqxNotification("open");
                }
            },
            error: function () {
            }
        });   
    }
    <#assign itlength = listGlAccountOrganizationAndClass.size()/>
    <#if listGlAccountOrganizationAndClass?size gt 0>
	    <#assign lgaoac="var lgaoac = ['" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(0).glAccountId?if_exists) + "'"/>
		<#assign lgaoacValue="var lgaoacValue = ['" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(0).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(0).accountName?if_exists) + "[" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(0).glAccountId?if_exists) + "]'"/>
		<#if listGlAccountOrganizationAndClass?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgaoac=lgaoac + ",'" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(i).glAccountId?if_exists) + "'"/>
				<#assign lgaoacValue=lgaoacValue + ",'" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(i).accountCode?if_exists) + "-" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(i).accountName?if_exists) + "[" + StringUtil.wrapString(listGlAccountOrganizationAndClass.get(i).glAccountId?if_exists) + "]'"/>
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
        row["description"] = lgaoacValue[i];
        row["glAccountId"] = lgaoac[i];
        dataGAOAC[i] = row;
    };
	<#assign itlength = listInvoiceItemType.size()/>
    <#if listInvoiceItemType?size gt 0>
	    <#assign litt="var litt = ['" + StringUtil.wrapString(listInvoiceItemType.get(0).invoiceItemTypeId?if_exists) + "'"/>
		<#assign littValue="var littValue = ['" + StringUtil.wrapString(listInvoiceItemType.get(0).description?if_exists) +"'"/>
		<#if listInvoiceItemType?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign litt=litt + ",'" + StringUtil.wrapString(listInvoiceItemType.get(i).invoiceItemTypeId?if_exists) + "'"/>
				<#assign littValue=littValue + ",'" + StringUtil.wrapString(listInvoiceItemType.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign litt=litt + "];"/>
		<#assign littValue=littValue + "];"/>
	<#else>
    	<#assign litt="var litt = [];"/>
    	<#assign littValue="var littValue = [];"/>
    </#if>
	${litt}
	${littValue}	
	var dataITT = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["description"] = littValue[i];
        row["invoiceItemTypeId"] = litt[i];
        dataITT[i] = row;
    };
</script>
<#assign dataField="[{ name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'overrideGlAccountId', type: 'string' },
					 { name: 'activeGlDescription', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.description}', datafield: 'description', width:250},
					 { text: '${uiLabelMap.FormFieldTitle_defaultGlAccountId}', datafield: 'defaultGlAccountId', width:200},
					 { text: '${uiLabelMap.FormFieldTitle_overrideGlAccountId}', datafield: 'overrideGlAccountId', width:250},
					 { text: '${uiLabelMap.FormFieldTitle_activeGlDescription}', datafield: 'activeGlDescription', width:250},
					 { text: '${uiLabelMap.CommonRemove}', cellsrenderer: cellsrendererRemove}
					"/>	
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQGetListGLAccountItemTypePO" dataField=dataField columnlist=columnlist showtoolbar="true" editable="false"
		 height="640" filterable="false" sortable="false" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" addrefresh="true"
		 id="jqxgrid" addColumns="invoiceItemTypeId;glAccountId;organizationPartyId" createUrl="jqxGeneralServicer?jqaction=C&sname=addInvoiceItemTypeGlAssignment"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_invoiceItemTypeId}:</td>
	 			<td align="left"><div id="invoiceItemTypeId"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_overrideGlAccountId}:</td>
	 			<td align="left"><div id="glAccountId"></div></td>
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
    var sourceITT =
    {
        localdata: dataITT,
        datatype: "array"
    };
    var dataAdapterITT = new $.jqx.dataAdapter(sourceITT);
    
    $('#glAccountId').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterGAOAC, displayMember: "description", valueMember: "glAccountId"});
    $('#invoiceItemTypeId').jqxDropDownList({theme:theme, selectedIndex: 0,  source: dataAdapterITT, displayMember: "description", valueMember: "invoiceItemTypeId"});
	$("#alterpopupWindow").jqxWindow({
        width: 500, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
    });

    $("#alterCancel").jqxButton({theme:theme});
    $("#alterSave").jqxButton({theme:theme});

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		glAccountId:$('#glAccountId').val(),
        		invoiceItemTypeId: $('#invoiceItemTypeId').val(),
        		organizationPartyId:'${parameters.organizationPartyId}'              
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
       $("#alterpopupWindow").jqxWindow('close');
       //$('#jqxgrid').jqxGrid('updatebounddata');
    });
</script>
