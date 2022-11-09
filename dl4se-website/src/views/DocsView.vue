<template functional>
  <div id="documentation">
    <h1 class="page-title">Documentation</h1>
    <h3 id="granularity" class="page-section">Granularity</h3>
    <div class="page-section">
      <p class="page-text">
        We currently support instance selection at two granularity levels:
      </p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><em>File</em></b-td>
            <b-td>
              Corresponds to entire source code files originating from projects hosted on GitHub.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><em>Function</em></b-td>
            <b-td>
              Corresponds to individual code snippets of source functions present in the aforementioned files.
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
      <p class="page-text">
        Regardless of granularity, we offer the following data for each instance:
      </p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><code>content</code></b-td>
            <b-td>
              The raw or processed source code of a matching instance.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>content_hash</code></b-td>
            <b-td>
              The SHA-256 hash computed over the content of the original instance. Used to identify duplicates.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>characters</code></b-td>
            <b-td>
              The total number of characters in the instance source code string.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>lines</code></b-td>
            <b-td>
              The total number of lines in the instance source code string.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>code_tokens</code></b-td>
            <b-td>
              The total number of code-only tokens present in the source code. These types of tokens constitute keywords,
              identifiers, literals and non-space separators. Does not include white spaces or comment tokens.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>total_tokens</code></b-td>
            <b-td>
              The total number of all tokens present in the source code.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>is_test</code></b-td>
            <b-td>
              Whether the file instance is a test file, or whether a function instance is from a test file. These types of
              file house special code aimed at evaluating and verifying other parts of the software system. Currently, we
              only distinguish test files by examining their <code>path</code>.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>is_parsed</code></b-td>
            <b-td>
              Whether the <code>file</code> instance was successfully parsed. Unparsable instances usually have certain
              information omitted (e.g., the token counts).
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><code>boilerplate_type</code></b-td>
            <b-td>
              Whether the <code>function</code> instance is one of following boilerplate types: constructor, getter,
              setter, builder, printer (i.e. <code>toString</code>), equality comparator (i.e. <code>equals</code>) and
              hash value computer (i.e. <code>hashCode</code>).
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
    </div>
    <h3 id="ast" class="page-section">AST-based Representation</h3>
    <div class="page-section">
      <p class="page-text">
        Certain models may benefit from information such as the structure of source code, which is why we chose to offer
        the option of including an Abstract Syntax Tree (<code>ast</code>) representation of each exported instance, in
        Extensible Markup Language (XML) format. However, the inclusion of this information within the dataset will
        without a doubt increase its size drastically. Furthermore, since the AST is pre-computed for each instance at
        mining time, any processing applied to the source code implies that the syntax tree will be recomputed to match.
        For this reason, the inclusion of ASTs in the dataset will drastically increase the size of the dataset file,
        and may increase the amount of time needed to export individual instances if processing is applied. Since not
        all models require the AST, we chose to not include it by default, and leave that decision to our users.
      </p>
    </div>
    <h3 id="filtering" class="page-section">Filtering</h3>
    <div class="page-section">
      <h4>Repository Filters</h4>
      <p class="page-text">
        Using these parameters, we filter the projects (repositories) from which we will source our data. The first and
        only required criteria present in the form is the <code>language</code>. Note that specifying a value of this
        filter will not match projects whose main language matches the one specified, but rather all projects that have
        files written in that language. That being said, the lower bound filters for statistical repository
        characteristics include those for:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item v-for="filter in ['commits', 'contributors', 'issues', 'stars']" :key="filter" class="border-0">
          <code>{{ filter }}</code>
        </b-list-group-item>
      </b-list-group>
      <p class="page-text">
        We also provide options for excluding specific categories of projects from the selection, namely those that:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item class="border-0">
          Do not have an <code>open source license</code>
        </b-list-group-item>
        <b-list-group-item class="border-0">
          Are <code>forks</code> of other projects
        </b-list-group-item>
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
        Note that in the case of <code>tokens</code>, the column queried against depends on whether processing operations
        are applied during export. For example, removing comments from exported instances means that filtering will be
        performed according to the <code>code_tokens</code>, otherwise <code>total_tokens</code> is used. Apart from these
        numeric attributes, we can also exclude all instances of:
      </p>
      <b-list-group class="mb-3">
        <b-list-group-item class="border-0">
          Source code considered to be test code
        </b-list-group-item>
        <b-list-group-item class="border-0">
          Source code containing non-ASCII characters
        </b-list-group-item>
        <b-list-group-item class="border-0">
          <em>Files</em> that were not successfully parsed
        </b-list-group-item>
        <b-list-group-item class="border-0">
          <em>Functions</em> that are considered boilerplate
        </b-list-group-item>
      </b-list-group>
    </div>
    <div class="page-section">
      <h4 id="duplicates-and-clones">Duplicates and Near-clones</h4>
      <p class="page-text">
        One of the most frequently employed types of filters in studies involves the act of removing similar entries
        from the dataset. We define two types of identical code instances, namely:
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
              Those that in spite of small differences in <code>content</code>, posses the same overall <code>ast</code>
              structure.
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
      <p class="page-text">
        By definition, duplicates are a subset of near-clones, as identical contents imply identical AST structure.
        Note that since the act of filtering both the former and latter categories of instances involves performing a
        special type of query that distinctly selects code based on the <code>content_hash</code> and <code>ast_hash</code>
        respectively, the amount of time needed to export a dataset comprised of unique instances will take longer than
        its non-unique counterpart.
      </p>
    </div>
    <h3 id="processing" class="page-section">Processing</h3>
    <div class="page-section">
      <h4 id="comment-removal">Comment Removal</h4>
      <p class="page-text">
        For certain models, removing comment strings from source code might be beneficial. We provide comment removal
        instance processing for two types of comments:
      </p>
      <b-table-simple stacked="md" borderless>
        <b-tbody>
          <b-tr>
            <b-td><em>Inner Comments</em></b-td>
            <b-td>
              Regular block or line comments found within instances.
            </b-td>
          </b-tr>
          <b-tr>
            <b-td><em>Docstrings</em></b-td>
            <b-td>
              Language-specific comments used for documentation purposes (i.e. JavaDoc for Java).
            </b-td>
          </b-tr>
        </b-tbody>
      </b-table-simple>
    </div>
    <div class="page-section">
      <h4 id="masking">Instance Masking</h4>
      <p class="page-text">
        This type of instance processing replaces a certain fraction of the source code with a user-specified token.
        It works as follows:
      </p>
      <ol>
        <li class="page-list-item">
          The source code of each processed instance is split into tokens;
        </li>
        <li class="page-list-item">
          A subset of the tokens is randomly selected for masking, its size equal to the percentage specified in the
          form. Although the selection is completely random by default, checking the contiguity flag will ensure that
          the tokens selected are all in sequence;
        </li>
        <li class="page-list-item">
          The selected tokens are replaced with the specified mask token string, where neighboring selections are
          replaced with only a single masking token;
        </li>
        <li class="page-list-item">
          All the tokens, replaced or otherwise, are joined back together to form the new masked version of the original
          <code>content</code>. Given that there are no guarantees as to the parsability of the masked code, we do not
          attempt to synchronize the <code>ast</code> to match.
        </li>
      </ol>
    </div>
    <div class="page-section">
      <h4 id="abstraction">Instance Abstraction</h4>
      <p class="page-text">
        Abstraction refers to the transformation of raw source code into its abstract textual counterpart, which
        consists of keywords, separators and ID tokens in place of identifiers and literals. When subjecting
        instances to abstraction, users have the ability to also specify idioms: identifiers and literals that occur so
        often in source code that they can almost be considered as keywords of the language. Although they can be added
        manually, we also support specifying them in a <code>.txt</code> format, each line of the file containing a
        single idiom.
      </p>
    </div>
  </div>
</template>