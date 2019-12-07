<#assign dataFields = "[{name: 'emplPositionTypeId', type: 'string'},
						{name: 'emplPositionTypeRateId', type: 'string'},
						{name: 'excludeGeo', type: 'string'},
						{name: 'includeGeo', type: 'string'},
						{name: 'roleTypeGroupId', type: 'string'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'rateAmount', type: 'number'},
						{name: 'rateCurrencyUomId', type: 'string'},
						]" />
<@jqGridMinimumLib/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthStart, timeZone, locale)/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var emplPosTypeArr = new Array();
	var elementCheckInJqxTreeGeo = [];
	
	<#list emplPosType as posType>
		var row = {};
		row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
		row["description"] = "${StringUtil.wrapString(posType.description)}";
		emplPosTypeArr[${posType_index}] = row;
	</#list>
	/* var emplPosTypeAdapter = new $.jqx.dataAdapter(emplPosTypeArr, {autoBind: true});
	var dataEmplPosTypeList = emplPosTypeAdapter.records;  */
	var periodTypeArr = new Array();
	<#list periodTypeList as periodType>
		var row = {};
		row["periodTypeId"] = "${periodType.periodTypeId}";
		row["description"] = "${StringUtil.wrapString(periodType.description?if_exists)}";
		periodTypeArr[${periodType_index}] = row;
	</#list>
	var filterBoxAdapter = new $.jqx.dataAdapter(periodTypeArr, {autoBind: true});
	var dataSoureList = filterBoxAdapter.records;

	var roleTypeGroupArr = new Array();
	
	<#if roleTypeGroupList?has_content>
		<#list roleTypeGroupList as roleTypeGroup>
			var row = {};
			row["roleTypeGroupId"] = "${roleTypeGroup.roleTypeGroupId}";
			row["description"] = "${StringUtil.wrapString(roleTypeGroup.description)}";
			roleTypeGroupArr[${roleTypeGroup_index}] = row;
		</#list>
	</#if>
	
	var filterRoleTypeGroupAdapter = new $.jqx.dataAdapter(roleTypeGroupArr, {autoBind: true});
	var dataSoureRoleTypeGroupList = filterRoleTypeGroupAdapter.records;
	
	var geoRegionArr = new Array();
	<#if geoRegionList?has_content>
		<#list geoRegionList as geo>
			var row = {};
			row["geoId"] = "${geo.geoId}";
			row["geoName"] = "${StringUtil.wrapString(geo.geoName)}";
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
			row["geoName"] = "${StringUtil.wrapString(geo.geoName)}";
			geoAreaArr[${geo_index}] = row;
		</#list>
	</#if>
	var filterGeoAreaAdapter = new $.jqx.dataAdapter(geoAreaArr, {autoBind: true});
	var dataSoureGeoAreaList = filterGeoAreaAdapter.records; 
	
	var uomArray = new Array();
	 <#list uomList as uom>
	 	var row = {};
	 	row["uomId"] = "${uom.uomId}";
	 	row["description"] = "${uom.description?if_exists}";
	 	uomArray[${uom_index}] = row;
	 </#list>
	 var filterUomAdapter = new $.jqx.dataAdapter(uomArray, {autoBind: true});
	 var dataSoureUomList = filterUomAdapter.records;
	
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
					var emplPositionTypeId = datarecord.emplPositionTypeId;
					var fromDate = datarecord.fromDate;
					var periodTypeId = datarecord.periodTypeId;
					var urlStr = 'getEmplPositionTypeRateHistory';
					
					var id = datarecord.uid.toString();
					var grid = $($(parentElement).children()[0]);
			        $(grid).attr('id', 'jqxgridDetail_' + id);
	        		var emplPosTypeSalarySource = {datafields: [
           		            {name: 'fromDateDetail', type: 'date'},
           		            {name: 'thruDateDetail', type: 'date'},
           		            {name: 'rateAmountDetail', type: 'number'},
           		            {name: 'periodTypeIdDetail', type: 'string'},
           		            {name: 'rateCurrencyUomIdDetail', type: 'string'}           		            
           				],
           				cache: false,
           				//localdata: emplSalaryArr,
           				datatype: 'json',
           				type: 'POST',
           				data: {emplPositionTypeId: emplPositionTypeId},
           		        url: urlStr,
           		        deleterow: function(rowId, commit){
           		        	
           		        }
           	        };
			        var nestedGridAdapter = new $.jqx.dataAdapter(emplPosTypeSalarySource);
			        if (grid != null) {
			        	grid.jqxGrid({
			        		source: nestedGridAdapter,
			        		width: '100%', height: 170,
			                showtoolbar:false,
					 		editable: false,
					 		editmode:'selectedrow',
					 		showheader: true,
					 		 
					 		selectionmode:'singlecell',
					 		theme: 'energyblue',
					 		columns: [
												 		          
					 		 	{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDateDetail', cellsalign: 'left', width: '25%', cellsformat: 'dd/MM/yyyy ', columntype: 'template',
					 				 			
					 		 	},
					 		 	{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDateDetail', cellsalign: 'left', width: '24%', cellsformat: 'dd/MM/yyyy ', columntype: 'template',
					 		 		
					 		 	},
					 		 	{text: '${uiLabelMap.CommonAmount}',datafield: 'rateAmountDetail', filterable: false,editable: false, cellsalign: 'right', width: '23%', 
					 		 		 cellsrenderer: function (row, column, value) {
						 		 		 var data = grid.jqxGrid('getrowdata', row);
						 		 		 if (data && data.rateAmountDetail){
						 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.rateAmountDetail, data.rateCurrencyUomIdDetail) + \"</div>\";
						 		 		 }
					 		 		 }
					 		 	},
					 		 	
					 		 	{text: '${uiLabelMap.PeriodTypePayroll}', datafield: 'periodTypeIdDetail', filterable: false,editable: false, cellsalign: 'left', width: '23%',
									cellsrenderer: function (row, column, value){
										for(var i = 0; i < periodTypeArr.length; i++){
											if(periodTypeArr[i].periodTypeId == value){
												return '<div style=\"margin-top: 4px; margin-left: 2px\">' + periodTypeArr[i].description + '</div>';		
											}
										}
									}	
								},
								{text:'', datafield: 'rateCurrencyUomIdDetail', hidden: true},
		 		            ]
			        	});
			        }
				}">
	
	<#assign columnlist = "{datafield: 'emplPositionTypeRateId', hidden: true, editable: false},
						   {text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', filterable: true, editable: false, cellsalign: 'left', 
								filtertype: 'checkedlist', width: 160,
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
							{text: '${uiLabelMap.HRCommonChannel}', datafield: 'roleTypeGroupId', filterable: false, editable: false, cellsalign: 'left', width: 90,
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < roleTypeGroupArr.length; i++){
										if(roleTypeGroupArr[i].roleTypeGroupId == value){
											return '<div style=\"\">' + roleTypeGroupArr[i].description + '</div>';		
										}
									}
									return '<div style=\"\">' + value + '</div>';
								}	
							},
							{text: '${uiLabelMap.HRAreaApply}', datafield: 'includeGeo', filterable: false, editable: false, cellsalign: 'left', width: 240},
							{text: '${uiLabelMap.HRAreaExclude}', datafield: 'excludeGeo', filterable: false, editable: false, cellsalign: 'left', width: 130},
							{text: '${uiLabelMap.PeriodTypePayroll}', datafield:'periodTypeId' , filterable: false, editable: false, cellsalign: 'left', width: 120,
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
										return '<div style=\"margin-left: 4px\">${uiLabelMap.HRCommonNotSetting}</div>';
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
					 		 		 	return \"<div style='margin-right: 2px; margin-top: 9.5px; text-align: right;'>\" + formatcurrency(data.rateAmount, data.rateCurrencyUomId) + \"</div>\";
					 		 		 }
				 		 		 }								
							},
							
							{text: '', datafield: 'rateCurrencyUomId', hidden: true}"/>
var rowUpdateIndex = -1;
theme = 'olbius';
$(document).ready(function () {
	initJqxDateTimeInput();
	initJqxDropDownList(); 
	initJqxNumberInput(); 
	//initJqxCombobox();
	initJqxButtonTree();
	
	$("#jqxgrid").on('rowDoubleClick', function (event){
		//var dataField = event.args.datafield;
		var rowBoundIndex = event.args.rowindex;
		var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
		fillDataInWindow(data);
		openJqxWindow($('#editEmplPosTypeRateWindow'));
	});
	
	$('#editEmplPosTypeRateWindow').jqxWindow({
        showCollapseButton: false, isModal:true, maxHeight: 460, minHeight: 460, height: 460, width: 530, minWidth: 530, theme:'olbius',
        autoOpen: false,  modalZIndex: 10000,
        initContent: function () {
            
        }
    });
	 jQuery("#popupWindowEmplPosTypeRate").jqxWindow({showCollapseButton: false, autoOpen: false,
			minHeight: 420, minWidth: 530, height: 420, width: 530, isModal: true, theme:theme, modalZIndex: 10000,
		initContent: function(){
			
		}		
	 });
	 
	 $('#editEmplPosTypeRateWindow').on('open', function(event){
	 });
	 $('#editEmplPosTypeRateWindow').on('close', function(event){
		 $("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('uncheckAll');
		 $("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('collapseAll');
	 });
	 
	 initBtnEvent();
	 
	 $("#updateNotificationSalary").jqxNotification({
         width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
         autoOpen: false, animationOpenDelay: 800, autoClose: false
     });
	 
	 $("#jqxgrid").on('cellEndEdit', function (event){
		 rowUpdateIndex = event.args.rowindex;
	 });
 	/*=============== validator =========================*/
 	initJqxValidator();
 	/*=============== ./end validator ===================*/
});		

function initJqxButtonTree(){
	$("#jqxBtnIncludeGeo, #jqxBtnIncludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
	$("#jqxBtnExcludeGeo, #jqxBtnExcludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
	
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
					"id": "${geo.geoId}_exclChild_edit",
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
	createJqxTree(dataIncludeGeoEdit, $("#jqxTreeIncludeGeoEdit"));
	createJqxTree(dataExcludeGeoEdit, $("#jqxTreeExcludeGeoEdit"));
	addEventToTreeEle($("#jqxTreeIncludeGeo"), $("#jqxBtnIncludeGeo"));
	addEventToTreeEle($("#jqxTreeExcludeGeo"), $("#jqxBtnExcludeGeo"));
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
	 // create data adapter.
    var dataAdapter = new $.jqx.dataAdapter(source);
    // perform Data Binding.
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

function checkItemIfExists(treeCheckEle){
	for(var i = 0; i < elementCheckInJqxTreeGeo.length; i++){
		var element = $("#" + elementCheckInJqxTreeGeo[i])[0];
		var item = treeCheckEle.jqxTree('getItem', element);
		if(item){
			treeCheckEle.jqxTree("checkItem", element, true);
		}
	}
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

function submitCreateEmplPosTypeRateForm(){
	$("#popupWindowEmplPosTypeRate").jqxWindow('close');
    //$("#alterSave").attr("disabled", "disabled");
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
		emplPositionTypeId: $('#setSalaryEmplPositionTypeId').val(),
		periodTypeId: $("#periodTypeNew").val(),
		rateAmount: $("#amountValueNew").val(),
		fromDate: $("#fromDateNew").jqxDateTimeInput('getDate'),
		thruDate: thruDate,
		roleTypeGroupId: $("#roleTypeGroupDropDown").val(),
		includeGeoId: JSON.stringify(includeSelectedGeoArr),
		excludeGeoId: JSON.stringify(excludeSelectedGeoArr) 
	};
	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
}

function initBtnEvent(){
	$("#alterSave").click(function(){
		var valid = $("#createEmplPosTypeRateForm").jqxValidator('validate');
		if(!valid){
			return false;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}?",
			[
				{
					"label": "${uiLabelMap.CommonSubmit}",
					"class" : "icon-ok btn btn-mini btn-primary",
					"callback": function(){
						submitCreateEmplPosTypeRateForm();
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
	 $("#alterCancel").click(function(){
		 $("#popupWindowEmplPosTypeRate").jqxWindow('close');
	 });
	 
	 $("#submitForm").click(function(){
		var valid = $("#editEmplPosTypeRateForm").jqxValidator('validate');
		if(!valid){
			return false;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.EmplPositionTypeRateGeoApplUpdate)}?",
		[
			{
				"label": "${uiLabelMap.CommonSubmit}",
				"class" : "icon-ok btn btn-mini btn-primary",
				"callback": function(){
					submitEditEmplPosTypeRateForm();
				}
			},
			{
				"label" : "${uiLabelMap.CommonCancel}",
    			"class" : "btn-danger icon-remove btn-mini",
    		 	"callback": function() {
    		 		//$('#editEmplPosTypeRateWindow').jqxWindow('close');
    		    }
			}
		]);
	 });
	 $("#cancelSubmit").click(function(){
		 $('#editEmplPosTypeRateWindow').jqxWindow('close');
	 });
	 
	 $("#addNew").click(function(event){
		openJqxWindow($("#popupWindowEmplPosTypeRate")); 
	 });
}

function submitEditEmplPosTypeRateForm(){
    var dataSubmit = new Array();
	var emplPositionTypeId = $("#emplPositionTypeIdEdit").val();
	var fromDate = $("#payrollFromDate").jqxDateTimeInput('getDate').getTime();
	var uomId = jQuery("#CurrencyUomIdSalary").jqxDropDownList('getSelectedItem').value;
	var periodTypeId = $("#periodTypeSalary").jqxDropDownList('getSelectedItem').value;
	var amount = $("#amountSalary").val();
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
	dataSubmit.push({name: "emplPositionTypeRateId", value: $("#emplPositionTypeRateId").val()});		 
	dataSubmit.push({name: "fromDate", value: fromDate});		 
	dataSubmit.push({name: "uomId", value: uomId});
	dataSubmit.push({name: "periodTypeId", value: periodTypeId});
	dataSubmit.push({name: "rateAmount", value: amount});
	dataSubmit.push({name: "emplPositionTypeId", value: emplPositionTypeId});
	dataSubmit.push({name: "roleTypeGroupId", value: $("#roleTypeGroupEdit").val()});
	dataSubmit.push({name: "includeGeoId", value: JSON.stringify(includeSelectedGeoArr)});
	dataSubmit.push({name: "excludeGeoId", value: JSON.stringify(excludeSelectedGeoArr)});
	
	if($("#payrollThruDate").val()){
		dataSubmit.push({name: "thruDate", value: $("#payrollThruDate").jqxDateTimeInput('getDate').getTime()});
	}
	$.ajax({
		url: "updateEmplPositionTypeRateGeoAppl",
		type: 'POST',
		data: dataSubmit,
		success: function(data){
			//console.log(data);
			$("#updateNotificationSalary").jqxNotification('closeLast');
			if(data.responseMessage == "success"){
				$("#notificationText").html(data.successMessage);
				$("#updateNotificationSalary").jqxNotification({ template: 'info' });
				$("#updateNotificationSalary").jqxNotification('open');
				/* var rowId = $('#jqxgrid').jqxGrid('getrowid', rowSelectedIndex);
				if($("#jqxgridDetail_" + rowId).length){
					$("#jqxgridDetail_" + rowId).jqxGrid('updatebounddata');
				}else{
					$('#jqxgrid').jqxGrid('showrowdetails', rowSelectedIndex);
				}
				var editRow = {
						emplPositionTypeId: $("#emplPositionTypeId").val(),
						periodTypeId: periodTypeId,
						thruDate: $("#payrollThruDate").jqxDateTimeInput('getDate'),
						rateAmount: amount,
						fromDate: $("#payrollFromDate").jqxDateTimeInput('getDate'),
						roleTypeGroupId: $("#roleTypeGroupId").val(),
						contactMechId:$("#contactMechId").val(),
						regionGeoId: $("#regionGeoId").val(),
						stateProvinceGeoId: $("#areaGeoId").val(),
						rateCurrencyUomId: data.rateCurrencyUomId
				} */
				$('#jqxgrid').jqxGrid("updatebounddata");
			}else{
				$("#notificationText").html(data.errorMessage);
				$("#updateNotificationSalary").jqxNotification({template: 'info' });
				$("#updateNotificationSalary").jqxNotification('open');
			}	
		}
	 });
	$('#editEmplPosTypeRateWindow').jqxWindow('close');
}

function initJqxDropDownList(){
	$("#periodTypeSalary").jqxDropDownList({ width: '98%',source: dataSoureList, displayMember: 'description', 
	 	valueMember : 'periodTypeId', height: '25px', theme: 'olbius', searchMode: 'contains', dropDownHeight: 140,
		renderer: function (index, label, value) {
			for(i=0; i < periodTypeArr.length; i++){
				if(periodTypeArr[i].periodTypeId == value){
					return periodTypeArr[i].description;
				}
			}
		    return value;
		}
	 });
	 <#if defaultPeriodTypeId?exists>
		 $("#periodTypeSalary").jqxDropDownList('selectItem', "${defaultPeriodTypeId}");
	 </#if>
	 <#if (periodTypeList?size < 8)>
	 	$('#periodTypeSalary').jqxDropDownList({autoDropDownHeight: true});
	 </#if>
	 
	 jQuery("#CurrencyUomIdSalary").jqxDropDownList({ width: '98%', source: dataSoureUomList, displayMember: 'uomId', valueMember : 'uomId', 
		 	height: '25px', theme: 'olbius', searchMode: 'contains', dropDownHeight: 190,
			renderer: function (index, label, value) {
				for(i=0; i < uomArray.length; i++){
					if(uomArray[i].uomId == value){
						return uomArray[i].description;
					}
				}
			    return value;
			}
		});
	
	 <#if rateCurrencyUomId?exists>
	 	jQuery("#CurrencyUomIdSalary").jqxDropDownList('selectItem', "${rateCurrencyUomId}");
	 </#if>
	 
	 $('#periodTypeNew').jqxDropDownList({  source: dataSoureList,
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
	 	$('#periodTypeNew').jqxDropDownList({autoDropDownHeight: true});
	 </#if>
	 $('#periodTypeNew').val("${defaultPeriodTypeId?if_exists}");
	 
	var emplPositionTypeSource = {
		 localdata: emplPosTypeArr,
         datatype: "array"	
	}; 
	var emplPositionTypeAdapter = new $.jqx.dataAdapter(emplPositionTypeSource);
	$('#setSalaryEmplPositionTypeId, #emplPositionTypeIdEdit').jqxDropDownList({selectedIndex: 0, source: emplPositionTypeAdapter, 
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
	$("#roleTypeGroupDropDown, #roleTypeGroupEdit").jqxDropDownList({source: dataSoureRoleTypeGroupList, placeHolder:'', displayMember: 'description',dropDownHeight:'200px',  
    	valueMember : 'roleTypeGroupId', theme: 'olbius', itemHeight: 25, height: 25, width: '98%',  
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
		$("#roleTypeGroupDropDown, #roleTypeGroupEdit").jqxDropDownList({autoDropDownHeight: true});
	</#if>
	
}

function initJqxValidator(){
	$("#createEmplPosTypeRateForm").jqxValidator({
 		rules:[
			{input: '#amountValueNew', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur', 
				rule: function (input, commit){
					var value = input.val();
					
					if(value < 0){
						return false
					}
					return true;
				}	
			},
			{input: '#roleTypeGroupDropDown', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
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
	
 	$("#editEmplPosTypeRateForm").jqxValidator({
 		rules:[
			{input: '#amountSalary', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur', 
				rule: function (input, commit){
					var value = input.val();
					
					if(value < 0){
						return false
					}
					return true;
				}	
			},
			{input: '#roleTypeGroupEdit', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
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

function checkSelectedValid(idSelected, parentIdSelected){
	for(var i = 0; i < parentIdSelected.length; i++){
		if(idSelected.indexOf(parentIdSelected[i]) > -1){
			return false;
		}
	}
	return true;
}

function substringBySeparator(str, separator){
	if(str.indexOf(separator) > -1){
		var separatorLastIndexOf = str.lastIndexOf(separator);
		return str.substring(0, separatorLastIndexOf);		
	}else{
		return str;		
	}
}

function fillDataInWindow(data){
	var emplPosDes = '';
	var roleTypeGroup = '';
	//rowSelectedIndex = rowBoundIndex;	
	$("#emplPositionTypeRateId").val(data.emplPositionTypeRateId);
	elementCheckInJqxTreeGeo = [];
	$.ajax({
		url: 'getEmplPositionTypeRateGeoAppl',
		data: {emplPositionTypeRateId: data.emplPositionTypeRateId},
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
	
	$("#emplPositionTypeIdEdit").jqxDropDownList('val', data.emplPositionTypeId);
	$("#roleTypeGroupEdit").jqxDropDownList('val', data.roleTypeGroupId);
	
	$("#payrollFromDate").jqxDateTimeInput('val', data.fromDate);
	if(data.thruDate){
		$("#payrollThruDate").jqxDateTimeInput('val', data.thruDate);	
	}else{
		$("#payrollThruDate").val(null);
	}
	$("#amountSalary").val(data.rateAmount);
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}

function initJqxDateTimeInput(){
	$("#payrollFromDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#payrollFromDate").jqxDateTimeInput({disabled: false});
	$("#payrollThruDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#payrollThruDate").val(null);
	
	$("#fromDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#thruDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#thruDateNew").val(null);
	
	$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
	var fromDate = new Date(${monthStart.getTime()});
	var thruDate = new Date(${monthEnd.getTime()});
	$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
	$("#dateTimeInput").on('change', function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    refreshGridData(fromDate, thruDate);
	});
}

function refreshGridData(fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQListEmplPositionTypeRate&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}

function initJqxNumberInput(){
	$("#amountSalary").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
	$("#amountValueNew").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
}

function enableAlterSave(){
	 $("#alterSave").removeAttr("disabled");
}

function refreshRowDetail(){
	if(rowUpdateIndex > -1 ){
		var id = $('#jqxgrid').jqxGrid('getrowid', rowUpdateIndex);
		 if($("#jqxgridDetail_" + id).length){
			 $("#jqxgridDetail_" + id).jqxGrid('updatebounddata');	 
		 }	
	}
}

</script>
<div class="row-fluid">
	<div id="appendNotification">
		<div id="updateNotificationSalary">
			<span id="notificationText"></span>
		</div>
	</div>	
</div>	
<#--<!-- removeUrl="jqxGeneralServicer?sname=deleteEmpPosTypeSalary&jqaction=D" deleteColumn="emplPositionTypeId;periodTypeId;fromDate(java.sql.Timestamp)" -->
<#--<!-- updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmpPosTypeSalary" editmode="selectedcell"
editColumns="emplPositionTypeId;periodTypeId;rateAmount(java.math.BigDecimal);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" -->	
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.SettingEmpPosTypeSalary}</h4>
		<div class="widget-toolbar none-content">
			<button id="addNew" class="grid-action-button icon-plus-sign">${uiLabelMap.accAddNewRow}</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span2' style="text-align: center;">
							<b>${uiLabelMap.Time}</b>
						</div>
						<div class="span7">
							<div id="dateTimeInput"></div>						
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
					 filterable="true" alternativeAddPopup="popupWindowEmplPosTypeRate"
					 deleterow="false" editable="false" addrow="true"
					 url="jqxGeneralServicer?hasrequest=Y&sname=JQListEmplPositionTypeRate" id="jqxgrid" 
					 createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplPositionTypeRateAmount" functionAfterUpdate="refreshRowDetail()" functionAfterAddRow="enableAlterSave()"
					 addColumns="periodTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);rateAmount;emplPositionTypeId;includeGeoId;excludeGeoId;roleTypeGroupId" 
					 selectionmode="singlerow" addrefresh="true" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>
					
<div class="row-fluid">
	<div id="popupWindowEmplPosTypeRate" class="hide">
		<div id="EmplPosTypeRateWindowHeader">
			 ${uiLabelMap.AddEmpPosTypeSalary}
		</div>
		<div class="form-window-container" id="EmplPosTypeRateWindowContent">
			<div class='form-window-content'>
				<form id="createEmplPosTypeRateForm">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<div id="setSalaryEmplPositionTypeId">
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="control-label asterisk">${uiLabelMap.HRCommonChannel}</label>
						</div>
						<div class="span7">
							<div id="roleTypeGroupDropDown"></div>
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
							<div id="periodTypeNew"></div>
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
							<label class="control-label">${uiLabelMap.DAAmount}</label>
						</div>
						<div class="span7">
							<div id="amountValueNew"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="alterCancel" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>	

<div id="editEmplPosTypeRateWindow" class="hide">
	<div id="windowHeader">
     	${uiLabelMap.EditEmpPosTypeRate}
     </div>	
     <div class='form-window-container' id="windowContent">
     	<div class='form-window-content'>
   			<form method="post" id="editEmplPosTypeRateForm">
   				<input type="hidden" name="emplPositionTypeRateId" id="emplPositionTypeRateId">
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="control-label">${uiLabelMap.EmplPositionTypeId}</label>
   					</div>
   					<div class="span7">
   						<div id="emplPositionTypeIdEdit"></div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.HRCommonChannel}</label>     					
   					</div>
					<div class="span7">
						<div id="roleTypeGroupEdit"></div>						
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
						<label class="control-label">${uiLabelMap.PayrollFromDate}</label>	     					
   					</div>
   					<div class="span7">
   						<div id='payrollFromDate'>
      					</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="control-label">${uiLabelMap.CommonThruDate}</label>
   					</div>
   					<div class="span7">
   						<div id='payrollThruDate'>
      					</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
					<label class="control-label">${uiLabelMap.CurrencyUomId}</label>     					
   					</div>
   					<div class="span7">
   						<div id="CurrencyUomIdSalary">
   						</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="control-label">${uiLabelMap.PeriodTypePayroll}</label>
   					</div>
   					<div class="span7">
   						<div id="periodTypeSalary"></div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
					<label class="control-label">${uiLabelMap.DAAmount}</label>     					
   					</div>
   					<div class="span7">
   						<div id="amountSalary">
   						</div>
   					</div>
   				</div>
   			</form>
     	</div>
     	<div class="form-action">
     		<button id="cancelSubmit" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.CommonCancel}</button>
     		<button id="submitForm" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSave}</button>
     	</div>	
     </div>
</div>	
