<style type="text/css">
	.buttonRt{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		margin-left : 30px;
	}
</style>
<#assign dataField = "[
	{name : 'partyId',type : 'string'},
	{name : 'groupName',type : 'string'}
]"/>
<#assign columnlist = "
	{text : '${uiLabelMap.DACustomerId}',datafield : 'partyId',width : '30%'},
	{text : '${uiLabelMap.DACustomerName}',datafield : 'groupName'},
	{text : '',width : '10%',filterable : false,cellsrenderer : function(row,columnfield,value){
		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
		if(data) return '<button class=\"buttonRt\" onclick = \"displayFormMark('+ \"'\" + data.partyId  + \"'\" +')\"><i style=\"margin-left : 10px;\" class=\"fa fa-pencil\"></i></button>'; 
		return ;
	}}
"/>
<#assign initrowdetail = "function(index, parentElement, gridElement, datarecord){
	var e = $('<div></div>');
	$('#containerRowdetail').append(e);
	e.attr('id','listEx' + index);
	var gridDetail = $('#listEx' + index);
	initRowDT(datarecord,gridDetail);
}" />
<#assign rowdetailstemplateAdvance="<div style='width : 100%;' id='containerRowdetail'></div>"/>
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" rowdetailstemplateAdvance=rowdetailstemplateAdvance initrowdetails="true" initrowdetailsDetail=initrowdetail  dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetListExhibitedMarking"
		/>	 
		
<div id="formMarking" style="display: block;"><div>${uiLabelMap.DAListExhibitedMark}</div><div><div id="nofiticationMarking"></div><div id="gridListEx"></div><div style="float:right;"><input type="button" class="btn btn-danger form-action-button pull-right" id="alterCancel" value="✖ ${uiLabelMap.CommonCancel}"/><input type="button" class='btn btn-primary form-action-button pull-right' id="alterSave" value="✔ ${uiLabelMap.CommonSave}"/></div></div></div>
<div id="jqxNotifcationNotValid" style="display : none;">${uiLabelMap.DAExhibitedRegisterMarkNotValid}</div>
<div id="jqxNotifcationSuccess" style="display : none;">${uiLabelMap.DAExhibitedRegisterMarkSuccess}</div>
<div id="jqxNotifcationError" style="display : none;">${uiLabelMap.DAExhibitedRegisterMarkError}</div>
<script type="text/javascript">
	function initRowDT(datarecord,gridDetail){
		var sourceDt = {
			url : "jqxGeneralServicer?sname=JQgetListPPM&customerId=" + datarecord.partyId,
			type : 'POST',
			datatype : 'json',
			cache : false,
			sort : function(){
				gridDetail.jqxGrid('updatebounddata');
			},
			data : {
				conditionsFind : 'N',
				noConditionFind : 'Y'				
			},
			datafields : [
				{name : 'productPromoId',type : 'string'},
				{name : 'promoName',type : 'string'},
				{name : 'productPromoRuleId',type : 'string'},
				{name : 'result',type : 'string'}
			],
			filter : function(){
				gridDetail.jqxGrid('updatebounddata');
			},
			beforeprocessing : function(data){
				sourceDt.totalrecords = data.TotalRows		
			},
			loadError : function(){
			
			}
		}
		
		var dataApt = new $.jqx.dataAdapter(sourceDt,{
			autoBind : true,
			formatData : function(data){
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
			}	
		});
		gridDetail.jqxGrid({
			source : dataApt,
			virtualmode : true,
			width  : $('#jqxgrid').jqxGrid('width') - 100,
			rendergridrows : function(obj){
				return obj.data;
			},
			filterable : true,
			showfilterrow : true,
			sortable : true,
			pageable : true,
			pagesize : 10,
			pagesizeoptions : ['5','10','15'],
			autoheight : true,
			localization: getLocalization(),
			columns : [
				{text : '${uiLabelMap.DAProductPromoId}',datafield : 'productPromoId',width : '15%'},
				{text : '${uiLabelMap.DAProductPromo}',datafield : 'promoName',width : '40%'},
				{text : '${uiLabelMap.DALevel}',datafield : 'productPromoRuleId'},
				{text : '${uiLabelMap.DAResult}',datafield : 'result',filterable : false,cellsrenderer : function(row,columnfield,value){
					var data = gridDetail.jqxGrid('getrowdata',row);
					if(data.result == 'Y'){
						return '<span><i style=\"color : rgb(65, 255, 0);\" class=\"icon-ok\"></i>&nbsp;${uiLabelMap.DAPass}</span>';
					}else if(data.result == 'N') return '<span><i style=\"color :red;\" class=\"icon-remove\"></i>&nbsp;${uiLabelMap.DAFail}</span>';
					return data.result;
				}}
			]
		})
	}
	function displayFormMark(partyId){
			var wtmp = window;
			var tmpwidth = $('#formMarking').jqxWindow('width');
            $("#formMarking").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
            $("#formMarking").jqxWindow('open');
			var tmpS = $('#gridListEx').jqxGrid('source');
			tmpS._source.url = 'jqxGeneralServicer?sname=JQgetListExhibitedForMark&customerId=' + partyId;
			$("#gridListEx").jqxGrid('source', tmpS);
			$('#gridListEx').jqxGrid('updatebounddata');
			$('#formMarking').jqxWindow('open');
		};
	$(document).ready(function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		$('#formMarking').jqxWindow({theme : theme,width : 800,height : 400,isModal: true,autoOpen : false,cancelButton  : $('#alterCancel'),resizable : false});
		$('#alterSave').jqxButton();
		$('#alterCancel').jqxButton();
	var source = {
			datatype : 'json',
			type : 'POST',
			filter : function(){
				//send update data filter to server
				$('#gridListEx').jqxGrid('updatebounddata');
			},
			beforeprocessing: function (data) {
                source.totalrecords = data.TotalRows;
            },
			data  : {
				noConditionFind : 'Y',
				conditionsFind : 'N'
			},
			cache : false,
			datafields : [
				{name :'productPromoRegisterId',type : 'string'},
				{name : 'partyId',type : 'string'},
				{name : 'groupName',type : 'string'},
				{name : 'promoName',type : 'string'},
				{name : 'productPromoRuleId',type : 'string'}
			],
		};
		var dataAdapter = new $.jqx.dataAdapter(source,{
			autoBind : true,
			formatData : function(data){
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
		               if (!source.totalrecords) {
                            source.totalrecords = parseInt(data["odata.count"]);
                        }
       			 }
		})
	$('#jqxNotifcationNotValid').jqxNotification({template : 'error',appendContainer : '#nofiticationMarking',width : '100%',autoClose: true,opacity : 1});	
	$('#jqxNotifcationSuccess').jqxNotification({template : 'success',appendContainer : '#nofiticationMarking',width : '100%',autoClose: true,opacity : 1});
	$('#jqxNotifcationError').jqxNotification({template : 'success',appendContainer : '#nofiticationMarking',width : '100%',autoClose: true,opacity : 1});
	$('#gridListEx').jqxGrid({
			source  : dataAdapter,
			width : 780,
			height : 300,
			filterable : true,
			showfilterrow : true,
			virtualmode : true,
			rendergridrows : function(obj) {
				return obj.data;
			},
			localization: getLocalization(),
			pageable : true,
			pagesizeoptions : ['5','10','15'],
			columns : [
				{text : '${uiLabelMap.DACustomerId}',datafield : 'partyId',width : '15%' },
				{text : '${uiLabelMap.DACustomerName}',datafield : 'groupName',width :'25%'},
				{text : '${uiLabelMap.DAPromosProgramme}',datafield : 'promoName',width : '30%'},
				{text : '${uiLabelMap.DALevel}',datafield : 'productPromoRuleId',width  :'7%'},
				{text : '',filterable : false,width : '23%',
					cellsrenderer  : function(row,columnfield,value,defaulthtml){
						return '<span><div style=\"display : inline;\"><input name=\"Y'+ row +'\" type=\"checkbox\"><span style=\"margin-top : -5px !important;font-weight : bold;color : blue;\" class=\"lbl\">${uiLabelMap.DAPass}</span>&nbsp;&nbsp;<input name=\"N'+ row +'\" type=\"checkbox\"><span style=\"margin-top : -5px !important;font-weight : bold;color : red;\" class=\"lbl\">${uiLabelMap.DAFail}</span></div></span>';
				}}
			]
		});
		
		$('#alterSave').click(function(){
			var obj = $('input[type=checkbox]:checked');
			var data  = [];
					obj.each(function(){
						var resultTmp = $(this).attr('name').substr(0,1);
						var rowTmp = $(this).attr('name').substr(1,2);
						var flag;
						if(data && data.length > 0){
							$.each(data,function(index){
								if(data[index].id == rowTmp){
									$('#jqxNotifcationNotValid').jqxNotification('open');
									data = new Array();
									flag = true;
									return false;
								}
							});
							if(!flag) {
								data.push({
									result  : resultTmp,
									id : parseInt(rowTmp)
								});
							}
						}else {
							if(!flag){
								data.push({
									result  : resultTmp,
									id : parseInt(rowTmp)
								});
							}
						}
						if(flag) return false;
					});
					if(data && data.length > 0) {
						var dataFinal = [];
						$.each(data,function(index){
							var rowdata = $('#gridListEx').jqxGrid('getrowdata',data[index].id);
							dataFinal.push({
								productPromoRegisterId : rowdata.productPromoRegisterId,
								result : data[index].result
							});
						});
						(function(){
							$.ajax({
								url : 'ResultMarking',
								datatype : 'json',
								data : {
									listEx : JSON.stringify(dataFinal)
								},
								async : false,
								cache : false,
								type : 'POST',
								success : function(response,status,xhr){
									if(response._EVENT_MESSAGE_){
										$('#jqxNotifcationSuccess').jqxNotification('open');
									}else if(response._ERROR_MESSAGE_){
										$('#jqxNotifcationError').jqxNotification('open');
									}
									$('#gridListEx').jqxGrid('updatebounddata');
									var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
									$('#listEx' + rowindex).jqxGrid('updatebounddata');
								},
								error : function(){
								}
							})
						}(data))
					}
				});
			})
</script>		