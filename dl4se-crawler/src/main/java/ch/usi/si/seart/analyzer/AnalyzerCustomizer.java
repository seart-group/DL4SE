package ch.usi.si.seart.analyzer;

@FunctionalInterface
public interface AnalyzerCustomizer<T extends Analyzer> {

    void customize(T analyzer);
}
