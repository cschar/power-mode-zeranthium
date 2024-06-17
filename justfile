
_default:
    just --list

test:
	POWERMODE_ZERANTHIUM_TESTS=1 ./gradlew :test


test_ui_launch_ide:
    ./gradlew :testIdeUi

test_ui_run_tests:
    TEST_TYPE=UI ./gradlew :test --tests com.cschar.pmode3.uitest.OpenSettingsJavaTest