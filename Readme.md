## xerus.util

Collection of various utilities I have created for cross-project use.

Use at your own risk, I sometimes force-push since this is only a personal collection.
This is mainly here for others to be able to contribute to my projects that depend on this project.

Even though I wil try to be more contribution-friendly in the future, you may encounter conflicts when pulling.
Then, assuming you have no local changes, simply perform a `git reset --hard origin/master`.

### Structure

This project consists of a few modules, with potentially more to come in the future.

| Module (folder)	| Summary | Dependencies |
|-----------------|---------|--------------|
| kotlin | Dependency for virtually all of my projects, because I write everything in Kotlin and it contains a lot of extensions to the stdlib and some helpful classes | kotlin stdlib, coroutines |
| javafx | Contains a lot of helpers for JavaFX and some additions like skins and icon sets | kotlin module |
