	  <#assign strLocale = locale.toString()?substring(0,2)>
	  <div id="language"><img src="/bigshop/images/flags/${strLocale}.png" alt="${locale.getDisplayName(locale)}" />${locale.getDisplayName(locale)}
	<ul>
		<#assign availableLocales = Static["org.ofbiz.base.util.UtilMisc"].availableLocales()/>
		<#list availableLocales as availableLocale>
			<#assign langAttr = availableLocale.toString()?replace("_", "-")>
			<#assign langDir = "ltr">
			<#if "ar.iw"?contains(langAttr?substring(0, 2))>
				<#assign langDir = "rtl">
			</#if>
			<li><a href="<@ofbizUrl>setSessionLocale?newLocale=${availableLocale.toString()}</@ofbizUrl>"><img src="/bigshop/images/flags/${availableLocale.toString()}.png" alt="${availableLocale.getDisplayName(availableLocale)}" /> ${availableLocale.getDisplayName(availableLocale)}</a></li>
		</#list>
	</ul>
      </div>
