# !/usr/bin/env python

from sys import argv
from pandas import read_csv
from se import SearchEngine, Embeddings
from utils import print_table


if __name__ == '__main__':
    data = read_csv("./data.csv", keep_default_na=False)
    se = SearchEngine(data)
    query = argv[1]
    print(f"Showing results for \"{query}\":\n")
    for embedding in Embeddings:
        print(f"{embedding}:")
        result = se.query(embedding, query)
        print_table(result)
