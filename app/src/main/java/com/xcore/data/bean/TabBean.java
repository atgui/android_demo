package com.xcore.data.bean;

import java.io.Serializable;
import java.util.List;

public class TabBean implements Serializable{
    private List<CategoriesBean> species;
    private List<CategoriesBean> categories;
    private List<CategoriesBean> sorttype;

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public List<CategoriesBean> getSorttype() {
        return sorttype;
    }

    public void setSorttype(List<CategoriesBean> sorttype) {
        this.sorttype = sorttype;
    }

    public List<CategoriesBean> getSpecies() {
        return species;
    }

    public void setSpecies(List<CategoriesBean> species) {
        this.species = species;
    }

    @Override
    public String toString() {
        return "TabBean{" +
                "species=" + species +
                ", categories=" + categories +
                ", sorttype=" + sorttype +
                '}';
    }
}
