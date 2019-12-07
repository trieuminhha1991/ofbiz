<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script type="text/javascript">
<#-- some labels are not unescaped in the JSON object so we have to do this manualy -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}
 
jQuery(window).load(createTree());

<#-- creating the JSON Data -->
var rawdata = [
        <#if (completedTree?has_content)>
            <@fillTree rootCat = completedTree/>
        </#if>
        
        <#macro fillTree rootCat>
            <#if (rootCat?has_content)>
                <#list rootCat as root>
                    {
                    "data": {"title" : unescapeHtmlText("<#if root.accountName?exists && root.glTaxFormId?exists>${root.accountCode?js_string} ${root.accountName} ${root.glTaxFormId}<#elseif root.accountName?exists>${root.accountCode?js_string} ${root.accountName}<#else>${root.accountCode?js_string}</#if>"), "attr": {"href" : "#","onClick" : "callDocument('${root.glAccountId}');"}},
                    "attr": {"id" : "${root.glAccountId}", "rel" : "root"}
                    <#if root.child?exists>
                    ,"state" : "closed"
                    </#if>
                    <#if root_has_next>
                        },
                    <#else>
                        }
                    </#if>
                </#list>
            </#if>
        </#macro>
     ];

 <#-- create Tree-->
  function createTree() {
    jQuery(function () {
        jQuery("#tree").jstree({
        "plugins" : [ "themes", "json_data","ui" ,"cookies", "types"],
            "json_data" : {
                "data" : rawdata,
                          "ajax" : { "url" : "<@ofbizUrl>getChild</@ofbizUrl>", "type" : "POST",
                          "data" : function (n) {
                            return { 
                                "glAccountId" : n.attr ? n.attr("id").replace("node_","") : 1 ,
                                "additionParam" : "','category" ,
                                "hrefString" : "GlAccountNavigate?glAccountId=" ,
                                "onclickFunction" : "callDocument"
                        }; 
                    }
                }
            },
            "types" : {
             "valid_children" : [ "root" ],
             "types" : {
                 "CATEGORY" : {
                     "icon" : { 
                         "image" : "/images/jquery/plugins/jsTree/themes/apple/d.png",
                         "position" : "10px40px"
                     }
                 }
             }
         }
        });
    });
  }
  
  function callDocument(id) {
    //jQuerry Ajax Request
    var dataSet = {"glAccountId":id, "trail":null, "ajaxUpdateEvent" : "Y"};
    jQuery.ajax({
        url: 'editGlAccount',
        type: 'POST',
        data: dataSet,
        error: function(msg) {
            alert("An error occured loading content! : " + msg);
        },
        success: function(msg) {
            jQuery('#editGlAccountDiv').html(msg);
        }
    });
  }
</script>

<div id="tree"></div>
