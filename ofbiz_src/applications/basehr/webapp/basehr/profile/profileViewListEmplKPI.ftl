<#include "script/profileViewListEmplKPIScript.ftl"/>
<#assign selectionmode = "singlerow"/>
<#assign datafield = "[{name: 'criteriaId', type: 'string'},
			{name: 'criteriaName', type: 'string'},
			{name: 'fromDate', type: 'date'},
			{name: 'dateReviewed', type: 'date'},
			{name: 'statusId', type: 'string'},
			{name: 'perfCriteriaTypeId', type: 'string'},
			{name: 'periodTypeId', type: 'string'},
			{name: 'target', type: 'number'},
			{name: 'uomId', type: 'string'},
			{name: 'weight', type: 'number'},
			{name: 'result', type: 'number'},
			{name: 'comment', type: 'string'},
			{name: 'description', type: 'string'},
			{name : 'partyId', type : 'string'},
			{name : 'description_uom', type : 'string'}
	]"/>
	
	<#assign columnlist = "{datafield: 'uomId', hidden: true},
		{datafield: 'criteriaId', hidden: true},
		{datafield: 'fromDate', hidden: true},
		{datafield: 'periodTypeId', hidden: true},
		{datafield : 'partyId', hidden :true},
		{text : '${StringUtil.wrapString(uiLabelMap.TimeAssessment)}', datafield : 'dateReviewed', width : '20%', cellsformat : 'dd/MM/yyyy', editable : false},
		{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', width : '13%', dataField : 'statusId', columntype: 'dropdownlist', 
			filtertype: 'checkedlist', editable: false,
			cellsrenderer: function (row, column, value) {
				for(var i = 0; i < globalVar.statusArr.length; i++){
					if(value == globalVar.statusArr[i].statusId){
						return '<span>' + globalVar.statusArr[i].description + '</span>'; 
					}
				}
				return '<span>' + value + '</span>';
			},
			createfilterwidget: function(column, columnElement, widget){
				var source = {
				        localdata: globalVar.statusArr,
				        datatype: 'array'
				};		
				var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
			    var dataSoureList = filterBoxAdapter.records;
			    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonFields)}', datafield: 'perfCriteriaTypeId', width: '20%', editable: false,
			columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
			cellsrenderer: function (row, column, value) {
				for(var i = 0; i < globalVar.perfCriteriaTypeArr.length; i++){
					if(value == globalVar.perfCriteriaTypeArr[i].perfCriteriaTypeId){
						return '<span>' + globalVar.perfCriteriaTypeArr[i].description + '</span>'; 
					}
				}
				return '<span>' + value + '</span>';
			},
			createfilterwidget: function(column, columnElement, widget){
				var source = {
				        localdata: globalVar.perfCriteriaTypeArr,
				        datatype: 'array'
				};		
				var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
			    var dataSoureList = filterBoxAdapter.records;
			    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'perfCriteriaTypeId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}	
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}', datafield: 'criteriaName', width: '20%', editable: false,
			cellsrenderer: function (row, column, value) {
				var data = $('#jqxgrid_monthly').jqxGrid('getrowdata', row);
				if(data){
					var description = data.description;
					var tooltipId = 'kpiNameTooltip_' + row;
				}
				return '<span id=' + tooltipId +'>' + value + '</span>';
			}
		},
		{text : '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', dataField : 'description', width: '23%', columntype: 'custom',editable: false,
			 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return defaulthtml;
		     }	
		},				
		{text: '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}', datafield: 'weight', columntype: 'numberinput', width: '10%', editable: false, filterType : 'number',
			cellsrenderer: function (row, column, value) {
				if(typeof(value) != 'undefined' && value != null){
					var tempValue = 100 * value;
					return '<span style=\"text-align: right\">' + tempValue + '%</span>';
				}
			}
		},
		{text : '${StringUtil.wrapString(uiLabelMap.HRTarget)}', width : '15%', dataField : 'target',  columntype: 'numberinput',
			filtertype: 'number', editable: false,
			cellsrenderer: function (row, column, value) {
				
				var retVal = value;
				if(typeof(value) == 'number'){
					retVal = formatNumber(value, 2, ' ');
				}
				return '<span style=\"text-align: right\">' + retVal + '<span>';
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonActual)}', dataField: 'result', columntype: 'numberinput', filtertype: 'number', 
			editable: true, width: '15%',
			cellsrenderer: function (row, column, value) {
			
				var retVal = value;
				if(typeof(value) == 'number'){
					retVal = formatNumber(value, 2, ' ');
				}
				return '<span style=\"text-align: right\">' + retVal + '<span>';
			},
			createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
				editor.jqxNumberInput({width : cellwidth, spinButtons: true});
			},
			validation : function(cell, value){
				if(value < 0){
					return{result : false, message : '${uiLabelMap.OnlyInputNumberGreaterThanZero}'};
				}
				return true;
			}
		},
		{text : '${StringUtil.wrapString(uiLabelMap.HRCommonUnit)}', datafield : 'description_uom', width : '12%', editable : false,
				cellsrenderer : function(row, column, value){
					var data = $('#jqxgrid_monthly').jqxGrid('getrowdata', row);
					if(data !== undefined){
						if(data.uomId == 'KM_PERCENT'){
							return '<span>' + '%' + '<span>';
						}
					}
				}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonComment)}', dataField: 'comment', editable: false, width: '22%',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return defaulthtml;
	         }	
		},
	"/>
<div id="containerApprover" style="background-color: transparent; overflow: auto;">
</div>
<div id="jqxNotificationApprover">
     <div id="notificationContentApprover">
     </div>
 </div>	
<div class="row-fluid">
	<ul class="nav nav-tabs padding-18" id="frequency_tab" >
		<li class="active">
			<a data-toggle="tab" href="#daily_tab">${uiLabelMap.CommonDaily}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#weekly_tab">${uiLabelMap.CommonWeekly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#monthly_tab">${uiLabelMap.CommonMonthly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#quarterly_tab">${uiLabelMap.Quarterly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#yearly_tab">${uiLabelMap.CommonYearly}</a>
		</li>
		<li class="pull-right">
			<div class="row-fluid">
				<div class="span12">
					<button id="removeFilter" class="grid-action-button open-sans" style="width: 100%"><i class="fa icon-filter"></i>${uiLabelMap.HRCommonRemoveFilter}</button>
				</div>
			</div>
		</li>
		<li class="pull-right">
			<div class="row-fluid">
				<div class="span12">
					<button id="sendPropsalAppr" class="grid-action-button open-sans" style="width: 100%"><i class="fa fa-paper-plane"></i>${uiLabelMap.SendProposalToApproval}</button>
				</div>
			</div>
		</li>
	</ul>
	<div class="tab-content overflow-visible" style="border: none !important">
		<#include "profileViewEmplListKPIDaily.ftl">
		<#include "profileViewEmplListKPIWeekly.ftl">
		<#include "profileViewEmplListKPIMonthly.ftl">
		<#include "profileViewEmplListKPIQuarterly.ftl">
		<#include "profileViewEmplListKPIYearly.ftl">
	</div>
</div>
<div id="ProposalApprWindow" class="hide">
	<div>${uiLabelMap.SendProposalToApproval}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRCommonApprover}</label>
				</div>
				<div class="span9">
					<div id="approverListDropDownBtn">
						 <div style="border-color: transparent;" id="jqxGridApprover"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="ajaxLoadingAppr" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinnerAjaxAppr"></div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/profile/profileViewListEmplKPI.js"></script>