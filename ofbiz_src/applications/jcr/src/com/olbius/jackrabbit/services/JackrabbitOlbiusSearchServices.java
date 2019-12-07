package com.olbius.jackrabbit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.api.Search;
import com.olbius.jackrabbit.api.SearchImpl;

public class JackrabbitOlbiusSearchServices {
	public final static String module = JackrabbitOlbiusSearchServices.class.getName();

	public static Map<String, Object> jackrabbitSearchByName(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodeNameLike = (String) context.get("nodeNameLike");

		String folder = (String) context.get("folder");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Search search = new SearchImpl(jcrSession);

				String contraint = search.getCondition(search.DynamicOperand(Search.LOWER, search.DynamicOperand(Search.LOCALNAME, null)),
						Search.LIKE, "%" + nodeNameLike + "%");
				if (folder != null) {
					contraint = search.getConstraint(Search.AND, contraint, search.getConstraint(Search.ISDESCENDANTNODE, "[nt:file]", folder),
							false);
				}

				String sqlQuery = search.getQuery("*", "[nt:file]", contraint, null);

				List<String> nodeMatches = search.search(sqlQuery);

				result.put("nodeMatches", nodeMatches);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}

		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		return result;
	}

	public static Map<String, Object> jackrabbitSearchBySql(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String sqlQuery = (String) context.get("sqlQuery");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				QueryManager manager = jcrSession.getWorkspace().getQueryManager();

				Query query = manager.createQuery(sqlQuery, Query.JCR_SQL2);

				QueryResult sqlResult = query.execute();

				NodeIterator nodes = sqlResult.getNodes();

				List<String> nodeMatches = new ArrayList<String>();

				while (nodes.hasNext()) {
					Node node = nodes.nextNode();
					nodeMatches.add(node.getPath());
				}

				result.put("nodeMatches", nodeMatches);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}

		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		return result;
	}

}
