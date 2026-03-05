package com.datn.moneyai.models.dtos.categories;

import com.datn.moneyai.models.entities.enums.CategoryType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class CategoryCreateRequest {

    private Long id;

    private String name;

    private CategoryType type;

    private String icon;

    private String colorCode;
}