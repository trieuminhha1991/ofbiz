<style>
	.widget-body {
		padding-top: 0px;
	}
	.login-layout .widget-box .widget-main {
		padding: 16px 36px 10px;
	}
	.login-layout label {
		margin-bottom: 10px;
	}
	.login-layout select {
		margin-bottom: 5px;
	}
</style>

<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign url = request.getRequestURL().toString().replace(request.getRequestURI().toString(),"")>
<#assign start = url.indexOf("//", 0)+2>
<#assign end = url.indexOf(".")>

<#if start &gt; 0 && end &gt; 0>
	<#assign tenantId = url.substring(start, end)>
</#if>


<#if requestAttributes.uiLabelMap?exists>
  <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<#assign previousParams = sessionAttributes._PREVIOUS_PARAMS_?if_exists>
<#if previousParams?has_content>
  <#assign previousParams = "?" + previousParams>
</#if>

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
							<div class="widget-body" style="margin-top: 10px !important;">
								<div class="widget-main">
									<h4 class="header lighter bigger"><i class="icon-coffee green"></i>${uiLabelMap.CommonRegistered}</h4>
									<div class="space-6"></div>
										<form method="post" action="<@ofbizUrl>login${previousParams?if_exists}</@ofbizUrl>" name="loginform">
									       <fieldset>
										       <label>
													<span class="block input-icon input-icon-right">
														<input type="text" class="span12" placeholder="${uiLabelMap.CommonUsername}" name="USERNAME" value="${username}" required/>
														<i class="icon-user"></i>
													</span>
												</label>
												<label>
													<span class="block input-icon input-icon-right">
														<input type="password" class="span12" placeholder="${uiLabelMap.CommonPassword}" name="PASSWORD" required/>
														<i class="icon-lock"></i>
													</span>
												</label>
												<label>
													<select id="productStore" title="${uiLabelMap.BPOSProductStore}" style="width:100% !important;" required>
														<option value="">${uiLabelMap.BSChooseProductStore}</option>
														<#list productStores as productStore>
															<option value="${productStore.inventoryFacilityId?if_exists}">${productStore.storeName?if_exists}</option>
														</#list>
													</select>
												</label>
												<label>
													<select name="posTerminalId" id="posTerminalId" title="${uiLabelMap.BPOSTerminalPOS}" style="width:100% !important;" required>
														<option value="">${uiLabelMap.BPOSChooseTerminalPOS}</option>
													</select>
												</label>
											 <#if ("Y" == useMultitenant) >
											 	<#if !requestAttributes.tenantId?exists>
											 		<label>
														<span class="block input-icon input-icon-right">
															<input type="text" class="span12" placeholder="${uiLabelMap.CommonTenantId}" name="tenantId" value="${tenantId?if_exists}"/>
															<i class="icon-user"></i>
														</span>
													</label>
												<#else>
													<input type="hidden" name="tenantId" value="${requestAttributes.tenantId?if_exists}"/>
												</#if>
											</#if>
												<div class="row-fluid">
													<label class="span7"></label>
													<button title="${uiLabelMap.CommonLogin}" class="span5 btn btn-small btn-primary"><i class="icon-key"></i> ${uiLabelMap.CommonLogin}</button>
												</div>
											</fieldset>
											<input type="hidden" name="JavaScriptEnabled" value="N"/>
									</form>
								</div>	
								<div class="toolbar clearfix" style="background: #5090c1; border-top: 2px solid #597597;">
									<div style="width: 51%;display: inline-block; float:left; padding: 9px 0 11px; text-align: left;">
										<a href="<@ofbizUrl>forgotPassword</@ofbizUrl>" title="${uiLabelMap.CommonForgotYourPassword}" class="forgot-password-link" style="margin-left: 11px;color: #FE9;"><i class="icon-arrow-left"></i>&nbsp${uiLabelMap.CommonForgotYourPassword}?</a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script language="JavaScript" type="text/javascript">
	document.loginform.JavaScriptEnabled.value = "Y";
	<#if focusName>
		document.loginform.USERNAME.focus();
	<#else>
		document.loginform.PASSWORD.focus();
	</#if>
	$(document).ready(function() {
		var renderTerminalInStore = function(facilityId) {
			var htmlInner = "<option value=''>${uiLabelMap.BPOSChooseTerminalPOS}</option>";
			if (facilityId) {
				htmlInner = "";
				var data = ${StringUtil.wrapString(mapPosTerminal!"{}")}[facilityId];
				for ( var x in data) {
					htmlInner += "<option value=" + data[x].posTerminalId + ">" + data[x].terminalName + "</option>"
				}
			}
			$("#posTerminalId").html(htmlInner);
		}
		if (getCookie("POSProductStoreId")) {
			$("#productStore").val(getCookie("POSProductStoreId"));
		}
		renderTerminalInStore($("#productStore").val());
		$("#productStore").change(function() {
			var value = $(this).val();
			if (value) {
				setCookie("POSProductStoreId", value);
			}
			renderTerminalInStore(value);
		});
	});
</script>