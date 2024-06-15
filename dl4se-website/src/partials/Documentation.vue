<script>
export default {
  name: "documentation",
  functional: true,
  render() {
    return (
      <article aria-labelledby="documentation">
        <h1 id="documentation" class="d-none">
          Documentation
        </h1>
        <section aria-labelledby="granularity">
          <h2 id="granularity">Granularity</h2>
          <p>You can build your dataset by considering as a single instance either an entire file or a function:</p>
          <b-table-simple stacked="md" borderless small>
            <b-tbody>
              <b-tr>
                <b-td>
                  <em>File</em>
                </b-td>
                <b-td>Corresponds to entire source code files originating from projects hosted on GitHub.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <em>Function</em>
                </b-td>
                <b-td>
                  Corresponds to individual code functions present in the aforementioned files and extracted via
                  parsing.
                </b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
          <p>Regardless of granularity, we provide the following data for each instance:</p>
          <b-table-simple stacked="md" borderless small>
            <b-tbody>
              <b-tr>
                <b-td>
                  <code>path</code>
                </b-td>
                <b-td>
                  The path of the file relative to the project repository root. At the function granularity, this value
                  corresponds to the file from which the function was extracted.
                </b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>language_name</code>
                </b-td>
                <b-td>The programming language of the instance.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>content</code>
                </b-td>
                <b-td>The raw source code of an instance.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>content_hash</code>
                </b-td>
                <b-td>
                  The SHA-256 hash computed over the content of the original instance. Used to identify duplicates.
                </b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>characters</code>
                </b-td>
                <b-td>The total number of characters composing the instance source code.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>lines</code>
                </b-td>
                <b-td>The total number of lines composing the instance source code.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>code_tokens</code>
                </b-td>
                <b-td>
                  The total number of code-only tokens present in the instance source code. These tokens constitute
                  keywords, identifiers, literals and non-space separators. Does not include white spaces and comment
                  tokens.
                </b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>total_tokens</code>
                </b-td>
                <b-td>The total number of all tokens present in the instance source code.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>is_test</code>
                </b-td>
                <b-td>
                  Whether the instance is a test file or a test case (depending on the granularity of the dataset). We
                  identify test files looking at their <code>path</code> within the project's repository.
                </b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>contains_error</code>
                </b-td>
                <b-td>Whether the parser encountered syntax errors while processing the instance.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <code>boilerplate_type</code>
                </b-td>
                <b-td>
                  Specific to <code>function</code> instances, this attribute indicates the type of boilerplate that the
                  function represents. This information is inferred from the function's name, and types may vary
                  depending on the programming language.
                </b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </section>
        <section aria-labelledby="meta">
          <h2 id="meta">Metadata</h2>
          <p>
            Certain studies/models may benefit from information such as the AST-representation of source code, which is
            why we chose to offer the option of including an Abstract Syntax Tree (<code>AST</code>) representation of
            each exported instance in Extensible Markup Language (XML) format. The inclusion of this information within
            the dataset <em>will drastically increase its size</em>.
          </p>
          <p>
            Alternatively, we also provide the Symbolic Expression (<code>S-Expression</code>) representation of the
            AST. This format is more compact as compared to XML, but it is also omits certain information such as the
            row/column positions of nodes in the source code. This is a more fitting format for studies/models that
            benefit from knowing the structure of the AST, without requiring exact positional information. The inclusion
            of this information within the dataset <em>will increase its size</em>, but not as drastically as XML.
          </p>
          <p>
            Finally, we provide the option to include the commit SHA and semantic version tag of the{" "}
            <code>tree-sitter</code> binding release which was used to mine the information for each instance. Given
            that the binding and its grammars change over time, this information can be useful for reproducibility
            purposes. Furthermore, it can be used for diagnostic purposes, should the user encounter any issues with the
            generated dataset. Since this information is also included on a per-instance basis, it will slightly
            increase the size of the dataset.
          </p>
        </section>
        <section aria-labelledby="repository">
          <h2 id="repository">Repository Filters</h2>
          <p>
            Using these parameters, you can filter the projects (repositories) from which we will extract data. The
            first and only required criteria present in the form is the <code>language</code>. Specifying a value for
            this filter will not match projects whose main language matches the one specified, but rather all projects
            that have files written in that language. You can also filter repositories by specifying a lower bound for
            specific characteristics: <code>commits</code>, <code>contributors</code>, <code>issues</code> and{" "}
            <code>stars</code>. We also provide options for excluding specific categories of projects from the
            selection, namely those that do not have an <code>open source license</code> or are <code>forks</code> of
            other projects.
          </p>
        </section>
        <section aria-labelledby="exclusion">
          <h2 id="exclusion">Instance Filters</h2>
          <p>
            Using these parameters, we filter the individual instances based on characteristics exhibited their the
            source code. These include closed, half-closed or unbound ranges for the minimum and maximum number of:{" "}
            <code>characters</code>, <code>tokens</code> and <code>lines</code>.
          </p>
          <p>
            Note that in the case of <code>tokens</code>, the column queried against depends on whether processing
            operations are applied during export. For example, removing comments from exported instances means that
            filtering will be performed according to the <code>code_tokens</code>, otherwise <code>total_tokens</code>{" "}
            is used. Apart from these numeric attributes, you can also exclude all instances of source code that:
          </p>
          <b-table-simple stacked="md" borderless small>
            <b-tbody>
              <b-tr>
                <b-td>Originates from test files</b-td>
              </b-tr>
              <b-tr>
                <b-td>Contains non-ASCII characters</b-td>
              </b-tr>
              <b-tr>
                <b-td>Contains syntax errors</b-td>
              </b-tr>
              <b-tr>
                <b-td>Is considered boilerplate (only functions)</b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </section>
        <section aria-labelledby="duplicates-and-clones">
          <h2 id="duplicates-and-clones">Duplicates and Near-clones</h2>
          <p>You can remove duplicated/similar entries from the dataset. This can be done by filtering out:</p>
          <b-table-simple stacked="md" borderless small>
            <b-tbody>
              <b-tr>
                <b-td>
                  <em>Duplicates</em>
                </b-td>
                <b-td>
                  Those with equivalent <code>content</code> columns that may differ only in whitespace formatting.
                </b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <em>Near-Clones</em>
                </b-td>
                <b-td>
                  Those that in spite of small differences in <code>content</code>, posses the same <code>ast</code>{" "}
                  structure.
                </b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
          <p>
            By definition, duplicates are a subset of near-clones, as identical contents imply identical AST structure.
            These filters have a substantial computational cost.
          </p>
        </section>
        <section aria-labelledby="processing">
          <h2 id="processing">Processing</h2>
          <p>
            At the moment, we only support the removal of comments from the source code. You can automatically remove
            two types of comments from each instance:
          </p>
          <b-table-simple stacked="md" borderless small>
            <b-tbody>
              <b-tr>
                <b-td>
                  <em>Inner Comments</em>
                </b-td>
                <b-td>Regular block or line comments found within instances.</b-td>
              </b-tr>
              <b-tr>
                <b-td>
                  <em>Docstrings</em>
                </b-td>
                <b-td>
                  Language-specific comments used for documentation purposes (JavaDoc for Java, docstring for Python,
                  etc.).
                </b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </section>
      </article>
    );
  },
};
</script>
