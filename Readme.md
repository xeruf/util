## xerus.util

Collection of various JVM utilities for cross-project use.

Use at your own risk, I sometimes force-push since this is only a personal collection.
This exists mainly to enable contributions to dependents.

Even though I wil try to be more contribution-friendly in the future,
you may encounter conflicts when pulling.
Then, assuming you have no local changes,
simply perform a `git reset --hard origin/master`.

### Structure

This project consists of a few modules, with potentially more to come in the future.

| Module (folder)	| Summary | Dependencies |
|-----------------|---------|--------------|
| kotlin | Contains a lot of extensions to the stdlib and some other helpers. Dependency for almost all of my projects written in Kotlin. | kotlin-stdlib, coroutines |
| javafx | Helpers for JavaFX and some additions like Themes and icon sets. | kotlin module |
| logging | logback configurator that takes CLI args | kotlin module |

### Usage

The project can be depended upon via [jitpack](https://jitpack.io/#xeruf/util):
```
repositories {
  ...
  maven("https://jitpack.io")
}

dependencies {
  ...
  implementation("com.github.xeruf.util", "MODULE", "VERSION")
}
```
Version can be a tag, a commit hash, "-SNAPSHOT" for the latest master build 
or "_branch_-SNAPSHOT" for the latest build of a specific branch.
For more information and explanations visit [jitpack](https://jitpack.io/#xeruf/util).
