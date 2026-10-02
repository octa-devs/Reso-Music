# Build diagnostics

- Commit: a4c90488320ae6bc457e73e4a6bc869282559cd1
- Run: https://github.com/octa-devs/Reso-Music/actions/runs/36971720268

## Errors


## Tail

```

For more details see https://docs.gradle.org/9.7.1/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)

> Configure project :app
WARNING: The option setting 'android.disallowKotlinSourceSets=false' is experimental.
The current default is 'true'.
Add android.sync.suppressAgpWarnings=UNSUPPORTED_PROJECT_OPTION_USE to the gradle.properties file to suppress this warning.

> Task :app:preBuild UP-TO-DATE
> Task :app:preReleaseBuild UP-TO-DATE
> Task :app:mergeReleaseJniLibFolders
> Task :app:mergeReleaseNativeLibs
> Task :app:generateReleaseBuildConfig
> Task :app:checkReleaseDuplicateClasses
> Task :app:generateReleaseResources
> Task :app:packageReleaseResources
> Task :app:processReleaseNavigationResources
> Task :app:stripReleaseDebugSymbols
> Task :app:extractReleaseNativeSymbolTables
> Task :app:mergeReleaseNativeDebugMetadata NO-SOURCE
> Task :app:mergeReleaseArtProfile SKIPPED
> Task :app:javaPreCompileRelease
> Task :app:mapReleaseSourceSetPaths
> Task :app:checkReleaseAarMetadata
> Task :app:compileReleaseNavigationResources
> Task :app:parseReleaseLocalResources
> Task :app:createReleaseCompatibleScreenManifests
> Task :app:extractDeepLinksRelease
> Task :app:generateReleaseRFile
> Task :app:processReleaseMainManifest
> Task :app:mergeReleaseResources
> Task :app:processReleaseManifest
> Task :app:extractProguardFiles
> Task :app:mergeReleaseStartupProfile
> Task :app:generateReleaseAssets UP-TO-DATE
> Task :app:mergeReleaseAssets
> Task :app:compressReleaseAssets
> Task :app:extractReleaseVersionControlInfo
> Task :app:processReleaseManifestForPackage
> Task :app:validateSigningRelease
> Task :app:writeReleaseAppMetadata
> Task :app:writeReleaseSigningConfigVersions
> Task :app:processReleaseResources
> Task :app:kspReleaseKotlin
> Task :app:compileReleaseKotlin
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/activities/LiquidGlassActivity.kt:445:9 'fun Slider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier = ..., enabled: Boolean = ..., valueRange: ClosedFloatingPointRange<Float> = ..., steps: Int = ..., onValueChangeFinished: (() -> Unit)? = ..., colors: SliderColors = ..., interactionSource: MutableInteractionSource = ...): Unit' is deprecated. Use the Slider overload that accepts SliderState, onValueChange, and onValueChangeFinished instead.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/activities/SettingsActivity.kt:312:69 'val Icons.Filled.ViewList: ImageVector' is deprecated. Use the AutoMirrored version at Icons.AutoMirrored.Filled.ViewList.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/activities/SettingsActivity.kt:1473:45 'val Icons.Filled.ArrowForward: ImageVector' is deprecated. Use the AutoMirrored version at Icons.AutoMirrored.Filled.ArrowForward.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/components/FloatingDock.kt:72:27 'val Icons.Filled.QueueMusic: ImageVector' is deprecated. Use the AutoMirrored version at Icons.AutoMirrored.Filled.QueueMusic.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/components/FloatingDock.kt:79:26 'val Icons.Filled.QueueMusic: ImageVector' is deprecated. Use the AutoMirrored version at Icons.AutoMirrored.Filled.QueueMusic.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/player/PlayerComponents.kt:1694:21 'fun Slider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier = ..., enabled: Boolean = ..., valueRange: ClosedFloatingPointRange<Float> = ..., steps: Int = ..., onValueChangeFinished: (() -> Unit)? = ..., colors: SliderColors = ..., interactionSource: MutableInteractionSource = ...): Unit' is deprecated. Use the Slider overload that accepts SliderState, onValueChange, and onValueChangeFinished instead.
w: file:///home/runner/work/Reso-Music/Reso-Music/app/src/main/java/com/octadevs/resomusic/ui/sheets/BottomSheets.kt:1861:57 'val LocalClipboardManager: ProvidableCompositionLocal<ClipboardManager>' is deprecated. Use LocalClipboard instead which supports suspend functions.

> Task :app:compileReleaseJavaWithJavac
> Task :app:expandReleaseArtProfileWildcards
> Task :app:mergeReleaseGeneratedProguardFiles
> Task :app:processReleaseJavaRes
> Task :app:generateReleaseLintVitalReportModel
> Task :app:mergeReleaseJavaResource
> Task :app:produceReleaseComposeMapping
> Task :app:reportReleaseComposeMappingErrors
> Task :app:lintVitalAnalyzeRelease
> Task :app:minifyReleaseWithR8
> Task :app:lintVitalReportRelease
> Task :app:lintVitalRelease
> Task :app:mergeReleaseComposeMapping
> Task :app:compileReleaseArtProfile SKIPPED
> Task :app:convertShrunkResourcesToBinaryRelease
> Task :app:optimizeReleaseResources
> Task :app:packageRelease
> Task :app:createReleaseApkListingFileRedirect
> Task :app:assembleRelease

[Incubating] Problems report is available at: file:///home/runner/work/Reso-Music/Reso-Music/build/reports/problems/problems-report.html

BUILD SUCCESSFUL in 3m 8s
49 actionable tasks: 49 executed
Consider enabling configuration cache to speed up this build: https://docs.gradle.org/9.7.1/userguide/configuration_cache_enabling.html
```
