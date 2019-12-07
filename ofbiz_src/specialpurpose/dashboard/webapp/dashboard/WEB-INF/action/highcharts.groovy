import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.*;
import java.util.List;

totalPerson = delegator.findByAnd("Employment",UtilMisc.toMap("roleTypeIdFrom","INTERNAL_ORG","roleTypeIdTo","EMPLOYEE","partyIdFrom","company"),null,false);
totalPerson.
