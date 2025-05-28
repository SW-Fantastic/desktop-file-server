# File Server

基于Vertx的桌面文件服务器，支持文件上传、下载、删除等功能，
本服务器提供FTP和WebDAV两种协议的访问方式，可以与操作系统原生集成。

当然，WebDAV和FTP的协议都是我手写的，并不是基于现有的开源库，所以难免
会有一些不完善的地方。

本项目还在持续的开发中，欢迎提出任何意见。

## 项目结构

```
|-- src
|   |-- main
|       |-- java
|       |   |-- org.swdc.rmdisk       // 项目包，同时包含项目的launcher，通过我自行搭建的框架实现项目的启动类，配置类等
|       |       |-- client            // 客户端协议和数据处理
|       |       |-- core              // 核心模块，ORM的领域对象，日志，FTP和WebDAV的通用类型
|       |       |-- service           // 服务端协议和数据处理，基于Vertx的FTP和WebDAV服务
|       |       |-- views             // 基于JavaFX的UI的View和Controllers
|       |-- resources                 // 资源文件
|           |-- database              // 数据库配置，hibernate框架使用里面的配置文件加载数据库
|           |-- icons                 // 图标，启动后的JavaFX窗口需要它们
|           |-- lang                  // 语言文件，暂时不支持多语言，但是预留了接口
|           |-- views                 // JavaFX的FXML文件
|           |-- webcontent            // 请首先构建srcweb的前端，它的dist目录的内容就是这里的内容。
|-- assets                            // 应用的资源目录，包含配置文件和主题
|-- data                              // 应用的运行时数据目录，这里存放的是MySQL的数据库文件，它的内容会在首次启动后自动初始化。
|-- logs                              // 日志目录
|-- srcweb                            // WebDAV的Web前端源码目录
|   |-- dist                          // Web打包目录，需要放到到webcontent中。
|   |-- node_modules                  // nodejs类库，通常可以通过npm下载到。
|   |-- public                        // Web资源目录
|   |-- src                           // Web源码，本项目使用Vue3 这里存放Vue组件


```

## 依赖的项目

以下项目是本项目依赖的，它们是我自行开发的，所以如果想要
编译本项目，需要首先clone它们并且install到本地仓库。

它们都可以在我的Github仓库中找到：

基础框架：

- org.swdc/application-fx
- org.swdc/application-configure
- org.swdc/application-component
- org.swdc/our-commons

数据库：
- org.swdc/swdata
- org.swdc/mariadb
- org.swdc/mariadb-embedded

## 第三方项目引用

- vertx：服务器的基础框架
- password4j：用户的密码处理
- jcifs：用于NTLM协议的认证
- caffeine：缓存框架
- jjwt：令牌和Token的生成及验证
- apache-tika：Metadata以及MimeType的检测
- Vue3: Web前端基础框架
- Antd-Vue：Ant design组件库

