import hljs from "highlight.js/lib/core";
import text from "highlight.js/lib/languages/plaintext";
import java from "highlight.js/lib/languages/java";
import python from "highlight.js/lib/languages/python";
import json from "highlight.js/lib/languages/json";
import xml from "highlight.js/lib/languages/xml";

hljs.registerLanguage("text", text);
hljs.registerLanguage("java", java);
hljs.registerLanguage("python", python);
hljs.registerLanguage("json", json);
hljs.registerLanguage("xml", xml);
