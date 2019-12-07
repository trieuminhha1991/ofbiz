 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<style type="text/css">
	.buttonRt{
		margin-left : 10px !important;
		padding-left : 60px !important;
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		margin-left : 30px;
	}
	.buttonTheme{
		color: #438eb9 !important;
		border-color: #FFF !important;
		margin-top: 5px;
		font-size: 14px!important;
		background: none!important;
		border: 1px solid #FFF;
	}
</style>
<#assign dataField = "[
		{name : 'productPromoId',type : 'string'},
		{name : 'promoName',type : 'string'},
		{name : 'fromDate',type : 'date',other : 'Timestamp'},
		{name : 'thruDate',type : 'date',other : 'Timestamp'}
]" />
<#assign columnlist = "
			{text : '${uiLabelMap.DAProductPromoId}',datafield : 'productPromoId',width : '15%'},
			{text : '${uiLabelMap.DAPromoName}',datafield : 'promoName',width : '40%'},
			{text : '${uiLabelMap.DAFromDate}',datafield : 'fromDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range',width : '15%'},
			{text : '${uiLabelMap.DAThroughDate}',datafield : 'thruDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range',width : '15%'},
			{text : '${uiLabelMap.DACommonRegister}',width : '15%',filterable : false,sortable :false,cellsrenderer : function(row,columnfield,value){
				var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
				return '<button class=\"buttonRt\" onclick=\"displayList(' + \"'\" + data.productPromoId + \"'\" + ')\"><i class=\"fa fa-list\"></i></button>';
			}}
"/>

<@jqGrid filtersimplemode="true" filterable="true" editable="true" addrefresh="true" showtoolbar="true"  dataField=dataField columnlist=columnlist  clearfilteringbutton="true"  
		 url="jqxGeneralServicer?sname=JQgetListExhibitedRegister" 
		 createUrl="jqxGeneralServicer?sname=exhibitedRegisterSUP&jqaction=C" addColumns="customerId;productPromoId;ruleId"
		 />
<div id="notification" style="display : none;"></div>	
<div id="noti"></div>	 
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.DAExhibitedRegister}</div>
		<div style="overflow: hidden;">
			<div id="containerErr"></div>
	    	<form id="formAdd" class="form-horizontal">
	    		<div class='row-fluid form-window-content'>
			    		<div class='span12'>
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.DACustomer}
			    				</div>
			    				<div class='span7'>
		    						<div id="customerAdd">
		    							<div id="gridCustomer"></div>
	    							</div>
			    				</div>
		    				</div>
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.DAExhibitedPromotion}
			    				</div>
			    				<div class='span7'>
			    					<div id="exhibitedAdd"></div>
			    				</div>
		    				</div>
		    				<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.DALevel}
			    				</div>
			    				<div class='span7'>
			    					<div id="levelAdd"></div>
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
	    	<!-- End -->
  </div>
</div> 
<div id="listCustomerRegisted" style="display:none;">
	<div>${uiLabelMap.DAListExhibitedRegister}</div>
	<div style="overflow: hidden;"><div id="containerEx" style="width : 100%;"></div><div id="gridListCustomer" style="margin :0px 0 0 5px !important;"></div></div>
</div>
<div id="deleteDialog"></div>
<script type="text/javascript">
	//prepare data exhibited
	var listExhibited = [];
	var listLevel = [];
	var customerSelected;
	(function(){
		$.ajax({
			url : 'getListExhibited',
			datatype : 'json',
			type : 'POST',
			cache  : false,
			async : false,
			success : function(response,status,xhr){
				if(response.listIterator && response.listIterator.length){
					listExhibited = response.listIterator;
				}
			},
			error : function(){
			}
		});		
	}());
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$('#alterpopupWindow').jqxWindow({theme : theme,resizable : false,width : 410,height : 250,isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });
	$('#customerAdd').jqxDropDownButton({width : 200,height : 24});
	$('#levelAdd').jqxDropDownList({width : 200,height : 24,disabled : false,selectionRenderer : function(){
		var item = $('#levelAdd').jqxDropDownList('getSelectedItem');
		if(item) return  '<span>' + item.label + '</span>';	
		return '<span style=\"margin-top : 5px !important;\">${uiLabelMap.DASelectList}</span>';
	}});
	$('#exhibitedAdd').on('select',function(event){
		var id = event.args.item.value;
		if(id){
		}
	});
	$('#dropdownlistContentexhibitedAdd').css('margin-top','5px','important');
	$('#dropdownlistContentlevelAdd').css('margin-top','5px','important');
	$('#alterpopupWindow').on('close',function(){
		$('#exhibitedAdd').jqxDropDownList('clearSelection');
		$('#customerAdd').val('');
		$('#levelAdd').jqxDropDownList('clearSelection');
		$('#levelAdd').jqxDropDownList({disabled : false});
		listLevel = new Array();
	});
	//init customer grid
	var sourcecm = {
		datafields : [
			{name : 'partyId',type : 'string'},
			{name : 'groupName',type : 'string'}
		],
		cache: false,
		beforeprocessing: function (data) {
                sourcecm.totalrecords = data.TotalRows;
        },
		datatype: "json",
		filter: function () {
		   	// update the grid and send a request to the server.
		   	$("#gridCustomer").jqxGrid('updatebounddata');
		},
		pagesize : 10,
		sortcolumn: 'partyId',
       	sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N'
		},
		contentType: 'application/x-www-form-urlencoded',
		url : 'jqxGeneralServicer?sname=getListCustomerDelys'
	}
	var dataAdaptercm = new $.jqx.dataAdapter(sourcecm,{
			autoBind : true,
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
		            }else data.filterListFields = "";
		            return data;
		        },
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
		                if (!sourcecm.totalRecords) {
		                    sourcecm.totalRecords = parseInt(data['odata.count']);
                	}
       			 }
		}
	);
	$('#gridCustomer').jqxGrid({
		source : dataAdaptercm,
		virtualmode : true,
		filterable : true,
		localization: getLocalization(),
		showfilterrow : true,
		rendergridrows : function(obj){
			return obj.data
		},
		pageable : true,
		altrows: true,
		width : 500,
		autoHeight : true,
		filterable : true,
		columns : [
			{text : '${uiLabelMap.DACustomerId}',datafield : 'partyId'},
			{text : '${uiLabelMap.DACustomerName}',datafield : 'groupName'}
		]
	});
	$('#gridCustomer').jqxGrid('autoresizecolumns');
	$('#gridCustomer').on('rowselect',function(event){
		var dropDownContent = '<div id="test" style="position: relative; margin-left: 3px; margin-top: 5px;">' +  event.args.row.groupName + '</div>';
		$('#customerAdd').jqxDropDownButton('setContent', dropDownContent);	
		$('#formAdd').jqxValidator('hideHint','#customerAdd');
		customerSelected = event.args.row.partyId;
	});
	$('#customerAdd').on('close',function(){
		$('#gridCustomer').jqxGrid('clearSelection');
	});
	$('#formAdd').jqxValidator({
		rules : [
			{ input: '#customerAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'change', rule: function(input,commit){
				var data = $('#customerAdd').val();
				if(!data || data === 'undefined') return false;
				return true;
			} },
			{ input: '#levelAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'change', rule: function(){
				var data = $('#levelAdd').val();
				if(data == 0 || data === 'undefined' ) return false;
				return true;
			} }
		]
	});
	$('#notification').jqxNotification({template : 'success',autoClose : true,opacity : 1,appendContainer : '#containerEx'});
	$('#noti').jqxNotification({template : 'error',autoClose : true,opacity : 1,appendContainer : '#containerErr'});
	$('#alterSave').click(function(){
		if(!$('#formAdd').jqxValidator('validate')) return;
		var index = $('#jqxgrid').jqxGrid('getselectedrowindex');
		 var rowdata =  $('#jqxgrid').jqxGrid('getrowdata',index);
			var row = {
					customerId : customerSelected,
					productPromoId : rowdata.productPromoId,
					ruleId : $('#levelAdd').jqxDropDownList('getSelectedItem').value
				}
	        $.ajax({
	        	url : 'exhibitedRegisterSUP',
	        	data : row,
	        	type : 'POST',
	        	datatype  : 'JSON',
	        	async : false,
	        	cache  : false,
	        	success :  function(response,status,xhr){
		        	if(response.result.duplicate){
		        		$('#noti').text('${StringUtil.wrapString(uiLabelMap.DANotiExhibited)}');
		        		$('#noti').jqxNotification('open');
		        	}else{
		        		$('#notification').text('${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}');
		        		 $("#alterpopupWindow").jqxWindow('close');
		        		 $('#notification').jqxNotification('open');
		        		 $('#gridListCustomer').jqxGrid('updatebounddata');
		        	}
	        	},
	        	error : function(){
	        	}
	        });
	        
	});
	$('#listCustomerRegisted').jqxWindow({width : 800,height : 450,resizable : false,isModal : true,autoOpen : false,modalOpacity : 0.7});
	//display list customer register exhibited
	var displayList = function(promoId){
		initGridListCustomer(promoId);
		$('#listCustomerRegisted').css('display','block');
		$('#listCustomerRegisted').jqxWindow('open');
	}
	var source = {};
	var getPromoName = function(){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowdata;
		rowdata =  $('#jqxgrid').jqxGrid('getrowdata',rowindex);
		if(rowdata) return rowdata.promoName;
		return '';
	}
	var initGridListCustomer = function(promoId){
	if(promoId)  {
		$('#exhibitedAdd').html('<span style="color:red;">' + getPromoName() + '</span>');
		$.ajax({
				url : 'getLevelExhibited',
				data : {
					exhibitedId  : promoId
				},
				cache : false,
				async : false,
				type : 'POST',
				datatype : 'json',
				success: function(response,status,xhr){
					if(response.listLevel){
						listLevel = response.listLevel;
					}
					$('#levelAdd').jqxDropDownList({source : listLevel,displayMember : 'ruleName',valueMember : 'productPromoRuleId',autoDropDownHeight : true,width : 200,height : 25});
					$('#levelAdd').jqxDropDownList({disabled : false});
				},
				error: function(){
				},
				
			});
		}
			var wtmp = window;
			var tmpwidth = $('#listCustomerRegisted').jqxWindow('width');
            $("#listCustomerRegisted").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			var tmpS = $('#gridListCustomer').jqxGrid('source');
			tmpS._source.url = 'jqxGeneralServicer?sname=getListCustomerRegister&promoId=' + promoId
			$("#gridListCustomer").jqxGrid('source', tmpS);
			$('#gridListCustomer').jqxGrid('updatebounddata');
	}
	var grid = $('#gridListCustomer');
		 source = {
			datafields : [
				{name : 'productPromoRegisterId',type : 'string'},
				{name : 'productPromoId',type : 'string'},
				{name : 'productPromoRuleId',type : 'string'},
				{name : 'partyId',type : 'string'},
				{name : 'groupName',type : 'string'},
				{name : 'createdDate',type : 'date',other : 'Timestamp'},
				{name : 'registerStatus',type : 'string'}
			],
			cache: false,
			datatype: "json",
			beforeprocessing: function (data) {
                source.totalrecords = data.TotalRows;
            },
			filter: function () {
			   	grid.jqxGrid('updatebounddata');
			},
			sort: function () {
                grid.jqxGrid('updatebounddata');
            },
			pager: function (pagenum, pagesize, oldpagenum) {
			  	// callback called when a page or page size is changed.
			},
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N'
			},
			contentType: 'application/x-www-form-urlencoded',
		}
	 var dataAdapter = new $.jqx.dataAdapter(source,{
			autoBind : true,
			formatData: function (data) {
					if (source.totalrecords) {
                                if (data.sortdatafield && data.sortorder) {
                                    data.$orderby = data.sortdatafield + " " + data.sortorder;
                                }
                    }
		    		if (data.filterscount) {
		                var filterListFields = "";
                                var tmpFieldName = "";
                                for (var i = 0; i < data.filterscount; i++) {
                                    var filterValue = data["filtervalue" + i];
                                    var filterCondition = data["filtercondition" + i];
                                    var filterDataField = data["filterdatafield" + i];
                                    var filterOperator = data["filteroperator" + i];
                                    if(getFieldType(filterDataField)=='number'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                                    }else if(getFieldType(filterDataField)=='date'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                                    }else if(getFieldType(filterDataField)=='Timestamp'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy hh:mm:ss aa]";
                                    }
                                    else{
                                        filterListFields += "|OLBIUS|" + filterDataField;
                                    }
                                    if(getFieldType(filterDataField)=='Timestamp'){
                                        if(tmpFieldName != filterDataField){
                                            filterListFields += "|SUIBLO|" + filterValue + " 00:00:00 am";
                                        }else{
                                            filterListFields += "|SUIBLO|" + filterValue + " 11:59:59 pm";
                                        }
                                    }else{
                                        filterListFields += "|SUIBLO|" + filterValue;
                                    }
                                    filterListFields += "|SUIBLO|" + filterCondition;
                                    filterListFields += "|SUIBLO|" + filterOperator;
                                    tmpFieldName = filterDataField;
                                }
		                data.filterListFields = filterListFields;
		            }else data.filterListFields = "";
		            return data;
		        },
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
		                if (!source.totalRecords) {
		                    source.totalRecords = parseInt(data['odata.count']);
                	}
       			 }
		}
	);
	function getFieldType(fName){
            for (i=0;i < source.datafields.length;i++) {
               if(source.datafields[i]['name'] == fName){
                    if(!(typeof source.datafields[i]['other'] === 'undefined' || source.datafields[i]['other'] =="")){
                        return  source.datafields[i]['other'];
                    }else{
                        return  source.datafields[i]['type'];
                    }
                    
               }
            }
        }
	<#assign listStt = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId","REG_PROMO_STTS"),null,null,null,false) !>
		var listStatus = [
			<#list listStt as stt>
				{
					id : '${stt.statusId?if_exists}',
					des : '${stt.description?default('')}'
				},
			</#list>
		];
		grid.jqxGrid({
				source : dataAdapter,
				width : 770,
				height : 400,
				localization: getLocalization(),
				filterable : true,
				editable : true,
				sortable : true,
				showfilterrow : true,
				virtualmode : true,
				showtoolbar : true,
	            rendertoolbar : function(toolbar){
		            	if(!toolbar.children().length){
		            		var container = $('<div style=\"width : 100%;\"></div>');
			            	var containerTmp = $('<div id=\"btDiv\" style=\"float: right\"></div>');
			            	container.append(containerTmp);
			            	var btAdd = $('<button class="buttonTheme" id="addrowBt" onclick="addFormPopup()" class="grid-action-button"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
			            	var btReject = $('<button class="buttonTheme" id="rejectBt" onclick="reject()" class="grid-action-button"><i class="icon-remove"></i>${uiLabelMap.DACancelStatus}</button>');
			            	$('#btDiv').css('float','right','important');
			            	containerTmp.append(btAdd);
			            	containerTmp.append(btReject);
			            	toolbar.append(container);
		            	}
	            },
				rendergridrows : function(obj){
					return obj.data;
				},
				pageable : true,
				pagesizeoptions : ['5','10','15'],
				pagesize : 10,
				altrows: true,
				theme : 'olbius',
				columns : [
					{text : '${uiLabelMap.DAProductPromoId}',datafield : 'productPromoId',editable : false},
					{text : '${uiLabelMap.DALevel}',datafield : 'productPromoRuleId',editable : true,columntype : 'dropdownlist',createeditor : function(row,columnfield,editor){
						if(listLevelTmp && listLevelTmp.length > 0 ){
							editor.jqxDropDownList({source : listLevelTmp,width : 200,height : 24,displayMember : 'ruleName',valueMember : 'productPromoRuleId',autoDropDownHeight : true});	
						}
					}},
					{text : '${uiLabelMap.DACustomerId}',datafield : 'partyId',editable : false},
					{text : '${uiLabelMap.DACustomerName}',datafield : 'groupName',editable : false},
					{text : '${uiLabelMap.DACreatedDate}',datafield : 'createdDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range',editable : false},
					{text : '${uiLabelMap.DAStatus}',filterable :false,editable : false,datafield : 'registerStatus',cellsrenderer : function(row,columnfield,value){
						var data = $("#gridListCustomer").jqxGrid("getrowdata",row);
						for(var i = 0;i <listStatus.length;i++){
							if(listStatus[i].id == data.registerStatus){
								return '<span>' + listStatus[i].des + '</span>';
							}	
						}		
						return 	data.registerStatus;		
					}}
				]
			});	
		function addFormPopup(){
			$('#alterpopupWindow').jqxWindow('open');
		}
		function reject(){
			var rowindex = $('#gridListCustomer').jqxGrid('getselectedrowindex');
			var datarow = $('#gridListCustomer').jqxGrid('getrowdata',rowindex);
				if(datarow.registerStatus == 'REG_PROMO_CANCELED') {
					$('#deleteDialog').text('${StringUtil.wrapString(uiLabelMap.DANotiExhibitedCancel)}');
			   		$('#deleteDialog').dialog({
			   			resizable : false,
			   			height : 180,
			   			modal : true,
			   			buttons : {
			   				'${StringUtil.wrapString(uiLabelMap.wgok)}' : function(){$(this).dialog('close');}
			   			}
					});
					$('#deleteDialog').parents().css('z-index','99999999999');
					return;		
				}else if(datarow){
							$('#deleteDialog').text('${StringUtil.wrapString(uiLabelMap.DANotiExhibitedToCancel)}');
					   		$('#deleteDialog').dialog({
					   			resizable : false,
					   			height : 180,
					   			modal : true,
					   			buttons : {
					   				'${StringUtil.wrapString(uiLabelMap.wgok)}' : function(){
								   		$(this).dialog('close');
										var data ;
										if(datarow.partyId && datarow.productPromoId && datarow.productPromoRuleId){
											data = {
												partyId : datarow.partyId,
												productPromoId : datarow.productPromoId,
												productPromoRuleId : datarow.productPromoRuleId
											};	
										}
										$.ajax({
											url : 'rejectExhibited',
											type : 'POST',
											data : data,
											async : false,
											cache : false,
											datatype : 'json',
											success : function(response,status,xhr){
												$('#notification').text('${StringUtil.wrapString(uiLabelMap.DACancelStatusSuccess)}');
								        		$('#notification').jqxNotification('open');
								        		$('#gridListCustomer').jqxGrid('updatebounddata');
											},
											error : function(){
												$('#notification').text('${StringUtil.wrapString(uiLabelMap.DACancelStatusError)}');
												$('#notification').jqxNotification('open');
											}
										});
						   				},
										'${StringUtil.wrapString(uiLabelMap.wgcancel)}' : function(){
							   				$(this).dialog('close');
							   				$('#gridListCustomer').jqxGrid('clearSelection');
							   			}
						   				}
									});
									$('#deleteDialog').parents().css('z-index','99999999999');
							
						}
					}
			var listLevelTmp;
			$('#gridListCustomer').on('cellbeginedit',function(){
				var rowindex = $('#gridListCustomer').jqxGrid('getselectedrowindex');
				var datarow = $('#gridListCustomer').jqxGrid('getrowdata',rowindex);
				if(datarow){
					$.ajax({
						url : 'getLevelExhibited',
						data : {
							exhibitedId  : datarow.productPromoId
						},
						cache : false,
						async : false,
						type : 'POST',
						datatype : 'json',
						success: function(response,status,xhr){
							if(response.listLevel){
								listLevelTmp = response.listLevel;
							};
						},
						error: function(){
						},
						
					});
				}
			});		
			
			$('#gridListCustomer').on('cellendedit',function(event){
				var data;
				if(event.args.row){
					data = event.args.row;
					data.productPromoRuleId = event.args.value;
					$.ajax({
						url : 'updateExhibited',
						data : data,
						cache : false,
						async : false,
						type : 'POST',
						datatype : 'json',
						success: function(response,status,xhr){
							$('#notification').text('${StringUtil.wrapString(uiLabelMap.DAUpdateSuccessful)}');
			        		$('#notification').jqxNotification('open');
			        		$('#gridListCustomer').jqxGrid('updatebounddata');
			        		$('#gridListCustomer').jqxGrid('clearSelection');
						},
						error: function(){
							$('#notification').text('${StringUtil.wrapString(uiLabelMap.DAUpdateError)}');
			        		$('#notification').jqxNotification('open');
						},
						
					});
				} 
			});			
</script>

