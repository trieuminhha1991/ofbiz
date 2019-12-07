package com.olbius.security.core.application;

import com.olbius.security.api.Application;

public class OlbiusApplictionFactory {

	public static Application newInstance(String type) {
		if(type == null || type.isEmpty()) {
			return new OlbiusApp();
		}
		if(Application.ENTITY.equals(type)) {
			return new OlbiusEntity();
		}
		if(Application.MODULE.equals(type)) {
			return new OlbiusModule();
		}
		if(Application.SERVICE.equals(type)) {
			return new OlbiusService();
		}
		if(Application.SCREEN.equals(type)) {
			return new OlbiusScreen();
		}
		if(Application.MENU.equals(type)) {
			return new OlbiusMenu();
		}
		if(Application.WEBAPP.equals(type)) {
			return new OlbiusWebapp();
		}
		return null;
		
	}
	
}
