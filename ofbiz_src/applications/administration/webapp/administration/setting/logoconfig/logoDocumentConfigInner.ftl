<h4>${uiLabelMap.BSCurrentImage}</h4>
<#assign logoResourceValue = Static["com.olbius.basesales.util.SalesWorker"].getImageTextBase64(delegator, userLogin)!/>
<#if logoResourceValue?has_content>
	<img id="logoDocument" style="height: 2cm" src="${logoResourceValue?if_exists}"/>
<#else>
	<img id="logoDocument" style="height: 2cm" src="/poresources/logo/product_demo.png"/>
</#if>