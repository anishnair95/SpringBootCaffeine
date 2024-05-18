package com.javatechie.crud.example.mapper;

import com.javatechie.crud.example.dto.ProductDto;
import com.javatechie.crud.example.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "qty", source = "quantity")
    ProductDto fromEntity(Product product);

    @Mapping(target = "quantity", source = "qty")
    Product fromDto(ProductDto productDto);
}
