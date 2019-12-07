<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<!-- custom style-->
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
<!-- custom style-->

<script>

	<#assign listDayOfWeek = delegator.findList("DayOfWeek",null,null,null,null,false) !>
	var listDay = [
		<#list listDayOfWeek as day>
			{
				dayId : '${day.dayOfWeek?if_exists}',
				description : '${StringUtil.wrapString(day.get("description",locale)?default(''))}'
			},
		</#list>
	];

	if(!uiLabelMap) var uiLabelMap = {};
	uiLabelMap.CommonMonday = '${StringUtil.wrapString(uiLabelMap.CommonMonday)}';
	uiLabelMap.CommonTuesday = '${StringUtil.wrapString(uiLabelMap.CommonTuesday)}';
	uiLabelMap.CommonWednesday = '${StringUtil.wrapString(uiLabelMap.CommonWednesday)}';
	uiLabelMap.CommonThursday = '${StringUtil.wrapString(uiLabelMap.CommonThursday)}';
	uiLabelMap.CommonFriday = '${StringUtil.wrapString(uiLabelMap.CommonFriday)}';
	uiLabelMap.BSSaturday = '${StringUtil.wrapString(uiLabelMap.BSSaturday)}';
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSCustomerName = '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}';
	uiLabelMap.wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)}';
	uiLabelMap.wgok = '${StringUtil.wrapString(uiLabelMap.wgok)}';
	uiLabelMap.wgdeleteconfirm = '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?';
	uiLabelMap.BSCustomerNoGeoPointAreYouSureAdd = '${StringUtil.wrapString(uiLabelMap.BSCustomerNoGeoPointAreYouSureAdd)}';
	
	/*init columns list customer grid*/
	<#assign columnListCust = "
		{
			text : '${uiLabelMap.BSCustomerId}',
			datafield : 'partyId',
			width : '200px'
		},
		{
			text : '${uiLabelMap.BSCustomerName}',
			datafield : 'fullName'
		}
	"/>
	var columnListCust = '${columnListCust?if_exists}';

	/*init global variable*/
	var globalParent;		
	var globalData;
	var notificationRoute = '';
	var indexGlobal;
	var success = '${StringUtil.wrapString(uiLabelMap.distributionRouteSuccess)}';
	var error =  '${StringUtil.wrapString(uiLabelMap.distributionRouteError)}';
	
	/*init variable use route customer*/
	
	var gridCustomer =  $('#gridCustomer');
	var routePopupCus = $('#routePopupCus');
	var listCustomerDistribution = $("#listCustomerDistribution");
	var deleteDialogCus = $('#deleteDialogCus');
	var globallistCustomerOfRoute = [];	
	var sourceCustomer = {};
	var indexSelectedTmp = new Array();
	var rtIdCus;
	var messageNotificationCus = $("#messageNotificationCus");
	
	
	/*init variable use route employees salesman*/
	var rtId;
	var globallistSMOfRoute = [];
	var routePopup = $('#routePopup');
	var messageNotification = $("#messageNotification");
	
	/*init common function*/
	var alertMessage = function(popup,idNotification,message,flag){
		if(flag){
			popup.jqxWindow('close');
		}
		idNotification.css('display','block');
		idNotification.html(message);
		idNotification.jqxNotification('open');
 			setTimeout(function(){
 				idNotification.css('display','none');
			},500);
	}
	
	var initDialog = function(_dialog,callback){
		_dialog.text(uiLabelMap.wgdeleteconfirm);
		_dialog.dialog({
			resizable : false,
			height : 180,
			modal : true,
			buttons : {
				uiLabelMap.wgok : typeof(callback) == 'function' ? callback() : function(){},
				uiLabelMap.wgcancel : function(){
					$(this).dialog('close');
				}
			}
		});
		var daParent = _dialog.parent();
		if(daParent){
			daParent.css('z-index','100000000000000000');
		}
	}
	
  var sendRequestRemove = function(data,url,callback){
		   $.ajax({
	   			url : url,
	   			type : 'POST',
	   			dataType : 'json',
	   			data : {
	   				partyId : data.partyId
	   			},
	   			async : false,
	   			cache : false,
	   			success : (typeof(callback) == 'function') ? callback() : function(){},
	   			error : function(){
	   				throw 'error when send request remove' + data
	   			}
	   		});
	   }
	
	/*******************/
</script>