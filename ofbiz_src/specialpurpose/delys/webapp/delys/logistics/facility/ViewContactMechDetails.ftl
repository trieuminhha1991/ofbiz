<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script src="/delys/images/js/bootbox.min.js"></script>
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
		row['geoName'] = "${geo.geoName}";
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
		row['geoName'] = "${geo.geoName}";
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



<div>
	<div id="contentNotificationContentCreatePostalAddressSuccess">
	</div>

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
    	                 width: 1020,
    	                 height: 180,
    	                 showtoolbar:true,
    			 		 editable:false,
    			 		 editmode:\"click\",
    			 		 theme: 'olbius',
    			 		 showheader: true,
    			 		 localization: getLocalization(),
    			 		 rendertoolbar: function (toolbar) {
    			 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
    	                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.DSEdit}</span></div>\");
    	                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.DSDelete}</span></div>\");
    	                    container.append(editButton);
    	                    container.append(deleteButton);
    	                    toolbar.append(container);
    	                    editButton.jqxButton();
    	                    deleteButton.jqxButton();
    	                    // edit row.
    	                    editButton.click(function (event) {
    	                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
    	                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
    	                        var id = grid.jqxGrid('getrowid', selectedrowindex);
    	                        var data = grid.jqxGrid('getrowdatabyid', id);
    	                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckEditItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var valueContactMechId = data.contactMechId;
                                	loadContactMechDetailByEdit(valueContactMechId);
        	                        grid.jqxGrid('clearselection');
    	                        }
    	                    });
    	                    // delete selected row.
    	                    deleteButton.click(function (event) {
    	                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
    	                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
    	                        var id = grid.jqxGrid('getrowid', selectedrowindex);
    	                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckDeleteItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
        	                        var valueContactMechId = data.contactMechId;
        	                        deleteContactMechInFacility(valueContactMechId);
        	                        grid.jqxGrid('clearselection');
    	                        }
    	                    });
    			 		 },
    			 		 selectionmode:\"singlerow\",
    			 		 pageable: true,
    			 		 columns: 
    			 	     [
    			 	      	
    			 	      	{ text: \'${uiLabelMap.PartyContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true,
    	                	   cellsrenderer: function(row, colum, value){
    	                		   var data = grid.jqxGrid('getrowdata', row);
    	       					   var value = data.contactMechPurpuseTypeId;
    	       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
    	       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
    	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.PartyAddressLine1}\', datafield: \'address1\',editable: true},
    	                    { text: \'${uiLabelMap.PartyAddressLine2}\', datafield: \'address2\',editable: true},
    	                    { text: \'${uiLabelMap.LogContactName}\', datafield: \'attnName\',editable: true},
    	                    { text: \'${uiLabelMap.LogCommonNational}\', datafield: \'countyGeoId\',editable: true,
    	                    	cellsrenderer: function(row, colum, value){
     	                		   var data = grid.jqxGrid('getrowdata', row);
     	       					   var countryGeoIdValue = data.countryGeoId;
     	       					   if(countryGeoIdValue != null){
     	       						   var countyGeoId = getGeoId(countryGeoIdValue);
     	       						   return '<span>' + countyGeoId + '</span>';
     	       					   }
     	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.PartyState}\', datafield: \'stateProvinceGeoId\',editable: true,
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
		                 source: nestedGridAdapter, width: 1020,
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 rendertoolbar: function (toolbar) {
				 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
		                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.DSEdit}</span></div>\");
		                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.DSDelete}</span></div>\");
		                    container.append(editButton);
		                    container.append(deleteButton);
		                    toolbar.append(container);
		                    editButton.jqxButton();
		                    deleteButton.jqxButton();
		                    // edit row.
		                    editButton.click(function (event) {
		                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckEditItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                       	loadContactMechDetailByEdit(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
		                    // delete selected row.
		                    deleteButton.click(function (event) {
		                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckDeleteItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                        deleteContactMechInFacility(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
				 		 },
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.PartyContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.PartyEmailAddress}\', datafield: \'infoString\',editable: true},
		                 ]
		             });
   	             }
     		 }
     		 if(contactMechTypeId == 'TELECOM_NUMBER'){
     			if (grid != null) {
		             grid.jqxGrid({
		                 source: nestedGridAdapter, width: 1020,
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 rendertoolbar: function (toolbar) {
				 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
		                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.DSEdit}</span></div>\");
		                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.DSDelete}</span></div>\");
		                    container.append(editButton);
		                    container.append(deleteButton);
		                    toolbar.append(container);
		                    editButton.jqxButton();
		                    deleteButton.jqxButton();
		                    // edit row.
		                    editButton.click(function (event) {
		                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckEditItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                       	loadContactMechDetailByEdit(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
		                    // delete selected row.
		                    deleteButton.click(function (event) {
		                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.LogCheckDeleteItemInContactMech}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                        deleteContactMechInFacility(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
				 		 },
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.PartyContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true, width: 300,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.CommonCountryCode}\', datafield: \'countryCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.CommonAreaCode}\', datafield: \'areaCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.PartyContactNumber}\', datafield: \'contactNumber\',editable: true},
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
		{ text: '${uiLabelMap.MarketingContactListContactMechTypeId}', datafield: 'contactMechTypeId', filtertype: 'checkedlist', editable:false,
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
		
		<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true" addType="popup" dataField=dataField columnlist=columnlist addrow="true"  editable="true" alternativeAddPopup="alterpopupWindow"  showtoolbar="true" editmode="click"
			url="jqxGeneralServicer?sname=JQXgetContactMechInFacility&facilityId=${parameters.facilityId}"
			customcontrol1="icon-tasks open-sans@${uiLabelMap.CommonList}@listFacilities"	usecurrencyfunction="true"
			initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"
		/>
		
<div id="popupWindownDetail"  class="hide">
	<div>${uiLabelMap.PageTitleNewFacilityContactMech}</div>
	<div id="jqxGirdDetail"></div>
</div>


<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.PageTitleNewFacilityContactMech}</div>
	<div>
		<div id="contentNotificationContentCreatePostalAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreatePhoneNumbersError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternetIPAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateEmailAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateWebURLAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternalNoteviaPartyIdError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateElectronicAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternetDomainNameError" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartySelectContactType)}</label>
			</div>  
			<div class="span7">
				<div id="contactMechTypeId">
				</div>
	   		</div>
	   	</div>
	   	<div>
			<img id="myImg" src="/delys/images/css/import/ajax-loader.gif">
		</div>
		<div id="alterpopupContent">
		</div>
	</div>
</div>

<#include "editContactMechPostalAddress.ftl" />

<div id="jqxNotificationCreatePostalAddressSuccess" >
	<div id="notificationCreatePostalAddressSuccess">
	</div>
</div>

<script type="text/javascript">
		//Create theme
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		$("#alterpopupWindow").jqxExpander({ toggleMode: 'none', showArrow: false });
		var myVar;
		function onLoadData() {
			clearInterval(myVar);
			$("body").css("opacity", 0.2);
			$("#myImg").css({"visibility": "visible", "opacity": 1});
		}
		function onLoadDone() {
			myVar = setInterval(function(){ 
			$("body").css("opacity", 1);
			$("#myImg").css({"visibility": "hidden"});
			 }, 1000);
		}
		$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreatePostalAddressSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 600, minWidth: 400, height: 90 ,width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	    });
		
		function deleteContactMechInFacility(contactMechId){
			if(contactMechId != undefined){
				bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}", function(result) {
		            if(result) {
		            	$.ajax({
							  url: "deleteContactMechInFacility",
							  type: "POST",
							  data: {contactMechId : contactMechId, facilityId: facilityId},
							  dataType: "json",
							  success: function(data) {
							  }
						}).done(function(data) {
							var value = data["value"];
							if(value == "success"){
								$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
								$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
							}
							$('#jqxgrid').jqxGrid('updatebounddata'); 
						});
		            }
				});    
			}
		}
		
		
        $("#popupWindownDetail").jqxWindow({ width: '100%', height: '100%' , resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme });     
		function loadDetailTest(soureDeatilData){
			var sourceData =
		     {
				 localdata: soureDeatilData,
		         datatype: "array",
		         datafields: [
					{ name: 'address1', type: 'string' },
					{ name: 'descriptionContactMechPurpuseType', type: 'string' },
					{ name: 'infoString', type: 'string' },
					{ name: 'toName', type: 'string' },
		         ],
		     };
			var dataAdapter = new $.jqx.dataAdapter(sourceData);
			$("#jqxGirdDetail").jqxGrid(
		    {
		        source: dataAdapter,
		        height: '100%',
		        width: '100%',
		        keyboardnavigation: false,
		        columns: [
		            { text: '${uiLabelMap.PartyContactPurpose}', datafield: 'descriptionContactMechPurpuseType'},
		            { text: '${uiLabelMap.PartyAddressLine1}', datafield: 'address1'},
		            { text: '${uiLabelMap.ElectronicAddress}', datafield: 'infoString'},
		            { text: '${uiLabelMap.FormFieldTitle_toName}', datafield: 'toName'},
		        ]
		    });
			$("#popupWindownDetail").jqxWindow('show');
			
		}
		
		$("#contactMechTypeId").jqxDropDownList({dropDownHeight: 80 ,source: contactMechTypeData, placeHolder: "Please Choose...", displayMember: "description" ,valueMember: "contactMechTypeId"});
		
		$("#contactMechTypeId").on('select', function (event) {
			$('#alterpopupWindow').jqxValidator('hide');
			onLoadData();
			if (event.args) {
	            var item = event.args.item;
	        }
	        var contactMechTypeId = item.value;
	    	
	    	var request = $.ajax({
				  url: "viewContactMechPostalAddressInfo",
				  type: "POST",
				  data: {contactMechTypeId : contactMechTypeId, facilityId: facilityId},
				  dataType: "html",
				  success: function(data) {
					  $("#alterpopupContent").html(data);
				  }
			});
			request.done(function(data) {
				onLoadDone();
			});
		});
		
		$('#alterpopupWindow').on('close', function (event) {
			$("#alterpopupWindow").jqxWindow({
				height: 90           
		    });
			$("#alterpopupContent").html("");
			$('#alterpopupWindow').jqxValidator('hide');
			$("#contactMechTypeId").jqxDropDownList('clearSelection'); 
			$("#contactMechTypeId").jqxDropDownList({placeHolder: "Please Choose..."});
			$("#contactMechTypeId").jqxDropDownList('setContent', 'Please select....');
			$("#contactMechTypeId").jqxDropDownList({ disabled: false }); 
		});
	
</script>