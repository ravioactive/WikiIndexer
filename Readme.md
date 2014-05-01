========================================================
TOKENIZATION, PARSING AND INDEXING THE ENGLISH WIKIPEDIA
========================================================

An indexing system is implemented from scratch on the English Wikipedia, designed to handle millions of documents.
Each document is parsed and broken down into tokens. Using the markup of the Wikipedia XML, the contents of each document are parsed and stemmed using a pipeline of tokenizers to convert each XML Document to an Indexable Document which contains a list of parsed, lemmatized and normalized tokens of each cateogry.
Thus, 4 kinds of inverted indexes and associated dictionaries are created - Author, Category, Links and Terms.
The indexes with their postings list are serialized to disk, and can be read and loaded into the memory ready to be queried for their contents.

To run: Please import these folders into a standard console Java Project.
The outputs will occur in "/workspace/" folder
