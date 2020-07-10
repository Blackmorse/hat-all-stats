package com.blackmorse.hattrick.promotions;

import com.blackmorse.hattrick.promotions.model.IPromotion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generics for Unit Tests
 */
public class PromotionListsMerger<Team, Promote extends IPromotion> {
    private final BiFunction<List<Team>, List<Team>, Promote> mapFunction;

    public PromotionListsMerger(BiFunction<List<Team>, List<Team>, Promote> mapFunction) {
        this.mapFunction = mapFunction;
    }

    public List<Promote> mergeTeams(List<Team> leftList, List<Team> rightList) {
        List<Promote> result = new ArrayList<>();

        int i = 0;
        while (i < leftList.size()) {
            int leftMinInd = i;
            int leftMaxInd = i;
            while(leftMaxInd < leftList.size() && leftList.get(leftMinInd).equals(leftList.get(leftMaxInd))) {
                leftMaxInd++;
            }
            leftMaxInd--;

            int rightMinInd = i;
            while(rightMinInd >=0 && rightList.get(rightMinInd).equals(rightList.get(i))) {
                rightMinInd--;
            }
            rightMinInd++;

            int rightMaxInd = leftMaxInd;
            while(rightMaxInd < rightList.size() && rightList.get(rightMaxInd).equals(rightList.get(leftMaxInd))) {
                rightMaxInd++;
            }
            rightMaxInd--;

            List<Team> leftArray = IntStream.range(leftMinInd, leftMaxInd + 1)
                    .mapToObj(leftList::get)
                    .collect(Collectors.toList());

            List<Team> rightArray = IntStream.range(rightMinInd, rightMaxInd + 1)
                    .mapToObj(rightList::get)
                    .collect(Collectors.toList());


            result.add(mapFunction.apply(leftArray, rightArray));

            i = leftMaxInd + 1;
        }

        Iterator<Promote> iterator = result.iterator();

        Promote previous = iterator.next();
        Promote next ;//= //terator.next();
        do {
            next = iterator.next();
            if (previous.getUpTeams().equals(next.getUpTeams())) {
                iterator.remove();
                previous.getDownTeams().addAll(next.getDownTeams());
            } else {
                previous = next;
            }
//            next = iterator.next();
        } while (iterator.hasNext());

//        if (previous.getUpTeams().equals(next.getUpTeams())) {
//            iterator.remove();
//            previous.getDownTeams().addAll(next.getDownTeams());
//        }

            return result;
    }
}
