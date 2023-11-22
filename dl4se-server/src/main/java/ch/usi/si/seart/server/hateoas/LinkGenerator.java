package ch.usi.si.seart.server.hateoas;

@FunctionalInterface
public interface LinkGenerator<T> {

    String generate(T t);
}
