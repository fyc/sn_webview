package com.asgame.snbs;

public interface IExtensionFilter {

    boolean isFilter(String extension);

    void addExtension(String extension);

    void removeExtension(String extension);

    void clearExtension();

}
