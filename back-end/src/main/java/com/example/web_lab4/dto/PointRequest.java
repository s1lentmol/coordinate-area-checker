package com.example.web_lab4.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {
    private double x;

    private double y;

    @Min(0)
    private double radius;
}

