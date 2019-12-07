/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.entityext.synchronization;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityConfException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.config.model.DelegatorElement;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entityext.EntityGroupUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.xml.sax.SAXException;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

/**
 * Entity Engine Sync Services
 */
public class EntitySyncContext {

    public static final String module = EntitySyncContext.class.getName();

    // set default split to 10 seconds, ie try not to get too much data moving over at once
    public static final long defaultSyncSplitMillis = 10000;

    // default offline split is 30 minutes
    public static final long defaultOfflineSyncSplitMillis = 1800000;

    // default to 5 minutes
    public static final long defaultSyncEndBufferMillis = 300000;

    // default to 2 hours, 120m, 7200s
    public static final long defaultMaxRunningNoUpdateMillis = 7200000;

    public Delegator delegator;
    public LocalDispatcher dispatcher;
    public Map<String, ? extends Object> context;

    public GenericValue userLogin;
    public boolean isOfflineSync = false;

    public String entitySyncId;
    public GenericValue entitySync;

    public String targetServiceName;
    public String targetDelegatorName;

    public Timestamp syncEndStamp;
    public long offlineSyncSplitMillis = defaultOfflineSyncSplitMillis;
    public long syncSplitMillis = defaultSyncSplitMillis;
    public long syncEndBufferMillis = defaultSyncEndBufferMillis;
    public long maxRunningNoUpdateMillis = defaultMaxRunningNoUpdateMillis;

    public Timestamp lastSuccessfulSynchTime;
    public List<ModelEntity> entityModelToUseList;
    public Set<String> entityNameToUseSet;
    public Timestamp currentRunStartTime;
    public Timestamp currentRunEndTime;

    // these values are used to make this more efficient; if we run into an entity that has 0
    //results for a given time block, we will do a query to find the next create/update/remove
    //time for that entity, and also keep track of a global next with the lowest future next value;
    //using these we can skip a lot of queries and speed this up significantly
    public Map<String, Timestamp> nextEntityCreateTxTime = FastMap.newInstance();
    public Map<String, Timestamp> nextEntityUpdateTxTime = FastMap.newInstance();
    public Timestamp nextCreateTxTime = null;
    public Timestamp nextUpdateTxTime = null;
    public Timestamp nextRemoveTxTime = null;

    // this is the other part of the history PK, leave null until we create the history object
    public Timestamp startDate = null;

    long toCreateInserted = 0;
    long toCreateUpdated = 0;
    long toCreateNotUpdated = 0;
    long toStoreInserted = 0;
    long toStoreUpdated = 0;
    long toStoreNotUpdated = 0;
    long toRemoveDeleted = 0;
    long toRemoveAlreadyDeleted = 0;

    long totalRowsExported = 0;
    long totalRowsToCreate = 0;
    long totalRowsToStore = 0;
    long totalRowsToRemove = 0;

    long totalRowsPerSplit = 0;
    long totalStoreCalls = 0;
    long totalSplits = 0;
    long perSplitMinMillis = Long.MAX_VALUE;
    long perSplitMaxMillis = 0;
    long perSplitMinItems = Long.MAX_VALUE;
    long perSplitMaxItems = 0;
    long splitStartTime = 0;
    private String sequencedIdPrefix = null;
    private String posTerminalId = null;
    private String facilityId = null;
    private String productStoreId = null;
	public EntitySyncContext(DispatchContext dctx, Map<String, ? extends Object> context) throws SyncDataErrorException, SyncAbortException {
        this.context = context;
        this.dispatcher = dctx.getDispatcher();

        this.delegator = dctx.getDelegator();
        // what to do with the delegatorName? this is the delegatorName to use in this service...
        String delegatorName = (String) context.get("delegatorName");
        if (UtilValidate.isNotEmpty(delegatorName)) {
            this.delegator = DelegatorFactory.getDelegator(delegatorName);
        }
        String delegatorBaseName = this.delegator.getDelegatorBaseName();
        DelegatorElement delegatorInfo;
		try {
			delegatorInfo = EntityConfigUtil.getDelegator(delegatorBaseName);
			sequencedIdPrefix = delegatorInfo.getSequencedIdPrefix();
		} catch (GenericEntityConfException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        this.userLogin = (GenericValue) context.get("userLogin");

        this.entitySyncId = (String) context.get("entitySyncId");
        Debug.logInfo("Creating EntitySyncContext with entitySyncId=" + entitySyncId, module);

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin(7200);
        } catch (GenericTransactionException e) {
            throw new SyncDataErrorException("Unable to begin JTA transaction", e);
        }

        try {
            this.entitySync = delegator.findOne("EntitySync", false, "entitySyncId", this.entitySyncId);
            if (this.entitySync == null) {
                throw new SyncAbortException("Not running EntitySync [" + entitySyncId + "], no record found with that ID.");
            }

            targetServiceName = entitySync.getString("targetServiceName");
            targetDelegatorName = entitySync.getString("targetDelegatorName");

            // make the last time to sync X minutes before the current time so that if this machines clock is up to that amount of time
            //ahead of another machine writing to the DB it will still work fine and not lose any data
            this.syncEndBufferMillis = getSyncEndBufferMillis(entitySync); // new line
            syncEndStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);
            
            this.offlineSyncSplitMillis = getOfflineSyncSplitMillis(entitySync);
            this.syncSplitMillis = getSyncSplitMillis(entitySync);
            /*this.syncEndBufferMillis = getSyncEndBufferMillis(entitySync);*/
            this.maxRunningNoUpdateMillis = getMaxRunningNoUpdateMillis(entitySync);

            this.lastSuccessfulSynchTime = entitySync.getTimestamp("lastSuccessfulSynchTime");
            this.entityModelToUseList = this.makeEntityModelToUseList();
            this.entityNameToUseSet = this.makeEntityNameToUseSet();

            // set start and end times for the first/current pass
            this.currentRunStartTime = getCurrentRunStartTime(lastSuccessfulSynchTime, entityModelToUseList, delegator);
            this.setCurrentRunEndTime();

            // this is mostly for the pull side... will always be null for at the beginning of a push process, to be filled in later
            this.startDate = (Timestamp) context.get("startDate");
            this.posTerminalId = entitySync.getString("posTerminalId");
            if(UtilValidate.isNotEmpty(posTerminalId)){
            	GenericValue posTerminal = delegator.findOne("PosTerminal", UtilMisc.toMap("posTerminalId", posTerminalId), false);
            	String facilityId = posTerminal.getString("facilityId");
            	if(UtilValidate.isNotEmpty(facilityId)){
            		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
                	if(UtilValidate.isNotEmpty(facility)){
                		this.facilityId = facilityId;
                		String productStoreId = facility.getString("productStoreId");
                		if(UtilValidate.isNotEmpty(productStoreId)){
                			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
                			if(UtilValidate.isNotEmpty(productStore)){
                				this.productStoreId = productStoreId;
                			}
                		}
                	}
            	}
            } else {
            	//    add facilityId to EntitySync by hoanm
            	String facilityId = entitySync.getString("facilityId");
            	if(UtilValidate.isNotEmpty(facilityId)){
            		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
                	if(UtilValidate.isNotEmpty(facility)){
                		this.facilityId = facilityId;
                		String productStoreId = facility.getString("productStoreId");
                		if(UtilValidate.isNotEmpty(productStoreId)){
                			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
                			if(UtilValidate.isNotEmpty(productStore)){
                				this.productStoreId = productStoreId;
                			}
                		}
                	}
            	}
            }
        } catch (GenericEntityException e) {
            try {
                TransactionUtil.rollback(beganTransaction, "Entity Engine error while getting Entity Sync init information", e);
            } catch (GenericTransactionException e2) {
                Debug.logWarning(e2, "Unable to call rollback()", module);
            }
            throw new SyncDataErrorException("Error initializing EntitySync Context", e);
        }

        try {
            TransactionUtil.commit(beganTransaction);
        } catch (GenericTransactionException e) {
            throw new SyncDataErrorException("Unable to commit transaction", e);
        }
    }

    /**
     * To see if it is running check:
     *  - in the running status
     *  - AND when the entitySync was last updated, and if it was more than maxRunningNoUpdateMillis ago, then don't consider it to be running
     * @return boolean representing if the EntitySync should be considered running
     */
    public boolean isEntitySyncRunning() {
        boolean isInRunning = ("ESR_RUNNING".equals(this.entitySync.getString("runStatusId")) ||
                "ESR_PENDING".equals(this.entitySync.getString("runStatusId")));

        if (!isInRunning) {
            return false;
        }

        Timestamp esLastUpdated = this.entitySync.getTimestamp(ModelEntity.STAMP_FIELD);
        if (esLastUpdated == null) {
            // shouldn't ever happen, but just in case; assume is running if we don't know when it was last updated
            return true;
        }
        long esLastUpdatedMillis = esLastUpdated.getTime();
        long nowTimestampMillis = UtilDateTime.nowTimestamp().getTime();
        long timeSinceUpdated = nowTimestampMillis - esLastUpdatedMillis;
        if (timeSinceUpdated > this.maxRunningNoUpdateMillis) {
            // it has been longer than the maxRunningNoUpdateMillis, so don't consider it running
            return false;
        }

        return true;
    }

    public boolean hasMoreTimeToSync() {
        return currentRunStartTime.before(syncEndStamp);
    }

    protected void setCurrentRunEndTime() {
        this.currentRunEndTime = getNextRunEndTime();
    }

    protected Timestamp getNextRunEndTime() {
        long syncSplit = this.isOfflineSync ? offlineSyncSplitMillis : syncSplitMillis;
        Timestamp nextRunEndTime = new Timestamp(this.currentRunStartTime.getTime() + syncSplit);
        if (nextRunEndTime.after(this.syncEndStamp)) {
            nextRunEndTime = this.syncEndStamp;
        }
        return nextRunEndTime;
    }

    public void advanceRunTimes() {
        this.currentRunStartTime = this.currentRunEndTime;
        this.setCurrentRunEndTime();
    }

    public void setSplitStartTime() {
        this.splitStartTime = System.currentTimeMillis();
    }

    protected static long getSyncSplitMillis(GenericValue entitySync) {
        long splitMillis = defaultSyncSplitMillis;
        Long syncSplitMillis = entitySync.getLong("syncSplitMillis");
        if (syncSplitMillis != null) {
            splitMillis = syncSplitMillis.longValue();
        }
        return splitMillis;
    }

    protected static long getOfflineSyncSplitMillis(GenericValue entitySync) {
        long splitMillis = defaultOfflineSyncSplitMillis;
        Long syncSplitMillis = entitySync.getLong("offlineSyncSplitMillis");
        if (syncSplitMillis != null) {
            splitMillis = syncSplitMillis.longValue();
        }
        return splitMillis;
    }

    protected static long getSyncEndBufferMillis(GenericValue entitySync) {
        long syncEndBufferMillis = defaultSyncEndBufferMillis;
        Long syncEndBufferMillisLong = entitySync.getLong("syncEndBufferMillis");
        if (syncEndBufferMillisLong != null) {
            syncEndBufferMillis = syncEndBufferMillisLong.longValue();
        }
        return syncEndBufferMillis;
    }

    protected static long getMaxRunningNoUpdateMillis(GenericValue entitySync) {
        long maxRunningNoUpdateMillis = defaultMaxRunningNoUpdateMillis;
        Long maxRunningNoUpdateMillisLong = entitySync.getLong("maxRunningNoUpdateMillis");
        if (maxRunningNoUpdateMillisLong != null) {
            maxRunningNoUpdateMillis = maxRunningNoUpdateMillisLong.longValue();
        }
        return maxRunningNoUpdateMillis;
    }
    public String getSequencedIdPrefix() {
		return sequencedIdPrefix;
	}
    public String getPosTerminalId() {
		return posTerminalId;
	}
    
	public String getFacilityId() {
		return facilityId;
	}
	

	public String getProductStoreId() {
		return productStoreId;
	}
	public boolean filterSynchronization(GenericValue nextValue,ModelEntity modelEntity, List<String> compareList, List<String> listToAdd){
		boolean flag = false;
    	GenericPK primaryKeys =nextValue.getPrimaryKey();
    	Map<String, Object> primaryKey = primaryKeys.getAllFields();
    	String forPullOnly = entitySync.getString("forPullOnly");
    	if(UtilValidate.isNotEmpty(forPullOnly) && forPullOnly.equals("Y")){
    		if(modelEntity.getEntityName().equalsIgnoreCase("UserLogin")){
    			String partyId = nextValue.getString("partyId");
    			String userLoginId = nextValue.getString("userLoginId");
    			if(compareList.contains(partyId)){
    				listToAdd.add(userLoginId);
    				flag = true;
    				return flag;
    			}
    		}
    		if(UtilValidate.isNotEmpty(primaryKey)){
        		Set<String> fields = primaryKey.keySet();
        		for (String field : fields) {
        			//ignore file that has field is Timestamp filed is primary key
        			if((nextValue.get(field) instanceof String)){
        				String value = (String) nextValue.get(field);
        				if(UtilValidate.isNotEmpty(value)){
        					if(value.startsWith(getSequencedIdPrefix())){
        						flag = true;
        						return flag;
        					}
        				}
        			}
    				
    			}
        	}
    	}
    	
    	return flag;
	}
	public boolean filterSynchronization(GenericValue nextValue ,ModelEntity modelEntity, List<String> compareList){
    	boolean flag = false;
    	GenericPK primaryKeys =nextValue.getPrimaryKey();
    	Map<String, Object> primaryKey = primaryKeys.getAllFields();
    	String forPullOnly = entitySync.getString("forPullOnly");
		//product facility
    	if(UtilValidate.isNotEmpty(forPullOnly) && forPullOnly.equals("Y")){
    		if(modelEntity.getEntityName().equalsIgnoreCase("ProductFacility")){
    			String facilityId = nextValue.getString("facilityId");
    			if(UtilValidate.isNotEmpty(facilityId) && facilityId.equalsIgnoreCase(getFacilityId())){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("ProductStorePromoAppl")){
    			String productStoreId = nextValue.getString("productStoreId");
    			if(UtilValidate.isNotEmpty(productStoreId) && productStoreId.equalsIgnoreCase(getProductStoreId())){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("InventoryItem")){
    			String facilityId = nextValue.getString("facilityId");
    			String inventoryItemId = nextValue.getString("inventoryItemId");
    			if(UtilValidate.isNotEmpty(facilityId) && facilityId.equalsIgnoreCase(getFacilityId())){
    				compareList.add(inventoryItemId);
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("InventoryItemDetail")){
    			String inventoryItemId = nextValue.getString("inventoryItemId");
    			if(compareList.contains(inventoryItemId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("InventoryItemVariance")){
    			String inventoryItemId = nextValue.getString("inventoryItemId");
    			if(compareList.contains(inventoryItemId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		
    		if(modelEntity.getEntityName().equalsIgnoreCase("InventoryTransfer")){
    			String facilityId = nextValue.getString("facilityId");
    			String facilityIdTo = nextValue.getString("facilityIdTo");
    			if( (UtilValidate.isNotEmpty(facilityId) && facilityId.equalsIgnoreCase(getFacilityId())) || (UtilValidate.isNotEmpty(facilityIdTo) && facilityIdTo.equalsIgnoreCase(getFacilityId()))){
    				flag = true;
    				return flag;
    			}
    		}else
    		
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderHeader")){
    			if(!UtilValidate.isNotEmpty(nextValue.getString("orderTypeId").equals("SALES_ORDER"))){
    				String facilityId = nextValue.getString("originFacilityId");
    				String orderId = nextValue.getString("orderId");
    				if(facilityId.equals(getFacilityId())){
    					compareList.add(orderId);
    					flag = true;
    					return flag;
    				}
    				
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderItem")){
    			String orderId = nextValue.getString("orderId");
    			if(compareList.contains(orderId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderAdjustment")){
    			String orderId = nextValue.getString("orderId");
    			if(compareList.contains(orderId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderContactMech")){
    			String orderId = nextValue.getString("orderId");
    			if(compareList.contains(orderId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderRole")){
    			String orderId = nextValue.getString("orderId");
    			if(compareList.contains(orderId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("OrderStatus")){
    			String orderId = nextValue.getString("orderId");
    			if(compareList.contains(orderId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("FacilityParty")){
    			String facilityId = nextValue.getString("facilityId");
    			String partyId = nextValue.getString("partyId");
    			if(UtilValidate.isNotEmpty(facilityId) && facilityId.equalsIgnoreCase(getFacilityId())){
    				flag = true;
    				compareList.add(partyId);
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("UserLoginPasswordHistory")){
    			String userLoginId = nextValue.getString("userLoginId");
    			if(compareList.contains(userLoginId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("UserLoginHistory")){
    			String userLoginId = nextValue.getString("userLoginId");
    			if(compareList.contains(userLoginId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("UserLoginSession")){
    			String userLoginId = nextValue.getString("userLoginId");
    			if(compareList.contains(userLoginId)){
    				flag = true;
    				return flag;
    			}
    		}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("UserLoginSecurityGroup")){
    			return true;
    		}else
    		if(UtilValidate.isNotEmpty(primaryKey)){
        		Set<String> fields = primaryKey.keySet();
        		for (String field : fields) {
        			//ignore file that has field is Timestamp filed is primary key
        			if((nextValue.get(field) instanceof String)){
        				String value = (String) nextValue.get(field);
        				if(UtilValidate.isNotEmpty(value)){
        					if(value.startsWith(getSequencedIdPrefix())){
        						flag = true;
        						return flag;
        					}
        				}
        			}
    				
    			}
        	}else
    		if(modelEntity.getEntityName().equalsIgnoreCase("Requirement")){
    			String facilityId = nextValue.getString("facilityId");
    			String requirementId = nextValue.getString("requirementId");
				if(facilityId.equals(getFacilityId())){
					compareList.add(requirementId);
					flag = true;
					return flag;
				}
    		}else
			if(modelEntity.getEntityName().equalsIgnoreCase("RequirementItem")){
    			String requirementId = nextValue.getString("requirementId");
    			if(compareList.contains(requirementId)){
    				flag = true;
    				return flag;
    			}
    		}else
			if(modelEntity.getEntityName().equalsIgnoreCase("RequirementNote")){
    			String requirementId = nextValue.getString("requirementId");
    			if(compareList.contains(requirementId)){
    				flag = true;
    				return flag;
    			}
    		}else
			if(modelEntity.getEntityName().equalsIgnoreCase("RequirementItemBilling")){
    			String requirementId = nextValue.getString("requirementId");
    			if(compareList.contains(requirementId)){
    				flag = true;
    				return flag;
    			}
    		}else
			if(modelEntity.getEntityName().equalsIgnoreCase("RequirementRole")){
    			String requirementId = nextValue.getString("requirementId");
    			if(compareList.contains(requirementId)){
    				flag = true;
    				return flag;
    			}
    		}else
			if(modelEntity.getEntityName().equalsIgnoreCase("RequirementStatus")){
				String requirementId = nextValue.getString("requirementId");
				if(compareList.contains(requirementId)){
					flag = true;
					return flag;
				}
			}
    	}
		
    	return flag;
    }
    public boolean filterSynchronization(GenericValue nextValue){
    	boolean flag = false;
    	GenericPK primaryKeys =nextValue.getPrimaryKey();
    	
    	Map<String, Object> primaryKey = primaryKeys.getAllFields();
    	
    	if(UtilValidate.isNotEmpty(primaryKey)){
    		Set<String> fields = primaryKey.keySet();
    		for (String field : fields) {
    			//ignore file that has field is Timestamp filed is primary key
    			if((nextValue.get(field) instanceof String)){
    				String value = (String) nextValue.get(field);
    				if(UtilValidate.isNotEmpty(value)){
    					if(value.startsWith(getSequencedIdPrefix())){
    						flag = true;
    						return flag;
    					}
    				}
    			}
				
			}
    	}
    	return flag;
    }
    
    /** create history record, target service should run in own tx */
    public void createInitialHistory() throws SyncDataErrorException, SyncServiceErrorException {
        String errorMsg = "Not running EntitySync [" + entitySyncId + "], could not create EntitySyncHistory";
        try {
            Map<String, Object> initialHistoryRes = dispatcher.runSync("createEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "beginningSynchTime", this.currentRunStartTime, "lastCandidateEndTime", this.currentRunEndTime, "userLogin", userLogin));
            if (ServiceUtil.isError(initialHistoryRes)) {
                throw new SyncDataErrorException(errorMsg, null, null, initialHistoryRes, null);
            }
            this.startDate = (Timestamp) initialHistoryRes.get("startDate");
        } catch (GenericServiceException e) {
            throw new SyncServiceErrorException(errorMsg, e);
        }
    }
    
    private List<GenericValue> getListRelAndInfoEmployee(String partyId, Delegator delegator, List<String> compareList) throws GenericEntityException {
    	List<GenericValue> resultValue = FastList.newInstance();
    	List<String> newPartyIds = FastList.newInstance();
    	
    	newPartyIds.add(partyId);
    	
    	List<GenericValue> listDataRelTmp = FastList.newInstance();
    	List<GenericValue> listDataTmp = null;
    	
    	listDataTmp = delegator.findList("PartyRelationship", EntityCondition.makeCondition(
				UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", partyId),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.NOT_EQUAL, "GROUP_ROLLUP"), 
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.NOT_EQUAL, "EMPLOYMENT")), EntityOperator.AND), null, null, null, false);
    	if (listDataTmp != null && !listDataTmp.isEmpty()) listDataRelTmp.addAll(listDataTmp);
    	
    	// get list relationship between department and employee (getListDeptRelationshipOfEmployee)
    	listDataTmp.clear();
    	listDataTmp = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYMENT"), null, false);
    	if (listDataTmp != null && !listDataTmp.isEmpty()) {
    		listDataRelTmp.addAll(listDataTmp);
    		
    		List<String> deptIds = EntityUtil.getFieldListFromEntityList(listDataTmp, "partyIdFrom", true);
    		List<GenericValue> deptRelsTmp = null;
    		int i = 0;
    		while (i < deptIds.size()) {
    			String deptId = deptIds.get(i);
    			i++;
    			if (!compareList.contains(deptId)) {
    				compareList.add(deptId);
    				newPartyIds.add(deptId);
    				deptRelsTmp = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", deptId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, false);
    				if (deptRelsTmp != null && !deptRelsTmp.isEmpty()) {
    					listDataRelTmp.addAll(deptRelsTmp);
    					for (GenericValue deptRel : deptRelsTmp) {
    						if (deptIds.contains(deptRel.getString("partyIdFrom"))) {
    							continue;
    						}
    						deptIds.add(deptRel.getString("partyIdFrom"));
    					}
    				}
    			}
    		}
    	}
    	
    	// get list security OLBIUS
    	listDataTmp = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "SECURITY_GROUP", "partyRelationshipTypeId", "SECURITY_GROUP_REL"), null, false);
    	if (listDataTmp != null && !listDataTmp.isEmpty()) {
    		listDataRelTmp.addAll(listDataTmp);
    	}

    	if (!listDataRelTmp.isEmpty()) {
    		// party
        	listDataTmp = delegator.findList("Party", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        	}
        	
        	// party role
        	listDataTmp = delegator.findList("PartyRole", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        	}
        	
        	resultValue.addAll(listDataRelTmp);
        	
        	// person
        	listDataTmp = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        	}
        	
        	// party group
        	listDataTmp = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        	}
        	
        	// user login
        	List<String> newUerLoginIds = null;
        	listDataTmp = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        		newUerLoginIds = EntityUtil.getFieldListFromEntityList(listDataTmp, "userLoginId", true);
        	}
        	
        	// OLBIUS party permission
        	listDataTmp = delegator.findList("OlbiusPartyPermission", EntityCondition.makeCondition("partyId", EntityOperator.IN, newPartyIds), null, null, null, false);
        	if (listDataTmp != null && !listDataTmp.isEmpty()) {
        		resultValue.addAll(listDataTmp);
        	}
        	
        	// user login security group
        	if (newUerLoginIds != null && !newUerLoginIds.isEmpty()) {
        		listDataTmp = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition("userLoginId", EntityOperator.IN, newUerLoginIds), null, null, null, false);
        		if (UtilValidate.isNotEmpty(listDataTmp)) {
        			resultValue.addAll(listDataTmp);
        		}
        	}
    	}
    	
    	return resultValue;
    }
    
    private boolean filterSynchronizationNew(GenericValue nextValue, String groupFilterName, List<String> compareList, List<String> compareListNot, List<String> compareListSnd) throws GenericEntityException{
    	return filterSynchronizationNew(nextValue, groupFilterName, compareList, compareListNot, compareListSnd, null);
    }
	private boolean filterSynchronizationNew(GenericValue nextValue, String groupFilterName, List<String> compareList, List<String> compareListNot, List<String> compareListSnd, List<String> compareListSndNot) throws GenericEntityException{
    	boolean flagToFilter = false;
    	
    	String partyId = null;
    	String facilityId = getFacilityId();
    	String productStoreId = getProductStoreId();
    	switch (groupFilterName) {
		case "GROUP_PARTY":
			// case "Party": 
			// case "PartyAttribute": 
			// case "Person": 
			// case "PartyGroup": 
			// case "PartyRole": 
			// case "OlbiusPartyPermission": 
			// case "UserLogin": 
			// case "PartyClassification": 
			// case "PartyContactMech": 
			// case "PartyContactMechPurpose":
    		// check by PARTY_ID => partyIds
			
			// filter synchronization new
			partyId = nextValue.getString("partyId");
			if (compareListNot.contains(partyId)) {
				return false;
			}
			if (compareList.contains(partyId)) {
				flagToFilter = true;
			} else {
				GenericValue facilityParty = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyId), null, false));
				if (facilityParty != null) {
					compareList.add(partyId);
					flagToFilter = true;
				} else {
					GenericValue productStoreRole = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyId), null, false));
    				if (productStoreRole != null) {
    					compareList.add(partyId);
    					flagToFilter = true;
    				}
				}
			}
			if (!flagToFilter) compareListNot.add(partyId);
			break;
		case "GROUP_USERLOGIN": 
			// case "UserLoginSecurityGroup":
			
    		// check by USER_LOGIN_ID => userLoginIds => PARTY_ID => partyIds
			// filter synchronization new
			String userLoginId = nextValue.getString("userLoginId");
			if (compareListSnd.contains(userLoginId)) {
				flagToFilter = true;
			} else {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
				if (userLogin != null) {
					partyId = userLogin.getString("partyId");
					if (compareListNot.contains(partyId)) {
        				return false;
        			}
        			if (compareList.contains(partyId)) {
        				compareListSnd.add(userLoginId);
        				flagToFilter = true;
        			} else {
        				GenericValue facilityParty = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyId), null, false));
        				if (facilityParty != null) {
        					compareList.add(partyId);
        					compareListSnd.add(userLoginId);
        					flagToFilter = true;
        				} else {
        					GenericValue productStoreRole = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyId), null, false));
            				if (productStoreRole != null) {
            					compareList.add(partyId);
            					compareListSnd.add(userLoginId);
            					flagToFilter = true;
            				}
        				}
        			}
        			if (!flagToFilter) compareListNot.add(partyId);
				}
			}
			break;
		case "GROUP_PARTYREL":
			// case "PartyRelationship":
			
    		// check by PARTY_FROM or PARTY_TO
			// filter synchronization new
			String partyIdFrom = nextValue.getString("partyIdFrom");
    		String partyIdTo = nextValue.getString("partyIdTo");
    		if ("SECURITY_GROUP_REL".equals(nextValue.getString("partyRelationshipTypeId"))) {
    			if (compareListNot.contains(partyIdTo)) {
    				return false;
    			}
        		if (compareList.contains(partyIdTo)) {
        			flagToFilter = true;
    			} else {
    				// check party id from
    				GenericValue facilityPartyTo = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyIdTo), null, false));
    				GenericValue productStoreRoleTo = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyIdTo), null, false));
    				if (facilityPartyTo != null || productStoreRoleTo != null) {
    					compareList.add(partyIdTo);
    					flagToFilter = true;
    				} else {
    					if (facilityPartyTo == null && productStoreRoleTo == null) {
    						compareListNot.add(partyIdTo);
    					}
    				}
    			}
    		} else {
    			if (compareListNot.contains(partyIdFrom) || compareListNot.contains(partyIdTo)) {
    				return false;
    			}
        		if (compareList.contains(partyIdFrom) && compareList.contains(partyIdTo)) {
        			flagToFilter = true;
    			} else {
    				// check party id from
    				GenericValue facilityPartyFrom = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyIdFrom), null, false));
    				GenericValue facilityPartyTo = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyIdTo), null, false));
    				GenericValue productStoreRoleFrom = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyIdFrom), null, false));
    				GenericValue productStoreRoleTo = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyIdTo), null, false));
    				if ((facilityPartyFrom != null || productStoreRoleFrom != null) && (facilityPartyTo != null || productStoreRoleTo != null)) {
    					compareList.add(partyIdFrom);
    					compareList.add(partyIdTo);
    					flagToFilter = true;
    				} else {
    					if (facilityPartyFrom == null && productStoreRoleFrom == null) {
    						compareListNot.add(partyIdFrom);
    					}
    					if (facilityPartyTo == null && productStoreRoleTo == null) {
    						compareListNot.add(partyIdTo);
    					}
    				}
    			}
    		}
    		break;
		case "GROUP_CONTACTMECH":
			// case "ContactMech": 
			// case "ContactMechAttribute": 
			// case "PostalAddress": 
			// case "TelecomNumber":
			
    		// check by CONTACT_MECH => contactMechIds
			// filter synchronization new
			String contactMechId = nextValue.getString("contactMechId");
			if (compareList.contains(contactMechId)) {
				flagToFilter = true;
			} else {
    			GenericValue facilityContactMech = EntityUtil.getFirst(delegator.findByAnd("FacilityContactMech", UtilMisc.toMap("facilityId", facilityId, "contactMechId", contactMechId), null, false));
				if (facilityContactMech != null) {
					compareList.add(contactMechId);
					flagToFilter = true;
				} else {
					// find by party
					List<String> partyIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), null, false), "partyId", true);
					if (partyIdsTmp != null && !partyIdsTmp.isEmpty()) {
						for (String partyIdItem : partyIdsTmp) {
							if (compareListSndNot.contains(partyIdItem)) {
								flagToFilter = false;
								break;
							}
							if (compareListSnd.contains(partyIdItem)) {
								flagToFilter = true;
								break;
							} else {
								GenericValue facilityParty = EntityUtil.getFirst(delegator.findByAnd("FacilityParty", UtilMisc.toMap("facilityId", facilityId, "partyId", partyIdItem), null, false));
		        				if (facilityParty != null) {
		        					compareList.add(contactMechId);
		        					flagToFilter = true;
		        					break;
		        				} else {
		        					GenericValue productStoreRole = EntityUtil.getFirst(delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "partyId", partyIdItem), null, false));
		            				if (productStoreRole != null) {
		            					compareList.add(contactMechId);
		            					flagToFilter = true;
		            					break;
		            				}
		        				}
		        				if (!flagToFilter) compareListSndNot.add(partyIdItem);
							}
						}
					}
				}
			}
			if (!flagToFilter) compareListNot.add(contactMechId);
			break;
		case "GROUP_FACILITY":
			// case "Facility": 
			// case "FacilityContactMech": 
			// case "FacilityContactMechPurpose":
    		// case "ProductFacility": 
			// case "ProductStoreFacility": 
			// case "InventoryItem": 
			// case "PosTerminal":
			
    		// check by FACILITY default
			if (facilityId != null && facilityId.equals(nextValue.getString("facilityId"))) {
    			flagToFilter = true;
    		}
			break;
		case "GROUP_INVENTORYITEM":
			// case "InventoryItemDetail":
			// case "InventoryItemLabel":
			// case "InventoryItemLabelAppl":
			// case "InventoryItemLabelType":
			
    		// check by INVENTORY_ITEM => inventoryItemIds
			// filter synchronization new
			String inventoryItemId = nextValue.getString("inventoryItemId");
			if (compareList.contains(inventoryItemId)) {
				flagToFilter = true;
			} else {
				GenericValue inventoryItem = EntityUtil.getFirst(delegator.findByAnd("InventoryItem", UtilMisc.toMap("facilityId", facilityId, "inventoryItemId", inventoryItemId), null, false));
				if (inventoryItem != null) {
					compareList.add(inventoryItem.getString("inventoryItemId"));
					flagToFilter = true;
				}
			}
			break;
		case "GROUP_PRODUCTSTORE":
			// case "ProductStore":
			// case "ProductStoreCatalog":
			// case "ProductStorePaymentSetting":
			// case "ProductStoreShipmentMeth":
			// case "ProductStoreGroupMember":
			// case "ProductStorePromoAppl":
			// case "ProductStoreLoyaltyAppl":
			// case "ProductQuotationStoreAppl":
			// case "LoyaltyPoint":
			// case "ConfigPrintOrder":
			
    		// check by PRODUCT_STORE
			// filter synchronization new
    		if (productStoreId != null && productStoreId.equals(nextValue.getString("productStoreId"))) {
    			flagToFilter = true;
    		}
			break;
		case "GROUP_PRODPROMO":
			// case "ProductPromo":
			// case "ProductPromoAction":
			// case "ProductPromoCategory":
			// case "ProductPromoCode":
			// case "ProductPromoCodeParty":
			// case "ProductPromoCond":
			// case "ProductPromoProduct":
			// case "ProductPromoRule":
			// case "ProductPromoUse":
			// case "ProductPromoStatus":
			
    		// check by PRODUCT_PROMO => productPromoIds
			// filter synchronization new
			String productPromoId = nextValue.getString("productPromoId");
			if (compareList.contains(productPromoId)) {
				flagToFilter = true;
			} else {
				GenericValue productStorePromoAppl = EntityUtil.getFirst(delegator.findByAnd("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", productStoreId, "productPromoId", productPromoId), null, false));
				if (productStorePromoAppl != null) {
					compareList.add(productStorePromoAppl.getString("productPromoId"));
					flagToFilter = true;
				}
			}
			break;
		case "GROUP_PRODQUOTATION":
			// case "ProductQuotation":
			// case "ProductQuotationStatus":
			// case "ProductPriceRule":
			
    		// check by PRODUCT_QUOTATION => productQuotationIds
			// filter synchronization new
			String productQuotationId = nextValue.getString("productQuotationId");
			if (compareList.contains(productQuotationId)) {
				flagToFilter = true;
			} else {
				GenericValue productStoreQuotAppl = EntityUtil.getFirst(delegator.findByAnd("ProductQuotationStoreAppl", UtilMisc.toMap("productStoreId", productStoreId, "productQuotationId", productQuotationId), null, false));
				if (productStoreQuotAppl != null) {
					compareList.add(productStoreQuotAppl.getString("productQuotationId"));
					flagToFilter = true;
				} else {
					List<String> productStoreGroupIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId), null, false), "productStoreGroupId", true);
					if (UtilValidate.isNotEmpty(productStoreGroupIds)) {
						EntityCondition cond = EntityCondition.makeCondition(
								EntityCondition.makeCondition("productQuotationId", productQuotationId),
								EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, productStoreGroupIds));
						GenericValue productStoreGroupQuotAppl = EntityUtil.getFirst(delegator.findList("ProductQuotationStoreGroupAppl", EntityCondition.makeCondition(cond), null, null, null, false));
						if (productStoreGroupQuotAppl != null) {
							compareList.add(productQuotationId);
							flagToFilter = true;
						}
					}
				}
			}
			break;
		case "GROUP_PRODUCTSTOREGROUP":
			// case "ProductStoreGroup":
			// case "ProductQuotationStoreGroupAppl":
			
			// check by GROUP_PRODUCTSTOREGROUP => _
			// filter synchronization new
			String productStoreGroupId = nextValue.getString("productStoreGroupId");
			GenericValue productStoreGroupMember = EntityUtil.getFirst(delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId, "productStoreGroupId", productStoreGroupId), null, false));
			if (UtilValidate.isNotEmpty(productStoreGroupMember)) {
				flagToFilter = true;
			}
			break;
		case "GROUP_PRODPRICERULE":
			// case "ProductPriceAction":
			// case "ProductPriceActionType":
			// case "ProductPriceChange":
			// case "ProductPriceCond":
			
    		// check by PRODUCT_PRICE_RULE => productPriceRuleIds => PRODUCT_QUOTATION => productQuotationIds
			// filter synchronization new
			String productPriceRuleId = nextValue.getString("productPriceRuleId");
			if (compareList.contains(productPriceRuleId)) {
				flagToFilter = true;
			} else {
				GenericValue productPriceRule = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), false);
				if (productPriceRule != null) {
					String productQuotationIdTmp = productPriceRule.getString("productQuotationId");
					if (compareListSnd.contains(productQuotationIdTmp)) {
						compareList.add(productPriceRuleId);
						flagToFilter = true;
					} else {
						GenericValue productStorePromoAppl = EntityUtil.getFirst(delegator.findByAnd("ProductQuotationStoreAppl", UtilMisc.toMap("productStoreId", productStoreId, "productQuotationId", productQuotationIdTmp), null, false));
        				if (productStorePromoAppl != null) {
        					compareList.add(productPriceRuleId);
        					compareListSnd.add(productQuotationIdTmp);
        					flagToFilter = true;
        				} else {
        					List<String> productStoreGroupIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId), null, false), "productStoreGroupId", true);
        					if (UtilValidate.isNotEmpty(productStoreGroupIds)) {
        						EntityCondition cond = EntityCondition.makeCondition(
        								EntityCondition.makeCondition("productQuotationId", productQuotationIdTmp),
        								EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, productStoreGroupIds));
        						GenericValue productStoreGroupQuotAppl = EntityUtil.getFirst(delegator.findList("ProductQuotationStoreGroupAppl", EntityCondition.makeCondition(cond), null, null, null, false));
        						if (productStoreGroupQuotAppl != null) {
        							compareList.add(productPriceRuleId);
        							compareListSnd.add(productQuotationIdTmp);
        							flagToFilter = true;
        						}
        					}
        				}
					}
					
				}
			}
			break;
		case "GROUP_LOYALTY":
			// case "Loyalty":
			// case "LoyaltyAction":
			// case "LoyaltyCategory":
			// case "LoyaltyCondition":
			// case "LoyaltyProduct":
			// case "LoyaltyRoleTypeAppl":
			// case "LoyaltyRule":
			// case "LoyaltyStatus":
    		// case "LoyaltyUse":
			
    		// check by LOYALTY_ID => loyaltyIds
			// filter synchronization new
			String loyaltyId = nextValue.getString("loyaltyId");
			if (compareList.contains(loyaltyId)) {
				flagToFilter = true;
			} else {
				GenericValue productStoreLoyaltyAppl = EntityUtil.getFirst(delegator.findByAnd("ProductStoreLoyaltyAppl", UtilMisc.toMap("productStoreId", productStoreId, "loyaltyId", loyaltyId), null, false));
				if (productStoreLoyaltyAppl != null) {
					compareList.add(loyaltyId);
					flagToFilter = true;
				}
			}
			break;
		case "GROUP_LOYALTYPOINT":
			// case "LoyaltyPointDetail":
			
    		// check by LOYALTY_POINT_ID => loyaltyPointIds
			// filter synchronization new
			String loyaltyPointId = nextValue.getString("loyaltyPointId");
			if (compareList.contains(loyaltyPointId)) {
				flagToFilter = true;
			} else {
				GenericValue loyaltyPoint = EntityUtil.getFirst(delegator.findByAnd("LoyaltyPoint", UtilMisc.toMap("productStoreId", productStoreId, "loyaltyPointId", loyaltyPointId), null, false));
				if (loyaltyPoint != null) {
					compareList.add(loyaltyPointId);
					flagToFilter = true;
				}
			}
			break;
		default:
			break;
		}
    	return flagToFilter;
    }

    public ArrayList<GenericValue> assembleValuesToCreate() throws SyncDataErrorException {
        // first grab all values inserted in the date range, then get the updates (leaving out all values inserted in the data range)
        ArrayList<GenericValue> valuesToCreate = new ArrayList<GenericValue>(); // make it an ArrayList to easily merge in sorted lists

        if (this.nextCreateTxTime != null && (this.nextCreateTxTime.equals(currentRunEndTime) || this.nextCreateTxTime.after(currentRunEndTime))) {
            // this means that for all entities in this pack we found on the last pass that there would be nothing for this one, so just return nothing...
            return valuesToCreate;
        }
        
        //Debug.logInfo("Getting values to create; currentRunStartTime=" + currentRunStartTime + ", currentRunEndTime=" + currentRunEndTime, module);

        int entitiesSkippedForKnownNext = 0;
        // iterate through entities, get all records with tx stamp in the current time range, put all in a single list
        String forPullOnly = entitySync.getString("forPullOnly");
        if(UtilValidate.isNotEmpty(forPullOnly) && forPullOnly.equalsIgnoreCase("Y")){
        	/*List<String> orderIdList = FastList.newInstance();
        	List<String> inventoryItemIdList = FastList.newInstance();
        	List<String> partyIdList = FastList.newInstance();
        	List<String> userLoginIdList = FastList.newInstance();
        	List<String> requirements = FastList.newInstance();*/
        	// String facilityId = getFacilityId();
        	// String productStoreId = null;
        	List<String> partyIds = FastList.newInstance();
        	List<String> contactMechIds = FastList.newInstance();
        	List<String> inventoryItemIds = FastList.newInstance();
        	List<String> productPromoIds = FastList.newInstance();
        	List<String> productQuotationIds = FastList.newInstance();
        	List<String> productPriceRuleIds = FastList.newInstance();
        	List<String> loyaltyIds = FastList.newInstance();
        	List<String> loyaltyPointIds = FastList.newInstance();
        	List<String> userLoginIds = FastList.newInstance();
        	List<String> partyIdsRel = FastList.newInstance();
        	List<String> partyIdsNot = FastList.newInstance();
        	List<String> contactMechIdsNot = FastList.newInstance();
        	/*try {
        		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
        		if (facility != null) {
        			productStoreId = facility.getString("productStoreId");
        		}
			} catch (GenericEntityException e) {
				throw new SyncDataErrorException("Caught runtime error while getting values of facility", e);
			}*/
        	for (ModelEntity modelEntity: entityModelToUseList) {
                int insertBefore = 0;

                // first test to see if we know that there are no records for this entity in this time period...
                Timestamp knownNextCreateTime = this.nextEntityCreateTxTime.get(modelEntity.getEntityName());
                if (knownNextCreateTime != null && (knownNextCreateTime.equals(currentRunEndTime) || knownNextCreateTime.after(currentRunEndTime))) {
                    //Debug.logInfo("In assembleValuesToCreate found knownNextCreateTime [" + knownNextCreateTime + "] after currentRunEndTime [" + currentRunEndTime + "], so skipping time per period for entity [" + modelEntity.getEntityName() + "]", module);
                    entitiesSkippedForKnownNext++;
                    continue;
                }

                boolean beganTransaction = false;
                try {
                    beganTransaction = TransactionUtil.begin(7200);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Unable to begin JTA transaction", e);
                }

                try {
                    // get the values created within the current time range
                    EntityCondition findValCondition = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                    EntityListIterator eli = delegator.find(modelEntity.getEntityName(), findValCondition, null, null, UtilMisc.toList(ModelEntity.CREATE_STAMP_TX_FIELD, ModelEntity.CREATE_STAMP_FIELD), null);
                    
                    GenericValue nextValue = null;
                    long valuesPerEntity = 0;
                    String entityName = modelEntity.getEntityName();
                    
                	// TODOCHANGE NEW CODE add new filter for synchronization
            		switch (entityName) {
                	case "Party":
                	case "PartyAttribute":
                	case "Person":
                	case "PartyGroup":
                	case "PartyRole":
                	case "OlbiusPartyPermission":
                	case "UserLogin":
                	case "PartyClassification":
                	case "PartyContactMech":
                	case "PartyContactMechPurpose":
                		// check by PARTY_ID => partyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PARTY", partyIds, partyIdsNot, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "UserLoginSecurityGroup":
                		// check by USER_LOGIN_ID => userLoginIds => PARTY_ID => partyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_USERLOGIN", partyIds, partyIdsNot, userLoginIds);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "PartyRelationship":
                		// check by PARTY_FROM or PARTY_TO
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PARTYREL", partyIds, partyIdsNot, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ContactMech":
                	case "ContactMechAttribute":
                	case "PostalAddress":
                	case "TelecomNumber":
                		// check by CONTACT_MECH => contactMechIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_CONTACTMECH", contactMechIds, contactMechIdsNot, partyIds, partyIdsNot);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "Facility":
                	case "FacilityContactMech":
                	case "FacilityContactMechPurpose":
                	case "ProductFacility":
                	case "ProductStoreFacility":
                	case "PosTerminal":
                		// check by FACILITY default
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                    		boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_FACILITY", null, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "InventoryItem":
                		// check by FACILITY default
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                    		boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_FACILITY", null, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                nextValue.set("quantityOnHandTotal", BigDecimal.ZERO); // TODOCHANGE update quantity_on_hand_total, available_to_promise_total to ZERO
                                nextValue.set("availableToPromiseTotal", BigDecimal.ZERO); // TODOCHANGE update quantity_on_hand_total, available_to_promise_total to ZERO
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "FacilityParty":
                		// check by FACILITY default
                		while ((nextValue = eli.next()) != null) {
                			List<GenericValue> listPartyRelsExt = FastList.newInstance();
                			boolean flagToFilter = false;
                			// filter synchronization new
                    		if (facilityId != null && facilityId.equals(nextValue.getString("facilityId"))) {
                    			String partyId = nextValue.getString("partyId");
                    			flagToFilter = true;
                    			partyIds.add(partyId);
                    			
                    			if (!partyIdsRel.contains(partyId)) {
                    				partyIdsRel.add(partyId);
                    				List<GenericValue> listOtherDataTmp = getListRelAndInfoEmployee(partyId, delegator, partyIdsRel);
                        			if (listOtherDataTmp != null && !listOtherDataTmp.isEmpty()) {
                        				listPartyRelsExt.addAll(listOtherDataTmp);
                        			}
                    			}
                    		}
                			
                    		if (!listPartyRelsExt.isEmpty()) {
                        		for (GenericValue partyRel : listPartyRelsExt) {
                        			if (partyRel != null) {
                        				while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                            insertBefore++;
                                        }
                        				while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                            insertBefore++;
                                        }
                            			valuesToCreate.add(insertBefore, partyRel);
                                        valuesPerEntity++;
                        			}
                        		}
                        	}
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "InventoryItemDetail":
                	case "InventoryItemLabel":
                	case "InventoryItemLabelAppl":
                	case "InventoryItemLabelType":
                		// check by INVENTORY_ITEM => inventoryItemIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_INVENTORYITEM", inventoryItemIds, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductStore":
                	case "ProductStoreCatalog":
                	case "ProductStorePaymentSetting":
                	case "ProductStoreShipmentMeth":
                	case "ProductStoreGroupMember":
                	case "ProductStorePromoAppl":
                	case "ProductStoreLoyaltyAppl":
                	case "ProductQuotationStoreAppl":
                	case "LoyaltyPoint":
                	case "ConfigPrintOrder":
                		// check by PRODUCT_STORE
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTORE", null, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductStoreRole":
                		// check by PRODUCT_STORE
                		while ((nextValue = eli.next()) != null) {
                			List<GenericValue> listPartyRelsExt = FastList.newInstance();
                			boolean flagToFilter = false;
                			// filter synchronization new
                    		if (productStoreId != null && productStoreId.equals(nextValue.getString("productStoreId"))) {
                    			String partyId = nextValue.getString("partyId");
                    			flagToFilter = true;
                    			partyIds.add(partyId);
                    			
                    			if (!partyIdsRel.contains(partyId)) {
                    				partyIdsRel.add(partyId);
                    				List<GenericValue> listOtherDataTmp = getListRelAndInfoEmployee(partyId, delegator, partyIdsRel);
	                    			if (listOtherDataTmp != null && !listOtherDataTmp.isEmpty()) {
	                    				listPartyRelsExt.addAll(listOtherDataTmp);
	                    			}
                    			}
                    		}
                			
                    		if (!listPartyRelsExt.isEmpty()) {
                        		for (GenericValue partyRel : listPartyRelsExt) {
                        			if (partyRel != null) {
                        				while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                            insertBefore++;
                                        }
                        				while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                            insertBefore++;
                                        }
                            			valuesToCreate.add(insertBefore, partyRel);
                                        valuesPerEntity++;
                        			}
                        		}
                        	}
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductStoreGroup":
                	case "ProductQuotationStoreGroupAppl":
                		// check by PRODUCT_STORE_GROUP_ID
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTOREGROUP", null, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductPromo":
                	case "ProductPromoAction":
                	case "ProductPromoCategory":
                	case "ProductPromoCode":
                	case "ProductPromoCodeParty":
                	case "ProductPromoCond":
                	case "ProductPromoProduct":
                	case "ProductPromoRule":
                	case "ProductPromoUse":
                	case "ProductPromoStatus":
                		// check by PRODUCT_PROMO => productPromoIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODPROMO", productPromoIds, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductQuotation":
                	case "ProductQuotationStatus":
                	case "ProductPriceRule":
                		// check by PRODUCT_QUOTATION => productQuotationIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODQUOTATION", productQuotationIds, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductPriceAction":
                	case "ProductPriceActionType":
                	case "ProductPriceChange":
                	case "ProductPriceCond":
                		// check by PRODUCT_PRICE_RULE => productPriceRuleIds => PRODUCT_QUOTATION => productQuotationIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODPRICERULE", productPriceRuleIds, null, productQuotationIds);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "Loyalty":
                	case "LoyaltyAction":
                	case "LoyaltyCategory":
                	case "LoyaltyCondition":
                	case "LoyaltyProduct":
                	case "LoyaltyRoleTypeAppl":
                	case "LoyaltyRule":
                	case "LoyaltyStatus":
                	case "LoyaltyUse":
                		// check by LOYALTY_ID => loyaltyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_LOYALTY", loyaltyIds, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "LoyaltyPointDetail":
                		// check by LOYALTY_POINT_ID => loyaltyPointIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_LOYALTYPOINT", loyaltyPointIds, null, null);
                			
                			// sort by the tx stamp and then the record stamp
                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        	if(flagToFilter){
                        		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToCreate.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
					default:
						while ((nextValue = eli.next()) != null) {
                			//boolean flagToFilter = false;
                			// filter synchronization new
                			//flagToFilter = true;
                			
                			//if(flagToFilter){
								// sort by the tx stamp and then the record stamp
	                            // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
	                        	while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
	                                insertBefore++;
	                            }
	                            while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
	                                insertBefore++;
	                            }
	                            valuesToCreate.add(insertBefore, nextValue);
	                            valuesPerEntity++;
                        	//}
                        }
						break;
					}
                	/*case "TransferHeader":
                	case "TransferItem":
                		break;*/
                    eli.close();
                    
                    /* HOANDV code old
	                while ((nextValue = eli.next()) != null) {
	                	while ((nextValue = eli.next()) != null) {
	                	//hoandv add new filter for synchronization
	                	String entityName = modelEntity.getEntityName();
	                	boolean flagToFilter = false;
	                	switch (entityName) {
						case "ProductFacility":
							flagToFilter = filterSynchronization(nextValue, modelEntity, null);
							break;
						case "ProductStorePromoAppl":
							flagToFilter = filterSynchronization(nextValue, modelEntity, null);
							break;
						case "InventoryItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "InventoryItemDetail":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						NOT_USE ========================================
						case "InventoryItemVariance":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "InventoryTransfer":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "OrderHeader":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderAdjustment":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderStatus":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderContactMech":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderRole":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
							========================================
						case "FacilityParty":
							flagToFilter = filterSynchronization(nextValue, modelEntity, partyIdList);
							break;
						case "UserLogin":
							flagToFilter = filterSynchronization(nextValue, modelEntity, partyIdList, userLoginIdList);
							break;
						NOT_USE ========================================
						case "UserLoginPasswordHistory":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "UserLoginHistory":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "UserLoginSession":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
							========================================
						case "UserLoginSecurityGroup":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						NOT_USE ========================================
						case "Requirement":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementNote":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementItemBilling":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementRole":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementStatus":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
							========================================
						default:
							flagToFilter = true;
							break;
						}
	                	
	            		// sort by the tx stamp and then the record stamp
	                    // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
	                	if(flagToFilter){
	                		while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
	                            insertBefore++;
	                        }
	                        while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
	                            insertBefore++;
	                        }
	                        valuesToCreate.add(insertBefore, nextValue);
	                        valuesPerEntity++;
	                	}
                	}
                	eli.close(); */
                
                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);

                    // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                    if (valuesPerEntity == 0) {
                        Timestamp startCheckStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);

                        EntityListIterator eliNext = null;
                        GenericValue firstVal = null;
                        try {
                        	 EntityFindOptions findNextOpts = new EntityFindOptions();
                             findNextOpts.setLimit(1);
                             findNextOpts.setMaxRows(1);
                             EntityCondition findNextCondition = EntityCondition.makeCondition(
                                     EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                     EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime));
                             eliNext = delegator.find(modelEntity.getEntityName(), findNextCondition, null, null, UtilMisc.toList(ModelEntity.CREATE_STAMP_TX_FIELD), findNextOpts);
                             // get the first element and it's tx time value...
                             if (eliNext != null) firstVal = eliNext.next();
                        } catch (Exception e) {
                        	try {
                                TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleValuesToCreate", e);
                            } catch (GenericTransactionException e2) {
                                Debug.logWarning(e2, "Unable to call rollback()", module);
                            }
                            throw new SyncDataErrorException("Error getting values to next timestamp create from the datasource", e);
                        } finally {
                        	if (eliNext != null) eliNext.close();
						}
                        
                        Timestamp nextTxTime;
                        if (firstVal != null) {
                            nextTxTime = firstVal.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD);
                        } else {
                            // no results? well, then it's safe to say that up to the pre-querytime (minus the buffer, as usual) we are okay
                            nextTxTime = startCheckStamp;
                        }
                        if (this.nextCreateTxTime == null || nextTxTime.before(this.nextCreateTxTime)) {
                            this.nextCreateTxTime = nextTxTime;
                            Debug.logInfo("EntitySync: Set nextCreateTxTime to [" + nextTxTime + "]", module);
                        }
                        Timestamp curEntityNextTxTime = this.nextEntityCreateTxTime.get(modelEntity.getEntityName());
                        if (curEntityNextTxTime == null || nextTxTime.before(curEntityNextTxTime)) {
                            this.nextEntityCreateTxTime.put(modelEntity.getEntityName(), nextTxTime);
                            Debug.logInfo("EntitySync: Set nextEntityCreateTxTime to [" + nextTxTime + "] for the entity [" + modelEntity.getEntityName() + "]", module);
                        }
                    }
                } catch (GenericEntityException e) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleValuesToCreate", e);

                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Error getting values to create from the datasource", e);
                } catch (Throwable t) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Throwable error in assembleValuesToCreate", t);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Caught runtime error while getting values to create", t);
                }

                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Commit transaction failed", e);
                }
            }
        }else{
        	for (ModelEntity modelEntity: entityModelToUseList) {
                int insertBefore = 0;

                // first test to see if we know that there are no records for this entity in this time period...
                Timestamp knownNextCreateTime = this.nextEntityCreateTxTime.get(modelEntity.getEntityName());
                if (knownNextCreateTime != null && (knownNextCreateTime.equals(currentRunEndTime) || knownNextCreateTime.after(currentRunEndTime))) {
                    //Debug.logInfo("In assembleValuesToCreate found knownNextCreateTime [" + knownNextCreateTime + "] after currentRunEndTime [" + currentRunEndTime + "], so skipping time per period for entity [" + modelEntity.getEntityName() + "]", module);
                    entitiesSkippedForKnownNext++;
                    continue;
                }

                boolean beganTransaction = false;
                try {
                    beganTransaction = TransactionUtil.begin(7200);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Unable to begin JTA transaction", e);
                }

                try {
                    // get the values created within the current time range
                    EntityCondition findValCondition = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                    EntityListIterator eli = delegator.find(modelEntity.getEntityName(), findValCondition, null, null, UtilMisc.toList(ModelEntity.CREATE_STAMP_TX_FIELD, ModelEntity.CREATE_STAMP_FIELD), null);
                    GenericValue nextValue = null;
                    long valuesPerEntity = 0;
                    while ((nextValue = eli.next()) != null) {
                    	
                		// sort by the tx stamp and then the record stamp
                        // find first value in valuesToCreate list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                            insertBefore++;
                        }
                        while (insertBefore < valuesToCreate.size() && valuesToCreate.get(insertBefore).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                            insertBefore++;
                        }
                        if ("InventoryItem".equals(modelEntity.getEntityName())){
                        	if (UtilValidate.isNotEmpty(nextValue.get("quantityOnHandTotal"))){
                        		nextValue.put("quantityOnHandTotal", BigDecimal.ZERO);
                        	}
                        	if (UtilValidate.isNotEmpty(nextValue.get("availableToPromiseTotal"))){
                        		nextValue.put("availableToPromiseTotal", BigDecimal.ZERO);
                        	}
                        }
                        valuesToCreate.add(insertBefore, nextValue);
                        valuesPerEntity++;
                        
                    }
                    eli.close();
                
                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);

                    // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                    if (valuesPerEntity == 0) {
                        Timestamp startCheckStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);

                        EntityCondition findNextCondition = EntityCondition.makeCondition(
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime));
                        EntityListIterator eliNext = delegator.find(modelEntity.getEntityName(), findNextCondition, null, null, UtilMisc.toList(ModelEntity.CREATE_STAMP_TX_FIELD), null);
                        // get the first element and it's tx time value...
                        GenericValue firstVal = eliNext.next();
                        eliNext.close();
                        Timestamp nextTxTime;
                        if (firstVal != null) {
                            nextTxTime = firstVal.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD);
                        } else {
                            // no results? well, then it's safe to say that up to the pre-querytime (minus the buffer, as usual) we are okay
                            nextTxTime = startCheckStamp;
                        }
                        if (this.nextCreateTxTime == null || nextTxTime.before(this.nextCreateTxTime)) {
                            this.nextCreateTxTime = nextTxTime;
                            Debug.logInfo("EntitySync: Set nextCreateTxTime to [" + nextTxTime + "]", module);
                        }
                        Timestamp curEntityNextTxTime = this.nextEntityCreateTxTime.get(modelEntity.getEntityName());
                        if (curEntityNextTxTime == null || nextTxTime.before(curEntityNextTxTime)) {
                            this.nextEntityCreateTxTime.put(modelEntity.getEntityName(), nextTxTime);
                            Debug.logInfo("EntitySync: Set nextEntityCreateTxTime to [" + nextTxTime + "] for the entity [" + modelEntity.getEntityName() + "]", module);
                        }
                        // Find the oldest time
                        for (String key : this.nextEntityCreateTxTime.keySet()) {
                        	Timestamp tmpTT = this.nextEntityCreateTxTime.get(key);
                            if(tmpTT != null && this.nextCreateTxTime != null && tmpTT.before(this.nextCreateTxTime)){
                            	this.nextCreateTxTime = tmpTT;
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleValuesToCreate", e);

                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Error getting values to create from the datasource", e);
                } catch (Throwable t) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Throwable error in assembleValuesToCreate", t);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Caught runtime error while getting values to create", t);
                }

                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Commit transaction failed", e);
                }
            }
        }
        

        if (entitiesSkippedForKnownNext > 0) {
            if (Debug.infoOn()) Debug.logInfo("In assembleValuesToCreate skipped [" + entitiesSkippedForKnownNext + "/" + entityModelToUseList + "] entities for the time period ending at [" + currentRunEndTime + "] because of next known create times", module);
        }

        // TEST SECTION: leave false for normal use
        boolean logValues = false;
        if (logValues && valuesToCreate.size() > 0) {
            StringBuilder toCreateInfo = new StringBuilder();
            for (GenericValue valueToCreate: valuesToCreate) {
                toCreateInfo.append("\n-->[");
                toCreateInfo.append(valueToCreate.get(ModelEntity.CREATE_STAMP_TX_FIELD));
                toCreateInfo.append(":");
                toCreateInfo.append(valueToCreate.get(ModelEntity.CREATE_STAMP_FIELD));
                toCreateInfo.append("] ");
                toCreateInfo.append(valueToCreate.getPrimaryKey());
            }
            Debug.logInfo(toCreateInfo.toString(), module);
        }
        
        // As the this.nextCreateTxTime calculation is only based on entities without values to create, if there at least one value to create returned
        // this calculation is false, so it needs to be nullified
        if (valuesToCreate.size() > 0) {
            this.nextCreateTxTime = null;
        }
        return valuesToCreate;
    }

    public ArrayList<GenericValue> assembleValuesToStore() throws SyncDataErrorException {
        // simulate two ordered lists and merge them on-the-fly for faster combined sorting
        ArrayList<GenericValue> valuesToStore = new ArrayList<GenericValue>(); // make it an ArrayList to easily merge in sorted lists

        if (this.nextUpdateTxTime != null && (this.nextUpdateTxTime.equals(currentRunEndTime) || this.nextUpdateTxTime.after(currentRunEndTime))) {
            // this means that for all entities in this pack we found on the last pass that there would be nothing for this one, so just return nothing...
            return valuesToStore;
        }

        // Debug.logInfo("Getting values to store; currentRunStartTime=" + currentRunStartTime + ", currentRunEndTime=" + currentRunEndTime, module);

        int entitiesSkippedForKnownNext = 0;
        String forPullOnly = entitySync.getString("forPullOnly");
        if(UtilValidate.isNotEmpty(forPullOnly) && forPullOnly.equalsIgnoreCase("Y")){
        	/*List<String> orderIdList = FastList.newInstance();
        	List<String> inventoryItemIdList = FastList.newInstance();
        	List<String> partyIdList = FastList.newInstance();
        	List<String> userLoginIdList = FastList.newInstance();
        	List<String> requirements = FastList.newInstance();*/
        	// String facilityId = getFacilityId();
        	// String productStoreId = null;
        	List<String> partyIds = FastList.newInstance();
        	List<String> contactMechIds = FastList.newInstance();
        	List<String> inventoryItemIds = FastList.newInstance();
        	List<String> productPromoIds = FastList.newInstance();
        	List<String> productQuotationIds = FastList.newInstance();
        	List<String> productPriceRuleIds = FastList.newInstance();
        	List<String> loyaltyIds = FastList.newInstance();
        	List<String> loyaltyPointIds = FastList.newInstance();
        	List<String> userLoginIds = FastList.newInstance();
        	List<String> partyIdsNot = FastList.newInstance();
        	List<String> contactMechIdsNot = FastList.newInstance();
        	/*try {
        		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
        		if (facility != null) {
        			productStoreId = facility.getString("productStoreId");
        		}
			} catch (GenericEntityException e) {
				throw new SyncDataErrorException("Caught runtime error while getting values of facility", e);
			}*/
        	// iterate through entities, get all records with tx stamp in the current time range, put all in a single list
            for (ModelEntity modelEntity: entityModelToUseList) {
                int insertBefore = 0;
                
                // first test to see if we know that there are no records for this entity in this time period...
                Timestamp knownNextUpdateTime = this.nextEntityUpdateTxTime.get(modelEntity.getEntityName());
                if (knownNextUpdateTime != null && (knownNextUpdateTime.equals(currentRunEndTime) || knownNextUpdateTime.after(currentRunEndTime))) {
                    entitiesSkippedForKnownNext++;
                    continue;
                }

                boolean beganTransaction = false;
                try {
                    beganTransaction = TransactionUtil.begin(7200);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Unable to begin JTA transaction", e);
                }

                try {
                    // get all values that were updated, but NOT created in the current time range; if no info on created stamp, that's okay we'll include it here because it won't have been included in the valuesToCreate list
                    EntityCondition createdBeforeStartCond = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.EQUALS, null),
                            EntityOperator.OR,
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunStartTime));
                    EntityCondition findValCondition = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime),
                            createdBeforeStartCond);
                    EntityListIterator eli = delegator.find(modelEntity.getEntityName(), findValCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD, ModelEntity.STAMP_FIELD), null);
                    GenericValue nextValue = null;
                    long valuesPerEntity = 0;
                    String entityName = modelEntity.getEntityName();
                    
                    // TODOCHANGE NEW CODE add new filter for synchronization
                	switch (entityName) {
                	case "Party":
                	case "PartyAttribute":
                	case "Person":
                	case "PartyGroup":
                	case "PartyRole":
                	case "OlbiusPartyPermission":
                	case "UserLogin":
                	case "PartyClassification":
                	case "PartyContactMech":
                	case "PartyContactMechPurpose":
                		// check by PARTY_ID => partyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PARTY", partyIds, partyIdsNot, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "UserLoginSecurityGroup":
                		// check by USER_LOGIN_ID => userLoginIds => PARTY_ID => partyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_USERLOGIN", partyIds, partyIdsNot, userLoginIds);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "PartyRelationship":
                		// check by PARTY_FROM or PARTY_TO
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PARTYREL", partyIds, partyIdsNot, null);
                			
                    		if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ContactMech":
                	case "ContactMechAttribute":
                	case "PostalAddress":
                	case "TelecomNumber":
                		// check by CONTACT_MECH => contactMechIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_CONTACTMECH", contactMechIds, contactMechIdsNot, partyIds, partyIdsNot);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "Facility":
                	case "FacilityParty":
                	case "FacilityContactMech":
                	case "FacilityContactMechPurpose":
                	case "ProductFacility":
                	case "ProductStoreFacility":
                	case "InventoryItem":
                		// check by FACILITY default
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_FACILITY", null, null, null);
                			
                    		if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "InventoryItemDetail":
                	case "InventoryItemLabel":
                	case "InventoryItemLabelAppl":
                	case "InventoryItemLabelType":
                		// check by INVENTORY_ITEM => inventoryItemIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_INVENTORYITEM", inventoryItemIds, null, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductStore":
                	case "ProductStoreRole":
                	case "ProductStoreCatalog":
                	case "ProductStorePaymentSetting":
                	case "ProductStoreShipmentMeth":
                	case "ProductStoreGroupMember":
                	case "ProductStorePromoAppl":
                	case "ProductStoreLoyaltyAppl":
                	case "ProductQuotationStoreAppl":
                	case "LoyaltyPoint":
                	case "ConfigPrintOrder":
                		// check by PRODUCT_STORE
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTORE", null, null, null);
                			
                    		if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductStoreGroup":
                	case "ProductQuotationStoreGroupAppl":
                		// check by PRODUCT_STORE_GROUP
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTOREGROUP", null, null, null);
                			
                    		if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductPromo":
                	case "ProductPromoAction":
                	case "ProductPromoCategory":
                	case "ProductPromoCode":
                	case "ProductPromoCodeParty":
                	case "ProductPromoCond":
                	case "ProductPromoProduct":
                	case "ProductPromoRule":
                	case "ProductPromoUse":
                	case "ProductPromoStatus":
                		// check by PRODUCT_PROMO => productPromoIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODPROMO", productPromoIds, null, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductQuotation":
                	case "ProductQuotationStatus":
                	case "ProductPriceRule":
                		// check by PRODUCT_QUOTATION => productQuotationIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODQUOTATION", productQuotationIds, null, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "ProductPriceAction":
                	case "ProductPriceActionType":
                	case "ProductPriceChange":
                	case "ProductPriceCond":
                		// check by PRODUCT_PRICE_RULE => productPriceRuleIds => PRODUCT_QUOTATION => productQuotationIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_PRODPRICERULE", productPriceRuleIds, null, productQuotationIds);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "Loyalty":
                	case "LoyaltyAction":
                	case "LoyaltyCategory":
                	case "LoyaltyCondition":
                	case "LoyaltyProduct":
                	case "LoyaltyRoleTypeAppl":
                	case "LoyaltyRule":
                	case "LoyaltyStatus":
                	case "LoyaltyUse":
                		// check by LOYALTY_ID => loyaltyIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_LOYALTY", loyaltyIds, null, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
                	case "LoyaltyPointDetail":
                		// check by LOYALTY_POINT_ID => loyaltyPointIds
                		while ((nextValue = eli.next()) != null) {
                			// filter synchronization new
                			boolean flagToFilter = filterSynchronizationNew(nextValue, "GROUP_LOYALTYPOINT", loyaltyPointIds, null, null);
                			
                			if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	}
                        }
                		break;
					default:
						while ((nextValue = eli.next()) != null) {
                			//boolean flagToFilter = false;
                			// filter synchronization new
                			//flagToFilter = true;
                			
                			//if(flagToFilter){
                        		// sort by the tx stamp and then the record stamp
                                // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                    insertBefore++;
                                }
                                while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                    insertBefore++;
                                }
                                valuesToStore.add(insertBefore, nextValue);
                                valuesPerEntity++;
                        	//}
                        }
						break;
					}
                	/*case "TransferHeader":
                	case "TransferItem":
                		break;*/
                    eli.close();
                    
                   /*HOANDV old code
                    while ((nextValue = eli.next()) != null) {
                    	//hoandv add new filter for synchronization
                    	String entityName = modelEntity.getEntityName();
                    	boolean flagToFilter = false;
                    	switch (entityName) {
						case "ProductFacility":
							flagToFilter = filterSynchronization(nextValue, modelEntity, null);
							break;
						case "ProductStorePromoAppl":
							flagToFilter = filterSynchronization(nextValue, modelEntity, null);
							break;
						case "InventoryItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "InventoryItemDetail":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "InventoryItemVariance":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "InventoryTransfer":
							flagToFilter = filterSynchronization(nextValue, modelEntity, inventoryItemIdList);
							break;
						case "OrderHeader":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderStatus":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;	
						case "OrderAdjustment":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderContactMech":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "OrderRole":
							flagToFilter = filterSynchronization(nextValue, modelEntity, orderIdList);
							break;
						case "FacilityParty":
							flagToFilter = filterSynchronization(nextValue, modelEntity, partyIdList);
							break;
						case "UserLogin":
							flagToFilter = filterSynchronization(nextValue, modelEntity, partyIdList, userLoginIdList);
							break;
						case "UserLoginPasswordHistory":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "UserLoginHistory":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "UserLoginSession":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "UserLoginSecurityGroup":
							flagToFilter = filterSynchronization(nextValue, modelEntity, userLoginIdList);
							break;
						case "Requirement":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementNote":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementItem":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementItemBilling":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementRole":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						case "RequirementStatus":
							flagToFilter = filterSynchronization(nextValue, modelEntity, requirements);
							break;
						default:
							flagToFilter = true;
							break;
						}
                    	if(flagToFilter){
                    		// sort by the tx stamp and then the record stamp
                            // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                            while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                                insertBefore++;
                            }
                            while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                                insertBefore++;
                            }
                            valuesToStore.add(insertBefore, nextValue);
                            valuesPerEntity++;
                    	}
                        
                    }
                    eli.close();*/

                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);

                    // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                    if (valuesPerEntity == 0) {
                        Timestamp startCheckStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);

                        EntityFindOptions findNextOpts = new EntityFindOptions();
                        findNextOpts.setLimit(1);
                        findNextOpts.setMaxRows(1);
                        EntityCondition findNextCondition = EntityCondition.makeCondition(
                                EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime),
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                        EntityListIterator eliNext = delegator.find(modelEntity.getEntityName(), findNextCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD), findNextOpts);
                        // get the first element and it's tx time value...
                        GenericValue firstVal = eliNext.next();
                        eliNext.close();
                        Timestamp nextTxTime;
                        if (firstVal != null) {
                            nextTxTime = firstVal.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD);
                        } else {
                            // no results? well, then it's safe to say that up to the pre-querytime (minus the buffer, as usual) we are okay
                            nextTxTime = startCheckStamp;
                        }
                        if (this.nextUpdateTxTime == null || nextTxTime.before(this.nextUpdateTxTime)) {
                            this.nextUpdateTxTime = nextTxTime;
                            Debug.logInfo("EntitySync: Set nextUpdateTxTime to [" + nextTxTime + "]", module);
                        }
                        Timestamp curEntityNextTxTime = this.nextEntityUpdateTxTime.get(modelEntity.getEntityName());
                        if (curEntityNextTxTime == null || nextTxTime.before(curEntityNextTxTime)) {
                            this.nextEntityUpdateTxTime.put(modelEntity.getEntityName(), nextTxTime);
                            Debug.logInfo("EntitySync: Set nextEntityUpdateTxTime to [" + nextTxTime + "] for the entity [" + modelEntity.getEntityName() + "]", module);
                        }
                    }
                } catch (GenericEntityException e) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleValuesToStore", e);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Error getting values to store from the datasource", e);
                } catch (Throwable t) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "General error in assembleValuesToStore", t);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Caught runtime error while getting values to store", t);
                }

                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Commit transaction failed", e);
                }
            }
        }else{
        	// iterate through entities, get all records with tx stamp in the current time range, put all in a single list
            for (ModelEntity modelEntity: entityModelToUseList) {
                int insertBefore = 0;
                
                // first test to see if we know that there are no records for this entity in this time period...
                Timestamp knownNextUpdateTime = this.nextEntityUpdateTxTime.get(modelEntity.getEntityName());
                if (knownNextUpdateTime != null && (knownNextUpdateTime.equals(currentRunEndTime) || knownNextUpdateTime.after(currentRunEndTime))) {
                    entitiesSkippedForKnownNext++;
                    continue;
                }

                boolean beganTransaction = false;
                try {
                    beganTransaction = TransactionUtil.begin(7200);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Unable to begin JTA transaction", e);
                }

                try {
                    // get all values that were updated, but NOT created in the current time range; if no info on created stamp, that's okay we'll include it here because it won't have been included in the valuesToCreate list
                    EntityCondition createdBeforeStartCond = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.EQUALS, null),
                            EntityOperator.OR,
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunStartTime));
                    EntityCondition findValCondition = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime),
                            createdBeforeStartCond);
                    EntityListIterator eli = delegator.find(modelEntity.getEntityName(), findValCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD, ModelEntity.STAMP_FIELD), null);
                    GenericValue nextValue = null;
                    long valuesPerEntity = 0;
                    while ((nextValue = eli.next()) != null) {
                		// sort by the tx stamp and then the record stamp
                        // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                        while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                            insertBefore++;
                        }
                        while (insertBefore < valuesToStore.size() && valuesToStore.get(insertBefore).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                            insertBefore++;
                        }
                        valuesToStore.add(insertBefore, nextValue);
                        valuesPerEntity++;
                        
                    }
                    eli.close();

                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);

                    // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                    if (valuesPerEntity == 0) {
                        Timestamp startCheckStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);

                        EntityFindOptions findNextOpts = new EntityFindOptions();
                        findNextOpts.setLimit(1);
                        findNextOpts.setMaxRows(1);
                        EntityCondition findNextCondition = EntityCondition.makeCondition(
                                EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime),
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                                EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                        EntityListIterator eliNext = delegator.find(modelEntity.getEntityName(), findNextCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD), findNextOpts);
                        // get the first element and it's tx time value...
                        GenericValue firstVal = eliNext.next();
                        eliNext.close();
                        Timestamp nextTxTime;
                        if (firstVal != null) {
                            nextTxTime = firstVal.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD);
                        } else {
                            // no results? well, then it's safe to say that up to the pre-querytime (minus the buffer, as usual) we are okay
                            nextTxTime = startCheckStamp;
                        }
                        if (this.nextUpdateTxTime == null || nextTxTime.before(this.nextUpdateTxTime)) {
                            this.nextUpdateTxTime = nextTxTime;
                            Debug.logInfo("EntitySync: Set nextUpdateTxTime to [" + nextTxTime + "]", module);
                        }
                        Timestamp curEntityNextTxTime = this.nextEntityUpdateTxTime.get(modelEntity.getEntityName());
                        if (curEntityNextTxTime == null || nextTxTime.before(curEntityNextTxTime)) {
                            this.nextEntityUpdateTxTime.put(modelEntity.getEntityName(), nextTxTime);
                            Debug.logInfo("EntitySync: Set nextEntityUpdateTxTime to [" + nextTxTime + "] for the entity [" + modelEntity.getEntityName() + "]", module);
                        }
                    }
                } catch (GenericEntityException e) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleValuesToStore", e);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Error getting values to store from the datasource", e);
                } catch (Throwable t) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "General error in assembleValuesToStore", t);
                    } catch (GenericTransactionException e2) {
                        Debug.logWarning(e2, "Unable to call rollback()", module);
                    }
                    throw new SyncDataErrorException("Caught runtime error while getting values to store", t);
                }

                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    throw new SyncDataErrorException("Commit transaction failed", e);
                }
            }
        }
        

        if (entitiesSkippedForKnownNext > 0) {
            if (Debug.infoOn()) Debug.logInfo("In assembleValuesToStore skipped [" + entitiesSkippedForKnownNext + "/" + entityModelToUseList + "] entities for the time period ending at [" + currentRunEndTime + "] because of next known update times", module);
        }

        // TEST SECTION: leave false for normal use
        boolean logValues = false;
        if (logValues && valuesToStore.size() > 0) {
            StringBuilder toStoreInfo = new StringBuilder();
            for (GenericValue valueToStore: valuesToStore) {
                toStoreInfo.append("\n-->[");
                toStoreInfo.append(valueToStore.get(ModelEntity.STAMP_TX_FIELD));
                toStoreInfo.append(":");
                toStoreInfo.append(valueToStore.get(ModelEntity.STAMP_FIELD));
                toStoreInfo.append("] ");
                toStoreInfo.append(valueToStore.getPrimaryKey());
            }
            Debug.logInfo(toStoreInfo.toString(), module);
        }
        
        // As the this.nextUpdateTxTime calculation is only based on entities without values to store, if there at least one value to store returned
        // this calculation is false, so it needs to be nullified
        if (valuesToStore.size() > 0) {
            this.nextUpdateTxTime = null;
        }        

        return valuesToStore;
    }

    public LinkedList<GenericEntity> assembleKeysToRemove() throws SyncDataErrorException {
        // get all removed items from the given time range, add to list for those
        LinkedList<GenericEntity> keysToRemove = new LinkedList<GenericEntity>();

        if (this.nextRemoveTxTime != null && (this.nextRemoveTxTime.equals(currentRunEndTime) || this.nextRemoveTxTime.after(currentRunEndTime))) {
            // this means that for all entities in this pack we found on the last pass that there would be nothing for this one, so just return nothing...
            return keysToRemove;
        }

        //Debug.logInfo("Getting keys to remove; currentRunStartTime=" + currentRunStartTime + ", currentRunEndTime=" + currentRunEndTime, module);

        String forPullOnly = entitySync.getString("forPullOnly");
        if (UtilValidate.isNotEmpty(forPullOnly) && forPullOnly.equalsIgnoreCase("Y")) {
        	boolean beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin(7200);
            } catch (GenericTransactionException e) {
                throw new SyncDataErrorException("Unable to begin JTA transaction", e);
            }

            try {
            	List<String> partyIds = FastList.newInstance();
            	List<String> contactMechIds = FastList.newInstance();
            	List<String> inventoryItemIds = FastList.newInstance();
            	List<String> productPromoIds = FastList.newInstance();
            	List<String> productQuotationIds = FastList.newInstance();
            	List<String> productPriceRuleIds = FastList.newInstance();
            	List<String> loyaltyIds = FastList.newInstance();
            	List<String> loyaltyPointIds = FastList.newInstance();
            	List<String> userLoginIds = FastList.newInstance();
            	List<String> partyIdsNot = FastList.newInstance();
            	List<String> contactMechIdsNot = FastList.newInstance();
            	
                // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                /*EntityCondition findValCondition = EntityCondition.makeCondition(
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));*/
            	EntityCondition createdBeforeStartCond = EntityCondition.makeCondition(
                        EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.EQUALS, null),
                        EntityOperator.OR,
                        EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunStartTime));
                EntityCondition findValCondition = EntityCondition.makeCondition(
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime),
                        createdBeforeStartCond);
                EntityListIterator removeEli = delegator.find("EntitySyncRemove", findValCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD, ModelEntity.STAMP_FIELD), null);
                GenericValue entitySyncRemove = null;
            	while ((entitySyncRemove = removeEli.next()) != null) {
            		// pull the PK from the EntitySyncRemove in the primaryKeyRemoved field, de-XML-serialize it
                    String primaryKeyRemoved = entitySyncRemove.getString("primaryKeyRemoved");
                    GenericEntity pkToRemove = null;
                    boolean isAdd = false;
                    try {
                        pkToRemove = (GenericEntity) XmlSerializer.deserialize(primaryKeyRemoved, delegator);
                        
                        String entityName = pkToRemove.getEntityName();
                        if (this.entityNameToUseSet.contains(entityName)) {
                        	GenericValue nextValue = delegator.findOne(entityName, pkToRemove.getPrimaryKey(), false);
                        	switch (entityName) {
                        	case "Party":
                        	case "PartyAttribute":
                        	case "Person":
                        	case "PartyGroup":
                        	case "PartyRole":
                        	case "OlbiusPartyPermission":
                        	case "UserLogin":
                        	case "PartyClassification":
                        	case "PartyContactMech":
                        	case "PartyContactMechPurpose":
                        		// check by PARTY_ID => partyIds
                    			// filter synchronization new
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PARTY", partyIds, partyIdsNot, null);
                        		break;
                        	case "UserLoginSecurityGroup":
                        		// check by USER_LOGIN_ID => userLoginIds => PARTY_ID => partyIds
                    			// filter synchronization new
                    			isAdd = filterSynchronizationNew(nextValue, "GROUP_USERLOGIN", partyIds, partyIdsNot, userLoginIds);
                        		break;
                        	case "PartyRelationship":
                        		// check by PARTY_FROM or PARTY_TO
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PARTYREL", partyIds, partyIdsNot, null);
                        		break;
                        	case "ContactMech":
                        	case "ContactMechAttribute":
                        	case "PostalAddress":
                        	case "TelecomNumber":
                        		// check by CONTACT_MECH => contactMechIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_CONTACTMECH", contactMechIds, contactMechIdsNot, partyIds, partyIdsNot);
                        		break;
                        	case "Facility":
                        	case "FacilityParty":
                        	case "FacilityContactMech":
                        	case "FacilityContactMechPurpose":
                        	case "ProductFacility":
                        	case "ProductStoreFacility":
                        	case "InventoryItem":
                        		// check by FACILITY default
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_FACILITY", null, null, null);
                        		break;
                        	case "InventoryItemDetail":
                        	case "InventoryItemLabel":
                        	case "InventoryItemLabelAppl":
                        	case "InventoryItemLabelType":
                        		// check by INVENTORY_ITEM => inventoryItemIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_INVENTORYITEM", inventoryItemIds, null, null);
                        		break;
                        	case "ProductStore":
                        	case "ProductStoreRole":
                        	case "ProductStoreCatalog":
                        	case "ProductStorePaymentSetting":
                        	case "ProductStoreShipmentMeth":
                        	case "ProductStoreGroupMember":
                        	case "ProductStorePromoAppl":
                        	case "ProductStoreLoyaltyAppl":
                        	case "ProductQuotationStoreAppl":
                        	case "LoyaltyPoint":
                        	case "ConfigPrintOrder":
                        		// check by PRODUCT_STORE
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTORE", null, null, null);
                        		break;
                        	case "ProductStoreGroup":
                        	case "ProductQuotationStoreGroupAppl":
                        		// check by PRODUCT_STORE_GROUP
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PRODUCTSTOREGROUP", null, null, null);
                        		break;
                        	case "ProductPromo":
                        	case "ProductPromoAction":
                        	case "ProductPromoCategory":
                        	case "ProductPromoCode":
                        	case "ProductPromoCodeParty":
                        	case "ProductPromoCond":
                        	case "ProductPromoProduct":
                        	case "ProductPromoRule":
                        	case "ProductPromoUse":
                        	case "ProductPromoStatus":
                        		// check by PRODUCT_PROMO => productPromoIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PRODPROMO", productPromoIds, null, null);
                        		break;
                        	case "ProductQuotation":
                        	case "ProductQuotationStatus":
                        	case "ProductPriceRule":
                        		// check by PRODUCT_QUOTATION => productQuotationIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PRODQUOTATION", productQuotationIds, null, null);
                        		break;
                        	case "ProductPriceAction":
                        	case "ProductPriceActionType":
                        	case "ProductPriceChange":
                        	case "ProductPriceCond":
                        		// check by PRODUCT_PRICE_RULE => productPriceRuleIds => PRODUCT_QUOTATION => productQuotationIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_PRODPRICERULE", productPriceRuleIds, null, productQuotationIds);
                        		break;
                        	case "Loyalty":
                        	case "LoyaltyAction":
                        	case "LoyaltyCategory":
                        	case "LoyaltyCondition":
                        	case "LoyaltyProduct":
                        	case "LoyaltyRoleTypeAppl":
                        	case "LoyaltyRule":
                        	case "LoyaltyStatus":
                        	case "LoyaltyUse":
                        		// check by LOYALTY_ID => loyaltyIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_LOYALTY", loyaltyIds, null, null);
                        		break;
                        	case "LoyaltyPointDetail":
                        		// check by LOYALTY_POINT_ID => loyaltyPointIds
                        		isAdd = filterSynchronizationNew(nextValue, "GROUP_LOYALTYPOINT", loyaltyPointIds, null, null);
                        		break;
        					default:
        						isAdd = true;
        						break;
        					}
                        }
                    } catch (IOException e) {
                        String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                        Debug.logError(e, errorMsg, module);
                        throw new SyncDataErrorException(errorMsg, e);
                    } catch (SAXException e) {
                        String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                        Debug.logError(e, errorMsg, module);
                        throw new SyncDataErrorException(errorMsg, e);
                    } catch (ParserConfigurationException e) {
                        String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                        Debug.logError(e, errorMsg, module);
                        throw new SyncDataErrorException(errorMsg, e);
                    } catch (SerializeException e) {
                        String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                        Debug.logError(e, errorMsg, module);
                        throw new SyncDataErrorException(errorMsg, e);
                    }

                    if (isAdd) {
                    	 // set the stamp fields for future reference
                        pkToRemove.set(ModelEntity.STAMP_TX_FIELD, entitySyncRemove.get(ModelEntity.STAMP_TX_FIELD));
                        pkToRemove.set(ModelEntity.STAMP_FIELD, entitySyncRemove.get(ModelEntity.STAMP_FIELD));
                        pkToRemove.set(ModelEntity.CREATE_STAMP_TX_FIELD, entitySyncRemove.get(ModelEntity.CREATE_STAMP_TX_FIELD));
                        pkToRemove.set(ModelEntity.CREATE_STAMP_FIELD, entitySyncRemove.get(ModelEntity.CREATE_STAMP_FIELD));

                        if (this.entityNameToUseSet.contains(pkToRemove.getEntityName())) {
                            keysToRemove.add(pkToRemove);
                        }
                    }
                }
                
                removeEli.close();

                // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                if (keysToRemove.size() == 0) {
                    EntityFindOptions findNextOpts = new EntityFindOptions();
                    findNextOpts.setLimit(1);
                    findNextOpts.setMaxRows(1);
                    //EntityCondition findNextCondition = EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime);
                	EntityCondition findNextCondition = EntityCondition.makeCondition(
                             EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                             EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime),
                             EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                             EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                	EntityListIterator eliNext = delegator.find("EntitySyncRemove", findNextCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD), findNextOpts);
                    // get the first element and it's tx time value...
                    GenericValue firstVal = eliNext.next();
                    eliNext.close();
                    if (firstVal != null) {
                        Timestamp nextTxTime = firstVal.getTimestamp(ModelEntity.STAMP_TX_FIELD);
                        if (this.nextRemoveTxTime == null || nextTxTime.before(this.nextRemoveTxTime)) {
                            this.nextRemoveTxTime = nextTxTime;
                        }
                    }
                }
            } catch (GenericEntityException e) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleKeysToRemove", e);
                } catch (GenericTransactionException e2) {
                    Debug.logWarning(e2, "Unable to call rollback()", module);
                }
                throw new SyncDataErrorException("Error getting keys to remove from the datasource", e);
            } catch (Throwable t) {
                try {
                    TransactionUtil.rollback(beganTransaction, "General error in assembleKeysToRemove", t);
                } catch (GenericTransactionException e2) {
                    Debug.logWarning(e2, "Unable to call rollback()", module);
                }
                throw new SyncDataErrorException("Caught runtime error while getting keys to remove", t);
            }

            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                throw new SyncDataErrorException("Commit transaction failed", e);
            }
        } else {
        	// OLD CODE
        	
        	boolean beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin(7200);
            } catch (GenericTransactionException e) {
                throw new SyncDataErrorException("Unable to begin JTA transaction", e);
            }

            try {
                // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                EntityCondition findValCondition = EntityCondition.makeCondition(
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime),
                        EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                EntityListIterator removeEli = delegator.find("EntitySyncRemove", findValCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD, ModelEntity.STAMP_FIELD), null);
                GenericValue entitySyncRemove = null;
            	while ((entitySyncRemove = removeEli.next()) != null) {
                		// pull the PK from the EntitySyncRemove in the primaryKeyRemoved field, de-XML-serialize it
                        String primaryKeyRemoved = entitySyncRemove.getString("primaryKeyRemoved");
                        GenericEntity pkToRemove = null;
                        try {
                            pkToRemove = (GenericEntity) XmlSerializer.deserialize(primaryKeyRemoved, delegator);
                        } catch (IOException e) {
                            String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                            Debug.logError(e, errorMsg, module);
                            throw new SyncDataErrorException(errorMsg, e);
                        } catch (SAXException e) {
                            String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                            Debug.logError(e, errorMsg, module);
                            throw new SyncDataErrorException(errorMsg, e);
                        } catch (ParserConfigurationException e) {
                            String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                            Debug.logError(e, errorMsg, module);
                            throw new SyncDataErrorException(errorMsg, e);
                        } catch (SerializeException e) {
                            String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                            Debug.logError(e, errorMsg, module);
                            throw new SyncDataErrorException(errorMsg, e);
                        }

                        // set the stamp fields for future reference
                        pkToRemove.set(ModelEntity.STAMP_TX_FIELD, entitySyncRemove.get(ModelEntity.STAMP_TX_FIELD));
                        pkToRemove.set(ModelEntity.STAMP_FIELD, entitySyncRemove.get(ModelEntity.STAMP_FIELD));
                        pkToRemove.set(ModelEntity.CREATE_STAMP_TX_FIELD, entitySyncRemove.get(ModelEntity.CREATE_STAMP_TX_FIELD));
                        pkToRemove.set(ModelEntity.CREATE_STAMP_FIELD, entitySyncRemove.get(ModelEntity.CREATE_STAMP_FIELD));

                        if (this.entityNameToUseSet.contains(pkToRemove.getEntityName())) {
                            keysToRemove.add(pkToRemove);
                        }
                    
                }
                
                removeEli.close();

                // if we didn't find anything for this entity, find the next value's Timestamp and keep track of it
                if (keysToRemove.size() == 0) {
                    EntityFindOptions findNextOpts = new EntityFindOptions();
                    findNextOpts.setLimit(1);
                    findNextOpts.setMaxRows(1);
                    /*EntityCondition findNextCondition = EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime);*/
                	EntityCondition findNextCondition = EntityCondition.makeCondition(
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                            EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunEndTime),
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null),
                            EntityCondition.makeCondition(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime));
                    EntityListIterator eliNext = delegator.find("EntitySyncRemove", findNextCondition, null, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD), findNextOpts);
                    // get the first element and it's tx time value...
                    GenericValue firstVal = eliNext.next();
                    eliNext.close();
                    if (firstVal != null) {
                        Timestamp nextTxTime = firstVal.getTimestamp(ModelEntity.STAMP_TX_FIELD);
                        if (this.nextRemoveTxTime == null || nextTxTime.before(this.nextRemoveTxTime)) {
                            this.nextRemoveTxTime = nextTxTime;
                        }
                    }
                }
            } catch (GenericEntityException e) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Entity Engine error in assembleKeysToRemove", e);
                } catch (GenericTransactionException e2) {
                    Debug.logWarning(e2, "Unable to call rollback()", module);
                }
                throw new SyncDataErrorException("Error getting keys to remove from the datasource", e);
            } catch (Throwable t) {
                try {
                    TransactionUtil.rollback(beganTransaction, "General error in assembleKeysToRemove", t);
                } catch (GenericTransactionException e2) {
                    Debug.logWarning(e2, "Unable to call rollback()", module);
                }
                throw new SyncDataErrorException("Caught runtime error while getting keys to remove", t);
            }

            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                throw new SyncDataErrorException("Commit transaction failed", e);
            }
        }

        // TEST SECTION: leave false for normal use
        boolean logValues = false;
        if (logValues && keysToRemove.size() > 0) {
            StringBuilder toRemoveInfo = new StringBuilder();
            for (GenericEntity keyToRemove: keysToRemove) {
                toRemoveInfo.append("\n-->[");
                toRemoveInfo.append(keyToRemove.get(ModelEntity.STAMP_TX_FIELD));
                toRemoveInfo.append(":");
                toRemoveInfo.append(keyToRemove.get(ModelEntity.STAMP_FIELD));
                toRemoveInfo.append("] ");
                toRemoveInfo.append(keyToRemove);
            }
            Debug.logInfo(toRemoveInfo.toString(), module);
        }

        // As this.nextRemoveTxTime calculation is only based on entities without keys to remove, if there at least one key to remove returned
        // this calculation is false, so it needs to be nullified
        if (keysToRemove.size() > 0) {
            this.nextRemoveTxTime = null;
        }
        
        return keysToRemove;
    }

    public void saveResultsReportedFromDataStore() throws SyncDataErrorException, SyncServiceErrorException {
        try {
            long runningTimeMillis = System.currentTimeMillis() - startDate.getTime();

            // get the total for this split
            long splitTotalTime = System.currentTimeMillis() - this.splitStartTime;
            if (splitTotalTime < this.perSplitMinMillis) {
                this.perSplitMinMillis = splitTotalTime;
            }
            if (splitTotalTime > this.perSplitMaxMillis) {
                this.perSplitMaxMillis = splitTotalTime;
            }

            // start the timer for the next split
            setSplitStartTime();

            // total the rows saved so far, and gather some info about them before saving
            this.totalRowsPerSplit = this.toCreateInserted + this.toCreateNotUpdated + this.toCreateUpdated +
                    this.toStoreInserted + this.toStoreNotUpdated + this.toStoreUpdated +
                    this.toRemoveAlreadyDeleted + this.toRemoveDeleted;
            if (this.totalRowsPerSplit < this.perSplitMinItems) {
                this.perSplitMinItems = this.totalRowsPerSplit;
            }
            if (this.totalRowsPerSplit > this.perSplitMaxItems) {
                this.perSplitMaxItems = this.totalRowsPerSplit;
            }
            this.totalRowsToCreate += this.toCreateInserted + this.toCreateNotUpdated + this.toCreateUpdated;
            this.totalRowsToStore += this.toStoreInserted + this.toStoreNotUpdated + this.toStoreUpdated;
            this.totalRowsToRemove += this.toRemoveAlreadyDeleted + this.toRemoveDeleted;

            // store latest result on EntitySync, ie update lastSuccessfulSynchTime, should run in own tx
            Map<String, Object> updateEsRunResult = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "lastSuccessfulSynchTime", this.currentRunEndTime, "userLogin", userLogin));

            // store result of service call on history with results so far, should run in own tx
            Map<String, Object> updateHistoryMap = UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate,
                    "lastSuccessfulSynchTime", this.currentRunEndTime, "lastCandidateEndTime", this.getNextRunEndTime(),
                    "lastSplitStartTime", Long.valueOf(this.splitStartTime));
            updateHistoryMap.put("toCreateInserted", Long.valueOf(toCreateInserted));
            updateHistoryMap.put("toCreateUpdated", Long.valueOf(toCreateUpdated));
            updateHistoryMap.put("toCreateNotUpdated", Long.valueOf(toCreateNotUpdated));
            updateHistoryMap.put("toStoreInserted", Long.valueOf(toStoreInserted));
            updateHistoryMap.put("toStoreUpdated", Long.valueOf(toStoreUpdated));
            updateHistoryMap.put("toStoreNotUpdated", Long.valueOf(toStoreNotUpdated));
            updateHistoryMap.put("toRemoveDeleted", Long.valueOf(toRemoveDeleted));
            updateHistoryMap.put("toRemoveAlreadyDeleted", Long.valueOf(toRemoveAlreadyDeleted));
            updateHistoryMap.put("runningTimeMillis", Long.valueOf(runningTimeMillis));
            updateHistoryMap.put("totalStoreCalls", Long.valueOf(totalStoreCalls));
            updateHistoryMap.put("totalSplits", Long.valueOf(totalSplits));
            updateHistoryMap.put("totalRowsExported", Long.valueOf(totalRowsExported));
            updateHistoryMap.put("totalRowsToCreate", Long.valueOf(totalRowsToCreate));
            updateHistoryMap.put("totalRowsToStore", Long.valueOf(totalRowsToStore));
            updateHistoryMap.put("totalRowsToRemove", Long.valueOf(totalRowsToRemove));
            updateHistoryMap.put("perSplitMinMillis", Long.valueOf(perSplitMinMillis));
            updateHistoryMap.put("perSplitMaxMillis", Long.valueOf(perSplitMaxMillis));
            updateHistoryMap.put("perSplitMinItems", Long.valueOf(perSplitMinItems));
            updateHistoryMap.put("perSplitMaxItems", Long.valueOf(perSplitMaxItems));
            updateHistoryMap.put("userLogin", userLogin);
            Map<String, Object> updateEsHistRunResult = dispatcher.runSync("updateEntitySyncHistory", updateHistoryMap);

            // now we have updated EntitySync and EntitySyncHistory, check both ops for errors...
            if (ServiceUtil.isError(updateEsRunResult)) {
                String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySync record with lastSuccessfulSynchTime failed.";
                throw new SyncDataErrorException(errorMsg, null, null, updateEsRunResult, null);
            }

            if (ServiceUtil.isError(updateEsHistRunResult)) {
                String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySyncHistory (startDate:[" + startDate + "]) record with lastSuccessfulSynchTime and result stats failed.";
                throw new SyncDataErrorException(errorMsg, null, null, updateEsHistRunResult, null);
            }
        } catch (GenericServiceException e) {
            throw new SyncServiceErrorException("Error saving results reported from data store", e);
        }
    }

    public void saveFinalSyncResults() throws SyncDataErrorException, SyncServiceErrorException {
        String newStatusId = "ESR_COMPLETE";
        if (this.isOfflineSync && totalRowsExported > 0) {
            newStatusId = "ESR_PENDING";
        }

        // the lastSuccessfulSynchTime on EntitySync will already be set, so just set status as completed
        String esErrMsg = "Could not mark Entity Sync as complete, but all synchronization was successful";
        try {
            Map<String, Object> completeEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", newStatusId, "userLogin", userLogin));
            if (ServiceUtil.isError(completeEntitySyncRes)) {
                // what to do here? try again?
                throw new SyncDataErrorException(esErrMsg, null, null, completeEntitySyncRes, null);
            }
        } catch (GenericServiceException e) {
            throw new SyncServiceErrorException(esErrMsg, e);
        }

        // if nothing moved over, remove the history record, otherwise store status
        long totalRows = totalRowsToCreate + totalRowsToStore + totalRowsToRemove;
        if (totalRows == 0) {
            String eshRemoveErrMsg = "Could not remove Entity Sync History (done because nothing was synced in this call), but all synchronization was successful";
            try {
                Map<String, Object> deleteEntitySyncHistRes = dispatcher.runSync("deleteEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "userLogin", userLogin));
                if (ServiceUtil.isError(deleteEntitySyncHistRes)) {
                    throw new SyncDataErrorException(eshRemoveErrMsg, null, null, deleteEntitySyncHistRes, null);
                }
            } catch (GenericServiceException e) {
                throw new SyncServiceErrorException(eshRemoveErrMsg, e);
            }
        } else {
            // the lastSuccessfulSynchTime on EntitySync will already be set, so just set status as completed
            String eshCompleteErrMsg = "Could not mark Entity Sync History as complete, but all synchronization was successful";
            try {
                Map<String, Object> completeEntitySyncHistRes = dispatcher.runSync("updateEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "runStatusId", "ESR_COMPLETE", "userLogin", userLogin));
                if (ServiceUtil.isError(completeEntitySyncHistRes)) {
                    // what to do here? try again?
                    throw new SyncDataErrorException(eshCompleteErrMsg, null, null, completeEntitySyncHistRes, null);
                }
            } catch (GenericServiceException e) {
                throw new SyncServiceErrorException(eshCompleteErrMsg, e);
            }
        }

        if (Debug.infoOn()) Debug.logInfo("Finished saveFinalSyncResults [" + entitySyncId + "]: totalRows=" + totalRows + ", totalRowsToCreate=" + totalRowsToCreate + ", totalRowsToStore=" + totalRowsToStore + ", totalRowsToRemove=" + totalRowsToRemove, module);
    }

    public Set<String> makeEntityNameToUseSet() {
        Set<String> entityNameToUseSet = FastSet.newInstance();
        for (ModelEntity modelEntity: this.entityModelToUseList) {
            entityNameToUseSet.add(modelEntity.getEntityName());
        }
        return entityNameToUseSet;
    }

    /** prepare a list of all entities we want to synchronize: remove all view-entities and all entities that don't match the patterns attached to this EntitySync */
    protected List<ModelEntity> makeEntityModelToUseList() throws GenericEntityException {
        List<GenericValue> entitySyncIncludes = entitySync.getRelated("EntitySyncInclude", null, null, false);
        // get these ones as well, and just add them to the main list, it will have an extra field but that shouldn't hurt anything in the code below
        List<GenericValue> entitySyncGroupIncludes = entitySync.getRelated("EntitySyncInclGrpDetailView", null, null, false);
        entitySyncIncludes.addAll(entitySyncGroupIncludes);

        List<ModelEntity> entityModelToUseList = EntityGroupUtil.getModelEntitiesFromRecords(entitySyncIncludes, delegator, true);

        if (Debug.infoOn()) Debug.logInfo("In makeEntityModelToUseList for EntitySync with ID [" + entitySync.get("entitySyncId") + "] syncing " + entityModelToUseList.size() + " entities", module);
        return entityModelToUseList;
    }
    protected static Timestamp getCurrentRunStartTime(Timestamp lastSuccessfulSynchTime, List<ModelEntity> entityModelToUseList, Delegator delegator) throws GenericEntityException {
        // if currentRunStartTime is null, what to do? I guess iterate through all entities and find earliest tx stamp
        if (lastSuccessfulSynchTime == null) {
            Timestamp currentRunStartTime = null;
            for (ModelEntity modelEntity: entityModelToUseList) {
                // fields to select will be PK and the STAMP_TX_FIELD, slimmed down so we don't get a ton of data back
                Set<String> fieldsToSelect = UtilMisc.toSet(modelEntity.getPkFieldNames());
                // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                fieldsToSelect.add(ModelEntity.STAMP_TX_FIELD);
                EntityListIterator eli = delegator.find(modelEntity.getEntityName(), EntityCondition.makeCondition(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null), null, fieldsToSelect, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD), null);
                GenericValue nextValue = eli.next();
                eli.close();
                if (nextValue != null) {
                    Timestamp candidateTime = nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD);
                    if (currentRunStartTime == null || candidateTime.before(currentRunStartTime)) {
                        currentRunStartTime = candidateTime;
                    }
                }
            }
            if (Debug.infoOn()) Debug.logInfo("No currentRunStartTime was stored on the EntitySync record, so searched for the earliest value and got: " + currentRunStartTime, module);
            return currentRunStartTime;
        } else {
            return lastSuccessfulSynchTime;
        }
    }

    public void saveSyncErrorInfo(String runStatusId, List<Object> errorMessages) {
        // set error statuses on the EntitySync and EntitySyncHistory entities
        try {
            Map<String, Object> errorEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", runStatusId, "userLogin", userLogin));
            if (ServiceUtil.isError(errorEntitySyncRes)) {
                errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + errorEntitySyncRes.get(ModelService.ERROR_MESSAGE));
            }
        } catch (GenericServiceException e) {
            errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + e.toString());
        }
        if (startDate != null) {
            try {
                Map<String, Object> errorEntitySyncHistoryRes = dispatcher.runSync("updateEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "runStatusId", runStatusId, "userLogin", userLogin));
                if (ServiceUtil.isError(errorEntitySyncHistoryRes)) {
                    errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySyncHistory with ID [" + entitySyncId + "]: " + errorEntitySyncHistoryRes.get(ModelService.ERROR_MESSAGE));
                }
            } catch (GenericServiceException e) {
                errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySyncHistory with ID [" + entitySyncId + ":" + startDate + "]: " + e.toString());
            }
        }
    }

    // ======================== PUSH Methods ========================
    public void runPushStartRunning() throws SyncDataErrorException, SyncServiceErrorException, SyncAbortException {
        if (UtilValidate.isEmpty(targetServiceName)) {
            throw new SyncAbortException("Not running EntitySync [" + entitySyncId + "], no targetServiceName is specified, where do we send the data?");
        }

        // check to see if this sync is already running, if so return error
        if (this.isEntitySyncRunning()) {
            throw new SyncAbortException("Not running EntitySync [" + entitySyncId + "], an instance is already running.");
        }

        String markErrorMsg = "Could not start Entity Sync service, could not mark as running";
        try {
            // not running, get started NOW
            // set running status on entity sync, run in its own tx
            Map<String, Object> startEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "userLogin", userLogin));
            if (ModelService.RESPOND_ERROR.equals(startEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                throw new SyncDataErrorException(markErrorMsg, null, null, startEntitySyncRes, null);
            }
        } catch (GenericServiceException e) {
            throw new SyncServiceErrorException(markErrorMsg, e);
        }

        // finally create the initial history record
        this.createInitialHistory();
    }

    public long setTotalRowCounts(ArrayList<GenericValue> valuesToCreate, ArrayList<GenericValue> valuesToStore, List<GenericEntity> keysToRemove) {
        this.totalRowsToCreate = valuesToCreate.size();
        this.totalRowsToStore = valuesToStore.size();
        this.totalRowsToRemove = keysToRemove.size();
        this.totalRowsPerSplit = this.totalRowsToCreate + this.totalRowsToStore + this.totalRowsToRemove;
        return this.totalRowsPerSplit;
    }

    public void runPushSendData(ArrayList<GenericValue> valuesToCreate, ArrayList<GenericValue> valuesToStore, List<GenericEntity> keysToRemove) throws SyncOtherErrorException, SyncServiceErrorException {
        // grab the totals for this data
        this.setTotalRowCounts(valuesToCreate, valuesToStore, keysToRemove);

        // call service named on EntitySync, IFF there is actually data to send over
        if (this.totalRowsPerSplit > 0) {
            Map<String, Object> targetServiceMap = UtilMisc.toMap("entitySyncId", entitySyncId, "valuesToCreate", valuesToCreate, "valuesToStore", valuesToStore, "keysToRemove", keysToRemove, "userLogin", userLogin);
            if (UtilValidate.isNotEmpty(targetDelegatorName)) {
                targetServiceMap.put("delegatorName", targetDelegatorName);
            }
            String serviceErrorMsg = "Error running EntitySync [" + entitySyncId + "], call to store service [" + targetServiceName + "] failed.";
            try {
                Map<String, Object> remoteStoreResult = dispatcher.runSync(targetServiceName, targetServiceMap);
                if (ServiceUtil.isError(remoteStoreResult)) {
                    throw new SyncOtherErrorException(serviceErrorMsg, null, null, remoteStoreResult, null);
                }

                this.totalStoreCalls++;

                long toCreateInsertedCur = remoteStoreResult.get("toCreateInserted") == null ? 0 : ((Long) remoteStoreResult.get("toCreateInserted")).longValue();
                long toCreateUpdatedCur = remoteStoreResult.get("toCreateUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toCreateUpdated")).longValue();
                long toCreateNotUpdatedCur = remoteStoreResult.get("toCreateNotUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toCreateNotUpdated")).longValue();
                long toStoreInsertedCur = remoteStoreResult.get("toStoreInserted") == null ? 0 : ((Long) remoteStoreResult.get("toStoreInserted")).longValue();
                long toStoreUpdatedCur = remoteStoreResult.get("toStoreUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toStoreUpdated")).longValue();
                long toStoreNotUpdatedCur = remoteStoreResult.get("toStoreNotUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toStoreNotUpdated")).longValue();
                long toRemoveDeletedCur = remoteStoreResult.get("toRemoveDeleted") == null ? 0 : ((Long) remoteStoreResult.get("toRemoveDeleted")).longValue();
                long toRemoveAlreadyDeletedCur = remoteStoreResult.get("toRemoveAlreadyDeleted") == null ? 0 : ((Long) remoteStoreResult.get("toRemoveAlreadyDeleted")).longValue();

                this.toCreateInserted += toCreateInsertedCur;
                this.toCreateUpdated += toCreateUpdatedCur;
                this.toCreateNotUpdated += toCreateNotUpdatedCur;
                this.toStoreInserted += toStoreInsertedCur;
                this.toStoreUpdated += toStoreUpdatedCur;
                this.toStoreNotUpdated += toStoreNotUpdatedCur;
                this.toRemoveDeleted += toRemoveDeletedCur;
                this.toRemoveAlreadyDeleted += toRemoveAlreadyDeletedCur;
            } catch (GenericServiceException e) {
                throw new SyncServiceErrorException(serviceErrorMsg, e);
            }
        }
    }

    // ======================== PULL Methods ========================
    public void runPullStartOrRestoreSavedResults() throws SyncDataErrorException, SyncServiceErrorException, SyncAbortException {
        // if EntitySync.statusId is ESR_RUNNING, make sure startDate matches EntitySync.lastHistoryStartDate; or return error
        if (isEntitySyncRunning() && this.startDate == null) {
            throw new SyncAbortException("Not running EntitySync [" + entitySyncId + "], an instance is already running and no startDate for the current run was passed.");
        }

        if (this.startDate == null) {
            // get it started!
            String markErrorMsg = "Could not start Entity Sync service, could not mark as running";
            try {
                // not running, get started NOW
                // set running status on entity sync, run in its own tx
                Map<String, Object> startEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "userLogin", userLogin));
                if (ModelService.RESPOND_ERROR.equals(startEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                    throw new SyncDataErrorException(markErrorMsg, null, null, startEntitySyncRes, null);
                }
            } catch (GenericServiceException e) {
                throw new SyncServiceErrorException(markErrorMsg, e);
            }

            // finally create the initial history record
            this.createInitialHistory();
            this.setSplitStartTime();
        } else {
            try {
                // set the latest values from the EntitySyncHistory, based on the values on the EntitySync
                GenericValue entitySyncHistory = delegator.findOne("EntitySyncHistory", false, "entitySyncId", entitySyncId, "startDate", startDate);
                this.toCreateInserted = UtilMisc.toLong(entitySyncHistory.getLong("toCreateInserted"));
                this.toCreateUpdated = UtilMisc.toLong(entitySyncHistory.getLong("toCreateUpdated"));
                this.toCreateNotUpdated = UtilMisc.toLong(entitySyncHistory.getLong("toCreateNotUpdated"));

                this.toStoreInserted = UtilMisc.toLong(entitySyncHistory.getLong("toStoreInserted"));
                this.toStoreUpdated = UtilMisc.toLong(entitySyncHistory.getLong("toStoreUpdated"));
                this.toStoreNotUpdated = UtilMisc.toLong(entitySyncHistory.getLong("toStoreNotUpdated"));

                this.toRemoveDeleted = UtilMisc.toLong(entitySyncHistory.getLong("toRemoveDeleted"));
                this.toRemoveAlreadyDeleted = UtilMisc.toLong(entitySyncHistory.getLong("toRemoveAlreadyDeleted"));

                this.totalStoreCalls = UtilMisc.toLong(entitySyncHistory.getLong("totalStoreCalls"));
                this.totalSplits = UtilMisc.toLong(entitySyncHistory.getLong("totalSplits"));
                this.totalRowsToCreate = UtilMisc.toLong(entitySyncHistory.getLong("totalRowsToCreate"));
                this.totalRowsToStore = UtilMisc.toLong(entitySyncHistory.getLong("totalRowsToStore"));
                this.totalRowsToRemove = UtilMisc.toLong(entitySyncHistory.getLong("totalRowsToRemove"));

                this.perSplitMinMillis = UtilMisc.toLong(entitySyncHistory.getLong("perSplitMinMillis"));
                this.perSplitMaxMillis = UtilMisc.toLong(entitySyncHistory.getLong("perSplitMaxMillis"));
                this.perSplitMinItems = UtilMisc.toLong(entitySyncHistory.getLong("perSplitMinItems"));
                this.perSplitMaxItems = UtilMisc.toLong(entitySyncHistory.getLong("perSplitMaxItems"));

                this.splitStartTime = UtilMisc.toLong(entitySyncHistory.getLong("lastSplitStartTime"));
            } catch (GenericEntityException e) {
                throw new SyncDataErrorException("Error getting existing EntitySyncHistory values", e);
            }

            // got the previous values, now add to them with the values from the context...
            this.toCreateInserted += UtilMisc.toLong(this.context.get("toCreateInserted"));
            this.toCreateUpdated += UtilMisc.toLong(this.context.get("toCreateUpdated"));
            this.toCreateNotUpdated += UtilMisc.toLong(this.context.get("toCreateNotUpdated"));
            this.toStoreInserted += UtilMisc.toLong(this.context.get("toStoreInserted"));
            this.toStoreUpdated += UtilMisc.toLong(this.context.get("toStoreUpdated"));
            this.toStoreNotUpdated += UtilMisc.toLong(this.context.get("toStoreNotUpdated"));
            this.toRemoveDeleted += UtilMisc.toLong(this.context.get("toRemoveDeleted"));
            this.toRemoveAlreadyDeleted += UtilMisc.toLong(this.context.get("toRemoveAlreadyDeleted"));

            this.totalStoreCalls++;

            this.saveResultsReportedFromDataStore();
        }
    }

    // ======================== OFFLINE Methods ========================
    public void runOfflineStartRunning() throws SyncDataErrorException, SyncServiceErrorException, SyncAbortException {
        // check to see if this sync is already running, if so return error
        if (this.isEntitySyncRunning()) {
            throw new SyncAbortException("Not running EntitySync [" + entitySyncId + "], an instance is already running.");
        }

        // flag this context as offline
        this.isOfflineSync = true;

        String markErrorMsg = "Could not start Entity Sync service, could not mark as running";
        try {
            // not running, get started NOW
            // set running status on entity sync, run in its own tx
            Map<String, Object> startEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "preOfflineSynchTime", this.lastSuccessfulSynchTime, "userLogin", userLogin));
            if (ModelService.RESPOND_ERROR.equals(startEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                throw new SyncDataErrorException(markErrorMsg, null, null, startEntitySyncRes, null);
            }
        } catch (GenericServiceException e) {
            throw new SyncServiceErrorException(markErrorMsg, e);
        }

        // finally create the initial history record
        this.createInitialHistory();
    }

    public void runSaveOfflineSyncInfo(long rowsInSplit) throws SyncDataErrorException, SyncServiceErrorException, SyncAbortException {
        this.totalRowsExported += rowsInSplit;
        this.saveResultsReportedFromDataStore();
    }

    /**
     * Static method to obtain a list of entity names which will be synchronized
     */
    public static Set<String> getEntitySyncModelNamesToUse(LocalDispatcher dispatcher, String entitySyncId) throws SyncDataErrorException, SyncAbortException {
        DispatchContext dctx = dispatcher.getDispatchContext();
        EntitySyncContext ctx = new EntitySyncContext(dctx, UtilMisc.toMap("entitySyncId", entitySyncId));
        return ctx.makeEntityNameToUseSet();
    }

    /** This class signifies an abort condition, so the state and such of the EntitySync value in the datasource should not be changed */
    @SuppressWarnings("serial")
    public static class SyncAbortException extends GeneralServiceException {
        public SyncAbortException() {
            super();
        }

        public SyncAbortException(String str) {
            super(str);
        }

        public SyncAbortException(String str, Throwable nested) {
            super(str, nested);
        }

        public SyncAbortException(Throwable nested) {
            super(nested);
        }

        public SyncAbortException(String str, List<Object> errorMsgList, Map<String, Object> errorMsgMap, Map<String, Object> nestedServiceResult, Throwable nested) {
            super(str, errorMsgList, errorMsgMap, nestedServiceResult, nested);
        }
    }

    @SuppressWarnings("serial")
    public static abstract class SyncErrorException extends GeneralServiceException {
        public SyncErrorException() { super(); }
        public SyncErrorException(String str) { super(str); }
        public SyncErrorException(String str, Throwable nested) { super(str, nested); }
        public SyncErrorException(Throwable nested) { super(nested); }
        public SyncErrorException(String str, List<Object> errorMsgList, Map<String, Object> errorMsgMap, Map<String, Object> nestedServiceResult, Throwable nested) { super(str, errorMsgList, errorMsgMap, nestedServiceResult, nested); }
        public abstract void saveSyncErrorInfo(EntitySyncContext esc);
    }

    /** This class signifies an error condition, so the state of the EntitySync value and the EntitySyncHistory value in the datasource should be changed to reflect the error */
    @SuppressWarnings("serial")
    public static class SyncOtherErrorException extends SyncErrorException {
        public SyncOtherErrorException() { super(); }
        public SyncOtherErrorException(String str) { super(str); }
        public SyncOtherErrorException(String str, Throwable nested) { super(str, nested); }
        public SyncOtherErrorException(Throwable nested) { super(nested); }
        public SyncOtherErrorException(String str, List<Object> errorMsgList, Map<String, Object> errorMsgMap, Map<String, Object> nestedServiceResult, Throwable nested) { super(str, errorMsgList, errorMsgMap, nestedServiceResult, nested); }
        @Override
        public void saveSyncErrorInfo(EntitySyncContext esc) {
            if (esc != null) {
                List<Object> errorList = FastList.newInstance();
                esc.saveSyncErrorInfo("ESR_OTHER_ERROR", errorList);
                this.addErrorMessages(errorList);
            }
        }
    }

    /** This class signifies an error condition, so the state of the EntitySync value and the EntitySyncHistory value in the datasource should be changed to reflect the error */
    @SuppressWarnings("serial")
    public static class SyncDataErrorException extends SyncErrorException {
        public SyncDataErrorException() { super(); }
        public SyncDataErrorException(String str) { super(str); }
        public SyncDataErrorException(String str, Throwable nested) { super(str, nested); }
        public SyncDataErrorException(Throwable nested) { super(nested); }
        public SyncDataErrorException(String str, List<Object> errorMsgList, Map<String, Object> errorMsgMap, Map<String, Object> nestedServiceResult, Throwable nested) { super(str, errorMsgList, errorMsgMap, nestedServiceResult, nested); }
        @Override
        public void saveSyncErrorInfo(EntitySyncContext esc) {
            if (esc != null) {
                List<Object> errorList = FastList.newInstance();
                esc.saveSyncErrorInfo("ESR_DATA_ERROR", errorList);
                this.addErrorMessages(errorList);
            }
        }
    }

    /** This class signifies an error condition, so the state of the EntitySync value and the EntitySyncHistory value in the datasource should be changed to reflect the error */
    @SuppressWarnings("serial")
    public static class SyncServiceErrorException extends SyncErrorException {
        public SyncServiceErrorException() { super(); }
        public SyncServiceErrorException(String str) { super(str); }
        public SyncServiceErrorException(String str, Throwable nested) { super(str, nested); }
        public SyncServiceErrorException(Throwable nested) { super(nested); }
        public SyncServiceErrorException(String str, List<Object> errorMsgList, Map<String, Object> errorMsgMap, Map<String, Object> nestedServiceResult, Throwable nested) { super(str, errorMsgList, errorMsgMap, nestedServiceResult, nested); }
        @Override
        public void saveSyncErrorInfo(EntitySyncContext esc) {
            if (esc != null) {
                List<Object> errorList = FastList.newInstance();
                esc.saveSyncErrorInfo("ESR_SERVICE_ERROR", errorList);
                this.addErrorMessages(errorList);
            }
        }
    }
}
