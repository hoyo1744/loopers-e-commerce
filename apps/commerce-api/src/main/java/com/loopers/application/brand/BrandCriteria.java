package com.loopers.application.brand;

import lombok.EqualsAndHashCode;
import lombok.Getter;

public class BrandCriteria {

    @EqualsAndHashCode
    @Getter
    public static class Search {
        private Long brandId;

        private Search(Long brandId) {
            this.brandId = brandId;
        }

        public static Search of(Long brandId) {
            return new Search(brandId);
        }
    }
}
