package me.catsflex.test.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
	
	public static final String MOD_ID = "test";
	
	private static final ModContainer MOD = FabricLoader
		.getInstance()
		.getModContainer(MOD_ID)
		.orElseThrow();
	
	public static final String MOD_NAME = MOD.getMetadata().getName();
	public static final String MOD_PREFIX = String.format("[%s]", MOD_NAME);
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String CONFIG_NAME = MOD_ID + ".json";
	public static final String YACL_MOD_ID = "yet_another_config_lib_v3";
}
