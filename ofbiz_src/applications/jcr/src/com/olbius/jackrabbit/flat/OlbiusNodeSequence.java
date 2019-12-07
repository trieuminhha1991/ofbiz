package com.olbius.jackrabbit.flat;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.flat.ItemSequence;
import org.apache.jackrabbit.commons.flat.TreeManager;
import org.apache.jackrabbit.commons.flat.TreeTraverser.ErrorHandler;

public class OlbiusNodeSequence extends ItemSequence{

	protected OlbiusNodeSequence(TreeManager treeManager, ErrorHandler errorHandler) {
		super(treeManager, errorHandler);
	}

	@Override
	protected Node getParent(String arg0) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected static class OlbiusNodeSequenceImpl extends NodeSequenceImpl {

		public OlbiusNodeSequenceImpl(TreeManager treeManager, ErrorHandler errorHandler) {
			super(treeManager, errorHandler);
		}
		
	}

}
