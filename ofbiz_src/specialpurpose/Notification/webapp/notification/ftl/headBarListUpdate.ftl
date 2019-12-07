<#-- Compare before replacing-->
<#assign count = 0 >
<#list context.listNofification as elm>
	<#assign count = count + 1>
</#list>
<#if context.listNG?has_content>
	<#list context.listNG as elm>
		<#assign tmpList = elm.get(context.listKey.get(elm_index)) />
		<#list tmpList as tl>
			<#assign count = count + 1>
		</#list>
	</#list>
</#if>
<a href="#" class="dropdown-toggle" data-toggle="dropdown">
	<i class="icon-bell-alt icon-animated-bell icon-only"></i>
	<span class="badge badge-important">${count}</span>
</a>
<script type="text/javascript">
	function replaceAll(find, replace, str) {
	  return str.replace(new RegExp(find, 'g'), replace);
	}
	function closeNTF(strFormName,ntfId){
		$.ajax({
  			url: 'closeNTF',
  			type: 'POST',
  			data: 'ntfId=' + ntfId,
  			success: function(data) {
  				$('form[name="' + strFormName + '"]').submit();
  			},
  			error: function(e) {
				alert(e.message);
  			}
		});
	}
</script>
<ul style="overflow-y: scroll; height: 422px; width: 300px;" class="pull-right dropdown-navbar navbar-pink dropdown-menu dropdown-caret dropdown-closer overflow-noti" style="width:350px;">
	<li class="nav-header">
		<i class="icon-warning-sign"></i> ${count} Notification<#if count gt 1>s</#if>
	</li>
	<#if context.listNG?has_content>
		<#list context.listNG as lng>
			<#if lng.get(context.listKey.get(lng_index)).size() gt 1>
				<#assign mindate = lng.get(context.listKey.get(lng_index)).get(0).dateTime/>
				<#assign maxdate = lng.get(context.listKey.get(lng_index)).get(0).dateTime/>
				<#list lng.get(context.listKey.get(lng_index)) as ldtime>
					<#if ldtime.dateTime gt maxdate>
						<#assign maxdate = ldtime.dateTime />
					</#if>
					<#if ldtime.dateTime lt mindate>
						<#assign mindate = ldtime.dateTime />
					</#if>
				</#list>
				<#assign elm = lng.get(context.listKey.get(lng_index)).get(0)/>
				<li>
					<form method="POST" style="display:none;" action="<#if elm.actionGroup?has_content>${elm.actionGroup?if_exists}<#else>NotificationList</#if>" name="h_${lng_index}_g">
						<input type="hidden" name="ntfGroupId" value="${elm.ntfGroupId}">
					</form>		
					<a href="javascript:void(0)" onclick="h_${lng_index}_g.submit();">
						<!-- TODO update image of sender or notification group-->
						<img src="<#if lng.imagePath?has_content>${lng.imagePath}<#else>/aceadmin/assets/avatars/reqrec.png</#if>" class="msg-photo" alt="Avatar">
						<span class="msg-body" style="width:250px;">
							<span class="msg-title" style="width:250px;">
								<span class="badge badge-important">[${lng.get(context.listKey.get(lng_index)).size()}]</span>&nbsp;${elm.description?if_exists}
							</span>
							<span class="msg-time" style="width:250px;">
								<i class="icon-time"></i>
								<span>${mindate?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}&nbsp;>>&nbsp;${maxdate?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}</span>
							</span>
						</span>
					</a>
				</li>
			<#else>
				<#assign elm = lng.get(context.listKey.get(lng_index)).get(0)/>
				<li>	
					<form method="POST" action="${elm.action?if_exists}" name="h_${lng_index}_g">
						<#assign tmps = elm.targetLink?string/>
						<#assign tmps = tmps?replace("&#61;", "=")/>
						<#assign tmps = tmps?replace("&#59;", ";")/>
						<#list tmps?split(";") as listInput>
							<#assign tmpi=listInput?split("=")/>
							<input type="hidden" name="${tmpi[0]}" value="${tmpi[1]}">
						</#list>
					</form>
					<#if elm.ntfType?has_content && elm.ntfType?upper_case == "ONE">
						<a href="javascript:void(0)" onclick="closeNTF('h_${lng_index}_g', ${elm.ntfId})">
					<#else>
						<a href="javascript:void(0)" onclick="h_${lng_index}_g.submit();">
					</#if>
						<!-- TODO update image of sender or notification group-->
						<img src="/aceadmin/assets/avatars/avatar.png" class="msg-photo" alt="Avatar">
						<span class="msg-body" style="width:250px;">
							<span class="msg-title" style="width:250px;">
								${(elm.header)?if_exists}
							</span>
							<span class="msg-time">
								<i class="icon-time"></i>
								<span>${elm.dateTime?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}</span>
							</span>
						</span>
					</a>
				</li>
			</#if>
		</#list>
	</#if>
	<#list context.listNofification as elm>
		<li>	
			<form method="POST" action="${elm.action?if_exists}" name="h_${elm_index}_g">
				<#assign tmps = elm.targetLink?string/>
				<#assign tmps = tmps?replace("&#61;", "=")/>
				<#assign tmps = tmps?replace("&#59;", ";")/>
				<#list tmps?split(";") as listInput>
					<#assign tmpi=listInput?split("=")/>
					<input type="hidden" name="${tmpi[0]}" value="${tmpi[1]}">
				</#list>
			</form>
			<#if elm.ntfType?has_content && elm.ntfType?upper_case == "ONE">
				<a href="javascript:void(0)" onclick="closeNTF('h_${elm_index}', ${elm.ntfId})">
			<#else>
				<a href="javascript:void(0)" onclick="h_${elm_index}.submit();">
			</#if>
				<!-- TODO update image of sender or notification group-->
				<img src="/aceadmin/assets/avatars/avatar.png" class="msg-photo" alt="Avatar">
				<span class="msg-body" style="width:250px;">
					<span class="msg-title" style="width:250px;">
						${(elm.header)?if_exists}
					</span>
					<span class="msg-time">
						<i class="icon-time"></i>
						<span>${elm.dateTime?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}</span>
					</span>
				</span>
			</a>
		</li>
	</#list>
	<li>
		<a href="NotificationList">
			View all notifications
			<i class="icon-arrow-right"></i>
		</a>
	</li>
</ul>
