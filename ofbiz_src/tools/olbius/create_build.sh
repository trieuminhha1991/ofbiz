#! /bin/bash

re='^[0-9]+$'

param () {
	if [[ -z "$1" ]]; then
		echo "$2"
	else
		if [[ $1 =~ $re ]]; then
			echo "$1M"
		else
			echo "$1"
		fi
	fi
}


MEMORY_INIT=$(param "$MEMORY_INIT" "256M")
MEMORY_MAX=$(param "$MEMORY_MAX" "512M")
MEMORY_PERM=$(param "$MEMORY_PERM" "512M")
MEMORY_STACK=$(param "$MEMORY_STACK" "1M")

cat > ${OLBIUS_PATH:-"../.."}/build.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project name="Olbius OFBiz" default="start-batch" basedir="." xmlns:ivy="antlib:org.apache.ivy.ant" xmlns:sonar="antlib:org.sonar.ant:sonar">
	<import file="macros.xml"/>
    <property name="site.dir" value="../site"/>
    
    <property name="memory.initial.param" value="-Xms$MEMORY_INIT"/>
    <property name="memory.max.param" value="-Xmx$MEMORY_MAX"/>
    <property name="memory.maxpermsize.param" value="-XX:MaxPermSize=$MEMORY_PERM"/>
    <property name="memory.stack" value="-Xss$MEMORY_STACK"/>

    <available file="applications/build.xml" property="applications.present"/>
    <available file="specialpurpose/build.xml" property="specialpurpose.present"/>
    
    <property file="build.properties" />
    
    <target name="start"
            description="Start OFBiz">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <jvmarg value="\${memory.stack}"/>
        </java>
    </target>

    <target name="stop"
            description="Stop OFBiz">
        <java jar="ofbiz.jar" fork="true">
            <arg value="-shutdown"/>
        </java>
    </target>
    
    <target name="load-seed" 
            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="readers=seed"/>
        </java>
    </target>
    
    <target name="load-file"
            description="Load data using the command line argument 'data-file' to load data from a given file using the 'default' delegator or a delegator specified in the command line argument 'delegator'">
        <property name="delegator" value="default" />
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="delegator=\${delegator}"/>
            <arg value="file=\${data-file}"/>
        </java>
    </target>

    <target name="load-admin-user-login"
            description="Create a user login with admin privileges and a temporary password equal to 'ofbiz'; after a successful login the user will be prompted for a new password. Example command for the userLogin 'admin': ./ant load-admin-user-login -DuserLoginId=admin">
        <fail message="userLoginId parameter is required. To add the parameter to the command for user admin: -DuserLoginId=admin">
            <condition>
                <not><isset property="userLoginId"/></not>
            </condition>
        </fail>
        <copy file="\${basedir}/framework/resources/templates/AdminUserLoginData.xml" tofile="runtime/tmp/tmpUserLogin.xml">
            <filterset>
                <filter token="userLoginId" value="\${userLoginId}"/>
            </filterset>
        </copy>
        <antcall target="load-file">
            <param name="data-file" value="runtime/tmp/tmpUserLogin.xml"/>
        </antcall>
        <delete file="runtime/tmp/tmpUserLogin.xml"/>
    </target>

    <target name="create-admin-user-login"
            description="Prompt for a user name, then create a user login with admin privileges and a temporary password equal to 'ofbiz'. After a successful login the user will be prompted for a new password. Note: this uses load-admin-user-login target">
        <!--<input addproperty="userLoginId" message="Enter user name (log in with the temporary password 'ofbiz'):"/> -->
    	<property name="userLoginId" value="admin"></property>
        <antcall target="load-admin-user-login"/>
    </target>
    
    <target name="-load-core-seed" depends="load-seed"
            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="readers=coreseed"/>
        </java>
    </target>
    
    <target name="load-base-seed" depends="-load-core-seed,create-admin-user-login" 
	            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
	        <java jar="ofbiz.jar" fork="true">
	            <jvmarg value="\${memory.initial.param}"/>
	            <jvmarg value="\${memory.max.param}"/>
	            <jvmarg value="\${memory.maxpermsize.param}"/>
	            <arg value="install"/>
	            <arg value="readers=baseseedreader"/>
	        </java>
	</target>
	
	<target name="-load-core-demo"
            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="readers=coredemo"/>
        </java>
    </target>
    
    <target name="-load-coremtl-seed" depends=""
            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="readers=coremtlseed"/>
        </java>
    </target>
    
	<target name="-load-coremtl-demo" depends="-load-coremtl-seed"
            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
        <java jar="ofbiz.jar" fork="true">
            <jvmarg value="\${memory.initial.param}"/>
            <jvmarg value="\${memory.max.param}"/>
            <jvmarg value="\${memory.maxpermsize.param}"/>
            <arg value="install"/>
            <arg value="readers=coremtldemo"/>
        </java>
    </target>
		
	<target name="load-base-demo" depends="load-base-seed,-load-core-demo" 
		description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
		<java jar="ofbiz.jar" fork="true">
	      <jvmarg value="\${memory.initial.param}"/>
	      <jvmarg value="\${memory.max.param}"/>
	      <jvmarg value="\${memory.maxpermsize.param}"/>
	      <arg value="install"/>
	      <arg value="readers=basedemoreader"/>
	   </java>
	</target>
	
	<target name="load-base-product-demo" depends="" 
		description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
		<java jar="ofbiz.jar" fork="true">
	      <jvmarg value="\${memory.initial.param}"/>
	      <jvmarg value="\${memory.max.param}"/>
	      <jvmarg value="\${memory.maxpermsize.param}"/>
	      <arg value="install"/>
	      <arg value="readers=baseproddemoreader"/>
	   </java>
	</target>
	
	<target name="load-basemtl-seed" depends="load-seed,create-admin-user-login,-load-core-seed,-load-coremtl-seed" 
	            description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
	        <java jar="ofbiz.jar" fork="true">
	            <jvmarg value="\${memory.initial.param}"/>
	            <jvmarg value="\${memory.max.param}"/>
	            <jvmarg value="\${memory.maxpermsize.param}"/>
	            <arg value="install"/>
	            <arg value="readers=baseseedreadermtl"/>
	        </java>
	</target>
	
	<target name="load-basemtl-demo" depends="load-basemtl-seed,-load-core-demo,-load-coremtl-demo" 
		description="Load ONLY the seed data (not seed-initial, demo, ext* or anything else); meant for use after an update of the code to reload the seed data as it is generally maintained along with the code and needs to be in sync for operation">
		<java jar="ofbiz.jar" fork="true">
	      <jvmarg value="\${memory.initial.param}"/>
	      <jvmarg value="\${memory.max.param}"/>
	      <jvmarg value="\${memory.maxpermsize.param}"/>
	      <arg value="install"/>
	      <arg value="readers=basedemoreadermtl"/>
	   </java>
	</target>
	
</project>
EOF
