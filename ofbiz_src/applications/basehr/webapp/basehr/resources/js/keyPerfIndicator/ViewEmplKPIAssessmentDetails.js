var emplKPIAssessmentDetails = (function(){
	var _perfCriteriaAssessmentId = "";
	var _partyId = "";
	var _fullName = "";
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initJqxNotification();
	};
	var initJqxNotification = function(){
		$("#jqxNotificationemplKPIAssessGeneralGrid").jqxNotification({ width: "100%", appendContainer: "#containeremplKPIAssessGeneralGrid", opacity: 0.9, template: "info" });
	};
	var initJqxGrid = function(){
		var config = {
				url: '',
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: false,
				localization: getLocalization(),
				source: {pagesize: 10}
		};
		
		var datafieldDetail = [{name: 'partyId', type: 'string'},
				                 {name: 'criteriaId', type: 'string'},
				                 {name: 'criteriaName', type: 'string'},
				                 {name: 'periodTypeId', type: 'string'},
				                 {name: 'weight', type: 'number'},
				                 {name: 'target', type: 'number'},
				                 {name: 'result', type: 'number'},
				                 {name: 'uomId', type: 'string'},
				                 {name: 'dateReviewed', type: 'date'},
				                 ];
		
		var columnDetail = [{datafield: 'partyId', hidden: true},
				            {datafield: 'criteriaId', hidden: true},
				            {datafield: 'uomId', hidden: true},
				            {datafield: 'dateReviewed', hidden: true},
				            {text: uiLabelMap.HRCommonKPIName, datafield: 'criteriaName', width: '25%'},
				            {text: uiLabelMap.HRFrequency, datafield: 'periodTypeId', width: '25%', cellsalign: 'right', 
			            	   columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
			            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								   for(var i = 0; i < globalVar.kpiPeriodTypeArr.length; i++){
									   if(globalVar.kpiPeriodTypeArr[i].periodTypeId == value){
										   return '<span>' + globalVar.kpiPeriodTypeArr[i].description + '<span>';
									   }
								   }
								   return '<span>' + value + '<span>';
							   },
							   createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.kpiPeriodTypeArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
								    if(dataSoureList.length < 8){
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }
								}
			                },
			                {text : uiLabelMap.HRTarget, width : '25%', dataField : 'target',  columntype: 'numberinput',
								filtertype: 'number', editable: false,
								cellsrenderer: function (row, column, value) {
									var data = $('#emplKPIAssessDetailGrid').jqxGrid('getrowdata', row);
									var retVal = formatNumber(value, 2, " ");
									if(data){
										var uomId = data.uomId;
										var uomDes = uomId;
										if(value){
											//retVal = formatNumber(value);
											if(uomId){
												for(var i = 0; i < globalVar.uomArr.length; i++){
													if(globalVar.uomArr[i].uomId == uomId){
														uomDes = globalVar.uomArr[i].abbreviation;
														break;
													}
												}
											}
											retVal += ' ' + uomDes;
										}
									}
									return '<span style="text-align: right">' + retVal + '<span>';
								}
							},
							{text: uiLabelMap.HRCommonActual, dataField: 'result', columntype: 'numberinput', filtertype: 'number', 
								editable: false,
								cellsrenderer: function (row, column, value) {
									var data = $('#emplKPIAssessDetailGrid').jqxGrid('getrowdata', row);
									var retVal = formatNumber(value, 2, " ");
									if(data){
										var uomId = data.uomId;
										var uomDes = uomId;
										if(value){
											//retVal = formatNumber(value);
											if(uomId){
												for(var i = 0; i < globalVar.uomArr.length; i++){
													if(globalVar.uomArr[i].uomId == uomId){
														uomDes = globalVar.uomArr[i].abbreviation;
														break;
													}
												}
											}
											retVal += ' ' + uomDes;
										}
									}
									return '<span style="text-align: right">' + retVal + '<span>';
								},
							},
				            ];
		
		var gridDetail = $("#emplKPIAssessDetailGrid");
		var rendertoolbarDetail = function (toolbar){
			toolbar.html("");
			var id = "emplKPIAssessDetailGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='emplKPIAssessDetailTitle'>" + uiLabelMap.DetailsOfReviewPoint + " " + _fullName + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createFilterButton(gridDetail, container, uiLabelMap.accRemoveFilter);
		};
		config.filterable = true;
		config.rendertoolbar = rendertoolbarDetail;
		Grid.initGrid(config, datafieldDetail, columnDetail, null, gridDetail);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#emplKPIAssessmentDetailWindow"), 880, 500);
		$("#emplKPIAssessmentDetailWindow").on('open', function(event){
			$("#emplKPIAssessGeneralTitle").html(uiLabelMap.GeneralOfReviewPoint + " " + _fullName);
			$("#emplKPIAssessDetailTitle").html(uiLabelMap.DetailsOfReviewPoint + " " + _fullName);
		});
		$("#emplKPIAssessmentDetailWindow").on('close', function(event){
			_partyId = null;
			_perfCriteriaAssessmentId = null;
			_fullName = null;
		})
	};
	var setData = function(data){
		_partyId = data.partyId;
		_perfCriteriaAssessmentId = data.perfCriteriaAssessmentId;
		_fullName = data.fullName;
//		var source = $("#emplKPIAssessGeneralGrid").jqxGrid('source');
//		source._source.url = "jqxGeneralServicer?sname=JQGetEmplKPIAssessetmentGeneral&perfCriteriaAssessmentId=" + _perfCriteriaAssessmentId + "&partyId=" + _partyId;
//		$("#emplKPIAssessGeneralGrid").jqxGrid('source', source);
		
		var sourceDetail = $("#emplKPIAssessDetailGrid").jqxGrid('source');
		sourceDetail._source.url = "jqxGeneralServicer?sname=JQGetEmplKPIAssessetmentDetail&perfCriteriaAssessmentId=" + _perfCriteriaAssessmentId + "&partyId=" + _partyId;
		$("#emplKPIAssessDetailGrid").jqxGrid('source', sourceDetail);
	};
	var openWindow = function(){
		openJqxWindow($("#emplKPIAssessmentDetailWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	emplKPIAssessmentDetails.init();
});