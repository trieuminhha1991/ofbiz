<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdragdrop.js"></script>
<style type="text/css">
	.buttonRt{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		float : right;
		margin-right : 10px;
	}
	.form-action{
		right : 10px;
	}
	.form-window-content{
		overflow : hidden;
	}
</style>
<#assign index = 0 />
<#assign listCustomerOfSup = Static["com.olbius.util.SalesPartyUtil"].getListCustomerBySup(delegator,'${parameters.userLogin.partyId}',null,null,null) !>
<#assign listCustomer = delegator.findList("PartyRoleAndPartyDetail",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId","DELYS_CUSTOMER_GT"),null,null,null,false)!>
<#assign dataField = "[
	{name : 'routeId',type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'scheduleRoute',type : 'string'}
]"/>
<#assign listDayOfWeek = delegator.findList("DayOfWeek",null,null,null,null,false) !>
<script type="text/javascript">
	var listCusOfSup = [
        <#list listCustomerOfSup as cus>
        	{
        		cusId : '${cus.partyId?if_exists}',	
     			name : "${StringUtil.wrapString(cus.groupName?default(''))}"
        	},
        </#list>
    ];
	var listDay = [
		<#list listDayOfWeek as day>
			{
				dayId : '${day.dayOfWeek?if_exists}',
				description : "${StringUtil.wrapString(day.description?default(''))}"
			},
		</#list>
	];
	 var listCus = [
     	<#list listCustomer as cus>
     		{
     			cusId : '${cus.partyId?if_exists}',	
     			name : "${StringUtil.wrapString(cus.groupName?default(''))}"
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
						 					routeScheduleStr += '- ' +  listDay[i].description;
						 					if(listDay[i+1] !== 'undefined'){
						 						routeScheduleStr += ' - ';
							 					}
						 					break;
						 				}
						 			}
					 			}
					 		}
					 		return '<span style=\"font-weight : bold;color : #438EB9;\"><i class=\"fa-road\"></i>' + routeScheduleStr +'</span>';
					 }}
"/>
<div id="notification" style="width : 100%;"></div>
<#assign rowdetailstemplateAdvance = "<div style='margin-left : 10px;'><div class='jobRequirement'></div></div>"/>	
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
	var dataCs;
	var dataCus = [
		<#list listCustomerOfSup as cus>
			{
			cusId : '${cus.partyId?if_exists}',
			name : '${StringUtil.wrapString(cus.groupName?default(''))}'
			},
		</#list>
	];
	var dataCusNotAvaiable;
	(function(){
		$.ajax({
   	  		url : 'distributionRouteStores',
   	  		async : false,
   	  		type : 'POST',
   	  		dataType : 'json',
   	  		success : function(response){
   	  		dataCusNotAvaiable = new Array();
				$.each(response.result,function(index){
   	  				dataCusNotAvaiable.push({
	   	  				'cusId' : response.result[index].partyIdTo,
	   	  				'name' : response.result[index].groupName
   	  				});
   	  			});	
   	  		},
   	  		error : function(xhr,status,request){
   	  		}
   	  	});
	}());
	dataCs = dataCus;
	var source = {
		localdata : dataCs,
		datatype : 'array',
		datafields: [
			{name : 'cusId',type: 'string'},	
			{name : 'name',type: 'string'}
		],
		updaterow: function (rowid, rowdata, commit) {
			commit(true);
                    // synchronize with the server - send update command
                    // call commit with parameter true if the synchronization with the server is successful 
                    // and with parameter false if the synchronization failder.
                    commit(true);
            }
	};
var indexSelectedTmp = new Array();
var bindEvent = function(id){
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
	        var tmp = $('#listCustomerDistribution').jqxGrid('getrows');
	        for(var i=0;i<tmp.length;i++){
	        	if(data.cusId==tmp[i].cusId){
	        		return false;
				}
	        }
	        if (cellDis != null) {
	       	 	$("#listCustomerDistribution").jqxGrid('addrow',null,data);
	       	 	indexSelectedTmp.push(data);
	        }
		});	
};

var initgridCustomer = function(parent,datarecord,routeId){		
	//init Grid Customer 
	dataAdapter = new $.jqx.dataAdapter(source);
		$('#gridCustomer').jqxGrid({
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
							text : '${uiLabelMap.DACustomerId}',
							datafield : 'cusId',
							width : '200px'
						},
						{
							text : '${uiLabelMap.DACustomerName}',
							datafield : 'name'
						}
					]
		});
			//init jqxDragDrop for each element of grid
				setTimeout(function(){
				var tmp  = $('#gridCustomer').find("div[role='row']");
					for(var i = 0 ;i < tmp.length ; i++){	
						(function(i){
							$('#' + tmp[i].id).jqxDragDrop({dropTarget : $('#listCustomerDistribution'),revert : true, appendTo: 'body',  dragZIndex: 99999,
		                        dropAction: 'none',
		                        initFeedback: function (feedback) {
		                            feedback.height(25);
		                        }});
							bindEvent($('#' + tmp[i].id));
						})(i);
						
					}
				 },500);
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
			                    { name: 'cusId',type : 'string' },
			                    { name: 'name',type : 'string' }
			                ]
			                },
			                columns: [
			                   { text: '${uiLabelMap.DACustomerId}', dataField: 'cusId', width: 300 },
			                   { text: '${uiLabelMap.DACustomerName}', dataField: 'name' }
			                ]
			            });      
			};
	var globalParent;		
	var globalData;
	var globallistCustomerOfRoute = [];	
	var sourceCus = {};
	var indexGlobal;
	var submitRoute = function(){
			var tmp = $("#listCustomerDistribution").jqxGrid('getboundrows');
				   	  	var arrData = [];
				   	  	var arrVal = [];
				   	  	var grid = $('#listCustomerOfRoute' + indexGlobal);
				   	  	if(tmp && tmp.length > 0){
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
					   	  					cusId : indexSelectedTmp[index].cusId,
					   	  					name : indexSelectedTmp[index].name
					   	  					};			
				   	  					$.each(dataCs,function(index){
						   	  				if(dataCs[index].cusId == tmp.cusId && dataCs[index].name == tmp.name){
						   	  					dataCs.splice(index,1);	
						   	  					return false;
						   	  				}
					   	  				})				   	  			
					   	  			})
					   	  			indexSelectedTmp = new Array();	
					   	  			$('#gridCustomer').jqxGrid('updatebounddata');
					   	  			$('#routePopup').jqxWindow('close');
					   	  			$('#messageNotification').css('display','block');
					   	  			$('#messageNotification').html('${uiLabelMap.distributionRouteSuccess}');
					   	  			$('#messageNotification').jqxNotification('open');
					   	  			var tmp = grid.jqxGrid('addrow',null,arrVal);
					   	  			setTimeout(function(){
					   	  				$('#messageNotification').css('display','none');
					   	  			},500);
					   	  		},
					   	  		error : function(request,status,xhr){
					   	  			$('#routePopup').jqxWindow('close');
					   	  			$('#messageNotification').css('display','block');
					   	  			$('#messageNotification').html('${uiLabelMap.distributionRouteError}');
					   	  			$('#messageNotification').jqxNotification('open');
					   	  			setTimeout(function(){
					   	  				$('#messageNotification').css('display','none');
					   	  			},500);
					   	  		}
					   	  	});
				   	  	}
				};
			var initrowdetails = function (index, parentElement, gridElement, datarecord) {
						var parent = $($(parentElement).children()[0]);
						globalParent = parent;
						globalData = datarecord;
						parent.css({
							margin: '-18px 0 20px',
							padding: '10px 20px 10px 0',
							height: '70%'
						});
						var jobRequirement = null;
						jobRequirement = parent.find('.jobRequirement');
						var container = $('<div style="margin : 5px;width : 100%;"></div>');
						jobRequirement.append(container);
						var leftColumn = $('<div  style="float:left;width :50%"></div>');
						var rightColumn = $('<div id="bodyRowDetails"><button id="displayPopup" class="buttonRt"  onclick="displayPopup(\'' + datarecord.routeId + '\',\'' + index + '\')"><i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.DistributionRoute}</button>&nbsp;&nbsp;<button class="buttonRt" onclick="removeCustomer(\''+ index + '\')"><i class="icon-trash"></i>&nbsp;${uiLabelMap.CommonDelete}</button><div id="renderList"></div><div style="width : 100%;display : none;"><div style="float:right;width :50%"></div></div>');
						container.append(leftColumn);
						container.append(rightColumn);
						var e = $('<div></div>');
						$('#renderList').append(e);
						e.attr('id','listCustomerOfRoute' + index);
						initlistCustomerOfRoute(datarecord.routeId,'listCustomerOfRoute' + index);
					};	
				
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
			   	  				'cusId' : response.result[index].partyIdTo,
			   	  				'name' : response.result[index].groupName
		   	  				});
		   	  			});	
		   	  		},
		   	  		error : function(xhr,status,request){
		   	  		}
		   	  	});
		   	  	 sourceCus = {
					localdata : globallistCustomerOfRoute,
					datatype : 'array',
					datafields: [
						{name : 'cusId',type: 'string'},
						{name : 'name',type: 'string'}
					],
				};
   			  var dataAdapter = new $.jqx.dataAdapter(sourceCus,{autoBind : true});	
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
	
	var rtId;	
	var displayPopup = function(routeId,index){
		filterCustomer();
		rtId = routeId;
		indexGlobal = index;
		initgridCustomer(parent,globalData);
		$('#routePopup').css('display','block');
		$('#routePopup').jqxWindow({ width: 800, height : 500,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
		$('#routePopup').jqxWindow('open');
	}
	$('#routePopup').on('close',function(){
		$('#routePopup').css('display','none');
		indexSelectedTmp = new Array();
	});
	 $("#messageNotification").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "info",appendContainer: "#notification"
            });
     $("#messageNotificationError").jqxNotification({
                opacity: 1, autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "info",appendContainer: "#notification"
            });       
   var removeCustomer = function(index){
   		(function(index){
   			$('#deleteDialog').text('${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?');
   			$('#deleteDialog').dialog({
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
			   				cusId : data.cusId
			   			},
			   			async : false,
			   			cache : false,
			   			success : function(response,status,xhr){
			   				if(response._ERROR_MESSAGE_){
				   	  			$("#messageNotification").html(response._ERROR_MESSAGE_);
				   	  			$("#messageNotification").css('display','block');
				   	  			$("#messageNotification").jqxNotification('open');
				   	  			setTimeout(function(){
				   	  				$("#messageNotification").css('display','none');
				   	  			},300);
			   				}else{
			   					$('#listCustomerOfRoute'+index).jqxGrid('deleterow',cell.rowindex);
			   					var obj = {'cusId': data.cusId,'name' : data.name};
			   					dataCs.push(obj);
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
   		}(index));
   };    	
	var filterCustomer = function(){
		dataCusNotAvaiable,dataCs;
		var dataTmp;
		$.each(dataCusNotAvaiable,function(indexcus){
			$.each(dataCs,function(index){
				if(dataCs[index].cusId == dataCusNotAvaiable[indexcus].cusId && dataCusNotAvaiable[indexcus].name == dataCs[index].name){
					dataCs.splice(index,1);return false;
				}
			});
		});
	
	}
</script>
<div id="deleteDialog"></div>
<div id="routePopup" style="display : none;" class="hide">
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
			<button style="display:block;" id="submitRt" class="btn btn-primary form-action-button pull-right" onclick="submitRoute()"><i class="icon-ok"></i>&nbsp;${uiLabelMap.Confirm}</button>
		</div>
	</div>
</div>