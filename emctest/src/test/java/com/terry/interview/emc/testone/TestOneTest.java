package com.terry.interview.emc.testone;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import java.util.LinkedList;

/**
 * Created by Xianghe on 2017/4/13.
 */
public class TestOneTest {
    private Logger log = LoggerFactory.getLogger(TestOneTest.class);

    private LinkedListRefactor linkedListRefactor;


    @BeforeClass(alwaysRun = true)
    public void initRefactorInstance() {
        linkedListRefactor = new LinkedListRefactor();
    }

    @Test(description = "The list size if multiple of K, and the elments are Integer. We will reverse every k nodes of a linked list")
    public void multipleOfKTest_IntValue() {
        LinkedList<Integer> sourceList = Lists.newLinkedList();
        sourceList.add(1);
        sourceList.add(2);
        sourceList.add(3);
        sourceList.add(4);
        sourceList.add(5);
        sourceList.add(6);
        sourceList.add(7);
        sourceList.add(8);
        sourceList.add(null);

        LinkedList<Integer> expectedTargetList = Lists.newLinkedList();
        expectedTargetList.add(3);
        expectedTargetList.add(2);
        expectedTargetList.add(1);
        expectedTargetList.add(6);
        expectedTargetList.add(5);
        expectedTargetList.add(4);
        expectedTargetList.add(7);
        expectedTargetList.add(8);
        expectedTargetList.add(null);

        LinkedList<Integer> actualTargetList = linkedListRefactor.doRefactor(sourceList, 3);
        Assert.assertTrue(compareLinkedList(actualTargetList, expectedTargetList), "Linked list returned by the refactor is wrong!");
    }

    @Test(description = "The list size if multiple of K, and the elements are String. We will reverse every k nodes of a linked list")
    public void multipleOfKTest_StringValue() {
        LinkedList<String> sourceList = Lists.newLinkedList();
        sourceList.add("a");
        sourceList.add("b");
        sourceList.add("c");
        sourceList.add("d");
        sourceList.add("e");
        sourceList.add(null);

        LinkedList<String> expectedTargetList = Lists.newLinkedList();
        expectedTargetList.add("c");
        expectedTargetList.add("b");
        expectedTargetList.add("a");
        expectedTargetList.add("d");
        expectedTargetList.add("e");
        expectedTargetList.add(null);

        LinkedList<String> actualTargetList = linkedListRefactor.doRefactor(sourceList, 3);
        Assert.assertTrue(compareLinkedList(actualTargetList, expectedTargetList), "Linked list returned by the refactor is wrong!");
    }


    @Test(description = "The list size is not multiple of K, we will return the remainder of the data")
    public void notMultipleOfKTest() {
        LinkedList<String> sourceList = Lists.newLinkedList();
        sourceList.add("a");
        sourceList.add("b");
        sourceList.add("c");
        sourceList.add("d");

        LinkedList<String> expectedTargetList = Lists.newLinkedList();
        expectedTargetList.add("a");

        LinkedList<String> actualTargetList = linkedListRefactor.doRefactor(sourceList, 3);
        Assert.assertTrue(compareLinkedList(actualTargetList, expectedTargetList), "Linked list returned by the refactor is wrong!");
    }

    private <T> boolean compareLinkedList(LinkedList<T> firstList, LinkedList<T> secondList) {
        if (firstList.size() == secondList.size()) {
            for (int i = 0 ; i < firstList.size(); i++) {

                if (firstList.poll() != secondList.poll()) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
