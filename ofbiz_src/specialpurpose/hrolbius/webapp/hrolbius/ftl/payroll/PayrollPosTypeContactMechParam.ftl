<#assign dataFields = "[{name: 'payrollParamPositionTypeId', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'code', type: 'string'},	
						{name: 'roleTypeGroupId', type: 'string'},
						{name: 'excludeGeo', type: 'string'},
						{name: 'includeGeo', type: 'string'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'rateAmount', type: 'number'},
						{name: 'uomId', type: 'string'},
						]" />
<@jqGridMinimumLib/>					
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<script type="text/javascript">
	var emplPosTypeArr = new Array();	
	<#list emplPosType as posType>
		var row = {};
		row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
		row["description"] = "${StringUtil.wrapString(posType.description)}";
		emplPosTypeArr[${posType_index}] = row;
	</#list>
	var dataEmplPosTypeAdapter = new $.jqx.dataAdapter(emplPosTypeArr, {autoBind: true});
	var dataEmplPosTypeList = dataEmplPosTypeAdapter.records;
	
	var periodTypeArr = [
		<#if periodTypeList?has_content>
			<#list periodTypeList as periodType>
				{
					periodTypeId: "${periodType.periodTypeId}",
					description: "${StringUtil.wrapString(periodType.description?if_exists)}"
				},
			</#list>
		</#if>	                     
	];
	
	var filterBoxAdapter = new $.jqx.dataAdapter(periodTypeArr, {autoBind: true});
	var dataSourePeriodTypeList = filterBoxAdapter.records;
	
	var roleTypeGroupArr = new Array();
	<#if roleTypeGroupList?has_content>
		<#list roleTypeGroupList as roleTypeGroup>
			var row = {};
			row["roleTypeGroupId"] = "${roleTypeGroup.roleTypeGroupId}";
			row["description"] = "${roleTypeGroup.description}";
			roleTypeGroupArr[${roleTypeGroup_index}] = row;
		</#list>
	</#if>
	/* roleTypeGroupArr.push({"roleTypeGroupId": "selectAll", "description": "${StringUtil.wrapString(uiLabelMap.filterselectallstring)}"}); */
	var filterRoleTypeGroupAdapter = new $.jqx.dataAdapter(roleTypeGroupArr, {autoBind: true});
	var dataSoureRoleTypeGroupList = filterRoleTypeGroupAdapter.records;
	
	var parametersArr = [
		<#if payrollParametersList?has_content>
			<#list payrollParametersList as param>
			{
				code: "${param.code}",
				name: "${param.name}",
				type: "${param.type?if_exists}",
				periodTypeId: "${param.periodTypeId?if_exists}"
			},
			</#list>
		</#if>
	];
	
	
	var filterParameterAdapter = new $.jqx.dataAdapter(parametersArr, {autoBind: true});
	var dataSoureParameterList = filterParameterAdapter.records;
	
	<#--/* var geoRegionArr = new Array();
	<#if geoRegionList?has_content>
		<#list geoRegionList as geo>
			var row = {};
			row["geoId"] = "${geo.geoId}";
			row["geoName"] = "${geo.geoName}";
			geoRegionArr[${geo_index}] = row;
		</#list>
	</#if>
	var filterGeoRegionAdapter = new $.jqx.dataAdapter(geoRegionArr, {autoBind: true});
	var dataSoureGeoRegionList = filterGeoRegionAdapter.records;
	
	var geoAreaArr = new Array();
	<#if geoAreaList?has_content>
		<#list geoAreaList as geo>
			var row = {};
			row["geoId"] = "${geo.geoId}";
			row["geoName"] = "${geo.geoName}";
			geoAreaArr[${geo_index}] = row;
		</#list>
	</#if>
	var filterGeoAreaAdapter = new $.jqx.dataAdapter(geoAreaArr, {autoBind: true});
	var dataSoureGeoAreaList = filterGeoAreaAdapter.records; */-->
	
	var uomArray = new Array();
	 <#list uomList as uom>
	 	var row = {};
	 	row["uomId"] = "${uom.uomId}";
	 	row["description"] = "${uom.description?if_exists}";
	 	uomArray[${uom_index}] = row;
	 </#list>
	 var filterUomAdapter = new $.jqx.dataAdapter(uomArray, {autoBind: true});
	 var dataSoureUomList = filterUomAdapter.records;
	
	 <#assign columnlist = "{datafield: 'payrollParamPositionTypeId', hidden: true, editable: false},
	 {text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', filterable: true, editable: false, cellsalign: 'left', 
		 	width: 160,
			filtertype: 'checkedlist',
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < emplPosTypeArr.length; i++){
					if(emplPosTypeArr[i].emplPositionTypeId == value){
						return '<div style=\"\">' + emplPosTypeArr[i].description + '</div>';		
					}
				}
			},
			createfilterwidget: function(column, columnElement, widget){
				var sourceEmplPosType = {
			        localdata: emplPosTypeArr,
			        datatype: 'array'
			    };		
				var filterBoxAdapter = new $.jqx.dataAdapter(sourceEmplPosType, {autoBind: true});
			    var dataEmplPosTypeList = filterBoxAdapter.records;
			    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
			    dataEmplPosTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataEmplPosTypeList,  displayMember: 'description', valueMember : 'emplPositionTypeId', 
			    	height: '25px', autoDropDownHeight: false, searchMode: 'containsignorecase', incrementalSearch: true, filterable:true,
					renderer: function (index, label, value) {
						for(i=0; i < emplPosTypeArr.length; i++){
							if(emplPosTypeArr[i].emplPositionTypeId == value){
								return emplPosTypeArr[i].description;
							}
						}
					    return value;
					}
				});									
			}
		},
		{text: '${uiLabelMap.parameters}', datafield: 'code', filterable: false, editable: false, cellsalign: 'left', width: 110,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < parametersArr.length; i++){
					if(parametersArr[i].code == value){
						return '<div style=\"\">' + parametersArr[i].name + '</div>';		
					}
				}
				return '<div style=\"\">' + value + '</div>';
			}
		},
		{text: '${uiLabelMap.HRCommonChannel}', datafield: 'roleTypeGroupId', filterable: false, editable: false, cellsalign: 'left', width: 110,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < roleTypeGroupArr.length; i++){
					if(roleTypeGroupArr[i].roleTypeGroupId == value){
						return '<div style=\"\">' + roleTypeGroupArr[i].description + '</div>';		
					}
				}
				return '<div style=\"\">' + value + '</div>';
			}	
		},
		{text: '${uiLabelMap.HRAreaApply}', datafield: 'includeGeo', filterable: false, editable: false, cellsalign: 'left', width: 180},
		{text: '${uiLabelMap.HRAreaExclude}', datafield: 'excludeGeo', filterable: false, editable: false, cellsalign: 'left', width: 90},
		{text: '${uiLabelMap.CommonPeriodType}', datafield:'periodTypeId' , filterable: false, editable: false, cellsalign: 'left', width: 100,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < periodTypeArr.length; i++){
					if(periodTypeArr[i].periodTypeId == value){
						return '<div style=\"\">' + periodTypeArr[i].description + '</div>';		
					}
				}
			}
		},
		{text: '${uiLabelMap.PayrollFromDate}', datafield:'fromDate', filterable: false,editable: false, cellsalign: 'left', width: 110, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
		{text: '${uiLabelMap.PayrollThruDate}', datafield: 'thruDate', filterable: false, editable: true, cellsalign: 'left', width: 110, cellsformat: 'dd/MM/yyyy ', columntype: 'template',
			cellsrenderer: function (row, column, value) {
				if(!value){
					return '<div style=\"margin-left: 4px\">${uiLabelMap.HRCommonCurrent}</div>';
				}
			},
			createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		        editor.jqxDateTimeInput({width: 158, height: 28});
		        editor.val(cellvalue);
		    }
		}, 
		{text: '${uiLabelMap.DAAmount}', datafield: 'rateAmount', filterable: false,editable: true, cellsalign: 'right',
			cellsrenderer: function (row, column, value) {
		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		 		 if (data && data.rateAmount){
		 		 	return \"<div style='margin-right: 2px; margin-top: 9.5px; text-align: right;'>\" + formatcurrency(data.rateAmount, data.uomId) + \"</div>\";
		 		 }
	 		 }								
		},
		{text: '', datafield: 'uomId', hidden: true}"/>
	
	var theme = "olbius";
	var elementCheckInJqxTreeGeo = [];
	
	$(document).ready(function () {
		initJqxDropDownList(); 
		initJqxDateTimeInput();
		initJqxNumberInput();
		initJqxButtonTree();
		initJqxValidator();
		btnEvent();
		$("#popupWindowAddNew, #popupWindowEdit").jqxWindow({showCollapseButton: false, maxHeight: 460, autoOpen: false,
				maxWidth: 530, minHeight: 460, minWidth: 530, height: 460, width: 550, isModal: true, theme:theme});
		
		jQuery("#popupWindowAddNew").on('open', function (event){
			$("#amountValueNew").val(0);
			$("#thruDateNew").val(null);
			$("#alterSave").removeAttr('disabled');
			$("#fromDateNew").val(new Date(${monthStart.getTime()}));
		});
		
		jQuery("#popupWindowAddNew").on('close', function (event){
			GridUtils.clearForm($(this));
			$("#jqxTreeExcludeGeo, #jqxTreeIncludeGeo").jqxTree('uncheckAll');	
			$("#periodTypeNew").html("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		});
		
		$("#jqxgrid").on('rowDoubleClick', function (event){
			$("#popupWindowEdit").jqxWindow('open');
		});
		
		$("#popupWindowEdit").on('open', function(event){
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			if(rowIndex > -1){
				var data = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
				fillDataInWindowEdit(data);				
			}
		});
		
		$("#popupWindowEdit").on('close', function(event){
			$("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('uncheckAll');
			$("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('collapseAll');
		});
	});
	
	function initJqxValidator(){
		$("#createPayrollParamPositionTypeForm").jqxValidator({
	 		rules:[
				{input: '#emplPositionTypeIdAddNew', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(value < 0){
							return false
						}
						return true;
					}	
				},
				{input: '#codeAddNew', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false;
						}
						return true;
					}	
				},
				{input: '#roleTypeGroupId', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false;
						}
						return true;
					}	
				},
				{input: "#jqxBtnIncludeGeo", message: "${uiLabelMap.NotChooseOrChooseInvalid}", action: 'blur',
					rule: function(input, commit){
						var parentIdSelected = new Array();
						var idSelected = new Array();
						var items = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
						if(items.length == 0){
							return false;
						}
						for(var i = 0; i < items.length; i++){
							idSelected.push(substringBySeparator(items[i].id, "_"));
							if(items[i].parentId && items[i].parentId != "-1"){
								if(parentIdSelected.indexOf(items[i].parentId)  == -1){
									parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
								}
							}
						}
						var check = checkSelectedValid(idSelected, parentIdSelected);
						return check;
					}				
				},
				{input: '#jqxBtnExcludeGeo', message: "${uiLabelMap.ChooseInvalid}", action: 'blur',
					rule: function(input, commit){
						var parentIdSelected = new Array();
						var idSelected = new Array();
						var items = $("#jqxTreeExcludeGeo").jqxTree('getCheckedItems');
						if(items.length == 0){
							return true;
						}
						var itemsInclude = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
						var itemsIncludeArr = new Array();
						for(var i = 0; i < itemsInclude.length; i++){
							itemsIncludeArr.push(substringBySeparator(itemsInclude[i].id, "_"));
						}
						
						for(var i = 0; i < items.length; i++){
							idSelected.push(substringBySeparator(items[i].id, "_"));
							if(items[i].parentId && items[i].parentId != "-1"){
								if(parentIdSelected.indexOf(items[i].parentId)  == -1){
									parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
								}
							}else{
								parentIdSelected.push("-1");
							}
						}
						//if select of excludeGeo not child of includeGeo return false
						for(var i = 0; i < parentIdSelected.length; i++){
							if(itemsIncludeArr.indexOf(parentIdSelected[i]) == -1){
								return false;
							}
						}
						var check = checkSelectedValid(idSelected, parentIdSelected);
						return check;
					}
				}
	        ]
	 	});
		
		$("#editPayrollParamPositionTypeForm").jqxValidator({
	 		rules:[
				{input: '#emplPositionTypeIdEdit', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(value < 0){
							return false
						}
						return true;
					}	
				},
				{input: '#codeEdit', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false;
						}
						return true;
					}	
				},
				{input: '#roleTypeGroupIdEdit', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false;
						}
						return true;
					}	
				},
				{input: "#jqxBtnIncludeGeoEdit", message: "${uiLabelMap.NotChooseOrChooseInvalid}", action: 'blur',
					rule: function(input, commit){
						var parentIdSelected = new Array();
						var idSelected = new Array();
						var items = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
						if(items.length == 0){
							return false;
						}
						for(var i = 0; i < items.length; i++){
							idSelected.push(substringBySeparator(items[i].id, "_"));
							if(items[i].parentId && items[i].parentId != "-1"){
								if(parentIdSelected.indexOf(items[i].parentId)  == -1){
									parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
								}
							}
						}
						var check = checkSelectedValid(idSelected, parentIdSelected);
						return check;
					}				
				},
				{input: '#jqxBtnExcludeGeoEdit', message: "${uiLabelMap.ChooseInvalid}", action: 'blur',
					rule: function(input, commit){
						var parentIdSelected = new Array();
						var idSelected = new Array();
						var items = $("#jqxTreeExcludeGeoEdit").jqxTree('getCheckedItems');
						if(items.length == 0){
							return true;
						}
						var itemsInclude = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
						var itemsIncludeArr = new Array();
						for(var i = 0; i < itemsInclude.length; i++){
							itemsIncludeArr.push(substringBySeparator(itemsInclude[i].id, "_"));
						}
						
						for(var i = 0; i < items.length; i++){
							idSelected.push(substringBySeparator(items[i].id, "_"));
							if(items[i].parentId && items[i].parentId != "-1"){
								if(parentIdSelected.indexOf(items[i].parentId)  == -1){
									parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
								}
							}else{
								parentIdSelected.push("-1");
							}
						}
						//if select of excludeGeo not child of includeGeo return false
						for(var i = 0; i < parentIdSelected.length; i++){
							if(itemsIncludeArr.indexOf(parentIdSelected[i]) == -1){
								return false;
							}
						}
						var check = checkSelectedValid(idSelected, parentIdSelected);
						return check;
					}
				}
	        ]
	 	});
	}
	
	function initJqxButtonTree(){
		$("#jqxBtnIncludeGeo").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		$("#jqxBtnExcludeGeo").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		$("#jqxBtnIncludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		$("#jqxBtnExcludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		var dataIncludeGeo = [
  			<#if geoRegionList?has_content>
	   			<#list geoRegionList as geo>
	   				{
	   					"id": "${geo.geoId}_include",
	   					"parentid": "-1",
	   					"text": "${StringUtil.wrapString(geo.geoName)}",
	   					"value": "${geo.geoId}"
	   				},
	   				{
	   					"id": "${geo.geoId}_inclChild",
	   					"parentid": "${geo.geoId}_include",
	   					"text": "Loading...",
	   					"value": "getGeoAssoc"
	   				}
	   				<#if geo_has_next>
	   					,
	   				</#if>
	   			</#list>
	   		</#if>
   		];
   	
   	var dataExcludeGeo = [
	   		<#if geoRegionList?has_content>
	   			<#list geoRegionList as geo>
	   				{
	   					"id": "${geo.geoId}_exclude",
	   					"parentid": "-1",
	   					"text": "${StringUtil.wrapString(geo.geoName)}",
	   					"value": "${geo.geoId}"
	   				},
	   				{
	   					"id": "${geo.geoId}_exclChild",
	   					"parentid": "${geo.geoId}_exclude",
	   					"text": "Loading...",
	   					"value": "getGeoAssoc"
	   				}
	   				<#if geo_has_next>
	   					,
	   				</#if>
	   			</#list>
	   		</#if>
	   	];
   	
		var dataIncludeGeoEdit = [
  			<#if geoRegionList?has_content>
	   			<#list geoRegionList as geo>
	   				{
	   					"id": "${geo.geoId}_includeEdit",
	   					"parentid": "-1",
	   					"text": "${StringUtil.wrapString(geo.geoName)}",
	   					"value": "${geo.geoId}"
	   				},
	   				{
	   					"id": "${geo.geoId}_inclChildEdit",
	   					"parentid": "${geo.geoId}_includeEdit",
	   					"text": "Loading...",
	   					"value": "getGeoAssoc"
	   				}
	   				<#if geo_has_next>
	   					,
	   				</#if>
	   			</#list>
	   		</#if>
   		];
   	
   	var dataExcludeGeoEdit = [
	   		<#if geoRegionList?has_content>
	   			<#list geoRegionList as geo>
	   				{
	   					"id": "${geo.geoId}_excludeEdit",
	   					"parentid": "-1",
	   					"text": "${StringUtil.wrapString(geo.geoName)}",
	   					"value": "${geo.geoId}"
	   				},
	   				{
	   					"id": "${geo.geoId}_exclChildEdit",
	   					"parentid": "${geo.geoId}_excludeEdit",
	   					"text": "Loading...",
	   					"value": "getGeoAssoc"
	   				}
	   				<#if geo_has_next>
	   					,
	   				</#if>
	   			</#list>
	   		</#if>
	   	];
   	createJqxTree(dataIncludeGeo, $("#jqxTreeIncludeGeo"));
	createJqxTree(dataExcludeGeo, $("#jqxTreeExcludeGeo"));
	addEventToTreeEle($("#jqxTreeIncludeGeo"), $("#jqxBtnIncludeGeo"));
	addEventToTreeEle($("#jqxTreeExcludeGeo"), $("#jqxBtnExcludeGeo"));

	createJqxTree(dataIncludeGeoEdit, $("#jqxTreeIncludeGeoEdit"));
	createJqxTree(dataExcludeGeoEdit, $("#jqxTreeExcludeGeoEdit"));
	addEventToTreeEle($("#jqxTreeIncludeGeoEdit"), $("#jqxBtnIncludeGeoEdit"));
	addEventToTreeEle($("#jqxTreeExcludeGeoEdit"), $("#jqxBtnExcludeGeoEdit"));
	}
	
	function createJqxTree(data, treeEle){
		var source =
	    {
	        datatype: "json",
	        datafields: [
	            { name: 'id' },
	            { name: 'parentid' },
	            { name: 'text' },
	            { name: 'value' }
	        ],
	        id: 'id',
	        localdata: data
	    };	
	    var dataAdapter = new $.jqx.dataAdapter(source);
	    dataAdapter.dataBind();
	    var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
	    treeEle.jqxTree({ source: records, width: "98%", height: "200px", theme: 'energyblue', checkboxes: true});
	}
	
	function addEventToTreeEle(tree, btnDropDown){
		tree.on('expand', function (event) {
			 var item = tree.jqxTree('getItem', event.args.element);
			 var label = item.label;
			 var value = item.value;
			 var idItem = item.id;
			 var $element = $(event.args.element);
			 var loader = false;
			 var loaderItem = null;
	         var children = $element.find('ul:first').children();
	         $.each(children, function () {
	             var item = tree.jqxTree('getItem', this);
	             if (item && item.label == 'Loading...') {
	                 loaderItem = item;
	                 loader = true;
	                 return false
	             };
	         });
	         if (loader) {
	        	var suffixIndex = idItem.lastIndexOf("_");
	        	var suffix = idItem.substring(suffixIndex);        	        	
	            $.ajax({
	                 url: loaderItem.value,
	                 data: {geoId: value},
	                 async: true,
	                 success: function (data, status, xhr) { 
	                	 var listGeoAssoc = data.listGeoAssoc;
	                	 for(var i = 0; i < listGeoAssoc.length; i++){
	                		 var id = listGeoAssoc[i].id;
	                		 if(id){
	                			 listGeoAssoc[i].id = id + suffix;
	                    		 listGeoAssoc[i].parentid = idItem;                			 
	                		 }
	                	 }
	                     var items = jQuery.parseJSON(JSON.stringify(data.listGeoAssoc));
	                     tree.jqxTree('addTo', items, $element[0]);
	                     tree.jqxTree('removeItem', loaderItem.element);
	                     checkItemIfExists(tree);
	                 },
	                 complete: function(){
	                 }
	             });
	         }else{
	        	checkItemIfExists(tree);
	         }
		});
		
		tree.on('checkChange', function(event){
			setDropDownContent(tree, btnDropDown);
		});
	}
	
	function setDropDownContent(jqxTree, dropdownBtn){
		var items = jqxTree.jqxTree('getCheckedItems');
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">';	
		for(var i = 0; i < items.length; i++){
			dropDownContent += items[i].label;
			if(i < items.length - 1){
				dropDownContent += ", ";
			}
		}
		dropDownContent += '</div>';	
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	}
	
	function initJqxNumberInput(){
		$("#amountValueNew, #amountValueEdit").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0});		
	}
	
	function initJqxDateTimeInput(){
		$("#fromDateNew, #fromDateEdit").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDateNew, #thruDateEdit").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDateNew").val(null);
	} 
	 
	function initJqxDropDownList(){
		$("#roleTypeGroupId, #roleTypeGroupIdEdit").jqxDropDownList({source: dataSoureRoleTypeGroupList, placeHolder:'', displayMember: 'description',dropDownHeight:'200px',  
	    	valueMember : 'roleTypeGroupId', theme: 'olbius',itemHeight: 25, height: 25, width: '98%',  
			renderer: function (index, label, value) {
				for(var i=0; i < roleTypeGroupArr.length; i++){
					if(roleTypeGroupArr[i].roleTypeGroupId == value){
						return roleTypeGroupArr[i].description;
					}
				}
			    return value;
			}
		});
		<#if (roleTypeGroupList?size < 8)>
			$("#roleTypeGroupId, #roleTypeGroupIdEdit").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		$('#emplPositionTypeIdAddNew, #emplPositionTypeIdEdit').jqxDropDownList({selectedIndex: 0, source: dataEmplPosTypeList, 
	    	displayMember: "description", valueMember: "emplPositionTypeId", itemHeight: 25, height: 25, width: '98%', theme: theme, dropDownHeight: 200,
	    	autoDropDownHeight: false,
	        renderer: function (index, label, value) {
	            for(var i = 0; i < emplPosTypeArr.length; i++){
	            	if(emplPosTypeArr[i].emplPositionTypeId == value){
	            		return emplPosTypeArr[i].description; 
	            	}
	            }
	            return value;
	        }
		});
		$('#codeAddNew, #codeEdit').jqxDropDownList({  source: dataSoureParameterList,
	    	displayMember: "name", valueMember: "code", itemHeight: 25, height: 25, width: '98%', theme: theme, dropDownHeight: 120,
	        renderer: function(index, label, value) {
	            for(var i = 0; i < parametersArr.length; i++){
	            	if(parametersArr[i].code == value){
	            		return parametersArr[i].name; 
	            	}
	            }
	            return value;
	        }
	   	});
	 	<#if (payrollParametersList?has_content && payrollParametersList?size < 8)>
		 	$('#codeAddNew, #codeEdit').jqxDropDownList({autoDropDownHeight: true});
		</#if> 
		$('#codeEdit').on('select', function (event){
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
				var type = parametersArr[index].type;
				if(type == 'CONSTPERCENT'){
		    		$("#amountValueEdit").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    		$("#amountLabelEdit").text("${StringUtil.wrapString(uiLabelMap.HrCommonRates)}");
		    		$("#amountValueEdit").val(0);
		    	}else{
		    		$("#amountValueEdit").jqxNumberInput({decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    		$("#amountLabelEdit").text("${StringUtil.wrapString(uiLabelMap.DAAmount)}");
		    		$("#amountValueEdit").val(0);
		    	}
			}                        
		});
		$('#codeAddNew').on('select', function (event){
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
				var type = parametersArr[index].type;				
				var periodTypeId = parametersArr[index].periodTypeId;
				for(var i = 0; i < periodTypeArr.length; i++){
					if(periodTypeId == periodTypeArr[i].periodTypeId){
						$("#periodTypeNew").html(periodTypeArr[i].description);
						break;
					}
				}
				if(type == 'CONSTPERCENT'){
		    		$("#amountValueNew").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    		$("#amountLabelNew").text("${StringUtil.wrapString(uiLabelMap.HrCommonRates)}");
		    	}else{
		    		$("#amountValueNew").jqxNumberInput({decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    		$("#amountLabelNew").text("${StringUtil.wrapString(uiLabelMap.DAAmount)}");
		    	}
				
			}                        
		});
		
		$('#periodTypeEdit').jqxDropDownList({source: dataSourePeriodTypeList,
	    	displayMember: "description", valueMember: "periodTypeId", itemHeight: 25, height: 25, width: '98%', theme: theme, dropDownHeight: 120,
	        renderer: function (index, label, value) {
	            for(var i = 0; i < periodTypeArr.length; i++){
	            	if(periodTypeArr[i].periodTypeId == value){
	            		return periodTypeArr[i].description; 
	            	}
	            }
	            return value;
	        }
	   	});
	 	<#if (periodTypeList?size < 8)>
		 	$('#periodTypeEdit').jqxDropDownList({autoDropDownHeight: true});
		</#if>
		<#if defaultPeriodTypeId?exists>
			//$("#periodTypeNew").jqxDropDownList('selectItem', "${defaultPeriodTypeId}");
		</#if>
	}
	
	function btnEvent(){
		$("#alterCancel").click(function(){
			 $("#popupWindowAddNew").jqxWindow('close');
		});
		
		$("#editCancel").click(function(event){
			$("#popupWindowEdit").jqxWindow('close');
		});
		$("#alterSave").click(function(){
			var valid = $("#createPayrollParamPositionTypeForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}?",
				[
					{
						"label": "${uiLabelMap.CommonSubmit}",
						"class" : "icon-ok btn btn-mini btn-primary",
						"callback": function(){
							submitCreatePayrollParamEmplPosType();
						}
					},
					{
						"label" : "${uiLabelMap.CommonCancel}",
						"class" : "btn-danger icon-remove btn-mini",
					 	"callback": function() {
					   
					   }
					} 
				]		
			);
		 });
		$("#editSave").click(function(){
			var valid = $("#editPayrollParamPositionTypeForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.EmplPositionTypeRateGeoApplUpdate)}?",
				[
					{
						"label": "${uiLabelMap.CommonSubmit}",
						"class" : "icon-ok btn btn-mini btn-primary",
						"callback": function(){
							submitEditPayrollParamEmplPosType();
						}
					},
					{
						"label" : "${uiLabelMap.CommonCancel}",
						"class" : "btn-danger icon-remove btn-mini",
					 	"callback": function() {
					   
					   }
					} 
				]		
			);
		 });
	}
	
	function submitEditPayrollParamEmplPosType(){
		$("#popupWindowEdit").jqxWindow('close');
		var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		if(rowIndex > -1){
			var dataSubmit = {};
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
			dataSubmit["payrollParamPositionTypeId"] = data.payrollParamPositionTypeId;
			dataSubmit["emplPositionTypeId"] = $("#emplPositionTypeIdEdit").jqxDropDownList('getSelectedItem').value;
			dataSubmit["code"] = $("#codeEdit").jqxDropDownList('getSelectedItem').value;
			dataSubmit["roleTypeGroupId"] = $("#roleTypeGroupIdEdit").jqxDropDownList('getSelectedItem').value;
			dataSubmit["periodTypeId"] = $("#periodTypeEdit").jqxDropDownList('getSelectedItem').value;
			dataSubmit["fromDate"] = $("#fromDateEdit").jqxDateTimeInput('getDate').getTime();
			if($("#thruDateEdit").val()){
				dataSubmit["thruDate"] = $("#thruDateEdit").jqxDateTimeInput('getDate').getTime();
			}
			dataSubmit["rateAmount"] = $("#amountValueEdit").val();
			var includeSelectedGeoItems = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
			var excludeSelectedGeoItems = $("#jqxTreeExcludeGeoEdit").jqxTree('getCheckedItems');
			var includeSelectedGeoArr = new Array();
			var excludeSelectedGeoArr = new Array();
			for(var i = 0; i < includeSelectedGeoItems.length; i++){
				includeSelectedGeoArr.push({"includeGeoId": includeSelectedGeoItems[i].value});
			}
			for(var i = 0; i < excludeSelectedGeoItems.length; i++){
				excludeSelectedGeoArr.push({"excludeGeoId": excludeSelectedGeoItems[i].value});
			}
			dataSubmit["includeGeoId"] = JSON.stringify(includeSelectedGeoArr);
			dataSubmit["excludeGeoId"] = JSON.stringify(excludeSelectedGeoArr);
			$.ajax({
				url: "updatePayrollParamPosTypeGeoAppl",
				type: 'POST',
				data: dataSubmit,
				success: function(data){
					var notifyEle = $("#jqxNotify"); 
					notifyEle.jqxNotification('closeLast');
					if(data.responseMessage == "success"){
						$("#jqxNotifyContainer").empty();
						notifyEle.empty();
						notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
							autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
						notifyEle.text(data.successMessage);
						notifyEle.jqxNotification("open");
						$('#jqxgrid').jqxGrid("updatebounddata");
					}else{
						$("#jqxNotifyContainer").empty();
						notifyEle.empty();
						notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
							autoClose: false, template: "error", appendContainer: "#jqxNotifyContainer"});
						notifyEle.text(data.errorMessage);
						notifyEle.jqxNotification("open");
					}	
				}
			 });
		}
	}
	
	function submitCreatePayrollParamEmplPosType(){
		var thruDate = $("#thruDateNew").jqxDateTimeInput('getDate');
		var includeSelectedGeoItems = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
		var excludeSelectedGeoItems = $("#jqxTreeExcludeGeo").jqxTree('getCheckedItems');
		var includeSelectedGeoArr = new Array();
		var excludeSelectedGeoArr = new Array();
		 
		for(var i = 0; i < includeSelectedGeoItems.length; i++){
			includeSelectedGeoArr.push({"includeGeoId": includeSelectedGeoItems[i].value});
		}
		for(var i = 0; i < excludeSelectedGeoItems.length; i++){
			excludeSelectedGeoArr.push({"excludeGeoId": excludeSelectedGeoItems[i].value});
		}
		var row = {
    		emplPositionTypeId: $('#emplPositionTypeIdAddNew').val(),
    		//periodTypeId: $("#periodTypeNew").val(),
    		rateAmount: $("#amountValueNew").val(),
    		fromDate: $("#fromDateNew").jqxDateTimeInput('getDate'),
    		thruDate: thruDate,
    		code: $("#codeAddNew").val(),
    		roleTypeGroupId: $("#roleTypeGroupId").val(),
    		includeGeoId: JSON.stringify(includeSelectedGeoArr),
    		excludeGeoId: JSON.stringify(excludeSelectedGeoArr)
    	};
		
    	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    	$("#popupWindowAddNew").jqxWindow('close');
	}
	
	function checkItemIfExists(treeCheckEle){
		for(var i = 0; i < elementCheckInJqxTreeGeo.length; i++){
			var element = $("#" + elementCheckInJqxTreeGeo[i])[0];
			var item = treeCheckEle.jqxTree('getItem', element);
			if(item){
				treeCheckEle.jqxTree("checkItem", element, true);
			}
		}						
	}
	
	function substringBySeparator(str, separator){
		if(str.indexOf(separator) > -1){
			var separatorLastIndexOf = str.lastIndexOf(separator);
			return str.substring(0, separatorLastIndexOf);		
		}else{
			return str;		
		}
	}
	
	function checkSelectedValid(idSelected, parentIdSelected){
		for(var i = 0; i < parentIdSelected.length; i++){
			if(idSelected.indexOf(parentIdSelected[i]) > -1){
				return false;
			}
		}
		return true;
	}
	
	function fillDataInWindowEdit(data){
		elementCheckInJqxTreeGeo = [];
		$.ajax({
			url: 'getpayrollParamPositionTypeGeoAppl',
			data: {payrollParamPositionTypeId: data.payrollParamPositionTypeId},
			type: 'POST',
			success: function(data){	
				if(data.checkIncludeGeoList){
					var checkIncludeGeoList = data.checkIncludeGeoList; 
					for(var i = 0; i < checkIncludeGeoList.length; i++){
						var element = $("#" + checkIncludeGeoList[i] + "_includeEdit")[0];
						var item = $('#jqxTreeIncludeGeoEdit').jqxTree('getItem', element);
						if(item){
							$("#jqxTreeIncludeGeoEdit").jqxTree("checkItem", item, true);							
						}else{
							elementCheckInJqxTreeGeo.push(checkIncludeGeoList[i] + "_includeEdit");	
						}
					}
				}
				if(data.checkExcludeGeoList){
					var checkExcludeGeoList = data.checkExcludeGeoList;
					for(var i = 0; i < checkExcludeGeoList.length; i++){
						var element = $("#" + checkExcludeGeoList[i] + "_excludeEdit")[0];
						var item = $('#jqxTreeExcludeGeoEdit').jqxTree('getItem', element);
						if(item){
							$("#jqxTreeExcludeGeoEdit").jqxTree("checkItem", item, true);							
						}else{
							elementCheckInJqxTreeGeo.push(checkExcludeGeoList[i] + "_excludeEdit");	
						}
					}
				}
				if(data.expandIncludeGeoList){
					var expandIncludeGeoList = data.expandIncludeGeoList; 
					for(var i = 0; i < expandIncludeGeoList.length; i++){
						$("#jqxTreeIncludeGeoEdit").jqxTree("expandItem", $("#" + expandIncludeGeoList[i] + "_includeEdit")[0]);
					}
				}
				if(data.expandExcludeGeoList){
					var expandExcludeGeoList = data.expandExcludeGeoList; 
					for(var i = 0; i < expandExcludeGeoList.length; i++){
						$("#jqxTreeExcludeGeoEdit").jqxTree("expandItem", $("#" +expandExcludeGeoList[i] + "_excludeEdit")[0]);
					}
				}
			}
		});
		$("#payrollParamPositionTypeId").val(data.payrollParamPositionTypeId);
		$("#emplPositionTypeIdEdit").jqxDropDownList('val', data.emplPositionTypeId);
		$("#roleTypeGroupIdEdit").jqxDropDownList('val', data.roleTypeGroupId);
		$("#fromDateEdit").jqxDateTimeInput('val', data.fromDate);
		$("#codeEdit").jqxDropDownList('val', data.code);
		$("#periodTypeEdit").jqxDropDownList('val', data.periodTypeId);
		if(data.thruDate){
			$("#thruDateEdit").jqxDateTimeInput('val', data.thruDate);	
		}else{
			$("#thruDateEdit").val(null);
		}
		$("#amountValueEdit").val(data.rateAmount);
	}
	
</script>
<div class="row-fluid" id="jqxNotifyContainer"></div>
<div id="jqxNotify">
	<div id="ntfContent"></div>
</div>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="true" alternativeAddPopup="popupWindowAddNew" deleterow="false" editable="false" addrow="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListParamPosTypeGeo" id="jqxgrid" customControlAdvance="<div id='dateTimeInput'></div>"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=processPayrollParamPositionType" jqGridMinimumLibEnable="false"		
		 addColumns="periodTypeId;code;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);rateAmount;emplPositionTypeId;includeGeoId;excludeGeoId;roleTypeGroupId" 
		 selectionmode="singlerow" addrefresh="true"/>

<script type="text/javascript">
$(document).ready(function () {
	$("#jqxgrid").on("loadCustomControlAdvance", function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(${monthStart.getTime()});
		var thruDate = new Date(${monthEnd.getTime()});
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    refreshGridData(fromDate, thruDate);
		});
	});
});

function refreshGridData(fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=JQListParamPosTypeGeo&hasrequest=Y&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}
</script>		 
<div class="row-fluid">
	<div id="popupWindowEdit" class='hide'>
		<div>${uiLabelMap.EditPayrollParamPositionType}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form class="form-horizontal" id="editPayrollParamPositionTypeForm">
					<input type="hidden" name="payrollParamPositionTypeId" id="payrollParamPositionTypeId">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<div id="emplPositionTypeIdEdit">
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.parameters}</label>
						</div>
						<div class="span7">
							<div id="codeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.HRCommonChannel}</label>
						</div>
						<div class="span7">
							<div id="roleTypeGroupIdEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.HRAreaApply}</label>
						</div>
						<div class="span7">
							<div id="jqxBtnIncludeGeoEdit">
								<div style="border: none;" id="jqxTreeIncludeGeoEdit"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.HRAreaExclude}</label>
						</div>
						<div class="span7">
							<div id="jqxBtnExcludeGeoEdit">
								<div style="border: none;" id="jqxTreeExcludeGeoEdit"></div>
							</div>
						</div>
					</div>								
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
						</div>
						<div class="span7">
							<div id="fromDateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class="span7">
							<div id="thruDateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label"><span id="amountLabelEdit">${uiLabelMap.DAAmount}</span></label>
						</div>
						<div class="span7">
							<div id="amountValueEdit"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="editSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="popupWindowAddNew" class='hide'>
		<div id="EmplPosTypeRateWindowHeader">
			 ${uiLabelMap.AddEmpPosTypeSalary} 
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form class="form-horizontal" id="createPayrollParamPositionTypeForm">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<div id="emplPositionTypeIdAddNew">
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.parameters}</label>
						</div>
						<div class="span7">
							<div id="codeAddNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.HRCommonChannel}</label>
						</div>
						<div class="span7">
							<div id="roleTypeGroupId"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.HRAreaApply}</label>
						</div>
						<div class="span7">
							<div id="jqxBtnIncludeGeo">
								<div style="border: none;" id="jqxTreeIncludeGeo"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.HRAreaExclude}</label>
						</div>
						<div class="span7">
							<div id="jqxBtnExcludeGeo">
								<div style="border: none;" id="jqxTreeExcludeGeo"></div>
							</div>
						</div>
					</div>								
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeNew">${uiLabelMap.HRCommonNotSetting}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
						</div>
						<div class="span7">
							<div id="fromDateNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class="span7">
							<div id="thruDateNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label"><span id="amountLabelNew">${uiLabelMap.DAAmount}</span></label>
						</div>
						<div class="span7">
							<div id="amountValueNew"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>			 
