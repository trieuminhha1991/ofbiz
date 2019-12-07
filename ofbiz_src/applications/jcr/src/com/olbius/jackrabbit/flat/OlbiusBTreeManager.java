package com.olbius.jackrabbit.flat;

import java.util.Comparator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.flat.BTreeManager;

public class OlbiusBTreeManager extends BTreeManager{

	public OlbiusBTreeManager(Node root, int minChildren, int maxChildren, Comparator<String> order, boolean autoSave) throws RepositoryException {
		super(root, minChildren, maxChildren, order, autoSave);
	}

}
