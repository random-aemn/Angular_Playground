package com.lessons.sync.services;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Service("com.lessons.sync.services.FileService")
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${s3.upload-bucket-name:null}")
    private String s3BucketName;

    @Value("${s3.enabled}")
    private Boolean isS3Enabled;

    @PostConstruct
    public void init(){
        if (this.isS3Enabled){
            if (StringUtils.isBlank(this.s3BucketName)){
                throw new RuntimeException("The S3 documents upload bucket name is blank");
            }
        }
    }

    public void deleteAllUploadedFilesInS3(){
        try {
            S3Client s3 = S3Client.builder()
                    .crossRegionAccessEnabled(true)
                    .build();

            ArrayList<ObjectIdentifier> keys = listBucketObjects(s3);

            if (!keys.isEmpty()){
                logger.debug("Starting to delete all objects in the S3 bucket: " + s3BucketName);

                Delete del = Delete.builder()
                        .objects(keys)
                        .build();

                DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                        .bucket(s3BucketName)
                        .delete(del)
                        .build();

                s3.deleteObjects(multiObjectDeleteRequest);
                logger.debug("Successfully deleted all objects in the S3 bucket: " + s3BucketName);
            }

            else {
                logger.debug("There are no objects to delete in the S3 bucket: " + s3BucketName);
            }

        } catch (S3Exception e) {
            logger.error("Error in deleteAllUploadedFilesInS3(). The deletion of S3 objects failed", e);
            RuntimeException re = new RuntimeException();
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    public ArrayList<ObjectIdentifier> listBucketObjects(S3Client s3) throws S3Exception {
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(s3BucketName)
                .build();

        ListObjectsResponse res = s3.listObjects(listObjects);
        List<S3Object> listofObjects = res.contents();
        ArrayList<ObjectIdentifier> keys = new ArrayList<>();
        for (S3Object obj : listofObjects) {
            keys.add(ObjectIdentifier.builder()
                    .key(obj.key())
                    .build());
        }
        return keys;
    }
}
