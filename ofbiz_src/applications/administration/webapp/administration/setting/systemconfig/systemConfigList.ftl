<script type="text/javascript">
	var dataBooleanChoose = [
		{id : "false", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "true", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
</script>

<#assign dataField = "[
			{ name: 'systemConfigId', type: 'string' },
			{ name: 'systemValue', type: 'string' },
			{ name: 'description', type: 'string' },
			{ name: 'typeData', type: 'string' },
			{ name: 'manualSetting', type: 'string' },
		]"/>
<#assign columnlist = "
			{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', minwidth: 160, editable: false },
			{ text: '${StringUtil.wrapString(uiLabelMap.BSEditable)}', dataField: 'manualSetting', width: 160, editable: false },
			{ text: '${StringUtil.wrapString(uiLabelMap.BSValue)}', dataField: 'systemValue', width: 220, columntype: 'dropdownlist', 
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridSystemConfig').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent\">';
			 		if (data && data.typeData == 'boolean') {
			 			for (var i = 0 ; i < dataBooleanChoose.length; i++){
							if (value == dataBooleanChoose[i].id){
								returnVal += dataBooleanChoose[i].description + '</div>';
		   						return returnVal;
							}
						}
			 		}
		   			returnVal += value + '</div>';
	   				return returnVal;
				},
			 	initeditor: function (row, cellvalue, editor) {
					var data = $('#jqxgridSystemConfig').jqxGrid('getrowdata', row);
					if (data && data.typeData == 'boolean') {
						var itemSelected = data['systemValue'];
						var sourceDataPacking = {
			                localdata: dataBooleanChoose,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'id'});
			            editor.jqxDropDownList('selectItem', itemSelected);
					}
		      	}
			},
		"/>
<#assign permitUpdate = false>
<#if security.hasEntityPermission("SYSTEMCONFIG", "_UPDATE", session)><#assign permitUpdate = true></#if>
<@jqGrid id="jqxgridSystemConfig" clearfilteringbutton="true" columnlist=columnlist dataField=dataField alternativeAddPopup="" jqGridMinimumLibEnable="true" 
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true" 
		url="jqxGeneralServicer?sname=JQGetListSystemConfig" addrow="false" deleterow="false" selectionmode="multiplecellsadvanced" editmode="dblclick"  
		editable="${permitUpdate?string}" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateSystemConfig" editColumns="systemConfigId;systemValue"
		mouseRightMenu="false" contextMenuId="" enabletooltips="true"/>
