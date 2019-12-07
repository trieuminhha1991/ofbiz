package com.olbius.bi.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Process {

	private ProcessBuilder processBuilder;
	
	private String dir;
	
	private List<String> command;
	
	public final static String os = System.getProperty("os.name");
	
	private final static String[] bash = new String[] {"Pan.bat", "pan.sh"};
	
	private java.lang.Process p;
	
	public Process() {
		processBuilder = new ProcessBuilder();
		command = new ArrayList<String>();
	}
	
	public void start(String... bash) throws Exception {
		
		String[] tmp = new String[2];
		
		if(bash.length == 2) {
			tmp[0] = bash[0];
			tmp[1] = bash[1];
		} else {
			tmp[0] = Process.bash[0];
			tmp[1] = Process.bash[1];
		}
		
		if(os.contains("Windows")) {
			command.add(0, tmp[0]);
			command.add(0, "/wait");
			command.add(0, "start");
			command.add(0, "/c");
			command.add(0, "cmd");
		}
		if(os.contains("Linux") || os.contains("Mac")) {
			command.add(0, tmp[1]);
			command.add(0, "bash");
		}
		
		processBuilder.command(command);
		if(dir != null && !dir.isEmpty()) {
			processBuilder.directory(new File(dir));
		}
		p = processBuilder.inheritIO().start();
		
	}

	public int waitFor(){
		try {
			int i = p.waitFor();
			return i;
		} catch (InterruptedException e) {
			return -1;
		}
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public void addCommand(String string) {
		command.add(string);
	}
	
	public void addCommand(String string, boolean flag) {
		if(flag) {
			if(os.contains("Windows")) {
				command.add("\"".concat(string).concat("\""));
			}
			if(os.contains("Linux") || os.contains("Mac")) {
				command.add(string);
			}
		} else {
			addCommand(string);
		}
		
	}
	
	public void addCommand(String... string) {
		
		for(String s : string) {
			command.add(s);
		}
		
	}
	
}
