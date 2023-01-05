package usi.si.seart.analyzer.test;

import usi.si.seart.treesitter.LibraryLoader;

public abstract class BaseTest {
    static {
        LibraryLoader.load();
    }
}
