package com.terry.interview.emc.testone;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Xianghe on 2017/4/13.
 *
 * Description of the program
 *
 * Write a program to reverse every k nodes of a linked list.
 * If the list size is not a multiple of k, then leave the remainder nodes as is.

 * Example:
 * Inputs:  1->2->3->4->5->6->7->8->NULL and k = 3
 * Output:  3->2->1->6->5->4->7->8->NULL
 */




public class LinkedListRefactor {
    private final static Logger log = LoggerFactory.getLogger(LinkedListRefactor.class);

    /**
     * Refactor the provided linklist with the predefined logical
     * @param linkedList
     *  The source link list
     * @param k
     *  The int value
     * @param <T>
     *   The class type of the elements
     * @return the linked list after refactor
     */
    public <T> LinkedList<T> doRefactor(LinkedList<T> linkedList, int k) {
        checkNotNull(linkedList, "NULL LIST is provided!");
        checkArgument(linkedList.size() > 0, "Provided LIST does not have any element!");
        checkArgument(k > 0, "Value of k should be a positive value");

        log.info("We received a linked list as {}>", linkedList.toString());

        LinkedList<T> targetLinkedList = Lists.newLinkedList();

        int listSize = linkedList.size();
        int remainderVal = listSize % k;
        if (listSize >= k &&  0 == remainderVal) {
            // The size of the list is multiple of the k
            int reverseTime = listSize / k;

            List<T> tempArrayList = Lists.newArrayList(linkedList);

            for (int i = 0; i < reverseTime; i++) {
                // The elements need to change position
                int firstElementIndex = i * k;
                int lastElementIndex = i * k + k - 1;
                T firstElement = tempArrayList.get(firstElementIndex);
                T lastElement = tempArrayList.get(lastElementIndex);

                if (null != lastElement ) {
                    // We only reverse the nodes when the last element is not NULL
                    tempArrayList.set(firstElementIndex, lastElement);
                    tempArrayList.set(lastElementIndex, firstElement);
                }
            }

            targetLinkedList = Lists.newLinkedList(tempArrayList);
        } else {
            // The size of the list is not multiple of the k, we leave the remainder nodes as is
            targetLinkedList = Lists.newLinkedList(linkedList.subList(0, remainderVal));

        }

        log.info("The refactor linked list is {}>", targetLinkedList);
        return targetLinkedList;
    }
}