package com.changgou.file.util;

import com.changgou.file.pojo.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastDFSUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FastDFSUtil.class);


    static {

        try {
            String filePath = new ClassPathResource("fdfs_client.conf").getPath();
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            logger.error("FastDFS Client Init Fail!", e);
        }
    }

    /***
     * 获取Tracker
     * @return
     * @throws IOException
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /***
     * 获取 StorageClient
     * @return
     * @throws IOException
     */
    private static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();

        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    /***
     * 文件上传
     * 0: groupName 1:remoteFileName
     * @param file
     * @return
     */
    public static String[] upload(FastDFSFile file) {
        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair("author", file.getAuthor());
        String[] uploadResults = null;
        StorageClient storageClient = null;

        try {
            storageClient = getStorageClient();
            uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), nameValuePairs);
        } catch (Exception e) {
            logger.info("fastDFS 上传文件异常：" + e);
            if (uploadResults == null && storageClient != null) {
                logger.info("fastDFS 文件上传失败：" + storageClient.getErrorCode());
            }

        }
        return uploadResults;
    }

    /***
     * 文件下载
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static InputStream downFile(String groupName, String remoteFileName) {
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            byte[] download_file = storageClient.download_file(groupName, remoteFileName);
            InputStream inputStream = new ByteArrayInputStream(download_file);
            return inputStream;
        } catch (Exception e) {
            logger.info("fastDFS 下载文件失败：" + e);
        }

        return null;
    }

    /***
     * 文件删除
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName, String remoteFileName) {
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            storageClient.delete_file(groupName, remoteFileName);
        } catch (Exception e) {
            logger.info("fastDFS 删除文件失败：" + e);
        }
    }

    public static String getTrackerUrl() throws IOException {
        return "http://" + getTrackerServer().getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port() + "/";
    }

}
