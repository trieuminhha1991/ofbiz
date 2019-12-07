import org.ofbiz.product.store.ProductStoreWorker;

productStoreId = ProductStoreWorker.getProductStoreId(request);
productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);

if (productStore) {
	context.storeName = productStore.getString("storeName");
}
