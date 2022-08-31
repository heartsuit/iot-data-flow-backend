# 物联网设备数据是如何流转的：基于EMQX与TDengine的前后端分离项目实践

## 背景

在我写了[TDengine极简实战：从采集到入库，从前端到后端，体验物联网设备数据流转](https://blog.csdn.net/u013810234/article/details/122400447)这篇文章后，不少读者朋友评论、私信说可不可以提供代码参考学习下，那必须是可以的。那篇文章主要介绍了数据采集、数据传输、数据转发、数据解析、数据存储、数据查询、数据呈现、数据导出、消息推送的物联网设备数据流程过程，作为体验 `TDengine` 的极简实战项目，没引入过多的业务设计（复杂功能）以及异步解耦设计（消息中间件），甚至没有用到微服务架构。

![2022-04-17-IoT-DataFlow.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-04-17-IoT-DataFlow.jpg)

在整理代码的过程中，我发现虽然整个项目并不复杂，但是涉及到的技术点较多，有必要对关键的组件进行说明；当前代码已经整理完毕，并升级 `EMQX` （4.2.4-->4.4.2）与 `TDengine` （2.2.0.0-->2.4.0.16）这两个核心服务组件为最新版。不过实践出真知，我还是建议大家能够自己动手实践下，因此我决定将实现的过程拆分为不同的模块，逐篇介绍下从数据采集、转发、存储、查询、推送、展现、导出的全过程，相当于这个极简实战项目相对详细的 `README.md` 文档。

> 人至践则无敌。

## 写作计划

将实现过程进行分解，拆分为几个关键步骤，在安排各篇文章时，尽量拆分为小的模块，小步快跑，初步暂定以下内容。

01. 物联网设备数据流转之搭建环境：开源云原生分布式物联网MQTT消息服务器EMQX
02. 物联网设备数据流转之搭建环境：开源高性能分布式支持SQL的时序数据库TDengine
03. 物联网设备数据流转之实时数据从哪里来、如何转发：Node.js, MQTT, EMQX的Webhook
04. 物联网设备数据流转之搭建后端服务框架：SpringBoot统一响应封装，全局异常拦截
05. 物联网设备数据流转之数据如何存储：TDengine集成SpringBoot, MyBatisPlus实现ORM与CRUD
06. 物联网设备数据流转之数据如何查询：TDengine条件查询, 聚合查询, 分页查询, TopN查询, 降采样
07. 物联网设备数据流转之搭建前端服务框架：Vue3.0, ElementPlus, Axios, Echarts
08. 物联网设备数据流转之前后端数据交互与展示：Layout, Cascader, Card, Dialog, Table, Pagination
09. 物联网设备数据流转之数据如何进行可视化：Echarts图表
10. 物联网设备数据流转之数据如何导出：Excel文件
11. 物联网设备数据流转之数据何时存储：Spring事件及监听机制, 数据入库
12. 物联网设备数据流转之数据如何实时推送至前端：WebSocket服务端推送
13. 物联网设备数据流转之数据如何实时推送至前端：WebSocket前端接收
14. 物联网设备数据流转之告警信息推送：TDengine-alert
15. 物联网设备数据流转之历史数据从哪里来：TDengine批量写入数据

## 技术选型

![2022-04-17-Mind.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-04-17-Mind.jpg)

* 物联网终端技术：
    - MQTT
    - EMQX
    - Node.js

* 后端技术：
    - SpringBoot
    - TDengine
    - TDengine-alert
    - MyBatisPlus
    - WebSocket
    - Lambda
    - HuTool

* 前端技术：
    - Vue
        * vue-router

    - ElementPlus

        * Layout 响应式布局
        * Cascader 级联选择器
        * Card 卡片
        * Dialog 对话框
        * Table 表格
        * Pagination 分页
        * DateTimePicker 日期时间选择器
        * Message 消息提示

    - Echarts

## 适合谁看

从整个架构来看， `TDengine` 在整个系统中扮演的角色无非就是个数据库，所以其实也没多复杂，我们可以简单将其理解为类似 `MySQL` , `PostgreSQL` , `MongoDB` , `ElasticSearch` , `HBase` , `ClickHouse` 抑或 `Redis` 这种存储层，只是这对应的每一种存储方案有其独特之处，需要进行针对性的学习，关于 `TDengine` 各项功能的具体细节，我们这里不做展开，可参考前面写的 `TDengine` 入门系列共15篇文章。

希望熟悉物联网设备数据流转过程的后端工程师、前端工程师以及运维工程师。

如果你是一名后端工程师，我假设你的机器上当前已具备Java环境（JDK，Maven）
如果你是一名前端工程师，我假设你的工作环境肯定已经安装了Node.js
如果你是一名运维工程师，我假设你对Linux的基本操作必然已烂熟于心

可是，如果你是一个小白，那也没关系，经过这个系列，在熟悉物联网设备流转过程的同时你会顺便了解下如今主流的前后端分离技术栈是如何串联起来的。

> 你要学前端、后端还是运维？作为成年人，我三个都要学。

在实际工作中，我一直都建议团队里的同学要**一专多能**，前端的同学可以接触下后端或运维的知识，后端的同学也可以适当了解下前端的内容。接触并不代表一定要做，**了解整体可以培养你的全局观**，这是作为系统架构师的必备技能。假设作为前端工程师的你遇到了一个难题，你只会在前端的范围内钻研，可能这个问题在后端处理只是一行代码的事情，然而，你并不知道呀，所以会浪费不少时间。下图是一张来自于 `GitHub` 的2019年的后端工程师成长路线图，即**一专多能**的*专*：

![2022-04-17-Backend.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-04-17-Backend.jpg)

## 有哪些收获

通过这个 `TDengine` 极简实践系列，你可以学到什么？

01. 物联网设备数据流转过程
02. Node.js模拟MQTT客户端
03. EMQX WebHook消息路由
04. TDengine亿级数据存储与查询
05. TDengine-alert告警信息推送
06. SpringBoot+MyBatisPlus服务端数据ORM、分页
07. 统一响应封装、全局异常拦截
08. Spring事件与监听机制
09. Hutool与poi完成Excel文件导出
10. WebSocket实时消息推送
11. Vue3.0的基本使用
12. ElementPlus主要组件应用
13. ECharts数据可视化图表

![2022-04-17-Mind-Index.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-04-17-Mind-Index.jpg)

## 扩展实践

这个项目本身是个 `Demo` 级的练手项目，仅提供一个想法与思路，当然如果你有心扩展，以下是一些不错的方向。
01.  该项目可以作为本科毕业设计的项目原型，加以完善、升级后可以作为毕业设计
02.  该项目可以作为初级前、后端工程师的练手项目，用以熟悉前后端的交互操作
03.  该项目还可以作为对物联网感兴趣的同学们一个入门实例进行扩展，深入体验物联网设备数据流转
04.  。。。

## Reference

* [https://blog.csdn.net/u013810234/article/details/122400447](https://blog.csdn.net/u013810234/article/details/122400447)
* [https://github.com/kamranahmedse/developer-roadmap](https://github.com/kamranahmedse/developer-roadmap)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***