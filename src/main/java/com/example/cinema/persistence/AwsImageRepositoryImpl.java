package com.example.cinema.persistence;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.cinema.exceptions.LoadImageException;
import com.example.cinema.exceptions.SaveImageException;
import com.example.cinema.exceptions.UnsupportedImageTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Repository
public class AwsImageRepositoryImpl implements ImageRepository {
    @Value("${images.aws.s3.access-key-id}")
    private String accessKey;

    @Value("${images.aws.s3.secret-access-key}")
    private String secretKey;

    @Value("${images.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${images.aws.s3.region}")
    private String region;

    @Value("${images.aws.s3.poster-folder}")
    public String postersFolder;

    @Value("${images.aws.s3.preview-folder}")
    public String previewFolder;

    @Value("${images.allowed-extensions-regex}")
    private static String IMAGE_EXTENSION_REGEX = "(jp.?g|png|bmp|webp)";

    private AmazonS3 getS3client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    @Override
    public String savePosterImage(MultipartFile file){
        return saveImage(postersFolder, file);
    }

    @Override
    public String savePreviewImage(MultipartFile file){
        return saveImage(previewFolder, file);
    }

    public String saveImage(String folder, MultipartFile file){
        if(file == null){
            return null;
        }

        String extension = getFileExtension(file);
        if (extension == null || !extension.matches(IMAGE_EXTENSION_REGEX)) {
            throw new UnsupportedImageTypeException(extension, IMAGE_EXTENSION_REGEX);
        }

        String imageName = UUID.randomUUID() + "." + extension;
        InputStream imageIS;
        try {
            imageIS = file.getInputStream();
        } catch (IOException e) {
            throw new SaveImageException(file.getName());
        }

        AmazonS3 s3client = getS3client();
        s3client.putObject(bucketName, folder + "/" + imageName, imageIS, new ObjectMetadata());

        return imageName;
    }

    @Override
    public byte[] loadPosterImage(String posterName){
        return loadImage(postersFolder + "/" + posterName);
    }

    @Override
    public byte[] loadPreviewImage(String previewName){
        return loadImage(previewFolder + "/" + previewName);
    }

    public byte[] loadImage(String imageName){
        AmazonS3 s3client = getS3client();
        S3Object s3object = s3client.getObject(bucketName, imageName);

        S3ObjectInputStream inputStream = s3object.getObjectContent();

        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        } catch (IOException e){
            throw new LoadImageException(imageName);
        }

        return bytes;
    }

    private String getFileExtension(MultipartFile filePart) {
        String name = filePart.getOriginalFilename();

        if (name == null) {
            return null;
        }

        String[] nameParts = name.split("\\.");
        return nameParts[nameParts.length - 1];
    }
}
