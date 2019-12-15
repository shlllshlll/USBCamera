# USBCamera

这是一个USB摄像头APP，基于[UVCCamera](https://github.com/saki4510t/UVCCamera)和[AndroidUSBCamera](https://github.com/jiangdongguo/AndroidUSBCamera)。实现了外接USB摄像头的预览与图片保存功能，同时解决了原项目中一些bug和问题。照片会保存到手机根目录下的USBCamera文件夹下，同时也会保存到相册。

**项目地址:**[USBCamera](https://github.com/shlllshlll/USBCamera)

**下载地址:**[国外地址](https://github.com/shlllshlll/USBCamera/releases/latest)、[国内地址](https://cloud.shlll.top/s/saNgZJS3FSNsQS9)

<p align="center">
<img src="pictures/screenshot.jpg" width="45%" height="45%"/>
</p>

## 兼容性测试

|    机型    |  系统版本  | 安卓版本  | 是否可用 |                           问题描述                           |
| :--------: | :--------: | :-------: | :------: | :----------------------------------------------------------: |
| 红米Note4x | Lineage16 | Android9 | 是 | 无 |
|   小米8    |   MIUI11   | Android10 |    是    |                              无                              |
|   小米9    |   MIUI11   | Android10 |    是    |                              无                              |
|  荣耀Note10  |  EMUI9.1   | Android9  |    是    |                              无                              |
|   荣耀V9   |  EMUI9.1   | Android9  |    是    |                              无                              |
|  荣耀V10   |  EMUI9.1   | Android9  |    是    |                              无                              |
|  荣耀V20   | MagicUI2.1 | Android9  |    是    |                              无                              |
| 荣耀Magic2 | MagicUI2.1 | Android9 | 是 | 无 |
| 华为MateS  |  EMUI4.0   | Android6  |    否    | 可检测到摄像头插入，但点击连接后无显示。调试后发现调用SO库时报错，暂未定位到问题 |
| 华为Mate30 |  EMUI10.0  | Android10 |    是    |                              无                              |
|   华为M3   | EMUI5.0.4  | Android7  |    是    |                              无                              |
| 一加6T | - | Android9 | 是 | 无 |
| VIVO X21i | FuntouchOS | Android9 | 否 | 在设置选项中打开OTG后可检测到摄像头，可正确获取分辨率设置，但无画面显示 |
| VIVO X23 | FuntouchOS | Android9 | 是 | 需要在设置内找到OTG选项并打开 |

## 解决的问题

1. **在部分Android9以上版本无法使用**

此问题是由于Android9中部分设备的UVC摄像头的ID等信息发生了变化，原作者在源码中已修复此问题。

2. **需要手动选择USB设备才可使用**

目前构建的APP，可自动寻找USB摄像头并打开。

3. **Android9中在程序运行时拔出摄像头会导致程序崩溃**

根据Github Pull Request [#454](https://github.com/saki4510t/UVCCamera/pull/454)，重新编译后问题已解决。

4. **JNI库中存在的内存泄露问题**

根据Github Issue [#259](https://github.com/saki4510t/UVCCamera/issues/259)已修改SO库中的空指针错误，重新编译后问题已解决。

5. **解决新版NDK对ABI支持的变动导致JNI编译失败的问题**
6. **将项目迁移到AndroidX**
7. **解决Issues [#244](https://github.com/saki4510t/UVCCamera/issues/244)**

## 注意事项

1. 目前编译的目标SDK版本不能超过27，否则会无法正确获取USB摄像头权限，此问题是由于Android9的API特性更改导致的。如果需要支持，可能需要较多修改。
可参考Pull Request [#480](https://github.com/saki4510t/UVCCamera/pull/480)
