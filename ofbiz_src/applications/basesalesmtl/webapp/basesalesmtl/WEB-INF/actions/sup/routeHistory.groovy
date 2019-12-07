import com.olbius.salesmtl.util.SupUtil;
import com.olbius.salesmtl.DistributorServices;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.*;
import java.util.*;

String partyId = userLogin.getString("partyId");
context.routes = SupUtil.getAllRouteValue(delegator, partyId);
Map temp = FastMap.newInstance();
temp.putAll(UtilMisc.toMap("userLogin",userLogin,"listAllConditions",FastList.newInstance(),"listSortFields",UtilMisc.toList("partyId"),
																		"opts",new EntityFindOptions(),"parameters",FastMap.newInstance()));
Map result = DistributorServices.listSalesman(dispatcher.getDispatchContext(),temp);

if(result.containsKey("listIterator"))
	context.listSalesman = result.get("listIterator");
