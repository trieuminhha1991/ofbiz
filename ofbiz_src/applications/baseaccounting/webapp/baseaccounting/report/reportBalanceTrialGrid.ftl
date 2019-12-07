<script>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var customtoolbaraction = function(toolbar){
		  toolbar.append('<div  class="pull-right margin-top5" id="customTimePeriods"></div>');
		  $("#customTimePeriods").jqxDropDownList({source: customTimePeriods, valueMember: 'customTimePeriodId', displayMember: 'description', height: 24, theme: theme, width: 300});
		  var selectedPeriodId = '${parameters.customTimePeriodId?if_exists}'; 
		  if (selectedPeriodId == undefined || selectedPeriodId == null || selectedPeriodId == '') {
			  accutils.setValueDropDownListOnly($('#customTimePeriods'), '${customTimePeriodDefault.customTimePeriodId?if_exists}', 'customTimePeriodId', customTimePeriods);
		  } else {
			  accutils.setValueDropDownListOnly($('#customTimePeriods'), selectedPeriodId, 'customTimePeriodId', customTimePeriods);
		  }
		  
		  $('#customTimePeriods').on('select', function (event) {
		        var args = event.args;
		        var customTimePeriodId = $('#customTimePeriods').jqxDropDownList('getItem', args.index).value;
		        if (customTimePeriodId != null) {
					window.location.replace('<@ofbizUrl>GlAccountTrialBalance?organizationPartyId=${userLogin.lastOrg}&customTimePeriodId=' + customTimePeriodId + '</@ofbizUrl>');
		        }
		  });
		  return toolbar;
	 }
	
	function getData(){
		var data = new Array();
		var submitData = {};
		submitData['organizationPartyId'] = '${parameters.organizationPartyId?if_exists}';
		var selectedPeriodId = '${parameters.customTimePeriodId?if_exists}'; 
		if (selectedPeriodId == undefined || selectedPeriodId == null || selectedPeriodId == '') {
			submitData['customTimePeriodId'] = '${customTimePeriodDefaultId}';
		} else {
			submitData['customTimePeriodId'] = selectedPeriodId;
		}
		//Send Ajax Request
		$.ajax({
			url: 'getListTrialBalanceAccount',
			type: "POST",
			data: submitData,
			dataType: 'json',
			success : function(rep) {
				data = rep['listBal'];
				renderGridData(data);
				Loading.hide('loadingMacro');
				$('#jqxgrid').show();
			},
			beforeSend: function(){
				$('#jqxgrid').hide();
				Loading.show('loadingMacro');
			}
		});
		return data;
	}
	
	var renderGridData = function(data){
		var grid = $('#jqxgrid');
		var adapter = grid.jqxGrid('source');
		if(adapter){
			adapter.localdata = data;
			adapter._source.localdata = data;
			grid.jqxGrid('source', adapter);
		}
	};
	
	var cellclass = function (row, columnfield, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		if(data != undefined && data){
			if (data.isLeaf == 'Y') {
	            return 'leaf';
	        } else if (data.isLeaf == 'N') {
	            return 'brand';
	        }
		}
    }
	
	var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		var rows = $('#jqxgrid').jqxGrid('getrows');
		if(data != undefined && data && rows && rows != undefined){
			value = getAmount(columnfield, data, rows)
			return '<span style=\"text-align: right;\">' + formatcurrency(value.toFixed(2)) + '</span>';
		}
    }
	
	function getAmount(columnfield, bal, data){
		var amount = bal[columnfield];
		var listChild = getChild(bal, data);
		if(listChild.length != 0){
			for(var i = 0; i < listChild.length; i++){
				amount += getAmount(columnfield, listChild[i], data);
			}
		}
		return amount;
	}
	
	function getChild(bal, data){
		var listChild = new Array();
		var index = 0;
		for(var i = 0 ; i < data.length; i++){
			if(data[i].parentId == bal.glAccountId){
				listChild[index++] = data[i];
			}
		}
		return listChild;
	}
</script>

<style>
	.brand {
		font-weight: 600;
		font-size: 105%;
	}
</style>

<#assign dataField="[{ name: 'glAccountId', type: 'string'},
					 { name: 'accountName', type: 'string'},
					 { name: 'isLeaf', type: 'string'},
					 { name: 'parentId', type: 'string'},
					 { name: 'openingCrBalance', type: 'number'},
					 { name: 'openingDrBalance', type: 'number'},
					 { name: 'endingCrBalance', type: 'number'},
					 { name: 'endingDrBalance', type: 'number'},
					 { name: 'postedDebits', type: 'number'},
					 { name: 'postedCredits', type: 'number'}
				]"/>	

<#assign columnlist="{ text: '${uiLabelMap.BACCAccountCode}', dataField: 'glAccountId', width: 150, cellclassname: cellclass},
              		 { text: '${uiLabelMap.BACCAccountName}', dataField: 'accountName', width : 250, cellclassname: cellclass},
              		 { text: '${uiLabelMap.BACCDebit}', width : 150, dataField: 'openingDrBalance', filtertype : 'number', filterable : false, 
              		 	columngroup: 'openingBalance', cellsrenderer: cellsrenderer, cellclassname: cellclass
              		 },
              		 { text: '${uiLabelMap.BACCCredit}', width : 150, dataField: 'openingCrBalance', filtertype : 'number', filterable : false, 
              		 	columngroup: 'openingBalance', cellsrenderer: cellsrenderer, cellclassname: cellclass
               		 },
               		 { text: '${uiLabelMap.BACCDebit}', width : 150, dataField: 'postedDebits', filtertype : 'number', filterable : false, 
               		 	columngroup: 'postedAmount', cellsrenderer: cellsrenderer, cellclassname: cellclass
              		 },
              		 { text: '${uiLabelMap.BACCCredit}', dataField: 'postedCredits', width : 150 , filtertype : 'number', filterable : false, 
              		 	columngroup: 'postedAmount', cellsrenderer: cellsrenderer, cellclassname: cellclass
              		 },
              		 { text: '${uiLabelMap.BACCDebit}', width : 150, dataField: 'endingDrBalance', filtertype : 'number', filterable : false, 
              		 	columngroup: 'endingBalance', cellsrenderer: cellsrenderer, cellclassname: cellclass
               		 },
              		 { text: '${uiLabelMap.BACCCredit}', dataField: 'endingCrBalance', width : 150, filtertype : 'number', filterable : false, 
              		 	columngroup: 'endingBalance', cellsrenderer: cellsrenderer, cellclassname: cellclass
              		 }
                  "/>
                  
<#assign columngrouplist="{ text: '${uiLabelMap.BACCOpeningBalance}', name: 'openingBalance', align: 'center'},
      		 			  { text: '${uiLabelMap.BACCEndingBalance}', name: 'endingBalance', align: 'center'},
      		 			  { text: '${uiLabelMap.BACCPostedAmount}', name: 'postedAmount', align: 'center'}
              		">
              		
<@jqGrid dataField=dataField columnlist=columnlist filterable="false" filtersimplemode="false" usecurrencyfunction="true" url=""
		customcontrol1="fa fa-file-excel-o@@javascript: void(0);@exportExcel()" sortable="false"
		id="jqxgrid" columngrouplist=columngrouplist customtoolbaraction="customtoolbaraction" virtualmode="false" localdata="getData()"/>

<script type="text/javascript">
	var exportExcel = function(){
		var allData = $('#jqxgrid').jqxGrid('getrows');
		if(allData.length > 0){
 		    var organizationPartyId = '${parameters.organizationPartyId?if_exists}';
			var selectedPeriodId = '${parameters.customTimePeriodId?if_exists}'; 
			var customTimePeriodId;
			if (selectedPeriodId == undefined || selectedPeriodId == null || selectedPeriodId == '') {
				customTimePeriodId = '${customTimePeriodDefaultId}';
			} else {
				customTimePeriodId = selectedPeriodId;
			}
 		    
			var url = "exportGlAccountTrialBalanceExcel?organizationPartyId=" + organizationPartyId + "&customTimePeriodId=" + customTimePeriodId;
			window.open(url, "_blank");
		} else {
    		bootbox.alert("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}");
    	}
	};
</script>