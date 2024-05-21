package com.dacm.hexagonal.domain.model.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmailPayload {

    private String to;
    private String subject;
    private Map<String, Object> model;
    private String templateName;

}
