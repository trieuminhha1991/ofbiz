import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.*;
import java.util.List;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

completedTree =  FastList.newInstance();

orderBy = FastList.newInstance().add("glAccountId");
rootAccounts = delegator.findByAnd("GlAccount", [parentGlAccountId : null],null,false);
if (rootAccounts) {
    //child
    for(account in rootAccounts) {
        accountMap = FastMap.newInstance();
        String glAccountId = account.getString("glAccountId");
        accountMap.put("glAccountId", glAccountId);
        accountMap.put("accountCode", account.getString("accountCode"));
        accountMap.put("accountName", account.getString("accountName"));
        accountMap.put("postedBalance", account.getString("postedBalance"));
        accountMap.put("glTaxFormId", account.getString("glTaxFormId"));
        
        childAccounts = delegator.findByAnd("GlAccount", [parentGlAccountId : glAccountId],null,false);
        if (childAccounts) {
        	childTree =  FastList.newInstance();
        	for(childAccount in childAccounts) {
        		childAccountMap = FastMap.newInstance();
		        childAccountMap.put("glAccountId", childAccount.getString("glAccountId"));
		        childAccountMap.put("accountCode", childAccount.getString("accountCode"));
		        childAccountMap.put("accountName", childAccount.getString("accountName"));
		        childAccountMap.put("postedBalance", childAccount.getString("postedBalance"));
		        childAccountMap.put("glTaxFormId", childAccount.getString("glTaxFormId"));
		        childTree.add(childAccountMap);
        	}
        	accountMap.put("child", childTree);
        }
        
        completedTree.add(accountMap);
    }
}
// The complete tree list for the category tree
context.completedTree = completedTree;
