# spring-boot-file-browser-example — 局域网文件共享

> 这个项目深刻地揭示了什么是无聊的最高境界：用企业级框架 Spring Boot
> 精心打造一个本该几行代码就能搞定的局域网文件共享，却做得如此完善、如此严谨，以至于你不得不佩服这种对无聊的极致追求。

## 📖 项目简介

**spring-boot-file-browser-example** 是一个基于 Spring Boot 的极简局域网文件共享示例。它提供了文件列表、在线预览、下载等基础功能，并自带一个清爽的
Web 界面。虽然功能简单，但代码中却处处体现着“无聊的工匠精神”——从路径安全校验、中文文件名编码，到数十种 MIME Type
的硬核映射，再到打包成多种可执行文件发布，每一个细节都被认真对待。这正是无聊的最高境界：在毫无意义的事情上追求极致，最终做出一个有意义的小工具
。

---

## ✨ 功能特性

- 📁 **文件浏览** – 列出目录下的文件和文件夹，支持导航进入子目录。
- 👁️ **在线预览** – 直接查看图片、PDF、文本等（浏览器支持的情况下）。
- ⬇️ **文件下载** – 一键下载文件，支持中文文件名。
- 🧭 **友好的 Web 界面** – 内置 HTML 页面，包含面包屑导航、文件大小格式化、双击/按钮操作。
- 🛡️ **路径安全** – 严格防止路径遍历攻击（`..` 绕过）。
- 📦 **多种运行方式** – 源码运行、免 Java 环境的 exe 安装包、轻量级 exe 启动器。
- 🧩 **跨平台** – 基于 Java，理论上可在任何支持 Java 的系统运行（但 exe 仅限 Windows）。

---

## 🛠️ 技术栈

- **Java 17+** – 使用 `jakarta.servlet`，基于最新 Jakarta EE。
- **Spring Boot 3.x** – 提供 REST API 和嵌入式 Web 服务器。
- **前端** – 纯 HTML/CSS/JavaScript（零依赖，原生实现）。
- **打包工具** – jpackage（生成包含 JRE 的安装包）、Launch4j（生成 exe 启动器）。

---

## 🚀 快速开始

### 方法一：直接运行可执行文件（最简单）

从 [Releases](https://github.com/xumous/spring-boot-file-browser-example/releases) 下载适合你的 exe 文件：

| 文件                              | 说明                                                      |
|---------------------------------|---------------------------------------------------------|
| `*_jpackage.exe`                | 使用 jpackage 打包的安装程序，**包含 JRE**，安装后可直接运行（无需预装 Java）。     |
| `*_launch4j.exe`                | 使用 Launch4j 包装的 exe，**需要系统已安装 JRE 17+**，双击运行（会显示控制台窗口）。 |
| `*_hidden-console_launch4j.exe` | 同上，但**隐藏控制台窗口**，适合后台运行。                                 |

运行后，打开浏览器访问 `http://localhost:8080/files/index` 即可看到局域网文件共享界面。

> ⚠️ 注意：默认下载目录为 `F:/common_share`，如果该目录不存在，程序会自动创建。请确保该路径存在且可访问，或参考下文修改目录。

### 方法二：从源码运行

1. **克隆仓库**
   ```bash
   git clone https://github.com/xumous/spring-boot-file-browser-example.git
   cd spring-boot-file-browser-example
   ```

2. **使用 Maven 运行**
   ```bash
   mvn spring-boot:run
   ```
   或导入 IDE 后直接运行 `main` 方法。

3. **修改下载目录（可选）**  
   默认下载目录为 `F:/common_share`。如需修改，可以在启动前通过代码设置：
   ```java
   com.demo_249050433.controller.FileController.setDownloadDir("你的绝对路径");
   ```
   或者在 `FileController` 中直接修改 `DOWNLOAD_DIR` 静态变量。

4. **访问界面**  
   打开浏览器：`http://localhost:8080/files/index`

---

## 📝 使用说明

首页会显示当前目录下的所有文件和文件夹：

- **文件夹**：点击文件夹名称或“打开”按钮即可进入子目录。
- **文件**：
    - 点击文件名称或“查看”按钮，会在新标签页中尝试预览文件（浏览器支持则直接显示，否则会下载）。
    - 点击“下载”按钮直接下载文件。
- **顶部路径栏**：显示当前目录的层级，点击任意层级可快速跳转。

文件列表按“文件夹优先、名称排序”排列，文件大小会自动格式化为 B/KB/MB/GB。

---

## 🔍 接口文档

| 端点                   | 方法  | 作用                                      | 示例                            |
|----------------------|-----|-----------------------------------------|-------------------------------|
| `/files/index`       | GET | 返回内置的 HTML 界面                           | `/files/index`                |
| `/files/list?path=`  | GET | 获取指定路径下的文件和文件夹列表（JSON 格式）               | `/files/list?path=subfolder`  |
| `/files/view/**`     | GET | 在线查看文件（`Content-Disposition: inline`）   | `/files/view/image.jpg`       |
| `/files/download/**` | GET | 下载文件（`Content-Disposition: attachment`） | `/files/download/archive.zip` |

**注**：`**` 表示路径通配符，支持多级子目录，例如 `/files/view/documents/report.pdf`。

---

## 🧠 无聊的最高境界体现在哪里？

- **用牛刀杀鸡**：Spring Boot + 全套 REST API，只为做一个简单的局域网文件共享。
- **安全至上**：代码中严格校验路径，防止 `../` 绕过，即便本地运行也绝不马虎。
- **文件名编码的极致**：支持中文文件名，使用 RFC 5987 标准编码，并提供了 ASCII 回退方案。
- **MIME Type 硬核映射**：手写了数十种文件扩展名到 Content-Type 的映射，覆盖图片、文档、音视频等常见格式。
- **前端无依赖**：没使用任何前端框架，纯原生 JavaScript 实现面包屑导航、文件大小格式化、双击/按钮操作，代码清晰且功能完整。
- **打包成 exe**：为了让不懂 Java 的用户也能体验这个无聊的项目，提供了三种 exe 版本（含 JRE 的安装版、带控制台的启动版、隐藏控制台版），甚至用了
  jpackage 和 Launch4j 两种工具。

---

## ⚠️ 注意事项

- 默认下载目录为 `F:/common_share`，如果不存在会自动创建。请确保该路径没有敏感文件（如果暴露在公网）。
- 项目仅提供文件浏览功能，不支持上传、删除等操作。
- 若作为公网服务使用，请务必做好访问控制（例如增加登录验证），避免泄露隐私。
- 对于 exe 版本，隐藏控制台的启动方式可能无法看到程序输出，若遇到问题可先使用带控制台的版本调试。

---

## 🔧 自定义与构建

### 修改下载目录

- **源码方式**：在启动类中调用 `FileController.setDownloadDir("你的路径")`。
- **exe 方式**：目前不支持外部配置，可考虑后续通过环境变量或配置文件扩展。

### 自行打包 exe

如果你想体验打包过程：

- **jpackage**（需要 JDK 14+）：
  ```bash
  jpackage --name file-browser --input target --main-jar your-app.jar --type exe --win-console
  ```
- **Launch4j**：使用图形工具或配置文件将 jar 包装成 exe。

---

## 🤝 贡献

欢迎提交 Issue 或 Pull Request！但请保持项目无聊的风格：任何改进都必须在不增加实用性的前提下，尽量增加代码的复杂度或趣味性。

---

## 📄 许可证

[MIT](LICENSE)

---

> 如果你觉得这个项目无聊得有趣，请给一个 ⭐️ 支持一下！  