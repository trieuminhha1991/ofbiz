<#assign count = 0 >
<#assign countLimit = 0 >
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
	<span class="badge badge-important"><#if context.ntfLimitNumber &lt; count>${count-1}+<#else>${count}</#if></span>
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
<ul style="overflow-y: scroll; max-height: 422px; width: 300px;" class="pull-right dropdown-navbar navbar-pink dropdown-menu dropdown-caret dropdown-closer">
	<li class="nav-header">
		<i class="icon-warning-sign"></i> <#if context.ntfLimitNumber &lt; count>${count-1}+<#else>${count}</#if> ${uiLabelMap.Notification}<#if locale = "en"><#if count gt 1>s</#if></#if>
	</li>
	<#if context.listNG?has_content>
		<#list context.listNG as lng>
			<#assign countLimit = countLimit + 1>
			<#if countLimit gt context.ntfLimitNumber>
				<#break>
			</#if>
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
					<script type="text/javascript">
						document.write("<form method=\"POST\" action=\"" + "<#if elm.actionGroup?has_content>${elm.actionGroup?if_exists}<#else>NotificationList</#if>" + "\" name=\"" + "h_${lng_index}_g" + "\">");
						document.write("	<input type=\"hidden\" name=\"ntfGroupId\" value=\"${elm.ntfGroupId}\">");
						document.write("</form>");		
					</script>
					<a href="javascript:void(0)" onclick="h_${lng_index}_g.submit();">
						<img src="<#if lng.imagePath?has_content>${lng.imagePath}<#else>/aceadmin/assets/avatars/reqrec.png</#if>" class="msg-photo" alt="Avatar">
						<span class="msg-body"">
							<span class="msg-title">
								<span class="badge badge-important">[${lng.get(context.listKey.get(lng_index)).size()}]</span>&nbsp;${elm.description?if_exists}
							</span>
							<span class="msg-time">
								<i class="icon-time"></i>
								<span>${mindate?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}&nbsp;>>&nbsp;${maxdate?if_exists?string["yyyy-MM-dd, HH:mm:ss"]}</span>
							</span>
						</span>
					</a>
				</li>
			<#else>
				<#assign elm = lng.get(context.listKey.get(lng_index)).get(0)/>
				<li>	
					<script type="text/javascript">
						var txtAction = "${elm.targetLink?if_exists}";
						var txtprams = replaceAll('&#61;','=',txtAction);
						txtprams = replaceAll('&#59;',';',txtprams);
						txtprams = replaceAll('&#58;',':',txtprams);
						txtprams = replaceAll('&#47;','/',txtprams);
						var arrParams = txtprams.split(";");
						document.write("<form method=\"POST\" action=\"" + "${elm.actionGroup?if_exists}" + "\" name=\"" + "h_${lng_index}_g" + "\">");
						for(i = 0; i < arrParams.length;i++){
							var kv = arrParams[i].split("=");
							document.write("<input type=\"hidden\" name=\"" + kv[0] + "\" value=\"" + kv[1] + "\">");					
						}
						document.write("<input type=\"hidden\" name=\"ntfId\" value=\"" + "${elm.ntfId}" + "\">");
						document.write("</form>");		
					</script>
					<#if elm.ntfType?has_content && elm.ntfType?upper_case == "ONE">
						<a href="javascript:void(0)" onclick="closeNTF('h_${lng_index}_g', '${elm.ntfId}')">
					<#else>
						<a href="javascript:void(0)" onclick="h_${lng_index}_g.submit();">
					</#if>
						<!-- TODO update image of sender or notification group-->
						<img src="/aceadmin/assets/avatars/avatar.png" class="msg-photo" alt="Avatar">
						<span class="msg-body">
							<span class="msg-title">
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
	<#assign countLimit2 = 0>
	<#list context.listNofification as elm>
		<#assign countLimit2 = countLimit2 + 1>
		<#if countLimit2 gt context.ntfLimitNumber>
			<#break>
		</#if>
		<li>	
			<script type="text/javascript">
				var txtAction = "${elm.targetLink?if_exists}";
				var txtprams = replaceAll('&#61;','=',txtAction);
				txtprams = replaceAll('&#59;',';',txtprams);
				txtprams = replaceAll('&#58;',':',txtprams);
				txtprams = replaceAll('&#47;','/',txtprams);
				var arrParams = txtprams.split(";");
				document.write("<form method=\"POST\" action=\"" + "${elm.action?if_exists}" + "\" name=\"" + "h_${elm_index}" + "\">");
				for(i = 0; i < arrParams.length;i++){
					var kv = arrParams[i].split("=");
					document.write("<input type=\"hidden\" name=\"" + kv[0] + "\" value=\"" + kv[1] + "\">");					
				}
				document.write("<input type=\"hidden\" name=\"ntfId\" value=\"" + "${elm.ntfId}" + "\">");
				document.write("</form>");	
			</script>
			<#if elm.ntfType?has_content && elm.ntfType?upper_case == "ONE">
				<a href="javascript:void(0)" onclick="closeNTF('h_${elm_index}', '${elm.ntfId}')">
			<#else>
				<a href="javascript:void(0)" onclick="h_${elm_index}.submit();">
			</#if>
				<!-- TODO update image of sender or notification group-->
				<img src="/aceadmin/assets/avatars/avatar.png" class="msg-photo" alt="Avatar">
				<span class="msg-body">
					<span class="msg-title">
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
			${uiLabelMap.NotificationList}
			<i class="icon-arrow-right"></i>
		</a>
	</li>
</ul>
