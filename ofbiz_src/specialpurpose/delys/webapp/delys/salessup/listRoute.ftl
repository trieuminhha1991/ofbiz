<style type="text/css">
	.jqx-clear.jqx-border-reset.jqx-overflow-hidden.jqx-max-size{
		width :  !important;
	}
	.jqx-clear.jqx-max-size.jqx-position-relative.jqx-overflow-hidden.jqx-background-reset>div.jqx-widget-header-energyblue{
		width : 1044px !important;
	}
	.buttonRt{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		float : right;
		margin-left : 10px;
	}
	.buttonRt1{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		float : right;
		margin-left : 10px;
		padding-left : 117px;
		padding-right : 126px;
	}
	.form-window-content{
		overflow : hidden;
	}
	
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign dataField = "[
	{name : 'routeId',type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'scheduleRoute',type : 'string'}
]"/>
<#assign listDayOfWeek = delegator.findList("DayOfWeek",null,null,null,null,false) !>
<script type="text/javascript">
	var listDay = [
		<#list listDayOfWeek as day>
			{
				dayId : '${day.dayOfWeek?if_exists}',
				description : '${StringUtil.wrapString(day.get('description',locale)?default(''))}'
			},
		</#list>
	];
</script>
<#assign columnlist= "	
					 { text : '${uiLabelMap.DARouteId}',datafield : 'routeId',width : '150px'},
					 { text : '${uiLabelMap.DARouteName}',datafield : 'description'},
					 {text : '${uiLabelMap.DARouteSchedule}',datafield : 'scheduleRoute',columntype : 'combobox',cellsrenderer : function(row,columnfield,value){
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
			 				var scheduleElement;
					 		if(data.scheduleRoute){
					 			scheduleElement = (data.scheduleRoute).split(',');
					 		}
					 		var routeScheduleStr = '';
					 		for(var i = 0 ;i < listDay.length;i++){
					 			for(var e in scheduleElement){
						 			if(listDay[i].dayId == scheduleElement[e]){
						 				if(listDay[i].description !== 'undefined' ){
						 					routeScheduleStr +='-' +  listDay[i].description + '-';
						 					break;
						 				}
						 			}
					 			}
					 		}
					 		return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
					 	 },filterable : false,
					 	 createeditor : function(row,column,editor){
					 		combobox = [];
					 		editor.jqxComboBox({source: schedule,autoDropDownHeight : true,valueMember : 'routeScheduleId',displayMember : 'name',multiSelect : true ,width : '190px',height : '25px'});
						 	editor.on('change', function (event) {
							    var args = event.args;
							    if (args) {
							   		 var item = args.item;
								   	 var value = item.value;
								    combobox.push(value);
								}
							}); 
						 },
//						 initeditor : function(row, column, editor){
//							 combobox = [];
//							 editor.jqxComboBox({source : schedule, valueMember : 'routeScheduleId', displayMember : 'name'});
//							 editor.on('change', function (event) {
//							    var args = event.args;
//							    if (args) {
//							   		 var item = args.item;
//								   	 var value = item.value;
//								    combobox.push(value);
//								}
//							}); 
//						 }
				 	 }
"/>
<div id="notification" style="width : 100%;"></div>
<#assign rowdetailstemplateAdvance = "<ul class='margin-left: 10px;'><li>${StringUtil.wrapString(uiLabelMap.DAviewSalemansDetail)}</li><li>${StringUtil.wrapString(uiLabelMap.DAViewCustomerDetail)}</li></ul><div class='salemandetail'></div><div class='customerdetail'></div>" />
<@jqGrid filtersimplemode="true" filterable="true" editable="true" addrefresh="true" editrefresh="true" updateoffline="false" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addrow="true" deleterow="true" rowdetailsheight="275" initrowdetails="true" rowdetailstemplateAdvance = rowdetailstemplateAdvance initrowdetailsDetail="initrowdetails"
		 url="jqxGeneralServicer?sname=JQgetListRoute"
		 createUrl="jqxGeneralServicer?sname=createRoute&jqaction=C" addColumns="routeName;infoRoute"
		 updateUrl="jqxGeneralServicer?sname=updateRoute&jqaction=U" editColumns="routeId;scheduleRoute(java.util.List);description"
		 removeUrl="jqxGeneralServicer?sname=deleteRoute&jqaction=D" deleteColumn="routeId"
		 />
<#assign columnListCust = "
			{
				text : '${uiLabelMap.DACustomerId}',
				datafield : 'partyId',
				width : '200px'
			},
			{
				text : '${uiLabelMap.DACustomerName}',
				datafield : 'fullName'
			}
"/>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>	
	<div style="over-flow:hidden">
		<form id="formAdd" class="form-horizontal">
	    		<div class='row-fluid form-window-content'>
			    		<div class='span12'>
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.DARouteName}
			    				</div>
			    				<div class='span7'>
		    						<input name="routeName" id="routeAdd" type="text"/>
			    				</div>
		    				</div>
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.DARouteSchedule}
			    				</div>
			    				<div class='span7'>
			    					<div id="routeScheduleAdd"></div>
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
<div id="deleteDialog"></div>
<div id="routePopup" style="display : none;">
<div>${uiLabelMap.DistributionRoute}</div>
<div class="form-window-container">
	<div class="form-window-content">
		<div class="row-fluid margin-bottom10">
			<div id="gridSM"></div>
		</div>
		<hr>
		<div class="row-fluid margin-bottom10">
			<div id="listSMDistribution"></div>
		</div>
	</div>
	<div class="form-action">
		<button id="cancelRt" class='btn btn-danger form-action-button pull-right' onclick="cancelRoute()"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
		<button id="submitRt" class="btn btn-primary form-action-button pull-right" onclick="submitRoute()"><i class="fa-check"></i>&nbsp;${uiLabelMap.DAConfirm}</button>
	</div>
</div>
</div>
<div id="messageNotification" style="display : none"></div>
<div id="messageNotificationError" style="display : none"></div>
<div id="deleteDialogCus"></div>
<div id="routePopupCus" style="display : none;" class="hide">
	<div>${uiLabelMap.DistributionRoute}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid margin-bottom10">
				<div id="gridCustomer"></div>
			</div>
			<hr>
			<div class="row-fluid margin-bottom10">
				<div id="listCustomerDistribution"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelRtCus" class='btn btn-danger form-action-button pull-right' onclick="cancelRouteCus()"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
			<button style="display;" id="submitRtCus" class="btn btn-primary form-action-button pull-right" onclick="submitRouteCus()"><i class="fa-check"></i>&nbsp;${uiLabelMap.DAConfirm}</button>
		</div>
	</div>
</div>
<div id="messageNotificationCus" style="display : none"></div>
<div id="messageNotificationErrorCus" style="display : none"></div>
<div id="alterpopupWindowSalesmanDetail" style="display:none;">
	<div style="overflow:hidden;">
		<form id="alterpopupWindowSalesmanDetailForm" class="form-horizontal">
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
			<button id="cancelRtSalemanDetail" class='btn btn-danger form-action-button pull-right' onclick="cancelRouteCus()"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
//	var action = (function(){
//		var initElement = function(){
//			
//			
//			
//		}
//		
//		var bindEvent  = function(){
//			
//			
//			
//		}
//		
//		var initGrid = function(){
//			/***init Grid distribute salesman***/
//			var dataFields = [
//				              {name : "partyId", type:"String"},
//				              {name : "firstName", type : "String"},
//				              {name : "middleName", type : "String"},
//				              {name : "lastName", type : "String"},
//				              {name : "fullName", type: "String"}
//			              ];
//			var columns = [
//	  						{
//	  							text : '${uiLabelMap.DAEmployeeId}',
//	  							datafield : 'partyId',
//	  							width : '200px'
//	  						},
//	  						{
//	  							text : '${uiLabelMap.DAEmployeeName}',
//	  							datafield : 'fullName'
//	  						}
//	  					];
//			GridUtils.initGrid({url : 'JQGetListSalesmanOfUserLogin&partyId=${parameters.userLogin.partyId?if_exists}',width : '100%',selectionmode: 'singlecell',showfilterrow : true,localization: getLocalization(),autoHeight : true},dataFields,columns,null,$('#gridSM'));
//			/********************************************************/
//			/***init Grid distribute Customer****/
//			
//			
//			
//			/*************************************/
//			
//		}
//		return {
//			init : function(){
//				initElement();
//				bindEvent();
//			}
//		}
//	}())
//	$(document).ready(function(){
//		action.init();
//	})
	 var schedule  =  [{routeScheduleId : 'T2',name : 'Thứ 2'},{routeScheduleId : 'T3',name : 'Thứ 3'},{routeScheduleId : 'T4',name : 'Thứ 4'},{routeScheduleId : 'T5',name : 'Thứ 5'},{routeScheduleId : 'T6',name : 'Thứ 6'},
	 	{routeScheduleId : 'T7',name : 'Thứ 7'} ];
	$.jqx.theme = 'olbius';
	theme  = $.jqx.theme;
	var globalParent;		
	var globalData;
	var notificationRoute = '';
	var indexGlobal;
	var rtId;
//	set UI for SaleMan
	var bindEventSaleMan = function(id){
		id.bind('dropTargetEnter',function(event){
				id.jqxDragDrop('dropAction','none');	
				id.jqxDragDrop({revert : false});
			});	
		id.bind('dropTargetLeave',function(event){
				$(this).jqxDragDrop('dropAction','none');	
			});	
		id.bind('dragEnd',function(event){
			var cell = $('#gridSM').jqxGrid('getselectedcell');
			if(cell == 'undefined' || cell == null){
				return;
			}
			var data = $('#gridSM').jqxGrid('getrowdata',cell.rowindex);
		     var rowindex = $('#'+event.args.element.id).jqxGrid('getselectedcell');
		        var position = $.jqx.position(event.args);
		        var cell = $("#listSMDistribution").jqxGrid('getcellatposition', position.left, position.top);
		        var tmp = $("#listSMDistribution").jqxGrid('getrows');
		        for(var i=0; i<tmp.length;i++){
		        	if(data.partyId == tmp[i].partyId){
		        		return false;
		        	}
		        }
		        if (cell != null) {
		            $("#listSMDistribution").jqxGrid('setcellvalue', cell.row,'partyId', data.partyId);
		            $("#listSMDistribution").jqxGrid('setcellvalue', cell.row, 'fullName', data.fullName);
		        }
			});	
	};
	var dataFieldSaleMan = [
			              {name : "partyId", type:"String"},
			              {name : "firstName", type : "String"},
			              {name : "middleName", type : "String"},
			              {name : "lastName", type : "String"},
			              {name : "fullName", type: "String"}
		              ];
	var columnsaleman = [
  						{
  							text : '${uiLabelMap.DAEmployeeId}',
  							datafield : 'partyId',
  							width : '200px',
  							cellsrenderer : function(row, column, value){
  								var data = $("#gridSM").jqxGrid('getrowdata', row);
  								return '<span><button id=\"partyIdButton\" class=\"buttonRt1\" onclick=\"DetailSalesman()\">'+ data.partyId +'</button></span>';
  							}
  						},
  						{
  							text : '${uiLabelMap.DAEmployeeName}',
  							datafield : 'fullName'
  						}
  					];
	GridUtils.initGrid({url : 'JQGetListSalesmanOfUserLogin&partyId=${parameters.userLogin.partyId?if_exists}',width : '100%',selectionmode: 'singlecell',showfilterrow : true,localization: getLocalization(),autoHeight : true},dataFieldSaleMan,columnsaleman,null,$('#gridSM'));
	$('#gridSM').on('bindingComplete',function(){
		bindDragDrop();
	});
//	gridSalesmanDetail
	var DetailSalesman = function(){
		var cell = $("#gridSM").jqxGrid('getselectedcell');
		var rowindex = cell.rowindex;
		var data = $("#gridSM").jqxGrid('getrowdata', rowindex);
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
		$("#alterpopupWindowSalesmanDetail").jqxWindow('open');
		$("#GridSalesman").jqxGrid({
			width:'100%',
			filterable :true,
			editable: false,
			pageable : true,
			autoheight : true,
			columnsresize: true,
			source : dataAdapterSalesmanDetails,
			columns : [
	           {text : "${uiLabelMap.DARouteId}", datafield : "routeId", width : "25%"},
	           {text : "${uiLabelMap.DARouteName}", datafield : "description", width : "40%"},
	           {text : "${uiLabelMap.DARouteSchedule}",datafield : "scheduleRoute",cellsrenderer : function(row,columnfield,value){
			 		var data = $("#GridSalesman").jqxGrid("getrowdata",row);
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
	$("#alterpopupWindowSalesmanDetail").jqxWindow({width : 640, height : 300, isModal: true,autoOpen: false,modalOpacity : 0.8,theme : theme});
	$("#cancelRtSalemanDetail").on('click', function(){
		$("#alterpopupWindowSalesmanDetail").jqxWindow('close');
	})
//	end
	$("#listSMDistribution").jqxGrid(
	{
   		theme : 'olbius',
        width: '100%',
        selectionmode: 'singlecell',
        autoheight: true,
        localization: getLocalization(),
		pagesize : 5,
		pagesizeoptions: ['5', '10', '15'],
		pageable: true,
        source: { totalrecords: 1, unboundmode: true, datafields:
        [
            { name: 'partyId',type : 'string' },
            { name: 'fullName',type : 'string' }
        ]
        },
        columns: [
           { text: '${uiLabelMap.DAEmployeeId}', dataField: 'partyId', width: 300 },
           { text: '${uiLabelMap.DAEmployeeName}', dataField: 'fullName' }
        ]
    });  
			  /*end*/
		function bindDragDrop(){
				var tmp  = $('#gridSM').find("div[role='row']");
					for(var i = 0 ;i < tmp.length ; i++){
						$('#' + tmp[i].id).jqxDragDrop({dropTarget : $('#listSMDistribution'),revert : true, appendTo: 'body',  dragZIndex: 99999,
		                        dropAction: 'none',
		                        initFeedback: function (feedback) {
		                            feedback.height(25);
		                        }});
						bindEventSaleMan($('#' + tmp[i].id));
					}
		}
 		var cancelRoute = function(){
 			$('#routePopup').jqxWindow('close');
 		}
		var submitRoute = function(){
			var tmp = $("#listSMDistribution").jqxGrid('getboundrows');
				   	  	var arrData =[];
				   	  	var arrVal =[];
				   	  	if(tmp && tmp.length > 0 && rtId){
				   	  		for(var i = 0 ;i < tmp.length ; i++){
				   	  			if(tmp[i].partyId){
				   	  				arrData.push({
				   	  					partyId : tmp[i].partyId,
				   	  					routeId : rtId
				   	  				});
				   	  				arrVal.push({
				   	  					partyId : tmp[i].partyId,
				   	  					fullName : tmp[i].fullName
				   	  				});
				   	  				}
				   	  		}
				   	  	}
				   	 
				   	  	if(arrData && arrData.length > 0){
				   	  		$.ajax({
					   	  		url : 'distributionRouteSalesMan',
					   	  		data : {
					   	  			listDistribution : JSON.stringify(arrData)
					   	  		},
					   	  		async : false,
					   	  		type : 'POST',
					   	  		dataType : 'json',
					   	  		success : function(response){
					   	  		if(response._ERROR_MESSAGE_) {
					   	  			notificationRoute = response._ERROR_MESSAGE_;
					   	  			$("#messageNotification").html(notificationRoute);
					   	  			$("#messageNotification").css('display','block');
					   	  			$("#messageNotification").jqxNotification('open');
					   	  			setTimeout(function(){
					   	  				$("#messageNotification").css('display','none');
					   	  			},300);
					   	  		}else{
					   	  			$('#messageNotification').css('display','block');
					   	  			$('#messageNotification').html('${uiLabelMap.distributionRouteSuccess}');
					   	  			$('#messageNotification').jqxNotification('open');
					   	  			setTimeout(function(){
					   	  				$("#messageNotification").css('display','none');
					   	  			},300);
					   	  			var rows = $("#listSMOfRoute" + indexGlobal).jqxGrid('getboundrows');
					   	  			var rowsDel = new Array();
					   	  			$.each(rows,function(row){
					   	  				rowsDel.push(rows[row].uid);
					   	  			});
;					   	  			$('#listSMOfRoute'+indexGlobal).jqxGrid('deleterow',rowsDel);
					   	  			$('#listSMOfRoute'+indexGlobal).jqxGrid('addrow',0,arrVal);
					   	  		}
					   	  			$('#routePopup').jqxWindow('close');
					   	  		},
					   	  		error : function(request,status,xhr){
					   	  			$('#routePopup').jqxWindow('close');
					   	  			$('#messageNotificationError').css('display','block');
					   	  			$('#messageNotificationError').jqxNotification('open');
					   	  		}
					   	  	});
				   	  	}
				};
//						done for SaleMan
//						set UI for Customer
	var globallistCustomerOfRoute = [];	
	var sourceCustomer = {};
	var cancelRouteCus = function(){
		$("#routePopupCus").jqxWindow('close');
	}
	var submitRouteCus = function(){
		var tmp = $("#listCustomerDistribution").jqxGrid('getboundrows');
			   	  	var arrData = [];
			   	  	var arrVal = [];
			   	  	var grid = $('#listCustomerOfRoute' + indexGlobal);
			   	  	if(tmp && tmp.length > 0){
			   	  		for(var i = 0 ;i < tmp.length ; i++){
			   	  			if(tmp[i].partyId){
			   	  				arrData.push({
			   	  					partyId : tmp[i].partyId,
			   	  					routeId : rtIdCus
			   	  				});
			   	  				arrVal.push({
			   	  					partyId : tmp[i].partyId,
			   	  					groupName : tmp[i].groupName
			   	  				});
			   	  				}
			   	  		}
			   	  	}
			   	 
			   	  	if(arrData && arrData.length > 0){
			   	  		$.ajax({
				   	  		url : 'distributionRouteStores',
				   	  		data : {
				   	  			listDistributionStores : JSON.stringify(arrData)
				   	  		},
				   	  		async : false,
				   	  		type : 'POST',
				   	  		dataType : 'json',
				   	  		success : function(response){
				   	  			$.each(indexSelectedTmp,function(index){
									var tmp = {
				   	  					partyId : indexSelectedTmp[index].partyId,
				   	  					groupName : indexSelectedTmp[index].groupName
				   	  					};
				   	  			})
				   	  			indexSelectedTmp = new Array();	
				   	  			$('#gridCustomer').jqxGrid('updatebounddata');
				   	  			$('#routePopupCus').jqxWindow('close');
				   	  			$('#messageNotificationCus').css('display','block');
				   	  			$('#messageNotificationCus').html('${uiLabelMap.distributionRouteSuccess}');
				   	  			$('#messageNotificationCus').jqxNotification('open');
				   	  			var tmp = grid.jqxGrid('addrow',null,arrVal);
				   	  			setTimeout(function(){
				   	  				$('#messageNotificationCus').css('display','none');
				   	  			},500);
				   	  		},
				   	  		error : function(request,status,xhr){
				   	  			$('#routePopupCus').jqxWindow('close');
				   	  			$('#messageNotificationCus').css('display','block');
				   	  			$('#messageNotificationCus').html('${uiLabelMap.distributionRouteError}');
				   	  			$('#messageNotificationCus').jqxNotification('open');
				   	  			setTimeout(function(){
				   	  				$('#messageNotificationCus').css('display','none');
				   	  			},500);
				   	  		}
				   	  	});
			   	  	}
			};
	var initrowdetails = function(index, parentElement, gridElement, datarecord){
		globalParent = tabsdiv;
		globalData = datarecord;
		indexGlobal = index;
		var tabsdiv = null;
		var salemandetail = null;
		var customerdetail = null;
		tabsdiv = $($(parentElement).children()[0]);
		if(tabsdiv != null){
			salemandetail = tabsdiv.find('.salemandetail');
			customerdetail = tabsdiv.find('.customerdetail');
			var container = $('<div style=\"margin: 5px;\"></div>');
			container.appendTo($(salemandetail));
			container.appendTo($(customerdetail));
			var photocolumnsaleman =  $('<div style=\"margin : 3px 10px 20px 10px ; width: auto;\"></div>');
			salemandetail.append(photocolumnsaleman);
			var buttonsaleman = $('<div><button id="displayPopup" class="buttonRt" onclick="displayPopup(\'' + datarecord.routeId + '\',\'' + index + '\')"><i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.DistributionRoute}</button>&nbsp;&nbsp;<button class="buttonRt" onclick="removeSM(\''+ index +'\')"><i class="icon-trash"></i>&nbsp;${uiLabelMap.CommonDelete}</button></div>');
			photocolumnsaleman.append(buttonsaleman);
			var gridSaleMan = $('<div id="renderListSaleMan"></div>');
			photocolumnsaleman.append(gridSaleMan);
			var esaleman = $('<div></div>');
			var tmp = 'listSMOfRoute' + index;
			esaleman.attr('id',tmp);
			$('#renderListSaleMan').append(esaleman);
			initlistSMOfRoute(datarecord.routeId,index);
//			
			var photocolumncustom = $('<div style=\"margin : 3px 10px 18px 10px; width: auto;\"></div>');
			customerdetail.append($(photocolumncustom));
			var buttoncustom = $('<button id="displayPopupCus" class="buttonRt"  onclick="displayPopupCus(\'' + datarecord.routeId + '\',\'' + index + '\')"><i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.DistributionRoute}</button>&nbsp;&nbsp;<button class="buttonRt" onclick="removeCustomer(\''+ index + '\')"><i class="icon-trash"></i>&nbsp;${uiLabelMap.CommonDelete}</button>');
			photocolumncustom.append(buttoncustom);
			var gridCustom = $('<div id="renderListCustom"></div>');
			photocolumncustom.append(gridCustom);
			var ecustom = $('<div></div>');
			var tmp1 = 'listCustomerOfRoute' + index;
			ecustom.attr('id',tmp1);
			$('#renderListCustom').append(ecustom);
			initlistCustomerOfRoute(datarecord.routeId,'listCustomerOfRoute' + index);
			$(tabsdiv).jqxTabs({ theme: 'energyblue', width: '96%', height: 250});
		}
	};
//	set UI for Customer
	var indexSelectedTmp = new Array();
	var bindEventCustom = function(id){
		id.bind('dropTargetEnter',function(event){
				event.preventDefault();
				$(event.args.target).css('border','2px solid #000');
				id.jqxDragDrop('dropAction','none');	
				id.jqxDragDrop({revert : false});
			});	
		id.bind('dropTargetLeave',function(event){
				event.preventDefault();
				$(event.args.target).css('border','2px solid #000');
				$(this).jqxDragDrop('dropAction','none');	
			});	
		id.bind('dragEnd',function(event){
			event.preventDefault();
			var cell = $('#gridCustomer').jqxGrid('getselectedcell');
			if(cell === 'undefined' || cell == null){
				return;
			}
			var data = $('#gridCustomer').jqxGrid('getrowdata',cell.rowindex);
		     $('#listCustomerDistribution').css('border', '2px dashed #aaa');
		        var position = $.jqx.position(event.args);
		        var cellDis = $("#listCustomerDistribution").jqxGrid('getcellatposition', position.left, position.top);
		        var tmp = $("#listCustomerDistribution").jqxGrid('getrows');
		        for(var i=0;i<tmp.length;i++){
		        	if(data.partyId==tmp[i].partyId){
		        		return false;
					}
		        }
		        if (cellDis != null) {
		       	 	$("#listCustomerDistribution").jqxGrid('addrow',null,data);
		       	 	indexSelectedTmp.push(data);
		        }
			});	
	};
	var dataFieldCus = [
	              {name : "partyId", type:"String"},
	              {name: "groupName", type: "String"}
              ];
	var columncus = [
						{
							text : '${uiLabelMap.DACustomerId}',
							datafield : 'partyId',
							width : '200px'
						},
						{
							text : '${uiLabelMap.DACustomerName}',
							datafield : 'groupName'
						}
					];
	GridUtils.initGrid({url : 'JQGetListCustomerOfUserLogin&partyId=${parameters.userLogin.partyId?if_exists}',width : '100%',selectionmode: 'singlecell',showfilterrow : true,localization: getLocalization(),autoHeight : true},dataFieldCus,columncus,null,$('#gridCustomer'));
	$('#gridCustomer').on('bindingComplete',function(){
		bindDragDropCus();
	});
	$("#listCustomerDistribution").jqxGrid(
    		{
    		theme : 'olbius',
         width: '100%',
         selectionmode: 'singlecell',
         autoheight: true,
         altrows: true,
         localization: getLocalization(),
			pagesize : 5,
			pagesizeoptions: ['5', '10', '15'],
			pageable: true,
         source: { totalrecords: 1, unboundmode: true, datafields:
         [
             { name: 'partyId',type : 'string' },
             { name: 'groupName',type : 'string' }
         ]
         },
         columns: [
            { text: '${uiLabelMap.DACustomerId}', dataField: 'partyId', width: 300 },
            { text: '${uiLabelMap.DACustomerName}', dataField: 'groupName' }
         ]
     });
	function bindDragDropCus(){
		var tmp  = $('#gridCustomer').find("div[role='row']");
		for(var i = 0 ;i < tmp.length ; i++){	
			(function(i){
				$('#' + tmp[i].id).jqxDragDrop({dropTarget : $('#listCustomerDistribution'),revert : true, appendTo: 'body',  dragZIndex: 99999,
                    dropAction: 'none',
                    initFeedback: function (feedback) {
                        feedback.height(25);
                    }});
				bindEventCustom($('#' + tmp[i].id));
			})(i);
			
		}
	}
	
	var initlistCustomerOfRoute = function(routeId,grid){
		$.ajax({
		   	  		url : 'distributionRouteStores',
		   	  		async : false,
		   	  		type : 'POST',
		   	  		dataType : 'json',
		   	  		data : {
		   	  			globalParty : routeId
		   	  		},
		   	  		success : function(response){
		   	  		globallistCustomerOfRoute = new Array();
	  					$.each(response.result,function(index){
		   	  				globallistCustomerOfRoute.push({
			   	  				'partyId' : response.result[index].partyIdTo,
			   	  				'groupName' : response.result[index].groupName
		   	  				});
		   	  			});	
		   	  		},
		   	  		error : function(xhr,status,request){
		   	  		}
		   	  	});
		   	  	 sourceCustomer = {
					localdata : globallistCustomerOfRoute,
					datatype : 'array',
					datafields: [
						{name : 'partyId',type: 'string'},
						{name : 'groupName',type: 'string'}
					],
				};
   			  var dataAdapter = new $.jqx.dataAdapter(sourceCustomer,{autoBind : true});	
   	  	if(globallistCustomerOfRoute){
	   	  		$('#' + grid).jqxGrid({
							width : '100%',
							height : 168,
							filterable : true,
							showfilterrow : true,
							localization: getLocalization(),
							altrows: true,
							pagesize : 5,
							pagesizeoptions: ['5', '10', '15'],
    						pageable: true,
							source : dataAdapter,
							selectionmode: 'singlecell',
							columns : [${columnListCust}]
				});
	   	  	}
	}
	var rtIdCus;
	var displayPopupCus = function(routeId,index){
		rtIdCus = routeId;
		indexGlobal = index;
		$('#routePopupCus').css('display','block');
		$('#routePopupCus').jqxWindow({ width: 800, height : 500,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
		$('#routePopupCus').jqxWindow('open');
	}
	$('#routePopupCus').on('open', function(){
		bindDragDropCus();
	})
	$('#routePopupCus').on('close',function(){
		$('#routePopupCus').css('display','none');
		indexSelectedTmp = new Array();
	});
	 $("#messageNotificationCus").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "info",appendContainer: "#notification"
            });
     $("#messageNotificationErrorCus").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "info",appendContainer: "#notification"
            });
     var removeCustomer = function(index){
    		(function(index){
    			$('#deleteDialogCus').text('${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?');
    			$('#deleteDialogCus').dialog({
    			resizable : false,
    			height : 180,
    			modal : true,
    			buttons : {
    				'${StringUtil.wrapString(uiLabelMap.wgok)}' : function(){
    					$(this).dialog('close');
    					$('#listCustomerOfRoute'+index).jqxGrid('clearfilters');
    					var cell = $('#listCustomerOfRoute'+index).jqxGrid('getselectedcell');
 			   		if(!cell) return;
 			   		var data =   $('#listCustomerOfRoute'+index).jqxGrid('getrowdata',cell.rowindex);
 			   		$.ajax({
 			   			url : 'removeCustomerOutRoute',
 			   			type : 'POST',
 			   			dataType : 'json',
 			   			data : {
 			   				partyId : data.partyId
 			   			},
 			   			async : false,
 			   			cache : false,
 			   			success : function(response,status,xhr){
 			   				if(response._ERROR_MESSAGE_){
 				   	  			$("#messageNotificationCus").html(response._ERROR_MESSAGE_);
 				   	  			$("#messageNotificationCus").css('display','block');
 				   	  			$("#messageNotificationCus").jqxNotification('open');
 				   	  			setTimeout(function(){
 				   	  				$("#messageNotificationCus").css('display','none');
 				   	  			},300);
 			   				}else{
 			   					$('#listCustomerOfRoute'+index).jqxGrid('deleterow',cell.rowindex);
 			   					var obj = {'partyId': data.partyId,'groupName' : data.groupName};
 			   					dataCs.push(obj);
 			   					$("#messageNotificationCus").html(response._EVENT_MESSAGE_);
 				   	  			$("#messageNotificationCus").css('display','block');
 				   	  			$("#messageNotificationCus").jqxNotification('open');
 				   	  			setTimeout(function(){
 				   	  				$("#messageNotificationCus").css('display','none');
 				   	  			},300);
 			   				}
 			   			},
 			   			error : function(){
 			   			}
 			   		});
 	   			},
 	   			'${StringUtil.wrapString(uiLabelMap.wgcancel)}' : function(){
 	   				$(this).dialog('close');
 	   			}
    			}
    		});
 		$('#deleteDialogCus').parent().css('z-index','100000000000000000');
    		}(index));
    }
//	done Customer
//	Set UI for SaleMan
	var globallistSMOfRoute = [];
	var initlistSMOfRoute = function(routeId,index){
		$.ajax({
	   	  		url : 'distributionRouteSalesMan',
	   	  		async : false,
	   	  		type : 'POST',
	   	  		dataType : 'json',
	   	  		data : {
	   	  			globalParty : routeId
	   	  		},
	   	  		success : function(response){
	   	  		globallistSMOfRoute = new Array();
  					$.each(response.result,function(index){
	   	  				globallistSMOfRoute.push({
		   	  				'partyId' : response.result[index].partyIdFrom,
		   	  				'fullName' : response.result[index].groupName
	   	  				});
	   	  			});	
	   	  		},
	   	  		error : function(xhr,status,request){
	   	  		}
	   	  	});
	   	  	var source = {
				localdata : globallistSMOfRoute,
				datatype : 'array',
				datafields: [
					{name : 'partyId',type: 'string'},
					{name : 'fullName',type: 'string'}
				],
				url : 'distributionRouteSalesMan',
				data : {
	   	  			globalParty : routeId
	   	  		},
	   	  		virtualmode : true,
				rendergridrows: function () {
	                return dataAdapter.records;
	            },
				datatype : 'json',
				type : 'POST',
				beforeLoadComplete : function(records){
				}
			};
	   	  var dataAdapter = new $.jqx.dataAdapter(source,{autoBind : true});	
   	  	if(globallistSMOfRoute){
	   	  		$('#listSMOfRoute'+index).jqxGrid({
							width : '100%',
							height : 168,
							autoHeight :false,
							localization: getLocalization(),
							pagesize : 5,
							pagesizeoptions: ['5', '10', '15'],
							pageable: true,							
							filterable : false,
							showfilterrow : false,
							altrows: true,
							source : dataAdapter,
							selectionmode: 'singlecell',
							columns : [${columnListCust}]
				});
	   	  	}
	};
	var displayPopup = function(routeId,index){
		rtId = routeId;
		indexGlobal = index;
		$('#routePopup').css('display','block');
		$('#routePopup').jqxWindow({ width: 800, height : 425,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
		$('#routePopup').jqxWindow('open');
	};
	$('#routePopup').on('open',function(){
		bindDragDrop();
	})
	$('#routePopup').on('close',function(){
		$('#routePopup').css('display','none');
	});
	 $("#messageNotification").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "info",appendContainer: "#notification"
            });
     $("#messageNotificationError").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "error",appendContainer: "#notification"
            });
     var removeSM = function(index){
    	   	$('#deleteDialog').text('${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?');
    	   		$('#deleteDialog').dialog({
    	   			resizable : false,
    	   			height : 180,
    	   			modal : true,
    	   			buttons : {
    	   				'${StringUtil.wrapString(uiLabelMap.wgok)}' : function(){
    						var cell = $('#listSMOfRoute'+index).jqxGrid('getselectedcell');
    				   		var data =   $('#listSMOfRoute'+index).jqxGrid('getrowdata',cell.rowindex);
    				   		$(this).dialog('close');
    				   		$.ajax({
    				   			url : 'removeSMOutRoute',
    				   			type : 'POST',
    				   			dataType : 'json',
    				   			data : {
    				   				partyId : data.partyId
    				   			},
    				   			async : false,
    				   			cache : false,
    				   			success : function(response,status,xhr){
    				   				$('#listSMOfRoute'+index).jqxGrid('deleterow',cell.rowindex);
    				   				if(response._ERROR_MESSAGE_){
    					   	  			$("#messageNotification").html(response._ERROR_MESSAGE_);
    					   	  			$("#messageNotification").css('display','block');
    					   	  			$("#messageNotification").jqxNotification('open');
    					   	  			setTimeout(function(){
    					   	  				$("#messageNotification").css('display','none');
    					   	  			},300);
    				   				}else{
    				   					$("#messageNotification").html(response._EVENT_MESSAGE_);
    					   	  			$("#messageNotification").css('display','block');
    					   	  			$("#messageNotification").jqxNotification('open');
    					   	  			setTimeout(function(){
    					   	  				$("#messageNotification").css('display','none');
    					   	  			},300);
    				   				}
    				   			},
    				   			error : function(){
    				   			}
    				   		});
    	   				},
    					'${StringUtil.wrapString(uiLabelMap.wgcancel)}' : function(){
    		   				$(this).dialog('close');
    		   			}
    	   				}
    				});
    	   		$('#deleteDialog').parent().css('z-index','100000000000000000');
    	   };
//     
//     
	$("#alterpopupWindow").jqxWindow({
	        width: 390, height : 220,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
	    });
	    $('#routeAdd').jqxInput({width : '195px',height : '25px'});
	    $('#routeScheduleAdd').jqxComboBox({multiSelect : true,displayMember : 'name',autoDropDownHeight : true,valueMember : 'routeScheduleId',source : schedule,width : '200px',height : '25px'});
	    $('#formAdd').jqxValidator({
	    	rules :  [{input : routeAdd,message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',action : 'blur',rule : 'required'},
	    			{input : routeScheduleAdd,message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',action : 'blur',rule : function(input,commit){
	    				var routeSchedule =  $('#routeScheduleAdd').jqxComboBox('getSelectedItems');
	    				if(!routeSchedule || routeSchedule.length == 0 ){
	    					return false;
	    				}
	    				return true;
	    			}}
	    	]
	    });
	$('#alterSave').click(function(){
		if(!$('#formAdd').jqxValidator('validate')){
			return;
		};
		var itemsVal = "";
		(function(){
			var items = $('#routeScheduleAdd').jqxComboBox('getSelectedItems');
			if(items && items.length > 0 ){
				for(var i = 0 ; i < items.length ; i ++){
					if(items[i].value){
						itemsVal += items[i].value + ",";
					}
				}
			}
		})();
		var row =  {
			routeName : $('#routeAdd').val(),
			infoRoute : itemsVal
		};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	});
</script>
