<div class="row-fluid" >
	<div class="span12">
		<ol type="I">
		  <li><h6><b>${uiLabelMap.SelfReview}</b></h6></li>
		  <div id="jqxgridDisplaySelfReview"></div>
		  <li><h6><b>${uiLabelMap.JobStatus}</h6></b></li>
		  <ol type="a">
		  	<li><h6><b>${uiLabelMap.education}</h6></b></li>
		  	<div id="disEducation"></div>
		  	<li><h6><b>${uiLabelMap.completedJob}</h6></b></li>
		  	<div id="disCompletedJob"></div>
		  	<li><h6><b>${uiLabelMap.advantageAndDisadvantage}</h6></b></li>
		  	<div id="disAdvDisadv"></div>
		  </ol>
		  <li><h6><b>${uiLabelMap.futureJob}</h6></b></li>
		  <div id="disFutureJob"></div>
		  <li><h6><b>${uiLabelMap.JobPropsal}</h6></b></li>
		  <ul>
		  	<li><h6><b>${uiLabelMap.jobProposal}</h6></b></li>
		  	<div id="disJobProposal"></div>
		  	<li><h6><b>${uiLabelMap.policyProposal}</h6></b></li>
		  	<div id="disPolicyProposal"></div>
		  	<li><h6><b>${uiLabelMap.eduWishes}</h6></b></li>
		  	<div id="disEduWishes"></div>
		  </ul>
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
	
	$("#jqxgridDisplaySelfReview").jqxGrid(
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
	<#assign probReport = delegator.findList("ProbationaryReportView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("offerProbationId", parameters.offerProbationId), null, null, null, false)>
	var education = $('<textarea />').html('${probReport[0].education}').text();
	$("#disEducation").append(education);
	var completedJob = $('<textarea />').html('${probReport[0].completedJob}').text();
	$("#disCompletedJob").append(completedJob);
	var advantageAndDisadvantage = $('<textarea />').html('${probReport[0].advantageAndDisadvantage}').text();
	$("#disAdvDisadv").append(advantageAndDisadvantage);
	var futureJob = $('<textarea />').html('${probReport[0].futureJob}').text();
	$("#disFutureJob").append(futureJob);
	var jobProposal = $('<textarea />').html('${probReport[0].jobProposal}').text();
	$("#disJobProposal").append(jobProposal);
	var policyProposal = $('<textarea />').html('${probReport[0].policyProposal}').text();
	$("#disPolicyProposal").append(policyProposal);
	var eduWishes = $('<textarea />').html('${probReport[0].eduWishes}').text();
	$("#disEduWishes").append(eduWishes);
</script>