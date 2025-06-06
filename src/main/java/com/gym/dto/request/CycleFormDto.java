package com.gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleFormDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;


    @Min(1)
    private int durationInDays;

    private boolean published;

    @Min(0)
    private BigDecimal price;

    public CycleFormDto(String name, String description, int durationInDays, boolean published, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.durationInDays = durationInDays;
        this.published = published;
        this.price = price;
    }
}
