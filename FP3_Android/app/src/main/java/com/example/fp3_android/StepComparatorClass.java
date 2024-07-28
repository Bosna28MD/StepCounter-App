package com.example.fp3_android;

import java.util.Comparator;

public class StepComparatorClass implements Comparator<LeaderBordClass> {

    @Override
    public int compare(LeaderBordClass t1, LeaderBordClass t2) {
        int steps1 = Integer.parseInt(t1.steps);
        int steps2 = Integer.parseInt(t2.steps);
        return Integer.compare(steps2, steps1);  // Ascending->  return Integer.compare(steps1, steps2);
                                                 // Descending-> return Integer.compare(steps2, steps1);
    }
}
