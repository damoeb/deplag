deplag
==========================

What is it?
-----------

This a prototype of a plagiarism detector.  It is comes with a HTML frontend to display the potential rip-offs. These matches are displayed as diffs. 

The Latest Version
------------------
There is no final version yet. Last Updated Nov 2011.


Documentation
------------
The process of plagiarism detection can be seen as a complex search, like google does. So you have an search index, that holds the relevant data you want to match against and a complex query (e.g. a paper). 

In contrast to a conventianal search, the author of the document that is checked for plagiarisms, will (assume it is acting puckish) try to cover all tracks. So, rephrasing, reordering words and shuffling sentences are well-known problems that limit the performance of plagiarism detection.

Hence, we cannot work on word level. The abstract projection that will be used must be independend of text length, word order, misspelling, removed words and the well-known issues mentioned before. 

We create checksums of group of words (Locality-sensitive hashing) and store it in a nosql database.
    {
        hash: 'n5o8s8n4s7n5t5a3',
        documents: [
            {
                documentId: 326,
                segments: [
                    {
                        fromIndex: 8924,
                        toIndex: 8978
                    }
                ]
            }
        ]
    }


checksuming groups of words


Challenges

False-Positives: coincidatial similar phrases
False-Negatives: rephrasing, translations, reordering



Installation
------------
I am sorry to tell you, the current code does not run. It's been a while..

    mvn install

### Requirements
* git
* java 1.7
* mysql
* mongo db

### Modules
* *deplag-core* index and search
* *deplag-Service* diff ui


Licensing
------------

Please see the file called LICENSE.

Contacts
--------
Markus Ruepp @damoeb
