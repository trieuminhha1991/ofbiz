<#include "script/ViewRecruitmentDetailReportScript.ftl" />
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script id="grid" type="text/javascript">
	$(function(){
		var now = new Date();
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.RecruitPlanBoard)}',
				columns : [
				      {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}',datafield : 'party_id', hidden : 'true'},
				      {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitment_plan_name', width : '25%'},
				      {text : '${StringUtil.wrapString(uiLabelMap.RecruitPlanName)}', datafield : 'party_name', width : '25%'},
				      {text : '${StringUtil.wrapString(uiLabelMap.PositionInOrg)}', datafield : 'job_title', width : '25%'},
				      {text : '${StringUtil.wrapString(uiLabelMap.RoleInBoard)}', datafield : 'role_description', width : '25%'},
	           ],
				groupable : true
		};
		var config_popup = [
	            {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id: 'from_date',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
	            		value: OLBIUS.dateToString(past_date),
	            	}],
	            	before : 'thru_date'
	            },
	            {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id : 'thru_date',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
	            		value: OLBIUS.dateToString(cur_date),
	            	}],
	            	after : 'from_date'
	            },
	            {
	            	action : 'addJqxTree',
	            	params : [{
	            		id : 'organization',
	            		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
	            		source : rootPartyArr
	            	}],
	            	event : function(popup){
	            		var uuid = popup.$jqxWindowElement.getUuid();
	            		$('#organization-jqxtree-' + uuid).on('itemClick', function(event){
	            			 var fromDate = $('#from_date-' + uuid).jqxDateTimeInput('getDate').getTime();
	                		 var thruDate = $('#thru_date-' + uuid).jqxDateTimeInput('getDate').getTime();
	            			 var args = event.args;
	        			     var item = $('#organization-jqxtree-' + uuid).jqxTree('getItem', args.element);
	        			     var orgId = item.value;
	        			     var source = $('#recruitment-jqxgrid-' + uuid).jqxGrid('source');
	        			     source._source.url = "jqxGeneralServicer?sname=getRecruimentListByOrg&fromDate=" + fromDate + "&thruDate=" + thruDate + "&partyId=" + orgId;
	        			     $('#recruitment-jqxgrid-' + uuid).jqxGrid('source', source);
	            		})
	            	}
	            },
	            {
	            	action : 'addJqxGridMultiSource',
	            	params : [{
	            		id : 'recruitment',
	            		label : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlan)}',
	            		source : {
	            			datafields : [
	    			              {name : 'recruitmentPlanId', type : 'string'},
	    			              {name : 'recruitmentPlanName', type : 'string'},
	    			              {name : 'partyId', type : 'string'}
				            ],
				            datatype : 'json',
				            url : 'getRecruimentListByOrg'
	            		},
	            		value : 'recruitmentPlanId',
	            		text : 'recruitmentPlanName',
	            		columns : [
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanId)}', datafield : 'recruitmentPlanId', width : '50%', columntype: 'textbox'},
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '50%', columntype: 'textbox'}
			           ]
	            	}]
	            }
	    ];
		var testGrid = OLBIUS.oLapGrid('grid', config, config_popup, 'getRecruitmentPlanBoardOlap', true, true, OLBIUS.defaultLineFunc);
		
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				'recruimentPlanId' : oLap.val('recruitment')
			})
		});
		testGrid.init(function(){
			testGrid.runAjax();
		})
	})
</script>
<script id="grid2" type="text/javascript">
	$(function(){
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.RecruitmentRound)}',
				columns : [
		           {datafield : 'roundOrder', hidden : 'true'},
		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'planName', width : '30%'},
		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentRoundName)}', datafield : 'roundName', width : '30%'},
		           {text : '${StringUtil.wrapString(uiLabelMap.Note)}', datafield : 'roundComment', width : '40%'},
	           ],
	            groupable : true,
		};
		var config_popup = [
                {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id: 'from_date_2',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
	            		value: OLBIUS.dateToString(past_date),
	            	}],
	            	before : 'thru_date_2'
	            },
	            {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id : 'thru_date_2',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
	            		value: OLBIUS.dateToString(cur_date),
	            	}],
	            	after : 'from_date_2'
	            },
	            {
	            	action : 'addJqxTree',
	            	params : [{
	            		id : 'organization_2',
	            		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
	            		source : rootPartyArr
	            	}],
	            	event : function(popup){
	            		var uuid = popup.$jqxWindowElement.getUuid();
	            		$('#organization_2-jqxtree-' + uuid).on('itemClick', function(event){
	            			 var fromDate = $('#from_date_2-' + uuid).jqxDateTimeInput('getDate').getTime();
	                		 var thruDate = $('#thru_date_2-' + uuid).jqxDateTimeInput('getDate').getTime();
	            			 var args = event.args;
	        			     var item = $('#organization_2-jqxtree-' + uuid).jqxTree('getItem', args.element);
	        			     var orgId = item.value;
	        			     var source = $('#recruitment_2-jqxgrid-' + uuid).jqxGrid('source');
	        			     source._source.url = "jqxGeneralServicer?sname=getRecruimentListByOrg&fromDate=" + fromDate + "&thruDate=" + thruDate + "&partyId=" + orgId;
	        			     $('#recruitment_2-jqxgrid-' + uuid).jqxGrid('source', source);
	            		})
	            	}
	            },
	            {
	            	action : 'addJqxGridMultiSource',
	            	params : [{
	            		id : 'recruitment_2',
	            		label : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlan)}',
	            		source : {
	            			datafields : [
	    			              {name : 'recruitmentPlanId', type : 'string'},
	    			              {name : 'recruitmentPlanName', type : 'string'},
	    			              {name : 'partyId', type : 'string'}
				            ],
				            datatype : 'json',
				            url : 'getRecruimentListByOrg'
	            		},
	            		value : 'recruitmentPlanId',
	            		text : 'recruitmentPlanName',
	            		columns : [
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanId)}', datafield : 'recruitmentPlanId', width : '50%', columntype: 'textbox'},
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '50%', columntype: 'textbox'}
			           ]
	            	}]
	            }
        ];
		var testGrid1 = OLBIUS.oLapGrid('grid2', config, config_popup, 'getRecruitmentRoundOlap', true, true, OLBIUS.defaultLineFunc);
		
		testGrid1.funcUpdate(function(oLap){
			oLap.update({
				'recruimentPlanId' : oLap.val('recruitment_2')
			})
		});
		testGrid1.init(function(){
			testGrid1.runAjax();
		})
	})
</script>
<script id="grid3" type="text/javascript">
	$(function(){
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		var config = {
			//FIXME CHUA TEST SERVICE
			service : 'person',
			title : '${StringUtil.wrapString(uiLabelMap.RecruitmentCost)}',
			columns : [
		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'planName', width : '25%'},
		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentCostItemName)}', datafield : 'costName', width : '25%'},
		           {text : '${StringUtil.wrapString(uiLabelMap.Amount)}', datafield : 'amount', width : '25%'},
		           {text : '${StringUtil.wrapString(uiLabelMap.Note)}', datafield : 'comment', width : '25%'},
           ],
            groupable : true,
		};
		var config_popup = [
                {
                	action : 'addDateTimeInput',
	            	params : [{
	            		id: 'from_date_3',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
	            		value: OLBIUS.dateToString(past_date),
	            	}],
	            	before : 'thru_date_3'
                },
                {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id : 'thru_date_3',
	            		label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
	            		value: OLBIUS.dateToString(cur_date),
	            	}],
	            	after : 'from_date_3'
	            },
	            {
	            	action : 'addJqxTree',
	            	params : [{
	            		id : 'organization_3',
	            		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
	            		source : rootPartyArr
	            	}],
	            	event : function(popup){
	            		var uuid = popup.$jqxWindowElement.getUuid();
	            		$('#organization_3-jqxtree-' + uuid).on('itemClick', function(event){
	            			 var fromDate = $('#from_date_3-' + uuid).jqxDateTimeInput('getDate').getTime();
	                		 var thruDate = $('#thru_date_3-' + uuid).jqxDateTimeInput('getDate').getTime();
	            			 var args = event.args;
	        			     var item = $('#organization_3-jqxtree-' + uuid).jqxTree('getItem', args.element);
	        			     var orgId = item.value;
	        			     var source = $('#recruitment_3-jqxgrid-' + uuid).jqxGrid('source');
	        			     source._source.url = "jqxGeneralServicer?sname=getRecruimentListByOrg&fromDate=" + fromDate + "&thruDate=" + thruDate + "&partyId=" + orgId;
	        			     $('#recruitment_3-jqxgrid-' + uuid).jqxGrid('source', source);
	            		})
	            	}
	            },
	            {
	            	action : 'addJqxGridMultiSource',
	            	params : [{
	            		id : 'recruitment_3',
	            		label : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlan)}',
	            		source : {
	            			datafields : [
	    			              {name : 'recruitmentPlanId', type : 'string'},
	    			              {name : 'recruitmentPlanName', type : 'string'},
	    			              {name : 'partyId', type : 'string'}
				            ],
				            datatype : 'json',
				            url : 'getRecruimentListByOrg'
	            		},
	            		value : 'recruitmentPlanId',
	            		text : 'recruitmentPlanName',
	            		columns : [
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanId)}', datafield : 'recruitmentPlanId', width : '50%', columntype: 'textbox'},
	    		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '50%', columntype: 'textbox'}
			           ]
	            	}]
	            }
        ];
		var testGrid2 = OLBIUS.oLapGrid('grid3', config, config_popup, 'getRecruitmentCostOlap', true, true, OLBIUS.defaultLineFunc);
		
		testGrid2.funcUpdate(function(oLap){
			oLap.update({
				'recruimentPlanId' : oLap.val('recruitment_3')
			})
		});
		testGrid2.init(function(){
			testGrid2.runAjax();
		})
	})
</script>
<script id="chart" type="text/javascript">
	$(function(){
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		var config = {
			chart : {
				type : 'column',
			},
			title : {
				text : '${StringUtil.wrapString(uiLabelMap.AnalysisRecruitCostChart)}',
				x : -20
			},
			xAxis : {
				type: 'category',
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
			yAxis : {
				plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#000000'
                }],
                title : {
                    text: null
                },
                min: 0
			},
			legend: {
                enabled: true
            },
		};
		var config_popup = [
                {
                	action : 'addDateTimeInput',
                	params : [{
                		id : 'from_date_chart',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
  		    		  	value : OLBIUS.dateToString(past_date)
                	}],
                	before : 'thru_date_chart'
                },
                {
                	action : 'addDateTimeInput',
                	params : [{
                		id : 'thru_date_chart',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
  		    		  	value : OLBIUS.dateToString(cur_date)
                	}],
                	after : 'from_date_chart'
                },
                {
                	action : 'addJqxTree',
                	params : [{
                		id : 'organization_chart',
                		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
                		source : rootPartyArr
                	}],
                	event : function(popup){
                		var uuid = popup.$jqxWindowElement.getUuid();
                		$('#organization_chart-jqxtree-' + uuid).on('itemClick', function(event){
               			 var fromDate = $('#from_date_chart-' + uuid).jqxDateTimeInput('getDate').getTime();
                   		 var thruDate = $('#thru_date_chart-' + uuid).jqxDateTimeInput('getDate').getTime();
               			 var args = event.args;
           			     var item = $('#organization_chart-jqxtree-' + uuid).jqxTree('getItem', args.element);
           			     var orgId = item.value;
           			     var source = $('#cost-jqxgrid-' + uuid).jqxGrid('source');
           			     source._source.url = "jqxGeneralServicer?sname=getRecruitCostCatType&fromDate=" + fromDate + "&thruDate=" + thruDate + "&partyId=" + orgId;
           			     $('#cost-jqxgrid-' + uuid).jqxGrid('source', source);
               		})
                	}
                },
                {
                	action : 'addJqxGridMultiSource',
                	params : [{
                		id : 'cost',
                		label : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlan)}',
                		source : {
                			datafields : [
        			              {name : 'recruitCostCatTypeId', type : 'string'},
        			              {name : 'recruitCostCatName', type : 'string'}
    			            ],
    			            datatype : 'json',
    			            url : 'getRecruitCostCatType'
                		},
                		value : 'recruitCostCatTypeId',
                		text : 'recruitCostCatName',
                		columns : [
        		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitCostCatId)}', datafield : 'recruitCostCatTypeId', width : '50%', columntype: 'textbox'},
        		           {text : '${StringUtil.wrapString(uiLabelMap.RecruitCostCatName)}', datafield : 'recruitCostCatName', width : '50%', columntype: 'textbox'}
    		           ]
                	}]
                }
        ];
		var testChart = OLBIUS.oLapChart('chart', config, config_popup, 'analysisRecruitCostChart', true, true, OLBIUS.defaultColumnFunc);
		testChart.funcUpdate(function(oLap){
			oLap.update({
				'recruitCostCatTypeId' : oLap.val('cost'),
				'fromDate' : oLap.val('from_date_chart'),
				'thruDate' : oLap.val('thru_date_chart'),
				'orgId' : oLap.val('organization_chart')
			})
		});
		testChart.init(function(){
			testChart.runAjax();
		})
	})
</script>