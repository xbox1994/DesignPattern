package PipelineFilter;

import PipelineFilter.filter.PersonFilter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PersonFilterManager {
    //@Autowired
    private List<PersonFilter> filters;

    public PersonFilterManager(List<PersonFilter> filters) {
        this.filters = filters;
    }

    //@PostConstruct
    public void setListArranged() {
        this.filters = filters.stream()
                .filter(filter -> filter.getPriority() != PersonFilter.Priority.IGNORE)
                .sorted(Comparator.comparingInt(PersonFilter::getPriority))
                .collect(Collectors.toList());
    }

    public void startToFilter(PersonFilterContext filterContext) {
        filters.forEach(filter -> {
                    System.out.println("current filter is: " + filter.getDescription());
                    filter.filter(filterContext);
                }
        );
    }
}
