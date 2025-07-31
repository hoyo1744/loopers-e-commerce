package com.loopers.domain.brand;

import lombok.Getter;

public class BrandCommand {

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
