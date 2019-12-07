<style>
	.facebookSetting label {
		margin-top: 5px;
	}
</style>
<#assign pageDone = "FacebookSetting"/>
<div class='padding-bottom40'>
	<div class="row-fluid">
		<div class="span12 boder-all-profile">
			<span class="text-header">${uiLabelMap.BEFacebookAPI}</span>
			<form name="facebookSetting" method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<div class="row-fluid margin-top10 facebookSetting">
					<div class="span6">
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right asterisk">${uiLabelMap.FB_APP_ID}</label></div>
							<div class="span7">
								<input type="text" name="description_o_0" value="${(settings.FB_APP_ID)?if_exists}" required/>
								<input type="hidden" name="contentId_o_0" value="FB_APP_ID"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right asterisk">${uiLabelMap.FB_THEME}</label></div>
							<div class="span7">
								<input type="text" name="description_o_1" value="${(settings.FB_THEME)?if_exists}" required/>
								<input type="hidden" name="contentId_o_1" value="FB_THEME"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_PAGE_WIDTH}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_2" value="${(settings.FB_PAGE_WIDTH)?if_exists}"/>
								<input type="hidden" name="contentId_o_2" value="FB_PAGE_WIDTH"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_PAGE_FRIEND_SHOW}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_3" value="${(settings.FB_PAGE_FRIEND_SHOW)?if_exists}"/>
								<input type="hidden" name="contentId_o_3" value="FB_PAGE_FRIEND_SHOW"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_PAGE_POST_SHOW}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_4" value="${(settings.FB_PAGE_POST_SHOW)?if_exists}"/>
								<input type="hidden" name="contentId_o_4" value="FB_PAGE_POST_SHOW"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right asterisk">${uiLabelMap.FB_PAGE_ID}</label></div>
							<div class="span7">
								<input type="text" name="description_o_5" value="${(settings.FB_PAGE_ID)?if_exists}" required/>
								<input type="hidden" name="contentId_o_5" value="FB_PAGE_ID"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right asterisk">${uiLabelMap.FB_PAGE_URL}</label></div>
							<div class="span7">
								<input type="text" name="description_o_6" value="${(settings.FB_PAGE_URL)?if_exists}" required/>
								<input type="hidden" name="contentId_o_6" value="FB_PAGE_URL"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_PAGE_HEIGHT}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_7" value="${(settings.FB_PAGE_HEIGHT)?if_exists}"/>
								<input type="hidden" name="contentId_o_7" value="FB_PAGE_HEIGHT"/>
							</div>
						</div>
					</div>
					<div class="span5">
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_COMMENT_NUMBER}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_8" value="${(settings.FB_COMMENT_NUMBER)?if_exists}"/>
								<input type="hidden" name="contentId_o_8" value="FB_COMMENT_NUMBER"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_SHARING}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_9" value="${(settings.FB_SHARING)?if_exists}"/>
								<input type="hidden" name="contentId_o_9" value="FB_SHARING"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_TYPE}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_10" value="${(settings.FB_TYPE)?if_exists}"/>
								<input type="hidden" name="contentId_o_10" value="FB_TYPE"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right asterisk">${uiLabelMap.FB_COMMENT_WIDTH}</label></div>
							<div class="span7">
								<input type="text" name="description_o_11" value="${(settings.FB_COMMENT_WIDTH)?if_exists}" required/>
								<input type="hidden" name="contentId_o_11" value="FB_COMMENT_WIDTH"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_SHARING_FRIEND_LIST}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_12" value="${(settings.FB_SHARING_FRIEND_LIST)?if_exists}"/>
								<input type="hidden" name="contentId_o_12" value="FB_SHARING_FRIEND_LIST"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_STREET_ADDRESS}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_13" value="${(settings.FB_STREET_ADDRESS)?if_exists}"/>
								<input type="hidden" name="contentId_o_13" value="FB_SHARING_FRIEND_LIST"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_LOCALITY}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_14" value="${(settings.FB_LOCALITY)?if_exists}"/>
								<input type="hidden" name="contentId_o_14" value="FB_SHARING_FRIEND_LIST"/>
							</div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.FB_COUNTRY_NAME}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7">
								<input type="text" name="description_o_15" value="${(settings.FB_COUNTRY_NAME)?if_exists}"/>
								<input type="hidden" name="contentId_o_15" value="FB_SHARING_FRIEND_LIST"/>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>

	<div class="row-fluid">
		<div class="span12 boder-all-profile">
			<span class="text-header">Zopim Embed</span>
			<form method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, "ZOPIM_OBB") />
				<input type="hidden" name="contentId_o_0" value="ZOPIM_OBB"/>
				<textarea name="description_o_0" rows="8" class="textarea-standard">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
			</form>
		</div>
	</div>
	<div class='row-fluid'>
		<div class="span6 boder-all-profile">
			<span class="text-header">${uiLabelMap.BEHomeDescription}</span>
			<form name="description" method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, "MAIN_DESCRIPTION") />
				<input type="hidden" name="contentId_o_0" value="MAIN_DESCRIPTION"/>
				<textarea name="description_o_0" class="textarea-standard" rows="8">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
			</form>
		</div>
		<div class="span6 boder-all-profile">
			<span class="text-header">${uiLabelMap.BEHomeTitle}</span>
			<form method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, "MAIN_TITLE") />
				<input type="hidden" name="contentId_o_0" value="MAIN_TITLE"/>
				<textarea name="description_o_0" class="textarea-standard" rows="2">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
			</form>
		</div>
		<div class="span6 boder-all-profile">
			<span class="text-header">${uiLabelMap.BEHomeKeywords}</span>
			<form method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, "MAIN_KEYWORDS") />
				<input type="hidden" name="contentId_o_0" value="MAIN_KEYWORDS"/>
				<textarea name="description_o_0" class="textarea-standard" rows="2">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
			</form>
		</div>
	</div>
	
	<div class='row-fluid'>
		<div class="span6 boder-all-profile">
			<span class="text-header">${uiLabelMap.BEAboutUs}</span>
			<form name="description" method="post" action="<@ofbizUrl>updateContents</@ofbizUrl>"/>
				<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, "MAIN_ABOUT_US") />
				<input type="hidden" name="contentId_o_0" value="MAIN_ABOUT_US"/>
				<textarea name="description_o_0" class="textarea-standard" rows="8">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
			</form>
		</div>
	</div>
	
	<button id='save' class="btn btn-primary form-action-button fixed-button-action"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
</div>
<script>
	$(document).ready(function(){
		var send = 0;
		var submit = function(){
			var fo = $('form');
			send = fo.length;
			for(var i = 0; i < fo.length; i++){
				(function(form){
					var data = $(form).serialize();
					var url = $(form).attr("action");
					Request.post(url, data, function(res){
						send--;
						if(send == 0){
							Loading.hide('loadingMacro');
							window.location.reload();
						}
					}, Loading.show('loadingMacro'), null);
				})(fo[i]);
			}
		};
		$('#save').click(function(){
			submit();
		});
	});
</script>