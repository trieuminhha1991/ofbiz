package com.olbius.jackrabbit.security;

import java.security.Principal;
import java.util.Arrays;

import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.core.security.principal.EveryonePrincipal;

public class OlbiusEntry {
	
	public static String toString(AccessControlEntry entry) {
		String text = "";
		Principal principal = entry.getPrincipal();
		Privilege[] privileges = entry.getPrivileges();
		if (principal instanceof EveryonePrincipal) {
			text += "Everyone";
		} else {
			text += principal.getName();
		}
		JackrabbitAccessControlEntry entry2 = (JackrabbitAccessControlEntry) entry;
		text += " - " + Arrays.toString(privileges) + " - " + entry2.isAllow();
		return text;
	}

	public static String toString(AccessControlEntry[] entries) {
		String text = "";
		for (AccessControlEntry e : entries) {
			text += toString(e) + "\n";
		}
		return text;
	}
}
