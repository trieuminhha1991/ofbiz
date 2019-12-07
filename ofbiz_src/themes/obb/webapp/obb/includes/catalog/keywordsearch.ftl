<#-- FIXME check with sorting, may be lost keyword -->

<#if parameters.hoz??>
<#assign hoz=parameters.hoz/>
<#else>
<#assign hoz="N"/>
</#if>
<#if parameters.VIEW_SIZE??>
<#assign view_size=parameters.VIEW_SIZE?number/>
<#else>
<#assign view_size=8/>
</#if>

<#if parameters.countResult??>
<#assign list_size=parameters.countResult?number/>
<#else>
<#assign list_size=1/>
</#if>

<#if parameters.VIEW_INDEX??>
<#assign view_index=parameters.VIEW_INDEX?number/>
<#else>
<#assign view_index=0/>
</#if>

<div id="jm-current-content" class="clearfix">
	<div class="page-title category-title">
		<#if parameters.name??>
		<h3>${uiLabelMap.BESearchResultFor}: ${parameters.name}</h3>
		</#if>
	</div>
	<#--
	<ul>
		<#list searchConstraintStrings as searchConstraintString>
		<li>
			<a href="<@ofbizUrl>keywordsearch?removeConstraint=${searchConstraintString_index}&amp;clearSearch=N</@ofbizUrl>" class="btn btn-danger btn-mini icon-trash"></a>&nbsp;${searchConstraintString}
		</li>
		</#list>
	</ul>
	-->
	<#-- FIXME implement advanced search
	<div>
		<a href="<@ofbizUrl>advancedsearch?SEARCH_CATEGORY_ID=${(requestParameters.SEARCH_CATEGORY_ID)?if_exists}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonRefineSearch}</a>
	</div> -->
	<#if parameters.result?has_content>
	<div class="category-products">
		<div class="toolbar">
			<div class="row">
			<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12'>
				<ul class="pagination-sm pagination-obl"></ul>
			</div>
			<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12'>
				<div class="limiter">
					<div class="select-box2">
						<select id="viewSize" name="viewSize" onchange="javascript:onSelect();">
							<option value="8" <#if view_size?int==8>selected="selected"</#if>>8</option>
							<option value="16" <#if view_size?int==16>selected="selected"</#if>>16</option>
							<option value="20" <#if view_size?int==20>selected="selected"</#if>>20</option>
							<option value="24" <#if view_size?int==24>selected="selected"</#if>>24</option>
							<option value="32" <#if view_size?int==32>selected="selected"</#if>>32</option>
							<option value="40" <#if view_size?int==40>selected="selected"</#if>>40</option>
						</select>
					</div>
					<label>${uiLabelMap.CommonShow}:</label>
				</div>
			</div>
			</div>
		</div>

		<#if hoz=="Y">
		<ul class="products-grid products-grid-special first last odd center">
			<#list parameters.result as proId>
			${setRequestAttribute("optProductId", proId)}
			${setRequestAttribute("listIndex", proId_index)}
			${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
			</#list>
		</ul>
		<#else>
		<ol class="products-list center" id="products-list">
			<#list parameters.result as proId>
			${setRequestAttribute("optProductId", proId)}
			${setRequestAttribute("listIndex", proId_index)}
			${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini2")}
			</#list>
		</ol>
		</#if>

		<div class="toolbar-bottom">
			<div class="toolbar">
				<div class="row">
					<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12'>
						<ul class="pagination-sm pagination-obl"></ul>
					</div>
					<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12'>
					<div class="limiter" style="">
						<div class="select-box2">
							<select name="viewSize" id="viewSize2" onchange="javascript:onSelect2();">
								<option value="8" <#if view_size?int==8>selected="selected"</#if>>8</option>
								<option value="16" <#if view_size?int==16>selected="selected"</#if>>16</option>
								<option value="20" <#if view_size?int==20>selected="selected"</#if>>20</option>
								<option value="24" <#if view_size?int==24>selected="selected"</#if>>24</option>
								<option value="32" <#if view_size?int==32>selected="selected"</#if>>32</option>
								<option value="40" <#if view_size?int==40>selected="selected"</#if>>40</option>
							</select>
						</div>
						<label>${uiLabelMap.CommonShow}:</label>
					</div>
				</div>
			</div>
			</div>
		</div>
	</div>

	<script>
		jQuery(function() {

			var param = getUrlParameters();
			var name = param["name"];
			var catalog = param["SEARCH_CATALOG_ID"];

			var _hoz = "${StringUtil.wrapString(hoz)}";

			var _view_size = ${ view_size };
			var _view_index = ${ view_index };

			var _url_search = "keywordsearch?";
			var _url_search2 = "keywordsearch?";
			var _url_param = "";
			if (name) {
				if (_url_param === "") {
					_url_param += "name=" + name;
				} else {
					_url_param += "&name=" + name;
				}
			}

			if (catalog) {
				if (_url_param === "") {
					_url_param += "SEARCH_CATALOG_ID=" + catalog;
				} else {
					_url_param += "&SEARCH_CATALOG_ID=" + catalog;
				}
			}

			_url_search2 += _url_param;

			if (_url_param === "") {
				_url_param += "VIEW_SIZE=" + _view_size + "&VIEW_INDEX=" + _view_index;
			} else {
				_url_param += "&VIEW_SIZE=" + _view_size + "&VIEW_INDEX=" + _view_index;
			}

			_url_search += _url_param;

			var text = "";

			if (_hoz === "Y") {
				text += '<span class="ico-outer active"><i class="fa fa-th"></i></span>';
				text += '<a href="<@ofbizUrl>' + _url_search + '&hoz=N</@ofbizUrl>" title="List" class="list">';
				text += '<span class="ico-outer"><i class="fa fa-th-list"></i></span>';
				text += '</a>';
			} else {
				text += '<a href="<@ofbizUrl>' + _url_search + '&hoz=Y</@ofbizUrl>" title="List" class="list">';
				text += '<span class="ico-outer"><i class="fa fa-th"></i></span>';
				text += '</a>';
				text += '<span class="ico-outer active"><i class="fa fa-th-list"></i></span>';
			}

			jQuery("#view_mode").append(text);

			var pagesProduct = jQuery("#pagesProduct");
			var pagesProduct2 = jQuery("#pagesProduct2");
			if (!pagesProduct) {
				return;
			}
			if (!pagesProduct2) {
				return;
			}

			var _list_size = ${list_size};
			var viewIndexMax = _list_size / _view_size;
			_url_search = _url_search2;
			text = "";
			if (viewIndexMax > 0) {
				text += '<ol>';
				if (_view_index != 0) {
					text += '<li class="previous">';
					text += '<a class="i-previous" href="<@ofbizUrl>' + _url_search + '&VIEW_SIZE=' + _view_size + '&VIEW_INDEX=' + (_view_index - 1) + '&hoz=${StringUtil.wrapString(hoz)}</@ofbizUrl>" title="Previous">';
					text += '<i class="fa fa-caret-left"></i>';
					text += '</a>';
					text += '</li>';
				}
				for (var i = 0; i < viewIndexMax; i++) {
					text += '<li class="current">';
					if (_view_index == i) {
						text += '' + (i + 1);
					} else {
						text += '<a href="<@ofbizUrl>' + _url_search + '&VIEW_SIZE=' + _view_size + '&VIEW_INDEX=' + i + '&hoz=${StringUtil.wrapString(hoz)}</@ofbizUrl>">' + (i + 1) + '</a>';
					}
					text += '</li>';
				}
				if ((_view_index + 1) < viewIndexMax) {
					text += '<li class="next">';
					text += '<a class="i-next" href="<@ofbizUrl>' + _url_search + '&VIEW_SIZE=' + _view_size + '&VIEW_INDEX=' + (_view_index + 1) + '&hoz=${StringUtil.wrapString(hoz)}</@ofbizUrl>" title="Next">';
					text += '<i class="fa fa-caret-right"></i>';
					text += '</a>';
					text += '</li>';
				}
				text += '</ol>';
				pagesProduct.html(text);
				pagesProduct2.html(text);
			}
		});

		function getUrlParameters() {
			var sPageURL = window.location.search.substring(1);
			var sURLVariables = sPageURL.split('&');
			var param = {};
			for (var i = 0; i < sURLVariables.length; i++) {
				var sParameterName = sURLVariables[i].split('=');
				param[sParameterName[0]] = sParameterName[1];
			}
			return param;
		}

		function onSelect() {
			var _val = jQuery("#viewSize").val();

			var _url_search = "keywordsearch?";

			var _hoz = "${StringUtil.wrapString(hoz)}";
			var param = getUrlParameters();
			var name = param["name"];
			var catalog = param["SEARCH_CATALOG_ID"];

			var _url_param = "";
			if (name) {
				if (_url_param === "") {
					_url_param += "name=" + name;
				} else {
					_url_param += "&name=" + name;
				}
			}

			if (catalog) {
				if (_url_param === "") {
					_url_param += "SEARCH_CATALOG_ID=" + catalog;
				} else {
					_url_param += "&SEARCH_CATALOG_ID=" + catalog;
				}
			}

			if (_url_param === "") {
				_url_param += "VIEW_SIZE=" + _val + "&VIEW_INDEX=0&hoz=" + _hoz;
			} else {
				_url_param += "&VIEW_SIZE=" + _val + "&VIEW_INDEX=0&hoz=" + _hoz;
			}

			_url_search += _url_param;
			window.location.href = _url_search;
		}

		function onSelect2() {
			var _val = jQuery("#viewSize2").val();

			var _url_search = "keywordsearch?";

			var _hoz = "${StringUtil.wrapString(hoz)}";
			var param = getUrlParameters();
			var name = param["name"];
			var catalog = param["SEARCH_CATALOG_ID"];

			var _url_param = "";
			if (name) {
				if (_url_param === "") {
					_url_param += "name=" + name;
				} else {
					_url_param += "&name=" + name;
				}
			}

			if (catalog) {
				if (_url_param === "") {
					_url_param += "SEARCH_CATALOG_ID=" + catalog;
				} else {
					_url_param += "&SEARCH_CATALOG_ID=" + catalog;
				}
			}

			if (_url_param === "") {
				_url_param += "VIEW_SIZE=" + _val + "&VIEW_INDEX=0&hoz=" + _hoz;
			} else {
				_url_param += "&VIEW_SIZE=" + _val + "&VIEW_INDEX=0&hoz=" + _hoz;
			}

			_url_search += _url_param;
			window.location.href = _url_search;
		}
	</script>

	<#else>
	<div style="width:722px;">
		<p class="alert alert-info">
			${uiLabelMap.ProductNoProductsInThisCategory}
		</p>
	</div>
	</#if>
</div>



<script type="text/javascript" src="/obbresources/asset/twbs-pagination/jquery.twbsPagination.min.js"></script>
<#assign totalPages = Static["java.lang.Math"].ceil((list_size - 1)?double / view_size?double)>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		var initPadding = function(obj){
			var par = obj.parent();
			var root = par.parent();
			var parw = par.outerWidth();
			var rootw = root.width();
			var objw = obj.width();
			if(rootw == parw){
				var p = (par.width() - objw) / 2;
				obj.css('margin-left', p + 'px');
			}else{
				obj.css('margin-left', '0px');
			}
		};
		var startPage = parseInt("${view_index}") + 1;
		var totalPages = parseInt("${totalPages}");
		totalPages = totalPages==0?totalPages=1:totalPages;
		var width = $(window).width();
		var visiblePages = 7;
		if(width <= 400){
			visiblePages = 3;
		}
		var obj = $('.pagination-obl');
		obj.twbsPagination({
		    totalPages: totalPages,
		    visiblePages: visiblePages,
		    startPage: startPage,
		    first: "<i class='fa fa-angle-double-left'></i>",
		    prev: "<i class='fa fa-angle-left'></i>",
		    next: "<i class='fa fa-angle-right'></i>",
		    last: "<i class='fa fa-angle-double-right'></i>",
		    onPageClick: function (event, page) {
		    	if (startPage != page) {
		    		page -= 1;
		    		var param = getUrlParameters();
		    		var name = param["name"];
					var catalog = param["SEARCH_CATALOG_ID"];
					var _view_size = ${ view_size };
					var _url_param = "keywordsearch?";
					if (name) {
						if (_url_param === "") {
							_url_param += "name=" + name;
						} else {
							_url_param += "&name=" + name;
						}
					}
					if (catalog) {
						if (_url_param === "") {
							_url_param += "SEARCH_CATALOG_ID=" + catalog;
						} else {
							_url_param += "&SEARCH_CATALOG_ID=" + catalog;
						}
					}
					if (_url_param === "") {
						_url_param += "VIEW_SIZE=" + _view_size + "&VIEW_INDEX=" + page + '&hoz=${StringUtil.wrapString(hoz)}';
					} else {
						_url_param += "&VIEW_SIZE=" + _view_size + "&VIEW_INDEX=" + page + '&hoz=${StringUtil.wrapString(hoz)}';
					}
					
					location.href = "<@ofbizUrl>" + _url_param + "</@ofbizUrl>"
				}
		    }
		});
		initPadding(obj);
	});
</script>
