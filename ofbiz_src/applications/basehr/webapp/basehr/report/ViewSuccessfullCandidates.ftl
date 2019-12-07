<#include "script/ViewSuccessfullCandidatesScript.ftl" />
<script src="/hrresources/js/popup.extends.js"></script>
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
		}
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.SuccessCandidates)}',
				columns : [
		            {datafield : 'recruitmentPlanId', hidden : 'true'},
		            {datafield : 'partyId', hidden : 'true'},
		            {datafield : 'statusId', hidden: 'true'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield : 'recruitmentPlanName', width : '20%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.CandidateName)}', datafield : 'partyName', width : '20%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.Sexual)}', datafield : 'gender', width : '15%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield : 'phoneNumber', width : '20%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.CommonAddress)}', datafield : 'address', width : '20%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.BirthDate)}', datafield : 'dateOfBirth', width : '20%', cellsformat : 'dd/MM/yyyy'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.Status)}', datafield : 'description', width : '20%'},
	           		{text : '${StringUtil.wrapString(uiLabelMap.TotalPoint)}', datafield : 'point', width : '20%'}
	           ] 
		};
		var config_popup = [
	            {
	            	action : 'addDateTimeInput',
	            	params : [{
	            		id : 'from_date',
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
		
		var testGrid = OLBIUS.oLapGrid('grid', config, config_popup, 'getSuccessfullCandidatesDetail', true, true, OLBIUS.defaultLineFunc);
		
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				'recruimentPlanId' : oLap.val('recruitment')
			})
		});
		
		testGrid.init(function(){
			testGrid.runAjax();
		});
	})
</script>
<script id="chart" type="text/javascript">
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
			chart : {
				type : 'column'
			},
			title : {
				text : '${StringUtil.wrapString(uiLabelMap.EffectivelyRecruitingChart)}',
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
            		value: OLBIUS.dateToString(past_date)
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
        			     var source = $('#recruitment_chart-jqxgrid-' + uuid).jqxGrid('source');
        			     source._source.url = "jqxGeneralServicer?sname=getRecruimentListByOrg&fromDate=" + fromDate + "&thruDate=" + thruDate + "&partyId=" + orgId;
        			     $('#recruitment_chart-jqxgrid-' + uuid).jqxGrid('source', source);
            		})
            	}
            },
            {
            	action : 'addJqxGridMultiSource',
            	params : [{
            		id : 'recruitment_chart',
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
		
		var testChart = OLBIUS.oLapChart('chart', config, config_popup, 'effectivelyRecruitingChart', true, true, OLBIUS.defaultColumnFunc);
		testChart.funcUpdate(function(oLap){
			oLap.update({
				'recruimentPlanId' : oLap.val('recruitment_chart')
			})
		});
		testChart.init(function(){
			testChart.runAjax();
		})
	})
</script>