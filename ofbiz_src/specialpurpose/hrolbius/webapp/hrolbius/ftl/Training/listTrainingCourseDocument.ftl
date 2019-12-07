<ul class="unstyled" style="margin-left: 15px">
	<#list listTrainingCourseDocument as trainingCourseDocument>
		<#assign dataResource = delegator.findOne("DataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("dataResourceId", trainingCourseDocument.dataResourceId), false)>
		<li><i class="icon-ok green"></i>
			<a href="${dataResource.objectInfo}">${trainingCourseDocument.contentName?if_exists}</a>
		</li>
	</#list>
</ul>