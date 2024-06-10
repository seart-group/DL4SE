package ch.usi.si.seart.server.hateoas;

import java.net.URL;

@FunctionalInterface
public interface URLGenerator<T> {

    URL generate(T t);
}
