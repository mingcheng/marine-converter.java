# NMEA 日志文件转换为 JSON 格式，同时在地图显示

### 注意，这个项目几乎不怎么更新

![http://files.gracecode.com/2015_12_22/1450775876@800.png](https://friable.rocks/_/2015_12_22/1450775876@800.png)

本人实用的车载导航其中有个叫 [DSA 软件](http://www.zenlane.com/html/index.html)，它可以记录导航的原生 NMEA 信息。因此，有考虑解析其中的信息统计并显示到地图上的想法，目前已经达到了可用的状态。

## 概述

简单的讲，将 NMEA 日志显示到地图上需要两个步骤：

1. 实用 Java 的 NMEA 解析类库将其中的信息解析为 JSON 格式
2. 使用 高德地图 SDK 将 JSON 格式的数据显示到地图上

## 使用说明

清理项目以及编译成 jar 包（需要 Gradle 的支持）：

```gradle clean jar```

然后运行

```java -jar build/libs/marine-converter.jar```

<del>如果没有错误信息（大误）</del>，则可以正常使用了，其中参数对应本地的的目录即可。然后，再将转换好的 `json` 文件拷贝到 `web` 对应的目录中就可以可视化显示。

## 更新记录

### 2018-09-01

纳入 Gradle 项目管理， @TODO 优化下代码？

### 2015-12-22

完成基本功能

## 参考资料

* https://github.com/ktuukkan/marine-api
* https://github.com/xdan/datetimepicker
* https://commons.apache.org/proper/commons-cli/
* http://lbs.amap.com/
