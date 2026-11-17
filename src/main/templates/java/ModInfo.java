package ${mod_group}.${mod_package};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated from src/main/templates/java/ModInfo.java by the generateTemplateSources task.
 * Edit the template (or gradle.properties), not the generated copy.
 */
public final class ModInfo {
	public static final String MOD_ID = "${mod_id}";
	public static final String MOD_NAME = "${mod_name}";
	public static final String MOD_VERSION = "${mod_version}";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	
	private ModInfo() {}
}
