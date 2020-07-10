package com.blackmorse.hattrick.promotions;

import com.blackmorse.hattrick.promotions.model.IPromotion;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PromotionListsMergerTest {
    private PromotionListsMerger<Integer, Promote> merger = new PromotionListsMerger<>(Promote::new);

    private static class Promote implements IPromotion<Integer> {
        public final List<Integer> left;
        public final List<Integer> right;

        private Promote(List<Integer> left, List<Integer> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public List<Integer> getUpTeams() {
            return right;
        }

        @Override
        public List<Integer> getDownTeams() {
            return left;
        }
    }

    @Test
    public void test() {
        List<Integer> leftList = Arrays.asList(100, 100, 100, 100, 99, 99, 98);
        List<Integer> rightList = Arrays.asList(40, 39, 39, 38, 38, 37, 37);

        List<Promote> promotes = merger.mergeTeams(leftList, rightList);

        assertEquals(3, promotes.size());

        assertEquals(Arrays.asList(100, 100, 100, 100), promotes.get(0).getDownTeams());
        assertEquals(Arrays.asList(40, 39, 39, 38, 38), promotes.get(0).getUpTeams());

        assertEquals(Arrays.asList(99, 99), promotes.get(1).getDownTeams());
        assertEquals(Arrays.asList(38, 38, 37, 37), promotes.get(1).getUpTeams());

        assertEquals(Arrays.asList(98), promotes.get(2).getDownTeams());
        assertEquals(Arrays.asList(37, 37), promotes.get(2).getUpTeams());
    }

    @Test
    public void test2() {
        List<Integer> leftList = Arrays.asList(3, 3, 2, 1, 0);
        List<Integer> rightList = Arrays.asList(5, 5, 5, 4, 4);

        List<Promote> promotes = merger.mergeTeams(leftList, rightList);

        assertEquals(2, promotes.size());

        assertEquals(Arrays.asList(3, 3, 2), promotes.get(0).getDownTeams());
        assertEquals(Arrays.asList(5, 5, 5), promotes.get(0).getUpTeams());

        assertEquals(Arrays.asList(1, 0), promotes.get(1).getDownTeams());
        assertEquals(Arrays.asList(4, 4), promotes.get(1).getUpTeams());
    }

    @Test
    public void test3() {
        List<Integer> leftList = Arrays.asList(3, 3, 2, 2, 1);
        List<Integer> rightList = Arrays.asList(5, 5, 5, 4, 4);

        List<Promote> promotes = merger.mergeTeams(leftList, rightList);

        assertEquals(3, promotes.size());

        assertEquals(Arrays.asList(3, 3), promotes.get(0).getDownTeams());
        assertEquals(Arrays.asList(5, 5, 5), promotes.get(0).getUpTeams());

        assertEquals(Arrays.asList(2, 2), promotes.get(1).getDownTeams());
        assertEquals(Arrays.asList(5, 5, 5, 4, 4), promotes.get(1).getUpTeams());

        assertEquals(Arrays.asList(1), promotes.get(2).getDownTeams());
        assertEquals(Arrays.asList(4, 4), promotes.get(2).getUpTeams());
    }

    @Test
    public void test4() {
        List<Integer> leftList = Arrays.asList(4, 3);
        List<Integer> rightList = Arrays.asList(2, 1);

        List<Promote> promotes = merger.mergeTeams(leftList, rightList);


        assertEquals(2, promotes.size());

        assertEquals(Arrays.asList(4), promotes.get(0).getDownTeams());
        assertEquals(Arrays.asList(2), promotes.get(0).getUpTeams());

        assertEquals(Arrays.asList(3), promotes.get(1).getDownTeams());
        assertEquals(Arrays.asList(1), promotes.get(1).getUpTeams());
    }

    @Test
    public void test5() {
        List<Integer> leftList = Arrays.asList(3, 2, 1);
        List<Integer> rightList = Arrays.asList(4, 4, 4);

        List<Promote> promotes = merger.mergeTeams(leftList, rightList);


        assertEquals(1, promotes.size());

        assertEquals(Arrays.asList(3, 2, 1), promotes.get(0).getDownTeams());
        assertEquals(Arrays.asList(4, 4, 4), promotes.get(0).getUpTeams());
    }
}