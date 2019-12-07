<div class="row-fluid" >
	<div class="span12">
		<ol type="I">
		<li><h6><b>${uiLabelMap.LeaderReview}</b></h6></li>
		  <div id="jqxgridDisplayReview"></div>
		  <li><h6><b>${uiLabelMap.proposal}</h6></b></li>
		  <div id="disResultId"></div>
		  <li><h6><b>${uiLabelMap.extTime}</h6></b></li>
		  <div id="disExtTime"></div>
		  <li><h6><b>${uiLabelMap.assignedTask}</h6></b></li>
		  <div id="disAssignedTask"></div>
		  <li><h6><b>${uiLabelMap.futureEdu}</h6></b></li>
		  <div id="disFutureEdu"></div>
		  <li><h6><b>${uiLabelMap.otherReq}</h6></b></li>
		  <div id="disOtherReq"></div>
		</ol>  
	</div>
</div>
<script>
	selfReviewData = new Array();
	<#assign mapCondition = Static["org.ofbiz.base.util.UtilMisc"].toMap("offerProbationId", parameters.offerProbationId, "roleTypeId", "EMPLOYEE")>
	<#assign selfReview = delegator.findList("ProbationaryReviewItemView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(mapCondition), null, null, null, false)>
	<#list selfReview as item>
		var row = {};
		row['probReviewItemTypeId'] = '${item.probReviewItemTypeId?if_exists}';
		row['resultId'] = '${item.resultId?if_exists}';
		row['comment'] = '${item.comment?if_exists}';
		selfReviewData[${item_index}] = row;
	</#list>
	var source =
	{
	    localdata: selfReviewData,
	    datatype: "array",
	    datafields:
	    [
	    	{ name: 'probReviewItemTypeId', type: 'string' },
			{ name: 'resultId', type: 'string' },
			{ name: 'comment', type: 'string' }
	    ]
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#jqxgridDisplayReview").jqxGrid(
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
	<#assign mapCondition = Static["org.ofbiz.base.util.UtilMisc"].toMap("offerProbationId", parameters.offerProbationId, "roleTypeId", "MANAGER")>
	<#assign probReview = delegator.findList("ProbationaryReviewView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(mapCondition), null, null, null, false)>
	var assignedTask = $('<textarea />').html('${probReview[0].assignedTask?if_exists}').text();
	$("#disAssignedTask").append(assignedTask);
	var futureEdu = $('<textarea />').html('${probReview[0].futureEdu?if_exists}').text();
	$("#disFutureEdu").append(futureEdu);
	var otherReq = $('<textarea />').html('${probReview[0].otherReq?if_exists}').text();
	$("#disOtherReq").append(otherReq);
	var resultId = $('<textarea />').html('${probReview[0].resultId?if_exists}').text();
	for(var i = 0; i < reviewResultData.length; i++){
		if('${probReview[0].resultId?if_exists}' == reviewResultData[i].resultId){
			resultId = $('<textarea />').html(reviewResultData[i].description).text();
		}
	}
	$("#disResultId").append(resultId);
	$("#disExtTime").html('${probReview[0].extTime?if_exists}');
</script>