package PipelineFilter.filter;

import PipelineFilter.PersonFilterContext;
import PipelineFilter.entity.Person;

public class PersonAgeRangeFilter implements PersonFilter {
    @Override
    public void filter(PersonFilterContext filterContext) {
        Person person = filterContext.getPerson();
        int age = person.getAge();
        if (age < 0 || age > filterContext.getRule().getPersonAgeLimit()) {
            throw new IllegalArgumentException("your age is too big");
        }
    }

    @Override
    public int getPriority() {
        return PersonFilter.Priority.AGE_RANGE;
    }

    @Override
    public String getDescription() {
        return "this is the filter that limit between age range";
    }
}
