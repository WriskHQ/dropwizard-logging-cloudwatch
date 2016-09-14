package co.wrisk.dropwizard.cloudwatch.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import org.eluder.logback.ext.cloudwatch.appender.CloudWatchAppender;

@JsonTypeName("awslogs")
public class CloudWatchAppenderFactory extends AbstractAppenderFactory<ILoggingEvent> {

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
    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, LayoutFactory<ILoggingEvent> layoutFactory, LevelFilterFactory<ILoggingEvent> levelFilterFactory, AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {
        final CloudWatchAppender appender = new CloudWatchAppender();
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

        final LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(buildLayout(context, layoutFactory));
        appender.setEncoder(layoutEncoder);

        appender.addFilter(levelFilterFactory.build(threshold));
        getFilterFactories().stream().forEach(f -> appender.addFilter(f.build()));
        appender.start();

        return appender;
    }


}
