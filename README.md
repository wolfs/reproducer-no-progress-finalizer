Reproducer for cannot make progress error
====

In order to reproduce the error, run:

    $ ./gradlew clean installDist test

The build will then fail with:

```
Unable to make progress running work. The following items are queued for execution but none of them can be started:
  - Build ':':
      - Waiting for nodes:
          - :test (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=task group 2, dependencies=[destroyer locations for task group 1, :classes (complete), :compileJava (complete), :compileTestJava, :testClasses], waiting-for=[:compileTestJava, destroyer locations for task group 1, :testClasses], has-failed-dependency=false )
          - :copyTestClasses (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=finalizer :copyTestClasses ordinal: task group 1, delegate: default group, no dependencies, finalizes=[:deleteTestSrcFiles] )
          - :deleteTestClassesInMain (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=finalizer :deleteTestClassesInMain ordinal: task group 1, delegate: default group, dependencies=[producer locations for task group 0 (complete)], has-failed-dependency=false, finalizes=[:copyTestClasses] )
          - :deleteTestSrcFiles (state=SHOULD_RUN, dependencies=COMPLETE_AND_SUCCESSFUL, group=finalizer :deleteTestSrcFiles ordinal: task group 1, delegate: default group, dependencies=[Resolve mutations for :deleteTestSrcFiles (complete), producer locations for task group 0 (complete)], has-failed-dependency=false, finalizes=[:copyTestSrcFiles (complete)] )
      - Nodes ready to start: :deleteTestSrcFiles
      - Reachable nodes:
          - Resolve mutations for :deleteTestClassesInMain (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=default group, no dependencies )
          - destroyer locations for task group 1 (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=task group 1, dependencies=[Resolve mutations for :deleteTestClassesInMain, Resolve mutations for :deleteTestSrcFiles (complete), destroyer locations for task group 0 (complete)], waiting-for=[Resolve mutations for :deleteTestClassesInMain], has-failed-dependency=false )
          - :compileTestJava (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=task group 2, dependencies=[destroyer locations for task group 1, :classes (complete), :compileJava (complete)], waiting-for=[destroyer locations for task group 1], has-failed-dependency=false )
          - :processTestResources (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=task group 2, dependencies=[destroyer locations for task group 1], waiting-for=[destroyer locations for task group 1], has-failed-dependency=false )
          - :testClasses (state=SHOULD_RUN, dependencies=NOT_COMPLETE, group=task group 2, dependencies=[:compileTestJava, :processTestResources], waiting-for=[:compileTestJava, :processTestResources], has-failed-dependency=false )
      - Ordinal groups:
          - group 0 entry nodes: [:clean (complete)]
          - group 1 entry nodes: [:installDist (complete)]
          - group 2 entry nodes: [:test]
  - Workers waiting for work: 8
  - Stopped workers: 0

FAILURE: Build failed with an exception.

* What went wrong:
Unable to make progress running work. There are items queued for execution but none of them can be started
```