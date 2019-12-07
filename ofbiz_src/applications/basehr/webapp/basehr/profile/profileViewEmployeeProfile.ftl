<#include "script/profileViewEmployeeProfileScript.ftl"/>
<link rel="stylesheet" type="text/css" href="/hrresources/css/profile/profile.css">
<script type="text/javascript" src="/hrresources/js/profile/profile.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<div class="row-fluid">
	<ul class="nav nav-tabs padding-18" id="recent-tab">
		<li class="active">
			<a data-toggle="tab" href="#personal-info" aria-expanded="true">
				<i class="black fa fa-user bigger-120"></i>
				${uiLabelMap.generalInfo}
			</a>
		</li>
		<li class="">
			<a data-toggle="tab" href="#familyMemberTab" aria-expanded="false">
				<i class="black ace-icon fa fa-home bigger-120"></i>
				${uiLabelMap.familyMemberInfo}
			</a>
		</li>

	</ul>
	<div class="tab-content overflow-visible" style="border: none !important">
			<#include "profileViewEmployeeProfileGeneral.ftl" >
			<#include "profileViewEmployeeProfileFamily.ftl" >
	</div>
</div>
