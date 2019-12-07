<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/ui/development-bundle/external/jquery.cookie.js</@ofbizContentUrl>"></script>

<#if (requestAttributes.topLevelList)?exists>
    <#assign topLevelList = requestAttributes.topLevelList>
</#if>
<#assign rootEle=true>

<#macro fillTree rootCat>
  <#if (rootCat?has_content)>
	<div<#if rootEle=true> id="menu"><span>Menu</span><#else>></#if>
	  <ul>
		<#if rootEle=true>
		<li class="home"><a  title="Home" href="<@ofbizUrl>main</@ofbizUrl>"><span>Home</span></a></li>
		</#if>
		<#assign rootEle=false>
	<#list rootCat?sort_by("productCategoryId") as root>
	<li>
		<a href="<@ofbizCatalogAltUrl productCategoryId=root.productCategoryId/>" ><#if root.categoryName?exists>${root.categoryName}<#elseif root.categoryDescription?exists>${root.categoryDescription}<#else>${root.productCategoryId}</#if></a>
		<#if root.child?has_content>
                <@fillTree rootCat=root.child/>
            </#if>
	</li>
        </#list>
      </ul>
    </div>
  </#if>
</#macro>
<script type="text/javascript">
<#-- some labels are not unescaped in the JSON object so we have to do this manuely -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}

 <#-------------------------------------------------------------------------------------define Requests-->
  var editDocumentTreeUrl = '<@ofbizUrl>/views/EditDocumentTree</@ofbizUrl>';
  var listDocument =  '<@ofbizUrl>/views/ListDocument</@ofbizUrl>';
  var editDocumentUrl = '<@ofbizUrl>/views/EditDocument</@ofbizUrl>';
  var deleteDocumentUrl = '<@ofbizUrl>removeDocumentFromTree</@ofbizUrl>';


<#-------------------------------------------------------------------------------------callDocument function-->
    function callDocument(id, parentCategoryStr) {
        var checkUrl = '<@ofbizUrl>productCategoryList</@ofbizUrl>';
        if(checkUrl.search("http"))
            var ajaxUrl = '<@ofbizUrl>productCategoryList</@ofbizUrl>';
        else
            var ajaxUrl = '<@ofbizUrl>productCategoryListSecure</@ofbizUrl>';

        //jQuerry Ajax Request
        jQuery.ajax({
            url: ajaxUrl,
            type: 'POST',
            data: {"category_id" : id, "parentCategoryStr" : parentCategoryStr},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#content').html(msg);
            }
        });
     }
<#-------------------------------------------------------------------------------------callCreateDocumentTree function-->
      function callCreateDocumentTree(contentId) {
        jQuery.ajax({
            url: editDocumentTreeUrl,
            type: 'POST',
            data: {contentId: contentId,
                        contentAssocTypeId: 'TREE_CHILD'},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }
<#-------------------------------------------------------------------------------------callCreateSection function-->
    function callCreateDocument(contentId) {
        jQuery.ajax({
            url: editDocumentUrl,
            type: 'POST',
            data: {contentId: contentId},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }
<#-------------------------------------------------------------------------------------callEditSection function-->
    function callEditDocument(contentIdTo) {
        jQuery.ajax({
            url: editDocumentUrl,
            type: 'POST',
            data: {contentIdTo: contentIdTo},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });

    }
<#-------------------------------------------------------------------------------------callDeleteItem function-->
    function callDeleteDocument(contentId, contentIdTo, contentAssocTypeId, fromDate) {
        jQuery.ajax({
            url: deleteDocumentUrl,
            type: 'POST',
            data: {contentId : contentId, contentIdTo : contentIdTo, contentAssocTypeId : contentAssocTypeId, fromDate : fromDate},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                location.reload();
            }
        });
    }
 <#-------------------------------------------------------------------------------------callRename function-->
    function callRenameDocumentTree(contentId) {
        jQuery.ajax({
            url: editDocumentTreeUrl,
            type: 'POST',
            data: {  contentId: contentId,
                     contentAssocTypeId:'TREE_CHILD',
                     rename: 'Y'
                     },
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }
 <#------------------------------------------------------pagination function -->
    function nextPrevDocumentList(url){
        url= '<@ofbizUrl>'+url+'</@ofbizUrl>';
         jQuery.ajax({
            url: url,
            type: 'POST',
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }

</script>

	<#if (topLevelList?has_content)>
		<@fillTree rootCat=completedTree/>
	</#if>
