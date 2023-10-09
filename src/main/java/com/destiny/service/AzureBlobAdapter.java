package com.destiny.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

@Service
public class AzureBlobAdapter {

  private final String endpoint;
  private final String accountName;
  private final String accountKey;

  @Autowired
  public AzureBlobAdapter(
          @Value("${azure.storage.blob-endpoint}") String endpoint,
          @Value("${azure.storage.account-name}") String accountName,
          @Value("${azure.storage.account-key}") String accountKey) {

    this.endpoint = endpoint;
    this.accountName = accountName;
    this.accountKey = accountKey;
  }

  private BlobContainerClient getContainer(String containerName) {
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .endpoint(endpoint)
        .credential(new StorageSharedKeyCredential(accountName, accountKey))
        .buildClient();

    return blobServiceClient.getBlobContainerClient(containerName);
  }

  public String upload(MultipartFile file, String containerName) throws IOException {

    String originalFilename = file.getOriginalFilename();
    String fileExtension = originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
    String imgFileName = UUID.randomUUID().toString() + fileExtension;
    BlobContainerClient container = getContainer(containerName);
    BlobClient blobClient = container.getBlobClient(imgFileName);
    BlobHttpHeaders headers = new BlobHttpHeaders();
    headers.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      blobClient.uploadWithResponse(new BlobParallelUploadOptions(inputStream).setHeaders(headers), null, null);
      return blobClient.getBlobUrl();
    }
  }

  public void delete(String blobName, String containerName) {
    BlobContainerClient container = getContainer(containerName);
    BlobClient blobClient = container.getBlobClient(blobName);
    blobClient.delete();
  }

}
