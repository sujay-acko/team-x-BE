package com.example.translation.dtos.request;

import com.example.translation.models.TranslationDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdatetranslationRequest extends TranslationDetails{

}
