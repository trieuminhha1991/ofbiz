$(document).ready(function(){
	emplSalaryHistoryObject.init();
});

var emplSalaryHistoryObject = (function(){
	var init = function(){
		initJqxDropdownList();
		initSimpleInput();
		create_spinner($("#spinnerSalaryInfo"));
		create_spinner($("#spinnerAllowanceInfo"));
		initData();
		initEvent();
	};
	var initJqxDropdownList = function(){
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $('#month'), "month",  "description", 25, 85);
	};
	
	var initSimpleInput = function(){
		$("#year").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initData = function(){
		var nowDate = new Date();
		var month = nowDate.getMonth();
		var year = nowDate.getFullYear()
		$('#month').val(month);
		$("#year").val(year);
		getEmplSalaryBaseFlat(month, year);
		getEmplAllowance(month, year);
		renderTitlePayslip(month, year);
		getEmplPayrolloverview(month, year);
	};
	var initEvent = function(){
		$("#month").on('select', function(event){
			var args = event.args;
			if(args){			
				var args = event.args;
				if (args) {
					var month = args.item.value;
					var year = $("#year").val();
					getEmplSalaryBaseFlat(month, year);
					getEmplAllowance(month, year);
					renderTitlePayslip(month, year);
					getPartyPayrollInfo(month, year);
					getEmplPayrolloverview(month, year);
				}
			}
		});
		
		$("#year").on('valueChanged', function(event){
			var year = event.args.value;
			var month = $("#month").val();
			getEmplSalaryBaseFlat(month, year);
			getEmplAllowance(month, year);
			renderTitlePayslip(month, year);
			getPartyPayrollInfo(month, year);
			getEmplPayrolloverview(month, year);
		});
	};
	var getEmplPayrolloverview = function(month, year){
		$.ajax({
			url: 'getEmplPayrolloverview',
			data: {month: month, year: year},
			type:'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					$("#acutalReceiveSalView").html(formatcurrency(response.actualSalReceive));
					$("#totalIncomeView").html(formatcurrency(response.totalIncome));
					$("#totalDeductionView").html(formatcurrency(response.totalDedution));
				}else{
					$("#acutalReceiveSalView").html(uiLabelMap.HRCommonNotSetting);
					$("#totalIncomeView").html(uiLabelMap.HRCommonNotSetting);
					$("#totalDeductionView").html(uiLabelMap.HRCommonNotSetting);
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var getEmplSalaryBaseFlat = function(month, year){
		$("#loadingSalaryInfo").show();
		$("#emplSalaryInfo").empty();
		$.ajax({
			url: 'getEmplSalaryBaseFlat',
			data: {month: month, year: year},
			type:'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					renderEmplIncomeInfo(response.salaryBaseInfo, uiLabelMap.SalaryBaseFlat, $("#emplSalaryInfo"));
					renderEmplIncomeInfo(response.salaryInsuranceInfo, uiLabelMap.InsuranceSalaryShort, $("#emplSalaryInfo"));
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingSalaryInfo").hide();
			}
		});
	};
	var getEmplAllowance = function(month, year){
		$("#loadingAllowanceInfo").show();
		$("#emplAllowanceInfo").empty();
		$.ajax({
			url: 'getEmplAllowance',
			data: {month: month, year: year},
			type:'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					var dataMap = response.results;
					if(dataMap){
						for(var code in dataMap){
							var listAllowance = dataMap[code];
							var title = listAllowance[0].abbreviation;
							if(!title){
								title = listAllowance[0].name;
							}
							renderEmplIncomeInfo(listAllowance, title, $("#emplAllowanceInfo"));
						}
					}else{
						var noDataDiv = $("<div style='text-align: center'><b>" + uiLabelMap.wgemptydatastring + "</b></div>");
						$("#emplAllowanceInfo").append(noDataDiv);
					}
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingAllowanceInfo").hide();
			}
		});
	};
	var renderEmplIncomeInfo = function(incomeInfo, mainTitle, parentDiv){
		var incomeDiv = $('<div class="row-fluid margin-bottom10"></div>');
		var incomeLabelDiv = $("<div class='span4 text-algin-left'></div>");
		var label = $('<label><i class="icon-plus blue"></i><b>' + mainTitle + '</b></label>');
		var incomeContentDiv = $("<div class='span8'></div>");
		if(incomeInfo.length > 0){
			var ulEle = $("<ul class='unstyled'></ul>");
			incomeInfo.forEach(function(income){
				var liEle = $("<li><i class='icon-ok green'></i></li>");
				var periodTypeId = income.periodTypeId;
				var amount = income.amount;
				var text = formatcurrency(amount) + " - ";
				for(var i = 0; i < globalVar.periodTypeArr.length; i++){
					if(globalVar.periodTypeArr[i].periodTypeId == periodTypeId){
						text += globalVar.periodTypeArr[i].description;
						break;
					}
				}
				var fromDate = new Date(income.fromDate);
				text += " (" + uiLabelMap.HrCommonFromLowercase + " " +  getDate(fromDate) + "/" + getMonth(fromDate) + "/" + fromDate.getFullYear().toString().substring(2);
				if(income.thruDate){
					var thruDate = new Date(income.thruDate);
					text += " - " +  getDate(thruDate) + "/" + getMonth(thruDate) + "/" + thruDate.getFullYear().toString().substring(2);
				}
				text += ")";
				liEle.append($("<span>" + text +"</span>"))
				ulEle.append(liEle);
			});
			incomeContentDiv.append(ulEle);
		}else{
			incomeContentDiv.append($('<div style="font-size: 13px">' + uiLabelMap.NotSetting + '</div>'));
		}
		incomeLabelDiv.append(label);
		incomeDiv.append(incomeLabelDiv);
		incomeDiv.append(incomeContentDiv);
		parentDiv.append(incomeDiv);
	};
	var renderTitlePayslip = function(month, year){
		$("#payslipTitle").html(uiLabelMap.HRPayslip + " " + uiLabelMap.CommonMonth + " " + (month + 1) + "/" + year);
	};
	var getPartyPayrollInfo = function (month, year){
		var sourceIncome = $("#jqxgridIncome").jqxGrid('source');
		var sourceDeduction = $("#jqxgridDeduction").jqxGrid('source');
		sourceIncome._source.url = "jqxGeneralServicer?sname=JQPartyPayrollAmount&hasrequest=Y&payrollCharacteristicId=INCOME&month=" + month + "&year=" + year;
		sourceDeduction._source.url = "jqxGeneralServicer?sname=JQPartyPayrollAmount&hasrequest=Y&payrollCharacteristicId=DEDUCTION&month=" + month + "&year=" + year;
		$("#jqxgridIncome").jqxGrid('source', sourceIncome);
		$("#jqxgridDeduction").jqxGrid('source', sourceDeduction);
	};
	return{
		init: init
	}
}());


