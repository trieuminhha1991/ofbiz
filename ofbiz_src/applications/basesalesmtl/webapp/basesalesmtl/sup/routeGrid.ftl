<#-- Init Grid List Route Of Sup -->
<#assign dataField = "[
		{name : 'routeId',type : 'string'},
		{name : 'description',type : 'string'},
		{name : 'scheduleRoute',type : 'string'}
		]"/>
<#assign columnlist= "	
				 { text : '${uiLabelMap.BSRouteId}',datafield : 'routeId',width : '150px'},
				 { text : '${uiLabelMap.BSRouteName}',datafield : 'description'},
				 {text : '${uiLabelMap.BSRouteSchedule}',datafield : 'scheduleRoute',columntype : 'combobox',cellsrenderer : function(row,columnfield,value){
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
						 }
			 	 	}
"/>
<div id="notification" style="width : 100%;"></div>
<#assign rowdetailstemplateAdvance = "<ul class='margin-left: 10px;'><li>${StringUtil.wrapString(uiLabelMap.BSViewSalemansDetail)}</li><li>${StringUtil.wrapString(uiLabelMap.BSViewCustomerDetail)}</li></ul><div class='salemandetail'></div><div class='customerdetail'></div>" />
<@jqGrid filtersimplemode="true" filterable="true" editable="true" addrefresh="true" editrefresh="true" updateoffline="false" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addrow="true" deleterow="true" rowdetailsheight="275" initrowdetails="true" rowdetailstemplateAdvance = rowdetailstemplateAdvance initrowdetailsDetail="grids.initrowdetails"
	 url="jqxGeneralServicer?sname=JQgetListRoute"
	 createUrl="jqxGeneralServicer?sname=createRoute&jqaction=C" addColumns="routeName;infoRoute"
	 updateUrl="jqxGeneralServicer?sname=updateRoute&jqaction=U" editColumns="routeId;scheduleRoute(java.util.List);description"
	 removeUrl="jqxGeneralServicer?sname=deleteRoute&jqaction=D" deleteColumn="routeId"
	 />