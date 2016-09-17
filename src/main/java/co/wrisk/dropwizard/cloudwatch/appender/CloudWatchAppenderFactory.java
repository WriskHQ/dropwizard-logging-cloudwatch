package co.wrisk.dropwizard.cloudwatch.appender;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import org.eluder.logback.ext.cloudwatch.appender.AbstractCloudWatchAppender;
import org.eluder.logback.ext.core.CommonEventAttributes;

@JsonTypeName("awslogs")
public class CloudWatchAppenderFactory<E extends DeferredProcessingAware> extends AbstractAppenderFactory<E> {

    private static final int DEFAULT_MAX_BATCH_SIZE = 512;
    private static final int DEFAULT_MAX_BATCH_TIME = 1000;
    private static final int DEFAULT_INTERNAL_QUEUE_SIZE = 8192;

    @JsonProperty
    private String region;
    @JsonProperty
    private String logGroup;
    @JsonProperty
    private String logStream;
    @JsonProperty
    private String accessKey;
    @JsonProperty
    private String secretKey;

    @JsonProperty
    private int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;

    @JsonProperty
    private long maxBatchTime = DEFAULT_MAX_BATCH_TIME;

    @JsonProperty
    private boolean skipCreate = false;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLogGroup() {
        return logGroup;
    }

    public void setLogGroup(String logGroup) {
        this.logGroup = logGroup;
    }

    public String getLogStream() {
        return logStream;
    }

    public void setLogStream(String logStream) {
        this.logStream = logStream;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public long getMaxBatchTime() {
        return maxBatchTime;
    }

    public void setMaxBatchTime(long maxBatchTime) {
        this.maxBatchTime = maxBatchTime;
    }

    public boolean isSkipCreate() {
        return skipCreate;
    }

    public void setSkipCreate(boolean skipCreate) {
        this.skipCreate = skipCreate;
    }

    @Override
    public Appender<E> build(LoggerContext context, String applicationName, LayoutFactory<E> layoutFactory, LevelFilterFactory<E> levelFilterFactory, AsyncAppenderFactory<E> asyncAppenderFactory) {
        final AbstractCloudWatchAppender<E> appender = new AbstractCloudWatchAppender<E>() {
            @Override
            protected CommonEventAttributes applyCommonEventAttributes(E event) {
                if (event instanceof ILoggingEvent) {
                    return new LoggingEventCommonEventAttributes((ILoggingEvent) event);
                } else
                    return new AccessEventCommonAttributes((IAccessEvent) event);
            }
        };
        appender.setName("awslogs-appender");
        appender.setRegion(region);
        appender.setLogGroup(logGroup);
        appender.setLogStream(logStream);

        appender.setMaxBatchSize(maxBatchSize);
        appender.setMaxBatchTime(maxBatchTime);

        appender.setAccessKey(accessKey);
        appender.setSecretKey(secretKey);

        int queueSize = getQueueSize();
        if (queueSize < DEFAULT_INTERNAL_QUEUE_SIZE) {
            queueSize = DEFAULT_INTERNAL_QUEUE_SIZE;
        }
        appender.setInternalQueueSize(queueSize);

        appender.setSkipCreate(skipCreate);

        appender.setContext(context);

        final LayoutWrappingEncoder<E> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(buildLayout(context, layoutFactory));
        appender.setEncoder(layoutEncoder);

        appender.addFilter(levelFilterFactory.build(threshold));
        getFilterFactories().forEach(f -> appender.addFilter(f.build()));
        appender.start();

        return appender;
    }


    private static class AccessEventCommonAttributes implements CommonEventAttributes {

        private final IAccessEvent event;

        AccessEventCommonAttributes(IAccessEvent event) {
            this.event = event;
        }

        @Override
        public String getThreadName() {
            return event.getThreadName();
        }

        @Override
        public long getTimeStamp() {
            return event.getTimeStamp();
        }
    }

    private static class LoggingEventCommonEventAttributes implements CommonEventAttributes {

        private final ILoggingEvent event;

        LoggingEventCommonEventAttributes(ILoggingEvent loggingEvent) {
            this.event = loggingEvent;
        }

        @Override
        public String getThreadName() {
            return event.getThreadName();
        }

        @Override
        public long getTimeStamp() {
            return event.getTimeStamp();
        }
    }

}
