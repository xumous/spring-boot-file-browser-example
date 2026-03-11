package com.demo_249050433.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/files")
public class FileController {
    // 从应用启动时获取的目录，获取失败则使用默认值
    public static String DOWNLOAD_DIR = "F:/common_share";

    /**
     * 设置下载目录（在应用启动时调用）
     */
    public static void setDownloadDir(String downloadDir) {
        if (downloadDir != null && !downloadDir.trim().isEmpty()) {
            DOWNLOAD_DIR = downloadDir;
            // 确保目录存在
            File dir = new File(DOWNLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * 文件下载（会弹出保存对话框）
     */
    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        String path = extractFilePath(request);
        return handleFileRequest(path, "attachment");
    }

    /**
     * 文件查看/预览（直接在浏览器中打开）
     */
    @GetMapping("/view/**")
    public ResponseEntity<Resource> viewFile(HttpServletRequest request) {
        String path = extractFilePath(request);
        return handleFileRequest(path, "inline");
    }

    /**
     * 从请求中提取文件路径
     */
    private String extractFilePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 移除前缀
        path = path.replaceFirst("^/files/(download|view)/", "");
        // 处理URL编码
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
            // 将URL中的反斜杠替换为正斜杠（Windows系统处理）
            path = path.replace('\\', '/');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 处理文件请求的通用方法
     *
     * @param relativePath 相对于DOWNLOAD_DIR的文件路径
     * @param disposition  "attachment"表示下载，"inline"表示查看
     */
    private ResponseEntity<Resource> handleFileRequest(String relativePath, String disposition) {
        try {
            // 安全检查：防止路径遍历攻击
            Path safeBaseDir = Paths.get(DOWNLOAD_DIR).normalize();
            Path filePath = safeBaseDir.resolve(relativePath).normalize();

            // 验证文件路径是否在指定目录内
            if (!filePath.startsWith(safeBaseDir)) {
                System.out.println("非法文件路径: " + relativePath);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            System.out.println("尝试访问文件: " + filePath.toAbsolutePath());
            Resource resource = new FileSystemResource(filePath);

            // 检查文件是否存在且可读
            if (!resource.exists()) {
                System.out.println("文件不存在: " + filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 获取文件名（用于Content-Disposition）
            String filename = Paths.get(relativePath).getFileName().toString();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();

            // 对中文文件名进行编码，避免HTTP头中的字符编码问题
            String encodedFilename = URLEncoder.encode(filename, String.valueOf(StandardCharsets.UTF_8))
                    .replaceAll("\\+", "%20");

            // 使用RFC 5987标准编码文件名，支持中文
            String contentDisposition = String.format("%s; filename=\"%s\"; filename*=UTF-8''%s",
                    disposition,
                    getSafeAsciiFilename(filename), // ASCII回退
                    encodedFilename);

            headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

            // 根据文件类型设置Content-Type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                // 根据文件扩展名猜测内容类型
                contentType = getContentTypeFromExtension(filename);
            }

            System.out.println("文件" + ("attachment".equals(disposition) ? "下载" : "查看") +
                    "成功: " + relativePath + ", Content-Type: " + contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * 获取安全的ASCII文件名（用于Content-Disposition的filename参数）
     */
    private String getSafeAsciiFilename(String filename) {
        // 将非ASCII字符替换为下划线
        return filename.replaceAll("[^\\x20-\\x7E]", "_");
    }

    /**
     * 根据文件扩展名获取内容类型
     */
    private String getContentTypeFromExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 获取文件和文件夹列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(@RequestParam(required = false) String path) {
        try {
            Path basePath = Paths.get(DOWNLOAD_DIR);
            Path currentPath;

            if (path != null && !path.isEmpty()) {
                currentPath = basePath.resolve(path).normalize();
                // 安全检查
                if (!currentPath.startsWith(basePath)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                currentPath = basePath;
            }

            File currentDir = currentPath.toFile();
            if (!currentDir.exists() || !currentDir.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<Map<String, Object>> items = new ArrayList<>();
            File[] files = currentDir.listFiles();

            if (files != null) {
                // 先添加文件夹
                Arrays.stream(files)
                        .filter(File::isDirectory)
                        .sorted()
                        .forEach(file -> {
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", file.getName());
                            item.put("type", "folder");
                            item.put("path", getRelativePath(basePath, file.toPath()));
                            items.add(item);
                        });

                // 再添加文件
                Arrays.stream(files)
                        .filter(File::isFile)
                        .sorted()
                        .forEach(file -> {
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", file.getName());
                            item.put("type", "file");
                            item.put("path", getRelativePath(basePath, file.toPath()));
                            item.put("size", file.length());
                            // 获取文件扩展名
                            String filename = file.getName();
                            int dotIndex = filename.lastIndexOf('.');
                            if (dotIndex > 0) {
                                item.put("extension", filename.substring(dotIndex + 1).toLowerCase());
                            }
                            items.add(item);
                        });
            }

            Map<String, Object> result = new HashMap<>();
            result.put("items", items);
            result.put("currentPath", getRelativePath(basePath, currentPath));
            result.put("parentPath", getParentRelativePath(basePath, currentPath));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * 获取相对于基目录的路径
     */
    private String getRelativePath(Path basePath, Path targetPath) {
        if (basePath.equals(targetPath)) {
            return "";
        }
        return basePath.relativize(targetPath).toString().replace('\\', '/');
    }

    /**
     * 获取父目录的相对路径
     */
    private String getParentRelativePath(Path basePath, Path currentPath) {
        if (currentPath.equals(basePath)) {
            return null;
        }
        Path parentPath = currentPath.getParent();
        if (parentPath == null || !parentPath.startsWith(basePath)) {
            return null;
        }
        return basePath.relativize(parentPath).toString().replace('\\', '/');
    }

    /**
     * 添加一个简单的HTML页面，方便用户操作
     */
    @GetMapping("/index")
    public String fileIndex() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>文件管理系统</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                        h1 { color: #333; margin-bottom: 20px; }
                        .current-path { 
                            background-color: #e9ecef; 
                            padding: 10px 15px; 
                            border-radius: 5px; 
                            margin-bottom: 20px; 
                            display: flex; 
                            align-items: center;
                        }
                        .path-segment { 
                            color: #007bff; 
                            cursor: pointer; 
                            margin: 0 5px; 
                        }
                        .path-segment:hover { text-decoration: underline; }
                        .path-separator { margin: 0 5px; color: #6c757d; }
                        .file-list { 
                            background-color: white; 
                            border-radius: 5px; 
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1); 
                            overflow: hidden;
                        }
                        .file-item { 
                            padding: 12px 15px; 
                            border-bottom: 1px solid #eee; 
                            display: flex; 
                            align-items: center;
                            cursor: pointer;
                            transition: background-color 0.2s;
                        }
                        .file-item:hover { background-color: #f8f9fa; }
                        .file-item:last-child { border-bottom: none; }
                        .file-icon { 
                            margin-right: 10px; 
                            width: 24px; 
                            text-align: center;
                            color: #6c757d;
                        }
                        .folder-icon { color: #ffc107; }
                        .file-name { flex: 1; }
                        .file-size { 
                            color: #6c757d; 
                            font-size: 0.9em; 
                            margin-right: 15px;
                        }
                        .file-actions { display: flex; gap: 10px; }
                        .action-btn { 
                            padding: 4px 12px; 
                            cursor: pointer; 
                            border: none;
                            border-radius: 4px;
                            color: white;
                            font-size: 0.9em;
                            transition: opacity 0.2s;
                        }
                        .action-btn:hover { opacity: 0.9; }
                        .view-btn { background-color: #28a745; }
                        .download-btn { background-color: #007bff; }
                        .empty-message { 
                            padding: 40px; 
                            text-align: center; 
                            color: #6c757d; 
                            font-style: italic;
                        }
                        .folder-children { 
                            margin-left: 30px; 
                            display: none;
                            border-left: 2px solid #e9ecef;
                        }
                        .folder-children.expanded { display: block; }
                        .folder-toggle { 
                            margin-right: 5px; 
                            cursor: pointer; 
                            color: #6c757d;
                            width: 20px;
                            text-align: center;
                        }
                        .folder-header { 
                            font-weight: bold; 
                            background-color: #f8f9fa; 
                        }
                    </style>
                </head>
                <body>
                    <h1>文件管理系统</h1>
                    <div id="currentPath" class="current-path">
                        <span id="pathDisplay">根目录</span>
                    </div>
                    <div class="file-list" id="fileList">
                        <div class="empty-message">正在加载文件列表...</div>
                    </div>
                                
                    <script>
                        let currentPath = '';
                                
                        // 加载文件列表
                        function loadFileList(path) {
                            const url = path ? `/files/list?path=${encodeURIComponent(path)}` : '/files/list';
                            fetch(url)
                                .then(response => {
                                    if (!response.ok) throw new Error('加载失败');
                                    return response.json();
                                })
                                .then(data => {
                                    currentPath = data.currentPath || '';
                                    updatePathDisplay(data.currentPath, data.parentPath);
                                    renderFileList(data.items);
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    document.getElementById('fileList').innerHTML = 
                                        '<div class="empty-message">加载文件列表失败</div>';
                                });
                        }
                                
                        // 更新路径显示
                        function updatePathDisplay(currentPath, parentPath) {
                            const pathDisplay = document.getElementById('pathDisplay');
                            if (!currentPath) {
                                pathDisplay.innerHTML = '<span class="path-segment" onclick="loadFileList(\\'\\')">根目录</span>';
                                return;
                            }
                                
                            let html = '<span class="path-segment" onclick="loadFileList(\\'\\')">根目录</span>';
                            const segments = currentPath.split(/[\\\\/]/);
                            let accumulatedPath = '';
                                
                            segments.forEach((segment, index) => {
                                if (segment) {
                                    if (accumulatedPath) accumulatedPath += '/';
                                    accumulatedPath += segment;
                                    html += `<span class="path-separator">/</span>
                                            <span class="path-segment" onclick="loadFileList('${accumulatedPath}')">${segment}</span>`;
                                }
                            });
                                
                            pathDisplay.innerHTML = html;
                        }
                                
                        // 渲染文件列表
                        function renderFileList(items) {
                            const fileListDiv = document.getElementById('fileList');
                                
                            if (!items || items.length === 0) {
                                fileListDiv.innerHTML = '<div class="empty-message">文件夹为空</div>';
                                return;
                            }
                                
                            fileListDiv.innerHTML = '';
                                
                            items.forEach(item => {
                                const fileDiv = document.createElement('div');
                                fileDiv.className = 'file-item';
                                
                                if (item.type === 'folder') {
                                    fileDiv.innerHTML = `
                                        <div class="file-icon folder-icon">📁</div>
                                        <div class="file-name">${item.name}</div>
                                        <div class="file-actions">
                                            <button class="action-btn view-btn" onclick="loadFileList('${item.path}')">打开</button>
                                        </div>
                                    `;
                                    fileDiv.onclick = (e) => {
                                        if (!e.target.closest('.action-btn')) {
                                            loadFileList(item.path);
                                        }
                                    };
                                } else {
                                    // 对文件路径进行双重编码，确保特殊字符正确处理
                                    const encodedPath = encodeURIComponent(item.path);
                                    const doubleEncodedPath = encodeURIComponent(encodedPath);
                                
                                    fileDiv.innerHTML = `
                                        <div class="file-icon">📄</div>
                                        <div class="file-name">${item.name}</div>
                                        <div class="file-size">${formatFileSize(item.size)}</div>
                                        <div class="file-actions">
                                            <button class="action-btn view-btn" onclick="viewFile('${encodedPath}')">查看</button>
                                            <button class="action-btn download-btn" onclick="downloadFile('${encodedPath}')">下载</button>
                                        </div>
                                    `;
                                    fileDiv.onclick = (e) => {
                                        if (!e.target.closest('.action-btn')) {
                                            viewFile(encodedPath);
                                        }
                                    };
                                }
                                
                                fileListDiv.appendChild(fileDiv);
                            });
                        }
                                
                        // 查看文件
                        function viewFile(path) {
                            // 对路径进行双重解码
                            const decodedPath = decodeURIComponent(path);
                            window.open(`/files/view/${decodedPath}`, '_blank');
                        }
                                
                        // 下载文件
                        function downloadFile(path) {
                            // 对路径进行双重解码
                            const decodedPath = decodeURIComponent(path);
                            window.location.href = `/files/download/${decodedPath}`;
                        }
                                
                        // 格式化文件大小
                        function formatFileSize(bytes) {
                            if (bytes === 0) return '0 B';
                            const k = 1024;
                            const sizes = ['B', 'KB', 'MB', 'GB'];
                            const i = Math.floor(Math.log(bytes) / Math.log(k));
                            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
                        }
                                
                        // 页面加载时初始化
                        document.addEventListener('DOMContentLoaded', function() {
                            loadFileList('');
                        });
                    </script>
                </body>
                </html>
                """;
    }
}