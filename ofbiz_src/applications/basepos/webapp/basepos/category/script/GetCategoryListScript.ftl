<#assign categoryList = delegator.findList("ProdCatalogCategoryAndProductCategory", null, null, null, null, false) !>
<script type="text/javascript">
    var categoryListGlobalObject = (function(){
            var categoryList = [
            <#if categoryList?exists>
                <#list categoryList as category >
                    {
                        categoryId: '${category.productCategoryId?if_exists}',
                        categoryName: '${StringUtil.wrapString(category.categoryName?if_exists)}'
                    },
                </#list>
            </#if>
        ];
        var getCategoryName = function(categoryId){
        	var categoryName = categoryId;
            for (var index in categoryList) {
                if (categoryList[index].categoryId == categoryId) {
                    categoryName =  categoryList[index].categoryName;
                }
            }
            return categoryName;
        };
        return{
        	categoryList: categoryList,
        	getCategoryName: getCategoryName
        }
    }());
</script>