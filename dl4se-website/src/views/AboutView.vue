<template functional>
  <article aria-labelledby="about">
    <h1 id="about" class="d-none">About</h1>
    <section aria-labelledby="description">
      <h2 id="description">What is the SEART dataset hub?</h2>
      <p>
        Our platform allows researchers to easily create large-scale datasets that can be used to either run studies on
        mining software repositories or to train deep learning models to automate software engineering tasks. Use our
        forms to define the characteristics of the dataset you would like to build. The question mark icons in the forms
        guide you by providing explanations about the different filters you can use as well as processing operations
        that you can apply.
      </p>
    </section>
    <section aria-labelledby="how-does-it-work">
      <h2 id="how-does-it-work">How does it work?</h2>
      <p>
        We continuously mine the source code of public GitHub repositories having at least 10 stars. While we
        acknowledge that the number of stars is not a good proxy of a repository's quality or relevance, we found the 10
        stars threshold to be a reasonable mechanism to remove most of personal/toy projects unlikely to be relevant for
        empirical studies. Note that, since the platform continuously updates the information about the mined projects,
        a project excluded in the first place as having less than 10 stars can be mined a few days after once its number
        of stars reaches the set threshold. Also, the source code of the mined repositories is kept in line with its
        evolution on GitHub. This means that, pending a small time delay, you will always get fresh data as they appear
        in the projects' latest snapshot on GitHub.
      </p>
      <p>
        Besides providing the source code of the mined projects, we also implemented several filters and processing
        steps frequently adopted in software engineering studies. For example, you can automatically remove from your
        dataset test code, unparsable code, or code including non-ASCII characters. You can exclude exact and near
        duplicates from your dataset, ask for an AST-based representation of each mined code instance, and much more!
      </p>
    </section>
    <section aria-labelledby="dataset-creation-time">
      <h2 id="dataset-creation-time">How long does it take to create a dataset?</h2>
      <p>
        It depends on the size of the dataset and on the amount of requests running on our server. We allow for up to 10
        requests to run in parallel. If there are no runners available when you submit your request, it will be queued
        until a runner is ready. You'll get an email as soon as your dataset is ready! In general, we do not expect the
        creation of a dataset to take more than 24 hours. The constructed dataset will be provided in compressed
        <abbr title="JavaScript Object Notation Lines">JSONL</abbr> format.
      </p>
    </section>
    <section aria-labelledby="language-support">
      <h2 id="language-support">Why do you only support Java &amp; Python?</h2>
      <p>
        This is only our first step. As time goes on, we plan to integrate JavaScript, TypeScript, C++, etc. We will
        also further expand the types of datasets that can be built, providing forms specialized for the creation of
        datasets to automate the code summarization and code review tasks.
      </p>
    </section>
    <section aria-labelledby="bug-reports">
      <h2 id="bug-reports">How can I report a bug?</h2>
      <p>
        Our platform is open source. Use the
        <a href="https://github.com/seart-group/dl4se/issues" target="_blank">issue tracker</a> hosted on GitHub.
      </p>
    </section>
  </article>
</template>

<style scoped lang="sass" src="@/assets/styles/view/about.sass" />
