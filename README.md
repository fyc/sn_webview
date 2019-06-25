## 项目概述
Android游戏壳（浏览器），该项目运行的是一个游戏链接，链接目前在jysdk的初始化中获取。游戏网页通过js调用android的原生方法，从而实现了调用原生济游sdk。
## 运行环境
* 语言 java
* JDK 1.8
* 工具 Android Studio 3.3.2
* 主工程 compileSdkVersion 26、 targetSdkVersion 22、minSdkVersion 15

## aar依赖
目前该项目需要依赖一下5个aar包，在app工程的build.gradle中进行如下依赖配置
```
    implementation(name: 'jydudailib-release', ext: 'aar')
    implementation(name: 'jygeneralimplib-release', ext: 'aar')
    implementation(name: 'jysdklib-release', ext: 'aar')
    implementation(name: 'toutiaopluginlib-release', ext: 'aar')
    implementation(name: 'AkSDK_PayWebNative-release', ext: 'aar')
```

