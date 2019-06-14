package org.hanframework.web.view;

import lombok.Getter;

import java.util.Map;

/**
 * @author liuxin
 * @version Id: ViewModel.java, v 0.1 2019-06-10 22:46
 */
public class ViewModel {
    @Getter
    private final String viewName;

    @Getter
    private final Map<String, Object> model;

    public ViewModel(String viewName) {
        this(viewName, null);
    }

    public ViewModel(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
