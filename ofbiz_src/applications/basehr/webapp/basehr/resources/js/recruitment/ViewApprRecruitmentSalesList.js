var apprRecruitmentSaleObj = (function(){
	var init = function(){
		initEvent();
		initContextMenu();
		$("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 0.9, template: "info" });
	};
	var initEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimePeriod"), "customTimePeriodId", "periodName", 25, 90);
			createJqxDropDownList([], $("#monthCustomTimePeriod"), "customTimePeriodId", "periodName", 25, 90);
			$("#monthCustomTimePeriod").on('select', function(event){
				var args = event.args;
				if(args){
					var value = args.item.value;
					var monthCustomTimePeriodId = null; 
					var yearCustomTimePeriodId = null; 
					if(value == "ALL"){
						yearCustomTimePeriodId = $("#yearCustomTimePeriod").val();
					}else{
						monthCustomTimePeriodId = value;
					}
					updateJqxGrid(monthCustomTimePeriodId, yearCustomTimePeriodId);
				}
			});
			$("#yearCustomTimePeriod").on('select', function(event){
				var args = event.args;
				if(args){
					 var value = args.item.value;
					 $.ajax({
						url: "getCustomTimePeriodByParent",
						data: {parentPeriodId: value},
						type: 'POST',
						success: function(data){
							if(data.listCustomTimePeriod){
								var listCustomTimePeriod = data.listCustomTimePeriod;
								listCustomTimePeriod.unshift({customTimePeriodId: 'ALL', periodName: '-----------'});
								updateSourceDropdownlist($("#monthCustomTimePeriod"), listCustomTimePeriod);
							}
						},
						complete: function(jqXHR, textStatus){
							$("#monthCustomTimePeriod").jqxDropDownList({selectedIndex: 0});
						}
					});
				}
			});
			$("#yearCustomTimePeriod").jqxDropDownList({selectedIndex: 0});
		});
	};
	var updateJqxGrid = function(monthCustomTimePeriodId, yearCustomTimePeriodId){
		var source = $("#jqxgrid").jqxGrid('source');
		if(monthCustomTimePeriodId){
			source._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentSalesOfferList&monthCustomTimePeriodId=" + monthCustomTimePeriodId;
		}else if(yearCustomTimePeriodId){
			source._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentSalesOfferList&yearCustomTimePeriodId=" + yearCustomTimePeriodId;
		}
		$("#jqxgrid").jqxGrid('source', source);
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 180);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "listEmplOffer"){
            	apprRecSalesEmplListObj.openWindow();//apprRecSalesEmplListObj is defined in ApprRecruitmentSalesEmplList.js
            	apprRecSalesEmplListObj.setData(dataRecord);
            }else if(action == "approvalList"){
            	apprRecSalesEmplObj.openWindow();//apprRecSalesEmplObj is defined in ApprRecruitmentSalesNotApprList.js
            	apprRecSalesEmplObj.setData(dataRecord);
            }
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	apprRecruitmentSaleObj.init();
});