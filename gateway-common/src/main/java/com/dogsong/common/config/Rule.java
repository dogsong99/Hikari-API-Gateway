package com.dogsong.common.config;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 规则对象
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/5/31
 */
@Data
public class Rule implements Comparable<Rule>, Serializable {

    private String id;

    private String name;

    private String protocol;

    private Integer order;

    private Set<FilterConfig> filterConfigs = new HashSet<>();

    public Rule() {
        super();
    }

    public Rule(String id, String name, String protocol, Integer order, Set<FilterConfig> filterConfigs) {
        super();
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.order = order;
        this.filterConfigs = filterConfigs;
    }

    /**
     * 向规则里面提供一些新增配置的方法
     *
     * @param filterConfig 规则配置
     */
    public boolean addFilterConfig(FilterConfig filterConfig) {
        return filterConfigs.add(filterConfig);
    }

    /**
     * 通过指定的ID获取指定的配置信息
     *
     * @param id id
     */
    public FilterConfig getFilterConfig(String id) {
        Optional<FilterConfig> configOptional = filterConfigs.stream()
                .filter(filterConfig -> filterConfig.getId().equalsIgnoreCase(id))
                .findAny();
        return configOptional.orElse(null);
    }

    @Override
    public int compareTo(Rule rule) {
        int orderCompare = Integer.compare(getOrder(), rule.getOrder());
        if (orderCompare == 0) {
            return getId().compareTo(rule.getId());
        }
        return orderCompare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rule that = (Rule) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }



    /**
     * 过滤器配置
     * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
     * @since 2023/6/12.
     */
    @Data
    public static class FilterConfig implements Serializable {

        private String id;

        private String config;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FilterConfig that = (FilterConfig) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

    }
}
