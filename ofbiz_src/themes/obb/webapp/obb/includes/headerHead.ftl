<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml" class="no-touch off-canvas">
<head>
	<script>
		var baseUrl = "<@ofbizUrl></@ofbizUrl>";
		<#if userLogin?exists>
			var partyId = "${userLogin.partyId}";
		<#else>
			var partyId = "";
		</#if>
	</script>

  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="google-site-verification" content="NL1SKf7CCrL8HtWJVPCVaba7hvhrUHfqiWO3pRrhRTQ"/>

  <meta property="og:title" content="${(StringUtil.wrapString(mainTitle.description))?if_exists}"/>
  <meta property="og:type" content="${(StringUtil.wrapString(fbType.description))?if_exists}" />
  <meta property="business:contact_data:street_address" content="${(StringUtil.wrapString(fbAddress.description))?if_exists}" />
  <meta property="business:contact_data:locality" content="${(StringUtil.wrapString(fbLocality.description))?if_exists}" />
  <meta property="business:contact_data:country_name" content="${(StringUtil.wrapString(fbCountry.description))?if_exists}" />
  <#if thumb?exists>
	<#list thumb as slide>
  <meta property="og:image" content="${StringUtil.wrapString(slide)}"/>
	</#list>
  </#if>
  <title>${(mainTitle.description)?if_exists}<#if title?has_content>: ${title}</#if></title>
  <meta name="description" content="${(mainDescription.description)?if_exists}" />
  <meta name="keywords" content="${(mainKeywords.description)?if_exists}" />
  <#if layoutSettings.VT_SHORTCUT_ICON?has_content>
    <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
  <#elseif layoutSettings.shortcutIcon?has_content>
    <#assign shortcutIcon = layoutSettings.shortcutIcon/>
  </#if>
  <#if shortcutIcon?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
  </#if>
  <#if layoutSettings.styleSheets?has_content>
    <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.VT_STYLESHEET?has_content>
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
    <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
    <#list layoutSettings.rtlStyleSheets as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
    <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#-- Append CSS for catalog -->
  <#if catalogStyleSheet?exists>
    <link rel="stylesheet" href="${StringUtil.wrapString(catalogStyleSheet)}" type="text/css"/>
  </#if>
  <#-- Append CSS for tracking codes -->
  <#if sessionAttributes.overrideCss?exists>
    <link rel="stylesheet" href="${StringUtil.wrapString(sessionAttributes.overrideCss)}" type="text/css"/>
  </#if>
  <#if layoutSettings.javaScripts?has_content>
    <#--layoutSettings.javaScripts is a list of java scripts. -->
    <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    <#list layoutSettings.javaScripts as javaScript>
      <#if javaScriptsSet.contains(javaScript)>
        <#assign nothing = javaScriptsSet.remove(javaScript)/>
        <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
      </#if>
    </#list>
  </#if>
  <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
      <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
    </#list>
  </#if>
  ${layoutSettings.extraHead?if_exists}
  <#if layoutSettings.VT_EXTRA_HEAD?has_content>
    <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
      ${extraHead}
    </#list>
  </#if>

  <#-- Meta tags if defined by the page action -->
  <meta name="generator" content="Olbius OFBiz - Obb"/>
  <#if metaDescription?exists>
    <meta name="description" content="${metaDescription}"/>
  </#if>
  <#if metaKeywords?exists>
    <meta name="keywords" content="${metaKeywords}"/>
  </#if>
  <#if webAnalyticsConfigs?has_content>
    <script language="JavaScript" type="text/javascript">
    <#list webAnalyticsConfigs as webAnalyticsConfig>
      <#if  webAnalyticsConfig.webAnalyticsTypeId != "BACKEND_ANALYTICS">
        ${StringUtil.wrapString(webAnalyticsConfig.webAnalyticsCode?if_exists)}
      </#if>
    </#list>
    </script>
  </#if>

	<meta name="HandheldFriendly" content="true">
	<meta name="apple-touch-fullscreen" content="YES">
	<meta name="description" content="Olbius Obb"> <#-- FIXME update from content -->
	<meta name="keywords" content="Olbius, obb"> <#-- FIXME update from content -->
	<meta name="robots" content="INDEX,FOLLOW">
	<link rel="icon" href="/obbresources/images/favicon.ico?v=5" type="image/x-icon">  <#-- FIXME update icon -->
	<link rel="shortcut icon" href="/obbresources/images/favicon.ico?v=5" type="image/x-icon"> <#-- FIXME update icon -->
	<link rel="stylesheet" type="text/css" href="/obbresources/js/calendar/calendar-win2k-1.css">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/addons.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/colorbox.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/styles.1.1.2.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/css/slick.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/fonts/stylesheet.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/base/default/css/widgets.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/off-canvas.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/base/default/joomlart/jmproducts/css/jmproduct.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmproductsslider/css/style.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmslideshow/css/jm.slideshow.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmmegamenu/css/jmmegamenu.1.0.0.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmquickview/css/jmquickview.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/css/custom.1.0.2.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/css/style-obb.1.0.5.css" media="all">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-hd.css" media="only screen and (min-width:1891px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-wide-extra.css" media="only screen and (min-width:1586px) and (max-width: 1890px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-wide.1.0.0.css" media="only screen and (min-width:1200px) and (max-width: 1585px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-normal.css" media="only screen and (min-width:980px) and (max-width: 1199px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-tablet.1.0.4.css" media="only screen and (min-width:768px) and (max-width: 979px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-mobile1.0.8.css" media="only screen and (max-width:767px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/layout-mobile-portrait1.0.1.css" media="only screen and (max-width:480px)">
	<link rel="stylesheet" type="text/css" href="/obbresources/skin/frontend/default/jm_megamall/css/print.css" media="print">

	<script type="text/javascript" src="/obbresources/js/lib/ccard.js"></script>
	<script type="text/javascript" src="/obbresources/js/scriptaculous/builder.js"></script>
	<script type="text/javascript" src="/obbresources/js/mage/cookies.js"></script>
	<script type="text/javascript" src="/obbresources/js/calendar/calendar.js"></script>
	<script type="text/javascript" src="/obbresources/js/calendar/calendar-setup.js"></script>
	<script type="text/javascript">
		//<![CDATA[
			Mage.Cookies.path     = '/';
			Mage.Cookies.domain   = 'nhanhqua.vn';
		//]]>
	</script>
	<script type="text/javascript">
		//<![CDATA[
			optionalZipCountries = ["HK","IE","MO","PA"];
		//]]>
	</script>
	<script type="text/javascript">
//		var webapp = "baseecommerce/";
//		var baseurl = location.protocol + "//" + location.host + "/" + webapp;
		var baseurl = location.href.split("control")[0];
	</script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/default/wavethemes/jmbasetheme/js/jquery.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/default/wavethemes/jmbasetheme/js/jmbasetheme.js"></script>
	<script type="text/javascript">
	    var offcanvaseffect = '';
	    ja = jQuery.noConflict();
	    ja(document).ready(function($) {
	          var test = new ja.fn.jmbasetheme({
	              ismobile:1,
	              istable:0,
	              productgridimagewidth:298,
	              productgridimageheight:298,
	              productlistimageheight:298,
	              productlistimagewidth:298,
	              productgridnumbercolumn:4,
	              productgridimagewidthtabletportrait:298,
	              productgridimageheighttabletportrait:298,
	              productlistimagewidthtabletportrait:298,
	              productlistimageheighttabletportrait:298,
	              productgridnumbercolumntabletportrait:4,
	              productlistimagewidthmobileportrait:298,
	              productlistimageheightmobileportrait:298,
	              productgridimageheightmobileportrait:298,
	              productgridimagewidthmobileportrait:298,
	              productgridnumbercolumnmobileportrait:1,

	          });
	          $('.no-space').keyup(function(){
					var val = $(this).val();
					val = val.replace(/[^\w-]/gi, '');
					var res = '';
					for(var x = 0; x < val.length; x++){
						// res += val[x].toUpperCase();
						res += val[x];
					}
					$(this).val(res);
				});
				$(".product-image").on('click', function(e) { 
				   if( e.which == 2 ) {
				      e.preventDefault();
				   }
				});
		});
	</script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/jquery/plugins/colorbox/jquery.colorbox.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmquickview/js/jmquickview.js"></script>
    <script type="text/javascript">
	    //ie10 class
	    var doc = document.documentElement;
	    if(navigator.userAgent.indexOf("MSIE 10.0") > 0){
	      doc.className+=' ie10';
	    }
	   var Baseurl = location.protocol + "//" + location.host + "/";
	   var uiLabel = {
			   BEQuickView: "${StringUtil.wrapString(uiLabelMap.BEQuickView)}",
			   BEProductNo: "${StringUtil.wrapString(uiLabelMap.BEProductNo)}",
			   BEIn: "${StringUtil.wrapString(uiLabelMap.BEIn)}",
			   BEItems: "${StringUtil.wrapString(uiLabelMap.BEItems)}".toLowerCase(),
	   }
	</script>
	<script type="text/javascript">
		function limittext(strInput, maxlen){
			var arr = strInput.split(" ");
			var len = 6;
			if(maxlen){
				len = maxlen;
			}
			if(arr.length > len){
				var res = "";
				for(var i = 0; i < len; i++){
					res += arr[i] + " ";
				}
				if (res.length < 30) {
					if (arr[len]) {
						res += arr[len];
					}
				}
				return res + "...";
			}
			return strInput;
		}
	</script>
	<script type="text/javascript" src="/obbresources/js/lib/slick.min.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/jquery/plugins/iscroll/iscroll.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/jquery/jquery.noconflict.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/touchmenu.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/jquery.equalheights.js"></script>
	<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/off-canvas.js"></script>
	<link type="text/css" rel="stylesheet" href="/obbresources/skin/frontend/default/default/wavethemes/jmbasetheme/css/settings.css">
	<link type="text/css" rel="stylesheet" href="/obbresources/skin/frontend/default/jm_megamall/wavethemes/jmbasetheme/profiles/default/default.css">
	<link href='//fonts.googleapis.com/css?family=Open+Sans:400italic,700italic,400,300,700,600&subset=latin,vietnamese' rel='stylesheet' type='text/css'>
	
	<script type="text/javascript" src="/obbresources/asset/js/jquery.lazyload.min.js"></script>
	<script type="text/javascript">
		jQuery(function($) {
		    $("img").lazyload();
		});
	</script>

	<!-- Google Tag Manager -->
	<noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-PKXDW4"
	height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
	<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
	new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
	j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
	'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
	})(window,document,'script','dataLayer','GTM-PKXDW4');</script>
	<!-- End Google Tag Manager -->
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

	  ga('create', 'UA-74878582-1', 'auto');
	  ga('send', 'pageview');

	</script>
</head>
<a class="btn-btt pointer" title="Back to Top" id="button-btt" style="display: inline;display: none;"><i class="fa fa-caret-up"></i>Top</a>

<script type="text/javascript">
$(window).scroll(function() {
	if ( $(window).scrollTop() > 300 ) {
		$('#button-btt').fadeIn('slow');
	} else {
		$('#button-btt').fadeOut('slow');
	}
});
$('#button-btt').click(function() {
	$('html, body').animate({
		scrollTop: 0
	}, 700);
	return false;
});

<#assign localeStr = "vi" />
	<#if locale != "vi">
	<#assign localeStr = "en" />
</#if>
var localeStr = "${localeStr}";
</script>