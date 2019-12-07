package com.olbius.security;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

public class MultiOrgSecurity implements Security{
	
	public static final String module = MultiOrgSecurity.class.getName();

    protected Delegator delegator = null;
	
    public MultiOrgSecurity() {}

    public MultiOrgSecurity(Delegator delegator) {
        this.delegator = delegator;
    }
    
	@Override
	public Delegator getDelegator() {
		// TODO Auto-generated method stub
		return this.delegator;
	}

	@Override
	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	@Override
	public Iterator<GenericValue> findUserLoginSecurityGroupByUserLoginId(String userLoginId) {
		List<GenericValue> collection;
		SystemUser systemUser = new SystemUserImpl();
        try {
        	if(systemUser.isSystemUser(userLoginId)) {
                collection = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId), null, false);
        	}else {
        		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
                collection = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "organizationId", userLogin.getString("lastOrg")), null, false);
        	}
        } catch (GenericEntityException e) {
            // make an empty collection to speed up the case where a userLogin belongs to no security groups, only with no exception of course
            collection = FastList.newInstance();
            Debug.logWarning(e, module);
        }
        // filter each time after cache retreival, ie cache will contain entire list
        collection = EntityUtil.filterByDate(collection, true);
        return collection.iterator();
	}

	@Override
	public boolean securityGroupPermissionExists(String groupId, String permission) {
		GenericValue securityGroupPermissionValue = delegator.makeValue("SecurityGroupPermission",
                UtilMisc.toMap("groupId", groupId, "permissionId", permission));
        try {
            return delegator.findOne(securityGroupPermissionValue.getEntityName(), securityGroupPermissionValue, false) != null;
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return false;
        }
	}

	@Override
	public boolean hasPermission(String permission, HttpSession session) {
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        if (userLogin == null) return false;

        return hasPermission(permission, userLogin);
	}

	@Override
	public boolean hasPermission(String permission, GenericValue userLogin) {
		if (userLogin == null) return false;

        Iterator<GenericValue> iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
        GenericValue userLoginSecurityGroup = null;

        while (iterator.hasNext()) {
            userLoginSecurityGroup = iterator.next();
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), permission)) return true;
        }

        return false;
	}

	@Override
	public boolean hasEntityPermission(String entity, String action, HttpSession session) {
		if (session == null) {
            return false;
        }
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin == null) {
            return false;
        }
        return hasEntityPermission(entity, action, userLogin);
	}

	@Override
	public boolean hasEntityPermission(String entity, String action, GenericValue userLogin) {
		if (userLogin == null) return false;

        // if (Debug.infoOn()) Debug.logInfo("hasEntityPermission: entity=" + entity + ", action=" + action, module);
        Iterator<GenericValue> iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
        GenericValue userLoginSecurityGroup = null;

        while (iterator.hasNext()) {
            userLoginSecurityGroup = iterator.next();

            // if (Debug.infoOn()) Debug.logInfo("hasEntityPermission: userLoginSecurityGroup=" + userLoginSecurityGroup.toString(), module);

            // always try _ADMIN first so that it will cache first, keeping the cache smaller
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), entity + "_ADMIN"))
                return true;
            if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), entity + action))
                return true;
        }

        return false;
	}

	@Override
	public boolean hasRolePermission(String application, String action,
			String primaryKey, String role, HttpSession session) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRolePermission(String application, String action,
			String primaryKey, String role, GenericValue userLogin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRolePermission(String application, String action,
			String primaryKey, List<String> roles, GenericValue userLogin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRolePermission(String application, String action,
			String primaryKey, List<String> roles, HttpSession session) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearUserData(GenericValue userLogin) {
		// TODO Auto-generated method stub
		
	}
	
}
