<script type="text/javascript" language="Javascript">
    <#assign itlength = listGlAccount.size()/>
    <#if listGlAccount?size gt 0>
	    <#assign lgla="var lgla = [\"" + StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + "\""/>
		<#assign lglaValue="var lglaValue = [\"" + StringUtil.wrapString(listGlAccount.get(0).glAccountId?if_exists) + ":" + StringUtil.wrapString(listGlAccount.get(0).description?if_exists) +"\""/>
		<#if listGlAccount?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lgla=lgla + ",\"" + StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + "\""/>
				<#assign lglaValue=lglaValue + ",\"" + StringUtil.wrapString(listGlAccount.get(i).glAccountId?if_exists) + ":" + StringUtil.wrapString(listGlAccount.get(i).accountName?if_exists) + "\""/>
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
</script>
<#assign dataField="[{ name: 'paymentMethodTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.CommonPaymentMethodType}', datafield: 'paymentMethodTypeId',editable:false, selectable:false},
					 { text: '${uiLabelMap.description}', datafield: 'description',editable:false},
					 { text: '${uiLabelMap.accDefaultGlAccountId}', columntype: 'dropdownlist', datafield: 'defaultGlAccountId',
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
					 }}
					"/>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListPaymentMethodTypes" dataField=dataField columnlist=columnlist showtoolbar="true" editable="true" clearfilteringbutton="true" editmode="selectedcell"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePaymentMethodType" editColumns="paymentMethodTypeId;description;defaultGlAccountId" height="640" selectionmode="singlecell"
		 />