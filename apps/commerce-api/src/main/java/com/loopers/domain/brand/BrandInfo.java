package com.loopers.domain.brand;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class BrandInfo {

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

        public static Brand of(String name, String description) {
            return Brand.builder().name(name).description(description).build();
        }
    }
}
