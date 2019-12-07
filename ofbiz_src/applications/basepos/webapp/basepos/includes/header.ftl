<#assign externalKeyParam = "&amp;externalLoginKey=" + requestAttributes.externalLoginKey?if_exists>

<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<#if defaultOrganizationPartyGroupName?has_content>
    <#assign orgName = defaultOrganizationPartyGroupName?if_exists>
<#else>
    <#assign orgName = "">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
<#if layoutSettings.shortcutIcon?has_content>
    <#assign shortcutIcon = layoutSettings.shortcutIcon/>
<#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
    <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
</#if>
<#if shortcutIcon?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
</#if>
<#if layoutSettings.javaScripts?has_content>
<#--layoutSettings.javaScripts is a list of java scripts. -->
<#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    <#list layoutSettings.javaScripts as javaScript>
        <#if javaScriptsSet.contains(javaScript)>
            <#assign nothing = javaScriptsSet.remove(javaScript)/>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#if>
    </#list>
</#if>
<#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
        <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
    </#list>
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
	<script src="/aceadmin/assets/js/loading.js" type="text/javascript"></script>
	<link rel="stylesheet" href="/aceadmin/assets/css/spinkit/spinkit.min.css" type="text/css" />
    <link rel="stylesheet" href="/aceadmin/assets/css/jquery-ui-1.10.3.custom.min.css" type="text/css">
    <link rel="stylesheet" href="/posresources/assets/css/chosen.css" type="text/css">
    <link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.base.css" type="text/css" />
	<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.energyblue.css" type="text/css" />
	<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.wigetolbius.css" type="text/css" />
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid2.full.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter2.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script>	
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsreorder.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.pos.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
</head>
<#--
<#if layoutSettings.headerImageLinkUrl?exists>
    <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}">
<#else>
    <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}">
</#if>
<#assign organizationLogoLinkURL = "${layoutSettings.organizationLogoLinkUrl?if_exists}">
-->

<#if person?has_content>
    <#assign userName = person.firstName?if_exists + " " + person.middleName?if_exists + " " + person.lastName?if_exists>
<#elseif partyGroup?has_content>
    <#assign userName = partyGroup.groupName?if_exists>
<#elseif userLogin?exists>
    <#assign userName = userLogin.userLoginId>
<#else>
    <#assign userName = "">
</#if>
<#include "component://widget/templates/jqxMacro.ftl"/>
<body<#if userLogin?has_content><#else> class="login-layout"</#if>>
<@loading id="loadingMacro" width="200" height="200" autofit="true" isRandom="false" fixed="true" option=2 
	top="40%" zIndex="99999" hide="false" background="rgba(0,0,0,0.5)" opacity="" />
<div class="navbar navbar-fixed-top bs-docs-nav">
<div id="nav" class="container-fluid">
    <div class="navbar-header">
        <button class="navbar-toggle btn-navbar" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
            <span>Menu</span>
        </button>
        <!-- Site name for smallar screens -->
        <a href="#" class="navbar-brand hidden-lg hidden-md hidden-sm">
            <span>Olbius POS</span>
        </a>
    </div>
<#if userLogin?has_content>
<nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
    <div id="poslogo" class="pull-left hidden-xs">
        <a href="#">
            <a class="pull-left hidden-xs" href="main"><img src="/images/olbius/poslogo.png"/></a>
        </a>
    </div>
    <!-- Product Search form -->
   <div id="jqxProductList" class="navbar-left"></div>
   <div id="jqxPartyList" class="navbar-left"></div>
   
   <ul class="nav ace-nav pull-right">
        <li class="light-blue user-profile">
        	<#assign currentTime = .now>
        	<div style="font-size: 85%; text-align: right;padding-top: 3px;padding-right: 15px; color: #777;">${currentTime?date}, ${storeName?if_exists}</div>
        	
            <a class="user-menu dropdown-toggle" href="#" data-toggle="dropdown" style="padding: 0px !important;">
							<span id="user_info">
								<small>${uiLabelMap.BPOSWelcome},</small> ${person.lastName?if_exists} ${person.firstName?if_exists}
							</span>
                <i class="icon-caret-down"></i>
            </a>
            <ul id="user_menu" class="pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-closer" style="top:90% !important">
                <li><a href="#"><i class="icon-flag"></i> ${uiLabelMap.BPOSLanguageTitle}</a> 
                    <ul class="obl_submenu">
                        <#assign altRow = true>
                        <#assign availableLocales = Static["org.ofbiz.base.util.UtilMisc"].availableLocales()/>
                        <#list availableLocales as availableLocale>
                            <#assign altRow = !altRow>
                            <#assign langAttr = availableLocale.toString()?replace("_", "-")>
                            <#assign langDir = "ltr">
                            <#if "ar.iw"?contains(langAttr?substring(0, 2))>
                                <#assign langDir = "rtl">	
                            </#if>
                            <li>
                                <a href="<@ofbizUrl>setSessionLocale</@ofbizUrl>?newLocale=${availableLocale.toString()}">
                                    <div>${availableLocale.getDisplayName(availableLocale)} &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; [${availableLocale.toString()}]</div>
                                </a>
                            </li>
                        </#list>
                    </ul>
                </li>
                <li class="divider"></li>
                <li><a href="<@ofbizUrl>logout</@ofbizUrl>"><i class="icon-off"></i> ${uiLabelMap.BPOSLogout}</a></li>
            </ul>
        </li>
    </ul>

	<div class="pos-help pull-right" title="${uiLabelMap.POSListOfShortcuts} (Alt + S)" id="divListOfShortcuts"><i class="icon-question" aria-hidden="true"></i></div>
</nav>
</#if>
</div>
</div>

<script type="text/javascript">
var disableChangeAfterPaid = 0;
<#assign productUomShowList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) !>
$(function () {
	Loading.hide('loadingMacro');
	var pusData = [
	   			<#if productUomShowList?exists>
	   				<#list productUomShowList as itemUomShow >
	   				{
	   					quantityUomId: '${itemUomShow.uomId?if_exists}',
	   					description: '${itemUomShow.description?if_exists}'
	   				},
	   				</#list>
	   			</#if>
	                  ];
	
	function getDescriptionOfUom(uomId){
		for (var i = 0; i < pusData.length; i++) {
	    	if(pusData[i].quantityUomId == uomId){
	       		return '<div>' + pusData[i].description + '</div>';
	       	}
	    }
	    return '<div>' + uomId + '</div>';
	}
	
	$('body').keydown(function(e) {
		//112 F1
		$(window).keydown(function(e){
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 112){e.preventDefault();}
		});
		//113 F2
		$(window).keydown(function(e){
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 113){e.preventDefault();}
		});
		
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 112) {
			if (flagPopup){
				productToSearchFocus();
			}
			e.preventDefault();
			return false;
		}
		
		if (code === 113) {
			if (flagPopup){
				partyToSearchFocus();
			}
			e.preventDefault();
			return false;
		}
	});
	
	var sourceProduct =
	{
	    datatype: "json",
	    datafields: [
	        { name: 'productId' },
	        { name: 'productCode' },
	        { name: 'internalName' },
	        { name: 'productName' },
	        { name: 'price' },
	        { name: 'termUomId' },
	        { name: 'idSKU' },
	        { name: 'qoh' },
	        { name: 'facilityId' },
	        { name: 'currencyUomId' },
	        { name: 'requireAmount' },
	        { name: 'amount' },
	        { name: 'idEAN' }
	    ],
	    type: "POST",
	    root: "productsList",
	    contentType: 'application/x-www-form-urlencoded',
	    url: "FindProducts"
	};
	
   	var dataAdapter = new $.jqx.dataAdapter(sourceProduct,
	{
    	downloadComplete: function (data, status, xhr) {
     		if(data.productsList.length < 2){
     			$("#jqxProductList").jqxComboBox({autoOpen: false});
     		}else{
     			$("#jqxProductList").jqxComboBox({autoOpen: true}); 
     		}
     		disableKeyProduct();
        },
    	formatData: function (data) {
        	if ($("#jqxProductList").jqxComboBox('searchString') != undefined) {
	            data.productToSearch = $("#jqxProductList").jqxComboBox('searchString');
	            return data;
            }
        }
	});
   	
	$("#jqxProductList").jqxComboBox({
   		width: 208,
    	dropDownWidth: 680,
        placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)",
        showArrow: false,
        height: 30,
        source: dataAdapter,
        remoteAutoComplete: true,
        selectedIndex: 0,
        displayMember: "productName",
        valueMember: "productCode",
        scrollBarSize: 15,
        autoComplete: true,
        renderer: function (index, label, value) {
        	var item = dataAdapter.records[index];
            if (item != null) {
           		var productName = item.productName;
            	if (productName.length > 65){
            		productName = productName.substring(0, 65);
                	productName = productName + '...';
            	}
				var idSKU = "";
				if (item.idSKU){
					idSKU = item.idSKU;
				}
            	var tableItem = '<div class="span12" style="width: 700px; height: 35px">'
            	   + '<div class="span2" style="margin-left: -30px; width: 150px;">' + '[' + limitByChar(item.productCode, 20) + ']' + '</div>'
            	   + '<div class="span6" style="width: 300px; margin-left: 10px; white-space: normal">' + item.productName;
            		if (item.requireAmount == "Y") tableItem += ' (A: ' + (typeof(item.amount) == "undefined" ? 'NULL' : item.amount) + ')';
            		tableItem += '</div>'
            	   + '<div class="span1" style="width: 30px; margin-left: 10px">' + getDescriptionOfUom(item.termUomId) + '</div>';
                if(item.price && item.currencyUomId){
             		var productPrice = item.price;
             	   	var priceLabel = '<div class="span2" style="width: 100px; margin-left: 10px">' + "( " + formatcurrency(productPrice, item.currencyUomId)+ ")" + '</div>';
             	   	tableItem += priceLabel;
                } else {
             	   	var priceLabel = '<div class="span2" style="width: 100px; margin-left: 10px">' + "( " + formatcurrency(0, item.currencyUomId)+ ")" + '</div>';
             	   	tableItem += priceLabel;
                }
                var quantity = item.qoh;
                if(quantity == null || typeof quantity == "undefined"){
                	quantity = 0;
                }
                var quantityLabel = '<div class="span1" style="width: 50px; margin-left: 0px">' + quantity + '</div>';
             	tableItem += quantityLabel + '</div>';
                return tableItem;
             }
               return "";
		},
        renderSelectedItem: function(index, item)
        {
        	var item = dataAdapter.records[index];
            if (item != null) {
           		var label = item.productName;
                return label;
            }
            return "";   
        },
        search: function (searchString) {}
	});
	$("#jqxProductList").keypress(function(e) {
	    if(e.which == 13) {
	    	dataAdapter.dataBind();
	    }
	});
	var sourceCustomer =
    {
	    datatype: "json",
	    datafields: [
	        { name: 'partyId' },
	        { name: 'firstName' },
	        { name: 'middleName' },
	        { name: 'phoneMobile'},
	        { name: 'addressPrimary1'},
	        { name: 'cityPrimary'},
	        { name: 'lastName' },
	        { name: 'contactMechId'},
	     	{ name: 'contactMechPurposeTypeId'}
	    ],
	    type: "POST",
	    root: "partiesList",
	    contentType: 'application/x-www-form-urlencoded',
	    url: "FindParties"
	};
	
    var dataAdapterCustomer = new $.jqx.dataAdapter(sourceCustomer,
    {
    	downloadComplete: function (data, status, xhr) {
     		if(data.partiesList.length < 2){
     			$("#jqxPartyList").jqxComboBox({autoOpen: false});
     		}else{
     			$("#jqxPartyList").jqxComboBox({autoOpen: true});
     		}
     		disableKeyParty();
        },
        formatData: function (data) {
        	if ($("#jqxPartyList").jqxComboBox('searchString') != undefined) {
            	data.partyToSearch = $("#jqxPartyList").jqxComboBox('searchString');
                return data;
            }
        }
	});
    
	$("#jqxPartyList").jqxComboBox({
   		width: 208,
	    dropDownWidth: 645,
	    placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchParty)} (F2)",
	    height: 30,
	    showArrow: false,
	    autoOpen: false,
	    source: dataAdapterCustomer,
        dropDownHorizontalAlignment:'right',
	    remoteAutoComplete: true,
	    selectedIndex: 0,
	    displayMember: "firstName",
	    valueMember: "partyId",
	    autoComplete: true,
	    renderer: function (index, label, value) {
   	    	var item = dataAdapterCustomer.records[index];
   	        if (item != null) {
   	        	var addressPrimary1 = "";
   	            var cityPrimary = "";
   	            var phoneMobile = "";
   	            var middleName = "";
   	            if (item.addressPrimary1 != null){
   	            	addressPrimary1 = item.addressPrimary1;
   	            }
   	            if (item.cityPrimary != null){
   	            	cityPrimary = item.cityPrimary;
   	            }
   	            if (item.phoneMobile != null){
   	            	phoneMobile = item.phoneMobile;
   	           	}
   	            if (item.middleName != null){
   	            	middleName = item.middleName;
   	           	}
   	            var tableItem = '<div class="span12" style="width: 600px; height: 35px">'
   	            	+ '<div class="span2" style="margin-left: -30px; width: 130px">' + '[' + item.partyId + ']' + '</div>'
   	            	+ '<div class="span3" style="width: 110px; margin-left: 15px; white-space: normal">' + item.lastName + ' ' + middleName + ' ' + item.firstName + '</div>'
   	            	+ '<div class="span5" style="width: 250px; margin-left: 10px; white-space: normal">' + addressPrimary1 + ' ' + cityPrimary + '</div>'
   	            	+ '<div class="span2" style="width: 100px; margin-left: 10px">' + phoneMobile + '</div></div>';
   	           	return tableItem;
   	      	}
   	        return "";
		},
   	    renderSelectedItem: function(index, item){
   	    	var item = dataAdapterCustomer.records[index];
   	        if (item != null) {
   	        	var label = item.firstName;
   	            return label;
   	        }
   	        return ""; 
   	    },
   	    search: function (searchString) {}
	});
	$("#jqxPartyList").keypress(function(e) {
	    if(e.which == 13) {
	    	dataAdapterCustomer.dataBind();
	    }
	});
	$('#jqxProductList').on('close', function (event) {
    	var item = $("#jqxProductList").jqxComboBox('getSelectedItem'); 
    	if(item != undefined){
    		item = item.originalItem;
    		if(item){
    			var amount = item.requireAmount == "Y" ? item.amount : null;
    			addItem(item.productId, '1', 'Y', item.termUomId, amount, item.idEAN);
        		$('#jqxProductList').jqxComboBox({ disabled: false }); 
    		}
    	} else {
    		flagPopup = true;
    	}
    });
	
	$("#jqxProductList").on('bindingComplete', function (event) {
		var items = $("#jqxProductList").jqxComboBox('getItems');
	    $("#jqxProductList").jqxComboBox({ autoOpen: false });
        if((items)&&(items.length > 0)){
        	if(items.length == 1){
        		var firstItem = items[0];
        		if(firstItem != undefined){
        			firstItem = firstItem.originalItem;
        			if(firstItem){
        				var amount = firstItem.requireAmount == "Y" ? firstItem.amount : null;
        				addItem(firstItem.productId, '1', 'Y',firstItem.termUomId, amount, firstItem.idEAN);
        			}
                } 
        	}
        } else {
        	$("#jqxProductList").jqxComboBox('clearSelection');
        }
        disableKeyProduct();
	});
        
    $("#jqxProductList").on('open', function (event) {
    	flagPopup = false;
    });
        
	$('#jqxPartyList').on('close', function (event) {
    	var item = $("#jqxPartyList").jqxComboBox('getSelectedItem'); 
        if(item != undefined){
        	var originalItem = item.originalItem;
        	if(originalItem != undefined){
        		setPartyToCart(originalItem.partyId);
        	}
        } else {
        	flagPopup = true;
        }
	});
        
	$("#jqxPartyList").on('bindingComplete', function (event) {
        var items = $("#jqxPartyList").jqxComboBox('getItems'); 
        $("#jqxPartyList").jqxComboBox({ autoOpen: false });
        if((items)&&(items.length > 0)){
        	if(items.length == 1){
        		var firstItem = items[0];
        		if(firstItem != undefined){
        			var originalItem = firstItem.originalItem;
        	    	if(originalItem != undefined){
        	    		setPartyToCart(originalItem.partyId);
        	    	}
                }
        	}
        } else {
        	$("#jqxPartyList").jqxComboBox('clearSelection');
        }
        disableKeyParty();
	});
        
	$("#jqxPartyList").on('open', function (event) {
    	flagPopup = false;
    });
        
   	disableKeyProduct();
    disableKeyParty();
});

function disableKeyProduct(){
	$( "#jqxProductList" ).on('keydown', function (event) {
    	if(event.keyCode === 38 || event.keyCode === 40) { //up or down
            // focus to other element
        	var e = $.Event('keydown');
            e.keyCode = event.keyCode; 
            $('body').trigger(e);
            return false;
        }
    	if (event.keyCode === 13){
    		var item = $("#jqxProductList").jqxComboBox('getSelectedItem'); 
			if(item != undefined){
	    		item = item.originalItem;
	    		if(item){
	    			var amount = item.requireAmount == "Y" ? item.amount : null;
	    			addItem(item.productId, '1', 'Y', item.termUomId, amount, item.idEAN);
	        		$('#jqxProductList').jqxComboBox({ disabled: false }); 
	    		}
	    	}
    	}
        if(event.keyCode === 9){
			event.preventDefault();
			return false;
        }
    });
}	

function disableKeyParty(){
	$( "#jqxPartyList" ).on('keydown', function (event) {
    	if(event.keyCode === 38 || event.keyCode === 40) { //up or down
        	// focus to other element
        	var e = $.Event('keydown');
        	e.keyCode = event.keyCode; 
            $('body').trigger(e);
            return false;
        }
    	if (event.keyCode === 13){
    		var item = $("#jqxPartyList").jqxComboBox('getSelectedItem'); 
            if(item != undefined){
            	var originalItem = item.originalItem;
            	if(originalItem != undefined){
            		setPartyToCart(originalItem.partyId);
            	}
            }
    	}
        if(event.keyCode === 9){
			event.preventDefault();
			return false;
        }
    });
}
</script>
<script type="text/javascript">
    //focus into product search
    function nav(){
        $('div#nav ul li').mouseover(function() {
            $(this).find('ul:first').show();
        });

        $('div#nav ul li').mouseleave(function() {

            $('div#nav ul li ul').hide();
        });

        $('div#nav ul li ul').mouseleave(function() {
            $('div#nav ul li ul').hide();
        });
    };

    $(document).ready(function() {
        nav();
    });
	$(document).ajaxError(function( event, request, settings ) {
		if (request.status == 403) {
			location.reload();
		}
	});
</script>

<style type="text/css">
	#jqxProductList{
		margin-top: 10px;
		margin-right: 10px;
	}
	#jqxPartyList{
		margin-top: 10px;
	}
    #user_menu ul,
    #user_menu ul li {
        margin:0;
        padding:0;
        list-style:none;
    }
    #user_menu ul li{
        float:left;
        display:block;
    }
    .obl_submenu {
        right:100%;
        position: absolute;
        width: 100%;
        background: #FFF;
        display: none;
        line-height: 26px;
        z-index: 1000;
        margin-top:-30px !important;
        border-radius: 0;
        box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        border:1px solid #ccc;
        padding:5px 0 !important;
    }
    .obl_submenu:before{
        position: absolute;
        top: 4px;
        right: -7px;
        display: inline-block;
        border-bottom: 7px solid transparent;
        border-left: 7px solid #efefef;
        border-top: 7px solid transparent;
        border-left-color: rgba(0,0,0,0.2);
        content: '';
    }
    .obl_submenu:after{
        position: absolute;
        top: 5px;
        right: -6px;
        display: inline-block;
        border-bottom: 6px solid transparent;
        border-left: 6px solid #fff;
        border-top: 6px solid transparent;
        content: '';
    }
    .obl_submenu li{
        display:block !important;
        width:93% !important;
    }
    .obl_submenu a{
        display:block !important;
        width:92% !important;
        color: black !important;
    }
    .obl_submenu a:hover{
        text-decoration:none !important;
    }
    .obl_submenu a div{
        padding:4px;
    }
</style>