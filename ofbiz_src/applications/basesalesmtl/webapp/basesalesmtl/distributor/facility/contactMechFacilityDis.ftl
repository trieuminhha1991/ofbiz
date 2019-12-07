<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
<style>
#myImg { 
	position : fixed;
	left: 654px;
	top: 266px;
	visibility: hidden;
}
</style>
<script>
	var facilityId = '${parameters.facilityId}';
	var contactMechTypeData = new Array();
	<#assign listContactMechType = delegator.findList("ContactMechType", null, null, null, null, false) />
	<#list listContactMechType as contactMechType>
			var row = {};
			row['contactMechTypeId'] = "${contactMechType.contactMechTypeId}";
			row['description'] = "${contactMechType.get('description', locale)?if_exists}";
			contactMechTypeData[${contactMechType_index}] = row;
	</#list>
	
	<#assign geoList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var geoData = new Array();
	<#list geoList as geo>
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName?if_exists}";
		geoData[${geo_index}] = row;
	</#list>
	
	function getGeoId(geoId) {
		for ( var x in geoData) {
			if (geoId == geoData[x].geoId) {
				return geoData[x].geoName;
			}
		}
	}
	
	<#assign geoDataList = delegator.findList("Geo", null, null, null, null, false) />
	var geoDataByData = new Array();
	<#list geoDataList as geo>
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName?if_exists}";
		geoDataByData[${geo_index}] = row;
	</#list>
	
	function getGeoIdByGeoDistrict(geoId) {
		for ( var x in geoDataByData) {
			if (geoId == geoDataByData[x].geoId) {
				return geoDataByData[x].geoName;
			}
		}
	}
	
	<#assign contactMechPurposeTypeList = delegator.findList("ContactMechPurposeType", null, null, null, null, false) />
	var contactMechPurposeTypeData = new Array();
	<#list contactMechPurposeTypeList as contactMechPurposeType>
		var row = {};
		row['contactMechPurposeTypeId'] = "${contactMechPurposeType.contactMechPurposeTypeId}";
		row['description'] = "${StringUtil.wrapString(contactMechPurposeType.get('description', locale)?if_exists)}";
		contactMechPurposeTypeData[${contactMechPurposeType_index}] = row;
	</#list>
	function getContactMechPurposeTypeId(contactMechPurposeTypeId) {
		for ( var x in contactMechPurposeTypeData) {
			if (contactMechPurposeTypeId == contactMechPurposeTypeData[x].contactMechPurposeTypeId) {
				var value = contactMechPurposeTypeData[x].description;
				return value;
			}
		}
	}
	function getContactMechTypeId(contactMechTypeId) {
		for ( var x in contactMechTypeData) {
			if (contactMechTypeId == contactMechTypeData[x].contactMechTypeId) {
				return contactMechTypeData[x].description;
			}
		}
	}
	
	var getLocalization = function () {
	     var localizationobj = {};
	     localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
	     localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
	     localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
	     localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	     localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	     localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	     localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	     localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	     localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	     localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	     localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	     localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	     localizationobj.firstDay = 1;
	     localizationobj.percentsymbol = "%";
	     localizationobj.currencysymbol = "đ";
	     localizationobj.decimalseparator = ",";
	     localizationobj.thousandsseparator = ".";
	     <#if defaultOrganizationPartyCurrencyUomId?has_content>
	     	<#if defaultOrganizationPartyCurrencyUomId == "USD">
	     		localizationobj.currencysymbol = "$";
	     		localizationobj.decimalseparator = ".";
	     		localizationobj.thousandsseparator = ",";
    		<#elseif defaultOrganizationPartyCurrencyUomId == "EUR">
    			localizationobj.currencysymbol = "€";
    			localizationobj.decimalseparator = ".";
	     		localizationobj.thousandsseparator = ",";
	     	</#if>
	     </#if>
	     localizationobj.currencysymbolposition = "after";
	     
	     var days = {
	         // full day names
	         names: ["${StringUtil.wrapString(uiLabelMap.wgmonday)}", "${StringUtil.wrapString(uiLabelMap.wgtuesday)}", "${StringUtil.wrapString(uiLabelMap.wgwednesday)}", "${StringUtil.wrapString(uiLabelMap.wgthursday)}", "${StringUtil.wrapString(uiLabelMap.wgfriday)}", "${StringUtil.wrapString(uiLabelMap.wgsaturday)}", "${StringUtil.wrapString(uiLabelMap.wgsunday)}"],
	         // abbreviated day names
	         namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgamonday)}", "${StringUtil.wrapString(uiLabelMap.wgatuesday)}", "${StringUtil.wrapString(uiLabelMap.wgawednesday)}", "${StringUtil.wrapString(uiLabelMap.wgathursday)}", "${StringUtil.wrapString(uiLabelMap.wgafriday)}", "${StringUtil.wrapString(uiLabelMap.wgasaturday)}", "${StringUtil.wrapString(uiLabelMap.wgasunday)}"],
	         // shortest day names
	         namesShort: ["${StringUtil.wrapString(uiLabelMap.wgsmonday)}", "${StringUtil.wrapString(uiLabelMap.wgstuesday)}", "${StringUtil.wrapString(uiLabelMap.wgswednesday)}", "${StringUtil.wrapString(uiLabelMap.wgsthursday)}", "${StringUtil.wrapString(uiLabelMap.wgsfriday)}", "${StringUtil.wrapString(uiLabelMap.wgssaturday)}", "${StringUtil.wrapString(uiLabelMap.wgssunday)}"],
	     };
	     localizationobj.days = days;
	     var months = {
	         // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
	         names: ["${StringUtil.wrapString(uiLabelMap.wgjanuary)}", "${StringUtil.wrapString(uiLabelMap.wgfebruary)}", "${StringUtil.wrapString(uiLabelMap.wgmarch)}", "${StringUtil.wrapString(uiLabelMap.wgapril)}", "${StringUtil.wrapString(uiLabelMap.wgmay)}", "${StringUtil.wrapString(uiLabelMap.wgjune)}", "${StringUtil.wrapString(uiLabelMap.wgjuly)}", "${StringUtil.wrapString(uiLabelMap.wgaugust)}", "${StringUtil.wrapString(uiLabelMap.wgseptember)}", "${StringUtil.wrapString(uiLabelMap.wgoctober)}", "${StringUtil.wrapString(uiLabelMap.wgnovember)}", "${StringUtil.wrapString(uiLabelMap.wgdecember)}", ""],
	         // abbreviated month names
	         namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgajanuary)}", "${StringUtil.wrapString(uiLabelMap.wgafebruary)}", "${StringUtil.wrapString(uiLabelMap.wgamarch)}", "${StringUtil.wrapString(uiLabelMap.wgaapril)}", "${StringUtil.wrapString(uiLabelMap.wgamay)}", "${StringUtil.wrapString(uiLabelMap.wgajune)}", "${StringUtil.wrapString(uiLabelMap.wgajuly)}", "${StringUtil.wrapString(uiLabelMap.wgaaugust)}", "${StringUtil.wrapString(uiLabelMap.wgaseptember)}", "${StringUtil.wrapString(uiLabelMap.wgaoctober)}", "${StringUtil.wrapString(uiLabelMap.wganovember)}", "${StringUtil.wrapString(uiLabelMap.wgadecember)}", ""],
	     };
	     var patterns = {
	        d: "dd/MM/yyyy",
			D: "dd MMMM yyyy",
			f: "dd MMMM yyyy h:mm tt",
			F: "dd MMMM yyyy h:mm:ss tt",
			M: "dd MMMM",
			Y: "MMMM yyyy"
	     }
	     localizationobj.patterns = patterns;
	     localizationobj.months = months;
	     localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	     localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	     return localizationobj;
	 }
	
</script>	

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
		if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
			return 'Not Data';
		}
		var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
	    var orders = ordersDataAdapter.records;
	    
			var nestedGrids = new Array();
	        var id = datarecord.uid.toString();
	        
	         var grid = $($(parentElement).children()[0]);
	         $(grid).attr(\"id\",\"jqxgridDetail\");
	         nestedGrids[index] = grid;
	       
	         var ordersbyid = [];
	        
	         for (var ii = 0; ii < orders.length; ii++) {
	                 ordersbyid.push(orders[ii]);
	         }
	         var contactMechTypeId = datarecord.contactMechTypeId;
	         var orderssource = { 
	        		 datafields: [	
     		         	 { name: \'contactMechId\', type:\'string\' },
     		         	 { name: \'contactMechPurpuseTypeId\', type:\'string\' },
     		             { name: \'infoString\', type:\'string\' }, 
     		         	 { name: \'address1\', type:\'string\' },
     		         	 { name: \'toName\', type:\'string\' },
     		         	 { name: \'countryCode\', type:\'string\' },
        		         { name: \'areaCode\', type:\'string\' },
     		         	 { name: \'contactNumber\', type:\'string\' },
     		         	 { name: \'attnName\', type:\'string\' },
        		         { name: \'address2\', type:\'string\' },
        		         { name: \'postalCode\', type:\'string\' },
        		         { name: \'countryGeoId\', type:\'string\' },
        		         { name: \'stateProvinceGeoId\', type:\'string\' },
        		         { name: \'extendsion\', type:\'string\' },
     	         	 ],
     	             localdata: ordersbyid
 	    	 };
    	 	    	     
	         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
             if(contactMechTypeId == 'POSTAL_ADDRESS'){
            	 if (grid != null) {
    	             grid.jqxGrid({
    	                 source: nestedGridAdapter, 
    	                 width: '98%',
    	                 height: 180,
    	                 showtoolbar:true,
    			 		 editable:false,
    			 		 editmode:\"click\",
    			 		 theme: 'olbius',
    			 		 showheader: true,
    			 		 localization: getLocalization(),
    			 		 selectionmode:\"singlerow\",
    			 		 pageable: true,
    			 		 columns: 
    			 	     [
    			 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true, width: \'200\',
    	                	   cellsrenderer: function(row, colum, value){
    	                		   var data = grid.jqxGrid('getrowdata', row);
    	       					   var value = data.contactMechPurpuseTypeId;
    	       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
    	       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
    	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.Address} 1\', datafield: \'address1\',editable: true,},
    	                    { text: \'${uiLabelMap.National}\', datafield: \'countyGeoId\',editable: true,
    	                    	cellsrenderer: function(row, colum, value){
     	                		   var data = grid.jqxGrid('getrowdata', row);
     	       					   var countryGeoIdValue = data.countryGeoId;
     	       					   if(countryGeoIdValue != null){
     	       						   var countyGeoId = getGeoId(countryGeoIdValue);
     	       						   return '<span>' + countyGeoId + '</span>';
     	       					   }
     	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.Provinces}\', datafield: \'stateProvinceGeoId\',editable: true, width: \'150\',
    	                    	cellsrenderer: function(row, colum, value){
     	                		   var data = grid.jqxGrid('getrowdata', row);
     	       					   var stateProvinceGeoIdValue = data.stateProvinceGeoId;
     	       					   if(stateProvinceGeoIdValue != null){
     	       						   var stateProvinceGeoId = getGeoIdByGeoDistrict(stateProvinceGeoIdValue);
     	       						   return '<span>' + stateProvinceGeoId + '</span>';
     	       					   }
     	       				    }, 
    	                    },
    	                 ]
    	             });
    	         }
  	         }
     		 if(contactMechTypeId == 'EMAIL_ADDRESS'){
     			if (grid != null) {
		             grid.jqxGrid({
		                 source: nestedGridAdapter,
		                 width: '98%',
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 localization: getLocalization(),
				 		 
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.Email}\', datafield: \'infoString\',editable: true},
		                 ]
		             });
   	             }
     		 }
     		 if(contactMechTypeId == 'TELECOM_NUMBER'){
     			if (grid != null) {
		             grid.jqxGrid({
		                 source: nestedGridAdapter, 
		                 width: '98%',
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 localization: getLocalization(),
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true, width: 300,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.CountryCode}\', datafield: \'countryCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.AreaCode}\', datafield: \'areaCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.PhoneNumber}\', datafield: \'contactNumber\',editable: true},
		                 ]
		             });
  	             }
     		 }
	 }"/>


		<#assign dataField="[
			{ name: 'contactMechTypeId', type: 'string'},
			{ name: 'rowDetail', type: 'string' }
		]"/>
		<#assign columnlist="
		{ text: '${uiLabelMap.ContactMechType}', datafield: 'contactMechTypeId', filtertype: 'checkedlist', editable:false,
				cellsrenderer: function(row, colum, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					var contactMechTypeId = data.contactMechTypeId;
					var contactMechTypeId = getContactMechTypeId(contactMechTypeId);
					return '<span>' + contactMechTypeId + '</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
	        		widget.jqxDropDownList({ selectedIndex: 0,  source: contactMechTypeData, displayMember: 'contactMechTypeId', valueMember: 'contactMechTypeId',
	                	renderer: function (index, label, value) {
		                    var datarecord = contactMechTypeData[index];
		                    return datarecord.description;
		                }
	                });
	        	},
		}
		
		"/>
		
		<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true"  dataField=dataField columnlist=columnlist editmode="click"
			url="jqxGeneralServicer?sname=getFacilityContactMech&facilityId=${parameters.facilityId}"
			usecurrencyfunction="true"
			initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"
		/>