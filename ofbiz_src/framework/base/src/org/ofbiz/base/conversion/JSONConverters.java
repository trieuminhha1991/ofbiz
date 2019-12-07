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
package org.ofbiz.base.conversion;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.json.JSON;
import org.ofbiz.base.util.UtilGenerics;

/** JSON Converter classes. */
public class JSONConverters implements ConverterLoader {

	public static JSONToList JSONToList = new JSONToList();
	public static JSONToMap JSONToMap = new JSONToMap();
	public static MapToJSON MapToJSON = new MapToJSON();
	public static ListToJSON ListToJSON = new ListToJSON();

	public static class ObjectToJSONResultCreator<R extends JSONResult> implements ConverterCreator, ConverterLoader {
		public void loadConverters() {
			Converters.registerCreator(this);
		}

		public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
			if (!JSONResult.class.isAssignableFrom(targetClass)) {
				return null;
			}
			if (Collection.class.isAssignableFrom(sourceClass)) {
			} else if (Map.class.isAssignableFrom(sourceClass)) {
			} else if (Byte.class == sourceClass) {
			} else if (Character.class == sourceClass) {
			} else if (Double.class == sourceClass) {
			} else if (Float.class == sourceClass) {
			} else if (Integer.class == sourceClass) {
			} else if (Long.class == sourceClass) {
			} else if (Short.class == sourceClass) {
			} else {
				return null;
			}
			return UtilGenerics.cast(new ObjectToJSONWriterResult<S, JSONResult>(sourceClass,
					UtilGenerics.<Class<JSONResult>> cast(targetClass)));
		}
	}

	private static class ObjectToJSONWriterResult<S, T extends JSONResult> extends AbstractConverter<S, T> {
		public ObjectToJSONWriterResult(Class<S> sourceClass, Class<T> targetClass) {
			super(sourceClass, targetClass);
		}

		public T convert(S obj) throws ConversionException {
			try {
				T result = UtilGenerics.<T> cast(getTargetClass().newInstance());
				result.getWriter().write(obj);
				return result;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new ConversionException(e);
			}
		}
	}

	public static class JSONToList extends AbstractConverter<JSON, List<Object>> {
		public JSONToList() {
			super(JSON.class, List.class);
		}

		public List<Object> convert(JSON obj) throws ConversionException {
			try {
				return UtilGenerics.<List<Object>> cast(obj.toObject(List.class));
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}
	}

	public static class JSONToMap extends AbstractConverter<JSON, Map<String, Object>> {
		public JSONToMap() {
			super(JSON.class, Map.class);
		}

		public Map<String, Object> convert(JSON obj) throws ConversionException {
			try {
				return UtilGenerics.<Map<String, Object>> cast(obj.toObject(Map.class));
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}
	}

	public static class ListToJSON extends AbstractConverter<List<Object>, JSON> {
		public ListToJSON() {
			super(List.class, JSON.class);
		}

		public JSON convert(List<Object> obj) throws ConversionException {
			try {
				return JSON.from(obj);
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}
	}

	public static class MapToJSON extends AbstractConverter<Map<String, Object>, JSON> {
		public MapToJSON() {
			super(Map.class, JSON.class);
		}

		public JSON convert(Map<String, Object> obj) throws ConversionException {
			try {
				return JSON.from(obj);
			} catch (IOException e) {
				throw new ConversionException(e);
			}
		}
	}

	public void loadConverters() {
		Converters.loadContainedConverters(JSONConverters.class);
	}
}
