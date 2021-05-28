package com.example.demo.repository.specification;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueSpecification {

    public static Specification<Issue> getSpecificationFromFilters(List<String> filters) {
        Specification<Issue> specification = Specification.where(createSpecification(filters.get(0), filters.get(1)));
        for (int i = 2; i < filters.size(); i += 2) {
            specification = specification.and(createSpecification(filters.get(i), filters.get(i + 1)));
        }
        return specification;
    }

    private static Specification<Issue> createSpecification(String key, String value) {
        if (key.equals("tag")) {
            return hasTag(value);
        } else if (key.equals("status")) {
            return hasStatus(value);
        }
        return null;
    }

    private static Specification<Issue> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Issue> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(tag, root.get("tag"));
    }

    public static Specification<Issue> belongsToProject(Project project) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project"), project.getProject_id());
    }

}
