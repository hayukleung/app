####################################################################################################
# adb常用命令
# from http://www.jikexueyuan.com
# ref http://adbshell.com/
# ref http://wiki.jikexueyuan.com/project/linux-command/
# see https://github.com/hayukleung/adb-wrapper
####################################################################################################
##001.获取连接上计算机的设备
adb devices
##002.获取设备序列号
adb get-serialno
##003.重启设备
adb reboot
##004.重启设备进入fastboot模式
adb reboot bootloader
##005.重启设备进入recovery模式
adb reboot recovery
##006.发送命令到指定设备
adb [-d|-e|-s <serialNumber>] shell <command>
##007.启动adb服务进程
adb start-server
##008.终止adb服务进程
adb kill-server
##009.以root权限重启adb进程
adb root
##010.获取wifi mac地址
adb shell cat /sys/class/net/wlan0/address
##011.获取CPU序列号
adb shell cat /proc/cpuinfo
##012.获取设备编译属性
adb shell cat /system/build.prop
##013.获取WiFi配置信息
adb shell cat /data/misc/wifi/*.conf
##014.安装APK
adb install [-r|-s] <apk-file>
##015.卸载APP
adb uninstall [-k] <package-name>
##016.查看内存占用情况
adb shell top [-m <number>]
##017.查看进程列表
adb shell ps
##018.杀死一个进程
adb shell kill <pid>
##019.查看指定进程情况
adb shell ps -x <pid>
##020.查看后台service信息
adb shell service list
##021.查看当前内存占用
adb shell cat /proc/meminfo
##022.查看IO内存分区（root required）
adb shell cat /proc/iomem
##023.查看所有存储设备名
adb shell ls mnt
##024.将system分区重新挂载为可读写分区
adb remount
##025.本地文件复制至设备
adb push <local> <remote>
##026.设备文件复制至本地
adb pull <remote> <local>
##027.列出目录下所有文件|夹
adb shell ls
##028.进入文件夹
adb shell cd <folder>
##029.删除文件|夹
adb shell rm [-r] <path/filename>
##030.查看文件内容
adb shell cat <file>
##031.新建文件夹
adb shell mkdir path/foldername
##032.发送文本内容
adb shell input text <content>
##033.发送键盘事件
adb shell input keyevent <keycode>
##034.获取设备分辨率
adb shell wm size
##035.获取设备参数信息
adb shell getprop <key>
##036.设置设备参数信息
adb shell setprop <key> <value>
##037.截屏
adb shell screencap -p <path|file>
##038.屏幕录像
adb shell screenrecord [option] <path|file>
option:
        --size w*h
        --bit-rate bitrate
        --time-limit time
        --rotate
##039.终止录像
ctrl+C

####################################################################################################
# Activity Manager
####################################################################################################
##001.启动Activity
adb shell am start [option] <intent>
option:
        -D 调试
        -W 反馈信息
intent:
        -a <action>
        -d <data-uri>
        -t <mime-type>
        -c <category>
        -n <component>
        -f <flags>
##002.监控Crash与ANR
adb shell am monitor
##003.强制结束
adb shell am force-stop <package-name>
##004.启动Service
adb shell am startservice <intent>
##005.发送广播
adb shell am broadcast <intent>

####################################################################################################
# Package Manager
####################################################################################################
##001.获取应用列表
adb shell pm list package [option] <filter>
option:
        -f 列出包名、APK名及存放位置
        -d 过滤出系统禁用应用
        -e 过滤出系统正常使用应用
        -s 过滤出系统应用
        -3 过滤出第三方应用
        -i 列出包名及安装来源
        -u 列出包含卸载的应用
##002.列出包名对应apk位置
adb shell pm path <package-name>
##003.dump应用信息
adb shell pm dump <package-name>
##004.安装应用
adb shell pm install [option] <apk-file>
option:
        -r 覆盖安装
        -s 安装在SDCard
        -f 安装在内部存储
        -d 允许安装低版本应用
##005.卸载应用
adb shell pm uninstall <package-name>

####################################################################################################
# Logcat
####################################################################################################
##001.日志
adb logcat [option] [filter]
option:
        -s 日志标签
        -f 输出到指定文件
        -v 输出格式
        -b 加载日志缓冲区
        -c 清空所有日志缓存信息
filter:
        -V Verbose
        -D Debug
        -I Info
        -W Warn
        -E Error
        -S Silent
