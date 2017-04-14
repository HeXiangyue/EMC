package com.terry.interview.emc.test3option2;

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Lists;
import com.terry.interview.emc.Common;
import com.terry.interview.emc.TestBase;
import com.terry.interview.emc.annotation.ResFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class Test3Option2Test extends FileTestBase {
    private static Logger log = LoggerFactory.getLogger(Test3Option2Test.class);

    private final int WRITE_THREAD_COUNT = 10;
    private final int READ_THREAD_COUNT = 2;

    private ExecutorService writeExecutorService = Executors.newFixedThreadPool(WRITE_THREAD_COUNT);
    private ExecutorService readExecutorService = Executors.newFixedThreadPool(READ_THREAD_COUNT);


    /**
     * A runnable class that used to write dynamic content into the file.
     */
    private class writeFileRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {

                FileAccess fileAccess = null;

                try {
                    fileAccess = getFileAccessController().getWriteBlockingQueue().take();
                } catch (InterruptedException e) {
                   throw new IllegalStateException(String.format("Fail to get the file to write, which would impact the testing result! We fail it now!"), e);
                }

                // Generate a dynamic content that need to be written into the file
                String randomVal = UUID.randomUUID().toString();

                fileAccess.writeStringToFile(fileAccess.getFile(), randomVal);

                try {
                    // After file written, put it into the read blocking queue which will be processed by the read thread to verify the result
                    getFileAccessController().addFileToRead(fileAccess);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(String.format("Failed to put the file to the read queue, which would impact the testing result! We fail it now!"), e);
                }
            }
        }
    }


    /**
     * A runnable class that used to read the file from the read blocking queue
     */
    private class readFileRunnable implements Runnable {
        @Override
        public void run() {
            FileAccess fileAccess = null;

            while (true) {
                try {
                    fileAccess = getFileAccessController().getReadBlockingQueue().take();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("We failed to read the file from the read blocking queue, which will impact the testing result, we fail it now!", e);
                }

                String content = fileAccess.readFile(fileAccess.getFile());
                log.info("Following is the verification result <{} write, {} read> for file: \n" +
                        "File Name: {}\n" +
                        "Expected Content: {}\n" +
                        "Actual Content: {}\n" +
                        "Test Result: {}", fileAccess.getCurrentWriteCount(), fileAccess.getCurrentReadCount(),
                        fileAccess.getFile().getAbsolutePath(), fileAccess.getLastWrite(), content, fileAccess.getLastWrite().equals(content) ? "Pass" : "Fail");



                if (fileAccess.getCurrentWriteCount() < fileAccess.getMaxWrite()) {
                    // If the file written count is smaller than the max write count, we put it into the write queue to write once more
                    log.debug("CurrentWriteCount <{}> is smaller than MaxWriteCount <{}>, we put the file to the write queue again: {}",
                            fileAccess.getCurrentWriteCount(), fileAccess.getMaxWrite(), fileAccess.getFile().getAbsolutePath());

                    try {
                        getFileAccessController().addFileToWrite(fileAccess);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("We fail to put the file intot he write queue, which will impact the testing result, we fail it now!", e);
                    }
                }
            }
        }
    }

    @Test
    public  void FileReadWriteOperationTest() throws ExecutionException, InterruptedException {
        log.info("Start the FileReadWriteOperationTest...");

        // Start the thread to write the content to the file
        for (int i = 0; i < WRITE_THREAD_COUNT; i++) {
            Future writeFuture = writeExecutorService.submit(new writeFileRunnable());
        }

        // Start the thread to read the content from file and verify the result
        for (int i = 0; i < READ_THREAD_COUNT; i++) {
            Future readFuture = readExecutorService.submit(new readFileRunnable());
        }

        // Put all the files to the write blocking queue which is monitored by the write thread
        Set<FileAccess> allFilesNeedToWrite = getFileAccessController().getAllFilesNeedToWrite();
        getFileAccessController().addFilesToWrite(allFilesNeedToWrite);

        while (true) {
            // Wait all the files are written and read for the configured times
            allFilesNeedToWrite = getFileAccessController().getAllFilesNeedToWrite();
            if (allFilesNeedToWrite.isEmpty()) {
                 /*
                  * Just end the application when all files are written and read for the configured times.
                  * Don't need to explicitly shutdown the executor poll in this case because we ended the application and the JVM will do it.
                  */
                break;
            }
        }

    }
}
