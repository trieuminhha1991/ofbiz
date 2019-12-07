
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

channel = UtilProperties.getPropertyValue("ChannelBus", "defaultChannel");

values = delegator.findByAnd("ProductStore", UtilMisc.toMap("salesMethodChannelEnumId", channel), null, false);

productStoreId = null;

if(values != null && !values.isEmpty()) {
    productStoreId = values.get(0).getString("productStoreId");
}

context.defaultProductStoreId = productStoreId;
