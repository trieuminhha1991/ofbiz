package com.olbius.basepos.lean;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepos.events.WebPosEvents;
import com.olbius.basepos.session.WebPosSession;

import javolution.util.FastMap;

public class POSWorker {
	public static String resource_error = "BasePosErrorUiLabels";
	public static String module = POSWorker.class.getName();
	private MessageWorker messageWorker;
	private DataWorker dataWorker;
	private WebPosSession webposSession;
	private String strCashAmount;
	private String strCreditCardAmount;
	
	public POSWorker(HttpServletRequest request, HttpServletResponse response){
		messageWorker = new MessageWorker(request, resource_error);
		HttpSession session = request.getSession();
		webposSession = (WebPosSession)session.getAttribute("webPosSession");
		strCashAmount = request.getParameter("amountCash");
		strCreditCardAmount = request.getParameter("amountCreditCard");
		dataWorker = new DataWorker(webposSession);
	}
	public String completeTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		// Make sure POSTransaction is initialized
		if(UtilValidate.isNotEmpty(webposSession)){
			AccountingWorker accountingWorker = new AccountingWorker(dataWorker, MultiOrganizationUtil.getCurrentOrganization(dataWorker.delegator));
			// Manipulate payment
			PaymentWorker paymentWorker = new PaymentWorker(dataWorker, accountingWorker);
			if(paymentWorker.validateAmount(strCashAmount, strCreditCardAmount)){
				try {
					OrderWorker orderWorker = new OrderWorker(dataWorker);
					ReturnWorker returnWorker = new ReturnWorker(dataWorker);
					InventoryWorker inventoryWorker = new InventoryWorker(dataWorker, accountingWorker);
					// Begin transaction
					TransactionUtil.begin(7200);
					// Create order
					if(returnWorker.determineReturnAble()){
						orderWorker.createOrderAndComplete();
						inventoryWorker.immediatelyFulfilledOrder();
					}
					// Create payment 
					paymentWorker.createPayment();
					paymentWorker.createReceivedPayments();
					// Create invoice
					InvoiceWorker invoiceWorker = new InvoiceWorker(dataWorker, accountingWorker);
					invoiceWorker.createInvoice();
					// Return
					returnWorker.createAndCompleteReturn();
					// Update Tx Log
					this.webposSession.getCurrentTransaction().updateTxLog();
					// Create PosHistory record
					createPosHistory();
					// End of transaction
					TransactionUtil.commit();
					// TODO Improve the following function
					WebPosEvents.emptyCartAndClearAutoSaveList(request, response);
					
				} catch (Exception e) {
					try {
						TransactionUtil.rollback();
					} catch (Exception e1) {
						Debug.logError(e1.getMessage(), module);
					}
					Debug.logError(e.getMessage(), module);
					return messageWorker.sendErrorMessage("BPOSCanNotCreateOrder");
				}
			}else{
				// There is invalid input amount
				return messageWorker.sendErrorMessage("BPOSAmountCreditCardParam");
			}
		}else{
			// Require login
			return messageWorker.sendErrorMessage("BPOSNotLoggedIn");
		}
		return MessageWorker.sendSuccessMessage();
	}
	
	private void createPosHistory() throws GenericServiceException{
		Map<String, Object> createPosHeader = FastMap.newInstance();
		createPosHeader.put("partyId", dataWorker.cart.getBillToCustomerPartyId());
		createPosHeader.put("posTerminalLogId", dataWorker.webPosTransaction.getTerminalLogId());
		createPosHeader.put("userLogin", dataWorker.posSession.getUserLogin());
		createPosHeader.put("returnId", dataWorker.getReturnId());
		createPosHeader.put("orderId", dataWorker.cart.getOrderId());
        if(dataWorker.webPosTransaction.getTerminalState().equals("POSTX_RETURNED")){
        	createPosHeader.put("returnCreatedBy", dataWorker.cart.getUserLogin().getString("partyId"));
        	createPosHeader.put("returnGrandTotal", dataWorker.getReturnGrandTotal());
        	createPosHeader.put("returnDate", UtilDateTime.nowTimestamp());
        	dataWorker.dispatcher.runSync("createPosHistoryReturnRecord", createPosHeader); 
        }else{
        	createPosHeader.put("createdBy", dataWorker.cart.getUserLogin().getString("partyId"));
        	createPosHeader.put("grandTotal", dataWorker.cart.getGrandTotal());
        	createPosHeader.put("orderDate", UtilDateTime.nowTimestamp());
        	dataWorker.dispatcher.runSync("createPosHistoryOrderRecord", createPosHeader); 
        }
	}
}
