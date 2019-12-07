
<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>
<div id="main-content">
	<div class="row-fluid">
		<div class="span12">						
			<div class="login-container">
				<div class="space-6"></div>
				<div class="row-fluid">
					<div class="position-relative">
						<div id="login-box" class="visible widget-box no-border">
							<div class="widget-body">
		 						<div class="widget-main">
									<h4 class="header lighter bigger"><i class="icon-coffee green"></i> ${uiLabelMap.CommonRegistered}</h4>
									<div class="space-6"></div>
									<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
										<fieldset>
											<label>
												<span class="block input-icon input-icon-right">
													<input type="text" class="span12" placeholder="${uiLabelMap.CommonUsername}" name="USERNAME" value="${username}"/>
													<i class="icon-user"></i>
												</span>
											</label>
											<label>
												<span class="block input-icon input-icon-right">
													<input type="password" class="span12" placeholder="${uiLabelMap.CommonPassword}" name="PASSWORD"/>
													<i class="icon-lock"></i>
												</span>
											</label>
											 <#if ("Y" == useMultitenant) >
              									<#if !requestAttributes.tenantId?exists>
              										<label>
														<span class="block input-icon input-icon-right">
															<input type="text" class="span12" placeholder="${uiLabelMap.CommonTenantId}" name="tenantId"value="${parameters.tenantId?if_exists}"/>
															<i class="icon-user"></i>
														</span>
													</label>
              									<#else>
                  									<input type="hidden" name="tenantId" value="${requestAttributes.tenantId?if_exists}"/>
             									</#if>
          									 </#if>
          									 <div class="space"></div>
											 <div class="row-fluid">
												<label class="span8"></label>
												<button class="span4 btn btn-small btn-primary"><i class="icon-key"></i> Login</button>
											 </div>
											 <input type="hidden" name="JavaScriptEnabled" value="N"/>
										</fieldset>
									</form>
								</div>	
		 						<div class="toolbar clearfix" style="background: #5090c1; border-top: 2px solid #597597;">
									<div style="width: 51%;display: inline-block; float:left; padding: 9px 0 11px; text-align: left;">
										<a href="<@ofbizUrl>forgotPassword</@ofbizUrl>" class="forgot-password-link" style="margin-left: 11px;color: #FE9;"><i class="icon-arrow-left"></i>&nbsp${uiLabelMap.CommonForgotYourPassword}?</a>
									</div>
		 						</div>
							</div><!--/widget-body-->
						</div><!--/login-box-->
					</div><!--/position-relative-->
				</div>
			</div>
		</div><!--/span-->
	</div><!--/row-->
</div>

<script language="JavaScript" type="text/javascript">
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>
