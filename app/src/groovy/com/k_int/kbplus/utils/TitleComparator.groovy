package com.k_int.kbplus.utils;

public class TitleComparator implements java.util.Comparator {

    int compare(Object o1, Object o2) {
      return o1.title.id.compareTo(o2.title.id);
    }

    boolean equals(Object o1, Object o2) {
      return o1.title.id.equals(o2.title.id);
    }
}

