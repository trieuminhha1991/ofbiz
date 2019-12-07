package com.olbius.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.recruitment.helper.RoleTyle;

public class RoleHelper implements RoleTyle{
	public static String getCurrentRole(String partyId, Delegator delegator) {
		List<String> listRoles = SecurityUtil.getCurrentRoles(partyId, delegator);
		List<RoleType> listRoleTypes = new ArrayList<RoleHelper.RoleType>();
		String actorRoleTypeId = null;
		for(String role : listRoles) {
			RoleType item = null;
			switch (RoleTypeEnum.getRoleType(role)) {
			case CEO_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size());
				listRoleTypes.add(item);
				break;
			case NBD_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 1);
				listRoleTypes.add(item);
				break;
			case CSM_GT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 2);
				listRoleTypes.add(item);
				break;
			case CSM_MT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 2);
				listRoleTypes.add(item);
				break;
			case RSM_GT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 3);
				listRoleTypes.add(item);
				break;
			case RSM_MT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 3);
				listRoleTypes.add(item);
				break;
			case ASM_GT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 4);
				listRoleTypes.add(item);
				break;
			case ASM_MT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 4);
				listRoleTypes.add(item);
				break;
			case SUP_GT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 5);
				listRoleTypes.add(item);
				break;
			case SUP_MT_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 5);
				listRoleTypes.add(item);
				break;
			case HRM_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 1);
				listRoleTypes.add(item);
				break;
			case MANAGER_ROLE:
				item = new RoleHelper().new RoleType(role, listRoles.size() - 2);
				listRoleTypes.add(item);
				break;
			case EMPL_ROLE:
				item = new RoleHelper().new RoleType(role, 0);
				listRoleTypes.add(item);
				break;
			default:
				break;
			}
		}
		
		Collections.sort(listRoleTypes);
		
		for(RoleType role : listRoleTypes) {
			switch (RoleTypeEnum.getRoleType(role.getValue())) {
			case CEO_ROLE:
				actorRoleTypeId = role.getValue();
			case NBD_ROLE:
				actorRoleTypeId = role.getValue();
			case CSM_GT_ROLE:
				actorRoleTypeId = role.getValue();
			case CSM_MT_ROLE:
				actorRoleTypeId = role.getValue();
			case RSM_GT_ROLE:
				actorRoleTypeId = role.getValue();
			case RSM_MT_ROLE:
				actorRoleTypeId = role.getValue();
			case ASM_GT_ROLE:
				actorRoleTypeId = role.getValue();
			case ASM_MT_ROLE:
				actorRoleTypeId = role.getValue();
			case SUP_GT_ROLE:
				actorRoleTypeId = role.getValue();
			case SUP_MT_ROLE:
				actorRoleTypeId = role.getValue();
			case HRM_ROLE:
				actorRoleTypeId = role.getValue();
			case MANAGER_ROLE:
				actorRoleTypeId = role.getValue();
			case EMPL_ROLE:
				actorRoleTypeId = role.getValue();
			default:
				break;
			}
		}
		return actorRoleTypeId;
	}
	public static String getCurrentRole(GenericValue userLogin, Delegator delegator) {
		String actorRoleTypeId = getCurrentRole(userLogin.getString("partyId"), delegator);
		return actorRoleTypeId;
	}
	
	public enum RoleTypeEnum{
		CEO_ROLE("PHOTONGGIAMDOC"),
		EMPL_ROLE("EMPLOYEE"),
		MANAGER_ROLE("MANAGER"),
		HRM_ROLE("HRMADMIN"),
		ORG_ROLE("INTERNAL_ORGANIZATIO"),
		ASM_GT_ROLE("DELYS_ASM_GT"),
		ASM_MT_ROLE("DELYS_ASM_MT"),
		RSM_GT_ROLE("DELYS_RSM_GT"),
		RSM_MT_ROLE("DELYS_RSM_MT"),
		CSM_MT_ROLE("DELYS_CSM_MT"),
		CSM_GT_ROLE("DELYS_CSM_GT"),
		NBD_ROLE("DELYS_NBD"),
		SUP_GT_ROLE("DELYS_SALESSUP_GT"),
		SUP_MT_ROLE("DELYS_SALESSUP_MT"),
		_NA_("_NA_");
		
		private String value;
		
		private RoleTypeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		public static RoleTypeEnum getRoleType(String value) {
			for (RoleTypeEnum item : RoleTypeEnum.values()) {
				if(item.getValue().equals(value)) {
					return item;
				}
			}
			return RoleTypeEnum._NA_;
		}
	}
	
	class RoleType implements Comparable<RoleType>{
		//Parameters
		private String value;
		private int level;
		
		//Getters, setters
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}
		
		public RoleType(String value, int level) {
			super();
			this.value = value;
			this.level = level;
		}

		@Override
		public int compareTo(RoleType o) {
			// TODO Auto-generated method stub
			return Integer.valueOf(this.level).compareTo(Integer.valueOf(o.level));
		}
	}
}
