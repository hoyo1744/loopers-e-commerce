package com.loopers.application.brand;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class BrandFacade {

    private final BrandService brandService;

    public BrandResult.Brand getBrand(BrandCriteria.Search search) {

        if (search.getBrandId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다.");
        }

        BrandInfo.Brand brand = brandService.getBrand(BrandCommand.Search.of(search.getBrandId()));

        return BrandResult.Brand.from(brand);
    }

}
