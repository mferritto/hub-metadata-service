package com.welltok.hub.metadata.validator

import com.amazonaws.services.datapipeline.model.ValidationError


interface Validator<E> {
	
	boolean validate(E oldObject, E newObject, List<ValidationError> e)
}
