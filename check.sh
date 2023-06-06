#!/bin/sh
result=""
rm -rf ./app/src/main/assets/apk/*.apk
rm -rf ./app/build
rm -rf ./pluginapk/build
./gradlew -q clean &&
./gradlew -q pluginapk:assembleDebug &&
cp ./pluginapk/build/outputs/apk/debug/pluginapk-debug.apk ./app/src/main/assets/apk

./gradlew app:clean
./gradlew app:assembleDebug
host_path=./app/build/outputs/host_id.txt
plugin_path=./app/build/outputs/plugin_id.txt
aapt d resources ./app/build/outputs/apk/debug/app-debug.apk >$host_path
aapt d resources ./app/src/main/assets/apk/pluginapk-debug.apk >$plugin_path
# shellcheck disable=SC2002
img_id=$(cat $plugin_path | grep plugin_img | awk NR==1 | awk '{print $3}')
# shellcheck disable=SC2002
cat $host_path | grep -q "$img_id"
# shellcheck disable=SC2181
if [ $? -ne 0 ] ;then
    result="very good"
else
    result="插件图片id和宿主资源id冲突"
fi
echo $result