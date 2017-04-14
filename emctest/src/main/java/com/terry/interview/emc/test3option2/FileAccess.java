package com.terry.interview.emc.test3option2;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class FileAccess {
    private final static Logger log = LoggerFactory.getLogger(FileAccess.class);

    private File file;
    private int maxWrite;
    private int maxRead;
    private int currentWriteCount;
    private int currentReadCount;
    /*
     * Store the last written content
     */
    private String lastWrite;

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public FileAccess(File file, int maxRead, int maxWrite) {
        this.file = file;
        this.maxRead = maxRead;
        this.maxWrite = maxWrite;
    }

    public void addWriteCount() {
        this.currentWriteCount += 1;
    }

    public void addReadCount() {
        this.currentReadCount += 1;
    }


    /**
     * Write data in a String to a file. It creates the file if it does not
     * exist.
     *
     * @param fileToBeWrittenTo Path to the file to write data into.
     * @param contents          Contents to be written to the file
     */

    public void writeStringToFile(File fileToBeWrittenTo, String contents) {
        checkNotNull(fileToBeWrittenTo, "The target file need to be provide to write content");

        checkNotNull(contents, "Content should NOT be NULL!");

        rwl.writeLock().lock();
        log.debug("Lock write access for file <{}>", fileToBeWrittenTo.getName());

        if (this.currentWriteCount >= this.maxWrite) {
            // The second place to ensure the file access count won't exceed the configured value
            log.debug("The write access count of the file <{}> is exceed the configured max count, we will not do anything!", this.file.getAbsolutePath());
            rwl.writeLock().unlock();
        } else  {
            log.debug("Writing content <{}> to file <{}>", contents, fileToBeWrittenTo.getAbsolutePath());

            try {
                FileUtils.writeStringToFile(fileToBeWrittenTo, contents);
                this.lastWrite = contents;
            } catch (IOException e) {
                throw new IllegalStateException(
                        format("An exception was thrown when we tried to write contents [<%s>] to file <%s> ",
                                contents, fileToBeWrittenTo.getAbsolutePath()), e);

            } finally {
                this.addWriteCount();
                rwl.writeLock().unlock();
            }
        }
    }

    /**
     * Read the content from the give file
     * @param inputFile The source file
     *
     * @return Content in String format
     */
    public String readFile(File inputFile) {
        checkNotNull(inputFile);
        checkArgument(inputFile.exists());
        checkArgument(inputFile.isFile());
        checkArgument(inputFile.canRead());

        String fileContents = null;

        rwl.readLock().lock();
        log.debug("Lock read access for file <{}>", inputFile.getName());

        if (this.currentReadCount >= this.maxRead) {
            log.debug("The read access count of the file <{}> is exceed the configured max count, we will not do anything!", this.file.getAbsolutePath());
            rwl.readLock().unlock();
            return null;
        }

        try {
            fileContents = FileUtils.readFileToString(inputFile);
        } catch (IOException e) {
            throw new IllegalStateException(format(
                    "Unable to read contents of file <%s> into a string.",
                    inputFile.getAbsolutePath()), e);
        } finally {
            this.addReadCount();
            rwl.readLock().unlock();
        }

        return fileContents;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLastWrite() {
        return lastWrite;
    }

    public void setLastWrite(String lastWrite) {
        this.lastWrite = lastWrite;
    }

    public int getMaxWrite() {
        return maxWrite;
    }

    public void setMaxWrite(int maxWrite) {
        this.maxWrite = maxWrite;
    }

    public int getMaxRead() {
        return maxRead;
    }

    public void setMaxRead(int maxRead) {
        this.maxRead = maxRead;
    }

    public int getCurrentWriteCount() {
        return currentWriteCount;
    }

    public void setCurrentWriteCount(int currentWriteCount) {
        this.currentWriteCount = currentWriteCount;
    }

    public int getCurrentReadCount() {
        return currentReadCount;
    }

    public void setCurrentReadCount(int currentReadCount) {
        this.currentReadCount = currentReadCount;
    }

    @Override
    public String toString() {
        return String.format("filename: %s\n" +
                "MaxWriteCount: %s\n" +
                "MaxReadCount: %s\n" +
                "currentWriteCount: %s\n" +
                "currentReadCount: %s", this.file.getAbsolutePath(), this.maxWrite, this.maxRead, this.currentWriteCount, this.currentReadCount);
    }

}
