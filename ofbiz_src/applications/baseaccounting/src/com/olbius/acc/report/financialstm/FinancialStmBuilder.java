package com.olbius.acc.report.financialstm;

import com.olbius.acc.utils.ErrorUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

import java.util.List;
import java.util.Map;

public abstract class FinancialStmBuilder extends Fomular {

	public static final String module = FinancialStmBuilder.class.getName();
	public static final int timeout = 100000000;

	public abstract List<FinancialStm> buildFinStm(Map<String, Object> parameters, Delegator delegator);

	public JSONArray convertToJsonArray(Map<String, Object> parameters, Delegator delegator) {
		JSONArray jsonArray = new JSONArray();
		boolean beganTransaction = false;
		boolean okay = true;
		String mess = "";
		try {
			beganTransaction = TransactionUtil.begin(timeout);
			List<FinancialStm> listStms = buildFinStm(parameters, delegator);
			for (FinancialStm elm : listStms) {
				jsonArray.add(JSONObject.fromObject(elm));
			}
		} catch (GenericTransactionException e) {
			okay = false;
			mess = e.getMessage();
		} finally {
			if (!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, mess, null);
				} catch (GenericTransactionException gte) {
					ErrorUtils.processException(gte, module);
				}
			} else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					ErrorUtils.processException(gte, module);
				}
			}
		}
		return jsonArray;
	}
}