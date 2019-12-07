<#if context.testJson?has_content>
    ${StringUtil.wrapString(context.testJson?if_exists)};
</#if>