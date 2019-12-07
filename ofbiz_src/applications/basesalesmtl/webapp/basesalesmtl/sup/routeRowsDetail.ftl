<script>
var initGlobalRowsDetail = function(){};
initGlobalRowsDetail.prototype.initlistSMOfRoute = function(routeId,index){
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
							columns : [columnListCust]
				});
		  	}
	};


initGlobalRowsDetail.prototype.initrowdetails = function(index, parentElement, gridElement, datarecord){
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
		
$(function(){
	var grids = new initGlobalRowsDetail();
})
</script>
