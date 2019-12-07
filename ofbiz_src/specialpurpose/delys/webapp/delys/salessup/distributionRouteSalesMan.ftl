<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdragdrop.js"></script>
<style type="text/css">
	.buttonRt{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		float : right;
		margin-left : 10px;
	}
</style>
<#assign listSMOfUserLogin = Static["com.olbius.util.SalesPartyUtil"].getListSalesmanActiveBySupervisor('${parameters.userLogin.partyId}',null,null,null,delegator) !>
<#assign listSM = delegator.findList("PartyRoleAndPartyDetail",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId","DELYS_SALESMAN_GT"),null,null,null,false)!>
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
				description : '${StringUtil.wrapString(day.description?default(''))}'
			},
		</#list>
	];
	 var listCus = [
     	<#list listSM as cus>
     		{
     			cusId : '${cus.partyId?if_exists}',	
     			name : '${StringUtil.wrapString(cus.firstName?default(''))} ${StringUtil.wrapString(cus.middleName?default(''))} ${StringUtil.wrapString(cus.lastName?default(''))}'
     		},
     	</#list>
     ];   
</script>
<#assign columnlist= "	
					{ text : '${uiLabelMap.DARouteId}',datafield : 'routeId',width : '150px'},
					 { text : '${uiLabelMap.DADescription}',datafield : 'description'},
					 {text : '${uiLabelMap.DARouteSchedule}',datafield : 'scheduleRoute',filterable : false,cellsrenderer : function(row,columnfield,value){
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
						 					routeScheduleStr += '- ' +  listDay[i].description + ' - ';
						 					break;
						 				}
						 			}
					 			}
					 		}
					 		return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
					 }}
"/>
<div id="notification" style="width : 100%;"></div>
<#assign rowdetailstemplateAdvance = "<div style='margin-left : 10px;'><div class='distributionSM'></div></div>"/>	
<@jqGrid filtersimplemode="true" filterable="true" editable="false" addrefresh="true" showtoolbar="true" rowdetailsheight="200" rowdetailstemplateAdvance=rowdetailstemplateAdvance initrowdetails="true" initrowdetailsDetail="initrowdetails" dataField=dataField columnlist=columnlist  clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQgetListRoute"
		 />
<div id="messageNotification" style="display : none">
</div>
<div id="messageNotificationError" style="display : none">
</div>
<#assign columnListCust = "
							{
								text : '${uiLabelMap.DACustomerId}',
								datafield : 'cusId',
								width : '200px'
							},
							{
								text : '${uiLabelMap.DACustomerName}',
								datafield : 'name'
							}
							"/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme  = $.jqx.theme;

var rtId;
var bindEvent = function(id){
	id.bind('dropTargetEnter',function(event){
			id.jqxDragDrop('dropAction','none');	
			id.jqxDragDrop({revert : false});
		});	
	id.bind('dropTargetLeave',function(event){
			$(this).jqxDragDrop('dropAction','none');	
		});	
	id.bind('dragEnd',function(event){
		var cell = $('#gridSM').jqxGrid('getselectedcell');
		if(cell === 'undefined' || cell == null){
			return;
		}
		var data = $('#gridSM').jqxGrid('getrowdata',cell.rowindex);
	     var rowindex = $('#'+event.args.element.id).jqxGrid('getselectedcell');
	        var position = $.jqx.position(event.args);
	        var cell = $("#listSMDistribution").jqxGrid('getcellatposition', position.left, position.top);
	        if (cell != null) {
	            $("#listSMDistribution").jqxGrid('setcellvalue', cell.row,'cusId', data.cusId);
	            $("#listSMDistribution").jqxGrid('setcellvalue', cell.row, 'name', data.name);
	        }
		});	
};


var initgridSalesMan= function(parent,datarecord){		
	//init Grid Salesman 
	var dataSM = [
		<#list listSMOfUserLogin as cus>
     		{
     			cusId : '${cus.partyId?if_exists}',	
     			name : '${StringUtil.wrapString(cus.firstName?default(''))} ${StringUtil.wrapString(cus.middleName?default(''))} ${StringUtil.wrapString(cus.lastName?default(''))}'
     		},
     	</#list>
	];
	var source = {
		localdata : dataSM,
		datatype : 'array',
		datafields: [
			{name : 'cusId',type: 'string'},
			{name : 'name',type: 'string'}
		]
	};
	var dataAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
		$('#gridSM').jqxGrid({
					theme : 'olbius',
					width : '100%',
					autoHeight : true,
					filterable : true,
					showfilterrow : true,
					altrows: true,
					localization: getLocalization(),
					pagesize : 5,
					pagesizeoptions: ['5', '10', '15'],
					pageable: true,
					source : dataAdapter,
					selectionmode: 'singlecell',
					columns : [
						{
							text : '${uiLabelMap.DAEmployeeId}',
							datafield : 'cusId',
							width : '200px'
						},
						{
							text : '${uiLabelMap.DAEmployeeName}',
							datafield : 'name'
						}
					]
		});
			//init jqxDragDrop for each element of grid
				setTimeout(function(){
				var tmp  = $('#gridSM').find("div[role='row']");
					for(var i = 0 ;i < tmp.length ; i++){	
						$('#' + tmp[i].id).jqxDragDrop({dropTarget : $('#listSMDistribution'),revert : true, appendTo: 'body',  dragZIndex: 99999,
		                        dropAction: 'none',
		                        initFeedback: function (feedback) {
		                            feedback.height(25);
		                        }});
							bindEvent($('#' + tmp[i].id));
					}
				 },500);
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
			                    { name: 'cusId',type : 'string' },
			                    { name: 'name',type : 'string' }
			                ]
			                },
			                columns: [
			                   { text: '${uiLabelMap.DAEmployeeId}', dataField: 'cusId', width: 300 },
			                   { text: '${uiLabelMap.DAEmployeeName}', dataField: 'name' }
			                ]
			            });      
			      $("#listSMDistribution").on('cellvaluechanged',function(){
			      		$('#submitRt').css('display','block');
			      });      
			};
	var globalParent;		
	var globalData;
	var notificationRoute = '';
	var indexGlobal;
	var submitRoute = function(){
			var tmp = $("#listSMDistribution").jqxGrid('getboundrows');
				   	  	var arrData =[];
				   	  	var arrVal =[];
				   	  	if(tmp && tmp.length > 0 && rtId){
				   	  		for(var i = 0 ;i < tmp.length ; i++){
				   	  			if(tmp[i].cusId){
				   	  				arrData.push({
				   	  					partyId : tmp[i].cusId,
				   	  					routeId : rtId
				   	  				});
				   	  				arrVal.push({
				   	  					cusId : tmp[i].cusId,
				   	  					name : tmp[i].name
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
			var initrowdetails = function (index, parentElement, gridElement, datarecord) {
						var parent = $($(parentElement).children()[0]);
						globalParent = parent;
						globalData = datarecord;
						indexGlobal = index;
						parent.css({
							margin: '-18px 0 20px',
							padding: '10px 20px 10px 0',
							height: '70%'
						});
						var distributionSM = null;
						distributionSM = parent.find('.distributionSM');
						var container = $('<div style="margin: 5px; width: 100%;"></div>');
						distributionSM.append(container);
						var leftColumn = $('<div id="bodyRowDetail" style="float:left;width :50%"></div>');
						var rightColumn = $('<div><button id="displayPopup" class="buttonRt" onclick="displayPopup(\'' + datarecord.routeId + '\',\'' + index + '\')"><i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.DistributionRoute}</button>&nbsp;&nbsp;<button class="buttonRt" onclick="removeSM(\''+ index +'\')"><i class="icon-trash"></i>&nbsp;${uiLabelMap.CommonDelete}</button><div id="renderList"></div><div style="width : 100%;display : none;"><div style="float:right;width :50%"></div></div>');
						container.append(leftColumn);
						container.append(rightColumn);
						var e = $('<div></div>');
						var tmp = 'listSMOfRoute' + index;
						e.attr('id',tmp);
						$('#renderList').append(e);
						initlistSMOfRoute(datarecord.routeId,index);
					};	
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
		   	  				'cusId' : response.result[index].partyIdFrom,
		   	  				'name' : response.result[index].groupName
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
					{name : 'cusId',type: 'string'},
					{name : 'name',type: 'string'}
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
	}				
	var displayPopup = function(routeId,index){
		rtId = routeId;
		indexGlobal = index;
		initgridSalesMan(parent,globalData);
		$('#routePopup').css('display','block');
		$('#routePopup').jqxWindow({ width: 800, height : 425,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
		$('#routePopup').jqxWindow('open');
	}
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
			   				cusId : data.cusId
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
   
</script>
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
			<button style="display:none;" id="submitRt" class="btn btn-primary form-action-button pull-right" onclick="submitRoute()"><i class="icon-ok"></i>&nbsp;${uiLabelMap.DAConfirm}</button>
		</div>
	</div>
</div>