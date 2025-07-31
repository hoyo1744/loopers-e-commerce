package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandResult;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class BrandResponse {

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class Brand {
        private String name;
        private String description;

        private Brand(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public static Brand of(String name, String description) {
            return Brand.builder().name(name).description(description).build();
        }

        public static Brand from(BrandResult.Brand brand) {
            return Brand.builder().name(brand.getName()).description(brand.getDescription()).build();
        }

    }
}
