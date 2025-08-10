
_default:
    just --list

test:
	POWERMODE_ZERANTHIUM_TESTS=1 ./gradlew :test


test_ui_launch_ide:
    ./gradlew :testIdeUi

test_ui_run_tests:
    TEST_TYPE=UI ./gradlew :test --tests com.cschar.pmode3.uitest.OpenSettingsJavaTest

gradle_check_cache:
    dust ~/.gradle

gradle_clean_cache:
    rm -rf ~/.gradle/caches/transforms/
    # rm -rf ~/.gradle/caches
