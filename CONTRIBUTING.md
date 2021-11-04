# Contributing

## Local Development

### Build

```
./gradlew build
```

### Test

```
./gradlew test --info
```

### Format

```
./gradlew ktlintFormat
```

### Publish Local Version

```
./gradlew publishToMavenLocal
```

## Release

With the proper credentials configure, run the following commands to publish.

### Publish Snapshot

Will automatically append the `-SNAPSHOT` identifier to the `version` and publish artifacts to https://s01.oss.sonatype.org/content/repositories/snapshots.

```
./gradlew publishToMyNexus
```

### Publish to Nexus Staging Repository

Publishes to the Nexus staging repository which will begin the release process. Staging repository must be manually released via the Nexus Repository Manager: https://s01.oss.sonatype.org/#stagingRepositories

```
./gradlew publishToMyNexus -Prelease
```
