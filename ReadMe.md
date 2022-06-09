# Data Exchange （数据交换平台）

## 设计思路



## 设计图

### 架构图 ###
### 架构图 ###
![](https://github.com/achui1980/data-exchange/blob/develop/diagram/architecture.png?raw=true)
### 数据流 ###
![](https://raw.githubusercontent.com/achui1980/data-exchange/develop/diagram/dataflow.png)
Edit

## 设计实现

- 模块化
    - [x] 将消费端和数据产生端分开成两个模块
    - [x] 数据模型独立成一个模块，供消费端和生产端使用
    - [x] 数据生产端处理后的数据进行对象序列化（当前都是String对象）
    - [x] 消费端能够自动识别生产端的对象，并进行反序列化

## 现有功能

- 从数据源获取数据

    - [x] SFTP
    - [ ] API
- 数据源格式解析

    - [x] CSV
    - [x] Excel
- 写入kafka

    - [x] Spring Kafa integration
- 写入redis

    - [x] Spring Redis ingegration
- 对接收到的每条数据进行处理

    - 业务逻辑可以根据实际的需要进行编写
- 将redis的数据统一获取写入NFS

    - 业务逻辑可以根据实际的需要进行编写
- 生成业务指标

    - [ ] 任务处理时间
### 开发组件
- Spring Boot (API开发)
- [Easy Batch](https://github.com/j-easy/easy-batch)
- [Guava (并发编程, EventBus)](https://github.com/google/guava)
- Kafka
- Redis
- [OpenCsv](http://opencsv.sourceforge.net/)
- [Easy Excel](https://easyexcel.opensource.alibaba.com/docs/current/)