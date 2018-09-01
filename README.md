# NMEA 日志文件转换为 JSON 格式同时在地图显示

![http://files.gracecode.com/2015_12_22/1450775876@800.png](https://friable.rocks/_/2015_12_22/1450775876@800.png)



本人实用的车载导航其中有个叫 DSA 软件的可以记录导航的原生 NMEA 信息，因此有考虑将其中的信息统计并显示到地图上的想法。

这个项目就是根据这个想法而得来的，目前已经达到了可用的状态。

## 概述

简单的讲，将 NMEA 日志显示到地图上需要两个步骤：

1. 实用 Java 的 NMEA 解析类库将其中的信息解析为 JSON 格式
2. 使用 高德地图 SDK 将 JSON 格式的数据显示到地图上

## 更新记录

### 2015-12-22

完成基本功能

## 参考资料

* https://github.com/ktuukkan/marine-api
  
* https://github.com/xdan/datetimepicker
  
* https://commons.apache.org/proper/commons-cli/
  
* http://lbs.amap.com/
  
  ​

