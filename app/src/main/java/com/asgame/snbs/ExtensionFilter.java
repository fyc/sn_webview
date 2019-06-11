package com.asgame.snbs;

import java.util.HashSet;
import java.util.Set;

/**
 * 纾佺洏瀛樺偍杩囨护鍣�
 *
 * @author shamschu
 * @Date 2018/2/11 涓嬪崍3:16
 */
public class ExtensionFilter implements IExtensionFilter {

    private Set<String> mFilterExtensions;

    public ExtensionFilter() {
        mFilterExtensions = new HashSet<String>();
    }

    @Override
    public boolean isFilter(String extension) {
        return mFilterExtensions.contains(extension);
    }

    @Override
    public void addExtension(String extension) {
        mFilterExtensions.add(extension);
    }

    @Override
    public void removeExtension(String extension) {
        mFilterExtensions.remove(extension);
    }

    @Override
    public void clearExtension() {
        mFilterExtensions.clear();
    }

}
