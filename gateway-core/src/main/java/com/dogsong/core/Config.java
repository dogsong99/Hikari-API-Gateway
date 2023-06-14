package com.dogsong.core;

import lombok.Data;

/**
 * config
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/14
 */
@Data
public class Config {

    private int port = 7001;

    private String applicationName = "hikari-api-gateway";

    private String registryAddress = "127.0.0.1:7001";

    private String env = "dev";

    private int eventLoopGroupBossNum = 1;

    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();

    private int maxContentLength = 64 * 1024 * 1024;

    /** 默认单异步模式 */
    private boolean whenComplete = true;
}
