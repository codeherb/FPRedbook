package io.funfun.redbook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void testAppHasAGreeting() {
        FPList<String> expect = FPList.cons("a", FPList.cons("b", FPList.cons("c", FPList.none())));
        FPList<String> actual = FPList.fpList("a", "b", "c");
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expect, actual);
        Assertions.assertEquals(actual.length(), 3);
    }

    @Test
    void testHashCode() {
        FPList ob1 = FPList.cons("a");
        FPList ob2 = new FPList.Cons("a", FPList.none());
        Assertions.assertTrue(ob1.equals(ob1));
        Assertions.assertTrue(ob1.equals(ob2));
        Assertions.assertEquals(ob1.hashCode(), ob2.hashCode());
        FPList<String> list1 = FPList.cons("a", FPList.cons("b", FPList.cons("c", FPList.none())));
        FPList<String> list2 = FPList.fpList("a", "b", "c");
        Assertions.assertEquals(list1, list2);
        Assertions.assertEquals(list1.hashCode(), list2.hashCode());
    }
}
