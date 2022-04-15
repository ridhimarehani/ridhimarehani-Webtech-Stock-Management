package com.example.stocks;
import java.util.List;

public class HomeSections {
    private String sectionName;
    private List<List<String>> secItems;

    public HomeSections(String sectionName,List<List<String>> secItems) {
        this.sectionName = sectionName;
        this.secItems = secItems;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }


    public void setSecItems(List<List<String>> secItems) {
        this.secItems = secItems;
    }

    public String getSectionName() {
        return sectionName;
    }
    public List<List<String>> getSecItems() {
        return secItems;
    }

    @Override
    public String toString() {
        return "HomeSections{" +
                "sectionName='" + sectionName + '\'' +
                ", secItems=" + secItems +
                '}';
    }
}

