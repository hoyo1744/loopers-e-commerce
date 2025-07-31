package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandCriteria;
import com.loopers.application.brand.BrandFacade;
import com.loopers.application.brand.BrandResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandV1Controller implements BrandV1ApiSpec {

    private final BrandFacade brandFacade;

    @GetMapping("/{brandId}")
    public ApiResponse<BrandResponse.Brand> getBrand(@PathVariable Long brandId) {
        BrandResult.Brand brand = brandFacade.getBrand(BrandCriteria.Search.of(brandId));
        return ApiResponse.success(BrandResponse.Brand.from(brand));
    }

}
