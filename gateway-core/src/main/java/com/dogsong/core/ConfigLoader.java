package com.dogsong.core;

import com.dogsong.common.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * ConfigLoader
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/14
 */
@Slf4j
public class ConfigLoader {

    private static final String CONFIG_FILE = "gateway.properties";

    private static final String ENV_PREFIX = "GATEWAY_";

    private static final String JVM_PREFIX = "gateway.";

    private static final ConfigLoader INSTANCE = new ConfigLoader();

    private ConfigLoader() {}

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    private Config config;

    public static Config getConfig() {
        return INSTANCE.config;
    }


    /**
     * 优先级高的会覆盖优先级低的
     * 运行参数 > jvm参数 > 环境变量 > 配置文件 > 配置对象对默认值
     *
     * @param args 运行参数
     */
    public Config load(String[] args) {
        // 配置对象对默认值
        config = new Config();

        // 配置文件
        loadFromConfigFile();

        // 环境变量
        loadFromEnv();

        // jvm参数
        loadFromJvm();

        // 运行参数
        loadFromArgs(args);

        return config;
    }

    private void loadFromArgs(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        Properties properties = new Properties();
        for (String arg : args) {
            // --port=7001
            if (StringUtils.startsWith(arg, "--") && StringUtils.contains(arg, "=")) {
                properties.put(
                        StringUtils.substring(arg, 2, arg.indexOf("=")),
                        StringUtils.substring(arg, arg.indexOf("=") + 1)
                );
            }
        }
        PropertiesUtil.properties2Object(properties, config);
    }

    private void loadFromJvm() {
        Properties properties = System.getProperties();
        PropertiesUtil.properties2Object(properties, config, JVM_PREFIX);
    }

    private void loadFromEnv() {
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.putAll(env);
        PropertiesUtil.properties2Object(properties, config, ENV_PREFIX);
    }

    private void loadFromConfigFile() {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                PropertiesUtil.properties2Object(properties, config);
            }
        } catch (IOException e) {
            log.warn("load config file {} error", CONFIG_FILE, e);
        }
    }
}
