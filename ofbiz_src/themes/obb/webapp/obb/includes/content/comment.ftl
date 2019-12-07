<script type="text/javascript" src="/dpc/js/comment/comment.js"></script>
<script src="/crmresources/js/DataAccess.js"></script>

<#if !pid?exists>
	<#if parameters.pid?exists>
		<#assign pid = parameters.pid/>
		<#assign isProduct = "false"/>
		<#else>
		<#if parameters.product_id?exists>
		<#assign pid = parameters.product_id/>
		<#assign isProduct = "true"/>
		</#if>
	</#if>
</#if>

<#assign resultValue = dispatcher.runSync("getCommentsByTopic", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", pid, "isProduct", isProduct))/>
<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValue)>
	<#assign comments = resultValue.get("comments")/>
</#if>

<script>
	var pid = "${pid?if_exists}";
	var pidOriginal = "${pid?if_exists}";
	var isProduct = "${isProduct?if_exists}";
</script>

<h3 class="ret">Có 24 bình luận về Thuốc giảm cân Best Slim Plus</h3>
<div class="comment" id="focusF">
	<div class="send-cmt">
		<form method="post">
			<div class="message-error"></div>
			<div class="box-cmt">
				<textarea rows="2" id="description" placeholder="Mời bạn thảo luận, vui lòng gõ tiếng Việt có dấu.." cols="20" class="txtCmt font-arial"></textarea>
				<div class="block-form-user form-group">
					<div class="col-lg-4">
						<input type="text" id="author" placeholder="Nhập tên" class="font-arial form-control">
					</div>
					<div class="col-lg-4">
						<input type="text" id="email" placeholder="Nhập mail" class="font-arial form-control">
					</div>
					<div class="col-lg-2">
						<input type="button" value="Gửi bình luận" class="btn btn-ask font-arial" name="add-comment" id="btnAddComment">
					</div>
				</div>
			</div>
		</form>
	</div>

	<div class="lst-cmt font-arial">
		<#if comments?exists>
			<#list comments as comment>

				<div class="ask">
					<#if comment.author?exists>
						<#assign shortcut = comment.author?substring(0, 1)/>
						<#else>
						<#assign shortcut = "X"/>
					</#if>
					<div class="icon-shortcut">${shortcut}</div>
					<strong>${(comment.author)?if_exists}</strong>
					<div class="content">
						<p>
							${StringUtil.wrapString((comment.longDescription)?if_exists)}
						</p>
						<a title="Trả lời" href="javascript:Comments.reply('${(comment.contentId)?if_exists}');" class="alif">Trả lời</a>
						<b class="dot">●</b><span class="time">${(comment.createdStamp)?if_exists}</span>
					</div>
				</div>

				<#assign replys = comment.replys/>
				<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(replys)>
					<#list replys as reply>
						<div class="replay">
							<#if reply.author?exists>
								<#assign shortcut = reply.author?substring(0, 1)/>
								<#else>
								<#assign shortcut = "X"/>
							</#if>
							<div class="icon-shortcut">${shortcut}</div>
							<strong>${(reply.author)?if_exists}</strong><#if (reply.partyRole)?exists><span class="mob">${(reply.partyRole)?if_exists}</span></#if>
							<div class="content">
								<div>
									${StringUtil.wrapString((reply.longDescription)?if_exists)}
								</div>
								<a title="Trả lời" href="javascript:Comments.reply('${(comment.contentId)?if_exists}');" class="alif">Trả lời</a>
								<span class="time">${(reply.createdStamp)?if_exists}</span>
							</div>
						</div>
					</#list>
				</#if>
			</#list>
		</#if>

	<#--	<div class="pagcomment">
			<a title="trang 4" onclick="listcomment(2,1,4);return false;">«</a>
			<a title="trang 1" onclick="listcomment(2,1,1);return false;">1</a>
			<span>...</span>
			<a title="trang 4" onclick="listcomment(2,1,4);return false;">4</a>
			<span class="active">5</span>
			<a title="trang 6" onclick="listcomment(2,1,6);return false;">6</a>
			<span>...</span>
			<a title="trang 378" onclick="listcomment(2,1,378);return false;">378</a>
			<a title="trang 6" onclick="listcomment(2,1,6);return false;">»</a>
		</div> -->
	</div>
	<div class="send-cmt">
		<form method="post">
			<div class="box-cmt">
				<textarea rows="2" placeholder="Mời bạn thảo luận, vui lòng gõ tiếng Việt có dấu.."  cols="20" class="txtCmt font-arial" id="gotoForm"></textarea>
			</div>
		</form>
	</div>
</div>