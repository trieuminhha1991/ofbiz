<div class="row-fluid" >
	<div class="span12">
	  <input type="hidden" id="probReviewId" value="${leaderReview?if_exists.probReviewId?if_exists}" />
	  <ol type="I">
		  <li><h6><b>${uiLabelMap.review}</b></h6></li>
		  <div id="jqxgridLeaderReview"></div>
		  <li><h6><b>${uiLabelMap.proposal}</b></h6></li>
		  <div>
			 <div style='margin-top: 10px; display: inline-block' id='jqxBtnAccepted'><span>${uiLabelMap.prAccepted}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxBtnRejected'><span>${uiLabelMap.prRejected}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxBtnExtTime'><span>${uiLabelMap.prContinuedProbation}</span></div>
	      </div>
	      <div class="row-fluid" >
		      <div class="span12">
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 170px !important">${uiLabelMap.extTime}</label>
						<div class="controls" style="margin-left: 230px !important">
							<div id="extTime"></div>
						</div>
					</div>
		      	</div>
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 90px !important;">${uiLabelMap.comment}</label>
						<div class="controls" style="margin-left: 150px !important">
							<input id="comment">
						</div>
					</div>
		      	</div>
		      </div>
	      </div>
	      <li><h6><b>${uiLabelMap.ifAcccepted}</b></h6></li>
	      <ul>
		      <li><h6><b>${uiLabelMap.assignedTask}</b></h6></li>
			  <div id="assignedTask"></div>
			  <li><h6><b>${uiLabelMap.futureEdu}</b></h6></li>
			  <div id="futureEdu"></div>
			  <li><h6><b>${uiLabelMap.otherReq}</b></h6></li>
			  <div id="otherReq"></div>
	      <ul>
      </ol>
	</div>
</div>
<script>
	isLeaderReview = true;
	//Prepare for status data
	var reviewData = new Array();
	if('${leaderReview?if_exists.probReviewId?if_exists}' != ''){
		<#assign listProbReviewItem = delegator.findList("ProbationaryReviewItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("probReviewId", leaderReview?if_exists.probReviewId?if_exists), null, null, null, false)>
		<#list listProbReviewItem as item>
			var row = {};
			row['probReviewItemTypeId'] = '${item.probReviewItemTypeId?if_exists}';
			row['probReviewItemId'] = '${item.probReviewItemId?if_exists}';
			row['resultId'] = '${item.resultId?if_exists}';
			row['comment'] = '${item.comment?if_exists}';
			reviewData['${item_index}'] = row;
		</#list>
	}else{
		for(var i = 0; i < probReviewItemTypeData.length; i++){
			var row = {};
			row['probReviewItemTypeId'] = probReviewItemTypeData[i]['probReviewItemTypeId'];
			reviewData[i] = row;
		}
	}
	var source =
	{
	    localdata: reviewData,
	    datatype: "array",
	    datafields:
	    [
	    	{ name: 'probReviewItemTypeId', type: 'string' },
			{ name: 'resultId', type: 'string' },
			{ name: 'comment', type: 'string' },
			{ name: 'probReviewItemId', type: 'string' }
	    ],
	    updaterow: function (rowid, rowdata, commit) {
	    	reviewData[rowid] = rowdata;
	        commit(true);
	    }
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#jqxgridLeaderReview").jqxGrid(
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
	var isAccepted = true;
	var isRejected = false;
	var isExt = false;
	<#if leaderReview?if_exists.resultId?if_exists = 'RR_ACCEPTED'>
		isAccepted = true;
		isRejected = false;
		isExt = false;
	<#elseif leaderReview?if_exists.resultId?if_exists = 'RR_REJECTED'>
		isAccepted = false;
		isRejected = true;
		isExt = false;
	<#elseif leaderReview?if_exists.resultId?if_exists = 'RR_EXTTIME'>
		isAccepted = false;
		isRejected = false;
		isExt = true;
	</#if>
	$("#jqxBtnAccepted").jqxRadioButton({ width: 150, height: 25, checked: isAccepted});
    $("#jqxBtnRejected").jqxRadioButton({ width: 150, height: 25, checked: isRejected});
    $("#jqxBtnExtTime").jqxRadioButton({ width: 150, height: 25, checked: isExt});
    $("#extTime").jqxNumberInput({ width: '100px', height: '25px',  spinButtons: true, decimal: '${leaderReview?if_exists.extTime?if_exists}'});
    $("#comment").jqxInput({width: 200, height: 25});
    $("#comment").val('${leaderReview?if_exists.comment?if_exists}');
    $("#assignedTask").jqxEditor({tools: 'bold italic underline font size'});
    var assignedTask = $('<textarea/>').html('${leaderReview?if_exists.assignedTask?if_exists}').text();
    $("#assignedTask").val(assignedTask);
    $("#futureEdu").jqxEditor({tools: 'bold italic underline font size'});
    var futureEdu = $('<textarea/>').html('${leaderReview?if_exists.futureEdu?if_exists}').text();
    $("#futureEdu").val(futureEdu);
    $("#otherReq").jqxEditor({tools: 'bold italic underline font size'});
    var otherReq = $('<textarea/>').html('${leaderReview?if_exists.otherReq?if_exists}').text();
    $("#otherReq").val(otherReq);
</script>