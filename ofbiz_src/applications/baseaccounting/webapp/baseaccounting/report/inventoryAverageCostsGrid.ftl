<#--Import LIB-->
<@jqGridMinimumLib />
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->

<script>
	//Prepare for customTimePeriod
	var facilities = [
	<#list facilityList as item>
		{
			<#assign description = StringUtil.wrapString(item.facilityName + " [" + item.facilityId + "]" ) />
			facilityId : '${item.facilityId}',
			description : '${description}',
		},
	</#list>
	]
	
	<#if (facilitiesDefault?has_content)>
		<#assign facilitiesDefaultId =  facilitiesDefault.facilityId />
		<#else>
		<#assign facilitiesDefaultId = 'null' />
	</#if>
</script>
<#--===================================/Prepare Data=====================================================-->

<#assign dataField="[
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'totalQuantityOnHand', type: 'number' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'quantityUomDesc', type: 'string' },
					{ name: 'productAverageCost', type: 'number' }, 
					{ name: 'totalInventoryCost', type: 'number' },
					{ name: 'currencyUomId', type: 'string' }
				]"/>	
   
<#assign columnlist="{ text: '${uiLabelMap.AccountingProductId}', dataField: 'productCode', width: '13%'},
					 { text: '${uiLabelMap.BACCProductName}', dataField: 'productName', width: '25%'},
					 { text: '${uiLabelMap.FormFieldTitle_rateCurrencyUomId}', dataField: 'quantityUomDesc', width: '8%'},
					 { text: '${uiLabelMap.ProductAverageCost}', dataField: 'productAverageCost', width: '13%' ,filtertype : 'number',
				 	 	cellsrenderer : function(row, columnfield, value, defaulthtml, columnproperties){
				 			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
				 			if(typeof(data.currencyUomId) != 'undefined'){
								return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';				 			
				 			}
			 		  	}	
			  		 },
					 { text: '${uiLabelMap.BACCTotalQuantityFacility}', dataField: 'totalQuantityOnHand', filtertype : 'number', width: '19%',
						cellsrenderer : function(row, columnfield, value, defaulthtml, columnproperties){
				 			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
				 			if(typeof(value) == 'number'){
								return '<span class=\"align-right\">' + value + '</span>';				 			
				 			}
			 		  	}
					 },
					 { text: '${uiLabelMap.BACCTotalAmountFacility}', dataField: 'totalInventoryCost', width: '22%', filtertype : 'number',
				 		cellsrenderer : function(row, columnfield, value, defaulthtml, columnproperties){
				 			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
				 			if(typeof(data.currencyUomId) != 'undefined'){
				 				return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
				 			}
				  		}
					 }  		  
				"/>		 

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>  		  
<script type="text/javascript">	
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	if (facilitySelected === undefined) var facilitySelected = "${facilitiesDefaultId}";
	var customtoolbaraction = function(toolbar){
		var str = "<div id='facility' class='pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'><div id='jqxgridFacility'></div></div>";
		toolbar.append(str);
		
		//init dropdown button
		$("#facility").jqxDropDownButton({width: 300, dropDownHorizontalAlignment: 'right'}); 
		var descTmp =  "${uiLabelMap.Facility}";
		$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+descTmp+'</div>');
		
		//init facility grid
		var url = "JQGetListFacilities&organizationPartyId=${organizationPartyId?if_exists}&facilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{ name: 'facilityId', type: 'string'},
			{ name: 'facilityName', type: 'string'},
			{ name: 'facilityCode', type: 'string'}
      	];
      	var columnlist = [
			{ text: '${uiLabelMap.FacilityId}', datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
				cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value) + '</div>';
			    }
			},
			{ text: '${uiLabelMap.FacilityName}', datafield: 'facilityName', width: '80%',
				cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value) + '</div>';
			    }
			}
      	];
      	
      	var config = {
  			width: 580, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridFacility"));
      	
      	//init event
      	$('#facility').on('close', function (event){
			if (facilitySelected.length != ""){
				var tmpS = $("#jqxgrid").jqxGrid('source');
			 	tmpS._source.url = "jqxGeneralServicer?sname=getListInventoryAverageCost&facilityId="+facilitySelected;
			 	$("#jqxgrid").jqxGrid('source', tmpS);
			}
		});
		
		$("#jqxgridFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var description = rowData.facilityName + ' ['+rowData.facilityId+']';
	        facilitySelected = rowData.facilityId;
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	        $('#facility').jqxDropDownButton('close');
	    });
		
		return toolbar;
	}
</script>

<@jqGrid url="jqxGeneralServicer?sname=getListInventoryAverageCost&facilityId=${facilitiesDefaultId}" dataField=dataField columnlist=columnlist 
		filterable="true" filtersimplemode="false" usecurrencyfunction="true" clearfilteringbutton="true"  id="jqxgrid" isShowTitleProperty ="true" 
		customtoolbaraction="customtoolbaraction" isSaveFormData="true" formData="filterObjData" jqGridMinimumLibEnable="false"
		customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()" />

<script type="text/javascript">
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgrid").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportInventoryAverageCostExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		if (!_.isEmpty(facilitySelected)) {
			var hiddenField0 = document.createElement("input");
			hiddenField0.setAttribute("type", "hidden");
			hiddenField0.setAttribute("name", "facilityId");
			hiddenField0.setAttribute("value", facilitySelected);
			form.appendChild(hiddenField0);
		}
		
		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField1 = document.createElement("input");
				hiddenField1.setAttribute("type", "hidden");
				hiddenField1.setAttribute("name", key);
				hiddenField1.setAttribute("value", value);
				form.appendChild(hiddenField1);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
	}
</script>		