import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastSet;


EntityFindOptions opts = new EntityFindOptions();
opts.setDistinct(true);
listProductSupplier = delegator.findList("ProductBrand", null, UtilMisc.toSet("brandName", "groupName"), UtilMisc.toList("+groupName"), opts, false);
context.listProductSupplier = listProductSupplier;

listOrigin = delegator.findList("ProductGeo",  null, UtilMisc.toSet("originGeoId", "geoName"), UtilMisc.toList("+geoName"), opts, false);

context.listOrigin = listOrigin;