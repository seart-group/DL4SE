package org.example;

public class Boilerplate {
    String key;
    String value;

    Boilerplate(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Boilerplate.BoilerplateBuilder builder() {
        return new Boilerplate.BoilerplateBuilder();
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public static class BoilerplateBuilder {
        private String key;
        private String value;

        BoilerplateBuilder() {
        }

        public Boilerplate.BoilerplateBuilder key(final String key) {
            this.key = key;
            return this;
        }

        public Boilerplate.BoilerplateBuilder id(final String value) {
            this.value = value;
            return this;
        }

        public Boilerplate build() {
            return new Boilerplate(this.key, this.value);
        }
    }
}