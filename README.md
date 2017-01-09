Dropwizard component to enable cloudwatch log appender
==================

[![Build Status](https://circleci.com/gh/WriskHQ/dropwizard-logging-cloudwatch.svg?style=shield&circle-token=199d2503bab54351c77c4721458ec04d340454b3)](https://circleci.com/gh/WriskHQ/dropwizard-logging-cloudwatch)
[![License](https://img.shields.io/github/license/WriskHQ/dropwizard-logging-cloudwatch.svg?style=flat-square)]()
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/co.wrisk.dropwizard/dropwizard-logging-cloudwatch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/co.wrisk.dropwizard/dropwizard-logging-cloudwatch/)


## Setup

Add dependency to `build.gradle`

```groovy
compile 'co.wrisk.dropwizard:dropwizard-logging-cloudwatch:1.0.3'
```

Set up new appender in [Dropwizard logging configuration](http://www.dropwizard.io/1.0.5/docs/manual/configuration.html#logging):

```yaml
logging:
  appenders:
    - type: console
    - type: awslogs 
      logGroup: logGroupName # required
      logStream: logStreamName # required
      region: us-east-1 # required
      accessKey: AWS access key # optional
      secretKey: AWS secret key # optional
      skipCreate: false # optional, does not create log group and stream if they don't exist
      maxBatchSize: 512 # optional
      maxBatchTime: 1000 # optional
```

### Continuous Integration

CircleCI builds the project with Oracle JDK 8. Builds are deployed
to Sonatype OSSRH.


