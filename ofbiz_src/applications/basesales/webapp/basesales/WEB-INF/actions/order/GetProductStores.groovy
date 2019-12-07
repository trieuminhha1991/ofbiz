import com.olbius.basesales.product.ProductStoreWorker;

//OLD: productStores = delegator.findList("ProductStore", null, null, ["productStoreId", "storeName"], null, true);
List<GenericValue> productStores = ProductStoreWorker.getListProductStoreSell(delegator, userLogin);
context.productStores = productStores;