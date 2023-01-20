package usi.si.seart.treesitter;

import lombok.Getter;

import java.util.function.LongSupplier;

public enum Language {

    _INVALID_(),
    AGDA(Languages::agda),
    BASH(Languages::bash),
    C(Languages::c),
    CSHARP(Languages::cSharp),
    CPP(Languages::cpp),
    CSS(Languages::css),
    DART(Languages::dart),
    ELM(Languages::elm),
    EMBEDDED_TEMPLATE(Languages::embeddedTemplate),
    ENO(Languages::eno),
    GO(Languages::go),
    HASKELL(Languages::haskell),
    HTML(Languages::html),
    JAVA(Languages::java),
    JAVASCRIPT(Languages::javascript),
    JULIA(Languages::julia),
    KOTLIN(Languages::kotlin),
    LUA(Languages::lua),
    MARKDOWN(Languages::markdown),
    OCAML(Languages::ocaml),
    PHP(Languages::php),
    PYTHON(Languages::python),
    RUBY(Languages::ruby),
    RUST(Languages::rust),
    SCALA(Languages::scala),
    SCSS(Languages::scss),
    SWIFT(Languages::swift),
    TOML(Languages::toml),
    TSX(Languages::tsx),
    TYPESCRIPT(Languages::typescript),
    VUE(Languages::vue),
    YAML(Languages::yaml),
    WASM(Languages::wasm);

    @Getter
    private final long id;

    Language() {
        this.id = 0L;
    }

    Language(LongSupplier supplier) {
        long id = 0L;
        try {
            id = supplier.getAsLong();
        } catch (UnsatisfiedLinkError ignored) {
            // Triggered whenever a language is not included in the native library.
        } finally {
            this.id = id;
        }
    }
}
