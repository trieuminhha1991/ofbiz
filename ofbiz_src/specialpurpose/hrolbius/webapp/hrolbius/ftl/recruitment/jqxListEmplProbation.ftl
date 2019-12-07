<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script>	
	//Prepare data for Employee Position Type 
	<#assign listAllEmplPositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false) >
	var allPositionTypeData = new Array();
	<#list listAllEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		allPositionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PROB_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	//Prepare for status data
	<#assign probReviewItemTypes = delegator.findList("ProbationaryReviewItemType", null, null, null, null, false)>
	var probReviewItemTypeData = new Array();
	<#list probReviewItemTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['probReviewItemTypeId'] = '${item.probReviewItemTypeId}';
		row['description'] = '${description}';
		probReviewItemTypeData[${item_index}] = row;
	</#list>
	
	//Prepare for result data
	<#assign results = delegator.findList("ResultItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("resultTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["REVIEW_ITEM_RESULT"]), null, null, null, false)>
	var resultData = new Array();
	<#list results as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['resultId'] = '${item.resultId}';
		row['description'] = '${description}';
		resultData[${item_index}] = row;
	</#list>
	isLeaderReview = false;
	isHrmReview = false;
	isCeoReview = false;
</script>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'offerProbationId', type: 'string' },
					 { name: 'emplPositionTypeId', type: 'date', other: 'Timestamp' },
					 { name: 'partyIdWork', type: 'string' },
					 { name: 'inductedStartDate', type: 'date', other: 'Timestamp' },
					 { name: 'inductedCompletionDate', type: 'date', other: 'Timestamp' },
					 { name: 'statusId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.probEmployeeId}', datafield: 'partyId', width: 150, editable: false,
						cellsrenderer: function(row, column, value){
						  var partyName = value;
                  		  $.ajax({
                  				url: 'getPartyName',
                  				type: 'POST',
                  				data: {partyId: value},
                  				dataType: 'json',
                  				async: false,
                  				success : function(data) {
                  					if(!data._ERROR_MESSAGE_){
                  						partyName = data.partyName;
                  					}
                  		        }
                  			});
                  		  return '<span title' + value + '>' + partyName + '</span>';
						}
                     },
                     { text: '${uiLabelMap.probStartDate}', datafield: 'inductedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false},
                     { text: '${uiLabelMap.probEndDate}', datafield: 'inductedCompletionDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false},
                     { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', width: 150, editable: false,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < allPositionTypeData.length; i++){
								if(value == allPositionTypeData[i].emplPositionTypeId){
									return '<span title=' + value + '>' + allPositionTypeData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
                     },
                     { text: '${uiLabelMap.Department}', datafield: 'partyIdWork', width: 200, editable: false,
                    	 cellsrenderer: function(row, column, value){
   						  var partyName = value;
                     		  $.ajax({
                     				url: 'getPartyName',
                     				type: 'POST',
                     				data: {partyId: value},
                     				dataType: 'json',
                     				async: false,
                     				success : function(data) {
                     					if(!data._ERROR_MESSAGE_){
                     						partyName = data.partyName;
                     					}
                     		        }
                     			});
                     		  return '<span title' + value + '>' + partyName + '</span>';
   						}
                     },
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', columntype:'dropdownlist',
                    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>';
								}
							}
							return '<span> ' + value + '</span>';
						}
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" jqGridMinimumLibEnable="true" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplProbation" mouseRightMenu="true" contextMenuId="contextMenu" dataField=dataField columnlist=columnlist editable="true"
		/>
<#include "jqxEditProbReport.ftl">
<div id='contextMenu' style="display: none">
	<ul>
	    <li id="createProbReportItem"><i class="fa fa-pencil-square-o"></i>&nbsp;${uiLabelMap.createProbReport}</li>
	    <li id="proposeToLeader"><i class="fa fa-paper-plane"></i>&nbsp;${uiLabelMap.proposeToLeader}</li>
	</ul>
</div>
<script>
	$( document ).ready(function() {
		theme = 'olbius';
		//Create alterpopupNewProbReport
		selfReviewData = new Array();
		for(var i = 0; i < probReviewItemTypeData.length; i++){
			var row = {};
			row['probReviewItemTypeId'] = probReviewItemTypeData[i]['probReviewItemTypeId'];
			selfReviewData[i] = row;
		}
		$("#alterpopupNewProbReport").jqxWindow({
			showCollapseButton: false, maxHeight: 1000,modalZIndex: 1000, autoOpen: false, maxWidth: "80%", height: 530, minWidth: '40%', width: "80%", isModal: true,
	        theme:'olbius', collapsed:false,
	        initContent: function(){
	    	  $('#jqxTabs').jqxTabs({ width: '98%', height: 430, position: 'top', disabled:true,
		        	initTabContent:function (tab) {
		        		if(tab == 0){
		        			var source =
		                    {
		                        localdata: selfReviewData,
		                        datatype: "array",
		                        datafields:
		                        [
	                            	{ name: 'probReviewItemTypeId', type: 'string' },
									{ name: 'resultId', type: 'string' },
									{ name: 'comment', type: 'string' }
		                        ],
		                        updaterow: function (rowid, rowdata, commit) {
		                        	selfReviewData[rowid] = rowdata;
			        		        commit(true);
			        		    }
		                    };
		                	var dataAdapter = new $.jqx.dataAdapter(source);
		                    
		                	$("#jqxgridSelfReview").jqxGrid(
		                    {
		                        width: '98%',
		                        source: dataAdapter,
		                        columnsresize: true,
		                        pageable: true,
		                        autoheight: true,
		                        showtoolbar: false,
		                        editable: true,
		                        columns: [
	                          	  { text: '${uiLabelMap.probReviewItemTypeId}', datafield: 'probReviewItemTypeId', width: 350, editable: false,
	                          		 cellsrenderer: function(row, column, value){
	                          			 for(var i = 0; i < probReviewItemTypeData.length; i++){
	                          				 if(value == probReviewItemTypeData[i].probReviewItemTypeId){
	                          					 return '<span title=' + value + '>' + probReviewItemTypeData[i].description + '</span>'
	                          				 }
	                          			 }
	                          			 return '<span>' + value + '</span>'
	                          		 }
	                          	  },
		                          { text: '${uiLabelMap.resultId}', datafield: 'resultId', width: 150,columntype: 'dropdownlist', editable: true,
	                          		 cellsrenderer: function(row, column, value){
	                          			 for(var i = 0; i < resultData.length; i++){
	                          				 if(value == resultData[i].resultId){
	                          					 return '<span title=' + value + '>' + resultData[i].description + '</span>'
	                          				 }
	                          			 }
	                          			 return '<span>' + value + '</span>'
	                          		 },
	                          		 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                          	        editor.jqxDropDownList({source: resultData, displayMember: 'description', valueMember: 'resultId'});
	                          	    }
		                          },
		                          { text: '${uiLabelMap.comment}', datafield: 'comment', editable: true}
		                        ]
		                    });
	        			}else if(tab == 1){
	        				$("#education").jqxEditor({tools: 'bold italic underline font size'});
	        				$("#completedJob").jqxEditor({tools: 'bold italic underline font size'});
	        				$("#advantageAndDisadvantage").jqxEditor({tools: 'bold italic underline font size'});
	        			}else if(tab == 2){
	        				$("#futureJob").jqxEditor({tools: 'bold italic underline font size'});
	        			}else{
	        				$("#jobProposal").jqxEditor({tools: 'bold italic underline font size'});
	        				$("#policyProposal").jqxEditor({tools: 'bold italic underline font size'});
	        				$("#eduWishes").jqxEditor({tools: 'bold italic underline font size'});
	        			}
	        		}
	    	  });
	        }
	    });
		
		//Create Context Menu
		var contextMenu = $("#contextMenu").jqxMenu({ width: 250, height: 120, theme: theme, autoOpenPopup: false, mode: 'popup'});
		//handle context menu clicks.
		$("#contextMenu").on('itemclick', function (event) {
			var element = event.args;
			if(element.getAttribute('id') == 'createProbReportItem'){
				$("#alterpopupNewProbReport").jqxWindow('open');
			}else if(element.getAttribute('id') == 'proposeToLeader'){
				var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var dataRow = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
				var submitData = {};
				var partyName = dataRow['partyId'];
				$.ajax({
    				url: 'getPartyName',
    				type: 'POST',
    				data: {partyId: dataRow['partyId']},
    				dataType: 'json',
    				async: false,
    				success : function(data) {
    					if(!data._ERROR_MESSAGE_){
    						partyName = data.partyName;
    					}
    		        }
    			});
				$.ajax({
    				url: 'getManagerOfEmpl',
    				type: 'POST',
    				data: {partyId: dataRow['partyId']},
    				dataType: 'json',
    				async: false,
    				success : function(data) {
    					if(!data._ERROR_MESSAGE_){
    						managerId = data.managerId;
    					}
    		        }
    			});
				submitData['header'] = 'Đánh giá thử việc nhân viên ' + partyName;
				var now = new Date();
				submitData['dateTime'] = now.getFullYear()+ "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
				submitData['action'] = 'FindProbation';
				submitData['targetLink'] = 'partyId=' + dataRow['partyId'];
				submitData['state'] = 'open';
				submitData['partyId'] = managerId;
				submitData['ntfType'] = 'ONE';
				$.ajax({
					type: 'POST',
					url: 'createNotification',
					async: 'false',
					data: submitData,
					success: function(data){
						if(data._EVENT_MESSAGE_){
							$.ajax({
								type: 'POST',
								url: 'updateOfferProbation',
								async: 'false',
								data: {offerProbationId : dataRow['offerProbationId'], statusId: 'PROB_RP_LPROPOSED'},
								success: function(data){
									$("#jqxgrid").jqxGrid('updatebounddata');
								}
							});
						}
					}
				});
			}
		});
		
		$('.next').on('click', function(){
			var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
			$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
			$("#jqxTabs").jqxTabs('disableAt', selectedItem);
			$("#jqxTabs").jqxTabs('next');
		});
		
		$('.back').on('click', function(){
			var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
			$("#jqxTabs").jqxTabs('enableAt', selectedItem - 1);
			$("#jqxTabs").jqxTabs('disableAt', selectedItem);
			$("#jqxTabs").jqxTabs('previous');
		});
		
		$('#submit').on('click', function(){
			//Get selected row in grid
			var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
			var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			
			var submitData = {};
			var inductedCompletionDate = rowData['inductedCompletionDate'];
			submitData['thruDate'] = inductedCompletionDate.getFullYear()+ "-" + (inductedCompletionDate.getMonth() + 1) + "-" + inductedCompletionDate.getDate();
			var inductedStartDate = rowData['inductedStartDate'];
			submitData['fromDate'] = inductedStartDate.getFullYear()+ "-" + (inductedStartDate.getMonth() + 1) + "-" + inductedStartDate.getDate();
			submitData['offerProbationId'] = rowData['offerProbationId'];
			submitData['education'] = $("#education").val();
			submitData['completedJob'] = $("#completedJob").val();
			submitData['advantageAndDisadvantage'] = $("#advantageAndDisadvantage").val();
			submitData['futureJob'] = $("#futureJob").val();
			submitData['jobProposal'] = $("#jobProposal").val();
			submitData['policyProposal'] = $("#policyProposal").val();
			submitData['eduWishes'] = $("#eduWishes").val();
			submitData['listProbReviewItems'] = JSON.stringify(selfReviewData);
			
			//Send Ajax Request
			$.ajax({
				url: 'createProbationaryReport',
				type: "POST",
				data: submitData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(data.responseMessage == 'error'){
						bootbox.confirm("${uiLabelMap.createError}", function(result) {
							return;
						});
					}else{
						$("#alterpopupNewProbReport").jqxWindow('close');
						$("#jqxgrid").jqxGrid('updatebounddata');
					}
				}
			});
		});
	});
</script>