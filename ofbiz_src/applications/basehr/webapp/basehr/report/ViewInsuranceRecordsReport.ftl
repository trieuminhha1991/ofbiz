<script type="text/javascript" id="grid">
	$(function(){
		function getFormattedDateMonth(date) {
			  var year = date.getFullYear();
			  var month = (1 + date.getMonth()).toString();
			  month = month.length > 1 ? month : '0' + month;
			  return month + '/' + year;
		};
		
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		
		var emplList = new Array();
		//Send request to get Employee
		var getEmployee = function(partyId){
			$.ajax({
				url: 'getEmplByOrg',
				type: 'POST',
				data: {'partyId': partyId},
				async: false,
				dataType: 'json',
				success: function(data) {
					emplList = data['listIterator'];
				}
			});
		}
		
		getEmployee(rootPartyArr[0]['partyId']);
		
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.InsuranceRecordsReport)}',
				columns : [
			           {text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield : 'partyName', width : '15%' },
			           {text : '${StringUtil.wrapString(uiLabelMap.JobPosition)}', datafield : 'emplPositionType', width : '15%' },
			           {text : '${StringUtil.wrapString(uiLabelMap.FromMonthYear)}', datafield : 'fromDate', width : '15%',
		        	   		cellsrenderer : function(row, column, value){
		        	   			if(value){
		        	   				var date = new Date(value);
			        	   			return '<span>' + getFormattedDateMonth(date) + '</span>';
		        	   			}
		        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.ThruMonthYear)}', datafield : 'thruDate', width : '15%',
			        	   cellsrenderer : function(row, column, value){
			        		    if(value){
			        		    	var date = new Date(value);
			        	   			return '<span>' + getFormattedDateMonth(date) + '</span>';
			        		    }
		        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.PartyInsuranceSalTitle)}', datafield : 'amount', width : '10%' },
			           {text : '${StringUtil.wrapString(uiLabelMap.InsuranceAllowancePositionFull)}', datafield : 'position', width : '10%', columngroups : 'ALLOWANCE' },
			           {text : '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniorityExces)}', datafield : 'seniorityExces', width : '10%',columngroups : 'ALLOWANCE' },
			           {text : '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniority)}', datafield : 'seniority', width : '10%', columngroups : 'ALLOWANCE' },
			           {text : '${StringUtil.wrapString(uiLabelMap.SalaryAllowance)}', datafield : 'salary', width : '10%', columngroups : 'ALLOWANCE' },
			           {text : '${StringUtil.wrapString(uiLabelMap.AdditionalAmounts)}', datafield : 'other', width : '10%', columngroups : 'ALLOWANCE' },
			           {text : '${StringUtil.wrapString(uiLabelMap.SocialIns)}', datafield : 'BHXH', width : '10%', columngroups : 'INS' },
			           {text : '${StringUtil.wrapString(uiLabelMap.HealthIns)}', datafield : 'BHYT', width : '10%', columngroups : 'INS' },
			           {text : '${StringUtil.wrapString(uiLabelMap.VoluntaryIns)}', datafield : 'BHTN', width : '10%', columngroups : 'INS' },
		       ],
				groupable : true,
				columngroups : [
			            {text : '${StringUtil.wrapString(uiLabelMap.InsuranceAllowance)}', align : 'center', name : 'ALLOWANCE'},
			            {text : '${StringUtil.wrapString(uiLabelMap.InsurancePayment)}', align : 'center', name : 'INS'},
		    	],
		};
		var config_popup = [
                {
                	action : 'addJqxTree',
                	params : [{
                		id : 'org',
                		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
                		source : rootPartyArr,
	          			value: rootPartyArr[0]['partyId']
                	}],
                	event : function(popup){
	            		var uuid = popup.$jqxWindowElement.getUuid();
	            		var orgGrid = $('#org-jqxtree-' + uuid);
	            		orgGrid.on('itemClick', function(event){
	            			 var args = event.args;
	        			     var item = orgGrid.jqxTree('getItem', args.element);
	        			     var orgId = item.value;
	        			     var source = $('#empl-jqxgrid-' + uuid).jqxGrid('source');
	        			     getEmployee(orgId);
	        			     source._source.localdata = emplList;
	        			     $('#empl-jqxgrid-' + uuid).jqxGrid('source', source);
	            		})
	            		$('#empl-jqxgrid-' + uuid).on('bindingcomplete', function () {
       			     	});
	            	}
                },
                {
                	action : 'addJqxGridMultil',
                	params : [{
                		id : 'empl',
            			label : '${StringUtil.wrapString(uiLabelMap.CommonEmployee)}',
                		title1 : '${StringUtil.wrapString(uiLabelMap.EmployeeId)}',
                		title2 : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}',
                		data : emplList,
                	}]
                }
        ];
		var testGrid = OLBIUS.oLapGrid('grid', config, config_popup, 'insuranceRecords', true, true, OLBIUS.defaultLineFunc);
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				'partyId' : oLap.val('empl'),
			});
		});
		testGrid.init(function(){
			testGrid.runAjax();
		})
	});
</script>
<script id= "chart" type="text/javascript">
	$(function(){
		function getFormattedDateMonth(date) {
			  var year = date.getFullYear();
			  var month = (1 + date.getMonth()).toString();
			  month = month.length > 1 ? month : '0' + month;
			  var day = date.getDate().toString();
			  day = day.length > 1 ? day : '0' + day;
			  return day + '/' +  month + '/' + year;
		};
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		var config = {
					title : {
						text : '${StringUtil.wrapString(uiLabelMap.TimelyInsParticipateChart)}',
						x : -20
					},
					chart : {
						type : 'column'
					},
					xAxis : {
						labels : {
		                    style: {
		                        fontSize: '13px',
		                        fontFamily: 'Verdana, sans-serif'
		                    }
						},
						title : {
		                    text: null
		                }
					},
					 yAxis: {
						 	type: 'datetime',
			                title: {
			                    text: null
			                },
			                dateTimeLabelFormats : {
			                	millisecond: '%H:%M:%S.%L',
			                	second: '%H:%M:%S',
			                	minute: '%H:%M',
			                	hour: '%H:%M',
			                	day: '%e. %b',
			                	week: '%e. %b',
			                	month: '%b \'%y',
			                	year: '%Y'
			                }
	            	},
		            credits: {
		                enabled: false
		            },
		            tooltip : {
		            	formatter : function(){
		            		var date = new Date(this.y);
		            		var time = getFormattedDateMonth(date);
		            		return this.x + ' : ' + time;
		            	}
		            }
			};
			var config_popup = [
	              {
	            	  action : 'addDropDownList',
	            	  params : [{
	            		  id : 'status',
	            		  label : '${StringUtil.wrapString(uiLabelMap.CommonStatus)}',
	            		  data : status_data,
	                      index: 0
	            	  }]
	              },
	              {
	            	  action : 'addDateTimeInput',
	            	  params : [{
	            		  id : 'date',
	            		  label : '${StringUtil.wrapString(uiLabelMap.Time)}',
	            		  value : OLBIUS.dateToString(past_date)
	            	  }]
	              },
	              {
	            	  action : 'addJqxTree',
	            	  params : [{
	            		  id : 'org',
	            		  label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
	          			  source : rootPartyArr,
	          			  value: rootPartyArr[0]['partyId']
	            	  }]
	              }
	      ];
		  var testChart = OLBIUS.oLapChart('chart', config, config_popup, 'timelyInsParticipate', true, true, OLBIUS.defaultColumnFunc);
		  testChart.funcUpdate(function(oLap){
			  oLap.update({
				  'status' : oLap.val('status'),
				  'date' : oLap.val('date'),
				  'org' : oLap.val('org')
			  })
		  });
		  testChart.init(function(){
			  testChart.runAjax();
		  })
	})
</script>
