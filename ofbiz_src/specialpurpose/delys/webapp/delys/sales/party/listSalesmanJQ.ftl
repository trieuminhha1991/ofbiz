<style type="text/css">
	#jqxgrid .jqx-tabs-headerWrapper.jqx-tabs-header {
		height:25px;
	}
	#jqxgrid .jqx-tabs-headerWrapper.jqx-tabs-header ul.jqx-tabs-title-container {
		height: 25px;
	}
	#jqxgrid .jqx-tabs-headerWrapper.jqx-tabs-header ul.jqx-tabs-title-container li.jqx-tabs-title {
		height: 13px;
	}
	#jqxgrid .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-grid.jqx-widget {
		border:none;
	}
	#jqxgrid .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-grid-header {
		border-width: 1px 1px 1px 1px;
	}
	#jqxgrid .jqx-widget-content .jqx-tabs .jqx-tabs-content-element .jqx-widget-content .jqx-grid-cell{
		border-width: 0px 1px 1px 1px;
	}
	.jqx-window-olbius .jqx-window-content table.table-left-width250 tr td.td-left {
	  	width: 250px;
	  	min-width: 250px;
	  	max-width: 250px;
	}
	.ui-dialog.ui-widget.ui-widget-content {
		z-index:18005 !important;
	}
	.ui-widget-overlay {
		z-index:18004 !important;
	}
	.ui-datepicker#ui-datepicker-div {
		z-index:18006 !important;
	}
</style>
<#assign dependentForm = "editOrganizationalUnit"/>
<#assign mainId = "countryGeoId"/>
<#assign requestName = "getAssociatedStateList"/>
<#assign paramKey = "countryGeoId"/>
<#assign dependentId = "stateProvinceGeoId"/>
<#assign responseName = "stateList"/>
<#assign dependentKeyName = "geoId"/>
<#assign descName = "geoName"/>
<#assign selectedDependentOption = "mechMap.postalAddress.stateProvinceGeoId"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var tabsdiv = null;
    var information = null;
    var notes = null;
    tabsdiv = $($(parentElement).children()[0]);
    if (tabsdiv != null) {
        notes = tabsdiv.find('.notes');
        
        var loadingStr = '<div id=\"info_loader_' + index + '\" style=\"overflow: hidden; position: absolute; display: none; left: 45%; top: 25%;\" class=\"jqx-rc-all jqx-rc-all-olbius\">';
        loadingStr += '<div style=\"z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;\" ';
        loadingStr += ' class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
        loadingStr += '<div style=\"float: left;\"><div style=\"float: left; overflow: hidden; width: 32px; height: 32px;\" class=\"jqx-grid-load\"></div>';
        loadingStr += '<span style=\"margin-top: 10px; float: left; display: block; margin-left: 5px;\">${uiLabelMap.DALoading}...</span></div></div></div>';
        var notescontainer = $(loadingStr);
        $(notes).append(notescontainer);
        
        var partyId = datarecord.partyId;
        
        $(tabsdiv).jqxTabs({ theme: 'energyblue', width: '96%', height: 170});
        
        var loadPage = function (url, tabClass, data, index) {
            $.ajax({
			  	type: 'POST',
			  	url: url,
			  	data: data,
			  	dataType: 'json',
			  	beforeSend: function () {
					$(\"#info_loader_\" + index).show();
				}, 
				success: function(data){
					//$('.' + tabClass).text(data);
					var tabActive = tabsdiv.find('.' + tabClass);
					var container2 = $('<div style=\"margin: 5px;\"></div>')
			        container2.appendTo($(tabActive));
			        
			        var column1 = $('<div style=\"float: left; width: 40%;\"></div>');
			        var column2 = $('<div style=\"float: left; width: 60%;\"></div>');
			        container2.append(column1);
			        container2.append(column2);
			        
			        var partyIdQ = data.partyId;
			        var partyNameViewQ = data.partyNameView;
			        var postalAddressQ = data.postalAddress;
			        var phoneQ = data.phone;
			        var emailQ = data.email;
			        
			        if (partyIdQ != undefined) {
			        	var partyIdDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DASalesmanId)}:</b> ' + partyIdQ + '</div>';
			        	$(column1).append(partyIdDiv);
			        }
			        if (phoneQ != undefined) {
			        	var phoneDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAPhone)}:</b> ' + phoneQ + '</div>';
			        	$(column1).append(phoneDiv);
			        }
                    if (emailQ != undefined) {
			        	var emailDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAEmail)}:</b> ' + emailQ + '</div>';
			        	$(column1).append(emailDiv);
			        }
			        if (postalAddressQ != undefined) {
			        	for (var i = 0; i < postalAddressQ.length; i++) {
			        		var postalAddressQItem = postalAddressQ[i];
			        		var div1 = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAAddress)} ' + (i + 1) + ':</b></div>';
			        		$(column2).append(div1);
			        		if(postalAddressQItem.address1 != null){
			        			var div2 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAAddress)}: ' + postalAddressQItem.address1 + '</div>';
				        		$(column2).append(div2);
			        		}
			        		else {
			        			var div2 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAAddress)}: ' + '</div>';
			        			$(column2).append(div2);
			        		}
			        		if(postalAddressQItem.countryGeoId != null){
			        			var div3 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountry)}: ' + postalAddressQItem.countryGeoId + '</div>';
				        		$(column2).append(div3);
			        		}
			        		else{
			        			var div3 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountry)}: ' + '</div>';
			        			$(column2).append(div3);
			        		} 
			        		if(postalAddressQItem.stateProvinceGeoId != null){
			        			var div4 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAStateProvince)}: ' + postalAddressQItem.stateProvinceGeoId + '</div>';
				        		$(column2).append(div4);
			        		}
			        		else{
			        			var div4 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAStateProvince)}: ' +'</div>';
			        			$(column2).append(div4);
			        		}
			        		if(postalAddressQItem.countyGeoId != null){
			        			var div5 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountyGeoId)}: ' + postalAddressQItem.countyGeoId + '</div>';
				        		$(column2).append(div5); 	
			        		}
			        		else{
			        			var div5 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountyGeoId)}: ' + '</div>';
				        		$(column2).append(div5); 
			        		}
			        	}
			        }
				},
				error: function(){
				}, 
	            complete: function() {
			        $(\"#info_loader_\" + index).hide();
			    }
			});
        }
        loadPage('getGeneralInformationOfParty', 'notes', {'partyId' : partyId}, index);
        /*
        $('#jqxTabs').on('selected', function (event) {
            var pageIndex = event.args.item + 1;
            loadPage('pages/ajax' + pageIndex + '.htm', pageIndex);
        });
        */
    }
 }"/>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'fullName', type: 'string'},
					 { name: 'address', type: 'string'},
					 { name: 'phone', type: 'string'},
					 { name: 'email', type: 'string'},
					 { name: 'birthDate', type: 'date'},
					 { name: 'countryGeoId', type: 'string'},
					 { name: 'stateProvinceGeoId', type: 'string'}, 
					 { name: 'countyGeoId', type: 'string'},
					 { name: 'preferredCurrencyUomId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'createdDate', type: 'date', other:'Timestamp'},
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'toName', type: 'string'},
					 {name: 'rowDetail', type: 'string'},
					 {name: 'supName', type: 'string'},
					 {name: 'asmName', type: 'string'}
					 ]"/>
					 <#--
					 { text: '${StringUtil.wrapString(uiLabelMap.DACurrencyUomId)}', width:'7%', datafield: 'preferredCurrencyUomId'},
					 { text: '${StringUtil.wrapString(uiLabelMap.DACreatedBy)}', width:'12%', datafield: 'createdByUserLogin'}
					 { text: '${StringUtil.wrapString(uiLabelMap.DACreatedDate)}', width:'10%', datafield: 'createdDate', cellsformat: 'dd/MM/yyyy'},
					 -->
<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', width:'8%', datafield: 'partyId'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield: 'fullName'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DABirthday)}', width:'10%', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DASUP)}', width:'14%', datafield: 'supName'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DAASM)}', width:'14%', datafield: 'asmName'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', width:'12%', datafield: 'statusId'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', width:'12%', datafield: 'description'}
					 	"/>
<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li>${StringUtil.wrapString(uiLabelMap.DAGeneralInformation)}</li></ul><div class='notes'></div>"/>
<#assign addrow = "false"/>
<#if security.hasPermission("PARTYSALESMAN_CREATE", session) || security.hasPermission("PARTYSALESMAN_ADMIN", session)>
	<#assign addrow = "true"/>
</#if>
<#assign customcontrol1 = ""/>
<#if security.hasPermission("PARTYSALESMAN_ADD", session) || security.hasPermission("PARTYSALESMAN_SUP", session)>
	<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.DANewSalesMan}@javascript: void(0)@popupCreateSalesman()"/>
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListSalesman" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" deleterow="true"
		 mouseRightMenu="true" contextMenuId="contextMenu" rowdetailstemplateAdvance=rowdetailstemplateAdvance 
		 addrow=addrow createUrl="jqxGeneralServicer?sname=createPartySalesman&jqaction=C" addrefresh="true" 
		 addColumns="partyId;firstName;lastName;middleName;gender;roleTypeId;parentOrgId;functions;birthDate(java.sql.Timestamp);currencyUomId;description;address1;emailAddress;countryGeoId;stateProvinceGeoId;countyGeoId;useForShippingAddress;countryCode;areaCode;contactNumber;userLoginIdStr;currentPassword;currentPasswordVerify;passwordHint;requirePasswordChange;partyId"
		 removeUrl="jqxGeneralServicer?sname=removePartySalesman&jqaction=C" deleteColumn="partyId"
		 customcontrol1 = customcontrol1
		 />

<div id='contextMenu'>
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DARouteSchedule)}</li>
	    <li><i class="fa fa-bus"></i>${StringUtil.wrapString(uiLabelMap.DATransferRouteForAnotherSalesman)}</li>
	</ul>
</div>
<script type="text/javascript">
	//Create Theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	<#assign listDayOfWeek = delegator.findList("DayOfWeek",null,null,null,null,false) !>
	var listDay = [
	       		<#list listDayOfWeek as day>
	       			{
	       				dayId : '${day.dayOfWeek?if_exists}',
	       				description : '${StringUtil.wrapString(day.description?default(''))}'
	       			},
	       		</#list>
	       	];
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
        if(tmpKey == "${StringUtil.wrapString(uiLabelMap.DARouteSchedule)}"){
        	$("#alterpopupWindowSalesmanDetail").jqxWindow('open');
        	var partyId = data.partyId;
        	var sourceSalesmanDetails = {
    				type : "POST",
    				datatype : "json",
    				url : "JQGetRouteDetailsOfSalesman",
    				data : {
    					partyId : partyId
    				},
    				dataField : [
    		             {name : "routeId", type : "string"},
    		             {name : "description", type : "string"},
    		             {name : "scheduleRoute", type : "string"}
    	             ]
    			};
    		var dataAdapterSalesmanDetails = new $.jqx.dataAdapter(sourceSalesmanDetails, {autobind : true});
    		$("#GridSalesmanDetail").jqxGrid({
    			width:'100%',
    			filterable :true,
    			editable: true,
    			pageable : true,
    			autoheight : true,
    			columnsresize: true,
    			source : dataAdapterSalesmanDetails,
    			columns : [
    	           {text : "${uiLabelMap.DARouteId}", datafield : "routeId", width : "25%"},
    	           {text : "${uiLabelMap.DARouteName}", datafield : "description", width : "40%"},
    	           {text : "${uiLabelMap.DARouteSchedule}",datafield : "scheduleRoute",cellsrenderer : function(row,columnfield,value){
    			 		var data = $("#GridSalesmanDetail").jqxGrid("getrowdata",row);
    					 		var scheduleElement;
    					 		if(data.scheduleRoute){
    					 			scheduleElement = (data.scheduleRoute).split(',');
    					 		}
    					 		var routeScheduleStr = '';
    					 		for(var i = 0 ;i < listDay.length;i++){
    					 			for(var e in scheduleElement){
    						 			if(listDay[i].dayId == scheduleElement[e]){
    						 				if(listDay[i].description !== 'undefined' ){
    						 					routeScheduleStr +=' - ' +  listDay[i].description + ' - ';
    						 					break;
    						 				}
    						 			}
    					 			}
    					 		}
    					 		return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
    					 	 }}
    	       ]
    		});
        }
        if(tmpKey == "${StringUtil.wrapString(uiLabelMap.DATransferRouteForAnotherSalesman)}"){
        	$("#alterpopupWindowRouteTransfer").jqxWindow('open');
        	var partyId = data.partyId;
        	var sourceSalesmanRouteDetails = {
    				type : "POST",
    				datatype : "json",
    				url : "JQGetRouteDetailsOfSalesman",
    				data : {
    					partyId : partyId
    				},
    				dataField : [
    		             {name : "routeId", type : "string"},
    		             {name : "description", type : "string"},
    		             {name : "scheduleRoute", type : "string"}
    	             ]
    			};
    		var dataAdapterSalesmanRouteDetails = new $.jqx.dataAdapter(sourceSalesmanRouteDetails, {autobind : true});
    		$("#GridRouteTransfer").jqxGrid('source',dataAdapterSalesmanRouteDetails);
        	var sourceListSalesmanToTransfer = {
        		type : "POST",
    			datatype : "json",
    			url : "JQGetListSalesmanToTransfer",
    			data : {
    				partyId : partyId
    			},
        		dataField : [
	              {name : "partyId", type : "string"},
	              {name : "fullName", type : "string"}
              ]
        	};
        	var dataAdapterListSalesmanToTransfer = new $.jqx.dataAdapter(sourceListSalesmanToTransfer, {autobind : true});
        	$("#GridListSalesmanToTransfer").jqxGrid('source',dataAdapterListSalesmanToTransfer);
        	$("#GridListSalesmanToTransfer").on('rowselect', function(event){
        		 var args = event.args;
        		 var row = $("#GridListSalesmanToTransfer").jqxGrid('getrowdata', args.rowindex);
        		 var partyId = row.partyId;
        		 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] +  '</div>';
        		 $("#ListSalesmanToTransfer").jqxDropDownButton('setContent', dropDownContent);
        		 var sourceSalesmanToTransfer = {
        					type : "POST",
        					datatype : "json",
        					url : "JQGetRouteDetailsOfSalesman",
        					data : {
        						partyId : partyId
        					},
        					dataField : [
        			             {name : "routeId", type : "string"},
        			             {name : "description", type : "string"},
        			             {name : "scheduleRoute", type : "string"}
        		           ]
        			 };
    			var dataAdapterSalesmanToTransfer = new $.jqx.dataAdapter(sourceSalesmanToTransfer, {autobind : true});
    			$("#GridSalesmanToTransfer").jqxGrid('source',dataAdapterSalesmanToTransfer);
    		 });
        }
    });
	<#assign currencyUomId = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator) />
	<#assign currencies = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, false)/>
	<#if currencies?exists>
		var dataCurrcies = [
              <#list currencies as currency>
              	{
              		uomId : '${currency.uomId?if_exists}',
              	},
              </#list>
          ]
	</#if>
	var sourceCurrencies = {
			localdata : dataCurrcies,
			datatype : "array",
			dataField : [
	             {name : 'uomId', type :'string'}
             ]
	};
	var dataAdapterCurrencies = new $.jqx.dataAdapter(sourceCurrencies, {autobind: true});
	<#assign roleTypes = delegator.findByAnd("RoleType", {"parentTypeId" : "DELYS_SALESMAN"}, null, false)/>
	<#if roleTypes?exists>
		var dataRoleTypes = [
             <#list roleTypes as roleType>
             	{
             		roleTypeId : '${roleType.roeTypeId?if_exists}',
             		description : '${roleType.description?if_exists}'
             	},
             </#list>
         ]
	</#if>
	var sourceRoleTypes = {
			localdata : dataRoleTypes,
			datatype : "array",
			dataFields : [
	              {name:"roleTypeId", type:"string"},
	              {name:"description", type:"string"}
              ]
	};
	var dataAdapterRoleTypes = new $.jqx.dataAdapter(sourceRoleTypes, {autobind:true});
	<#assign countries = Static["org.ofbiz.common.CommonWorkers"].getCountryList(delegator)>
	<#if countries?exists>
	var dataCountryGeos = [
       <#list countries as country>
           {
        	   countryGeoId : '${country.geoId?if_exists}',
               geoName : '${country.geoName?if_exists}'
           },
       </#list>
       ];
	</#if>
	<#assign countries = Static["org.ofbiz.common.CommonWorkers"].getCountryList(delegator) !>
	var countryCodeData = new Array();
	<#if countries?exists>
		<#list countries as country >
			var row = {};
			row['geoId'] = '${country.geoId?if_exists}';
			row['codeNumber'] = '${country.codeNumber?if_exists}';
			countryCodeData[${country_index}] = row;
		</#list>
	</#if>
	
	<#assign provinces = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "PROVINCE"), null, null, null, false) !>
	var areaCodeData = new Array();
	<#if provinces?exists>
		<#list provinces as province >
			var row = {};
			row['geoId'] = '${province.geoId?if_exists}';
			row['codeNumber'] = '${province.codeNumber?if_exists}';
			areaCodeData[${province_index}] = row;
		</#list>
	</#if>
	
	function searchCountry(){
		var countryGeoId = $("#editOrganizationalUnit_countryGeoId").val();
		for (var i = 0; i < countryCodeData.length; i++){
			if (countryGeoId==countryCodeData[i].geoId){
				$("#countryCode").val(countryCodeData[i].codeNumber);
			}
		}
	}
	
	<#assign counties = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "DISTRICT"), null, null, null, false) !>
	<#if counties?exists>
		var dataCounties = [
			<#list counties as county>
				{
					countyGeoId : '${county.geoId?if_exists}',
					geoName : '${county.geoName?if_exists}'
				},
			</#list>
        ]
	</#if>
	var sourceCounties = {
			localdata : dataCounties,
			datatype : "array",
			datafields : [
              {name : "countyGeoId", type: "String"},
              {name: "geoName", type :"String"}
          ]
	};
	var dataAdapterCounties = new $.jqx.dataAdapter(sourceCounties, {autobind :true}); 
	var dataGender = [
          {
        	  value : 'M',
        	  description : '${StringUtil.wrapString(uiLabelMap.CommonMale)}'
          },
          {
        	  value : 'F',
        	  description : '${StringUtil.wrapString(uiLabelMap.CommonFemale)}'
          },
      ];
	var dataUseForShippingAddress = [
          {
        	  value : 'Y',
        	  description : '${uiLabelMap.CommonY}'
          },
          {
        	  value : 'N',
        	  description : '${uiLabelMap.CommonN}'
          },
     ];
	var sourceSalesMan = {
		type : 'POST',
		url : 'getListSalesMan',
		datatype : 'json',
		data : {},
		contentType: 'application/x-www-form-urlencoded',
		datafields : [
          {name : 'partyId', type : 'String'},
          {name : 'fullName', type : 'String'}
      ]
	};
	var dataAdapterSalesMan = new $.jqx.dataAdapter(sourceSalesMan, {autobind: true});
</script>
<#--
<#assign orgUnitLevels = delegator.findByAnd("RoleType", {"parentTypeId" : "ORGANIZATION_UNIT"}, null, false)/>
<#assign partyRoles = Static["com.olbius.util.SecurityUtil"].getCurrentRoles(parameters.parentOrgId, delegator)/>-->
<div id="alterpopupWindow" style="display:none">
<div>${uiLabelMap.DACreateNewSalesman}</div>
	<div style="overflow:hidden;">
		<form name="editOrganizationalUnit" class="form-horizontal" id="editOrganizationalUnit" method="post" action="<@ofbizUrl>createOrganizationalUnit</@ofbizUrl>">
			<div class="row-fluid form-window-content">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class='span5 align-right asterisk'>
							${uiLabelMap.DASalesmanId}
				        </div>
						<div class="span7">
							<input type="text" id="partyId"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DAFirstName}
						</div>
						<div class="span7">
							<input type="text" id="firstName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DALastName}
						</div>
						<div class="span7">
							<input type="text" id="lastName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.Functions}
						</div>
						<div class="span7">
							<input type="text" id="functions"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DACurrencyUomId}
						</div>
						<div class="span7">
							<div id="currencyUomId"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DARoleTypeId}
						</div>
						<div class="span7">
							<div id="roleTypeId"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.PartyAddressLine}
						</div>
						<div class="span7">
							<input type="text" id="address1"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.CommonCountry}
						</div>
						<div class="span7">
							<div id="editOrganizationalUnit_countryGeoId" name="countryGeoId"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.PartyState}
						</div>
						<div class="span7">
							<div id="editOrganizationalUnit_stateProvinceGeoId"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DACountyGeoId}
						</div>
						<div class="span7">
							<div id="editOrganizationalUnit_countyGeoId"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.UserLoginID}
						</div>
						<div class="span7">
							<input type="text" id="userLoginIdStr"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.CurrentPasswordVerify}
						</div>
						<div class="span7">
							<input type="text" id="currentPasswordVerify"></input>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DABelongsToSUP}
						</div>
						<div class="span7">
							<div id="parentOrgId">
								<div id="jqxParentOrgId"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DABirthday}
						</div>
						<div class="span7">
							<div id="birthDate"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DAMiddleName}
						</div>
						<div class="span7">
							<input type="text" id="middleName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DADescription}
						</div>
						<div class="span7">
							<input type="text" id="description"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.gender}
						</div>
						<div class="span7">
							<div id="gender"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.EmailAddress}
						</div>
						<div class="span7">
							<input type="text" id="emailAddress"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.PartyPhoneNumber}
						</div>
						<div class="span7">
							<input type="tel" size="4" maxlength="10" name="countryCode" style="width: 30px" id="countryCode" />
							<b>-</b>&nbsp;<input type="text" size="4" maxlength="10"  name="areaCode" id="areaCode" style="width: 30px"/>
							<b>-</b>&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" style="width: 96px"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DAUseForShippingAddress}
						</div>
						<div class="span7">
							<div id="useForShippingAddress"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.CurrentPassword}
						</div>
						<div class="span7">
							<input type="password" id="currentPassword"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.PasswordHint}
						</div>
						<div class="span7">
							<input type="text" id="passwordHint"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.PasswordHint}
						</div>
						<div class="span7">
							<input type="text" id="passwordHint"></input>
						</div>
					</div>
				</div>	
			</div>
		</form>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="notificationsuccess" style="display:none;">${uiLabelMap.DACreateSuccessful}</div>
<div id="notificationerror" style="display:none;">${uiLabelMap.DArequiredValueGreaterThanFromDate}</div>
<div id="alterpopupWindowCreatSalesman" style="display:none;">
	<div>${uiLabelMap.DAAddNewSalesman}</div>
		<div style="overflow:hidden;">
			<form id="alterpopupWindowCreatSalesmanForm" class="form-horizontal">
				<div class="row-fluid form-window-content">
					<div class="span12">
						<div class="row-fluid margin-bottom10">
							<div id="msg"></div>
							<div id="GridSalesman"></div>
						</div>
					</div>
				</div>
			</form>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
					</div>
				</div>
			</div>
		</div>
</div>
<div id="alterpopupWindowSalesmanDetail" style="display:none;">
	<div>${uiLabelMap.DARouteSchedule}</div>
	<div style="overflow:hidden;">
		<form id="alterpopupWindowSalesmanDetailForm" class="form-horizontal">
			<div class="row-fluid form-window-content">
				<div class="span12">
					<div class="row-fluid margin-bottom10">
						<div id="msg"></div>
						<div id="GridSalesmanDetail"></div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<button id="cancelRtSalemanDetail" class='btn btn-danger form-action-button pull-right'"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
		</div>
	</div>
</div>
<div id="alterpopupWindowRouteTransfer" style="display:none;">
	<div>${uiLabelMap.DARouteSchedule}</div>
	<div style="overflow:hidden;">
		<form id="alterpopupWindowRouteTransfer" class="form-horizontal">
			<div class="row-fluid form-window-content">
				<div class="span12">
					<div class="row-fluid margin-bottom10">
						<div id="msgRouteTransfer"></div>
						<div id="GridRouteTransfer"></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10" style="margin-left: -19px;">
						<div class="span3 align-right asterisk">
							${uiLabelMap.DALstSalesman}
						</div>
						<div class="span9">
							<div id="ListSalesmanToTransfer">
								<div style="border-color: transparent;" id="GridListSalesmanToTransfer"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="span12" style="margin-left: 0px;">
					<div class="row-fluid margin-bottom10">
						<div id="GridSalesmanToTransfer"></div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<button id="cancelRtRouteTransfer" class='btn btn-danger form-action-button pull-right'"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
			<button id="alterSaveRouteTransfer" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
// create popup RouteTransfers
	var notificationRoute = '';
	$("#alterpopupWindowRouteTransfer").jqxWindow({width : 640, height :480, isModal: true,autoOpen: false, modalOpacity : 0.8})
	$("#cancelRtRouteTransfer").on('click', function(){
		$("#GridSalesmanToTransfer").jqxGrid('clear');
		$("#GridListSalesmanToTransfer").jqxGrid('selectrow', null);
		$("#ListSalesmanToTransfer").jqxDropDownButton('setContent', null);
		$("#alterpopupWindowRouteTransfer").jqxWindow('close');
	});
	var dataFieldRouteTransfer = [
              {name : "routeId", type : "string"},
              {name : "description", type : "string"},
              {name : "scheduleRoute", type : "string"}
      ];
	var columnRouteTransfer = [
               {text : "${uiLabelMap.DARouteId}", width:"25%", datafield : "routeId"},
               {text : "${uiLabelMap.DARouteName}", width:"40%", datafield: "description"},
               {text : "${uiLabelMap.DARouteSchedule}", datafield: "scheduleRoute",cellsrenderer : function(row,columnfield,value){
		 		var data = $("#GridRouteTransfer").jqxGrid("getrowdata",row);
				 		var scheduleElement;
				 		if(data.scheduleRoute){
				 			scheduleElement = (data.scheduleRoute).split(',');
				 		}
				 		var routeScheduleStr = '';
				 		for(var i = 0 ;i < listDay.length;i++){
				 			for(var e in scheduleElement){
					 			if(listDay[i].dayId == scheduleElement[e]){
					 				if(listDay[i].description !== 'undefined' ){
					 					routeScheduleStr +=' - ' +  listDay[i].description + ' - ';
					 					break;
					 				}
					 			}
				 			}
				 		}
				 		return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
				 	 }}
      ];
	GridUtils.initGrid({url : 'JQGetRouteDetailsOfSalesman',width : '100%',showfilterrow : true,editable: true,pageable : true,columnsresize: true,selectionmode : 'checkbox',localization: getLocalization(),autoHeight : true},dataFieldRouteTransfer,columnRouteTransfer,null,$('#GridRouteTransfer'));
	$("#ListSalesmanToTransfer").jqxDropDownButton({width : 125, height : 25});
	var dataFieldListSalesmanToTransfer = [
           {name : "partyId", type : "string"},
           {name : "fullName", type : "string"}
   ];
	var columnListSalesmanToTransfer = [
            {text : "${StringUtil.wrapString(uiLabelMap.DASalesmanId)}",datafield : 'partyId', width : '40%'},
            {text : '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield : 'fullName'}
    ];
	GridUtils.initGrid({url : 'JQGetListSalesmanToTransfer', width : '100%', showfilterrow : true,editable: false,pageable : true,columnsresize: true,localization: getLocalization(),autoHeight : true},dataFieldListSalesmanToTransfer,columnListSalesmanToTransfer,null,$("#GridListSalesmanToTransfer"));
	 var dataFieldSalesmanToTransfer = [
        	{name : "routeId", type : "string"},
        	{name : "description", type : "string"},
        	{name : "scheduleRoute", type : "string"}
    ];
	var columnSalesmanToTransfer = [
            {text : "${uiLabelMap.DARouteId}", datafield : "routeId", width : "25%"},
            {text : "${uiLabelMap.DARouteName}", datafield : "description", width : "40%"},
            {text : "${uiLabelMap.DARouteSchedule}",datafield : "scheduleRoute",cellsrenderer : function(row,columnfield,value){
				var data = $("#GridSalesmanToTransfer").jqxGrid("getrowdata",row);
				 		var scheduleElement;
				 		if(data.scheduleRoute){
				 			scheduleElement = (data.scheduleRoute).split(',');
				 		}
				 		var routeScheduleStr = '';
				 		for(var i = 0 ;i < listDay.length;i++){
				 			for(var e in scheduleElement){
					 			if(listDay[i].dayId == scheduleElement[e]){
					 				if(listDay[i].description !== 'undefined' ){
					 					routeScheduleStr +=' - ' +  listDay[i].description + ' - ';
					 					break;
					 				}
					 			}
				 			}
				 		}
				 		if(data.hasOwnProperty('routeId')){
				 			return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
				 		}else return ''; 
				 	 }}
    ];
	GridUtils.initGrid({url : 'JQGetRouteDetailsOfSalesman', width : '100%', showfilterrow : true,editable: false,pageable : true,columnsresize: true,localization: getLocalization(),autoHeight : true},dataFieldSalesmanToTransfer,columnSalesmanToTransfer,null,$("#GridSalesmanToTransfer"));
	$("#alterSaveRouteTransfer").click(function(){
		var data = new Array();
		var rowSalesmanRouteTransferIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var dataSalesmanRouteTransferIndex = $("#jqxgrid").jqxGrid('getrowdata',rowSalesmanRouteTransferIndex);
		var rowdRouteTransferIndex = $("#GridRouteTransfer").jqxGrid('getselectedrowindex');
		var dataRouteTransfer = $("#GridRouteTransfer").jqxGrid('getrowdata',rowdRouteTransferIndex);
		var routeId = dataRouteTransfer.routeId;
		var row = {
				routeId : routeId,
				partyIdTransfer : dataSalesmanRouteTransferIndex.partyId,
				partyIdToTransfer : $("#ListSalesmanToTransfer").val()
		}
		var rowToAdd = {
				routeId : routeId,
				description : dataRouteTransfer.description,
				scheduleRoute : dataRouteTransfer.scheduleRoute
		}
		if(!dataRouteTransfer.hasOwnProperty('routeId')){
			$("#notificationerror").jqxNotification({width: "100%",appendContainer: "#msgRouteTransfer",opacity: 0.8,autoClose: true,template: "error"});
			$("#notificationerror").text('${StringUtil.wrapString(uiLabelMap.DANoRoute)}');
			$("#notificationerror").jqxNotification('open');
			return false;
		}
		$.ajax({
			type : "POST",
			url : "JQRouteTransfer",
			datatype : "json",
			data : row,
			success : function(response){
				if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_) {
					notificationRoute = response._ERROR_MESSAGE_;
					$("#notificationerror").jqxNotification({width: "100%",appendContainer: "#msgRouteTransfer",opacity: 0.8,autoClose: true,template: "error"});
					$("#notificationerror").text(notificationRoute);
					$("#notificationerror").jqxNotification('open');
					setTimeout(function(){
	   	  				$("#notificationerror").css('display','none');
	   	  			},300);
				}
				else{
					$("#notificationsuccess").jqxNotification({width: "100%",appendContainer: "#msgRouteTransfer",opacity: 0.8,autoClose: true,template: "success"});
					$("#notificationsuccess").text('${StringUtil.wrapString(uiLabelMap.DATransferSuccess)}');
					$("#notificationsuccess").jqxNotification('open');
					$("#GridRouteTransfer").jqxGrid('updatebounddata');
					$("#GridSalesmanToTransfer").jqxGrid('addRow', null, rowToAdd, "first");
//					$("#alterpopupWindowRouteTransfer").jqxWindow('close');
				}
			}
		})
	})
	
// end
// create popup RouteSalesmanDetails
	$("#alterpopupWindowSalesmanDetail").jqxWindow({width : 640, height : 300,isModal: true,autoOpen: false, modalOpacity : 0.8});
	$("#cancelRtSalemanDetail").on('click', function(){
		$("#alterpopupWindowSalesmanDetail").jqxWindow('close');
	})

// end
	//Create alterpopupWindow
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 410, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
    $("#birthDate").jqxDateTimeInput({width: '208px', height: '25px', allowNullDate: true, value: null});
    $("#currencyUomId").jqxComboBox({width: '220px', height: '25px', source: dataAdapterCurrencies, displayMember : 'uomId', valueMember : 'uomId', dropDownHeight : 100});
    $("#roleTypeId").jqxComboBox({width : '220px', height : '25px', source: dataAdapterRoleTypes, displayMember :'description', valueMember : 'roleTypeId', autoDropDownHeight: true});
    $("#editOrganizationalUnit_countryGeoId").jqxComboBox({width : '220px', height:'25px', source: dataCountryGeos, displayMember : 'geoName', valueMember : 'countryGeoId', dropDownHeight: 100});
    $("#editOrganizationalUnit_countryGeoId").on('change', searchCountry());
    $("#editOrganizationalUnit_countryGeoId").on('select', function(event){
    	var tmp = new Array();
    	var obj = new Object();
    	var tmpValue = new Array();
    	var dataStateProvinceGeoId = new Array();
    	 $.ajax({
    			datatype : "json",
    			type : "POST",
    			url : "getAssociatedStateList",
    			data : {
    				countryGeoId : $("#editOrganizationalUnit_countryGeoId").val(),
    				listOrderBy : null,
    			},
    			root : "stateList",
    			async : false,
    			success : function(result){
    				tmp = result.stateList;
    				
    			}
    	});
    	 for(var i=0;i<tmp.length;i++){
    		 tmpValue  = tmp[i].split(": ");
    		 obj = {
    				 'stateProvinceGeoId'  : !tmpValue[1] ? '' : tmpValue[1],
    				'geoName' : !tmpValue[0] ? '' : tmpValue[0]		 
    		 };
    		 dataStateProvinceGeoId.push(obj);
    	 }
    	 $("#editOrganizationalUnit_stateProvinceGeoId").jqxComboBox({source : dataStateProvinceGeoId, displayMember : 'geoName', valueMember : 'stateProvinceGeoId'});
    });
    $("#editOrganizationalUnit_stateProvinceGeoId").jqxComboBox({width: '220px', height:'25px', dropDownHeight : 100, autoDropDownHeight : true});
    $("#editOrganizationalUnit_countyGeoId").jqxComboBox({width: '220px', height:'25px', dropDownHeight : 100, source : dataAdapterCounties, displayMember: 'geoName', valueMember : 'countyGeoId'});
    $("#gender").jqxDropDownList({width: '125px', height :'25px', autoDropDownHeight: true, source : dataGender, displayMember : 'description', valueMember :'value'});
    $("#useForShippingAddress").jqxDropDownList({width: '125px', height :'25px', autoDropDownHeight: true, source : dataUseForShippingAddress, displayMember : 'description', valueMember :'value'})
    
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { partyId:$('#partyId').val(),
        		firstName:$('#firstName').val(),
        		lastName:$('#lastName').val(),
        		middleName:$('#middleName').val(),
        		gender:$('#gender').val(),
        		roleTypeId:$('#roleTypeId').val(),
        		parentOrgId:$('#parentOrgId').val(),
        		functions:$('#functions').val(),
        		birthDate: $('#birthDate').jqxDateTimeInput('getDate'),
        		currencyUomId:$('#currencyUomId').val(),
        		description:$('#description').val(),
        		address1:$('#address1').val(),
        		emailAddress: $('#emailAddress').val(),
        		countryGeoId: $('#editOrganizationalUnit_countryGeoId').val(),
        		stateProvinceGeoId: $('#editOrganizationalUnit_stateProvinceGeoId').val(),
        		useForShippingAddress: $('#useForShippingAddress').val(),
        		countyGeoId: $('#editOrganizationalUnit_countyGeoId').val(),
        		countryCode: $('#countryCode').val(),
        		areaCode:$('#areaCode').val(),
        		contactNumber: $('#contactNumber').val(),
        		userLoginIdStr: $('#userLoginIdStr').val(),
        		currentPassword: $('#currentPassword').val(),
        		currentPasswordVerify: $('#currentPasswordVerify').val(),
        		passwordHint: $('#passwordHint').val(),
        		requirePasswordChange: $('#requirePasswordChange').val(),
        	  };
	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
    
    var sourcePartyFrom = {
				datafields:[{name: 'partyId', type: 'string'},
					   		{name: 'firstName', type: 'string'},
					      	{name: 'lastName', type: 'string'},
					      	{name: 'middleName', type: 'string'},
					      	{name: 'groupName', type: 'string'},
			    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePartyFrom.totalrecords = data.TotalRows;
				},
				filter: function () {
				   	// update the grid and send a request to the server.
				   	$("#jqxParentOrgId").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  	// callback called when a page or page size is changed.
				},
				sort: function () {
				  	$("#jqxParentOrgId").jqxGrid('updatebounddata');
				},
				sortcolumn: 'partyId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListSupervisorDepartmentInRel',
	};
	// create Party From
	$('#parentOrgId').jqxDropDownButton({ width: 200, height: 25});
	<#if strCurrentOrganization?exists && strCurrentOrganization?has_content>
		$('#parentOrgId').jqxDropDownButton({disabled: true});
		$('#parentOrgId').jqxDropDownButton('setContent', '<div style="position: relative; margin-left: 3px; margin-top: 5px;">${strCurrentOrganization}</div>');
	</#if>
	$("#jqxParentOrgId").jqxGrid({
		width:600,
		source: sourcePartyFrom,
		filterable: true,
		virtualmode: true, 
		sortable:true,
	   localization: getLocalization(),
		editable: false,
		autoheight:true,
		pageable: true,
		showfilterrow: true,
		rendergridrows: function(obj) {	
			return obj.data;
		},
		columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '160px'},
					{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '160px'},
					{text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '160px'},
					{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '160px'},
					{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '160px'},
				]
	});
	$("#jqxParentOrgId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxParentOrgId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
        $("#parentOrgId").jqxDropDownButton('setContent', dropDownContent);
    });
//    done alterpopupWindow
	function popupCreateSalesman(){
		$("#alterpopupWindowCreatSalesman").jqxWindow('open');
	}
	$("#alterpopupWindowCreatSalesman").jqxWindow({width : 640, height: 480, resizable : true, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7});
	$("#GridSalesman").jqxGrid({
		width:'100%',
		filterable :true,
		editable: true,
		source : dataAdapterSalesMan,
		pageable : true,
		localization: getLocalization(),
		autoheight : true,
		columnsresize: true,
		selectionmode: 'checkbox',
        altrows: true,
		columns : [
           {text : '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', dataField : 'partyId', width : '20%'},
           {text : '${StringUtil.wrapString(uiLabelMap.DAFullName)}', dataField : 'fullName'},
           {text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField : 'fromDate', cellsformat: 'dd/MM/yyyy HH:mm:ss',width:'25%',columntype:'datetimeinput', createeditor : function(row, cell ,editor){
        	   editor.jqxDateTimeInput({formatString :'dd/MM/yyyy HH:mm:ss',allowNullDate:true,value : null});
           }},
           {text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField : 'thruDate', cellsformat : 'dd/MM/yyyy HH:mm:ss',width : '25%',columntype:'datetimeinput', createeditor : function(row, cell,editor){
        	   editor.jqxDateTimeInput({formatString : 'dd/MM/yyyy HH:mm:ss',allowNullDate:true,value : null});
           }}
       ]
	});
	$("#notificationsuccess").jqxNotification({width: "100%",appendContainer: "#msg",opacity: 0.8,autoClose: true,template: "success"});
	$("#notificationerror").jqxNotification({width: "100%",appendContainer: "#msg",opacity: 0.8,autoClose: true,template: "error"});
//	$("#GridSalesman").on('rowselect', function(){
//		$("#alterSave1")[0].disabled = false;
//	})
	var formatDate = function(val){
		   var date = new Date(val);
		   var newFormat;
		   if(date){
		    newFormat = date.format('yyyy-mm-dd HH:MM:ss');
		   }
		   return newFormat;
		  }
	$("#alterSave1").click(function(){
		var rowindexes = $("#GridSalesman").jqxGrid('getselectedrowindexes');
		if(rowindexes.length == 0){
			$("#notificationerror").empty();
			$("#notificationerror").jqxNotification({template : 'error'});
			$("#notificationerror").text('${StringUtil.wrapString(uiLabelMap.DANotAddSalesmanYet)}');
			$("#notificationerror").jqxNotification('open');
			return false;
		}
		else{
			var datas = new Array();
			var now = new Date();
			var check = true;
			for(var i=0;i<rowindexes.length;i++){
				var data = $("#GridSalesman").jqxGrid('getrowdata',rowindexes[i]);
				datas.push(data);
				if(data.fromDate > data.thruDate){
					check = false;
					break;
				}
			}
			for(var i=0;i<datas.length;i++){
				var now = new Date();
				if(datas[i].fromDate < now){
					$("#notificationerror").empty();
					$("#notificationerror").jqxNotification({template : 'error'});
					$("#notificationerror").text('${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}');
					$("#notificationerror").jqxNotification('open');
					return false;
				}
				if(datas[i].fromDate == null){
					datas[i].fromDate = '${nowTimestamp}';
				}
				if(datas[i].thruDate != null){
					datas[i].thruDate = formatDate(datas[i].thruDate);
				}
				if(datas[i].fromDate != null){
					datas[i].fromDate = formatDate(datas[i].fromDate);
				}
			}
			var tmp = JSON.stringify(datas);
			if(check){
				$.ajax({
					type : "POST",
					url : "createNewSalesman",
					data : {
						listdata : tmp,
					},
					datatype : "JSON",
					success : function(data, status, xhr){
						$("#notificationsuccess").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
						$("#notificationsuccess").jqxNotification('open');
						$("#alterpopupWindowCreatSalesman").jqxWindow('close');
						$("#GridSalesman").jqxGrid('updatebounddata');	
						$("#jqxgrid").jqxGrid('updatebounddata');
					}
				});
			}
			else{
				$("#notificationerror").text('${StringUtil.wrapString(uiLabelMap.DArequiredValueGreaterThanFromDate)}');
				$("#notificationerror").jqxNotification('open');
				return false;
			}
		}
	})
</script>