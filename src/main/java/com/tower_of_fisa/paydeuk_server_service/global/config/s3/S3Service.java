package com.tower_of_fisa.paydeuk_server_service.global.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 이미지 업로드 하기
     */
    public String uploadProfileImage(MultipartFile image,Long userId) throws IOException {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename(); // 고유한 파일 이름 생성
        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();

        // 사용자 정의 메타데이터 추가
        metadata.setContentLength(image.getSize());
        metadata.addUserMetadata("uploaded-by", String.valueOf(userId));
        metadata.addUserMetadata("size", String.valueOf(image.getSize()));
        metadata.addUserMetadata("upload-source", "user-profile");
        metadata.addUserMetadata("uploaded-at", OffsetDateTime.now().toString());
        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);

        // S3에 파일 업로드
        amazonS3Client.putObject(putObjectRequest);
        return getPublicUrl(fileName);
    }
    /**
     * S3에 이미지 업로드 하기전 유저가 가지고 있는 이미지 삭제
     */
    public void deleteImage(String key) {
        amazonS3Client.deleteObject(bucket, key);
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3Client.getRegionName(), fileName);
    }

}