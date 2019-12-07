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
<script type="text/javascript">
	jQuery(document).ready(function() {
	    if (jQuery('#${dependentForm}').length) {
	      jQuery("#${dependentForm}_${mainId}").change(function(e, data) {
	          getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}');
	      });
	      getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
	    }
	})
</script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var tabsdiv = null;
    var information = null;
    var notes = null;
    tabsdiv = $($(parentElement).children()[0]);
    if (tabsdiv != null) {
        information = tabsdiv.find('.information');
        notes = tabsdiv.find('.notes');
        var title = tabsdiv.find('.title');
        title.text(\"${StringUtil.wrapString(uiLabelMap.DAListSalesman)}\");
        
        var container = $('<div style=\"margin: 5px;\"></div>')
        container.appendTo($(information));
        
        var photocolumn = $('<div style=\"float: left; width: 100%;\"></div>');
        container.append(photocolumn);
        
        var loadingStr = '<div id=\"info_loader_' + index + '\" style=\"overflow: hidden; position: absolute; display: none; left: 45%; top: 25%;\" class=\"jqx-rc-all jqx-rc-all-olbius\">';
        loadingStr += '<div style=\"z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;\" ';
        loadingStr += ' class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
        loadingStr += '<div style=\"float: left;\"><div style=\"float: left; overflow: hidden; width: 32px; height: 32px;\" class=\"jqx-grid-load\"></div>';
        loadingStr += '<span style=\"margin-top: 10px; float: left; display: block; margin-left: 5px;\">${uiLabelMap.DALoading}...</span></div></div></div>';
        var notescontainer = $(loadingStr);
        $(notes).append(notescontainer);
        
        var partyId = datarecord.partyId;
	
		var urlStr = 'jqxGeneralServicer?sname=JQGetListSalesmanByDist&partyId=' + partyId;
		var id = datarecord.uid.toString();
	    var grid = $(photocolumn);
	    //$(grid).attr(\"id\",productId+\"jqxgridDetail\");
	 	var sourceRowDetail = {
	    	datafields: [{ name: 'partyId', type: 'string' },
				        { name: 'statusId', type: 'string' },
				        { name: 'firstName', type: 'number' },
				        { name: 'middleName', type: 'number' },
				        { name: 'lastName', type: 'string' },
	    			],
	    	cache: false,
	    	root: 'results',
	   	 	datatype: 'json',
	    	updaterow: function (rowid, rowdata) {},
	   	 	beforeprocessing: function (data) {},
	    	pager: function (pagenum, pagesize, oldpagenum) {},
	    	type: 'POST',
		    data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		    },
	    	pagesize:5,
	    	contentType: 'application/x-www-form-urlencoded',
	    	url: urlStr,
	 	};
	    var dataAdapterRowDetail = new $.jqx.dataAdapter(sourceRowDetail);
	    if (grid != null) {
	        grid.jqxGrid({
	         source: dataAdapterRowDetail, 
	         width: '100%', 
	         height: 130,
	         showtoolbar:false,
	 		 editable:false,
	 		 editmode:'click',
	 		 showheader: true,
	 		 selectionmode:'singlecell',
	 		 theme: 'energyblue',
	 		 pageable: true,
	 		 pagesizeoptions: ['3', '5', '10', '20', '30'],
	 		 pagesize: 3,
	         columns: [{ text: '${uiLabelMap.DASeqId}', dataField: '', width: '70px', columntype: 'number', 
	                      cellsrenderer: function (row, column, value) {
	                          return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
                          }
	                  },
		              { text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '16%'},	
		              { text: '${uiLabelMap.DAStatus}', datafield: 'statusId', width: '16%'},
		              { text: '${uiLabelMap.DAFirstName}', datafield: 'firstName'},
		              { text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName'},
		              { text: '${uiLabelMap.DALastName}', datafield: 'lastName'},
	           		]
	        });
	   	}
        
        
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
			        	var partyIdDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DADistributorId)}:</b> ' + partyIdQ + '</div>';
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
			        		var div2 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAAddress)}: ' + postalAddressQItem.address1 + '</div>';
			        		$(column2).append(div2);
			        		var div3 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountry)}: ' + postalAddressQItem.countryGeoId + '</div>';
			        		$(column2).append(div3);
			        		var div4 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAStateProvince)}: ' + postalAddressQItem.stateProvinceGeoId + '</div>';
			        		$(column2).append(div4);
			        		var div5 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountyGeoId)}: ' + postalAddressQItem.countyGeoId + '</div>';
			        		$(column2).append(div5);
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
					 { name: 'groupName', type: 'string'},
					 { name: 'address', type: 'string'},
					 { name: 'phone', type: 'string'},
					 { name: 'email', type: 'string'},
					 { name: 'officeSiteName', type: 'string'},
					 { name: 'countryGeoId', type: 'string'},
					 { name: 'stateProvinceGeoId', type: 'string'}, 
					 { name: 'countyGeoId', type: 'string'},
					 { name: 'preferredCurrencyUomId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'createdDate', type: 'date', other:'Timestamp'},
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'toName', type: 'string'},
					 {name: 'rowDetail', type: 'string'}
					 ]"/>
<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.DADistributorId)}', width:'12%', datafield: 'partyId'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield: 'groupName'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DACurrencyUomId)}', width:'7%', datafield: 'preferredCurrencyUomId'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', width:'12%', datafield: 'statusId'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', width:'12%', datafield: 'description'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DACreatedDate)}', width:'12%', datafield: 'createdDate', cellsformat: 'dd/MM/yyyy'},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DACreatedBy)}', width:'12%', datafield: 'createdByUserLogin'}
					 	"/>
<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li>${StringUtil.wrapString(uiLabelMap.DAGeneralInformation)}</li><li class='title'></li></ul><div class='notes'></div><div class='information'></div>"/>

<#assign addrow = "false"/>
<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR_NEW", "")>
	<#assign addrow = "true"/>
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListDistributor" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" 
		 mouseRightMenu="true" contextMenuId="contextMenu" rowdetailstemplateAdvance=rowdetailstemplateAdvance  defaultSortColumn="createdDate" sortdirection="desc" 
		 addrow="${addrow}" createUrl="jqxGeneralServicer?sname=createPartyDistributor&jqaction=C" addrefresh="true" rowdetailsheight="200" 
		 addColumns="partyId;organizationalUnitName;parentOrgId;functions;officeSiteName;currencyUomId;description;address1;emailAddress;countryGeoId;stateProvinceGeoId;countyGeoId;useForShippingAddress;countryCode;areaCode;contactNumber;userLoginIdStr;currentPassword;currentPasswordVerify;passwordHint;requirePasswordChange;partyId" 
		 />

<div id='contextMenu'>
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="icon-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.DAEdit)}</li>
	</ul>
</div>

<div id="popupEditDistributor" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="CreateMemberForm" class="form-horizontal">
			<input type="hidden" value="${parameters.roleTypeGroupId?if_exists}" id="roleTypeGroupId" name="roleTypeGroupId" />
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<div class="row-fluid no-left-margin">
							<label class="span5 align-right asterisk">${uiLabelMap.DADistributorId}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<input id="DistributorIdEdit" />
							</div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<div class="row-fluid no-left-margin">
							<label class="span5 align-right asterisk">${uiLabelMap.DADistributorName}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<input id="DistributorNameEdit" />
							</div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<div class="row-fluid no-left-margin">
							<label class="span5 align-right asterisk">${uiLabelMap.Functions}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<input id="FunctionsEdit" />
							</div>
						</div>
					</div>
					
					
				</div>
				<div class="span6"></div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterEdit" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-pencil'></i> ${uiLabelMap.Edit}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	//Create Theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAEdit)}") {
        	var wtmp = window;
    	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
    	   	var tmpwidth = $('#popupEditDistributor').jqxWindow('width');
    	   	$('#popupEditDistributor').jqxWindow({ width: 800, height : 600,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
    	   	$("#popupEditDistributor").jqxWindow('open');
    	   	$("#DistributorIdEdit").jqxInput({height: 19, width: 193, minLength: 1, });
    	   	$("#DistributorNameEdit").jqxInput({height: 19, width: 193, minLength: 1, });
    	   	$("#FunctionsEdit").jqxInput({height: 19, width: 193, minLength: 1, });
        }
    });
    
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
</script>
<#--
<#assign orgUnitLevels = delegator.findByAnd("RoleType", {"parentTypeId" : "ORGANIZATION_UNIT"}, null, false)/>
<#assign partyRoles = Static["com.olbius.util.SecurityUtil"].getCurrentRoles(parameters.parentOrgId, delegator)/>-->
<div id="alterpopupWindow" style="display:none">
    <div>${uiLabelMap.DACreateNewDistributor}</div>
    <div style="overflow: hidden;">
    	<form name="editOrganizationalUnit" id="editOrganizationalUnit" method="post" action="<@ofbizUrl>createOrganizationalUnit</@ofbizUrl>">
    		<input type="hidden" name="statusIdAdd" id="statusIdAdd" value="AGREEMENT_CREATED"/>
	        <table class="table-left-width250" style="width:100%">
	        	<tr>
	    	 		<td align="right">${uiLabelMap.DADistributorId}</td>
		 			<td align="left" class="td-left">
	 					<input type="text" name="partyId" id="partyId"/>
		 			</td>
		 			
		 			<td align="right" class="required">${uiLabelMap.DABelongsToSUP}</td>
		 			<td align="left" class="td-left">
		 				<@htmlTemplate.lookupField formName="editOrganizationalUnit" name="parentOrgId" id="parentOrgId" fieldFormName="LookupPartyGroup" value="${parameters.parentOrgId?if_exists}"/>
		 			</td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="right" class="required">${uiLabelMap.DADistributorName}</td>
		 			<td align="left" class="td-left">
	 					<input type="text" name="organizationalUnitName" id="organizationalUnitName"/>
		 			</td>
		 			
		 			<td align="right">${uiLabelMap.OfficeSiteName}</td>
		 			<td align="left" class="td-left"><input type="text" name="officeSiteName" id="officeSiteName"/></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="right">${uiLabelMap.Functions}</td>
		 			<td align="left" class="td-left">
		 				<input type="text" name="functions" id="functions"/>
		 			</td>
		 			
		 			<td align="right">${uiLabelMap.DADescription}</td>
		 			<td align="left" class="td-left">
		 				<input type="text" id="description" name="description" value=""/>
		 			</td>
	    	 	</tr>
	    	 	<tr>
					<td align="right">${uiLabelMap.DACurrencyUomId}</td>
		 			<td align="left" class="td-left">
		 				<#assign currencyUomId = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator) />
		 				<#assign currencies = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, false)/>
						<select id="currencyUomId" name="currencyUomId" style="background-color:#f5f5f5">
			              	<option value=""></option>
			              	<#list currencies as currency>
			              	<option value="${currency.uomId}" <#if currencyUomId?default('VND') == currency.uomId>selected="selected"</#if>>
			              		${currency.uomId}
		              		</option>
			              	</#list>
			            </select>
					</td>
		 			
		 			<td align="right"></td>
		 			<td align="left" class="td-left"></td>
	    	 	</tr>
	    	 	<#--
	    	 	<tr>
					<td align="right">${uiLabelMap.OrganizationUnitLevel}</td>
					<td align="left" class="td-left">
						<select id="orgUnitLevel" name="orgUnitLevel">
							<#if orgUnitLevels?exists>
								<#list orgUnitLevels as orgUnitLevel>
									<option value="${orgUnitLevel.roleTypeId}">${orgUnitLevel.description}</option>
								</#list>
							</#if>
						</select>
					</td>
					<td align="right">${uiLabelMap.OrganizationUnitLevel}</td>
					<td align="left" class="td-left">
						<input id="parentRoleTypeId" name="parentRoleTypeId" value=""/>
						<select id="parentRoleTypeId" name="parentRoleTypeId">
							<#list partyRoles as roleType>
								<#assign roleTypeGv = delegator.findOne("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", roleType), false)>
								<option value="${roleTypeGv.roleTypeId}">${roleTypeGv.description}</option>
							</#list>
						</select>
					</td>
	    	 	</tr>-->
	    	 	<tr style="border-top: 1px solid #ddd; padding-top: 15px;">
	    	 		<td align="right">${uiLabelMap.PartyAddressLine}</td>
		 			<td align="left" class="td-left"><input type="text" name="address1" id="address1"/></td>
		 			
					<td align="right">${uiLabelMap.EmailAddress}</td>
		 			<td align="left" class="td-left"><input type="text" size="60" maxlength="255" name="emailAddress" id="emailAddress" /></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="right">${uiLabelMap.CommonCountry}</td>
		 			<td align="left" class="td-left">
						<select name="countryGeoId" id="editOrganizationalUnit_countryGeoId" onchange="searchCountry()">
							${screens.render("component://common/widget/CommonScreens.xml#countries")}        
							<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
							<option selected="selected" value="${defaultCountryGeoId}">
								<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
								${countryGeo.get("geoName",locale)}
							</option>
						</select>
					</td>
		 			
					<td align="right">${uiLabelMap.PartyPhoneNumber}</td>
		 			<td align="left" class="td-left">
		 				<input type="tel" size="4" maxlength="10" name="countryCode" style="width: 30px" id="countryCode" />
						<b>-</b>&nbsp;<input type="text" size="4" maxlength="10"  name="areaCode" id="areaCode" style="width: 30px"/>
						<b>-</b>&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" style="width: 96px"/>
		 			</td>
	    	 	</tr>
	    	 	<tr>
					<td align="right">${uiLabelMap.PartyState}</td>
		 			<td align="left" class="td-left">
						<select name="stateProvinceGeoId" id="editOrganizationalUnit_stateProvinceGeoId">
						</select>
					</td>
		 			
		 			<td align="right">${uiLabelMap.DAUseForShippingAddress}</td>
		 			<td align="left" class="td-left">
		 				<select name="useForShippingAddress" id="useForShippingAddress">
							<option value="Y" selected="selected">${uiLabelMap.CommonY}</option>
							<option value="N">${uiLabelMap.CommonN}</option>
						</select>
		 			</td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="right">${uiLabelMap.DACountyGeoId}</td>
		 			<td align="left" class="td-left">
						<select name="countyGeoId" id="editOrganizationalUnit_countyGeoId">
							<#list counties as county>
								<option value="${county.geoId}">${county.geoName}</option>
							</#list>
						</select>
					</td>
					
					<td align="right"></td>
		 			<td align="left" class="td-left"></td>
	    	 	</tr>
	    	 	<tr style="border-top: 1px solid #ddd; padding-top: 15px;">
	    	 		<td align="right" class="required">${uiLabelMap.UserLoginID}</td>
		 			<td align="left" class="td-left"><input type="text" id="userLoginIdStr" name="userLoginIdStr" /></td>
		 			
					<td align="right" class="required">${uiLabelMap.CurrentPassword}</td>
		 			<td align="left" class="td-left"><input type="password" id="currentPassword" name="currentPassword" /></td>
	    	 	</tr>
	    	 	<tr>
	    	 		<td align="right" class="required">${uiLabelMap.CurrentPasswordVerify}</td>
		 			<td align="left" class="td-left"><input type="password" id="currentPasswordVerify" name="currentPasswordVerify" /></td>
		 			
					<td align="right">${uiLabelMap.PasswordHint}</td>
		 			<td align="left" class="td-left"><input type="text" id="passwordHint" name="passwordHint" /></td>
	    	 	</tr>
	    	 	<#--
	    	 	<tr>
	    	 		<td align="right" class="required">${uiLabelMap.DAAbbRequirePasswordChange}</td>
		 			<td align="left" class="td-left">
		 				<select name="requirePasswordChange" id="requirePasswordChange">
							<option value="Y">${uiLabelMap.CommonY}</option>
							<option value="N" selected="selected">${uiLabelMap.CommonN}</option>
						</select>
		 			</td>
		 			
					<td align="right"></td>
		 			<td align="left" class="td-left"></td>
	    	 	</tr>
	    	 	-->
	            <tr>
	            	<td align="right"></td>
		 			<td align="left"></td>
		 			
	                <td align="right">
	                	<input type="button" id="alterSave" value="${uiLabelMap.CommonSave}" />
	                </td>
	                <td align="left">
	                	<input type="button" id="alterCancel" value="${uiLabelMap.CommonCancel}" />
	                </td>
	            </tr>
	        </table>
    	</form>
    </div>
</div>

<script type="text/javascript">
	//Create alterpopupWindow
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 410, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
    $("#alterCancel").jqxButton({width: 100});
    $("#alterSave").jqxButton({width: 100});
    
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { partyId:$('#partyId').val(),
        		organizationalUnitName:$('#organizationalUnitName').val(),
        		parentOrgId:$(input[name='parentOrgId']).val(),
        		functions:$('#functions').val(),
        		officeSiteName:$('#officeSiteName').val(),
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
</script>