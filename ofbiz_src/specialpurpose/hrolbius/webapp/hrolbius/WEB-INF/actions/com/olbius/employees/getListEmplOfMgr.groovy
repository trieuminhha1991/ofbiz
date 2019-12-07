import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

//partyId = parameters.partyId;
context.emplList = PartyUtil.getListEmployeeOfManager(delegator, userLogin.partyId);