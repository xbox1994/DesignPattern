package PipelineFilter;

import PipelineFilter.entity.Person;
import PipelineFilter.entity.Rule;
import PipelineFilter.filter.PersonAgeRangeFilter;
import PipelineFilter.filter.PersonSensitiveNameFilter;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //准备数据
        Person person = Person.builder().name("fuck xx").age(1000).build();
        Rule rule = Rule.builder().personAgeLimit(100).sensitiveString("fuck").build();
        System.out.println(person);
        PersonFilterContext filterContext = PersonFilterContext.builder().person(person).rule(rule).build();

        //动态配置Filter，这部分可以放在Spring的容器管理中用@Service @Component替换
        PersonFilterManager filterManager = new PersonFilterManager(Arrays.asList(
                new PersonAgeRangeFilter(), new PersonSensitiveNameFilter()//, 随意扩展
        ));
        filterManager.setListArranged();

        //调用执行
        filterManager.startToFilter(filterContext);
        System.out.println(filterContext.getPerson());
    }
}
