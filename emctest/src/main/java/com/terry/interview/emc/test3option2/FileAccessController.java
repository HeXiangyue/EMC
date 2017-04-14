package com.terry.interview.emc.test3option2;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class FileAccessController {
    private final static Logger log = LoggerFactory.getLogger(FileAccessController.class);

    /*
     * Store all the files that configured to be written and read
     */
    private Collection<FileAccess> allFileAccess = Sets.newHashSet();

    /*
     * The Blocking queue to store the files to be written
     */
    private BlockingQueue<FileAccess> writeBlockingQueue;

    /*
     * The Blocking queue to store the files to read
     */
    private BlockingQueue<FileAccess> readBlockingQueue;

    private Lock lock = new ReentrantLock();


    /**
     * Initialize the blocking queue for the written and read file
     * @param size The queue sise
     */
    public void initReadWriteBlockingQueue(int size) {
        writeBlockingQueue = new LinkedBlockingQueue<>(size);
        readBlockingQueue = new LinkedBlockingQueue<>(size);
    }

    /**
     * Add file to the write blocking queue
     *
     * @param fileAccess
     * @throws InterruptedException
     */
    public void addFileToWrite(FileAccess fileAccess) throws InterruptedException {
        writeBlockingQueue.put(fileAccess);
    }

    /**
     * Add a collection of file to the write blocking queue
     *
     * @param fileAccesses A collection of the files
     */
    public void addFilesToWrite(Collection<FileAccess> fileAccesses) {
        writeBlockingQueue.addAll(fileAccesses);
    }

    /**
     * Add file to the read blocking queue
     *
     * @param fileAccess The file to be read
     * @throws InterruptedException
     */
    public void addFileToRead(FileAccess fileAccess) throws InterruptedException {
        readBlockingQueue.put(fileAccess);
    }

    /**
     * Get a set of files that need write operation
     *
     * @return A set of files that need to put to the write blocking queue
     */
    public Set<FileAccess> getAllFilesNeedToWrite() {
        Set<FileAccess> collect = allFileAccess.stream().filter(f -> f.getCurrentWriteCount() < f.getMaxWrite()).collect(Collectors.toSet());
        return collect;
    }

    /**
     * Add a file that to be monitored
     *
     * @param fileAccess
     */
    public void addFile(FileAccess fileAccess) {
        this.allFileAccess.add(fileAccess);
    }

    public BlockingQueue<FileAccess> getWriteBlockingQueue() {
        return writeBlockingQueue;
    }

    public void setWriteBlockingQueue(BlockingQueue<FileAccess> writeBlockingQueue) {
        this.writeBlockingQueue = writeBlockingQueue;
    }

    public BlockingQueue<FileAccess> getReadBlockingQueue() {
        return readBlockingQueue;
    }

    public void setReadBlockingQueue(BlockingQueue<FileAccess> readBlockingQueue) {
        this.readBlockingQueue = readBlockingQueue;
    }
}
