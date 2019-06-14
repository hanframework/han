package org.hanframework.core;

import org.hanframework.context.ApplicationContext;
import org.hanframework.tool.app.ApplicationPid;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.date.StopWatch;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * @author liuxin
 * @version Id: StartupInfoLogger.java, v 0.1 2019-06-13 17:49
 */
public class StartupInfoLogger {
    private final Class<?> sourceClass;

    public StartupInfoLogger(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public void logStarting(Logger log) {
        Assert.notNull(log, "Log must not be null");
        if (log.isInfoEnabled()) {
            log.info(this.getStartupMessage());
        }

        if (log.isDebugEnabled()) {
            log.debug(this.getRunningMessage().toString());
        }

    }

    public void logStarted(Logger log, StopWatch stopWatch, boolean prettyPrint) {
        if (log.isInfoEnabled()) {
            log.info(getStartedMessage(stopWatch).toString());
        }
        if (prettyPrint) {
            log.debug(stopWatch.prettyPrint(getApplicationName()));
        }
    }

    private String getStartupMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Starting ");
        message.append(this.getApplicationName());
        message.append(this.getVersion(this.sourceClass));
        message.append(this.getOn());
        message.append(this.getPid());
        message.append(this.getContext());
        return message.toString();
    }

    private StringBuilder getRunningMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Running with Spring Boot");
        message.append(this.getVersion(this.getClass()));
        message.append(", Spring");
        message.append(this.getVersion(ApplicationContext.class));
        return message;
    }

    private StringBuilder getStartedMessage(StopWatch stopWatch) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        message.append(this.getApplicationName());
        message.append(" in ");
        message.append(stopWatch.getTotalTimeSeconds());

        try {
            double uptime = (double) ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0D;
            message.append(" seconds (JVM running for " + uptime + ")");
        } catch (Throwable var5) {
        }

        return message;
    }

    private String getApplicationName() {
        return this.sourceClass != null ? ClassTools.getShortName(this.sourceClass) : "application";
    }

    private String getVersion(Class<?> source) {
        return this.getValue(" v", () -> {
            return source.getPackage().getImplementationVersion();
        }, "");
    }

    private String getOn() {
        return this.getValue(" on ", () -> {
            return InetAddress.getLocalHost().getHostName();
        });
    }

    private String getPid() {
        return this.getValue(" with PID ", () -> {
            return (new ApplicationPid()).toString();
        });
    }

    private String getContext() {
        return "111";
    }

    private String getValue(String prefix, Callable<Object> call) {
        return this.getValue(prefix, call, "");
    }

    private String getValue(String prefix, Callable<Object> call, String defaultValue) {
        try {
            Object value = call.call();
            if (value != null && StringTools.hasLength(value.toString())) {
                return prefix + value;
            }
        } catch (Exception var5) {
        }

        return defaultValue;
    }
}
