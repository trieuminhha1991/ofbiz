package com.olbius.product.exception;

import org.ofbiz.service.GenericServiceException;

@SuppressWarnings("serial")
public class DuplicateProductIdException extends GenericServiceException {
	public DuplicateProductIdException(){
		super();
	}
    public DuplicateProductIdException(String str) {
        super(str);
    }

    public DuplicateProductIdException(String str, Throwable nested) {
        super(str, nested);
    }

    public DuplicateProductIdException(Throwable nested) {
        super(nested);
    }
}
