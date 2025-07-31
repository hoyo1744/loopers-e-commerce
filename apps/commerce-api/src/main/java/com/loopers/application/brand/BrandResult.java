package com.loopers.application.brand;

import com.loopers.domain.brand.BrandInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class BrandResult {

    @Getter
    @EqualsAndHashCode
    @Builder
    public static class Brand {
        private String name;
        private String description;

        private Brand(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public static Brand from(BrandInfo.Brand brand) {
            return Brand.builder().name(brand.getName()).description(brand.getDescription()).build();
        }
    }
}
