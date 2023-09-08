package com.logicalis.apisolver.model.utilities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SortedUnpaged implements Pageable {

    private final Sort sort;

    private SortedUnpaged(Sort sort) {
        this.sort = sort;
    }

    public static SortedUnpaged getInstance(Sort sort) {
        return new SortedUnpaged(sort);
    }

    public boolean isPaged() {
        return false;
    }

    public Pageable previousOrFirst() {
        return this;
    }

    public Pageable next() {
        return this;
    }

    public boolean hasPrevious() {
        return false;
    }

    public Sort getSort() {
        return sort;
    }

    public int getPageSize() {
        return 0;
    }

    public int getPageNumber() {
        return 0;
    }

    public long getOffset() {
        return 0;
    }

    public Pageable first() {
        return this;
    }
}