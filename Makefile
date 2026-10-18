# One-command build and test for the whole monorepo. Later phases add services/ and apps/dashboard.
ANDROID := apps/android

.PHONY: build test android android-test screenshots i18n apk-size

build: android

test: android-test

android:
	cd $(ANDROID) && ./gradlew assembleDebug assembleRelease
	cd $(ANDROID) && tools/apk-size-check.sh

android-test: i18n
	cd $(ANDROID) && ./gradlew testDebugUnitTest -Proborazzi.test.verify=true

# Re-record screenshot baselines after an intended UI change (review the PNG diff before committing).
screenshots:
	cd $(ANDROID) && ./gradlew testDebugUnitTest -Proborazzi.test.record=true

# Check that Android string resources match apps/android/i18n/*.tsv (run generate.py without --check to regenerate).
i18n:
	python3 $(ANDROID)/i18n/generate.py --check

apk-size:
	cd $(ANDROID) && tools/apk-size-check.sh
