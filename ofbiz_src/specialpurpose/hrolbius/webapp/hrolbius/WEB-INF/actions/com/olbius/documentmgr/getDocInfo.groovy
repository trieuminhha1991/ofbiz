dataResourceId = parameters.dataResourceId;
dataResource = delegator.findOne("DataResource", [dataResourceId : dataResourceId], false);
String path = dataResource.objectInfo;
path = path.substring(34);
context.path = path;