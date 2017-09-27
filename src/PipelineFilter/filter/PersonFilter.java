package PipelineFilter.filter;

import PipelineFilter.PersonFilterContext;

public interface PersonFilter {
    void filter(PersonFilterContext processorContext);

    int getPriority();

    String getDescription();

    final class Priority {
        public static final int IGNORE = -1;
        public static final int BASIC = 0;
        public static final int SENSITIVE_NAME = 1;
        public static final int AGE_RANGE = 2;
    }
}
