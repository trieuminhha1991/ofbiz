<#assign dataField = "[
			{name : 'insuranceTypeId', type : 'string'},
			{name : 'description', type : 'string'},
			{name : 'employerRate', type : 'number', other : 'Double'},
			{name : 'employeeRate', type : 'number', other : 'Double'},
			{name : 'isCompulsory', type : 'string'},
	]"/>
			
<script type="text/javascript">
	<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTypeId)}', width : '20%', datafield : 'insuranceTypeId', editable : false},
				{text : '${StringUtil.wrapString(uiLabelMap.InsuranceType)}',width : '20%', datafield : 'description', editable : false},
				{text : '${StringUtil.wrapString(uiLabelMap.InsuranceTypeEmployerRate)}',width : '20%', datafield : 'employerRate', filterType : 'number',
					columntype : 'numberinput',
					cellsrenderer : function(row, column, value){
						return '<span>' + value + '(' + '%' + ')' + '</span>';
					},
					createeditor : function(row, column, editor){
						editor.jqxNumberInput({ width: 228, height: 25, spinButtons: true, decimalDigits: 2});
					},
					initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
						if(!cellvalue || cellvalue == ''){
							editor.val(0);
						}
					},
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						if (newvalue < 0){
				            return false;
				        }
				    }
				},
				{text : '${StringUtil.wrapString(uiLabelMap.InsuranceTypeEmployeeRate)}',width : '20%', datafield : 'employeeRate', filterType : 'number',
					columntype : 'numberinput',
					cellsrenderer : function(row, column, value){
						return '<span>' + value + '(' + '%' + ')' + '</span>';
					},
					createeditor : function(row, column, editor){
						editor.jqxNumberInput({ width: 228, height: 25, spinButtons: true, decimalDigits: 4});
					},
					initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
						if(!cellvalue || cellvalue == ''){
							editor.val(0);
						}
					},
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						if (newvalue < 0){
				            return false;
				        }
				    }
				},
				{text : '${StringUtil.wrapString(uiLabelMap.InsuranceTypeIsCompulsory)}',width : '20%', datafield : 'isCompulsory', columntype : 'checkbox',filterType : 'checkedlist',
						createfilterwidget : function(column, columnElement, widget){
							var datalocal = [
					                 {
					                 	value : 'Y',
					                 	description : '${StringUtil.wrapString(uiLabelMap.CommonYes)}',
					                 },
					                 {
					         	    	value : 'N',
					         	    	description : '${StringUtil.wrapString(uiLabelMap.CommonNo)}',
					                 }
					         ];
							var source = {
									localdata : datalocal,
									datatype : 'array'
							};
							var fitlerBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							var dataFilter = fitlerBoxAdapter.records;
							//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: dataFilter, valueMember: 'value', displayMember : 'description'});
							if(dataFilter.length <=8){
								widget.jqxDropDownList({autoDropDownHeight : true});
							}else{
								widget.jqxDropDownList({autoDropDownHeight : false})
							}
						}
				},
		"/>
</script>
<@jqGrid url="jqxGeneralServicer?sname=JQGetListInsuranceType" dataField=dataField columnlist=columnlist
		id="jqxgrid" editable="true" width="100%" filterable="true" clearfilteringbutton="true" showlist='false'
		showtoolbar="true" deleterow="false"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInsuranceType" 
		editColumns="insuranceTypeId;employerRate(java.lang.Double);employeeRate(java.lang.Double);isCompulsory"	
		/>
