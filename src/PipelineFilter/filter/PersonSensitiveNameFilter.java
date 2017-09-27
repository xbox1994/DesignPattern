package PipelineFilter.filter;

import PipelineFilter.PersonFilterContext;
import PipelineFilter.entity.Person;

public class PersonSensitiveNameFilter implements PersonFilter {

    @Override
    public void filter(PersonFilterContext filterContext) {
        Person person = filterContext.getPerson();
        person.setName(person.getName().replaceAll(filterContext.getRule().getSensitiveString(), "*"));
    }

    @Override
    public int getPriority() {
        return PersonFilter.Priority.SENSITIVE_NAME;
    }

    @Override
    public String getDescription() {
        return "this is the name filter that filter sensitive name";
    }
}
