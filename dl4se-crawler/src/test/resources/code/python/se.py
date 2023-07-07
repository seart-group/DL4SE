from typing import List, Iterable, Tuple
from numbers import Number
from enum import Enum
from itertools import chain
from os import makedirs
from os.path import isfile, exists, join
from re import sub
from string import punctuation
from pandas import DataFrame
from gensim.corpora import Dictionary
from gensim.models import TfidfModel, LsiModel
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
from gensim.similarities import Similarity, SparseMatrixSimilarity, MatrixSimilarity

model_path = "./models"
if not exists(model_path):
    makedirs(model_path)

sims_path = "./similarities"
if not exists(sims_path):
    makedirs(sims_path)


class Models(Enum):
    DICTIONARY = join(model_path, "freq")
    TFIDF = join(model_path, "tfidf")
    LSI = join(model_path, "lsi")
    D2V = join(model_path, "d2v")


class Similarities(Enum):
    FREQ = join(sims_path, "freq")
    TFIDF = join(sims_path, "tfidf")
    LSI = join(sims_path, "lsi")


class Embeddings(Enum):
    FREQ = "Frequency"
    TFIDF = "TF-IDF"
    LSI = "LSI"
    D2V = "Doc2Vec"

    def __str__(self):
        return self.value


class SearchEngine:
    _ret_cols = ["name", "file", "line"]
    with open("./stopwords.txt") as file:
        _stopwords = {line.rstrip() for line in file.readlines()}

    def __init__(self, data: DataFrame):
        data["document"] = \
            data["name"].apply(SearchEngine.__normalize_text__) + data["comment"].apply(SearchEngine.__normalize_text__)
        self._data = data
        self._corpus = data["document"]
        self.__init_dictionary()
        self._features = len(self._dictionary)
        self._corpus_bow = [self.__to_bow__(text) for text in self._corpus]
        self._topics = 300
        self._n = 5
        self.__init_freq__()
        self.__init_tfidf__()
        self.__init_lsi__()
        self.__init_doc2vec__()

    def __init_dictionary(self):
        file_path = Models.DICTIONARY.value
        if isfile(file_path):
            self._dictionary = Dictionary.load(file_path)
        else:
            self._dictionary = Dictionary(self._corpus)
            self._dictionary.save(file_path)

    def __init_freq__(self):
        file_path = Similarities.FREQ.value
        if isfile(file_path):
            self._freq_index = Similarity.load(file_path)
        else:
            self._freq_index = Similarity("freq", self._corpus_bow, num_features=self._features)
            self._freq_index.save(file_path)

    def __init_tfidf__(self):
        file_path = Models.TFIDF.value
        if isfile(file_path):
            self._tfidf = TfidfModel.load(file_path)
        else:
            self._tfidf = TfidfModel(self._corpus_bow)
            self._tfidf.save(file_path)

        self._corpus_tfidf = self._tfidf[self._corpus_bow]

        file_path = Similarities.TFIDF.value
        if isfile(file_path):
            self._tfidf_index = SparseMatrixSimilarity.load(file_path)
        else:
            self._tfidf_index = SparseMatrixSimilarity(self._corpus_tfidf, num_features=self._features)
            self._tfidf_index.save(file_path)

    def __init_lsi__(self):
        file_path = Models.LSI.value
        if isfile(file_path):
            self._lsi = LsiModel.load(file_path)
        else:
            self._lsi = LsiModel(self._corpus_tfidf, id2word=self._dictionary, num_topics=self._topics)
            self._lsi.save(file_path)

        self._corpus_lsi = self._lsi[self._corpus_tfidf]

        file_path = Similarities.LSI.value
        if isfile(file_path):
            self._lsi_index = MatrixSimilarity.load(file_path)
        else:
            self._lsi_index = MatrixSimilarity(self._corpus_lsi)
            self._lsi_index.save(file_path)

    def __init_doc2vec__(self):
        file_path = Models.D2V.value
        if isfile(file_path):
            self._d2v = Doc2Vec.load(file_path)
        else:
            self._corpus_d2v = [TaggedDocument(tokens, [i]) for i, tokens in list(enumerate(self._corpus))]
            self._d2v = Doc2Vec(min_count=5, epochs=300)
            self._d2v.build_vocab(self._corpus_d2v)
            self._d2v.train(self._corpus_d2v, total_examples=self._d2v.corpus_count, epochs=self._d2v.epochs)
            self._d2v.save(file_path)

    @property
    def n(self):
        return self._n

    @n.setter
    def n(self, n: int):
        self._n = n

    @staticmethod
    def __normalize_text__(text: str) -> List[str]:
        def split_camel_case(s: str):
            return sub('([A-Z][a-z]+)', r' \1', sub('([A-Z]+)', r' \1', s)).split()

        stage_1 = text.split("_")
        stage_2 = chain.from_iterable([split_camel_case(word) for word in stage_1])
        stage_3 = [word.lower() for word in stage_2]
        stage_4 = [text.translate(str.maketrans('', '', punctuation)) for text in stage_3]
        stage_5 = [word for word in stage_4 if word not in SearchEngine._stopwords]
        return stage_5

    def __to_bow__(self, tokens: List[str]) -> List[Tuple[Number, Number]]:
        return self._dictionary.doc2bow(tokens)

    def __top_n__(self, similarities: Iterable[float]) -> DataFrame:
        sorted_idx_sim_pairs = sorted(enumerate(similarities), key=lambda x: x[1], reverse=True)
        top_n_idx_sim_pairs = sorted_idx_sim_pairs[:self._n]
        top_n_idx = [idx_sim_pair[0] for idx_sim_pair in top_n_idx_sim_pairs]
        result = self._data.loc[top_n_idx][SearchEngine._ret_cols]
        return result

    def freq_query(self, query: str) -> DataFrame:
        query_tokens = SearchEngine.__normalize_text__(query)
        query_bow = self.__to_bow__(query_tokens)
        sims = self._freq_index[query_bow]
        return self.__top_n__(sims)

    def tfidf_query(self, query: str) -> DataFrame:
        query_tokens = SearchEngine.__normalize_text__(query)
        query_bow = self.__to_bow__(query_tokens)
        sims = self._tfidf_index[self._tfidf[query_bow]]
        return self.__top_n__(sims)

    def lsi_query(self, query: str) -> DataFrame:
        query_tokens = SearchEngine.__normalize_text__(query)
        query_bow = self.__to_bow__(query_tokens)
        sims = abs(self._lsi_index[self._lsi[query_bow]])
        return self.__top_n__(sims)

    def doc2vec_query(self, query: str) -> DataFrame:
        query_tokens = SearchEngine.__normalize_text__(query)
        inferred = self._d2v.infer_vector(query_tokens)
        top_n_idx_sim_pairs = self._d2v.dv.most_similar([inferred], topn=self._n)
        top_n_idx = [idx_sim_pair[0] for idx_sim_pair in top_n_idx_sim_pairs]
        result = self._data.loc[top_n_idx][SearchEngine._ret_cols]
        return result

    def query(self, embedding: Embeddings, query: str) -> DataFrame:
        if embedding == Embeddings.FREQ:
            return self.freq_query(query)
        elif embedding == Embeddings.TFIDF:
            return self.tfidf_query(query)
        elif embedding == Embeddings.LSI:
            return self.lsi_query(query)
        elif embedding == Embeddings.D2V:
            return self.doc2vec_query(query)
        else:
            raise ValueError(f"Specified embedding not supported: {embedding}")

    def lsi_infer_query(self, query: str) -> List[float]:
        query_tokens = SearchEngine.__normalize_text__(query)
        query_bow = self.__to_bow__(query_tokens)
        return [tup[1] for tup in self._lsi[query_bow]]

    def doc2vec_infer_query(self, query: str) -> Iterable:
        query_tokens = SearchEngine.__normalize_text__(query)
        return self._d2v.infer_vector(query_tokens)

    def lsi_corpus_vector(self, idx: int) -> List[float]:
        return [tup[1] for tup in self._corpus_lsi[idx]]

    def doc2vec_vector(self, document: List[str]) -> Iterable:
        return self._d2v.infer_vector(document)
