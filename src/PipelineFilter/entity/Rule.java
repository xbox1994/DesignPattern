package PipelineFilter.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Rule {
    private String sensitiveString;
    private int personAgeLimit;
}
