<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#--
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4 class="lighter">${uiLabelMap.DAShowOnlySalesPeriodsWithOrganization}</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>editCustomTimeSalesPeriod</@ofbizUrl>">
				<i class="icon-pencil open-sans">${uiLabelMap.DAEditCustomTimePeriods}</i>
			</a>
			<a href="<@ofbizUrl>newCustomTimeSalesPeriod</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DACreateNewCustomTimePeriod}</i>
			</a>
		</span>
	</div>
	<div class="widget-body">
 		<form method="post" action="<@ofbizUrl>editCustomTimeSalesPeriod</@ofbizUrl>" name="setOrganizationPartyIdForm" class="form-horizontal basic-custom-form">
         	<input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
         	<div class="row">
         		<div class="span11">
         			<div class="control-group">
						<label class="control-label">${uiLabelMap.DAOrganizationId}:</label>
						<div class="controls">
							<@htmlTemplate.lookupField name="findOrganizationPartyId" id="" value='${findOrganizationPartyId?if_exists}' 
										formName="setOrganizationPartyIdForm" fieldFormName="LookupPartyGroupName"/>
							<button class="btn btn-mini btn-primary" type="submit" style="margin-left: 5px">
				         		<i class="icon-ok" ></i>${uiLabelMap.CommonUpdate}
				         	</button>
						</div>
					</div>
         		</div>h
         	</div>
     	</form>
 	</div>
</div>
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
    	<h4>${uiLabelMap.DAListCustomTimePeriod}</h4>
      	<span class="widget-toolbar></span>
      	<br class="clear"/>
    </div>
    <#if customTimePeriods?has_content>
		<div class="widget-body" >
    		<div style="width:100%; ">
		      	<table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
		        	<thead>
		        	<tr class="header-row">
		          		<th>${uiLabelMap.CommonId}</th>
		          		<th>${uiLabelMap.DAParentCustomTimePeriod}</th>
		          		<th nowrap>${uiLabelMap.DAPartyGroupId}</th>
		          		<th>${uiLabelMap.AccountingPeriodType}</th>
		          		<th>${uiLabelMap.CommonNbr}</th>
		          		<th>${uiLabelMap.AccountingPeriodName}</th>
		          		<th>${uiLabelMap.CommonFromDate}</th>
		          		<th>${uiLabelMap.CommonThruDate}</th>
		          		<th>&nbsp;</th>
		        	</tr>
		        	</thead>
		        	<#assign line = 0>
		        	<#list customTimePeriods as customTimePeriod>
		          		<#assign line = line + 1>
		          		<#assign periodType = customTimePeriod.getRelatedOne("PeriodType", true)>
		          		<#assign hasntStarted = false>
		          		<#assign compareDate = customTimePeriod.getDate("fromDate")>
		              	<#assign classNameFromDate = "">
		              	<#assign classNameThruDate = "">
		              	<#if compareDate?has_content>
		                	<#if nowTimestamp.before(compareDate)><#assign hasntStarted = true></#if>
		              	</#if>
		              	<#if hasntStarted>
							<#assign classNameFromDate = "alert">
						</#if>
						<#assign hasExpired = false>
						<#assign compareDate = customTimePeriod.getDate("thruDate")>
		              	<#if compareDate?has_content>
			                <#if nowTimestamp.after(compareDate)><#assign hasExpired = true></#if>
		              	</#if>
		              	<#if hasExpired>
		              		<#assign classNameThruDate = "alert">
		              	</#if>
		          		<tr>
	            			<td><a href="<@ofbizUrl>editCustomTimeSalesPeriod?currentCustomTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>">${customTimePeriod.customTimePeriodId}</a></td>
				            <td><#if currentCustomTimePeriod?exists>${currentCustomTimePeriod.customTimePeriodId}</#if></td>
				            <td>${customTimePeriod.organizationPartyId?if_exists}</td>
				            <td>
			            		<#assign periodObj = customTimePeriod.getRelatedOne("PeriodType", false)!>
			            		<#if periodObj?exists>
			            			${periodObj.description?if_exists}
			            		</#if>
				            </td>
	            			<td>${customTimePeriod.periodNum?if_exists}</td>
	            			<td>${customTimePeriod.periodName?if_exists}</td>
	            			<td no-wrap class="${classNameFromDate?if_exists}">
								<#if customTimePeriod.fromDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimePeriod.fromDate, "dd/MM/yyyy", locale, timeZone)!}</#if>
	            			</td>
	            			<td no-wrap class="${classNameThruDate?if_exists}">
				              	<#if customTimePeriod.thruDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimePeriod.thruDate, "dd/MM/yyyy", locale, timeZone)!}</#if>
	             			</td>
			             	<td>
			              		<div class="hidden-phone visible-desktop btn-group">
									<button type="button" class="btn btn-mini btn-primary" onclick="window.location.href='<@ofbizUrl>editCustomTimeSalesPeriod?currentCustomTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>';" 
										title="${uiLabelMap.DASetAsCurrent}">
										<i class="icon-edit bigger-120"></i>
									</button>
									<button class="btn btn-mini btn-danger" onclick="window.location.href='<@ofbizUrl>deleteCustomTimeSalesPeriod?customTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;currentCustomTimePeriodId=${currentCustomTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>';">
										<i class="icon-trash bigger-120"></i>
									</button>
								</div>
				            </td>
		          		</tr>
		        	</#list>
		      	</table>
      		</div>
  		</div>
    <#else>
      	<div class="widget-body"><p class="alert alert-info">${uiLabelMap.AccountingNoChildPeriodsFound}</p></div>
    </#if>
</div> 
-->

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
<#assign listPeriodType = delegator.findList("PeriodType", null, null, null, null, false) >

//EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("periodTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("SALES%");

var periodTypeData = new Array();
<#list listPeriodType as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['periodTypeId'] = '${item.periodTypeId}';
	row['description'] = '${description}';
	periodTypeData[${item_index}] = row;
</#list>


</script>

<#assign dataField = "[{name: 'customTimePeriodId', type: 'string'}, 
{name: 'parentPeriodId', type: 'string'}, 
{name: 'organizationPartyId', type: 'string'}, 
{name: 'periodTypeId', type: 'string'},
{name: 'periodNum', type: 'number'},
{name: 'periodName', type: 'string'}, 
{name: 'fromDate', type: 'date', other: 'Date'},
{name: 'thruDate', type: 'date', other: 'Date'}

]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.CommonId)}', dataField: 'customTimePeriodId', width: '7%', editable: false,
	cellsrenderer: function(row, colum, value) {
    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    	return \"<span style='color:#08c;cursor:pointer;'>\" + data.customTimePeriodId + \"</span>\";
    }	
}, 
{text: '${StringUtil.wrapString(uiLabelMap.DAParentCustomTimePeriod)}', dataField: 'parentPeriodId',
	cellsrenderer: function(column, row, value){
		for(var i = 0;  i < parentPeriod.length; i++){
			if(parentPeriod[i].parentPeriodId == value){
				return '<span title=' + value + '>' + parentPeriod[i].periodName + ' - ['+ parentPeriod[i].parentPeriodId +']' + '</span>'
			}
		}
		return '<span>' + value + '</span>'
	}
},
{text: '${StringUtil.wrapString(uiLabelMap.DAPartyGroupId)}', dataField: 'organizationPartyId',},
{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodType)}', dataField: 'periodTypeId',  filtertype: 'list', columntype: 'dropdownlist', editable: true,
	cellsrenderer: function(column, row, value){
		for(var i = 0;  i < periodTypeData.length; i++){
			if(periodTypeData[i].periodTypeId == value){
				return '<span title=' + value + '>' + periodTypeData[i].description + '</span>'
			}
		}
		return '<span>' + value + '</span>'
	},
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
        editor.jqxDropDownList({source: periodTypeData, valueMember: 'periodTypeId', displayMember:'description' });
    },
    createfilterwidget: function (column, htmlElement, editor) {
        editor.jqxDropDownList({ source: fixSelectAll(periodTypeData), displayMember: 'description', valueMember: 'periodTypeId' ,
        	renderer: function (index, label, value) {
        		if (index == 0) {
        			return value;
        		}
                for(var i = 0; i < periodTypeData.length; i++){
                	if(value == periodTypeData[i].periodTypeId){
                		return periodTypeData[i].description; 
                	}
                }
            }});
        editor.jqxDropDownList('checkAll');
    },
},
{text: '${StringUtil.wrapString(uiLabelMap.CommonNbr)}', dataField: 'periodNum'},
{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodName)}', dataField: 'periodName'},
{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
}
},
{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
	}
},
"/>



<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false"  addType="popup"
url="jqxGeneralServicer?sname=JQGetListCustomTimePeriod" mouseRightMenu="true" contextMenuId="contextMenu"
createUrl="jqxGeneralServicer?sname=createCustomTimePeriod&jqaction=C" addColumns="customTimePeriodId;parentPeriodId;organizationPartyId;periodTypeId;periodName;periodNum(java.lang.Long);fromDate(java.sql.Date);thruDate(java.sql.Date)"	
updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod" editColumns="customTimePeriodId;parentPeriodId;organizationPartyId;periodTypeId;periodName;periodNum(java.lang.Long);fromDate(java.sql.Date);thruDate(java.sql.Date)"
/>  

<div id="alterpopupWindow1" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="CustomTimePeriodForm" class="form-horizontal">
			<div class="row-fluid no-left-margin">
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="customTimePeriodIdAdd"/>
						</div>
					</div>
				
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAParentCustomTimePeriod}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="parentPeriodIdAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAFormFieldTitle_organizationPartyId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="organizationPartyIdAdd">
								<div id="jqxOrganizationPartyGrid"></div>
							</div>
						</div>
					</div>
		
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.AccountingPeriodType}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodTypeIdAdd"></div>
						</div>
					</div>
				</div>
				
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodName}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="periodNameAdd"/>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodNumber}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodNumAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="fromDateNewAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="thruDateNewAdd"></div>
						</div>
					</div>		
				</div>
			</div>		
				
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div id="alterpopupEditCTP" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="CustomTimePeriodEditForm" class="form-horizontal">
			<input type="hidden" value="${parameters.customTimePeriodId?if_exists}" id="customTimePeriodId" name="customTimePeriodId" />
			<div class="row-fluid no-left-margin">
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAParentCustomTimePeriod}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="parentPeriodIdEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAFormFieldTitle_organizationPartyId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="organizationPartyIdEdit">
								<div id="jqxOrganizationPartyGrid2" ></div>
							</div>
						</div>
					</div>
		
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.AccountingPeriodType}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodTypeIdEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodName}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="periodNameEdit"/>
						</div>
					</div>
				</div>
				
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodNumber}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodNumEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="fromDateNewEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="thruDateNewEdit"></div>
						</div>
					</div>		
				</div>
			</div>		
				
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-pencil'></i> ${uiLabelMap.Edit}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div id='contextMenu' style="display:none">
<ul>
    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
    <li><i class="fa fa-pencil"></i>${StringUtil.wrapString(uiLabelMap.DAEdit)}</li>
    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
</ul>
</div>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<#assign parentPeriod = delegator.findByAnd("CustomTimePeriod", null, null, false)>
<#assign periodTypePT = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].LIKE ,"SALES_%"), null, null, null, false)>

<script type="text/javascript">

$.jqx.theme = 'olbius';
theme = $.jqx.theme;
$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
$("#contextMenu").on('itemclick', function (event) {
	var args = event.args;
    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
    var tmpKey = $.trim($(args).text());
    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
    	$("#jqxgrid").jqxGrid('updatebounddata');
    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
    	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
		if (data != undefined && data != null) {
			var customTimePeriodId = data.customTimePeriodId;
			var url = 'viewCustomTimeSalesPeriod?customTimePeriodId=' + customTimePeriodId;
			var win = window.open(url, '_self');
			win.focus();
		}
    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
    	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
		if (data != undefined && data != null) {
			var customTimePeriodId = data.customTimePeriodId;
			var url = 'viewCustomTimeSalesPeriod?customTimePeriodId=' + customTimePeriodId;
			var win = window.open(url, '_blank');
			win.focus();
		}
    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAEdit)}") {
    		var wtmp = window;
    	   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
    	   var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
    	   var tmpwidth = $('#alterpopupEditCTP').jqxWindow('width');
    	   $('#alterpopupEditCTP').jqxWindow({ width: 860, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
    	   $("#alterpopupEditCTP").jqxWindow('open');
    	   $("#parentPeriodIdEdit").jqxDropDownList({ source: parentPeriod, width: '198px', height: '25px',displayMember: "periodName", valueMember : "parentPeriodId"});
    	   $("#periodTypeIdEdit").jqxDropDownList({ source: periodTypePT, width: '198px', height: '25px',displayMember: "description", valueMember : "periodTypeId"});
    	   $('#periodNameEdit').jqxInput({width : '193px',height : '19px', value: data.periodName});
    	   $('#periodNumEdit').jqxNumberInput({width : '198px',height : '25px', value: data.periodNum,spinButtons: false, inputMode: 'simple', decimalDigits: 0});
    	   $("#fromDateNewEdit").jqxDateTimeInput({width: '198px', height: '25px', value: data.fromDate});
    	   $("#thruDateNewEdit").jqxDateTimeInput({width: '198px', height: '25px', value: data.thruDate});
    	   $('#organizationPartyIdEdit').jqxDropDownButton({ width: 198, height: 25});
    	   $("#parentPeriodIdEdit").jqxDropDownList('val', data.parentPeriodId);
    	   $("#periodTypeIdEdit").jqxDropDownList('val', data.periodTypeId);
    	   $("#organizationPartyIdEdit").jqxDropDownButton('val', data.organizationPartyId);
    	   $("#organizationPartyIdEdit").jqxDropDownButton({ disabled: true});
    	   
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
    				   	$("#jqxOrganizationPartyGrid2").jqxGrid('updatebounddata');
    				},
    				pager: function (pagenum, pagesize, oldpagenum) {
    				  	// callback called when a page or page size is changed.
    				},
    				sort: function () {
    				  	$("#jqxOrganizationPartyGrid2").jqxGrid('updatebounddata');
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
    				url: 'jqxGeneralServicer?sname=JQGetListParties',
    		};
    	   
    	   var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom,
    			    {
    			    	autoBind: true,
    			    	formatData: function (data) {
    			    		if (data.filterscount) {
    			                var filterListFields = "";
    			                for (var i = 0; i < data.filterscount; i++) {
    			                    var filterValue = data["filtervalue" + i];
    			                    var filterCondition = data["filtercondition" + i];
    			                    var filterDataField = data["filterdatafield" + i];
    			                    var filterOperator = data["filteroperator" + i];
    			                    filterListFields += "|OLBIUS|" + filterDataField;
    			                    filterListFields += "|SUIBLO|" + filterValue;
    			                    filterListFields += "|SUIBLO|" + filterCondition;
    			                    filterListFields += "|SUIBLO|" + filterOperator;
    			                }
    			                data.filterListFields = filterListFields;
    			            }
    			            return data;
    			        },
    			        loadError: function (xhr, status, error) {
    			            alert(error);
    			        },
    			        downloadComplete: function (data, status, xhr) {
    			                if (!sourcePartyFrom.totalRecords) {
    			                    sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
    			                }
    			        }
    			    });	
    		$("#jqxOrganizationPartyGrid2").jqxGrid({
    			width:600,
    			source: dataAdapterPF,
    			filterable: true,
    			virtualmode: true, 
    			sortable:true,
    			editable: false,
    			autoheight:true,
    			pageable: true,
    			showfilterrow: true,
    			rendergridrows: function(obj) {	
    				return obj.data;
    			},
    			columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '25%'},
    						{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '15%'},
    						{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '15%'},
    						{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '45%'},
    					]
    		});
    		
    		$("#jqxOrganizationPartyGrid2").on('rowselect', function (event) {
    		    var args = event.args;
    		    var row = $("#jqxOrganizationPartyGrid2").jqxGrid('getrowdata', args.rowindex);
    		    var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
    		    $("#organizationPartyIdEdit").jqxDropDownButton('setContent', dropDownContent);
    		});
    		
    			$("#alterSave2").click(function () {
    				 var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
    		        if (rowindex >= 0) {
    		        	var data1 = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
    		            var row = {
    		            		customTimePeriodId: data1.customTimePeriodId,
    		            		parentPeriodId : $('#parentPeriodIdEdit').val(),
    		        			organizationPartyId: $('#organizationPartyIdEdit').val(),
    		        			periodTypeId : $('#periodTypeIdEdit').val(),
    		        			periodName : $('#periodNameEdit').val(),
    		        			periodNum : $('#periodNumEdit').val(),
    		        			fromDate : $('#fromDateNewEdit').val(),
    		        			thruDate : $('#thruDateNewEdit').val(),
    		            };
    		            row.fromDate = new Date();
    		        	row.thruDate = new Date();
    		            var rowID = $('#jqxgrid').jqxGrid('getrowid', rowindex);
    		            $('#jqxgrid').jqxGrid('updaterow', rowID, row);
    		            $("#alterpopupEditCTP").jqxWindow('hide');
    		            $("#alterpopupEditCTP").jqxWindow('close');
    		        }
    		    });
    }
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
		   	$("#jqxOrganizationPartyGrid").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
		  	// callback called when a page or page size is changed.
		},
		sort: function () {
		  	$("#jqxOrganizationPartyGrid").jqxGrid('updatebounddata');
		},
		sortcolumn: 'partyId',
       	sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N',
		},
		pagesize:40,
		contentType: 'application/x-www-form-urlencoded',
		url: 'jqxGeneralServicer?sname=JQGetListParties',
};

function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
    };
	var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
	uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	return uniqueRecords2;
}

var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
	    		if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourcePartyFrom.totalRecords) {
	                    sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
	                }
	        }
	    });	
$('#organizationPartyIdAdd').jqxDropDownButton({ width: 198, height: 25});
$("#jqxOrganizationPartyGrid").jqxGrid({
	width:600,
	source: dataAdapterPF,
	filterable: true,
	virtualmode: true, 
	pagesize: 40,
    pagesizeoptions: ['20', '40', '60'],
	sortable:true,
	editable: false,
	autoheight:true,
	pageable: true,
	showfilterrow: true,
	rendergridrows: function(obj) {	
		return obj.data;
	},
	columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '25%'},
				{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '15%'},
				{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '15%'},
				{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '45%'},
			]
});
$(document).ready(function() {
//	$("#jqxOrganizationPartyGrid").on('rowselect', function (event) {
//	    var args = event.args;
//	    var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
//	    $("#organizationPartyIdAdd").jqxDropDownButton('setContent', dropDownContent);
//	});
//	
//	$("#jqxOrganizationPartyGrid").jqxGrid('selectrow', 10);
	
	$("#jqxOrganizationPartyGrid").on('rowselect', function (event) {
	    var args = event.args;
	    var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + args.row.partyId + '</div>';
	    $("#organizationPartyIdAdd").jqxDropDownButton('setContent', dropDownContent);
	});
	$("#jqxOrganizationPartyGrid").jqxGrid('selectrow',34);
	$("#organizationPartyIdAdd").jqxDropDownButton({ disabled: true});
});

var parentPeriod = [
                         <#list parentPeriod as pP>
                         {
                        parentPeriodId : "${pP.parentPeriodId?if_exists}",
                      	   periodName : "${StringUtil.wrapString(pP.periodName)}"
                         },
                         </#list>	
                     ];

var periodTypePT = [
                    <#list periodTypePT as pT>
                    {
                    	periodTypeId : "${pT.periodTypeId}",
                 	   description : "${StringUtil.wrapString(pT.description)}"
                    },
                    </#list>	
                ];

$('#alterpopupWindow1').jqxWindow({ width: 860, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
$("#parentPeriodIdAdd").jqxDropDownList({ source: parentPeriod, width: '198px', height: '25px',displayMember: "periodName", valueMember : "parentPeriodId"});
$("#periodTypeIdAdd").jqxDropDownList({ source: periodTypePT, width: '198px', height: '25px',displayMember: "description", valueMember : "periodTypeId"});
$('#periodNameAdd').jqxInput({width : '193px',height : '19px'});
$('#periodNumAdd').jqxNumberInput({width : '198px',height : '25px',spinButtons: false, inputMode: 'simple', decimalDigits: 0});
$("#fromDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px'});
$("#thruDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px'});
$('#customTimePeriodIdAdd').jqxInput({width : '193px',height : '19px'});
$('#periodNumAdd').jqxNumberInput('val', 1);
$("#periodTypeIdAdd").jqxDropDownList({autoDropDownHeight: true}); 
$("#thruDateNewAdd").jqxDateTimeInput('val', null);

$('#CustomTimePeriodForm').jqxValidator({
	rules : [
	         {input: '#organizationPartyIdAdd', message: '${uiLabelMap.DANotYetChooseItem}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
	]
});


$('#alterSave1').click(function(){
	$('#CustomTimePeriodForm').jqxValidator('validate');
});

$('#CustomTimePeriodForm').on('validationSuccess',function(){
	var row = {};
	row = {
			customTimePeriodId : $('#customTimePeriodIdAdd').val(),
			parentPeriodId : $('#parentPeriodIdAdd').val(),
			organizationPartyId: $('#organizationPartyIdAdd').val(),
			periodTypeId : $('#periodTypeIdAdd').val(),
			periodName : $('#periodNameAdd').val(),
			periodNum : $('#periodNumAdd').val(),
			fromDate : $('#fromDateNewAdd').val(),
			thruDate : $('#thruDateNewAdd').val(),
	};
	row.fromDate = new Date();
	row.thruDate = new Date();
	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
// select the first row and clear the selection.
	$("#jqxgrid").jqxGrid('clearSelection');                        
	$("#jqxgrid").jqxGrid('selectRow', 0);  
	$("#alterpopupWindow1").jqxWindow('close');
});

$('#alterpopupWindow1').on('close',function(){
	$('#CustomTimePeriodForm').jqxValidator('hide');
	$('#customTimePeriodIdAdd').val('');
	$('#parentPeriodIdAdd').val('');
	$('#periodTypeId').val('');
	$('#periodName').val('');
	$('#periodNum').val('');
	$('#fromDateNewAdd').val('');
	$('#thruDateNewAdd').val('');
//	$('#PersonEducationForm').trigger('reset');
});


</script>
