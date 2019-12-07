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
                           "data": {"title" : unescapeHtmlText("<#if root.categoryName?exists>${root.categoryName?js_string} [${root.dataCategoryId}]<#else>${root.dataCategoryId?js_string}</#if>")},
                           "attr": {"id" : "${root.dataCategoryId}", "rel" : "E"}
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
            $.cookie('jstree_select', null);
            $.cookie('jstree_open', null);        
            jQuery("#tree").jstree({
            "core" : {
    			"initially_open" : ["${context.homeDataCategoryId}"],
    			"async" : true
            },
            "plugins" : [ "themes","json_data","search","ui" ,"cookies", "types", "crrm", "contextmenu"],
                "json_data" : {
                    "data" : rawdata,
                    "ajax" : { "url" : "<@ofbizUrl>getDataCategoryChild</@ofbizUrl>", "type" : "POST",
                        "data" : function (n) {
                          return { 
                              "dataCategoryId" : n.attr ? n.attr("id").replace("node_","") : 1,
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
                },
                "contextmenu": {items: customMenu}
            });
        });
	};
	
function customMenu(node) {
		    // The default set of all items

		    if(node.attr('rel')=='E'){ 
		    var items = {
		    		createDataCategory: {
		   	    		label: "${uiLabelMap.CtxMenuItemTitle_AddDataCategory}",
		   	    		action: function (NODE, TREE_OBJ){
		   	    			var dataSet = {"parentCategoryId" : NODE.attr("id")};
		   	    			jQuery.ajax({
		   	    				type: "GET",
		   	    				url: "EditDataCategory",
		   	    				data: dataSet,
		   	    				success: function(data){
		   	    					jQuery('div.contentarea').html(data);
		   	    				}, 
		   	    				error: function(error){
		   	    					bootbox.dialog({
		   								message: error,
		   								title: "${uiLabelMap.ErrorWhenLoadContent}",
		   								buttons:{
		   									main: {
		   										label: "OK!",
		   										className: "btn-small btn-danger"
		   									}
		   								}
		   							});
		   	    				}
		   	    			});
		   	    		}
		   	    	},
		   	    	uploadDocument: {
		   	    		label: "${uiLabelMap.CtxMenuItemTitle_UploadDocument}",
		   	    		action: function (NODE, TREE_OBJ){
		   	    			var dataSet = {"dataCategoryId" : NODE.attr("id")};
		   	    			jQuery.ajax({
		   	    				type: "GET",
		   	    				url: "UploadDocument",
		   	    				data: dataSet,
		   	    				success: function(data){
		   	    					jQuery('div.contentarea').html(data);
		   	    				}, 
		   	    				error: function(error){
		   	    					bootbox.dialog({
		   	    						message: error,
		    							title: "${uiLabelMap.ErrorWhenLoadContent}",
		    							buttons:{
		    								main: {
		    									label: "OK!",
		    									className: "btn-small btn-danger"
		    								}
		    							}
		   	    					});
		   	    				}
		   	    			});
		   	    		}
		   	    	}
		    	};
		    
		    return items;
		    }
		else if (node.attr('rel')=='L'){
			var items = {
		   	    	downloadDocument: {
		   	    		label: "${uiLabelMap.CtxMenuItemTitle_DownloadDocument}",
		   	    		action: function (NODE, TREE_OBJ){
		   	    			var path = NODE.attr("path")+"?externalLoginKey="+'${Request['externalLoginKey']}';
		   	    			location.assign(path);
		   	    		}
		   	    	},
			
					changeMode: {
						label: "${uiLabelMap.CtxMenuItemTitle_ChangeMode}",
						action: function (NODE, TREE_OBJ){
							var dataSet = {"dataResourceId" : NODE.attr("id")};
							jQuery.ajax({
								type: "GET",
								url: "ChangeMode",
								data: dataSet,
								success: function(data){
									jQuery('div.contentarea').html(data);
								}, 
								error: function(error){
									bootbox.dialog({
										message: error,
										title: "${uiLabelMap.ErrorWhenLoadContent}",
										buttons:{
											main: {
											label: "OK!",
											className: "btn-small btn-danger"
											}
										}
									});
								}
							});
						}
					},
			
					removeDocument: {
						label: "${uiLabelMap.CtxMenuItemTitle_RemoveDocument}",
						action: function (NODE, TREE_OBJ){
							var answer = confirm ("Are you sure you want to remove ?")
							if(answer){
							var dataSet = {"dataResourceId" : NODE.attr("id")};
							jQuery.ajax({
								type: "POST",
								url: "removeDocument",
								data: dataSet,
								error: function(msg) {
			                        alert("An error occured loading content! : " + msg);
			                    },
			                    success: function(msg) {
			                    	location.reload();
			                    }
							});
						}else{
							return;
						}
						}
					}
		    	};
		    
		    return items;
		}
	}

function callDocument(id,type) {
    //jQuerry Ajax Request
    var dataSet = {};
        URL = 'DocumentInfo';
        dataSet = {"dataResourceId" : id};
        
   jQuery.ajax({
        url: URL,
        type: 'POST',
        data: dataSet,
        error: function(msg) {
           alert("An error occured loading content! : " + msg);
        },
        success: function(msg) {;
            jQuery('div.contentarea').html(msg);
        }
    });
  }
</script>
  
<div class="row-fluid">
	<div class="span5">
		<div class="widget-box transparent no-bottom-border">
			<div class="widget-header widget-header-small">
				<h4 style="margin-top: 3px">${uiLabelMap.WidgetTitle_DocManager}</h4>
				<div class="widget-toolbar">
					<div class="nav-search" id="nav-search" style="position: relative;">
						<span class="input-icon">
							<input type="text" id="jqsearch" placeholder="${uiLabelMap.CommonSearch} ..." class="input-small nav-search-input" id="nav-search-input" autocomplete="off" />
							<i class="icon-search nav-search-icon"></i>
						</span>
					</div>
				</div>
			</div>
			<div class="widget-body">
				<div id="tree" class="span12" style="margin-top: 5px"></div>				
			</div>
		</div>
	</div>
	<div class="span7">
		<div class="contentarea"></div>
	</div>
</div>