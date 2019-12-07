<#include "script/ViewResultDataKPIEmplScript.ftl">
<#assign datafield = "[
		{name: 'criteriaId', type: 'string'},
		{name: 'criteriaName', type: 'string'},
		{name: 'fromDate', type: 'date'},
		{name: 'thruDate', type: 'date'},
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
		{name : 'partyCode', type : 'string'},
		{name : 'fullName', type : 'string'}
	]"/>

	<#--comment hide weigt, because weight not use-->
	<#--<#assign columnlist = "
		{datafield: 'criteriaId', hidden: true},
		{datafield: 'fromDate', hidden: true},
		{datafield: 'periodTypeId', hidden: true},
		{datafield : 'partyId', hidden : true},
		{text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', width : '20%', datafield : 'fullName', editable :false},
		{text : '${StringUtil.wrapString(uiLabelMap.TimeAssessment)}', datafield : 'dateReviewed', width : '20%', editable: false,cellsformat : 'dd/MM/yyyy'},
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
			    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'perfCriteriaTypeId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}	
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}', datafield: 'criteriaName', width: '20%', editable: false},
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
			cellsrenderer : function(row, column, value){
				if(value){
					var val = formatNumber(value, 2, ' ');
					return '<span style=\"text-align: right\">' + val + '<span>';
				}
			}
		},
		
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonActual)}', dataField: 'result', columntype: 'numberinput', filtertype: 'number', 
			editable: false, width: '10%',
			cellsrenderer : function(row, column, value){
				if(value){
					var val = formatNumber(value, 2, ' ');
					return '<span style=\"text-align: right\">' + val + '<span>';
				}
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonUnit)}', datafield: 'uomId', width : '10%', editable : false,
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				for(var i = 0; i < globalVar.uomArr.length; i++){
					if(globalVar.uomArr[i].uomId == value){
						return '<span>' + globalVar.uomArr[i].abbreviation + '</span>'
					}
				}
				return '<span>' + value + '</span>'
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonComment)}', dataField: 'comment', editable: false, width: '20%',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return defaulthtml;
	         }	
		},
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
			    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}
		},
	"/>-->
<#assign columnlist = "
		{datafield: 'criteriaId', hidden: true},
		{datafield: 'fromDate', hidden: true},
		{datafield: 'thruDate', hidden: true},
		{datafield: 'periodTypeId', hidden: true},
		{datafield : 'partyId', hidden : true},
		{text : '${StringUtil.wrapString(uiLabelMap.HRPartyCode)}', width : '12%', datafield : 'partyCode', editable :false},
		{text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', width : '15%', datafield : 'fullName', editable :false},
		{text : '${StringUtil.wrapString(uiLabelMap.TimeAssessment)}', datafield : 'dateReviewed', width : '13%', editable: false,cellsformat : 'dd/MM/yyyy'},
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
			    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'perfCriteriaTypeId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}', datafield: 'criteriaName', width: '20%', editable: false},
		{text : '${StringUtil.wrapString(uiLabelMap.HRTarget)}', width : '15%', dataField : 'target',  columntype: 'numberinput',
			filtertype: 'number', editable: false,
			cellsrenderer : function(row, column, value){
				if(value){
					var val = formatNumber(value, 2, ' ');
					return '<span style=\"text-align: right\">' + val + '<span>';
				}
			}
		},

		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonActual)}', dataField: 'result', columntype: 'numberinput', filtertype: 'number',
			editable: false, width: '10%',
			cellsrenderer : function(row, column, value){
				if(value){
					var val = formatNumber(value, 2, ' ');
					return '<span style=\"text-align: right\">' + val + '<span>';
				}
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonUnit)}', datafield: 'uomId', width : '10%', editable : false,
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				for(var i = 0; i < globalVar.uomArr.length; i++){
					if(globalVar.uomArr[i].uomId == value){
						return '<span>' + globalVar.uomArr[i].abbreviation + '</span>'
					}
				}
				return '<span>' + value + '</span>'
			}
		},
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
			    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
			    if(dataSoureList.length < 8){
			    	widget.jqxDropDownList({autoDropDownHeight: true});
			    }else{
			    	widget.jqxDropDownList({autoDropDownHeight: false});
			    }
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonComment)}', dataField: 'comment', editable: false, width: '15%',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return defaulthtml;
	         }
		},
	"/>

<div class="row-fluid">
	<ul class="nav nav-tabs padding-18" id="container_tab">
		<li class="active">
			<a data-toggle="tab" href="#daily_li">${uiLabelMap.CommonDaily}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#weekly_li">${uiLabelMap.CommonWeekly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#monthly_li">${uiLabelMap.CommonMonthly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#quarterly_li">${uiLabelMap.Quarterly}</a>
		</li>
		<li>
			<a data-toggle="tab" href="#yearly_li">${uiLabelMap.CommonYearly}</a>
		</li>
	</ul>
	<div class="tab-content overflow-visible" style="border: none !important">
		<#include "ViewResultDataKPIEmplPeriod.ftl" >
		<#include "ViewResultDataKPIEmplWeek.ftl">
		<#include "ViewResultDataKPIEmplMonth.ftl">
		<#include "ViewResultDataKPIEmplQuarter.ftl">
		<#include "ViewResultDataKPIEmplYear.ftl">
	</div>
</div>
<div class="row-fluid">
    <div id="pushResultKPIWindow" class='hide'>
        <div>${uiLabelMap.ApproveKPIEmpl}</div>
        <div class="form-window-container">
            <div class="form-window-content">
                <div class='row-fluid margin-bottom10' style="margin-top: 1px">
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.EmployeeName}</label>
                    </div>
                    <div class='span8'>
                        <input id="partyName" type="text" />
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRCommonFields}</label>
                    </div>
                    <div class='span8'>
                        <input id="kpiType" type="text" />
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRCommonKPIName}</label>
                    </div>
                    <div class='span8'>
                        <input id="kpiName" type="text" />
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.KPIWeigth}</label>
                    </div>
                    <div class='span8'>
                        <input id="kpiWeight" type="text" />
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRTarget}</label>
                    </div>
                    <div class='span8'>
                        <div id="kpiTarget"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRCommonActual}</label>
                    </div>
                    <div class='span8'>
                        <div id="kpiActual"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRCommonComment}</label>
                    </div>
                    <div class='span8'>
                        <textarea id="kpiComment"></textarea>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.CommonStatus}</label>
                    </div>
                    <div class='span8'>
                        <input type="text" id="statusKPI"/>
                    </div>
                </div>
                <#--<div class='row-fluid'>
                    <div class='span4 align-right'>
                        <label>${uiLabelMap.HRApprove}</label>
                    </div>
                    <div class='span8'>
                        <div class="span6">
                            <div id="approveKPI">${uiLabelMap.HRCommonAccept}</div>
                        </div>
                        <div class="span6">
                            <div id="rejectKPI">${uiLabelMap.HRReject}</div>
                        </div>
                    </div>
                </div>
                <div class='row-fluid'>
                    <div class='span4 align-right'>
                        <label></label>
                    </div>
                    <div class="span8">
                        <div style="margin-left: 16px; margin-top: 4px">
                            <div id="sendNtfToEmpl"><span style="font-size: 14px">${uiLabelMap.NotifyForEmployee}</span></div>
                        </div>
                    </div>
                </div>-->
            </div>
            <div class="form-action">
                <div class='row-fluid'>
                    <div class="span12 margin-top10">
                        <button id="cancelResultKPIType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
                        <button id="saveResultKPIType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
                    </div>
                </div>
            </div>
            <div class="row-fluid no-left-margin">
                <div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
                    <div class="loader-page-common-custom" id="spinnerAjax"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/PushResultDataKPIEmpl.js?v=0.0.1"></script>