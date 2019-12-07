package com.olbius.catalina.container;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.filters.RequestDumperFilter;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.container.ClassLoaderContainer;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerConfig.Container.Property;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.catalina.container.CrossSubdomainSessionValve;
import org.ofbiz.catalina.container.SslAcceleratorValve;
import org.w3c.dom.Document;

public class CatalinaContainer extends org.ofbiz.catalina.container.CatalinaContainer {
	
	@Override
	protected Engine createEngine(Property engineConfig) throws ContainerException {
		if (tomcat == null) {
            throw new ContainerException("Cannot create Engine without Tomcat instance!");
        }

        ContainerConfig.Container.Property defaultHostProp = engineConfig.getProperty("default-host");
        if (defaultHostProp == null) {
            throw new ContainerException("default-host element of server property is required for catalina!");
        }

        String engineName = engineConfig.name;
        String hostName = defaultHostProp.value;

        StandardEngine engine = new StandardEngine();
        engine.setName(engineName);
        engine.setDefaultHost(hostName);

        // set the JVM Route property (JK/JK2)
        String jvmRoute = ContainerConfig.getPropertyValue(engineConfig, "jvm-route", null);
        if (jvmRoute != null) {
            engine.setJvmRoute(jvmRoute);
        }

        // create the default realm -- TODO: make this configurable
        String dbConfigPath = new File(System.getProperty("catalina.home"), "catalina-users.xml").getAbsolutePath();
        MemoryRealm realm = new MemoryRealm();
        realm.setPathname(dbConfigPath);
        engine.setRealm(realm);

        // cache the engine
        engines.put(engine.getName(), engine);

        // create a default virtual host; others will be created as needed
        Host host = createHost(engine, hostName);
        hosts.put(engineName + "._DEFAULT", host);
        engine.addChild(host);

        // configure the CrossSubdomainSessionValve
        boolean enableSessionValve = ContainerConfig.getPropertyValue(engineConfig, "enable-cross-subdomain-sessions", false);
        if (enableSessionValve) {
            CrossSubdomainSessionValve sessionValve = new CrossSubdomainSessionValve();
            engine.addValve(sessionValve);
        }

        // configure the access log valve
        String logDir = ContainerConfig.getPropertyValue(engineConfig, "access-log-dir", null);
        AccessLogValve al = null;
        if (logDir != null) {
            al = new AccessLogValve();
            if (!logDir.startsWith("/")) {
                logDir = System.getProperty("ofbiz.home") + "/" + logDir;
            }
            File logFile = new File(logDir);
            if (!logFile.isDirectory()) {
                throw new ContainerException("Log directory [" + logDir + "] is not available; make sure the directory is created");
            }
            al.setDirectory(logFile.getAbsolutePath());
        }

        // configure the SslAcceleratorValve
        String sslAcceleratorPortStr = ContainerConfig.getPropertyValue(engineConfig, "ssl-accelerator-port", null);
        if (UtilValidate.isNotEmpty(sslAcceleratorPortStr)) {
            Integer sslAcceleratorPort = Integer.valueOf(sslAcceleratorPortStr);
            SslAcceleratorValve sslAcceleratorValve = new SslAcceleratorValve();
            sslAcceleratorValve.setSslAcceleratorPort(sslAcceleratorPort);
            engine.addValve(sslAcceleratorValve);
        }


        String alp2 = ContainerConfig.getPropertyValue(engineConfig, "access-log-pattern", null);
        if (al != null && !UtilValidate.isEmpty(alp2)) {
            al.setPattern(alp2);
        }

        String alp3 = ContainerConfig.getPropertyValue(engineConfig, "access-log-prefix", null);
        if (al != null && !UtilValidate.isEmpty(alp3)) {
            al.setPrefix(alp3);
        }


        boolean alp4 = ContainerConfig.getPropertyValue(engineConfig, "access-log-resolve", true);
        if (al != null) {
            al.setResolveHosts(alp4);
        }

        boolean alp5 = ContainerConfig.getPropertyValue(engineConfig, "access-log-rotate", false);
        if (al != null) {
            al.setRotatable(alp5);
        }

        if (al != null) {
            engine.addValve(al);
        }

        tomcat.getService().setContainer(engine);
        return engine;
	}
	
	@Override
	protected Callable<Context> createContext(final ComponentConfig.WebappInfo appInfo) throws ContainerException {
		Debug.logInfo("createContext(" + appInfo.name + ")", module);
        final Engine engine = engines.get(appInfo.server);
        if (engine == null) {
            Debug.logWarning("Server with name [" + appInfo.server + "] not found; not mounting [" + appInfo.name + "]", module);
            return null;
        }
        List<String> virtualHosts = appInfo.getVirtualHosts();
        final Host host;
        if (UtilValidate.isEmpty(virtualHosts)) {
            host = hosts.get(engine.getName() + "._DEFAULT");
        } else {
            // assume that the first virtual-host will be the default; additional virtual-hosts will be aliases
            Iterator<String> vhi = virtualHosts.iterator();
            String hostName = vhi.next();

            boolean newHost = false;
            if (hosts.containsKey(engine.getName() + "." + hostName)) {
                host = hosts.get(engine.getName() + "." + hostName);
            } else {
                host = createHost(engine, hostName);
                newHost = true;
            }
            while (vhi.hasNext()) {
                host.addAlias(vhi.next());
            }

            if (newHost) {
                hosts.put(engine.getName() + "." + hostName, host);
                engine.addChild(host);
            }
        }

        if (host instanceof StandardHost) {
            // set the catalina's work directory to the host
            StandardHost standardHost = (StandardHost) host;
            standardHost.setWorkDir(new File(System.getProperty(Globals.CATALINA_HOME_PROP)
                    , "work" + File.separator + engine.getName() + File.separator + host.getName()).getAbsolutePath());
        }

        return new Callable<Context>() {
            @Override
			public Context call() throws ContainerException, LifecycleException {
                StandardContext context = configureContext(engine, host, appInfo);
                context.setParent(host);
                context.start();
                return context;
            }
        };
    }
	
	private Manager manager() {
    	
		/*GenericHelperInfo ofbiz = ((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz");
		
		Datasource datasourceInfo = EntityConfigUtil.getDatasource(ofbiz.getHelperBaseName());
    	
    	InlineJdbc jdbcElement = datasourceInfo.getInlineJdbc();
    	
    	String _uri_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideJdbcUri()) ? ofbiz.getOverrideJdbcUri() : jdbcElement.getJdbcUri();
    	String _user_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideUsername()) ? ofbiz.getOverrideUsername() : jdbcElement.getJdbcUsername();
    	String _pwd_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverridePassword()) ? ofbiz.getOverridePassword() : EntityConfigUtil.getJdbcPassword(jdbcElement);
    	*/
		PersistentManager manager = new PersistentManager();
    	manager.setMaxIdleBackup(10);
    	/*JDBCStore store = new JDBCStore();
    	store.setConnectionURL(_uri_ofbiz);
    	store.setConnectionName(_user_ofbiz);
    	store.setConnectionPassword(_pwd_ofbiz);
    	store.setDriverName(jdbcElement.getJdbcDriver());
    	store.setSessionAppCol("app_name");
		store.setSessionDataCol("session_data");
		store.setSessionIdCol("session_id");
		store.setSessionLastAccessedCol("last_access");
		store.setSessionMaxInactiveCol("max_inactive");
		store.setSessionTable("tomcat_sessions");
		store.setSessionValidCol("valid_session");*/
    	OlbiusStore store = new OlbiusStore(delegator);
    	manager.setStore(store);
    	return manager;
    }
	
	private StandardContext configureContext(Engine engine, Host host, ComponentConfig.WebappInfo appInfo) throws ContainerException {
        // webapp settings
        Map<String, String> initParameters = appInfo.getInitParameters();

        // set the root location (make sure we set the paths correctly)
        String location = appInfo.componentConfig.getRootLocation() + appInfo.location;
        location = location.replace('\\', '/');
        if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        // get the mount point
        String mount = appInfo.mountPoint;
        if (mount.endsWith("/*")) {
            mount = mount.substring(0, mount.length() - 2);
        }

        final String webXmlFilePath = new StringBuilder().append("file:///").append(location).append("/WEB-INF/web.xml").toString();
        boolean appIsDistributable = distribute;
        URL webXmlUrl = null;
        try {
            webXmlUrl = FlexibleLocation.resolveLocation(webXmlFilePath);
        } catch (MalformedURLException e) {
            throw new ContainerException(e);
        }
        File webXmlFile = new File(webXmlUrl.getFile());
        if (webXmlFile.exists()) {
            Document webXmlDoc = null;
            try {
                webXmlDoc = UtilXml.readXmlDocument(webXmlUrl);
            } catch (Exception e) {
                throw new ContainerException(e);
            }
            appIsDistributable = webXmlDoc.getElementsByTagName("distributable").getLength() > 0;
        } else {
            Debug.logInfo(webXmlFilePath + " not found.", module);
        }
        final boolean contextIsDistributable = distribute && appIsDistributable;

        // configure persistent sessions
        Manager sessionMgr = null;
        sessionMgr = manager();

        // create the web application context
        StandardContext context = new StandardContext();
        context.setParent(host);
        context.setDocBase(location);
        context.setPath(mount);
        context.addLifecycleListener(new ContextConfig());

        JarScanner jarScanner = context.getJarScanner();
        if (jarScanner instanceof StandardJarScanner) {
            StandardJarScanner standardJarScanner = (StandardJarScanner) jarScanner;
            standardJarScanner.setScanClassPath(false);
        }

        Engine egn = (Engine) context.getParent().getParent();
        egn.setService(tomcat.getService());

        Debug.logInfo("host[" + host + "].addChild(" + context + ")", module);
        //context.setDeployOnStartup(false);
        //context.setBackgroundProcessorDelay(5);
        context.setJ2EEApplication(J2EE_APP);
        context.setJ2EEServer(J2EE_SERVER);
        context.setLoader(new WebappLoader(ClassLoaderContainer.getClassLoader()));

        context.setCookies(appInfo.isSessionCookieAccepted());
        context.addParameter("cookies", appInfo.isSessionCookieAccepted() ? "true" : "false");

        context.setDisplayName(appInfo.name);
        context.setDocBase(location);
        context.setAllowLinking(true);

        context.setReloadable(contextReloadable);

        context.setDistributable(contextIsDistributable);

        context.setCrossContext(crossContext);
        context.setPrivileged(appInfo.privileged);
        context.setManager(sessionMgr);
        context.getServletContext().setAttribute("_serverId", appInfo.server);
        context.getServletContext().setAttribute("componentName", appInfo.componentConfig.getComponentName());

        // request dumper filter
        String enableRequestDump = initParameters.get("enableRequestDump");
        if ("true".equals(enableRequestDump)) {
            // create the Requester Dumper Filter instance
            FilterDef requestDumperFilterDef = new FilterDef();
            requestDumperFilterDef.setFilterClass(RequestDumperFilter.class.getName());
            requestDumperFilterDef.setFilterName("RequestDumper");
            FilterMap requestDumperFilterMap = new FilterMap();
            requestDumperFilterMap.setFilterName("RequestDumper");
            requestDumperFilterMap.addURLPattern("*");
            context.addFilterMap(requestDumperFilterMap);
        }

        // create the Default Servlet instance to mount
        StandardWrapper defaultServlet = new StandardWrapper();
        defaultServlet.setParent(context);
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.setServletName("default");
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listing", "true");
        defaultServlet.addMapping("/");
        context.addChild(defaultServlet);
        context.addServletMapping("/", "default");

        // create the Jasper Servlet instance to mount
        StandardWrapper jspServlet = new StandardWrapper();
        jspServlet.setParent(context);
        jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
        jspServlet.setServletName("jsp");
        jspServlet.setLoadOnStartup(1);
        jspServlet.addInitParameter("fork", "false");
        jspServlet.addInitParameter("xpoweredBy", "true");
        jspServlet.addMapping("*.jsp");
        jspServlet.addMapping("*.jspx");
        context.addChild(jspServlet);
        context.addServletMapping("*.jsp", "jsp");

        // default mime-type mappings
        configureMimeTypes(context);

        // set the init parameters
        for (Map.Entry<String, String> entry: initParameters.entrySet()) {
            context.addParameter(entry.getKey(), entry.getValue());
        }

        context.setRealm(host.getRealm());
        host.addChild(context);
        context.getMapper().setDefaultHostName(host.getName());

        return context;
    }
	
}
