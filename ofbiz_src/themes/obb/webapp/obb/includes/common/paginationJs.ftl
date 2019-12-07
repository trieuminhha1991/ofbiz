<script type="text/javascript" src="/obbresources/asset/twbs-pagination/jquery.twbsPagination.min.js"></script>
<#if listSize?exists && viewSize?exists>
	<#assign totalPages = Static["java.lang.Math"].ceil((listSize)?double / viewSize?double)>
<#else>
	<#assign totalPages = 0>
</#if>

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
		var startPage = parseInt("${viewIndex}") + 1;
		
		var totalPages = parseInt("${totalPages}");
		totalPages = totalPages==0?totalPages=1:totalPages;
		var width = $(window).width();
		var visiblePages = 4;
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
		    		callDocumentByPaginate('${productCategoryId}~${viewSize}~' + (page - 1));
				}
		    }
		});
		initPadding(obj);
		$(window).resize(function(){
			initPadding(obj);
		})
		
		if(width <= 400){
			$("#displayOptions").removeClass("hide");
			$("#displayOptionsContainer").addClass("collapse");
		} else {
			$("#sortOrder-hidden-mobile").removeClass("hide");
		}
	});
	
</script>