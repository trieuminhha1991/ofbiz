package com.olbius.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ofbiz.base.metrics.MetricsFactory;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelPermGroup;
import org.ofbiz.service.ModelPermission;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ModelServiceIface;
import org.reflections.Reflections;
import com.olbius.security.api.Application;
import com.olbius.service.annotations.Attribute;
import com.olbius.service.annotations.Attributes;
import com.olbius.service.annotations.AutoAttribute;
import com.olbius.service.annotations.AutoAttributes;
import com.olbius.service.annotations.Description;
import com.olbius.service.annotations.Disable;
import com.olbius.service.annotations.Implement;
import com.olbius.service.annotations.Implements;
import com.olbius.service.annotations.Invoke;
import com.olbius.service.annotations.Metric;
import com.olbius.service.annotations.Namespace;
import com.olbius.service.annotations.OverrideAttribute;
import com.olbius.service.annotations.OverrideAttributes;
import com.olbius.service.annotations.Permission;
import com.olbius.service.annotations.Service;
import com.olbius.service.annotations.Services;
import com.olbius.service.annotations.Validate;
import com.olbius.service.annotations.type.HtmlType;
import com.olbius.service.annotations.type.ModeType;
import com.olbius.service.callback.InvokeCallback;
import com.olbius.service.callback.OlbiusCallback;
import com.olbius.service.callback.StaticCallback;
import com.olbius.service.permission.ModelOlbiusPermission;

import javolution.util.FastList;

public class OlbiusServiceReader {

	public static final String module = OlbiusServiceReader.class.getName();

	private final Reflections reflections;
	protected DispatchContext dctx = null;

	public static Map<String, ModelService> getModelServiceMap(DispatchContext dctx) {
		OlbiusServiceReader reader = new OlbiusServiceReader(dctx);
		return reader.getModelServices();
	}

	public OlbiusServiceReader(DispatchContext dctx) {
		this.dctx = dctx;
		this.reflections = new Reflections("com.olbius");
	}

	private boolean isAnnotationPresent(Object obj, Class<? extends Annotation> cl) {
		if (obj instanceof Class) {
			return ((Class<?>) obj).isAnnotationPresent(cl);
		}
		if (obj instanceof Method) {
			return ((Method) obj).isAnnotationPresent(cl);
		}
		return false;
	}

	private <A extends Annotation> A getAnnotation(Object obj, Class<A> cl) {
		if (obj instanceof Class) {
			return ((Class<?>) obj).getAnnotation(cl);
		}
		if (obj instanceof Method) {
			return ((Method) obj).getAnnotation(cl);
		}
		return null;
	}

	private String getLocation(Object obj) {
		if (obj instanceof Class) {
			return ((Class<?>) obj).getName();
		}
		if (obj instanceof Method) {
			return ((Method) obj).getDeclaringClass().getName();
		}
		return null;
	}

	private String getDefinitionLocation(Object obj) {
		if (obj instanceof Class) {
			return ((Class<?>) obj).getSimpleName();
		}
		if (obj instanceof Method) {
			return ((Method) obj).getDeclaringClass().getSimpleName();
		}
		return null;
	}

	private OlbiusModelService createModelService(Object obj) {
		OlbiusModelService service = new OlbiusModelService();

		Service model = getAnnotation(obj, Service.class);

		service.name = model.name();
		service.definitionLocation = getDefinitionLocation(obj);
		service.engineName = model.engine();
		service.location = getLocation(obj);
		service.semaphore = model.semaphore().value();
		service.defaultEntityName = model.defaultEntityName();
		service.fromLoader = model.loader();

		// these default to true; if anything but true, make false
		service.auth = model.auth();
		service.export = model.export();
		service.debug = model.debug();

		// these defaults to false; if anything but false, make it true
		service.validate = model.validate();
		service.useTransaction = model.useTransaction();
		service.requireNewTransaction = model.requireNewTransaction();
		service.hideResultInLog = model.hideResultInLog();

		// set the semaphore sleep/wait times
		service.semaphoreWait = model.semaphoreWaitSeconds();
		service.semaphoreSleep = model.semaphoreSleep();

		// set the max retry field
		service.maxRetry = model.maxRetry();

		// get the timeout and convert to int
		service.transactionTimeout = model.transactionTimeout();

		if (isAnnotationPresent(obj, Description.class)) {
			service.description = getAnnotation(obj, Description.class).value();
		}
		if (isAnnotationPresent(obj, Namespace.class)) {
			service.nameSpace = getAnnotation(obj, Namespace.class).value();
		}

		this.createPermGroups(obj, service, model.permission());
		this.createImplDefs(obj, service);
		this.createAutoAttrDefs(obj, service);
		this.createAttrDefs(obj, service);
		this.createOverrideDefs(obj, service);

		// Get metrics.
		if (isAnnotationPresent(obj, Metric.class)) {
			Metric metric = getAnnotation(obj, Metric.class);
			int estimationSize = UtilProperties.getPropertyAsInteger("serverstats", "metrics.estimation.size", 100);
			long estimationTime = UtilProperties.getPropertyAsLong("serverstats", "metrics.estimation.time", 1000);
			double smoothing = UtilProperties.getPropertyNumber("serverstats", "metrics.smoothing.factor", 0.7);
			service.metrics = MetricsFactory.getInstance(metric.name(),
					metric.estimationSize() == 0 ? estimationSize : metric.estimationSize(),
					metric.estimationTime() == 0 ? estimationTime : metric.estimationTime(),
					metric.smoothing() == 0 ? smoothing : metric.smoothing(), metric.threshold());
		}
		return service;
	}

	private void createPermGroups(Object obj, ModelService model, String permission) {

		if (isAnnotationPresent(obj, Permission.class) && getAnnotation(obj, Permission.class).value()) {
			ModelPermGroup group = new ModelPermGroup();
			group.joinType = ModelPermGroup.PERM_JOIN_AND;

			ModelPermission perm = new ModelOlbiusPermission(Application.SERVICE, model.name);
			perm.nameOrRole = permission;
			perm.serviceModel = model;
			group.permissions.add(perm);

			model.permissionGroups.add(group);
		}

	}

	private void createImplDefs(Object obj, ModelService service) {
		if (isAnnotationPresent(obj, Implements.class)) {
			Implement[] impls = getAnnotation(obj, Implements.class).value();
			if (impls != null) {
				for (Implement impl : impls) {
					String serviceName = impl.service();
					boolean optional = impl.optional();
					if (serviceName != null && !serviceName.isEmpty()) {
						service.implServices.add(new ModelServiceIface(serviceName, optional));
					}
				}
			}
		}
	}

	private void createAutoAttrDefs(Object obj, ModelService service) {
		if (isAnnotationPresent(obj, AutoAttributes.class)) {
			AutoAttribute[] attributes = getAnnotation(obj, AutoAttributes.class).value();
			if (attributes != null) {
				for (AutoAttribute attribute : attributes) {
					createAutoAttrDef(attribute, service);
				}
			}
		}
	}

	private void createAutoAttrDef(AutoAttribute attribute, ModelService service) {
		// get the entity name; first from the auto-attributes then from the
		// service def
		String entityName = attribute.entity();
		if (UtilValidate.isEmpty(entityName)) {
			entityName = service.defaultEntityName;
			if (UtilValidate.isEmpty(entityName)) {
				Debug.logWarning(
						"Auto-Attribute does not specify an entity-name; not default-entity on service definition",
						module);
			}
		}

		// get the include type 'pk|nonpk|all'
		String includeType = attribute.include().value();
		boolean includePk = "pk".equals(includeType) || "all".equals(includeType);
		boolean includeNonPk = "nonpk".equals(includeType) || "all".equals(includeType);

		// need a delegator for this
		Delegator delegator = dctx.getDelegator();
		if (delegator == null) {
			Debug.logWarning("Cannot use auto-attribute fields with a null delegator", module);
		}

		if (delegator != null && entityName != null) {
			Map<String, ModelParam> modelParamMap = new LinkedHashMap<String, ModelParam>();
			try {
				ModelEntity entity = delegator.getModelEntity(entityName);
				if (entity == null) {
					throw new GeneralException("Could not find entity with name [" + entityName + "]");
				}
				Iterator<ModelField> fieldsIter = entity.getFieldsIterator();
				if (fieldsIter != null) {
					while (fieldsIter.hasNext()) {
						ModelField field = fieldsIter.next();
						if ((!field.getIsAutoCreatedInternal())
								&& ((field.getIsPk() && includePk) || (!field.getIsPk() && includeNonPk))) {
							ModelFieldType fieldType = delegator.getEntityFieldType(entity, field.getType());
							if (fieldType == null) {
								throw new GeneralException(
										"Null field type from delegator for entity [" + entityName + "]");
							}
							ModelParam param = new ModelParam();
							param.entityName = entityName;
							param.fieldName = field.getName();
							param.name = field.getName();
							param.type = fieldType.getJavaType();
							// this is a special case where we use something
							// different in the service layer than we do in the
							// entity/data layer
							if ("java.sql.Blob".equals(param.type)) {
								param.type = "java.nio.ByteBuffer";
							}
							param.mode = attribute.mode().value();
							param.optional = attribute.optional(); // default to false
							param.formDisplay = attribute.formDisplay(); // default to true
							param.allowHtml = attribute.allowHtml().value(); // default to none
							modelParamMap.put(field.getName(), param);
						}
					}

					// get the excludes list; and remove those from the map
					if (attribute.excludes() != null) {
						for (String exclude : attribute.excludes()) {
							modelParamMap.remove(exclude);
						}
					}

					// now add in all the remaining params
					for (ModelParam thisParam : modelParamMap.values()) {
						// Debug.logInfo("Adding Param to " + service.name + ":
						// " + thisParam.name + " [" + thisParam.mode + "] " +
						// thisParam.type + " (" + thisParam.optional + ")",
						// module);
						service.addParam(thisParam);
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, "Problem loading auto-attributes [" + entityName + "] for " + service.name, module);
			} catch (GeneralException e) {
				Debug.logError(e, "Cannot load auto-attributes : " + e.getMessage() + " for " + service.name, module);
			}
		}
	}

	private void createAttrDefs(Object obj, ModelService service) {
		// Add in the defined attributes (override the above defaults if
		// specified)
		if (isAnnotationPresent(obj, Attributes.class)) {
			Attribute[] attributes = getAnnotation(obj, Attributes.class).value();
			if (attributes != null) {
				for (Attribute attribute : attributes) {
					createAttrDef(attribute, service);
				}
			}
		}

		// Add the default optional parameters
		ModelParam def;

		// responseMessage
		def = new ModelParam();
		def.name = ModelService.RESPONSE_MESSAGE;
		def.type = "String";
		def.mode = "OUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// errorMessage
		def = new ModelParam();
		def.name = ModelService.ERROR_MESSAGE;
		def.type = "String";
		def.mode = "OUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// errorMessageList
		def = new ModelParam();
		def.name = ModelService.ERROR_MESSAGE_LIST;
		def.type = "java.util.List";
		def.mode = "OUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// successMessage
		def = new ModelParam();
		def.name = ModelService.SUCCESS_MESSAGE;
		def.type = "String";
		def.mode = "OUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// successMessageList
		def = new ModelParam();
		def.name = ModelService.SUCCESS_MESSAGE_LIST;
		def.type = "java.util.List";
		def.mode = "OUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// userLogin
		def = new ModelParam();
		def.name = "userLogin";
		def.type = "org.ofbiz.entity.GenericValue";
		def.mode = "INOUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// login.username
		def = new ModelParam();
		def.name = "login.username";
		def.type = "String";
		def.mode = "IN";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// login.password
		def = new ModelParam();
		def.name = "login.password";
		def.type = "String";
		def.mode = "IN";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// Locale
		def = new ModelParam();
		def.name = "locale";
		def.type = "java.util.Locale";
		def.mode = "INOUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
		// timeZone
		def = new ModelParam();
		def.name = "timeZone";
		def.type = "java.util.TimeZone";
		def.mode = "INOUT";
		def.optional = true;
		def.internal = true;
		service.addParam(def);
	}

	private void createAttrDef(Attribute attribute, ModelService service) {
		ModelParam param = new ModelParam();

		param.name = attribute.name();
		param.description = attribute.description();
		param.type = attribute.type().getName();
		param.mode = attribute.mode().value();
		param.entityName = attribute.entity();
		param.fieldName = attribute.fieldName();
		param.requestAttributeName = attribute.requestAttributeName();
		param.sessionAttributeName = attribute.sessionAttributeName();
		param.stringMapPrefix = attribute.stringMapPrefix();
		param.stringListSuffix = attribute.stringListSuffix();
		param.formLabel = attribute.formLabel();
		param.optional = attribute.optional(); // default to false
		param.formDisplay = attribute.formDisplay(); // default to true
		param.allowHtml = attribute.allowHtml().value(); // default to none

		// default value
		String defValue = attribute.defaultValue();
		if (UtilValidate.isNotEmpty(defValue)) {
			if (Debug.verboseOn())
				Debug.logVerbose("Got a default-value [" + defValue + "] for service attribute [" + service.name + "."
						+ param.name + "]", module);
			param.setDefaultValue(defValue.intern());
		}

		// set the entity name to the default if not specified
		if (param.entityName.length() == 0) {
			param.entityName = service.defaultEntityName;
		}

		// set the field-name to the name if entity name is specified but no
		// field-name
		if (param.fieldName.length() == 0 && param.entityName.length() > 0) {
			param.fieldName = param.name;
		}

		// set the validators
		this.addValidators(attribute.validate(), param);
		service.addParam(param);
	}

	private void addValidators(Validate[] validate, ModelParam param) {
		if (validate.length > 0) {
			// always clear out old ones; never append
			param.validators = FastList.newInstance();

			String methodName = validate[0].method();
			String className = validate[0].classValidate().getName();

			if (validate[0].failMessage() != null) {
				String message = validate[0].failMessage();
				param.addValidator(className, methodName, message);
			} else if (validate[0].failProperty().length > 0) {
				String resource = validate[0].failProperty()[0].resource();
				String property = validate[0].failProperty()[0].property();
				param.addValidator(className, methodName, resource, property);
			}
		}
	}

	private void createOverrideDefs(Object obj, ModelService service) {

		if (isAnnotationPresent(obj, OverrideAttributes.class)) {
			OverrideAttribute[] attributes = getAnnotation(obj, OverrideAttributes.class).value();
			if (attributes != null) {
				for (OverrideAttribute attribute : attributes) {
					createOverrideDef(attribute, service);
				}
			}
		}
	}

	private void createOverrideDef(OverrideAttribute attribute, ModelService service) {
		String name = attribute.name();
		ModelParam param = service.getParam(name);
		boolean directToParams = true;
		if (param == null) {
			if (!service.inheritedParameters()
					&& (service.implServices.size() > 0 || "group".equals(service.engineName))) {
				// create a temp def to place in the ModelService
				// this will get read when we read implemented services
				directToParams = false;
				param = new ModelParam();
				param.name = name;
			} else {
				Debug.logWarning(
						"No parameter found for override parameter named: " + name + " in service " + service.name,
						module);
			}
		}

		if (param != null) {
			// set only modified values
			if (!void.class.equals(attribute.type())) {
				param.type = attribute.type().getName();
			}
			if (!ModeType.NULL.equals(attribute.mode())) {
				param.mode = attribute.mode().value();
			}
			if (UtilValidate.isNotEmpty(attribute.entity())) {
				param.entityName = attribute.entity();
			}
			if (UtilValidate.isNotEmpty(attribute.fieldName())) {
				param.fieldName = attribute.fieldName();
			}
			if (UtilValidate.isNotEmpty(attribute.formLabel())) {
				param.formLabel = attribute.formLabel();
			}
			if (attribute.optional().value() != null) {
				param.optional = attribute.optional().value().booleanValue();
				param.overrideOptional = true;
			}
			if (attribute.formDisplay().value() != null) {
				param.formDisplay = attribute.formDisplay().value().booleanValue();
				param.overrideFormDisplay = true;
			}

			if (!HtmlType.NULL.equals(attribute.allowHtml())) {
				param.allowHtml = attribute.allowHtml().value();
			}

			// default value
			String defValue = attribute.defaultValue();
			if (UtilValidate.isNotEmpty(defValue)) {
				param.setDefaultValue(defValue);
			}

			// override validators
			this.addValidators(attribute.validate(), param);

			if (directToParams) {
				service.addParam(param);
			} else {
				service.overrideParameters.add(param);
			}
		}
	}

	private void createModelService(Map<String, ModelService> modelServices, Object obj) {

		if (isAnnotationPresent(obj, Disable.class) && getAnnotation(obj, Disable.class).value()) {
			return;
		}

		Service service = getAnnotation(obj, Service.class);
		String serviceName = service.name();
		if (modelServices.containsKey(serviceName)) {
			Debug.logWarning("WARNING: Service " + serviceName + " is defined more than once, "
					+ "most recent will over-write previous definition(s)", module);
		}

		OlbiusModelService svc = createModelService(obj);

		if (svc != null) {

			if (obj instanceof Class) {
				Class<?> cl = (Class<?>) obj;
				if (AbstactService.class.isAssignableFrom(cl)) {
					svc.setExecuteCallback(new OlbiusCallback(svc));
				} else {
					for (Method method : cl.getDeclaredMethods()) {
						if (isAnnotationPresent(method, Invoke.class)) {
							svc.invoke = method.getName();
							break;
						}
					}
					svc.setExecuteCallback(new InvokeCallback(svc));
				}
			} else {
				Method m = (Method) obj;
				svc.invoke = m.getName();
				if (Modifier.isStatic(m.getModifiers())) {
					svc.setExecuteCallback(new StaticCallback(svc));
				} else {
					svc.setExecuteCallback(new InvokeCallback(svc));
				}
			}

			modelServices.put(serviceName, svc);
		} else {
			Debug.logWarning(
					"-- -- SERVICE ERROR:getModelService: Could not create service for serviceName: " + serviceName,
					module);
		}
	}

	private Map<String, ModelService> getModelServices() {
		Map<String, ModelService> modelServices = new HashMap<String, ModelService>();

		for (Class<?> cl : reflections.getTypesAnnotatedWith(Service.class)) {
			if (isAnnotationPresent(cl, Service.class)) {
				createModelService(modelServices, cl);
			}
		}
		
		for (Class<?> cl : reflections.getTypesAnnotatedWith(Services.class)) {
			for (Method method : cl.getDeclaredMethods()) {
				if (isAnnotationPresent(method, Service.class)) {
					createModelService(modelServices, method);
				}
			}
		}

		return modelServices;
	}

}
