package br.com.company.logistics.project.driver;

import java.net.URL;

public interface DriverFileService {

    URL getUrl(String path);

    String getFileName(String path);

    void deleteFile(String path);

}
