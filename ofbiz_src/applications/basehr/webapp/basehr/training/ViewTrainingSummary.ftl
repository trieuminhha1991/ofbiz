<#include "script/ViewTrainingSummaryScript.ftl"/>
<#assign statusId = trainingCourse.statusId/>
<#assign hasApprPerm = security.hasEntityPermission("HR_TRAINING", "_APPROVE", session)/>
<#if (statusId == "TRAINING_PLANNED_ACC" && hasApprPerm)>
	<#include "CreateTrainingCourseSummary.ftl"/>
<#elseif statusId == "TRAINING_SUMMARY" || statusId == "TRAINING_COMPLETED">
	<#include "ViewTrainingSummarized.ftl"/>
<#else>
	<div class="alert alert-info">
		<span>${uiLabelMap.YouDoNotHavePermissitonToPerformThisAction}</span>
	</div>
</#if>