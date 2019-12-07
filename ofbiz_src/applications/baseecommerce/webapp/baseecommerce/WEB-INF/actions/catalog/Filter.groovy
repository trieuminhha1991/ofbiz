import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;
import org.ofbiz.product.store.ProductStoreWorker;
import com.olbius.product.product.ProductEvents;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.category.FilterUtils;

Map<String, Object> price_range1 = FastMap.newInstance();
price_range1.put("low", 0);
price_range1.put("high", 100000);
Map<String, Object> price_range2 = FastMap.newInstance();
price_range2.put("low", 100000);
price_range2.put("high", 300000);
Map<String, Object> price_range3 = FastMap.newInstance();
price_range3.put("low", 300000);
price_range3.put("high", 500000);
Map<String, Object> price_range4 = FastMap.newInstance();
price_range4.put("low", 500000);
price_range4.put("high", 1000000);
Map<String, Object> price_range5 = FastMap.newInstance();
price_range5.put("low", 1000000);
price_range5.put("high", 10000000);
ArrayList<Map<String, Object>> price = new ArrayList<Map<String, Object>>();

price.add(price_range1);
price.add(price_range2);
price.add(price_range3);
price.add(price_range4);
price.add(price_range5);

context.filterPrice = price;

def sb = parameters.sb;
if("price".equals(sb)) {
	def vl = parameters.vl;
	if(vl) {
		vlSpl = vl.split("-");
		if(vlSpl.length >= 1) {
			context.lowSlted = Integer.valueOf(vlSpl[0]);
		}
		if(vlSpl.length >= 2) {
			context.highSlted = Integer.valueOf(vlSpl[1]);
		}
	}
}

//context.brNoBrandSize = brNoBrandSize;
if(context.productCategoryId){
	brandMap = FilterUtils.getBrandAndProductNumber(delegator, context.productCategoryId, locale);
	brands = brandMap.brands;
	brTotalSize = brandMap.brTotalSize;
	context.brands = brands;
	context.brTotalSize = brTotalSize;
}
context.otherBrandId = EntityUtilProperties.getPropertyValue("ecommerce.properties", "category.brand.other.default", "OTHER_BRAND", delegator);
