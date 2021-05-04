package com.project.knit.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.knit.dto.res.S3ImageResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

@Slf4j
//@RequiredArgsConstructor
@Service
public class S3Service {

    //Amazon-s3-sdk
    private AmazonS3 s3Client;

    private final String bucketName = "knit-document";

    private final List<String> extensionList = asList("jpg", "jpeg", "png", "gif");

    private S3Service(@Value("${aws.access-key}") final String accessKey, @Value("${aws.secret-key}") final String secretKey) {
        createS3Client(accessKey, secretKey);
    }

    //aws S3 client 생성
    private void createS3Client(final String accessKey, final String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    public S3ImageResDto upload(MultipartFile multipartFile, String type) throws IOException {
        String fileExtension = multipartFile.getOriginalFilename();
        if (!validateFileExtension(fileExtension)) {
            throw new IllegalArgumentException("Invalid File Extension.");
        }
        if (type.equals("thumbnail")) {
            return uploadThumbnail(new PutObjectRequest(bucketName, "thumbnail/" + generateFileName(type, fileExtension), convertMultiPartToFile(multipartFile)));
        } else if (type.equals("thread")) {
            return uploadThreadFile(new PutObjectRequest(bucketName, "thread/" + generateFileName(type, fileExtension), convertMultiPartToFile(multipartFile)));
        } else {
            return new S3ImageResDto();
        }
    }

    private S3ImageResDto uploadThumbnail(PutObjectRequest putObjectRequest) {
        try {
            this.s3Client.putObject(putObjectRequest);
            log.info("upload complete : {}", putObjectRequest.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        S3ImageResDto res = new S3ImageResDto();
        s3Client
                .setObjectAcl(
                        bucketName,
                        putObjectRequest.getKey(),
                        CannedAccessControlList.PublicRead
                );
        res.setUrl(s3Client.getUrl(bucketName, putObjectRequest.getKey()).toExternalForm());
//        res.setUrl(getPresignedUrl(putObjectRequest.getKey()));

        return res;
    }

    private S3ImageResDto uploadThreadFile(PutObjectRequest putObjectRequest) {
        try {
            this.s3Client.putObject(putObjectRequest);
            log.info("upload complete : {}", putObjectRequest.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        S3ImageResDto res = new S3ImageResDto();
        s3Client
                .setObjectAcl(
                        bucketName,
                        putObjectRequest.getKey(),
                        CannedAccessControlList.PublicRead
                );
        res.setUrl(s3Client.getUrl(bucketName, putObjectRequest.getKey()).toExternalForm());
//        res.setUrl(getPresignedUrl(putObjectRequest.getKey()));
        return res;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getName());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(String type, String extension) {
        return LocalDateTime.now() + "_" + type + "." + extension;
    }

    private boolean validateFileExtension(String extension) {
        return extensionList.contains(extension);
    }

//    private String getPresignedUrl(String objectKey) {
//
//        Date expiration = new Date();
//        long expTimeMillis = expiration.getTime();
//        expTimeMillis += 1000 * 60 * 60; // 1시간
//        expiration.setTime(expTimeMillis);
////        LocalDateTime expiration = LocalDateTime.now(Clock.tick(Clock.systemDefaultZone(), Duration.ofHours(1)));
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
//                .withMethod(HttpMethod.PUT)
//                .withExpiration(expiration);
//
//        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL,
//                CannedAccessControlList.PublicRead.toString());
//
//        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
//
//        return url.toExternalForm();
//    }

//    // Create a bucket by using a S3Waiter object
////    public static void createBucket(S3Client s3Client, String bucketName, Region region) {
//    public void createBucket(S3Client s3Client, String bucketName, Region region) {
//        try {
//            S3Waiter s3Waiter = s3Client.waiter();
//            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
//                    .bucket(bucketName)
//                    .createBucketConfiguration(
//                            CreateBucketConfiguration.builder()
//                                    .locationConstraint(region.id())
//                                    .build())
//                    .build();
//
//            s3Client.createBucket(bucketRequest);
//            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
//                    .bucket(bucketName)
//                    .build();
//
//            // Wait until the bucket is created and print out the response
//            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
//            waiterResponse.matched().response().ifPresent(System.out::println);
//            log.info(bucketName + " is ready");
//
//        } catch (S3Exception e) {
//            log.error(e.awsErrorDetails().errorMessage());
//            System.exit(1);
//        }
//    }
}