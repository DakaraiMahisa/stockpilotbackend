package com.stockpilot.backend.catalog.tree;

import com.stockpilot.backend.catalog.dto.response.CategoryTreeDto;
import com.stockpilot.backend.catalog.entity.Category;
import com.stockpilot.backend.catalog.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CategoryTreeBuilder {

    private final CategoryMapper categoryMapper;

    public List<CategoryTreeDto> build(List<Category> categories) {

        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, List<Category>> childrenByParent = new HashMap<>();
        List<Category> rootCategories = new ArrayList<>();

        for (Category category : categories) {

            if (category.getParent() == null) {
                rootCategories.add(category);
                continue;
            }

            childrenByParent
                    .computeIfAbsent(
                            category.getParent().getId(),
                            ignored -> new ArrayList<>()
                    )
                    .add(category);
        }

        return rootCategories.stream()
                .map(root -> buildNode(root, childrenByParent, 0))
                .toList();
    }

    private CategoryTreeDto buildNode(
            Category category,
            Map<UUID, List<Category>> childrenByParent,
            int level
    ) {

        List<CategoryTreeDto> children =
                childrenByParent
                        .getOrDefault(category.getId(), Collections.emptyList())
                        .stream()
                        .map(child -> buildNode(child, childrenByParent, level + 1))
                        .toList();

        return CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .level(level)
                .leaf(children.isEmpty())
                .sortOrder(category.getSortOrder())
                .children(children)
                .build();
    }
}