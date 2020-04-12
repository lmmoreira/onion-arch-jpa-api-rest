package br.com.company.logistics.project.driver.file;

import com.company.file.FileRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.time.Duration;
import br.com.company.logistics.project.driver.DriverFileService;

@Service
public class DriverFileServiceImpl implements DriverFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFileServiceImpl.class);
    private static final long MINUTES_TTL_URL = 5L;

    private final FileRepositoryService fileRepositoryService;
    private final long driverFileUrlTtlMillis;

    public DriverFileServiceImpl(final FileRepositoryService fileRepositoryService,
            @Value("${driver.file.url.tll-minutes}") final long driverFileUrlTtlMinutes) {
        this.fileRepositoryService = fileRepositoryService;
        this.driverFileUrlTtlMillis = Duration.ofMinutes(driverFileUrlTtlMinutes).toMillis();
    }

    @Override
    public URL getUrl(final String path) {
        final Pair<String, String> bucketAndFilePath = BucketUtils.getBucketAndFilePath(path);
        return fileRepositoryService
                .getUrl(bucketAndFilePath.getFirst(), bucketAndFilePath.getSecond(), driverFileUrlTtlMillis, true);
    }

    @Override
    public String getFileName(final String path) {
        return BucketUtils.getFileName(path);
    }

    @Override
    public void deleteFile(final String path) {
        final Pair<String, String> bucketAndFilePath = BucketUtils.getBucketAndFilePath(path);
        LOGGER.info("Path to delete {}", bucketAndFilePath.getSecond());
        fileRepositoryService.delete(bucketAndFilePath.getSecond());
    }

}
