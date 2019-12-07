import org.ofbiz.entity.util.EntityUtil;


List<GenericValue> listProduct = new ArrayList<GenericValue>();
listProductCategoryMember = parameters.categoryMembers;
if (listProductCategoryMember) {
	List<GenericValue> listProductCategoryMemberFiltered = EntityUtil.filterByDate(listProductCategoryMember);
	for (item in listProductCategoryMemberFiltered) {
		GenericValue product = delegator.findOne("Product", ["productId" : item.productId], false);
		listProduct.add(product);
	}
}
context.listProduct = listProduct;