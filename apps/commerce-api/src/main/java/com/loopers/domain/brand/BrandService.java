package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandInfo.Brand getBrand(BrandCommand.Search search) {
        Brand brand = brandRepository.findById(search.getBrandId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "브랜드가 존재하지 않습니다. brandId: " + search.getBrandId()));
        return BrandInfo.Brand.of(brand.getName(), brand.getDescription());
    }
}
