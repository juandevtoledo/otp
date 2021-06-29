package com.lulobank.otp.services.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;

public class ListPaginator<T> {
  private List<T> items;

  public ListPaginator(List items) {
    this.items = items;
  }

  public List<T> getListPaginated(Integer pageNumber, Integer pageSize) {
    List<T> paginatedList = null;
    if (pageSize == 0 && pageNumber == 0) {
      paginatedList = this.items;
    } else if (pageSize > 0 && pageNumber > 0) {
      Map<Integer, List<T>> pagedMap = IntStream.iterate(0, i -> i + pageSize)
                                         .limit((items.size() + pageSize - 1) / pageSize).boxed().collect(
          toMap(i -> i / pageSize, i -> items.subList(i, min(i + pageSize, items.size()))));
      paginatedList = pagedMap.get(pageNumber - 1);
    }
    return Objects.isNull(paginatedList) ? new ArrayList<>() : paginatedList;
  }
}
