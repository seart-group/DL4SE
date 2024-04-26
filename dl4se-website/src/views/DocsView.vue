<template functional>
  <div id="documentation">
    <h1 class="page-title">Documentation</h1>
    <h3 id="granularity" class="page-section">Granularity</h3>
    <div class="page-section">
      <p class="page-text">
        You can build your dataset by considering as a single instance either an entire file or a function:
      </p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><em>File</em></b-td>
            <b-td> Corresponds to entire source code files originating from projects hosted on GitHub. </b-td>
          </b-tr>
          <b-tr>
            <b-td><em>Function</em></b-td>
            <b-td>
              Corresponds to individual code functions present in the aforementioned files and extracted via parsing.
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
      <p class="page-text">Regardless of granularity, we provide the following data for each instance:</p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><code>content</code></b-td>
            <b-td> The raw or processed source code of an instance. </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>content_hash</code></b-td>
            <b-td>
              The SHA-256 hash computed over the content of the original instance. Used to identify duplicates.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>characters</code></b-td>
            <b-td> The total number of characters composing the instance source code. </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>lines</code></b-td>
            <b-td> The total number of lines composing the instance source code. </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>code_tokens</code></b-td>
            <b-td>
              The total number of code-only tokens present in the instance source code. These tokens constitute
              keywords, identifiers, literals and non-space separators. Does not include white spaces and comment
              tokens.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>total_tokens</code></b-td>
            <b-td> The total number of all tokens present in the instance source code. </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>is_test</code></b-td>
            <b-td>
              Whether the instance is a test file or a test case (depending on the granularity of the dataset). We
              identify test files looking at their <code>path</code> within the project's repository.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>is_parsed</code></b-td>
            <b-td>
              Whether the <code>file</code> instance was successfully parsed. If a <code>file</code> is not parsable,
              this means that we did not manage to extract the <code>functions</code> from it. Thus, we cannot have
              unparsable instances in the function-level dataset. For unparsable instances we do not compute specific
              information (e.g., the token counts).
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>boilerplate_type</code></b-td>
            <b-td>
              Whether the <code>function</code> instance is one of following boilerplate types: constructor, getter,
              setter, builder, printer (i.e., <code>toString</code>), equality comparator (i.e., <code>equals</code>)
              and hash value computer (i.e., <code>hashCode</code>). The identification of these functions is done by
              analyzing their name.
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
    </div>
    <h3 id="meta" class="page-section">Metadata</h3>
    <div class="page-section">
      <p class="page-text">
        Certain studies/models may benefit from information such as the AST-representation of source code, which is why
        we chose to offer the option of including an Abstract Syntax Tree (<code>AST</code>) representation of each
        exported instance in Extensible Markup Language (XML) format. The inclusion of this information within the
        dataset will increase its size drastically.
      </p>
      <p class="page-text">
        Alternatively, we also provide the Symbolic Expression (<code>S-Expression</code>) representation of the AST.
        This format is more compact as compared to XML, but it is also omits certain information such as the row/column
        positions of nodes in the source code. This is a more fitting format for studies/models that benefit from
        knowing the structure of the AST, without requiring exact positional information. The inclusion of this
        information within the dataset will increase its size, but not as drastically as XML.
      </p>
      <p class="page-text">
        Finally, we provide the option to include the tag and semantic version of the <code>tree-sitter</code> binding
        with was used to mine the information for each instance. Given that the binding and its grammars change over
        time, this information can be useful for reproducibility purposes. Furthermore, it can be used for diagnostic
        purposes, should the user encounter any issues with the generated dataset. Given that this information is
        included on a per-instance basis, it will slightly increase the size of the dataset.
      </p>
    </div>
    <h3 id="filtering" class="page-section">Filtering</h3>
    <div class="page-section">
      <h4>Repository Filters</h4>
      <p class="page-text">
        Using these parameters, you can filter the projects (repositories) from which we will extract data. The first
        and only required criteria present in the form is the
        <code>language</code>. Specifying a value for this filter will not match projects whose main language matches
        the one specified, but rather all projects that have files written in that language. You can also filter
        repositories by specifying a lower bound for specific characteristics:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item
          v-for="filter in ['commits', 'contributors', 'issues', 'stars']"
          :key="filter"
          class="border-0"
        >
          <code>{{ filter }}</code>
        </b-list-group-item>
      </b-list-group>
      <p class="page-text">
        We also provide options for excluding specific categories of projects from the selection, namely those that:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item class="border-0"> Do not have an <code>open source license</code> </b-list-group-item>
        <b-list-group-item class="border-0"> Are <code>forks</code> of other projects </b-list-group-item>
      </b-list-group>
    </div>
    <div class="page-section">
      <h4 id="exclusion">Instance Filters</h4>
      <p class="page-text">
        Using these parameters, we filter the individual instances based on characteristics exhibited their the source
        code. These include closed, half-closed or unbound ranges for the minimum and maximum number of:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item v-for="filter in ['characters', 'tokens', 'lines']" :key="filter" class="border-0">
          <code>{{ filter }}</code>
        </b-list-group-item>
      </b-list-group>
      <p class="page-text">
        Note that in the case of <code>tokens</code>, the column queried against depends on whether processing
        operations are applied during export. For example, removing comments from exported instances means that
        filtering will be performed according to the <code>code_tokens</code>, otherwise <code>total_tokens</code> is
        used. Apart from these numeric attributes, you can also exclude all instances of:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item class="border-0"> Source code considered to be test code </b-list-group-item>
        <b-list-group-item class="border-0"> Source code containing non-ASCII characters </b-list-group-item>
        <b-list-group-item class="border-0"> <em>Files</em> that were not successfully parsed </b-list-group-item>
        <b-list-group-item class="border-0"> <em>Functions</em> that are considered boilerplate </b-list-group-item>
      </b-list-group>
    </div>
    <div class="page-section">
      <h4 id="duplicates-and-clones">Duplicates and Near-clones</h4>
      <p class="page-text">
        You can remove duplicated/similar entries from the dataset. This can be done by filtering out:
      </p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><em>Duplicates</em></b-td>
            <b-td>
              Those with equivalent <code>content</code> columns that may differ only in whitespace formatting.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><em>Near-Clones</em></b-td>
            <b-td>
              Those that in spite of small differences in <code>content</code>, posses the same
              <code>ast</code>
              structure.
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
      <p class="page-text">
        By definition, duplicates are a subset of near-clones, as identical contents imply identical AST structure.
        These filters have a substantial computational cost.
      </p>
    </div>
    <h3 id="processing" class="page-section">Processing</h3>
    <div class="page-section">
      <h4 id="comment-removal">Comment Removal</h4>
      <p class="page-text">You can automatically remove two types of comments from each instance:</p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><em>Inner Comments</em></b-td>
            <b-td> Regular block or line comments found within instances. </b-td>
          </b-tr>
          <b-tr>
            <b-td><em>Docstrings</em></b-td>
            <b-td> Language-specific comments used for documentation purposes (e.g., JavaDoc for Java). </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
    </div>
    <div class="page-section">
      <h4 id="masking">Instance Masking</h4>
      <p class="page-text">
        This type of instance processing replaces a specified percentage of tokens with a user-specified token. It works
        as follows:
      </p>
      <ol>
        <li class="page-list-item">The source code of each processed instance is split into tokens;</li>
        <li class="page-list-item">
          A subset of the tokens is randomly selected for masking accordingly to the percentage specified by the user in
          the form. Although the selection is completely random by default, checking the contiguity flag will ensure
          that the tokens selected are contiguous;
        </li>
        <li class="page-list-item">
          The selected tokens are replaced with the user-specified token (masking token), with contiguous tokens
          replaced by only a single masking token;
        </li>
        <li class="page-list-item">
          All the tokens, replaced or otherwise, are joined back together to form the new masked version of the original
          <code>content</code>. Given that there are no guarantees as to the parsability of the masked code, we do not
          attempt to synchronize the <code>AST</code> to match.
        </li>
      </ol>
    </div>
    <div class="page-section">
      <h4 id="abstraction">Instance Abstraction</h4>
      <p class="page-text">
        Applies to each instance the abstraction process defined by Tufano et al. in their work
        <a target="_blank" href="https://arxiv.org/abs/1812.08693">
          An Empirical Study on Learning Bug-Fixing Patches in the Wild via Neural Machine Translation </a
        >. The user can specify the set of idioms to consider (i.e., tokens that should not be abstracted).
      </p>
    </div>
  </div>
</template>

<style scoped lang="sass">
.page-list-item
  margin-left: 1.5rem!important
  margin-bottom: .5rem!important

.page-text
  text-align: justify!important
</style>
