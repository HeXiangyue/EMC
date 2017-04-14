package com.terry.interview.emc.test3option2;

import com.google.common.base.StandardSystemProperty;
import com.terry.interview.emc.TestBase;
import com.terry.interview.emc.annotation.ResFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class FileTestBase extends TestBase {
    private final Logger log = LoggerFactory.getLogger(FileTestBase.class);

    @ResFile("data/test3option2/config.yaml")
    private File configYamlFile;

    private FileAccessController fileAccessController = new FileAccessController();

    @BeforeClass(alwaysRun = true)
    public void parseConfigFile() throws InterruptedException {
        Yaml yaml = new Yaml();

        try {
            Map result = (Map) yaml.load(new FileInputStream(configYamlFile));
            int defaultReadWriteMaxCount = (Integer) result.get("defaultReadWriteMaxCount");

            List<Map> allFiles = (List<Map>) result.get("files");

            fileAccessController.initReadWriteBlockingQueue(allFiles.size());

            for (Map fileDetail : allFiles) {
                String filename = (String) fileDetail.get("name");
                File file = new File(configYamlFile.getParentFile().getAbsolutePath() + StandardSystemProperty.FILE_SEPARATOR.value() + filename);
                int actualReadWriteMaxCount = defaultReadWriteMaxCount;

                if (fileDetail.containsKey("readWriteCount")) {
                    actualReadWriteMaxCount = (Integer) fileDetail.get("readWriteCount");
                }

                FileAccess fileAccess = new FileAccess(file, actualReadWriteMaxCount, actualReadWriteMaxCount);
                fileAccessController.addFile(fileAccess);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public FileAccessController getFileAccessController() {
        return fileAccessController;
    }
}
