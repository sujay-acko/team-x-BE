package com.example.translation.dtos.response;

import com.example.translation.models.DecodeTextResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HtmlTextEncodedResponse {
    public String encodedHtmlResponse;
    public List<DecodeTextResponse> decodeTextResponseList;
}
