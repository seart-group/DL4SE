<template functional>
  <div id="about">
    <h3 class="page-section">DL4SE (Deep Learning 4 Software Engineering)</h3>
    <div class="page-section">
      <p class="page-text">
        The DL4SE tool allows to easily create large-scale datasets that can be used to either run MSR studies or to
        train DL models to automate SE tasks. Use our forms to define the characteristics of the dataset you would like
        to build. The question mark icons in the forms will guide you by providing explanations about the different
        filters you can use as well as processing operations that you can apply.
      </p>
    </div>
    <h3 class="page-section">How does it work?</h3>
    <div class="page-section">
      <p class="page-text">
        We continuously mine the source code of public GitHub repositories having at least 10 stars. While we
        acknowledge that the number of stars is not a good proxy of a repository's quality or relevance, we found the
        10 stars threshold to be a reasonable mechanism to remove most of personal/toy projects unlikely to be relevant
        for empirical studies. Note that, since DL4SE continuously updates the information about the mined projects, a
        project excluded in the first place as having less than 10 stars can be mined a few days after once its number
        of stars reaches the set threshold. Also, the source code of the mined repositories is kept in line with its
        evolution on GitHub. This means that, pending a small time delay, you will always get fresh data as they appear
        in the projects' latest snapshot on GitHub.
      </p>
      <p class="page-text">
        Besides providing the source code of the mined projects, we also implemented several filters and processing
        steps frequently adopted in SE studies. For example, you can automatically remove from your dataset test code,
        unparsable code, or code including non-ASCII characters. You can exclude exact and near duplicates from your
        dataset, ask for an AST-based representation of each mined code instance, and much more!
      </p>
    </div>
    <h3 class="page-section">How long does it take to create a dataset?</h3>
    <div class="page-section">
      <p class="page-text">
        It depends on the size of the dataset and on the amount of requests running on our server. We allow for up to 10
        requests to run in parallel. If there are no runners available when you submit your request, it will be queued
        until a runner is ready. You'll get an email as soon as your dataset is ready! In general, we do not expect the
        creation of a dataset to take more than 24 hours.
      </p>
      <p class="page-text">
        The dataset will be provided in JSON format.
      </p>
    </div>
    <h3 class="page-section">Why do you only support Java?</h3>
    <div class="page-section">
      <p class="page-text">
        This is only our first step. In the next months we plan to integrate Python, C/C++, and Javascript. Also, we
        will expand the types of datasets that can be built, providing forms specialized for the creation of datasets
        to automate code summarization and code review.
      </p>
    </div>
    <h3 class="page-section">How can I report a bug?</h3>
    <div class="page-section">
      <p class="page-text">
        DL4SE is open source. Use the
        <a href="https://github.com/seart-group/DL4SE/issues" target="_blank" class="text-secondary">issue tracker</a>
        hosted on GitHub.
      </p>
    </div>
  </div>
</template>