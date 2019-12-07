<script type="text/javascript" language="Javascript">
    <#assign itlength = listUom.size()/>
    <#if listUom?size gt 0>
	    <#assign lu="var lu = [\"" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "\""/>
		<#assign luValue="var luValue = [\"" + StringUtil.wrapString(listUom.get(0).description?if_exists) + "-" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) +"\""/>
		<#if listUom?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lu=lu + ",\"" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "\""/>
				<#assign luValue=luValue + ",\"" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "-" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) +"\""/>
			</#list>
		</#if>
		<#assign lu=lu + "];"/>
		<#assign luValue=luValue + "];"/>
	<#else>
    	<#assign lu="var lu = [];"/>
    	<#assign luValue="var luValue = [];"/>
    </#if>
    ${lu}
    ${luValue}
    var luData = new Array();
	var row = {};
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["uomId"] = lu[i];
        row["description"] = luValue[i];
        luData[i] = row;
    }
     <#assign itlength = listEnum.size()/>
    <#if listEnum?size gt 0>
	    <#assign le="var le = [\"" + StringUtil.wrapString(listEnum.get(0).enumId?if_exists) + "\""/>
		<#assign leValue="var leValue = [\"" + StringUtil.wrapString(listEnum.get(0).description?if_exists) + "\""/>
		<#if listEnum?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign le=le + ",\"" + StringUtil.wrapString(listEnum.get(i).enumId?if_exists) + "\""/>
				<#assign leValue=leValue + ",\"" + StringUtil.wrapString(listEnum.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign le=le + "];"/>
		<#assign leValue=leValue + "];"/>
	<#else>
    	<#assign le="var le = [];"/>
    	<#assign leValue="var leValue = [];"/>
    </#if>
    ${le}
    ${leValue}
    var leData = new Array();
	var row = {};
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["enumId"] = le[i];
        row["description"] = leValue[i];
        leData[i] = row;
    }
</script>

<#assign dataField="[{ name: 'uomId', type: 'string' },
					 { name: 'uomIdTo', type: 'string' },
					 { name: 'purposeEnumId', type: 'string' },
					 { name: 'conversionFactor', type: 'number' },
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp' }
					]"/>
<#assign columnlist="{ text: '${uiLabelMap.accFromUomId}', datafield: 'uomId', width: 200, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < lu.length;i++){
                        		if(lu[i] == value){
                        			return \"<span>\" + luValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }},
					 { text: '${uiLabelMap.accToUomId}', datafield: 'uomIdTo', width: 200, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < lu.length;i++){
                        		if(lu[i] == value){
                        			return \"<span>\" + luValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }},
                     { text: '${uiLabelMap.accConversionFactor}', datafield: 'conversionFactor', width: 140 },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.accThruDate}', datafield: 'thruDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.CommonPurpose}', datafield: 'purposeEnumId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < le.length;i++){
                        		if(le[i] == value){
                        			return \"<span>\" + leValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }
                     }
					 "/>
<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQListConversions" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=updateFXConversion&jqaction=C" addColumns="uomId;uomIdTo;purposeEnumId;conversionFactor;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 alternativeAddPopup="alterpopupWindow"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}:</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accFromUomId}:</td>
	 			<td align="left"><div id="uomId"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accToUomId}:</td>
	 			<td align="left"><div id="uomIdTo"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.CommonPurpose}:</td>
	 			<td align="left"><div id="purposeEnumId"/></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accConversionFactor}:</td>
	 			<td align="left"><input id="conversionFactor"/></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left"><div id="fromDate"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accThruDate}:</td>
	 			<td align="left"><div id="thruDate"></div></td>
    	 	</tr>
    	 	<tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    var sourceLe =
    {
        localdata: leData,
        datatype: "array"
    };
    var sourceLu =
    {
        localdata: luData,
        datatype: "array"
    };
    $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
    var dataAdapterLu = new $.jqx.dataAdapter(sourceLu);
    var dataAdapterLe = new $.jqx.dataAdapter(sourceLe);
    $('#uomId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterLu, displayMember: "description", valueMember: "uomId"});
    $('#uomIdTo').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterLu, displayMember: "description", valueMember: "uomId"});
    $('#purposeEnumId').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterLe, displayMember: "description", valueMember: "enumId"});
	$("#fromDate").jqxDateTimeInput({width: '200px', height: '25px'});
	$("#thruDate").jqxDateTimeInput({width: '200px', height: '25px'});
	$("#conversionFactor").jqxInput({width: '195px'});
	$("#alterpopupWindow").jqxWindow({
        width: 580, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme          
    });

    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		fromDate: $('#fromDate').jqxDateTimeInput('getDate'),
        		uomId:$('#uomId').val(),
        		uomIdTo:$('#uomIdTo').val(),
        		purposeEnumId:$('#purposeEnumId').val(),
        		conversionFactor:$('#conversionFactor').val(),
        		thruDate:  $('#thruDate').jqxDateTimeInput('getDate')              
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>