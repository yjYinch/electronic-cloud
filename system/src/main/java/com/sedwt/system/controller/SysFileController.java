package com.sedwt.system.controller;

import com.sedwt.common.config.ServerConfig;
import com.sedwt.common.core.controller.BaseController;
import com.sedwt.common.core.domain.R;
import com.sedwt.system.domain.AttachedFile;
import com.sedwt.system.service.ISysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/file")
public class SysFileController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private ISysFileService fileService;

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     */
//    @GetMapping("/download")
//    public void fileDownload(String fileName, HttpServletResponse response)
//    {
//        try
//        {
//            fileService.fileDownload(fileName, response);
//        }
//        catch (Exception e)
//        {
//            log.error("下载文件失败", e);
//        }
//    }

    /**
     * 通用上传请求
     */
    @PostMapping("/upload")
    public R uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("attachedFile") AttachedFile attachedFile) throws Exception
    {
        try
        {
            attachedFile.setCreateBy(String.valueOf(getCurrentUserId()));
            return fileService.uploadFile(file, attachedFile);
        }
        catch (Exception e)
        {
            return R.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public R delete(@RequestParam("fileId") Long fileId){
        return fileService.delete(fileId);
    }

    @DeleteMapping("/delete/project")
    public void deleteByProject(@RequestParam("projectId") Long projectId){
        fileService.deleteFileByProjectId(projectId);
    }

    /**
     * 本地资源通用下载
     */
//    @GetMapping("/download/resource")
//    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
//            throws Exception
//    {
//        try
//        {
//            if (!FileUtils.checkAllowDownload(resource))
//            {
//                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
//            }
//            // 本地资源路径
//            String localPath = SedwtConfig.getProfile();
//            // 数据库资源地址
//            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
//            // 下载名称
//            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//            FileUtils.setAttachmentResponseHeader(response, downloadName);
//            FileUtils.writeBytes(downloadPath, response.getOutputStream());
//        }
//        catch (Exception e)
//        {
//            log.error("下载文件失败", e);
//        }
//    }
}
