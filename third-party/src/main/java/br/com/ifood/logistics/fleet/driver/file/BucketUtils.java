package br.com.company.logistics.project.driver.file;

import org.springframework.data.util.Pair;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class BucketUtils {

    private static final int SINGLE_SLASH = '/';

    public static Pair<String, String> getBucketAndFilePath(final String fullFilePath) {
        final int delimiterIndex = fullFilePath.indexOf(SINGLE_SLASH);
        final String bucketName = fullFilePath.substring(0, delimiterIndex);
        final String filePath = fullFilePath.substring(delimiterIndex + 1);
        return Pair.of(bucketName, filePath);
    }

    public static String getFileName(final String fullFilePath) {
        return fullFilePath.substring(fullFilePath.lastIndexOf(SINGLE_SLASH) + 1);
    }

}
