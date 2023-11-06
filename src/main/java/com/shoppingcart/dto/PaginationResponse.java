package com.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponse<T>{
    private List<T> content;
    private int pageNo;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean isEmpty;
}
