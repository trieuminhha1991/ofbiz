<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<@jqGridMinimumLib />
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
	
	//Prepare for result data
	<#assign reviewResults = delegator.findList("ResultItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("resultTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["REVIEW_RESULT"]), null, null, null, false)>
	var reviewResultData = new Array();
	<#list reviewResults as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['resultId'] = '${item.resultId}';
		row['description'] = '${description}';
		reviewResultData[${item_index}] = row;
	</#list>
	
	isLeaderReview = false;
	isHrmReview = false;
	isCeoReview = false;
	
	 var idColumnFilter = function () {
         var filtergroup = new $.jqx.filter();
         var filter_or_operator = 1;
         var filtervalue = '${StringUtil.wrapString(parameters.partyId?if_exists)}';
         var filtercondition = 'contains';
         var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
         filtergroup.addfilter(filter_or_operator, filter);
         return filtergroup;
     }();
</script>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'offerProbationId', type: 'string' },
					 { name: 'emplPositionTypeId', type: 'date', other: 'Timestamp' },
					 { name: 'partyIdWork', type: 'string' },
					 { name: 'inductedStartDate', type: 'date', other: 'Timestamp' },
					 { name: 'inductedCompletionDate', type: 'date', other: 'Timestamp' },
					 { name: 'statusId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.probEmployeeId}', datafield: 'partyId', width: 150, editable: false, filter: idColumnFilter,
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

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" jqGridMinimumLibEnable="false" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListProbation" mouseRightMenu="true" contextMenuId="contextMenu" dataField=dataField columnlist=columnlist editable="true"
		/>
<#include "jqxReviewProbation.ftl">
<#include "jqxReportDetail.ftl">
<div id='contextMenu' style="display: none">
	<ul>
	    <li id="leaderReviewItem"><i class="fa fa-check"></i>&nbsp;${uiLabelMap.LeaderReview}</li>
	    <li id="proposeToHRMReview"><i class="fa fa-check-circle-o"></i>&nbsp;${uiLabelMap.proposeToHRMReview}</li>
	    <li id="hrmReviewItem"><i class="fa fa-check-circle"></i>&nbsp;${uiLabelMap.HrmReview}</li>
	    <li id="proposeToCeoReview"><i class="fa fa-check-circle-o"></i>&nbsp;${uiLabelMap.proposeToCeoReview}</li>
	    <li id="ceoReviewItem"><i class="fa fa-check-circle-o"></i>&nbsp;${uiLabelMap.CeoReview}</li>
	    <li id="reportDetailItem"><i class="fa fa-check-circle-o"></i>&nbsp;${uiLabelMap.reportDetail}</li>
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
		
		$("#alterpopupReviewProbation").jqxWindow({
			showCollapseButton: false, maxHeight: 1000,modalZIndex: 1000, autoOpen: false, maxWidth: "80%", height: 600, minWidth: '40%', width: "80%", isModal: true,
	        theme:'olbius', collapsed:false, cancelButton : '#cancel',
	        initContent: function(){
	        	$('#mainSplitter').jqxSplitter({ width: '98%', height: '90%', orientation: 'vertical', panels: [{ size: 200 }, { size: 300 }] });
	        	$("#feedExpander").jqxExpander({toggleMode: 'none', showArrow: false, width: "100%", height: "100%", 
	                initContent: function () {
	                    $('#jqxTree').jqxTree({ height: '100%', width: '100%', theme: 'energyblue', hasThreeStates: false});
	                    var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
	    				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
	    				if(rowData['statusId'] == 'PROB_RP_LPROPOSED' || rowData['statusId'] == 'PROB_RP_LREVIEWED'){
	    					$('#jqxTree').jqxTree('removeItem', $('#hrmReview')[0]);
	    					$('#jqxTree').jqxTree('removeItem', $('#ceoReview')[0]);
	    				}else if(rowData['statusId'] == 'PROB_RP_HPROPOSED' || rowData['statusId'] == 'PROB_RP_HREVIEWED'){
	    					$('#jqxTree').jqxTree('removeItem', $('#ceoReview')[0]);
	    				}
	                }
	            });
	        	
	            //Bind event select in tree
	            $('#jqxTree').on('select', function (event) {
	                var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	                if(item.id=='probReportItem'){
	                	$.ajax({
	                		url: 'loadProbReport',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#formSplitter").html(data);
	                        }
	                	});
	                }
	                if(item.id=='leaderReview'){
	                	$.ajax({
	                		url: 'loadLeaderReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#formSplitter").html(data);
	                        }
	                	});
	                }
	                if(item.id=='hrmReview'){
	                	$.ajax({
	                		url: 'loadHrmReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#formSplitter").html(data);
	                        }
	                	});
	                }
	                if(item.id=='ceoReview'){
	                	$.ajax({
	                		url: 'loadCeoReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#formSplitter").html(data);
	                        }
	                	});
	                }
	            });
	        }
	    });
		
		$("#alterpopupReportDetail").jqxWindow({
			showCollapseButton: false, maxHeight: 1000,modalZIndex: 1000, autoOpen: false, maxWidth: "80%", height: 600, minWidth: '40%', width: "80%", isModal: true,
	        theme:'olbius', collapsed:false, cancelButton : '#cancel',
	        initContent: function(){
	        	$('#mainSplitterDT').jqxSplitter({ width: '98%', height: '90%', orientation: 'vertical', panels: [{ size: 200 }, { size: 300 }] });
	        	$("#feedExpanderDT").jqxExpander({toggleMode: 'none', showArrow: false, width: "100%", height: "100%", 
	                initContent: function () {
	                    $('#jqxTreeDT').jqxTree({ height: '100%', width: '100%', theme: 'energyblue', hasThreeStates: false});
	                    var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
	    				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
	    				if(rowData['statusId'] == 'PROB_RP_LPROPOSED' || rowData['statusId'] == 'PROB_RP_CREATED'){
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#hrmReviewDT')[0]);
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#leaderReviewDT')[0]);
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#ceoReviewDT')[0]);
	    				}else if(rowData['statusId'] == 'PROB_RP_LREVIEWED' || rowData['statusId'] == 'PROB_RP_HPROPOSED'){
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#hrmReviewDT')[0]);
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#ceoReviewDT')[0]);
	    				}else if(rowData['statusId'] == 'PROB_RP_HREVIEWED' || rowData['statusId'] == 'PROB_RP_CPROPOSED'){
	    					$('#jqxTreeDT').jqxTree('removeItem', $('#ceoReviewDT')[0]);
	    				}
	                }
	            });
	        	
	            //Bind event select in tree
	            $('#jqxTreeDT').on('select', function (event) {
	                var item = $('#jqxTreeDT').jqxTree('getItem', event.args.element);
	                if(item.id=='probReportItemDT'){
	                	$.ajax({
	                		url: 'loadProbReport',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#detailForm").html(data);
	                        }
	                	});
	                }
	                if(item.id=='leaderReviewDT'){
	                	$.ajax({
	                		url: 'loadLeaderReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#detailForm").html(data);
	                        }
	                	});
	                }
	                if(item.id=='hrmReviewDT'){
	                	$.ajax({
	                		url: 'loadHrmReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#detailForm").html(data);
	                        }
	                	});
	                }
	                if(item.id=='ceoReviewDT'){
	                	$.ajax({
	                		url: 'loadCeoReview',
	                		data: {offerProbationId: offerProbationId},
	                		type: "POST",
	                		async: false,
	                		success : function(data) {
	                			$("#detailForm").html(data);
	                        }
	                	});
	                }
	            });
	        }
	    });
		
		//Create Context Menu
		var contextMenu = $("#contextMenu").jqxMenu({ width: 250, height: 160, theme: theme, autoOpenPopup: false, mode: 'popup'});
		$("#contextMenu").jqxMenu('disable', 'leaderReviewItem', true);
		$("#contextMenu").jqxMenu('disable', 'hrmReviewItem', true);
		$("#contextMenu").jqxMenu('disable', 'ceoReviewItem', true);
		$("#contextMenu").jqxMenu('disable', 'proposeToHRMReview', true);
		$("#contextMenu").jqxMenu('disable', 'proposeToCeoReview', true);
		$('#jqxgrid').on('rowClick', function (event) {
			var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
			var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			<#if Static["com.olbius.util.PartyUtil"].isHeadOfDept(delegator, userLogin)>
				if(rowData['statusId'] == 'PROB_RP_LPROPOSED' || rowData['statusId'] == 'PROB_RP_LREVIEWED'){
					$("#contextMenu").jqxMenu('disable', 'leaderReviewItem', false);
					if(rowData['statusId'] == 'PROB_RP_LREVIEWED'){
						$("#contextMenu").jqxMenu('disable', 'proposeToHRMReview', false);
					}
				}
			</#if>
			<#if Static["com.olbius.util.PartyUtil"].isAdmin(delegator, userLogin)>
				if(rowData['statusId'] == 'PROB_RP_HPROPOSED' || rowData['statusId'] == 'PROB_RP_HREVIEWED'){
					$("#contextMenu").jqxMenu('disable', 'hrmReviewItem', false);
					if(rowData['statusId'] == 'PROB_RP_HREVIEWED'){
						$("#contextMenu").jqxMenu('disable', 'proposeToCeoReview', false);
					}
				}
			</#if>
			<#if Static["com.olbius.util.PartyUtil"].isCEO(delegator, userLogin)>
				if(rowData['statusId'] == 'PROB_RP_CPROPOSED'){
					$("#contextMenu").jqxMenu('disable', 'ceoReviewItem', false);
				}
			</#if>                                                                                  
		});
		//handle context menu clicks.
		$("#contextMenu").on('itemclick', function (event) {
			var element = event.args;
			if(element.getAttribute('id') == 'leaderReviewItem' || element.getAttribute('id') == 'hrmReviewItem' || element.getAttribute('id') == 'ceoReviewItem'){
				var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
				offerProbationId = rowData['offerProbationId'];
				$("#alterpopupReviewProbation").jqxWindow('open');
			}else if(element.getAttribute('id') == 'proposeToHRMReview'){
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
				<#assign hrmId = Static["com.olbius.util.PartyUtil"].getHrmAdmin(delegator)>
				submitData['header'] = 'Đánh giá thử việc nhân viên ' + partyName;
				var now = new Date();
				submitData['dateTime'] = now.getFullYear()+ "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
				submitData['action'] = 'FindProbation';
				submitData['targetLink'] = 'partyId=' + dataRow['partyId'];
				submitData['state'] = 'open';
				submitData['partyId'] = '${hrmId}';
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
								data: {offerProbationId : dataRow['offerProbationId'], statusId: 'PROB_RP_HPROPOSED'},
								success: function(data){
									$("#jqxgrid").jqxGrid('updatebounddata');
								}
							});
						}
					}
				});
		  	}else if(element.getAttribute('id') == 'proposeToCeoReview'){
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
				<#assign ceoId = Static["com.olbius.util.PartyUtil"].getCEO(delegator)>
				submitData['header'] = 'Đánh giá thử việc nhân viên ' + partyName;
				var now = new Date();
				submitData['dateTime'] = now.getFullYear()+ "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
				submitData['action'] = 'FindProbation';
				submitData['targetLink'] = 'partyId=' + dataRow['partyId'];
				submitData['state'] = 'open';
				submitData['partyId'] = '${ceoId}';
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
								data: {offerProbationId : dataRow['offerProbationId'], statusId: 'PROB_RP_CPROPOSED'},
								success: function(data){
									$("#jqxgrid").jqxGrid('updatebounddata');
								}
							});
						}
					}
				});
			  }else if(element.getAttribute('id') == 'reportDetailItem'){
				  	var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
					var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
					offerProbationId = rowData['offerProbationId'];
					$("#alterpopupReportDetail").jqxWindow('open');
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
		$('#review').on('click', function(){
			if(isLeaderReview){
				var submitData = {};
				var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
				var resultId = "RR_ACCEPTED";
				if($("#jqxBtnRejected").val()){
					resultId = 'RR_REJECTED';
				}else if($("#jqxBtnExtTime").val()){
					resultId = "RR_EXTTIME";
				}
				submitData['resultId'] = resultId;
				submitData['extTime'] = $("#extTime").val();
				submitData['assignedTask'] = $("#assignedTask").val();
				submitData['futureEdu'] = $("#futureEdu").val();
				submitData['otherReq'] = $("#otherReq").val();
				submitData['comment'] = $("#comment").val();
				submitData['listProbReviewItems'] = JSON.stringify(reviewData);
				
				//Send Ajax Request
				var leaderReviewUrl = 'createLeaderReview';
				if($("#probReviewId").val()){
					leaderReviewUrl = 'updateLeaderReview';
					submitData['probReviewId'] = $("#probReviewId").val();
				}else{
					submitData['offerProbationId'] = rowData['offerProbationId'];
				}
				$.ajax({
					url: leaderReviewUrl,
					type: "POST",
					data: submitData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(data._ERROR_MESSAGE_LIST_){
							bootbox.confirm("${uiLabelMap.reviewError}", function(result) {
								return;
							});
						}else{
							$("#alterpopupReviewProbation").jqxWindow('close');
							$("#jqxgrid").jqxGrid('updatebounddata');
						}
					}
				});
			}
			if(isHrmReview){
				var submitData = {};
				var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
				var resultId = "RR_ACCEPTED";
				if($("#jqxHrmRejected").val()){
					resultId = 'RR_REJECTED';
				}else if($("#jqxHrmExtTime").val()){
					resultId = "RR_EXTTIME";
				}
				submitData['resultId'] = resultId;
				submitData['extTime'] = $("#hrmExtTime").val();
				submitData['comment'] = $("#hrmComment").val();
				hrmReviewUrl = "createHrmReview";
				if($("#probReviewId").val()){
					submitData['probReviewId'] = $("#probReviewId").val();
					hrmReviewUrl = "updateHrmReview";
				}else{
					submitData['offerProbationId'] = rowData['offerProbationId'];
				}
				//Send Ajax Request
				$.ajax({
					url: hrmReviewUrl,
					type: "POST",
					data: submitData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(data.responseMessage == 'error'){
							bootbox.confirm("${uiLabelMap.reviewError}", function(result) {
								return;
							});
						}else{
							isHrmReview = false;
							$("#alterpopupReviewProbation").jqxWindow('close');
							$("#jqxgrid").jqxGrid('updatebounddata');
						}
					}
				});
			}
			if(isCeoReview){
				var submitData = {};
				var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
				var resultId = "RR_ACCEPTED";
				if($("#jqxCeoRejected").val()){
					resultId = 'RR_REJECTED';
				}else if($("#jqxCeoExtTime").val()){
					resultId = "RR_EXTTIME";
				}
				submitData['offerProbationId'] = rowData['offerProbationId'];
				submitData['resultId'] = resultId;
				submitData['extTime'] = $("#ceoExtTime").val();
				submitData['comment'] = $("#ceoComment").val();
				ceoReviewUrl = "createCeoReview";
				if($("#probReviewId").val()){
					submitData['probReviewId'] = $("#probReviewId").val();
					ceoReviewUrl = "updateCeoReview";
				}else{
					submitData['offerProbationId'] = rowData['offerProbationId'];
				}
				//Send Ajax Request
				$.ajax({
					url: ceoReviewUrl,
					type: "POST",
					data: submitData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(data.responseMessage == 'error'){
							bootbox.confirm("${uiLabelMap.reviewError}", function(result) {
								return;
							});
						}else{
							isCeoReview = false;
							$("#alterpopupReviewProbation").jqxWindow('close');
							$("#jqxgrid").jqxGrid('updatebounddata');
						}
					}
				});
			}
		});
	});
</script>