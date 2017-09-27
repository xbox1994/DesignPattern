package PipelineFilter;

import PipelineFilter.entity.Person;
import PipelineFilter.entity.Rule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonFilterContext {
    private Person person;
    private Rule rule;
}
