package com.olbius.basepos.session;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;
import com.olbius.basepos.transaction.WebPosTransaction;

@SuppressWarnings("serial")
public class WebPosSession implements Serializable{
    public static final String module = WebPosSession.class.getName();

    private String id = null;
    private Map<String, Object> attributes = FastMap.newInstance();
    private GenericValue userLogin = null;
    private Locale locale = null;
    private String productStoreId = null;
    private String facilityId = null;
    private String currencyUomId = null;
    private transient Delegator delegator = null;
    private String delegatorName = null;
    private transient LocalDispatcher dispatcher = null;
    private String dispatcherName = null;
    private Boolean mgrLoggedIn = null;
    private transient WebPosTransaction webPosTransaction = null;
    private ShoppingCart cart = null;

    public WebPosSession(String id, Map<String, Object> attributes, GenericValue userLogin, Locale locale, String productStoreId, String facilityId, String currencyUomId, Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart) {
        this.id = id;
        if (attributes!=null){
        	this.attributes = attributes;
        }
        this.userLogin = userLogin;
        this.locale = locale;
        this.productStoreId = productStoreId;
        this.facilityId = facilityId;
        this.currencyUomId = currencyUomId;

        if (UtilValidate.isNotEmpty(delegator)) {
            this.delegator = delegator;
            this.delegatorName = delegator.getDelegatorName();
        } else {
            this.delegator = this.getDelegator();
            this.delegatorName = delegator.getDelegatorName();
        }
        if (UtilValidate.isNotEmpty(dispatcher)) {
        	 this.dispatcher = dispatcher;
             this.dispatcherName = dispatcher.getName();
        } else {
        	 this.dispatcher =  getDispatcher();
             this.dispatcherName = dispatcher.getName();
        }
        this.cart = cart;
        Debug.logInfo("Created WebPosSession [" + id + "]", module);
    }

    public GenericValue getUserLogin() {
        return this.userLogin;
    }
    
    public void setUserLogin(GenericValue userLogin) {
        this.userLogin = userLogin;
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public String getId() {
        return this.id;
    }

    public String getUserLoginId() {
        if (UtilValidate.isEmpty(getUserLogin())) {
            return null;
        } else {
            return this.getUserLogin().getString("userLoginId");
        }
    }

    public String getUserPartyId() {
        if (UtilValidate.isEmpty(getUserLogin())) {
            return null;
        } else {
            return this.getUserLogin().getString("partyId");
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getProductStoreId() {
        return this.productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getCurrencyUomId() {
        return this.currencyUomId;
    }

    public void setCurrencyUomId(String currencyUomId) {
        this.currencyUomId = currencyUomId;
    }

    public Delegator getDelegator() {
        if (UtilValidate.isEmpty(delegator)) {
            delegator = DelegatorFactory.getDelegator(delegatorName);
        }
        return delegator;
    }

    public LocalDispatcher getDispatcher() {
    	if (UtilValidate.isEmpty(dispatcher)) {
    		dispatcher = ServiceContainer.getLocalDispatcher(dispatcherName, getDelegator());
        }
        return dispatcher;
    }

    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }
    
    public ShoppingCart getCart() {
        return this.cart;
    }

    public void logout() {
        if (UtilValidate.isNotEmpty(webPosTransaction)) {
            webPosTransaction.closeTx();
            webPosTransaction = null;
        }

        if (UtilValidate.isNotEmpty(getUserLogin())) {
            LoginWorker.setLoggedOut(this.getUserLogin().getString("userLoginId"), this.getDelegator());
        }
    }

    public void login(String username, String password, LocalDispatcher dispatcher) throws UserLoginFailure {
        this.checkLogin(username, password, dispatcher);
    }

    public GenericValue checkLogin(String username, String password, LocalDispatcher dispatcher) throws UserLoginFailure {
        // check the required parameters and objects
        if (UtilValidate.isEmpty(dispatcher)) {
            throw new UserLoginFailure(UtilProperties.getMessage("BasePosErrorUiLabels", "BPOSUnableToLogIn", getLocale()));
        }
        if (UtilValidate.isEmpty(username)) {
            throw new UserLoginFailure(UtilProperties.getMessage("PartyUiLabels", "PartyUserNameMissing", getLocale()));
        }
        if (UtilValidate.isEmpty(password)) {
            throw new UserLoginFailure(UtilProperties.getMessage("PartyUiLabels", "PartyPasswordMissing", getLocale()));
        }

        // call the login service
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new UserLoginFailure(e);
        } catch (Throwable t) {
            Debug.logError(t, "Throwable caught!", module);
        }

        // check for errors
        if (ServiceUtil.isError(result)) {
            throw new UserLoginFailure(ServiceUtil.getErrorMessage(result));
        } else {
            GenericValue ul = (GenericValue) result.get("userLogin");
            if (ul == null) {
                throw new UserLoginFailure(UtilProperties.getMessage("BasePosErrorUiLabels", "BPOSUserLoginNotValid", getLocale()));
            }
            return ul;
        }
    }

    public boolean hasRole(GenericValue userLogin, String roleTypeId) {
        if (UtilValidate.isEmpty(userLogin) || UtilValidate.isEmpty(roleTypeId)) {
            return false;
        }
        String partyId = userLogin.getString("partyId");
        GenericValue partyRole = null;
        try {
            partyRole = getDelegator().findOne("PartyRole", false, "partyId", partyId, "roleTypeId", roleTypeId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return false;
        }

        if (UtilValidate.isEmpty(partyRole)) {
            return false;
        }

        return true;
    }

    public boolean isManagerLoggedIn() {
        if (UtilValidate.isEmpty(mgrLoggedIn)) {
            mgrLoggedIn = hasRole(getUserLogin(), "MANAGER");
        }
        return mgrLoggedIn.booleanValue();
    }

    public WebPosTransaction getCurrentTransaction() {
        if (UtilValidate.isEmpty(webPosTransaction)) {
            webPosTransaction = new WebPosTransaction(this);
        }
        return webPosTransaction;
    }

    public void setCurrentTransaction(WebPosTransaction webPosTransaction) {
        this.webPosTransaction = webPosTransaction;
    }   
    
    public void setCurrentTransaction() {
        this.webPosTransaction = null;
    } 
    
    @SuppressWarnings("serial")
    public class UserLoginFailure extends GeneralException {
        public UserLoginFailure() {
            super();
        }

        public UserLoginFailure(String str) {
            super(str);
        }

        public UserLoginFailure(String str, Throwable nested) {
            super(str, nested);
        }

        public UserLoginFailure(Throwable nested) {
            super(nested);
        }
    }
}
